package br.com.orionsoft.monstrengo.view.jsf.bean;

import java.util.Map;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;

/**
 * Esta interface � utilizada por controladores de vis�es que
 * est�o ligados diretamente a um processo de neg�cio (IProcess) e que 
 * o gerenciador de vis�es precisa saber quais processos s�o controlados.
 * @author lucio
 *
 */
public interface IProcessBean
{
	/** Prov� acesso ao Bean que controla a aplica��o */
	public abstract ApplicationBean getApplicationBean();
	public abstract void setApplicationBean(ApplicationBean appBean);
	
	/**
	 * Este m�todo � respons�vel por fornecer acesso aos 
	 * par�metros da atual requisi��o.
	 */
	public abstract Map getRequestParams();


	/**
	 * Limpa todos os par�metros anteriormente carregados,
	 * voltando seu valor padr�o. Assim, se o bean receber
	 * somente alguns par�metros eles n�o se misturar�o com 
	 * outros velhos par�metros.
	 */
	public abstract void doReset() throws BusinessException, Exception;

	/**
	 * Este m�todo anula a atual inst�ncia dos par�metros 
	 * carregados anteriormente e recarrega os par�metros. 
	 * Assim, a entidade corrente ser� novamente preparada 
	 * baseada nos par�metros atuais. 
	 */
	public abstract void doReload() throws BusinessException, Exception;

	/**
	 * Esta a��o foi definida para que todos os controladores de vis�o
	 * tenha pelo menos um action em comum para inici�-lo.<br>
	 * Este action � respons�vel por iniciar o controlador e redirecionar para 
	 * a primeira tela.<br>
	 * Com esta a��o tamb�m � possivel invocar genericamente o primeiro passo de
	 * um bean.<br>
	 * A necessidade desta a��o surgiu com a possibilidade de um gerenciador de processos
	 * invocar um processo tendo como par�metro uma entidade compat�vel. Este conceito
	 * foi tratado pela interface IRunnableEntityProcess.
	 * @author Lucio
	 * @since 20070530
	 * @version 20070530 
	 */
	public abstract String actionStart() throws BusinessException, Exception;

}