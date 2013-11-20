package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.services.CriarDocumentoCobrancaService;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabil;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.Operacao;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class InserirLancamentoServiceTest extends ServiceBasicTest {
	private Lancamento lancamento;
	
//	@Test
	public void inserir() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar();
		Calendar dataVencimento = CalendarUtils.getCalendar(2008, Calendar.JUNE, 30);
		String descricao = "InserirLancamentoServiceTest";		
		Transacao transacao = Transacao.DEBITO;
		BigDecimal valorTotal = DecimalUtils.getBigDecimal(30.00);
		BigDecimal valorItem = DecimalUtils.getBigDecimal(10.00);
		
		CentroCusto centroCusto = UtilsCrud.objectRetrieve(this.serviceManager, CentroCusto.class, 1l, null);
		ClassificacaoContabil classificacaoContabil = UtilsCrud.objectRetrieve(this.serviceManager, ClassificacaoContabil.class, 1l, null);
		Conta contaPrevista = UtilsCrud.objectRetrieve(this.serviceManager, Conta.class, 1l, null);
		Operacao operacao = UtilsCrud.objectRetrieve(this.serviceManager, Operacao.class, 1l, null);
		Contrato contrato = UtilsCrud.objectRetrieve(this.serviceManager, Contrato.class, 1l, null);
		List<ItemCusto> itemCustoList = UtilsCrud.objectList(this.serviceManager, ItemCusto.class, null);

		List<LancamentoItem> lancamentoItemList = new ArrayList<LancamentoItem>(0); 
		for (ItemCusto itemCusto : itemCustoList) {
			LancamentoItem lancamentoItem = UtilsCrud.objectCreate(this.serviceManager, LancamentoItem.class, null);
			lancamentoItem.setDataLancamento(data);
			lancamentoItem.setDataCompetencia(dataVencimento);
			lancamentoItem.setCentroCusto(centroCusto);
			lancamentoItem.setClassificacaoContabil(classificacaoContabil);
			lancamentoItem.setItemCusto(itemCusto);
			lancamentoItem.setValor(valorItem);
			lancamentoItemList.add(lancamentoItem);
		}
		
		ServiceData sd = new ServiceData(InserirLancamentoService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_CONTA_PREVISTA_OPT, contaPrevista);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_CONTRATO, contrato);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_DATA, data);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_DATA_VENCIMENTO, dataVencimento);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_DESCRICAO_OPT, descricao);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_LANCAMENTO_ITEM_LIST, lancamentoItemList);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_OPERACAO, operacao);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_TRANSACAO, transacao);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_NAO_RECEBER_APOS_VENCIMENTO, true);
		this.serviceManager.execute(sd);
		lancamento = sd.getFirstOutput();
		Assert.assertNotNull(lancamento);
		
		lancamento = UtilsCrud.objectRetrieve(this.serviceManager, Lancamento.class, lancamento.getId(), null);
		Assert.assertEquals(lancamento.getContaPrevista(), contaPrevista);
		Assert.assertEquals(lancamento.getContrato().toString(), contrato.toString());
		Assert.assertEquals(lancamento.getData(), data);
		Assert.assertEquals(lancamento.getDataVencimento(), dataVencimento);
		Assert.assertEquals(lancamento.getDescricao(), descricao);
		Assert.assertEquals(lancamento.getOperacao(), operacao);
		Assert.assertEquals(lancamento.getSaldo(), valorTotal);
		Assert.assertEquals(lancamento.getValor(), valorTotal);
	}

	@Test
	public void testComCriarDocumento() throws BusinessException {
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.JULY, 05);
		Calendar dataVencimento = CalendarUtils.getCalendar(2007, Calendar.JULY, 30);
		String descricao = "InserirLancamentoServiceTest";		
		Transacao transacao = Transacao.CREDITO;
		BigDecimal valorTotal = DecimalUtils.getBigDecimal(100.00);
		BigDecimal valorItem = DecimalUtils.getBigDecimal(10.00);
		
		CentroCusto centroCusto = UtilsCrud.objectRetrieve(this.serviceManager, CentroCusto.class, 1l, null);
		ClassificacaoContabil classificacaoContabil = UtilsCrud.objectRetrieve(this.serviceManager, ClassificacaoContabil.class, 1l, null);
		List<ItemCusto> itemCustoList = UtilsCrud.objectList(this.serviceManager, ItemCusto.class, null);

		IEntity contaPrevista = UtilsCrud.retrieve(this.serviceManager, Conta.class, 1l, null);
//		IEntity operacao = UtilsCrud.retrieve(this.serviceManager, Operacao.class, 1l, null);
		IEntity contrato = UtilsCrud.retrieve(this.serviceManager, Contrato.class, 1l, null);

		List<LancamentoItem> lancamentoItemList = new ArrayList<LancamentoItem>(0); 
		for (ItemCusto itemCusto : itemCustoList) {
			LancamentoItem lancamentoItem = UtilsCrud.objectCreate(this.serviceManager, LancamentoItem.class, null);
			lancamentoItem.setCentroCusto(centroCusto);
			lancamentoItem.setClassificacaoContabil(classificacaoContabil);
			lancamentoItem.setItemCusto(itemCusto);
			lancamentoItem.setValor(valorItem);
			lancamentoItemList.add(lancamentoItem);
		}
		
		ServiceData sdCriarDocumento = new ServiceData(CriarDocumentoCobrancaService.SERVICE_NAME, null);
		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_CONTRATO, contrato);
		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_DOCUMENTO, data);
		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_VENCIMENTO, dataVencimento);
		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DOCUMENTO_COBRANCA_CATEGORIA, UtilsCrud.retrieve(this.serviceManager, DocumentoCobrancaCategoria.class, 1, null));
		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_TRANSACAO, transacao);
		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_VALOR_DOCUMENTO, valorTotal);
		this.serviceManager.execute(sdCriarDocumento);
		
		IEntity documentoCobranca = sdCriarDocumento.getFirstOutput(); 
		
		ServiceData sd = new ServiceData(InserirLancamentoService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_CONTA_PREVISTA_OPT, contaPrevista.getObject());
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_CONTRATO, contrato.getObject());
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_DATA, data);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_DATA_VENCIMENTO, dataVencimento);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_DESCRICAO_OPT, descricao);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_LANCAMENTO_ITEM_LIST, lancamentoItemList);
//		sd.getArgumentList().setProperty(InserirLancamentoService.IN_OPERACAO, operacao.getObject());
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_TRANSACAO, transacao);
		sd.getArgumentList().setProperty(InserirLancamentoService.IN_DOCUMENTO_COBRANCA_OPT, documentoCobranca.getObject());
		this.serviceManager.execute(sd);
		Lancamento lancamentoO = sd.getFirstOutput();
		Assert.assertNotNull(lancamentoO);
		
		IEntity lancamento = UtilsCrud.retrieve(this.serviceManager, Lancamento.class, lancamentoO.getId(), null);
		Assert.assertEquals(lancamento.getPropertyValue(Lancamento.CONTA_PREVISTA).toString(), contaPrevista.toString());
		Assert.assertEquals(lancamento.getPropertyValue(Lancamento.CONTRATO).toString(), contrato.toString());
		Assert.assertEquals(lancamento.getPropertyValue(Lancamento.DATA), data);
		Assert.assertEquals(lancamento.getPropertyValue(Lancamento.DATA_VENCIMENTO), dataVencimento);
		Assert.assertEquals(lancamento.getPropertyValue(Lancamento.DESCRICAO), descricao);
//		Assert.assertEquals(lancamento.getPropertyValue(Lancamento.OPERACAO).toString(), operacao.toString());
		Assert.assertEquals(lancamento.getPropertyValue(Lancamento.SALDO), valorTotal);
		Assert.assertEquals(lancamento.getPropertyValue(Lancamento.VALOR), valorTotal);
	}

	public Lancamento getLancamento() {
		return lancamento;
	}
}
