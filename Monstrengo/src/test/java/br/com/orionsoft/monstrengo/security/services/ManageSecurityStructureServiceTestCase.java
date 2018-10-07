package br.com.orionsoft.monstrengo.security.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.services.GetCrudEntitiesService;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationModule;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;
import br.com.orionsoft.monstrengo.security.services.ManageSecurityStructureService;

/**
 * Esta classe testa os métodos manageEntitiesSecurity() e manageProcessSecurity(), na chamada do 
 * método execute da classe <b>ManageSecurityStructureService</b>.
 * <p><b>Procedimento de testRunEntities:</b>
 * <br>Obtém a Lista de Entidades do Sistema através de <code>GetCrudEntitiesService</code>;
 * <br>Obtém a Lista de Entidades do Banco através de <code>ListService</code>;
 * <br>Compara as duas listas de Entidades.
 * <p><b>Procedimento de testRunProcess:</b>
 * <br>Obtém a Lista de Processos do Sistema através de <code>GetAllProcessService</code>;
 * <br>Obtém a Lista de Processos do Banco através de <code>ListService</code>;
 * <br>Compara as duas listas de Processos.
 *  
 * @author estagio
 * @version 2005/10/27
 *
 */
public class ManageSecurityStructureServiceTestCase extends ProcessBasicTest{

	/**
	 * @param args
	 */
	public static void main (String[] args) {
		junit.textui.TestRunner.run(ManageSecurityStructureServiceTestCase.class);
	}			
		
	@SuppressWarnings("unchecked")
    @Test
	public void testRunAll(){
		try {
			
////////////Teste de Entidades//////////////////////////////////////////////////////////////////////////
			
			//realiza comparações com o Banco de Dados 
			ServiceData service = new ServiceData(ManageSecurityStructureService.SERVICE_NAME, null);
            service.getArgumentList().setProperty(ManageSecurityStructureService.IN_RESTORE_DEFAULT_OPT, new Boolean(true));
            this.processManager.getServiceManager().execute(service);
            
			//lista de todas as Entidades do Sistema
			ServiceData serviceData = new ServiceData(GetCrudEntitiesService.SERVICE_NAME, null);
			this.processManager.getServiceManager().execute(serviceData);
			
			List<IEntityMetadata> entityList = (List)serviceData.getOutputData(0);
			System.out.println("\nEntidades do Sistema - " + entityList.size());
			
			//modulesMap armazena os Módulos das Entidades
			Map<String, Object> modulesMap = new HashMap<String, Object>();
			for (IEntityMetadata entityInfo : entityList){
				   System.out.println(entityInfo);
				   modulesMap.put(this.processManager.getServiceManager().getApplication().extractModuleName(entityInfo.getType()), null);
			}
			
			//lista de todas as Entidades cadastradas no Banco
			ServiceData svcData = new ServiceData(ListService.SERVICE_NAME, null);
        	svcData.getArgumentList().setProperty(ListService.CLASS, ApplicationEntity.class);
        	this.processManager.getServiceManager().execute(svcData);
        	
        	IEntityCollection<ApplicationEntity> bdList = svcData.getFirstOutput();
        	System.out.println("\nEntidades no Banco - " + bdList.size());
        	for (IEntity<ApplicationEntity> bdClass : bdList){
        		System.out.println(bdClass.getObject());
        	}
        	
            //Verifica se as listas possuem o mesmo tamanho
            Assert.assertTrue(entityList.size() <= bdList.size());
            
            //UtilsCrud é uma classe criada para facilitar os serviços CRUD (ja faz os services)
            IEntityCollection enl = UtilsCrud.list(this.processManager.getServiceManager(), ApplicationModule.class, null);
            
        	System.out.println("\nModulo Entidade - Memoria - " + modulesMap.size());
        	System.out.println("Modulo Entidade - Banco - " + enl.size());        	

            //verifica o número de Módulos de Entidades no Sistema com o do Banco
        	Assert.assertTrue(modulesMap.size() <= enl.size());
        	
	
////////////Teste de Processos//////////////////////////////////////////////////////////////////////////
        	
        	
			//realiza comparações com o Banco de Dados 
			service = new ServiceData(ManageSecurityStructureService.SERVICE_NAME, null);
            service.getArgumentList().setProperty(ManageSecurityStructureService.IN_RESTORE_DEFAULT_OPT, new Boolean(true));
            service.getArgumentList().setProperty(ManageSecurityStructureService.IN_PROCESS_MANAGER, this.processManager);
            this.processManager.getServiceManager().execute(service);
            
			//lista de todos os Processos do Sistema
			Collection<Class<? extends IProcess>> processList = this.processManager.getAllProcessesClasses();
			System.out.println("\nProcessos do Sistema - " + processList.size());

			//utiliza o mesmo modulesMap da Entidade para armazenar os Módulos dos Processos
			for (Class processClass : processList){
				   System.out.println(processClass);
				   modulesMap.put(this.processManager.getServiceManager().getApplication().extractModuleName(processClass), null);
			}
			
			//lista de todos os Processos cadastrados no Banco
			svcData = new ServiceData(ListService.SERVICE_NAME, null);
        	svcData.getArgumentList().setProperty(ListService.CLASS, ApplicationProcess.class);
        	this.processManager.getServiceManager().execute(svcData);
        	
        	bdList = (IEntityCollection)svcData.getOutputData(0);
        	System.out.println("\nProcessos no Banco - " + bdList.size());
        	for (IEntity bdClass : bdList){
        		System.out.println(bdClass);
        	}
        	
            //Verifica se as listas dos Processos possuem o mesmo tamanho
            Assert.assertTrue(processList.size() <= bdList.size());
            
            //UtilsCrud é uma classe criada para facilitar os serviços CRUD (ja faz os services)
            IEntityCollection prcl = UtilsCrud.list(this.processManager.getServiceManager(), ApplicationModule.class, null);
            
        	System.out.println("\nModulo Processo - Memoria - " + modulesMap.size());
        	System.out.println("Modulo Processo - Banco - " + prcl.size());        	

            //verifica o número de Módulos de Processos no Sistema com o do Banco
        	Assert.assertTrue(modulesMap.size() <= prcl.size());
			
		} catch (Exception e) {

            e.printStackTrace();
            
            Assert.assertTrue(false);
		}
		
	}
}
