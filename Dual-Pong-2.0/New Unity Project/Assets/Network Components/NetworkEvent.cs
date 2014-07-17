using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Net.Sockets;

public class NetworkEvent : EventArgs
{
    public string Data { get; set; }
    public IPEndPoint remoteIp { get; set; }
    public SocketException error { get; set; }
    public string errorMessage { get; set; }
    public Connection.state state { get; set; }

    public void Clear()
    {
        Data = null;
        remoteIp = null;
        error = null;
        errorMessage = null;
    }
}

