package br.com.orionsoft.monstrengo.crud.processes;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Esta classe mantem os nomes do componente destino e da
 * propriedade que ser�o selecionadas caso o operador
 * selecione um registro e seja necess�rio retornar o
 * valor de alguma propriedade. COmo � o caso do 
 * cadastro onde um tela de pesquisa � aberta para que
 * o operador selecione uma entidade.
 * 
 * @author 20060308 
 */
public class SelectParam
{
    private String selectOneDest = "";
    private String selectProperty = IDAO.PROPERTY_ID_NAME;
    
	/**
	 * Este campo armazena o nome do componente HTML que receber� o valor do 
	 * id pesquisado
	 */
	public String getSelectOneDest() {return selectOneDest;}
	public void setSelectOneDest(String selectOne) {this.selectOneDest = selectOne;}
	public boolean isSelectOneActive(){return !StringUtils.isEmpty(selectOneDest);}
	
	/**
	 * Este campo armazena o nome da propriedade da entidade que dever�
	 * ser retornada
	 */
	public String getSelectProperty(){return selectProperty;}
	public void setSelectProperty(String selectOneField){this.selectProperty = selectOneField;}
	
}