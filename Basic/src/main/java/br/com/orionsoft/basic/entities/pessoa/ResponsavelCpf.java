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

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/** 
 * Identifica o CPF de um resposável pela pessoa fí­sica cadastrada no sistema. 
 * Deve ser identiificado um nome e um grau de parentesco. Na classe pessoa 
 * existirá um campo boolean calculado que irá verificar se o relacionamento 
 * com esta classe é null; caso seja, não marca o campo usaCpfResponsavel, 
 * caso contrário marca.
 * 
 * @author andre
 * 
 * @hibernate.class table="basic_responsavel_cpf"
 */
@Entity
@Table(name="basic_responsavel_cpf")
public class ResponsavelCpf {

	public static final String NOME = "nome";
	public static final String GRAU_PARENTESCO = "grauParentesco";

	private long id = IDAO.ENTITY_UNSAVED;
	private String nome;
	private GrauParentesco grauParentesco;

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
	
	/**
	 * @hibernate.many-to-one foreign-key="grauParentesco"
	 */
    @ManyToOne
    @JoinColumn(name="grauParentesco")
    @ForeignKey(name="grauParentesco")
	public GrauParentesco getGrauParentesco() {return grauParentesco;}
	public void setGrauParentesco(GrauParentesco grauParentesco) {this.grauParentesco = grauParentesco;}
	
	public String toString(){
		String result = "";
		if (this.nome != null)
			result += this.nome;
		if (this.grauParentesco != null)
			result += " (" + this.grauParentesco.nome + ")";
		
		return result; 
	}

}
