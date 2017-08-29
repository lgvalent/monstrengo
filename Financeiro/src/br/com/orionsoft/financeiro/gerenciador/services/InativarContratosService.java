package br.com.orionsoft.financeiro.gerenciador.services;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.MaskFormatter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.Observacoes;
import br.com.orionsoft.basic.entities.commons.Frequencia;
import br.com.orionsoft.basic.entities.endereco.Telefone;
import br.com.orionsoft.basic.entities.pessoa.EscritorioContabil;
import br.com.orionsoft.basic.entities.pessoa.Juridica;
import br.com.orionsoft.financeiro.utils.UtilsJuros;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.auditorship.support.EntityAuditValue;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.service.ServiceResultBean;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.core.util.NativeSQL;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

/**
 * Serviço que inativa uma lista de contratos.
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b>
 * 
 * @author Lucio
 * @version 20170829
 */
public class InativarContratosService extends ServiceBasic {

	public static final String SERVICE_NAME = "InativarContratosService";
	public static final String IN_CONTRATO_ID_SET = "contratoIdSet";
	public static final String IN_DATA_RESCISAO = "dataRescisao";
	public static final String IN_OBSERVACAO = "observacao";
	public static final String IN_USER_SESSION = "userSession";

	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("Iniciando a execução do serviço InativarContratosService");
		/*
		 * Parâmetros obrigatórios
		 */
		Set<Long> inContratoIdSet = (Set<Long>) serviceData.getArgumentList().getProperty(IN_CONTRATO_ID_SET);
		Calendar inDataRescisao = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_RESCISAO);
		String inObservacao = (String) serviceData.getArgumentList().getProperty(IN_OBSERVACAO);
		UserSession inUserSession = (UserSession) serviceData.getArgumentList().getProperty(IN_USER_SESSION);

		try {
			for(Long id: inContratoIdSet){
				IEntity<Contrato> contrato = UtilsCrud.retrieve(getServiceManager(), Contrato.class, id, serviceData);
	            log.debug("Inativando contrato: " + contrato);
				EntityAuditValue auditValue = new EntityAuditValue(contrato);
				contrato.getObject().setInativo(true);
				contrato.getObject().setDataRescisao(inDataRescisao);
				if(contrato.getObject().getObservacoes() == null)
					contrato.getObject().setObservacoes(new Observacoes());
				contrato.getObject().getObservacoes().setObservacoes(contrato.getObject().getObservacoes().getObservacoes() + " [Inativado: " + inObservacao + "]");
				
				UtilsAuditorship.auditUpdate(getServiceManager(), inUserSession, auditValue, serviceData);
				UtilsCrud.update(getServiceManager(), contrato, serviceData);
			}
		} catch (BusinessException e) {
            log.fatal(e.getMessage());
            /* Indica que o serviço falhou por causa de uma exceção da SQL. */
            throw new ServiceException(e.getErrorList());
		}
		
		addInfoMessage(serviceData, "SUCESSO", inContratoIdSet.size());

		
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

}
