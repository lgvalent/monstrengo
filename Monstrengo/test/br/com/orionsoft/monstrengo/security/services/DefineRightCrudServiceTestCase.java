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
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.RightCrud;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;
import br.com.orionsoft.monstrengo.security.services.DefineRightCrudService;

/**
 * Testa as permissões de CRUD a partir de um ServiceData e verifica se as mesmas permissões 
 * que foram setadas estão gravadas no banco.
 *
 */
public class DefineRightCrudServiceTestCase extends ServiceBasicTest
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(DefineRightCrudServiceTestCase.class);
    }

    public void testExecute()
    {
        IEntity group=null;

        try
        {
            // Cria um group
            group = UtilsCrud.create(this.serviceManager, SecurityGroup.class, null);
            UtilsCrud.update(this.serviceManager, group, null);
            
            IEntityCollection entityCol = UtilsCrud.list(this.serviceManager, ApplicationEntity.class, null);
            IEntity entity = entityCol.getFirst();
            
            // Testa as permissões alternadamente true, false, true, false
            ServiceData sd = new ServiceData(DefineRightCrudService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(DefineRightCrudService.GROUP_ID, group.getId());
            sd.getArgumentList().setProperty(DefineRightCrudService.ENTITY_ID, entity.getId());
            sd.getArgumentList().setProperty(DefineRightCrudService.CREATE_ALLOWED, true);
            sd.getArgumentList().setProperty(DefineRightCrudService.RETRIEVE_ALLOWED, false);
            sd.getArgumentList().setProperty(DefineRightCrudService.UPDATE_ALLOWED, true);
            sd.getArgumentList().setProperty(DefineRightCrudService.DELETE_ALLOWED, false);
            this.serviceManager.execute(sd);
            
            // Verifica se está gravado alternadamente true, false, true, false
            ServiceData sl = new ServiceData(ListService.SERVICE_NAME, null);
            sl.getArgumentList().setProperty(ListService.CLASS, RightCrud.class);
            sl.getArgumentList().setProperty(ListService.CONDITION_OPT_STR,  IDAO.ENTITY_ALIAS_HQL + "." + RightCrud.SECURITY_GROUP + ".id=" + group.getId() + " and " + IDAO.ENTITY_ALIAS_HQL + "." + RightCrud.APPLICATION_ENTITY + ".id=" + entity.getId());
            this.serviceManager.execute(sl);
            
            IEntity rightCrud = ((IEntityCollection) sl.getFirstOutput()).getFirst();
            Assert.assertTrue(rightCrud.getProperty(RightCrud.CREATE_ALLOWED).getValue().getAsBoolean());
            Assert.assertFalse(rightCrud.getProperty(RightCrud.RETRIEVE_ALLOWED).getValue().getAsBoolean());
            Assert.assertTrue(rightCrud.getProperty(RightCrud.UPDATE_ALLOWED).getValue().getAsBoolean());
            Assert.assertFalse(rightCrud.getProperty(RightCrud.DELETE_ALLOWED).getValue().getAsBoolean());
            
            // Testa as permissões alternadamente false, true, false, true
            sd = new ServiceData(DefineRightCrudService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(DefineRightCrudService.GROUP_ID, group.getId());
            sd.getArgumentList().setProperty(DefineRightCrudService.ENTITY_ID, entity.getId());
            sd.getArgumentList().setProperty(DefineRightCrudService.CREATE_ALLOWED, false);
            sd.getArgumentList().setProperty(DefineRightCrudService.RETRIEVE_ALLOWED, true);
            sd.getArgumentList().setProperty(DefineRightCrudService.UPDATE_ALLOWED, false);
            sd.getArgumentList().setProperty(DefineRightCrudService.DELETE_ALLOWED, true);
            this.serviceManager.execute(sd);
            
            // Verifica se está gravado alternadamente false, true, false, true
            sl = new ServiceData(ListService.SERVICE_NAME, null);
            sl.getArgumentList().setProperty(ListService.CLASS, RightCrud.class);
            sl.getArgumentList().setProperty(ListService.CONDITION_OPT_STR,  IDAO.ENTITY_ALIAS_HQL + "." + RightCrud.SECURITY_GROUP + ".id=" + group.getId() + " and " + IDAO.ENTITY_ALIAS_HQL + "." + RightCrud.APPLICATION_ENTITY + ".id=" + entity.getId());
            this.serviceManager.execute(sl);
            
            rightCrud = ((IEntityCollection) sl.getFirstOutput()).getFirst();
            Assert.assertFalse(rightCrud.getProperty(RightCrud.CREATE_ALLOWED).getValue().getAsBoolean());
            Assert.assertTrue(rightCrud.getProperty(RightCrud.RETRIEVE_ALLOWED).getValue().getAsBoolean());
            Assert.assertFalse(rightCrud.getProperty(RightCrud.UPDATE_ALLOWED).getValue().getAsBoolean());
            Assert.assertTrue(rightCrud.getProperty(RightCrud.DELETE_ALLOWED).getValue().getAsBoolean());
            
        }
        catch (BusinessException e)
        {
            UtilsTest.showMessageList(e.getErrorList());

            Assert.assertTrue(false);
        }
//        finally
//        {
//            try
//            {
//                //UtilsCrud.delete(this.serviceManager, group);
//            } catch (BusinessException e)
//            {
//                UtilsTest.showMessageList(e.getErrorList());
//            }
//        }
    }

}
