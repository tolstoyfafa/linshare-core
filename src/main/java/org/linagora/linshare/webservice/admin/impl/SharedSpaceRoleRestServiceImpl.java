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
package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.constants.NodeType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.SharedSpaceRoleFacade;
import org.linagora.linshare.mongo.entities.SharedSpacePermission;
import org.linagora.linshare.mongo.entities.SharedSpaceRole;
import org.linagora.linshare.webservice.admin.SharedSpaceRoleRestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


@Path("/shared_space_roles")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class SharedSpaceRoleRestServiceImpl implements SharedSpaceRoleRestService {

	private final SharedSpaceRoleFacade ssRoleFacade;

	public SharedSpaceRoleRestServiceImpl(SharedSpaceRoleFacade ssRoleFacade) {
		super();
		this.ssRoleFacade = ssRoleFacade;
	}
	
	@Path("{uuid}")
	@GET
	@Operation(summary = "Find a shared space role.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpacePermission.class))),
			responseCode = "200"
		)
	})
	@Override
	public SharedSpaceRole find(
			@Parameter(description = "The shared space role uuid.")
				@PathParam("uuid") String uuid)
			throws BusinessException {
		return ssRoleFacade.find(null, uuid);
	}

	@Path("/")
	@GET
	@Operation(summary = "Get all shared space roles.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpacePermission.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SharedSpaceRole> findAll(
			@Parameter(description = "Filter the roles by node type.", required = false)
				@QueryParam("nodeType") @DefaultValue("WORK_GROUP") NodeType nodeType) throws BusinessException {
		return ssRoleFacade.findAll(null, nodeType);
	}
	
	@Path("{uuid}/permissions")
	@GET
	@Operation(summary = "Get all shared space permissions of current role.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SharedSpacePermission.class))),
			responseCode = "200"
		)
	})
	@Override
	public List<SharedSpacePermission> findAllPermissions(
			@Parameter(description = "The role uuid")
				@PathParam(value="uuid")String roleUuid)
			throws BusinessException {
		return ssRoleFacade.findAll(null, roleUuid);
	}

}
