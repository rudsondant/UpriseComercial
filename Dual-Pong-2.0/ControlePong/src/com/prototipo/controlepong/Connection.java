package com.prototipo.controlepong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

public class Connection {
	
	private Context context;
	private ConectToTcpServer send;
	
	public Connection(Context context)
	{
		this.context = context;	
	}
	
	//Pega o IP de broadcast da inteface WiFi do aparelho	
	private InetAddress GetBroadcastAddress() throws IOException 
	{		
		WifiManager myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo myDhcpInfo = myWifiManager.getDhcpInfo();
		
		if (myDhcpInfo == null)		
			return null;	
		
		int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)	| ~myDhcpInfo.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
		quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		
		return InetAddress.getByAddress(quads);		
	}
	
	//Envia um pacote de descoberta UDP e aguarda resposta TCP na mesma porta de envio UDP
	public void SendBroadcastRequest(Handler handler, String dataToSend, int port) throws IOException, InterruptedException
	{
		this.StartTcpListening(port, handler);
		
		InetAddress broadcast = GetBroadcastAddress();
		SendUdp threadBroadcast = new SendUdp(handler, dataToSend, port, broadcast, true);		
		threadBroadcast.start();
	}
	
	public void SendUdp(Handler handler, String dataToSend, int port, String remoteIp) throws IOException, InterruptedException
	{	
		SendUdp threadUdp = new SendUdp(handler, dataToSend, port, InetAddress.getByName(remoteIp), false);		
		threadUdp.start();
	}
	
	public void StartTcpListening(int port, Handler handler) throws InterruptedException
	{
		ListenTcp receive = new ListenTcp(handler, port);
		receive.start();	
	}
	
	public void ConnectToTcp(Handler handler, String remoteIp, int port, String dataToSend) throws UnknownHostException
	{
		InetAddress ip = InetAddress.getByName(remoteIp);
		this.send = new ConectToTcpServer(handler, ip, port, dataToSend);
		send.start();
	}
	
	public void StopConnetion()
	{
		if(send.isAlive())
			send.interrupt();
	}
	
	 //Classe para envio de pacote UDP
	 private class SendUdp extends Thread {

		    private Handler handler;
		    //Variáveis de rede
		    private byte[] dataToSend; 
		 	private InetAddress remoteIp;
		 	private int port;
		 	private boolean broadcast;

		    public SendUdp(Handler handler, String dataToSend, int port, InetAddress remoteIp, boolean broadcast) 
		    {
		        this.handler = handler;
		        this.dataToSend = dataToSend.getBytes();
		        this.port = port;
		        this.broadcast = broadcast;
		        this.remoteIp = remoteIp;
		    }
		    
		    @Override
		    public void run() 
		    {	
		    	Message message = new Message();
		    	
			 	try 
			 	{		 			
					DatagramSocket socket = new DatagramSocket();
					socket.setBroadcast(broadcast);
					DatagramPacket packet = new DatagramPacket(dataToSend, dataToSend.length,remoteIp, port);
					socket.send(packet);
					socket.close();
					message.what = 1;
					message.obj = "Udp Sent";
				} 
			 	catch (IOException e) 
			 	{
					message.obj = "Erro no envio do broadcast!";
				}
			 	finally
			 	{
		        //Envio da mensagem.
		        handler.sendMessage(message);		            
		        }
		        
		    }
		}
	 
	//Classe para escuta de pacotes TCP
	 private class ListenTcp extends Thread 
	 {
		 Handler handler;
		 int port;
		 
		 public ListenTcp(Handler handler, int port)
		 {
			 this.handler = handler;
			 this.port = port;
		 }
		 
		 @Override
		 public void run() 
		 {		
			 String dados[] = new String[2];
			 BufferedReader br;
		 	        try 
		 	        {	
		 	        	while(!Thread.currentThread().isInterrupted())
		 	        	{	 	        		
		 	        		Message message = new Message();
		 	        	    ServerSocket ss = new ServerSocket(port);		 	                        			 	            	
		 	                Socket s = ss.accept();
		 	                InetAddress remoteIp = s.getInetAddress();
		 	                dados[0] = remoteIp.toString();
		 	                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		 	                dados[1] = br.readLine().toString(); 
		 	                message.obj = dados;
				 			message.what = 2;					 			
		 	                s.close(); 
		 	                ss.close();	 	    
		 	                handler.sendMessage(message);
		 	        	}
		 	        } 
		 	        catch (Throwable e) 
		 	        {
		 	        	 Message message = new Message();
		 	             e.printStackTrace();
		 	             message.obj = e.toString();
		 	             message.what = 0;
		 	             handler.sendMessage(message);	
		 	        }

		 }
	 }
	 
	//Classe para conexão a servidor TCP
		 private class ConectToTcpServer extends Thread 
		 {
			 Handler handler;
			 InetAddress remoteIp;
			 int port;
			 String dataToSend;
			 
			 public ConectToTcpServer(Handler handler, InetAddress remoteIp, int port, String dataToSend)
			 {
				 this.handler = handler;
				 this.remoteIp = remoteIp;
				 this.port = port;
				 this.dataToSend = dataToSend;
			 }
			 
			 @Override
			 public void run() 
			 {
			 	 try 
			 	 {	
			 		 
			 		Socket client = new Socket(remoteIp, port);  //connect to server
			 	    PrintWriter printwriter = new PrintWriter(client.getOutputStream(),true);
			 	    printwriter.write(dataToSend);  //write the message to output stream
			 	    printwriter.flush();			 	     
			 		
			 	    BufferedReader br=new BufferedReader(new InputStreamReader(client.getInputStream()));
			 		while (!Thread.currentThread().isInterrupted()) 
			 		{
			            String mstr = br.readLine();
			            if (mstr == null) {
			                break; // socket closed
			            }
			            if (mstr.equals("%QUIT%")) {
			                break; // protocol specific end message
			            }
			            	            
			            Message message = new Message();
		                message.what = 3;
		                message.obj = mstr;
		                handler.sendMessage(message); 
			        }			 		
			 		
			 			 
			 	    client.close();   //closing the connection
			 	    printwriter.close();
			 	 } 
			 	 catch (IOException e) 
			 	 {
			 	    e.printStackTrace();
			 	 }

			} 
		 }
	 
}


