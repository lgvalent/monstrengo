package br.com.orionsoft.basic.entities.endereco;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.basic.entities.pessoa.Pessoa;

/**
 * Created on 15/02/2005
 * @author Marcia
 * @hibernate.class table="basic_endereco"
 */
@Entity
@Table(name="basic_endereco")
public class Endereco
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String NUMERO = "numero";
    public static final String COMPLEMENTO = "complemento";
    /** Cep deste endereco (campo do tipo br.com.orionsoft.basic.entities.endereco.Cep) */
    public static final String CAIXA_POSTAL = "caixaPostal";
    public static final String CATEGORIA = "enderecoCategoria";
    public static final String PESSOA = "pessoa";

    public static final String ENDERECO_PARA_DOCUMENTO = "enderecoParaDocumento";

    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String LOGRADOURO = "logradouro";
    public static final String BAIRRO = "bairro";
    public static final String CEP = "cep";
    public static final String MUNICIPIO = "municipio";

    private long id = -1;
    private Integer numero;
    private String complemento;
    private String caixaPostal;

    private Logradouro logradouro;
    private Bairro bairro;
    private String cep;
    private Municipio municipio;
	private EnderecoCategoria enderecoCategoria;
	private Pessoa pessoa;

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
    public String getComplemento()
    {
        /* Lucio 15/12/2006 Complemento é somado com outras partes do
         * endereço nos Jasper, se ele for NULL o analisador de expressão
         * do jasper retorna NULL e imprime isto nos relatórios */
    	if(complemento==null)
        	return "";

    	return complemento;
    }
    public void setComplemento(String complemento)
    {
        this.complemento = complemento;
    }

    /**
     * @hibernate.property
     */
    @Column
    public Integer getNumero()
    {
        return numero;
    }
    public void setNumero(Integer numero)
    {
        this.numero = numero;
    }

    /**
     * Armazena a informação sobre caixa postal
     * @hibernate.property length="20"
     */
    @Column(length=20)
    public String getCaixaPostal()
    {
        return caixaPostal;
    }
    public void setCaixaPostal(String caixaPostal)
    {
        this.caixaPostal = caixaPostal;
    }

    /**
     * @hibernate.many-to-one foreign-key="bairro"
     */
    @ManyToOne
    @JoinColumn(name="bairro")
    @ForeignKey(name="bairro")
    public Bairro getBairro()
    {
        return bairro;
    }

    public void setBairro(Bairro bairro)
    {
        this.bairro = bairro;
    }

    /**
     * @hibernate.property length="8"
     */
    @Column(length=8)
    public String getCep()
    {
        return cep;
    }

    public void setCep(String cep)
    {
        this.cep = cep;
    }

    /**
     * @hibernate.many-to-one foreign-key="municipio"
     */
    @ManyToOne
    @JoinColumn(name="municipio")
    @ForeignKey(name="municipio")
    public Municipio getMunicipio()
    {
        return municipio;
    }

    public void setMunicipio(Municipio municipio)
    {
        this.municipio = municipio;
    }

    /**
     * @hibernate.many-to-one foreign-key="logradouro"
     */
    @ManyToOne
    @JoinColumn(name="logradouro")
    @ForeignKey(name="logradouro")
    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    @ManyToOne
	@JoinColumn(name=CATEGORIA)
	@ForeignKey(name=CATEGORIA)
	public EnderecoCategoria getEnderecoCategoria() {return enderecoCategoria;}
	
	@ManyToOne
	@JoinColumn(name=PESSOA)
	@ForeignKey(name=PESSOA)
	public Pessoa getPessoa() {return pessoa;}
	public void setEnderecoCategoria(EnderecoCategoria enderecoCategoria) {this.enderecoCategoria = enderecoCategoria;}
	public void setPessoa(Pessoa pessoa) {this.pessoa = pessoa;}


    public String toString()
    {
        String result = "";

        if(this.cep!=null)
        	result += this.cep + ": ";

        if (this.logradouro != null)
        	result +=this.logradouro.toString();

        if(this.numero!=null)
        	result += ", " + this.numero;

        if(!StringUtils.isEmpty(this.complemento))
        	result += " - " + this.complemento;

        if (this.bairro != null)
        	result +=" - " + this.bairro;

        if (this.municipio != null)
        	result +=" - " + this.municipio;

        return result;
    }

    /**
     * Este método formata o atual endereço para o exemplo:
     * Avenida Colombo, 5790 - Box 1 - Jardim Universitário
     * Esta propriedade é calculada e os metadados devem ficar sabendo disto.
     * @return
     */
    @Transient
    public String getEnderecoParaDocumento()
    {
    	String result = "";

    	if (this.logradouro != null){
    		result += this.logradouro;
    	}
    	if((this.numero!=null) && (this.numero!=0))
    		result += ", " + this.numero;

    	if(!StringUtils.isEmpty(this.complemento))
    		result += " - " + this.complemento;

    	if ((this.bairro != null) && (!StringUtils.isBlank(this.bairro.getNome())))
    		result +=" - " + this.bairro;

    	return result;
    }
	
}
