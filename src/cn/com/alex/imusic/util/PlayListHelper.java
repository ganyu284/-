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
	// ������ͣ��Intent Action
	public static final String INTENT_ACTION_CONTROL = "cn.alex.imusic.control";
	// ��ǰ���Ÿ���λ��
	public static final String INTENT_ACTION_POS = "cn.alex.imusic.pos";
	// ���Ž���
	public static final String INTENT_ACTION_PROGRESS = "cn.alex.imusic.progress";
	// �ֹ���������
	public static final String INTENT_ACTION_SEEK_PROGRESS = "cn.alex.imusic.seekprogress";
	// �ظ���������
	public static final String INTENT_ACTION_FLAG = "cn.alex.imusic.flag";
	// ���������������б�
	public static final String INTENT_ACTION_ADD_MUSIC_PLAYLIST = "cn.alex.imusic.addtoplaylist";
	// �Ӳ����б�ɾ������
	public static final String INTENT_ACTION_DELETE_MUSIC_FROM_PLAYLIST = "cn.alex.imusic.deletefromplaylist";
	// ɾ�������б�
	public static final String INTENT_ACTION_DELETE__PLAYLIST = "cn.alex.imusic.deleteplaylist";
	// ���������б�ɹ�
	public static final String INTENT_ACTION_ADD_PLAYLIST_SUCCESS = "cn.alex.imusic.addplaylist.successful";
	// ���÷����ı�
	public static final String INTENT_ACTION_SETTINGS_CHANGEED = "cn.alex.imusic.settingschanged";

	// ��ȡ��ʷ�ʽ
	public static final String GET_LYRICS_MODE_ALWAYS = "Always";
	public static final String GET_LYRICS_MODE_WIFI = "Wifi";
	public static final String GET_LYRICS_MODE_NEVER = "Never";

	public static final String PLAYLIST_FILENAME = "playlist.al";

	// ����״̬
	public static final int STATUS_STOPPED = 0;
	public static final int STATUS_PLAYING = 1;
	public static final int STATUS_PAUSE = 2;
	// ���ƶ���
	public static final int CLICK_STOP = 0;
	public static final int CLICK_PLAY_PAUSE = 1;
	public static final int CLICK_PREVIOUS = 2;
	public static final int CLICK_NEXT = 3;

	// �������Ѿ��˳��������ֲ�����
	public static final int MAIN_EXIT_STILL_PLAYING = 4;
	public static final int MAIN_CREATE = 5;// ����������
	public static final int SEND_NOTIFICATION = 100;
	// playingActivity�������Ķ���
	public static final int PLAYING_ACTIVITY_NEW = 10;

	// ���Ŷ���
	// public static final int ACTION_STOP = 1;
	public static final int ACTION_PLAY = 1;
	public static final int ACTION_PAUSE = 2;

	// ѭ����������Ʊ�־λ
	public static final int SHUFFLE_OFF = 1; // �����
	public static final int SHUFFLE_ON = 10;// �����
	public static final int REPEAT_OFF = 100;// �ظ���
	public static final int REPEAT_ONCE = 1000;// �ظ�һ�׸�
	public static final int REPEAT_ALL = 10000;// �����б��ظ�

	// ���ĸ�Activity���뵽CoverFlowMain��
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
