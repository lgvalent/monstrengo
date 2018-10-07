package br.com.orionsoft.monstrengo.crud.support;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.EntityManagerTestCase;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.crud.support.DocumentParserCrudExpression;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;


public class DocumentParserTestCase extends ServiceBasicTest{
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(EntityManagerTestCase.class);
	}
	
	@Test
	public void testParseString(){
		try
		{
			IEntity entity = UtilsCrud.retrieve(this.serviceManager, ApplicationUser.class, 1, null);
			Map<String, IEntity> mapEntities = new HashMap<String, IEntity>();
			mapEntities.put(entity.getInfo().getType().getSimpleName(), entity);
			
			String str = "#{ApplicationUser[?].id} - #{ApplicationUser[?].name}";
			System.out.println("==> " + DocumentParserCrudExpression.parseString(str, mapEntities, this.serviceManager.getEntityManager()));
			
			str = "Olá Sr.(a) #{ApplicationUser[?].name}, seu identificador é #{ApplicationUser[?].id}.";
			System.out.println("==> " + DocumentParserCrudExpression.parseString(str, mapEntities, this.serviceManager.getEntityManager()));

			str = "Olá Sr.(a) #{ApplicationUser[?].name}, seu identificador está correto.";
			System.out.println("==> " + DocumentParserCrudExpression.parseString(str, mapEntities, this.serviceManager.getEntityManager()));

			str = "Olá Sr.(a).";
			System.out.println("==> " + DocumentParserCrudExpression.parseString(str, mapEntities, this.serviceManager.getEntityManager()));

			str = "";
			System.out.println("==> " + DocumentParserCrudExpression.parseString(str, mapEntities, this.serviceManager.getEntityManager()));

			str = "Olá Sr.(a) #{ApplicationUser[?].name}, hoje é dia #{now(dd 'de' MMMM 'de' yyyy)}.";
			System.out.println("==> " + DocumentParserCrudExpression.parseString(str, mapEntities, this.serviceManager.getEntityManager()));

			/*	Expressão nao finalizada, porem interpreta todo o resto "name, seu identificador é #{ApplicationUser[?]"
			 *  como o nome de uma propriedade não encontrada, entao retorna uma string vazia e dá um erro silencioso  (log.info)
			 *  de propriedade nao encontrada */
			str = "Olá Sr.(a) #{ApplicationUser[?].name}, seu identificador é #{ApplicationUser[?].id}.";
			System.out.println(DocumentParserCrudExpression.parseString(str, mapEntities, this.serviceManager.getEntityManager()));
			
			/*	Testa a recursividade de expressões #{ApplicationUser[ApplicationGroup[1].id]}" */
			str = "Olá Sr.(a) #{ApplicationUser[SecurityGroup[1].id].name}.";
			System.out.println(DocumentParserCrudExpression.parseString(str, mapEntities, this.serviceManager.getEntityManager()));
			
		} catch (BusinessException e)
		{
			UtilsTest.showMessageList(e.getErrorList());
			Assert.assertTrue(false);
		}
	}
	
	public void testParseDocument(){
		try
		{
			IEntity entity = UtilsCrud.retrieve(this.serviceManager, ApplicationUser.class, 1, null);
			Map<String, IEntity> mapEntities = new HashMap<String, IEntity>();
			mapEntities.put(entity.getInfo().getType().getSimpleName(), entity);
			
			String str = "#{ApplicationUser[?].id} - \n #{ApplicationUser[?].name}";
//			System.out.println("==> " + str + "\n==>"+ DocumentParserCrudExpression.parseDocument(new ByteArrayInputStream(str.getBytes()), mapEntities, this.serviceManager.getEntityManager()));

			str = "Olá Sr.(a) #{ApplicationUser[?].name}, seu identificador é #{ApplicationUser[?].id}.";
			System.out.println("==> " + str + "\n==>"+ DocumentParserCrudExpression.parseDocument(new ByteArrayInputStream(str.getBytes()), mapEntities, this.serviceManager.getEntityManager()));

			str = "Olá Sr.(a) #{ApplicationUser[?].name}, seu identificador está correto.";
			System.out.println("==> " + str + "\n==>"+ DocumentParserCrudExpression.parseDocument(new ByteArrayInputStream(str.getBytes()), mapEntities, this.serviceManager.getEntityManager()));

			str = "Olá Sr.(a).";
			System.out.println("==> " + str + "\n==>"+ DocumentParserCrudExpression.parseDocument(new ByteArrayInputStream(str.getBytes()), mapEntities, this.serviceManager.getEntityManager()));

			str = "";
			System.out.println("==> " + DocumentParserCrudExpression.parseDocument(new ByteArrayInputStream(str.getBytes()), mapEntities, this.serviceManager.getEntityManager()));

			str = "Olá Sr.(a) #{ApplicationUser[?].name}, hoje é dia #{now(dd 'de' MMMM 'de' yyyy)}.";
			System.out.println("==> " + DocumentParserCrudExpression.parseDocument(new ByteArrayInputStream(str.getBytes()), mapEntities, this.serviceManager.getEntityManager()));

		} catch (BusinessException e)
		{
			UtilsTest.showMessageList(e.getErrorList());
			Assert.assertTrue(false);
		}
	}
}
