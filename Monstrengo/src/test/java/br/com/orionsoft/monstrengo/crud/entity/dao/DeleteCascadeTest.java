package br.com.orionsoft.monstrengo.crud.entity.dao;

import br.com.orionsoft.monstrengo.core.test.DaoBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;

public class DeleteCascadeTest extends DaoBasicTest
{

//    public static void main(String[] args)
//    {
//        junit.textui.TestRunner.run(DeleteCascadeTest.class);
//    }

    public void setUp() throws Exception
    {
        super.setUp();
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testCascade() throws Exception
    {
        IDAO groupDao = this.daoManager.getDaoByEntity(SecurityGroup.class);
//        SecurityGroup group = (SecurityGroup)groupDao.create();
//        group.setName("testeCascade");
//        groupDao.update(group);
//        
//        IDAO procDao = this.daoManager.getDaoByEntity(ApplicationProcess.class);
//        
//        IDAO rightDao = this.daoManager.getDaoByEntity(RightProcess.class);
//        RightProcess rp = (RightProcess)rightDao.create();
//        rp.setSecurityGroup(group);
//        rp.setExecuteAllowed(true);
//        rp.setApplicationProcess((ApplicationProcess)procDao.retrieve(1));
//        rightDao.update(rp);
//        RightProcess rp = (RightProcess)rightDao.retrieve(9);
//        rightDao.delete(rp);
        
        SecurityGroup group = (SecurityGroup)groupDao.retrieve(6);
        System.out.println(group.getName());
        groupDao.delete(group);
        
//        
//        List lista = rightDao.getList(IDAO.ENTITY_ALIAS_HQL + "." + RightProcess.APPLICATION_PROCESS + "=" + 1);
//        System.out.println(lista.size());
//        IDAO ufDao = this.daoManager.getDaoByEntity(Uf.class);
//        Uf uf = (Uf) ufDao.create();
//        uf.setNome("Parana");
//        uf.setSigla("PR");
//        ufDao.update(uf);
//        
//        Uf uf2 = (Uf) ufDao.create();
//        uf2.setNome("Sao Paulo");
//        uf2.setSigla("SP");
//        ufDao.update(uf2);
//        
//        IDAO municipioDao = this.daoManager.getDaoByEntity(Municipio.class);
//        Municipio municipio = (Municipio)municipioDao.create();
//        municipio.setNome("Maringá");
//        municipio.setUf(uf);
//        municipioDao.update(municipio);
//        
//        Municipio municipio2 = (Municipio)municipioDao.create();
//        municipio2.setNome("Marialva");
//        municipio2.setUf(uf);
//        municipioDao.update(municipio2);
//
//        Municipio municipio3 = (Municipio)municipioDao.create();
//        municipio3.setNome("São Paulo");
//        municipio3.setUf(uf2);
//        municipioDao.update(municipio3);
//
//        Municipio municipio4 = (Municipio)municipioDao.create();
//        municipio4.setNome("Sao Carlos");
//        municipio4.setUf(uf2);
//        municipioDao.update(municipio4);
//        
//        ufDao.delete(uf);
//        ufDao.update(uf);
    }
}
