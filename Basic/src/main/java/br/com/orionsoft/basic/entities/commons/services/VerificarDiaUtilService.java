package br.com.orionsoft.basic.entities.commons.services;

import java.util.Calendar;

import br.com.orionsoft.basic.entities.commons.Feriado;
import br.com.orionsoft.basic.entities.commons.FeriadoRecesso;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este servi�o � respons�vel por verificar se determinada data � feriado ou recesso.
 * 
 *<p><b>Argumentos:</b>
 * <br> IN_DATA: Uma data que ser� verificada(Calendar).
 * <br> IN_IGNORAR_SABADO_OPT: Verifica se a data � um s�bado(Boolean).
 * <br> IN_IGNORAR_DOMINGO_OPT = Verifica se a data � um domingo(Boolean).
 * <br> IN_IGNORAR_FERIADO_OPT = Verifica se a data � um feriado(Boolean).
 * <br> IN_IGNORAR_RECESSO_OPT = Verifica se a data � um recesso(Boolean).
  *  
 * <p><b>Procedimento:</b>
 * 
 * <p><b>Retorno:</b>
 * <br>Se nenhum filtro for fornecido ser� retornada uma cole��o vazia.
 * <br><b>OUT_DIA_UTIL</b>: Retorna se o dia verificado � �til(Boolean).
 * <br><b>OUT_PROXIMO_DIA_UTIL</b>: Retorna o pr�ximo dia �til � data verificada se esta n�o for dia �til(Calendar).
 * <br><b>OUT_DIA_UTIL_ANTERIOR</b>: Retorna o dia anterior � data verificada se esta n�o for dia �til(Calendar).
 * 
 * @author guilherme
 * @version 20070625
 * 
 * @spring.bean id="VerificarDiaUtilService" init-method="registerService" 
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class VerificarDiaUtilService extends ServiceBasic {
	
	public static final String SERVICE_NAME = "VerificarDiaUtilService";
	public static final String IN_DATA = "data";
	public static final String IN_IGNORAR_SABADO_OPT = "ignorarSabado";
	public static final String IN_IGNORAR_DOMINGO_OPT = "ignorarDomingo";
	public static final String IN_IGNORAR_FERIADO_OPT = "ignorarFeriado";
	public static final String IN_IGNORAR_RECESSO_OPT = "ignorarRecesso";
	
	public static final Integer OUT_DIA_UTIL = 0;
	public static final Integer OUT_DIA_UTIL_ANTERIOR = 1;
	public static final Integer OUT_DIA_UTIL_PROXIMO = 2;
	
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execu��o do servico VerificarDiaUtilService");
		
		try{
			/* Preparando os argumentos passados */
			Calendar inData = (Calendar)serviceData.getArgumentList().getProperty(IN_DATA);
			
			Boolean inIgnorarSabado = false;
			if(serviceData.getArgumentList().containsProperty(IN_IGNORAR_SABADO_OPT))
				inIgnorarSabado = (Boolean) serviceData.getArgumentList().getProperty(IN_IGNORAR_SABADO_OPT);
			
			Boolean inIgnorarDomingo = false;
			if(serviceData.getArgumentList().containsProperty(IN_IGNORAR_DOMINGO_OPT))
				inIgnorarDomingo = (Boolean) serviceData.getArgumentList().getProperty(IN_IGNORAR_DOMINGO_OPT);
			
			Boolean inIgnorarFeriado = false;
			if(serviceData.getArgumentList().containsProperty(IN_IGNORAR_FERIADO_OPT))
				inIgnorarFeriado = (Boolean) serviceData.getArgumentList().getProperty(IN_IGNORAR_FERIADO_OPT);
			
			Boolean inIgnorarRecesso = false;
			if(serviceData.getArgumentList().containsProperty(IN_IGNORAR_RECESSO_OPT))
				inIgnorarRecesso = (Boolean) serviceData.getArgumentList().getProperty(IN_IGNORAR_RECESSO_OPT);

			
			boolean isDiaUtil = isDiaUtil(serviceData, inData, inIgnorarSabado, inIgnorarDomingo, inIgnorarFeriado, inIgnorarRecesso);
			

			boolean isDiaUtilAnterior = isDiaUtil;
			Calendar diaUtilAnterior = (Calendar) inData.clone();
			while(!isDiaUtilAnterior) {
				diaUtilAnterior.add(Calendar.DATE, -1);
				isDiaUtilAnterior = isDiaUtil(serviceData, diaUtilAnterior, inIgnorarSabado, inIgnorarDomingo, inIgnorarFeriado, inIgnorarRecesso);
			}
			
			boolean isDiaUtilProximo = isDiaUtil;
			Calendar diaUtilProximo = (Calendar) inData.clone();
			while(!isDiaUtilProximo) {
				diaUtilProximo.add(Calendar.DATE, 1);
				isDiaUtilProximo = isDiaUtil(serviceData, diaUtilProximo, inIgnorarSabado, inIgnorarDomingo, inIgnorarFeriado, inIgnorarRecesso);
			}

			// retorna a entidade Documento que o gerenciador criou;			
			log.debug("::Fim da execu��o do servi�o");
			serviceData.getOutputData().add(isDiaUtil);
			serviceData.getOutputData().add(diaUtilAnterior);
			serviceData.getOutputData().add(diaUtilProximo);
			
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/* O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros. */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			
			/* Indica que o servi�o falhou por causa de uma exce��o do hibernate. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
		
	}

	private boolean isDiaUtil(ServiceData serviceData, Calendar inData, Boolean inIgnorarSabado, Boolean inIgnorarDomingo, Boolean inIgnorarFeriado, Boolean inIgnorarRecesso) throws BusinessException {
		/* Separa o dia e o m�s */
		int dia = inData.get(Calendar.DAY_OF_MONTH);
		int mes = inData.get(Calendar.MONTH) + 1;
		int ano = inData.get(Calendar.YEAR);
		Boolean isDiaUtil = true;
		
		if(!inIgnorarSabado)
			if(inData.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
				isDiaUtil = false;
		
		
		if(!inIgnorarDomingo)
			if(inData.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
				isDiaUtil = false;
			
					
		if(!inIgnorarFeriado){
		/* select * from Feriado f
		 * where (f.dia = dia) and (f.mes=mes) 
		 * 
		 */ 
			IEntityList<Feriado> feriados = UtilsCrud.list(this.getServiceManager(),
									Feriado.class,
									Feriado.DIA +"=" + dia + " and " + Feriado.MES + "=" + mes + " and " + Feriado.ANO + "=" + ano,
									serviceData);
			if(!feriados.isEmpty())
				isDiaUtil = false;
				
		}
		
		if(!inIgnorarRecesso){
			/* select * from Recesso r
			 * where (r.dia <= dia) and (r.mes <= mes) and (r.diaFinal >= dia) and (r.mesFinal >= mes)  
			 * 
			 */ 
			
			IEntityList<FeriadoRecesso> recessos = UtilsCrud.list(this.getServiceManager(),
									FeriadoRecesso.class,
									FeriadoRecesso.DIA +"<=" + dia + " and " + FeriadoRecesso.MES +"<=" + mes + " and " + FeriadoRecesso.DIA_FINAL +">=" + dia + " and " +FeriadoRecesso.MES_FINAL +">=" +mes,
									serviceData);
			
			if(!recessos.isEmpty())
				isDiaUtil = false;
		}
		return isDiaUtil;
	}
}
