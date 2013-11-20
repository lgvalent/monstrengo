package br.com.orionsoft.financeiro.documento.cobranca.processes;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;

import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.services.ReceberDocumentoCobrancaRetornoService;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultadoSumario;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo trabalha com um arquivo de retorno de títulos, obtido através de um upload do usuário
 * 
 * <p><b>Procedimentos:</b><br>
 * <br>Obter o InputStream referente ao upload do arquivo
 * <br>Chamar o servico ReceberDocumentoCobrancaRetornoProcess para ler o arquivo de retorno
 * 
 * @author Andre
 * @version 20061006
 *
 * @spring.bean id="ReceberDocumentoCobrancaRetornoProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
@ProcessMetadata(label="Processar arquivo de retorno", hint="Permite ler um arquivo de retorno e processar os recebimentos indicados", description="Processa um arquivo de retorno gerado pelos diversos convênios de cobranças bancárias ou de terceiros. As informações dos arquivos serão analisadas e os documentos de cobrança serão atualizados")
public class ReceberDocumentoCobrancaRetornoProcess extends ProcessBasic implements IRunnableEntityProcess {
    public static final String PROCESS_NAME = "ReceberDocumentoCobrancaRetornoProcess";

    /* 
     * O File será salvo primeiramente no Bean como UploadedFile; então o Bean converte o InputStream deste UploadedFile 
     * para um File e passa para o Processo, desta forma, na interface (JSF) o bean deve ser setado primeiro, pois este
     * então seta o Process 
     */
    private InputStream inputStream;
    private String nomeRetorno;
    private long convenioCobrancaId = IDAO.ENTITY_UNSAVED;
    private DocumentoRetornoResultadoSumario retornoSumario;
    
    public String getProcessName() {
        return PROCESS_NAME;
    }
    
    public boolean runReceberRetorno(){
    	super.beforeRun();
    	
    	ServiceData sdReceberRetorno = new ServiceData(ReceberDocumentoCobrancaRetornoService.SERVICE_NAME, null);
    	sdReceberRetorno.getArgumentList().setProperty(ReceberDocumentoCobrancaRetornoService.IN_CONVENIO_COBRANCA_ID, convenioCobrancaId);
    	sdReceberRetorno.getArgumentList().setProperty(ReceberDocumentoCobrancaRetornoService.IN_INPUT_STREAM, inputStream);

    	try{
    		//executando o serviço
    		this.getProcessManager().getServiceManager().execute(sdReceberRetorno);
    		
    		this.retornoSumario = sdReceberRetorno.getFirstOutput();
    		
    		//obtendo todas as mensagens durante o processamento
    		this.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, ReceberDocumentoCobrancaRetornoProcess.class, "NOME_ARQUIVO_RETORNO", this.nomeRetorno));
    		this.getMessageList().addAll(sdReceberRetorno.getMessageList());
    		
            /* Grava a auditoria do processo */
			UtilsAuditorship.auditProcess(this,"retorno='"+ nomeRetorno + "'", null);

			return true;
    	}catch (BusinessException e1) {
            //obtendo as mensagend de erro
    		this.getMessageList().addAll(e1.getErrorList());
            return false;
    	}
    }   
    
    /**
     * Constrói uma lista de ConvenioCobrancaList
     * @return SelectItem dos cedentes cadastrados no banco
     * @throws BusinessException 
     */
    public List<SelectItem> getConvenioCobrancaList() throws BusinessException {
        IEntityList<ConvenioCobranca> entityList = UtilsCrud.list(this.getProcessManager().getServiceManager(), ConvenioCobranca.class, null);
        List<SelectItem> result = new ArrayList<SelectItem>(entityList.getSize());
        for (IEntity<ConvenioCobranca> entity : entityList) {
            result.add(new SelectItem(entity.getId(), entity.getPropertyValue(ConvenioCobranca.NOME).toString()));
        }
        return result;
    }
    
    public String getConvenioCobrancaNome() throws BusinessException {
    	for(SelectItem item: this.getConvenioCobrancaList())
    		if((Long)item.getValue() == this.convenioCobrancaId)
    			return item.getLabel();
    	
    	return "(Convênio Cobrança não identificado:" +this.convenioCobrancaId + ")";
    }
    
    public InputStream getInputStream() { return inputStream; }
    public void setInputStream(InputStream inputStream) {this.inputStream = inputStream; }
    
	public long getConvenioCobrancaId() { return convenioCobrancaId; }
	public void setConvenioCobrancaId(long convenioCobrancaId) { this.convenioCobrancaId = convenioCobrancaId; }

	public DocumentoRetornoResultadoSumario getRetornoSumario() {
		return retornoSumario;
	}

	public String getNomeRetorno() {
		return nomeRetorno;
	}

	public void setNomeRetorno(String nomeRetorno) {
		this.nomeRetorno = nomeRetorno;
	}

	/* IRunnableEntityProcess */
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();
		boolean result = false;
		/* Verifica se a entidade passada eh um DocumentoCobranca ou pertence eh descendente */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), DocumentoCobranca.class)) {
			try{
				this.convenioCobrancaId = entity.getProperty(DocumentoCobranca.CONVENIO_COBRANCA).getValue().getId();

				result = true;
			} catch (BusinessException e) {
				this.getMessageList().addAll(e.getErrorList());
			}
		} else
			if (ClassUtils.isAssignable(entity.getInfo().getType(), DocumentoCobrancaCategoria.class)) {
				try{
					this.convenioCobrancaId = entity.getProperty(DocumentoCobrancaCategoria.CONVENIO_COBRANCA).getValue().getId();

					result = true;
				} catch (BusinessException e) {
					this.getMessageList().addAll(e.getErrorList());
				}
			} else
				if (ClassUtils.isAssignable(entity.getInfo().getType(), ConvenioCobranca.class)) {
					this.convenioCobrancaId = entity.getId();

					result = true;
		} else {
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		return result;
	}

}
