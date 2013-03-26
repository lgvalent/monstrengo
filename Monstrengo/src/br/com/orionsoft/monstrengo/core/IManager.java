package br.com.orionsoft.monstrengo.core;

import br.com.orionsoft.monstrengo.core.IApplication;

/**
 * Esta interface define os m�todos que um gerenciador padr�o da aplica��o
 * precisa ter para se integrar � arquitetura.
 * Implementando esta interface, o gerenciado poder� obter metadados da aplica��o geral
 * e futuramente se integrar a outros Gerenciados
 * 
 * @author lucio
 * @since 20111121
 */
public interface IManager {
	
	IApplication getApplication();
	void setApplication(IApplication application);

}
