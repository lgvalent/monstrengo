package br.com.orionsoft.monstrengo.core.service;

import java.util.Observable;

import br.com.orionsoft.monstrengo.core.exception.MessageList;

/**
 * Esta classe implementa o padrão <b>Observer</b>.
 * Ela recebe mensagens e os objetos registrados nela são notificados
 * a cada mensagem recebida.
 * <p>Ela é útil para enviar mensagem de dentro de um serviço para a interface.
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
