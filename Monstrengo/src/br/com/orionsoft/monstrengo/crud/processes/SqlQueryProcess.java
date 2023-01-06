package br.com.orionsoft.monstrengo.crud.processes;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.hibernate.Session;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditProcessRegister;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;

/**
 * Este processo permite executar uma SQL nativa no banco de dados.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir a SQL: <i>setSql(String)</i>
 * 
 * @author Lucio 20121021
 * @version 
 */
@ProcessMetadata(label="Pesquisa SQL nativa", hint="Permite e a execução de instruções SQL nativas diretamente no banco de dados da aplicação" , description="Escreva a instrução SQL, tanto de pesquisa quando de manipulação de dados.")
public class SqlQueryProcess extends ProcessBasic implements IRunnableEntityProcess
{
    public static final String PROCESS_NAME = "SqlQueryProcess";
    
    private String sql;
    
    private List<Object[]> result = new ArrayList<Object[]>();
    private String[] labels= null;
    private boolean hasResult = false;
    private int rowsFetched = 0;
    private int columnsCount = 0;
    private IEntityList<AuditProcessRegister> historyList = null;

    @Override
    public void start() throws ProcessException {
    	super.start();
    	
    	try {
			historyList = UtilsCrud.list(this.getProcessManager().getServiceManager(), 
									AuditProcessRegister.class,
									AuditProcessRegister.APPLICATION_USER + "=" + this.getUserSession().getUser().getId() + " AND " +
									AuditProcessRegister.APPLICATION_PROCESS + "." + ApplicationProcess.NAME + "='" + PROCESS_NAME + "'" +
									" ORDER BY " + AuditProcessRegister.OCURRENCY_DATE + " DESC limit 0,30", 
									null);
		} catch (BusinessException e) {
			throw new ProcessException(e.getErrorList());
		}
    }
    
    @Override
    protected void beforeRun() {
    	super.beforeRun();
    	
    	this.labels = null;
    	this.result.clear();
    	this.hasResult = false;
    	this.columnsCount = 0;
    }
    
	@SuppressWarnings("unchecked")
	public boolean runQuery(){
		beforeRun();

		try
		{
			Session session = this.getProcessManager().getServiceManager().getEntityManager().getDaoManager().getSessionFactory().openSession();

			Statement statement = session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			/* Verifica se a SQL é de Query ou Update*/
			if(Pattern.compile("(?i)(?s)(?m)update |alter |create |delete |insert ").matcher(this.sql).find()){
				this.rowsFetched = statement.executeUpdate(this.sql);
			}else{
				ResultSet resultSet = statement.executeQuery(this.sql);
				
				ResultSetMetaData metaData = resultSet.getMetaData();
				
				labels = new String[metaData.getColumnCount()];
				for(int i = 0;i < labels.length; i++){
					labels[i] = metaData.getColumnName(i+1);
				}
				
				while(resultSet.next()){
					Object[] r = new Object[labels.length];

					for(int i = 0;i < r.length; i++){
						r[i] = resultSet.getObject(i+1);
					}
					
					result.add(r);
				}
				
				this.hasResult = true;
				this.rowsFetched = result.size();
				this.columnsCount = labels.length;
			}
			
			/* Registra a auditoria da instrução e já insere ela no histórico atual do operador */
			historyList.add(0, UtilsAuditorship.auditProcess(this, "rowsFetched=" + this.rowsFetched + ";sql=" + this.sql, null));
			
			return true;
		} catch (Exception e)
		{
			this.getMessageList().addAll(MessageList.createSingleInternalError(e));
			return false;
		}
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public boolean isHasResult() {
		return hasResult;
	}

	public int getRowsFetched() {
		return rowsFetched;
	}

	public List<Object[]> getResult() {
		return result;
	}

	public String[] getLabels() {
		return labels;
	}

	public int getColumnsCount() {
		return columnsCount;
	}

	public IEntityList<AuditProcessRegister> getHistoryList() {
		return historyList;
	}

	public String getProcessName() {
		return PROCESS_NAME;
	}

	/* IRunnableEntityProcess */
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();
		boolean result = false;
		return result;
	}

}