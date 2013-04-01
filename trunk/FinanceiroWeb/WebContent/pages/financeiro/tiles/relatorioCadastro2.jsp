<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<a4j:region id="relatorioCadastro2" selfRendered="true">
	<h:form id="form">
		<h:panelGrid
			columns="2"
			cellpadding="3"
			border="1"
			styleClass="tableList"
			style="border-collapse: collapse; font: 12px">
			
   			<h:outputText value="CPF/CNPJ:"/>
	   		<h:outputText value="#{relatorioCadastroBean.process.cpfCnpj}"></h:outputText>

			<h:outputText value="Incluir inativos" />
			<h:outputText value="#{relatorioCadastroBean.process.incluirInativos}"/>
			
			<h:outputLabel value="Data de cadastro:"/>
			<h:panelGroup>
				<h:outputLabel value="de "/>
		 		<h:outputText value="#{relatorioCadastroBean.process.dataInicial.time}" />
				<h:outputLabel value=" até "/>
				<h:outputText value="#{relatorioCadastroBean.process.dataFinal.time}" />
			</h:panelGroup>

			<h:panelGroup>
				<h:outputLabel value="Escritório contábil:"/>
				<h:outputText value="#{relatorioCadastroBean.process.escritorioContabilId}" />
				<h:outputText value="Exceto selecionado: "/>
				<h:outputText value="#{relatorioCadastroBean.process.notEscritorioContabilId}"/>
			</h:panelGroup>

			<h:panelGroup>
				<h:outputLabel value="Ramo de atividade:"/>
				<h:outputText value="#{relatorioCadastroBean.process.cnaeId}"/>
				<h:outputText value="Exceto selecionado: "/>
				<h:outputText value="#{relatorioCadastroBean.process.notCnaeId}"/>
			</h:panelGroup>

			<h:panelGroup>
				<h:outputLabel value="Representante:"/>
				<h:outputText value="#{relatorioCadastroBean.process.representanteId}"/>
				<h:outputText value="Exceto selecionado: "/>
				<h:outputText value="#{relatorioCadastroBean.process.notRepresentanteId}"/>
			</h:panelGroup>

			<h:panelGroup>
				<h:outputLabel value="Cidade:"/>
				<h:outputText value="#{relatorioCadastroBean.process.municipioId}"/>
				<h:outputText value="Exceto selecionado: "/>
				<h:outputText value="#{relatorioCadastroBean.process.notMunicipioId}"/>
			</h:panelGroup>

			<h:panelGroup>
				<h:outputLabel value="Tipo de estabelecimento:"/>
				<h:outputText value="#{relatorioCadastroBean.process.tipoEstabelecimento}" />
				<h:outputText value="Exceto selecionado: "/>
				<h:outputText value="#{relatorioCadastroBean.process.notTipoEstabelecimentoId}"/>
			</h:panelGroup>

			<h:panelGroup>
				<h:outputLabel value="Contrato categoria"/>
				<h:outputText  value="#{relatorioCadastroBean.process.contratoCategoriaId}"/>
				<h:outputText value="Exceto selecionado: "/>
				<h:outputText value="#{relatorioCadastroBean.process.notContratoCategoriaId}"/>
			</h:panelGroup>
			
			<h:outputLabel value="Ordenar por"/>
			<h:outputText value="#{relatorioCadastroBean.process.ordenacao}"/>

			<h:outputLabel value="Agrupar por"/>
			<h:outputText value="#{relatorioCadastroBean.process.agrupamento}"/>

			<h:outputLabel value="Tipo de relatório"/>
			<h:outputText  value="#{relatorioCadastroBean.process.tipoRelatorio}"/>

			<h:outputText value="Filtrar por movimentações " />
			<h:outputText value="#{relatorioCadastroBean.process.incluirMovimentacoes}"/>

			<h:outputLabel value="Incluir movimentações retroativas:"/>
			<h:panelGroup>
				<h:outputLabel value="de "/>
		 		<h:outputText id="retroativoInicial" value="#{relatorioCadastroBean.process.retroativoInicial.time}" />
				<h:outputLabel value=" até "/>
				<h:outputText value="#{relatorioCadastroBean.process.retroativoFinal.time}" />
			</h:panelGroup>

			<h:outputLabel value="Itens de custo"/>
			<h:dataTable width="100%"
				border="1" value="#{relatorioCadastroBean.process.itemCustoList}"
			    var='itemCusto'
			    headerClass="tableListHeader"
			    styleClass="tableList"
			    rowClasses="tableListRowEven"
			    columnClasses="tableListColumn"
			    style="border-collapse:collapse: font: 12px">
			
				<h:column >
					<f:facet name="header">
						<h:outputLabel value="Item" styleClass="tableListHeader"/>
					</f:facet>
					<h:outputLabel value="#{itemCusto}"/>
				</h:column>
			</h:dataTable>

			<h:outputLabel value="Mostrar campos"/>
			<h:dataTable width="100%"
				border="1" value="#{relatorioCadastroBean.process.campoList}"
			    var='campo'
			    headerClass="tableListHeader"
			    styleClass="tableList"
			    rowClasses="tableListRowEven"
			    columnClasses="tableListColumn"
			    style="border-collapse:collapse: font: 12px">
			
				<h:column >
					<f:facet name="header">
						<h:outputLabel value="Campo" styleClass="tableListHeader"/>
					</f:facet>
					<h:outputLabel value="#{campo}"/>
				</h:column>
			</h:dataTable>
		</h:panelGrid>
	
	</h:form>
	
</a4j:region>
