package com.tools.sockettools.tcp.start;


import com.tools.sockettools.tcp.exception.TcpException;

import java.nio.channels.Selector;

public class TcpConn implements Adapter {
	protected final int MAX_PACKAGE_LEN = 2048;
	protected final int SINGLE_SERVER = 0;
	protected final int SINGLE_CLINET = 1;
	protected final int DUPLEX_SERVER = 2;
	protected final int DUPLEX_CLIENT = 3;

	protected final String ADAPTER_ID = "AdapterId";
	protected final String ADAPTER_NAME = "AdapterName";
	protected final String CONNECTTION_NUM = "ConnectionNum";
	protected final String CONNECTION_TYPE = "ConnectionType";
	protected final String HEART_BEATS_TIME = "HeartBeatsTime";
	protected final String PACKAGE_DEF = "PackageDef";
	protected final String RECONNEC_TTIME = "ReconnectTime"; // time reconnect
	protected final String RECV_STRATEGY = "RecvStrategy";
	protected final String ADDRESS = "Address";
	protected final String CONNECT_TIMES = "ConnectTimes";
	protected final String TIME_OUT = "TimeOut";
	protected final String LOCAL_ADDRESS = "LocalAddress";
	protected final String REMOTE_ADDRESS = "RemoteAddress";
	protected final String IPREGEXP = "IpRegexp";
	protected final String TIMEOUT = "ConnectionTimeout";
	protected final String HEAD_LEN = "HeadLen";
	protected final String LEN_CODE = "LenCode";

	protected String channelId;
	protected String adapterId;
	protected String adapterName;

	@Override
	public byte[] invoke(Object conn, byte[] message) throws TcpException {
		return null;
	}

	@Override
	public Selector start() throws  TcpException {
		return null;
	}

	@Override
	public void stop() throws TcpException {

	}

	@Override
	public void close(Object conn) {

	}

	//需修改
	/*public SocketChannel getAdapterConnection(String seqId) throws ILinkOSGIServiceException, ILinkTcpAdapterException {
		ChannelBus channelBus = (ChannelBus) OsgiUtil.getService(ChannelBus.class);
		ChannelSession as = channelBus.getChannelSessionQueue().getSession(seqId);
		if (as == null)
			throw new ILinkTcpAdapterException(0, "adapter session not found");
		
		return (SocketChannel) as.getConnection();
	}*/
}
