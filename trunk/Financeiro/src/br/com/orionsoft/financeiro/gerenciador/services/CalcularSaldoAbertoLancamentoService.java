package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;

/**
 * Este servi�o calcula o saldo em aberto de um Lancamento.
 * <p>
 * <b>Par�metros:</b><br>
 * IN_LANCAMENTO (IEntity) o lan�amento que ser� calculado o saldo.<br>
 * <p>
 * <b>Retorno:</b><br>
 * IEntity saldoAberto
 * 
 * @author Antonio Alves
 * @version 20070712
 * 
 * @spring.bean id="CalcularSaldoAbertoLancamentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class CalcularSaldoAbertoLancamentoService extends ServiceBasic {
    public static final String SERVICE_NAME = "CalcularSaldoAbertoLancamentoService";
    
    public static final String IN_LANCAMENTO = "lancamento";

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
    	log.debug("::Iniciando a execu��o do servi�o CalcularSaldoAbertoLancamentoService");

        try {
            /*
             * L� os par�metros obrigat�rios. 
             */
            Lancamento inLancamento = (Lancamento) serviceData.getArgumentList().getProperty(IN_LANCAMENTO);
            
            BigDecimal saldo = inLancamento.getValor();
            for (LancamentoMovimento movimento : inLancamento.getLancamentoMovimentos()) {
            	if (movimento.getLancamentoMovimentoCategoria() != LancamentoMovimentoCategoria.TRANSFERIDO)
                	saldo = saldo.subtract(movimento.getValor());
            }
            
            /*
             * Retorno do servi�o: saldo
             */
            serviceData.getOutputData().clear();
            serviceData.getOutputData().add(saldo);
            
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