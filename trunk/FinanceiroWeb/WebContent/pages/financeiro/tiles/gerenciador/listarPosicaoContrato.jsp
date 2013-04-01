<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form">
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
	   		<h:inputText value="#{listarPosicaoContratoBean.cpfCnpj}" id="cpfCnpj" size="18" maxlength="18" onkeypress="return keyPressInt(this, event)"
	   					title="Forneça o CPF ou o CNPJ completo ou ainda, somente os primeiros dígitos para seleção de todas as filiais de uma mesma empresa">
				<%--a4j:support event="onblur" reRender="contratoDescription" action="#{listarPosicaoContratoBean.doValidarCpfCnpj}" ajaxSingle="true"/ --%>
			</h:inputText>
			<h:panelGroup id="contratoDescription" >
			   <h:outputText value="#{listarPosicaoContratoBean.process.pessoa}" styleClass="tableViewColumnOdd"/>
		       <h:message styleClass="errorMessage" for="cpfCnpj"/>
			</h:panelGroup>

			<%-- Data --%>
			<h:outputLabel value="Data:"/>
			<h:panelGroup>
				<h:outputLabel value="de "/>
		 		<h:inputText id="dataInicial" value="#{listarPosicaoContratoBean.process.dataInicial.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
					<f:convertDateTime locale="pt_BR" pattern="dd/MM/yyyy"/>
				</h:inputText>
				<h:message styleClass="errorMessage" for="dataInicial"/>
				<h:outputLabel value=" até "/>
				<h:inputText id="dataFinal" value="#{listarPosicaoContratoBean.process.dataFinal.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
					<f:convertDateTime locale="pt_BR" pattern="dd/MM/yyyy"/>
				</h:inputText>
			</h:panelGroup>
			<h:message styleClass="errorMessage" for="dataFinal"/>
			
			<%-- Situação --%>
			<h:outputLabel value="Situação:"/>
			<h:selectOneMenu id="situacao" value="#{listarPosicaoContratoBean.process.situacao}" styleClass="input">
				<f:selectItems value="#{listarPosicaoContratoBean.process.listSituacao}"/>
			</h:selectOneMenu>
			<h:message styleClass="errorMessage" for="situacao"/>
	
			<%-- Item custo --%>
			<h:outputLabel value="#{menuBean.infoMap.LancamentoItem.propertiesMetadata.itemCusto.label}:"/>
	 		<h:panelGrid columns="4">
	 			<h:graphicImage value="../basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="itemCustoList"
					value="#{listarPosicaoContratoBean.process.itemCustoList}"
					size="5" maxlength="5" rendered="true"
					title="Dê um clique na caixa de texto para abrir a pesquisa"
					styleClass="queryInputSelectOne"
					onclick="javascript:openSelectOneId('br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto',this.value,this)"
					onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
				<h:selectBooleanCheckbox value="#{listarPosicaoContratoBean.process.itemCustoListNot}" />
				<h:outputLabel value="Exceto o item de custo selecionado" for="" style="font-size: small" />
			</h:panelGrid>
			<h:message styleClass="errorMessage" for="itemCustoList"/>

			<%-- Conta --%>
			<h:outputLabel value="Contas:"/>
			<h:selectManyListbox id="conta" value="#{listarPosicaoContratoBean.process.contaList}" styleClass="input">
				<f:selectItems value="#{listarPosicaoContratoBean.process.listConta}"/>
			</h:selectManyListbox>
			<h:message for="conta" styleClass="errorMessage"/>
			
		</h:panelGrid>
	
<%-- botão --%>
		<h:panelGrid columns="1" width="100%" styleClass="noprint">
			<h:commandButton value="Listar" action="#{listarPosicaoContratoBean.doVisualizar}" onclick="this.value='Aguarde...'"/>
		</h:panelGrid>
</h:form>

<%-- Informações cadastrais do cliente que está sendo consultado --%>
	<h:form rendered="#{listarPosicaoContratoBean.visualizando && listarPosicaoContratoBean.cpfCnpj!=''}">
		<h:panelGrid styleClass="tableList">
			<h:outputLink value="javascript:linkRetrieve('#{menuBean.infoMap.Pessoa.type.name}', '#{listarPosicaoContratoBean.process.pessoa.id}')">
				<h:outputText value="#{listarPosicaoContratoBean.process.pessoa.object.documento} "/>
				<h:outputText value="#{listarPosicaoContratoBean.process.pessoa.object.nome}"/>
			</h:outputLink>
			<h:panelGroup>
				<h:outputText value="#{listarPosicaoContratoBean.process.pessoa.object.enderecoCorrespondencia.logradouro.tipoLogradouro} "/>
				<h:outputText value="#{listarPosicaoContratoBean.process.pessoa.object.enderecoCorrespondencia.logradouro.nome}, "/>
				<h:outputText value="#{listarPosicaoContratoBean.process.pessoa.object.enderecoCorrespondencia.numero} - "/>
				<h:outputText value="#{listarPosicaoContratoBean.process.pessoa.object.enderecoCorrespondencia.bairro.nome}"/>
			</h:panelGroup>
			<h:panelGroup>
				<h:outputText value="#{listarPosicaoContratoBean.process.pessoa.object.enderecoCorrespondencia.cep} "/>
				<h:outputText value="#{listarPosicaoContratoBean.process.pessoa.object.enderecoCorrespondencia.municipio.nome} - "/>
				<h:outputText value="#{listarPosicaoContratoBean.process.pessoa.object.enderecoCorrespondencia.municipio.uf.sigla}"/>
			</h:panelGroup>
		</h:panelGrid>
	</h:form>
	
<%-- Listagem --%>
	<h:form 
	    target="_blank" 
	    rendered="#{listarPosicaoContratoBean.visualizando}" 
	    >
		<h:panelGroup rendered="#{listarPosicaoContratoBean.size<1}">
			<h:outputLabel value="Nenhum lançamento encontrado para as condições acima definidas." />
		</h:panelGroup>

		<h:outputText styleClass="errorMessage" value="#{messagesBean.firstMessageSummary}" escape="false" />
		<h:panelGrid width="100%">
			<h:dataTable
				width="100%"
				border="0" value="#{listarPosicaoContratoBean.array}"
			    var='lancamento'
			    headerClass="tableListHeader"
			    footerClass="tableListFooter"
			    styleClass="tableList"
			    rowClasses="tableListRowEven"
			    columnClasses="tableListColumn"
			    style="border-collapse:collapse">
			    
				<h:column>
			<f:facet name="header">
				<h:panelGroup rendered="#{listarPosicaoContratoBean.size>0}">
					<h:outputLabel value="Seleção dos lançamentos: " />
					<h:commandButton value="Marcar" title="Marca todos os contratos da lista atual" onclick="return checkAll(this.form, 'myCheck')"/>
					<h:commandButton value="Intervalo" title="Marca todas as propriedades entre um intervalo definido" onclick="return checkRange(this.form, 'myCheck')"/>
					<h:commandButton value="Desmarcar" title="Desmarca todos os contratos da lista atual" onclick="return clearAll(this.form, 'myCheck')"/>
					<h:commandButton value="Inverter" title="Inverte a seleção dos os contratos da lista atual" onclick="return inverseAll(this.form, 'myCheck')"/>
					<h:commandButton value="Desfazer" title="Volta a seleção anterior às alterações recentemente efetuadas" type="reset"/>
					<f:verbatim>
						<br>
					</f:verbatim>
					<h:outputLabel value="Ações selecionados: " />
					<h:commandButton value="Quitar" action="#{listarPosicaoContratoBean.actionQuitarSelecionados}"/>
					<h:commandButton value="Cancelar" action="#{listarPosicaoContratoBean.actionCancelarSelecionados}" />
					<h:commandButton value="Alterar vencimento" action="#{listarPosicaoContratoBean.actionAlterarVencimentoDocumentosCobrancaSelecionados}" />
					<h:commandButton value="Imprimir Doc. Cobrança" action="#{listarPosicaoContratoBean.actionImprimirDocumentosCobrancaSelecionados}" />
				</h:panelGroup>
			</f:facet>
			<f:facet name="footer">
				<h:panelGroup rendered="#{listarPosicaoContratoBean.size>0}">
					<h:outputLabel value="Seleção dos lançamentos: " />
					<h:commandButton value="Marcar" title="Marca todos os contratos da lista atual" onclick="return checkAll(this.form, 'myCheck')"/>
					<h:commandButton value="Intervalo" title="Marca todas as propriedades entre um intervalo definido" onclick="return checkRange(this.form, 'myCheck')"/>
					<h:commandButton value="Desmarcar" title="Desmarca todos os contratos da lista atual" onclick="return clearAll(this.form, 'myCheck')"/>
					<h:commandButton value="Inverter" title="Inverte a seleção dos os contratos da lista atual" onclick="return inverseAll(this.form, 'myCheck')"/>
					<h:commandButton value="Desfazer" title="Volta a seleção anterior às alterações recentemente efetuadas" type="reset"/>
					<f:verbatim>
						<br>
					</f:verbatim>
					<h:outputLabel value="Ações selecionados: " />
					<h:commandButton value="Quitar" action="#{listarPosicaoContratoBean.actionQuitarSelecionados}"/>
					<h:commandButton value="Cancelar" action="#{listarPosicaoContratoBean.actionCancelarSelecionados}" />
					<h:commandButton value="Alterar vencimento" action="#{listarPosicaoContratoBean.actionAlterarVencimentoDocumentosCobrancaSelecionados}" />
					<h:commandButton value="Imprimir Doc. Cobrança" action="#{listarPosicaoContratoBean.actionImprimirDocumentosCobrancaSelecionados}" />
				</h:panelGroup>
			</f:facet>
<%-- lançamento quitado --%>
 					<h:panelGrid columns="2" width="100%" styleClass="tableListRow" rendered="#{lancamento.object.lancamentoSituacao=='QUITADO'}">
						<h:outputLabel value="#{lancamento.object.contrato.pessoa.nome}" style="font-weight:bold" rendered="#{listarPosicaoContratoBean.cpfCnpj==''}"/>
						<h:outputLabel rendered="#{listarPosicaoContratoBean.cpfCnpj==''}"/>
						<h:panelGroup>
							<h:graphicImage value="../financeiro/img/quitar_d.png" title="Quita o lançamento" style="border:0 noprint"/>
	
							<h:graphicImage value="../financeiro/img/baixar_d.png" title="Baixa o lançamento" style="border:0 noprint"/>
	
							<h:outputLabel value="Valor "/>
							<h:outputLabel value="#{lancamento.object.valor}" style="font-weight:bold">
								<f:convertNumber type="currency"/>
							</h:outputLabel>
							<h:outputLabel value=" lançado em " />
							<h:outputLabel value="#{lancamento.object.data.time}" style="font-weight:bold"/>
							<h:outputLabel value=" com vencimento para " />
							<h:outputLabel value="#{lancamento.object.dataVencimento.time}" style="font-weight:bold"/>
							<f:verbatim><br></f:verbatim>
							<h:outputLink value="javascript:linkRetrieve('br.com.orionsoft.financeiro.gerenciador.entities.Lancamento', '#{lancamento.object.id}')" >
								<h:outputText value=" #{lancamento.object.descricao} "/>
							</h:outputLink>
						</h:panelGroup>
						<h:panelGrid width="100%" style="text-align:right" styleClass="tableListRow">
							<h:panelGroup>
								<h:outputLabel value="Saldo em aberto: " />
								<h:outputLabel value="#{lancamento.object.saldo}">
									<f:convertNumber type="currency"/>
								</h:outputLabel>
							</h:panelGroup>
						</h:panelGrid>
					</h:panelGrid>
<%-- lançamento cancelado --%>
 					<h:panelGrid columns="2" width="100%" style="background:#C0C0C0" styleClass="tableListRow" rendered="#{lancamento.object.lancamentoSituacao=='CANCELADO'}">
						<h:outputLabel value="#{lancamento.object.contrato.pessoa.nome}" style="font-weight:bold" rendered="#{listarPosicaoContratoBean.cpfCnpj==''}"/>
						<h:outputLabel rendered="#{listarPosicaoContratoBean.cpfCnpj==''}"/>
						<h:panelGroup>
							<h:graphicImage value="../financeiro/img/quitar_d.png" title="Quita o lançamento" style="border:0 noprint"/>
	
							<h:graphicImage value="../financeiro/img/baixar_d.png" title="Baixa o lançamento" style="border:0 noprint"/>
	
							<h:outputLabel value="Valor "/>
							<h:outputLabel value="#{lancamento.object.valor}" style="font-weight:bold">
								<f:convertNumber type="currency"/>
							</h:outputLabel>
							<h:outputLabel value=" lançado em " />
							<h:outputLabel value="#{lancamento.object.data.time}" style="font-weight:bold"/>
							<h:outputLabel value=" com vencimento para " />
							<h:outputLabel value="#{lancamento.object.dataVencimento.time}" style="font-weight:bold"/>
							<f:verbatim><br></f:verbatim>
							<h:outputLink value="javascript:linkRetrieve('br.com.orionsoft.financeiro.gerenciador.entities.Lancamento', '#{lancamento.object.id}')" >
								<h:outputText value=" #{lancamento.object.descricao} "/>
							</h:outputLink>
						</h:panelGroup>
						<h:panelGrid width="100%" style="text-align:right" styleClass="tableListRow">
							<h:panelGroup>
								<h:outputLabel value="Saldo em aberto: " />
								<h:outputLabel value="#{lancamento.object.saldo}">
									<f:convertNumber type="currency"/>
								</h:outputLabel>
							</h:panelGroup>
						</h:panelGrid>
					</h:panelGrid>
<%-- lançamento pendente --%> 
					<h:panelGrid columns="2" width="100%" style="background:#FFFFCC" styleClass="tableListRow" rendered="#{lancamento.object.lancamentoSituacao=='PENDENTE' && lancamento.object.dataVencimento.time>=listarPosicaoContratoBean.dataAtual.time}">
						<h:outputLabel value="#{lancamento.object.contrato.pessoa.nome}" style="font-weight:bold" rendered="#{listarPosicaoContratoBean.cpfCnpj==''}"/>
						<h:outputLabel rendered="#{listarPosicaoContratoBean.cpfCnpj==''}"/>
						<h:panelGroup>
							<h:selectBooleanCheckbox id="myCheck1" value="#{lancamento.selected}"/>
			  				<h:commandLink action="#{quitarLancamentoBean.actionInicio}" >
								<h:graphicImage value="../financeiro/img/quitar.png" title="Quita o lançamento" style="border:0" />
						     	<f:param name="lancamentoId" value="#{lancamento.object.id}"/>
						     	<f:param name="lancamentoSaldo" value="#{lancamento.object.saldo}"/>
							</h:commandLink>

			  				<h:commandLink action="#{cancelarLancamentoBean.actionCancelar}" >
								<h:graphicImage value="../financeiro/img/baixar.png" title="Cancela o lançamento" style="border:0" />
						     	<f:param name="lancamentoId" value="#{lancamento.object.id}"/>
						     	<f:param name="lancamentoSaldo" value="#{lancamento.object.saldo}"/>
							</h:commandLink>

							<h:outputLabel value="Valor "/>
							<h:outputLabel value="#{lancamento.object.valor}" style="font-weight:bold">
								<f:convertNumber type="currency"/>
							</h:outputLabel>
							<h:outputLabel value=" lançado em " />
							<h:outputLabel value="#{lancamento.object.data.time}" style="font-weight:bold"/>
							<h:outputLabel value=" com vencimento para " />
							<h:outputLabel value="#{lancamento.object.dataVencimento.time}" style="font-weight:bold"/>
							<f:verbatim><br></f:verbatim>
							<h:outputLink value="javascript:linkRetrieve('br.com.orionsoft.financeiro.gerenciador.entities.Lancamento', '#{lancamento.object.id}')" >
								<h:outputText value=" #{lancamento.object.descricao} "/>
							</h:outputLink>
						</h:panelGroup>
						<h:panelGrid width="100%" style="text-align:right" styleClass="tableListRow">
							<h:panelGroup>
								<h:outputLabel value="Saldo em aberto: " />
								<h:outputLabel value="#{lancamento.object.saldo}" rendered="#{lancamento.object.saldo>0}" style="font-weight:bold">
									<f:convertNumber type="currency"/>
								</h:outputLabel>
								<h:outputLabel value="#{lancamento.object.saldo}" rendered="#{lancamento.object.saldo<0}" style="color:red font-weight:bold">
									<f:convertNumber type="currency"/>
								</h:outputLabel>
							</h:panelGroup>
						</h:panelGrid>
					</h:panelGrid>
				
<%-- lançamento vencido --%>
					<h:panelGrid columns="2" width="100%" style="background:#FFC0CB" styleClass="tableListRow" rendered="#{lancamento.object.lancamentoSituacao=='PENDENTE' && lancamento.object.dataVencimento.time<listarPosicaoContratoBean.dataAtual.time}">
						<h:outputLabel value="#{lancamento.object.contrato.pessoa.nome}" style="font-weight:bold" rendered="#{listarPosicaoContratoBean.cpfCnpj==''}"/>
						<h:outputLabel rendered="#{listarPosicaoContratoBean.cpfCnpj==''}"/>
						<h:panelGroup>
							<h:selectBooleanCheckbox id="myCheck2" value="#{lancamento.selected}"/>
			  				<h:commandLink action="#{quitarLancamentoBean.actionInicio}" >
								<h:graphicImage value="../financeiro/img/quitar.png" title="Quita o lançamento" style="border:0" />
						     	<f:param name="lancamentoId" value="#{lancamento.object.id}"/>
						     	<f:param name="lancamentoSaldo" value="#{lancamento.object.saldo}"/>
							</h:commandLink>

			  				<h:commandLink action="#{cancelarLancamentoBean.actionCancelar}" >
								<h:graphicImage value="../financeiro/img/baixar.png" title="Cancela o lançamento" style="border:0" />
						     	<f:param name="lancamentoId" value="#{lancamento.object.id}"/>
						     	<f:param name="lancamentoSaldo" value="#{lancamento.object.saldo}"/>
							</h:commandLink>

							<h:outputLabel value="Valor "/>
							<h:outputLabel value="#{lancamento.object.valor}" style="font-weight:bold">
								<f:convertNumber type="currency"/>
							</h:outputLabel>
							<h:outputLabel value=" lançado em " />
							<h:outputLabel value="#{lancamento.object.data.time}" style="font-weight:bold"/>
							<h:outputLabel value=" com vencimento para " />
							<h:outputLabel value="#{lancamento.object.dataVencimento.time}" style="font-weight:bold"/>
							<f:verbatim><br></f:verbatim>
							<h:outputLink value="javascript:linkRetrieve('br.com.orionsoft.financeiro.gerenciador.entities.Lancamento', '#{lancamento.object.id}')" >
								<h:outputText value=" #{lancamento.object.descricao} "/>
							</h:outputLink>
						</h:panelGroup>
						<h:panelGrid width="100%" style="text-align:right" styleClass="tableListRow">
							<h:panelGroup>
								<h:outputLabel value="Saldo em aberto: " />
								<h:outputLabel value="#{lancamento.object.saldo}" rendered="#{lancamento.object.saldo>0}" style="font-weight:bold">
									<f:convertNumber type="currency"/>
								</h:outputLabel>
								<h:outputLabel value="#{lancamento.object.saldo}" rendered="#{lancamento.object.saldo<0}" style="color:red font-weight:bold">
									<f:convertNumber type="currency"/>
								</h:outputLabel>
							</h:panelGroup>
						</h:panelGrid>
					</h:panelGrid>
<%-- Movimento do lançamento --%>
					<h:dataTable
						width="100%"
						border="0" value="#{lancamento.object.lancamentoMovimentos}"
					    var='lancamentoMovimento'
					    rendered="#{listarPosicaoContratoBean.size>0}"
					    headerClass="tableListHeader"
					    styleClass="tableList"
					    rowClasses="tableListRowOdd"
					    columnClasses="tableListColumn,tableListColumnRight"
					    style="border-collapse: collapse">
						<h:column >
							<h:outputLink value="javascript:linkRetrieve('#{menuBean.infoMap.LancamentoMovimento.type.name}', '#{lancamentoMovimento.id}')"  rendered="#{menuBean.crudMap.LancamentoMovimento.canRetrieve}">
								<h:outputLabel value="#{lancamentoMovimento.lancamentoMovimentoCategoria.nome} em " />
								<h:outputLabel value="#{lancamentoMovimento.data.time}" />
								<h:outputLabel value=" na conta #{lancamentoMovimento.conta}"/>
							</h:outputLink>
						</h:column>
						<h:column >
							<h:outputLabel value="#{lancamentoMovimento.valor}" rendered="#{lancamentoMovimento.valor>=0}" >
								<f:convertNumber type="currency"/>
							</h:outputLabel>
							<h:outputLabel value="#{lancamentoMovimento.valor}" rendered="#{lancamentoMovimento.valor<0}" style="color:red" >
								<f:convertNumber type="currency"/>
							</h:outputLabel>
						</h:column>
					</h:dataTable>
				</h:column>
			</h:dataTable>
		</h:panelGrid>
	</h:form>
