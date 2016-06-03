/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.domain.vo;

import java.io.Serializable;

import org.linagora.linshare.core.domain.constants.AccountType;


public class DisplayableAccountOccupationEntryVo implements Comparable<DisplayableAccountOccupationEntryVo>, Serializable {

	private static final long serialVersionUID = -8892677890684059706L;

	private final String actorFirstname;
	
	private final String actorLastname;
	
	private final String actorMail;
	
	private final AccountType actorType;
	
	private final Long userUsedQuota;
	
	private final Long userAvailableQuota;
	
	private final Long userTotalQuota;

	public DisplayableAccountOccupationEntryVo(String actorFirstname,
			String actorLastname, String actorMail, AccountType actorType,
			Long userAvailableQuota, Long userTotalQuota, Long userUsedQuota) {
		this.actorFirstname = actorFirstname;
		this.actorLastname = actorLastname;
		this.actorMail = actorMail;
		this.actorType = actorType;
		this.userAvailableQuota = userAvailableQuota;
		this.userTotalQuota = userTotalQuota;
		this.userUsedQuota = userUsedQuota;
	}

	public String getActorMail() {
		return actorMail;
	}

	public String getActorFirstname() {
		return actorFirstname;
	}

	public String getActorLastname() {
		return actorLastname;
	}
	
	public AccountType getActorType() {
		return actorType;
	}

	public Long getUserAvailableQuota() {
		return userAvailableQuota;
	}

	public Long getUserTotalQuota() {
		return userTotalQuota;
	}
	
	public Long getUserUsedQuota() {
		return userUsedQuota;
	}
	
	public int compareTo(DisplayableAccountOccupationEntryVo o) {
		return this.userAvailableQuota.compareTo(o.getUserAvailableQuota());
	}
	
}