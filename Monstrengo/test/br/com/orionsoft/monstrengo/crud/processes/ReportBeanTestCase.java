package br.com.orionsoft.monstrengo.crud.processes;

import java.util.List;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.QueryProcess;
import br.com.orionsoft.monstrengo.crud.report.entities.QueryCondictionBean;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReportBean;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

public class ReportBeanTestCase extends ProcessBasicTest
{

	private QueryProcess queryProcess; 
	
//    public static void main(String[] args)
//    {
//        junit.textui.TestRunner.run(ReportBeanTestCase.class);
//    }
    
    @Override
    public void setUp() throws Exception, BusinessException {
    	super.setUp();
    	
    	queryProcess = (QueryProcess) this.processManager.createProcessByName(QueryProcess.PROCESS_NAME, this.getAdminSession());
    }
    
    public void testBuildReport(){
    	try {
			queryProcess.setEntityType(ApplicationUser.class);
			queryProcess.getUserReport().runQuery();
			
	    	String[][] result = queryProcess.getUserReport().getBuildResult2();
	    	
	    	for(String[] row: result){
	    		String out = "";
	    		for(String col: row)
	    			out += col + ", ";
	    		
	    		System.out.println(out);
	    	}

    	} catch (ProcessException e) {
			e.printStackTrace();
		}
    	
    	
    }

    public void testCreate()
    {
        try
        {	
        	/* Teste do orderIndex no OrderCondiction */
//        	System.out.println(":Testando instanciação de classe abstrata");
//    		IEntity userReportEntity = UtilsCrud.create(this.processManager.getServiceManager(), UserReportBean.class, null);
//    		List orders =  ((UserReportBean) userReportEntity.getObject()).getOrderCondictions();
//    		
//    		IEntity orderBean1 =  UtilsCrud.create(this.processManager.getServiceManager(), OrderCondictionBean.class, null);
//    		orderBean1.setPropertyValue(OrderCondictionBean.PROPERTY_PATH, "Bean1");
//    		IEntity orderBean2 =  UtilsCrud.create(this.processManager.getServiceManager(), OrderCondictionBean.class, null);
//    		orderBean2.setPropertyValue(OrderCondictionBean.PROPERTY_PATH, "Bean2");
//    		IEntity orderBean3 =  UtilsCrud.create(this.processManager.getServiceManager(), OrderCondictionBean.class, null);
//    		orderBean3.setPropertyValue(OrderCondictionBean.PROPERTY_PATH, "Bean3");
//    		
//    		orders.add(orderBean1.getObject());
//    		orders.add(orderBean2.getObject());
//    		orders.add(orderBean3.getObject());
//
//    		System.out.println(orders);
//    		
//    		UtilsCrud.update(this.processManager.getServiceManager(), userReportEntity, null);
//
//    		System.out.println(orders);
//
//    		userReportEntity = UtilsCrud.retrieve(this.processManager.getServiceManager(), UserReportBean.class, userReportEntity.getId(), null);
//    		
//    		orders =  ((UserReportBean) userReportEntity.getObject()).getOrderCondictions();
//    		System.out.println(orders);
//    		
//    		orders.remove(1);
//    		UtilsCrud.update(this.processManager.getServiceManager(), userReportEntity, null);
//    		userReportEntity = UtilsCrud.retrieve(this.processManager.getServiceManager(), UserReportBean.class, userReportEntity.getId(), null);
//    		orders =  ((UserReportBean) userReportEntity.getObject()).getOrderCondictions();
//    		System.out.println(orders);
//
//            Assert.assertTrue(true);
        	
        	/* Teste do orderIndex no QueryCondiction */
        	System.out.println(":Testando instanciação de classe abstrata");
    		IEntity userReportEntity = UtilsCrud.create(this.processManager.getServiceManager(), UserReportBean.class, null);
    		List querys =  ((br.com.orionsoft.monstrengo.crud.report.entities.UserReportBean) userReportEntity.getObject()).getQueryCondictions();
    		
    		IEntity queryBean1 =  UtilsCrud.create(this.processManager.getServiceManager(), QueryCondictionBean.class, null);
    		queryBean1.setPropertyValue(QueryCondictionBean.PROPERTY_PATH, "Bean1");
    		IEntity queryBean2 =  UtilsCrud.create(this.processManager.getServiceManager(), QueryCondictionBean.class, null);
    		queryBean2.setPropertyValue(QueryCondictionBean.PROPERTY_PATH, "Bean2");
    		IEntity queryBean3 =  UtilsCrud.create(this.processManager.getServiceManager(), QueryCondictionBean.class, null);
    		queryBean3.setPropertyValue(QueryCondictionBean.PROPERTY_PATH, "Bean3");
    		
    		querys.add(queryBean1.getObject());
    		querys.add(queryBean2.getObject());
    		querys.add(queryBean3.getObject());

    		System.out.println(querys);
    		
    		UtilsCrud.update(this.processManager.getServiceManager(), userReportEntity, null);

    		System.out.println(querys);

    		userReportEntity = UtilsCrud.retrieve(this.processManager.getServiceManager(), UserReportBean.class, userReportEntity.getId(), null);
    		
    		querys =  ((UserReportBean) userReportEntity.getObject()).getQueryCondictions();
    		System.out.println(querys);
    		
//    		querys.remove(1);
//    		UtilsCrud.update(this.processManager.getServiceManager(), userReportEntity, null);
//    		userReportEntity = UtilsCrud.retrieve(this.processManager.getServiceManager(), UserReportBean.class, userReportEntity.getId(), null);
//    		querys =  ((UserReportBean) userReportEntity.getObject()).getQueryCondictions();
//    		System.out.println(querys);

            Assert.assertTrue(true);

        } catch (BusinessException e)
        {
        	UtilsTest.showMessageList(e.getErrorList());
        	Assert.assertTrue(false);
        }
    }

}
