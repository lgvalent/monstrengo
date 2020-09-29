package br.com.orionsoft.basic.entities.endereco;


/**
 * Rela��o de tipo de logradouros
 * 
 * @author Lucio 20070906
 * 
 * @version Lucio 20101109: Adptado segundo documento do Correios: http://www.correios.com.br/servicos%5Carquivos%5CGuiaPadrao.pdf
 * Obs: ROD foi acrescentado por j� existirem registros com esta denomina��o
 */
public enum TipoLogradouro
{
	ALAMEDA("AL","Alameda"),
	AVENIDA("AV","Avenida"),
	BALNEARIO("BAL","Balne�rio"),
	BLOCO("BL","Bloco"),
	CHACARA("CH","Ch�cara"),
	CONDOMINIO("COND","Condom�nio"),
	CONJUNTO("CJ","Conjunto"),
	ESTRADA("EST","Estrada"),
	FAZENDA("FAZ","Fazenda"),
	GALERIA("GAL","Galeria"),
	GRANJA("GJA","Granja"),
	JARDIM("JD","Jardim"),
	LARGO("LG","Largo"),
	LOTEAMENTO("LOT","Loteamento"),
	PARQUE("PRQ","Parque"),
	PRACA("P�","Pra�a"),
	PRAIA("PR","Praia"),
	QUADRA("Q","Quadra"),
	RODOVIA("ROD","Rodovia"),
	RUA("R","Rua"),
	SETOR("ST","Setor"),
	TRAVESSA("TV","Travessa"),
	VAZIO("",""),
	VIA("VIA", "Via"),
	VILA("VL","Vila");

	public static final int COLUMN_DISCRIMINATOR_LENGTH = 11;

	private String nome;
	private String abreviacao;
	
	private TipoLogradouro(String abreviacao, String nome){
		this.nome = nome;
		this.abreviacao = abreviacao;
	}

	public String getNome(){return nome;}
	public String getAbreviacao() {return abreviacao;}
    
    public String toString(){return this.nome;}
}
