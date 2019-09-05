package dto;

import java.io.Serializable;

/**
 * @author xh
 * @description 富文本上传图片结果类
 * @date 2018-04-06 23:41
 **/
public class EditorResultDTO implements Serializable{
	/** 错误码. */
	private Integer errno;
	/** 具体的内容. */
	private String [] data;

	public EditorResultDTO() {
	}

	public Integer getErrno() {
		return errno;
	}

	public void setErrno(Integer errno) {
		this.errno = errno;
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}
}
