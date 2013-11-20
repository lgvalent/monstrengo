package br.com.orionsoft.basic.entities.pessoa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/** 
 * Grau de parentesco ligado a um ResponsavelCpf
 *
 * @author andre
 * 
 * @hibernate.class table="basic_grau_parentesco"
 */
@Entity
@Table(name="basic_grau_parentesco")
public class GrauParentesco {
	
	public static final String NOME = "nome";
	
	public long id = IDAO.ENTITY_UNSAVED;
	public String nome;

	/**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {return id;}
	public void setId(long id) {this.id = id;}
	
    /**
     * @hibernate.property length="255"
     */
	@Column(length=255)
	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}
	
	public String toString(){
		return this.nome != null ? this.nome : "";
	}

}
