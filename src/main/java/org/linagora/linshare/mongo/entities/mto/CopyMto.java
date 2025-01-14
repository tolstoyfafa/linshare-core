/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.mongo.entities.mto;

import org.linagora.linshare.core.domain.constants.TargetKind;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.mongo.entities.WorkGroupNode;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CopyMto {

	@Schema(description = "Uuid")
	protected String uuid;

	@Schema(description = "Name")
	protected String name;

	@Schema(description = "Kind")
	protected TargetKind kind;

	@Schema(description = "contextUuid | if TargetKind = UPLOAD_REQUEST it's upload request uuid , if TargetKind = SHARED_SPACE it's shared space uuid ")
	protected String contextUuid;

	@Schema(description = "contextName")
	protected String contextName;

	@Schema(description = "nodeType")
	protected WorkGroupNodeType nodeType;

	public CopyMto() {
		super();
	}

	public CopyMto(String uuid, String name, TargetKind kind) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.kind = kind;
	}

	public CopyMto(DocumentEntry de) {
		super();
		this.kind = TargetKind.PERSONAL_SPACE;
		this.uuid = de.getUuid();
		this.name = de.getName();
	}

	public CopyMto(ShareEntry de) {
		super();
		this.kind = TargetKind.RECEIVED_SHARE;
		this.uuid = de.getUuid();
		this.name = de.getName();
	}

	public CopyMto(WorkGroupNode node, WorkGroup workGroup) {
		super();
		this.kind = TargetKind.SHARED_SPACE;
		this.uuid = node.getUuid();
		this.name = node.getName();
		this.contextUuid = workGroup.getLsUuid();
		this.nodeType = node.getNodeType();
	}

	public CopyMto(UploadRequestEntry entry) {
		this.kind = TargetKind.UPLOAD_REQUEST;
		this.uuid = entry.getUuid();
		this.name = entry.getName();
		this.contextUuid = entry.getUploadRequestUrl().getUploadRequest().getUuid();
		this.contextName = entry.getUploadRequestUrl().getUploadRequest().getUploadRequestGroup().getSubject();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TargetKind getKind() {
		return kind;
	}

	public void setKind(TargetKind kind) {
		this.kind = kind;
	}

	public String getContextUuid() {
		return contextUuid;
	}

	public void setContextUuid(String contextUuid) {
		this.contextUuid = contextUuid;
	}

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public WorkGroupNodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(WorkGroupNodeType nodeType) {
		this.nodeType = nodeType;
	}

}
