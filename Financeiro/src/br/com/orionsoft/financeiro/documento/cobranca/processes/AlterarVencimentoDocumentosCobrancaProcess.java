package br.com.orionsoft.financeiro.documento.cobranca.processes;

import java.util.Calendar;

import org.apache.commons.lang.ClassUtils;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.services.AlterarVencimentoDocumentosCobrancaService;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityCollectionProcess;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;

/**
 * Este processo realiza a alteração do vencimento de vários documentos de cobranças 
 * em uma única transação.
 * 
 * <p>
 * <b>Procedimentos:</b><br>
 * Primeiramente deve-se alimentar a lista de documentos:
 * <i>getDocuments().add(IEntity<DocumentoCobranca>)</i><br>
 * Definir a nova data de vencimento:
 * <i>setData(Calendar)</i><br>
 * Gravar as alterações por <i>runAlterar()</i>.
 * 
 * @author Lucio
 * @version 20110404
 * 
 * @spring.bean id="AlterarVencimentoDocumentosCobrancaProcess" init-method="start"
 *              destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 * 
 */
@ProcessMetadata(label = "Alterar vencimento de documentos de cobrança", hint = "Alterar o vencimento de vários documentos de cobrança, de uma só vez", description = "Altera a data de vencimento dos documentos de cobrança, <b>evitando o pagamento de multa e juros</b> e registrando as alterações na auditoria de cada documento alterado")
public class AlterarVencimentoDocumentosCobrancaProcess extends ProcessBasic implements IRunnableEntityProcess, IRunnableEntityCollectionProcess {
	public static final String PROCESS_NAME="AlterarVencimentoDocumentosCobrancaProcess";

	private Calendar data = CalendarUtils.getCalendar();
	private String adendoInstrucoes3 = "";
	private IEntityList<DocumentoCobranca> documentos;

	public void start() {
		try {
			super.start();

			this.documentos = this.getProcessManager().getServiceManager()
					.getEntityManager().getEntityList(null, DocumentoCobranca.class);

		} catch (BusinessException e) {
			super.getMessageList().add(e.getErrorList());
		}
	}
	
	public boolean runAlterar() {
		super.beforeRun();
		try {
			/* Altera o vencimento */
			for(IEntity<DocumentoCobranca> doc: this.documentos){
				if(doc.isSelected()){
					ServiceData sds = new ServiceData(AlterarVencimentoDocumentosCobrancaService.SERVICE_NAME, null);
					sds.getArgumentList().setProperty(AlterarVencimentoDocumentosCobrancaService.IN_DATA, this.data);
					sds.getArgumentList().setProperty(AlterarVencimentoDocumentosCobrancaService.IN_ADENDO_INSTRUCOES_3, this.adendoInstrucoes3);
					sds.getArgumentList().setProperty(AlterarVencimentoDocumentosCobrancaService.IN_DOCUMENTOS, this.documentos);
					sds.getArgumentList().setProperty(AlterarVencimentoDocumentosCobrancaService.IN_USER_SESSION_OPT, this.getUserSession());
					this.getProcessManager().getServiceManager().execute(sds);

					/* Pega as mensagens do serviço */
					this.getMessageList().addAll(sds.getMessageList());
				}
			}

			return true;

		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}

	public String getProcessName() {
		return PROCESS_NAME;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}
	
	public String getAdendoInstrucoes3() {
		return adendoInstrucoes3;
	}

	public void setAdendoInstrucoes3(String adendoInstrucoes3) {
		this.adendoInstrucoes3 = adendoInstrucoes3;
	}

	/*
	 * ==========================================================================
	 * ==== IRunnableEntityProcess
	 * ================================================
	 * ==============================
	 */
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();

		boolean result = false;

		/* Verifica se a entidade é compatível */
		this.documentos.clear();
		/* Verifica se a entidade passada eh um DocumentoCobranca ou pertence eh descendente */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), DocumentoCobranca.class)) {
				this.documentos.add((IEntity<DocumentoCobranca>) entity);
				result = true;
		}else{
			if (entity.getInfo().getType() == Lancamento.class) {
				try {
					this.documentos.add(entity.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().<DocumentoCobranca>getAsEntity());
					result = true;
				} catch (BusinessException e) {
					this.getMessageList().add(e.getErrorList());
					result = false;
				}
			} else {
				this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class,"ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
			}
		}
		return result;
	}

	@Override
	public boolean runWithEntities(IEntityCollection<?> entities) {
		super.beforeRun();

		boolean result = false;

		/* Verifica se a entidade é compatível */
		this.documentos.clear();
		/* Verifica se a entidade passada eh um DocumentoCobranca ou pertence eh descendente */
		if (ClassUtils.isAssignable(entities.getInfo().getType(), DocumentoCobranca.class)) {
				this.documentos.addAll(((IEntityCollection<DocumentoCobranca>) entities).getCollection());
				result = true;
		}else
			if (entities.getInfo().getType() == Lancamento.class) {
				try {
					for(IEntity<DocumentoCobranca> entity: (IEntityCollection<DocumentoCobranca>) entities){
						this.documentos.add(entity.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().<DocumentoCobranca>getAsEntity());
					}
				} catch (BusinessException e) {
					this.getMessageList().add(e.getErrorList());
				}
				result = true;
			}
			else {
				this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class,"ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entities.getInfo().getType().getName()));
			}
		
		return result;

	}
	
	public IEntityList<DocumentoCobranca> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(IEntityList<DocumentoCobranca> documentos) {
		this.documentos = documentos;
	}
	
}
