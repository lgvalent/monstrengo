package br.com.orionsoft.basic.entities.pessoa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Essa classe corresponde ao javabean que representa a entidade do profissao
 * que corresponde as profiss�es associadas �s pessoas fisicas. <br>
 * O campo c�digo n�o obedece a nenhum padr�o regulamentado e poder ser livremente
 * usado pelo dom�nio ao qual o sistema est� sendo aplicado. <br>
 * 
 * No caso de sindicatos, este c�digo poder� ser um correspondente ao CNAE.
 * 
 * @author marcia
 * @hibernate.class table="basic_profissao"
 */
@Entity
@Table(name="basic_profissao")
public class Profissao
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no c�digo e evitar erro de digita��o. */
    public static final String CODIGO = "codigo";
    public static final String NOME = "nome";

    private long id = -1;
    private String codigo;
    private String nome;
    
    /**
     * @hibernate.id  generator-class="native" unsaved-value="-1"
     * @return id da inst�ncia de profissao
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}
    
    /**
     * @hibernate.property length="100"
     * @return nome da profiss�o
     */
    @Column(length=100)
    public String getNome(){return nome;}
    public void setNome(String nome){this.nome = nome;}

    /**
     * @hibernate.property length="4"
     * @return c�digo da profiss�o sem padr�o regulamentado
     */
    @Column(length=4)
    public String getCodigo(){return codigo;}
    public void setCodigo(String codigo){this.codigo = codigo;}
    
    public String toString(){
    	String result = "";
    	
    	if(this.codigo!=null)
    		result += this.codigo + "-";
    	
    	result += this.nome;
    	
    	return result;
    }
}
