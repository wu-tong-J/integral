package dto;

import java.io.Serializable;

/**
 * @author xh
 * @date 2019-06-12 21:11
 **/
public class SelfAuthDTO implements Serializable {
	//角色
	private Integer role;
	//角色名称
	private String roleName;
	//认证状态 0 未申请/未通过(二者一个性质，都是可以在页面显示该选项) 1 审核中 2 已通过
	private Integer status;
	private String statusName;

	public SelfAuthDTO() {
	}

	public SelfAuthDTO(Integer role, String roleName, Integer status, String statusName) {
		this.role = role;
		this.roleName = roleName;
		this.status = status;
		this.statusName = statusName;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
}
