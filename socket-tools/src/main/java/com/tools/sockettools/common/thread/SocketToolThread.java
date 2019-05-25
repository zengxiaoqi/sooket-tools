/**
 * Copyright ® 2012 Eastcom Co. Ltd.
 * All right reserved.
 */

package com.tools.sockettools.common.thread;


/**
 * ilink public thread
 * by shif
 */
public class SocketToolThread extends Thread {
	private Runnable runnable;
	
	public SocketToolThread(Runnable runnable){
		this.runnable = runnable;
	}

	@Override
	public void run() {
		//LogMdc.put("thread_id", String.format("%06d", Thread.currentThread().getId()));
		this.runnable.run();
	}
	

}
