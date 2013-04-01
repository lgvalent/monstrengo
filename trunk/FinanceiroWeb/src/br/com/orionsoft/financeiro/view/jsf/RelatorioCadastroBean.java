package br.com.orionsoft.financeiro.view.jsf;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.financeiro.gerenciador.process.RelatorioCadastroProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;

/**
 * @jsf.bean name="relatorioCadastroBean" scope="session"
 * @jsf.navigation from="*" result="relatorioCadastro" to="/pages/financeiro/relatorioCadastro1.jsp" 
 */
@ManagedBean
@SessionScoped
public class RelatorioCadastroBean extends BeanSessionBasic {
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que � ativada para cada view */
	public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/relatorioCadastro1?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_2 = "/pages/financeiro/relatorioCadastro2?faces-redirect=true";

	private RelatorioCadastroProcess process = null;
    
	public void loadParams() throws Exception {
    }

    public void doReset() throws BusinessException, Exception {
    }

    public void doReload() throws BusinessException, Exception {
    }
    
    public RelatorioCadastroProcess getProcess() {
    	try {
    		if (process == null)
    			process = (RelatorioCadastroProcess)this.getApplicationBean().getProcessManager().createProcessByName(RelatorioCadastroProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
    	} catch (ProcessException e) {
    		e.printStackTrace();
    	}
        return process;
    } 
    
//    public String doVisualizar() {
//    	return FACES_VIEW_PASSO_2;
//    }
    
    /* Gera o relatorio em pdf*/
    public void doVisualizar() {
		try {
			log.debug("RelatorioCadastroBean.doVisualizar");

			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("pdf-content"); //vinculo com o PDF
			String fileName = "RelatorioCadastro";
			response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + ".pdf\""); 
			ServletOutputStream out = response.getOutputStream();
			
			getProcess().setOutputStream(out);
			getProcess().execute();
			
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
  
    
}
