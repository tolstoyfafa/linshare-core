/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
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

package org.linagora.linshare.core.repository.hibernate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailingListRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class MailingListRepositoryImpl extends AbstractRepositoryImpl<ContactList> implements MailingListRepository {

	public MailingListRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(ContactList entity) {
		return DetachedCriteria.forClass(getPersistentClass()).add(Restrictions.eq("uuid", entity.getUuid()));
	}

	@Override
	public ContactList findByUuid(String uuid) {
		List<ContactList> contactList = findByCriteria(Restrictions.eq("uuid", uuid));

		if (contactList == null || contactList.isEmpty()) {
			return null;
		} else if (contactList.size() == 1) {
			return contactList.get(0);
		} else {
			throw new IllegalStateException("Uuid must be unique");
		}
	}

	@Override
	public ContactList findByIdentifier(User owner, String identifier) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());

		det.add(Restrictions.and(Restrictions.eq("identifier", identifier), Restrictions.eq("owner", owner)));
		List<ContactList> contactList = findByCriteria(det);

		if (contactList == null || contactList.isEmpty()) {
			return null;
		} else if (contactList.size() == 1) {
			return contactList.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	public List<ContactList> findAllListWhereOwner(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("owner", user));
		return findByCriteria(det);
	}

	@Override
	public List<ContactList> searchMyListWithInput(User user, String input) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.and(Restrictions.eq("owner", user), Restrictions.like("identifier", "%" + input + "%")
				.ignoreCase()));
		det.addOrder(Property.forName("identifier").desc());
		return findByCriteria(det);
	}

	@Override
	public List<ContactList> searchListWithInput(User user, String input) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());

		if (user.hasSuperAdminRole()) {
			det.add(Restrictions.like("identifier", "%" + input + "%").ignoreCase());
		} else {
			// all public lists that belong to my domain.
			LogicalExpression allPublicLists = Restrictions.and(Restrictions.eq("isPublic", true),
					Restrictions.eq("domain", user.getDomain()));
			// we exclude my personal lists.
			LogicalExpression allMyDomainPublicLists = Restrictions.and(allPublicLists, Restrictions.ne("owner", user));
			// adding all private and public lists that belong to me, to the
			// public
			// lists.

			LogicalExpression allMyLists = Restrictions.or(Restrictions.eq("owner", user), allMyDomainPublicLists);
			det.add(Restrictions.and(allMyLists, Restrictions.like("identifier", "%" + input + "%").ignoreCase()));
		}
		det.addOrder(Property.forName("identifier").desc());

		return findByCriteria(det);
	}

	@Override
	public List<ContactList> findAll(User user) {
		// all public lists owned by others people than current user.
		Conjunction public_others = Restrictions.conjunction();
		public_others.add(Restrictions.eq("domain", user.getDomain()));
		public_others.add(Restrictions.eq("isPublic", true));
		public_others.add(Restrictions.ne("owner", user));

		// all private lists owned by the current user.
		Conjunction private_mine = Restrictions.conjunction();
		private_mine.add(Restrictions.eq("domain", user.getDomain()));
		private_mine.add(Restrictions.eq("owner", user));

		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.or(public_others, private_mine));
		return findByCriteria(det);
	}

	@Override
	public List<ContactList> findAllMine(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("domain", user.getDomain()));
		det.add(Restrictions.eq("owner", user));
		return findByCriteria(det);
	}

	@Override
	public List<ContactList> findAllOthers(User user) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("domain", user.getDomain()));
		det.add(Restrictions.eq("isPublic", true));
		det.add(Restrictions.ne("owner", user));
		return findByCriteria(det);
	}

	@Override
	public List<ContactList> findAllByMemberEmail(User user, String email) {
		Conjunction public_others = Restrictions.conjunction();
		public_others.add(Restrictions.eq("domain", user.getDomain()));
		public_others.add(Restrictions.eq("isPublic", true));
		public_others.add(Restrictions.ne("owner", user));
		Conjunction private_mine = Restrictions.conjunction();
		private_mine.add(Restrictions.eq("domain", user.getDomain()));
		private_mine.add(Restrictions.eq("owner", user));
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.or(public_others, private_mine));
		det.createAlias("mailingListContact", "mlc");
		det.add(Restrictions.eq("mlc.mail", email));
		return findByCriteria(det);
	}

	@Override
	public List<ContactList> findAllMineByMemberEmail(User user, String email) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("domain", user.getDomain()));
		det.add(Restrictions.eq("owner", user));
		det.createAlias("mailingListContact", "mlc");
		det.add(Restrictions.eq("mlc.mail", email));
		return findByCriteria(det);
	}

	@Override
	public List<ContactList> findAllOthersByMemberEmail(User user, String email) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("domain", user.getDomain()));
		det.add(Restrictions.eq("isPublic", true));
		det.add(Restrictions.ne("owner", user));
		det.createAlias("mailingListContact", "mlc");
		det.add(Restrictions.eq("mlc.mail", email));
		return findByCriteria(det);
	}
	
	@Override
	public List<ContactList> findAllMyList(User user) {
		if (user.hasSuperAdminRole()) {
			return findAll();
		}
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());

		// all public lists that belong to my domain.
		LogicalExpression allPublicLists = Restrictions.and(Restrictions.eq("isPublic", true),
				Restrictions.eq("domain", user.getDomain()));
		// we exclude my personal lists.
		LogicalExpression allMyDomainPublicLists = Restrictions.and(allPublicLists, Restrictions.ne("owner", user));
		// adding all private and public lists that belong to me, to the public
		// lists.
		det.add(Restrictions.or(Restrictions.eq("owner", user), allMyDomainPublicLists));

		return findByCriteria(det);
	}

	@Override
	public ContactList update(ContactList entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public ContactList create(ContactList entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public List<ContactList> searchWithInputByVisibility(User user, boolean isPublic, String input) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());

		if (isPublic == false) {
			if (user.hasSuperAdminRole()) {
				det.add(Restrictions.and(Restrictions.like("identifier", "%" + input + "%").ignoreCase(),
						Restrictions.eq("isPublic", false)));
			} else {
				LogicalExpression publicLists = Restrictions.and(Restrictions.eq("owner", user),
						Restrictions.eq("isPublic", false));
				det.add(Restrictions.and(Restrictions.like("identifier", "%" + input + "%").ignoreCase(), publicLists));
			}
		} else {
			if (user.hasSuperAdminRole()) {
				det.add(Restrictions.and(Restrictions.like("identifier", "%" + input + "%").ignoreCase(),
						Restrictions.eq("isPublic", true)));
			} else {
				LogicalExpression privateLists = Restrictions.and(Restrictions.eq("isPublic", true),
						Restrictions.eq("domain", user.getDomain()));
				det.add(Restrictions.and(Restrictions.like("identifier", "%" + input + "%").ignoreCase(), privateLists));
			}
		}
		det.addOrder(Property.forName("identifier").desc());
		return findByCriteria(det);
	}

	@Override
	public List<ContactList> searchListByVisibility(User user, boolean isPublic) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());

		if (isPublic == false) {
			if (user.hasSuperAdminRole()) {
				det.add(Restrictions.eq("isPublic", false));
			} else {
				det.add(Restrictions.and(Restrictions.eq("owner", user), Restrictions.eq("isPublic", false)));
			}
		} else {
			if (user.hasSuperAdminRole()) {
				det.add(Restrictions.eq("isPublic", true));
			} else {
				det.add(Restrictions.and(Restrictions.eq("isPublic", true), Restrictions.eq("domain", user.getDomain())));
			}
		}
		det.addOrder(Property.forName("identifier").desc());
		return findByCriteria(det);
	}
}
