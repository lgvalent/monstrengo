package br.com.orionsoft.monstrengo.core.service;

import java.util.Observable;

import br.com.orionsoft.monstrengo.core.exception.MessageList;

/**
 * Esta classe implementa o padr�o <b>Observer</b>.
 * Ela recebe mensagens e os objetos registrados nela s�o notificados
 * a cada mensagem recebida.
 * <p>Ela � �til para enviar mensagem de dentro de um servi�o para a interface.
 * @author Lucio
 *
 */
public class Status extends Observable
{
   
    public void sendMessage(String message)
    {
        super.notifyObservers();
    }

    public void sendMessage(MessageList errorList)
    {
        super.notifyObservers();
       
    }
   
}
