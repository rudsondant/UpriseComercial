using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Net.Sockets;
using System.Net.NetworkInformation;
using System.Net;
class TcpConnection : Connection
{
	TcpListener tcpServer = null;        
	
	public event EventHandler<NetworkEvent> dataReceived;
	public event EventHandler<NetworkEvent> stateChanged;
	
	public TcpConnection()
	{
		netEvent = new NetworkEvent();
	}
	
	public override state GetCurrentState()
	{
		return currentState;
	}
	
	public override void StartListening(int port)
	{
		isListening = true;
		localPort = port;
		thread = new Thread(new ThreadStart(StartListeningTCP));
		thread.IsBackground = true;
		thread.Start();
	}
	
	public override void StopListening()
	{
		tcpServer.Stop();
		isListening = false;
		thread.Abort();
		netEvent.Clear();
		netEvent.state = state.TCP_STOPPED;
		onStateChanged(netEvent);
	}
	
	public void SendTcpResponse(string data, string remoteIp, int port)
	{
		try
		{
			TcpClient client = new TcpClient(remoteIp, port);
			// Translate the passed message into ASCII and store it as a Byte array.
			Byte[] dataTosend = System.Text.Encoding.ASCII.GetBytes(data);
			NetworkStream stream = client.GetStream();
			stream.Write(dataTosend, 0, dataTosend.Length);
			// Close everything.
			stream.Close();
			client.Close();
			
		}
		catch (SocketException e)
		{
			//Evento de envio de pacote TCP
		}
	}
	
	private void StartListeningTCP()
	{
		if (!TcpConnection.isListening)
			return;
		
		//Verificando se a porta TCP já está em uso na máquina
		bool alreadyInUse = (from p in IPGlobalProperties.GetIPGlobalProperties().GetActiveTcpListeners() where p.Port == localPort select p).Count() == 1;
		if (!alreadyInUse)
		{
			Int32 port = this.localPort;
			tcpServer = new TcpListener(port);
			tcpServer.Start();
			
			//gerando evento
			netEvent.Clear();
			netEvent.state = state.TCP_LISTENING;
			onStateChanged(netEvent);
			currentState = state.TCP_LISTENING;
		}
		else
		{
			//Gerando evento
			netEvent.Clear();
			netEvent.state = state.TCP_STOPPED;
			onStateChanged(netEvent);
			currentState = state.TCP_STOPPED;
			return;
		}  
		// Enter the listening loop. 
		while (TcpConnection.isListening)
		{
			TcpClient client = tcpServer.AcceptTcpClient();
			currentState = state.CLIENT_ACCEPTED;
			remoteAddress = client.Client.RemoteEndPoint as IPEndPoint;
			
			//Thread de gerenciamento de conexões. Uma thread é iniciada a cada conexão recebida
			//Thread newClient = new Thread(new ThreadStart(() => NewConnection(client)));
			//newClient.IsBackground = true;
			//newClient.Start();                                         
		}
		
		StartListeningTCP(); //Recursivamente chama o método para iniciar a escuta novamente
	}
	
	
	#region Eventos
	protected virtual void onDataReceived(NetworkEvent e)
	{
		EventHandler<NetworkEvent> handler = dataReceived;
		if (handler != null)
		{
			handler(this, e);
		}
	}
	
	protected virtual void onStateChanged(NetworkEvent e)
	{
		EventHandler<NetworkEvent> handler = stateChanged;
		if (handler != null)
		{
			handler(this, e);
		}
	}
	#endregion
}


