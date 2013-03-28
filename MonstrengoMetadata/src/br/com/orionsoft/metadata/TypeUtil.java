package br.com.orionsoft.metadata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilitário para facilitar a interpretação do tipo de uma variável.
 * 
 * @author andre
 *
 */
public class TypeUtil {
	
	public static String STRING_TYPE = String.class.getSimpleName();
	
	public static Set<String> NUMERIC_TYPES = new HashSet<String>(Arrays.asList(
			BigInteger.class.getSimpleName(), BigDecimal.class.getSimpleName(),
			Double.class.getSimpleName(), Float.class.getSimpleName(),
			Integer.class.getSimpleName(), Long.class.getSimpleName(), "int",
			"long", "float", "double"));
	
	public static Set<String> DATE_TYPES = new HashSet<String>(Arrays.asList(
			Date.class.getSimpleName(), Calendar.class.getSimpleName()));
	
	public static Set<String> BOOLEAN_TYPES = new HashSet<String>(Arrays.asList(
			Boolean.class.getSimpleName(), "boolean"));
	
}
