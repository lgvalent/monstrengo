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
 * Esta classe padroniza a execução de um serviço. São fornecidos
 * argumentos de entrada, dados de entrada, dados de saída e
 * lista de erros ou mensagens. 
 * 
 * @author Lucio
 *
 */
public class ServiceData
{
    //  Nome do serviço que será invocado
    private String serviceName;
    //  Lista de argumentos
    private ValueObject argumentList = new ValueObject();
    //  Coleção de objetos de entrada que poderão ser usados na execução do serviço
    private List<Object> inputData = new ArrayList<Object>(1);
    //  Coleção de objetos de saída resultantes do processamento do serviço
    private List<Object> outputData = new ArrayList<Object>(1);
    //  Lista de erros ou mensagens
    private MessageList messageList = new MessageList();
    // Permite o serviço enviar mensagens direto para a interface
    private Status status = new Status();
    // Indica quando o já existe uma transação ativa 
    private boolean transactionStarted = false;
    
    // Indica quando já existe uma sessão ativa 
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
     * Indica se já existe uma transação iniciada por serviços anteriormente
     * executados na pilha de execução. 
     * @return 
     */
    public boolean isTransactionStarted()
    {
        return transactionStarted;
    }
    
    /**
     * Define se uma transação está iniciada para evitar que múltiplas transações
     * sejam iniciadas em uma mesma pilha de execução.  
     * @param hasActiveTransaction
     */
    public void setTransactionStarted(boolean transactionStarted)
    {
        this.transactionStarted = transactionStarted;
    }
    
    /**
     * Constrói a estrutura de dados para um determinado serviço.
     */
    public ServiceData(String serviceName, ServiceData serviceDataOwner)
    {
        this.serviceName = serviceName;
        
        this.serviceDataOwner = serviceDataOwner;
    }
    /**
     * Obtem o nome do serviço.
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
     * Obtem a coleção de objetos de saída.
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
            throw new BusinessException(MessageList.createSingleInternalError(new Exception("Não foi possível acessar o objeto de saída com índice "
                    + index + ".")));
        }
        return inputData.get(index);
    }
    /**
     * Obtem a coleção de objetos de saída.
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
     * Adiciona um objeto na lista de saída.
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
            throw new BusinessException(MessageList.createSingleInternalError(new Exception("Não existe o índice "
                    + index + " na lista de dados de saída.")));
        }
        return (T) outputData.get(index);
    }
    /**
     * Obtem a lista de erros e mensagens.
     */
    public MessageList getMessageList() {
        return messageList;
    }
    
    /**  Permite que os serviços escrevam mensagens dentro desta propriedade, e estas
     * mensagens sejam passadas imediatamente para a interface. Utilizado para indicar
     * o atual progresso do serviço, quando o serviço é demorado.
     */
    public Status getStatus()
    {
        return status;
    }
   
    /**
     * Este método analisa os parâmetros do serviço e formata uma string que mostra os nomes
     * e os valores destes parâmetros.
     * Muito útil quando o toString do Calendar, por exemplo, resulta em uma coisa de difícil leitura.
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
