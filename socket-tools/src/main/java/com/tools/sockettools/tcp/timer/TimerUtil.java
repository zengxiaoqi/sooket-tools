package com.tools.sockettools.tcp.timer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author PanXiaotan
 *
 */
public class TimerUtil {
	private Timer timer = new Timer();
	private TimerRunJobInf runjob;
	private long interval = 10000;

	public TimerUtil(TimerRunJobInf runjob, long interval) {
		this.runjob = runjob;
		this.interval = interval;
	}

	public void startTimer() {
		timer.schedule(task, (long) 0, interval);
	}

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			runjob.runJob();
		}
	};

	public void stopTimer() {
		timer.cancel();
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

}
