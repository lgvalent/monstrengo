package br.com.orionsoft.financeiro.utils;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.entities.endereco.Endereco;


public class UtilsRemessa {

	/**
	 * Este método verifica a quantidade limite de caracteres que podem ser inseridos no campo 
	 * Endereço de um Título. Por exemplo, para o arquivo de remessa, o número máximo de caracteres
	 * pode ser 40, enquanto para um Título impresso diretamente pelo sistema, este valor pode ser 80.
	 * A string retornada irá conter o nome do logradouro + número + complemento
	 * 
	 * @param endereco - objeto que contém as informações completas do endereço da pessoa
	 * @param maxCaracteres - O número máximo de caracteres que devem ser retornados
	 * @return - Uma string contendo o endereço formatado de acordo com o número máximo de caracteres
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
	
	
//		//verificando se o endereço completo nao cabe no número máximo estipulado
//		if (endereco.length() > maxCaracteres){
//			List<String> tokens = new ArrayList<String>();
//			int countTokens = 0;
//			
//			//Separando os tokens --> delimitador "," (ponto-e-vírgula)
//			StringTokenizer tokenizer = new StringTokenizer(endereco, ",");
//			while(tokenizer.hasMoreTokens() && countTokens<MAX_TOKENS){
//				tokens.add(tokenizer.nextToken());
//				countTokens++;
//			}
//			
//			/*
//			 * Obtemos todos os tokens possíveis
//			 * Possivelmente teremos o endereço no 1o token e o número no 2o token e o complemento no 3o token
//			 * Mesmo se existirem mais tokens, o ENDEREÇO DEVE SER O PRIMEIRO TOKEN
//			 */
//			List<String> enderecos = new ArrayList<String>();
//			countTokens = 0;
//			
//			//Separando os tokens para o endereço - delimitador --> " " (espaço)
//			tokenizer = new StringTokenizer(tokens.get(0)); //ENDEREÇO DEVE SER O PRIMEIRO TOKEN
//			while(tokenizer.hasMoreTokens() && countTokens<MAX_TOKENS){
//				enderecos.add(tokenizer.nextToken());
//				countTokens++;
//			}
//			
//			//montando o endereço final
//			String result = StringUtils.repeat(" ", 100);
//			int iteracao = 1; //serve para decrementar a quantidade de nomes do endereço
//
//			/*
//			 * O procedimento é o seguinte:
//			 * - Primeiro, através do "for", monta uma string com todos os tokens do endereço e verifica
//			 * se "cabe" no máximo estipulado
//			 * - Se não couber, vai decrementando a "iteracao", fazendo com que cada vez que o "for" é executado 
//			 * novamente, é pego um token a menos, e o teste de tamanho é feito novamente
//			 */
//			while(result.length() > maxCaracteres){
//				result = "";
//				countTokens = 0;
//				for (int cont = enderecos.size()-iteracao; cont>=0; cont--){
//					result += enderecos.get(countTokens);
//					countTokens++;
//					if (cont==0) //se for o último token, usa vírgula para separar o endereço dos demais valores seguintes
//						result += "...,";
//					else
//						result += " "; //se não for o último token, separa os nomes do endereço com um espaço
//				}
//				result = result + tokens.get(1) + "," + tokens.get(2); //montando a string com endereço, número e complemento
//				iteracao++;
//			}
//			
//			return result;
//			
//		}else{ //se já couber, retorna o próprio endereço
//			return endereco;
//		}
//		
//	}
	
}
