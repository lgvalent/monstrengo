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
 * Este serviço verifica se um lançamento está quitado.  
 * <p>
 * <b>Parâmetros:</b><br>
 * IN_LANCAMENTO (Lancamento) o lançamento que será verificado. 
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
    	log.debug("::Iniciando a execução do serviço VerificarLancamentoQuitadoService");
    	/*
    	 * Parâmetros
    	 */
    	Lancamento lancamento = (Lancamento) serviceData.getArgumentList().getProperty(IN_LANCAMENTO);
    	
    	/*
    	 * Se o saldo do lançamento não for zero já considera como não quitado.
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
    	 * Retorna o resultado do serviço.
    	 */
    	serviceData.getOutputData().add(quitado);
    }
}
