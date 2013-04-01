<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form" >
	<h:outputText escape="false" value="<br>Contrato: #{quitarLancamentoBean.process.lancamentos.first.object.contrato}" /> 
	<h:outputLabel value="Data de quitação"/>
	<h:outputText id="dataQuitacao" value="#{quitarLancamentoBean.process.dataQuitacao.time}">
			<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
	</h:outputText>	
	
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
					<h:selectBooleanCheckbox id="myCheck" value="#{item.selected}" disabled="true"/> 
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
					<h:outputText value="#{item.propertiesMap.conta.value.asString}"/>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.valor.label}"/>
   	                </f:facet>
					<f:facet name="footer">
						<h:outputText value="#{quitarLancamentoBean.process.valor}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputText>
					</f:facet>
					<h:outputText value="#{item.propertiesMap.valor.value.asString}"/>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.multa.label}"/>
					</f:facet>
					<f:facet name="footer">
						<h:outputText value="#{quitarLancamentoBean.process.multa}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputText>
					</f:facet>
					<h:outputText value="#{item.propertiesMap.multa.value.asString}"/>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.juros.label}"/>
					</f:facet>
					<f:facet name="footer">
						<h:outputText value="#{quitarLancamentoBean.process.juros}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputText>
					</f:facet>
					<h:outputText value="#{item.propertiesMap.juros.value.asString}"/>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.desconto.label}"/>
					</f:facet>
					<f:facet name="footer">
						<h:outputText value="#{quitarLancamentoBean.process.desconto}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputText>
					</f:facet>
					<h:outputText value="#{item.propertiesMap.desconto.value.asString}"/>
				</h:column>

				<h:column >
					<f:facet name="header">
						<h:outputText value="Total"/>
					</f:facet>
					<f:facet name="footer">
						<h:outputText value="#{quitarLancamentoBean.process.total}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputText>
					</f:facet>
					<h:outputText id="total" value="#{item.propertiesMap.valorTotal.value.asString}"/>
					<h:message styleClass="errorMessage" for="total"/>
				</h:column>

		</h:dataTable>

	<h:panelGrid width="100%" columns="3" cellpadding="3" style="font: 12px" styleClass="tableList" columnClasses="tableListColumn" rowClasses="tableListRowEven,tableListRowOdd">
		<h:outputLabel value="#{menuBean.infoMap.DocumentoPagamento.label}" />
		<h:outputText value="#{quitarLancamentoBean.process.documentoPagamento}" />
		<h:outputText value="#{quitarLancamentoBean.process.documentoPagamento.propertiesMap.valor.value.asString}" />
	</h:panelGrid>

	<h:panelGrid columns="2" style="font: 12px">
		<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
		<h:commandButton value="Finalizar" action="#{quitarLancamentoBean.actionPasso4}"/>
	</h:panelGrid>
</h:form>
	