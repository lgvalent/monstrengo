/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.view.jsf;

import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.process.ListarPosicaoContratoProcess;
import br.com.orionsoft.financeiro.view.jsf.documento.cobranca.ImprimirDocumentosCobrancaBean;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;

/**
 * Lista todos os lançamentos de uma determinada pessoa
 * detalhando também os movimentos de cada lançamento e sua situação.
 */
@ManagedBean
@SessionScoped
public class ListarPosicaoContratoBean extends BeanSessionBasic implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "listarPosicaoContratoBean";

	public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/gerenciadorListarPosicaoContrato?faces-redirect=true";

	@ManagedProperty(value="#{cancelarLancamentoBean}")
	private CancelarLancamentoBean cancelarLancamentoBean = null;
	@ManagedProperty(value="#{quitarLancamentoBean}")
	private QuitarLancamentoBean quitarLancamentoBean = null;
	@ManagedProperty(value="#{alterarVencimentoDocumentosCobrancaBean}")
	private AlterarVencimentoDocumentosCobrancaBean alterarVencimentoDocumentosCobrancaBean = null;
	@ManagedProperty(value="#{imprimirDocumentosCobrancaBean}")
	private ImprimirDocumentosCobrancaBean imprimirDocumentosCobrancaBean = null;

	private ListarPosicaoContratoProcess process = null;
    private EntityList<Lancamento> list = null;
    private Calendar dataAtual = CalendarUtils.getCalendar();
    
	public void loadParams() throws Exception {
    }

    public void doReset() throws BusinessException, Exception {
    }

    public void doReload() throws BusinessException, Exception {
    }
    
    public EntityList<Lancamento> getList() throws Exception {
    	return list;
	}

    /**
     * Este método indica que existe um processo ativo e tem uma
     * lista de entidades prontas para serem visualizadas.
     * @return
     */
    public boolean isVisualizando(){
    	return (this.list != null);
    }
    
    /**
     * Fornece um meio de acesso rapido para o array de
     * entidades
     * @return
     */
    public Object[] getArray(){
    	return list.toArray();
    }
    
    public int getSize(){
    	return list.size();
    }
    
    /**
     * Lucio 20080817:Trata o exception dentro deste método porque este mestodo eh chamado por ajax
     * e um throw pode causar erro, eu acho.
     * @return
     */
    public ListarPosicaoContratoProcess getProcess() {
    	try {
    		if (process == null)
    			process = (ListarPosicaoContratoProcess)this.getApplicationBean().getProcessManager().createProcessByName(ListarPosicaoContratoProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
    	} catch (ProcessException e) {
    		e.printStackTrace();
    	}
        return process;
    }

//    public void doValidarCpfCnpj() {
//    	log.debug("ListarPosicaoContratoBean.doValidarCpfCnpj");
//    	if (!getProcess().runValidarCfpCnpj())
//    		FacesUtils.addErrorMsgs(getProcess().getMessageList());
//    }
    
    public void doVisualizar() {
    	log.debug("ListarPosicaoContratoBean.doVisualizar");
    
    	if (getProcess().runListar())
    		list = getProcess().getLancamentos();
    }
    
    public String actionCancelarSelecionados() {
        log.debug("ListarPosicaoContratoBean.actionCancelarSelecionados");
        try {
        	IEntityList<Lancamento> entities = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntityList(null, Lancamento.class);
        	// Adiciona os lancamentos selecionados no processo de cancelamento
        	for(IEntity<Lancamento> entity: this.getProcess().getLancamentos()){
        		if(entity.isSelected()){
        			entities.add(entity);
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
        log.debug("ListarPosicaoContratoBean.actionQuitarSelecionados");
        try {
        	IEntityList<Lancamento> entities = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntityList(null, Lancamento.class);
        	// Adiciona os lancamentos selecionados no processo de cancelamento
        	for(IEntity<Lancamento> entity: this.getProcess().getLancamentos()){
        		if(entity.isSelected()){
        			entities.add(entity);
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
        log.debug("ListarPosicaoContratoBean.actionAlterarVencimentoDocumentosCobrancaSelecionados");
        try {
        	IEntityList<DocumentoCobranca> entities = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntityList(null, DocumentoCobranca.class);
        	// Adiciona os lancamentos selecionados no processo de cancelamento
        	for(IEntity<Lancamento> entity: this.getProcess().getLancamentos()){
        		if(entity.isSelected()){
        			if(entity.getObject().getDocumentoCobranca()!=null){
        				IEntity<DocumentoCobranca> doc = entity.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().<DocumentoCobranca>getAsEntity();
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
        log.debug("ListarPosicaoContratoBean.actionImprimirDocumentosCobrancaSelecionados");
        try {
        	IEntityList<DocumentoCobranca> entities = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntityList(null, DocumentoCobranca.class);
        	// Adiciona os lancamentos selecionados no processo de cancelamento
        	for(IEntity<Lancamento> entity: this.getProcess().getLancamentos()){
        		if(entity.isSelected()){
        			if(entity.getObject().getDocumentoCobranca()!=null){
        				IEntity<DocumentoCobranca> doc = entity.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().<DocumentoCobranca>getAsEntity();
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

    public void actionImprimirCartasCobranca() {
        log.debug("ListarPosicaoContratoBean.actionImprimirCartaCobranca");
//        if (process.gerarCartasCobranca()) {
//            /* Adiciona as mensagens de info no Faces */
//            FacesUtils.addInfoMsgs(process.getMessageList());
//        }else{
//            /* Adiciona as mensagens de erro no Faces */
//            FacesUtils.addErrorMsgs(process.getMessageList());
//        }
    }

    public void actionGerarEtiquetas() {
        log.debug("ListarPosicaoContratoBean.actionGerarEtiqueta");
//        if (process.gerarEtiquetas()) {
//            /* Adiciona as mensagens de info no Faces */
//            FacesUtils.addInfoMsgs(process.getMessageList());
//        }else{
//            /* Adiciona as mensagens de erro no Faces */
//            FacesUtils.addErrorMsgs(process.getMessageList());
//        }
    }


//	public void setCpfCnpj(String cpfCnpj) {
//		if(!process.runValidarCfpCnpj())
//			FacesUtils.addErrorMsgs(process.getMessageList());
//	}

	public Calendar getDataAtual() {
		return dataAtual;
	}

	@Override
	public String actionStart() {
		return FACES_VIEW_PASSO_1;
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

	public String getRunnableEntityProcessName() {
		return ListarPosicaoContratoProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		
		if (!this.getProcess().runWithEntity(entity)){
			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}

		/* Alimenta a lista com os laçamentos processados */
		this.list = getProcess().getLancamentos();
		
		return FACES_VIEW_PASSO_1;
	}

}
