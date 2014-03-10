package br.com.orionsoft.monstrengo.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditProcessRegister;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;
import br.com.orionsoft.monstrengo.security.services.CheckAllRightCrudService;
import br.com.orionsoft.monstrengo.security.services.CheckAllRightProcessService;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Este bean � bastante utilizado na composi��o da interface.
 * Ele possui tr�s mapas <String, Object>:
 * - Lista das propriedades de uma entidade;
 * - Lista dos direitos crud do atual operador autenticado, que identifica quais a��es CRUD ele pode executar
 * - Lista dos direitos de processos que o operador pode executar
 * 
 *  TODO OTIMIZA��O A inicializa��o destes mapas demora bastante. Verificar quais servi
 *  �os demoram mais e tentar otimizar, pois est� bem lerdo.
 * 
 * @jsf.bean name="menuBean" scope="session"
 * #Esta regra de navega��o � insejata no faces-config.xml por merge, utilizando
 * o arquivo OrionSoft/jsf-navigations.xml
 * #@jsf.navigation from="*" result="menu" to="/pages/basic/menu.jsp" 
 */
@ManagedBean(name="menuBean")
@SessionScoped
public class MenuBean extends BeanSessionBasic
{

    /** Define a view JSF do menu */
	public static final String FACES_VIEW_MENU= "/pages/basic/menu?faces-redirect=true";
	public static final String FACES_VIEW_INDEX= "/index?faces-redirect=true";

	private Map<String, IEntityMetadata> infoMap=null;
	private Map<String, Map<String,Boolean>> crudMap=null;

	private Map<String, Boolean> processMap=null;
	private Map<String, ApplicationProcess> processInfoMap=null;

	public void doPrepareMenu() throws BusinessException
	{
		try{
			if(!this.getUserSessionBean().isLogged())
				throw new BusinessException(MessageList.create(MenuBean.class, "USER_NOT_AUTHENTICATED"));
			
			// Executa o servi�o para obter todos os direitos de todas entidades
			ServiceData sd = new ServiceData(CheckAllRightCrudService.SERVICE_NAME, null);
			sd.getArgumentList().getProperties().put(CheckAllRightCrudService.IN_USER_OPT, this.getUserSessionBean().getUserSession().getUser());
			this.getApplicationBean().getProcessManager().getServiceManager().execute(sd);
			crudMap = sd.<Map<String, Map<String,Boolean>>>getFirstOutput();

			/* 
			 * Para cada entidade crud encontrada na atual inst�ncia do sistema
			 * verifica se est� no mapa de direitos para nao dar erro pela ausencia do direito no mapa (null pointer)
			 */
			for(IEntityMetadata info: getInfoMap().values()){
				getInfoMap().put(info.getType().getSimpleName(), info);
				
				if(!crudMap.containsKey(info.getType().getSimpleName())){
					crudMap.put(info.getType().getSimpleName(), CheckAllRightCrudService.retrieveEmptyRightMap());
				}
			}
			
			// Executa o servi�o para obter todos os processos
			sd = new ServiceData(CheckAllRightProcessService.SERVICE_NAME, null);
			sd.getArgumentList().getProperties().put(CheckAllRightProcessService.IN_USER_OPT, this.getUserSessionBean().getUserSession().getUser());
			this.getApplicationBean().getProcessManager().getServiceManager().execute(sd);
			processMap = sd.<Map<String, Boolean>>getFirstOutput();
			
			// Prepara o processInfoMap
			processInfoMap = new HashMap<String, ApplicationProcess>();
			for(IEntity<ApplicationProcess> process: UtilsCrud.list(this.getApplicationBean().getProcessManager().getServiceManager(),
					                            ApplicationProcess.class,
					                            null)){
				ApplicationProcess oProcess = process.getObject();
  				processInfoMap.put(oProcess.getName(), oProcess);
			}
			
		}catch(ServiceException e)
		{
			// Adiciona as mensagens de erro no Faces
			FacesUtils.addErrorMsgs(e.getErrorList());
			throw e;
		}
		
	}
	
	
	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}
	
	public void doReload() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}
	
	public Map<String, Map<String, Boolean>> getCrudMap() throws BusinessException
	{
		if (crudMap==null)
			doPrepareMenu();
		
		return crudMap;
	}

	/**
	 * Retorna um mapa indexado pelo nome das entidades. Os items do mapa
	 * s�o do tipo {@link IEntityMetadata}. Assim, � possivel obter todos os
	 * metadados de uma entidade e tamb�m de suas propriedades.<br>
	 * Para conseguir os metadados, n�o � preciso executar de autentica��o. Logo, esta propriedade
	 * pode ser usada em vis�es fora do diret�rio "/pages/".    
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, IEntityMetadata> getInfoMap() throws BusinessException
	{
		if (infoMap==null){
			/* Cria o mapa de metadados das entidades 
			 * Exemplo de uso no JSF:
			 * #{menuBean.infoMap.MyClassName.label}*/
			infoMap = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitiesMetadata();
		}
		
		return infoMap;
	}

	/**
	 * Permite obter os metadados de uma entidade do mapa utilizando uma string.
	 * @param entityClassName
	 * @return
	 * @throws BusinessException
	 */
	public IEntityMetadata getInfo(String entityClassName) throws BusinessException{
		return infoMap.get(entityClassName);
	}

	private List<IEntityMetadata> infoListBuffer=null;
	public List<IEntityMetadata> getInfoList() throws BusinessException{
		if (infoListBuffer==null){
			infoListBuffer = new ArrayList<IEntityMetadata>(this.getInfoMap().size());
			for(IEntityMetadata info: this.getInfoMap().values()){
				infoListBuffer.add(info);
			}
			/* Ordena por ordem alfab�tica */
			Collections.<IEntityMetadata>sort(infoListBuffer, IEntityMetadata.COMPARATOR_LABEL);
			
		}
		return infoListBuffer;
	}


	/**
	 * Retorna um mapa indexado pelo nome das entidades. Os items do mapa
	 * s�o do tipo {@link IEntityMetadata}. Assim, � possivel obter todos os
	 * metadados de uma entidade e tamb�m de suas propriedades.<br>
	 * Para conseguir os metadados, n�o � preciso executar de autentica��o. Logo, esta propriedade
	 * pode ser usada em vis�es fora do diret�rio "/pages/".    
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Boolean> getProcessMap() throws BusinessException
	{
		if(processMap == null)
			doPrepareMenu();
		
		return processMap;
	}
	
	public void doRefreshEntitiesMetadata() throws BusinessException{
		this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().refreshEntitiesMetadata();
		
		/* Recompila os dados do menu para pegarem os novos metadados que est�o no EntityManager */
		doPrepareMenu();
		
		FacesUtils.addInfoMsg("Os metadados das entidades foram recarregados com sucesso");
	}


	public Map<String, ApplicationProcess> getProcessInfoMap() throws BusinessException {
		if(processMap == null)
			doPrepareMenu();

		return processInfoMap;
	}

	/**
	 * Este m�todo � utilizado para retornar uma lista com os metadados
	 * de todas as entidades cujo nome, label ou description possui a parte 
	 * do texto fornecido em entityName. N�o � sens�vel ao caso.
	 * @param entityName
	 * @return
	 */
	public List<IEntityMetadata> findEntity(String entityName) {
		List<IEntityMetadata> result = new ArrayList<IEntityMetadata>();
		for(IEntityMetadata info: this.infoMap.values()){
			if( /*(info.getName()!=null && info.getName().toLowerCase().contains(entityName.toString().toLowerCase())) ||*/
					(info.getLabel()!=null && info.getLabel().toLowerCase().contains(entityName.toLowerCase())) ||
					(info.getHint()!=null && info.getHint().toLowerCase().contains(entityName.toLowerCase())) ||
					(info.getDescription()!=null && info.getDescription().toLowerCase().contains(entityName.toLowerCase()))){
				result.add(info);
			}
		}
		return result;
	}
	
	/**
	 * Este m�todo � utilizado para retornar uma lista com os metadados
	 * de todas as entidades cujo nome, label ou description possui a parte 
	 * do texto fornecido em entityName. N�o � sens�vel ao caso.
	 * @param entityName
	 * @return
	 * @throws BusinessException 
	 */
	public List<ApplicationProcess> findProcess(String processName) throws BusinessException {
		List<ApplicationProcess> result = new ArrayList<ApplicationProcess>();
		for(ApplicationProcess info: this.getProcessInfoMap().values()){
			if( /*(info.getName()!=null && info.getName().toLowerCase().contains(entityName.toString().toLowerCase())) ||*/
					(info.getLabel()!=null && info.getLabel().toLowerCase().contains(processName.toLowerCase())) ||
					(info.getHint()!=null && info.getHint().toLowerCase().contains(processName.toLowerCase()))){
				result.add(info);
			}
		}
		return result;
	}

	/**
	 * Este m�todo � utilizado para retornar uma lista heterog�nea com os metadados
	 * de todas as entidades e processos cujos nome, label ou description possui a parte 
	 * do texto fornecido em findName. N�o � sens�vel ao caso.
	 * @param findName
	 * @return
	 * @throws BusinessException 
	 */
	public List<Object> findProcessAndEntity(String findName) throws BusinessException {
		List<Object> result = new ArrayList<Object>();
		
		result.addAll(findProcess(findName));
		result.addAll(findEntity(findName));
		
		return result;
	}
	
	
	public List<AuditProcessRegister> findAuditProccessRegister(String processName){
		return null;
	}
	
}
