<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGroup>
	<h:outputText value="Relatório de auditoria<br>" styleClass="title" escape="false"/>
	<h:outputText value="Entidade <i>#{checkAuditCrudBean.entity.info.label}: #{checkAuditCrudBean.entity}</i>." escape="false" styleClass="description"/>
</h:panelGroup>


<h:panelGrid>
	<h:dataTable value="#{checkAuditCrudBean.result.list}" 
	    var='item'
		headerClass="tableListHeader" 
		styleClass="tableList"
		rowClasses="tableListRowEven,tableListRowOdd"
		columnClasses="tableListColumn" 
		style="border-collapse: collapse; font-weight: normal">

		<h:column>
			<f:facet name="header">
				<h:outputLabel
					value="Ação" />
			</f:facet>

			<h:outputText value="Criada" rendered="#{item.propertiesMap.created.value.asBoolean}"/>
			<h:outputText value="Alterada" rendered="#{item.propertiesMap.updated.value.asBoolean}"/>
		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputLabel
					value="Data e hora #{item.propertiesMap.ocurrencyDate.info.label}" />
			</f:facet>

			<h:outputLabel value="#{item.propertiesMap.ocurrencyDate.value.asString}" />

		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputLabel
					value="Operador #{item.propertiesMap.applicationUser.info.label}" />
			</f:facet>

			<h:outputLabel value="#{item.propertiesMap.applicationUser.value.asString}" />

		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputLabel
					value="Terminal #{item.propertiesMap.terminal.info.label}" />
			</f:facet>

			<h:outputLabel value="#{item.propertiesMap.terminal.value.asString}" />

		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputLabel
					value="Descrição #{item.propertiesMap.description.info.label}" />
			</f:facet>

			<h:outputText value="#{item.propertiesMap.description.value.asString}" escape="true"/>

		</h:column>

	</h:dataTable>

	<h:panelGroup>
	<f:verbatim>
		<%-- div class="noprint, floatMenu" style="clear: left; border: solid #aaa 1px; margin: 0 0 1em 1em; font-size: 90%; background: #f9f9f9; width: 70%; padding: 2px; spacing: 2px; text-align: center; float: right;">  --%>
		<div class="floatMenu noprint" style="width: 300px">
		        Auditoria das relações
                <img width="16" height="16" src="../basic/img/close.png" style="border: 0pt none ;" title="Fechar" onmousedown="this.parentNode.style.display='none'"> 
	</f:verbatim>
	<h:dataTable  value="#{checkAuditCrudBean.subEntities}" 
	    var='item'
		headerClass="description" 
		styleClass="description"
		rowClasses="description"
		columnClasses="description" 
		>
		<h:column>
	    	<h:outputLink value="javascript:linkCheckAuditCrud('#{item.info.type.name}', #{item.value.id})" rendered="#{item.info.entity && !item.info.collection}">
				<h:graphicImage value="../basic/img/checkAuditCrud.png" title="Verifica quem criou e alterou este registro" style="border:0" width="10" height="10"/>
				<h:outputText value="#{item.info.label}: #{item.value.asString}" escape="false" style="font-size:10;"/>
		    </h:outputLink>
		</h:column>
	</h:dataTable>
	<f:verbatim>
	</div>
	</f:verbatim>
	</h:panelGroup>
</h:panelGrid>
<f:verbatim>
	<fieldset><legend class="tableViewUpdateHeader">
</f:verbatim>
	<h:outputLabel value="Auditora das propriedades"/>
<f:verbatim>
	</legend>
</f:verbatim>

	<h:dataTable value="#{checkAuditCrudBean.propertiesResult}" 
	    var='prop'
		style="border-collapse: collapse; font-weight: normal; text-align: left;">
		
		<h:column>
			<h:outputLabel value="Propriedade #{prop.property.info.label}: #{prop.property.value.asString}" 
			               style="font-size:10;"
			               rendered="#{!prop.property.info.collection}"/>
			<h:dataTable value="#{prop.result.list}" 
			    var='item'
				headerClass="tableListHeader" 
				styleClass="tableList"
				rowClasses="tableListRowEven,tableListRowOdd"
				columnClasses="tableListColumn" 
				style="border-collapse: collapse; font-weight: normal"
				rendered="#{!prop.property.info.collection}">
				
				<h:column>
					<f:facet name="header">
						<h:outputLabel
							value="Ação" />
					</f:facet>

					<h:outputText value="Criada" rendered="#{item.propertiesMap.created.value.asBoolean}"/>
					<h:outputText value="Alterada" rendered="#{item.propertiesMap.updated.value.asBoolean}"/>
				</h:column>

				<h:column>
					<f:facet name="header">
						<h:outputLabel
							value="Data e hora #{item.propertiesMap.ocurrencyDate.info.label}" />
					</f:facet>

					<h:outputLabel value="#{item.propertiesMap.ocurrencyDate.value.asString}" />

				</h:column>

				<h:column>
					<f:facet name="header">
						<h:outputLabel
							value="Operador #{item.propertiesMap.applicationUser.info.label}" />
					</f:facet>

					<h:outputLabel value="#{item.propertiesMap.applicationUser.value.asString}" />

				</h:column>

				<h:column>
					<f:facet name="header">
						<h:outputLabel
							value="Terminal #{item.propertiesMap.terminal.info.label}" />
					</f:facet>

					<h:outputLabel value="#{item.propertiesMap.terminal.value.asString}" />

				</h:column>

				<h:column>
					<f:facet name="header">
						<h:outputLabel
							value="Descrição #{item.propertiesMap.description.info.label}" />
					</f:facet>

					<h:outputText value="#{item.propertiesMap.description.value.asString}" escape="true"/>

				</h:column>

			</h:dataTable>
			
			<h:outputLabel value="Propriedade #{prop.property.info.label}:" 
			               style="font-size:10;"
			               rendered="#{prop.property.info.collection}"/>
			<h:dataTable value="#{prop.results}" 
			    var='ent'
				style="border-collapse: collapse; font-weight: normal;"
				rendered="#{prop.property.info.collection}">
				
				<h:column>
					<h:outputLabel value="#{ent.entity.info.label}: #{ent.entity}" 
					               style="font-size:10;"/>
					<h:dataTable value="#{ent.result.list}" 
					    var='item'
						headerClass="tableListHeader" 
						styleClass="tableList"
						rowClasses="tableListRowEven,tableListRowOdd"
						columnClasses="tableListColumn" 
						style="border-collapse: collapse; font-weight: normal">
				
						<h:column>
							<f:facet name="header">
								<h:outputLabel
									value="Ação" />
							</f:facet>

							<h:outputText value="Criada" rendered="#{item.propertiesMap.created.value.asBoolean}"/>
							<h:outputText value="Alterada" rendered="#{item.propertiesMap.updated.value.asBoolean}"/>
						</h:column>

						<h:column>
							<f:facet name="header">
								<h:outputLabel
									value="Data e hora #{item.propertiesMap.ocurrencyDate.info.label}" />
							</f:facet>

							<h:outputLabel value="#{item.propertiesMap.ocurrencyDate.value.asString}" />

						</h:column>

						<h:column>
							<f:facet name="header">
								<h:outputLabel
									value="Operador #{item.propertiesMap.applicationUser.info.label}" />
							</f:facet>

							<h:outputLabel value="#{item.propertiesMap.applicationUser.value.asString}" />

						</h:column>

						<h:column>
							<f:facet name="header">
								<h:outputLabel
									value="Terminal #{item.propertiesMap.terminal.info.label}" />
							</f:facet>

							<h:outputLabel value="#{item.propertiesMap.terminal.value.asString}" />

						</h:column>

						<h:column>
							<f:facet name="header">
								<h:outputLabel
									value="Descrição #{item.propertiesMap.description.info.label}" />
							</f:facet>

							<h:outputText value="#{item.propertiesMap.description.value.asString}" escape="true"/>

						</h:column>

					</h:dataTable>
				</h:column>
			</h:dataTable>
		</h:column>
	</h:dataTable>

<f:verbatim>
	</fieldset>
</f:verbatim>
