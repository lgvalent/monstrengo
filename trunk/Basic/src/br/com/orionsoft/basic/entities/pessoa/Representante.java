package br.com.orionsoft.basic.entities.pessoa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;


/**
 * Essa classe representa a entidade do tipo Representante.
 * Este representante é a pessoa responsável pelo afirmação 
 * do contrato de uma pessoa (PJ/PF) com a empresa atual.
 * Com se fosse um vendedor: Quem vendeu este contrato? 
 * @hibernate.class table="basic_representante"
 */
@Entity
@Table(name="basic_representante")
public class Representante
{
	public static final String FISICA = "fisica";
	public static final String GRUPO_REPRESENTANTE = "grupoRepresentante";
	
	private long id = -1;
	private Fisica fisica;
	private GrupoRepresentante grupoRepresentante;
	
	
	/* Getters e Setters*/
    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId(){return id;}
	public void setId(long id){this.id = id;}
	
    /**
     *  @hibernate.many-to-one foreign-key="fisica"
     */
	@ManyToOne
	@JoinColumn(name=FISICA)
	@ForeignKey(name=FISICA)
	public Fisica getFisica(){return fisica;}
	public void setFisica(Fisica fisica){this.fisica = fisica;}
	
    /**
     *  @hibernate.many-to-one foreign-key="grupoRepresentante"
     */
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name=GRUPO_REPRESENTANTE)
	@ForeignKey(name=GRUPO_REPRESENTANTE)
	public GrupoRepresentante getGrupoRepresentante(){return grupoRepresentante;}
	public void setGrupoRepresentante(GrupoRepresentante grupoRepresentante){this.grupoRepresentante = grupoRepresentante;}
	
	 public String toString()
	    {
	        String result = "";
	        result += (this.fisica==null?"":this.fisica.getNome()); 
	        return result;
	    }

}
