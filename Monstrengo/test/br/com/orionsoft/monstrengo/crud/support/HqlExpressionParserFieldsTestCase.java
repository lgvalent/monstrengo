package br.com.orionsoft.monstrengo.crud.support;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.EntityManagerTestCase;
import br.com.orionsoft.monstrengo.crud.support.HqlExpressionParserFields;
import br.com.orionsoft.monstrengo.crud.support.HqlExpressionParserFields.HqlExpressionField;


public class HqlExpressionParserFieldsTestCase extends ServiceBasicTest{
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(EntityManagerTestCase.class);
	}
	
	public void testHqlExpressionParserFields(){
		try
		{
			/* Teste apenas para conferir se os parametros estão sendo obtidos de maneira correta */
			String expression = "@{Calendar , Data de inicio , 21/10/2007}";
			System.out.println("==> " + HqlExpressionParserFields.findFields(expression));
			
//			expression = "@{Calendar , Data de inicio , 21/10/2007}";
//			System.out.println("==> " + HqlExpressionParserFields.findFields(expression));
			
			Map<String, HqlExpressionField> map = new HashMap<String, HqlExpressionField>();
			map.put("Data de inicio", new HqlExpressionField(Calendar.class, "Data de inicio", "21/10/2007"));

			for(Entry<String, HqlExpressionField> entry: map.entrySet())
				entry.getValue().setValue("15/10/2007");
			
			expression = "@{Calendar , Data de inicio , 21/10/2007}";
			System.out.println("==> RESULTADO " + HqlExpressionParserFields.replaceFields(expression, map));
			
			expression = "@{Calendar , Data de inicio , 15/10/2007}";
			System.out.println("==> RESULTADO " + HqlExpressionParserFields.replaceFields(expression, map));
			
			map.put("Id Item de Custo", new HqlExpressionField(Double.class, "Id Item de Custo", "0"));

			for(Entry<String, HqlExpressionField> entry: map.entrySet())
				entry.getValue().setValue("5");
			
			expression = "@{Number, Id Item de Custo, 0}";
			System.out.println("==> RESULTADO " + HqlExpressionParserFields.replaceFields(expression, map));
			
			
			
		} catch (BusinessException e)
		{
			UtilsTest.showMessageList(e.getErrorList());
			Assert.assertTrue(false);
		}
	}
	
}
