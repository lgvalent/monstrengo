package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.endereco.Endereco;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.utils.UtilsRemessa;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Classe que descreve todas as propriedades de um Título <br>
 * 
 * @author Andre
 * 
 */
public class TituloPrintBean {
    
    public TituloPrintBean() {
    }
    
    public TituloPrintBean(IEntity<? extends DocumentoCobranca> _titulo, String instrucoesAdicionais, IGerenciadorBanco gerenciadorBanco) throws RuntimeException, Exception {
        DocumentoTitulo titulo = (DocumentoTitulo)_titulo.getObject();
    	
        /* ce = cedente */
        ceNome = titulo.getCedente().getContratante().getNome();
        ceBancoCodigo = titulo.getCedente().getContaBancaria().getBanco().getCodigo();
        ceBancoDigito = titulo.getCedente().getContaBancaria().getBanco().getDigito();
        ceCarteiraCodigo = titulo.getCedente().getCarteiraCodigo();
        ceLocalPagamento = titulo.getCedente().getLocalPagamento();
        if (titulo.getCedente().isAceite())
            ceAceite = "S";
        else
            ceAceite = "N";
        
        /* Não permite que as instruções sejam nulas para não aparecer 'null' no documento quando
         * as instruções são usadas em expressões $F{dtInstrucoes3} + '\n' */
        ceInstrucoes0 = titulo.getDocumentoCobrancaCategoria().getInstrucoes0();
        if(ceInstrucoes0 == null) ceInstrucoes0 = "";
        
        ceInstrucoes1 = titulo.getDocumentoCobrancaCategoria().getInstrucoes1();
        if(ceInstrucoes1 == null) ceInstrucoes1 = "";

        ceInstrucoes2 = titulo.getDocumentoCobrancaCategoria().getInstrucoes2();
        if(ceInstrucoes2 == null) ceInstrucoes2 = "";
        
        this.dtInstrucoesAdicionais = instrucoesAdicionais;
        
        /* ds = Dados do Sacado */
        /* Pega o primeiro grupo do documento e os dados deste primeiro grupo. Pois, teoricamente
         * todos os grupos deste documento pertencem ao mesmo contrato */
      	Contrato contrato = titulo.getContrato();
        	
       	dsNome = contrato.getPessoa().getNome();
       	/* Pega o cnpj/cpf formatado */
   		dsNumeroInscricao = _titulo.getProperty(DocumentoTitulo.CONTRATO).getValue().getAsEntity().getProperty(Contrato.PESSOA).getValue().getAsEntity().getProperty(Pessoa.DOCUMENTO).getValue().getAsString();
       	
       	Endereco endereco = contrato.getPessoa().getEnderecoCorrespondencia();
       	dsEndereco = contrato.getPessoa().getEnderecoCorrespondencia().getLogradouro().getTipoLogradouro().getNome() + " " + 
       				 UtilsRemessa.formatarEndereco(endereco, 70);
       	
       	try{
       		dsCep = contrato.getPessoa().getEnderecoCorrespondencia().getCep() + " - " + 
       		contrato.getPessoa().getEnderecoCorrespondencia().getBairro().getNome() + " - " +
       		contrato.getPessoa().getEnderecoCorrespondencia().getMunicipio().toString();
       	}catch(Exception e){
       		throw new Exception("Problema com o cadastro do endereço do contrato:" + contrato.toString());
       	}

        /* dt = Dados do DocumentoTitulo */
        dtAgencia_CodigoCedente = gerenciadorBanco.formatarAgenciaCedente(_titulo);
        dtBancoCodigo = ceBancoCodigo + "-" + ceBancoDigito;

        dtInstrucoes3 = titulo.getInstrucoes3();
        if(dtInstrucoes3 == null) dtInstrucoes3 = "";
        
        dtDataVencimento = new SimpleDateFormat("dd/MM/yyyy").format(titulo.getDataVencimento().getTime());
        dtNossoNumero = titulo.getNumeroDocumento();
        
        /* dados da contribuição */
        if((titulo.getValor() != null) && (titulo.getValor().compareTo(BigDecimal.ZERO)!=0)){
        	/* Não imprime o valor, deixa em branco */
            dtValorDocumento = DecimalUtils.formatBigDecimal(titulo.getValor());
        }

        if((titulo.getValorDesconto() != null) && (titulo.getValorDesconto().compareTo(BigDecimal.ZERO)!=0))
        	dtValorDesconto = DecimalUtils.formatBigDecimal(titulo.getValorDesconto());

        if((titulo.getOutrasDeducoes() != null) && (titulo.getOutrasDeducoes().compareTo(BigDecimal.ZERO)!=0))
        	dtOutrasDeducoes = DecimalUtils.formatBigDecimal(titulo.getOutrasDeducoes());

        if((titulo.getValorMulta() != null) && (titulo.getValorMulta().compareTo(BigDecimal.ZERO)!=0))
        	dtValorMulta = DecimalUtils.formatBigDecimal(titulo.getValorMulta());

        if((titulo.getValorJuros() != null) && (titulo.getValorJuros().compareTo(BigDecimal.ZERO)!=0))
        	dtValorJuros = DecimalUtils.formatBigDecimal(titulo.getValorJuros());

        if((titulo.getValorAcrescimo() != null) && (titulo.getValorAcrescimo().compareTo(BigDecimal.ZERO)!=0))
        	dtValorAcrescimos = DecimalUtils.formatBigDecimal(titulo.getValorAcrescimo());
        
        if((titulo.getValorPago() != null) && (titulo.getValorPago().compareTo(BigDecimal.ZERO)!=0))
        	dtValorPago = DecimalUtils.formatBigDecimal(titulo.getValorPago());
        
        dtDataDocumento = new SimpleDateFormat("dd/MM/yyyy").format(titulo.getData().getTime());
        dtDataProcessamento = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        dtNumeroDocumento = titulo.getNumeroDocumento();

        /* Código de barras */
//        CodigoBarrasTitulo codigoBarras = new CodigoBarrasTitulo(_titulo, gerenciadorBanco.getCampoLivre(_titulo));
        cbCodigoBarras = gerenciadorBanco.getCodigoBarras(_titulo);
        cbLinhaDigitavel = gerenciadorBanco.getLinhaDigitavel(_titulo);
    }

    /** ce = Cedente */
    private String ceNome;
    private String ceBancoCodigo;
    private String ceBancoDigito;
    private String ceCarteiraCodigo;
    private String ceAceite;
    private String ceLocalPagamento;
    private String ceInstrucoes0;
    private String ceInstrucoes1;
    private String ceInstrucoes2;
    private String dtInstrucoesAdicionais;
    
    /** sa = Dados do Sacado */
    private String dsNome;
    private String dsEndereco;
    private String dsCep;
    private String dsNumeroInscricao; //CPF ou CNPJ do Sacado
    
    /** dt = Dados do DocumentoTitulo */
    private String dtInstrucoes3;
    private String dtNumeroDocumento;
    private String dtValorDocumento = ""; 
    private String dtValorDesconto = ""; 
    private String dtValorMulta = ""; 
    private String dtValorJuros = ""; 
    private String dtValorAcrescimos = ""; 
    private String dtValorPago = ""; 
    private String dtOutrasDeducoes; 
    private String dtDataDocumento;
    private String dtDataProcessamento;
    private String dtDataVencimento;
    private String dtAgencia_CodigoCedente;
    private String dtBancoCodigo;
    private String dtNossoNumero;
    
    /** cb = Código de barras */
    private String cbCodigoBarras;
    private String cbLinhaDigitavel;

    
    public String getCeAceite() {
        return ceAceite;
    }

    public void setCeAceite(String ceAceite) {
        this.ceAceite = ceAceite;
    }

    public String getCeBancoCodigo() {
        return ceBancoCodigo;
    }

    public void setCeBancoCodigo(String ceBancoCodigo) {
        this.ceBancoCodigo = ceBancoCodigo;
    }

    public String getCeBancoDigito() {
        return ceBancoDigito;
    }

    public void setCeBancoDigito(String ceBancoDigito) {
        this.ceBancoDigito = ceBancoDigito;
    }

    public String getCeCarteiraCodigo() {
        return ceCarteiraCodigo;
    }

    public void setCeCarteiraCodigo(String ceCarteiraCodigo) {
        this.ceCarteiraCodigo = ceCarteiraCodigo;
    }

    public String getCeLocalPagamento() {
        return ceLocalPagamento;
    }

    public void setCeLocalPagamento(String ceLocalPagamento) {
        this.ceLocalPagamento = ceLocalPagamento;
    }

    public String getCeNome() {
        return ceNome;
    }

    public void setCeNome(String ceNome) {
        this.ceNome = ceNome;
    }

    public String getDsEndereco() {
        return dsEndereco;
    }

    public void setDsEndereco(String dsEndereco) {
        this.dsEndereco = dsEndereco;
    }
    
    public String getDsCep() {
		return dsCep;
	}

	public void setDsCep(String dsCep) {
		this.dsCep = dsCep;
	}

	public String getDsNome() {
        return dsNome;
    }

    public void setDsNome(String dsNome) {
        this.dsNome = dsNome;
    }

    public String getDsNumeroInscricao() {
        return dsNumeroInscricao;
    }

    public void setDsNumeroInscricao(String dsNumeroInscricao) {
        this.dsNumeroInscricao = dsNumeroInscricao;
    }

    public String getDtNumeroDocumento() {
        return dtNumeroDocumento;
    }

    public void setDtNumeroDocumento(String dtNumeroDocumento) {
        this.dtNumeroDocumento = dtNumeroDocumento;
    }

    public String getDtAgencia_CodigoCedente() {
        return dtAgencia_CodigoCedente;
    }

    public void setDtAgencia_CodigoCedente(String dtAgencia_CodigoCedente) {
        this.dtAgencia_CodigoCedente = dtAgencia_CodigoCedente;
    }
    
    public String getDtBancoCodigo() {
        return dtBancoCodigo;
    }

    public void setDtBancoCodigo(String dtBancoCodigo) {
        this.dtBancoCodigo = dtBancoCodigo;
    }

    public String getDtDataDocumento() {
        return dtDataDocumento;
    }

    public void setDtDataDocumento(String dtDataDocumento) {
        this.dtDataDocumento = dtDataDocumento;
    }
    
    public String getDtDataProcessamento() {
        return dtDataProcessamento;
    }

    public void setDtDataProcessamento(String dtDataProcessamento) {
        this.dtDataProcessamento = dtDataProcessamento;
    }

    public String getDtDataVencimento() {
        return dtDataVencimento;
    }

    public void setDtDataVencimento(String dtDataVencimento) {
        this.dtDataVencimento = dtDataVencimento;
    }

    public String getDtNossoNumero() {
        return dtNossoNumero;
    }

    public void setDtNossoNumero(String dtNossoNumero) {
        this.dtNossoNumero = dtNossoNumero;
    }

    public String getDtOutrasDeducoes() {
        return dtOutrasDeducoes;
    }

    public void setDtOutrasDeducoes(String dtOutrasDeducoes) {
        this.dtOutrasDeducoes = dtOutrasDeducoes;
    }

    public String getDtValorAcrescimos() {
        return dtValorAcrescimos;
    }

    public void setDtValorAcrescimos(String dtValorAcrescimos) {
        this.dtValorAcrescimos = dtValorAcrescimos;
    }

    public String getDtValorDesconto() {
        return dtValorDesconto;
    }

    public void setDtValorDesconto(String dtValorDesconto) {
        this.dtValorDesconto = dtValorDesconto;
    }

    public String getDtValorDocumento() {
        return dtValorDocumento;
    }

    public void setDtValorDocumento(String dtValorDocumento) {
        this.dtValorDocumento = dtValorDocumento;
    }

    public String getDtValorMulta() {
        return dtValorMulta;
    }

    public void setDtValorMulta(String dtValorMulta) {
        this.dtValorMulta = dtValorMulta;
    }

    public String getDtValorJuros() {
        return dtValorJuros;
    }

    public void setDtValorJuros(String dtValorJuros) {
        this.dtValorJuros = dtValorJuros;
    }

    public String getDtValorPago() {
        return dtValorPago;
    }

    public void setDtValorPago(String dtValorPago) {
        this.dtValorPago = dtValorPago;
    }

    public String getCbCodigoBarras() {
        return cbCodigoBarras;
    }

    public void setCbCodigoBarras(String cbCodigoBarras) {
        this.cbCodigoBarras = cbCodigoBarras;
    }

    public String getCbLinhaDigitavel() {
        return cbLinhaDigitavel;
    }

    public void setCbLinhaDigitavel(String cbLinhaDigitavel) {
        this.cbLinhaDigitavel = cbLinhaDigitavel;
    }

	public String getDtInstrucoesAdicionais() {
		return dtInstrucoesAdicionais;
	}

	public void setDtInstrucoesAdicionais(String instrucoesAdicionais) {
		this.dtInstrucoesAdicionais = instrucoesAdicionais;
	}

	public String getCeInstrucoes0() {
		return ceInstrucoes0;
	}

	public void setCeInstrucoes0(String ceInstrucoes0) {
		this.ceInstrucoes0 = ceInstrucoes0;
	}

	public String getCeInstrucoes1() {
		return ceInstrucoes1;
	}

	public void setCeInstrucoes1(String ceInstrucoes1) {
		this.ceInstrucoes1 = ceInstrucoes1;
	}

	public String getCeInstrucoes2() {
		return ceInstrucoes2;
	}

	public void setCeInstrucoes2(String ceInstrucoes2) {
		this.ceInstrucoes2 = ceInstrucoes2;
	}

	public String getDtInstrucoes3() {
		return dtInstrucoes3;
	}

	public void setDtInstrucoes3(String dtInstrucoes3) {
		this.dtInstrucoes3 = dtInstrucoes3;
	}
}