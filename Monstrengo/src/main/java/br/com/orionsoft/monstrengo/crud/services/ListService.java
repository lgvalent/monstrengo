package br.com.orionsoft.monstrengo.crud.services;

import java.util.List;

import org.hibernate.HibernateException;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Serviço de listagem de entidades.
 * 
 * <p><b>Argumentos:</b>
 * <br> IN_CLASS: A classe das entidades que serão listadas.
 * <br> CONDITION_OPT_STR: Uma string com uma condição HQL. (Opcional) 
 *  
 * <p><b>Procedimento:</b>
 * <br>Obtem o Dao responsável pela classe solicitada.
 * <br>Obtem a lista de objetos armazenados pelo Dao.
 * <br>  Se há alguma condição definida obtem a lista filtrada.
 * <br>  Senão obtem a lista completa de todos objetos armazenados.
 * <br>Converte a lista de objetos em uma lista de entidades.
 * <br><b>Retorna a lista de entidades (IEntityList).</b>
 * 
 * @author Marcia
 * @version 28/09/2005
 * 
 */
public class ListService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "ListService";
    public static String CLASS = "classObj";
    public static String CONDITION_OPT_STR = "condition";
    
    public String getServiceName() {return SERVICE_NAME;}

    public void execute(ServiceData serviceData) throws ServiceException
    {
        try
        {
            log.debug("Iniciando a execução do serviço ListService");
            // Obtém os parâmetros
            Class<?> classObj = (Class<?>) serviceData.getArgumentList().getProperty(CLASS);
            String condiction="";
            if (serviceData.getArgumentList().containsProperty(CONDITION_OPT_STR)) 
                condiction = (String) serviceData.getArgumentList().getProperty(CONDITION_OPT_STR);
            
            if (log.isDebugEnabled())
                log.debug("Obtendo o dao correspondente à entidade" + classObj);
            
//            TODO: CORRIGIR Os daos implementam um objeto de acesso a Daos o Spring
//            Este objeto cria sua própria sessão, desta forma, as operaçoes feitas pelos
//            DAOS nao usam a sessao corrente criada pelo ServiceManager. Assim, objetos
//            são criados em sessoes diferentese começa a dar erros. Lucio 20060430
            
//            // Obtem a lista de objetos persistidos 
//            IDAO dao = daoManager.getDaoByEntity(classObj);
//            List list = null;
//            if (condition!="")
//                list = dao.getList(condition);
//            else
//                list = dao.getList();
//
            if (log.isDebugEnabled())
                log.debug("Executando a consulta no banco...");
            // Obtem a lista de objetos persistidos diretamente da sessão atual sem consultar DAO 
            List list = null;
            if (condiction.equals(""))
                list = serviceData.getCurrentSession().createQuery("FROM " + classObj.getName() + " " + IDAO.ENTITY_ALIAS_HQL).list();
            else
                list = serviceData.getCurrentSession().createQuery("FROM " + classObj.getName() + " " + IDAO.ENTITY_ALIAS_HQL + " WHERE " + condiction).list();
            
            if (log.isDebugEnabled() && list != null)
                log.debug("Foram encontrados " + list.size() + " elementos");
            
            IEntityList<?> result =  this.getServiceManager().getEntityManager().getEntityList(list, classObj);
            
            // Adiciona o resulta no serviceData
            serviceData.getOutputData().add(result);
            
        }
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        } catch (HibernateException e) {
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}

    }

}