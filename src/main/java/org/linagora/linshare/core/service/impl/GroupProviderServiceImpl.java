/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2018-2020 LINAGORA
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
package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.GroupProvider;
import org.linagora.linshare.core.domain.entities.LdapGroupProvider;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.GroupProviderRepository;
import org.linagora.linshare.core.repository.LdapGroupProviderRepository;
import org.linagora.linshare.core.service.GroupProviderService;

public class GroupProviderServiceImpl extends GenericAdminServiceImpl implements GroupProviderService {

	private GroupProviderRepository groupProviderRepository;

	private LdapGroupProviderRepository ldapGroupProviderRepository;

	public GroupProviderServiceImpl(GroupProviderRepository groupProviderRepository,
			LdapGroupProviderRepository ldapGroupProviderRepository,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(sanitizerInputHtmlBusinessService);
		this.groupProviderRepository = groupProviderRepository;
		this.ldapGroupProviderRepository = ldapGroupProviderRepository;
	}

	@Override
	public LdapGroupProvider find(String uuid) throws BusinessException {
		LdapGroupProvider provider = ldapGroupProviderRepository.findByUuid(uuid);
		if (provider == null) {
			throw new BusinessException(
					BusinessErrorCode.GROUP_LDAP_PROVIDER_NOT_FOUND,
					"Group provider identifier no found.");
		}
		return provider;
	}

	@Override
	public LdapGroupProvider create(LdapGroupProvider ldapGroupProvider) throws BusinessException {
			return ldapGroupProviderRepository.create(ldapGroupProvider);
	}

	@Override
	public LdapGroupProvider update(LdapGroupProvider groupProvider) throws BusinessException {
		return ldapGroupProviderRepository.update(groupProvider);
	}

	@Override
	public void delete(GroupProvider groupProvider) throws BusinessException {
		groupProviderRepository.delete(groupProvider);
	}

	@Override
	public boolean exists(String uuid) {
		LdapGroupProvider provider = ldapGroupProviderRepository.findByUuid(uuid);
		return provider != null;
	}
}
