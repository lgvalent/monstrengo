package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoSituacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Serviço que estorna um lancamentoMovimentoservice
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b>
 * 
 * @author Juan Garay III
 * @version 20070716
 * 
 * @spring.bean id="EstornarLancamentoMovimentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class EstornarLancamentoMovimentoService extends ServiceBasic {
	public static final String SERVICE_NAME = "EstornarLancamentoService";

	public static final String IN_LANCAMENTO_MOVIMENTO = "lancamentoMovimento";
	public static final String IN_DATA = "data";
	public static final String IN_DESCRICAO = "descricao";

	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando a execução do serviço EstornarLancamentoMovimentoService");
		try {
			LancamentoMovimento inLancamentoMovimento = (LancamentoMovimento) serviceData.getArgumentList().getProperty(IN_LANCAMENTO_MOVIMENTO);
			Calendar inData = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA);
			String inDescricao = (String) serviceData.getArgumentList().getProperty(IN_DESCRICAO);
			
//			BigDecimal valor = (BigDecimal) inLancamentoMovimento.getValor(); 
//			Calendar dataCompensacao =  (Calendar) inLancamentoMovimento.getDataCompensacao();
//			Conta conta = (Conta) inLancamentoMovimento.getConta();
//			DocumentoPagamento documentoPagamento = (DocumentoPagamento) inLancamentoMovimento.getDocumentoPagamento();
			
			LancamentoMovimentoCategoria lancamentoMovimentoCategoria = null;
			if (inLancamentoMovimento.getLancamentoMovimentoCategoria() == LancamentoMovimentoCategoria.QUITADO)
				lancamentoMovimentoCategoria = LancamentoMovimentoCategoria.QUITADO_ESTORNADO;
			else
			if (inLancamentoMovimento.getLancamentoMovimentoCategoria() == LancamentoMovimentoCategoria.CANCELADO)
				lancamentoMovimentoCategoria = LancamentoMovimentoCategoria.CANCELADO_ESTORNADO;
			else
				if (inLancamentoMovimento.getLancamentoMovimentoCategoria() == LancamentoMovimentoCategoria.TRANSFERIDO)
					throw new ServiceException(MessageList.create(EstornarLancamentoMovimentoService.class, "ERRO_ESTORNO_MOVIMENTO_TANSFERIDO"));
				else
					throw new ServiceException(MessageList.create(EstornarLancamentoMovimentoService.class, "ERRO_ESTORNO_MOVIMENTO_ESTORNADO"));
			
			/* Define a data de compensação do movimento estornado, caso ela esteja nulo */
			if(inLancamentoMovimento.getDataCompensacao() == null)
				inLancamentoMovimento.setDataCompensacao(inData);
			
			ServiceData sd = new ServiceData(InserirLancamentoMovimentoService.SERVICE_NAME, serviceData);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO, inLancamentoMovimento.getLancamento());
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, inLancamentoMovimento.getValor().negate());
			if (inLancamentoMovimento.getMulta() != null)
				sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_MULTA_OPT, inLancamentoMovimento.getMulta().negate());
			if (inLancamentoMovimento.getJuros() != null)
				sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_JUROS_OPT, inLancamentoMovimento.getJuros().negate());
			if (inLancamentoMovimento.getAcrescimo() != null)
				sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_ACRESCIMO_OPT, inLancamentoMovimento.getAcrescimo().negate());
			if (inLancamentoMovimento.getDesconto() != null)
				sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DESCONTO_OPT, inLancamentoMovimento.getDesconto().negate());
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA, inData);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA_COMPENSACAO_OPT, inLancamentoMovimento.getDataCompensacao());
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DESCRICAO, inDescricao);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO_MOVIMENTO_CATEGORIA, lancamentoMovimentoCategoria);			
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, inLancamentoMovimento.getConta());
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DOCUMENTO_PAGAMENTO_OPT, inLancamentoMovimento.getDocumentoPagamento());
			this.getServiceManager().execute(sd);
			LancamentoMovimento lancamentoMovimento = sd.getFirstOutput();
			
			/*
			 * Marca o LancamentoMovimento como estornado.
			 */
			inLancamentoMovimento.setEstornado(true);
			UtilsCrud.objectUpdate(this.getServiceManager(), inLancamentoMovimento, serviceData);
			
            /*
             * Recarrega lançamento para atualização.
             */
            Lancamento lancamento = UtilsCrud.objectRetrieve(this.getServiceManager(), Lancamento.class, inLancamentoMovimento.getLancamento().getId(), serviceData);
            
            /*
             * Atualiza o saldo do lançamento.
             */
            ServiceData sdSaldo = new ServiceData(CalcularSaldoAbertoLancamentoService.SERVICE_NAME, serviceData);
            sdSaldo.getArgumentList().setProperty(CalcularSaldoAbertoLancamentoService.IN_LANCAMENTO, lancamento);
            this.getServiceManager().execute(sdSaldo);
            BigDecimal saldo = (BigDecimal)sdSaldo.getFirstOutput();
            lancamento.setSaldo(saldo);
            if (saldo.signum() != 0)
            	lancamento.setLancamentoSituacao(LancamentoSituacao.PENDENTE);
            UtilsCrud.objectUpdate(this.getServiceManager(), lancamento, serviceData);
			
			/*
			 * Retorna o lancamento movimento criado e marca ele como estornado para que não possa ser 
			 * estornado tambem.
			 */
            lancamentoMovimento = UtilsCrud.objectRetrieve(this.getServiceManager(), LancamentoMovimento.class, lancamentoMovimento.getId(), serviceData);
            lancamentoMovimento.setEstornado(true);
            UtilsCrud.objectUpdate(this.getServiceManager(), lancamentoMovimento, serviceData);
            serviceData.getOutputData().add(lancamentoMovimento);
		
            /* 
             * Adiconando a mensagem de sucesso 
             */
            this.addInfoMessage(serviceData, "SUCESSO");
		
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/*
			 * O Serviço não precisa adicionar mensagem local. O Manager já
			 * indica qual srv falhou e os parâmetros.
			 */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			/*
			 * Indica que o serviço falhou por causa de uma exceção do
			 * hibernate.
			 */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

}
