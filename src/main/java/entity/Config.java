package entity;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author xh
 * @date 2019-05-05 16:02
 **/
@Entity
@Table
@DynamicInsert
public class Config implements Serializable {
	private String pkCfgId;
	private String name;
	private String term;
	private String content;
	private Integer status;

	@Id
	@Column(name = "pk_cfg_id", nullable = false, length = 32)
	public String getPkCfgId() {
		return pkCfgId;
	}

	public void setPkCfgId(String pkCfgId) {
		this.pkCfgId = pkCfgId;
	}

	@Basic
	@Column(name = "name", nullable = true, length = 50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "term", nullable = true, length = 50)
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	@Basic
	@Column(name = "content", nullable = true, length = 50)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Basic
	@Column(name = "status", nullable = true)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
