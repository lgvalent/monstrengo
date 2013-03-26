<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

	<h:outputLabel value="Indique quais propriedades serão exibidas no relatório da pesquisa:" style="color: #EE2C2C; font-size: smaller;"/>
	<f:verbatim>
		<div  style="height: 200px; overflow: auto;">
	</f:verbatim>
	<h:dataTable 
			 id="tbr"
			 width="100%"  
			 value="#{queryBean.currentProcess.userReport.resultParam.condictions}"
			 var='item' 
			 headerClass="tableAdvancedReportHeader" 
			 styleClass="tableCollectionAdvancedReport" 
			 rowClasses="tableAdvancedReportRowEven,tableAdvancedReportRowOdd" 
			 columnClasses="tableAdvancedReportColumnOdd"
			 style="border-collapse: collapse;" 
			 rendered="#{queryBean.currentProcess.userReport.resultParam.hasCondictions}"
			 >

			<h:column>
				<f:facet name="header">
					<h:outputLabel value="Exibir:" />
				</f:facet>

	  			<h:selectBooleanCheckbox id="myCheck" value="#{item.visible}"/>
				<%--h:inputText id="index" value="#{item.resultIndex}" title="Define o índice da propriedade no relatório" size="3" onkeypress="return keyPressInt(this,event)">
					<f:validateLongRange minimum="0" maximum="9999" />
				</h:inputText--%>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:panelGroup>
						<%--h:outputLabel value="Lista das propriedades visíveis no relatório: " /--%>
						<h:commandButton value="Marcar" title="Marca todas as propriedades da lista atual" onclick="return checkAll(this.form, 'myCheck')" style="font-size: small;"/>
						<h:commandButton value="Desmarcar" title="Desmarca todas as propriedades da lista atual" onclick="return clearAll(this.form, 'myCheck')" style="font-size: small;"/>
						<h:commandButton value="Inverter" title="Inverte a seleção das propriedades da lista atual" onclick="return inverseAll(this.form, 'myCheck')" style="font-size: small;"/>
						<h:commandButton value="Desfazer" title="Volta a seleção anterior às alterações recentemente efetuadas" type="reset" style="font-size: small;"/>
					</h:panelGroup>
				</f:facet>
				<h:outputText value="#{item.propertyPathLabel}" style="font-size: small;"/>
			</h:column>
			
	</h:dataTable>
	<f:verbatim>
		</div>
	</f:verbatim>

	<h:outputLabel value="Defina a ordem de exibição das propriedades no relatório da pesquisa:" style="color: #EE2C2C;  font-size: smaller;"/>
  	<h:commandButton id="refreshResultCondiction" value="Atualizar" action="#{queryBean.doPageRefresh}" onclick="this.value='Atualizando...'"/>
	<h:dataTable 
			 id="tbs"
			 width="100%"  
			 value="#{queryBean.currentProcess.userReport.resultParam.selectedCondictions}"
			 var='item' 
			 headerClass="tableAdvancedReportHeader" 
			 styleClass="tableCollectionAdvancedReport" 
			 rowClasses="tableAdvancedReportRowEven,tableAdvancedReportRowOdd" 
			 columnClasses="tableAdvancedReportColumnOdd"
			 style="border-collapse: collapse;" 
			 rendered="#{queryBean.currentProcess.userReport.resultParam.hasCondictions}"
			 >

			<h:column>
				<f:facet name="header">
					<h:outputLabel value="Índice:" />
				</f:facet>
				<h:inputText id="index" value="#{item.resultIndex}" size="3" onkeypress="return keyPressInt(this,event)" style="text-align:right;">
					<f:validateLongRange minimum="0" maximum="9999" />
				</h:inputText>
	  			<h:selectBooleanCheckbox value="#{item.visible}"/>
				<h:outputText value="#{item.propertyPathLabel}" style="font-size: small;"/>
			</h:column>
	</h:dataTable>
	