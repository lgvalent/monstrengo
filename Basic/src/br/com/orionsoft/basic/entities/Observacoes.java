/*
 * Created on 02/01/2006
 *
 */
package br.com.orionsoft.basic.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author antonio
 * 
 * @hibernate.class table="basic_observacoes"
 */
@Entity
@Table(name="basic_observacoes")
public class Observacoes {
    public static final String OBSERVACOES = "observacoes";
    public static final String EXIBIR = "exibir";
	
	private long id = -1;
    private String observacoes = "";
    private boolean exibir = false;

    /**
     * @hibernate.property 
     * @return
     */
    @Column
    public boolean isExibir() {
        return exibir;
    }

    public void setExibir(boolean exibir) {
        this.exibir = exibir;
    }

    /**
     * @hibernate.property type="text" 
     * @return
     */
    @Column(length=500, columnDefinition="text")
    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     * @return
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String toString(){
    	String result = observacoes;
    	
    	if(StringUtils.isEmpty(observacoes))
    			result = "(vazia)";
    	
    	if(exibir)
    		result = "(Exibir)" + result;
    	
    	return result; 
    	
    }

}
