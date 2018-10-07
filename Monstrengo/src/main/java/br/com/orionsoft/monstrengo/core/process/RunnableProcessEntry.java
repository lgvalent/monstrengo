package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.util.AnnotationUtils;

/**
 * Classe que descreve uma entrada para facilitar a construção de lista
 * de processos que podem ser disparados por uma visão.<br>
 * Útil para unir em uma estrutura os metadados do processo, pela entidade ApplicationProcess,
 * e os dados de execução, como por exemplo: disabled, messageList.
 * 
 * @author Lucio 20070925 
 */
public class RunnableProcessEntry{
	private ProcessMetadata info;
	private Class<? extends IProcess> processClass;
	private boolean disabled = false;
	private BusinessMessage message;
	
	public RunnableProcessEntry(Class<? extends IProcess> processClass) {
		this.processClass = processClass;
		this.info = AnnotationUtils.findAnnotation(ProcessMetadata.class, processClass);
		
		if(info == null)
			throw new RuntimeException("Nenhuma anotação de metadados foi encontrada no processo " + processClass.getName() + ". Anote o processo com @ProcessMetadata");
	}
	
	public Class<? extends IProcess> getProcessClass() {return processClass;}

	/** Obtem uma referência aos metadados anotatods do processo */
	public ProcessMetadata getInfo() {
		return info;
	}

	/**
	 * Define uma lista de mensagens que podem justificar porque o processo está desabilitado
	 * @return
	 */
	public BusinessMessage getMessage(){return message;}
	public void setMessage(BusinessMessage message){this.message = message;}
	
	/**
	 * Indica se o processo está desabilitado ou não
	 * @return
	 */
	public boolean isDisabled(){return disabled;}
	public void setDisabled(boolean disabled){this.disabled = disabled;}
}