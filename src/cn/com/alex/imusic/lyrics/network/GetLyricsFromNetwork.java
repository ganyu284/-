package cn.com.alex.imusic.lyrics.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class GetLyricsFromNetwork {
	//private HttpParams httpParams;
	// 发送的参数
	List<NameValuePair> params;
	private HttpClient httpClient;

	private String xmlUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=";
	private String lyricUrl = "http://box.zhangmen.baidu.com/bdlrc/";
	private static GetLyricsFromNetwork gli;

	private static GetLyricsFromNetwork getInstance() {
		if (gli == null) {
			gli = new GetLyricsFromNetwork();
		} else {
			gli.xmlUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=";
			gli.lyricUrl = "http://box.zhangmen.baidu.com/bdlrc/";
		}
		return gli;
	}

	public static InputStream getLyric(String title, String artist) {
		InputStream is = null;
		GetLyricsFromNetwork glid = getInstance();
		try {
			is = glid.getLyricStream(glid.getLyricId(glid.getLyricIdStream(
					title, artist)));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}

	public InputStream getLyricIdStream(String title, String artist) {
		if (httpClient == null)
			httpClient = getHttpClient();
		InputStream result = null;

		xmlUrl += URLEncoder.encode(title) + "$$" + URLEncoder.encode(artist)
				+ "$$$$";
		System.out.println(xmlUrl);
		try {

			/* 建立HTTPGet对象 */
			HttpGet httpRequest = new HttpGet(xmlUrl);

			/* 发送请求并等待响应 */
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 读响应数据 */
				result = httpResponse.getEntity().getContent();

			}
			httpRequest.abort();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int getLyricId(InputStream inputStream)
			throws XmlPullParserException, NumberFormatException, IOException {
		int lyricId = 0;
		XmlPullParser parser = Xml.newPullParser();
		if (inputStream != null) {
			parser.setInput(inputStream, "utf-8");

			int event = parser.getEventType();
			whileLoop: while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("lrcid".equals(parser.getName())) {
						lyricId = Integer.parseInt(parser.nextText());
						break whileLoop;
					}

					break;
				default:
					break;
				}
				event = parser.next();
			}
		}
		return lyricId;
	}

	private HttpClient getHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);

		return new DefaultHttpClient(conMgr, params);
	}

//	private HttpClient getHttpClient2() {
//
//		// 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）
//
//		this.httpParams = new BasicHttpParams();
//
//		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
//
//		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
//
//		HttpConnectionParams.setSoTimeout(httpParams, 60 * 1000);
//
//		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
//
//		// 设置重定向，缺省为 true
//		HttpClientParams.setRedirecting(httpParams, true);
//
//		// 设置 user agent
//		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
//		HttpProtocolParams.setUserAgent(httpParams, userAgent);
//
//		// 创建一个 HttpClient 实例
//		httpClient = new DefaultHttpClient(httpParams);
//
//		return httpClient;
//
//		// httpClient = new DefaultHttpClient();
//		// ClientConnectionManager mgr = httpClient.getConnectionManager();
//		// //HttpParams params = httpClient.getParams();
//		// httpClient = new DefaultHttpClient(new
//		// ThreadSafeClientConnManager(httpParams,
//		//
//		// mgr.getSchemeRegistry()), httpParams);
//		// return httpClient;
//
//	}

	public InputStream getLyricStream(int lyricId) {
		// 如果为0，表示没有找到歌词
		// System.out.println(lyricUrl);
		if (lyricId == 0) {
			return null;
		}
		InputStream is = null;
		if (httpClient == null)
			httpClient = getHttpClient();
		int path = (int) lyricId / 100;
		lyricUrl += path + "/" + lyricId + ".lrc";
		System.out.println(lyricUrl);
		try {

			/* 建立HTTPGet对象 */
			HttpGet httpRequest = new HttpGet(lyricUrl);

			/* 发送请求并等待响应 */
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 读响应数据 */
				// System.out.println(httpResponse.getEntity().toString());
				is = httpResponse.getEntity().getContent();
			}
			// httpRequest.abort();
			// httpClient.getConnectionManager().shutdown();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}
}
