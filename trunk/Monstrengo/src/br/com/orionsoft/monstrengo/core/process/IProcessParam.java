package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.core.process.IProcess;



/**
 * Esta interface define os m�todos que um par�metro de um processo.
 * 
 * @author lucio
 */
public interface IProcessParam<T>
{
	IProcess getProcess();
	
	Class<T> getType();
	
	boolean isRequired();
	boolean isNull();
	
	T getValue();
	void setValue(T value);
	
	String toString();
	
}
