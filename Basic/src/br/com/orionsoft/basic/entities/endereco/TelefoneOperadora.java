/*
 * Created on 20110919
 * 
 */
package br.com.orionsoft.basic.entities.endereco;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * @author Lucio
 */
@Entity
@Table(name="basic_telefone_operadora")
public class TelefoneOperadora 
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String NOME = "nome";

    private long id = -1;
    private String nome;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    @Column(length=100)
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    @Override
    public String toString() {
    	return this.nome;
    }

}
