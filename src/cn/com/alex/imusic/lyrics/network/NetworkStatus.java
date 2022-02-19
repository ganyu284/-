package cn.com.alex.imusic.lyrics.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus {
	// ��������Ƿ��
	public static boolean isConnected(Context ctx) {
		ConnectivityManager connec = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connec.getActiveNetworkInfo();

		return (info != null && info.isAvailable());
	}

	// ��������
	public static int connectType(Context ctx) {
		ConnectivityManager connec = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connec.getActiveNetworkInfo() == null
				|| !connec.getActiveNetworkInfo().isAvailable()) {
			return -1;
		}
		if (connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
			return ConnectivityManager.TYPE_WIFI;
		if (connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
			return ConnectivityManager.TYPE_MOBILE;
		else if (connec.getNetworkInfo(ConnectivityManager.TYPE_WIMAX)
				.getState() == NetworkInfo.State.CONNECTED)
			return ConnectivityManager.TYPE_WIMAX;
		else
			return -1;
	}
}
