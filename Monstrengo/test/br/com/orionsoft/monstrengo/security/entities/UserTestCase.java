package br.com.orionsoft.monstrengo.security.entities;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;

/**
 * Esse classe testa o relacionamento entre as classes user e group, verificando
 * se com a exclusão de um group, implica na exclusão do relacionamento com user 
 * na tabela de relacionamentos
 * @author marcia
 *
 */
public class UserTestCase extends ServiceBasicTest
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(UserTestCase.class);
    }
    
    @Test
    public void testCrud()
    {
        try
        {
            // Criando e setando duas instâncias de ApplicationUser
            IEntity user = UtilsCrud.create(this.serviceManager, ApplicationUser.class, null);
            user.setPropertyValue(ApplicationUser.LOGIN, "marcia");
            user.setPropertyValue(ApplicationUser.PASSWORD, "senha");
            UtilsCrud.update(this.serviceManager, user, null);
            
            IEntity user2 = UtilsCrud.create(this.serviceManager, ApplicationUser.class, null);
            user2.setPropertyValue(ApplicationUser.LOGIN, "BLABLA");
            user2.setPropertyValue(ApplicationUser.PASSWORD, "blabla");
            UtilsCrud.update(this.serviceManager, user2, null);
            
            IEntityCollection enl;

            // Criando e setando uma instância de SecurityGroup
            IEntity group = UtilsCrud.create(this.serviceManager, SecurityGroup.class, null);
            group.setPropertyValue(SecurityGroup.NAME, "TESTE");

            // Relacionando os usuários ao grupo recém criado
            enl = group.getProperty(SecurityGroup.USERS).getValue().getAsEntityCollection();
            enl.add(user);
            enl.add(user2);
            group.setPropertyValue(SecurityGroup.USERS, enl);
            
            UtilsCrud.update(this.serviceManager, group, null);
            
            IEntityCollection enl2;
            
            // Criando e setando uma instância de SecurityGroup
            IEntity group2 = UtilsCrud.create(this.serviceManager, SecurityGroup.class, null);
            group2.setPropertyValue(SecurityGroup.NAME, "CADASTRO");

            // Relacionando um dos usuários ao grupo recém criado
            // assim user pertence a dois grupos
            enl2 = group2.getProperty(SecurityGroup.USERS).getValue().getAsEntityCollection();
            enl2.add(user);
            group2.setPropertyValue(SecurityGroup.USERS, enl2);
            
            UtilsCrud.update(this.serviceManager, group2, null);
            
            // Deletando o grupo teste
            UtilsCrud.delete(this.serviceManager, group, null);
            
            System.out.println("Id: " + user.getId());
            
            IEntity usr = UtilsCrud.retrieve(this.serviceManager, user.getInfo().getType(), user.getId(), null);
            
            IEntityCollection userGroups = usr.getProperty(ApplicationUser.SECURITY_GROUPS).getValue().getAsEntityCollection();
            System.out.println("userGroup " + userGroups.size());
            
            // Como user pertencia a dois grupos, apesar da exclusão de um, ele ainda
            // possui um relacionamento com o outro grupo.
            Assert.assertFalse(userGroups.isEmpty());
            
            IEntity usr2 = UtilsCrud.retrieve(this.serviceManager, user2.getInfo().getType(), user2.getId(), null);
            
            // Testar multiplas condições
            ServiceData sd = new ServiceData(ListService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(ListService.CLASS, SecurityGroup.class);
            
            
            IEntityCollection userGroups2 = usr2.getProperty(ApplicationUser.SECURITY_GROUPS).getValue().getAsEntityCollection();
            System.out.println("userGroup " + userGroups2.size());
            
            // Como user pertencia a dois grupos, apesar da exclusão de um, ele ainda
            // possui um relacionamento com o outro grupo.
            Assert.assertFalse(userGroups.isEmpty());
            
            // Como user2 pertencia a um grupo, com a exclusão do grupo teste, ele nao 
            // possui mais um relacionamento com grupo.
            Assert.assertTrue(userGroups2.isEmpty());
            
        } catch (BusinessException e)
        {
        	UtilsTest.showMessageList(e.getErrorList());
        	Assert.assertTrue(false);
        }
    }

    @Test
    public void testList()
    {
        try
        {
            Calendar start = Calendar.getInstance(); 
        	System.out.println(":List.");
            System.out.println(":List.");
            ServiceData svdl = new ServiceData(ListService.SERVICE_NAME, null);
            svdl.getArgumentList().setProperty(ListService.CLASS, ApplicationUser.class);
            serviceManager.execute(svdl);
            IEntityCollection<ApplicationUser> enl = svdl.getFirstOutput();
            
            for(IEntity<ApplicationUser> en: enl)
            {
                System.out.println("Entidade:" + en.getInfo().getLabel());
                for(IProperty prop: en.getProperties()) 
                    System.out.println(" " + prop.getInfo().getLabel() + "=" + prop.getValue().getAsString());
            }

            Assert.assertTrue(true);
            System.out.println(CalendarUtils.formatDateTime(start)); 
            System.out.println(CalendarUtils.formatDateTime(Calendar.getInstance())); 
            System.out.println(Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis()); 
        } catch (BusinessException e)
        {
            for(BusinessMessage er: e.getErrorList())
                System.out.println(er.getMessageClass().getSimpleName() + ":" + er.getErrorKey() + "=" + er.getMessage());
            Assert.assertTrue(false);
        }
    }

}
