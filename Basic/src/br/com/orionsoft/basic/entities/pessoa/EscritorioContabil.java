package br.com.orionsoft.basic.entities.pessoa;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.basic.entities.Observacoes;

/**
 * JavaBean referente aos escritorios contabeis dos representados. Um
 * representado pode ou não ter um escritorio ligado a ele.
 * 
 * Created 2005/12/13
 * 
 * @author Marcia
 * 
 * @version 2005/12/29
 * @hibernate.class table="basic_escritorio_contabil"
 */
@Entity
@Table(name="basic_escritorio_contabil")
public class EscritorioContabil {
    public static final String PESSOA = "pessoa";
    public static final String DATA_CADASTRO = "dataCadastro";
    public static final String CONTADORES = "contadores";
    public static final String OBSERVACOES = "observacoes";

    private long id = -1;
    private Pessoa pessoa;
    private Calendar dataCadastro;
    private Set <Contador>contadores = new HashSet<Contador>();
    private Observacoes observacoes;

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * @hibernate.many-to-one unique="true" foreign-key="pessoa"
     */
    @ManyToOne /*@Column(unique=true)*/
    @JoinColumn(name="pessoa")
    @ForeignKey(name="pessoa")
    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    /**
     * @hibernate.property
     */
    @Column
    @Temporal(TemporalType.DATE)
    public Calendar getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Calendar dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    /**
     * @hibernate.many-to-one unique="true" foreign-key="observacoes"
     */
    @ManyToOne /*@Column(unique=true)*/
    @JoinColumn(name="observacoes")
    @ForeignKey(name="observacoes")
    public Observacoes getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(Observacoes observacoes) {
        this.observacoes = observacoes;
    }

    /**
     * @hibernate.set lazy="false" cascade="all" 
     * @hibernate.collection-one-to-many class="br.com.orionsoft.basic.entities.pessoa.Contador"
     * @hibernate.collection-key foreign-key="escritorioContabil"
     * @hibernate.collection-key-column name="escritorioContabil" index="escritorioContabil" 
     */
    @OneToMany @Cascade(CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name="escritorioContabil")
    @JoinColumn(name="escritorioContabil")
    public Set <Contador>getContadores() {
        return contadores;
    }
    public void setContadores(Set<Contador> contadores) {
        this.contadores = contadores;
    }
    
    public String toString(){
    	return this.id + "-" + (this.pessoa != null?this.pessoa.toString():"");
    }
    
}
