using UnityEngine;
using System.Collections;
using System.Xml;

public class NetworkManager : MonoBehaviour {

	public int port;
	public Transform player1, player2;

	UdpConnection connectionUdp;
	XmlDocument xml;
	string move1, move2;

	// Use this for initialization
	void Start () {
		connectionUdp = new UdpConnection ();
		connectionUdp.StartListening (port);
		xml = new XmlDocument ();
		Debug.Log ("Aberta: " + port);
		connectionUdp.dataReceived += udpDataReceived;
	}
	
	// Update is called once per frame
	void Update () {		
		//Controlando a movimentaçao do player 1
		if (move1 == "UP") {
						player1.SendMessage ("MoveUp");
				}else if(move1 == "DOWN")
						player1.SendMessage ("MoveDown");
				else
						player1.SendMessage ("PlayerStop");


		
		//Controlando a movimentaçao do player 2
		if (move2 == "UP") {
			player2.SendMessage ("MoveUp");
		}else if(move2 == "DOWN")
			player2.SendMessage ("MoveDown");
		else
			player2.SendMessage ("PlayerStop");	
	}
	
	void OnApplicationQuit()
	{
		connectionUdp.StopListening ();
	}

	private void udpDataReceived(object sender, NetworkEvent e)
	{
		string remoteIp = e.remoteIp.Address.ToString();
		xml.LoadXml (e.Data);
		string command = xml.DocumentElement.SelectSingleNode ("/pong/com").InnerText;

		if (command == "SEARCH") 
		{
			TcpConnection connectionTcp = new TcpConnection();
			if(!GameManager.pleyer1Connected)
			{
				connectionTcp.SendTcpResponse("<pong><com>ACCEPTED</com><player>1</player></pong>", remoteIp, 10405);
				GameManager.pleyer1Connected = true;
			}
			else if (!GameManager.pleyer2Connected)
			{
				connectionTcp.SendTcpResponse("<pong><com>ACCEPTED</com><player>2</player></pong>", remoteIp, 10405);
				GameManager.pleyer2Connected = true;
			}
			else
				connectionTcp.SendTcpResponse("<pong><com>DENIED</com><player></player></pong>", remoteIp, 10405);
		}

		if (command == "MOVE") 
		{
			string player = xml.DocumentElement.SelectSingleNode ("/pong/player").InnerText;
			string action = xml.DocumentElement.SelectSingleNode ("/pong/action").InnerText;
			if (player == "1")
				move1 = action;
			if (player == "2")
				move2 = action;
		}
	}
}
