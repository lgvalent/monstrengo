<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGrid cellpadding="3" rendered="#{estornarLancamentoMovimentoBean.process == null}">
	<h:messages/>
</h:panelGrid>

<h:form id="form" >
	<h:panelGrid width="100%" columns="2" cellpadding="3" styleClass="tableList" columnClasses="tableListColumn" rowClasses="tableListRowEven,tableListRowOdd">
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.conta.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.conta.nome}" />
 
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.data.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.data.time}" />
		
 		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.contrato.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.lancamento.contrato}" />

		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.descricao.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.descricao}" />
		
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.documentoPagamento.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.documentoPagamento}" />
		
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.valor.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.valor}">
			<f:convertNumber type="currency"/>
		</h:outputText>
		
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.multa.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.multa}">
			<f:convertNumber type="currency"/>
		</h:outputText>
		
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.juros.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.juros}">
			<f:convertNumber type="currency"/>
		</h:outputText>
		
<%-- 
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.acrescimo.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.acrescimo}">
			<f:convertNumber type="currency"/>
		</h:outputText>
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.desconto.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.desconto}">
			<f:convertNumber type="currency"/>
		</h:outputText>
 --%>		
		
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.valorTotal.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.valorTotal}">
			<f:convertNumber type="currency"/>
		</h:outputText>
		
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.lancamentoMovimentoCategoria.label}:"/>
		<h:outputText value="#{estornarLancamentoMovimentoBean.process.lancamentoMovimento.object.lancamentoMovimentoCategoria.nome}"/>
	</h:panelGrid>

	<h:panelGrid columns="3" cellpadding="1" style="font: 12px">
		<h:outputLabel value="Data de estorno"/>
		<h:inputText id="data" value="#{estornarLancamentoMovimentoBean.process.data.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" required="true">
			<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
		</h:inputText>
		<h:message styleClass="errorMessage" for="data"/>

		<h:outputLabel value="Motivo do estorno"/>
		<h:inputTextarea id="descricao" value="#{estornarLancamentoMovimentoBean.descricao}" rows="3" required="true"/>
		<h:message styleClass="errorMessage" for="descricao"/>

		<h:panelGroup>
			<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
			<h:commandButton value="Confirmar" action="#{estornarLancamentoMovimentoBean.doEstornar}"/>
		</h:panelGroup>
		<h:outputLabel value=""/>
		<h:outputLabel value=""/>
	</h:panelGrid>
</h:form>
