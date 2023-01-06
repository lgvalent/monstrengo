package br.com.orionsoft.monstrengo.view.jsf.bean;

import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;



/**
 * Esta interface define os métodos necessários para que o gerenciador 
 * de sessão do usuário (UserSessionBean) controle todas as visões que possam ser acionadas
 * para disparar um determinado processo.<br>
 * Esta interface surgiu da necessidade de uma visão (tela) disparar outra visão
 * baseada no processo que a visão manipula.<br>
 * No entanto, um processo não possui referência sobre qual tela ele deve acionar, pois 
 * isto é responsabilidade da camada de visão, e o processo se encontra numa 
 * camada inferior.
 * 
 * @author Lucio
 * @since 20070605
 * @version 20070605
 */
public interface IRunnableProcessView extends IBasicBean
{
	/** Fornece no nome do processo que a atual visão utiliza para ser executada.
	 * O UserSessionBean vai manter uma mapa com o nome dos processos apontando 
	 * para um controlador de visão (Bean). Assim, será possível à uma visão disparar
	 * outra visão, baseando-se no nome do processo que se deseja disparar.<br>
	 */
	public abstract String getRunnableEntityProcessName();

	/**
     * Este método possibilita que a visão seja invocada com base 
     * nos dados de uma determinada instância de uma entidade.<br>
     * A entidade fornecida pode não ser compatível. Porém isto será alertado
     * quando o método {@link IRunnableEntityProcess.runWithEntity(IEntity)} for invocado. 
     * 
     * @return Retorna o identificador da próxima visão que será desviada 
     */
	public String runWithEntity(IEntity<?> entity);

	/**
     * Este método possibilita que a visão seja invocada com base 
     * nos dados de uma coleção de instâncias de uma entidade.<br>
     * A entidade fornecida pode não ser compatível. Porém isto será alertado
     * quando o método {@link IRunnableEntityProcess.runWithEntity(IEntity)} for invocado. 
     * 
     * @return Retorna o identificador da próxima visão que será desviada 
     */
	public String runWithEntities(IEntityCollection<?> entities);
}