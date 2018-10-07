package br.com.orionsoft.basic.entities.pessoa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Essa classe corresponde ao javabean que representa a entidade do cargo/fun��o
 * de pessoas vinculadas como s�cios, funcion�rios e contato da pessoa jur�dica. <br>
 * 
 * @author lucio
 * @hibernate.class table="basic_cargo"
 */
@Entity
@Table(name="basic_cargo")
public class Cargo
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no c�digo e evitar erro de digita��o. */
    public static final String NOME = "nome";

    private long id = -1;
    private String nome;
    
    /**
     * @hibernate.id  generator-class="native" unsaved-value="-1"
     * @return id da inst�ncia de cargo
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}
    
    /**
     * @hibernate.property length="100"
     * @return nome do cargo/fun��o
     */
    @Column(length=100)
    public String getNome(){return nome;}
    public void setNome(String nome){this.nome = nome;}

    public String toString(){
    	return this.nome;
    }
}
