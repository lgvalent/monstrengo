package br.com.orionsoft.basic.entities.pessoa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

/**
 * JavaBean referente aos contatos de uma determinada empresa. O contato será
 * identificado por um nome e terá uma profissão que pode ser o cargo da pessoa
 * dentro da empresa. 
 * 
 * Created 2005/12/29
 * @author marcia
 * @hibernate.class table="basic_contato"
 */
@Entity
@Table(name="basic_contato")
public class Contato
{
    /** Nome do contato. Tipo String*/
    public static final String NOME_CONTATO = "nomeContato";
    /** Profissao o cargo do contato. Tipo Profissao*/
    public static final String CARGO = "cargo";
    
    private long id=-1;
    private String nomeContato;
    private Cargo cargo;
    
    /**
     * @hibernate.id  generator-class="native"  unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}
    
    /**
     * @hibernate.property length="50"
     * @return
     */
    @Column(length=50)
    public String getNomeContato()
    {
        return nomeContato;
    }
    public void setNomeContato(String nomeContato)
    {
        this.nomeContato = nomeContato;
    }
    
    /**
     * @hibernate.many-to-one foreign-key="cargo"
     */
    @ManyToOne
    @JoinColumn(name="cargo")
    @ForeignKey(name="cargo")
    public Cargo getCargo()
    {
        return cargo;
    }
    public void setCargo(Cargo profissao)
    {
        this.cargo = profissao;
    }
    
    public String toString(){
    	String result = nomeContato;
    	if(cargo!=null)
    		result += "("+cargo.getNome()+")";
    	return result;
    }
}
