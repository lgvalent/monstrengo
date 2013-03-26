package br.com.orionsoft.monstrengo.core.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import br.com.orionsoft.monstrengo.core.test.ApplicationBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.dao.DaoManager;

/**
 * TODO DOCUMENTAR essa classe de teste
 * @author marcia
 * @version 2005/04/11
 *
 */
public class DaoBasicTest extends ApplicationBasicTest
{
    protected DaoManager daoManager;
    protected Session session;
    
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        System.out.println("setup daobasic");

        daoManager = (DaoManager)ctx.getBean("DaoManager");
       
        session = daoManager.getSessionFactory().openSession();
        
        TransactionSynchronizationManager.bindResource(daoManager.getSessionFactory(), new SessionHolder(session));
    }

    @After
    public void tearDown() throws Exception
    {
        super.tearDown();
        System.out.println("tearDown daobasic");
        session.flush();
        TransactionSynchronizationManager.unbindResource(daoManager.getSessionFactory());
        SessionFactoryUtils.closeSession(session);
        
        daoManager = null;
        session = null;
        
//        SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
//        Session s = holder.getSession(); 
//        s.flush();
//        TransactionSynchronizationManager.unbindResource(sessionFactory);
//        SessionFactoryUtils.closeSessionIfNecessary(s, sessionFactory);

    }

}
