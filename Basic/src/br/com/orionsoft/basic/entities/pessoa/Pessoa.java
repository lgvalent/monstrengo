/*
 * Created on 15/02/2005
 * 
 */
package br.com.orionsoft.basic.entities.pessoa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.endereco.Endereco;
import br.com.orionsoft.basic.entities.endereco.Telefone;

/**
 * A classe pessoa é abstrata e possui duas especializações Juridica e Fisica.
 * Estas especializações podem ser verificadas utilizando os métodos isFisica(),
 * isJuridica(), e obtidas através dos métodos asFisica(), asJuridica().
 * 
 * @author Marcia
 * @hibernate.class table="basic_pessoa" 
 * @hibernate.joined-subclass-key column="id"
 *    
 */
@Entity
@Table(name="basic_pessoa")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator", discriminatorType=DiscriminatorType.STRING, length=1)
public abstract class Pessoa {
    /*
     * Constantes com o nomes das propriedades da classe para serem usadas no
     * código e evitar erro de digitação.
     */
    public static final String NOME = "nome";
    public static final String ENDERECOS = "enderecos";
    public static final String ENDERECO_CORRESPONDENCIA = "enderecoCorrespondencia";
    public static final String TELEFONES = "telefones";
    public static final String EMAIL = "email";
    public static final String DATA_CADASTRO = "dataCadastro";
    public static final String WWW = "www";
    public static final String DOCUMENTO = "documento";
    public static final String APELIDO = "apelido";
    public static final String DATA_INICIAL = "dataInicial";
    public static final String DATA_FINAL = "dataFinal";
    public static final String CMC = "cmc";

    public static final String AS_JURIDICA = "asJuridica";
    public static final String JURIDICA = "juridica";
    public static final String AS_FISICA= "asFisica";
    public static final String FISICA= "fisica";

    public static final String ESCRITORIO_CONTABIL = "escritorioContabil";
    public static final String CONTADOR = "contador";

    public static final String CODIGO_SEGURANCA = "codigoSeguranca";
    public static final String SENHA= "senha";

    private long id = -1;
    private String nome;
    private List<Endereco> enderecos = new ArrayList<Endereco>(0);
    private Endereco enderecoCorrespondencia;
    private Set<Telefone> telefones = new HashSet<Telefone>(0);
    private String email;
    private Calendar dataCadastro;
    private String www;
    private Set<Contrato> contratos = new HashSet<Contrato>(0);
    private String documento;
    private String apelido;
    private Calendar dataInicial;
    private Calendar dataFinal;
    private String cmc;

    private EscritorioContabil escritorioContabil;
    private Contador contador;
    
    private String codigoSeguranca;
    private String senha;
    
    
    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    /**
     * @return Returns the nome.
     * @hibernate.property length="255"
     */
    @Column(length=255)
    public String getNome() {
        return nome;
    }

    /**
     * @param nome
     *            The nome to set.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String toString() {
        return this.nome;
    }

    /**
     * Os endereços podem ser compartilhados entre as pessoas, no entanto, isto deve
     * ser transparente para o operador, para que ele não pense que alterando um endereço
     * todos os outros ligados ao mesmo serão alterados também. Foi discutido de, na 
     * interface, permitir que ele digite a alteração do endereço e o sistema realize uma
     * pesquisa para encontrar um endereço semelhante para fazer a ligação.
     * 
     * @hibernate.set table="basic_pessoa_endereco" cascade="save-update" lazy="false"
     * @hibernate.collection-key column="pessoa_id" 
     * @hibernate.collection-many-to-many class="br.com.orionsoft.basic.entities.endereco.Endereco" column="endereco_id"
     */
    @OneToMany 
    @LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name=Endereco.PESSOA) 
    @JoinColumn(name=Endereco.PESSOA)
    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    /**
     * @param endereco
     *            The endereco to set.
     */
    public void setEnderecos(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }

    /**
     * @return Returns the telefones.
     * @hibernate.set cascade="all" lazy="false" 
     * @hibernate.collection-one-to-many class="br.com.orionsoft.basic.entities.endereco.Telefone"
     * @hibernate.collection-key foreign-key="pessoa_id"
     * @hibernate.collection-key-column name="pessoa_id" index="pessoa_id"
     */
    @OneToMany 
    @LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name="pessoa_id") 
    @JoinColumn(name="pessoa_id")
    public Set<Telefone> getTelefones() {
        return telefones;
    }
    public void setTelefones(Set<Telefone> telefone) {
        this.telefones = telefone;
    }

    /**
     * @return Returns the email.
     * @hibernate.property length="70"
     */
    @Column(length=70)
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Propriedade não persistida que indica que o tipo de pessoa atualmente
     * instanciada é Fisica.
     * 
     * @return Retorna TRUE se a pessoa for Fisica.
     */
    @Transient
    public boolean isFisica() {
        return (this instanceof Fisica);
    }

    /**
     * Propriedade não persistida.
     * 
     * @return Se a pessoa for do tipo Fisica, retorna uma instância de Fisica,
     *         senão retorna null.
     */
    @Transient
    public Fisica getAsFisica() {
        if (isFisica())
            return Fisica.class.cast(this);

        return null;
    }

    /**
     * Propriedade não persistida que indica que o tipo de pessoa atualmente
     * instanciada é Juridica.
     * 
     * @return Retorna TRUE se a pessoa for Juridica.
     */
    @Transient
    public boolean isJuridica() {
        return (this instanceof Juridica);
    }

    /**
     * Propriedade não persistida.
     * 
     * @return Se a pessoa for do tipo Juridica, retorna uma instância de
     *         Juridica, senão retorna null.
     */
    @Transient
    public Juridica getAsJuridica() {
        if (isJuridica())
            return Juridica.class.cast(this);
        return null;
    }

    /**
     * @hibernate.many-to-one foreign-key="enderecoCorrespondencia"
     * @return
     */
    @ManyToOne
    @JoinColumn(name="enderecoCorrespondencia")
    @ForeignKey(name="enderecoCorrespondencia")
    public Endereco getEnderecoCorrespondencia() {
        return enderecoCorrespondencia;
    }

    public void setEnderecoCorrespondencia(Endereco enderecoCorrespondencia) {
        this.enderecoCorrespondencia = enderecoCorrespondencia;
    }

    /**
     * @hibernate.property
     * @return
     */
    @Column
    @Temporal(TemporalType.DATE)
    public Calendar getDataCadastro()
    {
        return dataCadastro;
    }
    public void setDataCadastro(Calendar dataCadastro)
    {
        this.dataCadastro = dataCadastro;
    }
    
    /**
     * @hibernate.property length="70"
     */
    @Column(length=70)
	public String getWww() {
		return www;
	}
	public void setWww(String www) {
		this.www = www;
	}

	/**
	 * @hibernate.property length="255"
	 */
	@Column(length=255)
	public String getApelido(){return apelido;}
	public void setApelido(String apelido){this.apelido = apelido;}
	
	/**
	 * @hibernate.property length="15"
	 */
	@Column(length=15)
	@Index(name=DOCUMENTO)
	public String getDocumento(){return documento;}
	public void setDocumento(String documento){this.documento = documento;}

	/**
	 * @hibernate.property
	 */
	@Column
	@Temporal(TemporalType.DATE)
    public Calendar getDataFinal(){return dataFinal;}
	public void setDataFinal(Calendar dataFinal){this.dataFinal = dataFinal;}
	
	/**
	 * @hibernate.property
	 */
	@Column
	@Temporal(TemporalType.DATE)
    public Calendar getDataInicial(){return dataInicial;}
	public void setDataInicial(Calendar dataInicial){this.dataInicial = dataInicial;}
	
	@Column(length=7)
	public String getCmc() {
		return cmc;
	}
	public void setCmc(String cmc) {
		this.cmc = cmc;
	}

	
	/**
     * @hibernate.set lazy="false"
     * @hibernate.collection-key-column index="pessoa" name="pessoa"  
     * @hibernate.collection-key foreign-key="pessoa" 
     * @hibernate.collection-one-to-many class="br.com.orionsoft.basic.entities.Contrato"
     */
	@OneToMany 
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name="pessoa") 
	@JoinColumn(name="pessoa")
	public Set<Contrato> getContratos() {
		return contratos;
	}
	public void setContratos(Set<Contrato> contratos) {
		this.contratos = contratos;
	}

    /**
     * @hibernate.many-to-one foreign-key="contador"
     * @return
     */
    @ManyToOne
    @JoinColumn(name="contador")
    @ForeignKey(name="contador")
    public Contador getContador() {
        return contador;
    }

    public void setContador(Contador contador) {
        this.contador = contador;
    }

    /**
     * @hibernate.many-to-one foreign-key="escritorioContabil"
     * @return
     */
    @ManyToOne
    @JoinColumn(name="escritorioContabil")
    @ForeignKey(name="escritorioContabil")
    public EscritorioContabil getEscritorioContabil() {
        return escritorioContabil;
    }

    public void setEscritorioContabil(EscritorioContabil escritorioContabil) {
        this.escritorioContabil = escritorioContabil;
    }

	/**
	 * Este código deve ser gerado pelo FisicaDVO, JuridicaDVO para aumentar na segurança
	 * de validação e autenticação dos usuários externos.
	 * @return
	 */
	@Column(length=3)
	public String getCodigoSeguranca() {return codigoSeguranca;}
	public void setCodigoSeguranca(String codigoSeguranca) {		this.codigoSeguranca = codigoSeguranca;}
	
	/**
	 * Define uma senha para a pessoa que pode ser utilizada pelos diversos módulos
	 * externos para que o usuário entre no sistema.<br>
	 * Inicialmente o usuário deverá entrar o número do seu documento, codigo segurança e senha. 
	 * 
	 * @return
	 */
	@Column(length=50)
	public String getSenha() {return senha;}
	public void setSenha(String senha) {this.senha = senha;}
	
}
