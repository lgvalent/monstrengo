package br.com.orionsoft.monstrengo.core.exception;

import java.util.ArrayList;
import java.util.List;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.GeneralException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class MessageList extends ArrayList<BusinessMessage>
{
	private static final long serialVersionUID = 1L;

	public MessageList(){
		super();
	}
	
	public MessageList(int size){
		super(size);
	}
	
	public void add(List<BusinessMessage> errors)
    {
       this.addAll(errors); 
    }
    
    public void add(Class messageClass, String errorKey, Object... args)
    {
        this.add(new BusinessMessage(messageClass ,errorKey, args));
    }

    public void add(int type, Class messageClass, String errorKey, Object... args)
    {
        this.add(new BusinessMessage(type, messageClass ,errorKey, args));
    }
    
    /**
     * Este método analisa a lista de erro para verificar se
     * existe algum erro do tipo TYPE_ERRO ou TYPE_CRITICAL
     * para então indicar que a transação não deve ser finalizada
     * com sucesso.
     *  
     * @return
     */
    public boolean isTransactionSuccess()
    {
        boolean result = true;
        
        for(BusinessMessage error: this)
            if ((error.getType() == BusinessMessage.TYPE_ERROR) || (error.getType() == BusinessMessage.TYPE_CRITICAL))
            {
                result = false;
                break;
            }
        
        return result;
    }
    
    /**
     * Este método é útil para se criar uma lista de mensagens
     * baseada em apena uma ÚNICA mensagem.<br>
     * Assim, evita-se de se criar objetos de lista, criar 
     * mensagens e ligar uma coisa com a outra<br>
     * <b>Exemplo:</b><br>
     * throws new BusinessException(MessageList.create(MyMessageClass.class, 'MY_MESSAGE_KEY', myValues));<br>
     * 
     * @see BusinessException
     * @param errorKey
     * @return
     */
    public static MessageList  create(Class exceptionClass, String errorKey, Object... args)
    {
        MessageList result = new MessageList(1);

        result.add(new BusinessMessage(exceptionClass ,errorKey, args));
        
        return result;
    }

    /**
     * Este método é útil para se criar uma lista de mensagens
     * baseada em apena uma ÚNICA mensagem.<br>
     * Pode-se especificar o tipo da mensagem.<br>
     * Assim, evita-se de se criar objetos de lista, criar 
     * mensagens e ligar uma coisa com a outra<br>
     * <b>Exemplo:</b><br>
     * throws new BusinessException(MessageList.create(MyMessageClass.class, 'MY_MESSAGE_KEY', myValues));<br>
     * 
     * @see BusinessException
     * @param errorKey
     * @return
     */
    public static MessageList  create(int msgType, Class exceptionClass, String errorKey, Object... args)
    {
        MessageList result = new MessageList(1);

        result.add(new BusinessMessage(msgType, exceptionClass ,errorKey, args));
        
        return result;
    }

    /**
     * Este método permite criar uma lista de mensagem de erro
     * utilizando uma exceção. É útil para converte Exception do java
     * em exceções de negócio.
     *  
     * @param e Exceção que foi disparada pelo sistema e que será convertida 
     * @return retorna uma lista com uma mensagem de erro
     */
    public static MessageList createSingleInternalError(Exception e)
    {
        MessageList result = new MessageList(1);
        
        // Prepara a pilha de execeção para ser mostrada
//        OutputStream stackStr= new OutputStream();
//        PrintStream stack=null;
/*        try
        {
*/            //Imprime no dispositivo de erro a ocorrência deste erro. 
            e.printStackTrace();
/*            stack = new PrintStream(stackStr);
            e.printStackTrace(stack);
        } catch (FileNotFoundException e1)
        {
            // Caso ocorra algum erro, este erro será propagado como 
            // a pilha de execução.
            stackStr = e1.getMessage();
        }
*/        
        Object[] args = new String[3];
        args[0]=e.getClass().getSimpleName();
        args[1]=e.getMessage();
//      args[2]=stackStr;
        args[2]="Verifique o log de erro para maiores informações.";

        // 1=Ocorreu um erro interno do tipo {0} com a mensagem {1}. Pilha={2}  
        result.add(new BusinessMessage(GeneralException.class, "GENERAL_ERROR", args));
        
        return result;
    }
    
    /**
     * Retorna a mensagem literal da primeira mensagem da lista, ou 
     * uma String vazia se não tiver mensagem.<br>
     * Útil quando se espera somente uma mensagem na lista. Ou se deseja
     * despresar as demais
     * @return
     */
    public String toString(){
    	if(this.isEmpty())
    		return "";
    	
    	return this.get(0).getMessage();
    }
    
}
