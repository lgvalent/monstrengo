package br.com.orionsoft.monstrengo.core.process;

import java.util.Calendar;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcessController;
import br.com.orionsoft.monstrengo.core.process.RunnableProcessEntry;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;


/**
 * Teste que obtem a lista de TODOS os processos registrados no Spring através da execução de um serviço.
 */
public class ProcessManagerTestCase extends ProcessBasicTest
{

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(ProcessManagerTestCase.class);
	}

	/**
	 * Teste de 07/06/2007 14:53
	 * 834:Mostrando todos os processo que o operador tem direito de executar para uma determinada entidades.
	 * 993:Exibindo...
	 * ===================================================
	 * Entidade: Processo do sistema
	 * 0:
	 * Id=22
	 * Nome=OverwritePasswordProcess
	 * Módulo=br.com.orionsoft.monstrengo
	 * Description=Este processo permite a alteração da senha de um operador sem exigir a senha atual. É útil para o administrador do sistema alterar a senha de um operador que perdeu ou esqueceu sua senha, mas que precisa renová-la.
	 * Hint=Este processo permite a alteração da senha de um operador sem exigir a senha atual
	 * Label=Alterar senha de acesso de um operador
	 * RunnableEntities=[Operador do sistema]
	 * 997:FIM. 
956
20
160

841:Mostrando todos os processo que o operador tem direito de executar para uma determinada entidades.
841
896
901
901ms:Exibindo...

	 */
	@Test
	public void testGetRunnableProcess()
	{
		try
		{
			IEntityList<?> list = UtilsCrud.list(this.processManager.getServiceManager(),
					ApplicationUser.class,
					null);

			int miliStart = Calendar.getInstance().get(Calendar.MILLISECOND);
			System.out.println(miliStart + ":Mostrando todos os processo que o operador tem direito de executar para uma determinada entidades.");
			for(RunnableProcessEntry runnableProcess: this.processManager.getRunnableEntityProcessesEntry(list.getFirst(), this.getAdminSession())){ 
				System.out.println(Calendar.getInstance().get(Calendar.MILLISECOND) + "ms:Exibindo...");
				System.out.println("Ficha do processo:" + runnableProcess.getProcessClass().getName());
				System.out.println("            label:" + runnableProcess.getInfo().label());
				System.out.println("             hint:" + runnableProcess.getInfo().hint());
				System.out.println("            label:" + runnableProcess.getInfo().label());
				System.out.println("       HABILITADO:" + (runnableProcess.isDisabled()?"[ ]":"[X]"));
				System.out.println("         mensagem:" + (runnableProcess.getMessage()));
			}
			System.out.println(Calendar.getInstance().get(Calendar.MILLISECOND) +  "ms:FIM.");

			Assert.assertTrue(true);
		} 
		catch (BusinessException e)
		{
			for(BusinessMessage er: e.getErrorList())
				System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());

			Assert.assertTrue(false);
		} 
		catch (Exception e)
		{

			System.out.println(e.getMessage());
			e.printStackTrace();

			Assert.assertTrue(false);

		}
	}


	public void testControllers() {
		try{
			System.out.println("Mostrando os controladores RunnableEntityProcess");
			for(Entry<String, IRunnableEntityProcessController> controllerEntry: this.processManager.getControllers().entrySet()){
				System.out.println(controllerEntry.getKey() + "=>");
				for(Class<?> klazz: controllerEntry.getValue().getRunnableEntities())
					System.out.println("       =>" + klazz.getName());
			}

			Assert.assertTrue(true);

			/* Testando se o mapa de controladores é realmente READ ONLY */
			this.processManager.getControllers().put("oi", (IRunnableEntityProcessController) this.processManager.getControllers().values().toArray()[0]);
			Assert.assertTrue(false);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

}