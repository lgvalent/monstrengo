package br.com.orionsoft.financeiro.view.jsf;

import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.process.ListarLancamentoProcess;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.QueryLancamento;
import br.com.orionsoft.financeiro.view.jsf.documento.cobranca.ImprimirDocumentosCobrancaBean;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
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

	@ManagedProperty(value="#{cancelarLancamentoBean}")
	private CancelarLancamentoBean cancelarLancamentoBean = null;
	@ManagedProperty(value="#{quitarLancamentoBean}")
	private QuitarLancamentoBean quitarLancamentoBean = null;
	@ManagedProperty(value="#{alterarVencimentoDocumentosCobrancaBean}")
	private AlterarVencimentoDocumentosCobrancaBean alterarVencimentoDocumentosCobrancaBean = null;
	@ManagedProperty(value="#{imprimirDocumentosCobrancaBean}")
	private ImprimirDocumentosCobrancaBean imprimirDocumentosCobrancaBean = null;

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
	
    public String actionCancelarSelecionados() {
        log.debug("ListarLancamentoBean.actionCancelarSelecionados");
        try {
        	IEntityList<Lancamento> entities = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntityList(null, Lancamento.class);

        	for(QueryLancamento item: this.getProcess().getQueryLancamentoList()){
        		if(item.isSelected()){
        			IEntity<Lancamento> lancamento = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(), Lancamento.class, item.getId(), null);
        			
        			entities.add(lancamento);
        		}
        	}
			return this.cancelarLancamentoBean.runWithEntities(entities);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
            FacesUtils.addErrorMsgs(e.getErrorList());
		}
		
		return "";
    }

    public String actionQuitarSelecionados() {
        log.debug("ListarLancamentoBean.actionQuitarSelecionados");
        try {
        	IEntityList<Lancamento> entities = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntityList(null, Lancamento.class);

        	for(QueryLancamento item: this.getProcess().getQueryLancamentoList()){
        		if(item.isSelected()){
        			IEntity<Lancamento> lancamento = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(), Lancamento.class, item.getId(), null);
        			
        			entities.add(lancamento);
        		}
        	}
        	return this.quitarLancamentoBean.runWithEntities(entities);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
            FacesUtils.addErrorMsgs(e.getErrorList());
		}
		
		return "";
    }

    public String actionAlterarVencimentoDocumentosCobrancaSelecionados() {
        log.debug("ListarLancamentoBean.actionAlterarVencimentoDocumentosCobrancaSelecionados");
        try {
        	IEntityList<DocumentoCobranca> entities = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntityList(null, DocumentoCobranca.class);

        	for(QueryLancamento item: this.getProcess().getQueryLancamentoList()){
        		if(item.isSelected()){
        			IEntity<Lancamento> lancamento = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(), Lancamento.class, item.getId(), null);
        			if(lancamento.getObject().getDocumentoCobranca() != null){
        				IEntity<DocumentoCobranca> doc = lancamento.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().<DocumentoCobranca>getAsEntity();
        				doc.setSelected(true);
        				entities.add(doc);
        			}
        		}
        	}
        	return this.alterarVencimentoDocumentosCobrancaBean.runWithEntities(entities);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
            FacesUtils.addErrorMsgs(e.getErrorList());
		}
		
		return "";
    }

    public String actionImprimirDocumentosCobrancaSelecionados() {
        log.debug("ListarLancamentoBean.actionImprimirDocumentosCobrancaSelecionados");
        try {
        	IEntityList<DocumentoCobranca> entities = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntityList(null, DocumentoCobranca.class);

        	for(QueryLancamento item: this.getProcess().getQueryLancamentoList()){
        		if(item.isSelected()){
        			IEntity<Lancamento> lancamento = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(), Lancamento.class, item.getId(), null);
        			if(lancamento.getObject().getDocumentoCobranca() != null){
        				IEntity<DocumentoCobranca> doc = lancamento.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().<DocumentoCobranca>getAsEntity();
        				doc.setSelected(true);
        				entities.add(doc);
        			}
        		}
        	}
        	return this.imprimirDocumentosCobrancaBean.runWithEntities(entities);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
            FacesUtils.addErrorMsgs(e.getErrorList());
		}
		
		return "";
    }

	public CancelarLancamentoBean getCancelarLancamentoBean() {return cancelarLancamentoBean;}
	public void setCancelarLancamentoBean(CancelarLancamentoBean cancelarLancamentoBean) {this.cancelarLancamentoBean = cancelarLancamentoBean;}
	
	public QuitarLancamentoBean getQuitarLancamentoBean() {return quitarLancamentoBean;}
	public void setQuitarLancamentoBean(QuitarLancamentoBean quitarLancamentoBean) {this.quitarLancamentoBean = quitarLancamentoBean;}
	
	public AlterarVencimentoDocumentosCobrancaBean getAlterarVencimentoDocumentosCobrancaBean() {return alterarVencimentoDocumentosCobrancaBean;}
	public void setAlterarVencimentoDocumentosCobrancaBean(AlterarVencimentoDocumentosCobrancaBean alterarVencimentoDocumentosCobrancaBean) {this.alterarVencimentoDocumentosCobrancaBean = alterarVencimentoDocumentosCobrancaBean;}
	
	public ImprimirDocumentosCobrancaBean getImprimirDocumentosCobrancaBean() {return imprimirDocumentosCobrancaBean;}
	public void setImprimirDocumentosCobrancaBean(ImprimirDocumentosCobrancaBean imprimirDocumentosCobrancaBean) {this.imprimirDocumentosCobrancaBean = imprimirDocumentosCobrancaBean;}

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

	public String runWithEntities(IEntityCollection<?> entities) {
		return FacesUtils.FACES_VIEW_FAILURE;
	}

}
