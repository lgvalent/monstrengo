<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>

		<f:subview id="Remove">

			<h:inputHidden value="#{entityEdit.currentClassKey}"/>
			<h:outputLabel value="#{updateBean.load}" />
			<%-- Força a preparação (seleção currentyEntity) da entitade antes de TUDO --%>
			<%--h:outputLabel value="#{updateBean.load}" /--%>
			<%--h:inputHidden value="#{entityEdit.currentClassKey}" /--%>

			<h:panelGrid columns="4">
				<h:outputText styleClass="title" value="Você está editando os dados de <i>#{entityEdit.currentEntity.label}</i>:<br>" escape="false" />
				<h:outputLink value="list.jsp?entity=#{entityEdit.currentEntity.className}" >
					<h:outputText value="[Listar]" />
				</h:outputLink>
			</h:panelGrid>

			<f:verbatim >
				<br><br>
			</f:verbatim>

			<h:form>
			<%--h:form --%>
  				<%--h:inputText value="#{entityEdit.currentClassId}" /--%>
				<%--h:inputHidden value="#{entityEdit.currentClassId}" /--%>
				<f:verbatim >
					<br>
				</f:verbatim>
				<h:dataTable 
					value="#{entityEdit.currentEntity.properties.valuesEdit}" 
					var='item'
					headerClass="tableListHeader"
				    styleClass="tableView"
				    rowClasses="tableViewRowEven,tableViewRowOdd"
				    columnClasses="tableViewColumnEven, tableViewColumnOdd"
				    style="border-collapse: collapse">

					<h:column>
						<f:facet name="header" >
                          <h:outputText value="Propriedade:" />
                        </f:facet>
						<h:outputLabel value="*" rendered="#{item.info.required}" styleClass="errorMessage"/>
						<h:outputLabel value="#{item.info.label}:"  title="#{item.info.hint}"/>
					</h:column>

					<h:column >
						<f:facet name="header" >
                          <h:outputText value="Conteúdo:"/>
                        </f:facet>

						<h:inputText id="strInput" value="#{item.value}" title="#{item.info.hint}" required="#{item.info.required}" rendered="#{item.info.isString()}" readonly="#{item.info.readOnly}" size="#{item.info.size}" maxlength="#{item.info.size}" >
						</h:inputText>
						<h:message for="strInput" styleClass="errorMessage" />

						<h:inputText id="intInput" value="#{item.value}" title="#{item.info.hint}" required="#{item.info.required}" rendered="#{item.info.isInteger()}" readonly="#{item.info.readOnly}" size="10">
							<f:validateLongRange minimum="-999999" maximum="999999" />
						</h:inputText>
						<h:message for="intInput" styleClass="errorMessage" />
						
						<h:inputText id="dateInput" value="#{item.value}" title="#{item.info.hint}" required="#{item.info.required}" rendered="#{item.info.date}" readonly="#{item.info.readOnly}">
							<f:convertDateTime  pattern="MM/dd/yyyy"  />
						</h:inputText>
						<h:message for="dateInput" styleClass="errorMessage"/>

						<%--h:panelGroup rendered="#{item.info.subClass}">
							<h:inputText value="#{item.id}" title="#{item.info.hint}" readonly="true" rendered="#{item.id!=-1}" size="5"/>
							<h:inputText id="subInput" value="#{item.value}" title="#{item.info.hint}" required="#{item.info.required}" />
							<h:message for="subInput" styleClass="errorMessage" />

							<h:outputLink value="view.jsp?entity=#{item.info.type.name}&parent=#{entityEdit.currentEntity.className}&parentId=#{entityEdit.currentEntity.id}&property=#{item.info.name}" rendered="#{not item.valueNull}">
								<h:outputText value="V " title="Visualiza mais informações de #{item.info.label}" />
							</h:outputLink>
							<h:outputLink value="list.jsp?entity=#{item.info.type.name}">
								<h:outputText value="L" title="Exibe um lista de #{item.info.label}"/>
							</h:outputLink>

						</h:panelGroup--%>

						<h:panelGroup rendered="#{item.info.list}">
							<h:inputText id="listInput" value="#{item.value}" title="#{item.info.hint}" required="#{item.info.required}" readonly="true"/>
							<h:message for="listInput" styleClass="errorMessage" />

							<%--h:outputLink target="_blank" value="list.jsp?entity=#{item.info.type.name}&parent=#{entityEdit.currentEntity.className}&parentId=#{entityEdit.currentEntity.id}&property=#{item.info.name}" rendered="#{not item.valueNull}"--%>
							<h:outputLink value="javascript:linkQuery('#{item.info.type.name}', '#{entityEdit.currentEntity.className}', '#{entityEdit.currentEntity.id}', '#{item.info.name}')" rendered="#{not item.valueNull}">
								<h:outputText value="V " title="Visualiza mais informações de #{item.info.label}" />
							</h:outputLink>
						</h:panelGroup>

						<h:panelGroup  rendered="#{item.info.editShowList && item.info.listFromFile}">
							<h:selectOneMenu id="selectInput" value="#{item.value}" required="#{item.info.required}" >
    	                   		<f:selectItems value="#{item.info.editList}"/>
        	            	</h:selectOneMenu>
							<h:message for="selectInput" styleClass="errorMessage" />

							<%--h:outputLink target="_blank" value="view.jsp?entity=#{item.info.type.name}&parent=#{entityEdit.currentEntity.className}&parentId=#{entityEdit.currentEntity.id}&property=#{item.info.name}" rendered="#{not item.valueNull}"--%>
							<h:outputLink value="javascript:linkRetrieve('#{item.info.type.name}', '#{entityEdit.currentEntity.className}', '#{entityEdit.currentEntity.id}', '#{item.info.name}')" rendered="#{not item.valueNull}">
								<h:outputText value="V " title="Visualiza mais informações de #{item.info.label}" />
							</h:outputLink>
						</h:panelGroup>

						<h:panelGroup rendered="#{item.info.editShowList && item.info.subClass}">
							<h:selectOneMenu id="selectSubClassInput" value="#{item.id}" required="#{item.info.required}" >
        	               		<f:selectItems value="#{item.info.editList}"/>
            	        	</h:selectOneMenu>

							<%--h:outputLink target="_blank" value="view.jsp?entity=#{item.info.type.name}&parent=#{entityEdit.currentEntity.className}&parentId=#{entityEdit.currentEntity.id}&property=#{item.info.name}" rendered="#{not item.valueNull}"--%>
							<h:outputLink value="javascript:linkRetrieve('#{item.info.type.name}', '#{entityEdit.currentEntity.className}', '#{entityEdit.currentEntity.id}', '#{item.info.name}')" rendered="#{not item.valueNull}">
								<h:outputText value="V " title="Visualiza mais informações de #{item.info.label}" />
							</h:outputLink>
						</h:panelGroup>

						<%--h:outputLink target="_blank" value="list.jsp?entity=#{item.info.type.name}" rendered="#{item.info.subClass || item.info.list}"--%>
						<h:outputLink value="javascript:linkQuery('#{item.info.type.name}', '', '', '')" rendered="#{item.info.subClass || item.info.list}">
							<h:outputText value=" L " title="Exibe uma lista de #{item.info.label}"/>
						</h:outputLink>

						<%--h:message for="selectSubClassInput" styleClass="errorMessage" /--%>

						<%--h:selectOneListbox id="ListBoxSubClassInput" value="#{item.id}" rendered="#{item.info.editShowList && item.info.subClass}">
                       		<f:selectItems value="#{item.info.editList}"/>
                    	</h:selectOneListbox>
						<h:message for="ListBoxSubClassInput" styleClass="errorMessage" /--%>

					</h:column>


					<h:column id="help" >
						<h:outputText value="#{item.info.help}" escape="false" />
					</h:column>
					<%--
					<h:column >
						<h:message for="intInput" styleClass="errorMessage" />
						<h:message for="strInput" styleClass="errorMessage" />
						<h:message for="dateInput" styleClass="errorMessage" />
					</h:column>
					--%>

				</h:dataTable>
				<br>

				<h:outputText >
						<h:messages layout="table" showDetail="true" showSummary="true" tooltip="true"/>
				</h:outputText>

				<h:commandButton value="Salvar" action="#{entityEdit.saveEntity}"/>
				<h:commandButton value="Submit" action="submit"/>
				<br>
				<br>
				<h:outputLabel value="* Campos requeridos " styleClass="errorMessage" />

				<%--h:inputHidden value="#{entityEdit.currentClassId}" /--%>
	 		</h:form>
			<%--h:outputLabel value="#{updateBean.load}" /--%>


		</f:subview>
