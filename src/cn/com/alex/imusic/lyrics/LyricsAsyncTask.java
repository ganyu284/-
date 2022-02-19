package cn.com.alex.imusic.lyrics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import cn.com.alex.imusic.PlayingActivity;
import cn.com.alex.imusic.bean.Music;
import cn.com.alex.imusic.lyrics.network.GetLyricsFromNetwork;
import cn.com.alex.imusic.lyrics.network.NetworkStatus;
import cn.com.alex.imusic.util.PlayListHelper;
import cn.com.alex.imusic.util.cache.FileCache;

public class LyricsAsyncTask extends
		AsyncTask<String, Void, Hashtable<String, String>> {

	InputStream is;
	int albumId, trackId;
	String title, artist;

	FileCache cache;
	Hashtable<String, String> lrcTable = null;
	PlayingActivity activity;
	String path;
	String lyricsMode;

	public LyricsAsyncTask(PlayingActivity activity, Music music,
			String lyricsMode) {

		this.albumId = music.getAlbum_id();
		this.trackId = music.getMusic_id();
		this.title = music.getTitle();
		this.artist = music.getArtist();
		this.path = music.getData();

		this.activity = activity;
		this.lyricsMode = lyricsMode;
	}

	@Override
	protected void onPostExecute(Hashtable<String, String> result) {
		// TODO Auto-generated method stub
		activity.setLrcTable(result);
		// super.onPostExecute(result);
	}

	@Override
	protected Hashtable<String, String> doInBackground(String... params) {
		String lrcPath = null;
		// 先清空原来的列表

		if (lrcTable != null)
			lrcTable.clear();
		// System.out.println("run,.......");
		int size = 0;
		is = null;

		// 尝试从同一目录的lrc文件中读取
		if (is == null) {
			String filePath = path.substring(0, path.lastIndexOf('/') + 1);
			String fileName = path.substring(path.lastIndexOf('/') + 1,
					path.lastIndexOf("."));
			lrcPath = filePath + fileName + ".lrc";
			File file = new File(lrcPath);
			// System.out.println(filePath + fileName +
			// ".lrc"+file.exists());
			if (file.exists()) {
				try {
					is = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (is != null) {
			try {
				size = is.available();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (size == 0) { // 如果内容为空，从网络读取
				lrcTable = readFromNetwork(lrcPath);
			} else {
				LrcDecode ld = new LrcDecode().readLrc(is);
				lrcTable = ld.getLrcTable();
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				is = null;

			}
		} else { // 如果为空，则从网络读取
			// 如果允许所有情况下的网络读取
			if (PlayListHelper.GET_LYRICS_MODE_ALWAYS.equals(lyricsMode)) {
				lrcTable = readFromNetwork(lrcPath);
			}
			if (PlayListHelper.GET_LYRICS_MODE_WIFI.equals(lyricsMode)
					&& NetworkStatus.connectType(activity) == ConnectivityManager.TYPE_WIFI) {
				lrcTable = readFromNetwork(lrcPath);
			}
		}

		return lrcTable;
	}

	private Hashtable<String, String> readFromNetwork(String lrcPath) {
		Hashtable<String, String> lrcTable = null;
		FileOutputStream fos = null;
		if (NetworkStatus.isConnected(activity.getApplicationContext()))
			is = GetLyricsFromNetwork.getLyric(title, artist);
		if (is != null) {
			byte[] temp;
			try {
				temp = new byte[1024];
				fos = new FileOutputStream(lrcPath);
				int readBytes = 0;
				while ((readBytes = is.read(temp)) != -1) { // 写入到lrc文件里
					fos.write(temp, 0, readBytes);
				}
				fos.flush();
				fos.close();
				is = new FileInputStream(lrcPath);
				LrcDecode ld = new LrcDecode().readLrc(is);
				lrcTable = ld.getLrcTable();
				is.close();
				fos = null;
				is = null;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// LrcDecode ld =new LrcDecode().readLrc(is);
			//
			// lrcTable = ld.getLrcTable();
		}
		return lrcTable;
	}
}
