package br.com.orionsoft.monstrengo.view.jsf.bean;

import br.com.orionsoft.monstrengo.crud.entity.IEntity;



/**
 * Esta interface define os m�todos necess�rios para que o gerenciador 
 * de sess�o do usu�rio (UserSessionBean) controle todas as vis�es que possam ser acionadas
 * para disparar um determinado processo.<br>
 * Esta interface surgiu da necessidade de uma vis�o (tela) disparar outra vis�o
 * baseada no processo que a vis�o manipula.<br>
 * No entanto, um processo n�o possui refer�ncia sobre qual tela ele deve acionar, pois 
 * isto � responsabilidade da camada de vis�o, e o processo se encontra numa 
 * camada inferior.
 * 
 * @author Lucio
 * @since 20070605
 * @version 20070605
 */
public interface IRunnableProcessView extends IBasicBean
{
	/** Fornece no nome do processo que a atual vis�o utiliza para ser executada.
	 * O UserSessionBean vai manter uma mapa com o nome dos processos apontando 
	 * para um controlador de vis�o (Bean). Assim, ser� poss�vel � uma vis�o disparar
	 * outra vis�o, baseando-se no nome do processo que se deseja disparar.<br>
	 */
	public abstract String getRunnableEntityProcessName();

	/**
     * Este m�todo possibilita que a vis�o seja invocada com base 
     * nos dados de uma determinada inst�ncia de uma entidade.<br>
     * A entidade fornecida pode n�o ser compat�vel. Por�m isto ser� alertado
     * quando o m�todo {@link IRunnableEntityProcess.runWithEntity(IEntity)} for invocado. 
     * 
     * @return Retorna o identificador da pr�xima vis�o que ser� desviada 
     */
	public String runWithEntity(IEntity<?> entity);
}