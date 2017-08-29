package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaBean;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaException;
import br.com.orionsoft.financeiro.documento.cobranca.GerenciadorDocumentoCobrancaBasic;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultado;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.services.GerarBancoRemessaService;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.services.ReceberBancoRetornoService;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.services.QuitarLancamentoService;
import br.com.orionsoft.financeiro.utils.UtilsJuros;
import br.com.orionsoft.financeiro.utils.UtilsOcorrencia;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;


/**
 * <p>Esta classe implementa o gerenciador de documento do tipo titulo.<p>
 *
 * @author Lucio
 * 
 * @spring.bean id="GerenciadorDocumentoTitulo" init-method="registrarGerenciador"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 * @spring.property name="provedorBanco" ref="ProvedorBanco"
 * @spring.property name="preenchimentoManual" value="false"
 */
public class GerenciadorDocumentoTitulo extends GerenciadorDocumentoCobrancaBasic
{

	public static final String GERENCIADOR_NOME = "GerenciadorDocumentoTitulo";

	public static final String LAYOUT_0 = "Nenhum documento impresso";
	public static final String LAYOUT_1 = "Boleto impresso pelo banco e inclu�do na remessa";
	public static final String LAYOUT_2 = "Boleto 2 vias em folha A4 (com espa�o superior)";
	public static final String LAYOUT_3 = "Boleto 2 vias em folha A5";

	public static final int LAYOUT_INT_0 = 0;
	public static final int LAYOUT_INT_1 = 1;
	public static final int LAYOUT_INT_2 = 2;
	public static final int LAYOUT_INT_3 = 3;

	private static final String LAYOUT_FILE_2 = "Boleto_A4.jrxml";
	private static final String LAYOUT_FILE_3 = "Boleto_A5.jrxml";

	public static final String CNAB_240 = "CNAB240";
	public static final String CNAB_400 = "CNAB400";

	public static final int OCORRENCIA_VAZIA = -1; //constante que indica que n�o foi encontrada uma ocorr�ncia (T�tulo importado por exemplo)

	private static ProvedorBanco provedorBanco = null;

	public String getNome() {
		return GERENCIADOR_NOME;
	}
	
	/**
	 */
	@Override
	public void registrarGerenciador() {
		super.registrarGerenciador();
		
		if(provedorBanco != null)
			throw new RuntimeException("ProvedorBanco j� iniciado anteriormente. O m�todo registrarGerenciador() n�o pode ser executado.");
		
		provedorBanco = new ProvedorBanco();
		provedorBanco.setProvedorDocumentoCobranca(getProvedorDocumentoCobranca());
		provedorBanco.init();
	}

	public IProvedorBanco getProvedorBanco() {
		return provedorBanco;
	}

	public IEntity<? extends DocumentoCobranca> criarDocumento(IEntity<Contrato> contrato, IEntity<? extends DocumentoCobrancaCategoria> documentoCobrancaCategoria, Calendar dataDocumento, Calendar dataVencimento, BigDecimal valorDocumento, Transacao transacao, ServiceData serviceDataOwner) throws BusinessException {

		IEntity<? extends DocumentoCobranca> titulo = super.criarDocumento(DocumentoTitulo.class, contrato, documentoCobrancaCategoria, dataDocumento, dataVencimento, valorDocumento, transacao, serviceDataOwner); 

		return titulo;
	}

	public void lancarDocumento(IEntity<? extends DocumentoCobranca> documento, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		try {
			DocumentoTitulo oTitulo = (DocumentoTitulo)documento.getObject();
			IGerenciadorBanco gerenciadorBanco = this.getProvedorBanco().retrieveGerenciadorBanco(oTitulo.getCedente().getContaBancaria().getBanco().getCodigo());
			documento.setPropertyValue(DocumentoTitulo.NUMERO_DOCUMENTO, gerenciadorBanco.formatarNossoNumero(documento, serviceDataOwner));

			UtilsCrud.update(this.getProvedorDocumentoCobranca().getServiceManager(), documento, serviceDataOwner);

			//criando a ocorr�ncia 'registrar remessa no banco'
			documento = atualizarOcorrencia(documento, Ocorrencia.REMESSA_REGISTRAR.getCodigo(), serviceDataOwner);

		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}

	public BigDecimal calcularVencimento(IEntity<? extends DocumentoCobranca> documento, Calendar dataPagamento, ServiceData serviceDataOwner) throws DocumentoCobrancaException {
		try
		{
			if(super.verificarNecessidadeCalcularJurosMulta(documento.getObject(), dataPagamento, serviceDataOwner)){
				/* Calcula os valores */
				BigDecimal valorDesconto = documento.getProperty(DocumentoTitulo.VALOR_DESCONTO).getValue().isValueNull()?BigDecimal.ZERO:documento.getProperty(DocumentoTitulo.VALOR_DESCONTO).getValue().getAsBigDecimal();
				BigDecimal valorAcrescimo =  documento.getProperty(DocumentoTitulo.VALOR_ACRESCIMO).getValue().isValueNull()?BigDecimal.ZERO:documento.getProperty(DocumentoTitulo.VALOR_ACRESCIMO).getValue().getAsBigDecimal();
				BigDecimal valorDocumento = documento.getProperty(DocumentoCobranca.VALOR).getValue().getAsBigDecimal();
				BigDecimal jurosMora = documento.getProperty(DocumentoTitulo.DOCUMENTO_COBRANCA_CATEGORIA).getValue().getAsEntity().getProperty(DocumentoCobrancaCategoria.JUROS_MORA).getValue().getAsBigDecimal();
				BigDecimal multa = documento.getProperty(DocumentoTitulo.DOCUMENTO_COBRANCA_CATEGORIA).getValue().getAsEntity().getProperty(DocumentoCobrancaCategoria.MULTA_ATRASO).getValue().getAsBigDecimal();
				BigDecimal valorJuros = UtilsJuros.calcularJuros(valorDocumento, jurosMora, documento.getProperty(DocumentoTitulo.DATA_VENCIMENTO).getValue().getAsCalendar(), dataPagamento, 0);
				BigDecimal valorMulta = UtilsJuros.calcularMulta(valorDocumento, multa, BigDecimal.ZERO, null, documento.getProperty(DocumentoTitulo.DATA_VENCIMENTO).getValue().getAsCalendar(), dataPagamento, 0);
				BigDecimal valorCobrado = valorDocumento.add(valorJuros).add(valorMulta).add(valorAcrescimo).subtract(valorDesconto);

				/* Verifica se o valor cobrado � consistente, ou seja,
				 * se ele � positivo */
				if(valorCobrado.signum()==-1)
					throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoTitulo.class, "ERRO_VALOR_INCONSISTENTE", valorCobrado.toString()));

				/* Preenche o documento com os valores calculados */
				//			documento.setPropertyValue(DocumentoTitulo.VALOR, valorCobrado);// Ajusta o valor o documento para sair no C�DIGO DE BARRAS o VALOR PAGO
				documento.setPropertyValue(DocumentoTitulo.VALOR_JUROS, valorJuros);
				documento.setPropertyValue(DocumentoTitulo.VALOR_MULTA, valorMulta);
				documento.setPropertyValue(DocumentoTitulo.VALOR_PAGO, valorCobrado);

				/* Ajusta a DATA de VENCIMENTO */
				documento.setPropertyValue(DocumentoTitulo.DATA_VENCIMENTO, dataPagamento);

				/* Retorna o valor total que dever� ser cobrado */
				return valorCobrado;
			}
			/* Retorna o valor original se documento n�o vencido ou vencido em FERIADO */
			return documento.getPropertyValue(DocumentoTitulo.VALOR);
		} catch (BusinessException e)
		{
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}

	public void quitarDocumento(DocumentoCobranca documento, Conta conta, Calendar dataQuitacao, Calendar dataCompensacao, ServiceData serviceDataOwner) throws DocumentoCobrancaException {
		try {
			/*
			 * Andre, 25/06/2008: Segundo o Antonio, n�o precisa mais criar a lista de itens de lan�amento,
			 * basta inserir o valores de juros e multa diretamente no lan�amento vinculado com o documento,
			 * por�m, deve ser pensado como tratar juros e multas para documentos com mais de um lan�amento.
			 */
			DocumentoTitulo oTitulo = (DocumentoTitulo) documento;
			/*
			 * TODO - Verificar como lan�ar juros e multas caso o documento contenha mais de um lan�amento.
			 * Por enquanto esses valores s�o inseridos apenas no primeiro lan�amento encontrado.
			 */
			if (oTitulo.getLancamentos().size() > 0){
				Lancamento oLancamento = oTitulo.getLancamentos().get(0);
				
				/*
				 * Andre, 12/07/2008: Obtendo os valores de juros, multa, acrescimo e desconto
				 * Segundo Antonio:
				 * "no Titulo, desconto �: valorDesconto, valorTarifa, valorIOF e outrasDeducoes
				 * e acrescimo �: valorAcrescimo"
				 */
				BigDecimal valorJuros = oTitulo.getValorJuros() == null ? BigDecimal.ZERO : oTitulo.getValorJuros();
				BigDecimal valorMulta = oTitulo.getValorMulta() == null ? BigDecimal.ZERO: oTitulo.getValorMulta();
				BigDecimal valorAcrescimo = oTitulo.getValorAcrescimo() == null ? BigDecimal.ZERO : oTitulo.getValorAcrescimo();
				BigDecimal valorDesconto = oTitulo.getValorDesconto() == null ? BigDecimal.ZERO : oTitulo.getValorDesconto();
				BigDecimal valorTarifa = oTitulo.getValorTarifa() == null ? BigDecimal.ZERO : oTitulo.getValorTarifa();
				BigDecimal valorIof = oTitulo.getValorIOF() == null ? BigDecimal.ZERO : oTitulo.getValorIOF();
				BigDecimal valorOutrasDeducoes = oTitulo.getOutrasDeducoes() == null ? BigDecimal.ZERO : oTitulo.getOutrasDeducoes();
				BigDecimal valorPago = oTitulo.getValorPago() == null ? BigDecimal.ZERO : oTitulo.getValorPago();
				
				/*
				 * TODO como saber se quitou o documento de cobran�a com um outro documento de pagamento?
				 */
				ServiceData quitarLancamento = new ServiceData(QuitarLancamentoService.SERVICE_NAME, serviceDataOwner);
				quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_CONTA, conta);
				// 20090331 Lucio - � data da ultima ocorr�ncia, pois foi quando o cliente pagou o titulo no banco
				quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA, oTitulo.getDataUltimaOcorrencia());
				quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA_COMPENSACAO_OPT, dataCompensacao);
				//TODO - como pegar um documento de pagamento escolhido na tela?
				if (oLancamento.getDocumentoPagamento() != null)
					quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_DOCUMENTO_PAGAMENTO_OPT, oLancamento.getDocumentoPagamento());
				quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_LANCAMENTO, oLancamento);
				quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_VALOR, valorPago.subtract(valorJuros).subtract(valorMulta).subtract(valorAcrescimo).add(valorDesconto));
				quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_JUROS_OPT, valorJuros);
				quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_MULTA_OPT, valorMulta);
				quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_ACRESCIMO_OPT, valorAcrescimo);
				quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_DESCONTO_OPT, valorDesconto);
				quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_SUBSTITUIR_VALOR_OPT, true);

				this.getProvedorDocumentoCobranca().getServiceManager().execute(quitarLancamento);
				
				
				/* Lan�a os IOF e TARIFA se o valor for fornecido */
				if(!oTitulo.getCedente().isBancoGeraOcorrenciaValorTarifa()){
					Cedente cedente = oTitulo.getCedente();
					IEntityList<LancamentoItem> lancamentoItemList = this.getProvedorDocumentoCobranca().getServiceManager().getEntityManager().getEntityList(null, LancamentoItem.class);
					LancamentoItem oLancamentoItem = null;

					if(!DecimalUtils.isZero(valorIof)){
						oLancamentoItem = lancamentoItemList.getRunEntity().getObject();
						oLancamentoItem.setCentroCusto(cedente.getCentroCustoGeral());
						oLancamentoItem.setItemCusto(cedente.getItemCustoIof());
						oLancamentoItem.setValor(valorIof);
						lancamentoItemList.runAdd();
					}

					if(!DecimalUtils.isZero(valorTarifa)){
						oLancamentoItem = lancamentoItemList.getRunEntity().getObject();
						oLancamentoItem.setCentroCusto(cedente.getCentroCustoGeral());
						oLancamentoItem.setItemCusto(cedente.getItemCustoIof());
						oLancamentoItem.setValor(valorIof);
						lancamentoItemList.runAdd();
					}

					/* Verifica se houve algum lan�amento extra para gerar o lan�amento */
					if(lancamentoItemList.getSize() > 0)
						super.inserirLancamentoItems(oTitulo, conta, dataQuitacao, Transacao.DEBITO, lancamentoItemList.getObjectList(), "Lan�amento autom�tico de IOF e TARIFA",  serviceDataOwner);
				}

			}
			
		} catch (BusinessException e)
		{
			throw new DocumentoCobrancaException(e.getErrorList());
		}

//		try
//		{
//		DocumentoTitulo oTitulo = (DocumentoTitulo) documento;
////
////		/* Pegando os valores */
////		BigDecimal valorDesconto = oTitulo.getValorDesconto() == null ? BigDecimal.ZERO : oTitulo.getValorDesconto();
////		BigDecimal valorAcrescimo = oTitulo.getValorAcrescimo() == null ? BigDecimal.ZERO : oTitulo.getValorAcrescimo();
////		BigDecimal valorJuros = oTitulo.getValorJuros() == null ? BigDecimal.ZERO : oTitulo.getValorJuros();
////		BigDecimal valorMulta = oTitulo.getValorMulta() == null ? BigDecimal.ZERO : oTitulo.getValorMulta();
//		BigDecimal valorIof = oTitulo.getValorIOF() == null ? BigDecimal.ZERO : oTitulo.getValorIOF();
//		BigDecimal valorTarifa = oTitulo.getValorTarifa() == null ? BigDecimal.ZERO : oTitulo.getValorTarifa();
////		BigDecimal valorOutrasDeducoes = oTitulo.getOutrasDeducoes() == null ? BigDecimal.ZERO : oTitulo.getOutrasDeducoes();
//		Transacao transacao = (Transacao)oTitulo.getTransacao();

//		/*
//		* TODO	Precisa colocar a classifica��o cont�bil no cedente.
//		*/
////		IEntity classificacaoContabil = cedente.getProperty(Cedente.CLASSIFICACAO_CONTABIL).getValue().getAsEntity();

//		Cedente cedente = oTitulo.getCedente();
//		/*
//		* TODO Criar um lan�amento com todos os itens de custo.
//		* Est� dependendo do InserirLancamentoService aceitar a lista de itens com valores.
//		*/

//		/* Cria a lista de lancamentoItem */
//		IEntityList lancamentoItemList = this.getProvedorDocumentoCobranca().getServiceManager().getEntityManager().getEntityList(new ArrayList<Object>(1), LancamentoItem.class);
//		LancamentoItem oLancamentoItem = null;

//		/* Se houver Juros, Multa, Descontos indica que o valorCObrado � maior que o valor
//		* do documento, desta forma, o Juros, Multa e etc dever�o ser lan�ados e quitados na movimenta��o. */
//		if(!DecimalUtils.isZero(valorDesconto)){
//		oLancamentoItem = lancamentoItemList.getRunEntity().getObject();
//		oLancamentoItem.setCentroCusto(cedente.getCentroCustoGeral());
//		oLancamentoItem.setItemCusto(cedente.getItemCustoDesconto()); //foi exclu�do ???
////		oLancamentoItem.setClassificacaoContabil(cedente.getClassificacaoContabil());
//		oLancamentoItem.setValor(valorDesconto);
//		lancamentoItemList.runAdd();
//		}


//		if(!DecimalUtils.isZerocom vencimento para(valorAcrescimo)){
//		oLancamentoItem = lancamentoItemList.getRunEntity().getObject();
//		oLancamentoItem.setCentroCusto(cedente.getCentroCustoGeral());
//		oLancamentoItem.setItemCusto(cedente.getItemCustoAcrescimo()); //foi exclu�do ???
////		oLancamentoItem.setClassificacaoContabil(cedente.getClassificacaoContabil());
//		oLancamentoItem.setValor(valorAcrescimo);
//		lancamentoItemList.runAdd();
//		}

//		if(!DecimalUtils.isZero(valorJuros)){
//		oLancamentoItem = lancamentoItemList.getRunEntity().getObject();
//		oLancamentoItem.setCentroCusto(cedente.getCentroCustoGeral());
//		oLancamentoItem.setItemCusto(cedente.getItemCustoJurosMora()); //foi exclu�do ???
////		oLancamentoItem.setClassificacaoContabil(cedente.getClassificacaoContabil());
//		oLancamentoItem.setValor(valorJuros);
//		lancamentoItemList.runAdd();
//		}

//		if(!DecimalUtils.isZero(valorMulta)){
//		oLancamentoItem = lancamentoItemList.getRunEntity().getObject();
//		oLancamentoItem.setCentroCusto(cedente.getCentroCustoGeral());
//		oLancamentoItem.setItemCusto(cedente.getItemCustoMultaAtraso()); //foi exclu�do ???
////		oLancamentoItem.setClassificacaoContabil(cedente.getClassificacaoContabil());
//		oLancamentoItem.setValor(valorMulta);
//		lancamentoItemList.runAdd();
//		}

//		if(!DecimalUtils.isZero(valorIof)){
//		oLancamentoItem = lancamentoItemList.getRunEntity().getObject();
//		oLancamentoItem.setCentroCusto(cedente.getCentroCustoGeral());
//		oLancamentoItem.setItemCusto(cedente.getItemCustoIof());
////		oLancamentoItem.setClassificacaoContabil(cedente.getClassificacaoContabil());
//		oLancamentoItem.setValor(valorIof);
//		lancamentoItemList.runAdd();
//		}

//		if(!DecimalUtils.isZero(valorTarifa)){
//		oLancamentoItem = lancamentoItemList.getRunEntity().getObject();
//		oLancamentoItem.setCentroCusto(cedente.getCentroCustoGeral());
//		oLancamentoItem.setItemCusto(cedente.getItemCustoTarifa());
////		oLancamentoItem.setClassificacaoContabil(cedente.getClassificacaoContabil());
//		oLancamentoItem.setValor(valorTarifa);
//		lancamentoItemList.runAdd();
//		}

//		if(!DecimalUtils.isZero(valorOutrasDeducoes)){
//		oLancamentoItem = lancamentoItemList.getRunEntity().getObject();
//		oLancamentoItem.setCentroCusto(cedente.getCentroCustoGeral());
//		oLancamentoItem.setItemCusto(cedente.getItemCustoOutrasDeducoes()); //foi exclu�do ???
////		oLancamentoItem.setClassificacaoContabil(cedente.getClassificacaoContabil());
//		oLancamentoItem.setValor(valorOutrasDeducoes);
//		lancamentoItemList.runAdd();
//		}

//		/* Verifica se houve algum lan�amento extra para gerar o lan�amento */
//		if(lancamentoItemList.getSize() > 0)
//		super.inserirLancamentoItems(oTitulo, conta, data, transacao, lancamentoItemList.<LancamentoItem>getObjectList(), serviceDataOwner);
//
//		} catch (BusinessException e)
//		{
//		throw new DocumentoCobrancaException(e.getErrorList());
//		}
	}

	public void baixarDocumento(IEntity<? extends DocumentoCobranca> documento, Calendar dataBaixa, ServiceData serviceDataOwner) throws DocumentoCobrancaException {
		try {
			//criando a ocorr�ncia 'baixado'
			documento = atualizarOcorrencia(documento, Ocorrencia.CONTROLE_INTERNO_BAIXA.getCodigo(), serviceDataOwner);

			UtilsCrud.update(this.getProvedorDocumentoCobranca().getServiceManager(), documento, serviceDataOwner);
		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoTitulo.class, "ERRO_BAIXAR", documento.toString(), GERENCIADOR_NOME));
		}
	}

	public void cancelarDocumento(IEntity<? extends DocumentoCobranca> documento, Calendar dataCancelamento, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		log.debug("utlizando cancelarDocumento do GerenciadorDocumentoTitulo");
		try {
			int ultimaOcorrenciaCodigo = OCORRENCIA_VAZIA;
			if(documento.getProperty(DocumentoTitulo.ULTIMA_OCORRENCIA).getValue().getAsEntity() != null)
				ultimaOcorrenciaCodigo = documento.getProperty(DocumentoTitulo.ULTIMA_OCORRENCIA).getValue().getAsEntity().getProperty(Ocorrencia.CODIGO).getValue().getAsInteger(); 

			//se o documento j� foi baixado, n�o pode ser cancelado
			if (ultimaOcorrenciaCodigo == Ocorrencia.CONTROLE_INTERNO_BAIXA.getCodigo()){
				log.debug("Documento n�o cancelado pois j� foi baixado no financeiro");
				throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoTitulo.class, "CANCELAR_DOCUMENTO_ERRO_BAIXADO", documento.toString(), GERENCIADOR_NOME));
			}
			//se o documento j� foi enviado ao banco, deve-se enviar um pedido de cancelamento
			else if (ultimaOcorrenciaCodigo == Ocorrencia.REMESSA_ENVIADA.getCodigo()){
				//TODO no m�todo que gera um arquivo de remessa do gerenciador de bancos, deve ser tratado o tipo de c�digo para o cancelamento, que ser� inclu�do no arquivo de remessa
				//TODO para o sicoob por exemplo, a cobran�a � sem registro, logo n�o existe, nem � necess�rio, um c�digo de cancelamento a ser enviado ao banco

				log.debug("Documento j� enviado ao banco, gerar pedido de cancelamento para o arquivo de remessa");

				documento = atualizarOcorrencia(documento, Ocorrencia.REMESSA_CANCELAR_DOCUMENTO.getCodigo(), serviceDataOwner);

				//o m�todo da classe pai grava a data de cancelamento e atualiza o documento
				super.cancelarDocumento(documento, dataCancelamento, serviceDataOwner); 
			}
			//o documento pode ser cancelado se ainda n�o foi enviado ao banco ou retornou no banco com o c�digo 'documento cancelado' ou ainda se n�o existe uma ocorr�ncia atribu�da ao documento
			else if (ultimaOcorrenciaCodigo == Ocorrencia.REMESSA_REGISTRAR.getCodigo() || ultimaOcorrenciaCodigo == Ocorrencia.RETORNO_DOCUMENTO_CANCELADO.getCodigo() || ultimaOcorrenciaCodigo == OCORRENCIA_VAZIA){
				log.debug("Cancelando o documento no GerenciadorDocumentoTitulo");
				//criando a ocorr�ncia 'cancelado'
				documento = atualizarOcorrencia(documento, Ocorrencia.CONTROLE_INTERNO_CANCELADO.getCodigo(), serviceDataOwner);

				//o m�todo da classe pai grava a data de cancelamento e atualiza o documento
				super.cancelarDocumento(documento, dataCancelamento, serviceDataOwner); 
			}

		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}

	public File gerarRemessa(IEntity<? extends ConvenioCobranca> cedente, Calendar inicioPeriodo, Calendar finalPeriodo, Integer quantidadeDiasProtesto, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		try {

			ServiceData sdGerarRemessa = new ServiceData(GerarBancoRemessaService.SERVICE_NAME, serviceDataOwner);
			sdGerarRemessa.getArgumentList().setProperty(GerarBancoRemessaService.IN_CEDENTE, cedente);
			sdGerarRemessa.getArgumentList().setProperty(GerarBancoRemessaService.IN_INICIO_PERIODO, inicioPeriodo);
			sdGerarRemessa.getArgumentList().setProperty(GerarBancoRemessaService.IN_FINAL_PERIODO, finalPeriodo);
			if (quantidadeDiasProtesto != null) {
				sdGerarRemessa.getArgumentList().setProperty(GerarBancoRemessaService.IN_QUANTIDADE_DIAS_PROTESTO_OPT, quantidadeDiasProtesto);
			}
			this.getProvedorDocumentoCobranca().getServiceManager().execute(sdGerarRemessa);

			/* Pegando a mensagem do servi�o */
			serviceDataOwner.getMessageList().addAll(sdGerarRemessa.getMessageList());

			return (File)sdGerarRemessa.getFirstOutput();

		} catch (ServiceException e) {
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}

	public List<DocumentoRetornoResultado> receberRetorno(IEntity<? extends ConvenioCobranca> cedente, InputStream dados, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		try {
			ServiceData sdReceberRetorno = new ServiceData(ReceberBancoRetornoService.SERVICE_NAME, serviceDataOwner);
			sdReceberRetorno.getArgumentList().setProperty(ReceberBancoRetornoService.IN_CEDENTE, cedente);
			sdReceberRetorno.getArgumentList().setProperty(ReceberBancoRetornoService.IN_INPUT_STREAM, dados);
			this.getProvedorDocumentoCobranca().getServiceManager().execute(sdReceberRetorno);

			/* Pegando a mensagem do servi�o */
			serviceDataOwner.getMessageList().addAll(sdReceberRetorno.getMessageList());

			return sdReceberRetorno.getFirstOutput();

		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}
	
	public void imprimirDocumentos(List<DocumentoCobrancaBean> documentos, OutputStream outputStream, int printerIndex, InputStream inputStreamImage, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		log.debug("::Iniciando o m�todo imprimirDocumentos");
		try {
			if (!documentos.isEmpty()){ 
				log.debug("Preparando documentos para serem impressos, por tipos de layout");

				List<TituloPrintBean> beansLayout2 = new ArrayList<TituloPrintBean>();
				List<TituloPrintBean> beansLayout3 = new ArrayList<TituloPrintBean>();

				for (DocumentoCobrancaBean bean : documentos){
					if(bean.isChecked()){
						IEntity<? extends DocumentoCobranca> documento;
						if(bean.isTemDocumentoOriginal()) {
							documento = bean.getDocumentoOriginal();

							log.debug("Marcando documento como impresso");
							documento.getProperty(DocumentoCobranca.DATA_IMPRESSAO).getValue().setAsCalendar(Calendar.getInstance());
							// Lucio 20100411: O FlushMode.ALWAYS ap�s a conclus�o do servi�o j� executa um update no banco
							// UtilsCrud.update(this.getProvedorDocumentoCobranca().getServiceManager(), documento, serviceDataOwner);
						}
						else{
							documento = UtilsCrud.retrieve(this.getProvedorDocumentoCobranca().getServiceManager(), DocumentoCobranca.class, bean.getId(), serviceDataOwner);
							log.debug("Marcando documento como impresso antes de colocar os valores calculados tempor�rios.");
							documento.getProperty(DocumentoCobranca.DATA_IMPRESSAO).getValue().setAsCalendar(Calendar.getInstance());
							// Lucio 20100411: O FlushMode.ALWAYS ap�s a conclus�o do servi�o j� executa um update no banco
							// UtilsCrud.update(this.getProvedorDocumentoCobranca().getServiceManager(), documento, serviceDataOwner);

							/* Define na entidade que foi recuperada os valores que est�o no Beans que possam ter sido modificados */
							/* Como o FlushMode causar� um UPDATE for�ado destes dados no banco. Logo mais abaixo, 
							 * uma rotina para voltar os valores anteriores*/
							documento.setPropertyValue(DocumentoCobranca.DATA, bean.getDataDocumento());
							documento.setPropertyValue(DocumentoCobranca.DATA_VENCIMENTO, bean.getDataVencimento());
							documento.setPropertyValue(DocumentoCobranca.VALOR, bean.getValorDocumento());	

							/* Lucio 20100411: Coloca uma referencia no bean do documento original para 
							 * posterior recuperacao dos valores definitivos antes dos valores temporarios.
							 * Esta linha tem que ser depois da utiliza��o dos valores temporarios do Bean,
							 * pois ao definir um DocumentoOriginal, o bean passa a fornecer os valores deste
							 * documento e n�o mais os valores pr�prios */
							bean.setDocumentoOriginal(documento);
						}

						String codigoBanco = ((DocumentoTitulo)documento.getObject()).getCedente().getContaBancaria().getBanco().getCodigo();

						//verificando o tipo do layout de impress�o informado na forma de pagamento
						if (documento.getProperty(DocumentoTitulo.LAYOUT_ID).getValue().getAsInteger() == LAYOUT_INT_2) //"Boleto 2 vias em folha A4 (com espa�o superior)"
							beansLayout2.add(new TituloPrintBean(documento, bean.getInstrucoesAdicionais(), this.getProvedorBanco().retrieveGerenciadorBanco(codigoBanco)));
						else if (documento.getProperty(DocumentoTitulo.LAYOUT_ID).getValue().getAsInteger() == LAYOUT_INT_3) //"Boleto 2 vias em folha A5"
							beansLayout3.add(new TituloPrintBean(documento, bean.getInstrucoesAdicionais(), this.getProvedorBanco().retrieveGerenciadorBanco(codigoBanco)));
						else //usa o Layout_2 como padr�o
							beansLayout2.add(new TituloPrintBean(documento, bean.getInstrucoesAdicionais(), this.getProvedorBanco().retrieveGerenciadorBanco(codigoBanco)));
					}
				}
				
				/* Preenche o Relat�rio */
				if (log.isDebugEnabled())
					log.debug("Imprimindo " + documentos.size() + " Documentos");

				/* Repassa o Stream com a imagem extra para publicidade */
				HashMap<String, Object> params = new HashMap<String, Object>();
				if(inputStreamImage != null)
					params.put("INPUT_STREAM_IMAGEM", inputStreamImage);
				
				
				//preparando os t�tulos com LAYOUT_2, se a lista de beansLayout2 n�o for vazia
				if (!beansLayout2.isEmpty()){
					JRDataSource jrdsLayout2 = new JRBeanCollectionDataSource(beansLayout2);

					JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(LAYOUT_FILE_2));
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, jrdsLayout2);
					
					/* Envia para PDF e impress�o oa mesmo tempo. A fun��o detecta qual op��o � v�lida.
					 * � poss�vel imprimir em ambas m�dias */
					PrintUtils.printJasper(jasperPrint, outputStream);
					/* print recebe o arquivo relacionado � impress�o do T�tulo e priterIndex � a impressora selecionada para a impress�o */
					PrintUtils.printJasper(jasperPrint, printerIndex);


					/* apenas visualiza a impress�o em pdf */
					//JRPrinterAWT.printPages(printLayout2, 0, beansLayout2.size()-1, false);
					//JasperViewer.viewReport(printLayout2);
				}

				//preparando os t�tulos com LAYOUT_3, se a lista de beansLayout3 n�o for vazia
				if (!beansLayout3.isEmpty()){
					JRDataSource jrdsLayout3 = new JRBeanCollectionDataSource(beansLayout3);

					JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream(LAYOUT_FILE_3));
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, jrdsLayout3);
					
					/* Envia para PDF e impress�o oa mesmo tempo. A fun��o detecta qual op��o � v�lida.
					 * � poss�vel imprimir em ambas m�dias */
					PrintUtils.printJasper(jasperPrint, outputStream);
					/* print recebe o arquivo relacionado � impress�o do T�tulo e priterIndex � a impressora selecionada para a impress�o */
					PrintUtils.printJasper(jasperPrint, printerIndex);

					/* apenas visualiza a impress�o em pdf*/
					//JRPrinterAWT.printPages(printLayout3, 0, beansLayout3.size()-1, false);
					//JasperViewer.viewReport(printLayout3);
				}

				/*
				 * Exemplo tirado do RelatorioCobrancaService
				 */
//				log.debug("Compilando o relat�rio.");
//				String nomeArquivoFonte = "RelatorioCobranca.jrxml";
//				Map<String, String> parametros = new HashMap<String, String>();
//				JasperReport relatorio = JasperCompileManager.compileReport(getClass().getResourceAsStream(nomeArquivoFonte));

//				log.debug("Imprimindo o relat�rio.");
//				JasperPrint print = JasperFillManager.fillReport(relatorio, parametros, new JRBeanCollectionDataSource(list));

//				JasperExportManager.exportReportToPdfStream(print, inOutputStream);
				
				/* Lucio 20100411: Retorna os valores temporarios para o objeto, pois o mesmo sera persistido incondicionalmente pela 
				 * operacao de FlushMode.ALWAYS antes da sess�o do hibernate ser fechada */
				for (DocumentoCobrancaBean bean : documentos){
					if(bean.isChecked()){
						if(bean.isTemDocumentoOriginal()){
							IEntity<? extends DocumentoCobranca> documento = bean.getDocumentoOriginal();

							documento.getProperty(DocumentoCobranca.DATA).getValue().restoreOldValue();
							documento.getProperty(DocumentoCobranca.DATA_VENCIMENTO).getValue().restoreOldValue();
							documento.getProperty(DocumentoCobranca.VALOR).getValue().restoreOldValue();
						}
					}
				}
			}else{
				log.debug("Lista de Titulos Vazia. Nenhum DocumentoTitulo foi impresso.");
				throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoTitulo.class, "ERRO_LISTA_IMPRESSAO_VAZIA"));
			}
			log.debug("::Fim da execu��o do m�todo imprimirDocumentos");

		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/*
			 * O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
			 */
			throw new DocumentoCobrancaException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			/* Indica que o servi�o falhou por causa de uma exce��o do Jasper. */
			DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.createSingleInternalError(e));
			/* Adiciona a mensagem local */
			de.getErrorList().add(new BusinessMessage(GerenciadorDocumentoTitulo.class, "ERRO_IMPRIMINDO"));
			throw de;
		}	
	}

	/**
	 * Atualiza a �ltima ocorr�ncia de um t�tulo banc�rio
	 *  
	 * @param documento - t�tulo que a ocorr�ncia ser� adicionada
	 * @param codigoOcorrencia - c�digo (int) da ocorr�ncia
	 * @param serviceDataOwner 
	 * @return - documento atualizado com a nova ocorr�ncia
	 * @throws DocumentoCobrancaException
	 */
	private IEntity<? extends DocumentoCobranca> atualizarOcorrencia(IEntity<? extends DocumentoCobranca> documento, int codigoOcorrencia, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		try {
			IEntity<Ocorrencia> ultimaOcorrencia = UtilsOcorrencia.obterOcorrencia(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), codigoOcorrencia, serviceDataOwner);

			/* Adicionando a �ltima ocorr�ncia ao hist�rico de ocorr�ncias deste titulo */
			IEntity<OcorrenciaControle> ocorrencia = UtilsCrud.create(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), OcorrenciaControle.class, serviceDataOwner);

			/* Lucio 10/10/2008: Foi removida a CONSTRAINT de OcorrenciaControle.titulo->DocumentoTitulo.id, pois
			 * como ao lan�ar o titulo ainda n�o est� persistido, dava um erro. Penso que o hibernate deve
			 * efetuar a persistencia destes objetos em mais de um passo, gerando o ID e efetuando UPDATES posteriores
			 * de acordo com o relacionamento */
			ocorrencia.setPropertyValue(OcorrenciaControle.TITULO, documento);
			ocorrencia.setPropertyValue(OcorrenciaControle.OCORRENCIA, ultimaOcorrencia);
			ocorrencia.setPropertyValue(OcorrenciaControle.DATA_OCORRENCIA, CalendarUtils.getCalendar());

			UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), ocorrencia, serviceDataOwner);

			documento.setPropertyValue(DocumentoTitulo.ULTIMA_OCORRENCIA, ultimaOcorrencia);
			documento.setPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA, CalendarUtils.getCalendar());
			documento.getProperty(DocumentoTitulo.OCORRENCIAS).getValue().<OcorrenciaControle>getAsEntityCollection().add(ocorrencia);
			
			UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), documento, serviceDataOwner);
			
			return documento;
		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}

	public List<SelectItem> getLayouts(){
		List<SelectItem> result = new ArrayList<SelectItem>(1);
		result.add(new SelectItem(0, LAYOUT_0));
		result.add(new SelectItem(1, LAYOUT_1));
		result.add(new SelectItem(2, LAYOUT_2));
		result.add(new SelectItem(3, LAYOUT_3));
		return result;
	}

	public void alterarDataVencimento(IEntity<? extends DocumentoCobranca> documento, Calendar dataVencimento, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		log.debug("Utilizando alterarVencimento do GerenciadorDocumentoTitulo");
		try {
			int ultimaOcorrenciaCodigo = OCORRENCIA_VAZIA;
			if(documento.getProperty(DocumentoTitulo.ULTIMA_OCORRENCIA).getValue().getAsEntity() != null)
				ultimaOcorrenciaCodigo = documento.getProperty(DocumentoTitulo.ULTIMA_OCORRENCIA).getValue().getAsEntity().getProperty(Ocorrencia.CODIGO).getValue().getAsInteger(); 
	
			//se o documento j� foi baixado, n�o pode ter o vencimento alterado
			if (ultimaOcorrenciaCodigo == Ocorrencia.CONTROLE_INTERNO_BAIXA.getCodigo()){
				log.debug("Vencimento n�o alterado, pois o documento j� foi baixado no financeiro");
				throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoTitulo.class, "CANCELAR_DOCUMENTO_ERRO_BAIXADO", documento.toString(), GERENCIADOR_NOME));
			}
			//o documento pode ser alterado se ainda n�o foi enviado ao banco ou retornou no banco com o c�digo 'documento cancelado' ou ainda se n�o existe uma ocorr�ncia atribu�da ao documento
			else if (ultimaOcorrenciaCodigo == Ocorrencia.REMESSA_REGISTRAR.getCodigo() || ultimaOcorrenciaCodigo == Ocorrencia.RETORNO_DOCUMENTO_CANCELADO.getCodigo() || ultimaOcorrenciaCodigo == OCORRENCIA_VAZIA){
				log.debug("Alterando o vencimento no GerenciadorDocumentoTitulo");
				
				//o m�todo da classe pai grava a data de cancelamento e atualiza o documento
				super.alterarDataVencimento(documento, dataVencimento, serviceDataOwner); 
			}
			//se o documento j� foi enviado ao banco, deve-se enviar um pedido de altera��o
			else if (ultimaOcorrenciaCodigo > Ocorrencia.REMESSA_ENVIADA.getCodigo()){
				log.debug("Documento j� enviado ao banco, gerar pedido de altera��o para o arquivo de remessa");
	
				documento = atualizarOcorrencia(documento, Ocorrencia.REMESSA_ALTERAR_VENCIMENTO.getCodigo(), serviceDataOwner);
	
				//o m�todo da classe pai grava a data de vencimento e atualiza o documento
				super.alterarDataVencimento(documento, dataVencimento, serviceDataOwner); 
			}
	
		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}

}