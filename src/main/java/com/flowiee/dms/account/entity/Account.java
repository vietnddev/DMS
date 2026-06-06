package com.flowiee.dms.account.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseEntity;
import javax.persistence.*;

import com.flowiee.dms.storage.entity.DocShare;
import com.flowiee.dms.storage.entity.FileStorage;
import com.flowiee.dms.notification.entity.Notification;
import com.flowiee.dms.audit.entity.SystemLog;
import com.flowiee.dms.common.utils.constants.AccountStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Builder
@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account extends BaseEntity implements Serializable {
	@Column(name = "username", nullable = false, unique = true)
	String username;

	@JsonIgnore
	@Column(name = "password", nullable = false, length = 100)
	String password;

	@Column(name = "fullname", nullable = false)
	String fullName;

	@Column(name = "sex", nullable = false)
	boolean sex;

	@Column(name = "phone_number", length = 15, unique = true)
	String phoneNumber;

	@Column(name = "email", length = 50, unique = true)
	String email;

	@Column(name = "address", length = 500)
	String diaChi;

	@Column(name = "avatar")
	String avatar;

	@Column(name = "remark", length = 500)
	String remark;

	@Column(name = "role", length = 20)
	String role;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_account")
	GroupAccount groupAccount;

	@Column(name = "reset_tokens", unique = true)
	String resetTokens;

	@Column(name = "password_expire_date")
	LocalDate passwordExpireDate;

	@Column(name = "fail_logon_count")
	Integer failLogonCount;

	@Column(name = "status")
	String status;

	@JsonIgnore
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	List<FileStorage> listFileStorage;

	@JsonIgnore
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	List<DocShare> listDocShare;

	@JsonIgnore
	@OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
	List<Notification> listNotify;

	@JsonIgnore
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	List<SystemLog> listLog;

	@Transient
	String ip;

	public Account(Long id) {
		super.id = id;
	}

	public Account(Long id, String username, String fullName) {
		super.id = id;
		this.username = username;
		this.fullName = fullName;
	}

	public FileStorage getAvatar() {
		if (getListFileStorage() != null) {
			for (FileStorage avatar : getListFileStorage()) {
				if (avatar.isActive())
					return avatar;
			}
		}
		return null;
	}

	public boolean isPasswordExpired() {
		return passwordExpireDate != null && passwordExpireDate.isBefore(LocalDate.now());
	}

	public boolean isNormal() {
		Assert.notNull(status, "Status not null!");
		return Objects.equals(AccountStatus.N.name(), status);
	}

	public boolean isLocked() {
		Assert.notNull(status, "Status not null!");
		return Objects.equals(AccountStatus.L.name(), status);
	}

	public boolean isClosed() {
		Assert.notNull(status, "Status not null!");
		return Objects.equals(AccountStatus.C.name(), status);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Account [");
		builder.append("username=").append(username);
		builder.append(", fullName=").append(fullName);
		builder.append(", sex=").append(sex);
		builder.append(", phoneNumber=").append(phoneNumber);
		builder.append(", email=").append(email);
		builder.append(", diaChi=").append(diaChi);
		builder.append(", avatar=").append(avatar);
		builder.append(", remark=").append(remark);
		builder.append(", role=").append(role);
		builder.append(", status=").append(status);
		builder.append(", ip=").append(ip);
		builder.append("]");
		return builder.toString();
	}
}