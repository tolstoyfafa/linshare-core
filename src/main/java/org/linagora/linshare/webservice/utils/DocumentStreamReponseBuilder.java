/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
package org.linagora.linshare.webservice.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

public class DocumentStreamReponseBuilder {

	private static Logger logger = LoggerFactory.getLogger(DocumentStreamReponseBuilder.class);

	public static ResponseBuilder getDocumentResponseBuilder(ByteSource byteSource, String fileName, String mimeType) {
		return getDocumentResponseBuilder(byteSource, fileName, mimeType, null);
	}

	public static ResponseBuilder getThumbnailResponseBuilder(FileAndMetaData data, boolean base64, ThumbnailType kind) {
		return getThumbnailResponseBuilder(data.getByteSource(), data.getName() + ThumbnailType.getFileType(kind), base64, kind);
	}

	public static ResponseBuilder getThumbnailResponseBuilder(ByteSource byteSource, String fileName, boolean base64, ThumbnailType kind) {
		ResponseBuilder response = null;
		if (base64) {
			response = getDocumentResponseBuilderBase64(byteSource, fileName,
					ThumbnailType.getFileMimeType(kind), null);
		} else {
			response = getDocumentResponseBuilder(byteSource, fileName,
					ThumbnailType.getFileMimeType(kind), null);
		}
		return response;
	}

	public static ResponseBuilder getDocumentResponseBuilder(ByteSource byteSource, String fileName, String mimeType,
			Long fileSize) {
		if (byteSource == null) {
			throw new TechnicalException(TechnicalErrorCode.MISSING_DOCUMENT_IN_FILEDATASTORE, "Can not download file : " + fileName);
		}
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream out) throws IOException, WebApplicationException {
				try (InputStream inputStream = byteSource.openBufferedStream()) {
					ByteStreams.copy(inputStream, out);
				} finally {
					out.close();
				}
			}
		};
		ResponseBuilder response = Response.ok(stream);
		setHeaderToResponse(response, fileName, mimeType, fileSize);
		return response;
	}

	public static ResponseBuilder getDocumentResponseBuilder(FileAndMetaData data) {
		return getDocumentResponseBuilder(data.getByteSource(), data.getName(), data.getMimeType(), data.getSize());
	}

	public static ResponseBuilder getDocumentResponseBuilderBase64(
			ByteSource byteSource, String fileName, String mimeType,
			Long fileSize) {
		if (byteSource == null) {
			throw new TechnicalException(TechnicalErrorCode.MISSING_DOCUMENT_IN_FILEDATASTORE, "Can not download file : " + fileName);
		}
		ResponseBuilder response = null;
		byte[] byteArray = null;
		try (InputStream inputStream = byteSource.openBufferedStream()) {
			if (inputStream != null) {
				byteArray = IOUtils.toByteArray(inputStream);
			}
			response = Response.ok(Base64.encodeBase64(byteArray));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessErrorCode.BASE64_INPUTSTREAM_ENCODE_ERROR,
					e.getMessage());
		}
		setHeaderToResponse(response, fileName, mimeType, fileSize);
		return response;
	}

	private static void setHeaderToResponse(ResponseBuilder response,
			String fileName, String mimeType, Long fileSize) {
		if (fileName != null)
			response.header("Content-Disposition", getContentDispositionHeader(fileName));
		response.header("Content-Type", mimeType);
		response.header("Content-Transfer-Encoding", "binary");
		if (fileSize != null)
			response.header("Content-Length", fileSize);

		// BUG WITH IE WHEN PRAGMA IS NO-CACHE solution is:
		// The proper solution to IE cache issues is to declare the attachment
		// as "Pragma: private"
		// and "Cache-Control: private, must-revalidate" in the HTTP Response.
		// This allows MS-IE to save the content as a temporary file in its
		// local cache,
		// but in not general public cache servers, before handing it off the
		// plugin, e.g. Adobe Acrobat, to handle it.

		// Pragma is a HTTP 1.0 directive that was retained in HTTP 1.1 for
		// backward compatibility.
		// no-cache prevent caching in proxy
		response.header("Pragma", "private");

		// cache-control: private. It instructs proxies in the path not to cache
		// the page. But it permits browsers to cache the page.
		// must-revalidate means the browser must revalidate the page against
		// the server before serving it from cache

		// post-check Defines an interval in seconds after which an entity must
		// be checked for freshness.
		// The check may happen after the user is shown the resource but ensures
		// that on the next roundtrip
		// the cached copy will be up-to-date
		// pre-check Defines an interval in seconds after
		// which an entity must be checked for freshness prior to showing the
		// user the resource.

		response.header("Cache-Control",
				"private,must-revalidate, post-check=0, pre-check=0");
	}

	private static String getContentDispositionHeader(String fileName) {
		String encodeFileName = null;
		try {
			URI uri = new URI(null, null, fileName, null);
			encodeFileName = uri.toASCIIString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("attachment; ");

		// Adding filename using the old way for old browser compatibility
		sb.append("filename=\"" + fileName + "\"; ");

		// Adding UTF-8 encoded filename. If the browser do not support this
		// parameter, it will use the old way.
		if (encodeFileName != null) {
			sb.append("filename*= UTF-8''" + encodeFileName);
		}
		return sb.toString();
	}
}
