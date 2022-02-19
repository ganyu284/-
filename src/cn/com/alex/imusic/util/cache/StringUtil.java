/* 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package cn.com.alex.imusic.util.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.regex.Matcher;

import android.content.res.Resources;

public class StringUtil {

	
	private static HashMap<String,String> htmlEntities;
	  static {
	    htmlEntities = new HashMap<String,String>();
	    htmlEntities.put("&lt;","<")    ; htmlEntities.put("&gt;",">");
	    htmlEntities.put("&amp;","&")   ; htmlEntities.put("&quot;","\"");
//	    htmlEntities.put("&agrave;","); htmlEntities.put("&Agrave;",",");
//	    htmlEntities.put("&acirc;",") ; htmlEntities.put("&auml;",");
//	    htmlEntities.put("&Auml;",")  ; htmlEntities.put("&Acirc;",");
//	    htmlEntities.put("&aring;",") ; htmlEntities.put("&Aring;",");
//	    htmlEntities.put("&aelig;",") ; htmlEntities.put("&AElig;"," );
//	    htmlEntities.put("&ccedil;","); htmlEntities.put("&Ccedil;",");
//	    htmlEntities.put("&eacute;","); htmlEntities.put("&Eacute;"," );
//	    htmlEntities.put("&egrave;","); htmlEntities.put("&Egrave;",");
//	    htmlEntities.put("&ecirc;",") ; htmlEntities.put("&Ecirc;",");
//	    htmlEntities.put("&euml;",")  ; htmlEntities.put("&Euml;",");
//	    htmlEntities.put("&iuml;",")  ; htmlEntities.put("&Iuml;",");
//	    htmlEntities.put("&ocirc;",") ; htmlEntities.put("&Ocirc;",");
//	    htmlEntities.put("&ouml;",")  ; htmlEntities.put("&Ouml;",");
//	    htmlEntities.put("&oslash;",") ; htmlEntities.put("&Oslash;",");
//	    htmlEntities.put("&szlig;",") ; htmlEntities.put("&ugrave;",");
//	    htmlEntities.put("&Ugrave;","); htmlEntities.put("&ucirc;",");
//	    htmlEntities.put("&Ucirc;",") ; htmlEntities.put("&uuml;",");
//	    htmlEntities.put("&Uuml;",")  ; htmlEntities.put("&nbsp;"," ");
	    htmlEntities.put("&copy;","\u00a9");
	    htmlEntities.put("&reg;","\u00ae");
	    htmlEntities.put("&euro;","\u20a0");
	    htmlEntities.put("&apos;", "'");
	    htmlEntities.put("&mdash;", "-");
	  }
	  
	  public static String unescapeHTML(String input) {
		  StringBuilder builder = new StringBuilder();
		  int pos = 0;
		  int c = input.indexOf('&', pos);

		  while(c != -1) {
			  builder.append(input.substring(pos, c));
			  int posEndToken = input.indexOf(';', c);
			  if(posEndToken != -1) {
				  posEndToken++;
				  String token = input.substring(c, posEndToken);
				  String unescapeToken = htmlEntities.get(token);
				  if(unescapeToken != null) {
					  builder.append(unescapeToken);
				  } else {
					  builder.append(input.substring(pos, posEndToken));
				  }
				  pos = posEndToken;
			  } else {
				  pos = c;
				  builder.append(input.charAt(pos));
				  pos++;
			  }
			  c = input.indexOf('&', pos);
		  }
		  
		  builder.append(input.substring(pos));
		  
		  return builder.toString();
	  }
	  
	  public static String getSha1Hash(String input) throws Exception {
		  String retval = null;
		  ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes("UTF-8"));
		  MessageDigest hash = MessageDigest.getInstance("SHA1");
		  byte[] buffer = new byte[1024];
		  
		  int numRead=0;
		  while((numRead=inputStream.read(buffer)) != -1){
			  hash.update(buffer, 0, numRead);
		  }
		  
		  
		  retval = toHexString(hash.digest());
		  return retval;
		  
	  }
	  
	  protected static final byte[] Hexhars = {
		  '0', '1', '2', '3', '4', '5',
		  '6', '7', '8', '9', 'a', 'b',
		  'c', 'd', 'e', 'f' 
		  };
	  
	  public static String toHexString(byte[] input) {
		  StringBuilder buf = new StringBuilder(2 * input.length);
		  for(int i=0;i<input.length;i++) {
			  int v = input[i] & 0xff;
			  buf.append((char)Hexhars[v >> 4]);
			  buf.append((char)Hexhars[v & 0xf]);
		  }
		  return buf.toString();
	  }
	  
	  public static String loadRawResourceString(Resources res, int resourceId) throws IOException {
			StringBuilder builder = new StringBuilder();
			InputStream is = res.openRawResource(resourceId);
			InputStreamReader reader = new InputStreamReader(is);
			char[] buf = new char[1024];
	        int numRead=0;
	        while((numRead=reader.read(buf)) != -1){
	        	builder.append(buf, 0, numRead);
	        }
	        return builder.toString();
			
	  }
	  
	  public static String elipse(String input, int numMaxChar) {
		  if(input.length() >= numMaxChar - 3) {
			  String retval = input.substring(0, numMaxChar);
			  retval += "...";
			  return retval;
		  } else {
			  return input;
		  }
	  }
	  
	  public static String replaceAll(String input, String regex, String value) {
		  return input.replaceAll(regex, value == null ? "" : Matcher.quoteReplacement(value));
	  }
	
}
