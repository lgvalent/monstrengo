package br.com.orionsoft.basic.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;


public class AlterarSenhaPessoaServiceTestCase extends ServiceBasicTest{

	@Test	
	
	public void testRun(){
		try {
			
			String inNumeroDocumento = "00736581979";
			String inSenha = "teste";
			String inCodigo = "123";
			String inSenhaNova = "orion";
			
			//realiza comparações com o Banco de Dados 
			ServiceData service = new ServiceData(AlterarSenhaPessoaService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(AlterarSenhaPessoaService.IN_NUMERO_DOCUMENTO, inNumeroDocumento);
			service.getArgumentList().setProperty(AlterarSenhaPessoaService.IN_SENHA, inSenha);
			service.getArgumentList().setProperty(AlterarSenhaPessoaService.IN_CODIGO_SEGURANCA_OPT, inCodigo);
			service.getArgumentList().setProperty(AlterarSenhaPessoaService.IN_NOVA_SENHA, inSenhaNova);
			           
			this.serviceManager.execute(service);
            		
		} catch (BusinessException e) {

            UtilsTest.showMessageList(e.getErrorList());
            
            Assert.assertTrue(false);
		}
		

	}

}
