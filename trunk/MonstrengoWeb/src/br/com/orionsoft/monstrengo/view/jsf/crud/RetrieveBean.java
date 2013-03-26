package br.com.orionsoft.monstrengo.view.jsf.crud;

import java.util.LinkedList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.processes.RetrieveProcess;
import br.com.orionsoft.monstrengo.view.MenuBean;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Está classe é o controlador da visão RetrieveBean.
 * Nela estão os métodos para acionamento da view e fluxo de tela.  
 * 
 * @author Lucio 20051117
 * @version 20060207
 * 
 * @jsf.bean name="retrieveBean" scope="session"
 * @jsf.navigation from="*" result="retrieve" to="/pages/basic/retrieve.jsp" 
*/
@ManagedBean
@SessionScoped
public class RetrieveBean extends CrudBasicBean
{
    /** Define a view JSF que é ativada para a visão RETRIEVE */
	public static final String FACES_VIEW_RETRIEVE = "/pages/basic/retrieve?faces-redirect=true";
    
    public static final String URL_PARAM_SELECT_ONE_DEST = "selectOneDest";
    public static final String URL_PARAM_SELECT_PROPERTY = "selectProperty";

    public static final String URL_PARAM_RUNNABLE_VIEW_NAME = "runnableViewName";
    
	private MenuBean menuBean;
	
	private List<IEntity<?>> lastViews = new LinkedList<IEntity<?>>();

    /**
     * Action que prepara a visualização
     * e controla o fluxo de tela. 
     * @return
     */
    public String actionView() throws Exception
    {
        log.debug("::Iniciando actionView");
        
        try{
        	// Prepara os parâmetros fornecidos
            this.loadEntityParams();

            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
        }catch(ProcessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Visualização REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a create
        log.debug("::Fim actionView");
        return RetrieveBean.FACES_VIEW_RETRIEVE;
    }
	
    /**
     * Action que prepara a visualização baseada
	 * nos parâmetros passados.
	 * Este método é usado pelos outros beans quando desejam 
	 * exibir uma entidade no browser
     * e controla o fluxo de tela. 
     * @return
     * @throws Exception 
     */
	public String actionView(String typeName, long id) throws Exception
	{
        log.debug("::Iniciando actionView");
        
        try{
    		/* Passa os parâmetros para o controlador de entidade da visao */
    		this.getEntityParam().getParentParam().setTypeName(null);
    		this.getEntityParam().setTypeName(typeName);
    		this.getEntityParam().setId(id);

            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
        }catch(ProcessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Visualização REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a create
        log.debug("::Fim actionView");
        return RetrieveBean.FACES_VIEW_RETRIEVE;
	}

	
	/**
     * Este método prepara a entidade correntemente visualizada, 
     * baseando-se na chave passada.<br>
     * O mapa de processos ativos é consultado e não for encontrado o processo,
     * um novo processo é criado.  
	 * @throws BusinessException 
     */
	public void prepareCurrentEntity(String currentEntityKey) throws BusinessException, Exception
	{
        if(processes.containsKey(currentEntityKey)){
        	log.debug("Utilizando o processo já ativo");
        	currentEntity = ((RetrieveProcess)processes.get(currentEntityKey)).retrieveEntity();
    	}else{
    		
    		log.debug("Iniciando um novo processo Retrieve");
    		RetrieveProcess retrieveProcess = (RetrieveProcess) this.getApplicationBean().getProcessManager().createProcessByName(RetrieveProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
    		try{
    			/* Preenche os parâmetros */
    			retrieveProcess.setEntityType(Class.forName(this.getEntityParam().getTypeName()));
    			retrieveProcess.setEntityId(this.getEntityParam().getId());
    			
    			/* Obtem a entidade Delete , a tentativa causará um throw
    			 * e o processo responderá com a mensage de DELETE_DENIED
    			 */
    			currentEntity = retrieveProcess.retrieveEntity();
    			
    			/* Lucio 20090408 - Atualiza o tipo da entidade de acordo com o objeto obtido */
    			this.getEntityParam().setTypeName(currentEntity.getInfo().getType().getName());
    			
    			/* Lucio 20110712 - Atualiza a lista das últimas entidades visualizadas 
    			 * TODO OTIMIZAR Este Bean não armazena o processo e cria o mesmo três vezes
    			 * para não fazer buffer de entidade alterada no banco :) */
    			this.lastViews.remove(currentEntity);
    			this.lastViews.add(0, currentEntity);
    			/* Mantém somente os últimos 20 registros*/
    			if(this.lastViews.size()>=20) this.lastViews.remove(19);
    			
    			log.debug("Novo processo criado com sucesso");
    		}catch(BusinessException e){
    			log.debug("Um erro ocorreu:" + e.getMessage());
    			/* Passa o erro pra frente */
    			throw e;
    		}finally{
    			/* O processo nunca é re-aproveitado para sempre pegar as atualizações do BD.
    			 * O RetrieveBean não possui ações */
   				log.debug("Novo processo sendo finalizado");
   				retrieveProcess.finish();
    		}
    	}

        /* Lucio 20090408 - Esta linha eh utilizada aqui, pois aps o processo obter a entidade,
         * haver uma atualizano do nome do tipo. Isto porque, o tipo pode ser
         * de uma classe abstrata, no entanto o objeto  de uma classe concreta.
         * Logo, os direitors e tudo mais precisa ser preprarado de acorco com a classe
         * concreta e nao com a abstrata.
         * Exemplo: retrieve chamado para Contrato e o objeto  ContratoSindicato, Logo, os rigths devem
         * ser preaparados para ContratoSindicato e no para Contrato . */
        super.prepareCurrentEntity(currentEntityKey);
    	
	}
	
    
	/**
     * Este método é responsável por compor a chave de criação da entidade usando
     * o tipo da entidade e o id da entidade. 
     * @return Retorna uma chave com entityType+entityId.
     */
	public String prepareCurrentEntityKey()
	{
    	return this.getEntityParam().getTypeName() + this.getEntityParam().getId();
	}
    
	/**
	 * Verifica se a atual visão possui uma propriedade de SELECT_ONE_ACTIVE 
	 */
	public boolean isSelectOneActive(){return currentParams!=null && currentParams.get(URL_PARAM_SELECT_ONE_DEST)!=null;}
	
	/**
	 * Este método analisa o propriedade que está definida para ser selecionada;
	 * Analisa a atual entidade da coleção;
	 * Pega o valor da propriedade na atual entidade.
	 * @return Retorna o valor da propriedade da entidade
	 * @throws BusinessException
	 * @throws Exception
	 */
	public String getSelectPropertyValue() throws BusinessException, Exception{
		/* Obtem o valor da propriedade que será retornado pela tela de pesquisa */
		if(currentEntity == null)
			throw new Exception("A entidade corrente não está preparada. Execute pelo menos uma vez o método getNextCurrentEntity()");
		
		/* Verifica se há um nome válido */
		String propName = this.currentParams.get(URL_PARAM_SELECT_PROPERTY);
		if(StringUtils.isEmpty(propName))
			return "";
		
		IProperty prop = currentEntity.getProperty(propName);
		
		/* Verifica se a propriedade possui uma máscara para retorna-la sem a máscara */
		if(prop.getInfo().isHasEditMask())
			return prop.getValue().getAsObject().toString();
		else
			return prop.getValue().getAsString();
	}	



	public MenuBean getMenuBean() {return menuBean;}
	public void setMenuBean(MenuBean menuBean) {this.menuBean = menuBean;}

	public List<IEntity<?>> getLastViews() {
		return lastViews;
	}

	public void setLastViews(List<IEntity<?>> lastViews) {
		this.lastViews = lastViews;
	}

	/*
	 * FIM
	 */
	
}