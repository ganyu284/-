package cn.com.alex.imusic.getdata;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import cn.com.alex.imusic.bean.Playlist;

public class GetPlaylist {
	public static ArrayList<Playlist> getPlaylist(Activity activity) {
		ArrayList<Playlist> musics = new ArrayList<Playlist>();

		ContentResolver mResolver = activity.getContentResolver();

		Cursor cursor = mResolver.query(
				// cursor = this.managedQuery(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null,
				null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);

		while (cursor.moveToNext()) {
			Playlist pl = new Playlist();
			pl.set_id(cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID)));
			pl.setDate_added(cursor.getLong(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Playlists.DATE_ADDED)));
			pl.setName(cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME)));
			pl.setDate_modified(cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Playlists.DATE_MODIFIED)));	
			musics.add(pl);
		}
		cursor.close();
		cursor = null;
		mResolver = null;
		
		return musics;
	}
}
