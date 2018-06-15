package com.ss.pojo;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class User {

	private Integer id;
	private String username;
	private String password;
	private Long createAt;
	private Long updateAt;

	private String phone;
	private String email;
	private String portrait;


	// 以下字段数据库中没有，仅仅用于存储些临时验证数据。
	private String IP;
	private String temcode;
	private String loginTime;

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", createAt=" + createAt +
				", updateAt=" + updateAt +
				", phone='" + phone + '\'' +
				", email='" + email + '\'' +
				", portrait='" + portrait + '\'' +
				", IP='" + IP + '\'' +
				", temcode='" + temcode + '\'' +
				", loginTime='" + loginTime + '\'' +
				'}';
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public String getTemcode() {
		return temcode;
	}

	public void setTemcode(String temcode) {
		this.temcode = temcode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String IP) {
		this.IP = IP;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Long createAt) {
		this.createAt = createAt;
	}

	public Long getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Long updateAt) {
		this.updateAt = updateAt;
	}
}
