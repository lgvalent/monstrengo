package br.com.orionsoft.monstrengo.view;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.crud.entity.IEntity;
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
@FacesConverter(value="SelectItemConverter")
public class SelectItemConverter implements Converter{
	  
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
	                
	            	String[] values = submittedValue.split(":");
	            	long value = Long.parseLong(values[0]);
	            	String label = values[1];
	            	
	            	
	            	return new SelectItem(value, label);
	            	
	            } catch(Exception exception) {  
	            	exception.printStackTrace();
	                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de conversão. Use como valor do componente uma expressão: #{IEntity.info.type.name}:#{IEntity.id}", "O valor submetido não pode ser convertido para IEntity. Utilize este Converter somente para esta interface. Valor recebido:" + submittedValue));  
	            }  
	        }  
	    }  
	  
	    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {  
	        if (value == null || value.equals("")) {  
	            return "(Vazio)";  
	        } else {
	        	SelectItem item = (SelectItem) value;
	        	
	            return String.format("%s:%s", item.getValue().toString(), item.getLabel());  
	        }  
	    }  
	    
	    public static void main(String[] args) {
			System.out.println(String.format("%s:%d", IEntity.class.getName(), 1221l));
		}
	    
}
