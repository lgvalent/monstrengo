package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.basic.entities.commons.Frequencia;
import br.com.orionsoft.financeiro.utils.UtilsJuros;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;

/**
 * 
 * @author Antonio Alves
 * @since 05/12/2007
 * @version 18/06/2008
 *
 * @spring.bean id="CalcularMultaJurosService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="transactional" value="true"
 */
public class CalcularMultaJurosService extends ServiceBasic {
	public static final String SERVICE_NAME = "CalcularMultaJurosService";
	
	public static final String IN_VALOR = "valor";
	public static final String IN_DATA = "data";
	public static final String IN_DATA_VENCIMENTO = "dataVencimento";
	
	/*
	 * Andre, 14/06/2008: Campos juros, multa e dias de tolerância passados por parâmetro.
	 * É útil caso seja necessário calcular juros e multas através das informações de
	 * uma categoria de documento de cobrança, por exemplo.
	 */
	public static final String IN_JUROS_MORA = "jurosMora";
	public static final String IN_MULTA_INICIAL = "multaInicial";
	public static final String IN_MULTA_ADICIONAL = "multaAdicional";
	public static final String IN_DIAS_TOLERANCIA = "diasTolerancia";
	
	public static final int OUT_MULTA = 0;
	public static final int OUT_JUROS = 1;

	public String getServiceName() {
		return SERVICE_NAME;
	}

	public void execute(ServiceData sd) throws ServiceException {
		/*
		 * Parâmetros obrigatórios
		 */
		BigDecimal inValor = (BigDecimal) sd.getArgumentList().getProperty(IN_VALOR);
		Calendar inData = (Calendar) sd.getArgumentList().getProperty(IN_DATA);
		Calendar inDataVencimento = (Calendar) sd.getArgumentList().getProperty(IN_DATA_VENCIMENTO);
		BigDecimal inJurosMora = (BigDecimal) sd.getArgumentList().getProperty(IN_JUROS_MORA);
		BigDecimal inMultaInicial = (BigDecimal) sd.getArgumentList().getProperty(IN_MULTA_INICIAL);
		BigDecimal inMultaAdicional = (sd.getArgumentList().containsProperty(IN_MULTA_ADICIONAL) ? 
			(BigDecimal) sd.getArgumentList().getProperty(IN_MULTA_ADICIONAL) : DecimalUtils.ZERO);
		Integer inDiasTolerancia = (sd.getArgumentList().containsProperty(IN_DIAS_TOLERANCIA) ? 
			(Integer) sd.getArgumentList().getProperty(IN_DIAS_TOLERANCIA) : 0);
		
//		long dias = CalendarUtils.diffDay(inData, inDataVencimento);

		/*
		 * Multa
		 */
		BigDecimal valorMulta = DecimalUtils.getBigDecimal(0);
		try {
			valorMulta = UtilsJuros.calcularMulta(inValor, inMultaInicial, inMultaAdicional, Frequencia.MENSAL, inDataVencimento, inData, inDiasTolerancia);
		} catch (NullPointerException e) {
            /* Indica que o serviço falhou por causa de uma exceção de NullPointer. */
            throw new ServiceException(MessageList.createSingleInternalError(e));
		} catch (ArithmeticException e) {
            /* Indica que o serviço falhou por causa de uma exceção aritimética. */
            throw new ServiceException(MessageList.createSingleInternalError(e));
		}
//        if (dias > 0) {
//            double multa = inMultaInicial.doubleValue();
//            if (dias - inDiasTolerancia.intValue() > 0)
//                multa += inMultaAdicional.doubleValue() * ((dias - inDiasTolerancia.intValue()) / 30);
//            valorMulta = inValor.multiply(BigDecimal.valueOf(multa / 100)); 
//        }
        sd.getOutputData().add(OUT_MULTA, valorMulta.setScale(2, BigDecimal.ROUND_HALF_UP));
        
        /*
         * Juros
         */
        BigDecimal valorJuros = DecimalUtils.getBigDecimal(0);
        try {
        	valorJuros = UtilsJuros.calcularJuros(inValor, inJurosMora, inDataVencimento, inData, inDiasTolerancia);
		} catch (NullPointerException e) {
            /* Indica que o serviço falhou por causa de uma exceção de NullPointer. */
            throw new ServiceException(MessageList.createSingleInternalError(e));
		} catch (ArithmeticException e) {
            /* Indica que o serviço falhou por causa de uma exceção aritimética. */
            throw new ServiceException(MessageList.createSingleInternalError(e));
		}
//        if (dias - inDiasTolerancia.intValue() > 0) {
//            double juros = inJurosMora.doubleValue() * (dias - inDiasTolerancia.intValue()) / 30;
//            valorJuros = inValor.multiply(BigDecimal.valueOf(juros / 100));
//        }
        sd.getOutputData().add(OUT_JUROS, valorJuros.setScale(2, BigDecimal.ROUND_HALF_UP));
	}
}
