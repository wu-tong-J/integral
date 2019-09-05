package entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2019-04-23 12:02
 **/
@Entity
@Table(name = "activity_feedback", schema = "integral", catalog = "")
@DynamicInsert
public class ActivityFeedback implements Serializable {
	private String pkAfId;
	private String actId;
	private String userId;
	private String register;
	private String feedback;
	private BigDecimal star;
	private LocalDateTime feedTime;

	private User user;
	private Activity activity;

	@Id
	@Column(name = "pk_af_id", nullable = false, length = 32)
	public String getPkAfId() {
		return pkAfId;
	}

	public void setPkAfId(String pkAfId) {
		this.pkAfId = pkAfId;
	}

	@Basic
	@Column(name = "act_id", nullable = true, length = 32)
	public String getActId() {
		return actId;
	}

	public void setActId(String actId) {
		this.actId = actId;
	}

	@Basic
	@Column(name = "user_id", nullable = true, length = 32)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Basic
	@Column(name = "register", nullable = true, length = 32)
	public String getRegister() {
		return register;
	}

	public void setRegister(String register) {
		this.register = register;
	}

	@Basic
	@Column(name = "feedback", nullable = true, length = 1000)
	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	@Basic
	@Column(name = "star", nullable = true, precision = 1)
	public BigDecimal getStar() {
		return star;
	}

	public void setStar(BigDecimal star) {
		this.star = star;
	}

	@Basic
	@Column(name = "feed_time", nullable = true)
	//入参格式化
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	//json转换格式化
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	public LocalDateTime getFeedTime() {
		return feedTime;
	}

	public void setFeedTime(LocalDateTime feedTime) {
		this.feedTime = feedTime;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id",insertable = false,updatable = false)
	@NotFound(action= NotFoundAction.IGNORE)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "act_id",insertable = false,updatable = false)
	@NotFound(action= NotFoundAction.IGNORE)
	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
}
