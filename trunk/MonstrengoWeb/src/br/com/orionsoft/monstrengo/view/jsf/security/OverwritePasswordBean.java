package br.com.orionsoft.monstrengo.view.jsf.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.security.processes.OverwritePasswordProcess;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla a view que permite substituir a senha de um operador
 */
@ManagedBean
@SessionScoped
public class OverwritePasswordBean extends BeanSessionBasic implements IRunnableProcessView
{
	/** Define a view JSF que é ativada para a visão QUERY */
	public static final String FACES_VIEW_START = "/pages/admin/securityOverwritePassword?faces-redirect=true";

	/* Identifica o nome da visão corrente para o gerenciador de visão */
	public static final String VIEW_NAME = "overwritePasswordBean";
	public String getViewName(){return VIEW_NAME; }

	/* Identifica o processo manipulado por esta visão. Implementação da interface IRunnableProcessView */
	public String getRunnableEntityProcessName() {return OverwritePasswordProcess.PROCESS_NAME;}

	
	private OverwritePasswordProcess process;

	public OverwritePasswordProcess getProcess() throws BusinessException {
    	if(this.process == null)
    		process = (OverwritePasswordProcess) this.getApplicationBean().getProcessManager().createProcessByName(OverwritePasswordProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
		
    	return process;
	}

	@Override
	public String actionStart() {
		return FACES_VIEW_START;
	}
	/**
	 * Este método implementa as atividades básicas de cancelamento do processo
	 * atual da visão. Os dados básicos são limpos. A visão seguinte é sugerida para "close".<br>
	 */
    public String actionCancel() throws Exception{
    	getProcess().finish();
    	process= null;
    	
    	return FacesUtils.FACES_VIEW_CLOSE;
    }
    
    public String actionOverwrite()
    {
    	String result = null;

    	if (process.runOverwrite()){
    		// Definir o fluxo de tela de SUCESSO
    		result = FacesUtils.FACES_VIEW_SUCCESS;
    		// Adiciona as mensagens no Faces
    		FacesUtils.addInfoMsgs(process.getMessageList());
    	}else{
    		// Definir o fluxo de tela de SUCESSO
    		result = FacesUtils.FACES_VIEW_FAILURE;
    		// Adiciona as mensagens no Faces
    		FacesUtils.addErrorMsgs(process.getMessageList());
    	}

    	return result;
    }


	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}

	public void doReload() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 */
	public String runWithEntity(IEntity<?> entity) {
		try {
			this.getProcess().runWithEntity(entity);
		} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		
		return FACES_VIEW_START;
	}



}
