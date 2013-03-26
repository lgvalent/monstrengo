package br.com.orionsoft.monstrengo.core.test;

import br.com.orionsoft.monstrengo.core.test.ApplicationBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.EntityManager;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;

public class EntityBasicTest extends ApplicationBasicTest
{
    protected IEntityManager entityManager;

//    public static void main(String[] args)
//    {
//        junit.textui.TestRunner.run(EntityBasicTest.class);
//    }

    public void setUp() throws Exception
    {
        super.setUp();
        System.out.println("setup EntityBasic");
        entityManager = (IEntityManager) ctx.getBean(EntityManager.MANAGER_NAME);

    }

    public void tearDown() throws Exception
    {
        super.tearDown();
        System.out.println("tearDown EntityBasic");
        entityManager = null;
    }
    
}
