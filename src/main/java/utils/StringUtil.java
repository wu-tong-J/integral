package utils;

import java.util.List;

/**
 * @author
 * @description 用于处理字符串的工具类
 * @date 2017-11-16 12:21
 **/
public class StringUtil {

	/**
	 * @Description:判断是否为空字符串
	 **/
	public static boolean notNull(String s){
		if (null == s || "".equals(s)){
			return false;
		}else {
			return true;
		}
	}

	/**
	 * @Description:将以某种字符间隔的字符串转为可以直接用于MySQL的IN关键字的字符串
	 **/
	public static String toInString(String s,String separator){
		String[] arr = s.split(separator);
		String toinString = "";
		for (String arrS : arr) {
			toinString = toinString + ",'" + arrS + "'";
		}
		String inString = toinString.substring(1, toinString.length());
		return inString;
	}
	/**
	 * @Description:把List转为可以用于MySQL的IN字段的字符串
	 * 由于IN有999个的限制，在此采用了多个not in拼接的方式
	 * @param column 字段名
	 * @param inOrNotIn 1代表IN  2代表NOT IN
	 **/
	public static String getNotInString(List<String> list,String column,int inOrNotIn){
		if (list == null || list.size()<=0){
			return "";
		}else {
			if (inOrNotIn==1){
				String in = column +" in (";
				for (int i = 0; i < list.size(); i++) {
					if (in.endsWith("(")){
						in = in+"'"+list.get(i)+"'";
					}else {
						in = in+ ",'"+list.get(i)+"'";
					}
					if (i%999==0 && i!=0){
						if (i<list.size()-1){
							in = in+") and "+ column +" in (";
						}else if (i==list.size()-1){
							in = in+")";
						}
					}
					if (i==list.size()-1){
						in = in+")";
					}
				}
				return in;
			}else if (inOrNotIn==2){
				String notIn = column +" not in (";
				for (int i = 0; i < list.size(); i++) {
					if (notIn.endsWith("(")){
						notIn = notIn+"'"+list.get(i)+"'";
					}else {
						notIn = notIn+ ",'"+list.get(i)+"'";
					}
					if (i%999==0 && i!=0){
						if (i<list.size()-1){
							notIn = notIn+") and "+ column +" not in (";
						}else if (i==list.size()-1){
							notIn = notIn+")";
						}
					}
					if (i==list.size()-1){
						notIn = notIn+")";
					}
				}
				return notIn;
			}else {
				return "";
			}
		}


	}
}
