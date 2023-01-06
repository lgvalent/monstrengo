package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.core.process.IProcess;



/**
 * Esta interface define os métodos que um parâmetro de um processo.
 * 
 * @author lucio
 */
public interface IProcessParam<T>
{
	IProcess getProcess();
	
	Class<T> getType();
	
	boolean isRequired();
	boolean isNull();
	boolean isEmpty();
	
	T getValue();
	void setValue(T value);
	
	String toString();
	
}
