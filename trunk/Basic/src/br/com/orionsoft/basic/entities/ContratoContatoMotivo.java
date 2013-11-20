package br.com.orionsoft.basic.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * @author Lucio 20060716 
 * @hibernate.class table="basic_contrato_contato_motivo"
 */
@Entity
@Table(name="basic_contrato_contato_motivo")
public class ContratoContatoMotivo {
    public static final String DESCRICAO = "descricao";
    
    protected long id = -1;
    protected String descricao;

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    /**
     * @hibernate.property length="50"
     */
    @Column(length=50)
    public String getDescricao() {return descricao;}
    public void setDescricao(String nome) {this.descricao = nome;}
    
	public String toString() {
        return this.descricao;
    }
}
