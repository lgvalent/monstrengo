package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import javax.persistence.Column;

/**
 * 
 * @author Lucio 20071113
 */
public enum OcorrenciaCategoria {
    
    /*
	 * Constantes que indicam a Ocorrência interna do Título no sistema
	 */
    RM_REGISTRAR(1001, "Remessa - Registrar título no banco"), //Registrar Título no Banco
    RM_ENVIADA(1002, "Remessa - Enviada ao banco"), //Indica que o título foi encaminhado por um arquivo de remessa e está aguardando o arquivo de retorno
    RM_BAIXAR(1003, "Remessa - Baixar"),
	RM_DEBITAR_EM_CONTA(1004, "Remessa - Debitar em conta"),
	RM_CONCEDER_ABATIMENTO(1005, "Remessa - Conceder abatimento"),
	RM_CANCELAR_ABATIMENTO(1006, "Remessa - Cancelar abatimento"),
	RM_CONCEDER_DESCONTO(1007, "Remessa - Conceder desconto"),
	RM_CANCELAR_DESCONTO(1008, "Remessa - Cancelar desconto"),
	RM_ALTERAR_VENCIMENTO(1009, "Remessa - Alterar vencimento"),
	RM_PROTESTAR(1010, "Remessa - Protestar"),
	RM_CANCELAR_INSTRUCAO_PROTESTO(1011, "Remessa - Cancelar instrução de protesto"),
	RM_DISPENSAR_JUROS(1012, "Remessa - Dispensar juros"),
	RM_ALTERAR_NOME_ENDERECO_SACADO(1013, "Remessa - Alterar nome-endereço do sacado"),
	RM_ALTERAR_NUMERO_CONTROLE(1014, "Remessa - Alterar número de controle"),
	RM_OUTRAS_OCORRENCIAS(1015, "Remessa - Outras ocorrências"),
	
	//Retorno
	RT_REGISTRO_CONFIRMADO(1016, "Retorno - Registro confirmado"), 
	RT_REGISTRO_RECUSADO(1017, "Retorno - Registro recusado"),
	RT_COMANDO_RECUSADO(1018, "Retorno - Comando recusado"),
	RT_LIQUIDADO(1019, "Retorno - Liquidado"),
	RT_LIQUIDADO_EM_CARTORIO(1020, "Retorno - Liquidado em Cartório"),
	RT_LIQUIDADO_PARCIALMENTE(1021, "Retorno - Liquidado parcialmente"),
	RT_LIQUIDADO_SALDO_RESTANTE(1022, "Retorno - Liquidado saldo restante"),
	RT_LIQUIDADO_SEM_REGISTRO(1023, "Retorno - Liquidado sem registro"), //Cobrança sem registro - título liquidado
	RT_LIQUIDADO_POR_CONTA(1024, "Retorno - Liquidado por conta"),
	RT_BAIXA_SOLICITADA(1025, "Retorno - Baixa solicitada"),
	RT_BAIXADO(1026, "Retorno - Baixado"),
	RT_BAIXADO_POR_DEVOLUCAO(1027, "Retorno - Baixado por devolução"),
	RT_BAIXADO_FRANCO_PAGAMENTO(1028, "Retorno - Baixado por franco pagamento"),
	RT_BAIXA_POR_PROTESTO(1029, "Retorno - Baixa por protesto"),
	RT_RECEBIMENTO_INSTRUCAO_BAIXAR(1030, "Retorno - Recebimento - Instrução baixar"),
	RT_BAIXA_OU_LIQUIDACAO_ESTORNADA(1031, "Retorno - Baixa ou liquidação estornada"),
	RT_TITULO_EM_SER(1032, "Retorno - Título EmSer"),
	RT_RECEBIMENTO_INSTRUCAO_CONCEDER_ABATIMENTO(1033, "Retorno - Recebimento - Instrução conceder abatimento"),
	RT_ABATIMENTO_CONCEDIDO(1034, "Retorno - Abatimento concedido"),
	RT_RECEBIMENTO_INSTRUCAO_CANCELAR_ABATIMENTO(1035, "Retorno - Recebimento - Instrução cancelar abatimento"),
	RT_ABATIMENTO_CANCELADO(1036, "Retorno - Abatimento cancelado"),
	RT_RECEBIMENTO_INSTRUCAO_CONCEDER_DESCONTO(1037, "Retorno - Recebimento - Instrução conceder desconto"),
	RT_DESCONTO_CONCEDIDO(1038, "Retorno - Desconto concedido"),
	RT_RECEBIMENTO_INSTRUCAO_CANCELAR_DESCONTO(1039, "Retorno - Recebimento - Instrução cancelar desconto"),
	RT_DESCONTO_CANCELADO(1040, "Retorno - Desconto cancelado"),
	RT_RECEBIMENTO_INSTRUCAO_ALTERAR_DADOS(1041, "Retorno - Recebimento - Instrução alterar dados"),
	RT_DADOS_ALTERADOS(1042, "Retorno - Dados alterados"),
	RT_RECEBIMENTO_INSTRUCAO_ALTERAR_VENCIMENTO(1043, "Retorno - Recebimento - Instrução alterar vencimento "),
	RT_VENCIMENTO_ALTERADO(1044, "Retorno - Vencimento alterado"),
	RT_ALTERACAO_DADOS_NOVA_ENTRADA(1045, "Retorno - Alteração dados nova entrada"),
	RT_ALTERACAO_DADOS_BAIXA(1046, "Retorno - Alteração dados baixa"),
	RT_RECEBIMENTO_INSTRUCAO_PROTESTAR(1047, "Retorno - Recebimento - Instrução protestar"),
	RT_PROTESTADO(1048, "Retorno - Protestado"),
	RT_RECEBIMENTO_INSTRUCAO_SUSTAR_PROTESTO(1049, "Retorno - Recebimento - Instrução sustar protesto"),
	RT_PROTESTO_SUSTADO(1050, "Retorno - Protesto sustado"),
	RT_INSTRUCAO_PROTESTO_REJEITADO_SUSTADO_PENDENTE(1051, "Retorno - Instrução protesto - rejeitado, sustado ou pendente"),
	RT_DEBITO_EM_CONTA(1052, "Retorno - Débito em conta"),
	RT_RECEBIMENTO_INSTRUCAO_ALTERAR_NOME_SACADO(1053, "Retorno - Recebimento - Instrução alterar nome do sacado"),
	RT_NOME_SACADO_ALTERADO(1054, "Retorno - Nome do sacado alterado"),
	RT_RECEBIMENTO_INSTRUCAO_ALTERAR_ENDERECO_SACADO(1055, "Retorno - Recebimento - Instrução alterar endereço do sacado"),
	RT_ENDERECO_SACADO_ALTERADO(1056, "Retorno - Endereço do sacado alterado"),
	RT_ENCAMINHADO_CARTORIO(1057, "Retorno - Encaminhado ao Cartório"),
	RT_RETIRADO_CARTORIO(1058, "Retorno - Retirado de Cartório"),
	RT_RECEBIMENTO_INSTRUCAO_DISPENSAR_JUROS(1059, "Retorno - Recebimento - Instrução dispensar juros"),
	RT_JUROS_DISPENSADOS(1060, "Retorno - Juros dispensados"),
	RT_MANUTENCAO_TITULO_VENCIDO(1061, "Retorno - Manutenção de título vencido"),
	RT_RECEBIMENTO_INSTRUCAO_ALTERAR_TIPO_COBRANCA(1062, "Retorno - Recebimento - Instrução alterar tipo de cobrança"),
	RT_TIPO_COBRANCA_ALTERADO(1063, "Retorno - Tipo de cobrança alterado"),
	RT_DESPESAS_PROTESTO(1064, "Retorno - Despesas protesto"),
	RT_DESPESAS_SUSTACAO_PROTESTO(1065, "Retorno - Despesas sustação protesto"),
	RT_DEBITO_CUSTAS_ANTECIPADAS(1066, "Retorno - Débito custas antecipadas"),
	RT_CUSTAS_CARTORIO_DISTRIBUIDOR(1067, "Retorno - Custas Cartório Distribuidor"),
	RT_CUSTAS_EDITAL(1068, "Retorno - Custas edital"),
	RT_PROTESTO_SUSTACAO_ESTORNADO(1069, "Retorno - Protesto sustação estornado"),
	RT_DEBITO_TARIFAS(1070, "Retorno - Débito de tarifas"),
	RT_ACERTO_DEPOSITARIA(1071, "Retorno - Acerto Depositária"),
	RT_OUT_OCORRENCIAS(1072, "Retorno - Outras ocorrências"),
	
	/* 
	 * Ocorrências novas devem ser adicionadas ao final, para que não ocorra mudança nos códigos de ocorrências antigas,
	 * evitando inconsitências nos dados que já estão no banco. 
	 */
	CN_INT_BAIXA(1073, "Controle Interno - Baixa efetuada no documento"),
	CN_INT_CANCELADO(1074, "Controle Interno - Documento cancelado no sistema"),
	RT_DOC_CANCELADO(1075, "Retorno - Documento cancelado no banco"),
	RM_CAN_DOCUMENTO(1076, "Remessa - Pedido de cancelamento do documento no banco");
	

    private int codigo;
    private String descricao;
    
	private OcorrenciaCategoria(int codigo, String descricao){
		this.codigo = codigo;
		this.descricao = descricao;
	}

    /**
     * @hibernate.property
     */
    @Column
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    /**
     * @hibernate.property length="100"
     */
    @Column(length=100)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String toString() {
        String result = "Código: " + this.codigo;
        result += " / Descrição: " + this.descricao;
        
        return result;
    }
}
