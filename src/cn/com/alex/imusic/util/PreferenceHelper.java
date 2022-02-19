package cn.com.alex.imusic.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class PreferenceHelper {
	static SharedPreferences preferences;

	public static int readShuffle(Context ctx) {

		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		return preferences.getInt("Shuffle", PlayListHelper.SHUFFLE_OFF);
	}

	public static int readRepeat(Context ctx) {
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		return preferences.getInt("Repeat", PlayListHelper.REPEAT_OFF);
	}

	public static void writeShuffle(Context ctx, int shuffleValue) {
		//preferences.unregisterOnSharedPreferenceChangeListener(lsn);
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		Editor editor = preferences.edit();
		editor.putInt("Shuffle", shuffleValue);
		editor.commit();
	}

	public static void writeRepeat(Context ctx, int repeatValue) {
		//preferences.unregisterOnSharedPreferenceChangeListener(lsn);
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		Editor editor = preferences.edit();
		editor.putInt("Repeat", repeatValue);
		editor.commit();
	}

	public static void writeLastPos(Context ctx, int lastPos, int lastProgress) {
		//preferences.unregisterOnSharedPreferenceChangeListener(lsn);
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		Editor editor = preferences.edit();
		editor.putInt("LastPos", lastPos);
		editor.putInt("LastProgress", lastProgress);
		editor.commit();
	}

	public static int readLastPos(Context ctx) {
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		return preferences.getInt("LastPos", 0);
	}

	public static void writeLastMusic(Context ctx, int lastMusicId,
			int lastProgress) {
		//preferences.unregisterOnSharedPreferenceChangeListener(lsn);
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		Editor editor = preferences.edit();
		editor.putInt("LastMusicId", lastMusicId);
		editor.putInt("LastProgress", lastProgress);
		editor.commit();
	}

	public static int readLastMusic(Context ctx) {
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		return preferences.getInt("LastMusicId", 0);
	}

	public static int readLastProgress(Context ctx) {
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		return preferences.getInt("LastProgress", 0);
	}

	// 读取PreferenceActivity中设置的相关值
	// 读取下载歌词的方式
	public static String readDownloadLyricsMode(Context ctx) {
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		lsn.setContext(ctx);
		preferences.registerOnSharedPreferenceChangeListener(lsn);
		return preferences.getString("dlLyrics", "Wifi");
	}

	// 是否显示歌词
	public static boolean readIfShowLyrics(Context ctx) {
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		lsn.setContext(ctx);
		preferences.registerOnSharedPreferenceChangeListener(lsn);
		return preferences.getBoolean("show_lyrics", true);
	}

	// 是否晃动播放下一首
	public static boolean readShakeStatus(Context ctx) {
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		lsn.setContext(ctx);
		preferences.registerOnSharedPreferenceChangeListener(lsn);
		return preferences.getBoolean("shake_next", true);
	}

	// 晃动播放灵敏度
	public static int readShakeStrength(Context ctx) {
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		lsn.setContext(ctx);
		preferences.registerOnSharedPreferenceChangeListener(lsn);
		return Integer
				.parseInt((preferences.getString("shake_strength", "300")));
	}

	// static OnSharedPreferenceChangeListener lsn = new
	// OnSharedPreferenceChangeListener() {
	static MySharedPreferenceChangeListener lsn = new MySharedPreferenceChangeListener();

	static class MySharedPreferenceChangeListener implements
			OnSharedPreferenceChangeListener {
		private Context context;

		
		public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
			//System.out.println("PPPPRRREEEFFF");
			Intent intent = new Intent(
					PlayListHelper.INTENT_ACTION_SETTINGS_CHANGEED);
			if (key.equals("show_lyrics")) {
				// 是否显示歌词
				intent.putExtra("isShowLyrics", pref.getBoolean(key, true));
				context.sendBroadcast(intent);
			} else if (key.equals("shake_strength")) {// 晃动灵敏度
				intent.putExtra("shake_strength",
						Integer.parseInt(pref.getString(key, "300")));
				context.sendBroadcast(intent);
			} else if (key.equals("shake_next")) { // 是否晃动播放下一首
				intent.putExtra("shake_next", pref.getBoolean(key, true));
				context.sendBroadcast(intent);
			} else if (key.equals("downloadlyrics")) {// 下载歌词方式				
				intent.putExtra("downloadlyrics", pref.getString(key, "Wifi"));
				context.sendBroadcast(intent);
			}
		}

		public void setContext(Context context) {
			this.context = context;
		}

		public Context getContext() {
			return context;
		}
	};
}
