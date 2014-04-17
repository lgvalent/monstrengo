package br.com.orionsoft.financeiro.utils;

import java.util.ArrayList;
import java.util.List;

import br.com.orionsoft.financeiro.documento.cobranca.titulo.Ocorrencia;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.banco748.Gerenciador748;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.QueryCondiction;
import br.com.orionsoft.monstrengo.crud.services.QueryService;

public class UtilsOcorrencia {
    
	public static IEntity<Ocorrencia> obterOcorrencia(IServiceManager serviceManager, int codigo, ServiceData serviceDataOwner) throws BusinessException{
		List<QueryCondiction> condictions = new ArrayList<QueryCondiction>();
		
		condictions.add(new QueryCondiction(serviceManager.getEntityManager(),
				Ocorrencia.class,
				Ocorrencia.CODIGO,
				Operator.EQUAL,
				String.valueOf(codigo),
				""));
		
		ServiceData sdQuery = new ServiceData(QueryService.SERVICE_NAME, serviceDataOwner);
		sdQuery.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, Ocorrencia.class);
		sdQuery.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, condictions);
		serviceManager.execute(sdQuery);
		
		IEntityList<Ocorrencia> entityList = (IEntityList<Ocorrencia>) sdQuery.getOutputData(QueryService.OUT_ENTITY_LIST);
		
		if (entityList.isEmpty())
			return null;
		
		return entityList.getFirst();
	}
	
	/**
	 * Este método permite que cada gerenciador converta a ocorrência de RETORNO na lista padrão que já faz parte do sistema
	 * 
	 * @param serviceManager
	 * @param codigoNativoBanco
	 * @param mapOcorrencias vetor de pares de correspondência entre o código da correspondência do Sistema com a do Banco
	 * @param serviceDataOwner
	 * @return
	 * @throws BusinessException
	 */
	public static IEntity<Ocorrencia> obterOcorrencia(IServiceManager serviceManager, int codigoNativoBanco, int[] mapOcorrencias, ServiceData serviceDataOwner) throws BusinessException{
        for(int i=1; i < mapOcorrencias.length; i+=2){
        	if(mapOcorrencias[i] == codigoNativoBanco)
        		return obterOcorrencia(serviceManager, mapOcorrencias[i-1], serviceDataOwner);
        }

		throw new BusinessException(MessageList.createSingleInternalError(new Exception("Código de ocorrência não mapeado para o sistema: " + codigoNativoBanco)));
	}
    
	/**
	 * Este método permite que cada gerenciador converta a Instrução do Sistema em uma Instrução para a REMESSA nativa do BANCO
	 * 
	 * @param serviceManager
	 * @param codigoNativoBanco
	 * @param mapOcorrencias vetor de pares de correspondência entre o código da correspondência do Sistema com a do Banco
	 * @param serviceDataOwner
	 * @return
	 * @throws BusinessException
	 */
	public static int obterInstrucaoBanco(int codigoInstrucaoSistema, int[] mapInstrucoes) throws BusinessException{
        for(int i=0; i < mapInstrucoes.length; i+=2){
        	if(mapInstrucoes[i] == codigoInstrucaoSistema)
        		return mapInstrucoes[i+1];
        }

		throw new BusinessException(MessageList.createSingleInternalError(new Exception("Código de Instrução não mapeado para a REMESSA do banco: " + codigoInstrucaoSistema)));
	}
	
	/**
	 * Gera uma lista separada por vírgula com os ID
	 * do sistema para as instruções de REMESSA para ser usada em query com Operator.IN
	 * 
	 * @param serviceManager
	 * @param codigoNativoBanco
	 * @param mapOcorrencias vetor de pares de correspondência entre o código da correspondência do Sistema com a do Banco
	 * @param serviceDataOwner
	 * @return
	 * @throws BusinessException
	 */
	public static String gerarListaInstrucaoRemessa(int[] mapInstrucoes){
        StringBuilder sb = new StringBuilder();
		for(int i=0; i < mapInstrucoes.length; i+=2){
			sb.append('\'');
			sb.append(mapInstrucoes[i]);
			sb.append('\'');
			sb.append(',');
        }
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
	
	public static void main(String[] args){
		System.out.println(gerarListaInstrucaoRemessa(Gerenciador748.MAPA_INSTRUCOES)); 
	}
    
}
