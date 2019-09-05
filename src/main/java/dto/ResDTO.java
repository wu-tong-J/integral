package dto;

import java.io.Serializable;

/**
 * ajax响应体
 * @author xh
 * @date 2019-04-28 21:56
 **/
public class ResDTO implements Serializable {
	//状态码 0 正常
	private Integer code = 0;
	//显示的信息
	private String msg;

	public ResDTO(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public ResDTO(String msg) {
		this.msg = msg;
	}

	public ResDTO() {
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
