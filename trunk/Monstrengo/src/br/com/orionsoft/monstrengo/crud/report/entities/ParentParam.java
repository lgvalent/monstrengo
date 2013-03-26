package br.com.orionsoft.monstrengo.crud.report.entities;

import br.com.orionsoft.monstrengo.crud.report.entities.ParentCondictionBean;
import br.com.orionsoft.monstrengo.crud.report.entities.ReportParam;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

/**
 * Esta classe é responsável pela manipulação dos parâmetros aceitos
 * pelas requisições que definem uma entidade pai.
 * 
 * @author 20060315
 */
public class ParentParam extends ReportParam
{
    public ParentParam(UserReport userReport)
	{
		super(userReport);
	}

	/** Parâmetros get/set */
    private Class<?> type;
    private long id = -1;
    private String property;
    
    public long getId() {return id;}
	public void setId(long parentId) {this.id = parentId;}

	public String getProperty() {return property;}
	public void setProperty(String parentProperty) {this.property = parentProperty;}

	public Class<?> getType() {return type;}
	public void setType(Class<?> parentType) {this.type = parentType;}

	public void clear(){
        this.type = null;
        this.id = -1;
        this.property = "";
	}

	public void beanToParam (ParentCondictionBean parentCondictionBean) throws BusinessException{
		this.clear();
		
		try
		{
			if(parentCondictionBean != null){
				this.setProperty(parentCondictionBean.getProperty());
				this.setType(Class.forName(parentCondictionBean.getApplicationEntity().getClassName()));
			}
		} catch (ClassNotFoundException e)
		{
			throw new BusinessException(MessageList.createSingleInternalError(e));
		}
	}

	public ParentCondictionBean paramToBean() throws BusinessException{
		/* Cria a copia os dados interno do Param para a estrutura Bean
		 * correspondente. Lembrando de definir o UserReportBean ao 
		 * qual o bean vai pertencer */
		ParentCondictionBean result = null;
		if(this.isHasParent()){
			result = new ParentCondictionBean();
			result.setProperty(this.getProperty());
			result.setApplicationEntity((ApplicationEntity)UtilsSecurity.retrieveEntity(this.getUserReport().getEntityManager().getServiceManager(), this.getType(), null).getObject());
			result.setUserReport(this.getUserReport().getUserReportBean());
		}
		return result;
		
	}

	public boolean isHasParent()
    {
        return (type != null) && (id!=IDAO.ENTITY_UNSAVED) && (property!=null);
    }
	
}
