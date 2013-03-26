package br.com.orionsoft.monstrengo.core.service;

/**
 * Esta classe implementa algumas funcionalidades comuns para beans que são
 * resultados personalizados de alguns serviços.<br>
 * Em sua maioria, são serviços que realizam consulta no banco e obtem uma lista
 * de entidades, porém, somente algumas propriedades das entidades são selecionadas 
 * e estas propriedades precisam ser armazenadas em um bean.
 * 
 * @author lucio
 * @version 20060613
 *
 */
public abstract class ServiceResultBean {

	/** Permite que um elemento seja marcado ou desmarcado, assim,
	 * ao fazer parte de uma lista, esta lista poderá ter alguns itens 
	 * desmarcados e estes itens não serão processados por outro determinado 
	 * serviço que receberá esta lista. 
	 */
	private boolean checked = true;
	public boolean isChecked() {return checked;}
	public void setChecked(boolean checked) {this.checked = checked;}
}
