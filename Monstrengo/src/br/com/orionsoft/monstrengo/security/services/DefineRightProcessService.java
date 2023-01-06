package br.com.orionsoft.monstrengo.security.services;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.UpdateService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.RightProcess;

/**
 * Serviço para definir o direito de um grupo sobre um determinado processo.
 * 
 * <p><b>Procedimento:</b>
 * <br>Tentar obter o Direito já instanciado.
 * <br>Se não conseguir cria um novo Direito.
 * <br>Define se o direito de execução é permitido ou não.
 * <br>
 * @author Lucio 21/10/2005
 * @version 21/10/2005
 * 
 * @spring.bean id="DefineRightProcessService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class DefineRightProcessService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "DefineRightProcessService";
    
    public static String GROUP_ID = "groupId";
    public static String PROCESS_ID = "processId";
    
    /** boolean: Define o direito de acesso */
    public static String EXECUTE_ALLOWED = "executeAllowed";

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            // Pega os argumentos
            long groupId = (Long) serviceData.getArgumentList().getProperty(GROUP_ID);
            long processId = (Long) serviceData.getArgumentList().getProperty(PROCESS_ID);
            boolean executeAllowed = (Boolean) serviceData.getArgumentList().getProperty(EXECUTE_ALLOWED);

            // TODO IMPLEMENTAR Usar um biblioteca mais eficiente para pesquisas de múltiplas condições
            // Obtem o Direito que se relaciona com o GrupoId e ProcessId indicados
            ServiceData sl = new ServiceData(ListService.SERVICE_NAME, serviceData);
            sl.getArgumentList().setProperty(ListService.CLASS, RightProcess.class);
            sl.getArgumentList().setProperty(ListService.CONDITION_OPT_STR,  IDAO.ENTITY_ALIAS_HQL + "." + RightProcess.SECURITY_GROUP + ".id=" + groupId + " and " + IDAO.ENTITY_ALIAS_HQL + "." + RightProcess.APPLICATION_PROCESS + ".id=" + processId);
            this.getServiceManager().execute(sl);
            
            IEntityList entCol = (IEntityList) sl.getFirstOutput();
            
            // Se este direito não existe, cria ele.
            IEntity right;
            if (entCol.size() == 0)
            {
                // Cria o novo direito
                right = UtilsCrud.create(this.getServiceManager(), RightProcess.class, serviceData);
                
                // Define suas novas propriedades
                right.getProperty(RightProcess.SECURITY_GROUP).getValue().setId(groupId, serviceData);
                right.getProperty(RightProcess.APPLICATION_PROCESS).getValue().setId(processId, serviceData);
            }
            else
            {
                right = entCol.get(0);
            }

            // Define o direito
            right.getProperty(RightProcess.EXECUTE_ALLOWED).getValue().setAsBoolean(executeAllowed);
            
            // Atualiza o direito
            ServiceData su = new ServiceData(UpdateService.SERVICE_NAME, serviceData);
            su.getArgumentList().setProperty(UpdateService.IN_ENTITY, right);
            this.getServiceManager().execute(su);
        } 
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }

}