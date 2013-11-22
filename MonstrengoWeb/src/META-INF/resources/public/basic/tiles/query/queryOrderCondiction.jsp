<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>
	<h:outputLabel value="Crie ordens avançadas para sua pesquisa: " style="color: #0000FF; font-size:smaller;"/>
	<h:selectOneListbox id="orderPath" value="#{queryBean.newOrderParam.propertyPath}" style="height: 150px" title="Escolha a propriedade utilizada na ordenaçaõ">
		<f:selectItems value="#{queryBean.currentProcess.userReport.orderParam.listPropertyPath}"/>
	</h:selectOneListbox>
	<h:panelGrid columns="2" columnClasses="tableViewColumnEven">

		<h:selectOneRadio id="asc_desc" value="#{queryBean.newOrderParam.orderDirection}" title="Define a direção de ordenação">
    		<f:selectItems value="#{queryBean.currentProcess.userReport.orderParam.listOrderDirection}"/>
        </h:selectOneRadio>


		<h:panelGrid  columnClasses="tableViewColumnEven">
			<h:commandButton value="Adicionar" action="#{queryBean.doAddNewOrder}" onclick="this.value='Adicionando...'" title="Adiciona a atual condição de ordem na lista de condições sem executar a atualização"/>
			<h:commandButton value="Adicionar e atualizar" action="#{queryBean.doAddNewOrderAndRefresh}" onclick="this.value='Adicionando...'" title="Adiciona a atual condição de ordem na lista e atualiza a pesquisa para exibir a nova ordem"/>
		</h:panelGrid>

	</h:panelGrid>

	<h:dataTable 
	         id="tbo"
			 value="#{queryBean.currentProcess.userReport.orderParam.condictions}" 
			 var='item' 
			 headerClass="tableCollectionUpdateHeader" 
			 styleClass="tableCollectionUpdate" 
			 rowClasses="tableViewRowEven,tableViewRowOdd" 
			 columnClasses="tableViewColumnOdd"
			 style="border-collapse: collapse" 
			 rendered="#{queryBean.currentProcess.userReport.orderParam.hasCondictions}"
			 >

			<h:column>
				<f:facet name="header">
					<h:outputLabel value="Comandos:" />
				</f:facet>

	  			<h:commandLink action="#{queryBean.doRemoveOrder}">
					<h:graphicImage value="../../public/basic/img/delete.png" title="Remove esta condição de ordenação da lista" style="border:0"/>
					<f:param name="condictionId" value="#{item.id}"/>
				</h:commandLink>

	  			<h:selectBooleanCheckbox value="#{item.active}" title="Ativa/Desativa a condição de ordenação da pesquisa atual"/>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:panelGroup>
						<h:outputLabel value="Lista das ordens atuais: " />
						<h:commandButton value="Limpar" action="#{queryBean.doClearOrders}" title="Remove todas as condições de ordenação atuais da lista de condições"/>
					</h:panelGroup>
				</f:facet>
				<h:outputText value="#{item}"/>
			</h:column>
	</h:dataTable>
	