package br.com.orionsoft.monstrengo.crud.entity.dvo;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.crud.entity.dvo.IDvo;
import br.com.orionsoft.monstrengo.crud.entity.dvo.IDvoManager;

/**
 * Esta classe oferece as funcionalidades b�sicas para a implementa��o
 * dos servi�os que ser�o registrados e executados pelo IDvoManager.
 *
 * @author Sergio
 */

public abstract class DvoBasic<T> implements IDvo<T> {
	   /***
       * Manipulador de Log para ser utilizado pelas implementa��es dos
       * servi�os.
       */
	    protected Logger log = LogManager.getLogger(getClass());

	    private IDvoManager dvoManager;

	    /***
	     *
	     * Retorna o gerenciador de DVO no qual este DVO est� registrado.
	     *
	     */
		public IDvoManager getDvoManager(){
	    	return dvoManager;
	    }
	    /***
	     *
	     * Seta o processo Dvo.
	     */
		public void setDvoManager(IDvoManager dvoManager){
	    	this.dvoManager = dvoManager;
	    }


}
