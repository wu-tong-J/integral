package utils;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * @author
 * @description
 * @date 2018-05-24 20:28
 **/
public class BlobUtil {
	public static String convertBlobToString(Blob blob){
		String result = "";
		try {
			ByteArrayInputStream msgContent =(ByteArrayInputStream) blob.getBinaryStream();
			byte[] byte_data = new byte[msgContent.available()];
			msgContent.read(byte_data, 0,byte_data.length);
			result = new String(byte_data);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
