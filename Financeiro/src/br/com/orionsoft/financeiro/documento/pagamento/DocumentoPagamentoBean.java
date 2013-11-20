package br.com.orionsoft.financeiro.documento.pagamento;

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
 * @author Lucio
 * @version 20060614
 * 
 */
public class DocumentoPagamentoBean extends ServiceResultBean{
    
	public static final String QUERY_SELECT = "new " + DocumentoPagamentoBean.class.getName() +
			"(entity.id" +
			",entity." + DocumentoPagamento.DATA +
			",entity." + DocumentoPagamento.DATA_VENCIMENTO +
			",entity." + DocumentoPagamento.CONTRATO + "." + Contrato.PESSOA + "." + Pessoa.NOME +
			",entity." + DocumentoPagamento.DOCUMENTO_PAGAMENTO_CATEGORIA + "." + DocumentoPagamentoCategoria.NOME +
			",entity." + DocumentoPagamento.LAYOUT_ID +
			",entity." + DocumentoPagamento.VALOR +
			")";
	
	public DocumentoPagamentoBean(
			long id,
			Calendar dataDocumento,
			Calendar dataVencimento,
			String pessoa,
			String documentoPagamentoCategoria,
			int layoutId,
			BigDecimal valorDocumento){

		this.id = id;
		this.dataDocumento = dataDocumento;
		this.dataVencimento = dataVencimento;
		this.pessoa = pessoa;
		this.documentoPagamentoCategoria = documentoPagamentoCategoria;
		this.layoutId = layoutId;
		this.valorDocumento = valorDocumento;
	}

	public DocumentoPagamentoBean(IEntity<? extends DocumentoPagamento> documento, String instrucoesAdicionais){
		/* Armazena o documento original deste Bean */
		this.documentoOriginal = documento;
		this.oDocumentoOriginal = documento.getObject();

		this.id = this.oDocumentoOriginal.getId();
		this.dataDocumento = this.oDocumentoOriginal.getData();
		this.dataVencimento = this.oDocumentoOriginal.getDataVencimento();
		this.pessoa = this.oDocumentoOriginal.getContrato().getPessoa().getNome();
		this.pessoa = this.oDocumentoOriginal.getContrato().getPessoa().getNome();
		this.documentoPagamentoCategoria = this.oDocumentoOriginal.getDocumentoPagamentoCategoria().getNome();
		this.valorDocumento = this.oDocumentoOriginal.getValor();
		
		this.instrucoesAdicionais = instrucoesAdicionais;
		
	}
	
	private long id = -1;
    private Calendar dataDocumento;
    private Calendar dataVencimento;
    private String pessoa;
    private String documentoPagamentoCategoria;
    private int layoutId;
    private BigDecimal valorDocumento;

    /**
     * Permite que cada bean de documento receba uma instrução adicional para
     * ser impressa.
     */
    private String instrucoesAdicionais;
    
	/**
     * Se este bean for criado de um documento e não de uma pesquisa
     * é importante manter a referência para o documento, pois o mesmo
     * pode ter sido alterado em memória e estas alterações podem ser
     * importantes, por exemplo, na hora de imprimir um documento com os
     * cálculos de vencimentos realizados.
     * Assim, o gerenciador de documento, poderá verificar se tem um documento original
     * vinculado e não executar o retrieve, e usar a referência; 
     */
    private IEntity<? extends DocumentoPagamento> documentoOriginal = null;
    private DocumentoPagamento oDocumentoOriginal = null;
	public IEntity<? extends DocumentoPagamento> getDocumentoOriginal() {return documentoOriginal;}
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

    public String getDocumentoPagamentoCategoria() {
    	if(isTemDocumentoOriginal())
			return this.oDocumentoOriginal.getDocumentoPagamentoCategoria().getNome();
    	return documentoPagamentoCategoria;
   	}
	public void setDocumentoPagamentoCategoria(String documentoPagamentoCategoria) {this.documentoPagamentoCategoria = documentoPagamentoCategoria;}

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

	public Calendar getDataVencimento() {return dataVencimento;}
	public void setDataVencimento(Calendar dataVencimento) {this.dataVencimento = dataVencimento;}


}


