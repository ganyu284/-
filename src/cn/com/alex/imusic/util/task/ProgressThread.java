package cn.com.alex.imusic.util.task;

public class ProgressThread implements Runnable {
	private boolean running = false;
	private boolean waiting = false;
	private Thread thread;
	// Ҫִ�е�����
	private TaskInterface task;
	// ��������Ĭ��Ϊ1��
	private long interval = 1000;

	// TaskInterface - Ҫִ�е�����
	// interval - ����ʱ��
	public ProgressThread(TaskInterface task, long interval) {
		thread = new Thread(this);
		this.task = task;
		this.interval = interval;
	}

	// �߳����壬��Ҫѭ��ִ�еķ�����������ִ��
	public void run() {
		for (;;) {
			// �����running״̬����ִ������
			if (running) {
				// �˴�����Ҫѭ��ִ�еķ��������ӿ�TaskInterface�е�task()����
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

				// ��������
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// ��ʼִ�з���
	public void start() {
		running = true;
		// ����̴߳��ڵȴ�״̬�����ѣ����Ҹı�־λ
		if (thread.getState() == Thread.State.WAITING) {
			synchronized (this) {
				this.running = true;
				this.waiting = false;
				this.notifyAll();
			}
		}

		// ����߳��Ѿ�����ֹ�ˣ����½��߳�
		if (thread.getState() == Thread.State.TERMINATED)
			thread = new Thread(this);
		// ����߳����½�״̬��������
		if (thread.getState() == Thread.State.NEW)
			thread.start();
	}

	// ��ִͣ��
	public void pause() {
		if (waiting) {
			return;
		}
		synchronized (this) {
			this.waiting = true;
		}
	}

	// ����ִ���жϵķ���
	public void resume() {
		if (!waiting) {
			return;
		}
		synchronized (this) {
			this.waiting = false;
			this.notifyAll();
		}
	}

	// ִֹͣ�з���
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