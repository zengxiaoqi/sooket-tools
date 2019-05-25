/**
 * Copyright ® 2012 Eastcom Co. Ltd.
 * All right reserved.
 */
package com.tools.sockettools.tcp.start;


import com.tools.sockettools.common.util.ByteUtil;
import com.tools.sockettools.tcp.exception.ErrCode;
import com.tools.sockettools.tcp.exception.TcpException;
import com.tools.sockettools.tcp.server.TcpListener;
import com.tools.sockettools.tcp.timer.CancelConnectionJob;
import com.tools.sockettools.tcp.timer.TimerUtil;
import com.tools.sockettools.tcp.msghandle.MsgHandlerDo;
import com.tools.sockettools.tcp.msghandle.MsgHandlerFixedData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Map;

@Slf4j
public class TcpConnShortServer extends TcpConn {
	// must be thread safe
	private Map<SelectionKey, Long> connectionMap = new Hashtable<SelectionKey, Long>();
	private long timeout;
	private String ipRegexp;
	private InetSocketAddress address;
	private TcpListener listener;
	private MsgHandlerDo msgHandler;
	private final int MAX_PACKAGE_LEN = 2048;

	@SuppressWarnings("rawtypes")
	public TcpConnShortServer(Map config) {
		log.debug("TcpShortServer config: [{}]", config);
		this.timeout = Long.valueOf((String) config.get(TIMEOUT));
		String addressString = (String) config.get(ADDRESS);

		String[] set = addressString.split(":");
		address = new InetSocketAddress(Integer.valueOf(set[1]));

		if (Integer.valueOf((String) config.get(RECV_STRATEGY)) == 0) {
			this.msgHandler = new MsgHandlerFixedData(Integer.valueOf((String) config.get(HEAD_LEN)),
					(String) config.get(LEN_CODE), timeout);
		}
		listener = new TcpListener(address, ipRegexp, timeout, msgHandler, connectionMap);
		TimerUtil timeheart = new TimerUtil(new CancelConnectionJob(connectionMap, timeout), timeout);
		timeheart.startTimer();
	}

	/**
	 * @see
	 */
	@Override
	public byte[] invoke(Object conn, byte[] message) throws TcpException {
		SelectionKey sk = (SelectionKey) conn;
		SocketChannel socketChannel = (SocketChannel) sk.channel();
		if (message == null || message.length < 1) {
			closeQuiet(socketChannel);
			throw new TcpException(ErrCode.TcpSenderException,ErrCode.MsgIsNullErr, "TcpShortShortServer send null msg");
		}
		if (socketChannel != null){
			log.info("组包完成:{}", ByteUtil.dumphex(message));
			log.info("组包完成:{}\n", new String(message));
			log.debug("返回数据到C端." + socketChannel.socket().getLocalAddress().toString() + "->"
					+ socketChannel.socket().getRemoteSocketAddress().toString());}
		try {
			ByteBuffer buffer = ByteBuffer.allocate(MAX_PACKAGE_LEN);
			int totle = 0;
			while (true) {
				if (message.length == 0)
					break;
				if (totle == message.length)
					break;
				int len = 0;
				if ((message.length - totle) < buffer.remaining())
					len = message.length - totle;
				else
					len = buffer.remaining();
				buffer.put(message, totle, len);
				buffer.flip();
				int bytes = socketChannel.write(buffer);
				buffer.clear();
				totle += bytes;
			}

		} catch (IOException e) {
			closeQuiet(socketChannel);
			throw new TcpException(ErrCode.TcpSenderException,ErrCode.AdapterInvokeErr,"send error", e);
		}
		return null;
	}

	public void closeQuiet(SocketChannel sc) {
		try {
			sc.close();
			//MDC.clear();    //Modify by DK
		} catch (IOException ex) {
			log.error("Error in close socket."+ ex.getMessage());
		}
	}

	@Override
	public void close(Object conn) {
		log.debug("###发送完成，关闭连接");
		SelectionKey sk = (SelectionKey) conn;
		SocketChannel socketChannel = (SocketChannel) sk.channel();

		// 根据根据Channel的附件来判断是否需要关闭并唤醒不需要关闭的Channel
		boolean closeSocket = true;
		final Object obj = sk.attachment();
		if (obj != null && Boolean.FALSE.equals(obj)) {
			closeSocket = false;
			sk.interestOps(sk.interestOps() | SelectionKey.OP_READ);
			sk.selector().wakeup();
		}
		if (closeSocket) {
			sk.cancel();
			closeQuiet(socketChannel);
		}
	}

	/**
	 * @throws TcpException
	 * @see
	 */
	@Override
	public Selector start() throws TcpException {
		log.debug("TcpShortServer listener start ");
		return listener.start();
	}

	/**
	 * @throws TcpException
	 * @see
	 */
	@Override
	public void stop() throws TcpException {
		log.debug("TcpShortServer stop adapterid: [{}]", adapterId);
		listener.stop();
	}

}
