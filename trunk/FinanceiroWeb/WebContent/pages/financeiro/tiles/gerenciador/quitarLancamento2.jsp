<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

	
	<h:messages layout="table" styleClass="infoMessage"/>
	<h:outputLabel value="Os valores de multa e juros s&atilde;o calculados de acordo com os par&acirc;metro definidos na categoria do documento de cobran&ccedil;a deste lançamento." styleClass="infoMessage"/>
	<h:outputText escape="false" value="<br>Contrato: #{quitarLancamentoBean.process.lancamentos.first.object.contrato}" /> 
	<h:form id="form" rendered="#{quitarLancamentoBean.process != null}">

		<h:outputLabel value="Data de quitação: "/>
		<h:inputText id="dataQuitacao" value="#{quitarLancamentoBean.process.dataQuitacao.time}" required="true" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" onfocus="setActiveComponent(this);">
			<%-- a4j:support event="onchange" reRender="tableMovimentos" action="#{quitarLancamentoBean.doCalcularMultaJuros}" ajaxSingle="false" oncomplete="getActiveComponent()"/--%>
			<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
		</h:inputText>	
		<h:message styleClass="errorMessage" for="dataQuitacao"/>
	
		<h:dataTable id="tableMovimentos"
		  			 border="1" 
		             value="#{quitarLancamentoBean.process.movimentos.array}" 
		             var='item'
		             headerClass="tableListHeader"
		             footerClass="tableListFooter"
		             styleClass="tableList"
		             rowClasses="tableListRowEven,tableListRowOdd"
		             columnClasses="tableListColumn"
		             style="border-collapse: collapse"
		             width="100%">
				<h:column >
					<h:selectBooleanCheckbox id="myCheck" value="#{item.selected}" onfocus="setActiveComponent(this);">
						<%-- a4j:support event="onblur" reRender="tableMovimentos" action="#{quitarLancamentoBean.doCalcularMultaJuros}" ajaxSingle="false" oncomplete="getActiveComponent();"/--%>
					</h:selectBooleanCheckbox>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="Lançamento"/>
   	                </f:facet>
					<h:outputText value="#{item.object.lancamento}"/>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.conta.label}"/>
   	                </f:facet>
					<h:selectOneMenu id="conta" value="#{item.propertiesMap.conta.value.id}" style="size:30px" onfocus="setActiveComponent(this);">
						<f:selectItems value="#{quitarLancamentoBean.process.listConta}"/>
					</h:selectOneMenu>
					<h:message styleClass="errorMessage" for="conta"/>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.valor.label}"/>
   	                </f:facet>
					<f:facet name="footer">
						<h:outputText value="#{quitarLancamentoBean.process.valor}">
						</h:outputText>
					</f:facet>
					<h:outputText value="Máx:#{item.propertiesMap.lancamento.value.asEntity.propertiesMap.saldo.value.asString}"/>
					<h:inputText id="valor" value="#{item.propertiesMap.valor.value.asString}" size="10" onkeypress="return keyPressFloat(this,event)" onfocus="setActiveComponent(this);">
						<%-- a4j:support event="onblur" reRender="tableMovimentos" action="#{quitarLancamentoBean.doCalcularMultaJuros}" ajaxSingle="false" oncomplete="getActiveComponent();"/--%>
					</h:inputText>
					<h:message styleClass="errorMessage" for="valor"/>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.multa.label}"/>
					</f:facet>
					<f:facet name="footer">
						<h:outputText value="#{quitarLancamentoBean.process.multa}">
						</h:outputText>
					</f:facet>
					<h:outputText value="#{item.object.lancamento.documentoCobranca.documentoCobrancaCategoria.multaAtraso}%"/>
					<h:inputText id="multa" value="#{item.propertiesMap.multa.value.asString}" size="10" onkeypress="return keyPressFloat(this,event)" onfocus="setActiveComponent(this);">
						<%-- a4j:support event="onblur" reRender="tableMovimentos" action="#{quitarLancamentoBean.doCalcularMultaJuros}" ajaxSingle="false" oncomplete="getActiveComponent();"/--%>
					</h:inputText>
					<h:message styleClass="errorMessage" for="multa"/>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.juros.label}"/>
					</f:facet>
					<f:facet name="footer">
						<h:outputText value="#{quitarLancamentoBean.process.juros}">
						</h:outputText>
					</f:facet>
					<h:outputText value="#{item.object.lancamento.documentoCobranca.documentoCobrancaCategoria.jurosMora}%/mês"/>
					<h:inputText id="juros" value="#{item.propertiesMap.juros.value.asString}" size="10" onkeypress="return keyPressFloat(this,event)" onfocus="setActiveComponent(this);">
						<%-- a4j:support event="onblur" reRender="tableMovimentos" action="#{quitarLancamentoBean.doCalcularMultaJuros}" ajaxSingle="false" oncomplete="getActiveComponent();"/--%>
					</h:inputText>
					<h:message styleClass="errorMessage" for="juros"/>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.desconto.label}"/>
					</f:facet>
					<f:facet name="footer">
						<h:outputText value="#{quitarLancamentoBean.process.desconto}">
						</h:outputText>
					</f:facet>
					<h:inputText id="desconto" value="#{item.propertiesMap.desconto.value.asString}" size="10" onkeypress="return keyPressFloat(this,event)" onfocus="setActiveComponent(this);">
						<%-- a4j:support event="onblur" reRender="tableMovimentos" action="#{quitarLancamentoBean.doCalcularMultaJuros}" ajaxSingle="false" oncomplete="getActiveComponent();"/--%>
					</h:inputText>
					<h:message styleClass="errorMessage" for="desconto"/>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="Total"/>
					</f:facet>
					<f:facet name="footer">
						<h:outputText value="#{quitarLancamentoBean.process.total}">
						</h:outputText>
					</f:facet>
					<h:outputText id="total" value="#{item.propertiesMap.valorTotal.value.asString}"/>
					<h:message styleClass="errorMessage" for="total"/>
				</h:column>
			</h:dataTable>
		<h:panelGrid styleClass="infoMessage">
			<h:commandButton value="Recalcular valores" onfocus="setActiveComponent(this);" action="#{quitarLancamentoBean.doCalcularMultaJuros}"/>
			<h:outputText value="Instruções:"/>
			<h:outputText value="#{quitarLancamentoBean.process.lancamentos.first.object.documentoCobranca.documentoCobrancaCategoria.instrucoes0}"/>
			<h:outputText value="#{quitarLancamentoBean.process.lancamentos.first.object.documentoCobranca.documentoCobrancaCategoria.instrucoes1}"/>
			<h:outputText value="#{quitarLancamentoBean.process.lancamentos.first.object.documentoCobranca.documentoCobrancaCategoria.instrucoes2}"/>
		</h:panelGrid>
	
		<h:panelGrid columns="4" style="font: 12px">
			<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();" onfocus="setActiveComponent(this);"/>
			<h:commandButton value="Reiniciar" action="#{quitarLancamentoBean.doReiniciarValores}" onfocus="setActiveComponent(this);" title="Reinicia os valores descartando as alterações realizadas até o momento"/>
			<h:commandButton value="Avançar >>" action="#{quitarLancamentoBean.actionPasso2}" onfocus="setActiveComponent(this);"/>
			<h:commandButton value="Finalizar" action="#{quitarLancamentoBean.actionPasso4}" onfocus="setActiveComponent(this);"/>
		</h:panelGrid>
	</h:form>
	