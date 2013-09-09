package br.com.orionsoft.financeiro.view;


import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ApplicationBasicTest;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

/**
 * Esta classe é útil para a primeira execução do sistema. Ela gera o Banco e
 * cria as estruturas básicas de segurança.
 * 
 * @author lucio 20130331
 */
public class InicializarSistema extends
		br.com.orionsoft.monstrengo.core.test.ProcessBasicTest{

	@Before
	public void setUp() throws Exception {
		// Definindo o arquivo SPRING do atual projeto para o teste
		ApplicationBasicTest.APPLICATION_CONTEXT_PATH = "./WebContent/WEB-INF/applicationContext.xml";

		super.setUp();
	}

	@Test
	public void inicializar() {
		try {
			ApplicationUser ap = UtilsCrud.objectRetrieve(this.processManager.getServiceManager(), ApplicationUser.class, 1, null);
			
			System.out.println("====" + ap.getSecurityGroups().iterator().next().getRightsCrud());
			
			System.out.println("00000000000000000000=" + UtilsSecurity.checkRightQuery(this.processManager.getServiceManager(), Conta.class, this.getAdminSession(), null));
			
			
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		
//		super.gerarTabelas.createSchema(false, true, false);
//		try {
//			super.popularTabelas.popular();
//		} catch (ServiceException e) {
//			e.printStackTrace();
//		}
	}

}
