package br.com.orionsoft.financeiro.gerenciador.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author marcia
 * 
 * @hibernate.class table="financeiro_banco"
 */
@Entity
@Table(name="financeiro_banco")
public class Banco {
    public static final String NOME = "nome";
    public static final String CODIGO = "codigo";
    public static final String DIGITO = "digito";
    
    private long id = -1;
    private String codigo;
    private String digito;
    private String nome;
    
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
     * @hibernate.property length="3"
     */
    @Column(length=3)
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    /**
     * @hibernate.property length="1" 
     */
    @Column(length=1)
    public String getDigito() {
        return this.digito;
    }
    
    public void setDigito(String digito) {
        this.digito = digito;
    }

    /**
     * @hibernate.property length="50"
     */
    @Column(length=50)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String toString(){
    	return this.codigo + "-" + this.digito + " " + this.nome; 
    }
}
