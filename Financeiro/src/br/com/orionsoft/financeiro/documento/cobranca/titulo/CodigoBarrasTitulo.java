package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

public class CodigoBarrasTitulo{
	public static final String MOEDA_CODIGO = "9";
	
	/* data base para efetuar os calculos necessários para associar uma data ao código de barras */
    private Calendar dataBase;

    /* valores para o código de barras e a linha digitável */
    private String bancoCodigo;
    private String moedaCodigo = MOEDA_CODIGO;
    private long fatorVencimento = 0;
    private long valorTitulo = 0;
    private String carteiraCodigo;
    private String agenciaCodigo;
    
    private String campoLivre;
    
    /**
     * Calcula o DV módulo 11 do código de barra
     * 
     * @param campo
     *            o campo a ser calculado
     * @return uma String contendo o DV
     */
    private static String modulo11(String codigo) {
        int total = 0;
        int peso = 2;
        for (int i = 0; i < 43; i++) {
            try {
                total += (codigo.charAt(42 - i) - '0') * peso;
            } catch (StringIndexOutOfBoundsException e) {
                throw e;
            }
            peso++;
            if (peso == 10)
                peso = 2;
        }
        int resto = total % 11;
        return (resto == 0 || resto == 1 || resto == 10) ? "1" : String.valueOf(11 - resto);
    }

    /**
     * Calcula o DV módulo 10 da linha digitável.
     * 
     * @param campo
     *            o campo a ser calculado
     * @return uma String contendo o DV
     */
    private static String modulo10(String codigo) {
        int total = 0;
        int peso;
        if (codigo.length() % 2 != 0)
            peso = 2;
        else
            peso = 1;
        for (int f = 1; f <= codigo.length(); f++) {
            int k = Integer.valueOf(codigo.substring(f - 1, f)) * peso;
            if (k > 9)
                k = 1 + (k - 10);
            total += k;
            if (peso == 1)
                peso = 2;
            else
                peso = 1;
        }
        peso = 1000 - total;
        return (String.valueOf(peso).substring(String.valueOf(peso).length() - 1, String.valueOf(peso).length()));
    }

    
//    public CodigoBarrasTitulo() {
//        setDataBase();
//    }

    public CodigoBarrasTitulo(IEntity<? extends DocumentoCobranca> documento, String campoLivre) {
    	DocumentoTitulo titulo = (DocumentoTitulo)documento.getObject();

    	setDataBase();
    	setBancoCodigo(titulo.getCedente().getContaBancaria().getBanco().getCodigo());
    	setFatorVencimento(titulo.getDataVencimento());
    	setValorTitulo(titulo.getValor());
    	setCarteiraCodigo(titulo.getCedente().getCarteiraCodigo());
    	setAgenciaCodigo(titulo.getCedente().getContaBancaria().getAgenciaCodigo());
        setCampoLivre(campoLivre);
    }

    /**
     * Gera a linha digitável do código de barras.
     * 
     * @return uma String contendo a linha digitável.
     */
    public String getLinhaDigitavel() {
        String grupo1 = bancoCodigo + moedaCodigo + carteiraCodigo + agenciaCodigo;
        grupo1 += modulo10(grupo1);
        
        String grupo2 = campoLivre.substring(0,10);
        grupo2 += modulo10(grupo2);
        
        String grupo3 = campoLivre.substring(10, campoLivre.length());
        grupo3 += modulo10(grupo3);
        
        String grupo4 = getCodigoBarras().substring(4,5);
        
        String grupo5 = String.format("%04d", fatorVencimento) + String.format("%010d", valorTitulo);
        
        String linha = grupo1.substring(0, 5) + "." + grupo1.substring(5, grupo1.length()) + " "
                + grupo2.substring(0, 5) + "." + grupo2.substring(5, grupo2.length()) + " " + grupo3.substring(0, 5)
                + "." + grupo3.substring(5, grupo3.length()) + " " + grupo4 + " " + grupo5;
        return linha;
    }

    /**
     * Gera os dígitos do código de barras.
     * 
     * @return uma String dos dígitos do código de barras.
     */
    public String getCodigoBarras() {
        String codigo = bancoCodigo + moedaCodigo + String.format("%04d", fatorVencimento) + String.format("%010d", valorTitulo)
                + carteiraCodigo + agenciaCodigo + campoLivre;
        String dg = modulo11(codigo);
        
        String codigoBarras = codigo.substring(0, 4) + dg + codigo.substring(4, codigo.length());
        return codigoBarras;
    }

    private void setDataBase() {
        this.dataBase = CalendarUtils.getBarCodeBaseDate();
    }
    
    public void setBancoCodigo(String banco) {
        this.bancoCodigo = banco;
    }
    
    public void setFatorVencimento(Calendar fatorVencimento) {
        this.fatorVencimento = CalendarUtils.diffDay(fatorVencimento, this.dataBase) + 1000;
    }

    public void setValorTitulo(BigDecimal valorTitulo) {
        if (valorTitulo != null){
        	this.valorTitulo = valorTitulo.multiply(new BigDecimal("100")).longValue();
        }
    }

    public void setCarteiraCodigo(String carteira){
        this.carteiraCodigo = carteira;
    }
    
    public void setAgenciaCodigo(String agencia){
        this.agenciaCodigo = agencia;
    }
    
    public void setCampoLivre(String campoLivre){
        this.campoLivre = campoLivre;
    }
    
}
