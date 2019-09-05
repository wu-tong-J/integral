package entity;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author
 * @date 2019-04-23 12:02
 **/
@Entity
@DynamicInsert
public class Goods implements Serializable {
	private String pkGoodsId;
	private Integer type;
	private String name;
	private BigDecimal priceIntegral;
	private String priceWork;
	private BigDecimal priceBonus;
	private BigDecimal percentage;
	private Integer status;
	private byte[] content;
	private Integer num;
	private String userId;
	private Integer getType;
	private Integer sellCount;
	private String pic;
	private String address;

	@Id
	@Column(name = "pk_goods_id", nullable = false, length = 32)
	public String getPkGoodsId() {
		return pkGoodsId;
	}

	public void setPkGoodsId(String pkGoodsId) {
		this.pkGoodsId = pkGoodsId;
	}

	@Basic
	@Column(name = "type", nullable = true)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Basic
	@Column(name = "name", nullable = true, length = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic
	@Column(name = "price_integral", nullable = true, precision = 2)
	public BigDecimal getPriceIntegral() {
		return priceIntegral;
	}

	public void setPriceIntegral(BigDecimal priceIntegral) {
		this.priceIntegral = priceIntegral;
	}

	@Basic
	@Column(name = "price_work", nullable = true, length = 100)
	public String getPriceWork() {
		return priceWork;
	}

	public void setPriceWork(String priceWork) {
		this.priceWork = priceWork;
	}

	@Basic
	@Column(name = "price_bonus", nullable = true, precision = 2)
	public BigDecimal getPriceBonus() {
		return priceBonus;
	}

	public void setPriceBonus(BigDecimal priceBonus) {
		this.priceBonus = priceBonus;
	}

	@Basic
	@Column(name = "percentage", nullable = true, precision = 2)
	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	@Basic
	@Column(name = "status", nullable = true)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
	@Column(name = "num", nullable = true)
	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
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
	@Column(name = "get_type", nullable = true)
	public Integer getGetType() {
		return getType;
	}

	public void setGetType(Integer getType) {
		this.getType = getType;
	}

	@Transient
	public Integer getSellCount() {
		return sellCount;
	}

	public void setSellCount(Integer sellCount) {
		this.sellCount = sellCount;
	}

	@Basic
	@Column(name = "pic", nullable = true, length = 200)
	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	@Basic
	@Column(name = "address", nullable = true, length = 255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}