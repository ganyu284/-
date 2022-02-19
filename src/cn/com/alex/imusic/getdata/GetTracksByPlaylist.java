package cn.com.alex.imusic.getdata;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import cn.com.alex.imusic.bean.Music;

public class GetTracksByPlaylist {

	// public static ArrayList<Music> getTracksByPlaylist(Activity activity,
	// int playListID) {
	// ArrayList<Music> musics = new ArrayList<Music>();
	// String[] ARG_STRING = {
	// MediaStore.Audio.Media._ID,
	// // MediaStore.Audio.Media.DATA,
	// MediaStore.Audio.Media.DISPLAY_NAME,
	// MediaStore.Video.Media.SIZE, MediaStore.Audio.Media.TITLE,
	// MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
	// MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST,
	// MediaStore.Audio.Media.ARTIST_ID,
	// MediaStore.Audio.Media.TITLE_KEY
	// // android.provider.MediaStore.MediaColumns.DATA
	// };
	// Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri(
	// "external", playListID);
	// Cursor cursor = activity.managedQuery(membersUri, ARG_STRING, null,
	// null, null);
	// while (cursor.moveToNext()) {
	// Music music = new Music();
	//
	// music.setMusic_id(cursor.getInt(cursor
	// .getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
	// music.setAlbum_id(cursor.getInt(cursor
	// .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
	// music.setTitle(cursor.getString(cursor
	// .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
	// music.setMusic_id(cursor.getInt(cursor
	// .getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
	// music.setData(cursor.getString(cursor
	// .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
	// music.setArtist(cursor.getString(cursor
	// .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
	// music.setArtist_id(cursor.getInt(cursor
	// .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)));
	// music.setCover(GetAlbumArt.getAlbumArt(
	// activity,
	// cursor.getInt(cursor
	// .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
	// + ""));
	// music.setDuration(cursor.getInt(cursor
	// .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
	// music.setTitle_key(cursor
	// .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE_KEY));
	// // System.out.println("D_Name:" + cursor.getString(1));
	// // System.out.println("AD_Name" + cursor.getShort(3));
	// }
	// /*
	// * int theSongIDIwantToPlay = 0; // PLAYING FROM THE FIRST SONG if
	// * (songsWithingAPlayList != null) {
	// * songsWithingAPlayList.moveToPosition(theSongIDIwantToPlay); String
	// * DataStream = songsWithingAPlayList.getString(4);
	// * PlayMusic(DataStream); songsWithingAPlayList.close(); }
	// */
	// cursor.close();
	//
	// cursor = null;
	// return musics;
	// }

	public static ArrayList<Music> getTracksByPlaylist(Activity activity,
			int playListID) {
		ArrayList<Music> musics = new ArrayList<Music>();
		String[] ARG_STRING = { MediaStore.Audio.Playlists.Members.AUDIO_ID,
				MediaStore.Audio.Playlists.Members._ID };
		Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri(
				"external", playListID);
		Cursor cursor = activity.managedQuery(membersUri, ARG_STRING, null,
				null, null);
		Music music = null;
		while (cursor.moveToNext()) {
			music = getMusicByMusicId(
					activity,
					cursor.getInt(cursor
							.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
			music.setId_in_playlist(cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members._ID)));
			if (music != null)
				musics.add(music);
		}
		cursor.close();

		cursor = null;
		return musics;
	}

	private static Music getMusicByMusicId(Context ctx, int music_id) {
		ContentResolver mResolver = ctx.getContentResolver();
		Cursor cursor = mResolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
				MediaStore.Audio.Media._ID + "=" + music_id, null,
				MediaStore.Audio.Media.TITLE);
		Music music = null;
		if (cursor.moveToFirst()) {
			music = new Music();
			int song_id = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
			// Log.e("Music ID", "Music ID" + song_id);
			music.setMusic_id(song_id);

			int album_id = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
			music.setAlbum_id(album_id);
			// 歌曲的名称 ：MediaStore.Audio.Media.TITLE
			String title = encodeCN(cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
			music.setTitle(title);
			// System.out.println(title);

			// 歌曲的专辑名：MediaStore.Audio.Media.ALBUM
			String album = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
			music.setAlbum(album);

			// 歌曲的歌手名： MediaStore.Audio.Media.ARTIST
			String artist = encodeCN(cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
			music.setArtist(artist);
			// 歌手ID
			int artist_id = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));
			music.setArtist_id(artist_id);

			// 歌曲文件的路径 ：MediaStore.Audio.Media.DATA
			String url = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			music.setData(url);

			// 歌曲的播放时长 ：MediaStore.Audio.Media.DURATION
			int duration = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
			music.setDuration(duration);

			// 封面
			String cover = GetAlbumArt.getAlbumArt(ctx, album_id + "");
			music.setCover(cover);

			// 歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
			// int size =
			// cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
			music.setTitle_key(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE_KEY));
		}
		return music;
	}

	// 转换编码
	public static String encodeCN(String src) {
		if (src == null)
			src = "Unknown";
		try {
			return new String(src.getBytes("GBK"), "GBK");
			// return new String(src.getBytes("ISO8859_1"), "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return src;
		}
	}

	public static void PlayMusic(String DataStream) {
		MediaPlayer mpObject = new MediaPlayer();
		if (DataStream == null)
			return;
		try {
			mpObject.setDataSource(DataStream);
			mpObject.prepare();
			mpObject.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
