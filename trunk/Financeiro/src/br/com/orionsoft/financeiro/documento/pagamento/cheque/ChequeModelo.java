package br.com.orionsoft.financeiro.documento.pagamento.cheque;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;


/**
 * Esta classe permite que o operador crie diversos modelos de
 * cheques com diferentes posicionamentos dos campos de acordo 
 * com a impressora. 
 * 
 * @hibernate.class table="financeiro_cheque_modelo"
 */
@Entity
@Table(name="financeiro_cheque_modelo")
public class ChequeModelo {
	public static final String NOME = "nome";
	
	/* Fontes */
	public static final String FONTE_NOME = "fonteNome";
	public static final String FONTE_TAMANHO = "fonteTamanho";
	
	/* Configurações de posições na folha */
	public static final String FOLHA_ALTURA = "folhaAltura";
	public static final String FOLHA_LARGURA = "folhaLargura";
	public static final String CHEQUE_ALTURA = "chequeAltura";
	
	/* Campos do Cheque */
	public static final String NUMERO_CHEQUE_X = "numeroChequeX";
	public static final String NUMERO_CHEQUE_Y = "numeroChequeY";
	public static final String NUMERO_CHEQUE_TAMANHO = "numeroChequeTamanho";
	
	public static final String CODIGO_BANCO_X = "codigoBancoX";
	public static final String CODIGO_BANCO_Y = "codigoBancoY";
	public static final String CODIGO_BANCO_TAMANHO = "codigoTamanho";
	
	public static final String VALOR_EXTENSO_X = "valorExtensoX";
	public static final String VALOR_EXTENSO_Y = "valorExtensoY";
	public static final String VALOR_EXENSO_TAMANHO = "valorExtensoTamanho";
	
	public static final String VALOR_DECIMAL_X = "valorDecimalX";
	public static final String VALOR_DECIMAL_Y = "valorDecimalY";
	public static final String VALOR_DECIMAL_TAMANHO = "valorDecimalTamanho";
	
	public static final String NOME_FAVORECIDO_X = "nomeFavorecidoX";
	public static final String NOME_FAVORECIDO_Y = "nomeFavorecidoY";
	public static final String NOME_FAVORECIDO_TAMANHO = "nomeFavorecidoTamanho";
	
	public static final String CIDADE_ESTADO_X = "cidadeEstadoX";
	public static final String CIDADE_ESTADO_Y = "cidadeEstadoY";
	public static final String CIDADE_ESTADO_TAMANHO = "cidadeEstadoTamanho";
	
	public static final String DIA_X = "diaX";
	public static final String DIA_Y = "diaY";
	public static final String DIA_TAMANHO = "diaTamanho";
	
	public static final String MES_X = "mesX";
	public static final String MES_Y = "mesY";
	public static final String MES_TAMANHO = "mesTamanho";
	
	public static final String ANO_X = "anoX";
	public static final String ANO_Y = "anoY";
	public static final String ANO_TAMANHO = "anoTamanho";
	
	private long id = IDAO.ENTITY_UNSAVED;
	
	private String nome;
	
	/* Fontes */
	private String fonteNome;
	private int fonteTamanho=10;

	/* Configurações de posições na folha */
	private float folhaAltura;
	private float folhaLargura;
	private float chequeAltura; 

	/*
	 * Campos do Cheque, onde X representa o componente horizontalmente e Y verticalmente, ex:
	 * 'diaX' é a posição horizontal do campo 'dia' no cheque, e 'diaY' é a posição vertical do campo 'dia' no cheque
	 */
	private float numeroChequeX;
	private float numeroChequeY;
	private float numeroChequeTamanho;
	private float codigoBancoX;
	private float codigoBancoY;
	private float codigoBancoTamanho;
	private float valorExtensoX;
	private float valorExtensoY;
	private float valorExtensoTamanho;
	private float valorDecimalX;
	private float valorDecimalY;
	private float valorDecimalTamanho;
	private float nomeFavorecidoX;
	private float nomeFavorecidoY;
	private float nomeFavorecidoTamanho;
	private float cidadeEstadoX;
	private float cidadeEstadoY;
	private float cidadeEstadoTamanho;
	private float diaX;
	private float diaY;
	private float diaTamanho;
	private float mesX;
	private float mesY;
	private float mesTamanho;
	private float anoX;
	private float anoY;
	private float anoTamanho;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId(){return id;}
	public void setId(long id){this.id = id;}

	@Column(length=50)
	public String getNome(){return nome;}
	public void setNome(String nome){this.nome = nome;}
	
	@Column(length=50)
	public String getFonteNome() {return fonteNome;}
	public void setFonteNome(String fonteNome) {this.fonteNome = fonteNome;}
	
	@Column
	public int getFonteTamanho() {return fonteTamanho;}
	public void setFonteTamanho(int fonteTamanho) {this.fonteTamanho = fonteTamanho;}

	@Column
	public float getFolhaAltura() {return folhaAltura;}
	public void setFolhaAltura(float folhaAltura) {this.folhaAltura = folhaAltura;}
	
	@Column
	public float getFolhaLargura() {return folhaLargura;}
	public void setFolhaLargura(float folhaLargura) {this.folhaLargura = folhaLargura;}

	@Column
	public float getChequeAltura() {return chequeAltura;}
	public void setChequeAltura(float chequeAltura) {this.chequeAltura = chequeAltura;}
	
	@Column
	public float getAnoX() {return anoX;}
	public void setAnoX(float anoX) {this.anoX = anoX;}
	
	@Column
	public float getAnoY() {return anoY;}
	public void setAnoY(float anoY) {this.anoY = anoY;}
	
	@Column
	public float getCidadeEstadoX() {return cidadeEstadoX;}
	public void setCidadeEstadoX(float cidadeEstadoX) {this.cidadeEstadoX = cidadeEstadoX;}
	
	@Column
	public float getCidadeEstadoY() {return cidadeEstadoY;}
	public void setCidadeEstadoY(float cidadeEstadoY) {this.cidadeEstadoY = cidadeEstadoY;}
	
	@Column
	public float getCodigoBancoX() {return codigoBancoX;}
	public void setCodigoBancoX(float codigoBancoX) {this.codigoBancoX = codigoBancoX;}
	
	@Column
	public float getCodigoBancoY() {return codigoBancoY;}
	public void setCodigoBancoY(float codigoBancoY) {this.codigoBancoY = codigoBancoY;}
	
	@Column
	public float getDiaX() {return diaX;}
	public void setDiaX(float diaX) {this.diaX = diaX;}
	
	@Column
	public float getDiaY() {return diaY;}
	public void setDiaY(float diaY) {this.diaY = diaY;}
	
	@Column
	public float getMesX() {return mesX;}
	public void setMesX(float mesX) {this.mesX = mesX;}
	
	@Column
	public float getMesY() {return mesY;}
	public void setMesY(float mesY) {this.mesY = mesY;}
	
	@Column
	public float getNomeFavorecidoX() {return nomeFavorecidoX;}
	public void setNomeFavorecidoX(float nomeFavorecidoX) {this.nomeFavorecidoX = nomeFavorecidoX;}
	
	@Column
	public float getNomeFavorecidoY() {return nomeFavorecidoY;}
	public void setNomeFavorecidoY(float nomeFavorecidoY) {this.nomeFavorecidoY = nomeFavorecidoY;}
	
	@Column
	public float getNumeroChequeX() {return numeroChequeX;}
	public void setNumeroChequeX(float numeroChequeX) {this.numeroChequeX = numeroChequeX;}
	
	@Column
	public float getNumeroChequeY() {return numeroChequeY;}
	public void setNumeroChequeY(float numeroChequeY) {this.numeroChequeY = numeroChequeY;}
	
	@Column
	public float getValorDecimalX() {return valorDecimalX;}
	public void setValorDecimalX(float valorDecimalX) {this.valorDecimalX = valorDecimalX;}
	
	@Column
	public float getValorDecimalY() {return valorDecimalY;}
	public void setValorDecimalY(float valorDecimalY) {this.valorDecimalY = valorDecimalY;}
	
	@Column
	public float getValorExtensoX() {return valorExtensoX;}
	public void setValorExtensoX(float valorExtensoX) {this.valorExtensoX = valorExtensoX;}
	
	@Column
	public float getValorExtensoY() {return valorExtensoY;}
	public void setValorExtensoY(float valorExtensoY) {this.valorExtensoY = valorExtensoY;}

	@Column
	public float getAnoTamanho() {return anoTamanho;}
	public void setAnoTamanho(float anoTamanho) {this.anoTamanho = anoTamanho;}

	@Column
	public float getCidadeEstadoTamanho() {return cidadeEstadoTamanho;}
	public void setCidadeEstadoTamanho(float cidadeEstadoTamanho) {this.cidadeEstadoTamanho = cidadeEstadoTamanho;}
	
	@Column
	public float getCodigoBancoTamanho() {return codigoBancoTamanho;}
	public void setCodigoBancoTamanho(float codigoBancoTamanho) {this.codigoBancoTamanho = codigoBancoTamanho;}
	
	@Column
	public float getDiaTamanho() {return diaTamanho;}
	public void setDiaTamanho(float diaTamanho) {this.diaTamanho = diaTamanho;}
	
	@Column
	public float getMesTamanho() {return mesTamanho;}
	public void setMesTamanho(float mesTamanho) {this.mesTamanho = mesTamanho;}
	
	@Column
	public float getNomeFavorecidoTamanho() {return nomeFavorecidoTamanho;}
	public void setNomeFavorecidoTamanho(float nomeFavorecidoTamanho) {this.nomeFavorecidoTamanho = nomeFavorecidoTamanho;}
	
	@Column
	public float getNumeroChequeTamanho() {return numeroChequeTamanho;}
	public void setNumeroChequeTamanho(float numeroChequeTamanho) {this.numeroChequeTamanho = numeroChequeTamanho;}
	
	@Column
	public float getValorDecimalTamanho() {return valorDecimalTamanho;}
	public void setValorDecimalTamanho(float valorDecimalTamanho) {this.valorDecimalTamanho = valorDecimalTamanho;}
	
	@Column
	public float getValorExtensoTamanho() {return valorExtensoTamanho;}
	public void setValorExtensoTamanho(float valorExtensoTamanho) {this.valorExtensoTamanho = valorExtensoTamanho;}
	
	public String toString(){
		return this.nome;
	}
}
