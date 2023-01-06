package br.com.orionsoft.monstrengo.crud.support;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.EntityManagerTestCase;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabel;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.crud.support.CrudExpression;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;


public class CrudExpressionTestCase extends ServiceBasicTest{
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(EntityManagerTestCase.class);
	}
	
	@Test
	public void testCrudExpression(){
		try
		{
			IEntity entity = UtilsCrud.retrieve(this.serviceManager, ApplicationUser.class, 1, null);
			Map<String, IEntity> mapEntities = new HashMap<String, IEntity>();
			mapEntities.put(entity.getInfo().getType().getSimpleName(), entity);

			/* é preciso adicionar ao mapa apenas as entidades em que não é especificado um id */
			IEntity<ModelLabel> modelLabel = UtilsCrud.create(this.serviceManager, ModelLabel.class, null);
			modelLabel.getObject().setPageHeight(210);
			mapEntities.put(modelLabel.getInfo().getType().getSimpleName(), modelLabel);
			
			
			String expression = "#{ApplicationUser[?].name}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{ApplicationUser[1].name}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{ApplicationUser[?].securityGroups}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{ApplicationUser[?].securityGroups[0]}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{ApplicationUser[?].securityGroups[1]}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{ApplicationUser[?].securityGroups[0].name}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{ApplicationUser[?].securityGroups[0].blabla}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{now()}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{now(30)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{now(-30)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{now(dd/MMMM/yy,30)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{now(dd 'de' MMMM 'de' yyyy, -30)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{extensiveNumber(10)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{extensiveMoney(10)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{extensiveNumber(203,47)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{extensiveMoney(203,47)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{extensiveNumber(295.300,50)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{extensiveMoney(295.300,50)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
//			expression = "#{extensiveNumber(7.3)}";
//			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
//			
//			expression = "#{extensiveMoney(7.3)}";
//			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{ApplicationUser[1].id}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{extensiveNumber(ApplicationUser[1].id)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{extensiveMoney(ApplicationUser[1].id)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{ModelLabel[?].pageHeight}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{extensiveNumber(ModelLabel[?].pageHeight)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{extensiveMoney(ModelLabel[1].pageHeight)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{format(ModelDocumentEntity[1].date, dd/MM)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{format(ModelDocumentEntity[1].date, dd/MM/yy)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{format(ModelDocumentEntity[1].date, dd/MMMM/yyyy)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{format(ModelDocumentEntity[1].date, dd 'de' MMMM 'de' yyyy)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
			
			expression = "#{format(ModelLabel[1].pageHeight, %,.4f)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));

			expression = "#{format(ModelLabel[1].id, %010.0f)}";
			System.out.println("==> " + CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));


			try  //função nao implementada
			{
				expression = "#{noww()}";
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(false);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(true);
			}
			
			try  //função encerrada incorretamente
			{
				expression = "#{now(}";
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(false);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(true);
			}
			
			try  //expressão encerrada incorretamente
			{
				expression = "#{now()";
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(false);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(true);
			}
			
			try  //Id da expressão encerrado incorretamente
			{
				expression = "#{ApplicationUser[?.name}";
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(false);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(true);
			}
			
			try  //id numérico inválido
			{
				expression = "#{ApplicationUser[-?].name}";
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(false);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(true);
			}
			
			try  //expressão iniciada incorretamente.
			{
				expression = "-{ApplicationUser[?].name}";
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(false);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(true);
			}
			
			try  //entidade desconhecida no sistema
			{
				expression = "#{AppplicationUser[?].name}";
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(false);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(true);
			}
			
			try  //entidade desconhecida no sistema
			{
				expression = "#{AppplicationUser[1].name}";
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(false);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(true);
			}
			
			try  //expressao terminada incorretamente
			{
				expression = "#{ApplicationUser[1].name";
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(false);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(true);
			}
			
			try  //função nao implementada
			{
				expression = "#{extensiveNumberr(10)}";
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(false);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(true);
			}
			
			try  //numero inválido
			{
				expression = "#{extensiveNumber(10,00.)}"; // Ele limpas os pontos e subtitui a virgula por ponto . CERTO
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(true);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(false);
			}
			
			try  //entidade nao pertence ao mapa
			{
				expression = "#{extensiveNumber(ModelLabell[1].pageHeight)}";
				System.out.println(CrudExpression.expressionToValue(expression, mapEntities, this.serviceManager.getEntityManager()));
				Assert.assertTrue(false);
			} catch (BusinessException e)
			{
				UtilsTest.showMessageList(e.getErrorList());
				Assert.assertTrue(true);
			}
			
		} catch (BusinessException e)
		{
			UtilsTest.showMessageList(e.getErrorList());
			Assert.fail(e.getMessage());
		}
	}
	
}
