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
package org.linagora.linshare.core.business.service;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;

public interface SharedSpaceMemberBusinessService {

	SharedSpaceMember find(String uuid) throws BusinessException;

	List<SharedSpaceMember> findAll() throws BusinessException;

	SharedSpaceMember create(SharedSpaceMember sharedSpacemember) throws BusinessException;

	SharedSpaceMember findByAccountAndNode(String accountUuid, String nodeUuid) throws BusinessException;

	void delete(SharedSpaceMember memberToDelete) throws BusinessException;

	SharedSpaceMember update(SharedSpaceMember foundMemberToUpdate, SharedSpaceMember member)
			throws BusinessException;

	List<SharedSpaceMember> findBySharedSpaceNodeUuid(String shareSpaceNodeUuid) throws BusinessException;

	void deleteAll(List<SharedSpaceMember> foundMembersToDelete) throws BusinessException;

	List<String> findMembersUuidBySharedSpaceNodeUuid(String shareSpaceNodeUuid) throws BusinessException;

	List<SharedSpaceMember> findByMemberName(String name) throws BusinessException;

	List<SharedSpaceNodeNested> findAllNestedNodeByAccountUuid(String accountUuid, boolean withRole);

	void updateNestedNode(SharedSpaceNode node) throws BusinessException;

	List<SharedSpaceMember> findAllUserMemberships(String userUuid);

	List<SharedSpaceMember> findAllByAccountAndRole(String accountUuid, String roleUuid);

	SharedSpaceMember findByNodeAndUuid(String nodeUuid, String uuid);

	List<SharedSpaceNodeNested> findAllByParentAndAccount(String accountUuid, String parentUuid);

	List<SharedSpaceMember> findAllMembersByParentAndAccount(String accountUuid, String parentUuid);

	List<SharedSpaceMember> findAllMembersWithNoConflictedRoles(String accountUuid, String parentUuid, String roleUuid);

	List<SharedSpaceNodeNested> findAllNodesByParent(String parentUuid);

	List<SharedSpaceMember> findAllMembersByParent(String parentUuid);
}
