package br.com.orionsoft.monstrengo.core.process;

import java.util.List;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessParamBasic;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;

public class ProcessParamEntity<E> extends ProcessParamBasic<IEntity<E>> {

	private Class<E> entityType;
	private IEntityMetadata info;
	private String staticHqlWhereFilter;
	

	public ProcessParamEntity(Class<E> entityType, boolean required, IProcess processOwner) {
		super(IEntity.class, required, processOwner);
		this.entityType = entityType;
	}
	public IEntityMetadata getInfo() {
		if(info==null)
			try {
				info = this.getProcess().getProcessManager().getServiceManager().getEntityManager().getEntityMetadata(entityType);
			} catch (EntityException e) {
				throw new RuntimeException(e.getMessage());
			}

		return info;
	}

	@SuppressWarnings("unchecked")
	public List<IEntity<E>> getList(String filter) {
		// A lista é criada depois com o tamanho já otimizado
		try{
			List<?> result = this.getProcess().getProcessManager().getServiceManager().getEntityManager().queryEntities(this.entityType, filter, staticHqlWhereFilter, 10).getList();
			
			return (List<IEntity<E>>) result;
		}
		catch (BusinessException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public List<IEntity<E>> getList() {
		// A lista é criada depois com o tamanho já otimizado
		try{
			List<?> result = this.getProcess().getProcessManager().getServiceManager().getEntityManager().queryEntities(this.entityType, "", staticHqlWhereFilter, 50).getList();
			
			return (List<IEntity<E>>) result;
		}
		catch (BusinessException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	public String getStaticHqlWhereFilter() {return staticHqlWhereFilter;}
	public void setStaticHqlWhereFilter(String staticHqlFilter) {this.staticHqlWhereFilter = staticHqlFilter;}
	
	
}
