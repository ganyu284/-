package cn.com.alex.imusic.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import java.io.InputStream;
import android.graphics.BitmapFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.os.ParcelFileDescriptor;
import java.io.FileDescriptor;


public class MusicUtils {
	
	public static Bitmap getArtwork(Context context, long song_id,
			long album_id, boolean allowdefault) {
		//android.util.Log.w("",song_id+"fffffffffff"+album_id);
		if (album_id < 0) {
			// This is something that is not in the database, so get the album
			// art directly
			// from the file.
			//android.util.Log.w("",song_id+"kkkkkkk"+album_id);
			if (song_id >= 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if (bm != null) {
					return bm;
				}
			}
			if (allowdefault) {
				return getDefaultArtwork(context);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
		if (uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, sBitmapOptions);
			} catch (FileNotFoundException ex) {
				// The album art thumbnail does not actually exist. Maybe the
				// user deleted it, or
				// maybe it never existed to begin with.
				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if (bm == null && allowdefault) {
							return getDefaultArtwork(context);
						}
					}
				} else if (allowdefault) {
					bm = getDefaultArtwork(context);
				}
				return bm;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
				}
			}
		}

		return null;
	}
	public static Bitmap getArtwork(Context context, long song_id,
			long album_id, boolean allowdefault,int defaultDrawable) {
		if (album_id < 0) {
			// This is something that is not in the database, so get the album
			// art directly
			// from the file.
			if (song_id >= 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if (bm != null) {
					return bm;
				}
			}
			if (allowdefault) {
				return getDefaultArtwork(context,defaultDrawable);
			}
			return null;
		}
		ContentResolver res = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
		if (uri != null) {
			InputStream in = null;
			try {
				in = res.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, sBitmapOptions);
			} catch (FileNotFoundException ex) {
				// The album art thumbnail does not actually exist. Maybe the
				// user deleted it, or
				// maybe it never existed to begin with.
				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if (bm == null && allowdefault) {
							return getDefaultArtwork(context,defaultDrawable);
						}
					}
				} else if (allowdefault) {
					bm = getDefaultArtwork(context,defaultDrawable);
				}
				return bm;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
				}
			}
		}

		return null;
	}
	
	private static Bitmap getArtworkFromFile(Context context, long songid,
			long albumid) {
		//android.util.Log.e("e",songid+">>>>"+albumid);
		Bitmap bm = null;
		if (albumid < 0 && songid < 0) {
			throw new IllegalArgumentException(
					"Must specify an album or a song id");
		}
		
		try {
			//android.util.Log.w("",">>>>>>>>>albumid:"+albumid);
			if (albumid < 0) {
				//android.util.Log.w("",songid+"##########albumid:"+albumid);
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ songid + "/albumart");
				android.util.Log.w("","uri...."+uri.toString());
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}
			} else {
				//android.util.Log.w("","^^^^^^^^^albumid:"+albumid);
				Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}
			}
			
		} 
		catch(IllegalStateException e){
			
		}
		catch (FileNotFoundException ex) {

		}

		return bm;
	}

	private static Bitmap getDefaultArtwork(Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return null;
		/*BitmapFactory.decodeStream(context.getResources()
				.openRawResource(R.drawable.playlist_bg), null, opts);
				*/
	}
	
	private static Bitmap getDefaultArtwork(Context context,int defaultDrawable) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(defaultDrawable), null, opts);
	}

	private static final Uri sArtworkUri = Uri
			.parse("content://media/external/audio/albumart");
	private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
	//private static Bitmap mCachedBit = null;
}
