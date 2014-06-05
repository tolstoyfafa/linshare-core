/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.webservice.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.MailFooter;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "MailFooter")
@ApiModel(value = "MailFooter", description = "")
public class MailFooterDto {

	@ApiModelProperty(value = "Name")
	private String name;

	@ApiModelProperty(value = "Domain")
	private String domain;

	@ApiModelProperty(value = "Visible")
	private boolean visible;

	@ApiModelProperty(value = "Language")
	private Language language;

	@ApiModelProperty(value = "Footer")
	private String footer;

	@ApiModelProperty(value = "CreationDate")
	private Date creationDate;

	@ApiModelProperty(value = "ModificationDate")
	private Date modificationDate;

	@ApiModelProperty(value = "Uuid")
	private String uuid;

	@ApiModelProperty(value = "Plaintext")
	private boolean plaintext;

	public MailFooterDto() {
	}

	public MailFooterDto(MailFooter footer) {
		this.uuid = footer.getUuid();
		this.domain = footer.getDomain().getIdentifier();
		this.name = footer.getName();
		this.footer = footer.getFooter();
		this.language = Language.fromInt(footer.getLanguage());
		this.plaintext = footer.getPlaintext();
		this.visible = footer.getVisible();
		this.creationDate = new Date(footer.getCreationDate().getTime());
		this.modificationDate = new Date(footer.getModificationDate().getTime());
	}

	public void setName(String value) {
		this.name = value;
	}

	public String getName() {
		return name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setVisible(boolean value) {
		this.visible = value;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setLanguage(Language value) {
		this.language = value;
	}

	public Language getLanguage() {
		return language;
	}

	public void setFooter(String value) {
		this.footer = value;
	}

	public String getFooter() {
		return footer;
	}

	public void setCreationDate(Date value) {
		this.creationDate = value;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setModificationDate(Date value) {
		this.modificationDate = value;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setUuid(String value) {
		this.uuid = value;
	}

	public String getUuid() {
		return uuid;
	}

	public void setPlaintext(boolean value) {
		this.plaintext = value;
	}

	public boolean isPlaintext() {
		return plaintext;
	}
}
