package cn.com.alex.imusic.getdata;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Albums;

public class GetAlbumArt {
	public static String getAlbumArt(Context activity, String album_id) {
		String art = null;
		Cursor cur = activity.getContentResolver().query(
				Albums.EXTERNAL_CONTENT_URI, new String[] { Albums.ALBUM_ART },
				Albums._ID + "=?", new String[] { album_id }, null);
		if (cur != null) {
			cur.moveToFirst();
			art = cur.getString(cur.getColumnIndex(Albums.ALBUM_ART));
			cur.close();
		}
		return art;
	}
}
