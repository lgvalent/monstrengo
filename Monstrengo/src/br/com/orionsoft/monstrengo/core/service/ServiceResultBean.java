package br.com.orionsoft.monstrengo.core.service;

/**
 * Esta classe implementa algumas funcionalidades comuns para beans que s�o
 * resultados personalizados de alguns servi�os.<br>
 * Em sua maioria, s�o servi�os que realizam consulta no banco e obtem uma lista
 * de entidades, por�m, somente algumas propriedades das entidades s�o selecionadas 
 * e estas propriedades precisam ser armazenadas em um bean.
 * 
 * @author lucio
 * @version 20060613
 *
 */
public abstract class ServiceResultBean {

	/** Permite que um elemento seja marcado ou desmarcado, assim,
	 * ao fazer parte de uma lista, esta lista poder� ter alguns itens 
	 * desmarcados e estes itens n�o ser�o processados por outro determinado 
	 * servi�o que receber� esta lista. 
	 */
	private boolean checked = true;
	public boolean isChecked() {return checked;}
	public void setChecked(boolean checked) {this.checked = checked;}
}
