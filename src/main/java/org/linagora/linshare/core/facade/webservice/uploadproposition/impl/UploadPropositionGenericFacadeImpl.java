/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
package org.linagora.linshare.core.facade.webservice.uploadproposition.impl;

import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.uploadproposition.UploadPropositionGenericFacade;
import org.linagora.linshare.core.facade.webservice.user.impl.GenericFacadeImpl;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;

public class UploadPropositionGenericFacadeImpl extends GenericFacadeImpl implements UploadPropositionGenericFacade {

	private final FunctionalityReadOnlyService functionalityService;

	public UploadPropositionGenericFacadeImpl(AccountService accountService,
			FunctionalityReadOnlyService functionalityService) {
		super(accountService);
		this.functionalityService = functionalityService;
	}

	@Override
	protected User checkAuthentication() throws BusinessException {
		User authUser = super.checkAuthentication();
		if (!authUser.hasUploadPropositionRole()) {
			logger.error("Current authUser is trying to access to a forbbiden api : " + authUser.getAccountRepresentation());
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"You are not authorized to use this service");
		}
		Functionality func = functionalityService.getUploadPropositionFunctionality(authUser.getDomain());
		if (!func.getActivationPolicy().getStatus()) {
			throw new BusinessException(
					BusinessErrorCode.WEBSERVICE_FORBIDDEN,
					"This service UploadProposition is not enable.");
		}
		return authUser;
	}

	@Override
	public UserDto isAuthorized() throws BusinessException {
		return UserDto.getFull(checkAuthentication());
	}
}
