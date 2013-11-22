<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<f:verbatim>
  <style> 
    .fonte{
   		color: #00008B;
		text-align: left;
		font-size: small;
    }
  </style>
</f:verbatim>

<a4j:region id="relatorioCadastro1" selfRendered="true">
	<a4j:status>
		<f:facet name="start">
			<f:verbatim>
				<div id="status" style="position:absolute; top:2px; right:2px; font: 14px; color:#FFFFFF; background: #993300">
		   			Carregando...
				</div>
			</f:verbatim>
		</f:facet>
	</a4j:status>

	<h:form id="form">
		<h:panelGrid styleClass="tableList" style="border-collapse: collapse; font: 12px" width="100%">
			<h:outputLabel value="Selecione os campos que serão exibidos no relatório:" />
			<a4j:outputPanel>
				<%-- Coloca barra de rolagem no eixo y; se quiser no eixo x, colocar 'overflow-x' --%>
				<f:verbatim>
					<div style="height: 70px; overflow-y: scroll;">
				</f:verbatim>

				<%--h:selectManyCheckbox id="campos" value="#{relatorioCadastroBean.process.campoList}" layout="lineDirection" style="font-size:small"--%>
				<h:selectManyCheckbox id="campos"
					value="#{relatorioCadastroBean.process.campoList}"
					layout="pageDirection" style="font-size:small">
					<f:selectItems value="#{relatorioCadastroBean.process.listCampo}" />
				</h:selectManyCheckbox>

				<f:verbatim>
					</div>
				</f:verbatim>

			</a4j:outputPanel>
		</h:panelGrid>
		<h:panelGrid
			columns="2"
			cellpadding="3"
			border="1"
			styleClass="tableList"
			style="border-collapse: collapse; font: 12px"
			width="100%">
			
	   		<h:panelGrid columns="6" style="font: 12px">
	   			<h:outputText value="CPF"/>
		    	<h:outputLink value="javascript:openSelectOneProp('br.com.orionsoft.basic.entities.pessoa.Fisica', document.getElementById('form:cpfCnpj').value, 'documento', document.getElementById('form:cpfCnpj'))" >
					<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0"/>
			    </h:outputLink>
	
	   			<h:outputText value="/CNPJ" />
		    	<h:outputLink value="javascript:openSelectOneProp('br.com.orionsoft.basic.entities.pessoa.Juridica', document.getElementById('form:cpfCnpj').value, 'documento', document.getElementById('form:cpfCnpj'))" >
					<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0"/>
			    </h:outputLink>
	   			<h:outputText value=":" />
				<h:panelGroup>
					<h:inputText value="#{relatorioCadastroBean.process.cpfCnpj}" id="cpfCnpj" size="14" maxlength="14" onkeypress="return keyPressInt(this, event)"/>
			       	<h:message styleClass="errorMessage" for="cpfCnpj"/>
				</h:panelGroup>
	   		</h:panelGrid>

			<h:panelGroup>
				<h:selectBooleanCheckbox id="incluirInativo" value="#{relatorioCadastroBean.process.incluirInativos}"/>
				<h:outputText value=" Incluir inativos" />
			</h:panelGroup>
					
			<h:panelGroup>
				<h:outputLabel value="Escritório contábil:"/>
				<h:graphicImage value="../../public/basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="escritorioContabil"
					value="#{relatorioCadastroBean.process.escritorioContabilId}"
					size="5" maxlength="5" rendered="true"
					title="Dê um clique na caixa de texto para abrir a pesquisa"
					styleClass="queryInputSelectOne"
					onclick="javascript:openSelectOneId('br.com.orionsoft.basic.entities.pessoa.EscritorioContabil',this.value,this)"
					onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
				<h:panelGroup>
					<h:selectBooleanCheckbox id="notEscritorioContabil" value="#{relatorioCadastroBean.process.notEscritorioContabilId}"/>
					<h:outputText value=" Exceto o selecionado" />
				</h:panelGroup>
			</h:panelGroup>

			<h:panelGroup>
				<h:outputLabel value="Ramo de atividade:"/>
				<h:graphicImage value="../../public/basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="cnae"
					value="#{relatorioCadastroBean.process.cnaeId}"
					size="5" maxlength="5" rendered="true"
					title="Dê um clique na caixa de texto para abrir a pesquisa"
					styleClass="queryInputSelectOne"
					onclick="javascript:openSelectOneId('br.com.orionsoft.basic.entities.pessoa.CNAE',this.value,this)"
					onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
				<h:panelGroup>
					<h:selectBooleanCheckbox id="notCnae" value="#{relatorioCadastroBean.process.notCnaeId}"/>
					<h:outputText value=" Exceto o selecionado" />
				</h:panelGroup>
			</h:panelGroup>
			
			<h:panelGroup>
				<h:outputLabel value="Representante:"/>
				<h:graphicImage value="../../public/basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="representante"
					value="#{relatorioCadastroBean.process.representanteId}"
					size="5" maxlength="5" rendered="true"
					title="Dê um clique na caixa de texto para abrir a pesquisa"
					styleClass="queryInputSelectOne"
					onclick="javascript:openSelectOneId('br.com.orionsoft.basic.entities.pessoa.Representante','',this)"
					onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
				<h:panelGroup>
					<h:selectBooleanCheckbox id="notRepresentante" value="#{relatorioCadastroBean.process.notRepresentanteId}"/>
					<h:outputText value=" Exceto o selecionado" />
				</h:panelGroup>
			</h:panelGroup>
			
			<h:panelGroup>
				<h:outputLabel value="Cidade:"/>
				<h:graphicImage value="../../public/basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="cidade"
					value="#{relatorioCadastroBean.process.municipioId}"
					size="5" maxlength="5" rendered="true"
					title="Dê um clique na caixa de texto para abrir a pesquisa"
					styleClass="queryInputSelectOne"
					onclick="javascript:openSelectOneId('br.com.orionsoft.basic.entities.endereco.Municipio','',this)"
					onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
				<h:panelGroup>
					<h:selectBooleanCheckbox id="notMunicipio" value="#{relatorioCadastroBean.process.notMunicipioId}"/>
					<h:outputText value=" Exceto o selecionado" />
				</h:panelGroup>
			</h:panelGroup>

			<h:panelGrid columns="2" styleClass="fonte">		
				<h:panelGroup>	
					<h:outputLabel value="Tipo de estabelecimento: "/>
					<h:selectOneMenu id="estabelecimento" value="#{relatorioCadastroBean.process.tipoEstabelecimento}">
						<f:selectItems value="#{relatorioCadastroBean.process.listTipoEstabelecimento}"/>
					</h:selectOneMenu>
				</h:panelGroup>
				<h:panelGroup>
					<h:selectBooleanCheckbox id="notTipoEstabelecimento" value="#{relatorioCadastroBean.process.notTipoEstabelecimento}"/>
					<h:outputText value=" Exceto o selecionado" />
				</h:panelGroup>
			</h:panelGrid>

			<h:panelGrid columns="2" styleClass="fonte">		
				<h:panelGroup>	
					<h:outputLabel value="Categoria de contrato: "/>
					<h:selectOneMenu id="contratoCategoria" value="#{relatorioCadastroBean.process.contratoCategoriaId}">
						<f:selectItems value="#{relatorioCadastroBean.process.listContratoCategoria}"/>
					</h:selectOneMenu>
				</h:panelGroup>
				<h:panelGroup>
					<h:selectBooleanCheckbox id="notContratoCategoria" value="#{relatorioCadastroBean.process.notContratoCategoriaId}"/>
					<h:outputText value=" Exceto o selecionado" />
				</h:panelGroup>
			</h:panelGrid>
<%-- **************************************************************************************************
			Por enquanto fica oculto,pois tem somente um tipo de relatorio, que e Listagem
			<h:panelGrid columns="2" styleClass="fonte">			
				<h:outputLabel value="Tipo de relatório"/>
				<h:selectOneMenu id="tipoRelatorio" value="#{relatorioCadastroBean.process.tipoRelatorio}">
					<f:selectItems value="#{relatorioCadastroBean.process.listTipoRelatorio}"/>
				</h:selectOneMenu>
			</h:panelGrid>
*******************************************************************************************************--%>
			<h:panelGrid columns="2">
				<h:outputLabel value="Ordenar por: " styleClass="fonte"/>
				<h:selectOneMenu id="ordenar" value="#{relatorioCadastroBean.process.ordenacao}">
					<f:selectItems value="#{relatorioCadastroBean.process.listOrdenacao}"/>
				</h:selectOneMenu>
			</h:panelGrid>

<%-- **************************************************************************************************	
			O tratamento da opção de agrupamento foi deixado pra depois
			<h:panelGrid columns="2">
				<h:outputLabel value="Agrupar por: " styleClass="fonte"/>
				<h:selectOneMenu id="agrupar" value="#{relatorioCadastroBean.process.agrupamento}">
					<f:selectItems value="#{relatorioCadastroBean.process.listAgrupamento}"/>
				</h:selectOneMenu>
			</h:panelGrid>
*******************************************************************************************************--%>

		</h:panelGrid>

		<h:panelGroup styleClass="fonte">
			<h:selectBooleanCheckbox id="incluirDatas" value="#{relatorioCadastroBean.process.incluirDatasCadastro}">
				<a4j:support event="onchange" reRender="tableDatasCadastro" ajaxSingle="#{relatorioCadastroBean.process.incluirDatasCadastro}"/>
			</h:selectBooleanCheckbox>
			<h:outputText value=" Filtrar por datas do cadastro" />
		</h:panelGroup>

		<h:panelGrid id="tableDatasCadastro" styleClass="tableList" columns="1" width="100%" border="1" style="border-collapse: collapse; font: 12px" >
			<h:outputText value="As datas que não forem selecionadas não serão incluídas na pesquisa" style="color: #000000" rendered="#{relatorioCadastroBean.process.incluirDatasCadastro}"/>
			<h:panelGroup id="panelDataContrato" rendered="#{relatorioCadastroBean.process.incluirDatasCadastro}">
				<h:selectBooleanCheckbox id="booleanDataContrato" value="#{relatorioCadastroBean.process.incluirDataContrato}" title="Incluir este filtro">
					<a4j:support event="onchange" reRender="panelDataContrato" ajaxSingle="#{relatorioCadastroBean.process.incluirDataContrato"/>
				</h:selectBooleanCheckbox>
				<h:outputLabel value=" Data inicial do contrato entre " />
				<h:inputText id="dataContratoInicio" value="#{relatorioCadastroBean.process.dataContratoInicio.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataContrato}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy" />
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataContratoInicio" />
				<h:outputLabel value=" e " />
				<h:inputText id="dataContratoFim" value="#{relatorioCadastroBean.process.dataContratoFim.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataContrato}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy" />
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataContratoFim" />
			</h:panelGroup>

			<h:panelGroup id="panelDataContratoRescisao" rendered="#{relatorioCadastroBean.process.incluirDatasCadastro}">
				<h:selectBooleanCheckbox id="booleanDataContratoRescisao" value="#{relatorioCadastroBean.process.incluirDataContratoRescisao}" title="Incluir este filtro">
					<a4j:support event="onchange" reRender="panelDataContratoRescisao" ajaxSingle="#{relatorioCadastroBean.process.incluirDataContratoRescisao"/>
				</h:selectBooleanCheckbox>
				<h:outputLabel value=" Data de rescisão/finalização do contrato entre " />
				<h:inputText id="dataContratoRescisaoInicio" value="#{relatorioCadastroBean.process.dataContratoRescisaoInicio.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataContratoRescisao}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy" />
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataContratoRescisaoInicio" />
				<h:outputLabel value=" e " />
				<h:inputText id="dataContratoRescisaoFim" value="#{relatorioCadastroBean.process.dataContratoRescisaoFim.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataContratoRescisao}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy" />
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataContratoRescisaoFim" />
			</h:panelGroup>

			<h:panelGroup id="panelDataComecoAtividade" rendered="#{relatorioCadastroBean.process.incluirDatasCadastro}">
				<h:selectBooleanCheckbox id="booleanDataComecoAtividade" value="#{relatorioCadastroBean.process.incluirDataComecoAtividade}" title="Incluir este filtro">
					<a4j:support event="onchange" reRender="panelDataComecoAtividade" ajaxSingle="#{relatorioCadastroBean.process.incluirDataComecoAtividade"/>
				</h:selectBooleanCheckbox>
				<h:outputLabel value=" Data de atividade da pessoa física/jurídica iniciada entre " />
				<h:inputText id="dataComecoAtividadeInicio" value="#{relatorioCadastroBean.process.dataComecoAtividadeInicio.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataComecoAtividade}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy" />
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataComecoAtividadeInicio" />
				<h:outputLabel value=" e " />
				<h:inputText id="dataComecoAtividadeFim" value="#{relatorioCadastroBean.process.dataComecoAtividadeFim.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataComecoAtividade}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy" />
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataComecoAtividadeFim" />
			</h:panelGroup>

			<h:panelGroup id="panelDataTerminoAtividade" rendered="#{relatorioCadastroBean.process.incluirDatasCadastro}">
				<h:selectBooleanCheckbox id="booleanDataTerminoAtividade" value="#{relatorioCadastroBean.process.incluirDataTerminoAtividade}" title="Incluir este filtro">
					<a4j:support event="onchange" reRender="panelDataTerminoAtividade" ajaxSingle="#{relatorioCadastroBean.process.incluirDataTerminoAtividade"/>
				</h:selectBooleanCheckbox>
				<h:outputLabel value=" Data final da pessoa física/jurídica entre " />
				<h:inputText id="dataTerminoAtividadeInicio" value="#{relatorioCadastroBean.process.dataTerminoAtividadeInicio.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataTerminoAtividade}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy" />
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataTerminoAtividadeInicio" />
				<h:outputLabel value=" e " />
				<h:inputText id="dataTerminoAtividadeFim" value="#{relatorioCadastroBean.process.dataTerminoAtividadeFim.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataTerminoAtividade}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy" />
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataTerminoAtividadeFim" />
			</h:panelGroup>

		</h:panelGrid>

		<h:panelGroup styleClass="fonte">
			<h:selectBooleanCheckbox id="incluirFinanceiro" value="#{relatorioCadastroBean.process.incluirMovimentacoes}">
				<a4j:support event="onchange" reRender="tableFinanceiro, itens" ajaxSingle="#{relatorioCadastroBean.process.incluirMovimentacoes}"/>
			</h:selectBooleanCheckbox>
			<h:outputText value=" Filtrar por movimentações" />
		</h:panelGroup>

		<h:panelGrid id="tableFinanceiro" styleClass="tableList" columns="1" width="100%" border="1" style="border-collapse: collapse; font: 12px">
			<h:outputText value="As datas não que não forem selecionadas não serão incluídas na pesquisa" style="color: #000000" rendered="#{relatorioCadastroBean.process.incluirMovimentacoes}"/>
			<h:panelGroup id="panelDataLancamento" rendered="#{relatorioCadastroBean.process.incluirMovimentacoes}">
				<h:selectBooleanCheckbox id="booleanDataLancamento" value="#{relatorioCadastroBean.process.incluirDataLancamento}" title="Incluir este filtro">
					<a4j:support event="onchange" reRender="panelDataLancamento" ajaxSingle="#{relatorioCadastroBean.process.incluirDataLancamento"/>
				</h:selectBooleanCheckbox>
				<h:outputLabel value=" Data de lançamento entre "/>
		 		<h:inputText id="dataLancamentoInicio" value="#{relatorioCadastroBean.process.dataLancamentoInicio.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataLancamento}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataLancamentoInicio"/>
				<h:outputLabel value=" e "/>
				<h:inputText id="dataLancamentoFim" value="#{relatorioCadastroBean.process.dataLancamentoFim.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataLancamento}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataLancamentoFim"/>
			</h:panelGroup>

			<h:panelGroup id="panelDataLancamentoRecebimento" rendered="#{relatorioCadastroBean.process.incluirMovimentacoes}">
				<h:selectBooleanCheckbox id="booleanDataLancamentoRecebimento" value="#{relatorioCadastroBean.process.incluirDataLancamentoRecebimento}" title="Incluir este filtro">
					<a4j:support event="onchange" reRender="panelDataLancamentoRecebimento" ajaxSingle="#{relatorioCadastroBean.process.incluirDataLancamentoRecebimento"/>
				</h:selectBooleanCheckbox>
				<h:outputLabel value=" Data de recebimento entre "/>
		 		<h:inputText id="dataLancamentoRecebimentoInicio" value="#{relatorioCadastroBean.process.dataLancamentoRecebimentoInicio.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataLancamentoRecebimento}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataLancamentoRecebimentoInicio"/>
				<h:outputLabel value=" e "/>
				<h:inputText id="dataLancamentoRecebimentoFim" value="#{relatorioCadastroBean.process.dataLancamentoRecebimentoFim.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataLancamentoRecebimento}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataLancamentoRecebimentoFim"/>
			</h:panelGroup>

			<h:panelGroup id="panelDataLancamentoVencimento" rendered="#{relatorioCadastroBean.process.incluirMovimentacoes}">
				<h:selectBooleanCheckbox id="booleanDataLancamentoVencimento" value="#{relatorioCadastroBean.process.incluirDataLancamentoVencimento}" title="Incluir este filtro">
					<a4j:support event="onchange" reRender="panelDataLancamentoVencimento" ajaxSingle="#{relatorioCadastroBean.process.incluirDataLancamentoVencimento"/>
				</h:selectBooleanCheckbox>
				<h:outputLabel value=" Data de vencimento entre "/>
		 		<h:inputText id="dataLancamentoVencimentoInicio" value="#{relatorioCadastroBean.process.dataLancamentoVencimentoInicio.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataLancamentoVencimento}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataLancamentoVencimentoInicio"/>
				<h:outputLabel value=" e "/>
				<h:inputText id="dataLancamentoVencimentoFim" value="#{relatorioCadastroBean.process.dataLancamentoVencimentoFim.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')" disabled="#{!relatorioCadastroBean.process.incluirDataLancamentoVencimento}">
					<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataLancamentoVencimentoFim"/>
			</h:panelGroup>

			<%-- O uso do <div> entre os verbatins não eram renderizados quando utilizado o componente <h:panelGroup>.
			Utilizando o componente <a4j:outputPanel>, o conteudo entre os verbatins são renderizados
			fonte: http://www.jroller.com/a4j/category/jsf (acessado em 26/02/2009)--%>
			<a4j:outputPanel id="itens" rendered="#{relatorioCadastroBean.process.incluirMovimentacoes}">
				<h:outputLabel value="Itens de custo"/>
				<%-- Coloca barra de rolagem no eixo y; se quiser no eixo x, colocar 'overflow-x' --%>
				<f:verbatim>
					<div  style="height: 60px; overflow-y: scroll;">
				</f:verbatim>

				<h:selectManyCheckbox id="itensCusto" value="#{relatorioCadastroBean.process.itemCustoList}" layout="pageDirection" style="font-size:small;" >
					<f:selectItems value="#{relatorioCadastroBean.process.listItemCusto}"/>
				</h:selectManyCheckbox>

				<f:verbatim>
					</div>
				</f:verbatim>
				
			</a4j:outputPanel>
			
		</h:panelGrid>
						
<%-- botão --%>
		<h:panelGrid columns="1" width="100%">
			<h:commandButton value="Visualizar" action="#{relatorioCadastroBean.doVisualizar}" onclick="this.value='Aguarde...'"/>
		</h:panelGrid>
	</h:form>
	
</a4j:region>
