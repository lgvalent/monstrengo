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
import br.com.orionsoft.financeiro.gerenciador.process.RelatorioCobrancaProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;

/**
 * Bean que controla o Relatório de Cobrança.
 * A página de entrada de dados é relatorioCobranca.jsp
 */
@ManagedBean
@SessionScoped
public class RelatorioCobrancaBean extends BeanSessionBasic  implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "relatorioCobrancaBean";

	public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/gerenciadorRelatorioCobranca?faces-redirect=true";
	
	private RelatorioCobrancaProcess process = null;

    public void doVisualizar() {
		try {
			log.debug("RelatorioCobrancaBean.doVisualizar");

			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("pdf-content"); //vinculo com o PDF
			String fileName = "RelatorioCobranca";
			response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + ".pdf\""); 
			ServletOutputStream out = response.getOutputStream();
			
			getProcess().setOutputStream(out);
			getProcess().runGerarPdf();
			
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void doImprimirCartaCobranca() {
    	try {
    		log.debug("RelatorioCobrancaBean.actionImprimirCartaCobranca");

    		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
    		response.setContentType("pdf-content"); //vinculo com o PDF
			response.setHeader("Content-Disposition", "attachment;filename=\"" +  getProcess().getFileName() + "\""); 
    		ServletOutputStream out = response.getOutputStream();

    		getProcess().setOutputStream(out);
    		getProcess().runImprimirCartaCobranca();

    		out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();			
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }

    public void doGerarEtiqueta() {
        log.debug("RelatorioCobrancaBean.actionImprimirEtiqueta");
		getProcess().setOutputStream(null);
        if (getProcess().gerarEtiquetas()) {
            /* Adiciona as mensagens de info no Faces */
            FacesUtils.addInfoMsgs(process.getMessageList());
        }else{
            /* Adiciona as mensagens de erro no Faces */
            FacesUtils.addErrorMsgs(process.getMessageList());
        }
    }

	public RelatorioCobrancaProcess getProcess() {
		try {
			if (process == null) {
				process = (RelatorioCobrancaProcess)this.getApplicationBean().getProcessManager().createProcessByName(RelatorioCobrancaProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
			}
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
	
	@Override
	public String actionStart() {
		return FACES_VIEW_PASSO_1;
	}

	public String getRunnableEntityProcessName() {
		return RelatorioCobrancaProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		
		if (!this.getProcess().runWithEntity(entity)){
			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}

		return FACES_VIEW_PASSO_1;
	}

	public String runWithEntities(IEntityCollection<?> entities) {
		return FacesUtils.FACES_VIEW_FAILURE;
	}


}
