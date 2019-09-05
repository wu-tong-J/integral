package entity;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xh
 * @date 2019-05-05 10:21
 **/
@Entity
@Table
@DynamicInsert
public class Authentication implements Serializable {
	private String pkAuthId;
	private String userId;
	private String idCard;
	private String job;
	private String politicalStatus;
	private String address;
	private String education;
	private String emergencyPerson;
	private String emergencyPhone;
	private String residentialType;
	private String medicalHistory;
	private String disabilityCertificate;
	private String lowAssurance;
	private Integer toChangeRole;
	private String operUserId;
	private LocalDateTime operTime;
	private String remark;
	private Integer status;
	private String stationId;

	private Station station;

	@Id
	@Column(name = "pk_auth_id", nullable = false, length = 32)
	public String getPkAuthId() {
		return pkAuthId;
	}

	public void setPkAuthId(String pkAuthId) {
		this.pkAuthId = pkAuthId;
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
	@Column(name = "id_card", nullable = true, length = 255)
	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	@Basic
	@Column(name = "job", nullable = true, length = 50)
	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	@Basic
	@Column(name = "political_status", nullable = true, length = 50)
	public String getPoliticalStatus() {
		return politicalStatus;
	}

	public void setPoliticalStatus(String politicalStatus) {
		this.politicalStatus = politicalStatus;
	}

	@Basic
	@Column(name = "address", nullable = true, length = 255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Basic
	@Column(name = "education", nullable = true, length = 50)
	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	@Basic
	@Column(name = "emergency_person", nullable = true, length = 20)
	public String getEmergencyPerson() {
		return emergencyPerson;
	}

	public void setEmergencyPerson(String emergencyPerson) {
		this.emergencyPerson = emergencyPerson;
	}

	@Basic
	@Column(name = "emergency_phone", nullable = true, length = 11)
	public String getEmergencyPhone() {
		return emergencyPhone;
	}

	public void setEmergencyPhone(String emergencyPhone) {
		this.emergencyPhone = emergencyPhone;
	}

	@Basic
	@Column(name = "residential_type", nullable = true, length = 100)
	public String getResidentialType() {
		return residentialType;
	}

	public void setResidentialType(String residentialType) {
		this.residentialType = residentialType;
	}

	@Basic
	@Column(name = "medical_history", nullable = true, length = 500)
	public String getMedicalHistory() {
		return medicalHistory;
	}

	public void setMedicalHistory(String medicalHistory) {
		this.medicalHistory = medicalHistory;
	}

	@Basic
	@Column(name = "disability_certificate", nullable = true, length = 255)
	public String getDisabilityCertificate() {
		return disabilityCertificate;
	}

	public void setDisabilityCertificate(String disabilityCertificate) {
		this.disabilityCertificate = disabilityCertificate;
	}

	@Basic
	@Column(name = "low_assurance", nullable = true, length = 255)
	public String getLowAssurance() {
		return lowAssurance;
	}

	public void setLowAssurance(String lowAssurance) {
		this.lowAssurance = lowAssurance;
	}

	@Basic
	@Column(name = "to_change_role", nullable = true)
	public Integer getToChangeRole() {
		return toChangeRole;
	}

	public void setToChangeRole(Integer toChangeRole) {
		this.toChangeRole = toChangeRole;
	}

	@Basic
	@Column(name = "oper_user_id", nullable = true, length = 32)
	public String getOperUserId() {
		return operUserId;
	}

	public void setOperUserId(String operUserId) {
		this.operUserId = operUserId;
	}

	@Basic
	@Column(name = "oper_time", nullable = true)
	public LocalDateTime getOperTime() {
		return operTime;
	}

	public void setOperTime(LocalDateTime operTime) {
		this.operTime = operTime;
	}

	@Basic
	@Column(name = "remark", nullable = true, length = 255)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
	@Column(name = "station_id", nullable = true, length = 32)
	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	@Transient
	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}
}
