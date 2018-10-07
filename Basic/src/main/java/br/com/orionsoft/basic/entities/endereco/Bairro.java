/*
 * Created on 16/02/2005
 */
package br.com.orionsoft.basic.entities.endereco;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Marcia
 * @hibernate.class table="basic_bairro"
 */
@Entity
@Table(name="basic_bairro")
public class Bairro
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String NOME = "nome";
    
    private long id = -1;
    private String nome;
    
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
    public String getNome()
    {
        return nome;
    }
    public void setNome(String nome)
    {
        this.nome = nome;
    }
    
    public String toString()
    {
        String result = "";
        result += this.nome; 
        return result;
    }

}
