package br.com.orionsoft.monstrengo.view.jsf.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableProcessEntry;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.view.jsf.bean.BasicBean;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Est� classe � o controlador da vis�o de gera��o de etiquetas 
 * para uma entidade espec�fica ou uma lista de entidades
 * usando um modelo.
 * 
 * @author Lucio 20061005
 * @version 20061005
 * 
 * @jsf.bean name="labelEntityBean" scope="session"
 * @jsf.navigation from="*" result="labelView" to="/pages/basic/label/view.jsp" 
 */
@ManagedBean
@SessionScoped
public class RunnableProcessBean extends BasicBean
{
	/*
	 * INICIO Controle de RunnableEntitiesProcess
	 */
	/**
	 * Classe que descreve uma entrada para facilitar a constru��o de links que disparam
	 * uma determinada vis�o pelo seu nome, mas que exibem detalhes (label, hint e description)
	 * sobre o processo que ela manipula.
	 * 
	 * @author lucio 
	 * @since 20070611
	 */
	public class RunnableViewEntry{
		private String viewName;
		private RunnableProcessEntry runnableProcessEntry;

		public RunnableViewEntry(String viewName, RunnableProcessEntry runnableProcessEntry) {
			this.viewName = viewName;
			this.runnableProcessEntry = runnableProcessEntry;
		}
		public String getViewName() {return viewName;}
		public RunnableProcessEntry getRunnableProcessEntry(){return runnableProcessEntry;}
	}

	/**
	 * Retorna uma lista de processos que podem ser executados a partir de uma dada entidade.
	 * Era feito buffer da lista, mas o m�todo � chamado somente uma vez com o JSF 2.0.
	 * Se voltar a fazer algum buffer, vale lembrar que o tipo e o id da entidade devem ser usados
	 * para verificar a validade do buffer
	 * @param entity
	 * @return
	 */
	public List<RunnableViewEntry> getRunnableViewsForUser(IEntity<?> entity) {
		List<RunnableViewEntry> runnableViewForUserBuffer = new ArrayList<RunnableViewEntry>();
		try{
				/* Pega todos os processo que podem ser disparados pela entidade e operador atuais */
				for(RunnableProcessEntry runnableProcessEntry: this.getApplicationBean().getProcessManager().getRunnableProcessesEntry(entity, this.getUserSessionBean().getUserSession())){
					/* Verifica quais vis�es manipula o atual processo */
					IRunnableProcessView view = this.getUserSessionBean().getRunnableEntityProcessViewByProcessName(runnableProcessEntry.getProcessClass().getSimpleName()); 
					if(view != null ){

						RunnableViewEntry entry = new RunnableViewEntry(view.getViewName(), runnableProcessEntry);

						runnableViewForUserBuffer.add(entry);
					}else
						FacesUtils.addErrorMsg("Nenhuma vis�o foi encontrada para o processo " + runnableProcessEntry.getProcessClass().getSimpleName() + ". Verifique se para um processo 'MyProcess' existe uma vis�o configurada com o nome 'myView'.");
				}
		}catch (ProcessException e){
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}

		return runnableViewForUserBuffer; 
	}

	public boolean hasRunnableViewaForUser(IEntity<?> entity){
		return this.getRunnableViewsForUser(entity).size()>0;
	}

	public String actionActivateView(String runnableViewName, IEntity<?> entity) throws BusinessException, Exception{

		/* Pega o identificador da vis�o */
		if(log.isDebugEnabled())
			log.debug("Iniciando a ativa��o da vis�o:" + runnableViewName);

		IRunnableProcessView runnableProcessView =  (IRunnableProcessView) this.getUserSessionBean().getRunnableEntityProcessViewByName(runnableViewName);

		if (runnableProcessView == null)
			FacesUtils.addErrorMsg("Nenhuma vis�o foi encontrada no facesConfig.xml com id: " + runnableViewName + ". Confira o nome VIEW_NAME definido na classe que contra a vis�o.");
		else
			if (entity == null)
				return runnableProcessView.actionStart();
			else
				return runnableProcessView.runWithEntity(entity);

		return FacesUtils.FACES_VIEW_FAILURE;
	}

	public String actionStartProcessView() throws BusinessException, Exception{

		String processName = FacesUtils.getRequestParams().get("processName").toString();
		/* Pega o identificador da vis�o */
		if(log.isDebugEnabled())
			log.debug("Buscando uma vis�o para o processo:" + processName);

		IRunnableProcessView runnableProcessView =  (IRunnableProcessView) this.getUserSessionBean().getRunnableEntityProcessViewByProcessName(processName);

		if (runnableProcessView == null)
			FacesUtils.addErrorMsg("Nenhuma vis�o registrada para o processo: " + processName + ".");
		else
			return runnableProcessView.actionStart();

		return FacesUtils.FACES_VIEW_FAILURE;
	}

	public String actionOpenProcessView(String processName) throws BusinessException, Exception{

		/* Pega o identificador da vis�o */
		if(log.isDebugEnabled())
			log.debug("Buscando uma vis�o para o processo:" + processName);

		IRunnableProcessView runnableProcessView =  (IRunnableProcessView) this.getUserSessionBean().getRunnableEntityProcessViewByProcessName(processName);

		if (runnableProcessView == null)
			FacesUtils.addErrorMsg("Nenhuma vis�o registrada para o processo: " + processName + ".");
		else
			return runnableProcessView.actionStart();

		return FacesUtils.FACES_VIEW_FAILURE;
	}

	@Override
	public Map getRequestParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void doReload() throws BusinessException, Exception {
		// TODO Auto-generated method stub

	}


}