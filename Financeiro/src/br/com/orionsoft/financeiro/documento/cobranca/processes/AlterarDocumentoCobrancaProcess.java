package br.com.orionsoft.financeiro.documento.cobranca.processes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.services.CancelarDocumentoCobrancaService;
import br.com.orionsoft.financeiro.documento.cobranca.services.CriarDocumentoCobrancaService;
import br.com.orionsoft.financeiro.documento.cobranca.services.LancarDocumentoCobrancaService;
import br.com.orionsoft.financeiro.documento.cobranca.services.ObterPropriedadesPreenchimentoManualService;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo altera o documento vinculado a um lan�amento,
 * caso este documento n�o exista, � criado um novo
 * 
 * <p>
 * <b>Procedimentos:</b> <br>
 * - Obter o lan�amento relacionado com o movimento que estava sendo visualisado;<br>
 * - Listar as formas de pagamento poss�veis com uma lista de SelectItens;<br>
 * - Obter as propriedades edit�veis do tipo de documento;<br>
 * - Atualizar o lan�amento com o novo documento criado;<br>  
 * 
 * @author Andre 20070207
 * @version Lucio 20/08/2008: Adi�ao do recurso RunnableProcess.
 * 
 * @spring.bean id="AlterarDocumentoCobrancaProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 * 
 */
@ProcessMetadata(label="Alterar documento de cobran�a", hint="Altera o documento de cobran�a de um determinado lan�amento em aberto", description="Altera o documento de cobran�a de um lan�amento que esteja com 100% de seu saldo em aberto.")
public class AlterarDocumentoCobrancaProcess extends ProcessBasic implements IRunnableEntityProcess{
    public static final String PROCESS_NAME = "AlterarDocumentoCobrancaProcess";
    public static final String NENHUM_DOCUMENTO_COBRANCA = "Nenhum documento de cobran�a";

    private long categoriaId = IDAO.ENTITY_UNSAVED;
    private long lancamentoId = IDAO.ENTITY_UNSAVED;

    private Calendar dataCancelamento = CalendarUtils.getCalendar();
    
    private List<IProperty> propriedadesEditaveis = null;
    
    private IEntity<Lancamento> lancamento; 
    private IEntity<DocumentoCobranca> documentoNovo;
    private IEntity<DocumentoCobranca> documentoAntigo;
    
    public String getProcessName(){
    	return PROCESS_NAME;
    }

    /**
     * Altera a categoria do documento de cobran�a;
     * Procedimento:
     * - � necess�rio verificar se algum dos documentos � null, pois o lan�amento pode n�o ter um documento associado (caso em que o
     * documento antigo � null) ou o documento novo � 'nenhum documento de cobran�a' (caso em que o documento novo � null);
     * - Caso o documento antigo seja diferente de null, ele � cancelado;
     * - Caso o documento novo seja diferente de null, ele � lan�ado;
     * - Finalizando, o documento novo � gravado no lancamento (seja este documento null ou n�o).
     */
    public boolean runAlterarCategoria(){
    	super.beforeRun();
    	
    	try {
    		/* Strings utilizadas nas mensagens finais do processo - evita que, caso algum dos documentos seja null, ocorra NullPointer */
    		String docAntigo = NENHUM_DOCUMENTO_COBRANCA;
    		String docNovo = NENHUM_DOCUMENTO_COBRANCA;

    		// Se o documentoAntigo n�o for null ele � cancelado
    		if (!isDocumentoNull(documentoAntigo)){
    			log.debug("Cancelando o documento antigo");
    			/* Cancelando o documento antigo (anterior) */
    			ServiceData sdCancelarDocumento = new ServiceData(CancelarDocumentoCobrancaService.SERVICE_NAME, null);
    			sdCancelarDocumento.getArgumentList().setProperty(CancelarDocumentoCobrancaService.IN_DOCUMENTO, documentoAntigo);
    			sdCancelarDocumento.getArgumentList().setProperty(CancelarDocumentoCobrancaService.IN_DATA_CANCELAMENTO, CalendarUtils.getCalendar());
    			this.getProcessManager().getServiceManager().execute(sdCancelarDocumento);	

    			/* Adiciona as mensagens na lista do processo */
    			this.getMessageList().addAll(sdCancelarDocumento.getMessageList());

    			docAntigo = this.documentoAntigo.toString();
    		}

    		/* Se o documentoNovo n�o for null, ele � lan�ando  */
    		if (!isDocumentoNull(documentoNovo)){
    			log.debug("Verificando se o documento ainda n�o foi confirmado (persistido)");
    			if(documentoNovo.getId()==IDAO.ENTITY_UNSAVED){
    				log.debug("Lan�ando o novo documento");
    				ServiceData sdLancarDocumento = new ServiceData(LancarDocumentoCobrancaService.SERVICE_NAME, null);
    				sdLancarDocumento.getArgumentList().setProperty(LancarDocumentoCobrancaService.IN_DOCUMENTO, documentoNovo.getObject());
    				this.getProcessManager().getServiceManager().execute(sdLancarDocumento);
    			}

    			docNovo = this.documentoNovo.toString();
    		}
    		
    		/* Setando o lancamento com o novo documento criado - se n�o houver documento, salva como null */
			this.lancamento.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().setAsEntity(this.documentoNovo);
			
			/* Atualizando o lancamento */
    		UtilsCrud.update(this.getProcessManager().getServiceManager(), this.lancamento, null);
			
    		/* Adiciona as mensagens na lista do processo */
    		// Se os dois documentos forem nulos ou seus ids forem iguais, n�o houve altera��o
    		if ((isDocumentoNull(documentoNovo) && isDocumentoNull(documentoAntigo)) || ((!isDocumentoNull(documentoNovo) && !isDocumentoNull(documentoAntigo)) && (documentoNovo.getId()==documentoAntigo.getId())))
    			this.getMessageList().add(new BusinessMessage(AlterarDocumentoCobrancaProcess.class, "DOCUMENTO_NAO_ALTERADO", docAntigo));
        	else 
    			this.getMessageList().add(new BusinessMessage(AlterarDocumentoCobrancaProcess.class, "ALTERAR_DOCUMENTO_SUCESSO", docAntigo, docNovo));

    		/* Grava a auditoria do processo */
    		UtilsAuditorship.auditProcess(this,"documentoCancelado='"+ docAntigo + "' " + "documentoCriado='" + docNovo + "'", null);
    		
    		return true;

    	} catch (BusinessException e) {
    		/* Armazenando a lista de erros */
    		this.getMessageList().addAll(e.getErrorList());
    		return false;
    	}

    }

    public void runInicializar() throws BusinessException{
    	super.beforeRun();
    	
    	this.documentoAntigo = null;
    	this.documentoNovo = null;
    	
    	this.lancamento = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(), Lancamento.class, lancamentoId, null);
    	
    	this.documentoAntigo = lancamento.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().getAsEntity();
    }
	
    /**
     * Quando a categoria do documento de cobran�a � alterada um novo documento em branco deve
     * ser criado e uma nova lista de propriedades edit�veis deve ser construida
     * 
     * @throws BusinessException
     */
    private void categoriaMudou() throws BusinessException {
        /* Limpa o buffer da lista de propriedades editaveis */
        propriedadesEditaveis = null;

        /* Limpa o documentoNovo e for�a o documentoAntigo a ser atualizado, pois estava usando o da vis�o antiga */
        runInicializar();
        
        /* Verifica se foi escolhida alguma categoria do documento de cobran�a */
        if (this.getCategoriaId() != IDAO.ENTITY_UNSAVED) {
            /* Obtem a nova categoria do documento de cobran�a */
            IEntity<DocumentoCobrancaCategoria> documentoCobrancaCategoria = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(), DocumentoCobrancaCategoria.class, categoriaId, null);
            
            /* 
             * Verificar as propriedade que s�o diferentes entre o lancamento e o documento; 
             * Caso o lancamento n�o tenha documento, deve obter as propriedades diretamente do lancamento.
             */
            Calendar dataDocumento;
            Transacao transacao;
            if (documentoAntigo != null){ // se tiver documento, obt�m algumas propriedades diretamento do documento
               	dataDocumento = this.documentoAntigo.getProperty(DocumentoCobranca.DATA).getValue().getAsCalendar();
            	transacao = (Transacao) this.documentoAntigo.getProperty(DocumentoCobranca.TRANSACAO).getValue().getAsObject();               	
            }else{ // se n�o tiver documento, obt�m as propriedades atrav�s do lancamento
            	dataDocumento = this.lancamento.getProperty(Lancamento.DATA).getValue().getAsCalendar();
            	if (this.lancamento.getProperty(Lancamento.VALOR).getValue().getAsBigDecimal().signum() > 0) // se o valor do lancamento for positivo, � um recebimento
            		transacao = Transacao.CREDITO;
            	else // sen�o se o valor for negativo, n�o � um recebimento
            		transacao = Transacao.DEBITO;
            }
            
            /* Cria um novo documento com os mesmos valores do documentoAntigo, a �nica exce��o � a categoria do documento de cobran�a*/
            ServiceData sdCriarDocumento = new ServiceData(CriarDocumentoCobrancaService.SERVICE_NAME, null);
            sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DOCUMENTO_COBRANCA_CATEGORIA, documentoCobrancaCategoria);
            sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_CONTRATO, this.lancamento.getProperty(Lancamento.CONTRATO).getValue().getAsEntity()); 
            sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_DOCUMENTO, dataDocumento);
            sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_VENCIMENTO, this.lancamento.getProperty(Lancamento.DATA_VENCIMENTO).getValue().getAsCalendar());
            sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_VALOR_DOCUMENTO, this.lancamento.getProperty(Lancamento.VALOR).getValue().getAsBigDecimal());
            sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_TRANSACAO, transacao);
            this.getProcessManager().getServiceManager().execute(sdCriarDocumento);

            this.documentoNovo = sdCriarDocumento.getFirstOutput();
            
            this.getMessageList().addAll(sdCriarDocumento.getMessageList());
        }else{
        	//caso a categoria n�o mude, manter o antigo documento
        	this.documentoNovo = this.lancamento.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().getAsEntity();
        }
    }
    
    public boolean isDocumentoNull(IEntity<DocumentoCobranca> documento){
    	return documento == null;
    }
    
    /**
     * Constr�i uma lista de Categorias de Documento de Cobran�a
     * 
     * @return uma lista de DocumentoCobrancaCategoria
     * @throws BusinessException
     */
    public List<SelectItem> getCategoriaList() throws BusinessException {
        IEntityList<DocumentoCobrancaCategoria> entityList = UtilsCrud.list(this.getProcessManager().getServiceManager(), DocumentoCobrancaCategoria.class, null);
        List<SelectItem> result = new ArrayList<SelectItem>(entityList.getSize());
        result.add(new SelectItem(IDAO.ENTITY_UNSAVED, NENHUM_DOCUMENTO_COBRANCA));
        for (IEntity<DocumentoCobrancaCategoria> entity : entityList) {
            result.add(new SelectItem(entity.getId(), entity.getPropertyValue(DocumentoCobrancaCategoria.NOME).toString()));
        }
        return result;
    }
    
    
    public List<IProperty> retrievePropriedadesEditaveis() throws BusinessException {
    	if (propriedadesEditaveis == null) {
            ServiceData sd = new ServiceData(ObterPropriedadesPreenchimentoManualService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(ObterPropriedadesPreenchimentoManualService.IN_DOCUMENTO, documentoNovo);
            this.getProcessManager().getServiceManager().execute(sd);

            propriedadesEditaveis = sd.getFirstOutput();
        }
        return propriedadesEditaveis;
    }
    
    public String getCategoriaAntigaAsString() throws BusinessException{
    	if (isDocumentoNull(documentoAntigo)) // se o documento antigo for null
    		return NENHUM_DOCUMENTO_COBRANCA;
   		return this.documentoAntigo.getProperty(DocumentoCobranca.DOCUMENTO_COBRANCA_CATEGORIA).getValue().getAsEntity().toString();
    }
    
    public String getCategoriaAsString() throws BusinessException{
    	if (isDocumentoNull(documentoNovo)) // se o documentoNovo for null, ou seja, n�o tiver um documento associado
    		return NENHUM_DOCUMENTO_COBRANCA;
    	return this.documentoNovo.getProperty(DocumentoCobranca.DOCUMENTO_COBRANCA_CATEGORIA).getValue().getAsEntity().toString(); 
    }

	public long getCategoriaId() {
		return categoriaId;
	}

    public void setCategoriaId(long documentoCobrancaId) throws BusinessException {
        if (this.categoriaId != documentoCobrancaId) {
            this.categoriaId = documentoCobrancaId;
            /* Avisa da mudan�a da categoria do documento de cobran�a */
            categoriaMudou();
        }
    }

	public long getLancamentoId() {
		return lancamentoId;
	}

	/* 
	 * O m�todo set � executado no in�cio, quando o �cone para alterar o documento � acessado pela interface.
	 * Dessa forma, o lan�amento j� � obtido no in�cio do processo, atrav�s do runInicializar(), e pode ser utilizado
	 * nos outros m�todos sem o problema de NullPointer. 
	 */
	public void setLancamentoId(long lancamentoId) throws BusinessException{
		if (this.lancamentoId != lancamentoId){
			this.lancamentoId = lancamentoId;
			runInicializar();
		}
	}

	public List<IProperty> getPropriedadesEditaveis() {return propriedadesEditaveis;}

	public Calendar getDataCancelamento() {return dataCancelamento;}
	public void setDataCancelamento(Calendar dataCancelamento) {this.dataCancelamento = dataCancelamento;}

	public IEntity getDocumentoAntigo() {return documentoAntigo;}

	public IEntity getDocumentoNovo() {return documentoNovo;}

	public IEntity getLancamento() {return lancamento;}
    
	/*==============================================================================
	 * IRunnableEntityProcess	
	 *==============================================================================*/
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();

		boolean result = false;

		/* Verifica se a entidade � compat�vel */
		if(entity.getInfo().getType() == Lancamento.class){
//			try {
				Lancamento oLancamento = (Lancamento) entity.getObject();
				this.lancamento = (IEntity<Lancamento>) entity;

				/* Alguns dados poderao ser inicializados aqui */
				this.lancamentoId = oLancamento.getId();

				result = true;
//			} catch (BusinessException e) {
//				this.getMessageList().addAll(e.getErrorList());
//			}
		}else
		{
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}

		return result;
	}
}
