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
 * Servi�o de listagem de entidades.
 * 
 * <p><b>Argumentos:</b>
 * <br> IN_CLASS: A classe das entidades que ser�o listadas.
 * <br> CONDITION_OPT_STR: Uma string com uma condi��o HQL. (Opcional) 
 *  
 * <p><b>Procedimento:</b>
 * <br>Obtem o Dao respons�vel pela classe solicitada.
 * <br>Obtem a lista de objetos armazenados pelo Dao.
 * <br>  Se h� alguma condi��o definida obtem a lista filtrada.
 * <br>  Sen�o obtem a lista completa de todos objetos armazenados.
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
            log.debug("Iniciando a execu��o do servi�o ListService");
            // Obt�m os par�metros
            Class<?> classObj = (Class<?>) serviceData.getArgumentList().getProperty(CLASS);
            String condiction="";
            if (serviceData.getArgumentList().containsProperty(CONDITION_OPT_STR)) 
                condiction = (String) serviceData.getArgumentList().getProperty(CONDITION_OPT_STR);
            
            if (log.isDebugEnabled())
                log.debug("Obtendo o dao correspondente � entidade" + classObj);
            
//            TODO: CORRIGIR Os daos implementam um objeto de acesso a Daos o Spring
//            Este objeto cria sua pr�pria sess�o, desta forma, as opera�oes feitas pelos
//            DAOS nao usam a sessao corrente criada pelo ServiceManager. Assim, objetos
//            s�o criados em sessoes diferentese come�a a dar erros. Lucio 20060430
            
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
            // Obtem a lista de objetos persistidos diretamente da sess�o atual sem consultar DAO 
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
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        } catch (HibernateException e) {
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}

    }

}