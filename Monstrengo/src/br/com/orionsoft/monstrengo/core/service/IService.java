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
     * Identifica o nome do Servi�o para que o localizador de servi�os
     * localize os mesmos pelo nome. 
     * 
     * @return Retorna o nome do servi�o que � definido pelo Tipo da sua Interface 
     * 
     */
    public abstract String getServiceName();
    
    public abstract void registerService() throws Exception;
    
    public abstract IServiceManager getServiceManager();
    
    public abstract void setServiceManager(IServiceManager app);
    
    public abstract void execute(ServiceData serviceData) throws ServiceException;
    
    /**
     * Indica se o servi�o precisa iniciar uma transa��o. Default � false.
     * @return
     */
    public abstract boolean isTransactional();

    /**
     * Este m�todo � �til para inserir mensagens de informa��es na lista 
     * de mensagens do servi�o. Auxiliando na gera��o de um Log para ser 
     * exibido para o operador ao final do servi�o.
     * @since 20060501
     */
    public abstract void addInfoMessage(ServiceData serviceData, String messageKey, Object... params);
    
}