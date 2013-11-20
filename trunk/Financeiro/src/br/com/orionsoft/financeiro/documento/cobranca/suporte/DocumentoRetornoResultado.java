package br.com.orionsoft.financeiro.documento.cobranca.suporte;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.monstrengo.core.service.ServiceResultBean;


/**
 * Esta classe foi criada para permitir que as rotinas de processamento de retorno
 * classifique os títulos processados para posteriormente serem gerados relatórios
 * sobre o processamento do arquivo.
 * @author lucio 20071029
 *
 */
public class DocumentoRetornoResultado extends ServiceResultBean{

	public DocumentoRetornoResultado(){
		super();
	}

	/** Este construtor é utilizado pelos gerenciados durante o processo de retorno do arquivo 
	 *  para armazena os dados dos títulos processados.
	 *  As propriedades numeroDocumento, dataVencimento e valorPago foram inseridas aqui com este objetivo.
	 *  TODO Talvez seria interessante na classe base DocumentoCobranca ter as propriedades valorPago, valorJuros, valorMulta?
	 */
	public DocumentoRetornoResultado(
			long id,
			Calendar dataDocumento,
			Calendar dataVencimento,
			Calendar dataOcorrencia,
			String numeroDocumento,
			String pessoa,
			BigDecimal valorDocumento,
			BigDecimal valorPago){

		this.id = id;
		this.dataDocumento = dataDocumento;
		this.dataVencimento = dataVencimento;
		this.dataOcorrencia = dataOcorrencia;
		this.numeroDocumento = numeroDocumento;
		this.pessoa = pessoa;
		this.valorDocumento = valorDocumento;
		this.valorPago = valorPago;
	}

	private long id = -1;
	private Calendar dataDocumento;
	private Calendar dataVencimento;
	private Calendar dataOcorrencia;
	private String numeroDocumento;
	private String pessoa;
	private BigDecimal valorDocumento;
	private BigDecimal valorPago;
	
	private DocumentoRetornoStatus status;
	
	/** Indica alguma mensagem adicional que o gerenciador pode ter gerado durante 
	 * o processamento o documento	 */
	private String mensagem;

	public long getId() {return id;}
	public void setId(long id) {this.id = id;}

	public BigDecimal getValorDocumento() {return valorDocumento;}
	public void setValorDocumento(BigDecimal valorDocumento) {this.valorDocumento = valorDocumento;}

	public String getPessoa() {return pessoa;}
	public void setPessoa(String pessoa) {this.pessoa = pessoa;}

	public Calendar getDataDocumento() {return dataDocumento;}
	public void setDataDocumento(Calendar dataDocumento) {this.dataDocumento = dataDocumento;}

	public Calendar getDataVencimento() {return dataVencimento;}
	public void setDataVencimento(Calendar dataVencimento) {this.dataVencimento = dataVencimento;}

	public Calendar getDataOcorrencia() {return dataOcorrencia;}
	public void setDataOcorrencia(Calendar dataOcorrencia) {this.dataOcorrencia = dataOcorrencia;}

	public BigDecimal getValorPago() {return valorPago;}
	public void setValorPago(BigDecimal valorPago) {this.valorPago = valorPago;}

	public String getNumeroDocumento() {return numeroDocumento;}
	public void setNumeroDocumento(String numeroDocumento) {this.numeroDocumento = numeroDocumento;}

	public DocumentoRetornoStatus getStatus() {return status;}
	public void setStatus(DocumentoRetornoStatus status) {this.status = status;}
	
	public String getMensagem() {return mensagem;}
	public void setMensagem(String mensagem) {this.mensagem = mensagem;}

}
