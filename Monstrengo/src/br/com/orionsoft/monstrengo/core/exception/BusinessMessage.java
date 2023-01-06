package br.com.orionsoft.monstrengo.core.exception;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * <p>Esta classe é uma estrutura que armazena as informações sobre um
 * erro ocorrido e que será posteriormente traduzido nas camadas superiores
 * da aplicação para ser exibida para o operador.
 * <p>São usadas duas estruturas básicas:
 * <li>Uma identifica a classe de erro para obtenção do arquivo de mensagens;
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
     * Converte o erro de negócio em uma mensagem amigável com informações.
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
     * Exibe a mensagem básica quando for chamado este método.
     * Útil para exibição nativa de List de BusinessMessage.
     * Assim, por exemplo, o resultado será {"msg1", "msg2"} 
     * 
     * @since 2006/01/02
     */
    public String toString(){
        return getMessage();
    }

    /**
     * Permite trocar o tipo de mensagem, pois uma mensagem pode ser um erro em um serviço,
     * mas ao passar para outro, a mensagem pode ser interpretada como somente uma informação
     * @version 20110127
     * @since Lucio 20110127
     */
    public void changeTypeToInfo(){
    	this.type = TYPE_INFO;
    }
    
    /**
     * Permite trocar o tipo de mensagem, pois uma mensagem pode ser um erro em um serviço,
     * mas ao passar para outro, a mensagem pode ser interpretada como somente uma informação
     * @version 20110127
     * @since Lucio 20110127
     */
    public void changeTypeToError(){
    	this.type = TYPE_ERROR;
    }
    
    
    
}
