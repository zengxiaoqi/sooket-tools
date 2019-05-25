/**
 * Copyright ® 2012 Eastcom Co. Ltd.
 * All right reserved.
 */

package com.tools.sockettools.tcp.start;

import com.tools.sockettools.tcp.exception.TcpException;

import java.nio.channels.Selector;

/**
 * 
 * @author PanXT
 * 
 */
public interface Adapter {

	public abstract byte[] invoke(Object conn, byte[] message) throws TcpException;

	public abstract void close(Object conn);

	public abstract Selector start() throws TcpException;

	public abstract void stop() throws TcpException;

}
