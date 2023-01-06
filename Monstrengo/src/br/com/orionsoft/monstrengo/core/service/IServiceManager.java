package br.com.orionsoft.monstrengo.core.service;

import br.com.orionsoft.monstrengo.core.service.IService;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.IManager;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;


/**
 * Gerenciador que mantém o registro de todos os serviços existentes e 
 * controla transações. Existe uma única instância desse gerenciador
 * na aplicação e todos os serviços possuem referência e são executados por ele.
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
