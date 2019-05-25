/**
 * Copyright ® 2012 Eastcom Co. Ltd.
 * All right reserved.
 */

package com.tools.sockettools.tcp.msghandle;

import com.tools.sockettools.common.util.BcdUtil;
import com.tools.sockettools.common.util.ByteUtil;
import com.tools.sockettools.tcp.exception.ErrCode;
import com.tools.sockettools.tcp.exception.TcpException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * 数据包处理
 * 
 */
@Slf4j
public class MsgHandlerFixedData extends MsgHandlerDo {

	private int headLen;
	private String lenCode;
	private long dataRecvTimeOut;

	public MsgHandlerFixedData(int headLen, String lenCode, long dataRecvTimeOut) {
		this.headLen = headLen;
		this.lenCode = lenCode;
		this.dataRecvTimeOut = dataRecvTimeOut;
	}

	@Override
	public void doRecvHandler(SelectionKey selectionKey) throws TcpException {
		SocketChannel channel = (SocketChannel) selectionKey.channel();
		recvMsg(channel, selectionKey, NO_RETURN);
	}

	@Override
	public byte[] doRecvHandler(SocketChannel socketChannel, SelectionKey selectionKey, int recvType)
			throws TcpException {
		return recvMsg(socketChannel, selectionKey, recvType);
	}

	private byte[] recvMsg(SocketChannel socketChannel, SelectionKey selectionKey, int recvType)
			throws TcpException {
		try {
			// LogMdc.setChannelAdapterNo(channelId, adapterId);
			
			if (recvType == RETURN) {
				byte[] pack = readPackage(socketChannel, selectionKey);
				log.info("接收到数据:{}",ByteUtil.dumphex(pack));
				log.info("接收到数据:{}",new String(pack));
				socketChannel.close();
				return pack;
			} else {
				byte[] msg = readPackage(socketChannel, selectionKey);
                log.info("接收到数据:{}",ByteUtil.dumphex(msg));
                log.info("接收到数据:{}",new String(msg));
				return null;
			}
		} catch (TcpException e1) {
			throw e1;
		} catch (Exception e) {
			throw new TcpException(ErrCode.TcpReadSocketException,ErrCode.ReadMsgErr, "socket read error",e);

		}
	}

	/*private byte[] readPackage(SocketChannel socketChannel, SelectionKey selectionKey) throws IOException {
        //获取缓冲器并进行重置,selectionKey.attachment()为获取选择器键的附加对象
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024*100);
        byteBuffer.clear();
        //没有内容则关闭通道
        if(socketChannel.read(byteBuffer) == -1) {
            log.error("没有收到报文.....");
            socketChannel.close();
        }else{
            //将缓冲器转换为读状态
            byteBuffer.flip();
            //将缓冲器中接收到的值按localCharset格式编码保存
            *//*String receivedRequestData = Charset.forName("UTF-8").newDecoder().decode(byteBuffer).toString();
            log.info("接收到客户端的请求数据："+ receivedRequestData);*//*
            *//*
            //返回响应数据给客户端
            String responseData ="已接收到你的请求数据，响应数据为：(响应数据)";
            byteBuffer =ByteBuffer.wrap(responseData.getBytes("UTF-8"));
            socketChannel.write(byteBuffer);
            //关闭通道
            socketChannel.close();
            *//*
        }
        return byteBuffer.array();
	}*/
	private byte[] readPackage(SocketChannel socketChannel, SelectionKey selectionKey)
			throws TcpException {
		try {
			byte[] head = readMsg(socketChannel, selectionKey, headLen);

			if( head == null) {
				return null;
			}
			int packeLen = 0;
			log.debug("MsgHandlerFixedData readPackage lenCode :{}", lenCode);
			if (lenCode.equalsIgnoreCase("ASCII")) {
				packeLen = Integer.valueOf(new String(head));
			} else if (lenCode.equalsIgnoreCase("BCD")) {
				packeLen = BcdUtil.bcdToInt(head, 1);
			} else if (lenCode.equalsIgnoreCase("HEX")) {
				packeLen = BcdUtil.bcdToInt(head, 0);
			} else {
				packeLen = 0;
			}

			//packeLen += reduce;
			if (packeLen < 0) {
				packeLen = 0;
			}
			if (packeLen == 0) {
				log.debug("FixedMsgHandler recv heartbeats");
				return null;
			}
			byte[] body = readMsg(socketChannel, selectionKey, packeLen);
			ByteBuffer buffer = ByteBuffer.allocate(headLen + packeLen);
			buffer.put(head);
			buffer.put(body);
			buffer.flip();
			return buffer.array();
		} catch (NumberFormatException e1) {
			throw new TcpException(ErrCode.TcpMsgHandlerException, ErrCode.MsgHeadErr, "Read Message head error!", e1);
		}
	}

	/**
	 * read msg from socket
	 */
	private byte[] readMsg(SocketChannel socketChannel, SelectionKey selectionKey, int length)
			throws TcpException {
		ByteBuffer buffer = ByteBuffer.allocate(length);
		long times = 5000;
		try {
			int len = 0;
			int read;
			long beginTime = System.currentTimeMillis();
			long nowTime = 0;
			while (len < length) {
				read = socketChannel.read(buffer);
				if (read == -1) {
					if (selectionKey != null)
						selectionKey.cancel();
					socketChannel.close();
					throw new TcpException(ErrCode.TcpSocketClosedException,ErrCode.SocketClosedErr, "Socket closed by Remote");
				}
				if (read > 0) {
					len += read;
					log.debug("[ChannelId:{} AdatperId:{}], Alread read msg length[{}]", channelId, adapterId, length);
				}
				if (dataRecvTimeOut > 0) {
					nowTime = System.currentTimeMillis();
					if ((nowTime - beginTime) > dataRecvTimeOut) {
						log.debug("[ChannelId:{} AdatperId:{}],readMsg timeout [{}],need [{}] already read [{}]",
								channelId, adapterId, dataRecvTimeOut, length, len);
						break;
					}
				} else {
					nowTime = System.currentTimeMillis();
					if ((nowTime - beginTime) > times) {
						log.debug("[ChannelId:{} AdatperId:{}],Read spend time [{}]ms, waiting...", channelId,
								adapterId, times);
						times = times + 5000;
					}
				}
			}
		} catch (TcpException e) {
			throw e;
		} catch (Exception e) {
			throw new TcpException(ErrCode.TcpReadSocketException,ErrCode.ReadMsgErr,"read msg error", e);
		}
		buffer.flip();
		if (buffer.limit() < length)
			throw new TcpException(ErrCode.TcpReadSocketNotEnoughtException,ErrCode.MsgNotEnoughtErr,
					"FixedMsgHandler recv msg not enought");
		return buffer.array();
	}

	@Override
	public void doStopHandler() {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] doRecvHandler(SocketChannel socketChannel, SelectionKey selectionKey) {
		// TODO Auto-generated method stub
		return null;
	}
}
