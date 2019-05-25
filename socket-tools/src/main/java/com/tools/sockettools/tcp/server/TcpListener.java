/**
 * Copyright ® 2012 Eastcom Co. Ltd.
 * All right reserved.
 */

package com.tools.sockettools.tcp.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tools.sockettools.common.thread.SocketToolThread;
import com.tools.sockettools.common.thread.SocketToolThreadFactory;
import com.tools.sockettools.tcp.exception.ErrCode;
import com.tools.sockettools.tcp.exception.TcpException;
import com.tools.sockettools.tcp.msghandle.MsgHandlerDo;
import lombok.extern.slf4j.Slf4j;

/**
 * Class: Listener (Short Server Component)
 */
@Slf4j
public class TcpListener {
	// private int recTotal = 0;
	private InetSocketAddress localAddress;
	private Map<SelectionKey, Long> connectionMap;
	private String ipRegexp;
	private long timeout;
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;
	private SelectionKey selectionKey;
	private MsgHandlerDo msgHandler;
	// private Timer timer;
	private boolean runflag;
	// private boolean closeByFlag;
	private static ExecutorService selectorThreadPool = Executors.newCachedThreadPool(new SocketToolThreadFactory(
			"Adapter#TcpSelector"));
	private static ExecutorService recvThreadPool = Executors.newCachedThreadPool(new SocketToolThreadFactory(
			"Adapter#TcpRecv"));

	public TcpListener(){

	}
	public TcpListener(InetSocketAddress address, String ipRegexp, long timeout,
			MsgHandlerDo msgHandler, Map<SelectionKey, Long> connectionMap) {
		try {
			this.localAddress = address;
			this.ipRegexp = "/" + ipRegexp;
			this.setTimeout(timeout);
			this.msgHandler = msgHandler;
			this.connectionMap = connectionMap;
			selector = Selector.open();
		} catch (IOException e) {
			log.error("error: "+e.getMessage());
		}
	}

	/**
	 * start listener
	 */
	public Selector start() throws TcpException {
		log.debug("[{}],Listener start listener", localAddress.getHostName() + ":" + localAddress.getPort());
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().setReuseAddress(true);
			serverSocketChannel.socket().bind(localAddress);
			selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			throw new TcpException(ErrCode.TcpListenerException,ErrCode.TcpListenerErr,localAddress.getHostName() + ":" + localAddress.getPort() , e);
		}
		runflag = true;
		selectorThreadPool.submit(new SocketRunnable());

		return selector;
	}

	/**
	 * stop listener
	 */
	public void stop() throws TcpException {
		log.debug("Listener stop listener");
		try {
			selectionKey.cancel();
			serverSocketChannel.close();
			// thread.stop();
			runflag = false;
			selector.wakeup();
		} catch (IOException e) {
			throw new TcpException(ErrCode.TcpListenerException,ErrCode.TcpListenerErr,"socket stop error", e);

		}
	}

	/**
	 * @see Runnable#run()
	 */
	class SocketRunnable implements Runnable {
		@Override
		public void run() {
			try {
				while (selector.select() >= 0) {
					if (!runflag)
						break;
					Set<SelectionKey> readyKey = selector.selectedKeys();
					Iterator<SelectionKey> iterator = readyKey.iterator();
					/*while (iterator.hasNext())*/ {
						SelectionKey selectionKey = (SelectionKey) iterator.next();
						//iterator.remove();

						if (!selectionKey.isValid()) {
							log.debug("Listener selectionkey: [{}] canncel", selectionKey.toString());
							selectionKey.cancel();
							continue;
						}
						if (selectionKey.isAcceptable()) {
							ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
							SocketChannel socketChannel = serverSocketChannel.accept();
							socketChannel.configureBlocking(false);
                            log.debug("###TCP->接收 [{}]->[{}]", socketChannel.socket().getRemoteSocketAddress()
                                    .toString(), socketChannel.socket().getLocalSocketAddress().toString());
                            SelectionKey sKey = socketChannel.register(selector, SelectionKey.OP_READ);
                            connectionMap.put(sKey, System.currentTimeMillis());
						} else if (selectionKey.isReadable()) {
							selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_READ);
							SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
							/*LogMdc.setChnFolderGroup(channelId);
							LogMdc.setChannelAdapterNo(channelId, adapterId);*/
							// logger.debug("Comming Data:[{}]->[{}]",
							// socketChannel.socket().getRemoteSocketAddress()
							// .toString(),
							// socketChannel.socket().getLocalSocketAddress().toString());
							recvThreadPool.submit(new RecvThread(selectionKey));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("接收报文异常："+e.getMessage());
			}
		}

	}

	/**
	 * check ip address use regex
	 */
	private boolean checkIp(String address) {
		// sample address /127.0.0.1:12835
		Pattern pattern = Pattern.compile(ipRegexp);
		Matcher matcher = pattern.matcher(address);
		return matcher.matches();
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	private class RecvThread implements Runnable {
		private SelectionKey selectionKey;
		private long sTime;

		public RecvThread(SelectionKey selectionKey) {
			this.selectionKey = selectionKey;
			sTime = System.currentTimeMillis();
		}

		@Override
		public void run() {
			try {
				//MonCounter.recvThreadCount();
				sTime = System.currentTimeMillis();
				msgHandler.doRecvHandler(selectionKey);
				log.debug("接收报文 cost# {} ms", System.currentTimeMillis() - sTime);
			} catch (TcpException e1) {
				e1.printStackTrace();
				SocketChannel channel = (SocketChannel) selectionKey.channel();
				try {
					channel.close();
					selectionKey.cancel();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
				log.error(e1.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
	}
}
