package com.tools.sockettools.tcp.msghandle;


import com.tools.sockettools.tcp.exception.TcpException;
import lombok.extern.slf4j.Slf4j;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 数据包处理
 */
@Slf4j
public abstract class MsgHandlerDo {
    public final static int RETURN = 0;
    public final static int NO_RETURN = 1;
    public final int MAX_PACKAGE_LEN = 1024 * 100;
    public String channelId;
    public String adapterId;

    public abstract void doRecvHandler(SelectionKey selectionKey) throws TcpException;

    public abstract void doStopHandler();

    public abstract byte[] doRecvHandler(SocketChannel socketChannel, SelectionKey selectionKey)
            throws TcpException;

    public abstract byte[] doRecvHandler(SocketChannel socketChannel, SelectionKey selectionKey, int recvType)
            throws TcpException;

    /**
     * Send message to Channel Bus
     */
    protected void submitToChannelBus(final SelectionKey sk) {
        //接收报文
        //应答报文
    }

}
