/*
 * Created on 16/02/2005
 * 
 */
package br.com.orionsoft.basic.entities.endereco;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.basic.entities.pessoa.Pessoa;


/**
 * @author Marcia
 * @hibernate.class table="basic_telefone"
 */
@Entity
@Table(name="basic_telefone")
public class Telefone 
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String DDD = "ddd";
    public static final String NUMERO = "numero";
    public static final String RAMAL = "ramal";
    public static final String TIPO_TELEFONE = "tipoTelefone";
    public static final String PESSOA = "pessoa";
    public static final String TELEFONE_OPERADORA = "telefoneOperadora";

    private long id = -1;
    private String ddd;
    private String numero;
    private String ramal;
    private TipoTelefone tipoTelefone;
    private Pessoa pessoa;
    private TelefoneOperadora telefoneOperadora;

	/**
     * @hibernate.many-to-one column="pessoa_id" foreign-key="pessoa_id"
     * @return
     */
    @ManyToOne
    @JoinColumn(name="pessoa_id")
    @ForeignKey(name="pessoa_id")
    public Pessoa getPessoa()
    {
        return pessoa;
    }
    public void setPessoa(Pessoa pessoa)
    {
        this.pessoa = pessoa;
    }
    
    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    /**
     * @hibernate.property length="4"
     */
    @Column(length=4)
    public String getDdd() {
        return ddd;
    }
    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    /**
     * @hibernate.property length="8"
     */
    @Column(length=8)
    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * @hibernate.property length="5"
     */
    @Column(length=5)
    public String getRamal() {
        return ramal;
    }
    public void setRamal(String ramal) {
        this.ramal = ramal;
    }

    /**
     * @hibernate.many-to-one foreign-key="tipoTelefone"
     */
    @ManyToOne
    @JoinColumn(name="tipoTelefone")
    @ForeignKey(name="tipoTelefone")
    public TipoTelefone getTipoTelefone() {
        return tipoTelefone;
    }
    public void setTipoTelefone(TipoTelefone tipoTelefone) {
        this.tipoTelefone = tipoTelefone;
    }

    @ManyToOne
    @JoinColumn(name="telefoneOperadora")
    @ForeignKey(name="telefoneOperadora")
    public TelefoneOperadora getTelefoneOperadora() {return telefoneOperadora;}
	public void setTelefoneOperadora(TelefoneOperadora telefoneOperadora) {this.telefoneOperadora = telefoneOperadora;}

	public String toString() {
        String result = "";
        if (this.tipoTelefone != null)
            result += this.tipoTelefone.toString() + " ";

        if (this.ddd != null)
            result += "(" + this.ddd + ") ";

        if (this.numero != null)
            if (this.numero.length() > 4)
                result += this.numero.substring(0, this.numero.length() - 4)
                        + "-" + this.numero.substring(this.numero.length() - 4);
            else
                result += this.numero;

        if (this.ramal != null)
            result += " R:" + this.ramal;

        if (this.telefoneOperadora != null)
            result += " - " + this.telefoneOperadora.toString();

        return result;
    }
}
