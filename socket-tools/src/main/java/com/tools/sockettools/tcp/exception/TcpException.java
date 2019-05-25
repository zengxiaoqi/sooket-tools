package com.tools.sockettools.tcp.exception;

import lombok.Data;

/**
 * TCP Adapter 异常类
 * @author zengxq
 *
 */
@Data
public class TcpException extends RuntimeException {
	private String errorType;
	private long errorCode;
	private String errorMsg;
	private static final long serialVersionUID = 987755498847225774L;

	public TcpException(String errorType, long errorCode, String errorMsg) {
		super(errorType+":"+errorCode+":"+errorMsg);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public TcpException(String errorType, long errorCode, String errorMsg, Throwable e) {
		super(errorType+":"+errorCode+":"+e.getMessage(), e);
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
}
