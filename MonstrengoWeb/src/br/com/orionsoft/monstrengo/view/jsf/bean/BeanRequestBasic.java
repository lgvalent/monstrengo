package br.com.orionsoft.monstrengo.view.jsf.bean;

import java.util.Map;

import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Esta classe possui as funcionalidades básicas de um Bean com o escopo REQUEST.
 * 
 * @author Lucio
 * @version 2005/11/08
 */
public abstract class BeanRequestBasic extends BasicBean
{
     /** Este método obtem os parâmetros da atual requisição.
      */  
    public Map getRequestParams()
    {
        return FacesUtils.getRequestParams();
    }
}
