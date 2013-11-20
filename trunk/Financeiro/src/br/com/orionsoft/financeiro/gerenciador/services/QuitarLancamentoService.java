package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoSituacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este serviço quita um Lancamento inserindo um LancamentoMovimento.  
 * <p>
 * <b>Parâmetros:</b><br>
 * IN_DATA (Calendar) data da quitação do lançamento.<br>
 * IN_CONTA (IEntity) conta em que o lançamento será quitado.<br>
 * IN_LANCAMENTO (IEntity) qual lançamento deverá ser quitado.<br>
 * IN_DOCUMENTO_PAGAMENTO (IEntity) documento que será usado na quitação.<br>
 * IN_VALOR (BigDecimal) valor da quitação.<br>
 * <p>
 * <b>Retorno:</b><br>
 * IEntity lancamentoMovimento
 * 
 * @author Antonio Alves
 * @version 20070712
 * 
 * @spring.bean id="QuitarLancamentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="transactional" value="true"
 */
public class QuitarLancamentoService extends ServiceBasic {
    public static final String SERVICE_NAME = "QuitarLancamentoService";
    
    public static final String IN_DATA = "data";
    public static final String IN_DATA_COMPENSACAO_OPT = "dataCompensacao";
    public static final String IN_CONTA = "conta";
    public static final String IN_LANCAMENTO = "lancamento";
    public static final String IN_DOCUMENTO_PAGAMENTO_OPT = "documentoPagamento";
    public static final String IN_VALOR = "valor";
	public static final String IN_JUROS_OPT = "juros"; //Andre, 19/06/2008: valores de juros e multa devem ser informados em Reais (R$)
	public static final String IN_MULTA_OPT = "multa";
	public static final String IN_ACRESCIMO_OPT = "acrescimo"; //Andre, 12/07/2008: valores de desconto e acrescimo devem ser informados em Reais (R$)
	public static final String IN_DESCONTO_OPT = "desconto";
    public static final String IN_SUBSTITUIR_VALOR_OPT = "substituirValor";
    
    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
    	log.debug("::Iniciando a execução do serviço QuitarLancamentoService");

        try {
            /* 
             * Lê os parâmetros obrigatórios. 
             */
            Calendar inData = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA);
            Conta inConta = (Conta) serviceData.getArgumentList().getProperty(IN_CONTA);
            Lancamento inLancamento = (Lancamento) serviceData.getArgumentList().getProperty(IN_LANCAMENTO);
            BigDecimal inValor = (BigDecimal) serviceData.getArgumentList().getProperty(IN_VALOR);

            /*
             * Lé os parâmetros opcionais.
             */
            Calendar inDataCompensacao = serviceData.getArgumentList().containsProperty(IN_DATA_COMPENSACAO_OPT)?(Calendar) serviceData.getArgumentList().getProperty(IN_DATA_COMPENSACAO_OPT):null;
            DocumentoPagamento inDocumentoPagamento = (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_PAGAMENTO_OPT) ? 
            		(DocumentoPagamento) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_PAGAMENTO_OPT) : null);
            BigDecimal inJuros = serviceData.getArgumentList().containsProperty(IN_JUROS_OPT) ? (BigDecimal) serviceData.getArgumentList().getProperty(IN_JUROS_OPT) : null;
            BigDecimal inMulta = serviceData.getArgumentList().containsProperty(IN_MULTA_OPT) ? (BigDecimal) serviceData.getArgumentList().getProperty(IN_MULTA_OPT) : null;
            BigDecimal inAcrescimo = serviceData.getArgumentList().containsProperty(IN_ACRESCIMO_OPT) ? (BigDecimal) serviceData.getArgumentList().getProperty(IN_ACRESCIMO_OPT) : null;
            BigDecimal inDesconto = serviceData.getArgumentList().containsProperty(IN_DESCONTO_OPT) ? (BigDecimal) serviceData.getArgumentList().getProperty(IN_DESCONTO_OPT) : null;

            boolean inSubstituirValor = serviceData.getArgumentList().containsProperty(IN_SUBSTITUIR_VALOR_OPT)?(Boolean) serviceData.getArgumentList().getProperty(IN_SUBSTITUIR_VALOR_OPT):false;
            
            /* Verifica COMPENSAÇÃO AUTOMÁTICA na conta */
            if(inConta.isCompensacaoAutomatica() && inDataCompensacao == null){
            	inDataCompensacao = inData;
            }

            /*
             * Falha se o valor não for maior que zero.
             */
            if (inValor.signum() != 1) {
                throw new ServiceException(MessageList.create(QuitarLancamentoService.class, "FALHA_VALOR_ZERO"));
            }

            /*
             * Falha se tiver saldo em aberto menor que o valor.
             */
            ServiceData sdSaldo = new ServiceData(CalcularSaldoAbertoLancamentoService.SERVICE_NAME, serviceData);
            sdSaldo.getArgumentList().setProperty(CalcularSaldoAbertoLancamentoService.IN_LANCAMENTO, inLancamento);
            this.getServiceManager().execute(sdSaldo);
            BigDecimal saldo = (BigDecimal)sdSaldo.getFirstOutput();

            log.debug("Verificando a opção de substituição de valor");
            if(inSubstituirValor){
                /* GAMBI! Lucio - 20/05/07. Ao retornar do banco um titulo jah quitado em carteira o sistema não alertava.
                 * Quando for implementada uma tela para recebimento de títulos em atraso oe em carteira que marque o título
                 * com uma ocorrência Quitado em Carteira, então esta verificação de size%2 != 0 pode ser removida. */
            	if((inLancamento.getValor().compareTo(inLancamento.getSaldo()) != 0) || (inLancamento.getLancamentoMovimentos().size()%2 != 0))
            		throw new ServiceException(MessageList.create(QuitarLancamentoService.class, "FALHA_SUBSTITUIR_VALOR", inLancamento.toString(), DecimalUtils.formatBigDecimal(inValor.abs())));
            	
            	log.debug("Verificando se o valor da quitação é diferente e será necessária a substituição");
            	if(inLancamento.getValor().compareTo(inValor) != 0){
            		log.debug("Substituindo valor");
            		inLancamento.setValor(inValor);
            		inLancamento.setSaldo(inValor);
            		/* Substitui o saldo atual, pois sera utilizado nas linhas abaixo*/
            		saldo = inValor;
            		
            		log.debug("Substituindo valor nos lançamentos");
            		/* Perccore a lista de itens atualizando os seus valores de acordo com seu peso 
            		 * TODO Isto pode gerar centavos a amis ou a menos!!!!!! 
            		 * Fazer rotina de arredondamento*/
            		for(LancamentoItem item: inLancamento.getLancamentoItens())
            			item.setValor(inValor.multiply(item.getPeso()));
            		UtilsCrud.objectUpdate(this.getServiceManager(), inLancamento, serviceData);
            	}
            	
            }
            else{	
            	log.debug("Verificando se tem saldo em aberto para executar a ação");
            	if (inValor.abs().compareTo(saldo.abs()) > 0) {
            		throw new ServiceException(MessageList.create(QuitarLancamentoService.class, "FALHA_SALDO_MENOR", DecimalUtils.formatBigDecimal(inValor.abs()), DecimalUtils.formatBigDecimal(saldo.abs())));
            	}
            }

        	/*
        	 * Lucio 29/01/2008: Define a transação pelo sinal do valor do saldo em aberto.
        	 * Se for 1 ou 0 é um saldo de valor positivo => Trata-se de um lançamento de CRÉDITO;
        	 * Se for -1 => Trata-se de um de DÉBITO
        	 */
        	if(saldo.signum() == -1){
        		inValor = inValor.negate();
        		inJuros = inJuros == null?null:inJuros.negate();
        		inMulta =  inMulta== null?null:inMulta.negate();
        		inDesconto =  inDesconto== null?null:inDesconto.negate();
        		inAcrescimo =  inAcrescimo== null?null:inAcrescimo.negate();
        	}
        	
            /*
             * Insere um LancamentoMovimento para este Lancamento.
             */
            ServiceData sd = new ServiceData(InserirLancamentoMovimentoService.SERVICE_NAME, serviceData);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO, inLancamento);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA, inData);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA_COMPENSACAO_OPT, inDataCompensacao);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, inConta);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, inValor);
            if (inJuros != null)
            	sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_JUROS_OPT, inJuros);
            if (inMulta != null)
            	sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_MULTA_OPT, inMulta);
            if (inAcrescimo != null)
            	sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_ACRESCIMO_OPT, inAcrescimo);
            if (inDesconto != null)
            	sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DESCONTO_OPT, inDesconto);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DESCRICAO, "");
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DOCUMENTO_PAGAMENTO_OPT, inDocumentoPagamento);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO_MOVIMENTO_CATEGORIA, LancamentoMovimentoCategoria.QUITADO);
            this.getServiceManager().execute(sd);
            LancamentoMovimento lancamentoMovimento = (LancamentoMovimento)sd.getFirstOutput();
            
            /*
             * Atualiza o saldo do lançamento.
             */
            saldo = saldo.subtract(inValor);
            inLancamento.setSaldo(saldo);
            if (saldo.signum() == 0)
            	inLancamento.setLancamentoSituacao(LancamentoSituacao.QUITADO);
            UtilsCrud.objectUpdate(this.getServiceManager(), inLancamento, serviceData);

            /*
             * Lucio 20090424:: Agora, sempre o QuitarDocumentoService é quem quitará os seus lançamentos.
             * TODO No entanto, precisa ver se ao quitar um lançamento pela tela, o mesmo não marcará o documento de cobrança
             * como quitado, e irá quitar somente o lançamento. Isto pode gerar inconsistências. Ler os comentários abaixo. 
             * 
             * Andre, 01/07/2008: Ocorria "dead lock" ao tentar quitar um documento diretamente
             * pela tela de quitação: o serviço de quitar lancamento chamava o serviço de
             * quitar documento que chamava novamente o serviço de quitar lançamento e 
             * ocorria erro de valor maior que o saldo de 0.
             * 
             * Segundo o Lúcio:
             * "Talvez tenhamos que criar mais um método na interface do IGerenciadorDocumento para possibilitar não simplesmente 
             * a quitação deste, mas sim a marcação dele como quitado em Carteira. Pois se invocarmos o quitarDocumento, 
             * este vai ver seus juros e quitar o lancamento, e ocorre o dead lock.
             * A princípio, sugiro que o QuitarLancamento service não invoque o quitarDocumento do gerenciador. 
             * Isto, somente para resolver o problema, mas precisaremos discutir uma solução."
             * 
             * Então é isso, o bloco a seguir está sendo comentado a princípio, mas deve-se pensar numa solução melhor.
             * TODO - pensar numa solução para o problema de dead lock na quitação de lançamento e documento  
             */
//            /*
//             * Quita o documento de cobrança caso exista.
//             */
//            if (inLancamento.getDocumentoCobranca() != null) {
//            	DocumentoCobranca documentoCobranca = inLancamento.getDocumentoCobranca();
//            	ServiceData sdQuitarDocumento = new ServiceData(QuitarDocumentoCobrancaService.SERVICE_NAME, serviceData);
//            	sdQuitarDocumento.getArgumentList().setProperty(QuitarDocumentoCobrancaService.IN_CONTA, inConta);
//            	sdQuitarDocumento.getArgumentList().setProperty(QuitarDocumentoCobrancaService.IN_DATA_MOVIMENTO, inData);
//            	sdQuitarDocumento.getArgumentList().setProperty(QuitarDocumentoCobrancaService.IN_DOCUMENTO, documentoCobranca);
//            	this.getServiceManager().execute(sdQuitarDocumento);
//            }
            
            /*
             * Adiciona o LancamentoMovimento ao retorno do seviço.
             */
            serviceData.getOutputData().add(lancamentoMovimento);

            /*
             * Adiciona a mensagem de sucesso.
             */
            this.addInfoMessage(serviceData, "SUCESSO", "");
            
            log.debug("::Fim da execução do serviço");
        } catch (BusinessException e) {
            log.fatal(e.getErrorList());
            throw new ServiceException(e.getErrorList());
        } catch (Exception e) {
            log.fatal(e.getMessage());
            throw new ServiceException(MessageList.createSingleInternalError(e));
        }
    }

}