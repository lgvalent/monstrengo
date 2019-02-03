package br.com.orionsoft.monstrengo.crud.services;

import org.hibernate.HibernateException;

import br.com.orionsoft.monstrengo.crud.services.DeleteService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Servi�o de exclus�o de entidades.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_ENTITY: Entidade que ser� exclu�da.
 *  
 * <p><b>Procedimento:</b>
 * <br>Obtem o Dao respons�vel pelo tipo da entidade.
 * <br>Obtem o objeto da entidade.
 * <br>Solicita para o Dao excluir o objeto.
 * 
 * @author Marcia
 * @version 2005/09/28
 */
public class DeleteService extends ServiceBasic 
{
    public static String SERVICE_NAME = "DeleteService";
    public static String IN_ENTITY = "entity";

    public String getServiceName() {return SERVICE_NAME;}
    
    public void execute(ServiceData serviceData) throws ServiceException
    {
        log.debug("Iniciando a execu��o do servi�o DeleteService");
//        IDAO dao;
        IEntity<?> entity=null; 
        try
        {
            log.debug("Obtendo os par�metros");
            entity = (IEntity<?>) serviceData.getArgumentList().getProperty(IN_ENTITY);


//          Lucio 08/11/2006
//          Usava os DAOS para obter os dados, porem cada DAO gera uma sess�o particular diferente da sess�o 
//          j� gerada para o servi�o. Agora eu pego a atual sess�o do servi�o e dela solicito o DELETE
            
//          log.debug("Obtendo o dao correspondente � entidade");
//          dao = daoManager.getDaoByEntity(entity.getInfo().getType());
//          
//          log.debug("Removendo a entidade");
//          dao.delete(entity.getObject());
            
            /*  Lucio 20100615: Esta validacao foi passada para o processo
             *  Executa a valida��o ap�s a confirma��o da exclus�o da entidade.
             
            if(this.getServiceManager().getEntityManager().getDvoManager().contains(entity))
            	   this.getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entity).afterDelete(entity, serviceData); 
            */
            serviceData.getCurrentSession().delete(entity.getObject());
            
        } catch (BusinessException e) {
            log.fatal(e.getErrorList());
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        } catch (HibernateException e) {
            throw new ServiceException(MessageList.createSingleInternalError(e));
        } catch (Exception e) {
        	log.fatal(e.getMessage());
        	// O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
        	throw new ServiceException(MessageList.create(DeleteService.class, "ERROR_DELETE_FOREING", entity.getObject().toString()));
		}
    }
    
}