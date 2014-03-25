/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.view.jsf;

import java.text.ParseException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.financeiro.gerenciador.process.ListarItemCustoProcess;
import br.com.orionsoft.financeiro.gerenciador.services.ListarItemCustoService.Coluna;
import br.com.orionsoft.financeiro.gerenciador.services.ListarItemCustoService.QueryItemCusto;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla a view de listagem dos movimentos 
 * que estão nas contas agrupados por data e item de custo
 * 
 */
@ManagedBean
@SessionScoped
public class ListarItemCustoBean extends BeanSessionBasic implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "listarItemCustoBean";

	public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/gerenciadorListarItemCusto?faces-redirect=true";

	private ListarItemCustoProcess process = null;
    
    private boolean colunaConta = true;
    private boolean colunaData = true;
    private boolean colunaCentroCusto = true;
    private List<QueryItemCusto> beanListQuitado = null;
//    private String creditoQuitado;
//    private String debitoQuitado;
//    private String saldoInicial;
//    private String saldoFinal;
//    private String dataInicial = null;
//    private String dataFinal = null;

    public void doReload() throws BusinessException, Exception {
    }

    public void doReset() throws BusinessException, Exception {
    }
    
    public void doVisualizar() throws ParseException {
        log.debug("ListarMovimentoBean.doVisualizar");
        
        this.colunaConta = this.colunaData = this.colunaCentroCusto = false;
        int[] colunas = process.getColunaList();
        for (int i = 0; i < colunas.length; i++) {
            if (colunas[i] == Coluna.DATA.ordinal())
                this.colunaData = true;
            if (colunas[i] == Coluna.CONTA.ordinal())
                this.colunaConta = true;
            if (colunas[i] == Coluna.CENTRO_CUSTO.ordinal())
                this.colunaCentroCusto = true;
        }
        
//        beanListLancado = null;
        beanListQuitado = null;
        beanListQuitado = getProcess().retrieveQuitados();
//        creditoQuitado = DecimalUtils.formatBigDecimal(process.getTotalCredito());
//        debitoQuitado = DecimalUtils.formatBigDecimal(process.getTotalDebito());
//        saldoInicial = DecimalUtils.formatBigDecimal(process.getSaldoInicial());
//        saldoFinal = DecimalUtils.formatBigDecimal(process.getSaldoFinal());
    }

    public Object[] getArrayQuitado() {
        return beanListQuitado.toArray();
    }

    public List<QueryItemCusto> getBeanListQuitado() throws Exception {
        return beanListQuitado;
    }

    public ListarItemCustoProcess getProcess() {
        try {
            if (process == null)
                process = (ListarItemCustoProcess) this.getApplicationBean().getProcessManager().createProcessByName(ListarItemCustoProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
        } catch (ProcessException e) {
            e.printStackTrace();
        }
        return process;
    }

    public int getSizeQuitado() {
        return beanListQuitado.size();
    }

    /**
     * Este método indica que existe um processo ativo e tem uma lista de
     * entidades prontas para serem visualizadas.
     * 
     * @return
     */
    public boolean isVisualizandoQuitado() {
        return beanListQuitado != null;
    }

    public void loadParams() throws Exception {
    }

    public boolean isColunaConta() {
        return colunaConta;
    }

    public void setColunaConta(boolean colunaConta) {
        this.colunaConta = colunaConta;
    }

    public boolean isColunaData() {
        return colunaData;
    }

    public void setColunaData(boolean colunaData) {
        this.colunaData = colunaData;
    }

	public boolean isColunaCentroCusto() {
		return colunaCentroCusto;
	}

	public void setColunaCentroCusto(boolean colunaCentroCusto) {
		this.colunaCentroCusto = colunaCentroCusto;
	}

	/* IRunnableProcessView */
	@Override
	public String actionStart() {
		return FACES_VIEW_PASSO_1;
	}

	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return ListarItemCustoProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		
		if (!this.getProcess().runWithEntity(entity)){
			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}

		/* Alimenta a lista com os laçamentos processados */
		try {
			doVisualizar();
		} catch (ParseException e) {
			FacesUtils.addErrorMsgs(MessageList.createSingleInternalError(e));
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		
		return FACES_VIEW_PASSO_1;
	}

	public String runWithEntities(IEntityCollection<?> entities) {
		return FacesUtils.FACES_VIEW_FAILURE;
	}
}
