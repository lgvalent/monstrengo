package br.com.orionsoft.monstrengo.view.jsf.bean;

import java.util.Map;

import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Esta classe possui as funcionalidades b�sicas de um Bean com o escopo SESSION.
 * 
 * @author Lucio
 * @version 2005/11/08
 */
public abstract class BeanSessionBasic extends BasicBean
{
    
	
	/** Este m�todo obtem os par�metros da atual requisi��o utilizando as fun��es Utils.  
     */  
    public Map getRequestParams()
    {
        return FacesUtils.getRequestParams();
    }
    
    public static final String URL_PARAM_LINK_REQUEST = "link";

    /**
     * Este m�todo identifica se na atual requisi��o foi recebido um
     * parametro link=true que identifica que a requisi��o foi iniciada
     * por um link em uma pagina e n�o por um m�todo Action.
     * Isto porque, quando a requisi��o � feita por URL, os par�metros da
     * requisi��o s�o passados pela URL e os beans de sess�o nao ficam 
     * sabendo para atualizar seus parametros internos.
     * Para detectar esta situa��o, sempre � verificado na requisi��o atual
     * se tem um par�metro "link=true" presente.
     */
    public boolean checkLinkRequest() {
    	if(log.isDebugEnabled())
    		log.debug("Par�metros da requisi��o:" + this.getRequestParams().toString());
        return FacesUtils.isNotNull(this.getRequestParams().get(URL_PARAM_LINK_REQUEST)); 
    }
}
