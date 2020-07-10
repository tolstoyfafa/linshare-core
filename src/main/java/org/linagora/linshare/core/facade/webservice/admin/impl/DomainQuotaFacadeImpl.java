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
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainQuotaFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainQuotaDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ContainerQuotaService;
import org.linagora.linshare.core.service.DomainQuotaService;
import org.linagora.linshare.core.service.QuotaService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class DomainQuotaFacadeImpl extends AdminGenericFacadeImpl implements DomainQuotaFacade {

	private final DomainQuotaService service;
	private final QuotaService quotaService;
	private final ContainerQuotaService containerQuotaService;
	private final AbstractDomainService abstractDomainService;

	public DomainQuotaFacadeImpl(
			final AccountService accountService,
			final DomainQuotaService domainQuotaService,
			final QuotaService quotaService,
			final ContainerQuotaService containerQuotaService,
			final AbstractDomainService abstractDomainService) {
		super(accountService);
		this.service = domainQuotaService;
		this.quotaService = quotaService;
		this.containerQuotaService = containerQuotaService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public DomainQuotaDto find(String uuid, boolean realTime) throws BusinessException {
		Validate.notNull(uuid, "Domain quota uuid must be set.");
		User authUser = checkAuthentication(Role.ADMIN);
		DomainQuota quota = service.find(authUser, uuid);
		DomainQuotaDto dto = new DomainQuotaDto(quota);
		List<ContainerQuota> containers = containerQuotaService.findAll(authUser, quota.getDomain());
		for (ContainerQuota containerQuota : containers) {
			dto.addContainerUuids(containerQuota.getUuid());
		}
		if (realTime) {
			Long usedSpace = quotaService.getRealTimeUsedSpace(authUser, authUser, quota);
			dto.setUsedSpace(usedSpace);
			if (quota.getDomain().isRootDomain()) {
				long currentValueForSubdomains = quotaService.getTodayUsedSpace(authUser, authUser);
				dto.setCurrentValueForSubdomains(currentValueForSubdomains);
			} else if (quota.getDomain().isTopDomain()) {
				long currentValueForSubdomains = 0;
				Set<AbstractDomain> subdomain = quota.getDomain().getSubdomain();
				for (AbstractDomain domain : subdomain) {
					DomainQuota domainQuota = quotaService.find(domain);
					currentValueForSubdomains += quotaService.getRealTimeUsedSpace(authUser, authUser, domainQuota);
				}
				dto.setCurrentValueForSubdomains(currentValueForSubdomains);
			}
		}
		return dto;
	}

	@Override
	public List<DomainQuotaDto> findAll(String parentUuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		List<DomainQuota> findAll = null;
		if (parentUuid != null) {
			AbstractDomain domain = abstractDomainService.findById(parentUuid);
			findAll = service.findAll(authUser, domain);
		} else {
			findAll = service.findAll(authUser);
		}
		return ImmutableList.copyOf(Lists.transform(findAll, DomainQuotaDto.toDto()));
	}

	@Override
	public DomainQuotaDto update(DomainQuotaDto dto, String uuid) throws BusinessException {
		Validate.notNull(dto, "DomainQuotaDto must be set.");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "Quota uuid must be set.");
//		Validate.notNull(entity.getQuotaWarning(), "QuotaWarning in DomainQuotaDto must be set.");
		User authUser = checkAuthentication(Role.ADMIN);
		DomainQuota domainQuota = dto.toObject();
		domainQuota = service.update(authUser, domainQuota);
		return new DomainQuotaDto(domainQuota);
	}

}
