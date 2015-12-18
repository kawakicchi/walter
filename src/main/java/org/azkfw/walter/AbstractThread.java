package org.azkfw.walter;

public abstract class AbstractThread {

	private boolean runningFlag;
	private boolean stopFlag;
	
	public AbstractThread() {
		runningFlag = false;
		stopFlag = false;
	}
	
	public synchronized final boolean start() {
		if (runningFlag) {
			return false;
		}
		stopFlag = false;
		runningFlag = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				doTask();
				runningFlag = false;
			}
		});	
		thread.start();
		return true;
	}
	
	public final void stop() {
		stopFlag = true;
	}
	
	public final boolean isRunning() {
		return runningFlag;
	}
	
	protected abstract void doTask() ;
	
	protected final boolean isStop() {
		return stopFlag;
	}
}
