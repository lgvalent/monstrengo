package br.com.orionsoft.basic.entities.endereco;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created on 16/02/2005
 * @author Marcia
 * @hibernate.class table="basic_logradouro"
 */
@Entity
@Table(name="basic_logradouro")
public class Logradouro
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String NOME = "nome";
    public static final String TIPO_LOGRADOURO = "tipoLogradouro";

    private long id = -1;
    private String nome;
    private TipoLogradouro tipoLogradouro;
    
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
    
    /**
     * @hibernate.many-to-one foreign-key="tipoLogradouro"
     */
    @Enumerated(EnumType.STRING)
    @Column(length=TipoLogradouro.COLUMN_DISCRIMINATOR_LENGTH)
    public TipoLogradouro getTipoLogradouro(){return tipoLogradouro;}
    public void setTipoLogradouro(TipoLogradouro tipoLogradouro){this.tipoLogradouro = tipoLogradouro;}
    
    public String toString()
    {  
        String result = ""; 
        if (this.tipoLogradouro != null)
            result +=this.tipoLogradouro.toString(); 
        result +=" " + this.nome;
        return result;
    }
}
