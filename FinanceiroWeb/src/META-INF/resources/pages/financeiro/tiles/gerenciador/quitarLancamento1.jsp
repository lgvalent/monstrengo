<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGrid cellpadding="3" rendered="#{quitarLancamentoBean.process == null}">
	<h:messages/>
</h:panelGrid>

<h:form id="form">
	<h:panelGrid columns="3" cellpadding="3" style="font: 12px">
 		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.lancamento.label}"/>
 		<h:panelGrid columns="2">
 			<h:graphicImage value="../basic/img/query_open_select.png" title="Dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
			<h:inputText id="lancamento"
				value="#{quitarLancamentoBean.process.lancamentos.first.id}" 
				size="5" maxlength="5" rendered="true"
				title="Dê um clique na caixa de texto para abrir a pesquisa"
				styleClass="queryInputSelectOne"
				onblur="javascript: if(this.value=='')this.value='0';"
				onclick="javascript:openSelectOneId('#{menuBean.infoMap.Lancamento.type.name}','',this)" />
		</h:panelGrid>
		<h:message styleClass="errorMessage" for="lancamento"/>
	</h:panelGrid>

	<h:panelGrid columns="2" style="font: 12px">
		<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
		<h:commandButton value="Avançar >>" action="#{quitarLancamentoBean.actionPasso1}"/>
	</h:panelGrid>
</h:form>
	