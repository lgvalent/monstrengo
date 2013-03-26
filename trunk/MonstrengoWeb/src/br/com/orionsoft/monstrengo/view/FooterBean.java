package br.com.orionsoft.monstrengo.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;

/**
 * Este bean controla a view o conteúdo que será exibido no
 * rodapé da página. A intenção era colocar os dados da empresa que
 * atualmente licencia o software e evitar alterações de JSP caso 
 * algum dado da empresa mude. Assim, as alterações realizadas no
 * cadastro seriam automaticamente refletidas no rodapé.
 */
@ManagedBean
@SessionScoped
public class FooterBean extends BeanSessionBasic
{
	public static final String LINHA1="linha1";
	public static final String LINHA2="linha2";
	public static final String LINHA3="linha3";
	public static final String LINHA4="linha4";
	
	private String linha1 = "";
	private String linha2 = "";
	private String linha3 = "";
	private String linha4 = "";
	
	public String getLinha1()
	{
		return linha1;
	}
	public String getLinha2()
	{
		return linha2;
	}
	public String getLinha3()
	{
		return linha3;
	}
	public String getLinha4()
	{
		return linha4;
	}
	public void doReset() throws BusinessException, Exception
	{
		// TODO Auto-generated method stub
		
	}
	public void doReload() throws BusinessException, Exception
	{
		// TODO Auto-generated method stub
		
	}
	
}
