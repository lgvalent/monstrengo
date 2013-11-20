package br.com.orionsoft.financeiro.documento.cobranca.suporte;

/**
 * Esta classe � �til para que as rotinas de retorno classifiquem
 * os documentos retornados de acordo com os estados aqui definidos.<br>
 * O estado SUCESSO deve ser usado para indicar que o t�tulo foi processado com sucesso,
 * os demais estados indicam que alguma inconsist�ncia foi encontrada. 
 * 
 * @author lucio 20071029
 *
 */
public enum DocumentoRetornoStatus {
	LIQUIDADO_COM_SUCESSO,
	NUMERO_DOCUMENTO_DUPLICADO,
	NUMERO_DOCUMENTO_NAO_ENCONTRADO,
	DOCUMENTO_JA_LIQUIDADO,
	OCORRENCIA_INVALIDA,
	OCORRENCIA_VALIDA,
	ERRO_ATUALIZANDO_DOCUMENTO;
}
