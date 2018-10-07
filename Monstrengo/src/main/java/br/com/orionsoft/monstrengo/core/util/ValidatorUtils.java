package br.com.orionsoft.monstrengo.core.util;

import org.apache.commons.lang.StringUtils;

/**
 * Esta classe possui os geradores de digitos de CPF e CNPJ. No entanto, seria interessante
 * definir uma interface PropertyValidator que outras classe pudessem implementar e dentro dos metadados
 * pudesse referenciar estes validadores.
 * Para ficar um mecanismo dinâmico, poderia sempre fornecer para esta classe validadora a instância
 * da entidade e o nome da propriedade que se deseja validar. Desta forma, propriedades co-relacionadas
 * poderiam ser validadas como DataNascimento maior que hoje, data de abertura do contrato antes da data 
 * de inicio de atividade de um empresa.
 * Tambem estah em mente, toda entidade talvez uma classe validadora. Esta classe seria responsavel por
 * fazer todas as validaçoes de uma entidade.
 * 
 * @author lucio
 *
 */
public class ValidatorUtils {


	/**
	 * Verifica se o CPF é válido calculando o digito e comparando com o informado. 
	 * @param cpf
	 * @return
	 */
	public static boolean validarCPF(String cpf){
		 final String dv = StringUtils.right(cpf, 2);
		 final String numCpf = StringUtils.left(cpf, cpf.length()- 2);
		 
		 return dv.equals(dvCPF(numCpf));
	}
	
	/** Calcula o Dígito verificador de um CPF passado sem o dígito.
     *
     * @param   strCPF número de CPF a ser validado
     * @return  DV do CPF
     */
    public static String dvCPF (String cpf) {
        int     d1, d2;
        int     digito1, digito2, resto;
        int     digitoCPF;
        String  nDigResult;
        
        d1 = d2 = 0;
        digito1 = digito2 = resto = 0;
        
       // System.out.println(cpf.length());
        for (int nCount = 0; nCount < cpf.length(); nCount++)
        {
            //System.out.println(cpf.substring(nCount, nCount + 1));
            digitoCPF = Integer.valueOf(cpf.substring(nCount, nCount + 1)).intValue();
            //digitoCPF = Integer.valueOf (strCpf).intValue();
            
            //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
            d1 = d1 + ( 10 - nCount ) * digitoCPF;
            
            //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
            d2 = d2 + ( 11 - nCount ) * digitoCPF;
        };
        
        //Primeiro resto da divisão por 11.
        resto = (d1 % 11);
        
        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
        if (resto < 2)
            digito1 = 0;
        else
            digito1 = 11 - resto;
        
        d2 += 2 * digito1;
        
        //Segundo resto da divisão por 11.
        resto = (d2 % 11);
        
        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
        if (resto < 2)
            digito2 = 0;
        else
            digito2 = 11 - resto;
        
        //Digito verificador do CPF que está sendo validado.
        //String nDigVerific = strCpf.substring (strCpf.length()-2, strCpf.length());
        
        //Concatenando o primeiro resto com o segundo.
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
        
        return nDigResult;
    }

	/**
	 * Verifica se o CNPJ é válido calculando o digito e comparando com o informado. 
	 * @param cnpj
	 * @return
	 */
	public static boolean validarCNPJ(String cnpj){
		 final String dv = StringUtils.right(cnpj, 2);
		 final String numCnpj = StringUtils.left(cnpj, cnpj.length()- 2);
		 
		 return dv.equals(dvCNPJ(numCnpj));
	}
	
    /** Realiza a validação do CNPJ.
     *
     * @param   strCNPJ número de CNPJ a ser validado
     * @return  DV do CNPJ
     */
    public static String dvCNPJ(String cnpj)
    {
        int     d1, d2;
        int     digito1, digito2, resto;
        int     digitoCNPJ;
        String  nDigResult;
        
        d1 = d2 = 0;
        digito1 = digito2 = resto = 0;
        
        for (int nCount = 0; nCount < cnpj.length(); nCount++)
        {
            digitoCNPJ = Integer.valueOf(cnpj.substring(nCount, nCount + 1)).intValue();
            
            if (nCount < 4){
                d1 = d1 + (5 - nCount) * digitoCNPJ;
                d2 = d2 + (6 - nCount) * digitoCNPJ;
            }else{
                d1 = d1 + (13 - nCount ) * digitoCNPJ;
                if (nCount == 4)
                    d2 = d2 + (6 - nCount) * digitoCNPJ;
                else
                    d2 = d2 + (14 - nCount) * digitoCNPJ;
            }
        }
        
        //Primeiro resto da divisão por 11.
        resto = (d1 % 11);
        
        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
        if (resto < 2)
            digito1 = 0;
        else
            digito1 = 11 - resto;
        
        d2 += 2 * digito1;
        
        //Segundo resto da divisão por 11.
        resto = (d2 % 11);
        
        //Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
        if (resto < 2)
            digito2 = 0;
        else
            digito2 = 11 - resto;
        
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
        
        return nDigResult;
    }
}