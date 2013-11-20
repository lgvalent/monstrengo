package br.com.orionsoft.financeiro.documento.cobranca.suporte;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Esta classe foi criada compilar e sumarizar os totais dos documentos retornados,
 * classificando pela situação.
 * @author lucio 20071114
 *
 */
public class DocumentoRetornoResultadoSumario{

	private List<DocumentoRetornoResultado> liquidadoComSucessoList = new ArrayList<DocumentoRetornoResultado>(); 
	private BigDecimal liquidadoComSucessoTotal = BigDecimal.ZERO; 
	
	private List<DocumentoRetornoResultado> numeroDocumentoDuplicadoList = new ArrayList<DocumentoRetornoResultado>(); 
	private BigDecimal numeroDocumentoDuplicadoTotal = BigDecimal.ZERO; 

	private List<DocumentoRetornoResultado> numeroDocumentoNaoEncontradoList = new ArrayList<DocumentoRetornoResultado>(); 
	private BigDecimal numeroDocumentoNaoEncontradoTotal = BigDecimal.ZERO; 

	private List<DocumentoRetornoResultado> documentoJaLiquidadoList = new ArrayList<DocumentoRetornoResultado>(); 
	private BigDecimal documentoJaLiquidadoTotal = BigDecimal.ZERO; 

	private List<DocumentoRetornoResultado> ocorrenciaInvalidaList = new ArrayList<DocumentoRetornoResultado>(); 
	private BigDecimal ocorrenciaInvalidaTotal = BigDecimal.ZERO; 

	private List<DocumentoRetornoResultado> ocorrenciaValidaList = new ArrayList<DocumentoRetornoResultado>(); 
	private BigDecimal ocorrenciaValidaTotal = BigDecimal.ZERO; 

	private List<DocumentoRetornoResultado> erroAtualizandoDocumentoList = new ArrayList<DocumentoRetornoResultado>(); 
	private BigDecimal erroAtualizandoDocumentoTotal = BigDecimal.ZERO; 

	private BigDecimal totalGeral = BigDecimal.ZERO; 

	/** Este construtor é utilizado pelos gerenciados durante o processo de retorno do arquivo 
	 *  para armazena os dados dos títulos processados.
	 *  As propriedades numeroDocumento, dataVencimento e valorPago foram inseridas aqui com este objetivo.
	 *  TODO Talvez seria interessante na classe base DocumentoCobranca ter as propriedades valorPago, valorJuros, valorMulta?
	 */
	public DocumentoRetornoResultadoSumario(List<DocumentoRetornoResultado> resultados){
		List<DocumentoRetornoResultado> listaCorrente = null;
		for(DocumentoRetornoResultado resultado: resultados){
			switch(resultado.getStatus()){
				case LIQUIDADO_COM_SUCESSO:{
					listaCorrente = liquidadoComSucessoList;
					if(resultado.getValorPago() !=null)
						liquidadoComSucessoTotal = liquidadoComSucessoTotal.add(resultado.getValorPago());
				}break;
				case DOCUMENTO_JA_LIQUIDADO:{
					listaCorrente = documentoJaLiquidadoList;
					if(resultado.getValorPago() !=null)
						documentoJaLiquidadoTotal = documentoJaLiquidadoTotal.add(resultado.getValorPago());
				}break;
				case ERRO_ATUALIZANDO_DOCUMENTO:{
					listaCorrente = erroAtualizandoDocumentoList;
					if(resultado.getValorPago() !=null)
						erroAtualizandoDocumentoTotal = erroAtualizandoDocumentoTotal.add(resultado.getValorPago());
				}break;
				case NUMERO_DOCUMENTO_DUPLICADO:{
					listaCorrente = numeroDocumentoDuplicadoList;
					if(resultado.getValorPago() !=null)
						numeroDocumentoDuplicadoTotal = numeroDocumentoDuplicadoTotal.add(resultado.getValorPago());
				}break;
				case NUMERO_DOCUMENTO_NAO_ENCONTRADO:{
					listaCorrente = numeroDocumentoNaoEncontradoList;
					if(resultado.getValorPago() !=null)
						numeroDocumentoNaoEncontradoTotal = numeroDocumentoNaoEncontradoTotal.add(resultado.getValorPago());
				}break;
				case OCORRENCIA_INVALIDA:{
					listaCorrente = ocorrenciaInvalidaList;
					if(resultado.getValorPago() !=null)
						ocorrenciaInvalidaTotal = ocorrenciaInvalidaTotal.add(resultado.getValorPago());
				}break;
				case OCORRENCIA_VALIDA:{
					listaCorrente = ocorrenciaValidaList;
					if(resultado.getValorPago() !=null)
						ocorrenciaInvalidaTotal = ocorrenciaInvalidaTotal.add(resultado.getValorPago());
				}break;
				default: {
					System.out.println("LUCIO");
				}
			}
			
			listaCorrente.add(resultado);
			
			/* Soma o total geral */
			totalGeral = totalGeral.add(liquidadoComSucessoTotal)
								   .add(documentoJaLiquidadoTotal)
								   .add(erroAtualizandoDocumentoTotal)
								   .add(numeroDocumentoDuplicadoTotal)
								   .add(numeroDocumentoNaoEncontradoTotal)
								   .add(ocorrenciaInvalidaTotal)
								   .add(ocorrenciaValidaTotal);
		}
	}

	public List<DocumentoRetornoResultado> getLiquidadoComSucessoList() {
		return liquidadoComSucessoList;
	}

	public BigDecimal getLiquidadoComSucessoTotal() {
		return liquidadoComSucessoTotal;
	}

	public List<DocumentoRetornoResultado> getNumeroDocumentoDuplicadoList() {
		return numeroDocumentoDuplicadoList;
	}

	public BigDecimal getNumeroDocumentoDuplicadoTotal() {
		return numeroDocumentoDuplicadoTotal;
	}

	public List<DocumentoRetornoResultado> getNumeroDocumentoNaoEncontradoList() {
		return numeroDocumentoNaoEncontradoList;
	}

	public BigDecimal getNumeroDocumentoNaoEncontradoTotal() {
		return numeroDocumentoNaoEncontradoTotal;
	}

	public List<DocumentoRetornoResultado> getDocumentoJaLiquidadoList() {
		return documentoJaLiquidadoList;
	}

	public BigDecimal getDocumentoJaLiquidadoTotal() {
		return documentoJaLiquidadoTotal;
	}

	public List<DocumentoRetornoResultado> getOcorrenciaInvalidaList() {
		return ocorrenciaInvalidaList;
	}

	public BigDecimal getOcorrenciaInvalidaTotal() {
		return ocorrenciaInvalidaTotal;
	}

	public List<DocumentoRetornoResultado> getErroAtualizandoDocumentoList() {
		return erroAtualizandoDocumentoList;
	}

	public BigDecimal getErroAtualizandoDocumentoTotal() {
		return erroAtualizandoDocumentoTotal;
	}

	public BigDecimal getTotalGeral() {
		return totalGeral;
	}

	public List<DocumentoRetornoResultado> getOcorrenciaValidaList() {
		return ocorrenciaValidaList;
	}

	public BigDecimal getOcorrenciaValidaTotal() {
		return ocorrenciaValidaTotal;
	}
	
	
}
