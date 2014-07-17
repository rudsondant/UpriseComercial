using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;

abstract public class Connection
{
        #region variaveis
        public enum state {ERROR, UDP_LISTENING, TCP_LISTENING, UDP_STOPPED, TCP_STOPPED, CLIENT_ACCEPTED, OPENING_SOCKET };
        protected state currentState;
        protected int localPort, remotePort;
        protected string receivedData, dataToSend;
        protected static bool isListening;
        protected IPEndPoint localAddress, remoteAddress;
        protected Thread thread;
        protected NetworkEvent netEvent;
        #endregion

        public abstract void StartListening(int port);
        public abstract void StopListening();
        public abstract state GetCurrentState();
}

