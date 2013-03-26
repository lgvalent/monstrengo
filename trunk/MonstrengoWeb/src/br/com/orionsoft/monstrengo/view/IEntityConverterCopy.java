package br.com.orionsoft.monstrengo.view;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.bean.ApplicationBean;

/**
 * Esta classe define o conversor genérico de uma IEntity. Útil para usar com o componentes Primes
 * que trabalham diretamente com valores IEntity e não mais os primitivos.
 * o parâmetro value="" é necessário, pois senão ele o Prime usa o o conversor para TUDO!@! 
 * @author lucio
 * @version 01012012
 */
@FacesConverter(value="IEntityConverter")
public class IEntityConverterCopy implements Converter{
	  
	/**
	 * O valor submetido para o conversor deve ser o nome da classe da entidade e o seu id.
	 * Exemplo: 'br.com.MyClass:1'
	 * Use uma expressão: #{IEntity.info.type.name}:#{IEntity.id}    
	 */
	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
	        if (submittedValue.trim().equals("")) {  
	            return null;  
	        } else {  
	            try {
	            	ApplicationBean applicationBean = facesContext.getApplication().evaluateExpressionGet(facesContext, "#{applicationBean}", ApplicationBean.class);
	                
	            	String[] values = submittedValue.split(":");
	            	String className = values[0];
	            	long entityId = Long.parseLong(values[1]);
	            	
	            	/* Verifica se a entidade a ser convertida existe. Senão, cria uma vazia */
	            	if(entityId != IDAO.ENTITY_UNSAVED){
	            		return UtilsCrud.retrieve(applicationBean.getProcessManager().getServiceManager(), Class.forName(className), entityId, null);
	            	}else{
		            	return UtilsCrud.create(applicationBean.getProcessManager().getServiceManager(), Class.forName(className), null);
	            	}
	            	
	            } catch(Exception exception) {  
	                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de conversão. Use como valor do componente uma expressão: #{IEntity.info.type.name}:#{IEntity.id}", "O valor submetido não pode ser convertido para IEntity. Utilize este Converter somente para esta interface." + exception.getMessage()));  
	            }  
	        }  
	    }  
	  
	    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {  
	        if (value == null || value.equals("")) {  
	            return "(Vazio)";  
	        } else {  
	            return value.toString();  
	        }  
	    }  
}
