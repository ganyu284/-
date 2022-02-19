package cn.com.alex.imusic.util.cache;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class FileCacheDemo {
	public InputStream getFile(Context context, String uniqueId) throws IOException {
		FileCache cache = FileCache.getFileCache(context);
		// will return null if uniqueId has not been inserted
		return cache.get(uniqueId);
	}

	/**
	 * 
	 * Insert a file into the cache.
	 * 
	 * @param context
	 * @param uniqueId
	 *            the unique identifier used to get the file back sometime in
	 *            the future. A Url works well.
	 * @param input
	 *            data to be inserted. The Cache will read the input until it is
	 *            empty.
	 */
	public void putFile(Context context, String uniqueId, InputStream input) {
		FileCache cache = null;
		try {
			cache = FileCache.getFileCache(context);
			cache.put(uniqueId, input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteFile(Context context, String uniqueId) {
		FileCache cache;
		try {
			cache = FileCache.getFileCache(context);
			cache.delete(uniqueId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
