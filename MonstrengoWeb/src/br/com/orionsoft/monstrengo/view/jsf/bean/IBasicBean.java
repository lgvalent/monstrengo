package br.com.orionsoft.monstrengo.view.jsf.bean;

import java.util.Map;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.view.jsf.security.UserSessionBean;

public interface IBasicBean
{
	/** Fornece no nome da visão */
	public abstract String getViewName();

	/** Provê acesso ao Bean que controla a aplicação */
	public abstract ApplicationBean getApplicationBean();
	public abstract void setApplicationBean(ApplicationBean appBean);
	
	/** Provê acesso ao Bean que controla a sessão Web do usuário */
	public abstract UserSessionBean getUserSessionBean();
	public abstract void setUserSessionBean(UserSessionBean userSessionBean);
	
	/**
	 * Este método é responsável por fornecer acesso aos 
	 * parâmetros da atual requisição.
	 */
	public abstract Map getRequestParams();


	/**
	 * Limpa todos os parâmetros anteriormente carregados,
	 * voltando seu valor padrão. Assim, se o bean receber
	 * somente alguns parâmetros eles não se misturarão com 
	 * outros velhos parâmetros.
	 */
	public abstract void doReset() throws BusinessException, Exception;

	/**
	 * Este método anula a atual instância dos parâmetros 
	 * carregados anteriormente e recarrega os parâmetros. 
	 * Assim, a entidade corrente será novamente preparada 
	 * baseada nos parâmetros atuais. 
	 */
	public abstract void doReload() throws BusinessException, Exception;

	/**
	 * Esta ação foi definida para que todos os controladores de visão
	 * tenha pelo menos um action em comum para iniciá-lo.<br>
	 * Este action é responsável por iniciar o controlador e redirecionar para 
	 * a primeira tela.<br>
	 * Com esta ação também é possivel invocar genericamente o primeiro passo de
	 * um bean.<br>
	 * A necessidade desta ação surgiu com a possibilidade de um gerenciador de processos
	 * invocar um processo tendo como parâmetro uma entidade compatível. Este conceito
	 * foi tratado pela interface IRunnableEntityProcess.<br>
	 * Contudo, foi verificada a necessidade de uma padronização para todas as visões.
	 * Logo, este método precisa estar em todas as visões.
	 * 
	 * @author Lucio
	 * @since 20070530
	 * @version 20070530 
	 */
	public abstract String actionStart() throws BusinessException, Exception;

}