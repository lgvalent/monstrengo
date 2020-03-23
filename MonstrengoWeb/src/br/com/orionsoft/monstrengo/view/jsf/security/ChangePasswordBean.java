package br.com.orionsoft.monstrengo.view.jsf.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.security.processes.ChangePasswordProcess;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla a view de alteração de senha do operador corrente
 * 
 * @jsf.bean name="changePasswordBean" scope="session"
 */
@ManagedBean
@SessionScoped
public class ChangePasswordBean extends BeanSessionBasic implements IRunnableProcessView
{
	/** Define a view JSF que é ativada para a visão QUERY */
	public static final String FACES_VIEW = "/pages/admin/securityChangePassword?faces-redirect=true";
	
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
    
	public String getCurrentPassword() {return currentPassword;}
	public void setCurrentPassword(String currentPassword) {this.currentPassword = currentPassword;}
	
	public String getNewPassword() {return newPassword;}
	public void setNewPassword(String newPassword) {this.newPassword = newPassword;}

	public String getConfirmNewPassword() {return confirmNewPassword;}
	public void setConfirmNewPassword(String confirmNewPassword) {this.confirmNewPassword = confirmNewPassword;}
	
    public String actionChange()
    {
        String result = null;
        
        try
        {
        	ChangePasswordProcess process = (ChangePasswordProcess) this.getApplicationBean().getProcessManager().createProcessByName(ChangePasswordProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());

        	process.setCurrentPassword(currentPassword);
        	process.setNewPassword(newPassword);
        	process.setConfirmNewPassword(confirmNewPassword);

        	if (process.runChange()){
        		// Definir o fluxo de tela de SUCESSO
        		result = FacesUtils.FACES_VIEW_SUCCESS;
        	}

        	// Adiciona as mensagens de erro no Faces
        	FacesUtils.addErrorMsgs(process.getMessageList());

        	process.finish();
        
        }catch(ProcessException e)
        {
        	// Adiciona as mensagens de erro no Faces
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	result = FacesUtils.FACES_VIEW_FAILURE;
        }
        
        return result;
    }

	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}

	public void doReload() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String actionStart() {
		return FACES_VIEW;
	}

	@Override
	public String getRunnableEntityProcessName() {
		return ChangePasswordProcess.PROCESS_NAME;
	}
	@Override
	public String runWithEntity(IEntity<?> entity) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String runWithEntities(IEntityCollection<?> entities) {
		// TODO Auto-generated method stub
		return null;
	}

}
