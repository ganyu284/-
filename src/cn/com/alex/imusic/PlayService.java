package cn.com.alex.imusic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcelable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import cn.com.alex.imusic.bean.Music;
import cn.com.alex.imusic.util.PlayListHelper;
import cn.com.alex.imusic.util.PreferenceHelper;
import cn.com.alex.imusic.util.task.ProgressThread;
import cn.com.alex.imusic.util.task.TaskInterface;

public class PlayService extends Service implements MediaPlayer.OnErrorListener {
	static MediaPlayer player;

	ServiceReceiver receiver;
	ProgressReceiver progressReceiver;
	FlagReceiver flagReceiver;
	public static int status = PlayListHelper.STATUS_STOPPED;
	String song_id = null;

	Notification notif;
	NotificationManager notiMgr;
	//
	private int step = 0;
	private final int STEP_START = 0;
	private final int STEP_PLAY_PAUSE = 1;
	private final int STEP_NEXT = 2;
	private final int STEP_PREVIOUS = 3;

	// ���ͽ��������ȵ��߳�
	private ProgressThread progressThread;

	private Music music;

	// ��ǰ���ŵĸ���·��
	private String current_song_path;

	private Intent progressIntent;

	private Intent sendIntent;

	private int current_progress_position = 0;

	//
	private int current_pos = -1;
	private int max_pos = 0;

	private SensorManager sm;
	private ShakeSensorListener sel;
	Sensor sensor;

	private int shuffleFlag;

	private int repeatFlag;

	// �Ƿ�ζ�������һ��
	private boolean shakeNext = true;
	// �ζ��̶�
	private int shake_strength = 300;
	public static ArrayList<Music> playingList = new ArrayList<Music>();
	public ArrayList<Music> originalList = new ArrayList<Music>();

	private cn.com.alex.imusic.PlayService.PreferenceReceiver prefReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// System.out.println("PlayService onCreate");
		status = PlayListHelper.STATUS_STOPPED; // 1;

		if (player == null) {
			player = new MediaPlayer();
			player.setOnCompletionListener(new CompletionListener());

		}

		if (progressThread == null) {
			TaskInterface task = new TaskInterface() {
				public void task() {
					sendProgress(); // ���ͽ���
				}
			};
			progressThread = new ProgressThread(task, 1000);
		}

		progressIntent = new Intent(PlayListHelper.INTENT_ACTION_PROGRESS);

		receiver = new ServiceReceiver();
		IntentFilter controlFilter = new IntentFilter();
		controlFilter.addAction(PlayListHelper.INTENT_ACTION_CONTROL);
		this.registerReceiver(receiver, controlFilter);

		progressReceiver = new ProgressReceiver();
		IntentFilter progressFilter = new IntentFilter();
		progressFilter.addAction(PlayListHelper.INTENT_ACTION_SEEK_PROGRESS);
		this.registerReceiver(progressReceiver, progressFilter);

		flagReceiver = new FlagReceiver();
		IntentFilter flagFilter = new IntentFilter();
		flagFilter.addAction(PlayListHelper.INTENT_ACTION_FLAG);
		this.registerReceiver(flagReceiver, flagFilter);

		// �ظ�\���״̬
		int shuffleValue = PreferenceHelper.readShuffle(this
				.getApplicationContext());
		shuffleFlag = (int) Math.log10(shuffleValue);

		int repeatValue = PreferenceHelper.readRepeat(this
				.getApplicationContext());
		repeatFlag = (int) Math.log10(repeatValue) - 2;
		// ���ñ仯������
		prefReceiver = new PreferenceReceiver();
		IntentFilter prefFilter = new IntentFilter();
		prefFilter.addAction(PlayListHelper.INTENT_ACTION_SETTINGS_CHANGEED);
		this.registerReceiver(prefReceiver, prefFilter);

		// System.out.println(shuffleFlag + "}}}" + repeatFlag);
		// ��ȡ�Ƿ�ζ�������һ��
		shakeNext = PreferenceHelper.readShakeStatus(this
				.getApplicationContext());

		// �ζ�������һ��
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sel = new ShakeSensorListener();
		if (shakeNext) {
			sm.registerListener(sel, sensor, SensorManager.SENSOR_DELAY_UI);
		}

		// ��������
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		ComingCallListener telListener = new ComingCallListener();
		tm.listen(telListener, PhoneStateListener.LISTEN_CALL_STATE);

		shake_strength = PreferenceHelper.readShakeStrength(this
				.getApplicationContext());
	}

	// ������Զ����ŵ���һ�׸�
	private class CompletionListener implements
			MediaPlayer.OnCompletionListener {
		public void onCompletion(MediaPlayer player) {
			autoplayNext();
		}
	}

	private void showError() {
		Toast.makeText(this.getApplicationContext(), "�������ܱ�ʶ��ֱ�Ӳ�����һ��",
				Toast.LENGTH_LONG).show();
		playNext();
	}

	@Override
	public void onStart(final Intent intent, int intentId) {
		Log.e("", "PlayService on Start");
		if (intent == null) {
			this.stopSelf();
			return;
		}
		try {
			current_pos = intent.getIntExtra("current_pos", 0);
			// notif = intent.getParcelableExtra("notif");
			// System.out.println("notif="+notif);

			// originalList = PlayListHelper.deserializePlaylist(this);//
			// PlayingHelper.playList;
			originalList = new ArrayList<Music>();
			Parcelable[] musicArray = (Parcelable[]) intent
					.getParcelableArrayExtra("playListArray");
			Music mm;
			for (Parcelable m : musicArray) {
				mm = (Music) m;
				originalList.add(mm);
			}

			// �Ȱ�ԭ�еĲ����б����
			playingList.clear();

			// �����ȸ��б����ռ�
			playingList.addAll(originalList);

			Collections.copy(playingList, originalList);

			max_pos = playingList.size() - 1;
			music = playingList.get(current_pos);

			current_progress_position = intent.getIntExtra(
					"current_progress_position", 0);
			current_song_path = music.getData();

			player.reset();
			// player.setDataSource(song_id);
			player.setDataSource(current_song_path);

			step = STEP_START;
			player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

			
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					switch (step) {
					case STEP_START:
						player.start();
						player.seekTo(intent.getIntExtra("last_progress", 0));
						// ���������߳�
						progressThread.start();
						PreferenceHelper.writeLastMusic(
								PlayService.this.getApplicationContext(),
								music.getMusic_id(),
								player.getCurrentPosition());
						break;
					case STEP_NEXT:
						if (status == PlayListHelper.STATUS_PLAYING) { // 2-����״̬
							player.start();
						}
						sendPosIntent(current_pos);
						PreferenceHelper.writeLastMusic(
								PlayService.this.getApplicationContext(),
								music.getMusic_id(),
								player.getCurrentPosition());
						break;
					case STEP_PREVIOUS:
						if (status == PlayListHelper.STATUS_PLAYING) {// 2-����״̬
							player.start();
							// �����������߳�
							progressThread.start();
							// status = 2;
						}
						sendPosIntent(current_pos);
						PreferenceHelper.writeLastMusic(
								PlayService.this.getApplicationContext(),
								music.getMusic_id(),
								player.getCurrentPosition());
						break;
					case STEP_PLAY_PAUSE:
						player.start();
						progressThread.start();
						sendPosIntent(current_pos);
						break;

					}
				}
			});
			player.prepareAsync();
			status = PlayListHelper.STATUS_PLAYING;// 2;

			// ��������ó���������ţ�����Ҳ����б�˳��
			if (shuffleFlag == 1) {
				Collections.shuffle(playingList);
			}

		} catch (java.io.IOException e) {
			// e.printStackTrace();
			showError();
		}
		// this.setForeground(false);
	}

	@Override
	public void onDestroy() {
		// ����ǰ���ŵĸ���ID�Ͳ��Ž��ȵȷŵ�����
		if (music != null && player != null)
			PreferenceHelper.writeLastMusic(this.getApplicationContext(),
					music.getMusic_id(), player.getCurrentPosition());
		if (receiver != null)
			this.unregisterReceiver(receiver);
		if (progressReceiver != null)
			this.unregisterReceiver(progressReceiver);
		if (flagReceiver != null)
			this.unregisterReceiver(flagReceiver);
		if (prefReceiver != null)
			this.unregisterReceiver(prefReceiver);

		if (sm != null)
			sm.unregisterListener(sel);
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}
		if (progressThread != null)
			progressThread.stop();
		if (notiMgr == null)
			notiMgr = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
		notiMgr.cancel(R.layout.notification_layout);

		super.onDestroy();
	}

	// send the value of seekbar ����״̬����ֵ
	public void sendProgress() {
		// System.out.println(player.getCurrentPosition());
		progressIntent.putExtra("progress", player.getCurrentPosition());

		PlayService.this.sendBroadcast(progressIntent);
	}

	private void playPauseAction() {
		// 1-Stopped 2-Playing 3-Pause

		if (status == PlayListHelper.STATUS_STOPPED) { // stopped
			// System.out.println("STOPPED");
			try {
				player.reset();
				player.setDataSource(song_id);
				status = PlayListHelper.STATUS_PLAYING;// 2;
				// sendIntent = new Intent("update");
				sendIntent.putExtra("update", status);
				sendIntent.putExtra("current_progress_position",
						current_progress_position);
				PlayService.this.sendBroadcast(sendIntent);

				step = STEP_PLAY_PAUSE;

				player.prepareAsync();
			} catch (java.io.IOException e) {
				// e.printStackTrace();
				showError();
			}
		} else if (status == PlayListHelper.STATUS_PLAYING) {// playing ����
			player.pause();
			progressThread.pause();

			status = PlayListHelper.STATUS_PAUSE;// 3;

		} else if (status == PlayListHelper.STATUS_PAUSE) {// paused ��ͣ״̬
			player.start();
			progressThread.resume();
			status = PlayListHelper.STATUS_PLAYING; // 2;
		}
	}

	private void playPrevious() {

		// ������׸������Ž����Ѿ�����5�룬���ͷ��ʼ���ű��׸���������������һ��
		if (player != null && player.getCurrentPosition() >= 5000) {
			player.seekTo(0);
			current_progress_position = 0;
		} else {
			if (this.repeatFlag == 1) {// �ظ�һ��

			} else if (this.repeatFlag == 2) {// ȫ���ظ�
				// current_pos = (--current_pos + max_pos) % max_pos;
				if (--current_pos < 0) {
					current_pos = max_pos;
				}
			} else if (repeatFlag == 0) {// �ظ������Ϊ�����ظ���
				if (--current_pos < 0) {
					current_pos = 0;
				}
			}
		}
		// Log.w("", "max:" + max_progress_position);
		try {
			player.reset();
			music = playingList.get(current_pos);
			player.setDataSource(music.getData());
			sendPosIntent(current_pos);

			step = STEP_PREVIOUS;
			player.prepareAsync();
			updateNotification();
		} catch (IOException e) {
			// e.printStackTrace();
			showError();
		}
	}

	private void autoplayNext() {

		if (this.repeatFlag == 1) {// �ظ�һ��

		} else if (this.repeatFlag == 2) {// ȫ���ظ�
			if (++current_pos > max_pos) {
				current_pos = 0;
			}
		} else if (repeatFlag == 0) {// �ظ������Ϊ�����ظ���
			if (++current_pos > max_pos) {
				current_pos = max_pos;
			}
		}
		// System.out.println(current_pos + "xux");
		try {
			player.reset();

			music = playingList.get(current_pos);
			player.setDataSource(music.getData());
			sendPosIntent(current_pos);
			step = STEP_NEXT;
			player.prepareAsync();
			updateNotification();
		} catch (IOException e) {
			// e.printStackTrace();
			showError();
		}
	}

	private void playNext() {
		// System.out.println("Service PL" + playingList);
		if (this.repeatFlag == 1) {// �ظ�һ��

		} else if (this.repeatFlag == 2) {// ȫ���ظ�
			if (++current_pos > max_pos) {
				current_pos = 0;
			}
		} else if (repeatFlag == 0) {// �ظ������Ϊ�����ظ���
			if (++current_pos > max_pos) {
				current_pos = max_pos;
			}
		}
		// System.out.println(current_pos + "xux");
		try {
			player.reset();
			music = playingList.get(current_pos);
			player.setDataSource(music.getData());
			sendPosIntent(current_pos);
			step = STEP_NEXT;
			player.prepareAsync();
			updateNotification();
		} catch (IOException e) {
			showError();
		}
	}

	private void updateNotification() {
		if (notiMgr == null)
			notiMgr = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);

		// System.out.println("nnnooottt" + notif + music.getTitle());
		if (notif != null) {
			notif.contentView.setTextViewText(R.id.textView1, music.getTitle());// ����
			notif.contentView
					.setTextViewText(R.id.textView2, music.getArtist());// �ݳ���
			if (music.getCover() != null) {
				notif.contentView.setImageViewUri(R.id.imageView1,
						Uri.parse(music.getCover()));
			} else {
				notif.contentView.setImageViewResource(R.id.imageView1,
						R.drawable.music_icon);
			}
			// this.setForeground(true);
			notiMgr.notify(R.layout.notification_layout, notif);
			// this.startForeground(R.layout.notification_layout, notif);
		}
	}

	private void sendPosIntent(int pos) {
		Intent intent = new Intent(PlayListHelper.INTENT_ACTION_POS);
		// intent.putExtra("current_pos", pos);
		intent.putExtra("music_id", playingList.get(pos).getMusic_id());
		intent.putExtra("progress", player.getCurrentPosition());
		intent.putExtra("isPlaying", player.isPlaying());
		// System.out.println(player+"service isplaying"+player.isPlaying());
		this.sendBroadcast(intent);
	}

	boolean isMainExit = false;

	// ���տ��ƶ�����Receiver
	class ServiceReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			int intent_control = intent.getIntExtra("playControl", -1);

			// System.out.println("SSSSSSSS" + intent_control);
			if (intent_control == PlayListHelper.CLICK_NEXT) {
				playNext();
			} else if (intent_control == PlayListHelper.CLICK_PREVIOUS) {
				playPrevious();
			} else if (intent_control == PlayListHelper.CLICK_PLAY_PAUSE) {
				playPauseAction();
			} else if (intent_control == PlayListHelper.CLICK_STOP) {
				stopSelf();
			} else if (intent_control == PlayListHelper.PLAYING_ACTIVITY_NEW) {
				// PlayingActivity����մ���������������������еģ���Ὣ���ڲ��ŵĸ����������ظ�����
				sendPosIntent(current_pos);
			} else if (intent_control == PlayListHelper.SEND_NOTIFICATION) {

				notif = intent.getParcelableExtra("notif");
				// System.out.println("kkkk" + notif);
			} else if (intent_control == PlayListHelper.MAIN_EXIT_STILL_PLAYING) {
				isMainExit = true;
			} else if (intent_control == PlayListHelper.MAIN_CREATE) {
				isMainExit = false;
			}
		}
	}


	public boolean onError(MediaPlayer player, int arg, int extra) {
		stopSelf();
		return true;
	}

	class ProgressReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			player.seekTo(intent.getIntExtra("current_progress", 0));
		}
	}

	class FlagReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			shuffleFlag = intent.getIntExtra("shuffleFlag", -1);
			if (shuffleFlag == 0) {
				// ��ȡ����������б������ڲ��ŵĸ���
				Music oldMusic = playingList.get(current_pos);
				// �����µ�˳�򲥷��б�
				Collections.copy(playingList, originalList);
				// �õ�˳�򲥷��б��еĸղ����ڲ��ŵĸ�����λ��
				current_pos = playingList.indexOf(oldMusic);
			} else if (shuffleFlag == 1) {
				Collections.shuffle(playingList);
			}

			if (intent.getIntExtra("repeatFlag", -1) != -1) {
				repeatFlag = intent.getIntExtra("repeatFlag", -1);
			}
		}

	}

	// ���øı������
	class PreferenceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
//			boolean oldShakeNext = shakeNext;
			shakeNext = intent.getBooleanExtra("shake_next", shakeNext);
			if(shakeNext){				
				sm.registerListener(sel, sensor, SensorManager.SENSOR_DELAY_UI);
			}else{
				sm.unregisterListener(sel);
			}
			// ���û��intent��shake_strengthֵ������ԭ�е�shake_strengthֵ
			
			shake_strength = intent.getIntExtra("shake_strength",
					shake_strength);
			sel.setShake_threshold(shake_strength);
		}
	}

	private class ShakeSensorListener implements SensorEventListener {
		long lastUpdate, lastShakeTime = 0;
		float x, y, last_x = 0, last_y = 0;
		// int SHAKE_THRESHOLD = shake_strength;
		private int shake_threshold = shake_strength;

	
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

	
		public void onSensorChanged(SensorEvent e) {
			// Log.e("","<><><><><<<<><>");
			long curTime = System.currentTimeMillis();
			if ((curTime - lastUpdate) > 100) {
				long diffTime = curTime - lastUpdate;
				lastUpdate = curTime;
				x = e.values[SensorManager.DATA_X];
				y = e.values[SensorManager.DATA_Y];

				float acceChangeRate = 0;
				if (last_x != 0)
					acceChangeRate = Math.abs(x + y - last_x - last_y)
							/ diffTime * 1000;

				if (acceChangeRate > shake_threshold
						&& curTime - lastShakeTime > 200) {
					lastShakeTime = curTime;
					// sendIntent.putExtra("previous_next", "auto_next");
					playNext();
					// playNextSong();
				}
				last_x = x;
				last_y = y;
			}
		}

		public void setShake_threshold(int shake_threshold) {
			this.shake_threshold = shake_threshold;
		}

	}

	// ��ص绰״̬
	class ComingCallListener extends PhoneStateListener {
		int old_status;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			// ���ժ���������磬����ͣ����
			if (state == TelephonyManager.CALL_STATE_OFFHOOK
					|| state == TelephonyManager.CALL_STATE_RINGING) {
				// ��������ڲ���״̬������Ҫ��ͣ
				if (status == PlayListHelper.STATUS_PLAYING) { // 2
					old_status = status;
					playPauseAction();
				}
			}
			// �һ�
			if (state == TelephonyManager.CALL_STATE_IDLE) {
				// ����������ڲ���״̬������Ҫ���²��ţ������ù�
				if (old_status == PlayListHelper.STATUS_PLAYING) { // 2
					playPauseAction();
					old_status = status;
				}
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

}
