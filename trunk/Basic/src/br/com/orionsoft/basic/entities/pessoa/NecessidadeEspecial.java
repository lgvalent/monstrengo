package br.com.orionsoft.basic.entities.pessoa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Identifica as necessidades especiais que uma pessoa possa ter.
 * 
 * @author andre
 * 
 * @hibernate.class table="basic_necessidade_especial"
 */
@Entity
@Table(name="basic_necessidade_especial")
public class NecessidadeEspecial {

	public static final String DESCRICAO = "descricao";
	
	private long id = IDAO.ENTITY_UNSAVED;
	private String descricao;

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
	public String getDescricao() {return descricao;}
	public void setDescricao(String descricao) {this.descricao = descricao;}
	
	public String toString(){
		return this.descricao != null ? this.descricao : "";
	}

}
