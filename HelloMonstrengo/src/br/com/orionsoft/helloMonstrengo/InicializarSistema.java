package br.com.orionsoft.helloMonstrengo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.GerarTabelas;
import br.com.orionsoft.monstrengo.PopularTabelas;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ApplicationBasicTest;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;

/**
 * Esta classe é útil para a primeira execução do sistema.
 * Ela gera o Banco e cria as estruturas básicas de segurança.
 * @author lucio 20121205 
 */
public class InicializarSistema extends ServiceBasicTest{
	
	protected GerarTabelas gerarTabelas = new  GerarTabelas();
	protected PopularTabelas popularTabelas = new  PopularTabelas();
	
	/**
	 * Permite que outros módulos implementem um popular tabelas personalizado
	 */
	public void replacePopularTabelas(PopularTabelas newPopularTabelas){
		this.popularTabelas = newPopularTabelas;
	}
	
	@Before
	public void setUp() throws Exception {
		// Lucio 20150223: Personaliza o application context que geralmente � pego do diret�rio corrente da aplica�ao 
		ApplicationBasicTest.APPLICATION_CONTEXT_PATH = "./WebContent/WEB-INF/applicationContext.xml";

		super.setUp();
		gerarTabelas.setUp();
		popularTabelas.setUp();
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		gerarTabelas.tearDown();
		popularTabelas.tearDown();
	}

	@Test
	public void inicializar(){
		this.gerarTabelas.createSchema(false, true, false);
		try {
			this.popularTabelas.popular();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

	}

}
