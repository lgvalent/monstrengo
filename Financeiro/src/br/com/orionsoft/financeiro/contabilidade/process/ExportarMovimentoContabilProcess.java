package br.com.orionsoft.financeiro.contabilidade.process;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import br.com.orionsoft.financeiro.contabilidade.services.ExportarMovimentoContabilService;
import br.com.orionsoft.financeiro.contabilidade.services.QueryLancamentoContabil;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessParamBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este processo realiza a exportação do movimento para um arquivo formatado
 * 
 * @author Lucio 20120822
 */
@ProcessMetadata(label="Exportar movimento contábil", hint="Exporta a movimentação financeira para a contabilidade usando um arquivo formatado pré-definido", description="O movimento do perído informado será analisado e um arquivo será gerado para download.<br/>O processo de exportação analisa os lançamentos e movimentos do período e utiliza os códigos das contas contábeis cadastrados nos contratos, contas, centros de custo e items de custo.<br/> Por esta razão, é indispensável que todos os códigos estejam cadastrados corretamente.")
public class ExportarMovimentoContabilProcess extends ProcessBasic implements IRunnableEntityProcess {
	public static final String PROCESS_NAME="ExportarMovimentoContabilProcess";
	
	private ProcessParamBasic<Calendar> paramDataInicial = new ProcessParamBasic<Calendar>(Calendar.class, CalendarUtils.getCalendarFirstMonthDay(), true, this);
	private ProcessParamBasic<Calendar> paramDataFinal = new ProcessParamBasic<Calendar>(Calendar.class, CalendarUtils.getCalendarLastMonthDay(), true, this);
	
	//TODO verificar por que ao utilizar ProcessParamBasic<Boolean> e passar getValue() para o serviço ocorre ClassCastException de String para Boolean
//	private ProcessParamBasic<Boolean> paramPrevisto = new ProcessParamBasic<Boolean>(Boolean.class, true, true, this);
	private Boolean paramPrevisto = true;
	private Boolean paramMovimento = true;
	private Boolean paramCompensacao = true;
	
	private List<QueryLancamentoContabil> resultPrevisto;
	private List<QueryLancamentoContabil> resultMovimento;
	private List<QueryLancamentoContabil> resultCompensacao;
	private File resultArquivoExportacao;
	
	public boolean runExportar() {
		super.beforeRun();
        try {
            ServiceData sd = new ServiceData(ExportarMovimentoContabilService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(ExportarMovimentoContabilService.IN_DATA_INICIAL, this.paramDataInicial.getValue());
            sd.getArgumentList().setProperty(ExportarMovimentoContabilService.IN_DATA_FINAL, this.paramDataFinal.getValue());
            sd.getArgumentList().setProperty(ExportarMovimentoContabilService.IN_QUERY_PREVISTO, this.paramPrevisto);
            sd.getArgumentList().setProperty(ExportarMovimentoContabilService.IN_QUERY_MOVIMENTO, this.paramMovimento);
            sd.getArgumentList().setProperty(ExportarMovimentoContabilService.IN_QUERY_COMPENSACAO, this.paramCompensacao);
            
            this.getProcessManager().getServiceManager().execute(sd);

            /* Pegas as mensagens do serviço */
            this.getMessageList().add(sd.getMessageList());
            
			if (sd.getMessageList().isTransactionSuccess()) {

				this.resultPrevisto = sd.getOutputData(ExportarMovimentoContabilService.OUT_PREVISTO);
				this.resultMovimento = sd.getOutputData(ExportarMovimentoContabilService.OUT_MOVIMENTO);
				this.resultCompensacao = sd.getOutputData(ExportarMovimentoContabilService.OUT_COMPENSACAO);
				
				this.resultArquivoExportacao = sd.getOutputData(ExportarMovimentoContabilService.OUT_ARQUIVO_EXPORTACAO);
				
				return true;
			}
			
			return false;
        } catch (BusinessException e) {
            /* Armazenando a lista de erros */
            this.getMessageList().addAll(e.getErrorList());
            return false;
		}
    }
	
	public String getProcessName() {
		return PROCESS_NAME;
	}

	public ProcessParamBasic<Calendar> getParamDataInicial() {
		return paramDataInicial;
	}

	public void setParamDataInicial(ProcessParamBasic<Calendar> paramDataInicial) {
		this.paramDataInicial = paramDataInicial;
	}

	public ProcessParamBasic<Calendar> getParamDataFinal() {
		return paramDataFinal;
	}

	public void setParamDataFinal(ProcessParamBasic<Calendar> paramDataFinal) {
		this.paramDataFinal = paramDataFinal;
	}

	public Boolean getParamPrevisto() {
		return paramPrevisto;
	}

	public void setParamPrevisto(Boolean paramPrevisto) {
		this.paramPrevisto = paramPrevisto;
	}

	public Boolean getParamMovimento() {
		return paramMovimento;
	}

	public void setParamMovimento(Boolean paramMovimento) {
		this.paramMovimento = paramMovimento;
	}

	public Boolean getParamCompensacao() {
		return paramCompensacao;
	}

	public void setParamCompensacao(Boolean paramCompensacao) {
		this.paramCompensacao = paramCompensacao;
	}
	
	public List<QueryLancamentoContabil> getResultPrevisto() {
		return resultPrevisto;
	}

	public List<QueryLancamentoContabil> getResultMovimento() {
		return resultMovimento;
	}
	
	public List<QueryLancamentoContabil> getResultCompensacao() {
		return resultCompensacao;
	}

	public File getResultArquivoExportacao() {
		return resultArquivoExportacao;
	}

	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();
		boolean result = false;
//		if (entity.getInfo().getType() == LancamentoMovimento.class) {
//			this.lancamentoMovimento = (IEntity<LancamentoMovimento>) entity;
//			resultPrevisto = true;
//		} else {
//			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
//		}
		return result;
	}

	
}
