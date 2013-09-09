<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGrid cellpadding="3" rendered="#{cancelarLancamentoBean.process == null}">
	<h:messages/>
</h:panelGrid>

<h:form id="form" >
	<h:panelGrid 
	  width="100%" 
	  columns="2" 
	  cellpadding="3" 
	  styleClass="tableList" 
	  columnClasses="tableListColumn" 
	  rowClasses="tableListRowEven,tableListRowOdd"
	  rendered="#{cancelarLancamentoBean.process.lancamento != null}">
		<h:outputLabel value="Selecionar:"/>
		<h:selectBooleanCheckbox value="#{cancelarLancamentoBean.process.lancamento.selected}"/>
	  
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.contaPrevista.label}:"/>
		<h:outputText value="#{cancelarLancamentoBean.process.lancamento.object.contaPrevista.nome}" />
 
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.data.label}:"/>
		<h:outputText value="#{cancelarLancamentoBean.process.lancamento.object.data.time}" />
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.dataVencimento.label}:"/>
		<h:outputText value="#{cancelarLancamentoBean.process.lancamento.object.dataVencimento.time}" />
		
 		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.contrato.label}:"/>
		<h:outputText value="#{cancelarLancamentoBean.process.lancamento.object.contrato}" />

		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.descricao.label}:"/>
		<h:outputText value="#{cancelarLancamentoBean.process.lancamento.object.descricao}" />
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.documentoCobranca.label}:"/>
		<h:outputText value="#{cancelarLancamentoBean.process.lancamento.object.documentoCobranca}" />
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.valor.label}:"/>
		<h:outputText value="#{cancelarLancamentoBean.process.lancamento.object.valor}">
			<f:convertNumber type="currency"/>
		</h:outputText>
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.saldo.label}:"/>
		<h:outputText value="#{cancelarLancamentoBean.process.lancamento.object.saldo}">
			<f:convertNumber type="currency"/>
		</h:outputText>
	</h:panelGrid>

	<h:dataTable 
		value="#{cancelarLancamentoBean.process.lancamentos}"
		var="item"
		width="100%" 
		cellpadding="3" 
		styleClass="tableList" 
		columnClasses="tableListColumn" 
		rowClasses="tableListRowEven,tableListRowOdd"
		>
	  <h:column>
		<h:selectBooleanCheckbox value="#{item.selected}"/>
	  </h:column>
	  <h:column>
	  	<f:facet name="header">
			<h:outputText value="Descriçao" />
	  	</f:facet>	
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.contaPrevista.label}:"/>
		<h:outputText value="#{item.object.contaPrevista.nome}" />
 
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.data.label}:"/>
		<h:outputText value="#{item.object.data.time}" />
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.dataVencimento.label}:"/>
		<h:outputText value="#{item.object.dataVencimento.time}" />
		
 		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.contrato.label}:"/>
		<h:outputText value="#{item.object.contrato}" />

		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.descricao.label}:"/>
		<h:outputText value="#{item.object.descricao}" />
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.documentoCobranca.label}:"/>
		<h:outputText value="#{item.object.documentoCobranca}" />
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.valor.label}:"/>
		<h:outputText value="#{item.object.valor}">
			<f:convertNumber type="currency"/>
		</h:outputText>
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.saldo.label}:"/>
		<h:outputText value="#{item.object.saldo}">
			<f:convertNumber type="currency"/>
		</h:outputText>
	  </h:column>
	</h:dataTable>


	<h:panelGrid columns="3" cellpadding="1" style="font: 12px">
		<h:outputLabel value="Data de cancelamento"/>
		<h:inputText id="data" value="#{cancelarLancamentoBean.process.data.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" required="true">
			<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
		</h:inputText>
		<h:message styleClass="errorMessage" for="data"/>

		<h:outputLabel value="Motivo do cancelamento"/>
		<h:inputTextarea id="descricao" value="#{cancelarLancamentoBean.descricao}" rows="3" required="true"/>
		<h:message styleClass="errorMessage" for="descricao"/>

		<h:panelGroup>
			<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
			<h:commandButton value="Confirmar" action="#{cancelarLancamentoBean.doCancelar}"/>
		</h:panelGroup>
		<h:outputLabel value=""/>
		<h:outputLabel value=""/>
	</h:panelGrid>
</h:form>
