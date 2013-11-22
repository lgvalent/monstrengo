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
				value="#{relatorioCobrancaBean.process.itemCustoIdList}"
				size="5" maxlength="5" rendered="true"
				title="Dê um clique na caixa de texto para abrir a pesquisa"
				styleClass="queryInputSelectOne"
				onclick="javascript:openSelectOneId('br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto',this.value,this)"
				onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
			<h:selectBooleanCheckbox value="#{relatorioCobrancaBean.process.itemCustoNot}" />
			<h:outputLabel value="Exceto o selecionado" for="" style="font-size: small"/>
		</h:panelGrid>
		<h:message styleClass="errorMessage" for="itemCusto"/>
 
 		<%-- Escritório contábil --%>
		<h:outputLabel value="#{menuBean.infoMap.Juridica.propertiesMetadata.escritorioContabil.label}: "/>
 		<h:panelGrid columns="4">
 			<h:graphicImage value="../../public/basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
			<h:inputText id="escritorioContabil"
				value="#{relatorioCobrancaBean.process.escritorioContabilIdList}"
				size="5" maxlength="5" rendered="true"
				title="Dê um clique na caixa de texto para abrir a pesquisa"
				styleClass="queryInputSelectOne"
				onclick="javascript:openSelectOneId('br.com.orionsoft.basic.entities.pessoa.EscritorioContabil',this.value,this)"
				onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
		</h:panelGrid>
		<h:message styleClass="errorMessage" for="escritorioContabil"/>
 
 		<%-- CNAE --%>
		<h:outputLabel value="#{menuBean.infoMap.Juridica.propertiesMetadata.cnae.label}: "/>
 		<h:panelGrid columns="4">
 			<h:graphicImage value="../../public/basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
			<h:inputText id="cnae"
				value="#{relatorioCobrancaBean.process.cnaeId}"
				size="5" maxlength="5" rendered="true"
				title="Dê um clique na caixa de texto para abrir a pesquisa"
				styleClass="queryInputSelectOne"
				onclick="javascript:openSelectOneId('br.com.orionsoft.basic.entities.pessoa.CNAE',this.value,this)"
				onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
		</h:panelGrid>
		<h:message styleClass="errorMessage" for="cnae"/>
 
 		<%-- CNAE descrição --%>
		<h:outputLabel value="CNAE descrições"/>
 		<h:panelGrid columns="3">
			<h:inputText id="cnaeDescricao"
				value="#{relatorioCobrancaBean.process.cnaeDescricao}"
				size="25" rendered="true"
				styleClass="queryInputSelectOne"
				title="Entre com mais de uma descrição separada por vírgula. Exemplo: Comércio,Varejo,Ferramentas"/>
		</h:panelGrid>
		<h:message styleClass="errorMessage" for="cnaeDescricao"/>
 
 		<%-- Categoria do contrato --%>
		<h:outputLabel value="#{menuBean.infoMap.Contrato.propertiesMetadata.categoria.label}: "/>
		<h:selectOneMenu id="categoriaContrato" value="#{relatorioCobrancaBean.process.categoriaContratoId}" >
			<f:selectItems value="#{relatorioCobrancaBean.process.listCategoriaContrato}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="categoriaContrato"/>

		<%-- Municipio --%>
		<h:outputLabel value="#{menuBean.infoMap.Endereco.propertiesMetadata.municipio.label}: "/>
		<h:panelGrid columns="3">
			<h:selectOneMenu id="municipio" value="#{relatorioCobrancaBean.process.municipioId}" >
				<f:selectItems value="#{relatorioCobrancaBean.process.listMunicipio}"/>
			</h:selectOneMenu>
			<h:selectBooleanCheckbox value="#{relatorioCobrancaBean.process.notMunicipio}" />
			<h:outputLabel value="Exceto o selecionado" for="" style="font-size: small"/>
		</h:panelGrid>
		<h:message styleClass="errorMessage" for="municipio"/>

		<%-- Contrato representante --%>
		<h:outputLabel value="#{menuBean.infoMap.Contrato.propertiesMetadata.representante.label}: "/>
		<h:selectOneMenu id="contratoRepresentante" value="#{relatorioCobrancaBean.process.contratoRepresentanteId}" >
			<f:selectItems value="#{relatorioCobrancaBean.process.listContratoRepresentante}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="contratoRepresentante"/>

		<%-- Tipo de contrato --%>
		<h:outputLabel value="Contratos:"/>
		<h:selectOneRadio id="tipoContrato" value="#{relatorioCobrancaBean.process.tipoContrato}" style="font-size: small" >
			<f:selectItems value="#{relatorioCobrancaBean.process.listTipoContrato}"/>
		</h:selectOneRadio>
		<h:message styleClass="errorMessage" for="tipoContrato"/>

		<%-- Data de lançamento --%>
		<h:outputLabel value="Data de lançamento:"/>
		<h:panelGroup>
	 		<h:inputText id="dataLancamentoInicial" value="#{relatorioCobrancaBean.process.dataLancamentoInicial.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
	 		</h:inputText>
			<h:outputLabel value=" até "/>
 			<h:inputText id="dataLancamentoFinal" value="#{relatorioCobrancaBean.process.dataLancamentoFinal.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
 			</h:inputText>
		</h:panelGroup>
		<h:panelGroup>
			<h:message styleClass="errorMessage" for="dataLancamentoInicial"/>
			<h:message styleClass="errorMessage" for="dataLancamentoFinal"/>
		</h:panelGroup>

		<%-- Data de vencimento --%>
		<h:outputLabel value="Data de vencimento:"/>
		<h:panelGroup>
	 		<h:inputText id="dataVencimentoInicial" value="#{relatorioCobrancaBean.process.dataVencimentoInicial.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
	 		</h:inputText>
			<h:outputLabel value=" até "/>
 			<h:inputText id="dataVencimentoFinal" value="#{relatorioCobrancaBean.process.dataVencimentoFinal.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
 			</h:inputText>
		</h:panelGroup>
		<h:panelGroup>
			<h:message styleClass="errorMessage" for="dataVencimentoInicial"/>
			<h:message styleClass="errorMessage" for="dataVencimentoFinal"/>
		</h:panelGroup>

		<%-- Data de pagamento para calculo de multa e juros --%>
		<h:outputLabel value="Calcular multa e juros até:"/>
		<h:panelGroup>
	 		<h:inputText id="dataPagamento" value="#{relatorioCobrancaBean.process.dataPagamento.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
	 		</h:inputText>
		</h:panelGroup>
		<h:panelGroup>
			<h:message styleClass="errorMessage" for="dataPagamento"/>
		</h:panelGroup>

		<%-- Quantidade de itens --%>
		<h:outputLabel value="Quantidade de itens:"/>
		<h:panelGroup>
	 		<h:inputText id="quantidadeItensInicial" value="#{relatorioCobrancaBean.process.quantidadeItensInicial}" size="4" maxlength="5" onkeypress="return keyPressInt(this, event)"/>
			<h:outputLabel value=" até "/>
 			<h:inputText id="quantidadeItensFinal" value="#{relatorioCobrancaBean.process.quantidadeItensFinal}" size="4" maxlength="5" onkeypress="return keyPressInt(this, event)"/>
		</h:panelGroup>
		<h:panelGroup>
			<h:message styleClass="errorMessage" for="quantidadeItensInicial"/>
			<h:message styleClass="errorMessage" for="quantidadeItensFinal"/>
		</h:panelGroup>
		
		<%-- Omitir valores --%>
		<h:outputLabel value="Não mostrar valores:"/>
		<h:selectBooleanCheckbox id="omitirValores" value="#{relatorioCobrancaBean.process.omitirValores}" />
		<h:message styleClass="errorMessage" for="omitirValores"/>
		

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
   		<h:inputText value="#{relatorioCobrancaBean.process.cpfCnpj}" id="cpfCnpj" size="14" maxlength="14" onkeypress="return keyPressInt(this, event)"/>
	   	<h:message  styleClass="errorMessage" for="cpfCnpj" />

	</h:panelGrid>

	<%-- Botões --%>
	<h:panelGrid columns="1" width="100%">
		<%-- Modelo do relatório de cobrança --%>
		<h:outputLabel value="Modelo do relatório:"/>
   		<h:selectOneMenu value="#{relatorioCobrancaBean.process.relatorioCobrancaModelo}" id="relatorioCobrancaModelo">
   			<f:selectItems value="#{relatorioCobrancaBean.process.listRelatorioCobrancaModelo}"/>
   		</h:selectOneMenu>
	   	<h:message  styleClass="errorMessage" for="relatorioCobrancaModelo" />
		<h:commandButton value="Gerar relatório de cobrança" action="#{relatorioCobrancaBean.doVisualizar}"/>
	</h:panelGrid>
	<h:panelGrid columns="1" width="100%">
		<%-- Modelo da carta de cobrança --%>
		<h:outputLabel value="Modelo da carta de cobrança:"/>
   		<h:selectOneMenu value="#{relatorioCobrancaBean.process.cartaCobrancaModelo}" id="cartaCobrancaModelo">
   			<f:selectItems value="#{relatorioCobrancaBean.process.listCartaCobrancaModelo}"/>
   		</h:selectOneMenu>
	   	<h:message  styleClass="errorMessage" for="cartaCobrancaModelo" />
		<h:commandButton value="Gerar carta de cobrança" action="#{relatorioCobrancaBean.doImprimirCartaCobranca}"/>
	</h:panelGrid>
	<h:panelGrid columns="1" width="100%">
		<h:commandButton value="Gerar etiqueta" action="#{relatorioCobrancaBean.doGerarEtiqueta}"/>
	</h:panelGrid>
<%-- 
	<h:panelGrid columns="2">
		<h:commandLink action="#{relatorioCobrancaBean.doImprimirCartaCobranca}" title="Gera carta de cobrança.">
			<h:outputText value="Imprimir carta de cobrança modelo " />
		</h:commandLink>
   		<h:selectOneMenu value="#{relatorioCobrancaBean.process.cartaCobrancaModelo}" id="cartaCobrancaModelo">
   			<f:selectItems value="#{relatorioCobrancaBean.process.listCartaCobrancaModelo}"/>
   		</h:selectOneMenu>
	</h:panelGrid>
 --%>

</h:form>
