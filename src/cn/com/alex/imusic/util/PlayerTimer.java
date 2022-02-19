package cn.com.alex.imusic.util;

public class PlayerTimer {
	public static String format(long ms) {
		int ss = 1000;
		int mi = ss * 60;
		int hr = ss * 60 * 60;

		long hour = ms / hr;
		long minute = (ms - hour * hr) / mi;
		long second = (ms - hour * hr - minute * mi) / ss;

		String strHour = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = second < 10 ? "0" + second : "" + second;
		
		return hour == 0 ? strMinute + ":" + strSecond : strHour + ":"
				+ strMinute + ":" + strSecond;
	}

	public static void displayTimer(long max_value_in_ms) {

	}
}
