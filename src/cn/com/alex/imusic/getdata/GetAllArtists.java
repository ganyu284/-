package cn.com.alex.imusic.getdata;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import cn.com.alex.imusic.bean.Artist;

public class GetAllArtists {

	public static ArrayList<Artist> getAllArtists(Activity activity) {
		ArrayList<Artist> artists = new ArrayList<Artist>();
		ContentResolver mResolver = activity.getContentResolver();
		Cursor cursor = mResolver.query(
				// cursor = this.managedQuery(
				MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null,
				null, MediaStore.Audio.Artists.ARTIST);

		while (cursor.moveToNext()) {
			artists.add(new Artist(
					cursor.getInt(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)),
					cursor.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)),
					cursor.getInt(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS)),
					cursor.getInt(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS))));
			
		}
		cursor.close();
		cursor = null;
		return artists;
	}
}
