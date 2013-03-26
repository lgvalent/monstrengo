<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>
	<h:outputLabel value="Crie filtros avançados para melhor restringir sua pesquisa:"  style="color: #006600; font-size:smaller;"/>
	<h:panelGrid>
        <h:selectOneRadio id="e_ou" value="#{queryBean.newCondictionParam.initOperator}" title="Define se a operação será restritiva (e) ou abrangente (ou).">
    		<f:selectItems value="#{queryBean.currentProcess.userReport.condictionParam.listInitOperator}"/>
        </h:selectOneRadio>

        <h:selectOneListbox id="propertyPath" value="#{queryBean.newCondictionParam.propertyPath}" onblur="this.form.submit()" style="height: 150px" title="Escolha a propriedade utilizada pelo filtro">
    		<f:selectItems value="#{queryBean.currentProcess.userReport.condictionParam.listPropertyPath}"/>
        </h:selectOneListbox>

  	  <h:panelGrid columns="2" columnClasses="tableViewColumnEven">
  	  <h:panelGrid columnClasses="tableViewColumnEven">
		<h:outputLabel value="Condição:"/>
        <h:selectOneMenu id="operatorId" value="#{queryBean.newCondictionParam.operatorId}" onchange="this.form.submit()">
    		<f:selectItems value="#{queryBean.currentProcess.userReport.condictionParam.listOperatorId}"/>
        </h:selectOneMenu>
  	  </h:panelGrid>

  	  <h:panelGrid columns="2">
  	  <h:panelGrid rendered="#{queryBean.newCondictionParam.operator.oneValueNeeded}" columnClasses="tableViewColumnEven">
		<h:outputLabel value="Valor:"/>

		<h:panelGroup id="value1">
			<h:inputText id="strInput1" value="#{queryBean.newCondictionParam.value1}"
			                           title="#{queryBean.newCondictionParam.propertyInfo.hint}"
			                           rendered="#{queryBean.newCondictionParam.propertyInfo.string}"
			                           maxlength="#{queryBean.newCondictionParam.propertyInfo.size}"
			                           size="20" />
			<h:message for="strInput1" styleClass="errorMessage" />

			<h:inputText id="intInput1" value="#{queryBean.newCondictionParam.value1}"
			                           title="#{queryBean.newCondictionParam.propertyInfo.hint}"
			                           rendered="#{queryBean.newCondictionParam.propertyInfo.integer or queryBean.newCondictionParam.propertyInfo.long}"
			                           maxlength="15"
			                           size="15"
			                           onkeypress="return keyPressInt(this,event)" />
			<h:message for="intInput1" styleClass="errorMessage" />

			<h:inputText id="bigInput1" value="#{queryBean.newCondictionParam.value1}"
			                           title="#{queryBean.newCondictionParam.propertyInfo.hint}"
			                           rendered="#{queryBean.newCondictionParam.propertyInfo.bigDecimal}"
			                           maxlength="15"
			                           size="15"
			                           onkeypress="return keyPressInt(this,event)" />
			<h:message for="bigInput1" styleClass="errorMessage" />

			<h:inputText id="dateInput1" value="#{queryBean.newCondictionParam.value1}"
			                           title="#{queryBean.newCondictionParam.propertyInfo.hint}"
			                           rendered="#{queryBean.newCondictionParam.propertyInfo.calendar}"
			                           maxlength="10"
			                           size="12"
			                           onblur="return onblurCalendar(this, '#{queryBean.newCondictionParam.propertyInfo.editMask}')"/>
			<h:message for="dateInput1" styleClass="errorMessage" />

			<h:selectBooleanCheckbox id="bolInput1"
			                         value="#{queryBean.newCondictionParam.value1AsBoolean}"
			                         title="#{queryBean.newCondictionParam.propertyInfo.hint}"
			                         rendered="#{queryBean.newCondictionParam.propertyInfo.boolean}"/>
			<h:message for="bolInput1" styleClass="errorMessage" />

			<h:selectOneMenu id="selectEnumInput1" 
			                 value="#{queryBean.newCondictionParam.value1}"
			                 rendered="#{queryBean.newCondictionParam.propertyInfo.enum}">
					<f:selectItems value="#{queryBean.newCondictionParam.propertyInfo.enumValuesList}" />
			</h:selectOneMenu>
			<h:message for="selectEnumInput1" styleClass="errorMessage" />


			<%-- Subclasse --%>
			<%-- Lista de Classes --%>
			<h:panelGroup rendered="#{queryBean.newCondictionParam.propertyInfo.entity}" >
   				<h:graphicImage value="../basic/img/query_open_select.png" title="Dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="idSubClassInput1"
				             value="#{queryBean.newCondictionParam.value1}"
				             styleClass="queryInputSelectOne"
				             title="Dê um clique na caixa de texto para abrir a pesquisa"
				             size="3"
				             onclick="javascript:openSelectOneId('#{queryBean.newCondictionParam.propertyInfo.type.name}',this.value,this)"/>
			</h:panelGroup>
			<h:message for="idSubClassInput1" styleClass="errorMessage" />
		</h:panelGroup>
  	  </h:panelGrid>

  	  <h:panelGrid rendered="#{queryBean.newCondictionParam.operator.twoValueNeeded}" columnClasses="tableViewColumnEven">
		<h:outputLabel value="até o valor:" />
		<h:panelGroup id="value2">
			<h:inputText id="strInput2" value="#{queryBean.newCondictionParam.value2}"
			                           title="#{queryBean.newCondictionParam.propertyInfo.hint}"
			                           rendered="#{queryBean.newCondictionParam.propertyInfo.string}"
			                           maxlength="#{queryBean.newCondictionParam.propertyInfo.size}"
			                           size="20" />
			<h:message for="strInput2" styleClass="errorMessage" />

			<h:inputText id="intInput2" value="#{queryBean.newCondictionParam.value2}"
			                           title="#{queryBean.newCondictionParam.propertyInfo.hint}"
			                           rendered="#{queryBean.newCondictionParam.propertyInfo.integer or queryBean.newCondictionParam.propertyInfo.long}"
			                           maxlength="15"
			                           size="15"
			                           onkeypress="return keyPressInt(this,event)" />
			<h:message for="intInput2" styleClass="errorMessage" />

			<h:inputText id="bigInput2" value="#{queryBean.newCondictionParam.value2}"
			                           title="#{queryBean.newCondictionParam.propertyInfo.hint}"
			                           rendered="#{queryBean.newCondictionParam.propertyInfo.bigDecimal}"
			                           maxlength="15"
			                           size="15"
			                           onkeypress="return keyPressInt(this,event)" />
			<h:message for="bigInput2" styleClass="errorMessage" />

			<h:inputText id="dateInput2" value="#{queryBean.newCondictionParam.value2}"
			                           title="#{queryBean.newCondictionParam.propertyInfo.hint}"
			                           rendered="#{queryBean.newCondictionParam.propertyInfo.calendar}"
			                           maxlength="10"
			                           size="12"
			                           onblur="return onblurCalendar(this, '#{queryBean.newCondictionParam.propertyInfo.editMask}')" />
			<h:message for="dateInput2" styleClass="errorMessage" />

			<h:selectBooleanCheckbox id="bolInput2"
			                         value="#{queryBean.newCondictionParam.value2AsBoolean}"
			                         title="#{queryBean.newCondictionParam.propertyInfo.hint}"
			                         rendered="#{queryBean.newCondictionParam.propertyInfo.boolean}"/>
			<h:message for="bolInput2" styleClass="errorMessage" />

			<h:selectOneMenu id="selectEnumInput2" 
			                 value="#{queryBean.newCondictionParam.value2}"
			                 rendered="#{queryBean.newCondictionParam.propertyInfo.enum}">
					<f:selectItems value="#{queryBean.newCondictionParam.propertyInfo.enumValuesList}" />
			</h:selectOneMenu>
			<h:message for="selectEnumInput2" styleClass="errorMessage" />

			<%-- Subclasse --%>
			<%-- Lista de Classes --%>
			<h:panelGroup rendered="#{queryBean.newCondictionParam.propertyInfo.entity}" >
   				<h:graphicImage value="../basic/img/query_open_select.png" title="Dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="idSubClassInput2"
				             value="#{queryBean.newCondictionParam.value2}"
				             styleClass="queryInputSelectOne"
				             title="Dê um clique na caixa de texto para abrir a pesquisa"
				             size="3"
				             onclick="javascript:openSelectOneId('#{queryBean.newCondictionParam.propertyInfo.type.name}',this.value,this)"/>
			</h:panelGroup>
			<h:message for="idSubClassInput2" styleClass="errorMessage" />
			
		</h:panelGroup>
  	  </h:panelGrid>
	  </h:panelGrid>
  	  </h:panelGrid>

  	  <h:panelGrid columns="2">
		<h:commandButton value="Adicionar" action="#{queryBean.doAddNewCondiction}" onclick="this.value='Adicionando...'" title="Adiciona a atual condição na lista de filtros da pesquisa sem executar a atualização"/>
		<h:commandButton value="Adicionar e atualizar" action="#{queryBean.doAddNewCondictionAndRefresh}" onclick="this.value='Adicionando...'" title="Adiciona a atual condição na lista de filtros da pesquisa e atualiza a pesquisa para exibir o novo resultado"/>
  	  </h:panelGrid>

	</h:panelGrid>

	<h:dataTable 
			 value="#{queryBean.currentProcess.userReport.condictionParam.condictions}"
			 var='item'
			 headerClass="tableAdvancedQueryHeader"
			 styleClass="tableCollectionAdvancedQuery"
			 rowClasses="tableAdvancedQueryRowEven,tableAdvancedQueryRowOdd"
			 columnClasses="tableAdvancedQueryColumnOdd"
			 style="border-collapse: collapse"
			 rendered="#{queryBean.currentProcess.userReport.condictionParam.hasCondictions}"
			 >

			<h:column>
				<f:facet name="header">
					<h:outputLabel value="Comandos:" />
				</f:facet>

	  			<h:commandLink action="#{queryBean.doRemoveCondiction}" rendered="#{!item.readOnly}">
					<h:graphicImage value="../basic/img/delete.png" title="Remove este filtro da lista" style="border:0"/>
					<f:param name="condictionId" value="#{item.id}"/>
				</h:commandLink>

	  			<h:selectBooleanCheckbox value="#{item.active}" title="Ativa/Desativa este filtro para a pesquisa" rendered="#{!item.readOnly}"/>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:panelGroup>
						<h:outputLabel value="Lista dos filtros atuais: " />
						<h:commandButton value="Limpar" action="#{queryBean.doClearCondictions}" title="Remove todos os filtros da lista"/>
					</h:panelGroup>
				</f:facet>
				<h:outputText value="#{item.propertyPathLabel} #{item.operator.label} "/>

		<h:panelGroup id="valueList1" rendered="#{!item.readOnly}">
			<h:inputText id="strInputList1" value="#{item.value1}"
			                           title="#{item.propertyInfo.hint}"
			                           rendered="#{item.propertyInfo.string}"
			                           maxlength="#{item.propertyInfo.size}"
			                           size="20" />
			<h:message for="strInputList1" styleClass="errorMessage" />

			<h:inputText id="intInputList1" value="#{item.value1}"
			                           title="#{item.propertyInfo.hint}"
			                           rendered="#{item.propertyInfo.integer or item.propertyInfo.long}"
			                           maxlength="15"
			                           size="15"
			                           onkeypress="return keyPressInt(this,event)" />
			<h:message for="intInputList1" styleClass="errorMessage" />

			<h:inputText id="bigInputList1" value="#{item.value1}"
			                           title="#{item.propertyInfo.hint}"
			                           rendered="#{item.propertyInfo.bigDecimal}"
			                           maxlength="15"
			                           size="15"
			                           onkeypress="return keyPressInt(this,event)" />
			<h:message for="bigInputList1" styleClass="errorMessage" />

			<h:inputText id="dateInputList1" value="#{item.value1}"
			                           title="#{item.propertyInfo.hint}"
			                           rendered="#{item.propertyInfo.calendar}"
			                           maxlength="10"
			                           size="12"
			                           onblur="return onblurCalendar(this,'#{item.propertyInfo.editMask}')"/>
			<h:message for="dateInputList1" styleClass="errorMessage" />

			<h:selectBooleanCheckbox id="bolInputList1"
			                         value="#{item.value1AsBoolean}"
			                         title="#{item.propertyInfo.hint}"
			                         rendered="#{item.propertyInfo.boolean}"/>
			<h:message for="bolInputList1" styleClass="errorMessage" />

			<h:selectOneMenu id="selectEnumInput1" 
			                 value="#{item.value1}"
			                 rendered="#{item.propertyInfo.enum}">
					<f:selectItems value="#{item.propertyInfo.enumValuesList}" />
			</h:selectOneMenu>
			<h:message for="selectEnumInput1" styleClass="errorMessage" />

			<%-- Subclasse --%>
			<%-- Lista de Classes --%>
			<h:panelGroup rendered="#{item.propertyInfo.entity}" >
   				<h:graphicImage value="../basic/img/query_open_select.png" title="Dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="idSubClassInputList1"
				             value="#{item.value1}"
				             styleClass="queryInputSelectOne"
				             title="Dê um clique na caixa de texto para abrir a pesquisa"
				             size="3"
				             onclick="javascript:openSelectOneId('#{item.propertyInfo.type.name}','',this)"/>
			</h:panelGroup>

			<h:outputLink value="javascript:linkRetrieve('#{item.propertyInfo.type.name}', '#{item.value1}')" rendered="#{item.propertyInfo.entity}">
				<h:outputText value="#{item.value1Description}"/>
			</h:outputLink>
		</h:panelGroup>
		<h:outputText id="strReadOnlyValue1"
						value="#{item.value1} #{item.value1Description}"
			            title="#{item.propertyInfo.hint}"
			            rendered="#{!item.propertyInfo.boolean && item.readOnly}"/>
		<h:selectBooleanCheckbox id="bolReadOnlyValue1"
		                         value="#{item.value1AsBoolean}"
		                         title="#{item.propertyInfo.hint}"
		                         rendered="#{item.propertyInfo.boolean && item.readOnly}"/>

		<h:panelGroup id="valueList2" rendered="#{!item.readOnly and item.operator.twoValueNeeded}">
			<h:outputText value=" até " />
			<h:inputText id="strInputList2" value="#{item.value2}"
			                           title="#{item.propertyInfo.hint}"
			                           rendered="#{item.propertyInfo.string}"
			                           maxlength="#{item.propertyInfo.size}"
			                           size="20" />
			<h:message for="strInputList2" styleClass="errorMessage" />

			<h:inputText id="intInputList2" value="#{item.value2}"
			                           title="#{item.propertyInfo.hint}"
			                           rendered="#{item.propertyInfo.integer or item.propertyInfo.long}"
			                           maxlength="15"
			                           size="15"
			                           onkeypress="return keyPressInt(this,event)" />
			<h:message for="intInputList2" styleClass="errorMessage" />

			<h:inputText id="bigInputList2" value="#{item.value2}"
			                           title="#{item.propertyInfo.hint}"
			                           rendered="#{item.propertyInfo.bigDecimal}"
			                           maxlength="15"
			                           size="15"
			                           onkeypress="return keyPressInt(this,event)" />
			<h:message for="bigInputList2" styleClass="errorMessage" />

			<h:inputText id="dateInputList2" value="#{item.value2}"
			                           title="#{item.propertyInfo.hint}"
			                           rendered="#{item.propertyInfo.calendar}"
			                           maxlength="10"
			                           size="12"
			                           onblur="return onblurCalendar(this, '#{item.propertyInfo.editMask}')"/>
			<h:message for="dateInputList2" styleClass="errorMessage" />

			<h:selectBooleanCheckbox id="bolInputList2"
			                         value="#{item.value2AsBoolean}"
			                         title="#{item.propertyInfo.hint}"
			                         rendered="#{item.propertyInfo.boolean}"/>
			<h:message for="bolInputList2" styleClass="errorMessage" />

			<h:selectOneMenu id="selectEnumInput2" 
			                 value="#{item.value2}"
			                 rendered="#{item.propertyInfo.enum}">
					<f:selectItems value="#{item.propertyInfo.enumValuesList}" />
			</h:selectOneMenu>
			<h:message for="selectEnumInput2" styleClass="errorMessage" />

			<%-- Subclasse --%>
			<%-- Lista de Classes --%>
			<h:panelGroup rendered="#{item.propertyInfo.entity}" >
   				<h:graphicImage value="../basic/img/query_open_select.png" title="Dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="idSubClassInputList2"
				             value="#{item.value2}"
				             styleClass="queryInputSelectOne"
				             title="Dê um clique na caixa de texto para abrir a pesquisa"
				             size="3"
				             onclick="javascript:openSelectOneId('#{item.propertyInfo.type.name}','',this)"/>
			</h:panelGroup>

			<h:outputLink value="javascript:linkRetrieve('#{item.propertyInfo.type.name}', '#{item.value2}')" rendered="#{item.propertyInfo.entity}">
				<h:outputText value="#{item.value2Description}"/>
			</h:outputLink>
		</h:panelGroup>
		<h:outputText id="strReadOnlyValue2"
						value="#{item.value2} #{item.value2Description}"
			            title="#{item.propertyInfo.hint}"
			            rendered="#{!item.propertyInfo.boolean && item.readOnly}"/>
		<h:selectBooleanCheckbox id="bolReadOnlyValue2"
		                         value="#{item.value2AsBoolean}"
		                         title="#{item.propertyInfo.hint}"
		                         rendered="#{item.propertyInfo.boolean && item.readOnly}"/>



			</h:column>
	</h:dataTable>

	<h:panelGrid rendered="#{queryBean.currentProcess.userReport.hqlWhereParam.hasHqlFields}">
		<h:outputText styleClass="title" value="Preencha os filtros adicionais" escape="false"/>
		<h:outputText value="Os filtros adicionais pré-definidos possibilitam a realização de pesquisas baseadas em parâmetros que o criador do relatório define nas expressões personalizadas." escape="false" styleClass="description"/>
		<h:dataTable width="100%"
			 value="#{queryBean.currentProcess.userReport.hqlWhereParam.hqlFieldsList}"
			 var='item'
			 headerClass="tableAdvancedQueryHeader"
			 styleClass="tableCollectionAdvancedQuery"
			 rowClasses="tableAdvancedQueryRowEven,tableAdvancedQueryRowOdd"
			 columnClasses="tableAdvancedQueryColumnOdd"
			 style="border-collapse: collapse"
			 >

			<h:column>
				<f:facet name="header">
					<h:outputText value="Propriedade:" />
				</f:facet>

				<h:outputText  value="#{item.name}" />

			</h:column>
			<h:column>
				<f:facet name="header">
						<h:outputText value="Conteúdo: " />
				</f:facet>

				<h:inputText  value="#{item.value}" />
			</h:column>
	</h:dataTable>
	</h:panelGrid>


