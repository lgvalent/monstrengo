package br.com.orionsoft.monstrengo.crud.processes;

import javax.faces.model.SelectItem;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditRegister;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.QueryProcess;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;
import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.OrderCondiction;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationModule;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * @author lucio
 */
public class QueryProcessTestCase extends ProcessBasicTest
{
	
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(QueryProcessTestCase.class);
	}
	
	/**
	 * Este método testa o caminho normal se todos os dados
	 * estiverem corretos.
	 */
	@Test
	public void testCondictions()
	{
		try
		{
			System.out.println("Testando:" + this.getClass().getName());
			
			//preparando processo query
			System.out.println(":Preparando o processo");
			QueryProcess queryProcess = (QueryProcess) processManager.createProcessByName(QueryProcess.PROCESS_NAME, this.getAdminSession());
			queryProcess.setEntityType(ApplicationEntity.class);
			
			queryProcess.getUserReport().getPageParam().setPageSize(2);
			queryProcess.getUserReport().getPageParam().setPage(1);
			
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setPropertyPath(queryProcess.getUserReport().getCondictionParam().getListPropertyPath().get(3).getValue().toString());
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setOperatorId(Operator.EQUAL);
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setValue1(Integer.toString(1));
			queryProcess.getUserReport().getCondictionParam().addNewCondiction();
			
			for(SelectItem item: queryProcess.getUserReport().getCondictionParam().getListPropertyPath()){
				System.out.println(item.getValue() + ":" + item.getLabel());
			}
			
			queryProcess.runQuery();
			
			Assert.assertEquals(queryProcess.getUserReport().getPageParam().getPage(), 1);
			
			if(queryProcess.getMessageList().size() > 0)
				UtilsTest.showMessageList(queryProcess.getMessageList());
			
			System.out.println("Registros selecionados:" + queryProcess.getUserReport().getEntityCollection().getSize());
			
			for(IEntity ent: queryProcess.getUserReport().getEntityCollection()){
				UtilsTest.showEntityProperties(ent);
			}
			
			Assert.assertTrue(true);
		} 
		catch (BusinessException e)
		{
			UtilsTest.showMessageList(e.getErrorList());
			
			Assert.assertTrue(false);
		}
	}
	
	/**
	 * Este método testa o caminho normal se todos os dados
	 * estiverem corretos.
	 */
	@Test
	public void testCondictionsCalculated()
	{
		try
		{
			System.out.println("Testando:" + this.getClass().getName());
			
			//preparando processo query
			System.out.println(":Preparando o processo");
			QueryProcess queryProcess = (QueryProcess) processManager.createProcessByName(QueryProcess.PROCESS_NAME, this.getAdminSession());
			queryProcess.setEntityType(AuditRegister.class);
			
			queryProcess.getUserReport().getPageParam().setPageSize(5);
			queryProcess.getUserReport().getPageParam().setPage(1);
			
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setPropertyPath("applicationUser.id");
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setOperatorId(Operator.EQUAL);
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setValue1(Integer.toString(1));
			queryProcess.getUserReport().getCondictionParam().addNewCondiction();
			
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setPropertyPath("asCrudRegister.id");
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setOperatorId(Operator.EQUAL);
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setValue1(Integer.toString(1));
			queryProcess.getUserReport().getCondictionParam().addNewCondiction();
			
			
			for(SelectItem item: queryProcess.getUserReport().getCondictionParam().getListPropertyPath()){
				System.out.println(item.getValue() + ":" + item.getLabel());
			}
			
			queryProcess.runQuery();
			
			Assert.assertEquals(queryProcess.getUserReport().getPageParam().getPage(), 1);
			
			if(queryProcess.getMessageList().size() > 0)
				UtilsTest.showMessageList(queryProcess.getMessageList());
			
			System.out.println("Registros selecionados:" + queryProcess.getUserReport().getEntityCollection().getSize());
			
			for(IEntity ent: queryProcess.getUserReport().getEntityCollection()){
				UtilsTest.showEntityProperties(ent);
			}
			
			Assert.assertTrue(true);
		} 
		catch (BusinessException e)
		{
			UtilsTest.showMessageList(e.getErrorList());
			
			Assert.assertTrue(false);
		}
	}
	
	/**
	 * Este método testa o caminho normal se todos os dados
	 * estiverem corretos.
	 */
	public void testRunQuery()
	{
		try
		{
			System.out.println("Testando:" + this.getClass().getName());
			
			String filter = "1";
			
			//preparando processo query
			System.out.println(":Preparando o processo");
			QueryProcess queryProcess = (QueryProcess) processManager.createProcessByName(QueryProcess.PROCESS_NAME, this.getAdminSession());
			queryProcess.setEntityType(ApplicationEntity.class);
			
			queryProcess.getUserReport().getPageParam().setPageSize(2);
			queryProcess.getUserReport().getPageParam().setPage(1);
			
			queryProcess.getUserReport().getParentParam().setType(ApplicationModule.class);
			queryProcess.getUserReport().getParentParam().setId(new Long(1));
			queryProcess.getUserReport().getParentParam().setProperty("entities");
			
			queryProcess.getUserReport().getFilterParam().setFilter(filter);
			
			queryProcess.runQuery();
			
			Assert.assertEquals(queryProcess.getUserReport().getPageParam().getPage(), 1);
			
			if(queryProcess.getMessageList().size() > 0)
				UtilsTest.showMessageList(queryProcess.getMessageList());
			
			System.out.println("Registros selecionados:" + queryProcess.getUserReport().getEntityCollection().getSize());
			
			for(IEntity ent: queryProcess.getUserReport().getEntityCollection()){
				UtilsTest.showEntityProperties(ent);
			}
			
			System.out.println("Mostrando entidade PAI das entidades acima:");
			UtilsTest.showEntityProperties(queryProcess.getUserReport().retrieveParentEntity());
			
			
			Assert.assertTrue(true);
		} 
		catch (BusinessException e)
		{
			UtilsTest.showMessageList(e.getErrorList());
			
			Assert.assertTrue(false);
		}
	}
	
	/**
	 * Este método testa o a utilização de condições 
	 * de ordenação.
	 */
	public void testOrderCondictionsBuildResult()
	{
		try
		{
			System.out.println("Testando:" + this.getClass().getName());
			
			String filter = "1";
			
			//preparando processo query
			System.out.println(":Preparando o processo");
			QueryProcess queryProcess = (QueryProcess) processManager.createProcessByName(QueryProcess.PROCESS_NAME, this.getAdminSession());
			queryProcess.setEntityType(ApplicationEntity.class);
			
			queryProcess.getUserReport().getPageParam().setPageSize(3);
			queryProcess.getUserReport().getPageParam().setPage(0);
			
			queryProcess.getUserReport().getParentParam().setType(ApplicationModule.class);
			queryProcess.getUserReport().getParentParam().setId(new Long(1));
			queryProcess.getUserReport().getParentParam().setProperty("entities");
			
			queryProcess.getUserReport().getOrderParam().getNewCondiction().setPropertyPath(ApplicationEntity.NAME);
			queryProcess.getUserReport().getOrderParam().getNewCondiction().setOrderDirection(OrderCondiction.ORDER_DESC);
			queryProcess.getUserReport().getOrderParam().addNewCondiction();
			
			queryProcess.getUserReport().getFilterParam().setFilter(filter);
			
			queryProcess.runQuery();
			
			Assert.assertEquals(queryProcess.getUserReport().getPageParam().getPage(), 1);
			
			if(queryProcess.getMessageList().size() > 0)
				UtilsTest.showMessageList(queryProcess.getMessageList());
			
			System.out.println("Registros selecionados:" + queryProcess.getUserReport().getPageParam().getItemsCount());
			
			for(IEntity ent: queryProcess.getUserReport().getEntityCollection()){
				UtilsTest.showEntityProperties(ent);
			}
			
			System.out.println("Mostrando entidade PAI das entidades acima:");
			UtilsTest.showEntityProperties(queryProcess.getUserReport().retrieveParentEntity());
			
			
			Assert.assertTrue(true);
			
			System.out.println("Selecionando o resultado");
			queryProcess.getUserReport().getResultParam().getCondictions().get(0).setVisible(true);
			queryProcess.getUserReport().getResultParam().getCondictions().get(1).setVisible(true);
			
			String[][] result = queryProcess.getUserReport().getBuildResult();
			
			System.out.println("Mostrando o resultado");
			System.out.println(queryProcess.getUserReport().getResultParam().getCondictions().get(0).getPropertyPathLabel() + "\t" + queryProcess.getUserReport().getResultParam().getCondictions().get(1).getPropertyPathLabel());
			for(int r=0; r< result.length;r++){
				String line = "";
				for(int c=0;c<result[r].length;c++)
					line += result[r][c] + "\t";
				
				System.out.println(line);
			}
					
				
		} 
		catch (BusinessException e)
		{
			UtilsTest.showMessageList(e.getErrorList());
			
			Assert.assertTrue(false);
		}
	}

	/**
	 * Este método testa o caminho normal se todos os dados
	 * estiverem corretos.
	 */
	public void testSaveRestore()
	{
		try
		{
			System.out.println("Testando:" + this.getClass().getName());
			
			//preparando processo query
			System.out.println(":Preparando o processo");
			QueryProcess queryProcess = (QueryProcess) processManager.createProcessByName(QueryProcess.PROCESS_NAME, this.getAdminSession());
			queryProcess.setEntityType(ApplicationEntity.class);
			
			queryProcess.getUserReport().getPageParam().setPageSize(2);
			queryProcess.getUserReport().getPageParam().setPage(1);
			
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setPropertyPath(queryProcess.getUserReport().getCondictionParam().getListPropertyPath().get(1).getValue().toString());
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setOperatorId(Operator.EQUAL);
			queryProcess.getUserReport().getCondictionParam().getNewCondiction().setValue1(Integer.toString(1));
			queryProcess.getUserReport().getCondictionParam().addNewCondiction();
			
			for(SelectItem item: queryProcess.getUserReport().getCondictionParam().getListPropertyPath()){
				System.out.println(item.getValue() + ":" + item.getLabel());
			}
			
			queryProcess.runQuery();
			
			queryProcess.getUserReport().getUserReportBean().setName("My first saved report");
			queryProcess.getUserReport().saveReport();
			
			queryProcess.getUserReport().restoreReport(5);

//			queryProcess.getUserReport().deleteReport();

			queryProcess.runQuery();
			
			Assert.assertEquals(queryProcess.getUserReport().getUserReportBean().getName(),"My first saved report");
			
			Assert.assertEquals(queryProcess.getUserReport().getPageParam().getPage(), 1);
			
			if(queryProcess.getMessageList().size() > 0)
				UtilsTest.showMessageList(queryProcess.getMessageList());
			
			System.out.println("Registros selecionados:" + queryProcess.getUserReport().getEntityCollection().getSize());
			
			for(IEntity ent: queryProcess.getUserReport().getEntityCollection()){
				UtilsTest.showEntityProperties(ent);
			}
			
			Assert.assertTrue(true);
		} 
		catch (BusinessException e)
		{
			UtilsTest.showMessageList(e.getErrorList());
			
			Assert.assertTrue(false);
		}
	}

	/**
	 * Este método testa o caminho normal se todos os dados
	 * estiverem corretos.
	 */
	public void testListReport()
	{
		try
		{
			System.out.println("Mostrando a lista de relatórios disponíveis");
			
			for(IEntity entity: UserReport.listUserReport(this.processManager.getServiceManager()))
				UtilsTest.showEntityProperties(entity);

			for(IEntity entity: UserReport.listUserReportByEntity(this.processManager.getServiceManager(), ApplicationUser.class))
				UtilsTest.showEntityProperties(entity);

			for(IEntity entity: UserReport.listUserReportByUser(this.processManager.getServiceManager(), 1))
				UtilsTest.showEntityProperties(entity);

			for(IEntity entity: UserReport.listUserReportByEntityAndUser(this.processManager.getServiceManager(), ApplicationUser.class, 1))
				UtilsTest.showEntityProperties(entity);

			Assert.assertTrue(true);
		} 
		catch (BusinessException e)
		{
			UtilsTest.showMessageList(e.getErrorList());
			
			Assert.assertTrue(false);
		}
	}
}
