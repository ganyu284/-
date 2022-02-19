package cn.com.alex.imusic.getdata;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class AddMusicToPlaylist {
	public static void addMusic(int musicid, Context ctx, int playListID) {
		ContentResolver mResolver = ctx.getContentResolver();
		Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri(
				"external", playListID);

		String[] cols = new String[] { "count(*)" };
		Cursor cur = mResolver.query(membersUri, cols, null, null, null);
		cur.moveToFirst();
		final int base = cur.getInt(0);
		cur.close();

		ContentValues values = new ContentValues();
//		values.put(MediaStore.Audio.Media._ID, music.getMusic_id());
//		values.put(MediaStore.Audio.Media.ALBUM_ID, music.getAlbum_id());
//		values.put(MediaStore.Audio.Media.ALBUM, music.getAlbum());
//		values.put(MediaStore.Audio.Media.ARTIST, music.getAlbum());
//		values.put(MediaStore.Audio.Media.ARTIST_ID, music.getArtist_id());
//		values.put(MediaStore.Audio.Media.TITLE, music.getTitle());
//		// values.put(MediaStore.Audio.Media.ALBUM_ART, music.getCover());
//		values.put(MediaStore.Audio.Media.DATA, music.getData());
//		values.put(MediaStore.Audio.Media.DURATION, music.getDuration());
		values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER,
				Integer.valueOf(base + musicid));
		values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID,
				musicid);
		values.put(MediaStore.Audio.Playlists.Members.PLAYLIST_ID, playListID);		

		mResolver.insert(membersUri, values);
	}

	public static void addMusics(int[] musicsids, Context ctx,
			int playListID) {
		for (int i = 0; i < musicsids.length; i++) {
			addMusic(musicsids[i], ctx, playListID);
		}
	}
}
