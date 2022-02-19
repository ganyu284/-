package cn.com.alex.imusic;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.com.alex.imusic.adapter.PlayingActivityListViewAdapter;
import cn.com.alex.imusic.bean.Music;
import cn.com.alex.imusic.flip3d.DisplayNextView;
import cn.com.alex.imusic.flip3d.Flip3DAnimation;
import cn.com.alex.imusic.flip3d.Rotate3dAnimation;
import cn.com.alex.imusic.lyrics.LrcDecode;
import cn.com.alex.imusic.lyrics.LyricsAsyncTask;
import cn.com.alex.imusic.util.ImageUtil;
import cn.com.alex.imusic.util.PlayListHelper;
import cn.com.alex.imusic.util.PlayerTimer;
import cn.com.alex.imusic.util.PreferenceHelper;

public class PlayingActivity extends Activity implements View.OnClickListener {
	private ImageButton btn1, btn2;
	private View flipView1, flipView2;
	private boolean isFirstImage = true;

	private ImageButton shuffleBtn, repeatBtn;
	private int shuffleFlag = 0;
	private int repeatFlag = 0;
	private int[] shuffleRes = { R.drawable.shuffle_off_btn,
			R.drawable.shuffle_on_btn }; // 0-off,1-on
	private int[] repeatRes = { R.drawable.repeat_off_btn,
			R.drawable.repeat_once_btn, R.drawable.repeat_all_btn };

	private String playingTitle;
	private String playingAlbum;
	private String playingArt;

	TextView playingTitleView, playingAlbumView, lyricsView;
	ImageView playingArtView;
	SeekBar volumeBar, progressBar;
	TextView progressView, progressMaxView;
	private int maxVolume;
	private int currentVolume;
	private AudioManager aMgr;

	RelativeLayout progressLayout;

	Intent seekProgressIntent;

	private boolean isPlaying = false;
	private ImageView playPauseView;

	private int current_pos;
	// ���ŷ����Ƿ��Ѿ�����
	private boolean playServiceStart = false;

	private CurrentPosReceiver cpreceiver;
	private ProgressReceiver progressReceiver;

	private int maxWidth, maxHeight;

	private boolean showProgressView = true;

	private ArrayList<Music> musics = new ArrayList<Music>();
	private ArrayList<Music> oldMusics = new ArrayList<Music>();

	private ListView lv;
	private int oldIdx, idx;

	private PlayingActivityListViewAdapter playingAdapter;

	private Music oldMusic;
	Music music = null;
	// �Ƿ���ʾ���
	private boolean isShowLyrics;
	//��ȡ���ģʽ
	private String lyricsMode = PlayListHelper.GET_LYRICS_MODE_ALWAYS;
	// ��ʱ�
	Hashtable<String, String> lrcTable;
	// ����߳�����
	LyricsAsyncTask task;

	NotificationManager notiMgr;
	Notification notif = new Notification();
	private PreferenceReceiver prefReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		System.out.println("Playing onCreate");
		// Debug.startMethodTracing("playingactivity");
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.now_playing);

		// musics = PlayListHelper.deserializePlaylist(this);
		// oldMusics = musics;

		repeatBtn = (ImageButton) findViewById(R.id.imageButton1);
		shuffleBtn = (ImageButton) findViewById(R.id.imageButton2);
		// PreferenceHelper.writeRepeat(this, 100);
		// PreferenceHelper.writeShuffle(this, 1);
		repeatBtn.setOnClickListener(this);
		shuffleBtn.setOnClickListener(this);

		// �л���ͼ��ť
		btn1 = (ImageButton) findViewById(R.id.imageButton4);
		btn2 = (ImageButton) findViewById(R.id.imageButton5);
		btn2.setVisibility(View.GONE);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);

		flipView1 = (FrameLayout) findViewById(R.id.lyricsFLayout1);
		flipView2 = (ListView) findViewById(R.id.listView1);
		flipView2.setVisibility(View.GONE);

		playingTitleView = (TextView) findViewById(R.id.playingTitle);
		playingArtView = (ImageView) findViewById(R.id.playingCover);
		playingAlbumView = (TextView) findViewById(R.id.playingAlbum);

		lyricsView = (TextView) findViewById(R.id.tvLyrics);

		// ���š���ͣ��ť
		playPauseView = (ImageView) findViewById(R.id.playPause);
		// ������
		progressBar = (SeekBar) findViewById(R.id.seekBar1);
		progressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					if (seekProgressIntent == null) {
						seekProgressIntent = new Intent(
								PlayListHelper.INTENT_ACTION_SEEK_PROGRESS);
					}
					seekProgressIntent.putExtra("current_progress", progress);
					sendBroadcast(seekProgressIntent);
				}
			}

		
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

	
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}
		});

		// �Զ���������������������
		volumeBar = (SeekBar) findViewById(R.id.volumnBar1);
		aMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = aMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		currentVolume = aMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		volumeBar.setMax(maxVolume);
		volumeBar.setProgress(currentVolume);
		volumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					aMgr.setStreamVolume(AudioManager.STREAM_MUSIC, progress,
							AudioManager.FLAG_PLAY_SOUND);
				}
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}
		});
		// ����ʹ�ð������������ƣ����ҷ�ӳ�������Լ������������������
		// this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		progressView = (TextView) findViewById(R.id.progressView);
		progressMaxView = (TextView) findViewById(R.id.progressMaxView);
		// ��ǰ���ŵĸ����Ľ�����
		cpreceiver = new CurrentPosReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayListHelper.INTENT_ACTION_POS);
		this.registerReceiver(cpreceiver, filter);
		// ���Ƚ�����
		progressReceiver = new ProgressReceiver();
		IntentFilter progressFilter = new IntentFilter();
		progressFilter.addAction(PlayListHelper.INTENT_ACTION_PROGRESS);
		this.registerReceiver(progressReceiver, progressFilter);
		// ���ñ仯������
		prefReceiver = new PreferenceReceiver();
		IntentFilter prefFilter = new IntentFilter();
		prefFilter.addAction(PlayListHelper.INTENT_ACTION_SETTINGS_CHANGEED);
		this.registerReceiver(prefReceiver, prefFilter);

		// setupListView(0, musics);

		playMusic(getIntent());
		// AppExit.allActivity.add(this);

		progressLayout = (RelativeLayout) findViewById(R.id.progressLayout);

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		maxWidth = displayMetrics.widthPixels;
		maxHeight = displayMetrics.heightPixels;

		isShowLyrics = PreferenceHelper.readIfShowLyrics(this
				.getApplicationContext());
		lyricsMode = PreferenceHelper.readDownloadLyricsMode(getApplicationContext());
		
		notiMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Debug.stopMethodTracing();
	}

	// �����������������¼����˴����ڼ���������ư������Լ�back����
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			volumeBar.setProgress(aMgr
					.getStreamVolume(AudioManager.STREAM_MUSIC));
			break;
		case KeyEvent.KEYCODE_BACK:
			// System.out.println("hjhjhjhjjh");
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("playingActivity onNewIntent()");
		playMusic(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onStart() {
		// Debug.startMethodTracing("start");
		// TODO Auto-generated method stub
		System.out.println("Playing onStart");
		int shuffleValue = PreferenceHelper.readShuffle(this
				.getApplicationContext());

		shuffleFlag = (int) Math.log10(shuffleValue);
		shuffleBtn.setImageResource(shuffleRes[shuffleFlag]);

		int repeatValue = PreferenceHelper.readRepeat(this
				.getApplicationContext());
		repeatFlag = (int) Math.log10(repeatValue) - 2;
		repeatBtn.setImageResource(repeatRes[repeatFlag]);

		showProgressView = true;
		// Debug.stopMethodTracing();
		super.onStart();
	}

	public Hashtable<String, String> getLrcTable() {
		return lrcTable;
	}

	public void setLrcTable(Hashtable<String, String> lrcTable) {
		this.lrcTable = lrcTable;
	}

	// ��ʾ�������ز��Ž�������ͼ
	public void showHideProgressView(View view) {
		showProgressView = !showProgressView;
		if (showProgressView) {
			progressLayout.setVisibility(View.VISIBLE);
		} else {
			progressLayout.setVisibility(View.INVISIBLE);
		}
	}

	private void playMusic(Intent intent) {
		// ���Intent��Intent������Դ�ڡ����ڲ��š���ť���͵�������б��е�ĳ�׸�������
		// �����ڲ��š����ı䲥��״̬�������ĳ�׸�����ģ���Ҫ����
		current_pos = intent.getIntExtra("current_pos", -1);
		// System.out.println("test...");
		// ���ĳ�׸������
		if (current_pos != -1) {
			isPlaying = true;
			// ���ϸ�ҳ���������б�
			musics.clear();
			Parcelable[] musicArray = (Parcelable[]) intent
					.getParcelableArrayExtra("playListArray");
			Music mm;
			for (Parcelable m : musicArray) {
				mm = (Music) m;
				musics.add(mm);
			}
			oldMusics = musics;
			setupUI(current_pos, 0, musics);// PlayingHelper.playList);

			// ��ͣ�����Ű�ť
			if (isPlaying) {
				playPauseView.setImageResource(R.drawable.pause);
			}
			startPlayService(0);
			playServiceStart = true;
			PlayListHelper.serializePlaylist(this, musics);
		} else {
			// �Ӵ��̶�ȡ�ϴβ��ŵ��б��Լ����Ž���
			musics = PlayListHelper.deserializePlaylist(this
					.getApplicationContext());
			oldMusics = musics;
			// current_pos = PreferenceHelper.readLastPos(this);
			Music music = new Music(PreferenceHelper.readLastMusic(this
					.getApplicationContext()));
			current_pos = musics.indexOf(music);

			// System.out.println("KKKKKK" + musics);
			setupUI(current_pos, PreferenceHelper.readLastProgress(this
					.getApplicationContext()), musics);
			// ��ͣ�����Ű�ť
			if (isPlaying) {
				playPauseView.setImageResource(R.drawable.pause);
			}
			// ����һ����Ϣ�����ŷ���������Ѿ��������򽫻��յ���������ǰ���ڲ��ŵĸ�������������
			if (playServiceStart) {
				// System.out.println("SSSERRVVV");
				control_intent.putExtra("playControl",
						PlayListHelper.PLAYING_ACTIVITY_NEW);
				// control_intent.putExtra("notif", notif);
				sendBroadcast(control_intent);
			}
		}
		setupListView(musics);
		modifyListView();
	}

	// �������ŷ���
	private void startPlayService(int progress) {
		Intent playServiceIntent = new Intent(this, PlayService.class);

		// ��ǰ���ŵĸ�����ArrayList�е�λ������
		playServiceIntent.putExtra("current_pos", current_pos);
		// �ϴβ��ź�Ľ���
		playServiceIntent.putExtra("last_progress", progress);
		// �������б���Service
		Music[] musicArray = new Music[musics.size()];
		musicArray = musics.toArray(musicArray);
		playServiceIntent.putExtra("playListArray", musicArray);
		// playServiceIntent.putExtra("notif", notif);

		startService(playServiceIntent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		System.out.println("Playing onResume");
		// ȡ��Notification
		notiMgr.cancel(R.layout.notification_layout);

		// ��notification���͸�service
		Intent notiIntent = new Intent();
		notiIntent.setAction(PlayListHelper.INTENT_ACTION_CONTROL);
		notiIntent.putExtra("playControl", PlayListHelper.SEND_NOTIFICATION);
		notif = null;
		notiIntent.putExtra("notif", notif);
		sendBroadcast(notiIntent);
		// ��ͣ�����Ű�ť
		if (isPlaying) {
			playPauseView.setImageResource(R.drawable.pause);
		} else {
			playPauseView.setImageResource(R.drawable.play);
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		System.out.println("Playing onPause");
		if (notif == null)
			notif = new Notification();

		// ���水ť״̬
		PreferenceHelper.writeShuffle(this.getApplicationContext(),
				(int) Math.pow(10, (shuffleFlag) % 2));

		PreferenceHelper.writeRepeat(this.getApplicationContext(),
				(int) Math.pow(10, (repeatFlag) % 3 + 2));
		// ����Notification
		notif.tickerText = music.getTitle();
		notif.icon = isPlaying ? R.drawable.n_play : R.drawable.n_pause;
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notification_layout);
		contentView.setTextViewText(R.id.textView1, music.getTitle());// ����
		contentView.setTextViewText(R.id.textView2, music.getArtist());// �ݳ���
		if (music.getCover() != null) {
			contentView.setImageViewUri(R.id.imageView1,
					Uri.parse(music.getCover()));
		}// ����

		notif.contentView = contentView;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, PlayingActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
				PendingIntent.FLAG_UPDATE_CURRENT);
		notif.contentIntent = contentIntent;
		notif.flags = Notification.FLAG_ONGOING_EVENT;
		notiMgr.notify(R.layout.notification_layout, notif);
		// ����R.layout.notification_layoutֻ�ǽ�����Ϊ���֪ͨ��ID

		// ��notification���͸�service
		Intent notiIntent = new Intent();
		notiIntent.setAction(PlayListHelper.INTENT_ACTION_CONTROL);
		notiIntent.putExtra("playControl", PlayListHelper.SEND_NOTIFICATION);
		notiIntent.putExtra("notif", notif);
		// System.out.println(notif+"<><><");
		sendBroadcast(notiIntent);

		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		System.out.println("Playing onStop");

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("Playing onDestroy");
		this.unregisterReceiver(cpreceiver);
		this.unregisterReceiver(progressReceiver);
		this.unregisterReceiver(prefReceiver);

		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		System.out.println("PlayingActivity onBack");
		back();
		super.onBackPressed();
	}

	private Intent control_intent = new Intent(
			PlayListHelper.INTENT_ACTION_CONTROL);;

	// ���š���ͣ��ť
	public void playPause(View view) {
		if (!playServiceStart) {
			// System.out.println("JJJJ");
			startPlayService(PreferenceHelper.readLastProgress(this
					.getApplicationContext()));
			playServiceStart = true;
			isPlaying = true;
		} else {
			isPlaying = !isPlaying;

			control_intent.putExtra("playControl",
					PlayListHelper.CLICK_PLAY_PAUSE);
			// ���͸������棬�����ж��˳�ʱ�Ƿ���ֹ����ı�־
			control_intent.putExtra("isPlaying", isPlaying);
			this.sendBroadcast(control_intent);
		}
		if (!isPlaying) {
			playPauseView.setImageResource(R.drawable.play);
			// ����һ���߳������浱ǰ���ȵ�״̬
			new Thread(new Runnable() {
				public void run() {
					PreferenceHelper.writeLastMusic(PlayingActivity.this,
							music.getMusic_id(), progressBar.getProgress());
				}
			}).start();

		} else {
			playPauseView.setImageResource(R.drawable.pause);
		}

		modifyListView();

	}

	// ǰһ�װ�ť
	public void playPrevious(View view) {
		// �����û���������������������ŷ���
		if (!playServiceStart) {
			startPlayService(0);
			playServiceStart = true;
			isPlaying = true;
			playPauseView.setImageResource(R.drawable.pause);
		}
		control_intent = new Intent(PlayListHelper.INTENT_ACTION_CONTROL);
		control_intent.putExtra("playControl", PlayListHelper.CLICK_PREVIOUS);
		this.sendBroadcast(control_intent);
	}

	// ��һ�װ�ť
	public void playNext(View view) {
		// �����û���������������������ŷ���
		if (!playServiceStart) {
			startPlayService(0);
			playServiceStart = true;
			isPlaying = true;
			playPauseView.setImageResource(R.drawable.pause);
		}

		control_intent = new Intent(PlayListHelper.INTENT_ACTION_CONTROL);
		control_intent.putExtra("playControl", PlayListHelper.CLICK_NEXT);
		this.sendBroadcast(control_intent);
	}
	public void playadd(View view) {
		// �����û���������������������ŷ���
		if (!playServiceStart) {
			startPlayService(0);
			playServiceStart = true;
			isPlaying = true;
			playPauseView.setImageResource(R.drawable.pause);
		}
		control_intent = new Intent(PlayListHelper.INTENT_ACTION_CONTROL);
		control_intent.putExtra("playControl", PlayListHelper.CLICK_ADD);
		this.sendBroadcast(control_intent);
		
	}
	// �����ذ�ť�ĵ��
	public void backActivity(View view) {
		back();
	}

	private void back() {
		// Intent intent = new Intent(this, MainActivity.class);
		// startActivity(intent);
		Intent i = new Intent();
		this.setResult(RESULT_OK, i);
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		// System.out.println(">>>>>>>>>>>>>>>>");
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// System.out.println("landscape");
			Intent intent = new Intent(this, CoverFlowMain.class);
			intent.putExtra("fromWhere", PlayListHelper.FROM_PLAYING);
			// ��ר��id���ݸ�CoverFlow
			// ������ŷ����Ѿ���������Ӳ����б���ȡ�����ڲ��ŵĸ�����ȡ����album_id
			if (this.playServiceStart) {
				intent.putExtra("album_id",
						PlayService.playingList.get(current_pos).getAlbum_id());
			} else {// ������ŷ���δ��������Ӵ��̶�ȡϵ�л����б����ϴα��ֵĲ��Ÿ���������
				intent.putExtra(
						"album_id",
						PlayListHelper
								.deserializePlaylist(
										this.getApplicationContext())
								.get(current_pos).getAlbum_id());
			}
			startActivity(intent);
		}
	}

	// zTranslate - �Ƿ���Ҫ��z���Ͻ����ƶ�
	private void applyRotation(float start, float end, View view1, View view2,
			boolean zTranslate) {
		// Find the center of View
		final float centerX = view1.getWidth() / 2.0f;
		final float centerY = view1.getHeight() / 2.0f;
		final Animation rotation;
		if (!zTranslate) {
			rotation = new Flip3DAnimation(start, end, centerX, centerY);
		} else {
			rotation = new Rotate3dAnimation(start, end, centerX, centerY,
					2.5f * centerX, true);
		}
		rotation.setDuration(300);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(isFirstImage, view1,
				view2));

		if (isFirstImage) {
			view1.startAnimation(rotation);
			modifyListView();
			// flipView1.startAnimation(rotation);
		} else {
			view2.startAnimation(rotation);
			modifyListView();
			// flipView2.startAnimation(rotation);
		}
	}

	// �޸����ڲ��ŵĸ�����ImageView�����ǣ�
	private void modifyListView() {

		int fp = lv.getFirstVisiblePosition();
		int wc = oldIdx - fp;
		View view0 = lv.getChildAt(wc);
		if (view0 != null) {
			ImageView iv = (ImageView) view0.findViewById(R.id.play_tri);
			iv.setVisibility(View.INVISIBLE);
		}
		// System.out.println(lv.getFirstVisiblePosition()+"|"+lv.getChildCount()+"|"+idx);

		lv.setSelection(idx);
		// System.out.println("idx===="+idx);

		int firstPosition = lv.getFirstVisiblePosition();
		int wantedChild = idx - firstPosition;
		View view1 = lv.getChildAt(wantedChild);
		// System.out.println(lv.getFirstVisiblePosition()+"|"+lv.getChildCount()+"|"+idx+"|"+wantedChild);
		// System.out.println(view1);
		if (view1 != null) {
			ImageView iv = (ImageView) view1.findViewById(R.id.play_tri);
			iv.setVisibility(View.VISIBLE);
		}
		oldIdx = idx;
	}

	
	public void onClick(View v) {
		Intent flagIntent = new Intent(PlayListHelper.INTENT_ACTION_FLAG);

		// �л���ͼ��ť���
		if (v == btn1 || v == btn2) {
			if (isFirstImage) {
				applyRotation(0, 90, btn1, btn2, false);
				applyRotation(0, 90, flipView1, flipView2, true);
				isFirstImage = !isFirstImage;

			} else {
				applyRotation(0, -90, btn1, btn2, false);
				applyRotation(0, -90, flipView1, flipView2, true);
				isFirstImage = !isFirstImage;
			}
		} else if (v == shuffleBtn) { // ������Ű�ť
			shuffleFlag++;
			shuffleBtn.setImageResource(shuffleRes[shuffleFlag % 2]);
			flagIntent.putExtra("shuffleFlag", shuffleFlag % 2);
			sendBroadcast(flagIntent);
		} else if (v == repeatBtn) { // �ظ���ť
			repeatFlag++;
			repeatBtn.setImageResource(repeatRes[repeatFlag % 3]);
			flagIntent.putExtra("repeatFlag", repeatFlag % 3);
			sendBroadcast(flagIntent);
		}
	}

	// ���ý����ϵĸ�����ר����
	// pos-���ŵĸ���������progress-���ȣ�musics-�����б�
	private void setupUI(int pos, int progress, ArrayList<Music> musics) {

		if (musics.size() > 0) {
			if (pos < 0 || pos >= musics.size())
				pos = 0;
			if (PlayService.status == PlayListHelper.STATUS_PLAYING) {
				playServiceStart = true;
				playPauseView.setImageResource(R.drawable.pause);
			} else {
				playPauseView.setImageResource(R.drawable.play);
			}
			music = musics.get(pos);//
			playingTitle = music.getTitle();
			playingAlbum = music.getAlbum();
			playingArt = music.getCover();
			// System.out.println(">>>>>"+music.getTitle_key());
			progressBar.setMax(music.getDuration());
			progressMaxView.setText(PlayerTimer.format(music.getDuration()));

			progressView.setText(PlayerTimer.format(progress));
			progressBar.setProgress(progress);

			playingTitleView.setText(playingTitle);
			playingAlbumView.setText(playingAlbum);
			// System.out.println(">>>>>" + playingArt);

			playingArtView.setMaxWidth(maxWidth * 3 / 4);
			playingArtView.setMaxHeight(maxHeight);
			playingArtView.setAdjustViewBounds(true);
			playingArtView.setImageBitmap(ImageUtil.getReflectionImage(
					this.getApplicationContext(), playingArt, maxWidth,
					maxHeight));

			// �Ѹ�����
			lyricsView.setText("");
			if (lrcTable != null)
				lrcTable.clear();

			// ��ø��
			if (task != null) {
				task.cancel(true); // ��ȡ��ԭ�е�����
			}
		
			//if (isShowLyrics) {//������ó���ʾ��ʣ����ȡ��ʣ����򣬲���ȡ
				//lyricsView.setText("hahahaha");
				task = new LyricsAsyncTask(this, music,lyricsMode);
				task.execute();
			//}
		}
		if (oldMusic != null) {
			oldIdx = oldMusics.indexOf(oldMusic);
		}
		oldMusic = music;
		idx = oldMusics.indexOf(music);
		if (playingAdapter != null)
			playingAdapter.setIdx(idx);
	}

	private void setupListView(ArrayList<Music> musics) {
		lv = (ListView) findViewById(R.id.listView1);
		playingAdapter = new PlayingActivityListViewAdapter(
				this.getApplicationContext(), 0, musics);
		lv.setAdapter(playingAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int fp = lv.getFirstVisiblePosition();
				int wc = oldIdx - fp;
				if (wc >= 0 && wc < lv.getChildCount()) {
					View view0 = lv.getChildAt(wc);
					if (view0 != null) {
						ImageView iv = (ImageView) view0
								.findViewById(R.id.play_tri);
						iv.setVisibility(View.INVISIBLE);
					}
				}
				ImageView iv = (ImageView) arg1.findViewById(R.id.play_tri);
				iv.setVisibility(View.VISIBLE);
				// ���Ÿ���
				isPlaying = true;
				current_pos = idx = arg2;
				setupUI(current_pos, 0, PlayingActivity.this.musics);// PlayingHelper.playList);

				// ��ͣ�����Ű�ť
				if (isPlaying) {
					playPauseView.setImageResource(R.drawable.pause);
				}
				startPlayService(0);
				playServiceStart = true;
				oldIdx = idx;
				if (playingAdapter != null)
					playingAdapter.setIdx(idx);
			}
		});
	}

	class CurrentPosReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, final Intent intent) {
			// ��ǰ���ŵĸ���
			// System.out.println("Reciiver");
			Music mic = new Music(intent.getIntExtra("music_id", 0));
			current_pos = musics.indexOf(mic);
			isPlaying = intent.getBooleanExtra("isPlaying", false);
			// intent.getIntExtra("current_pos", -1);
			setupUI(current_pos, intent.getIntExtra("progress", 0), musics);

			modifyListView();
		}
	}

	// ���Ƚ�����
	class ProgressReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int progress = intent.getIntExtra("progress", 0);
			// Log.e(">>", "" + progress);
			progressBar.setProgress(progress);
			progressView.setText(PlayerTimer.format(progress));
			// ��ʾ���
			if (isShowLyrics) {
				if (lrcTable != null
						&& lrcTable.get(LrcDecode.timeMode(progress)) != null) {
					lyricsView.setText(lrcTable.get(LrcDecode
							.timeMode(progress)));
				}
			}
		}
	}

	// ���øı������
	class PreferenceReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ���intentû��isShowLyricsֵ������ԭ�е�isShowLyricsֵ����
			isShowLyrics = intent.getBooleanExtra("isShowLyrics", isShowLyrics);
			if (isShowLyrics) {//������ó���ʾ��ʣ����ȡ��ʣ����򣬲���ȡ
				task = new LyricsAsyncTask(PlayingActivity.this, music,lyricsMode);
				task.execute();
			}
		}

	}
}
