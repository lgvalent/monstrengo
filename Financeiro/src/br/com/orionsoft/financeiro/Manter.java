package br.com.orionsoft.financeiro;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.Cedente;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.DocumentoTitulo;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.Ocorrencia;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.OcorrenciaControle;
import br.com.orionsoft.financeiro.documento.pagamento.ConvenioPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoCategoria;
import br.com.orionsoft.financeiro.documento.pagamento.cheque.ChequeModelo;
import br.com.orionsoft.financeiro.documento.pagamento.cheque.ConvenioCheque;
import br.com.orionsoft.financeiro.documento.pagamento.cheque.DocumentoCheque;
import br.com.orionsoft.financeiro.documento.pagamento.dinheiro.DocumentoDinheiro;
import br.com.orionsoft.financeiro.gerenciador.entities.Banco;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabil;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabilCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ContaBancaria;
import br.com.orionsoft.financeiro.gerenciador.entities.ContaCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.ContratoFinanceiro;
import br.com.orionsoft.financeiro.gerenciador.entities.ContratoFinanceiroCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.ContratoFinanceiroDescontoAcrescimo;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.Operacao;
import br.com.orionsoft.financeiro.gerenciador.entities.agendamento.Agendamento;
import br.com.orionsoft.financeiro.gerenciador.entities.agendamento.AgendamentoItem;
import br.com.orionsoft.monstrengo.ManterBasic;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.EntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntitySet;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/*

Perguntas para todas as classes
-------------------------------

- Persistir sem pesquisar no BD?
- Se for pesquisar, que critério usar?
- Como lidar com dependências circulares?
	- Como saber em qual método persistir e em qual método ignorar o campo? 
	- Existe algum caminho a seguir no diagrama de classes?
- Enums não precisam ser tratados? Eles são tratados por manterPrimitiveProperties?
- Tenho que manter todos os campos de classes que são subclasses? 


Lista de entidades geradas pelo GerarListaEntidades
---------------------------------------------------

#	br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca
		usando "nome" como critério
?	br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca
		não existe número do documento, quê critério usar?
#	br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria
		usando "nome" como critério
		manter contaPadrao e convenioCobranca?
?	br.com.orionsoft.financeiro.documento.cobranca.titulo.Cedente
		usar cedenteCodigo, cedenteDigito ou nome? usando "nome" atualmente
		extends ConvenioCobranca - manter todos os campos de ConvenioCobranca?
?	br.com.orionsoft.financeiro.documento.cobranca.titulo.DocumentoTitulo
		extends DocumentoCobranca
#	br.com.orionsoft.financeiro.documento.cobranca.titulo.Ocorrencia
		codigo? ocorrencia.getCodigo() == -1 ?
	br.com.orionsoft.financeiro.documento.cobranca.titulo.OcorrenciaControle
		qual critério: titulo : DocumentoTitulo, ocorrencia : Ocorrencia?
#	br.com.orionsoft.financeiro.documento.pagamento.ConvenioPagamento
		pessoas jurídicas: contratante, contratado? usar como critério? fazer a persistência delas?
?	br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento
		não existe número do documento
|	br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoCategoria
#	br.com.orionsoft.financeiro.documento.pagamento.cheque.ChequeModelo
		critério: nome?
	br.com.orionsoft.financeiro.documento.pagamento.cheque.ConvenioCheque
	br.com.orionsoft.financeiro.documento.pagamento.cheque.DocumentoCheque
#	br.com.orionsoft.financeiro.documento.pagamento.dinheiro.DocumentoDinheiro
		classe sem atributos
#	br.com.orionsoft.financeiro.gerenciador.entities.Banco
		comparar com codigo, digito ou nome? usando nome atualmente
#	br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto
#	br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabil
#	br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabilCategoria
#	br.com.orionsoft.financeiro.gerenciador.entities.Conta
	br.com.orionsoft.financeiro.gerenciador.entities.ContaBancaria
		agencia{codigo,digito}, conta{codigo,digito}, banco?
#	br.com.orionsoft.financeiro.gerenciador.entities.ContaCategoria
#	br.com.orionsoft.financeiro.gerenciador.entities.ContratoFinanceiro
		extends Contrato
#	br.com.orionsoft.financeiro.gerenciador.entities.ContratoFinanceiroCategoria
		classe vazia, extends ContratoCategoria
#	br.com.orionsoft.financeiro.gerenciador.entities.ContratoFinanceiroDescontoAcrescimo
		utilizar contratoFinanceiro como critério? atualmente está sempre persistindo quando chega
#	br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto
#	br.com.orionsoft.financeiro.gerenciador.entities.Lancamento
		que critério usar? contrato, conta prevista, documento de cobrança, de pagamento?
#	br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem
		LancamentoItem se relaciona com Lancamento, mas não iremos tratar isso, né? Pois já foi tratado em Lancamento.
		Não se preocupar em pesquisar? Chegou, grava? 
#	br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento
		chegou, persiste?
		se relaciona com lancamento, ignorar?
		conta: persiste através de lancamento, ou lancamento movimentos?
		transferencia : LancamentoMovimento: o método vai chamar ele mesmo para essa entidade?
-	br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria
		enum
#	br.com.orionsoft.financeiro.gerenciador.entities.Operacao
	br.com.orionsoft.financeiro.gerenciador.entities.agendamento.Agendamento
		extends Lancamento
	br.com.orionsoft.financeiro.gerenciador.entities.agendamento.AgendamentoItem
		classe vazia, extends LancamentoItem
 
  
Tabelas no BD
-------------
 
financeiro_agendamento
financeiro_agendamento_item
financeiro_banco
financeiro_centro_custo
financeiro_cheque_modelo
financeiro_classificacao_contabil
financeiro_classificacao_contabil_categoria
financeiro_conta
financeiro_conta_bancaria
financeiro_conta_categoria
financeiro_contrato_descontos_acrescimos
financeiro_convenio_cobranca
financeiro_convenio_pagamento
financeiro_documento_cobranca
financeiro_documento_cobranca_categoria
financeiro_documento_pagamento
financeiro_documento_pagamento_categoria
financeiro_item_custo
financeiro_lancamento
financeiro_lancamento_item
financeiro_lancamento_movimento
financeiro_operacao
financeiro_titulo_ocorrencia
financeiro_titulo_ocorrencia_controle

 */

@SuppressWarnings("unchecked")
public class Manter extends ManterBasic {

	public Manter(IServiceManager serviceManager, ServiceData serviceDataOwner) {
		super(serviceManager, serviceDataOwner);
	}

	
	public IEntity manterConvenioCobranca(ConvenioCobranca convenioCobranca) throws BusinessException, HibernateException {

		log.debug("ManterConvenioCobranca");

		if ((convenioCobranca == null) || (convenioCobranca.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ConvenioCobranca.class);
		crit.add(Expression.eq(ConvenioCobranca.NOME, convenioCobranca.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ConvenioCobranca.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity convenioCobrancaEntity = UtilsCrud.create(this.serviceManager, ConvenioCobranca.class, this.serviceData);

		manterPrimitiveProperties(convenioCobrancaEntity, convenioCobranca);
		
		IEntity documentoPagamentoCategoria = manterDocumentoPagamentoCategoria(convenioCobranca.getDocumentoPagamentoCategoria());
		if (documentoPagamentoCategoria != null)
			convenioCobrancaEntity.setPropertyValue(ConvenioCobranca.DOCUMENTO_PAGAMENTO_CATEGORIA, documentoPagamentoCategoria);

		/*
		 * FIXME Comentei estas linhas por causa do erro.
		 * Verificar pois houve mudanças.			
		 */
//		IEntity itemCustoJurosMora = manterItemCusto(convenioCobranca.getItemCustoJurosMora());
//		if (itemCustoJurosMora != null)
//			convenioCobrancaEntity.setPropertyValue(ConvenioCobranca.ITEM_CUSTO_JUROS_MORA, itemCustoJurosMora);
//
//		IEntity itemCustoMultaAtraso = manterItemCusto(convenioCobranca.getItemCustoMultaAtraso());
//		if (itemCustoMultaAtraso != null)
//			convenioCobrancaEntity.setPropertyValue(ConvenioCobranca.ITEM_CUSTO_MULTA_ATRASO, itemCustoMultaAtraso);		
//
//		IEntity itemCustoDescontoAntecipacao = manterItemCusto(convenioCobranca.getItemCustoDescontoAntecipacao());
//		if (itemCustoDescontoAntecipacao != null)
//			convenioCobrancaEntity.setPropertyValue(ConvenioCobranca.ITEM_CUSTO_DESCONTO_ANTECIPACAO, itemCustoDescontoAntecipacao);
		
		br.com.orionsoft.basic.Manter manterBasic = new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData);
		
		IEntity contratante = manterBasic.manterPessoaJuridica(convenioCobranca.getContratante());
		if (contratante != null)
			convenioCobrancaEntity.setPropertyValue(ConvenioCobranca.CONTRATANTE, contratante);
			
		IEntity contratado = manterBasic.manterContrato(convenioCobranca.getContratado());
		if (contratado != null)
			convenioCobrancaEntity.setPropertyValue(ConvenioCobranca.CONTRATADO, contratado);
		
		UtilsCrud.update(this.serviceManager, convenioCobrancaEntity, this.serviceData);

		return convenioCobrancaEntity;
	}
	

	public IEntity manterDocumentoCobranca(DocumentoCobranca documentoCobranca) throws BusinessException, HibernateException {

		log.debug("ManterDocumentoCobranca");

		if (documentoCobranca == null)
			return null;
		
		// TODO verificar
		Criteria crit = this.serviceData.getCurrentSession().createCriteria(DocumentoCobranca.class);
		crit.add(Expression.eq(DocumentoCobranca.NUMERO_DOCUMENTO, documentoCobranca.getNumeroDocumento()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), DocumentoCobranca.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity documentoCobrancaEntity = UtilsCrud.create(this.serviceManager, DocumentoCobranca.class, this.serviceData);

		manterPrimitiveProperties(documentoCobrancaEntity, documentoCobranca);

		// TODO verificar
		// documentoCobrancaCategoria : DocumentoCobrancaCategoria
		IEntity categoria = manterDocumentoCobrancaCategoria(documentoCobranca.getDocumentoCobrancaCategoria());
		if (categoria != null)
			documentoCobrancaEntity.setPropertyValue(DocumentoCobranca.DOCUMENTO_COBRANCA_CATEGORIA, categoria);

		// contrato : Contrato
		IEntity contrato = new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData).manterContrato(documentoCobranca.getContrato());
		if (contrato != null)
			documentoCobrancaEntity.setPropertyValue(DocumentoCobranca.CONTRATO, contrato);

		// lancamento : List<Lancamento>
		IEntityList lancamentos = manterLancamentos(documentoCobranca.getLancamentos());
		if (lancamentos != null)
			documentoCobrancaEntity.setPropertyValue(DocumentoCobranca.LANCAMENTOS, lancamentos);
			// getValue().setAsEntityList(lancamentos);

		// convenioCobranca : ConvenioCobranca
		IEntity convenioCobranca = manterConvenioCobranca(documentoCobranca.getConvenioCobranca());
		if (convenioCobranca != null)
			documentoCobrancaEntity.setPropertyValue(DocumentoCobranca.CONVENIO_COBRANCA, convenioCobranca);


		UtilsCrud.update(this.serviceManager, documentoCobrancaEntity, this.serviceData);

		return documentoCobrancaEntity;
	}
	
	
	public IEntityList manterDocumentoCobrancaCategorias(List<DocumentoCobrancaCategoria> documentoCobrancaCategorias) throws BusinessException, HibernateException {

		log.debug("ManterDocumentoCobrancaCategorias");

		IEntityList result = new EntityList(new ArrayList<Object>(), this.serviceManager.getEntityManager().getEntityMetadata(DocumentoCobrancaCategoria.class), this.serviceManager.getEntityManager());

		if (!documentoCobrancaCategorias.isEmpty()) {

			for (DocumentoCobrancaCategoria documentoCobrancaCategoria : documentoCobrancaCategorias) {

				IEntity documentoCobrancaCategoriaEntity = manterDocumentoCobrancaCategoria(documentoCobrancaCategoria);

				if (documentoCobrancaCategoriaEntity != null)
					result.add(documentoCobrancaCategoriaEntity);
			}
		}

		return result;
	}
	
	
	public IEntity manterDocumentoCobrancaCategoria(DocumentoCobrancaCategoria documentoCobrancaCategoria) throws BusinessException, HibernateException {

		log.debug("ManterDocumentoCobrancaCategoria");

		if ((documentoCobrancaCategoria == null) || (documentoCobrancaCategoria.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(DocumentoCobrancaCategoria.class);
		crit.add(Expression.eq(DocumentoCobrancaCategoria.NOME, documentoCobrancaCategoria.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), DocumentoCobrancaCategoria.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity documentoCobrancaCategoriaEntity = UtilsCrud.create(this.serviceManager, DocumentoCobrancaCategoria.class, this.serviceData);

		manterPrimitiveProperties(documentoCobrancaCategoriaEntity, documentoCobrancaCategoria);

		// TODO verificar
		// contaPadrao
		IEntity contaPadrao = manterConta(documentoCobrancaCategoria.getContaPadrao());
		if (contaPadrao != null)
			documentoCobrancaCategoriaEntity.setPropertyValue(DocumentoPagamentoCategoria.CONTA_PADRAO, contaPadrao);

		// convenioCobranca : ConvenioCobranca
		IEntity convenioCobranca = manterConvenioCobranca(documentoCobrancaCategoria.getConvenioCobranca());
		if (convenioCobranca != null)
			documentoCobrancaCategoriaEntity.setPropertyValue(DocumentoCobranca.CONVENIO_COBRANCA, convenioCobranca);

		UtilsCrud.update(this.serviceManager, documentoCobrancaCategoriaEntity, this.serviceData);

		return documentoCobrancaCategoriaEntity;
	}
	
	
	public IEntity manterCedente(Cedente cedente) throws BusinessException, HibernateException {

		log.debug("ManterCedente");

		if ((cedente == null) || (cedente.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(Cedente.class);
		crit.add(Expression.eq(Cedente.NOME, cedente.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Cedente.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity cedenteEntity = UtilsCrud.create(this.serviceManager, Cedente.class, this.serviceData);

		manterPrimitiveProperties(cedenteEntity, cedente);

		// TODO verificar
		// contaBancaria : ContaBancaria
		IEntity contaBancaria = manterContaBancaria(cedente.getContaBancaria());
		if (contaBancaria != null)
			cedenteEntity.setPropertyValue(Cedente.CONTA_BANCARIA, contaBancaria);

		// centroCustoGeral : CentroCusto
		IEntity centroCustoGeral = manterCentroCusto(cedente.getCentroCustoGeral());
		if (centroCustoGeral != null)
			cedenteEntity.setPropertyValue(Cedente.CENTRO_CUSTO_GERAL, centroCustoGeral);

		// itemCustoIof : ItemCusto
		IEntity itemCustoIof = manterItemCusto(cedente.getItemCustoIof());
		if (itemCustoIof != null)
			cedenteEntity.setPropertyValue(Cedente.ITEM_CUSTO_IOF, itemCustoIof);

		/*
		 * FIXME Comentei estas linhas por causa do erro.
		 * Verificar pois houve mudanças.			
		 */

		// itemCustoTarifa : ItemCusto
		IEntity itemCustoTarifa = manterItemCusto(cedente.getItemCustoTarifa());
		if (itemCustoTarifa != null)
			cedenteEntity.setPropertyValue(Cedente.ITEM_CUSTO_TARIFA, itemCustoTarifa);

		/*
		 * FIXME Comentei estas linhas por causa do erro.
		 * Verificar pois houve mudanças.			
		 */
		// itemCustoOutrasDeducoes : ItemCusto
//		IEntity itemCustoOutrasDeducoes = manterItemCusto(cedente.getItemCustoOutrasDeducoes());
//		if (itemCustoOutrasDeducoes != null)
//			cedenteEntity.setPropertyValue(Cedente.ITEM_CUSTO_OUTRAS_DEDUCOES, itemCustoOutrasDeducoes);
//		
//		UtilsCrud.update(this.serviceManager, cedenteEntity, this.serviceData);

		return cedenteEntity;
	}
	
	
	public IEntity manterDocumetoTitulo(DocumentoTitulo documentoTitulo) throws BusinessException, HibernateException {

		log.debug("ManterDocumetoTitulo");
		
		// TODO verificar
		if ((documentoTitulo == null) || (documentoTitulo.getNumeroDocumento() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(DocumentoTitulo.class);
		crit.add(Expression.eq(DocumentoTitulo.NUMERO_DOCUMENTO, documentoTitulo.getNumeroDocumento()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), DocumentoTitulo.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity documentoTituloEntity = UtilsCrud.create(this.serviceManager, DocumentoTitulo.class, this.serviceData);

		manterPrimitiveProperties(documentoTituloEntity, documentoTitulo);

		// TODO verificar
		// documentoCobrancaCategoria : DocumentoCobrancaCategoria
		IEntity categoria = manterDocumentoCobrancaCategoria(documentoTitulo.getDocumentoCobrancaCategoria());
		if (categoria != null)
			documentoTituloEntity.setPropertyValue(DocumentoTitulo.DOCUMENTO_COBRANCA_CATEGORIA, categoria);
		
		// contrato : Contrato
		IEntity contrato = new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData).manterContrato(documentoTitulo.getContrato());
		if (contrato != null)
			documentoTituloEntity.setPropertyValue(DocumentoTitulo.CONTRATO, contrato);
		
		// lancamento : List<Lancamento>
		IEntityList lancamentos = manterLancamentos(documentoTitulo.getLancamentos());
		if (lancamentos != null)
			documentoTituloEntity.setPropertyValue(DocumentoTitulo.LANCAMENTOS, lancamentos);
			// getValue().setAsEntityList(lancamentos);
		
		// convenioCobranca : ConvenioCobranca
		IEntity convenioCobranca = manterConvenioCobranca(documentoTitulo.getConvenioCobranca());
		if (convenioCobranca != null)
			documentoTituloEntity.setPropertyValue(DocumentoTitulo.CONVENIO_COBRANCA, convenioCobranca);
		
		// ocorrencias : List<OcorrenciaControle>
		IEntityList ocorrencias = manterOcorrenciaControles(documentoTitulo.getOcorrencias());
		if (ocorrencias != null)
			documentoTituloEntity.setPropertyValue(DocumentoTitulo.OCORRENCIAS, ocorrencias);

		// ultimaOcorrencia : Ocorrencia
		IEntity ultimaOcorrencia = manterOcorrencia(documentoTitulo.getUltimaOcorrencia());
		if (ultimaOcorrencia != null)
			documentoTituloEntity.setPropertyValue(DocumentoTitulo.ULTIMA_OCORRENCIA, ultimaOcorrencia);

		UtilsCrud.update(this.serviceManager, documentoTituloEntity, this.serviceData);

		return documentoTituloEntity;
	}
	
	
	public IEntityList manterOcorrencias(List<Ocorrencia> ocorrencias) throws BusinessException, HibernateException {

		log.debug("ManterOcorrencias");

		IEntityList result = new EntityList(new ArrayList<Object>(), this.serviceManager.getEntityManager().getEntityMetadata(Ocorrencia.class), this.serviceManager.getEntityManager());

		if (!ocorrencias.isEmpty()) {

			for (Ocorrencia ocorrencia : ocorrencias) {

				IEntity ocorrenciaEntity = manterOcorrencia(ocorrencia);

				if (ocorrenciaEntity != null)
					result.add(ocorrenciaEntity);
			}
		}

		return result;
	}
	
	
	public IEntity manterOcorrencia(Ocorrencia ocorrencia) throws BusinessException, HibernateException {

		log.debug("ManterOcorrencia");

		if (ocorrencia == null)
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(Ocorrencia.class);
		crit.add(Expression.eq(Ocorrencia.CODIGO, ocorrencia.getCodigo()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Ocorrencia.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity ocorrenciaEntity = UtilsCrud.create(this.serviceManager, Ocorrencia.class, this.serviceData);

		manterPrimitiveProperties(ocorrenciaEntity, ocorrencia);

		UtilsCrud.update(this.serviceManager, ocorrenciaEntity, this.serviceData);

		return ocorrenciaEntity;
	}
	
	
	public IEntityList manterOcorrenciaControles(List<OcorrenciaControle> ocorrenciaControles) throws BusinessException, HibernateException {

		log.debug("ManterOcorrenciaControles");

		IEntityList result = new EntityList(new ArrayList<Object>(), this.serviceManager.getEntityManager().getEntityMetadata(OcorrenciaControle.class), this.serviceManager.getEntityManager());

		if (!ocorrenciaControles.isEmpty()) {

			for (OcorrenciaControle ocorrenciaControle : ocorrenciaControles) {

				IEntity ocorrenciaControleEntity = manterOcorrenciaControle(ocorrenciaControle);

				if (ocorrenciaControleEntity != null)
					result.add(ocorrenciaControleEntity);
			}
		}

		return result;
	}
	
	
	public IEntity manterOcorrenciaControle(OcorrenciaControle ocorrenciaControle) throws BusinessException, HibernateException {

		log.debug("ManterOcorrenciaControle");

		// TODO verificar
		if ((ocorrenciaControle == null) || (ocorrenciaControle.getOcorrencia() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(OcorrenciaControle.class);
		crit.add(Expression.eq(OcorrenciaControle.OCORRENCIA, ocorrenciaControle.getOcorrencia()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), OcorrenciaControle.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity ocorrenciaControleEntity = UtilsCrud.create(this.serviceManager, OcorrenciaControle.class, this.serviceData);

		manterPrimitiveProperties(ocorrenciaControleEntity, ocorrenciaControle);

		// TODO verificar
		// titulo : DocumentoTitulo
		IEntity titulo = manterDocumetoTitulo(ocorrenciaControle.getTitulo());
		if (titulo != null)
			ocorrenciaControleEntity.setPropertyValue(OcorrenciaControle.TITULO, titulo);
		
		// ocorrencia : Ocorrencia
		IEntity ocorrencia = manterOcorrencia(ocorrenciaControle.getOcorrencia());
		if (ocorrencia != null)
			ocorrenciaControleEntity.setPropertyValue(OcorrenciaControle.OCORRENCIA, ocorrencia);
		
		UtilsCrud.update(this.serviceManager, ocorrenciaControleEntity, this.serviceData);

		return ocorrenciaControleEntity;
	}
	
	
	public IEntity manterConvenioPagamento(ConvenioPagamento convenioPagamento) throws BusinessException, HibernateException {

		log.debug("ManterConvenioPagamento");

		if ((convenioPagamento == null) || (convenioPagamento.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ConvenioPagamento.class);
		crit.add(Expression.eq(ConvenioPagamento.NOME, convenioPagamento.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ConvenioPagamento.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity convenioPagamentoEntity = UtilsCrud.create(this.serviceManager, ConvenioPagamento.class, this.serviceData);

		manterPrimitiveProperties(convenioPagamentoEntity, convenioPagamento);

		UtilsCrud.update(this.serviceManager, convenioPagamentoEntity, this.serviceData);

		return convenioPagamentoEntity;
	}
	
	
	public IEntity manterDocumentoPagamento(DocumentoPagamento documentoPagamento) throws BusinessException, HibernateException {

		log.debug("ManterDocumentoPagamento");

		if (documentoPagamento == null)
			return null;

		if (documentoPagamento instanceof DocumentoCheque)
			return manterDocumentoCheque((DocumentoCheque) documentoPagamento);

		if (documentoPagamento instanceof DocumentoDinheiro)
			return manterDocumentoDinheiro((DocumentoDinheiro) documentoPagamento);

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(DocumentoPagamento.class);
		crit.add(Expression.eq(DocumentoPagamento.NUMERO_DOCUMENTO, documentoPagamento.getNumeroDocumento()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), DocumentoPagamento.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity documentoPagamentoEntity = UtilsCrud.create(this.serviceManager, DocumentoPagamento.class, this.serviceData);

		manterPrimitiveProperties(documentoPagamentoEntity, documentoPagamento);

		// TODO
		// documentoPagamentoCategoria : DocumentoPagamentoCategoria
		IEntity categoria = manterDocumentoPagamentoCategoria(documentoPagamento.getDocumentoPagamentoCategoria());
		if (categoria != null)
			documentoPagamentoEntity.setPropertyValue(DocumentoPagamento.DOCUMENTO_PAGAMENTO_CATEGORIA, categoria);

		// contrato : Contrato
		IEntity contrato = new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData).manterContrato(documentoPagamento.getContrato());
		if (contrato != null)
			documentoPagamentoEntity.setPropertyValue(DocumentoPagamento.CONTRATO, contrato);

		// lancamentoMovimentos : List<LancamentoMovimento> mantido pelo LancamentoMovimento.documentoCobranca

		// convenioPagamento : ConvenioPagamento
		IEntity convenioPagamento = manterConvenioPagamento(documentoPagamento.getConvenioPagamento());
		if (convenioPagamento != null)
			documentoPagamentoEntity.setPropertyValue(DocumentoPagamento.CONVENIO_PAGAMENTO, convenioPagamento);

		UtilsCrud.update(this.serviceManager, documentoPagamentoEntity, this.serviceData);

		return documentoPagamentoEntity;
	}
	
	
	public IEntity manterDocumentoPagamentoCategoria(DocumentoPagamentoCategoria documentoPagamentoCategoria) throws BusinessException, HibernateException {

		log.debug("ManterDocumentoPagamentoCategoria");

		if ((documentoPagamentoCategoria == null) || (documentoPagamentoCategoria.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(DocumentoPagamentoCategoria.class);
		crit.add(Expression.eq(DocumentoPagamentoCategoria.NOME, documentoPagamentoCategoria.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), DocumentoPagamentoCategoria.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity documentoPagamentoCategoriaEntity = UtilsCrud.create(this.serviceManager, DocumentoPagamentoCategoria.class, this.serviceData);

		manterPrimitiveProperties(documentoPagamentoCategoriaEntity, documentoPagamentoCategoria);

		// contaPadrao
		IEntity contaPadrao = manterConta(documentoPagamentoCategoria.getContaPadrao());
		if (contaPadrao != null)
			documentoPagamentoCategoriaEntity.setPropertyValue(DocumentoPagamentoCategoria.CONTA_PADRAO, contaPadrao);

		// convenioPagamento
		IEntity convenioPagamento = manterConvenioPagamento(documentoPagamentoCategoria.getConvenioPagamento());
		if (convenioPagamento != null)
			documentoPagamentoCategoriaEntity.setPropertyValue(DocumentoPagamentoCategoria.CONVENIO_PAGAMENTO, convenioPagamento);

		UtilsCrud.update(this.serviceManager, documentoPagamentoCategoriaEntity, this.serviceData);

		return documentoPagamentoCategoriaEntity;
	}
	
	
	public IEntity manterChequeModelo(ChequeModelo chequeModelo) throws BusinessException, HibernateException {

		log.debug("ManterChequeModelo");

		if ((chequeModelo == null) || (chequeModelo.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ChequeModelo.class);
		crit.add(Expression.eq(ChequeModelo.NOME, chequeModelo.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ChequeModelo.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity chequeModeloEntity = UtilsCrud.create(this.serviceManager, ChequeModelo.class, this.serviceData);

		manterPrimitiveProperties(chequeModeloEntity, chequeModelo);

		UtilsCrud.update(this.serviceManager, chequeModeloEntity, this.serviceData);

		return chequeModeloEntity;
	}
	
	
	public IEntity manterConvenioCheque(ConvenioCheque convenioCheque) throws BusinessException, HibernateException {

		log.debug("ManterConvenioCheque");

		if ((convenioCheque == null) || (convenioCheque.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ConvenioCheque.class);
		crit.add(Expression.eq(ConvenioCheque.NOME, convenioCheque.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ConvenioCheque.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity convenioChequeEntity = UtilsCrud.create(this.serviceManager, ConvenioCheque.class, this.serviceData);
		
	    // contaBancaria
		IEntity contaBancaria = manterContaBancaria(convenioCheque.getContaBancaria());
		if (contaBancaria != null)
			convenioChequeEntity.setPropertyValue(ConvenioCheque.CONTA_BANCARIA, contaBancaria );
		
	    // chequeModelo
		IEntity chequeModelo = manterChequeModelo(convenioCheque.getChequeModelo());
		if (chequeModelo != null)
			convenioChequeEntity.setPropertyValue(ConvenioCheque.CHEQUE_MODELO, chequeModelo );
		
		// contratante
		IEntity contratante = new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData).manterPessoaJuridica(convenioCheque.getContratante());
		if (contratante != null)
			convenioChequeEntity.setPropertyValue(ConvenioCheque.CONTA_BANCARIA, contratante );
		
		// contratado
		IEntity contratado = new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData).manterPessoaJuridica(convenioCheque.getContratante());
		if (contratado != null)
			convenioChequeEntity.setPropertyValue(ConvenioCheque.CONTA_BANCARIA, contratado );
		
		manterPrimitiveProperties(convenioChequeEntity, convenioCheque);

		UtilsCrud.update(this.serviceManager, convenioChequeEntity, this.serviceData);

		return convenioChequeEntity;
	}
	
	
	public IEntity manterDocumentoCheque(DocumentoCheque documentoCheque) throws BusinessException, HibernateException {

		log.debug("ManterDocumentoCheque");

		if (documentoCheque == null)
			return null;

		IEntity documentoChequeEntity = UtilsCrud.create(this.serviceManager, DocumentoCheque.class, this.serviceData);

		manterPrimitiveProperties(documentoChequeEntity, documentoCheque);

		//	banco 
		IEntity banco = manterBanco(documentoCheque.getBanco());
		if (banco != null)
			documentoChequeEntity.setPropertyValue(DocumentoCheque.BANCO, banco);

		//Contrato
		IEntity contrato = new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData).manterContrato(documentoCheque.getContrato());
		if (contrato != null)
			documentoChequeEntity.setPropertyValue(DocumentoCheque.CONTRATO, contrato);

		//ConvenioPagamento
		IEntity convenioPagamento = manterConvenioPagamento(documentoCheque.getConvenioPagamento());
		if (convenioPagamento != null)
			documentoChequeEntity.setPropertyValue(DocumentoCheque.CONVENIO_PAGAMENTO, convenioPagamento);
		
		// DocumentoPagamentoCategoria
		IEntity documentoPagamentoCategoria = manterDocumentoPagamentoCategoria(documentoCheque.getDocumentoPagamentoCategoria());
		if (documentoPagamentoCategoria != null)
			documentoChequeEntity.setPropertyValue(DocumentoCheque.DOCUMENTO_PAGAMENTO_CATEGORIA, documentoPagamentoCategoria);

		UtilsCrud.update(this.serviceManager, documentoChequeEntity, this.serviceData);

		return documentoChequeEntity;
	}
	
	
	public IEntity manterDocumentoDinheiro(DocumentoDinheiro documentoDinheiro) throws BusinessException, HibernateException {

		log.debug("ManterDocumentoDinheiro");

		if (documentoDinheiro == null)
			return null;

		IEntity documentoDinheiroEntity = UtilsCrud.create(this.serviceManager, DocumentoDinheiro.class, this.serviceData);

		manterPrimitiveProperties(documentoDinheiroEntity, documentoDinheiro);
		
		//Contrato
		IEntity contrato = new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData).manterContrato(documentoDinheiro.getContrato());
		if (contrato != null)
			documentoDinheiroEntity.setPropertyValue(DocumentoDinheiro.CONTRATO, contrato);

		//ConvenioPagamento
		IEntity convenioPagamento = manterConvenioPagamento(documentoDinheiro.getConvenioPagamento());
		if (convenioPagamento != null)
			documentoDinheiroEntity.setPropertyValue(DocumentoDinheiro.CONVENIO_PAGAMENTO, convenioPagamento);
		
		// DocumentoPagamentoCategoria
		IEntity documentoPagamentoCategoria = manterDocumentoPagamentoCategoria(documentoDinheiro.getDocumentoPagamentoCategoria());
		if (documentoPagamentoCategoria != null)
			documentoDinheiroEntity.setPropertyValue(DocumentoDinheiro.DOCUMENTO_PAGAMENTO_CATEGORIA, documentoPagamentoCategoria);

		UtilsCrud.update(this.serviceManager, documentoDinheiroEntity, this.serviceData);

		return documentoDinheiroEntity;
	}
	
	
	public IEntity manterBanco(Banco banco) throws BusinessException, HibernateException {

		log.debug("ManterBanco");

		if ((banco == null) || (banco.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(Banco.class);
		crit.add(Expression.eq(Banco.NOME, banco.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Banco.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity bancoEntity = UtilsCrud.create(this.serviceManager, Banco.class, this.serviceData);

		manterPrimitiveProperties(bancoEntity, banco);

		UtilsCrud.update(this.serviceManager, bancoEntity, this.serviceData);

		return bancoEntity;
	}
	
	
	public IEntity manterCentroCusto(CentroCusto centroCusto) throws BusinessException, HibernateException {

		log.debug("ManterCentroCusto");

		if ((centroCusto == null) || (centroCusto.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(CentroCusto.class);
		crit.add(Expression.eq(CentroCusto.NOME, centroCusto.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), CentroCusto.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity centroCustoEntity = UtilsCrud.create(this.serviceManager, CentroCusto.class, this.serviceData);

		manterPrimitiveProperties(centroCustoEntity, centroCusto);

		UtilsCrud.update(this.serviceManager, centroCustoEntity, this.serviceData);

		return centroCustoEntity;
	}
	
	
	public IEntity manterClassificacaoContabil(ClassificacaoContabil classificacaoContabil) throws BusinessException, HibernateException {

		log.debug("ManterClassificacaoContabil");

		if ((classificacaoContabil == null) || (classificacaoContabil.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ClassificacaoContabil.class);
		crit.add(Expression.eq(ClassificacaoContabil.NOME, classificacaoContabil.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ClassificacaoContabil.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity classificacaoContabilEntity = UtilsCrud.create(this.serviceManager, ClassificacaoContabil.class, this.serviceData);

		manterPrimitiveProperties(classificacaoContabilEntity, classificacaoContabil);

		IEntity categoria = manterClassficacaoContabilCategoria(classificacaoContabil.getClassificacaoContabilCategoria());
		if (categoria != null)
			classificacaoContabilEntity.setPropertyValue(ClassificacaoContabil.CLASSIFICAO_CONTABIL_CATEGORIA, categoria);

		UtilsCrud.update(this.serviceManager, classificacaoContabilEntity, this.serviceData);

		return classificacaoContabilEntity;
	}
	
	
	public IEntity manterClassficacaoContabilCategoria(ClassificacaoContabilCategoria classificacaoContabilCategoria) throws BusinessException, HibernateException {

		log.debug("ManterClassficacaoContabilCategoria");

		if ((classificacaoContabilCategoria == null) || (classificacaoContabilCategoria.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ClassificacaoContabilCategoria.class);
		crit.add(Expression.eq(ClassificacaoContabilCategoria.NOME, classificacaoContabilCategoria.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ClassificacaoContabilCategoria.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity classificacaoContabilCategoriaEntity = UtilsCrud.create(this.serviceManager, ClassificacaoContabilCategoria.class, this.serviceData);

		manterPrimitiveProperties(classificacaoContabilCategoriaEntity, classificacaoContabilCategoria);

		UtilsCrud.update(this.serviceManager, classificacaoContabilCategoriaEntity, this.serviceData);

		return classificacaoContabilCategoriaEntity;
	}
	
	
	public IEntity manterConta(Conta conta) throws BusinessException, HibernateException {

		log.debug("ManterConta");

		if ((conta == null) || (conta.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(Conta.class);
		crit.add(Expression.eq(Conta.NOME, conta.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Conta.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity contaEntity = UtilsCrud.create(this.serviceManager, Conta.class, this.serviceData);

		manterPrimitiveProperties(contaEntity, conta);

		IEntity categoria = manterContaCategoria(conta.getContaCategoria());
		if (categoria != null)
			contaEntity.setPropertyValue(Conta.CONTA_CATEGORIA, categoria);

		UtilsCrud.update(this.serviceManager, contaEntity, this.serviceData);

		return contaEntity;
	}
	
	
	public IEntity manterContaBancaria(ContaBancaria contaBancaria) throws BusinessException, HibernateException {

		log.debug("ManterContaBancaria");

		if ((contaBancaria == null) || (contaBancaria.getBanco() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ContaBancaria.class);
		crit.add(Expression.eq(ContaBancaria.AGENCIA_CODIGO, contaBancaria.getAgenciaCodigo()));
		crit.add(Expression.eq(ContaBancaria.CONTA_CODIGO, contaBancaria.getContaCodigo()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ContaBancaria.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity contaBancariaEntity = UtilsCrud.create(this.serviceManager, ContaBancaria.class, this.serviceData);

		manterPrimitiveProperties(contaBancariaEntity, contaBancaria);

		IEntity banco = manterBanco(contaBancaria.getBanco());
		if (banco != null)
			contaBancariaEntity.setPropertyValue(ContaBancaria.BANCO, banco);

		UtilsCrud.update(this.serviceManager, contaBancariaEntity, this.serviceData);

		return contaBancariaEntity;
	}
	
	
	public IEntity manterContaCategoria(ContaCategoria contaCategoria) throws BusinessException, HibernateException {

		log.debug("ManterContaCategoria");

		if ((contaCategoria == null) || (contaCategoria.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ContaCategoria.class);
		crit.add(Expression.eq(ContaCategoria.NOME, contaCategoria.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ContaCategoria.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity contaCategoriaEntity = UtilsCrud.create(this.serviceManager, ContaCategoria.class, this.serviceData);

		manterPrimitiveProperties(contaCategoriaEntity, contaCategoria);

		UtilsCrud.update(this.serviceManager, contaCategoriaEntity, this.serviceData);

		return contaCategoriaEntity;
	}
	
	
	public IEntity manterContratoFinanceiro(ContratoFinanceiro contratoFinanceiro) throws BusinessException, HibernateException {

		log.debug("ManterContratoFinanceiro");

		if ((contratoFinanceiro == null) || (contratoFinanceiro.getCodigo() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ContratoFinanceiro.class);
		crit.add(Expression.eq(ContratoFinanceiro.CODIGO, contratoFinanceiro.getCodigo()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ContratoFinanceiro.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity contratoFinanceiroEntity = UtilsCrud.create(this.serviceManager, ContratoFinanceiro.class, this.serviceData);

		manterPrimitiveProperties(contratoFinanceiroEntity, contratoFinanceiro);

		// Classe ContratoFinanceiro
        IEntityList descontosAcrescimos = manterContratoFinanceiroDescontoAcrescimos(contratoFinanceiro.getDescontosAcrescimos());
		if (descontosAcrescimos != null)
			contratoFinanceiroEntity.getProperty(ContratoFinanceiro.DESCONTOS_ACRESCIMOS).getValue().setAsEntityList(descontosAcrescimos);
        
		// Classe Contrato
		br.com.orionsoft.basic.Manter manterBasic = new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData);
		
		IEntity pessoa = manterBasic.manterPessoa(contratoFinanceiro.getPessoa());
		if (pessoa != null)
			contratoFinanceiroEntity.getProperty(Contrato.PESSOA).getValue().setAsEntity(pessoa);
		
		IEntity representante = manterBasic.manterRepresentante(contratoFinanceiro.getRepresentante());
		if (representante != null)
			contratoFinanceiroEntity.getProperty(Contrato.REPRESENTANTE).getValue().setAsEntity(representante);
		
		IEntity categoria = manterBasic.manterContratoCategoria(contratoFinanceiro.getCategoria());
		if (categoria != null)
			contratoFinanceiroEntity.getProperty(Contrato.CATEGORIA).getValue().setAsEntity(categoria);
		
        IEntitySet contatos = manterBasic.manterContratoContatos(contratoFinanceiro.getContatos());
        if (contatos != null)
        	contratoFinanceiroEntity.getProperty(Contrato.CONTATOS).getValue().setAsEntitySet(contatos);
		
		IEntity observacoes = manterBasic.manterObservacoes(contratoFinanceiro.getObservacoes());
		if (observacoes != null)
			contratoFinanceiroEntity.getProperty(Contrato.OBSERVACOES).getValue().setAsEntity(observacoes);
		
		UtilsCrud.update(this.serviceManager, contratoFinanceiroEntity, this.serviceData);

		return contratoFinanceiroEntity;
	}
	
	
	public IEntity manterContratoFinanceiroCategoria(ContratoFinanceiroCategoria contratoFinanceiroCategoria) throws BusinessException, HibernateException {

		log.debug("ManterContratoFinanceiroCategoria");

		if (contratoFinanceiroCategoria == null)
			return null;
		
		IEntity contratoFinanceiroCategoriaEntity = UtilsCrud.create(this.serviceManager, ContratoFinanceiroCategoria.class, this.serviceData);

		manterPrimitiveProperties(contratoFinanceiroCategoriaEntity, contratoFinanceiroCategoria);

		UtilsCrud.update(this.serviceManager, contratoFinanceiroCategoriaEntity, this.serviceData);

		return contratoFinanceiroCategoriaEntity;
	}
	
	
	public IEntityList manterContratoFinanceiroDescontoAcrescimos(List<ContratoFinanceiroDescontoAcrescimo> descontoAcrescimos) throws BusinessException, HibernateException {

		log.debug("ManterContratoFinanceiroDescontoAcrescimos");

		IEntityList result = new EntityList(new ArrayList<Object>(), this.serviceManager.getEntityManager().getEntityMetadata(ContratoFinanceiroDescontoAcrescimo.class), this.serviceManager.getEntityManager());

		if (!descontoAcrescimos.isEmpty()) {

			for (ContratoFinanceiroDescontoAcrescimo descontoAcrescimo : descontoAcrescimos) {

				IEntity descontoAcrescimoEntity = manterContratoFinanceiroDescontoAcrescimo(descontoAcrescimo);

				if (descontoAcrescimoEntity != null)
					result.add(descontoAcrescimoEntity);
			}
		}

		return result;
	}
	
	
	public IEntity manterContratoFinanceiroDescontoAcrescimo(ContratoFinanceiroDescontoAcrescimo descontosAcrescimo) throws BusinessException, HibernateException {

		log.debug("ManterContratoFinanceiroDescontoAcrescimo");

		if (descontosAcrescimo == null)
			return null;

		IEntity descontosAcrescimoEntity = UtilsCrud.create(this.serviceManager, ContratoFinanceiroDescontoAcrescimo.class, this.serviceData);

		manterPrimitiveProperties(descontosAcrescimoEntity, descontosAcrescimo);

		IEntity itemCusto = manterItemCusto(descontosAcrescimo.getItemCusto());
		if (itemCusto != null)
			descontosAcrescimoEntity.setPropertyValue(ContratoFinanceiroDescontoAcrescimo.ITEM_CUSTO, itemCusto);
		
		IEntity classificacaoContabil = manterClassificacaoContabil(descontosAcrescimo.getClassificacaoContabil());
		if (classificacaoContabil != null)
			descontosAcrescimoEntity.setPropertyValue(ContratoFinanceiroDescontoAcrescimo.CLASSIFICACAO_CONTABIL, classificacaoContabil);

		UtilsCrud.update(this.serviceManager, descontosAcrescimoEntity, this.serviceData);

		return descontosAcrescimoEntity;
	}
	
	
	public IEntity manterItemCusto(ItemCusto itemCusto) throws BusinessException, HibernateException {

		log.debug("ManterItemCusto");

		if ((itemCusto == null) || (itemCusto.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ItemCusto.class);
		crit.add(Expression.eq(ItemCusto.NOME, itemCusto.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ItemCusto.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity itemCustoEntity = UtilsCrud.create(this.serviceManager, ItemCusto.class, this.serviceData);

		manterPrimitiveProperties(itemCustoEntity, itemCusto);

		UtilsCrud.update(this.serviceManager, itemCustoEntity, this.serviceData);

		return itemCustoEntity;
	}
	
	public IEntityList manterLancamentos(List<Lancamento> lancamentos) throws BusinessException, HibernateException {

		log.debug("ManterLancamentos");

		IEntityList result = new EntityList(new ArrayList<Object>(), this.serviceManager.getEntityManager().getEntityMetadata(Lancamento.class), this.serviceManager.getEntityManager());

		if (!lancamentos.isEmpty()) {

			for (Lancamento lancamento : lancamentos) {

				IEntity lancamentoEntity = manterLancamento(lancamento);

				if (lancamentoEntity != null)
					result.add(lancamentoEntity);
			}
		}

		return result;
	}
	
	public IEntity manterLancamento(Lancamento lancamento) throws BusinessException, HibernateException {

		log.debug("ManterLancamento");
		if(lancamento == null)
			return null;
		
		IEntity lancamentoEntity = UtilsCrud.create(this.serviceManager, Lancamento.class, this.serviceData);

		manterPrimitiveProperties(lancamentoEntity, lancamento);

		IEntity contrato = new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData).manterContrato(lancamento.getContrato());
		if (contrato != null)
			lancamentoEntity.setPropertyValue(Lancamento.CONTRATO, contrato);
		
		IEntity contaPrevista = manterConta(lancamento.getContaPrevista());
		if (contaPrevista != null)
			lancamentoEntity.setPropertyValue(Lancamento.CONTA_PREVISTA, contaPrevista);
		
		IEntity operacao = manterOperacao(lancamento.getOperacao());
		if (operacao != null)
			lancamentoEntity.setPropertyValue(Lancamento.OPERACAO, operacao);

		IEntity documentoCobranca = manterDocumentoCobranca(lancamento.getDocumentoCobranca());
		if (documentoCobranca != null)
			lancamentoEntity.setPropertyValue(Lancamento.DOCUMENTO_COBRANCA, documentoCobranca);
		
		IEntity documentoPagamento = manterDocumentoPagamento(lancamento.getDocumentoPagamento());
		if (documentoPagamento != null)
			lancamentoEntity.setPropertyValue(Lancamento.DOCUMENTO_PAGAMENTO, documentoPagamento);
		
		IEntityList lancamentoItens = manterLancamentoItens(lancamento.getLancamentoItens());
		if (lancamentoItens != null)
			lancamentoEntity.setPropertyValue(Lancamento.LANCAMENTO_ITENS, lancamentoItens);		

		IEntityList lancamentoMovimentos = manterLancamentoMovimentos(lancamento.getLancamentoMovimentos());
		if (lancamentoMovimentos != null)
			lancamentoEntity.setPropertyValue(Lancamento.LANCAMENTO_MOVIMENTOS, lancamentoMovimentos);		
		
		UtilsCrud.update(this.serviceManager, lancamentoEntity, this.serviceData);

		return lancamentoEntity;
	}
	
	
	public IEntityList manterLancamentoItens(List<LancamentoItem> lancamentoItens) throws BusinessException, HibernateException {

		log.debug("ManterLancamentoItens");

		IEntityList result = new EntityList(new ArrayList<Object>(), this.serviceManager.getEntityManager().getEntityMetadata(LancamentoItem.class), this.serviceManager.getEntityManager());

		if (!lancamentoItens.isEmpty()) {

			for (LancamentoItem lancamentoItem : lancamentoItens) {

				IEntity lancamentoItenEntity = manterLancamentoItem(lancamentoItem);

				if (lancamentoItenEntity != null)
					result.add(lancamentoItenEntity);
			}
		}

		return result;
	}
	
	
	public IEntity manterLancamentoItem(LancamentoItem lancamentoItem) throws BusinessException, HibernateException {

		log.debug("ManterLancamentoItem");

		if (lancamentoItem == null)
			return null;

		IEntity lancamentoItemEntity = UtilsCrud.create(this.serviceManager, LancamentoItem.class, this.serviceData);

		manterPrimitiveProperties(lancamentoItemEntity, lancamentoItem);

		IEntity centroCusto = manterCentroCusto(lancamentoItem.getCentroCusto());
		if (centroCusto != null)
			lancamentoItemEntity.setPropertyValue(LancamentoItem.CENTRO_CUSTO, centroCusto);

		IEntity itemCusto = manterItemCusto(lancamentoItem.getItemCusto());
		if (itemCusto != null)
			lancamentoItemEntity.setPropertyValue(LancamentoItem.ITEM_CUSTO, itemCusto);

		IEntity classficacaoContabil = manterClassificacaoContabil(lancamentoItem.getClassificacaoContabil());
		if (classficacaoContabil != null)
			lancamentoItemEntity.setPropertyValue(LancamentoItem.CLASSIFICACAO_CONTABIL, classficacaoContabil);

		UtilsCrud.update(this.serviceManager, lancamentoItemEntity, this.serviceData);

		return lancamentoItemEntity;
	}
	
	
	public IEntityList manterLancamentoMovimentos(List<LancamentoMovimento> lancamentoMovimentos) throws BusinessException, HibernateException {

		log.debug("ManterLancamentoMovimentos");

		IEntityList result = new EntityList(new ArrayList<Object>(), this.serviceManager.getEntityManager().getEntityMetadata(LancamentoMovimento.class), this.serviceManager.getEntityManager());

		if (!lancamentoMovimentos.isEmpty()) {

			for (LancamentoMovimento lancamentoMovimento : lancamentoMovimentos) {

				IEntity lancamentoMovimentoEntity = manterLancamentoMovimento(lancamentoMovimento);

				if (lancamentoMovimentoEntity != null)
					result.add(lancamentoMovimentoEntity);
			}
		}

		return result;
	}
	
	
	public IEntity manterLancamentoMovimento(LancamentoMovimento lancamentoMovimento) throws BusinessException, HibernateException {

		log.debug("ManterLancamentoMovimento");

		if (lancamentoMovimento == null)
			return null;

		IEntity lancamentoMovimentoEntity = UtilsCrud.create(this.serviceManager, LancamentoMovimento.class, this.serviceData);

		manterPrimitiveProperties(lancamentoMovimentoEntity, lancamentoMovimento);

		IEntity conta = manterConta(lancamentoMovimento.getConta());
		if (conta != null)
			lancamentoMovimentoEntity.setPropertyValue(LancamentoMovimento.CONTA, conta);
		
		IEntity documentoPagamento = manterDocumentoPagamento(lancamentoMovimento.getDocumentoPagamento());
		if (documentoPagamento != null)
			lancamentoMovimentoEntity.setPropertyValue(LancamentoMovimento.DOCUMENTO_PAGAMENTO, documentoPagamento);

		UtilsCrud.update(this.serviceManager, lancamentoMovimentoEntity, this.serviceData);

		return lancamentoMovimentoEntity;
	}
	
	public IEntity manterLancamentoMovimentoTransferencia(LancamentoMovimento lancamentoMovimento) throws BusinessException, HibernateException {

		log.debug("ManterLancamentoMovimentoTransferencia");

		if (lancamentoMovimento == null)
			return null;

		IEntity lancamentoMovimentoDe = UtilsCrud.create(this.serviceManager, LancamentoMovimento.class, this.serviceData);
		
		manterPrimitiveProperties(lancamentoMovimentoDe, lancamentoMovimento);

		IEntity contaDe = manterConta(lancamentoMovimento.getConta());
		if (contaDe != null)
			lancamentoMovimentoDe.setPropertyValue(LancamentoMovimento.CONTA, contaDe);
		
		UtilsCrud.update(this.serviceManager, lancamentoMovimentoDe, this.serviceData);


		IEntity lancamentoMovimentoPara = UtilsCrud.create(this.serviceManager, LancamentoMovimento.class, this.serviceData);
		
		manterPrimitiveProperties(lancamentoMovimentoPara, lancamentoMovimento.getTransferencia());

		IEntity contaPara = manterConta(lancamentoMovimento.getTransferencia().getConta());
		if (contaPara != null)
			lancamentoMovimentoPara.setPropertyValue(LancamentoMovimento.CONTA, contaPara);

		/* Relaciona os movimentos entre si */
		lancamentoMovimentoDe.setPropertyValue(LancamentoMovimento.TRANSFERENCIA, lancamentoMovimentoPara);

		lancamentoMovimentoPara.setPropertyValue(LancamentoMovimento.TRANSFERENCIA, lancamentoMovimentoDe);
		
		UtilsCrud.update(this.serviceManager, lancamentoMovimentoPara, this.serviceData);
		
		UtilsCrud.update(this.serviceManager, lancamentoMovimentoDe, this.serviceData);

		return lancamentoMovimentoDe;
	}
	
	public IEntity manterOperacao(Operacao operacao) throws BusinessException, HibernateException {

		log.debug("ManterOperacao");

		if ((operacao == null) || (operacao.getNome() == null))
			return null;

		Criteria crit = this.serviceData.getCurrentSession().createCriteria(Operacao.class);
		crit.add(Expression.eq(Operacao.NOME, operacao.getNome()));

		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Operacao.class);

		if (!entityList.isEmpty())
			return entityList.get(0);

		IEntity operacaoEntity = UtilsCrud.create(this.serviceManager, Operacao.class, this.serviceData);

		manterPrimitiveProperties(operacaoEntity, operacao);

		UtilsCrud.update(this.serviceManager, operacaoEntity, this.serviceData);


		
		return operacaoEntity;
	}

	
	public IEntity manterAgendamento(Agendamento agendamento) throws BusinessException, HibernateException {

		log.debug("ManterAgendamento");

		if (agendamento == null)
			return null;

//		Criteria crit = this.serviceData.getCurrentSession().createCriteria(Agendamento.class);
//		crit.add(Expression.eq(Agendamento.NOME, agendamento.getNome()));
//
//		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Agendamento.class);
//
//		if (!entityList.isEmpty())
//			return entityList.get(0);

		IEntity agendamentoEntity = UtilsCrud.create(this.serviceManager, Agendamento.class, this.serviceData);

		manterPrimitiveProperties(agendamentoEntity, agendamento);

		// TODO
//		frequencia : Frequencia
//		ultimoLancamento : Lancamento
//		agendamentoItem : AgendamentoItem

		UtilsCrud.update(this.serviceManager, agendamentoEntity, this.serviceData);

		return agendamentoEntity;
	}
	
	
	public IEntity manterAgendamentoItem(AgendamentoItem agendamentoItem) throws BusinessException, HibernateException {

		log.debug("ManterAgendamentoItem");

		if (agendamentoItem == null)
			return null;

//		Criteria crit = this.serviceData.getCurrentSession().createCriteria(AgendamentoItem.class);
//		crit.add(Expression.eq(AgendamentoItem.NOME, agendamentoItem.getNome()));
//
//		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), AgendamentoItem.class);
//
//		if (!entityList.isEmpty())
//			return entityList.get(0);

		IEntity agendamentoItemEntity = UtilsCrud.create(this.serviceManager, AgendamentoItem.class, this.serviceData);

		manterPrimitiveProperties(agendamentoItemEntity, agendamentoItem);

		// TODO

		UtilsCrud.update(this.serviceManager, agendamentoItemEntity, this.serviceData);

		return agendamentoItemEntity;
	}
}
