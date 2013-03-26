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
     * Este m�todo analisa a lista de erro para verificar se
     * existe algum erro do tipo TYPE_ERRO ou TYPE_CRITICAL
     * para ent�o indicar que a transa��o n�o deve ser finalizada
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
     * Este m�todo � �til para se criar uma lista de mensagens
     * baseada em apena uma �NICA mensagem.<br>
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
     * Este m�todo � �til para se criar uma lista de mensagens
     * baseada em apena uma �NICA mensagem.<br>
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
     * Este m�todo permite criar uma lista de mensagem de erro
     * utilizando uma exce��o. � �til para converte Exception do java
     * em exce��es de neg�cio.
     *  
     * @param e Exce��o que foi disparada pelo sistema e que ser� convertida 
     * @return retorna uma lista com uma mensagem de erro
     */
    public static MessageList createSingleInternalError(Exception e)
    {
        MessageList result = new MessageList(1);
        
        // Prepara a pilha de exece��o para ser mostrada
//        OutputStream stackStr= new OutputStream();
//        PrintStream stack=null;
/*        try
        {
*/            //Imprime no dispositivo de erro a ocorr�ncia deste erro. 
            e.printStackTrace();
/*            stack = new PrintStream(stackStr);
            e.printStackTrace(stack);
        } catch (FileNotFoundException e1)
        {
            // Caso ocorra algum erro, este erro ser� propagado como 
            // a pilha de execu��o.
            stackStr = e1.getMessage();
        }
*/        
        Object[] args = new String[3];
        args[0]=e.getClass().getSimpleName();
        args[1]=e.getMessage();
//      args[2]=stackStr;
        args[2]="Verifique o log de erro para maiores informa��es.";

        // 1=Ocorreu um erro interno do tipo {0} com a mensagem {1}. Pilha={2}  
        result.add(new BusinessMessage(GeneralException.class, "GENERAL_ERROR", args));
        
        return result;
    }
    
    /**
     * Retorna a mensagem literal da primeira mensagem da lista, ou 
     * uma String vazia se n�o tiver mensagem.<br>
     * �til quando se espera somente uma mensagem na lista. Ou se deseja
     * despresar as demais
     * @return
     */
    public String toString(){
    	if(this.isEmpty())
    		return "";
    	
    	return this.get(0).getMessage();
    }
    
}
