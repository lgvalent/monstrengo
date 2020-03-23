package br.com.orionsoft.monstrengo.view;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.bean.ApplicationBean;

/**
 * Esta classe define o conversor gen�rico de uma IEntity. �til para usar com o componentes Primes
 * que trabalham diretamente com valores IEntity e n�o mais os primitivos.
 * o par�metro value="" � necess�rio, pois sen�o ele o Prime usa o o conversor para TUDO!@! 
 * @author lucio
 * @version 01012012
 */
@FacesConverter(value="EntityConverter")
public class EntityConverter implements Converter{
	  
	/**
	 * O valor submetido para o conversor deve ser o nome da classe da entidade e o seu id.
	 * Exemplo: 'br.com.MyClass:1'
	 * Use uma express�o: #{IEntity.info.type.name}:#{IEntity.id}    
	 */
	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
	        if (submittedValue.trim().equals("")) {  
	            return null;  
	        } else {  
	            try {
	            	ApplicationBean applicationBean = (ApplicationBean) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{applicationBean}", ApplicationBean.class);
	            	
	            	String[] values = submittedValue.split(":");
	            	String className = values[0];
	            	long entityId = Long.parseLong(values[1]);
	            	
	            	/* Verifica se a entidade a ser convertida existe. Sen�o, cria uma vazia */
	            	if(entityId != IDAO.ENTITY_UNSAVED){
	            		return UtilsCrud.retrieve(applicationBean.getProcessManager().getServiceManager(), Class.forName(className), entityId, null);
	            	}else{
		            	/* Precisa retornar NULL pois se a entidade for do tipo abstrata n�o d� para dar um CREATE return UtilsCrud.create(applicationBean.getProcessManager().getServiceManager(), entityClass, null); */
		            	return null;
	            	}
	            	
	            } catch(Exception exception) {  
	            	exception.printStackTrace();
	                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de convers�o. Use como valor do componente uma express�o: #{IEntity.info.type.name}:#{IEntity.id}", "O valor submetido n�o pode ser convertido para IEntity. Utilize este Converter somente para esta interface. Valor recebido:" + submittedValue));  
	            }  
	        }  
	    }  
	  
	    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {  
	        if (value == null || value.equals("")) {  
	            return "";  
	        } else {
	        	IEntity<?> entity = (IEntity<?>) value;
	        	
	            return String.format("%s:%d", entity.getInfo().getType().getName(), entity.getId());  
	        }  
	    }  
	    
	    public static void main(String[] args) {
			System.out.println(String.format("%s:%d", IEntity.class.getName(), 1221l));
		}
	    
}
