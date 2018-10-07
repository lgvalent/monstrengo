package br.com.orionsoft.monstrengo.crud.report.services;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReportBean;
import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.QueryCondiction;
import br.com.orionsoft.monstrengo.crud.services.QueryService;

/**
 * Este serviço retorna a lista de relatórios personalizados.
 * É possível escolher uma Entidade e/ou um Operador disponível.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_APPLICATION_USER_OPT: Usuário que será usada para filtrar a pesquisa de relatórios personalizados disponíveis.
 * <br> IN_APPLICATION_ENTITY_OPT: Entidade que será usada para filtrar a pesquisa de relatórios personalizados disponíveis.
 * 
 * <p><b>Procedimento:</b>
 * <br>Pesquisa no banco todos os relatórios personalizados cuja entidade e/ou operador satisfaça os parâmetros fornecidos.
 * <br><b>Retorna uma lista de itens de seleção com o id e nome dos relatórios.</b>
 * 
 * 
 * @spring.bean id="ListUserReportService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ListUserReportService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "ListUserReportService";
    
    /** Parâmetros da entidade */
    public static String IN_APPLICATION_USER_ID_OPT = "applicationUserId";
    public static String IN_APPLICATION_ENTITY_ID_OPT = "applicationEntityId";

    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execução do serviço ListUserReportService");
            
            // Pega os argumentos
            long inApplicationUserId = IDAO.ENTITY_UNSAVED;
            if(serviceData.getArgumentList().containsProperty(IN_APPLICATION_USER_ID_OPT))
            	inApplicationUserId = (Long) serviceData.getArgumentList().getProperty(IN_APPLICATION_USER_ID_OPT);
            
            long inApplicationEntityId = IDAO.ENTITY_UNSAVED;
            if(serviceData.getArgumentList().containsProperty(IN_APPLICATION_ENTITY_ID_OPT))
            	inApplicationEntityId = (Long) serviceData.getArgumentList().getProperty(IN_APPLICATION_ENTITY_ID_OPT);

            log.debug("Buscando os relatórios");
            /* Cria a lista de condições */
            List<QueryCondiction> condictions = new ArrayList<QueryCondiction>();
            
            QueryCondiction condiction = null; 
            /* Verifica se foi passado um operador para selecionar o relatórios
             * deste operador e os que não têm operador definidos (públicos) */
            if(inApplicationUserId != IDAO.ENTITY_UNSAVED){
            	condiction = new QueryCondiction(this.getServiceManager().getEntityManager(),
            									UserReportBean.class,
            									UserReportBean.APPLICATION_USER,
            									Operator.EQUAL,
            									Long.toString(inApplicationUserId),
            									"");
            	condiction.setOpenPar(true);
            	condictions.add(condiction);
            	
            	condiction = new QueryCondiction(this.getServiceManager().getEntityManager(),
						UserReportBean.class,
						UserReportBean.APPLICATION_USER,
						Operator.NULL,
						"",
						"");
            	condiction.setInitOperator(QueryCondiction.INIT_OR);
            	condiction.setClosePar(true);
            	condictions.add(condiction);
            }
            								
            /* Verifica se foi passado uma entidade */
            if(inApplicationEntityId != IDAO.ENTITY_UNSAVED){
            	condiction = new QueryCondiction(this.getServiceManager().getEntityManager(),
            									UserReportBean.class,
            									UserReportBean.APPLICATION_ENTITY,
            									Operator.EQUAL,
            									Long.toString(inApplicationEntityId),
            									"");
            	condictions.add(condiction);
            }
            	
            								
            
            ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, serviceData);
            sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, UserReportBean.class);
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_SELECT, IDAO.ENTITY_ALIAS_HQL +"." + IDAO.PROPERTY_ID_NAME + "," + IDAO.ENTITY_ALIAS_HQL +"." + UserReportBean.NAME + "," + IDAO.ENTITY_ALIAS_HQL +"." + UserReportBean.DESCRIPTION);
            sd.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, condictions);
			sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT, UserReportBean.NAME);
            this.getServiceManager().execute(sd);
            
            /* Obtendo a lista de objetos UserReportBean selecionados no banco*/
            List<Object[]> beanList = (List<Object[]>) sd.getOutputData(QueryService.OUT_OBJECT_LIST);
            
            /* Convertendo em uma lista de seleção List<SelectItem> */
            List<SelectItem> result = new ArrayList<SelectItem>(beanList.size());
            for(Object[] bean: beanList){
            	result.add(new SelectItem(bean[0], bean[1].toString(), bean[2].toString()));
            }
        	
        	// Adiciona o registro criado no resultado no serviceData
            serviceData.getOutputData().add(result);
            
        } 
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }
    
    public String getServiceName() {return SERVICE_NAME;}
}