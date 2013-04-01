/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.view.jsf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.contabilidade.process.ExportarMovimentoContabilProcess;
import br.com.orionsoft.financeiro.gerenciador.process.EstornarLancamentoMovimentoProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.http.MediaType;
/**
 * Bean que cotrola a view de exportar movimento contábil.
 */
@ManagedBean
@SessionScoped
public class ExportarMovimentoContabilBean extends BeanSessionBasic implements IRunnableProcessView {
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String FACES_VIEW_EXPORTAR = "/pages/financeiro/exportarMovimentoContabil?faces-redirect=true";
	public static final String VIEW_NAME = "exportarMovimentoContabilBean";

    private ExportarMovimentoContabilProcess process = null;
    private StreamedContent resultArquivoExportacao = null;
    
    public StreamedContent getResultArquivoExportacao() {
		return resultArquivoExportacao;
	}

	public void doReset() throws BusinessException, Exception {
    }

    public void doReload() throws BusinessException, Exception {
    }
    
    public ExportarMovimentoContabilProcess getProcess() throws ProcessException {
        if (process == null)
            process = (ExportarMovimentoContabilProcess)this.getApplicationBean().getProcessManager().createProcessByName(ExportarMovimentoContabilProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
        return process;
    }
    
    public String actionStart() {
        return FACES_VIEW_EXPORTAR;
    }

    public void doExportar() throws ProcessException {
        /* Injeta a descrição no processo, mantendo a mesma sempre dentro do bean.
         * Assim, a última descrição sempre será mantida e o processo não */

        if (getProcess().runExportar()) {
				try {
					InputStream stream = new FileInputStream(getProcess().getResultArquivoExportacao());
					this.resultArquivoExportacao = new DefaultStreamedContent(
							stream, MediaType.TEXT_PLAIN.toString(),
							getProcess().getResultArquivoExportacao().getName());
				} catch (FileNotFoundException e) {
					FacesUtils.addErrorMsg(e.getMessage());
				}

            // Adiciona as mensagens de info no Faces
            FacesUtils.addInfoMsgs(process.getMessageList());
        }else{
            // Adiciona as mensagens de erro no Faces
            FacesUtils.addErrorMsgs(process.getMessageList());
        }
    }
    
    /* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return EstornarLancamentoMovimentoProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		try {
			this.getProcess().runWithEntity(entity);
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		return FACES_VIEW_EXPORTAR;
	}

}
