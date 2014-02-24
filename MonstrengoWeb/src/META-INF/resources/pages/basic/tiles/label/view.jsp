<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<h:messages styleClass="infoMessage"/>

<h:form id="formPrint">
	<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
	<h:outputLabel value="Impressão:" styleClass="titleGroup"/>
		<h:panelGrid>
			<h:panelGroup>
					<h:outputLabel value="Selecione o modelo de etiquetas "/>
				<h:selectOneMenu id="modelo" value="#{labelBean.modelLabelId}" title="Selecione o modelo da etiqueta">
					<f:selectItems value="#{labelBean.modelsLabel}"/>
				</h:selectOneMenu>
<%--				
				<h:outputLink value="javascript:linkRetrieve('#{menuBean.infoMap.EtiquetaModelo.type.name}', '#{labelBean.modeloId}')" rendered="#{menuBean.crudMap.EtiquetaModelo.canRetrieve}">
					<h:graphicImage value="../basic/img/retrieve.png" title="Visualiza os detalhes do registro" style="border:0"/>
				</h:outputLink>
				<h:graphicImage value="../basic/img/retrieve_d.png" title="Você não possui direitos para visualizar os dados do registro" style="border:0" rendered="#{!menuBean.crudMap.EtiquetaModelo.canRetrieve}"/>

				<h:outputLink value="javascript:linkUpdate('#{menuBean.infoMap.EtiquetaModelo.type.name}', '#{labelBean.modeloId}')" rendered="#{menuBean.crudMap.EtiquetaModelo.canUpdate}">
					<h:graphicImage value="../basic/img/update.png" title="Altera os dados do registro" style="border:0"/>
				</h:outputLink>
				<h:graphicImage value="../basic/img/update_d.png" title="Você não possui direitos para alterar os dados do registro" style="border:0" rendered="#{!menuBean.crudMap.EtiquetaModelo.canUpdate}"/>

				<h:outputLink value="javascript:linkDelete('#{menuBean.infoMap.EtiquetaModelo.type.name}', '#{labelBean.modeloId}')" rendered="#{menuBean.crudMap.EtiquetaModelo.canDelete}">
					<h:graphicImage value="../basic/img/delete.png" title="Apaga o registro" style="border:0"/>
				</h:outputLink>
				<h:graphicImage value="../basic/img/delete_d.png" title="Você não possui direitos para apagar o registro" style="border:0" rendered="#{!menuBean.crudMap.EtiquetaModelo.canDelete}"/>

				<h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.EtiquetaModelo.type.name}')" rendered="#{menuBean.crudMap.EtiquetaModelo.canCreate}">
   					<h:graphicImage value="../basic/img/create.png" title="Cria um novo registro de #{menuBean.infoMap.EtiquetaModelo.label}." style="border:0"/>
				</h:outputLink>
				<h:graphicImage value="../basic/img/create_d.png" title="Você não possui direitos para criar um novo registro de #{menuBean.infoMap.EtiquetaModelo.label}" style="border:0" rendered="#{!menuBean.crudMap.EtiquetaModelo.canCreate}"/>
--%>
			</h:panelGroup>

			<h:panelGroup>
				<h:outputLabel value="e a impressora de destino"/>
				<h:selectOneMenu id="impressora" value="#{labelBean.printerIndex}" title="Selecione a impressora na qual se deseja imprimir as etiquetas">
					<f:selectItems value="#{labelBean.printersIndex}"/>
				</h:selectOneMenu>
				<h:commandButton value="Imprimir" action="#{labelBean.doPrint}" title="Imprime as etiquetas selecionadas"/>
				<h:outputLabel value=" ou "/>
				<h:commandButton value="Download em PDF" action="#{labelBean.doDownload}" title="Gera um PDF das etiquetas selecionadas"/>
			</h:panelGroup>
		</h:panelGrid>
	<script>borderCreator.endDarkBorder()</script>
	
	<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
		<h:outputLabel value="Manutenção:" styleClass="titleGroup"/>
		<h:commandButton value="Recarregar" title="Recarrega a lista de etiquetas sem gravar as alterações" action="#{labelBean.doReload}"/>
		<h:commandButton value="Gravar" title="Grava a atual de definição de seleção de etiquetas" onclick="javascript:return confirm('Confirma gravar a seleção atual?')" action="#{labelBean.doUpdateSelection}" disabled="#{!menuBean.crudMap.AddressLabel.canUpdate || (labelBean.applicationUserId!=userSessionBean.userSession.user.id)}"/>
		<h:commandButton value="Excluir" title="Exclui as etiquetas selecionadas" onclick="javascript:return confirm('Confirma excluir as etiquetas selecionadas?')" action="#{labelBean.doDeleteSelection}" disabled="#{!menuBean.crudMap.AddressLabel.canDelete || (labelBean.applicationUserId!=userSessionBean.userSession.user.id)}"/>
	<script>borderCreator.endDarkBorder()</script>
	
	<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
		<h:outputLabel value="Exibir etiquetas de:" styleClass="titleGroup"/>
		<br/>
		<h:selectOneMenu id="operador" value="#{labelBean.applicationUserId}" onchange="this.form.submit()" title="Selecione o operador para visualizar suas etiquetas">
			<f:selectItems value="#{labelBean.applicationUsers}"/>
		</h:selectOneMenu>
		<br/>
		<h:selectOneMenu id="entidade" value="#{labelBean.applicationEntityId}" onchange="this.form.submit()" title="Selecione o tipo de entidade para visualizar suas etiquetas">
			<f:selectItems value="#{labelBean.applicationEntities}"/>
		</h:selectOneMenu>
		<br/>
		<h:selectOneMenu id="grupo" value="#{labelBean.addressLabelGroupId}" onchange="this.form.submit()" title="Selecione um grupo de etiquetas para visualizar">
			<f:selectItems value="#{labelBean.addressLabelGroupList}"/>
		</h:selectOneMenu>

		<br/>		
		<h:outputLabel value="Ordernar por:" styleClass="titleGroup"/>
		<h:selectOneMenu id="order" value="#{labelBean.orderProperty}" onchange="this.form.submit()" title="Selecione uma propriedade para ordenação das etiquetas na visualização">
			<f:selectItems value="#{labelBean.orderPropertyList}"/>
		</h:selectOneMenu>

		<br/>
		<h:outputLabel value="Seleção:" styleClass="titleGroup"/>
		<h:commandButton value="Marcar" title="Marca todas as propriedades da lista atual" onclick="return checkAll(this.form, 'myCheckEtiqueta')"/>
		<h:commandButton value="Intervalo" title="Marca todas as propriedades entre um intervalo definido" onclick="return checkRange(this.form, 'myCheckEtiqueta')"/>
		<h:commandButton value="Desmarcar" title="Desmarca todas as propriedades da lista atual" onclick="return clearAll(this.form, 'myCheckEtiqueta')"/>
		<h:commandButton value="Inverter" title="Inverte a seleção das propriedades da lista atual" onclick="return inverseAll(this.form, 'myCheckEtiqueta')"/>
		<h:commandButton value="Desfazer" title="Volta a seleção anterior às alterações recentemente efetuadas" type="reset"/>

	<script>borderCreator.endDarkBorder()</script>
	<h:outputLabel value="Não há etiquetas na lista para serem impressas. Utilize o menu Gerar Etiquetas para adicionar etiquetas na lista" styleClass="infoMessage" rendered="#{!labelBean.hasLabels}"/>

	<h:outputLabel value="Estão sendo exibidas <b>#{labelBean.labelsCount}</b> etiquetas. Selecione as etiquetas que serão enviadas para impressão:" style="color: #EE2C2C; text-align:left" rendered="#{labelBean.hasLabels}" escape="false"/>

	<div  style="height: auto; overflow-x: auto;">
	<h:dataTable 
			 id="tbr"
			 width="100%"  
			 value="#{labelBean.labels.list}"
			 var='item' 
			 headerClass="tableAdvancedReportHeader" 
			 styleClass="tableCollectionAdvancedReport" 
			 rowClasses="tableAdvancedReportRowEven,tableAdvancedReportRowOdd" 
			 columnClasses="tableAdvancedReportColumnOdd"
			 style="border-collapse: collapse; white-space: nowrap;" 
			 rendered="#{labelBean.hasLabels}"
			 >

			<h:column>
	  			<h:selectBooleanCheckbox id="myCheckEtiqueta" value="#{item.propertiesMap.print.value.asBoolean}"/>

				<f:facet name="header">
					<h:outputLabel value="" />
				</f:facet>

				<h:outputLink value="javascript:linkRetrieve('#{item.object.getClass().name}', '#{item.id}')" rendered="#{menuBean.crudMap.AddressLabel.canRetrieve}">
					<h:graphicImage value="../basic/img/retrieve.png" title="Visualiza os detalhes do registro" style="border:0"/>
				</h:outputLink>
				<h:graphicImage value="../basic/img/retrieve_d.png" title="Você não possui direitos para visualizar os dados do registro" style="border:0" rendered="#{!menuBean.crudMap.AddressLabel.canRetrieve}"/>

			</h:column>

			<h:column>
				<f:facet name="header">
				  <h:panelGroup>
						<h:outputText value="#{menuBean.infoMap.AddressLabel.propertiesMetadata.line1.label}<br>" escape="false"/>
						<h:outputText value="#{menuBean.infoMap.AddressLabel.propertiesMetadata.line2.label}<br>" escape="false"/>
						<h:outputText value="#{menuBean.infoMap.AddressLabel.propertiesMetadata.line3.label}<br>" escape="false"/>
						<h:outputText value="#{menuBean.infoMap.AddressLabel.propertiesMetadata.line4.label}<br>" escape="false"/>
						<h:outputText value="#{menuBean.infoMap.AddressLabel.propertiesMetadata.line5.label}" escape="false"/>
				  </h:panelGroup>
				</f:facet>
				<h:outputText value="#{item.propertiesMap.line1.value.asString}<br>" escape="false" style="font-size: small;"/>
				<h:outputText value="#{item.propertiesMap.line2.value.asString}<br>" escape="false" style="font-size: small;"/>
				<h:outputText value="#{item.propertiesMap.line3.value.asString}<br>" escape="false" style="font-size: small;"/>
				<h:outputText value="#{item.propertiesMap.line4.value.asString}<br>" escape="false" style="font-size: small;"/>
				<h:outputText value="#{item.propertiesMap.line5.value.asString}" escape="false" style="font-size: small;"/>
			</h:column>
			<h:column>
				<f:facet name="header">
				  <h:panelGroup>
						<h:outputText value="#{menuBean.infoMap.AddressLabel.propertiesMetadata.applicationUser.label}<br>" escape="false"/>
						<h:outputText value="#{menuBean.infoMap.AddressLabel.propertiesMetadata.ocurrencyDate.label}" escape="false"/>
				  </h:panelGroup>
				</f:facet>
				<h:outputText value="#{item.propertiesMap.applicationUser.value.asString}<br>" escape="false" style="font-size: small;"/>
				<h:outputText value="#{item.propertiesMap.ocurrencyDate.value.asString}" escape="false" style="font-size: small;"/>
			</h:column>
			<h:column>
				<f:facet name="header">
						<h:outputLabel value="#{menuBean.infoMap.AddressLabel.propertiesMetadata.addressLabelGroup.label}" />
				</f:facet>
				<h:outputText value="#{item.propertiesMap.addressLabelGroup.value.asString}" style="font-size: small;"/>
			</h:column>

	</h:dataTable>
	
	</div>
</h:form>
