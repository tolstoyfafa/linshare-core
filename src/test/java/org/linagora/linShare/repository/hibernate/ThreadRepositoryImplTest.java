package org.linagora.linShare.repository.hibernate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.linagora.linShare.core.domain.constants.LinShareConstants;
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.Account;
import org.linagora.linShare.core.domain.entities.DomainAccessPolicy;
import org.linagora.linShare.core.domain.entities.DomainPolicy;
import org.linagora.linShare.core.domain.entities.Internal;
import org.linagora.linShare.core.domain.entities.RootDomain;
import org.linagora.linShare.core.domain.entities.ThreadMember;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.entities.Thread;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.AbstractDomainRepository;
import org.linagora.linShare.core.repository.AccountRepository;
import org.linagora.linShare.core.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


@ContextConfiguration(locations={"classpath:springContext-test.xml",
		"classpath:springContext-datasource.xml",
        "classpath:springContext-repository.xml"})
public class ThreadRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	// default import.sql
	private static final String DOMAIN_IDENTIFIER = LinShareConstants.rootDomainIdentifier;

    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String MAIL = "mail";
    private static final String UID = "uid";
     
    
    @Autowired
	@Qualifier("accountRepository")
	private AccountRepository<Account> accountRepository;
    
    
	@Autowired
	@Qualifier("threadRepository")
	private ThreadRepository<Thread> threadRepository;
	
	
	@Autowired
	private AbstractDomainRepository abstractDomainRepository;

	
	
	private AbstractDomain domain;
	
	private User internal;
	
	
	@Before
	public void setUp() throws Exception {
		logger.debug("Begin setUp");
		domain = abstractDomainRepository.findById(DOMAIN_IDENTIFIER);
		internal = new Internal( FIRST_NAME, LAST_NAME, MAIL, UID);
		String uid = UID + "/" + DOMAIN_IDENTIFIER;
		internal.setLsUid(uid);
		internal.setDomain(domain);
		
		accountRepository.create(internal);
		logger.debug("End setUp");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("Begin tearDown");
		accountRepository.delete(internal);
		logger.debug("End tearDown");
	}
	
	
	@Test
	public void testCreateThread() throws BusinessException{
		
		Thread t = new Thread(domain, internal, "myThread");
		String uid = UID + "/" + DOMAIN_IDENTIFIER + "/myThread";
		t.setLsUid(uid);
		threadRepository.create(t);
	}
	
	
	@Test
	public void testCreateThreadAndMember() throws BusinessException{
		
		Thread t = new Thread(domain, internal, "myThread");
		String uid = UID + "/" + DOMAIN_IDENTIFIER + "/myThread";
		t.setLsUid(uid);
		threadRepository.create(t);
		
		ThreadMember m = new ThreadMember(true,true,internal,t);
		t.getMyMembers().add(m);
		threadRepository.update(t);
		
		logger.info("user id :" + internal.getId());
		logger.info("thread id :" + t.getId());
		logger.info("member id :" + m.getId());
		
		
	}
	
	
	
}
