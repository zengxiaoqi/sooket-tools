package com.tools.sockettools.http.server;
// ========================================================================
// Copyright 2004-2005 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ========================================================================


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.mortbay.component.LifeCycle;


/* ------------------------------------------------------------ */
/** Jetty ThreadPool using java 5 ThreadPoolExecutor
 * This class wraps a {@link ThreadPoolExecutor} with the {@link org.mortbay.thread.ThreadPool} and 
 * {@link LifeCycle} interfaces so that it may be used by the Jetty {@link org.mortbay.jetty.Server}
 * 
 * by shif
 *
 */
@Slf4j
public class JettyThreadPool implements org.mortbay.thread.ThreadPool, LifeCycle
{
 
	private ThreadPoolExecutor executor;
	private final int CORE_POOL_SIZE =  1000;
	private final int MAX_POOL_SIZE = 1000;
	private final int KEEP_ALIVE_TIME = 100;
	private final int QUEUE_SIZE = 100;
	

	public JettyThreadPool() {
		this.executor = new ThreadPoolExecutor(
				CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(QUEUE_SIZE),
				new ThreadPoolExecutor.DiscardOldestPolicy());
	}

    /* ------------------------------------------------------------ */
    public boolean dispatch(Runnable job)
    {
        try
        {       
        	executor.execute(job);
            return true;
        }
        catch(RejectedExecutionException e)
        {
            e.printStackTrace();
            log.error("execute error[{}]",e.getMessage());
            return false;
        }
    }

    /* ------------------------------------------------------------ */
    public int getIdleThreads()
    {
        return executor.getPoolSize()-executor.getActiveCount();
    }

    /* ------------------------------------------------------------ */
    public int getThreads()
    {
        return executor.getPoolSize();
    }

    /* ------------------------------------------------------------ */
    public boolean isLowOnThreads()
    {
        return executor.getActiveCount()>=executor.getMaximumPoolSize();
    }

    /* ------------------------------------------------------------ */
    public void join() throws InterruptedException
    {
    	executor.awaitTermination(Long.MAX_VALUE,TimeUnit.MILLISECONDS);
    }

    /* ------------------------------------------------------------ */
    public boolean isFailed()
    {
        return false;
    }

    /* ------------------------------------------------------------ */
    public boolean isRunning()
    {
        return !executor.isTerminated() && !executor.isTerminating();
    }

    /* ------------------------------------------------------------ */
    public boolean isStarted()
    {
        return !executor.isTerminated() && !executor.isTerminating();
    }

    /* ------------------------------------------------------------ */
    public boolean isStarting()
    {
        return false;
    }

    /* ------------------------------------------------------------ */
    public boolean isStopped()
    {
        return executor.isTerminated();
    }

    /* ------------------------------------------------------------ */
    public boolean isStopping()
    {
        return executor.isTerminating();
    }

    /* ------------------------------------------------------------ */
    public void start() throws Exception
    {
        if (executor.isTerminated() || executor.isTerminating() || executor.isShutdown())
            throw new IllegalStateException("Cannot restart");
    }

    /* ------------------------------------------------------------ */
    public void stop() throws Exception
    {
    	executor.shutdown();
        if (!executor.awaitTermination(60,TimeUnit.SECONDS))
        	executor.shutdownNow();
//        ExecutorUtils.shutdownAndAwaitTermination(executor);
    }

    /* ------------------------------------------------------------ */
    public void addLifeCycleListener(Listener listener)
    {
        throw new UnsupportedOperationException();
    }
    
    /* ------------------------------------------------------------ */
    public void removeLifeCycleListener(Listener listener)
    {
    }
    
}
