using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using System.Net.NetworkInformation;

public class UdpConnection : Connection
{
    private static UdpClient listener;

    public event EventHandler<NetworkEvent> dataReceived;
    public event EventHandler<NetworkEvent> stateChanges;

    public UdpConnection()
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
        this.localPort = port;
        thread = new Thread(new ThreadStart(StartListeningUDP));
        thread.IsBackground = true;
        thread.Start();
    }

    public override void StopListening()
    {
        listener.Close();
        isListening = false;
        thread.Abort();
        netEvent.state = state.UDP_STOPPED;
        onStateChanged(netEvent);
    }

    private void StartListeningUDP()
    {            
        try
        {
            listener = new UdpClient(localPort);
            netEvent.Clear();
            netEvent.state = Connection.state.UDP_LISTENING;
            onStateChanged(netEvent);
            currentState = state.TCP_LISTENING;
        }
        catch
        {
            netEvent.Clear();
            netEvent.state = Connection.state.ERROR;
            netEvent.errorMessage = "Port already in use.";
            onStateChanged(netEvent);
            return;
        }

        remoteAddress = new IPEndPoint(IPAddress.Any, localPort);
        byte[] receiveByteArray;

        while (isListening)
        {
            try
            {
                receiveByteArray = listener.Receive(ref remoteAddress);
                receivedData = Encoding.ASCII.GetString(receiveByteArray, 0, receiveByteArray.Length);
            }
            catch(SocketException e)
            {
                netEvent.Clear();
                netEvent.state = Connection.state.ERROR;
                netEvent.error = e;
                onStateChanged(netEvent);
            }
            finally
            {
                if (isListening)
                {
                    netEvent.Clear();
                    netEvent.Data = receivedData;
                    netEvent.remoteIp = remoteAddress;
                    onDataReceived(netEvent);
                 }
            }

        }  
          
        listener.Close();
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
        EventHandler<NetworkEvent> handler = stateChanges;
        if (handler != null)
        {
            handler(this, e);
        }
    }
        #endregion
}
