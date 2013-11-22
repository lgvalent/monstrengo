package br.com.orionsoft.monstrengo.view.jsf.crud;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditProcessRegister;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.SqlQueryProcess;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que cotrola a view de pesquisa SQL Nativa
 * 
 * @author Lucio 20121022
 */
@ManagedBean
@SessionScoped
public class SqlQueryBean extends BeanSessionBasic implements IRunnableProcessView
{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para a visão QUERY */
	public static final String VIEW_NAME = "sqlQueryBean";
	public static final String FACES_VIEW_QUERY = "/pages/basic/sqlQuery?faces-redirect=true";

	// Dados internos da classe
	private SqlQueryProcess currentProcess = null;
	
	private IEntity<AuditProcessRegister> history = null;

	public IEntity<AuditProcessRegister> getHistory() {return history;}
	public void setHistory(IEntity<AuditProcessRegister> history) {this.history = history;}

	public SqlQueryProcess getCurrentProcess() {

		if (currentProcess == null) {
			try {
				currentProcess = (SqlQueryProcess) this.getApplicationBean().getProcessManager().createProcessByName(SqlQueryProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
			} catch (ProcessException e) {
				FacesUtils.addErrorMsgs(e.getErrorList());
			}
		}
		return currentProcess;
	}

	/**
	 * Action que constrói a lista e redireciona a view para "list". 
	 * @return
	 */
	public void doExecute() throws Exception
	{
		log.debug("::Iniciando doExecute");

		if(this.getCurrentProcess().runQuery())
			FacesUtils.addInfoMsgs(this.getCurrentProcess().getMessageList());
		else
			FacesUtils.addErrorMsgs(this.getCurrentProcess().getMessageList());

		// Redireciona a create
		log.debug("::Fim doExecute");
	}
	
	public void doChooseHistory(){
		log.debug("::Iniciando doChooseHistory");
		String sql = this.history.getObject().getDescription().split("sql=")[1];

		this.getCurrentProcess().setSql(sql);

		// Redireciona a create
		log.debug("::Fim doChooseHistory");
	}

	@Override
	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void doReload() throws BusinessException, Exception {
		// TODO Auto-generated method stub

	}

	/* IRunnableProcessView */
	@Override
	public String actionStart() {
		return FACES_VIEW_QUERY;
	}
	
	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return SqlQueryProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		this.getCurrentProcess().runWithEntity(entity);
		return FACES_VIEW_QUERY;
	}

}