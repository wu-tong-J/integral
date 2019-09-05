package entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.DynamicInsert;
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
@DynamicInsert
public class Activity implements Serializable {
	private String pkActId;
	private Integer status;
	private LocalDateTime publishTime;
	private String title;
	private String subTitle;
	private byte[] content;
	private String userId;
	private String username;
	private LocalDateTime sTime;
	private LocalDateTime eTime;
	private String serviceProvider;
	private BigDecimal bonus;
	private String consumableMaterial;
	private BigDecimal deductedFraction;
	private BigDecimal rewardPoints;
	private Integer num;
	private Integer remain;
	private String pic;

	private Integer signInDays;

	@Id
	@Column(name = "pk_act_id", nullable = false, length = 32)
	public String getPkActId() {
		return pkActId;
	}

	public void setPkActId(String pkActId) {
		this.pkActId = pkActId;
	}

	@Basic
	@Column(name = "status", nullable = true)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Basic
	@Column(name = "publish_time", nullable = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public LocalDateTime getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(LocalDateTime publishTime) {
		this.publishTime = publishTime;
	}

	@Basic
	@Column(name = "title", nullable = true, length = 200)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Basic
	@Column(name = "sub_title", nullable = true, length = 200)
	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "content", nullable = true)
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
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
	@Column(name = "username", nullable = true, length = 50)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Basic
	@Column(name = "s_time", nullable = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public LocalDateTime getsTime() {
		return sTime;
	}

	public void setsTime(LocalDateTime sTime) {
		this.sTime = sTime;
	}

	@Basic
	@Column(name = "e_time", nullable = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	public LocalDateTime geteTime() {
		return eTime;
	}

	public void seteTime(LocalDateTime eTime) {
		this.eTime = eTime;
	}

	@Basic
	@Column(name = "service_provider", nullable = true, length = 3200)
	public String getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	@Basic
	@Column(name = "bonus", nullable = true, precision = 2)
	public BigDecimal getBonus() {
		return bonus;
	}

	public void setBonus(BigDecimal bonus) {
		this.bonus = bonus;
	}

	@Basic
	@Column(name = "consumable_material", nullable = true, length = 255)
	public String getConsumableMaterial() {
		return consumableMaterial;
	}

	public void setConsumableMaterial(String consumableMaterial) {
		this.consumableMaterial = consumableMaterial;
	}

	@Basic
	@Column(name = "deducted_fraction", nullable = true, precision = 2)
	public BigDecimal getDeductedFraction() {
		return deductedFraction;
	}

	public void setDeductedFraction(BigDecimal deductedFraction) {
		this.deductedFraction = deductedFraction;
	}

	@Basic
	@Column(name = "reward_points", nullable = true, precision = 2)
	public BigDecimal getRewardPoints() {
		return rewardPoints;
	}

	public void setRewardPoints(BigDecimal rewardPoints) {
		this.rewardPoints = rewardPoints;
	}

	@Basic
	@Column(name = "num", nullable = true)
	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	@Transient
	public Integer getRemain() {
		return remain;
	}

	public void setRemain(Integer remain) {
		this.remain = remain;
	}

	@Basic
	@Column(name = "pic", nullable = true, length = 200)
	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	@Transient
	public Integer getSignInDays() {
		return signInDays;
	}

	public void setSignInDays(Integer signInDays) {
		this.signInDays = signInDays;
	}
}
