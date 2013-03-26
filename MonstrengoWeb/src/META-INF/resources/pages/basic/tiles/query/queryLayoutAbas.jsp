<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<f:subview id="Query">
  <h:form id="formQuery">
	<%-- Sincroniza a atual entidade manipulada pela view --%>
	<h:inputHidden immediate="true" id="currentEntityKey" value="#{queryBean.currentEntityKey}" validator="#{queryBean.validateCurrentEntityKey}"/>
	<%-- Fornece um acesso para que o BEAN invalide um valor da view JSF e forçe a reconstrução quando necessário --%>
	<h:inputHidden immediate="true" binding="#{queryBean.inputCurrentEntityKey}"/>
	<%--/h:form--%>

	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr valign="top">
			<td>
			  <h:panelGroup styleClass="parentGroupHeader" rendered="#{queryBean.currentProcess.userReport.parentParam.hasParent}">
			    <f:verbatim>
					<SCRIPT>borderCreator.initDarkBorder('100%', '', 'center', 'top')</SCRIPT>
				</f:verbatim>
				<tiles:insert attribute="parent" ignore="false"/>
			    <f:verbatim>
					<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>
				</f:verbatim>
			  </h:panelGroup>
			</td>
		</tr>
		<tr>
			<td>
				<SCRIPT>borderCreator.initDarkBorder('100%', '', 'left', 'top')</SCRIPT>
				<tiles:insert attribute="filter" ignore="false"/>
				<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>
			</td>
		</tr>
		<tr>
			<td>
			   <t:panelTabbedPane width="100%" selectedIndex="0" rendered="#{queryBean.currentParams.advancedUserReport == true || queryBean.currentParams.advancedQuery == true || queryBean.currentParams.advancedOrder == true || queryBean.currentParams.advancedResult == true}">
			   		<t:panelTab rendered="#{queryBean.currentParams.advancedUserReport == true}" label="Relatório personalizado">
<h:outputLabel value="Grave e recupere relatórios personalizados: " style="color: #FF751A;"/>
<h:panelGrid columns="2">
  <h:panelGrid >
    <h:panelGrid>
      <h:panelGroup>
      	<h:outputLabel value="Meus relatórios:"/>
      	<h:commandLink action="#{queryBean.doRefreshListUserReport}">
	      	<h:outputText value="[atualizar]" title="Atualiza a lista de relatórios salvos"/>
      	</h:commandLink>
      </h:panelGroup>
      <h:selectOneMenu id="userReportId" value="#{queryBean.currentParams.userReportId}">
        <f:selectItems value="#{queryBean.listUserReport}"/>
      </h:selectOneMenu>
      <h:message for="userReportId"/>

      <h:panelGrid columns="4">
  	    <h:commandButton value="Carregar" action="#{queryBean.doRestoreUserReport}" onclick="this.value='Carregando...'" title="Carrega o relatório atualmente selecionado na lista"/>
	    <h:commandButton value="Excluir" onclick="javascript:return confirm('Confirma exclusão do relatório selecionado na lista?')" action="#{queryBean.doDeleteUserReport}" title="Exclui o relatório atualmente selecionado na lista"/>
	    <h:commandButton value="Limpar" action="#{queryBean.doClearUserReport}" onclick="this.value='Limpando...'" title="Limpa todos os atuais parâmetros da pesquisa"/>
	    <h:commandButton value="Salvar" onclick="javascript:return (popupPage('../basic/querySaveUserReport.jsp',600,300) || false)" title="Salva os atuais parâmetros de pesquisa para serem recuperados depois"/>
      </h:panelGrid>
    </h:panelGrid>
  </h:panelGrid>
  <h:panelGrid >
     <h:outputLabel value="Relatório ativo: #{queryBean.currentProcess.userReport.name}"/>
     <h:outputLabel value="Descrição: #{queryBean.currentProcess.userReport.description}"/>
  </h:panelGrid>
</h:panelGrid>
			   		</t:panelTab>

			   		<t:panelTab rendered="#{queryBean.currentParams.advancedQuery == true}" label="Filtro avançado">>
	<h:outputLabel value="Crie filtros avançados para melhor restringir sua pesquisa:"  style="color: #006600;"/>
	<h:panelGrid>
  	  <h:panelGrid columns="2">
		<h:outputLabel value="e/ou:"/>
		<h:outputLabel value="Propriedade:"/>

        <h:selectOneMenu id="e_ou" value="#{queryBean.newCondictionParam.initOperator}">
    		<f:selectItems value="#{queryBean.currentProcess.userReport.condictionParam.listInitOperator}"/>
        </h:selectOneMenu>

        <h:selectOneMenu id="propertyPath" value="#{queryBean.newCondictionParam.propertyPath}" onchange="this.form.submit()">
    		<f:selectItems value="#{queryBean.currentProcess.userReport.condictionParam.listPropertyPath}"/>
        </h:selectOneMenu>
  	  </h:panelGrid>
  	  <h:panelGrid columns="2">
  	  <h:panelGrid>
		<h:outputLabel value="Condição:"/>
        <h:selectOneMenu id="operatorId" value="#{queryBean.newCondictionParam.operatorId}" onchange="this.form.submit()">
    		<f:selectItems value="#{queryBean.currentProcess.userReport.condictionParam.listOperatorId}"/>
        </h:selectOneMenu>
  	  </h:panelGrid>

  	  <h:panelGrid columns="2">
  	  <h:panelGrid rendered="#{queryBean.newCondictionParam.operator.oneValueNeeded}">
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

  	  <h:panelGrid rendered="#{queryBean.newCondictionParam.operator.twoValueNeeded}">
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

	<h:dataTable width="100%"
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

	  			<h:commandLink action="#{queryBean.doRemoveCondiction}">
					<h:graphicImage value="../basic/img/delete.png" title="Remove este filtro da lista" style="border:0"/>
					<f:param name="condictionId" value="#{item.id}"/>
				</h:commandLink>

	  			<h:selectBooleanCheckbox value="#{item.active}" title="Ativa/Desativa este filtro para a pesquisa"/>
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:panelGroup>
						<h:outputLabel value="Lista dos filtros atuais: " />
						<h:commandButton value="Limpar" action="#{queryBean.doClearCondictions}" title="Remove todos os filtros da lista"/>
					</h:panelGroup>
				</f:facet>
				<h:outputText value="#{item.propertyPathLabel} #{item.operator.label} "/>
				<h:inputText value="#{item.value1}" size="10" rendered="#{item.operator.oneValueNeeded}" title="Digite um novo valor para esta condição do filtro e pressione ENTER para atualizar"/>
				<h:outputText value=" até " rendered="#{item.operator.twoValueNeeded}" />
				<h:inputText value="#{item.value2}" size="10" rendered="#{item.operator.twoValueNeeded}" title="Digite um novo valor para esta condição do filtro e pressione ENTER para atualizar"/>


			</h:column>
	</h:dataTable>
			   		</t:panelTab>

			   		<t:panelTab rendered="#{queryBean.currentParams.advancedOrder == true}" label="Ordenação avançada">>
	<h:outputLabel value="Crie ordens avançadas para sua pesquisa: " style="color: #0000FF;"/>
	<h:panelGrid columns="3" >
  	  <h:panelGrid columns="2">
		<h:outputLabel value="Propriedade:"/>
		<h:outputLabel value="Direção:"/>

        <h:selectOneMenu id="orderPath" value="#{queryBean.newOrderParam.propertyPath}">
    		<f:selectItems value="#{queryBean.currentProcess.userReport.orderParam.listPropertyPath}"/>
        </h:selectOneMenu>

        <h:selectOneMenu id="asc_desc" value="#{queryBean.newOrderParam.orderDirection}">
    		<f:selectItems value="#{queryBean.currentProcess.userReport.orderParam.listOrderDirection}"/>
        </h:selectOneMenu>

  	  </h:panelGrid>

  	  <h:panelGrid>
		<h:commandButton value="Adicionar" action="#{queryBean.doAddNewOrder}" onclick="this.value='Adicionando...'" title="Adiciona a atual condição de ordem na lista de condições sem executar a atualização"/>
		<h:commandButton value="Adicionar e atualizar" action="#{queryBean.doAddNewOrderAndRefresh}" onclick="this.value='Adicionando...'" title="Adiciona a atual condição de ordem na lista e atualiza a pesquisa para exibir a nova ordem"/>
  	  </h:panelGrid>

	</h:panelGrid>

	<h:dataTable
	         id="tbo"
			 width="100%"
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
					<h:graphicImage value="../basic/img/delete.png" title="Remove esta condição de ordenação da lista" style="border:0"/>
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
			   		</t:panelTab>

			   		<t:panelTab rendered="#{queryBean.currentParams.advancedResult == true}" label="Seleção das propriedades">
	<h:outputLabel value="Indique quais propriedades serão exibidas no relatório da pesquisa:" style="color: #EE2C2C;"/>
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
						<h:commandButton value="Marcar" title="Marca todas as propriedades da lista atual" onclick="return checkAll(this.form, 'myCheck')"/>
						<h:commandButton value="Desmarcar" title="Desmarca todas as propriedades da lista atual" onclick="return clearAll(this.form, 'myCheck')"/>
						<h:commandButton value="Inverter" title="Inverte a seleção das propriedades da lista atual" onclick="return inverseAll(this.form, 'myCheck')"/>
						<h:commandButton value="Desfazer" title="Volta a seleção anterior às alterações recentemente efetuadas" type="reset"/>
					</h:panelGroup>
				</f:facet>
				<h:outputText value="#{item.propertyPathLabel}" style="font-size: small;"/>
			</h:column>

	</h:dataTable>
	<f:verbatim>
		</div>
	</f:verbatim>

	<h:outputLabel value="Defina a ordem de exibição das propriedades no relatório da pesquisa:" style="color: #EE2C2C;"/>
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
				<h:inputText id="index" value="#{item.resultIndex}" size="3" onkeypress="return keyPressInt(this,event)">
					<f:validateLongRange minimum="0" maximum="9999" />
				</h:inputText>
	  			<h:selectBooleanCheckbox value="#{item.visible}"/>
				<h:outputText value="#{item.propertyPathLabel}" style="font-size: small;"/>
			</h:column>
	</h:dataTable>
			   		</t:panelTab>
			   </t:panelTabbedPane>
			</td>
		</tr>
		<tr>
			<td>
				<f:subview id="UpperPagination">
					<SCRIPT>borderCreator.initDarkBorder('100%', '', 'center', 'top')</SCRIPT>
					<tiles:insert attribute="pagination" ignore="false"/>
					<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>
				</f:subview>
			</td>
		</tr>
		<tr>
			<td align="center">
				<br>
				<tiles:insert attribute="body"/>
				<br>
			</td>
		</tr>
		<tr>
			<td>
				<SCRIPT>borderCreator.initDarkBorder('100%', '', 'center', 'top')</SCRIPT>
				<tiles:insert attribute="command" ignore="false"/>
				<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>
			</td>
		</tr>
		<tr>
			<td>
			  <h:panelGroup rendered="#{queryBean.hasModelsLabelEntity}">
			    <f:verbatim>
					<SCRIPT>borderCreator.initDarkBorder('100%', '', 'left', 'top')</SCRIPT>
			    </f:verbatim>
			    <tiles:insert attribute="label" ignore="false"/>
			    <f:verbatim>
				<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>
			    </f:verbatim>
			  </h:panelGroup>
			</td>
		</tr>
	</table>
  <%--/h:form--%>
  </h:form>
</f:subview>
