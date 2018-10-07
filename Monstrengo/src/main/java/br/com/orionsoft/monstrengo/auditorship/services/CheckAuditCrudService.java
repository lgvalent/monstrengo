package br.com.orionsoft.monstrengo.auditorship.services;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditCrudRegister;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;


/**
 * Este servico retorna todos os registros de alterações que ocorreram numa
 * determinada entidade. 
 * 
 * <p><b>Argumento:</b>
 * <br> IN_ENTITY_TYPE Define o tipo da entidade que será analisada.
 * <br> IN_ENTITY_ID Define o Id da instância da entidade que será analisada.
 * 
 * <p><b>Procedimento:</b>
 * <br>Busca no banco a entidade a ser analisada.
 * <br>Cria a consulta através da API Criteria do hibernate
 * <br>Transforma a lista de objetos retornado pela consulta e transforma numa lista de EntityList.
 * <br><b>Retorna uma lista de registros (IEntityList) de registros de auditoria que satisfazem a condição.
 * 
 * @author marcia 2005/11/30
 * @version 20060707
 * 
 * @spring.bean id="CheckAuditCrudService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class CheckAuditCrudService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "CheckAuditCrudService";
    
    public static String IN_ENTITY_TYPE = "entityType";
    public static String IN_ENTITY_ID = "entityId";
    
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @SuppressWarnings("unchecked")
    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execução do serviço CheckAuditCrudService");
            // Pega os argumentos
            Class entityType = (Class) serviceData.getArgumentList().getProperty(IN_ENTITY_TYPE);
            long entityId = (Long) serviceData.getArgumentList().getProperty(IN_ENTITY_ID);

            // Prepara as entidades persistidas de appProcess e appEntity 
            IEntity appEntity = UtilsSecurity.retrieveEntity(this.getServiceManager(), entityType, serviceData);
            
            Session session = serviceData.getCurrentSession();
            
            Criteria crit = session.createCriteria(AuditCrudRegister.class);
            crit.add(Expression.eq(AuditCrudRegister.APPLICATION_ENTITY, appEntity.getObject()));
            crit.add(Expression.eq(AuditCrudRegister.ENTITY_ID, entityId));
            IEntityList el = this.getServiceManager().getEntityManager().getEntityList(crit.list(), AuditCrudRegister.class);
            serviceData.getOutputData().add(el);
        } 
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
        catch (HibernateException e)
        {
            e.printStackTrace();
        }
    }
}