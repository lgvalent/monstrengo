package br.com.orionsoft.financeiro.view;


import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ApplicationBasicTest;

/**
 * Esta classe é útil para a primeira execução do sistema. Ela gera o Banco e
 * cria as estruturas básicas de segurança.
 * 
 * @author lucio 20130331
 */
public class InicializarSistema extends
		br.com.orionsoft.monstrengo.InicializarSistema {

	@Before
	public void setUp() throws Exception {
		// Definindo o arquivo SPRING do atual projeto para o teste
		ApplicationBasicTest.APPLICATION_CONTEXT_PATH = "./WebContent/WEB-INF/applicationContext.xml";

		super.setUp();
	}

	@Test
	public void inicializar() {
		super.gerarTabelas.createSchema(false, true, false);
		try {
			super.popularTabelas.popular();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

}
