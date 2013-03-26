package br.com.orionsoft.monstrengo.crud.services;

import java.util.ArrayList;
import java.util.List;

import br.com.orionsoft.monstrengo.crud.services.Operator;

/**
 *	Esta classe representa os operadores que fazem as restrições numa busca Hql.
 * 	Cada operador possui um id, label e simbol.
 * 
 */

public class Operator
{
	public static final int EQUAL = 0;
    public static final int NOT_EQUAL = 1;
    public static final int MORE_THEN = 2;
    public static final int MORE_EQUAL = 3;
    public static final int LESS_THEN = 4;
    public static final int LESS_EQUAL = 5;
    public static final int LIKE = 6;
    public static final int NOT_LIKE = 7;
    public static final int NULL = 8;
    public static final int NOT_NULL = 9;
    public static final int BETWEEN = 10;
    public static final int NOT_BETWEEN = 11;
    public static final int IN = 12;
    public static final int NOT_IN = 13;
    
    private int id=-1;
    private String label;
    private String symbol;
    
    
    public int getId(){return id;}
	public void setId(int id){this.id = id;}

	public String getLabel(){return label;}
	public void setLabel(String label){this.label = label;}

	public String getSymbol(){return symbol;}
	public void setSymbol(String symbol){this.symbol = symbol;}
	
	/** Indica que o atual operador não necessita da entrada de valores
	 * para realizar sua operação como é o caso dos operadores NULL e NOT_NULL. 
	 */
	public boolean isNoValueNeeded(){
		switch (this.id) {
		case NULL: return true;
		case NOT_NULL: return true;
		default:
			return false;
		}
	}

	/** Indica que o atual operador necessita de pelo menos uma entrada de valor
	 * para realizar sua operação como é o caso da maioria dos operadores. 
	 */
	public boolean isOneValueNeeded(){
		switch (this.id) {
		case NULL: return false;
		case NOT_NULL: return false;
		default:
			return true;
		}
	}

	/** Indica que o atual operador necessita da entrada de dois valores
	 * para realizar sua operação como é o caso dos operadores BETWEEN e NOT_BETWEEN. 
	 */
	public boolean isTwoValueNeeded(){
		switch (this.id) {
		case BETWEEN: return true;
		case NOT_BETWEEN: return true;
		default:
			return false;
		}
	}

	/* Construtor padrão 
	 * TODO IMPLEMENTAR colocar a descrição dos operadores em arquivos externos de texto */
	public Operator(int operatorId){
		switch (operatorId) {
		case EQUAL:
			fillOperator(this, EQUAL, "igual a", "=");
			break;
		case NOT_EQUAL:
			fillOperator(this, NOT_EQUAL, "diferente de", "!=");
			break;
		case MORE_THEN:
			fillOperator(this, MORE_THEN, "maior que", ">");
			break;
		case MORE_EQUAL:
			fillOperator(this, MORE_EQUAL, "maior ou igual a", ">=");
			break;
		case LESS_THEN:
			fillOperator(this, LESS_THEN, "menor que", "<");
			break;
		case LESS_EQUAL:
			fillOperator(this, LESS_EQUAL, "menor ou igual a", "<=");
			break;
		case LIKE:
			fillOperator(this, LIKE, "contenha", "$");
			break;
		case NOT_LIKE:
			fillOperator(this, NOT_LIKE, "não contenha", "!$");
			break;
		case NULL:
			fillOperator(this, NULL, "vazio", "?");
			break;
		case NOT_NULL:
			fillOperator(this, NOT_NULL, "não vazio", "!?");
			break;
		case BETWEEN:
			fillOperator(this, BETWEEN, "entre", "[]");
			break;
		case NOT_BETWEEN:
			fillOperator(this, NOT_BETWEEN, "não está entre", "][");
			break;
		case IN:
			fillOperator(this, IN, "está contido", "(,,)");
			break;
		case NOT_IN:
			fillOperator(this, NOT_IN, "não está contido", "),,(");
			break;

		default:
			throw new RuntimeException("Nenhum operador foi definido para o Id:" + operatorId);
		}
	}

	
	private void fillOperator(Operator op, int id, String label, String symbol){
		op.setId(id);
		op.setLabel(label);
		op.setSymbol(symbol);
	}
	
	/*Retorna lista de Operadores*/
	private static List<Operator> listOperator = null;
	public static List<Operator> getOperators(){
		if(listOperator == null){
			listOperator = new ArrayList<Operator>() ;
			listOperator.add(new Operator(EQUAL));
			listOperator.add(new Operator(NOT_EQUAL));
			listOperator.add(new Operator(MORE_THEN));
			listOperator.add(new Operator(MORE_EQUAL));
			listOperator.add(new Operator(LESS_THEN));
			listOperator.add(new Operator(LESS_EQUAL));
			listOperator.add(new Operator(LIKE));
			listOperator.add(new Operator(NOT_LIKE));
			listOperator.add(new Operator(NULL));
			listOperator.add(new Operator(NOT_NULL));
			listOperator.add(new Operator(BETWEEN));
			listOperator.add(new Operator(NOT_BETWEEN));
			listOperator.add(new Operator(IN));
			listOperator.add(new Operator(NOT_IN));
		}
		return listOperator;
    	
    }

}
