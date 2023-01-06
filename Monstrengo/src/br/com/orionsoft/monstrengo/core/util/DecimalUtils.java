package br.com.orionsoft.monstrengo.core.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;

/*
 * Created on 22/12/2005
 *
 */

/**
 * Classe utilitária para criação de instâncias de BigDecimal já padronizadas.
 * @author Antonio Alves
 * @version 20060501
 */
public class DecimalUtils {
	public static final BigDecimal ZERO = getBigDecimal(0); 
	public static final BigDecimal ONE = getBigDecimal(1); 
	public static final BigDecimal TEN = getBigDecimal(10); 

    /**
     * Retorna uma instância de BigDecimal a partir de uma String e assume escala 2,
     * com arredondamento para cima.
     * @param val
     * @return BigDecimal arredondado para cima
     * @version 20060703 Lucio
     */
    public static BigDecimal getBigDecimal(String val) {
        return getBigDecimal(val, 2);
    }
    
    /**
     * Retorna uma instância de BigDecimal a partir de uma String e uma escala,
     * com arredondamento para cima.
     * @param val
     * @param scale
     * @return BigDecimal arredondado para cima
     * @version 20060703 Lucio
     */
    public static BigDecimal getBigDecimal(String val, int scale) {
        if (!StringUtils.contains(val, '.')) {
            double format = Double.parseDouble(val) / (Math.pow(10, scale));
            return new BigDecimal(format, MathContext.DECIMAL64).setScale(scale, BigDecimal.ROUND_HALF_UP);
        }
        else {
            return new BigDecimal(val, MathContext.DECIMAL64).setScale(scale, BigDecimal.ROUND_HALF_UP);
        }
    }
    
    /**
     * Retorna uma instância de BigDecimal a partir de um double, 
     * com duas casas decimais e arredondamento para cima.
     * @param val
     * @return BigDecimal com 2 casas decimais arredondado para cima
     */
    public static BigDecimal getBigDecimal(double val) {
        return getBigDecimal(val, 2);
    }

    /**
     * Retorna uma instância de BigDecimal a partir de um double e uma escala,
     * com arredondamento para cima.
     * @param val
     * @param scale
     * @return BigDecimal arredondado para cima
     */
    public static BigDecimal getBigDecimal(double val, int scale) {
        return getBigDecimal(val, scale, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * Retorna uma instância de BigDecimal a partir de um double, uma escala e um RoundingMode.
     * @param val
     * @param scale
     * @param roundingMode
     * @return BigDecimal
     */
    public static BigDecimal getBigDecimal(double val, int scale, int roundingMode) {
        return new BigDecimal(val, MathContext.DECIMAL64).setScale(scale, roundingMode);
    }
    
    /**
     * Formata um Big Decimal verificando antes se ele é nulo.
     * Se o BigDecimal for igual à zero, a função de formatação
     * não dará erro, pois é executado o método doubleValue(). 
     * Assim, não ocorre erro:'formatException: Digits < 0'
     *   
     * @param bigDecimal
     * @return
     * @version 20060501
     */
    public static String formatBigDecimal(BigDecimal bigDecimal){
        if (bigDecimal!= null)
            return String.format("%,.2f", bigDecimal.doubleValue());
        
        return "";
    }
    
    /**
     * Compara um decimal com 0 ou nulo
     *   
     * @return
     * @version 20060619
     * @since 20060619
     */
    public static boolean isZero(BigDecimal bigDecimal){
        return (bigDecimal == null) || (bigDecimal.compareTo(new BigDecimal("0"))==0);
    }
    
    public static void main(String[] args) {
		BigDecimal d = new BigDecimal(1.2, MathContext.DECIMAL64);
		
		BigDecimal d2 = new BigDecimal(30.0);
		System.out.println(d.divide(d2, 5, RoundingMode.HALF_UP));
		System.out.println(d);

		BigDecimal d3 = new BigDecimal("000000000000");
		System.out.println(d3.doubleValue() == 0.00);
	}
}
