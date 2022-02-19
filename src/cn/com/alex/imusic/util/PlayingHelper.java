package cn.com.alex.imusic.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import cn.com.alex.imusic.PlayingActivity;
import cn.com.alex.imusic.R;
import cn.com.alex.imusic.bean.Music;

public class PlayingHelper {
	public static Class<? extends Activity> fromActivity;

	public static void playMusic(Context ctx, ArrayList<Music> musics,
			int currentPos, int a) {
		// ≤•∑≈“Ù¿÷
		ArrayList<Integer> music_ids;
		Intent intent = new Intent(ctx, PlayingActivity.class);
		PlayingHelper.fromActivity = PlayingActivity.class;
		music_ids = new ArrayList<Integer>();
		for (int i = 0; i < musics.size(); i++) {
			music_ids.add(musics.get(i).getMusic_id());
		}
		intent.putIntegerArrayListExtra("music_ids", music_ids);
		intent.putExtra("current_pos", currentPos);
		ctx.startActivity(intent);
	}

	// public static ArrayList<Music> playList;
	public static void playMusic(Activity ctx, int currentPos,
			final ArrayList<Music> playList) {
		System.out.println("playMusic.....");
		//Activity aty = (Activity)ctx;
		Intent intent = new Intent(ctx, PlayingActivity.class);
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("current_pos", currentPos);
		Music[] musicArray = new Music[playList.size()];
		musicArray = playList.toArray(musicArray);
		intent.putExtra("playListArray", musicArray);
		//ctx.startActivity(intent);
		ctx.startActivityForResult(intent,R.layout.main);
		//aty.overridePendingTransition(R.anim.animation_enter,R.anim.animation_leave);
		//PlayListHelper.serializePlaylist(ctx, playList);
	}
}
