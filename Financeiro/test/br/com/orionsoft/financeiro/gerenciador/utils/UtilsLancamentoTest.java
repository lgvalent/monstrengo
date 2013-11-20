package br.com.orionsoft.financeiro.gerenciador.utils;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabil;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.Operacao;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class UtilsLancamentoTest extends ServiceBasicTest {

//	@Test
	public void inserir() throws BusinessException {
		Lancamento lancamento = UtilsLancamento.inserir(
				serviceManager, 
				(Conta)UtilsCrud.objectRetrieve(serviceManager, Conta.class, 1l, null), 
				(Contrato)UtilsCrud.objectRetrieve(serviceManager, Contrato.class, 1l, null), 
				CalendarUtils.getCalendar(2007, Calendar.SEPTEMBER, 4), 
				CalendarUtils.getCalendar(2007, Calendar.SEPTEMBER, 30), 
				"UtilsLancamentoTest", 
				(Operacao)UtilsCrud.objectRetrieve(serviceManager, Operacao.class, 1l, null), 
				Transacao.CREDITO, 
				(CentroCusto)UtilsCrud.objectRetrieve(serviceManager, CentroCusto.class, 1l, null), 
				(ClassificacaoContabil)UtilsCrud.objectRetrieve(serviceManager, ClassificacaoContabil.class, 1l, null), 
				(ItemCusto)UtilsCrud.objectRetrieve(serviceManager, ItemCusto.class, 1l, null),
				DecimalUtils.TEN, 
				(DocumentoCobrancaCategoria)UtilsCrud.objectRetrieve(serviceManager, DocumentoCobrancaCategoria.class, 1l, null),
				false,
				null);
		assertNotNull(lancamento);
	}

//	@Test
	public void inserirComLista() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.SEPTEMBER, 4);
		Calendar dataVencimento = CalendarUtils.getCalendar(2007, Calendar.SEPTEMBER, 30);
		List<LancamentoItem> lancamentoItemList = new ArrayList<LancamentoItem>(0); 
		LancamentoItem lancamentoItem = UtilsCrud.objectCreate(serviceManager, LancamentoItem.class, null);
		lancamentoItem.setDataLancamento(data);
		lancamentoItem.setDataCompetencia(dataVencimento);
		lancamentoItem.setCentroCusto((CentroCusto)UtilsCrud.objectRetrieve(serviceManager, CentroCusto.class, 1l, null));
		lancamentoItem.setClassificacaoContabil((ClassificacaoContabil)UtilsCrud.objectRetrieve(serviceManager, ClassificacaoContabil.class, 1l, null));
		lancamentoItem.setItemCusto((ItemCusto)UtilsCrud.objectRetrieve(serviceManager, ItemCusto.class, 1l, null));
		lancamentoItem.setValor(DecimalUtils.TEN);
		lancamentoItemList.add(lancamentoItem);
		
		Lancamento lancamento = UtilsLancamento.inserir(
				serviceManager, 
				(Conta)UtilsCrud.objectRetrieve(serviceManager, Conta.class, 1l, null), 
				(Contrato)UtilsCrud.objectRetrieve(serviceManager, Contrato.class, 1l, null), 
				data, 
				dataVencimento, 
				"UtilsLancamentoTest", 
				(Operacao)UtilsCrud.objectRetrieve(serviceManager, Operacao.class, 1l, null), 
				Transacao.CREDITO,
				lancamentoItemList,
				(DocumentoCobrancaCategoria)UtilsCrud.objectRetrieve(serviceManager, DocumentoCobrancaCategoria.class, 1l, null),
				false,
				null);
		assertNotNull(lancamento);
	}
	
//	@Test
	public void imprimirDocumento() throws IOException, BusinessException {
		Lancamento lancamento = UtilsLancamento.inserir(
				serviceManager, 
				(Conta)UtilsCrud.objectRetrieve(serviceManager, Conta.class, 1l, null), 
				(Contrato)UtilsCrud.objectRetrieve(serviceManager, Contrato.class, 1l, null), 
				CalendarUtils.getCalendar(2007, Calendar.SEPTEMBER, 20), 
				CalendarUtils.getCalendar(2007, Calendar.SEPTEMBER, 22), 
				"UtilsLancamentoTest", 
				(Operacao)UtilsCrud.objectRetrieve(serviceManager, Operacao.class, 1l, null), 
				Transacao.CREDITO, 
				(CentroCusto)UtilsCrud.objectRetrieve(serviceManager, CentroCusto.class, 1l, null), 
				(ClassificacaoContabil)UtilsCrud.objectRetrieve(serviceManager, ClassificacaoContabil.class, 1l, null), 
				(ItemCusto)UtilsCrud.objectRetrieve(serviceManager, ItemCusto.class, 1l, null),
				DecimalUtils.getBigDecimal(100.00), 
				(DocumentoCobrancaCategoria)UtilsCrud.objectRetrieve(serviceManager, DocumentoCobrancaCategoria.class, 1l, null),
				false,
				null);
		
		String nomeArquivo = "../../impressaoDoDocumentoCobranca.pdf";
		OutputStream out = new FileOutputStream(nomeArquivo);
		
		UtilsLancamento.imprimirDocumentoCobranca(serviceManager, lancamento, out, PrintUtils.PRINTER_INDEX_NO_PRINT ,null);
		
		Runtime run = Runtime.getRuntime();
		run.exec("acroread "+nomeArquivo);
	}
	
	@Test
	public void verificarPendenciasFinanceirasVencidas() throws BusinessException {
		Contrato contrato = UtilsCrud.objectRetrieve(serviceManager, Contrato.class, 1l, null);
		Calendar dataLimite = CalendarUtils.getCalendar();
//		List<ItemCusto> itemCustoList = UtilsCrud.objectList(serviceManager, ItemCusto.class, null);
		assertTrue(UtilsLancamento.verificarPendenciasFinanceirasVencidas(serviceManager, contrato.getPessoa(), dataLimite, null, null));
	}
}
