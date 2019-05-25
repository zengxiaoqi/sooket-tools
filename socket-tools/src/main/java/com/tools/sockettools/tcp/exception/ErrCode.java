package com.tools.sockettools.tcp.exception;

public class ErrCode {
	// Http适配器异常  4051-4100
	public static final long HttpCallerErr = 4051; // Http呼出异常
	public static final long HttpHandleErr = 4052; // Http 处理异常
	public static final long HttpSenderErr = 4053; // Http发送异常
	public static final long HttpCloseErr = 4054;  // Http服务关闭异常
	public static final long HttpConnErr = 4055;   // Http连接异常
	//TCP Adapter
	public static final long MsgHeadErr = 4056;        //报文头异常
	public static final long ReadMsgErr = 4057;        //读报文内容异常
	public static final long MsgNotEnoughtErr = 4058;  //报文内容长度不够
	public static final long MsgIsNullErr = 4059;      //消息为空
	public static final long SocketClosedErr = 4060;   //Socket close exception.
	public static final long AdapterInvokeErr = 4061;  //Adapter invoke exception.
	public static final long TcpCallConnErr = 4062;    //TCP Call connection exception.
	public static final long TcpCallSendErr = 4063;    //TCP Call send exception.
	public static final long TcpCallRecvErr = 4064;    //TCP Call Recv exception.
	public static final long TcpCallRecvTimeOutErr = 4065;  //TCP协议接收超时
	public static final long TcpListenerErr = 4066;         
	public static final long WebServiceStartErr = 4067;
	public static final long WebServiceExecErr = 4067;
	public static final long AdapterSendDB = 4068;
	public static final long AdapterOutToInner = 4069;
	public static final long AdapterInnerToOut = 4070;

	//TCP Adapter
	public static final String TcpReadSocketException = "TcpReadSocketException";
	public static final String TcpSocketClosedException = "TcpSocketClosedException";
	public static final String TcpReadSocketNotEnoughtException = "TcpReadSocketNotEnoughtException";
	public static final String TcpConnectionException = "TcpConnectionException";
	public static final String TcpListenerException = "TcpListenerException";
	public static final String TcpMsgHandlerException = "TcpMsgHandlerException";
	public static final String TcpRecverException = "TcpRecverException";
	public static final String TcpSenderException = "TcpSenderException";
}
