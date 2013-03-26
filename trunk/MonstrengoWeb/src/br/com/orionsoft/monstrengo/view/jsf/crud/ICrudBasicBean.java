package br.com.orionsoft.monstrengo.view.jsf.crud;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.view.jsf.bean.IBasicBean;

public interface ICrudBasicBean extends IBasicBean
{

	/** 
	 * Faz a valida��o da entidade corrente antes de restaurar a vis�o. 
	 */
	public abstract void validateCurrentEntityKey(FacesContext facesContext,
			UIComponent component, Object newValue) throws BusinessException,
			Exception;

	/**
	 * Fornece acesso a liga��o entre o componente instanciado pelo JSF e 
	 * o Bean que ir� controlar a entidade correntemente manipulada pelo Bean. <br>
	 * Assim, � possivel manipular o componente JSF da p�gina diretamente
	 * pelo c�digo interno do bean.<br>
	 * � poss�vel acessar o valor do formul�rio antes mesmo dele ser validado ou 
	 * populado para dentro do Bean. E ainda, � poss�vel anular seu valor para
	 * for�ar uma valida��o na pr�xima renderiza��o.
	 */
	public abstract HtmlInputHidden getInputCurrentEntityKey();
	public abstract void setInputCurrentEntityKey(HtmlInputHidden inputCurrentEntityKey)
			throws Exception;

	/**
	 * Carrega os par�metros pertinente aos Bean da atual transa��o.   
	 * Antes de recarregar os par�metros, o Bean sofre um reset() para 
	 * que os par�metros atuais sejam limpos.
	 */
	public abstract void loadEntityParams() throws Exception;
	
	public String actionCancel() throws Exception;

	public abstract String getCurrentEntityKey() throws Exception;
	public abstract void setCurrentEntityKey(String currentEntityKey);
	
	/**
	 * De acordo com a chave passada, este m�todo prepara a entidade 
	 * que ser� manipulada pela atual requisi��o do Bean. 
	 * Com a chave, � pesquisado se um processo interno j� manipula 
	 * a entidade, se  n�o encontrar, um novo processo � instanciado
	 * e colocado no mapa de processos atualmente ativos no Bean. 
	 * @param currentEntityKey
	 * @throws BusinessException
	 * @throws Exception
	 */
	public void prepareCurrentEntity(String currentEntityKey) throws BusinessException, Exception;

	/**
	 * Este m�todo prepara a chave da entidade corrente baseando-se
	 * nos par�metros da entidade e do pai da entidade.
	 * Deve-se evitar utilizar outros recursos do bean que n�o seja
	 * o entityParam() para compor a chave da vis�o.
	 * @return Retorna uma chave de identifica��o da entidade corrente
	 * @throws Exception
	 */
	public String prepareCurrentEntityKey();
	
	public abstract EntityParam getEntityParam();

}