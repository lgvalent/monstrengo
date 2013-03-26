package br.com.orionsoft.monstrengo.core.service;

import br.com.orionsoft.monstrengo.core.service.IService;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.IManager;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;


/**
 * Gerenciador que mant�m o registro de todos os servi�os existentes e 
 * controla transa��es. Existe uma �nica inst�ncia desse gerenciador
 * na aplica��o e todos os servi�os possuem refer�ncia e s�o executados por ele.
 *  
 * @author Lucio
 */
public interface IServiceManager extends IManager
{
    public IService getServiceByName(String serviceName) throws ServiceException;

    public void registerService(IService service) throws Exception;
    
    public void execute(ServiceData serviceData) throws ServiceException;

    public IEntityManager getEntityManager();
    
}
