/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.mongo.entities.mto;

import java.util.Date;

import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;

public class UploadRequestGroupMto {

	private String uuid;

	private String subject;

	private String body;

	private Date creationDate;

	private Date modificationDate;

	private Integer maxFileCount;

	private Long maxDepositSize;

	private Long maxFileSize;

	private Date activationDate;

	private Date notificationDate;

	private Date expiryDate;

	private Boolean canDelete;

	private Boolean canClose;

	private Boolean canEditExpiryDate;

	private String locale;

	private boolean secured;

	private String mailMessageId;

	private Boolean enableNotification;

	private Boolean restricted;

	private AccountMto owner;

	private DomainMto domain;

	private UploadRequestStatus status;

	private Boolean statusUpdated;

	public UploadRequestGroupMto() {
	}

	public UploadRequestGroupMto(UploadRequestGroup group) {
		this.uuid = group.getUuid();
		this.subject = group.getSubject();
		this.body = group.getBody();
		this.creationDate = group.getCreationDate();
		this.modificationDate = group.getModificationDate();
		this.maxFileCount = group.getMaxFileCount();
		this.maxDepositSize = group.getMaxDepositSize();
		this.maxFileSize = group.getMaxFileSize();
		this.activationDate = group.getActivationDate();
		this.notificationDate = group.getNotificationDate();
		this.expiryDate = group.getExpiryDate();
		this.canDelete = group.getCanDelete();
		this.canClose = group.getCanClose();
		this.canEditExpiryDate = group.getCanEditExpiryDate();
		this.locale = group.getLocale();
		this.secured = group.isSecured();
		this.mailMessageId = group.getMailMessageId();
		this.enableNotification = group.getEnableNotification();
		this.restricted = group.getRestricted();
		this.owner = new AccountMto(group.getOwner());
		this.domain = new DomainMto(group.getAbstractDomain());
		this.status = group.getStatus();
		this.statusUpdated = false;
	}

	public UploadRequestGroupMto(UploadRequestGroup group, Boolean statusUpdated) {
		this.uuid = group.getUuid();
		this.subject = group.getSubject();
		this.body = group.getBody();
		this.creationDate = group.getCreationDate();
		this.modificationDate = group.getModificationDate();
		this.maxFileCount = group.getMaxFileCount();
		this.maxDepositSize = group.getMaxDepositSize();
		this.maxFileSize = group.getMaxFileSize();
		this.activationDate = group.getActivationDate();
		this.notificationDate = group.getNotificationDate();
		this.expiryDate = group.getExpiryDate();
		this.canDelete = group.getCanDelete();
		this.canClose = group.getCanClose();
		this.canEditExpiryDate = group.getCanEditExpiryDate();
		this.locale = group.getLocale();
		this.secured = group.isSecured();
		this.mailMessageId = group.getMailMessageId();
		this.enableNotification = group.getEnableNotification();
		this.restricted = group.getRestricted();
		this.owner = new AccountMto(group.getOwner());
		this.domain = new DomainMto(group.getAbstractDomain());
		this.status = group.getStatus();
		this.statusUpdated = statusUpdated;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Integer getMaxFileCount() {
		return maxFileCount;
	}

	public void setMaxFileCount(Integer maxFileCount) {
		this.maxFileCount = maxFileCount;
	}

	public Long getMaxDepositSize() {
		return maxDepositSize;
	}

	public void setMaxDepositSize(Long maxDepositSize) {
		this.maxDepositSize = maxDepositSize;
	}

	public Long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Boolean getCanDelete() {
		return canDelete;
	}

	public void setCanDelete(Boolean canDelete) {
		this.canDelete = canDelete;
	}

	public Boolean getCanClose() {
		return canClose;
	}

	public void setCanClose(Boolean canClose) {
		this.canClose = canClose;
	}

	public Boolean getCanEditExpiryDate() {
		return canEditExpiryDate;
	}

	public void setCanEditExpiryDate(Boolean canEditExpiryDate) {
		this.canEditExpiryDate = canEditExpiryDate;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public boolean isSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	public String getMailMessageId() {
		return mailMessageId;
	}

	public void setMailMessageId(String mailMessageId) {
		this.mailMessageId = mailMessageId;
	}

	public Boolean getEnableNotification() {
		return enableNotification;
	}

	public void setEnableNotification(Boolean enableNotification) {
		this.enableNotification = enableNotification;
	}

	public Boolean getRestricted() {
		return restricted;
	}

	public void setRestricted(Boolean restricted) {
		this.restricted = restricted;
	}

	public AccountMto getOwner() {
		return owner;
	}

	public void setOwner(AccountMto owner) {
		this.owner = owner;
	}

	public DomainMto getDomain() {
		return domain;
	}

	public void setDomain(DomainMto domain) {
		this.domain = domain;
	}

	public UploadRequestStatus getStatus() {
		return status;
	}

	public void setStatus(UploadRequestStatus status) {
		this.status = status;
	}

	public Boolean getStatusUpdated() {
		return statusUpdated;
	}

	public void setStatusUpdated(Boolean statusUpdated) {
		this.statusUpdated = statusUpdated;
	}
}
