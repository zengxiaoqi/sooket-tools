package com.tools.sockettools.tcp.timer;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * 中止连接
 * 
 * @author PanXT
 * 
 */
@Slf4j
public class CancelConnectionJob implements TimerRunJobInf {
	private Map<SelectionKey, Long> connectionMap;
	private ArrayList<SelectionKey> removeConnList = new ArrayList<SelectionKey>();
	private long timeout;
	private boolean isrun;

	public CancelConnectionJob(Map<SelectionKey, Long> connectionMap, long timeout) {
		this.connectionMap = connectionMap;
		this.timeout = timeout;
	}

	@Override
	public boolean runJob() {
		if (!isrun) {
			try {
				Thread.sleep(5000);
				isrun = true;
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
		}
		try {
			// logger.debug("start check if connection timeout..."+connectionMap.size());
			long timenow = System.currentTimeMillis();
			Map<SelectionKey, Long> connectionMapCopy = null;
			synchronized (connectionMap) {
				connectionMapCopy = new HashMap<SelectionKey, Long>(connectionMap);
			}
			Set<SelectionKey> set = connectionMapCopy.keySet();
			Iterator<SelectionKey> iterator = set.iterator();
			while (iterator.hasNext()) {
				SelectionKey sKey = iterator.next();
				long oldtime = connectionMapCopy.get(sKey);
				if ((timenow - oldtime) >= timeout) {
					try {
						SocketChannel socketChannel = (SocketChannel) sKey.channel();
						log.warn("Listener client: [{}] conneted timeout,[{}], will be close.", socketChannel.socket().getInetAddress(), timeout);
						sKey.cancel();
						socketChannel.close();
						removeConnList.add(sKey);
					} catch (IOException e) {
						log.error(e.getMessage());
					}
				}
			}
			for (int i = 0; i < removeConnList.size(); i++) {
				SelectionKey rkey = (SelectionKey) removeConnList.get(i);
				synchronized (connectionMap) {					
					connectionMap.remove(rkey);
					log.debug("key removed in connectionMap");
				}
			}
			removeConnList.clear();
			connectionMapCopy = null;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return false;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
