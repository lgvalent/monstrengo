package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.util.List;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;

/**
 * Este servi�o verifica se um lan�amento est� quitado.  
 * <p>
 * <b>Par�metros:</b><br>
 * IN_LANCAMENTO (Lancamento) o lan�amento que ser� verificado. 
 * <p>
 * <b>Retorno:</b><br>
 * IEntity lancamentoMovimento
 * 
 * @author Antonio Alves
 * @version 20070817
 * 
 * @spring.bean id="VerificarLancamentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="transactional" value="true"
 */
public class VerificarLancamentoQuitadoService extends ServiceBasic {
    public static final String SERVICE_NAME = "VerificarLancamentoQuitadoService";

    public static final String IN_LANCAMENTO = "lancamento";

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
    	log.debug("::Iniciando a execu��o do servi�o VerificarLancamentoQuitadoService");
    	/*
    	 * Par�metros
    	 */
    	Lancamento lancamento = (Lancamento) serviceData.getArgumentList().getProperty(IN_LANCAMENTO);
    	
    	/*
    	 * Se o saldo do lan�amento n�o for zero j� considera como n�o quitado.
    	 */
    	boolean quitado = false;
    	if (lancamento.getSaldo().signum() == 0) {
    		List<LancamentoMovimento> movimentoList = lancamento.getLancamentoMovimentos();
    		BigDecimal soma = DecimalUtils.ZERO;
    		for (LancamentoMovimento movimento : movimentoList) {
    			if (movimento.getLancamentoMovimentoCategoria() == LancamentoMovimentoCategoria.QUITADO &&
    					movimento.getEstornado() == false) {
    				soma = soma.add(movimento.getValor());
    			}
    		}
    		if (soma.equals(lancamento.getValor()))
    			quitado = true;
    	}
    	
    	/*
    	 * Retorna o resultado do servi�o.
    	 */
    	serviceData.getOutputData().add(quitado);
    }
}
