package br.com.orionsoft.monstrengo.view.jsf.crud;

import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.view.jsf.bean.IBasicBean;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Esta classe é responsável pela manipulação dos parâmetros aceitos
 * pelas requisições que definem uma entidade com ou sem pai.
 * 
 * @author Lucio 20060206
 */
public class EntityParam
{
    private Logger log = Logger.getLogger(this.getClass());
	public static final String URL_PARAM_ENTITY_TYPE = "entityType";
    public static final String URL_PARAM_ENTITY_ID = "entityId";

    /** Parâmetros get/set */
    private String typeName = null;
    private long id = IDAO.ENTITY_UNSAVED;
    
    /** Objetos preparados */
    private ParentParam parentParam;

    /** Define o objeto Bean que é pai deste objeto 
     * para utilizar as rotinas de comunicação com
     * a sessão e requisição correntes. 
     */
    private IBasicBean ownerBean;
    
    /**
     * Construtor com a obrigação de fornecer um owner para
     * ser utilizado internamente pela classe.
     * @param ownerBean Define o bean dono desta classe
     */
    public EntityParam(IBasicBean ownerBean){
    	this.ownerBean = ownerBean;
    	this.parentParam = new ParentParam(ownerBean);
    }
    
    public IBasicBean getOwnerBean(){return ownerBean;}
	public void setOwnerBean(IBasicBean ownerBean){this.ownerBean = ownerBean;}


    /**
     * Carrega os parâmetros pertinente aos Bean da atual transação.   
     * Antes de recarregar os parâmetros, o Bean sofre um reset() para 
     * que os parâmetros atuais sejam limpos e dados processados sejam 
     * descarregados.
     */
    public void loadParams() throws Exception
    {
    	log.debug("Lendo os parâmetros da requisição");
    	// Causa um reset para que os novos parâmetros entrem em ação
        this.doReset();

    	log.debug("Solicitando que o parentParam leia seus parâmetros da requisição");
        this.parentParam.loadParams();

        // Verifica se o pai preparou para pegar os parâmetros do Pai
        if(this.parentParam.isHasParent())
        {
        	log.debug("parentParam encontrou parâmetros e os tratou. Sincronizando dados do parentParam com entityParam");
        	// Pega os parâmetros do pai
            // Atualiza os parâmetros atuais, de acordo com a propriedade do pai
            // para manter a consistência do tipo e id da entidade atual.
            // Quando a propriedade do pai for uma coleção de objetos,
            // não será possível pegar o Id, pois uma coleção não possui 
        	// um único Id. Porém, o tipo da coleção é possível de ser pego.
            try
            {
                IProperty prop = this.parentParam.getEntity().getProperty(this.parentParam.getProperty()); 
                if (prop.getInfo().isCollection())
                {
                    IEntityCollection entityCollection = prop.getValue().getAsEntityCollection(); 
                
                    this.typeName = entityCollection.getInfo().getType().getName();
                }
                else
                {
                    IEntity entity = prop.getValue().getAsEntity(); 
                    
                    this.typeName = entity.getInfo().getType().getName();
                    this.id       = entity.getId();
                }

            } catch (BusinessException e)
            {
                FacesUtils.addErrorMsgs(e.getErrorList());
                
                throw e;
            } 
        	
        }else{
        	log.debug("parentParam não encontrou parâmetros. Lendo os parâmetros sobre a entidade");
        	
        	// Pega os parâmetros da requisição
        	if (FacesUtils.isNotNull(ownerBean.getRequestParams().get(URL_PARAM_ENTITY_TYPE)))
        	{
        		this.typeName = ownerBean.getRequestParams().get(URL_PARAM_ENTITY_TYPE).toString();
        	}
        	if (FacesUtils.isNotNull(ownerBean.getRequestParams().get(URL_PARAM_ENTITY_ID)))
        	{
        		this.id = Long.parseLong(ownerBean.getRequestParams().get(URL_PARAM_ENTITY_ID).toString());
        	}
        }
         
    }

    /**
     * Limpa todos os parâmetros anteriormente carregados,
     * voltando seu valor padrão.
     * Os dados processados internos também são marcados para
     * Se ocorrer alguma mudança nos parâmetros,
     * o controlador da View deverá se resetar.
     * Para isto, os objetos preparados deverão
     * ser destruídos 
     */
    public void doReset()
    {
    	log.debug("Resetando os parâmetros atuais");
        // Limpa os parâmetros
        this.typeName = null;
        this.id = IDAO.ENTITY_UNSAVED;
        
    }

    public ParentParam getParentParam(){return parentParam;}
    public void setParentParam(ParentParam parentParam){this.parentParam = parentParam;}

    public long getId(){return id;}
    public void setId(long entityId){this.id = entityId;}

    public String getTypeName(){return typeName;}
    public void setTypeName(String entityType){this.typeName = entityType;}
        
}
