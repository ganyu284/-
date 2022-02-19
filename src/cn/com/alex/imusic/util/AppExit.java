package cn.com.alex.imusic.util;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Service;

public class AppExit {
	public static ArrayList<Activity> allActivity = new ArrayList<Activity>();
	public static Service service;

	// 退出所有应用
	public static void exitApp(Activity context) {
		for (int i = 0; i < allActivity.size(); i++) {
			((Activity) allActivity.get(i)).finish();
		}
		allActivity.clear();
	}
}
