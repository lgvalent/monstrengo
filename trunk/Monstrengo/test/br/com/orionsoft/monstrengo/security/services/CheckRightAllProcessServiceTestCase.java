package br.com.orionsoft.monstrengo.security.services;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.services.CheckAllRightProcessService;

/**
 * Teste do servico CheckAllRightProcessService, que, dado um id de usuário, 
 * localiza o usuário, percorre todos os seus grupos e todos os direitos de processos .
 */
public class CheckRightAllProcessServiceTestCase extends ServiceBasicTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CheckRightAllProcessServiceTestCase.class);
    }

    public void testUserId()
    {
    	try{
    		// Buscando os processos que existem no banco
    		ServiceData sd = new ServiceData(CheckAllRightProcessService.SERVICE_NAME, null);
    		sd.getArgumentList().setProperty(CheckAllRightProcessService.IN_USER_ID_OPT, new Long(1));
    		this.serviceManager.execute(sd);
    		
    		Map<String, Boolean> rights = (Map<String, Boolean>) sd.getFirstOutput();
    		
    		System.out.println("MAPA ATUAL");
    		for(Entry ent: rights.entrySet()){
    			System.out.println(ent.getKey() + "\t:" + ent.getValue());
    		}
    		
    		
    	}catch (Exception e)
    	{
    		e.printStackTrace();
    		Assert.assertTrue(false);
    	}
    	finally{
    	}
    }

    public void testUserEntity()
    {
    	try{
    		IEntity user = UtilsCrud.retrieve(this.serviceManager, ApplicationUser.class, 1, null);
    		// Buscando os processos que existem no banco
    		ServiceData sd = new ServiceData(CheckAllRightProcessService.SERVICE_NAME, null);
    		sd.getArgumentList().setProperty(CheckAllRightProcessService.IN_USER_OPT, user);
    		this.serviceManager.execute(sd);
    		
    		Map<String, Boolean> rights = (Map<String, Boolean>) sd.getFirstOutput();
    		
    		System.out.println("MAPA ATUAL");
    		for(Entry ent: rights.entrySet()){
    			System.out.println(ent.getKey() + "\t:" + ent.getValue());
    		}
    		
    		
    	}catch (Exception e)
    	{
    		e.printStackTrace();
    		Assert.assertTrue(false);
    	}
    	finally{
    	}
    }

}
