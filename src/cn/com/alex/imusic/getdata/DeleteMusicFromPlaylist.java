package cn.com.alex.imusic.getdata;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

public class DeleteMusicFromPlaylist {
	public static int deleteMusic(Context ctx, int playListID,int id_in_playlist) {
		ContentResolver mResolver = ctx.getContentResolver();
		Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri(
				"external", playListID);

		return mResolver.delete(membersUri, MediaStore.Audio.Playlists.Members._ID+"="+id_in_playlist, null);
		//Toast.makeText(ctx, "É¾³ý¸èÇú³É¹¦", Toast.LENGTH_LONG).show();
	}
}
