package br.com.orionsoft.financeiro.contabilidade.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.core.util.StringUtils;

/**
 * Este serviço analisa os movimentos nas contas
 * para gerar um arquivo para exportação para a Contabilidade
 * 
 * <p>Argumentos:</p>
 * IN_DATA_INICIAL: (Calendar) Data inicial para o filtro da movimentação para exportação
 * IN_DATA_FINAL: (Calendar) Data final para o filtro da movimentação para exportação

 * @version 2012
 * @author lucio
 */
public class ExportarMovimentoContabilService extends ServiceBasic {
	
	public static final String SERVICE_NAME = "ExportarMovimentoContabilService";

	public static final String IN_DATA_INICIAL = "dataInicial";
	public static final String IN_DATA_FINAL = "dataFinal";
	public static final String IN_OUTPUT_STREAM = "outputStream";
	public static final String IN_QUERY_PREVISTO = "queryPrevisto";
	public static final String IN_QUERY_MOVIMENTO = "queryMovimento";
	public static final String IN_QUERY_COMPENSACAO = "queryCompensacao";
	
	public static final int OUT_PREVISTO = 0;
	public static final int OUT_MOVIMENTO = 1;
	public static final int OUT_COMPENSACAO = 2;
	public static final int OUT_ARQUIVO_EXPORTACAO = 3;
	
	private static final String ARQUIVO_NOME = "Movimento_Contabil";
	private static final String ARQUIVO_EXTENSAO = ".txt";
	private static final String ARQUIVO_SEPARADOR_CAMPOS = ";";
	private static final String ARQUIVO_SEPARADOR_CONTRATO = ".";
	private static final String ARQUIVO_SEPARADOR_HISTORICO = "|";
	private static final String ARQUIVO_DEFAULT_NULL = "";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute(ServiceData serviceData) throws ServiceException {
		/*
		 * Parâmetros opcionais.
		 */
		
        Calendar inDataInicial = (serviceData.getArgumentList().containsProperty(IN_DATA_INICIAL) ? 
                (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_INICIAL) : null);
        Calendar inDataFinal = (serviceData.getArgumentList().containsProperty(IN_DATA_FINAL) ? 
        		(Calendar) serviceData.getArgumentList().getProperty(IN_DATA_FINAL) : null);
        Boolean inQueryPrevisto = (serviceData.getArgumentList().containsProperty(IN_QUERY_PREVISTO) ? 
        		(Boolean) serviceData.getArgumentList().getProperty(IN_QUERY_PREVISTO) : false);
        Boolean inQueryMovimento = (serviceData.getArgumentList().containsProperty(IN_QUERY_MOVIMENTO) ? 
        		(Boolean) serviceData.getArgumentList().getProperty(IN_QUERY_MOVIMENTO) : false);
        Boolean inQueryCompensacao = (serviceData.getArgumentList().containsProperty(IN_QUERY_COMPENSACAO) ? 
        		(Boolean) serviceData.getArgumentList().getProperty(IN_QUERY_COMPENSACAO) : false);
        
        PrintWriter printWriterExportacao = null;
		try {
			List queryResult = new ArrayList();
			serviceData.getOutputData().add(OUT_PREVISTO, queryResult);
			serviceData.getOutputData().add(OUT_MOVIMENTO, queryResult);
			serviceData.getOutputData().add(OUT_COMPENSACAO, queryResult);
			
			String arquivoNome = ARQUIVO_NOME + "_"
					+ CalendarUtils.formatToSQLDate(inDataInicial) + "_a_"
					+ CalendarUtils.formatToSQLDate(inDataFinal) + "_";
			File fileExportacao = File.createTempFile(arquivoNome, ARQUIVO_EXTENSAO);
			FileWriter fileWriterExportacao = new FileWriter(fileExportacao);
			printWriterExportacao = new PrintWriter(fileWriterExportacao, true);
			
			printWriterExportacao.println(getCabecalho());
			
			if (inQueryPrevisto) {
				queryResult = executeQuery(serviceData.getCurrentSession(),
						QueryLancamentoContabil.SELECT_PREVISTO, inDataInicial,
						inDataFinal);
				adicionarMovimentoArquivo(printWriterExportacao, OUT_PREVISTO, queryResult);
				serviceData.getOutputData().add(OUT_PREVISTO, queryResult);
			}

			if (inQueryMovimento) {
				queryResult = executeQuery(serviceData.getCurrentSession(),
						QueryLancamentoContabil.SELECT_MOVIMENTO, inDataInicial,
						inDataFinal);
				adicionarMovimentoArquivo(printWriterExportacao, OUT_MOVIMENTO, queryResult);
				serviceData.getOutputData().add(OUT_MOVIMENTO, queryResult);
			}

			if (inQueryCompensacao) {
				queryResult = executeQuery(serviceData.getCurrentSession(),
						QueryLancamentoContabil.SELECT_COMPENSACAO, inDataInicial,
						inDataFinal);
				adicionarMovimentoArquivo(printWriterExportacao, OUT_COMPENSACAO, queryResult);
				serviceData.getOutputData().add(OUT_COMPENSACAO, queryResult);
			}
			
			serviceData.getOutputData().add(OUT_ARQUIVO_EXPORTACAO, fileExportacao);

		} catch (HibernateException e) {
            throw new ServiceException(MessageList.createSingleInternalError(e));
		
		} catch (IOException e) {
			throw new ServiceException(MessageList.createSingleInternalError(e));
		
		} finally {
			if (printWriterExportacao != null) {
				printWriterExportacao.close();
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private List executeQuery(Session session, String queryString,
			Calendar dataInicial, Calendar dataFinal) throws HibernateException {
		long time = System.currentTimeMillis();
		
		Query query = session.createQuery(queryString);
		query.setCalendar(IN_DATA_INICIAL, dataInicial);
		query.setCalendar(IN_DATA_FINAL, dataFinal);
		
		System.out.println("====================> Tempo decorrido: "+ (System.currentTimeMillis() - time));
		
		return query.list();
	}
	
	private void adicionarMovimentoArquivo(PrintWriter printWriter, int tipo,
			List<QueryLancamentoContabil> lancamentos) throws IOException, ServiceException {
		
		StringBuilder builder = null;
		
		for (QueryLancamentoContabil lancamento : lancamentos) {
			// Adiciona a data
			builder = new StringBuilder()
				.append(StringUtils.defaultIfBlank(
						CalendarUtils.formatDate(lancamento.getDataCompetencia()), ARQUIVO_DEFAULT_NULL))
				.append(ARQUIVO_SEPARADOR_CAMPOS);
			

			// Adiciona débito e crédito
			String contaDebito = getContaCredito(lancamento, tipo);
			String contaCredito = getContratDebito(lancamento);

			// Não exporta para o arquivo item ou conta com configuração em BRANCO
			if(!contaDebito.isEmpty() && !contaCredito.isEmpty()){
				if (lancamento.getValor().signum() >= 0) {
					builder.append(contaDebito).append(ARQUIVO_SEPARADOR_CAMPOS).append(contaCredito);
				} else {
					builder.append(contaCredito).append(ARQUIVO_SEPARADOR_CAMPOS).append(contaDebito);
				}

				// Adiciona histórico
				builder.append(ARQUIVO_SEPARADOR_CAMPOS).append(getHistorico(lancamento));

				//Adiciona valor
				builder.append(ARQUIVO_SEPARADOR_CAMPOS).append(
						StringUtils.defaultIfBlank(DecimalUtils.formatBigDecimal(lancamento.getValor().abs()),
								ARQUIVO_DEFAULT_NULL));

				printWriter.println(builder.toString());
			}
		}
	}
	
	private String getCabecalho() {
		return "DATA;CONTADEBITO;CONTACREDITO;HISTORICO;VALOR";
	}
	
	private String getContaCredito(QueryLancamentoContabil lancamento,
			int tipo) throws ServiceException {
		String result = null;
		switch (tipo) {
		case OUT_PREVISTO:
			result = StringUtils.defaultIfBlank(lancamento.getContaPrevistaCcc(), ARQUIVO_DEFAULT_NULL);
			break;

		case OUT_MOVIMENTO:
			result = StringUtils.defaultIfBlank(lancamento.getContaMovimentoCcc(), ARQUIVO_DEFAULT_NULL);
			break;

		case OUT_COMPENSACAO:
			result = StringUtils.defaultIfBlank(lancamento.getContaCompensacaoCcc(), ARQUIVO_DEFAULT_NULL);
			break;

		default:
			throw new ServiceException(MessageList.create(
					ExportarMovimentoContabilService.class,
					"MOVIMENTO_CONTABIL_TIPO_INVALIDO", tipo));
		}
		
		return result;
	}
	
	/**
	 * Concatena os códigos das contas na ordem:
	 *   CENTRO_CUSTO
	 *   ITEM_CUSTO
	 *   CONTRATO
	 * @param lancamento
	 * @return
	 */
	private String getContratDebito(QueryLancamentoContabil lancamento) {
		String centroCusto = lancamento.getCentroCustoCcc();
		String itemCusto = lancamento.getItemCustoCcc();
		String contrato = lancamento.getContratoCcc();
		
		return new StringBuilder()
				.append(StringUtils.defaultIfBlank(centroCusto, ARQUIVO_DEFAULT_NULL))
				
				.append(StringUtils.isNotBlank(centroCusto)
						&& StringUtils.isNotBlank(itemCusto) ? ARQUIVO_SEPARADOR_CONTRATO : "")
				
				.append(StringUtils.defaultIfBlank(itemCusto, ARQUIVO_DEFAULT_NULL))
				
				.append(StringUtils.isNotBlank(contrato)
						&& (StringUtils.isNotBlank(centroCusto) || StringUtils.isNotBlank(itemCusto)) 
						? ARQUIVO_SEPARADOR_CONTRATO : "")
				
				.append(StringUtils.defaultIfBlank(contrato, ARQUIVO_DEFAULT_NULL)).toString();
	}

	private String getHistorico(QueryLancamentoContabil lancamento) {
		return new StringBuilder()
				.append(StringUtils.defaultIfBlank(lancamento.getNomePessoa(), ARQUIVO_DEFAULT_NULL))
				.append(ARQUIVO_SEPARADOR_HISTORICO)
				.append(StringUtils.defaultIfBlank(lancamento.getDocumentoPessoa(), ARQUIVO_DEFAULT_NULL))
				.append(ARQUIVO_SEPARADOR_HISTORICO)
				.append(StringUtils.defaultIfBlank(lancamento.getDescricaoLancamento(), ARQUIVO_DEFAULT_NULL))
				.append(ARQUIVO_SEPARADOR_HISTORICO)
				.append(StringUtils.defaultIfBlank(lancamento.getPagamentoCategoria(), ARQUIVO_DEFAULT_NULL))
				.append(ARQUIVO_SEPARADOR_HISTORICO)
				.append(StringUtils.defaultIfBlank(lancamento.getPagamentoNumero(), ARQUIVO_DEFAULT_NULL)).toString();
	}
	
}
