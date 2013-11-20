package br.com.orionsoft.financeiro.documento.cobranca;

import java.math.BigDecimal;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.service.ServiceResultBean;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Esta classe é um bean simplificado da entidade Documento. É usada para facilitar o acesso e exibição
 * de muitos documentos que estão no banco de dados.
 * 
 * @author lucio
 * @version 20060614
 * 
 */
public class DocumentoCobrancaBean extends ServiceResultBean{
    
	public static final String QUERY_SELECT = "new " + DocumentoCobrancaBean.class.getName() +
			"(entity.id" +
			",entity." + DocumentoCobranca.DATA +
			",entity." + DocumentoCobranca.DATA_VENCIMENTO +
			",entity." + DocumentoCobranca.CONTRATO + "." + Contrato.PESSOA + "." + Pessoa.DOCUMENTO+
			",entity." + DocumentoCobranca.CONTRATO + "." + Contrato.PESSOA + "." + Pessoa.NOME +
			",entity." + DocumentoCobranca.DOCUMENTO_COBRANCA_CATEGORIA + "." + DocumentoCobrancaCategoria.NOME +
			",entity." + DocumentoCobranca.LAYOUT_ID +
			",entity." + DocumentoCobranca.VALOR +
			")";
	
	public DocumentoCobrancaBean(
			long id,
			Calendar dataDocumento,
			Calendar dataVencimento,
			String documento,
			String pessoa,
			String documentoCobrancaCategoria,
			int layoutId,
			BigDecimal valorDocumento){

		this.id = id;
		this.dataDocumento = dataDocumento;
		this.dataVencimento = dataVencimento;
		this.documento = documento;
		this.pessoa = pessoa;
		this.documentoCobrancaCategoria = documentoCobrancaCategoria;
		this.layoutId = layoutId;
		this.valorDocumento = valorDocumento;
	}

	/** Este construtor é utilizado pelos gerenciados durante o processo de retorno do arquivo 
	 *  para armazena os dados dos títulos processados.
	 *  As propriedades numeroDocumento, dataVencimento e valorPago foram inseridas aqui com este objetivo.
	 *  TODO Talvez seria interessante na classe base DocumentoCobranca ter as propriedades valorPago, valorJuros, valorMulta?
	 */
	public DocumentoCobrancaBean(
			long id,
			Calendar dataDocumento,
			Calendar dataVencimento,
			Calendar dataPagamento,
			String numeroDocumento,
			String documento,
			String pessoa,
			String documentoCobrancaCategoria,
			int layoutId,
			BigDecimal valorDocumento,
			BigDecimal valorPago){

		this.id = id;
		this.dataDocumento = dataDocumento;
		this.dataVencimento = dataVencimento;
		this.dataPagamento = dataPagamento;
		this.numeroDocumento = numeroDocumento;
		this.pessoa = pessoa;
		this.documentoCobrancaCategoria = documentoCobrancaCategoria;
		this.layoutId = layoutId;
		this.valorDocumento = valorDocumento;
		this.valorPago = valorPago;
	}
	
	public DocumentoCobrancaBean(IEntity<? extends DocumentoCobranca> documento, String instrucoesAdicionais){
		/* Armazena o documento original deste Bean */
		setDocumentoOriginal(documento);

		this.id = this.oDocumentoOriginal.getId();
		this.dataDocumento = this.oDocumentoOriginal.getData();
		this.dataVencimento = this.oDocumentoOriginal.getDataVencimento();
		this.documento = this.oDocumentoOriginal.getContrato().getPessoa().getDocumento();
		this.pessoa = this.oDocumentoOriginal.getContrato().getPessoa().getNome();
		this.documentoCobrancaCategoria = this.oDocumentoOriginal.getDocumentoCobrancaCategoria().getNome();
		this.valorDocumento = this.oDocumentoOriginal.getValor();
		
		this.instrucoesAdicionais = instrucoesAdicionais;
	}
	
	private long id = -1;
    private Calendar dataDocumento;
    private Calendar dataVencimento;
    private Calendar dataPagamento;
    private String numeroDocumento;
    private String documento;
    private String pessoa;
    private String documentoCobrancaCategoria;
    private int layoutId;
    private BigDecimal valorDocumento;
    private BigDecimal valorPago;

    /**
     * Permite que cada bean de documento receba uma instrução adicional para
     * ser impressa.
     */
    private String instrucoesAdicionais = "";
    
	/**
     * Se este bean for criado de um documento e não de uma pesquisa
     * é importante manter a referência para o documento, pois o mesmo
     * pode ter sido alterado em memória e estas alterações podem ser
     * importantes, por exemplo, na hora de imprimir um documento com os
     * cálculos de vencimentos realizados.
     * Assim, o gerenciador de documento, poderá verificar se tem um documento original
     * vinculado e não executar o retrieve, e usar a referência; 
     */
    private IEntity<? extends DocumentoCobranca> documentoOriginal = null;
    private DocumentoCobranca oDocumentoOriginal = null;
	public IEntity<? extends DocumentoCobranca> getDocumentoOriginal() {return documentoOriginal;}
	public void setDocumentoOriginal(IEntity<? extends DocumentoCobranca> documento) {
		this.documentoOriginal = documento;
		this.oDocumentoOriginal = documento.getObject();
	}
	public boolean isTemDocumentoOriginal() {return documentoOriginal!=null;}

    public long getId() {
    	if(isTemDocumentoOriginal())
			return this.oDocumentoOriginal.getId();
    	return id;
    }
    public void setId(long id) {this.id = id;}

    public BigDecimal getValorDocumento() {
    	if(isTemDocumentoOriginal())
			return this.oDocumentoOriginal.getValor();
    	return valorDocumento;
    }
    public void setValorDocumento(BigDecimal valorDocumento) {this.valorDocumento = valorDocumento;}

    public String getDocumentoCobrancaCategoria() {
    	if(isTemDocumentoOriginal())
			return this.oDocumentoOriginal.getDocumentoCobrancaCategoria().getNome();
    	return documentoCobrancaCategoria;
   	}
	public void setDocumentoCobrancaCategoria(String documentoCobrancaCategoria) {this.documentoCobrancaCategoria = documentoCobrancaCategoria;}

	public int getLayoutId() {
    	if(isTemDocumentoOriginal())
			return this.oDocumentoOriginal.getLayoutId();
		return layoutId;
	}
	public void setLayoutId(int layoutId) {		this.layoutId = layoutId;}
	
	public String getPessoa() {
    	if(isTemDocumentoOriginal())
			return this.oDocumentoOriginal.getContrato().getPessoa().getNome();
		return pessoa;
	}
	public void setPessoa(String pessoa) {this.pessoa = pessoa;}

	public String getInstrucoesAdicionais() {return instrucoesAdicionais;}
	public void setInstrucoesAdicionais(String instrucoesAdicionais) {this.instrucoesAdicionais = instrucoesAdicionais;}
	public boolean isTemInstrucoesAdicionais() {return !StringUtils.isBlank(instrucoesAdicionais);}

	public Calendar getDataDocumento() {return dataDocumento;}
	public void setDataDocumento(Calendar dataDocumento) {this.dataDocumento = dataDocumento;}

	public Calendar getDataVencimento() {
    	if(isTemDocumentoOriginal())
			return this.oDocumentoOriginal.getDataVencimento();
		return dataVencimento;}
	public void setDataVencimento(Calendar dataVencimento) {this.dataVencimento = dataVencimento;}

	public Calendar getDataPagamento() {
    	if(isTemDocumentoOriginal())
			return this.oDocumentoOriginal.getDataVencimento();
		return dataPagamento;}
	public void setDataPagamento(Calendar dataPagamento) {this.dataPagamento = dataPagamento;}

	public BigDecimal getValorPago() {
		if(isTemDocumentoOriginal())
			return this.oDocumentoOriginal.getValorPago();
		return valorPago;
	}
	public void setValorPago(BigDecimal valorPago) {this.valorPago = valorPago;}

	public String getNumeroDocumento() {return numeroDocumento;}
	public void setNumeroDocumento(String numeroDocumento) {this.numeroDocumento = numeroDocumento;}

	public String getDocumento() {return documento;}
	public void setDocumento(String documento) {this.documento = documento;}

}


