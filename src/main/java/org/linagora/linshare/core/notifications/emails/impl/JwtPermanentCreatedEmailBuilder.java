/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2019-2021 LINAGORA
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
package org.linagora.linshare.core.notifications.emails.impl;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.JwtPermanentCreatedEmailContext;
import org.linagora.linshare.core.notifications.dto.MailContact;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.thymeleaf.context.Context;

import com.google.common.collect.Lists;

public class JwtPermanentCreatedEmailBuilder extends EmailBuilder {

	@Override
	public MailContentType getSupportedType() {
		return MailContentType.ACCOUNT_OWNER_WARN_JWT_PERMANENT_TOKEN_CREATED;
	}

	@Override
	protected MailContainerWithRecipient buildMailContainer(EmailContext context) throws BusinessException {
		JwtPermanentCreatedEmailContext emailCtx = (JwtPermanentCreatedEmailContext) context;
		User owner = (User) emailCtx.getOwner();
		User recipient = (User) emailCtx.getRecipient();
		PermanentToken token = emailCtx.getJwtLongTime();
		MailConfig cfg = owner.getDomain().getCurrentMailConfiguration();
		Context ctx = new Context(emailCtx.getLocale());
		ctx.setVariable("label", token.getLabel());
		ctx.setVariable("description", token.getDescription());
		ctx.setVariable("creationDate", token.getCreationDate());
		ctx.setVariable("owner", owner);
		ctx.setVariable("recipient", recipient);
		MailContainerWithRecipient buildMailContainer = buildMailContainerThymeleaf(cfg, getSupportedType(), ctx,
				emailCtx);
		return buildMailContainer;
	}

	@Override
	protected List<Context> getContextForFakeBuild(Language language) {
		List<Context> res = Lists.newArrayList();
		Context ctx = newFakeContext(language);
		PermanentToken token = new PermanentToken();
		token.setLabel("Token label");
		token.setDescription("Token description");
		token.setCreationDate(new Date());
		ctx.setVariable("label", token.getLabel());
		ctx.setVariable("description", token.getDescription());
		ctx.setVariable("creationDate", new Date());
		ctx.setVariable("owner", new MailContact("peter.wilson@linshare.org", "Peter", "Wilson"));
		ctx.setVariable("recipient", new MailContact("amy.wolsh@linshare.org", "Amy", "Wolsh"));
		res.add(ctx);
		return res;
	}

}
