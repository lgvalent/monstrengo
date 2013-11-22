<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGrid cellpadding="3" rendered="#{inserirTransferenciaBean.process == null}">
	<h:messages/>
</h:panelGrid>

<h:form id="form" rendered="#{inserirTransferenciaBean.process != null}">
	<h:panelGrid columns="3" cellpadding="3">

		<h:outputLabel value="De"/>
		<h:selectOneMenu id="contaDe" value="#{inserirTransferenciaBean.process.contaDe}" disabled="#{inserirTransferenciaBean.process.transferenciaDe}">
			<f:selectItems value="#{inserirTransferenciaBean.process.listConta}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="contaDe"/>

		<h:outputLabel value="Para"/>
		<h:selectOneMenu id="contaPara" value="#{inserirTransferenciaBean.process.contaPara}" disabled="#{inserirTransferenciaBean.process.transferenciaPara}">
			<f:selectItems value="#{inserirTransferenciaBean.process.listConta}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="contaPara"/>

 		<h:outputLabel value="#{menuBean.infoMap.Grupo.propertiesMetadata.contrato.label}"/>
 		<h:panelGrid columns="2">
 			<h:graphicImage value="../../public/basic/img/query_open_select.png" title="Dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
			<h:inputText id="contrato"
				disabled="#{inserirTransferenciaBean.transferenciaMovimento}"
				value="#{inserirTransferenciaBean.process.contrato}"
				size="5" maxlength="5" rendered="true"
				title="Dê um clique na caixa de texto para abrir a pesquisa"
				styleClass="queryInputSelectOne"
				onblur="javascript: if(this.value=='')this.value='0';"
				onclick="javascript:openSelectOneId('br.com.orionsoft.basic.entities.Contrato','',this)" />
		</h:panelGrid>
		<h:message styleClass="errorMessage" for="contrato"/>

		<h:outputLabel value="Data da transferência"/>
		<h:inputText id="dataLancamento" value="#{inserirTransferenciaBean.process.dataLancamento}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')"/>
		<h:message styleClass="errorMessage" for="dataLancamento"/>

		<h:outputLabel value="#{menuBean.infoMap.Grupo.propertiesMetadata.descricao.label}"/>
		<h:inputTextarea id="descricao" value="#{inserirTransferenciaBean.process.descricao}" rows="3" disabled="#{inserirTransferenciaBean.transferenciaMovimento}"/>
		<h:message styleClass="errorMessage" for="descricao"/>

		<h:outputLabel value="#{menuBean.infoMap.Movimento.propertiesMetadata.valor.label}"/>
		<h:inputText id="valor" value="#{inserirTransferenciaBean.process.valor}" onkeypress="return keyPressFloat(this, event)" disabled="#{inserirTransferenciaBean.transferenciaMovimento}"/>
		<h:message styleClass="errorMessage" for="valor"/>

		<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
		<h:commandButton value="Inserir" action="#{inserirTransferenciaBean.actionInserir}"/>
		<h:outputLabel value=""/>
	</h:panelGrid>
	<h:outputText styleClass="errorMessage" value="#{messagesBean.firstMessageSummary}" escape="false" />
</h:form>
