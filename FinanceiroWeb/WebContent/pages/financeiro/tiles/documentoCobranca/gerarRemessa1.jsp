<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGrid cellpadding="3" rendered="#{gerarDocumentoCobrancaRemessaBean.process == null}">
	<h:messages/>
</h:panelGrid>
<h:form id="form" rendered="#{gerarDocumentoCobrancaRemessaBean.process != null}">
	<h:panelGrid columns="1">
		<h:outputLabel style="color:blue" value="Indique um convênio de cobrança e um período referente às datas de vencimento dos títulos bancários"/>
		<h:outputLabel style="color:blue" value="Todos os documentos de cobrança encontrados no período indicado serão inseridos no arquivo de remessa"/>
		<h:outputLabel value=" "/>
	</h:panelGrid>
	
	<h:panelGrid columns="3" cellpadding="3">
		<h:outputLabel value="#{menuBean.infoMap.ConvenioCobranca.label}"/>		
		<h:selectOneMenu id="convenioId" value="#{gerarDocumentoCobrancaRemessaBean.process.convenioId}">
			<f:selectItems value="#{gerarDocumentoCobrancaRemessaBean.process.cedentes}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="convenioId"/>
		
		<h:outputLabel value="Início Período"/>
		<h:panelGrid columns="2">
	 		<h:inputText id="inicioPeriodo" required="true" value="#{gerarDocumentoCobrancaRemessaBean.process.inicioPeriodo.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
			</h:inputText>
			<h:message styleClass="errorMessage" for="inicioPeriodo"/>
	  	</h:panelGrid>
		<h:outputLabel value=""/>
				
		<h:outputLabel value="Final Período"/>
		<h:panelGrid columns="2">		
	 		<h:inputText id="finalPeriodo" required="true" value="#{gerarDocumentoCobrancaRemessaBean.process.finalPeriodo.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
			</h:inputText>
			<h:message styleClass="errorMessage" for="finalPeriodo"/>
		</h:panelGrid>
	</h:panelGrid>
	
	<h:panelGrid columns="2">
		<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
		<h:commandButton value="Avançar >>" action="#{gerarDocumentoCobrancaRemessaBean.actionVisualizar}"/>
	</h:panelGrid>
</h:form>
	