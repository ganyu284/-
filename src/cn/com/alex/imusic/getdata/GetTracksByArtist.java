package cn.com.alex.imusic.getdata;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import cn.com.alex.imusic.bean.Music;

public class GetTracksByArtist {
	public static ArrayList<Music> getTracksByArtist(Activity activity,
			long artist_id) {
		ArrayList<Music> musics = new ArrayList<Music>();
		ContentResolver mResolver = activity.getContentResolver();
		String[] projection = { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.TITLE_KEY };
		String selection = MediaStore.Audio.Media.ARTIST_ID + "=" + artist_id
				+ " and " + MediaStore.Audio.Media.IS_MUSIC + "=1 and "
				+ MediaStore.Audio.Media.DURATION + ">20000";// 20秒以上的才取出来

		Cursor cursor = mResolver.query(
				// cursor = this.managedQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
				selection, null, MediaStore.Audio.Media.TITLE);

		while (cursor.moveToNext()) {
			Music music = new Music();
			music.setMusic_id(cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
			music.setAlbum_id(cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
			music.setTitle(cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
			music.setDuration(cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
			music.setAlbum(cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
			music.setArtist_id(cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)));
			music.setArtist(cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));			
			music.setData(cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
			music.setCover(GetAlbumArt.getAlbumArt(
					activity,
					cursor.getInt(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
							+ ""));
			music.setDuration(cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
			music.setTitle_key(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE_KEY));
			musics.add(music);
			//System.out.println();
			// cursor.moveToNext();
		}
		cursor.close();
		cursor = null;
		mResolver = null;
		return musics;
	}
}
