package br.com.orionsoft.financeiro.view.jsf;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.process.ListarLancamentoMovimentoProcess;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoMovimentoService.QueryLancamentoMovimento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * @jsf.bean name="listarLancamentoMovimentoBean" scope="session"
 * @jsf.navigation from="*" result="listarLancamentoMovimento" to="/pages/financeiro/gerenciadorListarLancamentoMovimento.jsp" 
 */
@ManagedBean
@SessionScoped
public class ListarLancamentoMovimentoBean extends BeanSessionBasic implements IRunnableProcessView{
	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "listarLancamentoMovimentoBean";
	public static final String FACES_VIEW_LISTAR = "/pages/financeiro/gerenciadorListarLancamentoMovimento?faces-redirect=true";

	@ManagedProperty(value="#{compensarLancamentoMovimentosBean}")
	private CompensarLancamentoMovimentosBean compensarLancamentoMovimentosBean = null;

	private ListarLancamentoMovimentoProcess process = null;
    private List<QueryLancamentoMovimento> list = null;
    
    
    
	public CompensarLancamentoMovimentosBean getCompensarLancamentoMovimentosBean() {
		return compensarLancamentoMovimentosBean;
	}

	public void setCompensarLancamentoMovimentosBean(
			CompensarLancamentoMovimentosBean compensarLancamentoMovimentosBean) {
		this.compensarLancamentoMovimentosBean = compensarLancamentoMovimentosBean;
	}

	public void loadParams() throws Exception {
    }

    public void doReset() throws BusinessException, Exception {
    }

    public void doReload() throws BusinessException, Exception {
    }
    
    public List<QueryLancamentoMovimento> getList() throws Exception {
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
    
    public ListarLancamentoMovimentoProcess getProcess() {
    	try {
    		if (process == null)
    			process = (ListarLancamentoMovimentoProcess)this.getApplicationBean().getProcessManager().createProcessByName(ListarLancamentoMovimentoProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
    	} catch (ProcessException e) {
    		e.printStackTrace();
    	}
        return process;
    }

    public void doVisualizar() {
    	log.debug("ListarLancamentoMovimentoBean.doVisualizar");
    
    	if (getProcess().runListar())
    		list = getProcess().getQueryLancamentoMovimentoList();
    }

    public String actionCompensarSelecionados() {
        log.debug("ListarPosicaoContratoBean.actionCompensarSelecionados");
        try {
        	IEntityList<LancamentoMovimento> entities = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntityList(null, LancamentoMovimento.class);
        	// Adiciona os lancamentos selecionados no processo de cancelamento
        	for(QueryLancamentoMovimento movBean: this.list){
        		if(movBean.isSelected()){
        			IEntity<LancamentoMovimento> entity = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(), LancamentoMovimento.class, movBean.getId(), null);
        			entities.add(entity);
        		}
        	}
        	return this.compensarLancamentoMovimentosBean.runWithEntities(entities);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
            FacesUtils.addErrorMsgs(e.getErrorList());
		}
		
		return "";
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
		return ListarLancamentoMovimentoProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		
		if (!this.getProcess().runWithEntity(entity)){
			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}

		/* Alimenta a lista com os laçamentos processados */
		this.list = getProcess().getQueryLancamentoMovimentoList();
		
		return FACES_VIEW_LISTAR;
	}
	
	public String runWithEntities(IEntityCollection<?> entities) {
		return FacesUtils.FACES_VIEW_FAILURE;
	}



}
