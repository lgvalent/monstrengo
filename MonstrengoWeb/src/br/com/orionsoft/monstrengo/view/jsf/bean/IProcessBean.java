package br.com.orionsoft.monstrengo.view.jsf.bean;

import java.util.Map;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;

/**
 * Esta interface é utilizada por controladores de visões que
 * estão ligados diretamente a um processo de negócio (IProcess) e que 
 * o gerenciador de visões precisa saber quais processos são controlados.
 * @author lucio
 *
 */
public interface IProcessBean
{
	/** Provê acesso ao Bean que controla a aplicação */
	public abstract ApplicationBean getApplicationBean();
	public abstract void setApplicationBean(ApplicationBean appBean);
	
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
	 * foi tratado pela interface IRunnableEntityProcess.
	 * @author Lucio
	 * @since 20070530
	 * @version 20070530 
	 */
	public abstract String actionStart() throws BusinessException, Exception;

}