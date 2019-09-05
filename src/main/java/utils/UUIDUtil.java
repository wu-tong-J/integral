package utils;

import java.util.UUID;

/**
 * @author
 * @description 用于主键的生成
 * @date 2017-10-30 16:38
 **/
public class UUIDUtil {
	public static String getUUID() {
		String s = UUID.randomUUID().toString();

		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23) + s.substring(24);
	}
}
