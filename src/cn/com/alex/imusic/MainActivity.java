package cn.com.alex.imusic;

import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.alex.imusic.util.AppExit;
import cn.com.alex.imusic.util.PlayListHelper;

public class MainActivity extends TabActivity {
	private boolean isPlaying = true;
	private ServiceReceiver receiver;
	NotificationManager notiMgr;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		System.out.println("Main onCreate");
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayListHelper.INTENT_ACTION_CONTROL);
		receiver = new ServiceReceiver();
		this.registerReceiver(receiver, filter);

		setTabs();
		AppExit.allActivity.add(this);
		notiMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent control_intent = new Intent(PlayListHelper.INTENT_ACTION_CONTROL);
		control_intent.putExtra("playControl", PlayListHelper.MAIN_CREATE);

		// 发送给播放服务
		sendBroadcast(control_intent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		System.out.println(">>>>>>>>>>>>>>>>");
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// System.out.println("landscape");
			Intent intent = new Intent(this, CoverFlowMain.class);
			intent.putExtra("fromWhere", PlayListHelper.FROM_MAIN);
			startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
		Intent control_intent = new Intent(PlayListHelper.INTENT_ACTION_CONTROL);
		if (!isPlaying) {
			// 停止服务的通知
			control_intent.putExtra("playControl", PlayListHelper.CLICK_STOP);
			notiMgr.cancel(R.layout.notification_layout);// 取消notification
		} else {
			// 退出主界面但播放中
			control_intent.putExtra("playControl",
					PlayListHelper.MAIN_EXIT_STILL_PLAYING);
		}
		// 发送给播放服务
		sendBroadcast(control_intent);
		this.unregisterReceiver(receiver);
		AppExit.exitApp(this);
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		System.out.println("Main onSaveInstanceState");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("Main onResume");
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// this.finish();
		// AppExit.exitApp(this);
		// super.onBackPressed();
		// System.out.println("sddddddd");
		// return;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("Main onPause");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("Main onStop");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("Main onStart");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		System.out.println("Main onRestart");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		System.out.println("Main onNewIntent");
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub
		System.out.println("Main onRetainNonConfigurationInstance");
		return super.onRetainNonConfigurationInstance();

	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		// TODO Auto-generated method stub
		System.out.println("Main onRestoreInstanceState");
		super.onRestoreInstanceState(state);
	}

	private void setTabs() {
		addTab("列表", R.drawable.playlist_1, TabPlayListActivity.class,1);
		addTab("歌手", R.drawable.artist, TabArtistActivity.class,2);
		addTab("歌曲", R.drawable.song, TabAllMusicActivity.class,3);
		addTab("专辑", R.drawable.album, TabAlbumActivity.class,4);
		addTab("设置", R.drawable.settings, TabSettingActivity.class,5);
	}

	private void addTab(String labelId, int drawableId, Class<?> c,int id) {
		TabHost tabHost = getTabHost();

		Intent intent = new Intent(this, c);
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.tab_indicator, getTabWidget(), false);
		
		tabIndicator.setId(id);
		
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);

		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

	// 接收控制动作的Receiver
	class ServiceReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			int intent_control = intent.getIntExtra("playControl", -1);
			// System.out.println("SSSSSSSS" + intent_control);
			if (intent_control == PlayListHelper.CLICK_PLAY_PAUSE) {
				isPlaying = intent.getBooleanExtra("isPlaying", true);
			}
		}
	}

}