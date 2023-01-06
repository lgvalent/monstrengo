package br.com.orionsoft.monstrengo.view.jsf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanRequestBasic;

/**
 * Esta classe fornece alguns recursos mais avançados para
 * exibição de imagens.
 * Como:
 * - Pegar somente a última mensagem e não a lista inteira
 * - Pegar a lista de mensagems como uma lista para ser exibida em componentes table
 * 
 * @author Lucio 20060216
 *
 * @jsf.bean name="messagesBean" scope="session"
 */
@ManagedBean(name="messagesBean")
@SessionScoped
public class MessagesBean extends BeanRequestBasic
{

	public String getFirstMessageSummary(){
		
		Iterator it = FacesContext.getCurrentInstance().getMessages();
		if(it.hasNext())
			return ((FacesMessage) it.next()).getSummary();
		else
			return "";
	}
	
	public boolean isHasMessages(){
		return FacesContext.getCurrentInstance().getMessages().hasNext();
	}
	
	public List<String> getAllMessagesSummary(){
		
		List<String> result = new ArrayList<String>(); 
		for(Iterator it = FacesContext.getCurrentInstance().getMessages();it.hasNext();)
			result.add(((FacesMessage) it.next()).getSummary());

		return result;
	}
	
	/**
	 * Este método é útil quando a primeira mensagem é exibida num painel e as demais em outra
	 * @return
	 */
	public List<FacesMessage> getFromSecondMessage(){
		
		List<FacesMessage> result = new ArrayList<FacesMessage>(); 
		try{
			Iterator it = FacesContext.getCurrentInstance().getMessages();
			/*Pula a primeira*/
			if(it.hasNext());
			it.next();
			
			for(Iterator it_=it;it_.hasNext();)
				result.add((FacesMessage) it_.next());
		}catch(NoSuchElementException e){
			/* Se não tiver items na lista esta exceção é disparada
			 * porem nada deve ser feitos, somente uma lista vazia será
			 * retornada */
		}
		
		return result;
	}
	
	public void loadEntityParams() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	public void doReset() throws BusinessException, Exception
	{
		// TODO Auto-generated method stub
		
	}

	public void doReload() throws BusinessException, Exception
	{
		// TODO Auto-generated method stub
		
	}
    
}
