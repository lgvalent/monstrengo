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
 * @hibernate.class table="basic_municipio"
 */
@Entity
@Table(name="basic_municipio")
public class Municipio
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String NOME = "nome";
    public static final String UF = "uf";
    public static final String CODIGO_IBGE = "codigoIbge";

    private long id = -1;
    private String nome;
    private Uf uf;
    private String codigoIbge;

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

    @Enumerated(EnumType.STRING)
    @Column(length=Uf.COLUMN_DISCRIMINATOR_LENGTH)
    public Uf getUf(){return uf;}
    public void setUf(Uf uf){this.uf = uf;}

    @Column(length=7)
    public String getCodigoIbge() {return codigoIbge;}
	public void setCodigoIbge(String codigoIbge) {this.codigoIbge = codigoIbge;}

    public String toString()
    {
        String result = "";
        result += this.nome;

        if (this.uf != null)
            result += "-" + this.uf.toString();

        return result;

    }

}
