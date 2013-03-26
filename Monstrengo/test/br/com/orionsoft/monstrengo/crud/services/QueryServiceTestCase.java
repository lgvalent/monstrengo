package br.com.orionsoft.monstrengo.crud.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditRegister;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.OrderCondiction;
import br.com.orionsoft.monstrengo.crud.services.QueryCondiction;
import br.com.orionsoft.monstrengo.crud.services.QueryService;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationModule;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.RightCrud;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;

public class QueryServiceTestCase extends ServiceBasicTest
{
    
//    public static void main(String[] args)
//    {
//        junit.textui.TestRunner.run(QueryServiceTestCase.class);
//    }

    /**
     * Este teste verifica se a pesquisa retorna nenhum elemento se um filtro
     * String for passado e a entidade tiver somente propriedades numéricas. Ou seja, 
     * o filtro não será aplicado a nenhuma entidade e o pesquisa incluirá uma 
     * condição OR FALSE=TRUE para indicar que o filtro passado nao 
     * foi aproveitado
     * 
     *  A pesquisa não pode resultar nenhuma entidade.
     *
     */
    public void testFiltroString()
    {
        try
        {
            String filter = "str";
        	
            ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, RightCrud.class);
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_FILTER, filter);
            this.serviceManager.execute(sd);
            
            IEntityList<RightCrud> collection = sd.getFirstOutput();
            for(IEntity<RightCrud> ent: collection){
            	
            	UtilsTest.showEntityProperties(ent);
            }
            
            Assert.assertTrue(sd.getMessageList().size()==0);
            Assert.assertFalse(collection.size()>0);
        }catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
        
    }

    public void testRun()
    {
        try
        {
            String filter = "user";
        	
            ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, AuditRegister.class);
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_FILTER, filter);
            this.serviceManager.execute(sd);
            
            IEntityList<AuditRegister> collection = sd.getFirstOutput();
            for(IEntity<AuditRegister> ent: collection){
            	
            	UtilsTest.showEntityProperties(ent);
            }
            
            Assert.assertTrue(sd.getMessageList().size()==0);
            Assert.assertTrue(collection.size()>0);
        }catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
        
    }

    public void testQueryParent()
    {
        try
        {
//            String filter = "Ob";
        	
            ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, ApplicationEntity.class);
            sd.getArgumentList().setProperty(QueryService.IN_PARENT_CLASS_OPT, ApplicationModule.class);
            sd.getArgumentList().setProperty(QueryService.IN_PARENT_ID_OPT, new Long(1));
            sd.getArgumentList().setProperty(QueryService.IN_PARENT_PROPERTY_OPT, "entities");
//            sd.getArgumentList().setProperty(QueryService.IN_QUERY_FILTER, filter);
            this.serviceManager.execute(sd);
            
            IEntityList<ApplicationEntity> collection = sd.getFirstOutput();
            for(IEntity<ApplicationEntity> ent: collection){
            	
            	UtilsTest.showEntityProperties(ent);
            }
            
            Assert.assertTrue(sd.getMessageList().size()==0);
            Assert.assertTrue(collection.size()>0);
        }catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
        
    }

    public void testQueryFilterEmpty()
    {
        try
        {
            String filter = "";
        	
            ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, AuditRegister.class);
//            sd.getArgumentList().setProperty(QueryService.IN_PARENT_CLASS_OPT, ApplicationModule.class);
//            sd.getArgumentList().setProperty(QueryService.IN_PARENT_ID_OPT, new Long(1));
//            sd.getArgumentList().setProperty(QueryService.IN_PARENT_PROPERTY_OPT, "entities");
//            sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT, IDAO.ENTITY_ALIAS_HQL + "." + ApplicationEntity.APPLICATION_MODULE + ".name DESC");
            sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, 2);
            sd.getArgumentList().setProperty(QueryService.IN_FIRST_RESULT_OPT, 0);
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_FILTER, filter);
            this.serviceManager.execute(sd);
            
            IEntityList<AuditRegister> collection = sd.getFirstOutput();
            for(IEntity<AuditRegister> ent: collection){
            	
            	UtilsTest.showEntityProperties(ent);
            }
            System.out.println("Total de items da lista:" + sd.getOutputData(QueryService.OUT_LIST_SIZE));
            System.out.println("Duração da consulta (ms):" + sd.getOutputData(QueryService.OUT_QUERY_TIME));
            Assert.assertTrue(sd.getMessageList().size()==0);
            Assert.assertTrue(collection.size()>0);
        }catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
        
    }

    @Test
    public void testQueryCondictions()
    {
        try
        {
            List<QueryCondiction>  conds = new ArrayList<QueryCondiction> ();
            
            QueryCondiction cond = new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.OCURRENCY_DATE, Operator.BETWEEN, "01/01/2006", "31/12/2007");
            cond.setOpenPar(true);
            conds.add(cond);

            conds.add(new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.APPLICATION_USER, Operator.MORE_EQUAL, "1", ""));
            conds.add(new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.APPLICATION_USER, Operator.BETWEEN, "1", "1"));
            conds.add(new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.APPLICATION_USER, Operator.NOT_BETWEEN, "2", "5"));
            conds.add(new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.APPLICATION_USER, Operator.NOT_NULL, "", ""));

            cond = new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.TERMINAL, Operator.NULL, "", "");
            cond.setClosePar(true);
            conds.add(cond);

            cond = new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.OCURRENCY_DATE, Operator.BETWEEN, "25/01/2006", "30/01/2006");
            cond.setInitOperator(QueryCondiction.INIT_OR);
            conds.add(cond);
            
            ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, AuditRegister.class);
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, conds);
//            sd.getArgumentList().setProperty(QueryService.IN_PARENT_CLASS_OPT, ApplicationModule.class);
//            sd.getArgumentList().setProperty(QueryService.IN_PARENT_ID_OPT, new Long(1));
//            sd.getArgumentList().setProperty(QueryService.IN_PARENT_PROPERTY_OPT, "entities");
//            sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT, IDAO.ENTITY_ALIAS_HQL + "." + ApplicationEntity.APPLICATION_MODULE + ".name DESC");
            sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, 10);
            sd.getArgumentList().setProperty(QueryService.IN_FIRST_RESULT_OPT, 0);
//            sd.getArgumentList().setProperty(QueryService.IN_QUERY_FILTER, filter);
            this.serviceManager.execute(sd);
            
            IEntityList<AuditRegister> collection = sd.getFirstOutput();
            for(IEntity<AuditRegister> ent: collection){
            	
            	UtilsTest.showEntityProperties(ent);
            }
            System.out.println("Total de items da lista:" + sd.getOutputData(QueryService.OUT_LIST_SIZE));
            System.out.println("Duração da consulta (ms):" + sd.getOutputData(QueryService.OUT_QUERY_TIME));
            Assert.assertTrue(sd.getMessageList().size()==0);
            Assert.assertTrue(collection.size()>0);
            
            System.out.println("Condições:=============================================");
            for(QueryCondiction cond_: conds)
            	System.out.println(cond_.toString());

        }catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.fail(e.getMessage());
        }
        
    }

    public void testQueryHqlWhere()
    {
        try
        {
            ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, AuditRegister.class);
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE, "entity.applicationUser=1");
            sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, 10);
            sd.getArgumentList().setProperty(QueryService.IN_FIRST_RESULT_OPT, 0);
            this.serviceManager.execute(sd);
            
            IEntityList<AuditRegister> collection = sd.getFirstOutput();
            for(IEntity<AuditRegister> ent: collection){
            	
            	UtilsTest.showEntityProperties(ent);
            }
            System.out.println("Total de items da lista:" + sd.getOutputData(QueryService.OUT_LIST_SIZE));
            System.out.println("Duração da consulta (ms):" + sd.getOutputData(QueryService.OUT_QUERY_TIME));
            Assert.assertTrue(sd.getMessageList().size()==0);
            Assert.assertTrue(collection.size()>0);
        }catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
        
    }

    public void testQueryFilterCondictions()
    {
        try
        {
        	List<QueryCondiction>  conds = new ArrayList<QueryCondiction> ();
            
            QueryCondiction cond = new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.OCURRENCY_DATE, Operator.BETWEEN, "01/01/2006", "31/12/2007");
            cond.setOpenPar(true);
            conds.add(cond);

            conds.add(new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.APPLICATION_USER, Operator.MORE_EQUAL, "1", ""));
            conds.add(new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.APPLICATION_USER, Operator.BETWEEN, "1", "1"));
            conds.add(new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.APPLICATION_USER, Operator.NOT_BETWEEN, "2", "5"));
            conds.add(new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.APPLICATION_USER, Operator.NOT_NULL, "", ""));

            cond = new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.TERMINAL, Operator.NULL, "", "");
            cond.setClosePar(true);
            conds.add(cond);

            cond = new QueryCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.OCURRENCY_DATE, Operator.BETWEEN, "01/01/2006", "31/12/2007");
            cond.setInitOperator(QueryCondiction.INIT_OR);
            conds.add(cond);
            
            ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, AuditRegister.class);
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, conds);
//            sd.getArgumentList().setProperty(QueryService.IN_PARENT_CLASS_OPT, ApplicationModule.class);
//            sd.getArgumentList().setProperty(QueryService.IN_PARENT_ID_OPT, new Long(1));
//            sd.getArgumentList().setProperty(QueryService.IN_PARENT_PROPERTY_OPT, "entities");
//            sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT, IDAO.ENTITY_ALIAS_HQL + "." + ApplicationEntity.APPLICATION_MODULE + ".name DESC");
            sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, 10);
            sd.getArgumentList().setProperty(QueryService.IN_FIRST_RESULT_OPT, 0);
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_FILTER, "nome*");
            this.serviceManager.execute(sd);
            
            IEntityList<AuditRegister> collection = sd.getFirstOutput();
            for(IEntity<AuditRegister> ent: collection){
            	
            	UtilsTest.showEntityProperties(ent);
            }
            System.out.println("Total de items da lista:" + sd.getOutputData(QueryService.OUT_LIST_SIZE));
            System.out.println("Duração da consulta (ms):" + sd.getOutputData(QueryService.OUT_QUERY_TIME));
            Assert.assertTrue(sd.getMessageList().size()==0);
            Assert.assertTrue(collection.size()>0);
            
            System.out.println("Condições:=============================================");
            for(QueryCondiction cond_: conds)
            	System.out.println(cond_.toString());

        }catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
        
    }

	public void testOrderCondictions()
	    {
	        try
	        {
	            List<OrderCondiction> conds = new ArrayList<OrderCondiction>();
	            
	            OrderCondiction cond = new OrderCondiction(this.serviceManager.getEntityManager(), AuditRegister.class, AuditRegister.OCURRENCY_DATE);
	            cond.setOrderDirection(OrderCondiction.ORDER_DESC);
	            conds.add(cond);
	
	            ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
	            sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, AuditRegister.class);
	            sd.getArgumentList().setProperty(QueryService.IN_ORDER_CONDICTIONS_OPT, conds);
	            sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, 10);
	            sd.getArgumentList().setProperty(QueryService.IN_FIRST_RESULT_OPT, 0);
	            this.serviceManager.execute(sd);
	            
	            IEntityList<AuditRegister> collection = sd.getFirstOutput();
	            for(IEntity<AuditRegister> ent: collection){
	            	
	            	UtilsTest.showEntityProperties(ent);
	            }
	            System.out.println("Total de items da lista:" + sd.getOutputData(QueryService.OUT_LIST_SIZE));
	            System.out.println("Duração da consulta (ms):" + sd.getOutputData(QueryService.OUT_QUERY_TIME));
	            Assert.assertTrue(sd.getMessageList().size()==0);
	            Assert.assertTrue(collection.size()>0);
	            
	            System.out.println("Condições:=============================================");
	            for(OrderCondiction cond_: conds)
	            	System.out.println(cond_.toString());
	
	        }catch (BusinessException e)
	        {
	            UtilsTest.showMessageList(e.getErrorList());
	            
	            Assert.assertTrue(false);
	        }
	        
	    }

	public void testOrderExpression()
	{
	    try
	    {
	        ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
	        sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, AuditRegister.class);
	        sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT, AuditRegister.OCURRENCY_DATE);
	        sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, 10);
	        sd.getArgumentList().setProperty(QueryService.IN_FIRST_RESULT_OPT, 0);
	        this.serviceManager.execute(sd);
	        
	        System.out.println("Listando em order CRESCENTE POR DATA E HORA");

            IEntityList<AuditRegister> collection = sd.getFirstOutput();
            for(IEntity<AuditRegister> ent: collection){
	        	UtilsTest.showEntityProperties(ent);
	        }

	        System.out.println("Total de items da lista:" + sd.getOutputData(QueryService.OUT_LIST_SIZE));
	        System.out.println("Duração da consulta (ms):" + sd.getOutputData(QueryService.OUT_QUERY_TIME));
	        Assert.assertTrue(sd.getMessageList().size()==0);
	        Assert.assertTrue(collection.size()>0);
	    }catch (BusinessException e)
	    {
	        UtilsTest.showMessageList(e.getErrorList());
	        
	        Assert.assertTrue(false);
	    }
	    
	}

    public void testQuerySelect()
    {
        try
        {
            ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, AuditRegister.class);
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_SELECT, "sum(entity.id), sum(entity.id/2)");
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE, "entity.applicationUser=1");
            sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, 10);
            sd.getArgumentList().setProperty(QueryService.IN_FIRST_RESULT_OPT, 0);
            this.serviceManager.execute(sd);
            
            IEntityList<AuditRegister> collection = sd.getFirstOutput();
            for(IEntity<AuditRegister> ent: collection){
            	
            	UtilsTest.showEntityProperties(ent);
            }

            List<Object[]> list = (List) sd.getOutputData(QueryService.OUT_OBJECT_LIST);
            
            for(Object[] line: list){
                String lineStr="";
                for(Object obj: line){
                    lineStr += "\t" + obj;
                    
                }
                System.out.println(lineStr);
            }

            System.out.println("Total de items da lista:" + sd.getOutputData(QueryService.OUT_LIST_SIZE));
            System.out.println("Duração da consulta (ms):" + sd.getOutputData(QueryService.OUT_QUERY_TIME));
            Assert.assertTrue(sd.getMessageList().size()==0);
            Assert.assertTrue(list.size()>0);
        }catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
        
    }

    /**
     * TEste a pesquisa em um item da propriedade do tipo coleção. Ex.:  Pessoa.enderecos 
     *
     */
    public void testQueryCondictions2()
    {
        try
        {
            List<QueryCondiction>  conds = new ArrayList<QueryCondiction> ();
            
            QueryCondiction cond = new QueryCondiction(this.serviceManager.getEntityManager(), ApplicationUser.class, ApplicationUser.SECURITY_GROUPS +  "." + SecurityGroup.RIGHTS_CRUD + ".id", Operator.LIKE, "adm", "");
            conds.add(cond);
            
            ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, ApplicationUser.class);
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, conds);
            sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, 10);
            sd.getArgumentList().setProperty(QueryService.IN_FIRST_RESULT_OPT, 0);
            this.serviceManager.execute(sd);
            
            IEntityList<ApplicationUser> collection = sd.getFirstOutput();
            for(IEntity<ApplicationUser> ent: collection){
            	
            	UtilsTest.showEntityProperties(ent);
            }
            System.out.println("Total de items da lista:" + sd.getOutputData(QueryService.OUT_LIST_SIZE));
            System.out.println("Duração da consulta (ms):" + sd.getOutputData(QueryService.OUT_QUERY_TIME));
            Assert.assertTrue(sd.getMessageList().size()==0);
            Assert.assertTrue(collection.size()>0);
            
            System.out.println("Condições:=============================================");
            for(QueryCondiction cond_: conds)
            	System.out.println(cond_.toString());

        }catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
        }
        
    }

}
