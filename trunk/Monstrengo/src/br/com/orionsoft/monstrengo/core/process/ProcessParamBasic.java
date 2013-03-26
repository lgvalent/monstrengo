package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IProcessParam;

/**
 * Classe que manipula os parâmetros de um processo.
 * Útil para marcar como requerido e integrar automaticamente com a visão.
 * 
 * FUTURO: Colocar metadados como label, hint, minValu, max, ETC para as telas automatizarem isto
 * 
 * @author lucio
 *
 * @param <T>
 */
public class ProcessParamBasic<T> implements IProcessParam<T> {
	
	private IProcess process;
	private Class<?> type;
	private boolean required;
	
	private T value;

	public ProcessParamBasic(Class<?> type, boolean required, IProcess processOwner) {
		this.process = processOwner;
		this.type = type;
		this.required = required;
	}
	
	public ProcessParamBasic(Class<?> type, T defaultValue, boolean required, IProcess processOwner) {
		this.process = processOwner;
		this.type = type;
		this.required = required;
		this.setValue(defaultValue);
	}
	
	public IProcess getProcess() {
		return process;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		return (Class<T>) this.type;
	}
	
	public boolean isRequired() {
		return this.required;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public boolean isNull() {
		return (this.value == null);
	}
	public String toString(){
		return isNull()?"(Vazio)":this.value.toString();
	}
}
