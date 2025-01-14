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

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.PatchDto;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceNodeFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.SharedSpaceMemberService;
import org.linagora.linshare.core.service.SharedSpaceNodeService;
import org.linagora.linshare.mongo.entities.SharedSpaceMember;
import org.linagora.linshare.mongo.entities.SharedSpaceNode;
import org.linagora.linshare.mongo.entities.SharedSpaceNodeNested;

import com.google.common.base.Strings;

public class SharedSpaceNodeFacadeImpl extends GenericFacadeImpl implements SharedSpaceNodeFacade {

	private final SharedSpaceNodeService nodeService;
	
	private final SharedSpaceMemberService memberService;

	public SharedSpaceNodeFacadeImpl(AccountService accountService,
			SharedSpaceNodeService nodeService,
			SharedSpaceMemberService memberService) {
		super(accountService);
		this.nodeService = nodeService;
		this.memberService = memberService;
	}

	@Override
	public SharedSpaceNode find(String actorUuid, String uuid, boolean withRole) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required shared space node uuid.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (withRole) {
			return nodeService.findWithRole(authUser, actor, uuid);
		}
		return nodeService.find(authUser, actor, uuid);
	}

	@Override
	public SharedSpaceNode create(String actorUuid, SharedSpaceNode node) throws BusinessException {
		Validate.notNull(node, "Missing required input shared space node.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		SharedSpaceNode toCreate = new SharedSpaceNode(node.getName(), node.getParentUuid(), node.getNodeType(),
				node.getVersioningParameters());
		return nodeService.create(authUser, actor, toCreate);
	}

	@Override
	public SharedSpaceNode delete(String actorUuid, SharedSpaceNode node, String uuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(uuid)) {
			node = nodeService.find(authUser, actor, uuid);
		} else {
			Validate.notNull(node, "node must be set");
			Validate.notEmpty(node.getUuid(), "node uuid must be set.");
		}
		return nodeService.delete(authUser, actor, node);
	}

	@Override
	public SharedSpaceNode updatePartial(String actorUuid, PatchDto patchNode, String uuid) throws BusinessException {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Validate.notNull(patchNode, "Missind required input patch node.");
		Validate.notNull(patchNode.getName(), "The name of the attribute to update must be set .");
		Validate.notNull(patchNode.getValue(),"The value of the attribute to update must be set.");
		patchNode.setUuid(uuid);
		return nodeService.updatePartial(authUser, actor, patchNode);
	}
	
	@Override
	public SharedSpaceNode update(String actorUuid, SharedSpaceNode node, String uuid) throws BusinessException {
		Validate.notNull(node, "Missind required input shared space node.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		if (!Strings.isNullOrEmpty(uuid)) {
			node.setUuid(uuid);
		} else {
			Validate.notEmpty(node.getUuid(), "node uuid must be set.");
		}
		return nodeService.update(authUser, actor, node);
	}

	@Override
	public List<SharedSpaceMember> members(String actorUuid, String uuid, String accountUuid) throws BusinessException {
		Validate.notEmpty(uuid, "Missing required shared space node");
		Account authUser = checkAuthentication();
		return nodeService.findAllMembers(authUser, authUser, uuid, accountUuid);
	}

	@Override
	public List<SharedSpaceNodeNested> findAllMyNodes(String actorUuid, boolean withRole) {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		return memberService.findAllNodesOnTopByAccount(authUser, actor, actor.getLsUuid(), withRole);
	}

	@Override
	public List<SharedSpaceNode> findAll() {
		Account authUser = checkAuthentication();
		return nodeService.findAll(authUser, authUser);
	}

	@Override
	public List<SharedSpaceNodeNested> findAllWorkGroupsInsideNode(String actorUuid, String uuid) {
		Validate.notEmpty(uuid, "Missing required node uuid.");
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		SharedSpaceNode parent = nodeService.find(authUser, actor, uuid);
		return nodeService.findAllWorkgroupsInNode(authUser, actor, parent);
	}

}
