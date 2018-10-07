package br.com.orionsoft.monstrengo.security.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import br.com.orionsoft.monstrengo.core.process.ProcessManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.RightProcess;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;
import br.com.orionsoft.monstrengo.security.services.CheckRightProcessService;

/**
 * Serviço para testar o servico CheckRightProcessService, que, dado um id de usuário e um
 * id de processo, verifica se o primeiro tem direito a execução da operação.
 * 
 * <p><b>Procedimento:</b>
 * <br>Criar usuários, grupos e setar direitos de acesso à eles.
 * <br>Invocar o servico para direitos permitidos, não permitidos e inexistentes.
 * <br>Apagar os usuários, grupos e direitos criados no banco.
 * <br>
 * @author Marcia
 * @version 2005/10/27
 *
 */
public class CheckRightProcessServiceTestCase extends ServiceBasicTest
{
    ProcessManager processManager;
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CheckRightProcessServiceTestCase.class);
    }

    public void testExecute()
    {
        List<IEntity> users = new ArrayList<IEntity>();
        List<IEntity> groups = new ArrayList<IEntity>();
        List<IEntity> rights = new ArrayList<IEntity>();
        
        try{
        // Criando usuários
        // Esse usuário pertence ao grupo1 e ao grupo 2
        IEntity user = UtilsCrud.create(this.serviceManager, ApplicationUser.class, null);
        user.setPropertyValue(ApplicationUser.LOGIN, "marcia");
        user.setPropertyValue(ApplicationUser.PASSWORD, "marcia");
        UtilsCrud.update(this.serviceManager, user, null);
        users.add(0, user);
        
        // Esse usuário pertence ao grupo1
        IEntity user2 = UtilsCrud.create(this.serviceManager, ApplicationUser.class, null);
        user2.setPropertyValue(ApplicationUser.LOGIN, "blabla");
        user2.setPropertyValue(ApplicationUser.PASSWORD, "blabla");
        UtilsCrud.update(this.serviceManager, user2, null);
        users.add(1, user2);
        
        // Esse usuário pertence ao grupo 2
        IEntity user3 = UtilsCrud.create(this.serviceManager, ApplicationUser.class, null);
        user3.setPropertyValue(ApplicationUser.LOGIN, "aro");
        user3.setPropertyValue(ApplicationUser.PASSWORD, "aro");
        UtilsCrud.update(this.serviceManager, user3, null);
        users.add(2, user3);
        
        // Criando grupos e associando aos usuários
        IEntity group = UtilsCrud.create(this.serviceManager, SecurityGroup.class, null);
        group.setPropertyValue(SecurityGroup.NAME, "group1");
        UtilsCrud.update(this.serviceManager, group, null);
        group = UtilsCrud.retrieve(this.serviceManager, SecurityGroup.class, group.getId(), null);
        group.getProperty(SecurityGroup.USERS).getValue().getAsEntitySet().add(user);
        group.getProperty(SecurityGroup.USERS).getValue().getAsEntitySet().add(user2);
        UtilsCrud.update(this.serviceManager, group, null);
        groups.add(0, group);
        
        IEntity group1 = UtilsCrud.create(this.serviceManager, SecurityGroup.class, null);
        group1.setPropertyValue(SecurityGroup.NAME, "group2");
        UtilsCrud.update(this.serviceManager, group1, null);
        group1 = UtilsCrud.retrieve(this.serviceManager, SecurityGroup.class, group1.getId(), null);
        group1.getProperty(SecurityGroup.USERS).getValue().getAsEntitySet().add(user);
        group1.getProperty(SecurityGroup.USERS).getValue().getAsEntitySet().add(user3);
        UtilsCrud.update(this.serviceManager, group1, null);
        groups.add(1, group1);
        
        // Buscando os processos que existem no banco
        ServiceData sd = new ServiceData(ListService.SERVICE_NAME, null);
        sd.getArgumentList().setProperty(ListService.CLASS, ApplicationProcess.class);
        this.serviceManager.execute(sd);
        IEntityList processes = (IEntityList) sd.getFirstOutput();
        
        // Definindo os direitos
        IEntity right1 = UtilsCrud.create(this.serviceManager, RightProcess.class, null);
        right1.setPropertyValue(RightProcess.SECURITY_GROUP, group);
        right1.setPropertyValue(RightProcess.EXECUTE_ALLOWED, true);
        right1.setPropertyValue(RightProcess.APPLICATION_PROCESS, processes.get(0));
        UtilsCrud.update(this.serviceManager, right1, null);
        rights.add(0, right1);
        
        IEntity right2 = UtilsCrud.create(this.serviceManager, RightProcess.class, null);
        right2.setPropertyValue(RightProcess.SECURITY_GROUP, group1);
        right2.setPropertyValue(RightProcess.EXECUTE_ALLOWED, false);
        right2.setPropertyValue(RightProcess.APPLICATION_PROCESS, processes.get(0));
        UtilsCrud.update(this.serviceManager, right2, null);
        rights.add(1, right2);
        
        // avaliando o servico
        ServiceData svd = new ServiceData(CheckRightProcessService.SERVICE_NAME, null);
        svd.getArgumentList().setProperty(CheckRightProcessService.IN_PROCESS_ID_OPT, processes.get(0).getId());
        svd.getArgumentList().setProperty(CheckRightProcessService.IN_USER_ID_OPT, user.getId());
        this.serviceManager.execute(svd);
        
        Assert.assertTrue((Boolean)svd.getFirstOutput());

        // avaliando o servico
        svd = new ServiceData(CheckRightProcessService.SERVICE_NAME, null);
        svd.getArgumentList().setProperty(CheckRightProcessService.IN_PROCESS_NAME_OPT, processes.get(0).getPropertyValue(ApplicationProcess.NAME).toString());
        svd.getArgumentList().setProperty(CheckRightProcessService.IN_USER_ID_OPT, user.getId());
        this.serviceManager.execute(svd);
        
        Assert.assertTrue((Boolean)svd.getFirstOutput());


        // avaliando o servico
        svd = new ServiceData(CheckRightProcessService.SERVICE_NAME, null);
        svd.getArgumentList().setProperty(CheckRightProcessService.IN_PROCESS_ID_OPT, processes.get(0).getId());
        svd.getArgumentList().setProperty(CheckRightProcessService.IN_USER_ID_OPT, user3.getId());
        this.serviceManager.execute(svd);
        
        Assert.assertFalse((Boolean)svd.getFirstOutput());
//        Assert.assertFalse(UtilsSecurity.checkRightProcess(this.serviceManager, (IProcess)processes.get(0), userSession3));
        // avaliando o servico
        svd = new ServiceData(CheckRightProcessService.SERVICE_NAME, null);
        svd.getArgumentList().setProperty(CheckRightProcessService.IN_PROCESS_ID_OPT, processes.get(1).getId());
        svd.getArgumentList().setProperty(CheckRightProcessService.IN_USER_ID_OPT, user.getId());
        this.serviceManager.execute(svd);
        
        Assert.assertFalse((Boolean)svd.getFirstOutput());

//      Assert.assertFalse(UtilsSecurity.checkRightProcess(this.serviceManager, (IProcess)processes.get(1), userSession));
        
        }catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        finally{
//            try{
//            // Deletando os direitos
//            System.out.println("Deletando " + rights.size() + " Rights");
//            int max = 0;
//            
//            
//            if (rights.size() >= users.size())
//            {
//                if (rights.size() >= groups.size())
//                    max = rights.size();    
//                else
//                    max = groups.size();
//            }
//            else
//            {
//                if (users.size() >= groups.size())
//                    max = users.size();
//                else
//                    max = groups.size();
//            }
//            
//            for (int i = 0; i<=max; i++)
//            {
////                if (rights.size() >= i)
////                {
////                    UtilsCrud.delete(this.serviceManager, rights.get(i));
////                }
//                if (users.size() >= i)
//                {
//                    UtilsCrud.delete(this.serviceManager, users.get(i));
//                }
//                if (groups.size() >= i)
//                {
//                    UtilsCrud.delete(this.serviceManager, groups.get(i));
//                }
//            }
////            for (IEntity right: rights)
////            {
////                UtilsCrud.delete(this.serviceManager, right);
////            }
////
////            for (IEntity user: users)
////            {
////                UtilsCrud.delete(this.serviceManager, user);
////            }
////            // Deletando os usuários
////            
////            // Deletando os grupos
////            System.out.println("Deletando " + groups.size() + "Groups.");
////            UtilsCrud.delete(this.serviceManager, groups.get(0));
////            UtilsCrud.delete(this.serviceManager, groups.get(1));
////            
//            }catch(Exception e)
//            {
//                e.printStackTrace();
//                Assert.assertFalse(true);
//            }
//            
//            
//            
        }
    }

}
