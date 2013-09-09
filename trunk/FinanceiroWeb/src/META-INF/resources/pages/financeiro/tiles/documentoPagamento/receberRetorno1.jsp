<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>

<h:panelGrid cellpadding="3" rendered="#{receberDocumentoCobrancaRetornoBean.process == null}">
	<h:messages/>
</h:panelGrid>
	<h:messages/>

<%-- enctype="multipart/form-data" é necessário para o inputFileUpload do MyFaces --%>
<h:form id="form" rendered="#{receberDocumentoCobrancaRetornoBean.process != null}" enctype="multipart/form-data">
	<h:panelGrid columns="1">
		<h:outputLabel style="color:blue" value="Indique um convênio de cobrança e o arquivo de retorno a ser processado"/>
		<h:outputLabel value=" "/>
	</h:panelGrid>

	<h:panelGrid columns="3" cellpadding="3">
		<h:outputLabel value="#{menuBean.infoMap.ConvenioCobranca.label}"/>		
		<h:selectOneMenu id="convenioCobrancaId" value="#{receberDocumentoCobrancaRetornoBean.process.convenioCobrancaId}">
			<f:selectItems value="#{receberDocumentoCobrancaRetornoBean.process.convenioCobrancaList}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="convenioCobrancaId"/>
		
        <h:outputLabel for="myFileId" value="Arquivo "/>
        <%-- O uploadFile deve ser setado primeiramente no Bean; logo após o próprio Bean faz a conversão pra File e seta o Processo --%>
		<t:inputFileUpload id="myFileId"
        	value="#{receberDocumentoCobrancaRetornoBean.arquivoRetorno}"
		    storage="file"
        	size="40"
		    required="true"
		    title="Selecione o Arquivo de Retorno a ser processado" />
        <h:message styleClass="errorMessage" for="myFileId"/>
	</h:panelGrid>
		
	<h:panelGrid columns="2">
		<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
		<h:commandButton value="Avançar >>" action="#{receberDocumentoCobrancaRetornoBean.actionVisualizar}"/>
	</h:panelGrid>
</h:form>
	