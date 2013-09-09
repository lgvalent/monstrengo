<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

	<h:form id="form">
<%-- Entrada de dados para geração do relatório --%>
		<h:panelGrid
			columns="3"
			cellpadding="3"
			border="1"
			styleClass="tableList noprint"
			style="border-collapse: collapse">

			<%-- CPF/CNPJ --%>
	   		<h:panelGrid columns="5" styleClass="tableList" border="0">
	   			<h:outputText value="CPF"/>
		    	<h:outputLink value="javascript:openSelectOneProp('br.com.orionsoft.basic.entities.pessoa.Fisica', document.getElementById('form:cpfCnpj').value, 'documento', document.getElementById('form:cpfCnpj'))" >
					<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0"/>
			    </h:outputLink>
	
	   			<h:outputText value="/CNPJ" />
		    	<h:outputLink value="javascript:openSelectOneProp('br.com.orionsoft.basic.entities.pessoa.Juridica', document.getElementById('form:cpfCnpj').value, 'documento', document.getElementById('form:cpfCnpj'))" >
					<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0"/>
			    </h:outputLink>
	   			<h:outputText value=":" />
	   		</h:panelGrid>
	   		<h:inputText value="#{listarLancamentoBean.process.cpfCnpj}" id="cpfCnpj" size="18" maxlength="18" onkeypress="return keyPressInt(this, event)"
	   					title="Forneça o CPF ou o CNPJ completo ou ainda, somente os primeiros dígitos para seleção de todas as filiais de uma mesma empresa">
			</h:inputText>
			<h:panelGroup id="contratoDescription" >
			   <h:outputText value="#{listarLancamentoBean.process.pessoa}" styleClass="tableViewColumnOdd"/>
		       <h:message styleClass="errorMessage" for="cpfCnpj"/>
			</h:panelGroup>

			<%-- Data --%>
			<h:outputLabel value="Data:"/>
			<h:panelGroup>
				<h:outputLabel value="de "/>
				<h:inputText id="dataInicial" value="#{listarLancamentoBean.process.dataInicial.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
					<f:convertDateTime pattern="dd/MM/yyyy" locale="Locale.BRAZIL"/>
				</h:inputText>
				<h:outputLabel value=" até "/>
				<h:inputText id="dataFinal" value="#{listarLancamentoBean.process.dataFinal.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
					<f:convertDateTime pattern="dd/MM/yyyy" locale="Locale.BRAZIL"/>
				</h:inputText>
			</h:panelGroup>
			<h:message styleClass="errorMessage" for="dataInicial"/>

			<%-- Situação --%>
			<h:outputLabel value="Situação:"/>
			<h:selectOneRadio id="situacao" value="#{listarLancamentoBean.process.situacao}" style="font-size: small">
				<f:selectItems value="#{listarLancamentoBean.process.listSituacao}"/>
			</h:selectOneRadio>
			<h:message styleClass="errorMessage" for="situacao"/>

			<%-- Conta --%>
			<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.contaPrevista.label}"/>
			<h:selectOneMenu id="conta" value="#{listarLancamentoBean.process.conta}">
				<f:selectItems value="#{listarLancamentoBean.process.listConta}"/>
			</h:selectOneMenu>
			<h:message styleClass="errorMessage" for="conta"/>
			
			<%-- Centro custo --%>
			<h:outputLabel value="#{menuBean.infoMap.LancamentoItem.propertiesMetadata.centroCusto.label}:"/>
	 		<h:panelGrid columns="2">
	 			<h:graphicImage value="../basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="centroCustoId"
					value="#{listarLancamentoBean.process.centroCusto}"
					size="5" maxlength="5" rendered="true"
					title="Dê um clique na caixa de texto para abrir a pesquisa"
					styleClass="queryInputSelectOne"
					onclick="javascript:openSelectOneId('br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto',this.value,this)"
					onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
			</h:panelGrid>
			<h:message styleClass="errorMessage" for="centroCustoId"/>

			<%-- Item custo --%>
			<h:outputLabel value="#{menuBean.infoMap.LancamentoItem.propertiesMetadata.itemCusto.label}:"/>
	 		<h:panelGrid columns="4" >
	 			<h:graphicImage value="../basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="itemCustoList"
					value="#{listarLancamentoBean.process.itemCustoIdList}"
					size="5" maxlength="5" rendered="true"
					title="Dê um clique na caixa de texto para abrir a pesquisa"
					styleClass="queryInputSelectOne"
					onclick="javascript:openSelectOneId('br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto',this.value,this)"
					onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
				<h:selectBooleanCheckbox value="#{listarLancamentoBean.process.itemCustoIdListNot}" />
				<h:outputLabel value="Exceto o selecionado" for="" style="font-size: small"/>
			</h:panelGrid>
			<h:message styleClass="errorMessage" for="itemCustoList"/>

			<%-- Ordem --%>
			<h:outputLabel value="Ordem:"/>
			<h:selectOneRadio id="ordem" value="#{listarLancamentoBean.process.ordem}" style="font-size: small">
				<f:selectItems value="#{listarLancamentoBean.process.listOrdem}"/>
			</h:selectOneRadio>
			<h:message styleClass="errorMessage" for="ordem"/>
			
		</h:panelGrid>
		<h:commandButton value="Listar" action="#{listarLancamentoBean.doVisualizar}" styleClass="noprint"/>
	</h:form>

	<h:form target="_blank">
		<h:panelGrid rendered="#{listarLancamentoBean.visualizando}">
			<h:dataTable
				width="100%"
				border="1"
				value="#{listarLancamentoBean.list}"
			    var='item'
			    rendered="#{listarLancamentoBean.size>0}"
			    headerClass="tableListHeader"
			    footerClass="tableListFooter"
			    styleClass="tableList"
			    rowClasses="tableListRowEven, tableListRowOdd"
			    columnClasses="tableListColumn,tableListColumn,tableListColumn,tableListColumn,tableListColumn,tableListColumnRight,tableListColumnRight,tableListColumnRight,tableListColumnRight"
			    style="border-collapse: collapse">

				<h:column >
	  				<h:commandLink action="#{quitarLancamentoBean.actionInicio}" rendered="#{item.situacao=='PENDENTE'}" styleClass="noprint">
						<h:graphicImage value="../financeiro/img/quitar.png" title="Quita o lançamento" style="border:0" />
				     	<f:param name="lancamentoId" value="#{item.id}"/>
				     	<f:param name="lancamentoSaldo" value="#{item.saldo}"/>
					</h:commandLink>
					<h:graphicImage value="../financeiro/img/quitar_d.png" title="Quita o lançamento" style="border:0" rendered="#{item.situacao!='PENDENTE'}" styleClass="noprint"/>
					
					<h:outputLink value="javascript:linkRetrieve('#{menuBean.infoMap.Lancamento.type.name}', '#{item.id}')"  rendered="#{menuBean.crudMap.Lancamento.canRetrieve}" styleClass="noprint">
						<h:graphicImage value="../basic/img/retrieve.png" title="Visualiza os detalhes do registro" style="border:0"/>
					</h:outputLink>
					<h:graphicImage value="../basic/img/retrieve_d.png" title="Você não possui direitos para visualizar os dados do registro" style="border:0" rendered="#{!menuBean.crudMap.Lancamento.canRetrieve}" styleClass="noprint"/>
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Data"/>
					</f:facet>
					<h:outputLabel value="#{item.data.time}" />
				</h:column>
 				<h:column>
					<f:facet name="header">
						<h:outputText value="Conta"/>
					</f:facet>
					<h:outputLabel value="#{item.contaPrevista}" style="white-space: nowrap"/>
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Contrato"/>
					</f:facet>
					<h:outputLabel value="#{item.pessoa}" />
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Descrição"/>
					</f:facet>
					<h:outputLabel value="#{item.descricao}" />
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Vencimento"/>
					</f:facet>
					<h:panelGrid columns="1" width="100%" styleClass="tableListRow" rendered="#{item.situacao!='PENDENTE' || item.dataVencimento.time>=listarLancamentoBean.dataAtual.time}">
						<h:outputLabel value="#{item.dataVencimento.time}" />
					</h:panelGrid>
					<h:panelGrid columns="1" width="100%" style="background:#FFC0CB" styleClass="tableListRow" rendered="#{item.situacao=='PENDENTE' && item.dataVencimento.time<listarLancamentoBean.dataAtual.time}">
						<h:outputLabel value="#{item.dataVencimento.time}" />
					</h:panelGrid>
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Valor"/>
					</f:facet>
					<h:outputLabel value="#{item.valor}" >
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
					</h:outputLabel>
					<f:facet name="footer">
						<h:outputLabel value="#{listarLancamentoBean.process.soma}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputLabel>
					</f:facet>
				</h:column>
			</h:dataTable>
			<h:panelGrid columns="5" style="text-align:left" styleClass="tableListRow" >
				<h:outputLabel value="#{listarLancamentoBean.size} registro(s)"/>
				<h:outputLabel value=" | Créditos: "/>
				<h:outputLabel value="#{listarLancamentoBean.process.credito}">
					<f:convertNumber pattern="###,###,##0.00" type="currency"/>
				</h:outputLabel>	
				<h:outputLabel value=" | Débitos: "/>
				<h:outputLabel value="#{listarLancamentoBean.process.debito}">
					<f:convertNumber pattern="###,###,##0.00" type="currency"/>
				</h:outputLabel>	
			</h:panelGrid>
		</h:panelGrid>
			
		<h:outputText styleClass="errorMessage" value="#{messagesBean.firstMessageSummary}" escape="false" />
	</h:form>
