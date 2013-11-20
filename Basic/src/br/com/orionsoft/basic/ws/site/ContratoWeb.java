package br.com.orionsoft.basic.ws.site;

import java.util.Calendar;

import br.com.orionsoft.basic.entities.Contrato;

/**
 * Classe POJO para informações simples sobre a pessoa e o contrato.
 * Útil para uma exibiÃ§Ã£o plana dos dados de uma pessoa sem navegaÃ§Ã£o hierÃ¡rquica.
 * @author lucio
 */
public class ContratoWeb {
    private long id = -1;
    private Calendar dataInicio;
    private Calendar dataRescisao;
    private Calendar dataVencimento;
    private boolean inativo;
    private String representante;
    private String categoria;
    private String observacoes;
    
    public ContratoWeb() {}
    public ContratoWeb(Contrato contrato) {
        id = contrato.getId();
        dataInicio = contrato.getDataInicio();
        dataRescisao = contrato.getDataRescisao();
        dataVencimento = contrato.getDataVencimento();
        inativo = contrato.isInativo();
        representante = contrato.getRepresentante().toString();
        categoria = contrato.getCategoria().toString();
        observacoes = contrato.getObservacoes()!=null?contrato.getObservacoes().toString():"";
    }
    
    
    public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Calendar getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Calendar dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Calendar getDataRescisao() {
		return dataRescisao;
	}
	public void setDataRescisao(Calendar dataRescisao) {
		this.dataRescisao = dataRescisao;
	}
	public Calendar getDataVencimento() {
		return dataVencimento;
	}
	public void setDataVencimento(Calendar dataVencimento) {
		this.dataVencimento = dataVencimento;
	}
	public boolean isInativo() {
		return inativo;
	}
	public void setInativo(boolean inativo) {
		this.inativo = inativo;
	}
	public String getRepresentante() {
		return representante;
	}
	public void setRepresentante(String representante) {
		this.representante = representante;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public String getObservacoes() {
		return observacoes;
	}
	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	private String codigo;

}
