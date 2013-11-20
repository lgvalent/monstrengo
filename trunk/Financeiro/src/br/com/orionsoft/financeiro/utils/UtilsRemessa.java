package br.com.orionsoft.financeiro.utils;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.entities.endereco.Endereco;


public class UtilsRemessa {

	/**
	 * Este m�todo verifica a quantidade limite de caracteres que podem ser inseridos no campo 
	 * Endere�o de um T�tulo. Por exemplo, para o arquivo de remessa, o n�mero m�ximo de caracteres
	 * pode ser 40, enquanto para um T�tulo impresso diretamente pelo sistema, este valor pode ser 80.
	 * A string retornada ir� conter o nome do logradouro + n�mero + complemento
	 * 
	 * @param endereco - objeto que cont�m as informa��es completas do endere�o da pessoa
	 * @param maxCaracteres - O n�mero m�ximo de caracteres que devem ser retornados
	 * @return - Uma string contendo o endere�o formatado de acordo com o n�mero m�ximo de caracteres
	 */
	public static String formatarEndereco(Endereco endereco, int maxCaracteres){
		String complemento = "";
		if (StringUtils.isNotBlank(endereco.getComplemento()))
			complemento = " - " + endereco.getComplemento(); 	
				
		String numero = ", " + endereco.getNumero();
		String logradouro = endereco.getLogradouro().getNome();
		
		if ((logradouro+numero+complemento).length() > maxCaracteres)
			logradouro = br.com.orionsoft.monstrengo.core.util.StringUtils.formatAlpha(logradouro, maxCaracteres-numero.length()-complemento.length(),false);
		
		return logradouro+numero+complemento;
	}
	
	
//		//verificando se o endere�o completo nao cabe no n�mero m�ximo estipulado
//		if (endereco.length() > maxCaracteres){
//			List<String> tokens = new ArrayList<String>();
//			int countTokens = 0;
//			
//			//Separando os tokens --> delimitador "," (ponto-e-v�rgula)
//			StringTokenizer tokenizer = new StringTokenizer(endereco, ",");
//			while(tokenizer.hasMoreTokens() && countTokens<MAX_TOKENS){
//				tokens.add(tokenizer.nextToken());
//				countTokens++;
//			}
//			
//			/*
//			 * Obtemos todos os tokens poss�veis
//			 * Possivelmente teremos o endere�o no 1o token e o n�mero no 2o token e o complemento no 3o token
//			 * Mesmo se existirem mais tokens, o ENDERE�O DEVE SER O PRIMEIRO TOKEN
//			 */
//			List<String> enderecos = new ArrayList<String>();
//			countTokens = 0;
//			
//			//Separando os tokens para o endere�o - delimitador --> " " (espa�o)
//			tokenizer = new StringTokenizer(tokens.get(0)); //ENDERE�O DEVE SER O PRIMEIRO TOKEN
//			while(tokenizer.hasMoreTokens() && countTokens<MAX_TOKENS){
//				enderecos.add(tokenizer.nextToken());
//				countTokens++;
//			}
//			
//			//montando o endere�o final
//			String result = StringUtils.repeat(" ", 100);
//			int iteracao = 1; //serve para decrementar a quantidade de nomes do endere�o
//
//			/*
//			 * O procedimento � o seguinte:
//			 * - Primeiro, atrav�s do "for", monta uma string com todos os tokens do endere�o e verifica
//			 * se "cabe" no m�ximo estipulado
//			 * - Se n�o couber, vai decrementando a "iteracao", fazendo com que cada vez que o "for" � executado 
//			 * novamente, � pego um token a menos, e o teste de tamanho � feito novamente
//			 */
//			while(result.length() > maxCaracteres){
//				result = "";
//				countTokens = 0;
//				for (int cont = enderecos.size()-iteracao; cont>=0; cont--){
//					result += enderecos.get(countTokens);
//					countTokens++;
//					if (cont==0) //se for o �ltimo token, usa v�rgula para separar o endere�o dos demais valores seguintes
//						result += "...,";
//					else
//						result += " "; //se n�o for o �ltimo token, separa os nomes do endere�o com um espa�o
//				}
//				result = result + tokens.get(1) + "," + tokens.get(2); //montando a string com endere�o, n�mero e complemento
//				iteracao++;
//			}
//			
//			return result;
//			
//		}else{ //se j� couber, retorna o pr�prio endere�o
//			return endereco;
//		}
//		
//	}
	
}
