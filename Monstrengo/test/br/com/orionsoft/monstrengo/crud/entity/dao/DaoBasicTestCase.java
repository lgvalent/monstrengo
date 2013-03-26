package br.com.orionsoft.monstrengo.crud.entity.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.test.DaoBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Esta classe realiza testes em todos os Daos registrados.
 * <p>Utilizando o <b>Dao Manager</b>, a lista de Daos � obtida.
 * <p>Para cada Dao, os m�todos <b>Create</b>, <b>Update</b>, <b>Retrieve</b> e <b>Delete</b> s�o executados.
 * <p>Para preencher um objeto de forma gen�rica s�o obtidas suas propriedades do tipo
 * String e � atribu�do um valor padr�o.  
 * 
 * @author estagio
 *
 */
public class DaoBasicTestCase extends DaoBasicTest {

	//Para alterar o n�mero de Objetos a serem testados, mudar esta constante 
	private final int NUMERO_TESTES = 2;  
	
//	public static void main(String[] args) {
//		junit.textui.TestRunner.run(DaoBasicTestCase.class);
//	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testGenericDao (){
		Field[] fields;
		Object objDao;
		int count = 0;
		
		for(Object obj: daoManager.getSessionFactory().getAllClassMetadata().values()){
			SingleTableEntityPersister c = (SingleTableEntityPersister) obj;
			System.out.println("=====>" + c.getEntityType().getReturnedClass());
		}
										
		// Obt�m a lista de DAOs e percorre a lista de DAOs
		for (IDAO dao : daoManager.getDaos().values()){
			
			try {
				//Obt�m a classe correspondente ao DAO 
				Class classDAO = dao.getEntityClass();
				System.out.println("Class - " + classDAO);
				
				//Obt�m os campos de uma classe em um array
				fields = classDAO.getDeclaredFields(); 
				
				//N�o instancia objetos de classe abstrata
				if (!Modifier.isAbstract(dao.getEntityClass().getModifiers())){
					
					List listaObjetos = new ArrayList(); //Lista dos Objetos que ser�o criados
					//cria determinado n�mero de Objetos e os testa
					for (int i = 0; i < NUMERO_TESTES; i++){
					
						//Cria objeto do tipo Object
						objDao = dao.create(); 
				
						//Percorre o vetor de campos 
						for (int j = 0; j < fields.length; j++){
							System.out.println("Fields - " + fields[j].getName());
							System.out.println("Type - " + fields[j].getType());
						
							/*
							 * Se o campo atual for do tipo String e n�o for uma CONSTANTE,
							 * populo (preencher) os campos do Objeto do tipo String com 
							 * uma string de "Teste" atrav�s do PropertyUtils.setProperty()
							 */
							if ((fields[j].getType() == java.lang.String.class) && 
								(!java.lang.reflect.Modifier.isStatic(fields[j].getModifiers()))){
							
								System.out.println("STRING - " + fields[j].getName());

								/*
								 * Usando dao.getEntityClass().cast(objDao)), passo o retorno como par�metro direto,
								 * assim n�o atribuo o resultado a uma vari�vel Object, usando um objeto do tipo da classe 
								 * que realmente queria (com o cast())  
								 */
								PropertyUtils.setProperty(dao.getEntityClass().cast(objDao), fields[j].getName(), "Teste" + i);
									
							}//if
						
						}//for
					
						/*
						 * Ap�s popular o Objeto com as Strings de Teste, 
						 * o Objeto � armazenado na Lista
						 */
//						listaObjetos.add(i, objDao);
						
					}//for
					
					//M�todo de Testes
					testDAOs(listaObjetos, dao);
				
				}//if
					count++;
					System.out.println();
							
				} catch (Exception e) {
					e.printStackTrace();

					Assert.assertTrue(false);
				}
			
			}//for
		
			System.out.println("Total de DAOs Testados - " + count); 
			
	} //fim de testA
	
	/**
	 * Este M�todo Testa os m�todos <b>update()</b>, <b>retrieve()</b>, <b>getList()</b>, 
	 * <b>getList("String condi��o")</b> e <b>delete()</b> da classe DaoBasic.
	 * <p>O m�todo retrieve() � testado implicitamente na chamada de update() e delete().
	 * <p>Os Objetos a serem testados est�o armazenados na Lista que passa como par�metro, 
	 * assim, as itera��es s�o feitas a partir do tamanho desta Lista.
	 */
	private void testDAOs(List listaObj, IDAO _dao){
		/*
		 * Como a classe � gen�rica, n�o sabemos o DAO que est� passando pelo teste, assim, 
		 * obtemos o id atrav�s do PropertyUtils.getProperty(), passando como par�metro 
		 * a classe do Objeto e uma String "id" para identificar o m�todo que queremos usar 
		 */
		Object _objDao;
		Long _id;
		List<Long> idBanco = new ArrayList<Long>(); //Lista que armazena os IDs adicionados no update 
		
		try {
			//update e retrieve
			for (int k = 0; k < listaObj.size(); k++){
				_objDao = listaObj.get(k);
				_dao.update(_objDao);
				_id = (Long)PropertyUtils.getProperty(_dao.getEntityClass().cast(_objDao), "id");				
				Assert.assertNotNull(_dao.retrieve(_id));
				idBanco.add(k, _id);
			}
			
			//getList()
			/*
			 * A Lista retornada pelo Banco de Dados (listaBanco) pode ser maior ou igual � Lista de Objetos
			 * criada no programa, caso contr�rio existe um erro 
			 */
			List listaBanco = _dao.getList();
			Assert.assertTrue(listaBanco.size() >= listaObj.size());
			listaBanco.clear(); //limpa a lista de objetos, para que possa ser usada novamente
			
			//getList(String condi��o)
			/*
			 * Obtenho apenas os Objetos que se correspondem ao par�metro que ser� passado, e a Lista 
			 * retornada pelo Banco de Dados (listaBanco) agora deve ser a mesma que foi inserida
			 * anteriormente com o comando update()
			 */
			String nomeClasse = _dao.getEntityClass().getSimpleName();
			for (int k = 0; k < listaObj.size(); k++){
//				listaBanco.add(k,_dao.getList("id = " + idBanco.get(k)));
				System.out.println("getList Teste - " + "from " + nomeClasse + " where id = " +idBanco.get(k));
			}
			Assert.assertTrue(listaBanco.size() == listaObj.size());
			
			//delete e retrieve
			/*
			 * Para o delete, uso os IDs armazenados na lista (idBanco) para excluir apenas os 
			 * Objetos que foram inseridos pelo update() 
			 */
            try{
			for (int k = 0; k < listaBanco.size(); k++){
                _dao.delete(_dao.retrieve(idBanco.get(k)));
//				assertNull(_dao.retrieve(idBanco.get(k)));		
			}
            }catch(Exception e){
                System.err.println("ocorreu uma exce��o no delete!");

                Assert.assertTrue(false);
            }
			
		} catch (Exception e) {
			e.printStackTrace();

			Assert.assertTrue(false);
		}
	}

}	
	
