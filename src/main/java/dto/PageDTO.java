package dto;

import java.util.List;

/**
 * layui带分页的json实体
 * @author xh
 * @date 2019-04-29 17:59
 **/
public class PageDTO {
	private Integer code = 0;
	private String msg;
	private String count;
	private List data;

	public PageDTO() {
	}

	public PageDTO(String msg, String count, List data) {
		this.msg = msg;
		this.count = count;
		this.data = data;
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

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}
}
