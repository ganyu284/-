package cn.com.alex.imusic.util.task;

public class ProgressThread implements Runnable {
	private boolean running = false;
	private boolean waiting = false;
	private Thread thread;
	// 要执行的任务
	private TaskInterface task;
	// 任务间隔，默认为1秒
	private long interval = 1000;

	// TaskInterface - 要执行的任务
	// interval - 休眠时间
	public ProgressThread(TaskInterface task, long interval) {
		thread = new Thread(this);
		this.task = task;
		this.interval = interval;
	}

	// 线程主体，需要循环执行的方法放在这里执行
	public void run() {
		for (;;) {
			// 如果是running状态，则执行任务
			if (running) {
				// 此处调用要循环执行的方法，即接口TaskInterface中的task()方法
				task.task();
			}
			try {
				synchronized (this) {
					if (!running) {
						break;
					}
					if (waiting) {
						this.wait();
					}
				}

				// 休眠周期
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// 开始执行方法
	public void start() {
		running = true;
		// 如果线程处于等待状态，则唤醒，并且改标志位
		if (thread.getState() == Thread.State.WAITING) {
			synchronized (this) {
				this.running = true;
				this.waiting = false;
				this.notifyAll();
			}
		}

		// 如果线程已经被终止了，则新建线程
		if (thread.getState() == Thread.State.TERMINATED)
			thread = new Thread(this);
		// 如果线程是新建状态，则启动
		if (thread.getState() == Thread.State.NEW)
			thread.start();
	}

	// 暂停执行
	public void pause() {
		if (waiting) {
			return;
		}
		synchronized (this) {
			this.waiting = true;
		}
	}

	// 重新执行中断的方法
	public void resume() {
		if (!waiting) {
			return;
		}
		synchronized (this) {
			this.waiting = false;
			this.notifyAll();
		}
	}

	// 停止执行方法
	public void stop() {
		if (!running) {
			return;
		}
		synchronized (this) {
			this.running = false;
			this.waiting = false;
			this.notifyAll();
		}
	}

	public void setinterval(long interval) {
		this.interval = interval;
	}

	public long getinterval() {
		return interval;
	}
}