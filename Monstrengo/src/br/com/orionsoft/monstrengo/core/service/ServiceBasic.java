package br.com.orionsoft.monstrengo.core.service;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.service.IService;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;

/**
 * Esta classe oferece as funcionalidades b�sicas para a implementa��o
 * dos servi�os que ser�o registrados e executados pelo ServiceManager.
 * 
 * Em todos os servi�os dever� ser injetada uma refer�ncia para o gerenciador de servi�os
 * (ServiceManager). O Spring com JavaDoc e xDcolet � usado para isto.
 * 
 * @author Lucio
 */
public abstract class ServiceBasic implements IService
{
    /**
     * Manipulador de Log para ser utilizado pelas implementa��es dos
     * servi�os. 
     */
    protected Logger log = LogManager.getLogger(getClass());
    
    private IServiceManager serviceManager;
    private boolean transactional = true;
    
    public void registerService() throws Exception
    {
      this.serviceManager.registerService(this);  
    }

    public IServiceManager getServiceManager(){return serviceManager;}
    public void setServiceManager(IServiceManager serviceManager){this.serviceManager = serviceManager;}
    
    public final boolean isTransactional(){return transactional;}
    public void setTransactional(boolean transactional){this.transactional = transactional;}
    
    public void addInfoMessage(ServiceData serviceData, String messageKey, Object... params){
    	serviceData.getMessageList().add(BusinessMessage.TYPE_INFO, this.getClass(), messageKey, params);
    }
    
    /**
     * M�todo �til para adi��o de mensagens sem ter que conhecer que as mensagens s�o
     * armazenadas no serviceData 
     * @param serviceData
     * @param messages
     * @since 20060714
     */
    public void addMessages(ServiceData serviceData, List<BusinessMessage> messages ){
    	serviceData.getMessageList().add(messages);
    }
}
