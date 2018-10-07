package br.com.orionsoft.monstrengo.core.service;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.service.IService;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;

/**
 * Esta classe oferece as funcionalidades básicas para a implementação
 * dos serviços que serão registrados e executados pelo ServiceManager.
 * 
 * Em todos os serviços deverá ser injetada uma referência para o gerenciador de serviços
 * (ServiceManager). O Spring com JavaDoc e xDcolet é usado para isto.
 * 
 * @author Lucio
 */
public abstract class ServiceBasic implements IService
{
    /**
     * Manipulador de Log para ser utilizado pelas implementações dos
     * serviços. 
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
     * Método útil para adição de mensagens sem ter que conhecer que as mensagens são
     * armazenadas no serviceData 
     * @param serviceData
     * @param messages
     * @since 20060714
     */
    public void addMessages(ServiceData serviceData, List<BusinessMessage> messages ){
    	serviceData.getMessageList().add(messages);
    }
}
