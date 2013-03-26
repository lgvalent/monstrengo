package br.com.orionsoft.monstrengo.core;

import br.com.orionsoft.monstrengo.core.IApplication;

/**
 * Esta interface define os métodos que um gerenciador padrão da aplicação
 * precisa ter para se integrar à arquitetura.
 * Implementando esta interface, o gerenciado poderá obter metadados da aplicação geral
 * e futuramente se integrar a outros Gerenciados
 * 
 * @author lucio
 * @since 20111121
 */
public interface IManager {
	
	IApplication getApplication();
	void setApplication(IApplication application);

}
