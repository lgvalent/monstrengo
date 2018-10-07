package br.com.orionsoft.basico.cadastro.pessoa;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.QueryProcess;

/**
 * @author lucio
 */
public class QueryProcessTestCase extends ProcessBasicTest {

	/**
	 * Este método testa o caminho normal se todos os dados estiverem corretos.
	 * 
	 * @throws ServiceException
	 */
	@Test
	public void testRunQuery() throws ServiceException {

		try
		{
			System.out.println("Testando:" + this.getClass().getName());

			String filter = "023323";

			//preparando processo query
			System.out.println(":Preparando o processo");
			QueryProcess queryProcess = (QueryProcess)
			processManager.createProcessByName(QueryProcess.PROCESS_NAME,
					this.getAdminSession());
			queryProcess.setEntityType(Contrato.class);
			// queryProcess.setParentType(ApplicationModule.class);
			// queryProcess.setParentId(new Long(1));
			// queryProcess.setParentProperty("entities");

			queryProcess.getUserReport().getPageParam().setPageSize(2);
			queryProcess.getUserReport().getPageParam().setPage(1);

			queryProcess.getUserReport().getOrderParam().setOrderExpression("id DESC");

			queryProcess.getUserReport().getFilterParam().setFilter(filter);

			queryProcess.runQuery();

			Assert.assertEquals(queryProcess.getUserReport().getPageParam().getPage(), 1);

			if(queryProcess.getMessageList().size() > 0)
				UtilsTest.showMessageList(queryProcess.getMessageList());

			System.out.println("Registros selecionados:" +
					queryProcess.getUserReport().getPageParam().getItemsCount());

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
}
