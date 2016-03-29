package br.com.orionsoft.monstrengo.view.jsf.crud;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabelGroup;
import br.com.orionsoft.monstrengo.crud.labels.processes.CreateLabelFromEntityProcess;
import br.com.orionsoft.monstrengo.crud.labels.services.ListModelLabelEntityService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Está classe é o controlador da visão de geração de etiquetas 
 * para uma entidade específica ou uma lista de entidades
 * usando um modelo.
 * 
 * @author Lucio 20061005
 * @version 20061005
 * 
 * @jsf.bean name="labelEntityBean" scope="session"
 * @jsf.navigation from="*" result="labelView" to="/pages/basic/label/view.jsp" 
*/
@ManagedBean
@SessionScoped
public class LabelEntityBean extends CrudBasicBean
{
    /** Define a view JSF que é ativada para a visão RETRIEVE */
	public static final String FACES_VIEW_LABELS = "/pages/basic/labelView?faces-redirect=true";
    
    private long modelLabelEntityId = IDAO.ENTITY_UNSAVED;
    private long addressLabelGroupId = IDAO.ENTITY_UNSAVED;
    
    public long getModelLabelEntityId() {return modelLabelEntityId;}
	public void setModelLabelEntityId(long modelLabelId) {this.modelLabelEntityId = modelLabelId;}

	public long getAddressLabelGroupId() {return addressLabelGroupId;}
	public void setAddressLabelGroupId(long addressLabelGroupId) {this.addressLabelGroupId = addressLabelGroupId;}

	/**
     * Action que prepara a visualização
     * e controla o fluxo de tela. 
     * @return
     */
    public void doCreate(String entityClassName, long entityId, long modelId) throws Exception
    {
        log.debug("::Iniciando actionView");
        
        try{
        	// Prepara os parâmetros fornecidos
            this.doReset();
            
        	IEntity<?> entity = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(), Class.forName(entityClassName), entityId, null);

            CreateLabelFromEntityProcess process = (CreateLabelFromEntityProcess) this.getApplicationBean().getProcessManager().createProcessByName(CreateLabelFromEntityProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
            
            process.setEntity(entity);
            process.setModelLabelEntityId(modelId); // Injetados diretamente no BEAN pela visão usuária
            process.setAddressLabelGroupId(this.addressLabelGroupId); // Injetados diretamente no BEAN pela visão usuária
            
            if(process.runCreate()){

                this.getLabelBean().doReload();
                
                FacesUtils.addInfoMsg("Etiqueta gerada com SUCESSO.<br>Consulte sua <a href='../basic/labelView.xhtml' target='_blank'>Lista de etiquetas</a>");
            	FacesUtils.addInfoMsgs(process.getMessageList());
            }else{
            	FacesUtils.addErrorMsg("FALHA ao criar a etiqueta");
            	FacesUtils.addErrorMsgs(process.getMessageList());
            }
            
        }catch(ProcessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Visualização REJEITADA */
        }
    }
	
	/**
     * Action que prepara a visualização
     * e controla o fluxo de tela. 
     * @return
     */
    public void doCreate(String entityClassName, long entityId) throws Exception{
    	this.doCreate(entityClassName, entityId, this.modelLabelEntityId);
    }
	
	/**
     * Este método é responsável por compor a chave de criação da entidade usando
     * o tipo da entidade e o id da entidade. 
     * @return Retorna uma chave com entityType+entityId.
     */
	public String prepareCurrentEntityKey()
	{
    	return this.getEntityParam().getTypeName() + this.getEntityParam().getId();
	}
	
	
	/**
	 *  Cria uma lista com os modelos de etiquetas de entidades disponivel
	 *  para a entidade atualmente manipulada.
	 *  O tipo da entidade já deve estar definido para executar este método, para que ele mostra somente os modelos 
	 *  da entidade selecionada
	 */
	private List<SelectItem> modelsLabelEntityBuffer = null;
	private Class<?> lastGetModelsLabelEntityType = null;
	public List<SelectItem> getModelsLabelEntity(IEntity<?> entity){
		try{
			if((modelsLabelEntityBuffer==null) || (lastGetModelsLabelEntityType != entity.getInfo().getType())){
				ServiceData sd = new ServiceData(ListModelLabelEntityService.SERVICE_NAME, null);
				sd.getArgumentList().setProperty(ListModelLabelEntityService.IN_ENTITY_TYPE_NAME, entity.getInfo().getType().getName());
				this.getApplicationBean().getProcessManager().getServiceManager().execute(sd);
				modelsLabelEntityBuffer = sd.getFirstOutput();
				lastGetModelsLabelEntityType = entity.getInfo().getType();
			}
			
			return modelsLabelEntityBuffer;
		}catch (ServiceException e){
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}
    }

	/**
	 *  Cria uma lista com os grupos de etiquetas disponíveis
	 */
	private List<SelectItem> addressLabelGroupBuffer = null;
	public List<SelectItem> getAddressLabelGroupList(){
		try{
			if(addressLabelGroupBuffer == null){
				addressLabelGroupBuffer = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(AddressLabelGroup.class, "");
		    	/* Adiciona a primeira opção para mostar todas as etiquetas */
				addressLabelGroupBuffer.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Não definido)"));
			}
			
			return addressLabelGroupBuffer;
		}catch (BusinessException e){
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}
    }

	public boolean hasModelsLabelEntity(IEntity<?> entity){
		return this.getModelsLabelEntity(entity).size()>0;
	}
	
	
}