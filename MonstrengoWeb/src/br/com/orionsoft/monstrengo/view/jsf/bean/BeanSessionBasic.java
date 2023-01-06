package br.com.orionsoft.monstrengo.view.jsf.bean;

import java.util.Map;

import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Esta classe possui as funcionalidades básicas de um Bean com o escopo SESSION.
 * 
 * @author Lucio
 * @version 2005/11/08
 */
public abstract class BeanSessionBasic extends BasicBean
{
    
	
	/** Este método obtem os parâmetros da atual requisição utilizando as funções Utils.  
     */  
    public Map getRequestParams()
    {
        return FacesUtils.getRequestParams();
    }
    
    public static final String URL_PARAM_LINK_REQUEST = "link";

    /**
     * Este método identifica se na atual requisição foi recebido um
     * parametro link=true que identifica que a requisição foi iniciada
     * por um link em uma pagina e não por um método Action.
     * Isto porque, quando a requisição é feita por URL, os parâmetros da
     * requisição são passados pela URL e os beans de sessão nao ficam 
     * sabendo para atualizar seus parametros internos.
     * Para detectar esta situação, sempre é verificado na requisição atual
     * se tem um parâmetro "link=true" presente.
     */
    public boolean checkLinkRequest() {
    	if(log.isDebugEnabled())
    		log.debug("Parâmetros da requisição:" + this.getRequestParams().toString());
        return FacesUtils.isNotNull(this.getRequestParams().get(URL_PARAM_LINK_REQUEST)); 
    }
}
