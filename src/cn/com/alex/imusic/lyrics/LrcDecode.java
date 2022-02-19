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

	private Hashtable<String, String> lrcTable = null; // ��Ž�������б�
	public static String charSet = "gbk"; // ����

	/**
	 * ����Lrc
	 * */
	public LrcDecode readLrc(InputStream is) {
		lrcTable = new Hashtable<String, String>();
		try {
			BufferedReader bis = new BufferedReader(new InputStreamReader(is,
					charSet), 1024 * 80);
			String str = null;
			while ((str = bis.readLine()) != null) { // ���н���
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
	 * ���н���
	 * */
	private LrcDecode decodeLine(String str) {
		//System.out.println(str);
		if (str.indexOf("[") < 0 || str.indexOf("]") < 0) // �������[ ]��ֱ������
			return this;
		if (str.length() < 3) // ������г���С��3����������ֱ������
			return this;

		int endIdx = str.lastIndexOf("]") >= 0 ? str.lastIndexOf("]") : str
				.length() - 1;
		if (str.startsWith("[ti")) {// ������
			lrcTable.put("ti", str.substring(4, endIdx));

		} else if (str.startsWith("[ar")) {// ������
			lrcTable.put("ar", str.substring(4, endIdx));

		} else if (str.startsWith("[al")) {// ר��
			lrcTable.put("al", str.substring(4, endIdx));

		} else if (str.startsWith("[by")) {// lrc������
			lrcTable.put("by", str.substring(4, endIdx));

		} else if (str.startsWith("[la")) {// ����
			lrcTable.put("la", str.substring(4, endIdx));
		} else if (str.startsWith("[offset")) {// ƫ��ʱ��
			lrcTable.put("offset", str.substring(8, endIdx));
		} else if (str.startsWith("[url")) {// ��ַ
			lrcTable.put("url", str.substring(5, endIdx));

		} else if (str.startsWith("[t_time")) {// ��ʱ��
			lrcTable.put("t_time", str.substring(8, endIdx));

		} else if (str.startsWith("[composer")) {// ����
			lrcTable.put("composer", str.substring(10, endIdx));

		} else if (str.startsWith("[lyricist")) {// ����
			lrcTable.put("lyricist", str.substring(10, endIdx));

		} else if (str.startsWith("[ve")) {// �汾
			lrcTable.put("version", str.substring(4, endIdx));

		} else if (str.startsWith("[re")) {// ��ʴ�������
			lrcTable.put("creator", str.substring(4, endIdx));

		} else if (str.startsWith("[au")) {// Author
			lrcTable.put("author", str.substring(4, endIdx));
		} else {
			// �������
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
					// ���ʱ���ʽm:ss
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
	 * ��ȡ�����ɹ��ĸ��
	 * */
	public Hashtable<String, String> getLrcTable() {
		return lrcTable;
	}

	/**
	 * ��֤ʱ���ʽһ�� Ϊm:ss
	 * 
	 * @param str
	 *            ʱ���ַ�
	 * @return �ж��õ�ʱ���
	 * */
	private String strToLongToTime(String str) {
		//�������ʱ���ʽ���ַ���������Null
		if(!isTime(str))
			return null;
		
		// System.out.println(str);
		int m = Integer.parseInt(str.substring(0, str.indexOf(":")));
		int s = 0;
		int ms = 0;

		// �жϸ��ʱ���Ƿ��к���
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
	 * �ж��ַ����Ƿ�Ϊ����
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
	 * �ж��Ƿ�ʱ���ʽ���ַ���
	 * @param str
	 * @return ����ʽΪ00:00.00����00:00���ָ�ʽ��Ϊtrue������false
	 */
	public boolean isTime(String str){		
		String patternStr = "\\d+:\\d+(\\.\\d+)?";
		Pattern ptn = Pattern.compile(patternStr);
		Matcher matcher = ptn.matcher(str);		
		return matcher.matches();
	}

	/**
	 * ����ʱ��
	 * 
	 * @param time
	 *            ����ʱ��
	 * */
	public static String timeMode(int time) {
		int tmp = (time / 1000) % 60;
		if (tmp < 10)
			return time / 60000 + ":" + "0" + tmp;
		else
			return time / 60000 + ":" + tmp;
	}

}