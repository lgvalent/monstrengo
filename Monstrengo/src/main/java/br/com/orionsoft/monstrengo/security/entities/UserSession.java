package br.com.orionsoft.monstrengo.security.entities;

import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Esta classe mantem as informa��es sobre um operador
 * autenticado. O processo de autentica��o cria uma instancia
 * desta classe quando um operador � v�lido e autenticado.
 * @author Lucio
 * @version 20060304
 */
public class UserSession
{
    // TODO UserSession o que tem aqui? ApplicationUser, SessionID, SessionStart, Linguagem, Tipo de Interface, ComputerName
    private IEntity<ApplicationUser> user;
    private String terminal;

    public UserSession(IEntity<ApplicationUser> user, String terminal)
    {
        this.user = user;
        this.terminal = terminal;
    }
    
    public IEntity<ApplicationUser> getUser(){return user;}
    public void setUser(IEntity<ApplicationUser> user){this.user = user;}

    public String getTerminal(){return terminal;}
    public void setTerminal(String terminal){this.terminal = terminal;}
    
    /**
     * @return Retorna, de uma forma mais facilitada, o login do usu�rio autenticado por esta classe de sess�o
     * @throws EntityException Lan�a qualquer erro que ocorrer ao tentar obter o valor da propriedade 
     * @since 20060304
     */
    public String getUserLogin() throws EntityException{
    	if(this.user==null)
        	return "";
    	else
    	return this.user.getPropertyValue(ApplicationUser.LOGIN).toString();
    }
    

}
