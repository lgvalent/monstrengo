package br.com.orionsoft.monstrengo.view.jsf.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.security.processes.CreateSecurityStructureProcess;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla a view que executa o processo que cria
 * a estrutura b�sica de seguran�a para um operador.
 * Muito �til tamb�m quando uma entidade ou processo s�o inseridos no sistema
 * e o mesmo precisa ser registrado na estrutura de controle de seguran�a.
 *  
 * @jsf.bean name="createSecurityStructureBean" scope="session"
 */
@ManagedBean
@SessionScoped
public class CreateSecurityStructureBean extends BeanSessionBasic
{
 
    /** Define a view JSF que � ativada para a vis�o RETRIEVE */
	public static final String FACES_VIEW = "/pages/basic/securityCreateSecurityStructure?faces-redirect=true";

	private String login;
    private String groupName;
    private boolean allowAll=false;

    public String getLogin(){return login;}
    public void setLogin(String login){this.login = login;}

	public boolean isAllowAll() {return allowAll;}
	public void setAllowAll(boolean allowAll) {this.allowAll = allowAll;}

	public String getGroupName() {return groupName;}
	public void setGroupName(String groupName) {this.groupName = groupName;}

    public String actionCreate()
    {
        String result = null;
        
        try
        {
        CreateSecurityStructureProcess process = (CreateSecurityStructureProcess) this.getApplicationBean().getProcessManager().createProcessByName(CreateSecurityStructureProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());

        process.setLogin(login);
        process.setGroupName(groupName);
        process.setAllowAll(allowAll);

    	if (process.runCreate()){
        	// Adiciona as mensagens de info no Faces
        	FacesUtils.addInfoMsg("Processo executado com sucesso. Veja as a��es abaixo para maiores detalhes.");
        	FacesUtils.addInfoMsgs(process.getMessageList());

        	// Definir o fluxo de tela de SUCESSO
        	result = FacesUtils.FACES_VIEW_SUCCESS;
        	
        	process.finish();
        	
        }else{
        	// Adiciona as mensagens de erro no Faces
        	FacesUtils.addErrorMsg("Ocorreram alguns erros");
        	FacesUtils.addErrorMsgs(process.getMessageList());
        	// Definir o fluxo de tela de SUCESSO
        	result = FacesUtils.FACES_VIEW_FAILURE;
        }
        
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

}
