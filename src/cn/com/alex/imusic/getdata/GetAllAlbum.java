package cn.com.alex.imusic.getdata;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import cn.com.alex.imusic.bean.Album;

public class GetAllAlbum {
	public static ArrayList<Album> getAllAlbums(Activity activity) {
		ArrayList<Album> albums = new ArrayList<Album>();
		ContentResolver mResolver = activity.getContentResolver();
		
		Cursor cursor = mResolver.query(
				// cursor = this.managedQuery(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Albums.ALBUM);

		while (cursor.moveToNext()) {
			albums.add(new Album(
					cursor.getInt(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)),
					cursor.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)),
					cursor.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)),
					cursor.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART)),
					cursor.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)),
					cursor.getInt(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS))));
		}
		cursor.close();
		cursor = null;
		return albums;
	}
}
