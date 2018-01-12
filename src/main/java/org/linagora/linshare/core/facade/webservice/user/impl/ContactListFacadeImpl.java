/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016-2018 LINAGORA
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

package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListContactDto;
import org.linagora.linshare.core.facade.webservice.common.dto.ContactListDto;
import org.linagora.linshare.core.facade.webservice.user.ContactListFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.ContactListService;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class ContactListFacadeImpl extends GenericFacadeImpl implements ContactListFacade {

	private final ContactListService contactListService;

	private final AuditLogEntryService auditLogEntryService;

	public ContactListFacadeImpl(final AccountService accountService,
			final ContactListService contactListservice,
			final AuditLogEntryService auditLogEntryService) {
		super(accountService);
		this.contactListService = contactListservice;
		this.auditLogEntryService = auditLogEntryService;
	}

	@Override
	public Set<ContactListDto> findAll(String ownerUuid, Boolean mine) throws BusinessException {
		User actor = checkAuthentication();
		List<ContactList> lists;
		User owner = getOwner(actor, ownerUuid);
		lists = contactListService.findAll(actor, owner, mine);
		return ImmutableSet.copyOf(Lists.transform(lists, ContactListDto.toDto()));
	}

	@Override
	public Set<ContactListDto> findAllByMemberEmail(String ownerUuid, Boolean mine, String email)
			throws BusinessException {
		User actor = checkAuthentication();
		List<ContactList> lists;
		User owner = getOwner(actor, ownerUuid);
		lists = contactListService.findAllByMemberEmail(actor, owner, mine, email);
		return ImmutableSet.copyOf(Lists.transform(lists, ContactListDto.toDto()));
	}
	
	@Override
	public ContactListDto find(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "List uuid must be set.");

		User actor = checkAuthentication();
		ContactList list;
		User owner = getOwner(actor, ownerUuid);
		list = contactListService.find(actor, owner, uuid);
		return new ContactListDto(list);
	}

	@Override
	public ContactListDto create(String ownerUuid, ContactListDto dto) throws BusinessException {
		Validate.notNull(dto, "Mailing list must be set.");

		User actor = checkAuthentication();
		ContactList list = dto.toObject();
		User owner = getOwner(actor, ownerUuid);
		list = contactListService.create(actor, owner, list);
		return new ContactListDto(list);
	}

	@Override
	public ContactListDto duplicate(String ownerUuid, String contactsListUuidSource, String contactListName)
			throws BusinessException {
		Validate.notNull(contactsListUuidSource, "Mailing list uuid must be set.");
		Validate.notNull(contactListName, "Mailing list name must be set.");

		User actor = checkAuthentication();
		ContactList list = contactListService.findByUuid(actor.getLsUuid(), contactsListUuidSource);
		User owner = getOwner(actor, ownerUuid);
		list = contactListService.duplicate(actor, owner, list, contactListName);
		return new ContactListDto(list);
	}

	@Override
	public ContactListDto update(String ownerUuid, ContactListDto dto, String uuid) throws BusinessException {
		Validate.notNull(dto, "Mailing list must be set.");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "Mailing list uuid must be set.");
		User actor = checkAuthentication();
		ContactList list = dto.toObject();
		User owner = getOwner(actor, ownerUuid);
		list = contactListService.update(actor, owner, list);
		return new ContactListDto(list);
	}

	@Override
	public ContactListDto delete(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Mailing list uuid must be set.");

		User actor = checkAuthentication();
		ContactList list;
		User owner = getOwner(actor, ownerUuid);
		list = contactListService.delete(actor, owner, uuid);
		return new ContactListDto(list);
	}

	@Override
	public Set<ContactListContactDto> findAllContacts(String ownerUuid, String listUuid) throws BusinessException {
		Validate.notEmpty(listUuid, "Mailing list uuid must be set.");

		User actor = checkAuthentication();
		List<ContactListContact> list;
		User owner = getOwner(actor, ownerUuid);
		list = contactListService.findAllContacts(actor, owner, listUuid);
		return ImmutableSet.copyOf(Lists.transform(list, ContactListContactDto.toDto()));
	}

	@Override
	public ContactListContactDto addContact(String ownerUuid, String listUuid, ContactListContactDto dto) throws BusinessException {
		Validate.notNull(dto, "List contact to add must be set.");
		Validate.notEmpty(listUuid, "Mailing list uuid must be set.");

		User actor = checkAuthentication();
		ContactListContact contact = dto.toObject();
		User owner = getOwner(actor, ownerUuid);
		ContactListContact contact2 = contactListService.addContact(actor, owner, listUuid, contact);
		return new ContactListContactDto(contact2);
	}

	@Override
	public void updateContact(String ownerUuid, ContactListContactDto dto) throws BusinessException {
		Validate.notNull(dto, "List uuid must be set.");
		Validate.notEmpty(dto.getUuid(), "List uuid must be set.");

		User actor = checkAuthentication();
		ContactListContact contact = dto.toObject();
		User owner = getOwner(actor, ownerUuid);
		contactListService.updateContact(actor, owner, contact);
	}

	@Override
	public void deleteContact(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Contact uuid must be set.");

		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		contactListService.deleteContact(actor, owner, uuid);
	}

	@Override
	public Set<AuditLogEntryUser> audit(String ownerUuid, String uuid) {
		Account actor = checkAuthentication();
		User owner = (User) getOwner(actor, ownerUuid);
		contactListService.find(actor, owner, uuid);
		return auditLogEntryService.findAllContactLists(actor, owner, uuid);
	}
}
