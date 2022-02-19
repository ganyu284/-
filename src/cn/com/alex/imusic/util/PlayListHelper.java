package cn.com.alex.imusic.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import cn.com.alex.imusic.bean.Music;

public class PlayListHelper {
	// 播放暂停的Intent Action
	public static final String INTENT_ACTION_CONTROL = "cn.alex.imusic.control";
	// 当前播放歌曲位置
	public static final String INTENT_ACTION_POS = "cn.alex.imusic.pos";
	// 播放进度
	public static final String INTENT_ACTION_PROGRESS = "cn.alex.imusic.progress";
	// 手工拉动进度
	public static final String INTENT_ACTION_SEEK_PROGRESS = "cn.alex.imusic.seekprogress";
	// 重复、随机标记
	public static final String INTENT_ACTION_FLAG = "cn.alex.imusic.flag";
	// 新增歌曲到播放列表
	public static final String INTENT_ACTION_ADD_MUSIC_PLAYLIST = "cn.alex.imusic.addtoplaylist";
	// 从播放列表删除歌曲
	public static final String INTENT_ACTION_DELETE_MUSIC_FROM_PLAYLIST = "cn.alex.imusic.deletefromplaylist";
	// 删除播放列表
	public static final String INTENT_ACTION_DELETE__PLAYLIST = "cn.alex.imusic.deleteplaylist";
	// 新增播放列表成功
	public static final String INTENT_ACTION_ADD_PLAYLIST_SUCCESS = "cn.alex.imusic.addplaylist.successful";
	// 配置发生改变
	public static final String INTENT_ACTION_SETTINGS_CHANGEED = "cn.alex.imusic.settingschanged";

	// 获取歌词方式
	public static final String GET_LYRICS_MODE_ALWAYS = "Always";
	public static final String GET_LYRICS_MODE_WIFI = "Wifi";
	public static final String GET_LYRICS_MODE_NEVER = "Never";

	public static final String PLAYLIST_FILENAME = "playlist.al";

	// 播放状态
	public static final int STATUS_STOPPED = 0;
	public static final int STATUS_PLAYING = 1;
	public static final int STATUS_PAUSE = 2;
	// 控制动作
	public static final int CLICK_STOP = 0;
	public static final int CLICK_PLAY_PAUSE = 1;
	public static final int CLICK_PREVIOUS = 2;
	public static final int CLICK_NEXT = 3;

	// 主界面已经退出，但音乐播放中
	public static final int MAIN_EXIT_STILL_PLAYING = 4;
	public static final int MAIN_CREATE = 5;// 进入主界面
	public static final int SEND_NOTIFICATION = 100;
	// playingActivity新启动的动作
	public static final int PLAYING_ACTIVITY_NEW = 10;

	// 播放动作
	// public static final int ACTION_STOP = 1;
	public static final int ACTION_PLAY = 1;
	public static final int ACTION_PAUSE = 2;

	// 循环和随机控制标志位
	public static final int SHUFFLE_OFF = 1; // 随机关
	public static final int SHUFFLE_ON = 10;// 随机开
	public static final int REPEAT_OFF = 100;// 重复关
	public static final int REPEAT_ONCE = 1000;// 重复一首歌
	public static final int REPEAT_ALL = 10000;// 整个列表重复

	// 从哪个Activity进入到CoverFlowMain的
	public static final int FROM_MAIN = 1;
	public static final int FROM_PLAYING = 2;

	public static void serializePlaylist(final Context ctx,
			final ArrayList<Music> playList) {

		new Thread(new Runnable() {
			FileOutputStream fos;

			
			public void run() {
				try {
					fos = ctx.openFileOutput(PLAYLIST_FILENAME,
							Context.MODE_PRIVATE);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					// oos.writeObject(PlayingHelper.playList);
					oos.writeObject(playList);
					oos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Music> deserializePlaylist(Context ctx) {
		ArrayList<Music> musics = new ArrayList<Music>();
		try {
			FileInputStream fis = ctx.openFileInput(PLAYLIST_FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			musics = (ArrayList<Music>) ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return musics;
	}
}
