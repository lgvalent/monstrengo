package br.com.orionsoft.basic.entities.pessoa;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Essa classe corresponde ao javabean que representa a entidade do tipo juridica, que é uma
 * especialização da classe pessoa. Os objetos do tipo juridica são armazenados na tabela
 * 'pessoa' do banco de dados, sendo discriminado pela letra j na coluna jf da tabela.
 * 
 * Created on 15/02/2005
 * @author Marcia
 * @hibernate.joined-subclass table="basic_juridica"
 * Esta tag, informa que o índice a ser usado para ligação entre as classes é
 * o PRIMARY. Assim, o hibernate não cria novo índice com nomes "FDFS8DD8765".
 * @hibernate.joined-subclass-key foreign-key="PRIMARY" 
*/
@Entity
@DiscriminatorValue("J")
public class Juridica extends Pessoa
{      
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String IE = "ie";
    public static final String TIPO_ESTABELECIMENTO = "tipoEstabelecimento";
    public static final String CNAE = "cnae";
    public static final String CONTATO = "contato";
    
    /** Collection de pessoas fisicas.*/
    public static final String SOCIOS = "socios";
    
    /** Collection de pessoas fisicas.*/
    public static final String FUNCIONARIOS = "funcionarios";
    public static final String NUMERO_FUNCIONARIOS = "numeroFuncionarios";
    public static final String CAPITAL_SOCIAL = "capitalSocial";
    
    private String ie;
    private String tipoEstabelecimento;
    private CNAE cnae;
    private Contato contato;
    private Set<Socio> socios = new HashSet<Socio>();
    
    private Set<Funcionario> funcionarios = new HashSet<Funcionario>();
    private int numeroFuncionarios;
    
    private BigDecimal capitalSocial;
    
    private RegimeTributario regimeTributario;
    
    /**
     * detalhes sobre o relacionamento one-to-one
     * http://www.hibernate.org/hib_docs/v3/reference/en/html/mapping.html#mapping-declaration-onetoone
     * 
     * @hibernate.many-to-one unique="true" foreign-key="contato"
     */
    @ManyToOne /*@Column(unique=true)*/
    @JoinColumn(name="contato")
    @ForeignKey(name="contato")
    public Contato getContato()
    {
        return contato;
    }
    public void setContato(Contato contato)
    {
        this.contato = contato;
    }
    
    /**
     * @hibernate.set cascade="all" lazy="false" 
     * @hibernate.collection-one-to-many class="br.com.orionsoft.basic.entities.pessoa.Socio"
     * @hibernate.collection-key foreign-key="juridica"
     * @hibernate.collection-key-column name="juridica" index="juridica"
     */
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name="juridica") 
    @JoinColumn(name="juridica")
    public Set<Socio> getSocios()
    {
        return socios;
    }
    public void setSocios(Set<Socio> socios)
    {
        this.socios = socios;
    }
    
    /**
     * @hibernate.property length="14"
     */
    @Column(length=14)
    public String getIe()
    {
        return ie;
    }
    public void setIe(String ie)
    {
        this.ie = ie;
    }
    
    /**
     * @return Retorna o tipo de estabelecimento. Indicado por: Filial, Único, Principal
     * @hibernate.property length="10"
     */
    @Column(length=10)
    public String getTipoEstabelecimento()
    {
        return tipoEstabelecimento;
    }
    public void setTipoEstabelecimento(String tipoEstabelecimento)
    {
        this.tipoEstabelecimento = tipoEstabelecimento;
    }
    
    /**
     * @return Retorna o ramo de atividade da pessoa juridica.
     * @hibernate.many-to-one foreign-key="cnae"
     */
    @ManyToOne
    @JoinColumn(name="cnae")
    @ForeignKey(name="cnae")
    public CNAE getCnae()
    {
        return cnae;
    }
    public void setCnae(CNAE cnae)
    {
        this.cnae = cnae;
    }
    
    /**
     * @hibernate.property
     * @hibernate.column name="capitalSocial" sql-type="Decimal(15,4)" 
     */
    @Column(name="capitalSocial", columnDefinition="Decimal(15,4)") 
    public BigDecimal getCapitalSocial()
    {
        return capitalSocial;
    }
    public void setCapitalSocial(BigDecimal capitalSocial)
    {
        this.capitalSocial = capitalSocial;
    }
    
    /**
     * @hibernate.property
     */
    @Column
    public int getNumeroFuncionarios()
    {
        return numeroFuncionarios;
    }
    public void setNumeroFuncionarios(int numeroFuncionarios)
    {
        this.numeroFuncionarios = numeroFuncionarios;
    }

    /**
     * @hibernate.set lazy="false" cascade="all" inverse="true"
     * @hibernate.collection-one-to-many class="br.com.orionsoft.basic.entities.pessoa.Funcionario"
     * @hibernate.collection-key foreign-key="juridica"
     * @hibernate.collection-key-column name="juridica" index="juridica" 
     */
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name="juridica") 
    @JoinColumn(name="juridica")
    public Set<Funcionario> getFuncionarios()
    {
        return funcionarios;
    }
    public void setFuncionarios(Set<Funcionario> funcionarios)
    {
        this.funcionarios = funcionarios;
    }
    
    @Enumerated(EnumType.STRING)
    @Column(length=RegimeTributario.COLUMN_DISCRIMINATOR_LENGTH)
    public RegimeTributario getRegimeTributario() {
		return regimeTributario;
	}
	public void setRegimeTributario(RegimeTributario regimeTributario) {
		this.regimeTributario = regimeTributario;
	}

	public String toString() {
        String result = getNome();
        if(StringUtils.isNotEmpty(super.getDocumento()))
        	result += " (CNPJ:" + super.getDocumento() + ")";
    	return result;
    }
	
}
