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
 * Este servi�o cancela um Lancamento inserindo um LancamentoMovimento.  
 * <p>
 * <b>Par�metros:</b><br>
 * IN_DATA (Calendar) data da quita��o do lan�amento.<br>
 * IN_LANCAMENTO (IEntity) qual lan�amento dever� ser quitado.<br>
 * IN_DESCRICAO (String) descri��o do motivo do cancelamento.<br>
 * <p>
 * <b>Retorno:</b><br>
 * LancamentoMovimento lancamentoMovimento
 * 
 * @author Antonio Alves
 * @version 20070712
 * 
 * @spring.bean id="CancelarLancamentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class CancelarLancamentoService extends ServiceBasic {
    public static final String SERVICE_NAME = "CancelarLancamentoService";
    
    public static final String IN_DATA = "data";
    public static final String IN_LANCAMENTO = "lancamento";
    public static final String IN_DESCRICAO = "descricao";

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
    	log.debug("::Iniciando a execu��o do servi�o CancelarLancamentoService");

        try {
            log.debug("Preparando os argumentos");
            
            /* 
             * L� os par�metros obrigat�rios. 
             */
            Calendar inData = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA);
            Lancamento inLancamento = (Lancamento) serviceData.getArgumentList().getProperty(IN_LANCAMENTO);
            String inDescricao = (String) serviceData.getArgumentList().getProperty(IN_DESCRICAO);

            /*
             * Falha se tiver saldo em aberto n�o maior que zero.
             */
            ServiceData sdSaldo = new ServiceData(CalcularSaldoAbertoLancamentoService.SERVICE_NAME, serviceData);
            sdSaldo.getArgumentList().setProperty(CalcularSaldoAbertoLancamentoService.IN_LANCAMENTO, inLancamento);
            this.getServiceManager().execute(sdSaldo);
            BigDecimal saldo = (BigDecimal)sdSaldo.getFirstOutput();
        	if (saldo.signum() == 0)
                throw new ServiceException(MessageList.create(CancelarLancamentoService.class, "FALHA_VALOR_ZERO"));

            /*
             * Insere um LancamentoMovimento para este Lancamento.
             */
            ServiceData sd = new ServiceData(InserirLancamentoMovimentoService.SERVICE_NAME, serviceData);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO, inLancamento);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA, inData);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA_COMPENSACAO_OPT, inData);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DESCRICAO, inDescricao);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, saldo);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, null);
            sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO_MOVIMENTO_CATEGORIA, LancamentoMovimentoCategoria.CANCELADO);
            this.getServiceManager().execute(sd);
            LancamentoMovimento lancamentoMovimento = sd.getFirstOutput();
            
            /*
             * Recarrega lan�amento para atualiza��o.
             */
            inLancamento = UtilsCrud.objectRetrieve(this.getServiceManager(), Lancamento.class, inLancamento.getId(), serviceData);
            
            /*
             * Recalcula o saldo do lan�amento.
             */
            sd = new ServiceData(CalcularSaldoAbertoLancamentoService.SERVICE_NAME, serviceData);
            sd.getArgumentList().setProperty(CalcularSaldoAbertoLancamentoService.IN_LANCAMENTO, inLancamento);
            this.getServiceManager().execute(sd);
            saldo = sd.getFirstOutput();
            
            /*
             * Atualiza o lan�amento com o novo saldo.
             */
            inLancamento.setSaldo(saldo);
            inLancamento.setLancamentoSituacao(LancamentoSituacao.CANCELADO);
            UtilsCrud.objectUpdate(this.getServiceManager(), inLancamento, serviceData);
            
            /*
             * Adiciona o LancamentoMovimento ao retorno do sevi�o.
             */
            lancamentoMovimento = UtilsCrud.objectRetrieve(this.getServiceManager(), LancamentoMovimento.class, lancamentoMovimento.getId(), serviceData);
            serviceData.getOutputData().add(lancamentoMovimento);

            /* 
             * Adiconando a mensagem de sucesso 
             */
            this.addInfoMessage(serviceData, "SUCESSO");
		
            log.debug("::Fim da execu��o do servi�o");
        } catch (BusinessException e) {
            log.fatal(e.getErrorList());
            throw new ServiceException(e.getErrorList());
        } catch (Exception e) {
            log.fatal(e.getMessage());
            throw new ServiceException(MessageList.createSingleInternalError(e));
        }
    }

}
