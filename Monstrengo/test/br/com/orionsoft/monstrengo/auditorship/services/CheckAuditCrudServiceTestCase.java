package br.com.orionsoft.monstrengo.auditorship.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditRegister;
import br.com.orionsoft.monstrengo.auditorship.services.CheckAuditCrudService;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.auditorship.support.EntityAuditValue;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

import com.sun.org.apache.xpath.internal.operations.String;

/**
 * Essa classe testa o servico CheckAuditCrudService. 
 * Procedimento:
 *    Cria-se uma instância da entidade de ApplicationUser, atribui a ele um valor e 
 *    atualiza
 *    Cria-se um userSession
 *    Cria-se um EntityAuditValue da entidade para guardar as informações antes da operação
 *    Audita-se a entidade apos a operação
 *    Invoca o serviço CheckAuditCrudService.
 *    
 * TODO MODIFICAR essa classe de teste para que seja mais dinâmico e incluir os asserts,
 * alem de incluir documentação
 * @author marcia
 *
 */
public class CheckAuditCrudServiceTestCase extends ServiceBasicTest
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CheckAuditCrudServiceTestCase.class);
    }

    @Test
    public void testExecute()
    {
        try
       {
            IEntity<ApplicationUser> user = UtilsCrud.create(this.serviceManager, ApplicationUser.class, null);
            
            EntityAuditValue entityAuditValue = new EntityAuditValue(user);
            
            user.setPropertyValue(ApplicationUser.LOGIN, "LoginDaMarcia");
            UtilsCrud.update(this.serviceManager, user, null);
            
            UserSession userSession = new UserSession(user, "");
            
            UtilsAuditorship.auditUpdate(this.serviceManager, userSession, entityAuditValue, null);
            
            ServiceData sd = new ServiceData(CheckAuditCrudService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(CheckAuditCrudService.IN_ENTITY_TYPE, ApplicationUser.class);
            sd.getArgumentList().setProperty(CheckAuditCrudService.IN_ENTITY_ID, user.getId());
            
            this.serviceManager.execute(sd);
            
            IEntityList<ApplicationUser> el = sd.getFirstOutput();
            
            for (IEntity<ApplicationUser> ent:el)
            {
                System.out.println("=========================");
                System.out.println("Entity Id: " + ent.getId());
                System.out.println("Description: " + ent.getProperty(AuditRegister.DESCRIPTION).getValue().getAsString());
                System.out.println("OcurrenceDate: " + ent.getProperty(AuditRegister.OCURRENCY_DATE).getValue().getAsString());
                System.out.println("=========================");
            }
            
        } catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
    
}
