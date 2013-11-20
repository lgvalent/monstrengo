package br.com.orionsoft.financeiro.gerenciador.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCadastroService.TipoRelatorio;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

public class RelatorioCadastroServiceTest extends ServiceBasicTest {

//	@Test
	public void testCampo() {
		Integer[] index = {1, 2, 4};
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < index.length; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(index[i]);
		}
		System.out.println(builder);
//		System.out.println(Campo.toString(index));
	}

	@Test
	public void test() throws ServiceException, FileNotFoundException {
		File file = new File("/home/juliana/Desktop/relatorio_cadastro.pdf");
		OutputStream stream = new FileOutputStream(file);
		
		ServiceData sd = new ServiceData(RelatorioCadastroService.SERVICE_NAME, null);
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_AGRUPAMENTO, RelatorioCadastroService.Agrupamento.CNAE);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_CAMPO_LIST, new Integer[]{0,1,2,3,4,5,6,7,8,9,10});
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_CNAE_ID, 1l);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_CNAE_ID, null);
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_CPF_CNPJ, "75317701000158");
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_CPF_CNPJ, null);
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_CPF_CNPJ, "02332390000122");
		/* Observação: o formato da data dado pelo CalendarUtils = 'YYYY/MM/DD' não é compativel pelo usado pelo MySql*/
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_COMECO_ATIVIDADE_INICIO, "01/01/1990");
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_COMECO_ATIVIDADE_INICIO, null);
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_COMECO_ATIVIDADE_INICIO, "1990-01-01");
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_COMECO_ATIVIDADE_FIM, "31/12/2005");
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_COMECO_ATIVIDADE_FIM, null);
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_COMECO_ATIVIDADE_FIM, "2005-12-31");
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_CONTRATO_INICIO, "01/01/1990");
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_CONTRATO_INICIO, "1990-01-01");
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_CONTRATO_FIM, "31/12/2005");
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_CONTRATO_FIM, "2005-12-31");
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_CONTRATO_RESCISAO_INICIO, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_CONTRATO_RESCISAO_FIM, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_INICIO, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_FIM, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_RECEBIMENTO_INICIO, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_RECEBIMENTO_FIM, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_VENCIMENTO_INICIO, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_VENCIMENTO_FIM, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_TERMINO_ATIVIDADE_INICIO, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_TERMINO_ATIVIDADE_FIM, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_ESCRITORIO_CONTABIL_ID, 7l);
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_ESCRITORIO_CONTABIL_ID, 1l);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_INATIVOS, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_ITEM_CUSTO_ID_LIST, new Long[]{new Long(0),new Long(1),new Long(2)});
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_ITEM_CUSTO_ID_LIST, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_MUNICIPIO_ID, 6l);
		/* (01/04/09)A ordenacao passa a ser pelo campo contido do enum Campo, pois a ordenação pode ser
		 * feita apenas pelos elementos que foram selecionados para serem exibidos */
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_ORDENACAO, new Integer(9));
//		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_ORDENACAO, RelatorioCadastroService.Ordenacao.TIPO_ESTABELECIMENTO);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_REPRESENTANTE_ID, null);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_TIPO_ESTABELECIMENTO, RelatorioCadastroService.TipoEstabelecimento.TODOS);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_TIPO_RELATORIO, TipoRelatorio.LISTAGEM);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_MOVIMENTACOES, true);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_CONTRATO_CATEGORIA_ID, IDAO.ENTITY_UNSAVED);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_OUTPUT_STREAM, stream);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_CNAE_ID, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_CONTRATO_CATEGORIA_ID, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_ESCRITORIO_CONTABIL_ID, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_MUNICIPIO_ID, true);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_REPRESENTANTE_ID, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_TIPO_ESTABELECIMENTO_ID, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATAS_CADASTRO, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_COMECO_ATIVIDADE, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_CONTRATO, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_CONTRATO_RESCISAO, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_LANCAMENTO, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_LANCAMENTO_RECEBIMENTO, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_LANCAMENTO_VENCIMENTO, false);
		sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_TERMINO_ATIVIDADE, false);
		
		this.serviceManager.execute(sd);
	}
}
