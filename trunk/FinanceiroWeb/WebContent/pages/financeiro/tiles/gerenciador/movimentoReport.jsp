<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

	<h:panelGrid columns="3">
		<h:panelGrid>
    	<h:graphicImage value="../basic/img/header.gif" title="Financeiro" style="border:0"/>
		</h:panelGrid>	

		<h:panelGrid styleClass="reportHeader">
		<f:verbatim>
				Sivamar - Sind. dos Loj. do Com. e do Com. Varej. de Maringá<br>
				Rua Néo Alves Martins, 2789 - sobreloja - CEP 87013-914<br>
				Maringá - PR<br>
				Fone/Fax: (44) 3026-4444<br>
		</f:verbatim>
		</h:panelGrid>	
		
		<h:panelGrid>		
   	    <h:outputText value="Usuário: #{userSessionBean.userSession.user.object}"  style="text-align: center;"/>
		</h:panelGrid>	
	</h:panelGrid>

	<h:panelGrid  styleClass="reportTitle">			
		<h:outputText value="Relatório de movimentação da conta"/>
	</h:panelGrid>
			
	<h:panelGrid>	
				<h:dataTable value="#{listarMovimentoBean.array}" 
				             var='item'
				             rendered="#{listarMovimentoBean.size>0}"
				             headerClass="tableListHeader"
				             styleClass="tableList"
				             rowClasses="tableListRowEven,tableListRowOdd"
				             columnClasses="tableListColumn"
				             style="border-collapse: collapse;">

					<h:column>
						<f:facet name="header">
		    	            	<h:outputText value="#{menuBean.infoMap.Movimento.propertiesMetadata.conta.label} "/>
    	                </f:facet>

						<h:outputLabel value="#{item.propertiesMap.conta.value.asString}"/>
					</h:column>

				</h:dataTable>
	</h:panelGrid>
	