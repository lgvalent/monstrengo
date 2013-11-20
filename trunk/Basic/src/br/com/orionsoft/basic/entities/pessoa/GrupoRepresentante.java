package br.com.orionsoft.basic.entities.pessoa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * @hibernate.class table="basic_grupo_representante"
 */
@Entity
@Table(name="basic_grupo_representante")
public class GrupoRepresentante
{
	
	public static final String NOME = "nome";
	public static final String REPRESENTANTES="representantes";
	
	private long id = -1;
	private String nome;
	private Juridica juridica;
	private Set <Representante>representantes = new HashSet<Representante>();

	/*Getters e Setters*/

	/**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId(){return id;}
	public void setId(long id){this.id = id;}
	
    /**
     * @hibernate.property length="50"
     */
	@Column(length=50)
	public String getNome(){return nome;}
	public void setNome(String nome){this.nome = nome;}
	
    /**
     *  @hibernate.many-to-one foreign-key="juridica"
     */
	@ManyToOne
	@JoinColumn(name="juridica")
	@ForeignKey(name="juridica")
	public Juridica getJuridica() {return juridica;}
	public void setJuridica(Juridica juridica) {this.juridica = juridica;}
	
    /**
     * @hibernate.set  lazy="false" 
     * @hibernate.collection-key-column index="grupoRepresentante" name="grupoRepresentante"  
     * @hibernate.collection-key foreign-key="grupoRepresentante"  
     * @hibernate.collection-one-to-many class="br.com.orionsoft.basic.entities.pessoa.Representante" 
     */
	@OneToMany @LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name="grupoRepresentante") 
	@JoinColumn(name="grupoRepresentante")
	public Set <Representante>getRepresentantes(){return representantes;}
	public void setRepresentantes(Set<Representante> representantes){this.representantes = representantes;}
	
	public String toString(){
	    String result = "";
	    result += this.nome; 
	    return result;
	}
}
