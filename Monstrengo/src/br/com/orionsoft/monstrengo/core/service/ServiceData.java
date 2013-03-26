package br.com.orionsoft.monstrengo.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.Session;

import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.Status;
import br.com.orionsoft.monstrengo.core.service.ValueObject;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Esta classe padroniza a execu��o de um servi�o. S�o fornecidos
 * argumentos de entrada, dados de entrada, dados de sa�da e
 * lista de erros ou mensagens. 
 * 
 * @author Lucio
 *
 */
public class ServiceData
{
    //  Nome do servi�o que ser� invocado
    private String serviceName;
    //  Lista de argumentos
    private ValueObject argumentList = new ValueObject();
    //  Cole��o de objetos de entrada que poder�o ser usados na execu��o do servi�o
    private List<Object> inputData = new ArrayList<Object>(1);
    //  Cole��o de objetos de sa�da resultantes do processamento do servi�o
    private List<Object> outputData = new ArrayList<Object>(1);
    //  Lista de erros ou mensagens
    private MessageList messageList = new MessageList();
    // Permite o servi�o enviar mensagens direto para a interface
    private Status status = new Status();
    // Indica quando o j� existe uma transa��o ativa 
    private boolean transactionStarted = false;
    
    // Indica quando j� existe uma sess�o ativa 
    private Session currentSession = null;

    private ServiceData serviceDataOwner=null;
    
    
    public Session getCurrentSession()
    {
        return currentSession;
    }

    public void setCurrentSession(Session currentSession)
    {
        this.currentSession = currentSession;
    }

    public ServiceData getServiceDataOwner()
    {
        return serviceDataOwner;
    }

    /**
     * Indica se j� existe uma transa��o iniciada por servi�os anteriormente
     * executados na pilha de execu��o. 
     * @return 
     */
    public boolean isTransactionStarted()
    {
        return transactionStarted;
    }
    
    /**
     * Define se uma transa��o est� iniciada para evitar que m�ltiplas transa��es
     * sejam iniciadas em uma mesma pilha de execu��o.  
     * @param hasActiveTransaction
     */
    public void setTransactionStarted(boolean transactionStarted)
    {
        this.transactionStarted = transactionStarted;
    }
    
    /**
     * Constr�i a estrutura de dados para um determinado servi�o.
     */
    public ServiceData(String serviceName, ServiceData serviceDataOwner)
    {
        this.serviceName = serviceName;
        
        this.serviceDataOwner = serviceDataOwner;
    }
    /**
     * Obtem o nome do servi�o.
     */
    public String getServiceName() {
        return serviceName;
    }
    /**
     * Obtem a lista de argumentos.
     */
    public ValueObject getArgumentList() {
        return argumentList;
    }
    
    /**
     * Adiciona os argumentos na lista de argumentos corrente.
     */
    public void addArguments(ValueObject value) {
        argumentList.getProperties().putAll(value.getProperties());
    }
    /**
     * Obtem a cole��o de objetos de sa�da.
     */
    public List<Object> getInputData() {
        return inputData;
    }
    /**
     * Adiciona um objeto na lista de entrada.
     */
    public void addInputData(Object object) {
        inputData.add(object);
    }
    /**
     * Obtem um objeto da lista de entrada.
     */
    public Object getInputData(int index) throws BusinessException 
    {
        if (index > (inputData.size() - 1)) {
            throw new BusinessException(MessageList.createSingleInternalError(new Exception("N�o foi poss�vel acessar o objeto de sa�da com �ndice "
                    + index + ".")));
        }
        return inputData.get(index);
    }
    /**
     * Obtem a cole��o de objetos de sa�da.
     */
    public List<Object> getOutputData() {
        return outputData;
    }

    /**
     * Obtem o primeiro resultado da lista de resultados.
     */
    @SuppressWarnings("unchecked")
	public <T> T getFirstOutput() {
		return (T) outputData.get(0);
    }

    /**
     * Adiciona um objeto na lista de sa�da.
     */
    public void addOutputData(ValueObject valueObject) {
        outputData.add(valueObject);
    }
    /**
     * Obtem um objeto da lista de saida.
     */
    @SuppressWarnings("unchecked")
	public <T> T getOutputData(int index)throws BusinessException {
        if (index > (outputData.size() - 1)) {
            throw new BusinessException(MessageList.createSingleInternalError(new Exception("N�o existe o �ndice "
                    + index + " na lista de dados de sa�da.")));
        }
        return (T) outputData.get(index);
    }
    /**
     * Obtem a lista de erros e mensagens.
     */
    public MessageList getMessageList() {
        return messageList;
    }
    
    /**  Permite que os servi�os escrevam mensagens dentro desta propriedade, e estas
     * mensagens sejam passadas imediatamente para a interface. Utilizado para indicar
     * o atual progresso do servi�o, quando o servi�o � demorado.
     */
    public Status getStatus()
    {
        return status;
    }
   
    /**
     * Este m�todo analisa os par�metros do servi�o e formata uma string que mostra os nomes
     * e os valores destes par�metros.
     * Muito �til quando o toString do Calendar, por exemplo, resulta em uma coisa de dif�cil leitura.
     * @param serviceData
     * @return
     * @author Lucio
     * @since 20061115
     */
    @Override
    public String toString() {
    	 String propertiesValues = "";

         for(Entry<String, Object> entry: argumentList.getProperties().entrySet()){
         	propertiesValues += entry.getKey() + "=";
         	if (entry.getValue() instanceof Calendar)
         		propertiesValues += CalendarUtils.formatDate((Calendar) entry.getValue()) + ",";
         	else
         		/*  */
         		if (entry.getValue() instanceof IEntity){
         			IEntity<?> entity = (IEntity<?>) entry.getValue();
         			propertiesValues += "(IEntity: " + entity.getInfo().getType().getSimpleName() + ")" + entry.getValue() + ", ";
         		}
         		else
         			if (entry.getValue()!= null)
         				propertiesValues += "(" + entry.getValue().getClass().getSimpleName() + ")" +entry.getValue() + ", ";
         			else
         				propertiesValues += "null, ";
         }

         return propertiesValues;
     }
}
