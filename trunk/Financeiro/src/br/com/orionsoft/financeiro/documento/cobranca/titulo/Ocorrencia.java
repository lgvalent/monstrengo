package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * 
 * @author marcia
 *
 * @hibernate.class table="financeiro_titulo_ocorrencia"
 */
@Entity
@Table(name="financeiro_titulo_ocorrencia")
public class Ocorrencia {
    
	public static final String CODIGO = "codigo";
    public static final String DESCRICAO = "descricao";
    
    /*
	 * Constantes que indicam a Ocorrência interna do Título no sistema
	 */
    public static final Ocorrencia REMESSA_REGISTRAR = new Ocorrencia(1001, "Remessa - Registrar título no banco"); //Registrar Título no Banco
    public static final Ocorrencia REMESSA_ENVIADA = new Ocorrencia(1002, "Remessa - Enviada ao banco"); //Indica que o título foi encaminhado por um arquivo de remessa e está aguardando o arquivo de retorno
    public static final Ocorrencia REMESSA_BAIXAR = new Ocorrencia(1003, "Remessa - Baixar");
	public static final Ocorrencia REMESSA_DEBITAR_EM_CONTA = new Ocorrencia(1004, "Remessa - Debitar em conta");
	public static final Ocorrencia REMESSA_CONCEDER_ABATIMENTO = new Ocorrencia(1005, "Remessa - Conceder abatimento");
	public static final Ocorrencia REMESSA_CANCELAR_ABATIMENTO = new Ocorrencia(1006, "Remessa - Cancelar abatimento");
	public static final Ocorrencia REMESSA_CONCEDER_DESCONTO = new Ocorrencia(1007, "Remessa - Conceder desconto");
	public static final Ocorrencia REMESSA_CANCELAR_DESCONTO = new Ocorrencia(1008, "Remessa - Cancelar desconto");
	public static final Ocorrencia REMESSA_ALTERAR_VENCIMENTO = new Ocorrencia(1009, "Remessa - Alterar vencimento");
	public static final Ocorrencia REMESSA_PROTESTAR = new Ocorrencia(1010, "Remessa - Protestar");
	public static final Ocorrencia REMESSA_CANCELAR_INSTRUCAO_PROTESTO = new Ocorrencia(1011, "Remessa - Cancelar instrução de protesto");
	public static final Ocorrencia REMESSA_DISPENSAR_JUROS = new Ocorrencia(1012, "Remessa - Dispensar juros");
	public static final Ocorrencia REMESSA_ALTERAR_NOME_ENDERECO_SACADO = new Ocorrencia(1013, "Remessa - Alterar nome-endereço do sacado");
	public static final Ocorrencia REMESSA_ALTERAR_NUMERO_CONTROLE = new Ocorrencia(1014, "Remessa - Alterar número de controle");
	public static final Ocorrencia REMESSA_OUTRAS_OCORRENCIAS = new Ocorrencia(1015, "Remessa - Outras ocorrências");
	
	//Retorno
	public static final Ocorrencia RETORNO_REGISTRO_CONFIRMADO = new Ocorrencia(1016, "Retorno - Registro confirmado"); 
	public static final Ocorrencia RETORNO_REGISTRO_RECUSADO = new Ocorrencia(1017, "Retorno - Registro recusado");
	public static final Ocorrencia RETORNO_COMANDO_RECUSADO = new Ocorrencia(1018, "Retorno - Comando recusado");
	public static final Ocorrencia RETORNO_LIQUIDADO = new Ocorrencia(1019, "Retorno - Liquidado");
	public static final Ocorrencia RETORNO_LIQUIDADO_EM_CARTORIO = new Ocorrencia(1020, "Retorno - Liquidado em Cartório");
	public static final Ocorrencia RETORNO_LIQUIDADO_PARCIALMENTE = new Ocorrencia(1021, "Retorno - Liquidado parcialmente");
	public static final Ocorrencia RETORNO_LIQUIDADO_SALDO_RESTANTE = new Ocorrencia(1022, "Retorno - Liquidado saldo restante");
	public static final Ocorrencia RETORNO_LIQUIDADO_SEM_REGISTRO = new Ocorrencia(1023, "Retorno - Liquidado sem registro"); //Cobrança sem registro - título liquidado
	public static final Ocorrencia RETORNO_LIQUIDADO_POR_CONTA = new Ocorrencia(1024, "Retorno - Liquidado por conta");
	public static final Ocorrencia RETORNO_BAIXA_SOLICITADA = new Ocorrencia(1025, "Retorno - Baixa solicitada");
	public static final Ocorrencia RETORNO_BAIXADO = new Ocorrencia(1026, "Retorno - Baixado");
	public static final Ocorrencia RETORNO_BAIXADO_POR_DEVOLUCAO = new Ocorrencia(1027, "Retorno - Baixado por devolução");
	public static final Ocorrencia RETORNO_BAIXADO_FRANCO_PAGAMENTO = new Ocorrencia(1028, "Retorno - Baixado por franco pagamento");
	public static final Ocorrencia RETORNO_BAIXA_POR_PROTESTO = new Ocorrencia(1029, "Retorno - Baixa por protesto");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_BAIXAR = new Ocorrencia(1030, "Retorno - Recebimento - Instrução baixar");
	public static final Ocorrencia RETORNO_BAIXA_OU_LIQUIDACAO_ESTORNADA = new Ocorrencia(1031, "Retorno - Baixa ou liquidação estornada");
	public static final Ocorrencia RETORNO_TITULO_EM_SER = new Ocorrencia(1032, "Retorno - Título EmSer");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_CONCEDER_ABATIMENTO = new Ocorrencia(1033, "Retorno - Recebimento - Instrução conceder abatimento");
	public static final Ocorrencia RETORNO_ABATIMENTO_CONCEDIDO = new Ocorrencia(1034, "Retorno - Abatimento concedido");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_CANCELAR_ABATIMENTO = new Ocorrencia(1035, "Retorno - Recebimento - Instrução cancelar abatimento");
	public static final Ocorrencia RETORNO_ABATIMENTO_CANCELADO = new Ocorrencia(1036, "Retorno - Abatimento cancelado");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_CONCEDER_DESCONTO = new Ocorrencia(1037, "Retorno - Recebimento - Instrução conceder desconto");
	public static final Ocorrencia RETORNO_DESCONTO_CONCEDIDO = new Ocorrencia(1038, "Retorno - Desconto concedido");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_CANCELAR_DESCONTO = new Ocorrencia(1039, "Retorno - Recebimento - Instrução cancelar desconto");
	public static final Ocorrencia RETORNO_DESCONTO_CANCELADO = new Ocorrencia(1040, "Retorno - Desconto cancelado");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_ALTERAR_DADOS = new Ocorrencia(1041, "Retorno - Recebimento - Instrução alterar dados");
	public static final Ocorrencia RETORNO_DADOS_ALTERADOS = new Ocorrencia(1042, "Retorno - Dados alterados");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_ALTERAR_VENCIMENTO = new Ocorrencia(1043, "Retorno - Recebimento - Instrução alterar vencimento ");
	public static final Ocorrencia RETORNO_VENCIMENTO_ALTERADO = new Ocorrencia(1044, "Retorno - Vencimento alterado");
	public static final Ocorrencia RETORNO_ALTERACAO_DADOS_NOVA_ENTRADA = new Ocorrencia(1045, "Retorno - Alteração dados nova entrada");
	public static final Ocorrencia RETORNO_ALTERACAO_DADOS_BAIXA = new Ocorrencia(1046, "Retorno - Alteração dados baixa");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_PROTESTAR = new Ocorrencia(1047, "Retorno - Recebimento - Instrução protestar");
	public static final Ocorrencia RETORNO_PROTESTADO = new Ocorrencia(1048, "Retorno - Protestado");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_SUSTAR_PROTESTO = new Ocorrencia(1049, "Retorno - Recebimento - Instrução sustar protesto");
	public static final Ocorrencia RETORNO_PROTESTO_SUSTADO = new Ocorrencia(1050, "Retorno - Protesto sustado");
	public static final Ocorrencia RETORNO_INSTRUCAO_PROTESTO_REJEITADO_SUSTADO_PENDENTE = new Ocorrencia(1051, "Retorno - Instrução protesto - rejeitado, sustado ou pendente");
	public static final Ocorrencia RETORNO_DEBITO_EM_CONTA = new Ocorrencia(1052, "Retorno - Débito em conta");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_ALTERAR_NOME_SACADO = new Ocorrencia(1053, "Retorno - Recebimento - Instrução alterar nome do sacado");
	public static final Ocorrencia RETORNO_NOME_SACADO_ALTERADO = new Ocorrencia(1054, "Retorno - Nome do sacado alterado");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_ALTERAR_ENDERECO_SACADO = new Ocorrencia(1055, "Retorno - Recebimento - Instrução alterar endereço do sacado");
	public static final Ocorrencia RETORNO_ENDERECO_SACADO_ALTERADO = new Ocorrencia(1056, "Retorno - Endereço do sacado alterado");
	public static final Ocorrencia RETORNO_ENCAMINHADO_CARTORIO = new Ocorrencia(1057, "Retorno - Encaminhado ao Cartório");
	public static final Ocorrencia RETORNO_RETIRADO_CARTORIO = new Ocorrencia(1058, "Retorno - Retirado de Cartório");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_DISPENSAR_JUROS = new Ocorrencia(1059, "Retorno - Recebimento - Instrução dispensar juros");
	public static final Ocorrencia RETORNO_JUROS_DISPENSADOS = new Ocorrencia(1060, "Retorno - Juros dispensados");
	public static final Ocorrencia RETORNO_MANUTENCAO_TITULO_VENCIDO = new Ocorrencia(1061, "Retorno - Manutenção de título vencido");
	public static final Ocorrencia RETORNO_RECEBIMENTO_INSTRUCAO_ALTERAR_TIPO_COBRANCA = new Ocorrencia(1062, "Retorno - Recebimento - Instrução alterar tipo de cobrança");
	public static final Ocorrencia RETORNO_TIPO_COBRANCA_ALTERADO = new Ocorrencia(1063, "Retorno - Tipo de cobrança alterado");
	public static final Ocorrencia RETORNO_DESPESAS_PROTESTO = new Ocorrencia(1064, "Retorno - Despesas protesto");
	public static final Ocorrencia RETORNO_DESPESAS_SUSTACAO_PROTESTO = new Ocorrencia(1065, "Retorno - Despesas sustação protesto");
	public static final Ocorrencia RETORNO_DEBITO_CUSTAS_ANTECIPADAS = new Ocorrencia(1066, "Retorno - Débito custas antecipadas");
	public static final Ocorrencia RETORNO_CUSTAS_CARTORIO_DISTRIBUIDOR = new Ocorrencia(1067, "Retorno - Custas Cartório Distribuidor");
	public static final Ocorrencia RETORNO_CUSTAS_EDITAL = new Ocorrencia(1068, "Retorno - Custas edital");
	public static final Ocorrencia RETORNO_PROTESTO_SUSTACAO_ESTORNADO = new Ocorrencia(1069, "Retorno - Protesto sustação estornado");
	public static final Ocorrencia RETORNO_DEBITO_TARIFAS = new Ocorrencia(1070, "Retorno - Débito de tarifas");
	public static final Ocorrencia RETORNO_ACERTO_DEPOSITARIA = new Ocorrencia(1071, "Retorno - Acerto Depositária");
	public static final Ocorrencia RETORNO_OUTRAS_OCORRENCIAS = new Ocorrencia(1072, "Retorno - Outras ocorrências");
	
	/* 
	 * Ocorrências novas devem ser adicionadas ao final, para que não ocorra mudança nos códigos de ocorrências antigas,
	 * evitando inconsitências nos dados que já estão no banco. 
	 */
	public static final Ocorrencia CONTROLE_INTERNO_BAIXA = new Ocorrencia(1073, "Controle Interno - Baixa efetuada no documento");
	public static final Ocorrencia CONTROLE_INTERNO_CANCELADO = new Ocorrencia(1074, "Controle Interno - Documento cancelado no sistema");
	public static final Ocorrencia RETORNO_DOCUMENTO_CANCELADO = new Ocorrencia(1075, "Retorno - Documento cancelado no banco");
	public static final Ocorrencia REMESSA_CANCELAR_DOCUMENTO = new Ocorrencia(1076, "Remessa - Pedido de cancelamento do documento no banco");

	public static final Ocorrencia RETORNO_TARIFA = new Ocorrencia(1080, "Retorno - Tarifa bancária");
	

	private long id = IDAO.ENTITY_UNSAVED;
    private int codigo;
    private String descricao;
    
	public Ocorrencia(){}
	
	public Ocorrencia(int codigo, String descricao){
		this.codigo = codigo;
		this.descricao = descricao;
	}

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
        String result = "" + this.codigo;
        result += " - " + this.descricao;
        
        return result;
    }
	
	@Override
	public boolean equals(Object obj) {
		return obj == null? false: this.codigo == ((Ocorrencia) obj).codigo;
	}
}
