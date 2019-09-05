package utils;

import java.security.MessageDigest;

/**
 * @author
 * @description
 * @date 2017-11-14 14:24
 **/
public class MD5Util {
	public static String parseStringToMD5(String txt){
		StringBuffer sb = new StringBuffer();
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bs = md.digest(txt.getBytes());  // 16
			for (byte b : bs) {
				int i  = (int)b;
				int tmp = i & 0xff;
				if(tmp < 16){
					sb.append(0);
				}
				String string = Integer.toHexString(tmp);
				sb.append(string);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String parseStringToSHA(String txt){
		StringBuffer sb = new StringBuffer();
		try{
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] bs = md.digest(txt.getBytes());  // 16
			for (byte b : bs) {
				int i  = (int)b;
				int tmp = i & 0xff;
				if(tmp < 16){
					sb.append(0);
				}
				String string = Integer.toHexString(tmp);
				sb.append(string);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
}
