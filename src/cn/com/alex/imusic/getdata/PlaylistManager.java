package cn.com.alex.imusic.getdata;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import cn.com.alex.imusic.util.PlayListHelper;

public class PlaylistManager {
	// 新增列表
	public static void addPlaylist(Context ctx, String playlistname) {
		ContentResolver mResolver = ctx.getContentResolver();
		Uri membersUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		ContentValues values = new ContentValues();
		values.put(MediaStore.Audio.Playlists.NAME, playlistname);
		Uri uri = mResolver.insert(membersUri, values);
		int id = -1;
		Cursor cursor = mResolver.query(uri,
				new String[] { MediaStore.Audio.Playlists.Members._ID }, null,
				null, null);
		if (cursor != null && cursor.moveToFirst()) {
			id = cursor
					.getInt(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members._ID));
			Intent intent = new Intent(
					PlayListHelper.INTENT_ACTION_ADD_PLAYLIST_SUCCESS);
			intent.putExtra("playlist_id", id);
			intent.putExtra("playlist_name", playlistname);
			ctx.sendBroadcast(intent);
			cursor.close();
			cursor = null;
		}
	}

	/**
	 * 删除列表
	 * 
	 * @param ctx
	 * @param playlist_id
	 * @return 删除的记录数
	 */
	public static int deletePlaylist(Context ctx, int playlist_id) {
		ContentResolver mResolver = ctx.getContentResolver();
		// 先删除从表内容
		Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",
				playlist_id);
		mResolver.delete(uri, MediaStore.Audio.Playlists.Members._ID + "="
				+ playlist_id, null);
		// 后删除主表内容
		Uri membersUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		return mResolver.delete(membersUri,
				MediaStore.Audio.Playlists.Members._ID + "=" + playlist_id,
				null);
	}

	// 更新列表
	public static int updatePlaylist(Context ctx, int playlist_id,
			String playlist_name) {
		ContentResolver mResolver = ctx.getContentResolver();
		Uri membersUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		ContentValues values = new ContentValues();
		values.put(MediaStore.Audio.Playlists.NAME, playlist_name);
		return mResolver.update(membersUri, values,
				MediaStore.Audio.Playlists.Members._ID + "=" + playlist_id,
				null);
	}
}
