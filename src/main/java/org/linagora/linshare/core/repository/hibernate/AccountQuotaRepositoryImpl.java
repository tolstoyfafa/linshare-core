/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.repository.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.repository.AccountQuotaRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate4.HibernateTemplate;

public class AccountQuotaRepositoryImpl extends GenericQuotaRepositoryImpl<AccountQuota>
		implements AccountQuotaRepository {

	public AccountQuotaRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public AccountQuota find(Account account) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("account", account));
		AccountQuota quota = DataAccessUtils.singleResult(findByCriteria(criteria));
		if (quota != null) {
			this.getHibernateTemplate().refresh(quota);
		}
		return quota;
	}

	@Override
	public List<String> findDomainUuidByBatchModificationDate(Date startDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.ge("batchModificationDate", startDate));
		// why do we check if the batchModificationDate is not in the future ?
		criteria.add(Restrictions.le("batchModificationDate", new Date()));
		criteria.createAlias("domain", "do");
		criteria.setProjection(Projections.distinct(Projections.property("do.uuid")));
		@SuppressWarnings("unchecked")
		List<String> listIdentifier = (List<String>) getHibernateTemplate().findByCriteria(criteria);
		return listIdentifier;
	}

	/**
	 * currentValue sum of all account quota in a container
	 */
	@Override
	public Long sumOfCurrentValue(ContainerQuota containerQuota) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("containerQuota", containerQuota));
		criteria.setProjection(Projections.sum("currentValue"));
		List<AccountQuota> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null) {
			return DataAccessUtils.longResult(list);
		}
		return 0L;
	}
}
