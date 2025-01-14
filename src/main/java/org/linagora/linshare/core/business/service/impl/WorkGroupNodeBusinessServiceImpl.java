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
package org.linagora.linshare.core.business.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.DocumentEntryRevisionBusinessService;
import org.linagora.linshare.core.business.service.WorkGroupNodeBusinessService;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.light.AuditDownloadLightEntity;
import org.linagora.linshare.mongo.entities.logs.WorkGroupNodeAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.NodeMetadataMto;
import org.linagora.linshare.utils.DocumentCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class WorkGroupNodeBusinessServiceImpl implements WorkGroupNodeBusinessService {

	protected Logger logger = LoggerFactory.getLogger(WorkGroupNodeBusinessServiceImpl.class);

	protected final static String PATH_SEPARATOR = "/";
	protected final static String ARCHIVE_MIME_TYPE = "application/zip";
	protected final static String ARCHIVE_EXTENTION = ".zip";

	protected final DocumentEntryBusinessService documentEntryBusinessService;

	protected final DocumentEntryRevisionBusinessService documentEntryRevisionBusinessService;

	protected final MongoTemplate mongoTemplate;

	public WorkGroupNodeBusinessServiceImpl(DocumentEntryBusinessService documentEntryBusinessService,
			DocumentEntryRevisionBusinessService documentEntryRevisionBusinessService,
			MongoTemplate mongoTemplate) {
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.documentEntryRevisionBusinessService = documentEntryRevisionBusinessService;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public Map<String, Long> findTotalOccurenceOfMimeTypeByDomain(List<String> workgroupsByDomains, Date bDate, Date eDate) {
		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(
				Criteria.where("workGroup").in(workgroupsByDomains)
				.and("uploadDate").gte(bDate).lt(eDate)),
				Aggregation.group("mimeType").count().as("total"),
				Aggregation.project("total").and("mimeType").previousOperation());
		List<DocumentCount> results = mongoTemplate.aggregate(aggregation, "work_group_nodes", DocumentCount.class).getMappedResults();
		return documentCountToMap(results);
	}

	private Map<String, Long> documentCountToMap(List<DocumentCount> results) {
		Map<String, Long> map = Maps.newHashMap();
		for (DocumentCount documentCount : results) {
			map.put(documentCount.getMimetype(), documentCount.getTotal());
		}
		return map;
	}

	@Override
	public Long computeNodeSize(WorkGroup workGroup, String pattern, WorkGroupNodeType nodeType) throws BusinessException {
		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(
				Criteria.where("workGroup").is(workGroup.getLsUuid())
				.and("path").regex(pattern).and("nodeType").is(nodeType)),
				Aggregation.group().sum("size").as("size"));
		NodeMetadataMto result = mongoTemplate.aggregate(aggregation, "work_group_nodes", NodeMetadataMto.class)
				.getUniqueMappedResult();
		if (result == null) {
			return 0L;
		}
		return result.getSize();
	}

	@Override
	public Long computeNodeCount(WorkGroup workGroup, String pattern, WorkGroupNode node) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		query.addCriteria(Criteria.where("workGroup").is(workGroup.getLsUuid()));
		if (WorkGroupNodeType.FOLDER.equals(node.getNodeType())
				|| WorkGroupNodeType.ROOT_FOLDER.equals(node.getNodeType())) {
			query.addCriteria(Criteria.where("path").regex(pattern));
			query.addCriteria(criteria.orOperator(
					Criteria.where("nodeType").is(WorkGroupNodeType.DOCUMENT),
					Criteria.where("nodeType").is(WorkGroupNodeType.FOLDER)));
		} else {
			query.addCriteria(Criteria.where("parent").is(node.getUuid())
					.and("nodeType").is(WorkGroupNodeType.DOCUMENT_REVISION));
		}
		Long result = mongoTemplate.count(query, WorkGroupNode.class);
		return result;
	}

	@Override
	public Map<String, WorkGroupNode> findAllSubNodes(WorkGroup workGroup, String pattern) {
		Query query = new Query();
		query.addCriteria(Criteria.where("workGroup").is(workGroup.getLsUuid()).orOperator(
				Criteria.where("nodeType").is(WorkGroupNodeType.DOCUMENT),
				Criteria.where("nodeType").is(WorkGroupNodeType.FOLDER)));
		query.addCriteria(Criteria.where("path").regex(pattern));
		List<WorkGroupNode> nodes = mongoTemplate.find(query, WorkGroupNode.class);
		return nodes.stream().collect(Collectors.toMap(WorkGroupNode::getUuid, Function.identity()));
	}

	@Override
	public List<WorkGroupNode> findAllSubDocuments(WorkGroup workGroup, String pattern) {
		Query query = new Query();
		query.addCriteria(Criteria.where("workGroup").is(workGroup.getLsUuid()));
		query.addCriteria(Criteria.where("nodeType").is(WorkGroupNodeType.DOCUMENT));
		query.addCriteria(Criteria.where("path").regex(pattern));
		return mongoTemplate.find(query, WorkGroupNode.class);
	}

	@Override
	public FileAndMetaData downloadFolder(Account actor, User owner, WorkGroup workGroup, WorkGroupNode rootNode,
			Map<String, WorkGroupNode> map, List<WorkGroupNode> documentNodes, WorkGroupNodeAuditLogEntry log) {
		FileAndMetaData fileAndMetaData = null;
		try {
			File zipFile = File.createTempFile("linshare-download-folder-", ARCHIVE_EXTENTION);
			zipFile.deleteOnExit();
			logger.debug("Zip file path : {}", zipFile.getAbsolutePath());
			try (FileOutputStream fos = new FileOutputStream(zipFile);
					ZipOutputStream zos = new ZipOutputStream(fos);) {
				for (WorkGroupNode node : documentNodes) {
					String humanPath = getGlobalPath(map, node.getPath(), rootNode);
					log.addAuditDownloadLightEntity(
							new AuditDownloadLightEntity(node.getUuid(), humanPath.concat(node.getName())));
					log.addRelatedResources(node.getUuid());
					WorkGroupDocumentRevision revision = (WorkGroupDocumentRevision) documentEntryRevisionBusinessService
							.findMostRecent(workGroup, node.getUuid());
					try (InputStream stream = documentEntryBusinessService.getByteSource(revision).openBufferedStream();) {
						addFileToZip(stream, zos, node.getName(), humanPath, revision.getSize());
					} catch (IOException ioException) {
						logger.error("Download folder with UUID {} was failed.", rootNode.getUuid(), ioException);
						throw new BusinessException(BusinessErrorCode.WORK_GROUP_NODE_DOWNLOAD_INTERNAL_ERROR,
								"Can not generate the archive for this directory");
					}
				}
				zos.close();
				fileAndMetaData = new FileAndMetaData(Files.asByteSource(zipFile), zipFile.length(),
						rootNode.getName().concat(ARCHIVE_EXTENTION), ARCHIVE_MIME_TYPE);
				fileAndMetaData.setFile(zipFile);
			} catch (IOException ioException) {
				logger.error("Download folder {} failed.", rootNode.getUuid(), ioException);
				throw new BusinessException(BusinessErrorCode.WORK_GROUP_NODE_DOWNLOAD_INTERNAL_ERROR,
						"Can not generate the archive for this directory");
			}
		} catch (IOException ioException) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_NODE_DOWNLOAD_INTERNAL_ERROR,
					"Can not generate a temp file");
		}
		return fileAndMetaData;
	}

	@Override
	public FileAndMetaData downloadArchiveRevision(Account actor, Account owner, WorkGroup workGroup,
			WorkGroupNode rootNode, List<WorkGroupNode> documentNodes, WorkGroupNodeAuditLogEntry log) {
		FileAndMetaData fileAndMetaData = null;
		try {
			File zipFile = File.createTempFile("linshare-download-revision-", ARCHIVE_EXTENTION);
			zipFile.deleteOnExit();
			try (FileOutputStream fos = new FileOutputStream(zipFile);
					ZipOutputStream zos = new ZipOutputStream(fos);) {
				for (WorkGroupNode node : documentNodes) {
					log.addAuditDownloadLightEntity(new AuditDownloadLightEntity(node.getUuid(), node.getName()));
					log.addRelatedResources(node.getUuid());
					try (InputStream stream = documentEntryBusinessService
							.getByteSource((WorkGroupDocumentRevision) node).openBufferedStream();) {
						addFileToZip(stream, zos, node.getName(), "", ((WorkGroupDocumentRevision) node).getSize());
					} catch (IOException ioException) {
						logger.error("Download document {} with its revisions failed.", rootNode.getUuid(),
								ioException);
						throw new BusinessException(BusinessErrorCode.WORK_GROUP_NODE_DOWNLOAD_INTERNAL_ERROR,
								"Can not generate the archive for this directory");
					}
				}
				zos.close();
				fileAndMetaData = new FileAndMetaData(Files.asByteSource(zipFile), zipFile.length(),
						rootNode.getName().concat(ARCHIVE_EXTENTION), ARCHIVE_MIME_TYPE);
				fileAndMetaData.setFile(zipFile);
			} catch (IOException ioException) {
				logger.error("Download document {} with its revisions failed.", rootNode.getUuid(), ioException);
				throw new BusinessException(BusinessErrorCode.WORK_GROUP_NODE_DOWNLOAD_INTERNAL_ERROR,
						"Can not generate the archive for this directory");
			}
		} catch (IOException ioException) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_NODE_DOWNLOAD_INTERNAL_ERROR,
					"Can not generate a temp file");
		}
		return fileAndMetaData;
	}

	/**
	 * Transform a path from uuid,uuid.. to /nodeName1/NodeName2..
	 * @return String
	 */
	private String getGlobalPath(Map<String, WorkGroupNode> nodes, String path, WorkGroupNode root) {
		String nodePath = root.getName() + PATH_SEPARATOR;
		if (path == null) {
			return "";
		}
		String[] split = path.split(",");
		for (String uuid : split) {
			if (!uuid.isEmpty()) {
				WorkGroupNode currentNode = nodes.get(uuid);
				if (currentNode != null) {
						nodePath = nodePath + currentNode.getName() + PATH_SEPARATOR;
				}
			}
		}
		return nodePath;
	}

	private void addFileToZip(InputStream stream, ZipOutputStream zos, String documentName, String path, Long size)
			throws IOException {
		String filePath = path + documentName;
		ZipEntry zipEntry = new ZipEntry(filePath);
		zos.putNextEntry(zipEntry);
		IOUtils.copy(stream, zos);
		zos.closeEntry();
	}

}
