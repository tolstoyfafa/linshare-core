/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2017-2021 LINAGORA
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
package org.linagora.linshare.core.domain.constants;

import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;

public enum UpgradeTaskType {

	/*
	 * uuid generation for domains instead of identifier/label
	 */
	UPGRADE_2_0_DOMAIN_UUID,

	/*
	 * uuid generation for domains instead of identifier/label
	 */
	UPGRADE_2_0_DOMAIN_POLICIES_UUID,

	/*
	 * Compute sha256sum for all stored document (this value may be undefined)
	 */
	UPGRADE_2_0_SHA256SUM,

	UPGRADE_2_0_CLEANUP_EXPIRED_GUEST,

	UPGRADE_2_0_CLEANUP_EXPIRED_ACCOUNT,

	UPGRADE_2_0_PURGE_ACCOUNT,

	/*
	 * initialization quota structure (domain quota and container quota) for all
	 * existing domains
	 */
	UPGRADE_2_0_DOMAIN_QUOTA_TOPDOMAINS,
	UPGRADE_2_0_DOMAIN_QUOTA_SUBDOMAINS,

	/*
	 * initialization quota structure for all existing accounts (users and
	 * workgroups)
	 */
	UPGRADE_2_0_ACCOUNT_QUOTA,

	UPGRADE_2_0_THREAD_TO_WORKGROUP,

	/*
	 * Trigger the migration of all documents from the old datastore to the new
	 * datastore.
	 */
	UPGRADE_2_0_UPGRADE_STORAGE,

	/*
	 * Add all document uuid in the garbage (DocumentGarbageCollector),
	 * and trigger it
	 */
	UPGRADE_2_1_DOCUMENT_GARBAGE_COLLECTOR,

	/*
	 * Calculus workgroup quota from wokgroup document entries
	 * and update it
	 */
	UPGRADE_2_1_COMPUTE_USED_SPACE_FOR_WORGROUPS,

	/*
	 * Remove all thread entries, they are useless on 2.1
	 *
	 */
	UPGRADE_2_1_REMOVE_ALL_THREAD_ENTRIES,

	/* 
	 * Compute the workgroup quota container for each domain,
	 * and update the current value in domains
	 * 
	 */
	UPGRADE_2_1_COMPUTE_CURRENT_VALUE_FOR_DOMAINS,

	/*
	 * Compute the current_value_for_subdomains for each top_domain
	 * Compute the current_value_for_subdomains for root_domain
	 * 
	 */
	UPGRADE_2_1_COMPUTE_TOP_AND_ROOT_DOMAIN_QUOTA,

	/*
	 * When we upgraded apache tika we have new mimeType
	 * We have to update our database
	 *
	 */
	UPGRADE_2_1_ADD_ALL_NEW_MIME_TYPE,

	/*
	 * We migrate all entities related to upload proposition filter into the mongo database
	 */
	UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_FILTER_TO_MONGO_DATABASE,
	
	/*
	 * We migrate all entities related to upload proposition into the mongo database
	 */
	UPGRADE_2_2_MIGRATE_UPLOAD_PROPOSITION_TO_MONGO_DATABASE,

	/*
	 * We migrate all uploadRequestHistory to uploadRequestAudit into the mongo database
	 */
	UPGRADE_2_2_MIGRATE_UPLOAD_REQUEST_HISTORY_TO_MONGO_AUDIT,
	
	/*
	 * We migrate all thread member to shared space members into mongo database.
	 */
	UPGRADE_2_2_MIGRATE_THREAD_AND_THREAD_MEMBERS_TO_MONGO_DATABASE,

	/* 
	 * Generate oneshot basic statistics from the old audit log entries,
	 * calculate daily basic statistics
	 * 
	 */
	UPGRADE_2_2_GENERATE_BASIC_STATISTICS_FROM_AUDIT_LOG_ENTRIES,

	/* 
	 * Migrate all old WorkGroup audit to shared space audit
	 * 
	 */
	UPGRADE_2_2_MIGRATE_WORKGROUP_AUDIT_TO_SHARED_SPACE_AUDIT,

	/* 
	 * Migrate all old WorkGroup Member audit to shared space Member audit
	 * 
	 */
	UPGRADE_2_2_MIGRATE_WORKGROUP_MEMBER_AUDIT_TO_SHARED_SPACE_MEMBER_AUDIT,

	/*
	 * When we upgraded apache tika (1.20) we have new mimeType
	 * We have to update our database
	 *
	 */
	UPGRADE_2_3_ADD_ALL_NEW_MIME_TYPE,

	/*
	 *The old version of PermanentToken is more compatible with SQL databases 
	 * we have to change the structure for compatibility with MongoDB 
	 */
	UPGRADE_2_3_MIGRATE_PERMANENT_TOKEN_ENTITY_TO_NEW_STRUCTURE,

	/* 
	 * 	Upgrade document structure for versioning
	 * 
	 */
	UPGRADE_2_3_UPDATE_DOCUMENT_STRUCTURE_FOR_VERSIONING,

	/* 
	 * 	Upgrade SharedSpace structure :  add quotaUuid to all existing sharedSpaces.
	 * 
	 */
	UPGRADE_2_3_ADD_QUOTA_UUID_TO_ALL_SHARED_SPACES,

	/* 
	 * 	Upgrade SharedSpaceNode structure for versioning
	 * 
	 */
	UPGRADE_2_3_UPDATE_SHARED_SPACE_NODE_STRUCTURE_FOR_VERSIONING,

	/* 
	 * 	Update tagetDomainUuid for mail attachment audit
	 * 
	 */
	UPGRADE_4_0_UPDATE_TARGET_DOMAIN_UUID_MAIL_ATTACHMENT_AUDIT,

	/**
	 * Upgrade the SharedSpaceMember structure by adding a NodeType for the member's roles
	 */
	UPGRADE_4_0_UPDATE_SHARED_SPACE_MEMBER_STRUCTURE_WITH_TYPED_ROLES,

	/*
	 * When we upgraded apache tika (1.24) we have new mimeType
	 * We have to update our database
	 *
	 */
	UPGRADE_4_0_ADD_ALL_NEW_MIME_TYPE,

	/*
	 * Notify all guests with old password encoding strategy to reset
	 */
	UPGRADE_4_0_PASSWORD_ENCODING_STRATEGY_CHANGES_FOR_GUESTS,

	/**
	 * Send mail notification that contains new generated password for anonymous shares  
	 */
	UPGRADE_4_0_PASSWORD_ENCODING_STRATEGY_CHANGES_FOR_ANONYMOUS,

	/*
	 * When we upgraded apache tika (1.25) we have new mimeType
	 * We have to update our database
	 *
	 */
	UPGRADE_4_1_ADD_ALL_NEW_MIME_TYPE;

	public static UpgradeTaskType fromString(String s) {
		try {
			return UpgradeTaskType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new TechnicalException(TechnicalErrorCode.DATABASE_INCOHERENCE,
					StringUtils.isEmpty(s) ? "null or empty" : s);
		}
	}
}
