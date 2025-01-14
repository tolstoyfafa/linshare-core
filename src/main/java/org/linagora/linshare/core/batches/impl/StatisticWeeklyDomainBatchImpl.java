/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2021 LINAGORA
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

package org.linagora.linshare.core.batches.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.business.service.DomainDailyStatBusinessService;
import org.linagora.linshare.core.business.service.DomainWeeklyStatBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BatchBusinessException;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.job.quartz.ResultContext;
import org.linagora.linshare.core.job.quartz.BatchRunContext;
import org.linagora.linshare.core.job.quartz.DomainBatchResultContext;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.service.AbstractDomainService;

public class StatisticWeeklyDomainBatchImpl extends GenericBatchImpl {

	private final DomainDailyStatBusinessService domainDailyStatBusinessService;

	private final DomainWeeklyStatBusinessService domainWeeklyStatBusinessService;

	private final AbstractDomainService abstractDomainService;

	private final BatchHistoryBusinessService batchHistoryBusinessService;

	public StatisticWeeklyDomainBatchImpl(
			final DomainDailyStatBusinessService domainDailyStatBusinessService,
			final DomainWeeklyStatBusinessService domainWeeklyStatBusinessService,
			final AbstractDomainService abstractDomainService,
			final AccountRepository<Account> accountRepository,
			final BatchHistoryBusinessService batchHistoryBusinessService) {
		super(accountRepository);
		this.domainDailyStatBusinessService = domainDailyStatBusinessService;
		this.domainWeeklyStatBusinessService = domainWeeklyStatBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.batchHistoryBusinessService = batchHistoryBusinessService;
	}

	@Override
	public List<String> getAll(BatchRunContext batchRunContext) {
		logger.info("WeeklyDomainBatchImpl job starting");
		List<String> domains = domainDailyStatBusinessService
				.findIdentifierDomainBetweenTwoDates(getFirstDayOfLastWeek(), getLastDayOfLastWeek());
		logger.info(domains.size() + "domain(s) have been found in DomainDailyStat table.");
		return domains;
	}

	@Override
	public ResultContext execute(BatchRunContext batchRunContext, String identifier, long total, long position)
			throws BatchBusinessException, BusinessException {
		AbstractDomain resource = abstractDomainService.findById(identifier);
		ResultContext context = new DomainBatchResultContext(resource);
		try {
			console.logInfo(batchRunContext, total, position, "processing domain : " + resource.getDescription());
			domainWeeklyStatBusinessService.create(resource, getFirstDayOfLastWeek(), getLastDayOfLastWeek());
		} catch (BusinessException businessException) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.add(GregorianCalendar.DATE, -7);
			BatchBusinessException exception = new BatchBusinessException(context,
					"Error while trying to create a DomainWeeklyStat for domain " + resource.getDescription()
							+ " in the week " + calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH,
									GregorianCalendar.LONG, Locale.US));
			exception.setBusinessException(businessException);
			console.logError(batchRunContext, total, position,
					"Error while trying to create a DomainWeeklyStat for domain " + resource
							.getDescription() + " in the week "
					+ calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH, GregorianCalendar.LONG, Locale.US), exception);
			throw exception;
		}
		return context;
	}

	@Override
	public void notify(BatchRunContext batchRunContext, ResultContext context, long total, long position) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) context;
		AbstractDomain domain = domainContext.getResource();
		console.logInfo(batchRunContext, total,
				position, "the WeeklyDomainStat for " + domain.getUuid() + "has been successfully created.");
	}

	@Override
	public void notifyError(BatchBusinessException exception, String identifier, long total, long position, BatchRunContext batchRunContext) {
		DomainBatchResultContext domainContext = (DomainBatchResultContext) exception.getContext();
		AbstractDomain domain = domainContext.getResource();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.DATE, -7);
		console.logError(batchRunContext, total, position,
				"creating WeeklyDomainStatistic has failed for domain " + domain.getDescription() + " in the week "
						+ calendar.getDisplayName(GregorianCalendar.WEEK_OF_MONTH, GregorianCalendar.LONG, Locale.US));
	}

	@Override
	public void terminate(BatchRunContext batchRunContext, List<String> all, long errors, long unhandled_errors,
			long total, long processed) {
		long success = total - errors - unhandled_errors;
		logger.info(success + " WeeklyDomainStatistic for domain(s) have bean created.");
		if (errors > 0) {
			logger.info(errors + "  WeeklyDomainStatistic for domain(s) failed to be created");
		}
		if (unhandled_errors > 0) {
			logger.error(unhandled_errors + " WeeklyDomainStatistic failed to be created (unhandled error.)");
		}
		logger.info("WeeklyDomainBatchImpl job terminated");
	}

	private Date getLastDayOfLastWeek() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		Date firstDate = getFirstDayOfLastWeek();
		dateCalendar.setTime(firstDate);
		int today = dateCalendar.get(GregorianCalendar.DAY_OF_MONTH);
		dateCalendar.set(GregorianCalendar.DAY_OF_MONTH, today + 6);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		dateCalendar.set(GregorianCalendar.MINUTE, 59);
		dateCalendar.set(GregorianCalendar.SECOND, 59);
		dateCalendar.set(GregorianCalendar.MILLISECOND, 999);
		return dateCalendar.getTime();
	}

	private Date getFirstDayOfLastWeek() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		dateCalendar.add(Calendar.DATE, -7);
		int firstDay = dateCalendar.getFirstDayOfWeek();
		if (firstDay == GregorianCalendar.SUNDAY)
			firstDay++;
		else {
			if (firstDay != GregorianCalendar.MONDAY)
				throw new BusinessException("the used local can not be supported");
		}
		dateCalendar.set(GregorianCalendar.DAY_OF_WEEK, firstDay);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		dateCalendar.set(GregorianCalendar.MILLISECOND, 0);
		return dateCalendar.getTime();
	}

	private Date getFirstDayOfWeek() {
		GregorianCalendar dateCalendar = new GregorianCalendar();
		int firstDay = dateCalendar.getFirstDayOfWeek();
		if (firstDay == GregorianCalendar.SUNDAY)
			firstDay++;
		else {
			if (firstDay != GregorianCalendar.MONDAY)
				throw new BusinessException("the used local can not be supported");
		}
		dateCalendar.set(GregorianCalendar.DAY_OF_WEEK, firstDay);
		dateCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		dateCalendar.set(GregorianCalendar.MINUTE, 0);
		dateCalendar.set(GregorianCalendar.SECOND, 0);
		dateCalendar.set(GregorianCalendar.MILLISECOND, 0);
		return dateCalendar.getTime();
	}

	@Override
	public boolean needToRun() {
		return !batchHistoryBusinessService.exist(getFirstDayOfWeek(), BatchType.WEEKLY_DOMAIN_BATCH);
	}
}
