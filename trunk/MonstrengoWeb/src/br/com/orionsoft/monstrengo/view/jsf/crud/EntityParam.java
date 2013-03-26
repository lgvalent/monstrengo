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
 * Esta classe � respons�vel pela manipula��o dos par�metros aceitos
 * pelas requisi��es que definem uma entidade com ou sem pai.
 * 
 * @author Lucio 20060206
 */
public class EntityParam
{
    private Logger log = Logger.getLogger(this.getClass());
	public static final String URL_PARAM_ENTITY_TYPE = "entityType";
    public static final String URL_PARAM_ENTITY_ID = "entityId";

    /** Par�metros get/set */
    private String typeName = null;
    private long id = IDAO.ENTITY_UNSAVED;
    
    /** Objetos preparados */
    private ParentParam parentParam;

    /** Define o objeto Bean que � pai deste objeto 
     * para utilizar as rotinas de comunica��o com
     * a sess�o e requisi��o correntes. 
     */
    private IBasicBean ownerBean;
    
    /**
     * Construtor com a obriga��o de fornecer um owner para
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
     * Carrega os par�metros pertinente aos Bean da atual transa��o.   
     * Antes de recarregar os par�metros, o Bean sofre um reset() para 
     * que os par�metros atuais sejam limpos e dados processados sejam 
     * descarregados.
     */
    public void loadParams() throws Exception
    {
    	log.debug("Lendo os par�metros da requisi��o");
    	// Causa um reset para que os novos par�metros entrem em a��o
        this.doReset();

    	log.debug("Solicitando que o parentParam leia seus par�metros da requisi��o");
        this.parentParam.loadParams();

        // Verifica se o pai preparou para pegar os par�metros do Pai
        if(this.parentParam.isHasParent())
        {
        	log.debug("parentParam encontrou par�metros e os tratou. Sincronizando dados do parentParam com entityParam");
        	// Pega os par�metros do pai
            // Atualiza os par�metros atuais, de acordo com a propriedade do pai
            // para manter a consist�ncia do tipo e id da entidade atual.
            // Quando a propriedade do pai for uma cole��o de objetos,
            // n�o ser� poss�vel pegar o Id, pois uma cole��o n�o possui 
        	// um �nico Id. Por�m, o tipo da cole��o � poss�vel de ser pego.
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
        	log.debug("parentParam n�o encontrou par�metros. Lendo os par�metros sobre a entidade");
        	
        	// Pega os par�metros da requisi��o
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
     * Limpa todos os par�metros anteriormente carregados,
     * voltando seu valor padr�o.
     * Os dados processados internos tamb�m s�o marcados para
     * Se ocorrer alguma mudan�a nos par�metros,
     * o controlador da View dever� se resetar.
     * Para isto, os objetos preparados dever�o
     * ser destru�dos 
     */
    public void doReset()
    {
    	log.debug("Resetando os par�metros atuais");
        // Limpa os par�metros
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
