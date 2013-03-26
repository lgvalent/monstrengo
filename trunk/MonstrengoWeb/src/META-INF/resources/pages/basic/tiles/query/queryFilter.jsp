<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<h:panelGrid >
	<h:panelGroup>
		<!-- h:outputText value="Conteúdo: "/-->
		<h:inputText id="filter" value="#{queryBean.currentProcess.userReport.filterParam.filter}" title="Digite qualquer conteúdo que será procurado em todas as propriedades da entidade, inclusive campos de datas e numéricos" size="60" onblur="onblurAlfanumeric(this)"/>
		<h:commandButton styleClass="noprint" value="Pesquisar" action="#{queryBean.doFilter}" onclick="this.value='Pesquisando...'"/>
	</h:panelGroup>

	<h:panelGrid columns="2">
	  <h:panelGroup>
		<h:outputText styleClass="title noprint" value="Não achou? Crie um novo registro de " escape="false" />
	  </h:panelGroup>
	  <h:panelGrid columns="1">
		<h:commandLink styleClass="noprint" action="#{createBean.actionCreate}"  rendered="#{queryBean.canCreate && !queryBean.info.abstract}" style="font-size: smaller;">
    		<h:graphicImage value="../basic/img/create.png" title="Clique aqui para criar um novo registro de #{queryBean.info.label}" style="border:0" />
			<h:outputText value="#{queryBean.info.label}" />
		  	<f:param value="#{queryBean.info.type.name}" name="entityType" />
		  	<f:param value="#{queryBean.currentParams.selectOneDest}" name="selectOneDest" />
		  	<f:param value="#{queryBean.currentParams.selectProperty}" name="selectProperty" />
		  	<f:param value="#{queryBean.currentProcess.userReport.filterParam.filter}" name="filter" />
		</h:commandLink>
		<h:panelGroup rendered="#{!queryBean.canCreate && !queryBean.info.hasSubEntities}">
			<h:graphicImage styleClass="noprint" value="../basic/img/create_d.png" title="Você não possui direitos para criar um novo registro de #{queryBean.info.label}" style="border:0" />
			<h:outputText value="#{queryBean.info.label}" />
		</h:panelGroup>
	    <h:dataTable
	        styleClass="noprint"
			value="#{queryBean.subEntities}" 
			var='item'
			width="100%"
			rendered="#{queryBean.info.hasSubEntities}">
			<h:column>
				<h:commandLink action="#{createBean.actionCreate}"  rendered="#{item.canCreate}" style="font-size: smaller;">
		    		<h:graphicImage value="../basic/img/create.png" title="Clique aqui para criar um novo registro de #{item.label}" style="border:0" />
					<h:outputText value="#{item.label}" />
	
				  	<f:param value="#{item.type.name}" name="entityType" />
				  	<f:param value="#{queryBean.currentParams.selectOneDest}" name="selectOneDest" />
				  	<f:param value="#{queryBean.currentParams.selectProperty}" name="selectProperty" />
				  	<f:param value="#{queryBean.currentProcess.userReport.filterParam.filter}" name="filter" />
				</h:commandLink>
				<h:graphicImage value="../basic/img/create_d.png" title="Você não possui direitos para criar um novo registro de #{item.label}" style="border:0" rendered="#{!item.canCreate}"/>
			</h:column>
	    </h:dataTable>
	  </h:panelGrid>
	</h:panelGrid>

	<h:panelGrid columns="5" styleClass="noprint">
 	  <h:panelGroup style="text-align: right; color: #006600; font-size: smaller;">
		<h:selectBooleanCheckbox id="sbAdvancedQuery" value="#{queryBean.currentParams.advancedQuery}" title="Marque ou desmarque para Ativar/Desativar a pesquisa com filtros avançados" onclick="this.form.submit()" />
		<h:graphicImage value="../basic/img/advanced_query.png" style="border:0" width="16" height="16"/>
		<h:outputLabel value="Filtrar" for="sbAdvancedQuery"/>
	  </h:panelGroup>

	  <h:panelGroup style="text-align: right; color: #0000FF; font-size: smaller;">
		<h:selectBooleanCheckbox  id="sbAdvancedOrder" value="#{queryBean.currentParams.advancedOrder}" title="Marque ou desmarque para Ativar/Desativar a ordenação avançada" onclick="this.form.submit()" />
		<h:graphicImage value="../basic/img/advanced_sort.png" style="border:0" width="16" height="16"/>
		<h:outputLabel value="Ordenar" for="sbAdvancedOrder"/>
	  </h:panelGroup>

	  <h:panelGroup style="text-align: right; color: #EE2C2C; font-size: smaller;">
		<h:selectBooleanCheckbox  id="sbAdvancedResult" value="#{queryBean.currentParams.advancedResult}" title="Marque ou desmarque para Ativar/Desativar o recurso de seleção das propriedades visíveis para o relatório da pesquisa" onclick="this.form.submit()" />
		<h:graphicImage value="../basic/img/advanced_report.png" style="border:0" width="16" height="16"/>
		<h:outputLabel value="Exibir" for="sbAdvancedResult"/>
	  </h:panelGroup>

	  <h:panelGroup style="text-align: right; color: #FF751A; font-size: smaller;">
		<h:selectBooleanCheckbox  id="sbAdvancedUserReport" value="#{queryBean.currentParams.advancedUserReport}" title="Marque ou desmarque para Ativar/Desativar o recurso de relatórios personalizados" onclick="this.form.submit()" />
		<h:graphicImage value="../basic/img/advanced_user_report.png" style="border:0" width="16" height="16"/>
		<h:outputLabel value="Salvar" for="sbAdvancedUserReport"/>
	  </h:panelGroup>

	  <h:panelGroup style="text-align: right; color: #00AAAA; font-size: smaller;">
		<h:selectBooleanCheckbox  id="sbAdvancedHqlWhere" value="#{queryBean.currentParams.advancedHqlWhere}" title="Marque ou desmarque para Ativar/Desativar o recurso de expressões personalizados" onclick="this.form.submit()" />
		<h:outputLabel value="Expressão" for="sbAdvancedHqlWhere"/>
	  </h:panelGroup>
	</h:panelGrid>
</h:panelGrid>
	