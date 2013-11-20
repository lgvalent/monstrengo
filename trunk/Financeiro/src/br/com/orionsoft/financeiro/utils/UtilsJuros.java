package br.com.orionsoft.financeiro.utils;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.basic.entities.commons.Frequencia;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;

public class UtilsJuros {
    
    /** 
     * Calcula os juros de um valor principal 
     * @param valorPrincipal
     * @param dataVencimento
     * @param dataPagamento
     * @param toleranciaDiasJuros
     * @return
     */
	public static BigDecimal calcularJuros(
			BigDecimal valorPrincipal, 
			BigDecimal taxaJuros, 
			Calendar dataVencimento, 
			Calendar dataPagamento, 
			int diasTolerancia) { 
		BigDecimal result = DecimalUtils.getBigDecimal(0);
		try {
			long diferenca = CalendarUtils.diffDay(dataPagamento, dataVencimento); 
			if (diferenca - diasTolerancia > 0) {
				double meses = 
					((dataPagamento.get(Calendar.YEAR) - dataVencimento.get(Calendar.YEAR)) * 12) +
					(dataPagamento.get(Calendar.MONTH) - dataVencimento.get(Calendar.MONTH));
				
				
				/* Lucio 20120821: Calcula juros proporcional aos dias do último mês incompleto de vencimento */
				int dias = (dataPagamento.get(Calendar.DATE) - dataVencimento.get(Calendar.DATE));
				if(dias > 0){
					/* Vence dia 10, está pagando dia 15: 5/30 */
					meses += dias / 30.0;
				}else{
					/* Vence dia 15, está pagando dia 10 de um mês seguinte
					 * Decrementa um mês e pega o complemento em dias, ou seja, não é uma mês inteiro  */
					meses += -1 + ((30 + dias)/ 30.0 );
				}
				
				double juros = taxaJuros.doubleValue() * meses;
				result = valorPrincipal.multiply(BigDecimal.valueOf(juros / 100.0));
			}
		} catch (NullPointerException e) {
		} catch (ArithmeticException e) {
		}
        return result.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

	/**
	 * 
	 * @param valorPrincipal
	 * @param percentualMulta
	 * @param percentualMultaAdicional Percentual da multa adicional
	 * @param frequenciaMultaAdicional Indica se a multa adicional deverá ser incidida diária, semanalmente, mensalmente ou etc.
	 * @param dataVencimento
	 * @param dataPagamento
	 * @param diasTolerancia Dias de tolerância dados para o pagamento em atraso sem a cobrança de multa
	 * @return
	 */
	public static BigDecimal calcularMulta(
			BigDecimal valorPrincipal, 
			BigDecimal percentualMulta, 
			BigDecimal percentualMultaAdicional, 
			Frequencia frequenciaMultaAdicional, 
			Calendar dataVencimento, 
			Calendar dataPagamento, 
			int diasTolerancia) {
		BigDecimal result = DecimalUtils.getBigDecimal(0);
		try {
			long diferenca = CalendarUtils.diffDay(dataPagamento, dataVencimento);
			if (diferenca - diasTolerancia > 0) {
				double multa = percentualMulta.doubleValue();
				// verificar a frequencia :)
				int meses = 
					((dataPagamento.get(Calendar.YEAR) - dataVencimento.get(Calendar.YEAR)) * 12) +
					(dataPagamento.get(Calendar.MONTH) - dataVencimento.get(Calendar.MONTH)) - 1;
				multa += percentualMultaAdicional.doubleValue() * meses;
				result = valorPrincipal.multiply(BigDecimal.valueOf(multa / 100.0)); 
			}
		} catch (NullPointerException e) {
		} catch (ArithmeticException e) {
		}
		return result.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/** Rotina para testes rápidos */
	public static void main(String[] args) {
		BigDecimal juros = UtilsJuros.calcularJuros(
				new BigDecimal(110),
				new BigDecimal(1), 
				CalendarUtils.getCalendar(2012, Calendar.AUGUST, 12),
				CalendarUtils.getCalendar(2012, Calendar.AUGUST, 30),
				0);
		
		BigDecimal multa = UtilsJuros.calcularMulta(
				new BigDecimal(110),
				new BigDecimal(10), 
				BigDecimal.ZERO,
				null,
				CalendarUtils.getCalendar(2012, Calendar.AUGUST, 12),
				CalendarUtils.getCalendar(2012, Calendar.AUGUST, 13),
				0);
		
		System.out.println("Juros calculado:" + juros);
		System.out.println("Multa calculada:" + multa);
	}
}
