package br.com.orionsoft.monstrengo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;

/**
 * Esta classe � �til para a primeira execu��o do sistema.
 * Ela gera o Banco e cria as estruturas b�sicas de seguran�a.
 * @author lucio 20121205 
 */
public class InicializarSistema extends ServiceBasicTest{
	
	private GerarTabelas gerarTabelas = new  GerarTabelas();
	private PopularTabelas popularTabelas = new  PopularTabelas();
	
	/**
	 * Permite que outros m�dulos implementem um popular tabelas personalizado
	 */
	public void replacePopularTabelas(PopularTabelas newPopularTabelas){
		this.popularTabelas = newPopularTabelas;
	}
	
	@Before
	public void setUp() throws Exception {
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
