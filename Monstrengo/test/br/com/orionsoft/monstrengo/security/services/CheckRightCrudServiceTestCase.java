package br.com.orionsoft.monstrengo.security.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.RightCrud;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;
import br.com.orionsoft.monstrengo.security.services.CheckRightCrudService;

/**
* Classe para testar o servico CheckRightCrudService
* 
* <p><b>Procedimento:</b>
* <br>Criar usuários, grupos e definir os direitos de acesso à eles.
* <br>Invocar o servico para direitos permitidos, não permitidos e inexistentes.
* <br>Apagar os usuários, grupos e direitos criados no banco na ordem inversa em que são criados.
*/
public class CheckRightCrudServiceTestCase extends ServiceBasicTest
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CheckRightCrudServiceTestCase.class);
    }

    public void testExecute()
    {
        List<IEntity> users = new ArrayList<IEntity>();
        List<IEntity> groups = new ArrayList<IEntity>();
        List<IEntity> rights = new ArrayList<IEntity>();
        
        try{
        //Criando usuários
        System.out.println("user1");
        //Esse usuário pertencerá ao grupo1 e ao grupo 2
        IEntity user1 = UtilsCrud.create(this.serviceManager, ApplicationUser.class, null);
        user1.setPropertyValue(ApplicationUser.LOGIN, "marcia");
        user1.setPropertyValue(ApplicationUser.PASSWORD, "marcia");
        UtilsCrud.update(this.serviceManager, user1, null);
        users.add(0, user1);
        
        System.out.println("user2");
        //Esse usuário pertencerá ao grupo1
        IEntity user2 = UtilsCrud.create(this.serviceManager, ApplicationUser.class, null);
        user2.setPropertyValue(ApplicationUser.LOGIN, "teste");
        user2.setPropertyValue(ApplicationUser.PASSWORD, "teste");
        UtilsCrud.update(this.serviceManager, user2, null);
        users.add(1, user2);
        
        System.out.println("user3");
        //Esse usuário pertencerá ao grupo 2
        IEntity user3 = UtilsCrud.create(this.serviceManager, ApplicationUser.class, null);
        user3.setPropertyValue(ApplicationUser.LOGIN, "stringteste");
        user3.setPropertyValue(ApplicationUser.PASSWORD, "stringteste");
        UtilsCrud.update(this.serviceManager, user3, null);
        users.add(2, user3);
        
        //Criando grupos e associando aos usuários
        System.out.println("group1");
        IEntity group1 = UtilsCrud.create(this.serviceManager, SecurityGroup.class, null);
        group1.setPropertyValue(SecurityGroup.NAME, "group1");
        UtilsCrud.update(this.serviceManager, group1, null);
        group1 = UtilsCrud.retrieve(this.serviceManager, SecurityGroup.class, group1.getId(), null);
        group1.getProperty(SecurityGroup.USERS).getValue().getAsEntityCollection().add(user1);
        group1.getProperty(SecurityGroup.USERS).getValue().getAsEntityCollection().add(user2);
        UtilsCrud.update(this.serviceManager, group1, null);
        groups.add(0, group1);
        
        System.out.println("group2");
        IEntity group2 = UtilsCrud.create(this.serviceManager, SecurityGroup.class, null);
        group2.setPropertyValue(SecurityGroup.NAME, "group2");
        UtilsCrud.update(this.serviceManager, group2, null);
        group2 = UtilsCrud.retrieve(this.serviceManager, SecurityGroup.class, group2.getId(), null);
        group2.getProperty(SecurityGroup.USERS).getValue().getAsEntityCollection().add(user1);
        group2.getProperty(SecurityGroup.USERS).getValue().getAsEntityCollection().add(user3);
        UtilsCrud.update(this.serviceManager, group2, null);
        groups.add(1, group2);
        
        System.out.println("ServiceData");
        //Buscando os processos que existem no banco
        ServiceData sd = new ServiceData(ListService.SERVICE_NAME, null);
        sd.getArgumentList().setProperty(ListService.CLASS, ApplicationEntity.class);
        this.serviceManager.execute(sd);
        IEntityList entities = (IEntityList) sd.getFirstOutput();
        
        //Definindo os direitos
        System.out.println("right1");
        IEntity right1 = UtilsCrud.create(this.serviceManager, RightCrud.class, null);
        right1.setPropertyValue(RightCrud.SECURITY_GROUP, group1);
        right1.setPropertyValue(RightCrud.CREATE_ALLOWED, true);
        right1.setPropertyValue(RightCrud.DELETE_ALLOWED, true);
        right1.setPropertyValue(RightCrud.RETRIEVE_ALLOWED, false);
        right1.setPropertyValue(RightCrud.UPDATE_ALLOWED, false);
        right1.setPropertyValue(RightCrud.APPLICATION_ENTITY, entities.getFirst());
        UtilsCrud.update(this.serviceManager, right1, null);
        rights.add(0, right1);
        
        System.out.println("right2");
        IEntity right2 = UtilsCrud.create(this.serviceManager, RightCrud.class, null);
        right2.setPropertyValue(RightCrud.SECURITY_GROUP, group2);
        right2.setPropertyValue(RightCrud.CREATE_ALLOWED, false);
        right2.setPropertyValue(RightCrud.DELETE_ALLOWED, false);
        right2.setPropertyValue(RightCrud.RETRIEVE_ALLOWED, true);
        right2.setPropertyValue(RightCrud.UPDATE_ALLOWED, true);
        right2.setPropertyValue(RightCrud.APPLICATION_ENTITY, entities.getFirst());
        UtilsCrud.update(this.serviceManager, right2, null);
        rights.add(1, right2);
        
        System.out.println("service1");
        //Avaliando o servico para user1
        ServiceData svd = new ServiceData(CheckRightCrudService.SERVICE_NAME, null);
        System.out.println("size - " + entities.size());
        svd.getArgumentList().setProperty(CheckRightCrudService.IN_ENTITY_ID, entities.getFirst().getId());
        svd.getArgumentList().setProperty(CheckRightCrudService.IN_USER_ID_OPT, user1.getId());
        System.out.println("0000");
        this.serviceManager.execute(svd);
        
        System.out.println("1o Assert.assert");
        Assert.assertTrue((Boolean)svd.getOutputData(CheckRightCrudService.OUT_CREATE)); //create
        Assert.assertTrue((Boolean)svd.getOutputData(CheckRightCrudService.OUT_DELETE)); //delete
        Assert.assertTrue((Boolean)svd.getOutputData(CheckRightCrudService.OUT_RETRIEVE)); //retrieve
        Assert.assertTrue((Boolean)svd.getOutputData(CheckRightCrudService.OUT_UPDATE)); //update
        
        //Avaliando o servico para user3
        svd = new ServiceData(CheckRightCrudService.SERVICE_NAME, null);
        svd.getArgumentList().setProperty(CheckRightCrudService.IN_ENTITY_ID, entities.getFirst().getId());
        svd.getArgumentList().setProperty(CheckRightCrudService.IN_USER_ID_OPT, user3.getId());
        this.serviceManager.execute(svd);
        
        System.out.println("2o Assert.assert");
        Assert.assertFalse((Boolean)svd.getOutputData(CheckRightCrudService.OUT_CREATE)); //create
        Assert.assertFalse((Boolean)svd.getOutputData(CheckRightCrudService.OUT_DELETE)); //delete
        Assert.assertTrue((Boolean)svd.getOutputData(CheckRightCrudService.OUT_RETRIEVE)); //retrieve
        Assert.assertTrue((Boolean)svd.getOutputData(CheckRightCrudService.OUT_UPDATE)); //update

        //Avaliando o servico para um Entity que não foi setado ou seja Index = 10 
        svd = new ServiceData(CheckRightCrudService.SERVICE_NAME, null);
        svd.getArgumentList().setProperty(CheckRightCrudService.IN_ENTITY_ID, entities.get(10).getId());
        svd.getArgumentList().setProperty(CheckRightCrudService.IN_USER_ID_OPT, user1.getId());
        this.serviceManager.execute(svd);
        
        Assert.assertFalse((Boolean)svd.getOutputData(CheckRightCrudService.OUT_CREATE)); //create
        Assert.assertFalse((Boolean)svd.getOutputData(CheckRightCrudService.OUT_DELETE)); //delete
        Assert.assertFalse((Boolean)svd.getOutputData(CheckRightCrudService.OUT_RETRIEVE)); //retrieve
        Assert.assertFalse((Boolean)svd.getOutputData(CheckRightCrudService.OUT_UPDATE)); //update

        }catch (Exception e)
        {
            e.printStackTrace();

            Assert.assertTrue(false);
        }
        finally{ 
            try{

            //Deletando os direitos
            System.out.println("Deletando " + rights.size() + " Rights");
            UtilsCrud.delete(this.serviceManager, rights.get(0), null);
            UtilsCrud.delete(this.serviceManager, rights.get(1), null);
            
            //Deletando os grupos
            System.out.println("Deletando " + groups.size() + " Groups");
            UtilsCrud.delete(this.serviceManager, groups.get(0), null);
            UtilsCrud.delete(this.serviceManager, groups.get(1), null);
          
            //Deletando os usuários
            System.out.println("Deletando " + users.size() + " Users");
            UtilsCrud.delete(this.serviceManager, users.get(0), null);
            UtilsCrud.delete(this.serviceManager, users.get(1), null);
            UtilsCrud.delete(this.serviceManager, users.get(2), null);
            
            }catch(Exception e)
            {
                e.printStackTrace();
                Assert.assertFalse(true);
            }
            
        }
   
    }

}
