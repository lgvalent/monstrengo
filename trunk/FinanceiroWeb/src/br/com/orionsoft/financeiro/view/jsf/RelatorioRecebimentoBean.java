/*
 * Created on 24/04/2007 by antonio
 */
package br.com.orionsoft.financeiro.view.jsf;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.gerenciador.process.RelatorioRecebimentoProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Bean que controla o Relatório de Recebimento.
 * A página de entrada de dados é relatorioRecebimento.jsp
 * @jsf.bean name="relatorioRecebimentoBean" scope="session"
 */
@ManagedBean
@SessionScoped
public class RelatorioRecebimentoBean extends BeanSessionBasic implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "relatorioRecebimentoBean";

	public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/gerenciadorRelatorioRecebimento?faces-redirect=true";

	private RelatorioRecebimentoProcess process = null;

    public void doVisualizar() {
		try {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("pdf-content"); //vinculo com o PDF
			String fileName = "RelatorioRecebimento";
			response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + ".pdf\""); 
			ServletOutputStream out = response.getOutputStream();
			
			log.debug("RelatorioRecebimentoBean.doVisualizar");
			getProcess().setOutputStream(out);
			getProcess().execute();
			
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public RelatorioRecebimentoProcess getProcess() {
		try {
			if (process == null)
				process = (RelatorioRecebimentoProcess)this.getApplicationBean().getProcessManager().createProcessByName(RelatorioRecebimentoProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
		} catch (ProcessException e) {
			e.printStackTrace();
		}
		return process;
	}

	public void loadParams() throws Exception {
    }

    public void doReset() throws BusinessException, Exception {
    }

    public void doReload() throws BusinessException, Exception {
    }

	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return RelatorioRecebimentoProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		
		if (!this.getProcess().runWithEntity(entity)){
			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}

		return FACES_VIEW_PASSO_1;
	}

}
