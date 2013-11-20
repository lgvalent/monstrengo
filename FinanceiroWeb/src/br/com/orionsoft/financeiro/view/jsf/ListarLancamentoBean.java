package br.com.orionsoft.financeiro.view.jsf;

import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.financeiro.gerenciador.process.ListarLancamentoProcess;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.QueryLancamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * @jsf.bean name="listarLancamentoBean" scope="session"
 * @jsf.navigation from="*" result="listarLancamento" to="/pages/financeiro/gerenciadorListarLancamento.jsp" 
 */
@ManagedBean
@SessionScoped
public class ListarLancamentoBean extends BeanSessionBasic implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "listarLancamentoBean";
	public static final String FACES_VIEW_LISTAR = "/pages/financeiro/gerenciadorListarLancamento?faces-redirect=true";

	private ListarLancamentoProcess process = null;
    private List<QueryLancamento> list = null;
    private Calendar dataAtual = CalendarUtils.getCalendar();
    
	public void loadParams() throws Exception {
    }

    public void doReset() throws BusinessException, Exception {
    }

    public void doReload() throws BusinessException, Exception {
    }
    
    public List<QueryLancamento> getList() throws Exception {
    	return list;
	}

    /**
     * Este método indica que existe um processo ativo e tem uma
     * lista de entidades prontas para serem visualizadas.
     * @return
     */
    public boolean isVisualizando(){
    	return (list != null);
    }

    public int getSize(){
    	return list.size();
    }
    
    public ListarLancamentoProcess getProcess() {
    	try {
    		if (process == null)
    			process = (ListarLancamentoProcess)this.getApplicationBean().getProcessManager().createProcessByName(ListarLancamentoProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
    	} catch (ProcessException e) {
    		e.printStackTrace();
    	}
        return process;
    }

    
    public void doVisualizar() {
    	log.debug("ListarLancamentoBean.doVisualizar");
    
    	if (getProcess().runListar())
    		list = getProcess().getQueryLancamentoList();
    }

	public Calendar getDataAtual() {
		return dataAtual;
	}

	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}
	
	@Override
	public String actionStart() {
		return FACES_VIEW_LISTAR;
	}

	public String getRunnableEntityProcessName() {
		return ListarLancamentoProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		
		if (!this.getProcess().runWithEntity(entity)){
			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}

		/* Alimenta a lista com os laçamentos processados */
		this.list = getProcess().getQueryLancamentoList();
		
		return FACES_VIEW_LISTAR;
	}


}
