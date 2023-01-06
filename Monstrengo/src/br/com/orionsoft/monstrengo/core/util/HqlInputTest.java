package br.com.orionsoft.monstrengo.core.util;

import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;

/**
 * Este teste fornece um formulário para a entrada de um HQL. 
 * @author marcia
 *
 */
public class HqlInputTest extends ServiceBasicTest
{

    public static void main(String[] args)
    {
//        junit.textui.TestRunner.run(HqlInputTest.class);
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testGetListString()
    {
        String condition = "entity.id>0";
        String className = "br.com.orionsoft.monstrengo.security.entities.ApplicationUser";
        String select = "entity.name, elements(entity.securityGroups)";
        
        
        while (!condition.equals(""))
        {
            try
            {
                select = JOptionPane.showInputDialog("Entre o select:", select);
                className = JOptionPane.showInputDialog("Entre a classe:", className);
                condition = JOptionPane.showInputDialog("Entre a condição HQL:", condition);
                Query query = this.serviceManager.getEntityManager().getDaoManager().getSessionFactory().openSession().createQuery("SELECT " + select + " FROM " + className + " entity WHERE " + condition);
                List<Object[]> list = query.list();
//                List list = executeList(className, condition);

                for(Object[] row: list){
                	String result = "";
                    for(Object obj: row)
                    	result += obj.toString() + ",";
                    System.out.println("Dados: " + result);
                }
                System.out.println("Items: " + list.size());
                this.serviceManager.getEntityManager().getDaoManager().getSessionFactory().close();
                Assert.assertTrue(true);
            }catch (HibernateException e)
            {
//                UtilsTest.showMessageList(e.getErrorList());
            	e.printStackTrace();            	
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
/*
Lucio - 12/04/2007 - testando como obter os grupos dos quais um
determinado operador participa

entity.name, entity.id
br.com.orionsoft.monstrengo.security.entities.SecurityGroup
entity.id in (select r.id from RightCrud r where  r.securityGroup=entity.id)

entity.id in (SELECT g.id FROM SecurityGroup g JOIN g.users u WHERE u.id =1)

 */
		
//    private List executeList(String entityClassName, String conditions) throws BusinessException
//    {
//
//        IDAO dao = this.daoManager.getDaoByEntity(entityClassName); 
//        
//        return dao.getList(conditions);
//        
//    }

}
