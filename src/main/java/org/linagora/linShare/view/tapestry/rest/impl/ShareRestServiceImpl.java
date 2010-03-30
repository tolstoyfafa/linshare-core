/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.view.tapestry.rest.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.ValidationMessagesSource;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.Facade.ShareFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.rest.ShareRestService;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.linagora.linShare.view.tapestry.services.impl.PropertiesSymbolProvider;
import org.linagora.restmarshaller.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.antera.t5restfulws.RestfulWebMethod;


public class ShareRestServiceImpl implements ShareRestService {

	private final ApplicationStateManager applicationStateManager; 
	
	private final ShareFacade shareFacade;
	private final UserFacade userFacade;
	private final DocumentFacade documentFacade;
	
	private final Marshaller xstreamMarshaller;

    private final Asset sharedTemplate;
    private final Asset sharedTemplateTxt;
    private final Asset passwordSharedTemplate;
    private final Asset passwordSharedTemplateTxt;
    
	private final PropertiesSymbolProvider propertiesSymbolProvider;
	
	private final Templating templating;
	private final ThreadLocale threadLocale; 
	private final ValidationMessagesSource validationMessagesSource;
	
	private static final Logger logger = LoggerFactory.getLogger(ShareRestServiceImpl.class);
	
	public ShareRestServiceImpl(final ApplicationStateManager applicationStateManager,
				final ShareFacade shareFacade, 
				final UserFacade userFacade,
				final DocumentFacade documentFacade,
				final Asset sharedTemplate,
				final Asset sharedTemplateTxt,
				final Asset passwordSharedTemplate,
				final Asset passwordSharedTemplateTxt,
				final PropertiesSymbolProvider propertiesSymbolProvider,
				final Templating templating,  final ValidationMessagesSource validationMessagesSource,
				final ThreadLocale threadLocale,
				final Marshaller xstreamMarshaller) {
		super();
		this.applicationStateManager = applicationStateManager;
		this.shareFacade = shareFacade;
		this.userFacade = userFacade;
		this.documentFacade = documentFacade;
		this.sharedTemplate = sharedTemplate;
		this.sharedTemplateTxt = sharedTemplateTxt;
		this.passwordSharedTemplate = passwordSharedTemplate;
		this.passwordSharedTemplateTxt = passwordSharedTemplateTxt;
		this.propertiesSymbolProvider = propertiesSymbolProvider;
		this.templating = templating;
		this.threadLocale = threadLocale;
		this.validationMessagesSource = validationMessagesSource;
		this.xstreamMarshaller = xstreamMarshaller;
	}
	
	
	/* (non-Javadoc)
	 * @see org.linagora.linShare.view.tapestry.rest.impl.ShareRestService#sharedocument(org.apache.tapestry5.services.Request, org.apache.tapestry5.services.Response, java.lang.String, java.lang.String)
	 */
	@RestfulWebMethod
	public void sharedocument(Request request, Response response, String targetMail, String uuid) throws IOException {
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);
		
		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}
		
		if ((actor.isGuest() && !actor.isUpload())) {
			response.sendError(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
			return;
		}
		
		
		// fetch the document
		DocumentVo docVo = documentFacade.getDocument(actor.getLogin(), uuid);

		if (docVo == null) {
			response.sendError(HttpStatus.SC_NOT_FOUND, "Document not found");
			return;
		}
		
		List<DocumentVo> listDoc = new ArrayList<DocumentVo>();
		listDoc.add(docVo);
		
		List<String> listRecipient = new ArrayList<String>();
		listRecipient.add(targetMail);
		
		SuccessesAndFailsItems<ShareDocumentVo> successes;

		// Prepare mail content :
		String linShareUrlInternal=propertiesSymbolProvider.valueForSymbol("linshare.info.url.internal");
		String linShareUrlBase=propertiesSymbolProvider.valueForSymbol("linshare.info.url.base");

		String sharedTemplateContent = templating.readFullyTemplateContent(sharedTemplate.getResource().openStream());
		String sharedTemplateContentTxt = templating.readFullyTemplateContent(sharedTemplateTxt.getResource().openStream());
		String passwordSharedTemplateContent = templating.readFullyTemplateContent(passwordSharedTemplate.getResource().openStream());
		String passwordSharedTemplateContentTxt = templating.readFullyTemplateContent(passwordSharedTemplateTxt.getResource().openStream());

		// Get the messages service to localize the mail sent
		Messages messages = validationMessagesSource.getValidationMessages(threadLocale.getLocale());

		String subject = messages.get("mail.user.all.share.subject");

		// Get if it has to be a secured share
		String securedShareValue = request.getParameter("securedShare");
		boolean secureSharing = securedShareValue == null ? false : securedShareValue.equals("1");

		try {
			successes = shareFacade.createSharingWithMailUsingRecipientsEmail(
                    actor, listDoc, listRecipient, "", subject, linShareUrlInternal, linShareUrlBase,
                    secureSharing, sharedTemplateContent, sharedTemplateContentTxt, passwordSharedTemplateContent, passwordSharedTemplateContentTxt,null,null);
		} catch (BusinessException e) {
			logger.error("could not share the document " + docVo.getIdentifier() + " to user " + targetMail + " by user " + actor.getMail() + " reason : " + e.getMessage());
			response.setHeader("BusinessError", e.getErrorCode().getCode()+"");
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "Could not share the document");
			return;
		}
		
		if ( (successes.getSuccessesItem()==null) || ((successes.getFailsItem()!=null) && (successes.getFailsItem().size()>0))) {
			logger.error("could not share the document " + docVo.getIdentifier() + " to user " + targetMail + " by user " + actor.getMail());
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "Could not share the document");
			return;
		}
		
		logger.debug("Shared the document " + docVo.getIdentifier() + " to user " + targetMail + " by user " + actor.getMail());
		response.setStatus(HttpStatus.SC_OK);

	}

	@RestfulWebMethod
	public void multiplesharedocuments(Request request, Response response)
			throws IOException {
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);
		
		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}
		
		if ((actor.isGuest() && !actor.isUpload())) {
			response.sendError(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
			return;
		}

		if (!"POST".equals(request.getMethod())) {
			response.sendError(HttpStatus.SC_METHOD_NOT_ALLOWED, "Method not allowed");
			response.setHeader("Allow", "POST");
			return;
		}
		
		if (!request.getParameterNames().contains("targetMail")) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Missing parameter targetMail");
			return;
		}
		
		if (!request.getParameterNames().contains("file")) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Missing parameter file");
			return;
		}
		// fetch the target
		String targetMail = request.getParameter("targetMail");
		if ((targetMail == null) || (targetMail.equals(""))) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Target is empty");
			return;
		}
		List<String> listRecipient = new ArrayList<String>();
		listRecipient.add(targetMail);
		
		// Get if it has to be a secured share
		String securedShareValue = request.getParameter("securedShare");
		boolean secureSharing = securedShareValue == null ? false : securedShareValue.equals("1");
		
		String fileUUID =request.getParameter("file") ;
		List<DocumentVo> listDoc = new ArrayList<DocumentVo>();
		
		// fetch the document
		DocumentVo docVo = documentFacade.getDocument(actor.getLogin(), fileUUID);
		if (docVo==null) {
			response.sendError(HttpStatus.SC_NOT_FOUND, "Document " + fileUUID + " not found");
			return;
		}
	
		listDoc.add(docVo);
		
		// fetching all the files
		int i=1;
		while (request.getParameterNames().contains("file"+i)) {
			fileUUID =request.getParameter("file"+i) ;
			// fetch the document
			docVo = documentFacade.getDocument(actor.getLogin(), fileUUID);
			if (docVo==null) {
				response.sendError(HttpStatus.SC_NOT_FOUND, "Document " + fileUUID + " not found");
				return;
			}
			listDoc.add(docVo);
			i++;
		}
		
		
		// getting the optionnal message
		String message = "";
		if (request.getParameterNames().contains("message")) {
			message = request.getParameter("message");
		}
		
		// getting the templating data
		String linShareUrlInternal=propertiesSymbolProvider.valueForSymbol("linshare.info.url.internal");
		String linShareUrlBase=propertiesSymbolProvider.valueForSymbol("linshare.info.url.base");
		
		
		String sharedTemplateContent = null;
		String sharedTemplateContentTxt = null;
		String passwordSharedTemplateContent = null;
		String passwordSharedTemplateContentTxt = null;
		
		sharedTemplateContent = templating.readFullyTemplateContent(sharedTemplate.getResource().openStream());
		sharedTemplateContentTxt = templating.readFullyTemplateContent(sharedTemplateTxt.getResource().openStream());
		passwordSharedTemplateContent = templating.readFullyTemplateContent(passwordSharedTemplate.getResource().openStream());
		passwordSharedTemplateContentTxt = templating.readFullyTemplateContent(passwordSharedTemplateTxt.getResource().openStream());
		
		/**
		 * retrieve the subject of the mail.
		 */
		// Get the messages service to localize the mail sent
		Messages messages = validationMessagesSource.getValidationMessages(threadLocale.getLocale());
		
		String subject=messages.get("mail.user.all.share.subject");
		
		SuccessesAndFailsItems<ShareDocumentVo> successes;
		
		try {
			successes = shareFacade.createSharingWithMailUsingRecipientsEmail(
					actor, listDoc, listRecipient, message, subject, linShareUrlInternal, linShareUrlBase, secureSharing,
					sharedTemplateContent, sharedTemplateContentTxt,passwordSharedTemplateContent,passwordSharedTemplateContentTxt,null,null);
		} catch (BusinessException e) {
			logger.error("could not share the document " + docVo.getIdentifier() + " to user " + targetMail + " by user " + actor.getMail() + " reason : " + e.getMessage());
			response.setHeader("BusinessError", e.getErrorCode().getCode()+"");
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "Could not share the document");
			return;
		
		}
		if ( (successes.getSuccessesItem()==null) || ((successes.getFailsItem()!=null) && (successes.getFailsItem().size()>0))) {
			logger.error("could not share the documents");
			response.sendError(HttpStatus.SC_METHOD_FAILURE, "Could not share the documents");
			return;
		}
		
		response.setStatus(HttpStatus.SC_OK);
		
	}
	
	
	
}
