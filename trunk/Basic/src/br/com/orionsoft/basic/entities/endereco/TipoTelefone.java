package br.com.orionsoft.basic.entities.endereco;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created on 16/02/2005
 * @author Marcia
 * @hibernate.class table="basic_tipo_telefone"
 */
@Entity
@Table(name="basic_tipo_telefone")
public class TipoTelefone
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
    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    
    /**
     * @hibernate.property length="20"
     */
    @Column(length=20)
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
        return this.nome;
    }
}
