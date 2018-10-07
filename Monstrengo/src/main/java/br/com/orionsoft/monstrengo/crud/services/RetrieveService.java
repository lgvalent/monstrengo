package br.com.orionsoft.monstrengo.crud.services;

import org.hibernate.HibernateException;

import br.com.orionsoft.monstrengo.crud.services.RetrieveService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Serviço de recuperação de entidades.
 * 
 *<p><b>Argumentos:</b>
 *<br>IN_CLASS: Class da entidade a ser recuperada.
 *<br>ID_LONG: O identificador unitário da entidade.
 *  
 *<p><b>Procedimento:</b>
 *<br>Obtem o Dao responsável pela classe.
 *<br>Obtem o objeto com o respectivo Id.
 *<br>Converte o objeto em uma entidade.
 *<br><b>Retorna uma entidade com seu objeto e metadados.</b> 
 * 
 * @author Marcia
 * @version 28/09/2005
 * 
 */
public class RetrieveService extends ServiceBasic {
    public static String SERVICE_NAME = "RetrieveService";
    public static String CLASS = "classObj";
    public static String ID_LONG = "id";
    
    public void execute (ServiceData serviceData) throws ServiceException
    {
        
        IEntity<?> result = null;
        try
        {
            log.debug("Iniciando a execução do serviço RetrieveService");
            // Obtem os parâmetros
            Class<?> classObj = (Class<?>) serviceData.getArgumentList().getProperty(CLASS); 
            Long id = (Long) serviceData.getArgumentList().getProperty(ID_LONG);

//            Lucio 03/11/2006
//            Usava os DAOS para obter os dados, porem cada DAO gera uma sessão particular diferente da sessão 
//            já gerada para o serviço. Agora eu pego a atual sessão do serviço e dela solicito o RETRIEVE
//            if (log.isDebugEnabled())
//            log.debug("Obtendo o dao correspondente à entidade" + classObj);
//            // Obtem o Dao responsável e o objeto
//            dao = daoManager.getDaoByEntity(classObj);
//            
//            log.debug("Recuperando o objeto correspondente e transformando num IEntidade");
//            result = this.getServiceManager().getEntityManager().getEntity(dao.retrieve(id));
//
            Object object = serviceData.getCurrentSession().get(classObj, id);
            
            if(object==null)
            	throw new ServiceException(MessageList.create(RetrieveService.class, "ERROR_RETRIEVING_ENTITY_BY_ID", classObj.getName(), id));
            	
            result = this.getServiceManager().getEntityManager().getEntity(object);
            
            
            // Adiciona o resultado no serviceData
            serviceData.getOutputData().add(result);

        } catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        } catch (HibernateException e) {
            // Trata o erro de hibernate
            throw new ServiceException(MessageList.createSingleInternalError(e));
		}
        
    }

    public String getServiceName() {return SERVICE_NAME;}
}