package br.com.orionsoft.monstrengo.crud.entity.dao;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.test.DaoBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.dao.DAOException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Esta classe realiza testes em todos os Daos registrados.
 * <p>
 * Utilizando o <b>Dao Manager</b>, a lista de Daos é obtida.
 * <p>
 * Para cada Dao, os métodos <b>Create</b>, <b>Update</b>, <b>Retrieve</b> e
 * <b>Delete</b> são executados.
 * <p>
 * Para preencher um objeto de forma genérica são obtidas suas propriedades do
 * tipo String e é atribuído um valor padrão.
 * 
 * @author estagio
 * 
 */
public class DaoTestCase extends DaoBasicTest {

	// Para alterar o número de Objetos a serem testados, mudar esta constante
	private final int NUMERO_TESTES = 2;

	// public static void main(String[] args) {
	// junit.textui.TestRunner.run(DaoBasicTestCase.class);
	// }

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testDao() {
		try {
			IDAO<ApplicationUser> dao = this.daoManager.getDaoByEntity(ApplicationUser.class);

			ApplicationUser user = dao.create();
			user.setLogin("CREATE");
			user.setPassword("CREATE");
			dao.update(user);
			org.junit.Assert.assertTrue("Um ID deve ser gerado pela persistência!",	user.getId() != IDAO.ENTITY_UNSAVED);
			
			user.setName("UPDATE");
			dao.update(user);
			ApplicationUser newUser = dao.retrieve(user.getId());
			org.junit.Assert.assertEquals("O nome deveria estar UPDATE!",	user.getName(), newUser.getName());
			
			dao.delete(user);
			List<ApplicationUser> users = dao.getList();
			for(ApplicationUser u: users){
				System.out.println("Analisando " + u.getId() + " - " + u.getLogin());
				org.junit.Assert.assertTrue("O usuário deveria ter sido removido do banco!", u.getId() != user.getId());
			}
			
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}