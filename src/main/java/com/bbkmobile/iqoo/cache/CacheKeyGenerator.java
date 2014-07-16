package com.bbkmobile.iqoo.cache;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 按条件生成key
 * @author time
 *
 */
public class CacheKeyGenerator {

	public static String key(String cacheName,Object ...param){
		if(param != null && param.length >0){

			StringBuffer sb = new StringBuffer();
			sb.append(cacheName).append(".");
			
			for(int i=0; i<param.length; i++){
				if(param[i] != null )
				{
					String paramStr = replaceBlank( String.valueOf(param[i]));
					sb.append(paramStr).append(".");
				}
			}
			return sb.toString().endsWith(".") ? sb.substring(0,sb.length()-1) : sb.toString();
		}
		return cacheName;
	}
	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	public static String csTStr(String cs){
		return "0".equals(cs)?"cellphone":"pc";
	}
}
