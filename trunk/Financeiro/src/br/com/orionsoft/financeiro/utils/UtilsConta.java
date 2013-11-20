package br.com.orionsoft.financeiro.utils;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * @author Lucio
 * @version 20100615
 *
 */
public class UtilsConta {
    
    /** 
     * Verifica se um determinador operador possui acesso a conta 
     * @return
     */
	public static Boolean verificarDireitoAcesso(Conta conta, ApplicationUser user) {
		if(conta == null) return true;
		
		for (ApplicationUser userConta: conta.getApplicationUsers()){
			if (userConta.getId()==user.getId()){
				return true;
			}
		}
		return false;
	}
    
}
