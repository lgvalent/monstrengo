package br.com.orionsoft.monstrengo.core.service;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationModule;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;

/**
 * Esta classe realiza testes sobre o ServiceManager.
 * Alguns erros são simulados para testar as características Commit e Rollback.
 * Observação: Propriedades com Cascade habilitado não poderão 
 * ser testadas, pois o erro de relacionamento não persistido não
 * acontecerá. 
 * 
 * @author estagio 2006/01/02
 *
 */
public class ServiceManagerTestCase extends ServiceBasicTest
{

    public static void main(String[] args)
    {
//        junit.textui.TestRunner.run(ServiceManagerTestCase.class);
    }

    @Test
    public void testCrud()
    {
        try
        {
            System.out.println(":Cria objetos com referências não persistidas.");
            IEntity process = UtilsCrud.create(serviceManager, ApplicationProcess.class, null);

            IEntity module = UtilsCrud.create(serviceManager, ApplicationModule.class, null);
            
            process.setPropertyValue(ApplicationProcess.APPLICATION_MODULE,module);
            
            System.out.println(":Tentando persistir uma entidade com uma referência não persistida.");
            UtilsCrud.update(serviceManager, process, null);
            
            Assert.assertTrue(false);
        } catch (BusinessException e)
        {
            for(BusinessMessage er: e.getErrorList())
                System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());
            Assert.assertTrue(true);
        }
    }

//    public void testList()
//    {
//        try
//        {
//            System.out.println(":List.");
//            ServiceData svdl = new ServiceData(ListService.SERVICE_NAME, null);
//            svdl.getArgumentList().setProperty(ListService.CLASS, ApplicationUser.class);
//            serviceManager.execute(svdl);
//            IEntityCollection enl = (IEntityCollection) svdl.getOutputData(0);
//            
//            for(IEntity en: enl)
//            {
//                System.out.println("Entidade:" + en.getInfo().getLabel());
//                for(IProperty prop: en.getProperties()) 
//                    System.out.println(" " + prop.getInfo().getLabel() + "=" + prop.getValue().getAsString());
//            }
//
//            assertTrue(true);
//        } catch (BusinessException e)
//        {
//            for(BusinessMessage er: e.getErrorList())
//                System.out.println(er.getExceptionClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());
//            assertTrue(false);
//        }
//    }
}
