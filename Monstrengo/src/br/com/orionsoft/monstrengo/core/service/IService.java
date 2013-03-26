package br.com.orionsoft.monstrengo.core.service;

import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;

/**
 * TODO DOCUMENTAR essa Interface.
 * 
 * @author Lucio
 */
public interface IService {
    
    /**
     * Identifica o nome do Serviço para que o localizador de serviços
     * localize os mesmos pelo nome. 
     * 
     * @return Retorna o nome do serviço que é definido pelo Tipo da sua Interface 
     * 
     */
    public abstract String getServiceName();
    
    public abstract void registerService() throws Exception;
    
    public abstract IServiceManager getServiceManager();
    
    public abstract void setServiceManager(IServiceManager app);
    
    public abstract void execute(ServiceData serviceData) throws ServiceException;
    
    /**
     * Indica se o serviço precisa iniciar uma transação. Default é false.
     * @return
     */
    public abstract boolean isTransactional();

    /**
     * Este método é útil para inserir mensagens de informações na lista 
     * de mensagens do serviço. Auxiliando na geração de um Log para ser 
     * exibido para o operador ao final do serviço.
     * @since 20060501
     */
    public abstract void addInfoMessage(ServiceData serviceData, String messageKey, Object... params);
    
}