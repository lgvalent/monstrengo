package br.com.orionsoft.monstrengo.security.services;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;
import br.com.orionsoft.monstrengo.security.entities.RightProcess;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;
import br.com.orionsoft.monstrengo.security.services.DefineRightProcessService;

/**
 * Testa as permissões de Processo a partir de um ServiceData e verifica se as mesmas permissões 
 * que foram setadas estão gravadas no banco.
 */
public class DefineRightProcessServiceTestCase extends ServiceBasicTest
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(DefineRightProcessServiceTestCase.class);
    }

    public void testExecute()
    {
        IEntity group=null;

        try
        {
            
            // Cria um group
            group = UtilsCrud.create(this.serviceManager, SecurityGroup.class, null);
            UtilsCrud.update(this.serviceManager, group, null);
            
            IEntityCollection processCol = UtilsCrud.list(this.serviceManager, ApplicationProcess.class, null);
            IEntity process = processCol.getFirst();
            
            // Define o direiro como PERMITIDO
            ServiceData sd = new ServiceData(DefineRightProcessService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(DefineRightProcessService.GROUP_ID, group.getId());
            sd.getArgumentList().setProperty(DefineRightProcessService.PROCESS_ID, process.getId());
            sd.getArgumentList().setProperty(DefineRightProcessService.EXECUTE_ALLOWED, true);
            this.serviceManager.execute(sd);
            
            // Verifica se está gravado PERMITIDO
            ServiceData sl = new ServiceData(ListService.SERVICE_NAME, null);
            sl.getArgumentList().setProperty(ListService.CLASS, RightProcess.class);
            sl.getArgumentList().setProperty(ListService.CONDITION_OPT_STR,  IDAO.ENTITY_ALIAS_HQL + "." + RightProcess.SECURITY_GROUP + ".id=" + group.getId() + " and " + IDAO.ENTITY_ALIAS_HQL + "." + RightProcess.APPLICATION_PROCESS + ".id=" + process.getId());
            this.serviceManager.execute(sl);
            
            IEntity rightProcess = ((IEntityCollection) sl.getFirstOutput()).getFirst();
            Assert.assertTrue(rightProcess.getProperty(RightProcess.EXECUTE_ALLOWED).getValue().getAsBoolean());
            
            // Define o direiro como NAO PERMITIDO
            sd = new ServiceData(DefineRightProcessService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(DefineRightProcessService.GROUP_ID, group.getId());
            sd.getArgumentList().setProperty(DefineRightProcessService.PROCESS_ID, process.getId());
            sd.getArgumentList().setProperty(DefineRightProcessService.EXECUTE_ALLOWED, false);
            this.serviceManager.execute(sd);
            
            // Verifica se está gravado NAO PERMITIDO
            sl = new ServiceData(ListService.SERVICE_NAME, null);
            sl.getArgumentList().setProperty(ListService.CLASS, RightProcess.class);
            sl.getArgumentList().setProperty(ListService.CONDITION_OPT_STR,  IDAO.ENTITY_ALIAS_HQL + "." + RightProcess.SECURITY_GROUP + ".id=" + group.getId() + " and " + IDAO.ENTITY_ALIAS_HQL + "." + RightProcess.APPLICATION_PROCESS + ".id=" + process.getId());
            this.serviceManager.execute(sl);
            
            rightProcess = ((IEntityCollection) sl.getFirstOutput()).getFirst();
            Assert.assertFalse(rightProcess.getProperty(RightProcess.EXECUTE_ALLOWED).getValue().getAsBoolean());
            
        }
        catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());

            Assert.assertTrue(false);
        }
        //não é usado o UtilsCrud.delete() pois ocorrem erros devido à dependência
//        finally
//        {
//            try
//            {
//                UtilsCrud.delete(this.serviceManager, group);
//            } catch (BusinessException e)
//            {
//                UtilsTest.showMessageList(e.getErrorList());
//            }
//        }
    }

}
