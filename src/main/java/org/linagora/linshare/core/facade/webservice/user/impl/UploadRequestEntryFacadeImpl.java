/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2021 LINAGORA
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

package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestEntryFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

import com.google.common.io.ByteSource;

public class UploadRequestEntryFacadeImpl extends GenericFacadeImpl implements UploadRequestEntryFacade {

	private final UploadRequestEntryService uploadRequestEntryService;
	private final AuditLogEntryService auditLogEntryService;

	public UploadRequestEntryFacadeImpl(final AccountService accountService,
			final UploadRequestEntryService uploadRequestEntryService,
			final AuditLogEntryService auditLogEntryService) {
		super(accountService);
		this.auditLogEntryService = auditLogEntryService;
		this.uploadRequestEntryService = uploadRequestEntryService;
	}

	@Override
	public UploadRequestEntryDto find(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Upload request entry uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequestEntry uploadRequestEntry =  uploadRequestEntryService.find(authUser, actor, uuid);
		return new UploadRequestEntryDto(uploadRequestEntry);
	}

	@Override
	public ByteSource download(String actorUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required document uuid");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		return uploadRequestEntryService.download(authUser, actor, uuid);
	}

	@Override
	public UploadRequestEntryDto delete(String actorUuid, String uuid) {
		Validate.notEmpty(uuid, "Upload request entry uuid must be set.");
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		UploadRequestEntry uploadRequestEntry =  uploadRequestEntryService.delete(authUser, actor, uuid);
		return new UploadRequestEntryDto(uploadRequestEntry);
	}

	@Override
	public Set<AuditLogEntryUser> findAllAudits(String actorUuid, String uploadRequestEntryUuid,
			List<LogAction> actions) {
		Account authUser = checkAuthentication();
		Validate.notEmpty(uploadRequestEntryUuid, "Upload request entry uuid must be set");
		Account actor = getActor(authUser, actorUuid);
		return auditLogEntryService.findAllUploadRequestEntryAudits(authUser, actor, uploadRequestEntryUuid, actions);
	}
}
