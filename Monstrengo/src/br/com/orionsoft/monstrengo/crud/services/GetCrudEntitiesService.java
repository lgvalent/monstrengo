package br.com.orionsoft.monstrengo.crud.services;

import java.util.ArrayList;
import java.util.List;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;

/**
 * Servi�o para obter a lista de entidades que est�o atualmente
 * registradas e podem ser manipuladas pelo sistema, e ainda, 
 * que utilizam processos b�sicos de Cria��o, Obten��o, Edi��o
 * e Exclus�o.
 * <br>Entidades registradas que s�o manipuladas por processos e
 * que n�o s�o definidas por <i>IEntityMetadata.{getCanCreate() | getCanRetrieve()
 * getCanUpdate() | getCanDelete()}</i> n�o s�o fornecidas por este servi�o.
 * 
 * <p><b>Procedimento:</b>
 * <br>Obtem a lista de DAOs pelo DaoManager.
 * <br>Percorre a lista dos DAOs registrados.
 * <br>Verifica a entidade manipulada por cada DAO.
 * <br>Verifica com o DAO se a entidade possui uma das propriedades CRUD.
 * <br><b>Retorna uma lista de metadados de entidades (IEntityMetadata);</b>
 * <br>
 * @author Lucio 23/09/2005
 * @version 06/10/2005
 * 
 */
public class GetCrudEntitiesService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "GetCrudEntitiesService";
    
    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execu��o do servi�o GetCrudEntitiesService");
            // Pega os argumentos
            // Class classObj = (Class) serviceData.getArgumentList().getProperty(IN_CLASS);

            List<IEntityMetadata> result = new ArrayList<IEntityMetadata>();

            log.debug("Obtendo a lista de daos registrados");
            // Percorre a lista dos DAOs registrados.
            for(IDAO<?> dao: this.getServiceManager().getEntityManager().getDaoManager().getDaos().values())
            {
            	/* Obtem os metadados da entidade manipulada pelo atual DAO */
            	IEntityMetadata entidade = this.getServiceManager().getEntityManager().getEntityMetadata(dao.getEntityClass());
                
                if (log.isDebugEnabled())
                    log.debug("Obtendo metadados da entidade " + dao.getEntityClassName());
                // Verifica a entidade manipulada por cada DAO.
                // Verifica atrav�s dos metadados da classe se a entidade � canCreate, canRetrieve, canUpdate, canDelete.
            	if(entidade.getCanCreate()||entidade.getCanRetrieve()||
            	   entidade.getCanUpdate()||entidade.getCanDelete())
                {
                    if (log.isDebugEnabled())
                        log.debug("Encontrado dao para a entidade crud " + dao.getEntityClassName());
                    result.add(entidade);
                }else{
                	if (log.isDebugEnabled())
                		log.debug("A entidade n�o � crud:" + dao.getEntityClassName());
                }
            }
            // Retorna uma lista de metadados de entidades;
            serviceData.getOutputData().add(result);
        } 
        catch (BusinessException e)
        {
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        }
    }
    

}