package com.tools.sockettools.tcp.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tools.sockettools.tcp.exception.TcpException;
import com.tools.sockettools.tcp.msghandle.MsgHandlerDo;
import lombok.extern.slf4j.Slf4j;

/**
 * TCP适配器服务端处理类
 * @author PanXT
 *
 */
@Slf4j
public class TcpServer {
	private List<SocketChannel> sockelist = new ArrayList<SocketChannel>();
//	private ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);
	private ServerSocketChannel serverSocketChannel;
	private MsgHandlerDo msghandle;
	private Selector selector;
	private Thread thread;
	private boolean isPortOpen;
	private String portOpenMsg;
	private String serverip;
	private int port;
	private int serverId;
//	private boolean isRecv = false;

	public TcpServer(String serverip, int inport, MsgHandlerDo msghandle, int serverId) {
		this.serverip = serverip;
		this.port = inport;
		this.msghandle = msghandle;
		this.serverId = serverId;
		initServer();
	}

	public void initServer() {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			if ((serverSocketChannel.isOpen()) && (selector.isOpen())) {
				serverSocketChannel.configureBlocking(false);
				serverSocketChannel.socket().bind(new InetSocketAddress(serverip, port), Integer.MAX_VALUE);
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				isPortOpen = true;
				thread = new Thread(new workhead());
				thread.start();
			}
		} catch (IOException ex) {
			isPortOpen = false;
			portOpenMsg = ex.getMessage();
			log.error(ex.getMessage());
		}
	}

	public String getPortOpenMsg() {
		return portOpenMsg;
	}

	public void setPortOpenMsg(String portOpenMsg) {
		this.portOpenMsg = portOpenMsg;
	}

	public void stopServer() {
		try {
			isPortOpen = false;
			thread.interrupt();
			serverSocketChannel.close();
			selector.close();
		} catch (IOException e) {
			log.error("Error in tcp server stop: "+e.getMessage());
		} 
	}

	public boolean getIsOpen() {
		return isPortOpen;
	}

	class workhead implements Runnable {
		@Override
		public void run() {
			while (!Thread.interrupted()) {
				if (!isPortOpen) {
					return;
				}
				try {
					selector.select();
					Iterator<?> keys = selector.selectedKeys().iterator();
					while (keys.hasNext()) {
						SelectionKey key = (SelectionKey) keys.next();
						keys.remove();
						if (!key.isValid()) {
							continue;
						}
						if (key.isAcceptable()) {
							ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
							SocketChannel socketChannel = serverChannel.accept();
							socketChannel.configureBlocking(false);
							socketChannel.register(selector, SelectionKey.OP_READ);
							sockelist.add(socketChannel);
							log.debug("Conn:[{}]<-[{}]", socketChannel.socket().getLocalSocketAddress().toString(), socketChannel.socket()
									.getRemoteSocketAddress().toString());
						} else if (key.isReadable()) {
							SocketChannel socketChannel = (SocketChannel) key.channel();
							log.debug("Receving data[{}]<-[{}]", socketChannel.socket().getLocalSocketAddress().toString(), socketChannel.socket()
									.getRemoteSocketAddress().toString());
							try {
//								if(isRecv) {
//									msghandle.doRecvHandler(socketChannel, key,MsgHandlerDo.RETURN);
//								}else {
									msghandle.doRecvHandler(key);
//								}
								
							} catch (TcpException e) {
								sockelist.remove(socketChannel);
								log.error(e.getMessage());
							}
						}
					}
				} catch (IOException e) {
					log.error("Error in tcp serve workhead",e);

				}
			}
		}
	}

	public void sendMsg(SocketChannel socketChannel, String msg) {
		if (socketChannel != null) {
			try {
				socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}

	public ServerSocketChannel getServerSocketChannel() {
		return serverSocketChannel;
	}

	public void setServerSocketChannel(ServerSocketChannel serverSocketChannel) {
		this.serverSocketChannel = serverSocketChannel;
	}

	public boolean isPortOpen() {
		return isPortOpen;
	}

	public void setPortOpen(boolean isPortOpen) {
		this.isPortOpen = isPortOpen;
	}

	public String getServerip() {
		return serverip;
	}

	public void setServerip(String serverip) {
		this.serverip = serverip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

}