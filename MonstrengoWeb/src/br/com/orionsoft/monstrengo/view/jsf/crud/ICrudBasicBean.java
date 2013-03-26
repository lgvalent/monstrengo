package br.com.orionsoft.monstrengo.view.jsf.crud;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.view.jsf.bean.IBasicBean;

public interface ICrudBasicBean extends IBasicBean
{

	/** 
	 * Faz a validação da entidade corrente antes de restaurar a visão. 
	 */
	public abstract void validateCurrentEntityKey(FacesContext facesContext,
			UIComponent component, Object newValue) throws BusinessException,
			Exception;

	/**
	 * Fornece acesso a ligação entre o componente instanciado pelo JSF e 
	 * o Bean que irá controlar a entidade correntemente manipulada pelo Bean. <br>
	 * Assim, é possivel manipular o componente JSF da página diretamente
	 * pelo código interno do bean.<br>
	 * É possível acessar o valor do formulário antes mesmo dele ser validado ou 
	 * populado para dentro do Bean. E ainda, é possível anular seu valor para
	 * forçar uma validação na próxima renderização.
	 */
	public abstract HtmlInputHidden getInputCurrentEntityKey();
	public abstract void setInputCurrentEntityKey(HtmlInputHidden inputCurrentEntityKey)
			throws Exception;

	/**
	 * Carrega os parâmetros pertinente aos Bean da atual transação.   
	 * Antes de recarregar os parâmetros, o Bean sofre um reset() para 
	 * que os parâmetros atuais sejam limpos.
	 */
	public abstract void loadEntityParams() throws Exception;
	
	public String actionCancel() throws Exception;

	public abstract String getCurrentEntityKey() throws Exception;
	public abstract void setCurrentEntityKey(String currentEntityKey);
	
	/**
	 * De acordo com a chave passada, este método prepara a entidade 
	 * que será manipulada pela atual requisição do Bean. 
	 * Com a chave, é pesquisado se um processo interno já manipula 
	 * a entidade, se  não encontrar, um novo processo é instanciado
	 * e colocado no mapa de processos atualmente ativos no Bean. 
	 * @param currentEntityKey
	 * @throws BusinessException
	 * @throws Exception
	 */
	public void prepareCurrentEntity(String currentEntityKey) throws BusinessException, Exception;

	/**
	 * Este método prepara a chave da entidade corrente baseando-se
	 * nos parâmetros da entidade e do pai da entidade.
	 * Deve-se evitar utilizar outros recursos do bean que não seja
	 * o entityParam() para compor a chave da visão.
	 * @return Retorna uma chave de identificação da entidade corrente
	 * @throws Exception
	 */
	public String prepareCurrentEntityKey();
	
	public abstract EntityParam getEntityParam();

}