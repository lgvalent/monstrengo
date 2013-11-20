package br.com.orionsoft.financeiro.documento.cobranca.processes;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;

import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.services.GerarDocumentoCobrancaRemessaService;
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
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo trabalha com um arquivo de remessa de títulos, arquivo este que será gerado ao Cedente correspondente
 * 
 * <p><b>Procedimentos:</b><br>
 * <br>Listar os Cedentes disponíveis
 * <br>Chamar o servico GerarDocumentoCobrancaRemessaService gerar o arquivo de remessa
 * 
 * @author Andre
 * @version 20060926
 *
 * @spring.bean id="GerarDocumentoCobrancaRemessaProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
@ProcessMetadata(label="Gerar arquivo de remessa", hint="Permite gerar um arquivo de remessa com os documentos de cobrança indicados no período", description="Gera um arquivo de remessa para ser enviado aos convênios de cobrança para impressão, registro e controles diversos de cobrança.")
public class GerarDocumentoCobrancaRemessaProcess extends ProcessBasic implements IRunnableEntityProcess{
    public static final String PROCESS_NAME = "GerarDocumentoCobrancaRemessaProcess";

    private File arquivoRemessa;
    private String nomeArquivoRemessa = null;
    private long convenioId = IDAO.ENTITY_UNSAVED;
    private Calendar inicioPeriodo = CalendarUtils.getCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), 01);
    private Calendar finalPeriodo = CalendarUtils.getCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
    /* 
     * esta propriedade indica se a remessa (títulos registrados no banco) utilizará protesto:
     * caso o campo esteja preenchido utilizar o valor de dias para protesto na remessa, 
     * caso não esteja preenchido (null) identificar na remessa que não utilizará protesto 
     */
    private Integer quantidadeDiasProtesto = null; 
    
    public String getProcessName() {
        return PROCESS_NAME;
    }
    
    public boolean runGerarRemessa(){
    	ServiceData sdGerarRemessa = new ServiceData(GerarDocumentoCobrancaRemessaService.SERVICE_NAME, null);
    	sdGerarRemessa.getArgumentList().setProperty(GerarDocumentoCobrancaRemessaService.IN_CONVENIO_ID, convenioId);
    	sdGerarRemessa.getArgumentList().setProperty(GerarDocumentoCobrancaRemessaService.IN_INICIO_PERIODO, inicioPeriodo);
    	sdGerarRemessa.getArgumentList().setProperty(GerarDocumentoCobrancaRemessaService.IN_FINAL_PERIODO, finalPeriodo);
    	if (this.quantidadeDiasProtesto != null){
    		sdGerarRemessa.getArgumentList().setProperty(GerarDocumentoCobrancaRemessaService.IN_QUANTIDADE_DIAS_PROTESTO_OPT, this.quantidadeDiasProtesto);
    	}
    		
    	this.getMessageList().clear();
    	try{
    		//executando o serviço
    		this.getProcessManager().getServiceManager().execute(sdGerarRemessa);
    		
    		this.arquivoRemessa = sdGerarRemessa.getFirstOutput();
    		this.nomeArquivoRemessa = arquivoRemessa.getName();
    		
    		//obtendo todas as mensagens durante o processamento
    		this.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, GerarDocumentoCobrancaRemessaProcess.class, "NOME_ARQUIVO_REMESSA", nomeArquivoRemessa));
    		this.getMessageList().addAll(sdGerarRemessa.getMessageList());
    		
            /* Grava a auditoria do processo */
			UtilsAuditorship.auditProcess(this,"arquivoRemessa='"+ nomeArquivoRemessa + "'", null);

			return true;
			
    	}catch (BusinessException e1) {
            //obtendo as mensagend de erro
    		this.getMessageList().addAll(e1.getErrorList());
            return false;
    	}
    }   
    
    /**
     * Constrói uma lista de Cedentes 
     * @return SelectItem dos cedentes cadastrados no banco
     * @throws BusinessException 
     */
    public List<SelectItem> getCedentes() throws BusinessException {
        IEntityList<ConvenioCobranca> entityList = UtilsCrud.list(this.getProcessManager().getServiceManager(), ConvenioCobranca.class, null);
        List<SelectItem> result = new ArrayList<SelectItem>(entityList.getSize());
        for (IEntity<ConvenioCobranca> entity : entityList) {
            result.add(new SelectItem(entity.getId(), entity.getPropertyValue(ConvenioCobranca.NOME).toString()));
        }
        return result;
    }
    
    public String getConvenioNome() throws BusinessException {
    	for(SelectItem item: this.getCedentes())
    		if((Long)item.getValue() == this.convenioId)
    			return item.getLabel();
    	
    	return "(Cedente não identificado:" +this.convenioId + ")";
    }

	public File getArquivoRemessa() { return arquivoRemessa; }

	public void setArquivoRemessa(File arquivoRemessa) { this.arquivoRemessa = arquivoRemessa;}

	public long getConvenioId() { return convenioId; }

	public void setConvenioId(long cedenteId) { this.convenioId = cedenteId; }

	public String getNomeArquivoRemessa() { return nomeArquivoRemessa; }

	public void setNomeArquivoRemessa(String fileName) { this.nomeArquivoRemessa = fileName; }

	public Calendar getFinalPeriodo() { return finalPeriodo; }

	public void setFinalPeriodo(Calendar finalPeriodo) { this.finalPeriodo = finalPeriodo; }

	public Calendar getInicioPeriodo() { return inicioPeriodo; }

	public void setInicioPeriodo(Calendar inicioPeriodo) { this.inicioPeriodo = inicioPeriodo; }

	public Integer getQuantidadeDiasProtesto() {return quantidadeDiasProtesto;}

	public void setQuantidadeDiasProtesto(Integer quantidadeDiasProtesto) {this.quantidadeDiasProtesto = quantidadeDiasProtesto;}
	
	/**
	 * Verifica se o número digitado tem no máximo 2 casas decimais
	 */
	public boolean isQuantidadeDiasProtestoValida(){
		if (this.quantidadeDiasProtesto != null){
			return this.quantidadeDiasProtesto < 100;
		}else{ //se for null indica que Protesto não será utilizado na remessa
			return true;
		}
	}

	/* IRunnableEntityProcess */
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();
		boolean result = false;
		/* Verifica se a entidade passada eh um DocumentoCobranca ou pertence eh descendente */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), DocumentoCobranca.class)) {
			try{
				this.convenioId = entity.getProperty(DocumentoCobranca.CONVENIO_COBRANCA).getValue().getId();

				result = true;
			} catch (BusinessException e) {
				this.getMessageList().addAll(e.getErrorList());
			}
		} else
			if (ClassUtils.isAssignable(entity.getInfo().getType(), DocumentoCobrancaCategoria.class)) {
				try{
					this.convenioId = entity.getProperty(DocumentoCobrancaCategoria.CONVENIO_COBRANCA).getValue().getId();

					result = true;
				} catch (BusinessException e) {
					this.getMessageList().addAll(e.getErrorList());
				}
			} else
				if (ClassUtils.isAssignable(entity.getInfo().getType(), ConvenioCobranca.class)) {
					this.convenioId = entity.getId();

					result = true;
		} else {
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		return result;
	}

}
