package br.com.orionsoft.monstrengo.crud.processes;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditProcessRegister;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.SqlQueryProcess;

/**
 * @author lucio 20121021
 */
public class SqlQueryProcessTestCase extends ProcessBasicTest
{
	
	public static void main(String[] args)
	{
		String sql = "ALTEsALTERelect  CREATE * \n from security_user  \n \t \\";
		Pattern p = Pattern.compile("(?i)(?s)(?m)update |alter |create |delete ");
		
		System.out.println(p.matcher(sql).find());
		System.out.println(sql.matches("(?i)(?s)(?m)^s[update|alter|create|delete][\\s\\S]*"));
		if(true)return;
		junit.textui.TestRunner.run(SqlQueryProcessTestCase.class);
	}
	
	/**
	 * Este método testa o caminho normal se todos os dados
	 * estiverem corretos.
	 */
	@Test
	public void testRunQuery()
	{
		try
		{
			System.out.println("Testando:" + this.getClass().getName());
			String sql = "create TABLE temp_luc2 \n SELECT * FROM security_user";
//			String sql = "SELECT * FROM security_user";
			//preparando processo query
			System.out.println(":Preparando o processo");
			SqlQueryProcess queryProcess = (SqlQueryProcess) processManager.createProcessByName(SqlQueryProcess.PROCESS_NAME, this.getAdminSession());
			
			System.out.println(":Mostrando histórico anterior do operador");
			for(IEntity<AuditProcessRegister> h: queryProcess.getHistoryList()){
				System.out.println("==>" + h.toString());
			}
				
			queryProcess.setSql(sql);
			
			queryProcess.runQuery();
			
			Assert.assertFalse("Alguns usuários deveriam ser exibidos", queryProcess.getRowsFetched()==0);
			
			System.out.println("Registros selecionados:" + queryProcess.getRowsFetched());
			System.out.println("Dados:" + queryProcess.getRowsFetched());
			
			for(Object[] obj: queryProcess.getResult()){
				System.out.println(queryProcess.getLabels()[1] + "=> " + obj[1]);
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
