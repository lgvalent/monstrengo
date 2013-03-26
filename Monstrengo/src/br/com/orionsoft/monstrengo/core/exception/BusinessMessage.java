package br.com.orionsoft.monstrengo.core.exception;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * <p>Esta classe � uma estrutura que armazena as informa��es sobre um
 * erro ocorrido e que ser� posteriormente traduzido nas camadas superiores
 * da aplica��o para ser exibida para o operador.
 * <p>S�o usadas duas estruturas b�sicas:
 * <li>Uma identifica a classe de erro para obten��o do arquivo de mensagens;
 * <li>Outra identifica a chave da mensagem do erro.
 * 
 * @author Lucio
 * @version 2006/01/02 
 *
 */
public class BusinessMessage
{
    private Class messageClass;
    
    private String errorKey;
    
    //  Default is standard error.
    private int type = TYPE_ERROR;

    private Object[] substitutionValues;
    
    public final static int TYPE_INFO = 1;
    public final static int TYPE_ERROR = 2;
    public final static int TYPE_CRITICAL = 3;


    public BusinessMessage(Class exceptionClass, String errorKey, Object... substitutionValues)
    {
        this(TYPE_ERROR, exceptionClass, errorKey, substitutionValues);
    }
    
    public BusinessMessage(int type, Class messageClass, String errorKey, Object... substitutionValues)
    {
        this.messageClass = messageClass;
        this.errorKey = errorKey;
        this.substitutionValues = substitutionValues;
        this.type = type;
        
    }
    
    public String getErrorKey()
    {
        return errorKey;
    }
    
    public Object[] getSubstitutionValues()
    {
        return substitutionValues;
    }
    
    public int getType()
    {
        return type;
    }

    public Class getMessageClass()
    {
        return messageClass;
    }
    
    /**
     * Converte o erro de neg�cio em uma mensagem amig�vel com informa��es.
     * @link http://java.sun.com/j2se/1.4.2/docs/api/java/text/MessageFormat.html
     * @return
     */
    public String getMessage()
    {
        try{
            String message = ResourceBundle.getBundle(messageClass.getName()).getString(errorKey);
            return MessageFormat.format(message , substitutionValues);
        }catch(Exception e)
        {
            return "Erro BusinessMessage.getMessage(): " + messageClass.getName() + "=" + e.getMessage();
            
        }
    }
    
    /**
     * Exibe a mensagem b�sica quando for chamado este m�todo.
     * �til para exibi��o nativa de List de BusinessMessage.
     * Assim, por exemplo, o resultado ser� {"msg1", "msg2"} 
     * 
     * @since 2006/01/02
     */
    public String toString(){
        return getMessage();
    }

    /**
     * Permite trocar o tipo de mensagem, pois uma mensagem pode ser um erro em um servi�o,
     * mas ao passar para outro, a mensagem pode ser interpretada como somente uma informa��o
     * @version 20110127
     * @since Lucio 20110127
     */
    public void changeTypeToInfo(){
    	this.type = TYPE_INFO;
    }
    
    /**
     * Permite trocar o tipo de mensagem, pois uma mensagem pode ser um erro em um servi�o,
     * mas ao passar para outro, a mensagem pode ser interpretada como somente uma informa��o
     * @version 20110127
     * @since Lucio 20110127
     */
    public void changeTypeToError(){
    	this.type = TYPE_ERROR;
    }
    
    
    
}
