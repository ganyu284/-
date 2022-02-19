package cn.com.alex.imusic.lyrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcDecode {

	private Hashtable<String, String> lrcTable = null; // 存放解析后的列表
	public static String charSet = "gbk"; // 编码

	/**
	 * 解析Lrc
	 * */
	public LrcDecode readLrc(InputStream is) {
		lrcTable = new Hashtable<String, String>();
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(is,
					charSet), 1024 * 80);
			String str = null;
			while ((str = bis.readLine()) != null) { // 逐行解析
				// System.out.println(str.trim());
				decodeLine(str.trim());
			}
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			lrcTable = null;
		}
		return this;
	}

	ArrayList<String> tmpList = new ArrayList<String>();

	/**
	 * 单行解析
	 * */
	private LrcDecode decodeLine(String str) {
		//System.out.println(str);
		if (str.indexOf("[") < 0 || str.indexOf("]") < 0) // 如果不带[ ]，直接跳过
			return this;
		if (str.length() < 3) // 如果该行长度小于3，不解析，直接跳过
			return this;

		int endIdx = str.lastIndexOf("]") >= 0 ? str.lastIndexOf("]") : str
				.length() - 1;
		if (str.startsWith("[ti")) {// 歌曲名
			lrcTable.put("ti", str.substring(4, endIdx));

		} else if (str.startsWith("[ar")) {// 艺术家
			lrcTable.put("ar", str.substring(4, endIdx));

		} else if (str.startsWith("[al")) {// 专辑
			lrcTable.put("al", str.substring(4, endIdx));

		} else if (str.startsWith("[by")) {// lrc制作者
			lrcTable.put("by", str.substring(4, endIdx));

		} else if (str.startsWith("[la")) {// 语言
			lrcTable.put("la", str.substring(4, endIdx));
		} else if (str.startsWith("[offset")) {// 偏移时间
			lrcTable.put("offset", str.substring(8, endIdx));
		} else if (str.startsWith("[url")) {// 网址
			lrcTable.put("url", str.substring(5, endIdx));

		} else if (str.startsWith("[t_time")) {// 总时长
			lrcTable.put("t_time", str.substring(8, endIdx));

		} else if (str.startsWith("[composer")) {// 作曲
			lrcTable.put("composer", str.substring(10, endIdx));

		} else if (str.startsWith("[lyricist")) {// 作词
			lrcTable.put("lyricist", str.substring(10, endIdx));

		} else if (str.startsWith("[ve")) {// 版本
			lrcTable.put("version", str.substring(4, endIdx));

		} else if (str.startsWith("[re")) {// 歌词创建工具
			lrcTable.put("creator", str.substring(4, endIdx));

		} else if (str.startsWith("[au")) {// Author
			lrcTable.put("author", str.substring(4, endIdx));
		} else {
			// 歌词正文
			int startIndex = -1;
			//
			while ((startIndex = str.indexOf("[", startIndex + 1)) != -1) {
				int endIndex = str.indexOf("]", startIndex + 1);
				if (str.substring(str.lastIndexOf("]") + 1, str.length())
				//if (str.substring(str.indexOf("]") + 1, str.length())
						.trim().length() == 0) {
					
					tmpList.add(strToLongToTime(str.substring(startIndex + 1,
							endIndex)));
					continue;
				} else {
					for (int i = 0; i < tmpList.size(); i++) {
						lrcTable.put(
								tmpList.get(i),
								 str.substring(str.lastIndexOf("]") + 1,
								//str.substring(str.indexOf("]") + 1,
										str.length()));
						// System.out.println(str);
					}
					tmpList.clear();
					// 添加时间格式m:ss
					lrcTable.put(
							strToLongToTime(str.substring(startIndex + 1,
									endIndex)) + "",
							str.substring(str.lastIndexOf("]") + 1,
									str.length()));
				}
			}
		}
		return this;
	}

	/**
	 * 获取解析成功的歌词
	 * */
	public Hashtable<String, String> getLrcTable() {
		return lrcTable;
	}

	/**
	 * 保证时间格式一致 为m:ss
	 * 
	 * @param str
	 *            时间字符
	 * @return 判断用的时间符
	 * */
	private String strToLongToTime(String str) {
		//如果不是时间格式的字符串，返回Null
		if(!isTime(str))
			return null;
		
		// System.out.println(str);
		int m = Integer.parseInt(str.substring(0, str.indexOf(":")));
		int s = 0;
		int ms = 0;

		// 判断歌词时间是否有毫秒
		if (str.indexOf(".") != -1) {
			s = Integer.parseInt(str.substring(str.indexOf(":") + 1,
					str.indexOf(".")));
			ms = Integer.parseInt(str.substring(str.indexOf(".") + 1,
					str.length()));
		} else {
			s = Integer.parseInt(str.substring(str.indexOf(":") + 1,
					str.length()));
		}
		return timeMode(m * 60000 + s * 1000 + ms * 10);
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public boolean isNumber(String str) {
		boolean b = true;
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				b = false;
				break;
			}
		}
		return b;
	}
	/**
	 * 判断是否时间格式的字符串
	 * @param str
	 * @return 当格式为00:00.00或者00:00这种格式，为true，否则，false
	 */
	public boolean isTime(String str){		
		String patternStr = "\\d+:\\d+(\\.\\d+)?";
		Pattern ptn = Pattern.compile(patternStr);
		Matcher matcher = ptn.matcher(str);		
		return matcher.matches();
	}

	/**
	 * 返回时间
	 * 
	 * @param time
	 *            毫秒时间
	 * */
	public static String timeMode(int time) {
		int tmp = (time / 1000) % 60;
		if (tmp < 10)
			return time / 60000 + ":" + "0" + tmp;
		else
			return time / 60000 + ":" + tmp;
	}

}