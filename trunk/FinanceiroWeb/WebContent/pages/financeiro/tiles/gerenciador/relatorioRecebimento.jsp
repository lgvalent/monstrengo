<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form">
	<h:panelGrid
		columns="3"
		cellpadding="3"
		border="1"
		styleClass="tableList"
		style="border-collapse: collapse">

		<%-- Item de custo --%>
		<h:outputLabel value="#{menuBean.infoMap.LancamentoItem.propertiesMetadata.itemCusto.label}: "/>
 		<h:panelGrid columns="4">
 			<h:graphicImage value="../../public/basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
			<h:inputText id="itemCusto"
				value="#{relatorioRecebimentoBean.process.itemCustoIdList}"
				size="5" maxlength="5" rendered="true"
				title="Dê um clique na caixa de texto para abrir a pesquisa"
				styleClass="queryInputSelectOne"
				onclick="javascript:openSelectOneId('br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto',this.value,this)"
				onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
			<h:selectBooleanCheckbox value="#{relatorioRecebimentoBean.process.itemCustoNot}" />
			<h:outputLabel value="Exceto o selecionado" for="" style="font-size: small"/>
		</h:panelGrid>
		<h:message styleClass="errorMessage" for="itemCusto"/>

		<%-- Escritório contábil --%>
		<h:outputLabel value="#{menuBean.infoMap.Juridica.propertiesMetadata.escritorioContabil.label}: "/>
 		<h:panelGrid columns="4">
 			<h:graphicImage value="../../public/basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
			<h:inputText id="escritorioContabil"
				value="#{relatorioRecebimentoBean.process.escritorioContabilIdList}"
				size="5" maxlength="5" rendered="true"
				title="Dê um clique na caixa de texto para abrir a pesquisa"
				styleClass="queryInputSelectOne"
				onclick="javascript:openSelectOneId('br.com.orionsoft.basic.entities.pessoa.EscritorioContabil',this.value,this)"
				onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
		</h:panelGrid>
		<h:message styleClass="errorMessage" for="escritorioContabil"/>
 
 		<%-- Categoria do contrato --%>
		<h:outputLabel value="#{menuBean.infoMap.Contrato.propertiesMetadata.categoria.label}: "/>
		<h:selectOneMenu id="categoriaContrato" value="#{relatorioRecebimentoBean.process.categoriaContratoId}" >
			<f:selectItems value="#{relatorioRecebimentoBean.process.listCategoriaContrato}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="categoriaContrato"/>

		<%-- Municipio --%>
		<h:outputLabel value="#{menuBean.infoMap.Endereco.propertiesMetadata.municipio.label}: "/>
		<h:panelGrid columns="3">
			<h:selectOneMenu id="municipio" value="#{relatorioRecebimentoBean.process.municipioId}" >
				<f:selectItems value="#{relatorioRecebimentoBean.process.listMunicipio}"/>
			</h:selectOneMenu>
			<h:selectBooleanCheckbox value="#{relatorioRecebimentoBean.process.notMunicipio}" />
			<h:outputLabel value="Exceto o selecionado" for="" style="font-size: small"/>
		</h:panelGrid>
		<h:message styleClass="errorMessage" for="municipio"/>

		<%-- Conta --%>
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.conta.label}: "/>
		<h:selectOneMenu id="conta" value="#{relatorioRecebimentoBean.process.contaId}" >
			<f:selectItems value="#{relatorioRecebimentoBean.process.listConta}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="conta"/>

		<%-- Contrato representante --%>
		<h:outputLabel value="#{menuBean.infoMap.Contrato.propertiesMetadata.representante.label}: "/>
		<h:selectOneMenu id="contratoRepresentante" value="#{relatorioRecebimentoBean.process.contratoRepresentanteId}" >
			<f:selectItems value="#{relatorioRecebimentoBean.process.listContratoRepresentante}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="contratoRepresentante"/>

		<%-- Tipo de contrato --%>
		<h:outputLabel value="Contratos:"/>
		<h:selectOneRadio id="tipoContrato" value="#{relatorioRecebimentoBean.process.tipoContrato}" style="font-size: small" >
			<f:selectItems value="#{relatorioRecebimentoBean.process.listTipoContrato}"/>
		</h:selectOneRadio>
		<h:message styleClass="errorMessage" for="tipoContrato"/>

<%-- 
		<h:outputLabel value="Data de lançamento:"/>
		<h:panelGroup>
	 		<h:inputText id="dataLancamentoInicial" value="#{relatorioRecebimentoBean.process.dataLancamentoInicial.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
	 		</h:inputText>
			<h:outputLabel value=" até "/>
 			<h:inputText id="dataLancamentoFinal" value="#{relatorioRecebimentoBean.process.dataLancamentoFinal.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
 			</h:inputText>
		</h:panelGroup>
		<h:panelGroup>
			<h:message styleClass="errorMessage" for="dataLancamentoInicial"/>
			<h:message styleClass="errorMessage" for="dataLancamentoFinal"/>
		</h:panelGroup>
 --%>

		<%-- Data de vencimento --%>
		<h:outputLabel value="Data de vencimento:"/>
		<h:panelGroup>
	 		<h:inputText id="dataVencimentoInicial" value="#{relatorioRecebimentoBean.process.dataVencimentoInicial.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
	 		</h:inputText>
			<h:outputLabel value=" até "/>
 			<h:inputText id="dataVencimentoFinal" value="#{relatorioRecebimentoBean.process.dataVencimentoFinal.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
 			</h:inputText>
		</h:panelGroup>
		<h:panelGroup>
			<h:message styleClass="errorMessage" for="dataVencimentoInicial"/>
			<h:message styleClass="errorMessage" for="dataVencimentoFinal"/>
		</h:panelGroup>

		<%-- Data de recebimento --%>
		<h:outputLabel value="Data de recebimento:"/>
		<h:panelGroup>
	 		<h:inputText id="dataRecebimentoInicial" value="#{relatorioRecebimentoBean.process.dataRecebimentoInicial.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
	 		</h:inputText>
			<h:outputLabel value=" até "/>
 			<h:inputText id="dataRecebimentoFinal" value="#{relatorioRecebimentoBean.process.dataRecebimentoFinal.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
 			</h:inputText>
		</h:panelGroup>
		<h:panelGroup>
			<h:message styleClass="errorMessage" for="dataRecebimentoInicial"/>
			<h:message styleClass="errorMessage" for="dataRecebimentoFinal"/>
		</h:panelGroup>

		<%-- Quantidade de itens --%>
		<h:outputLabel value="Quantidade de itens:"/>
		<h:panelGroup>
	 		<h:inputText id="quantidadeItensInicial" value="#{relatorioRecebimentoBean.process.quantidadeItensInicial}" size="4" maxlength="5" onkeypress="return keyPressInt(this, event)"/>
			<h:outputLabel value=" até "/>
 			<h:inputText id="quantidadeItensFinal" value="#{relatorioRecebimentoBean.process.quantidadeItensFinal}" size="4" maxlength="5" onkeypress="return keyPressInt(this, event)"/>
		</h:panelGroup>
		<h:panelGroup>
			<h:message styleClass="errorMessage" for="quantidadeItensInicial"/>
			<h:message styleClass="errorMessage" for="quantidadeItensFinal"/>
		</h:panelGroup>

		<%-- CPF/CNPJ --%>
   		<h:panelGrid columns="5" styleClass="tableList">
   			<h:outputText value="CPF"/>
	    	<h:outputLink value="javascript:openSelectOneProp('br.com.orionsoft.basic.entities.pessoa.Fisica', document.getElementById('body:form:cpfCnpj').value, 'documento', document.getElementById('body:form:cpfCnpj'))" >
				<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0"/>
		    </h:outputLink>

   			<h:outputText value="/CNPJ" />
	    	<h:outputLink value="javascript:openSelectOneProp('br.com.orionsoft.basic.entities.pessoa.Juridica', document.getElementById('body:form:cpfCnpj').value, 'documento', document.getElementById('body:form:cpfCnpj'))" >
				<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0"/>
		    </h:outputLink>
   			<h:outputText value=":" />
   		</h:panelGrid>
   		<h:inputText value="#{relatorioRecebimentoBean.process.cpfCnpj}" id="cpfCnpj" size="14" maxlength="14" onkeypress="return keyPressInt(this, event)"/>
	   	<h:message  styleClass="errorMessage" for="cpfCnpj" />

	</h:panelGrid>

	<h:panelGrid columns="1" width="100%">
		<h:commandButton value="Download PDF" action="#{relatorioRecebimentoBean.doVisualizar}" onclick="this.value='Aguarde...'"/>
	</h:panelGrid>

</h:form>
