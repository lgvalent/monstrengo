package br.com.orionsoft.basic.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * Esta classe herda a classe Contrato e adiciona a 
 * capacidade de adicionar adesões.<br>
 * @author Lucio 20110725
 * @version 20110725 Lucio 
 */
@Entity
@DiscriminatorValue("CCA")
public class ContratoComAdesao extends Contrato{
	public static final String ADESOES = "adesoes";
    
    private Set <AdesaoContrato> adesoes = new HashSet<AdesaoContrato>();

	@OneToMany(mappedBy=AdesaoContrato.CONTRATO, fetch=FetchType.EAGER)
	@OrderBy(AdesaoContrato.DATA_ADESAO)
	public Set<AdesaoContrato> getAdesoes() {return adesoes;}
	public void setAdesoes(Set<AdesaoContrato> adesoes) {this.adesoes = adesoes;}

	public String toString() {
		StringBuffer result = new StringBuffer(super.toString());

		result.append("(Adesões:");
		int i = 0,a =0;
		for(AdesaoContrato adesao: adesoes){
			if(adesao.inativo)i++;
			else a++;
		}
		result.append(i+"I "+ a+"A)");
		
        return result.toString();
    }


}
