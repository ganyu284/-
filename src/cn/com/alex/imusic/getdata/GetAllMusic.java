package cn.com.alex.imusic.getdata;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import cn.com.alex.imusic.bean.Music;

public class GetAllMusic {
	public static ArrayList<Music> getFromMediaStore(Activity activity) {
		ArrayList<Music> musics = new ArrayList<Music>();

		ContentResolver mResolver = activity.getContentResolver();
		
		String selection = MediaStore.Audio.Media.IS_MUSIC + "=1 and "
				+ MediaStore.Audio.Media.DURATION + ">20000";
		Cursor cursor = mResolver.query(
				// cursor = this.managedQuery(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection,
				null, MediaStore.Audio.Media.TITLE);

		while (cursor.moveToNext()) {
			Music music = new Music();
			int song_id = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
			// Log.e("Music ID", "Music ID" + song_id);
			music.setMusic_id(song_id);

			int album_id = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
			music.setAlbum_id(album_id);
			// ���������� ��MediaStore.Audio.Media.TITLE
			String title = encodeCN(cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
			music.setTitle(title);
			// System.out.println(title);

			// ������ר������MediaStore.Audio.Media.ALBUM
			String album = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
			music.setAlbum(album);

			// �����ĸ������� MediaStore.Audio.Media.ARTIST
			String artist = encodeCN(cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
			music.setArtist(artist);
			// ����ID
			int artist_id = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));
			music.setArtist_id(artist_id);

			// �����ļ���·�� ��MediaStore.Audio.Media.DATA
			String url = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			music.setData(url);

			// �����Ĳ���ʱ�� ��MediaStore.Audio.Media.DURATION
			int duration = cursor.getInt(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
			music.setDuration(duration);

			// ����
			String cover = GetAlbumArt.getAlbumArt(activity, album_id + "");
			music.setCover(cover);

			// �����ļ��Ĵ�С ��MediaStore.Audio.Media.SIZE
			// int size =
			// cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
			music.setTitle_key(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE_KEY));
			
			musics.add(music);

		}
		cursor.close();
		cursor = null;
		return musics;
	}

	// ת������
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
}
