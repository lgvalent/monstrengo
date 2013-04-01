<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>


<h:outputText value="Liquidado com SUCESSO" />
<h:dataTable
	value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.liquidadoComSucessoList}"
	border="1"
	var="doc">

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Id" />
		</f:facet>
		<h:outputText value="#{doc.id}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Data" />
		</f:facet>
		<h:outputText value="#{doc.dataDocumento.time}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Vencimento" />
		</f:facet>
		<h:outputText value="#{doc.dataVencimento.time}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Ocorrência" />
		</f:facet>
		<h:outputText value="#{doc.dataOcorrencia.time}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Nosso número" />
		</f:facet>
		<h:outputText value="#{doc.numeroDocumento}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Pessoa" />
		</f:facet>
		<h:outputText value="#{doc.pessoa}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Valor" />
		</f:facet>
		<h:outputText value="#{doc.valorDocumento}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Valor pago" />
		</f:facet>
		<h:outputText value="#{doc.valorPago}" />
			<f:facet name="footer">
				<h:outputLabel value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.liquidadoComSucessoTotal}" />
			</f:facet>
	</h:column>
</h:dataTable>

<h:outputText value="Nosso número de documento duplicado" />
<h:dataTable
	value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.numeroDocumentoDuplicadoList}"
	border="1"
	var="doc">
	
		<h:column>
			<f:facet name="header">
				<h:outputLabel value="Id" />
			</f:facet>
			<h:outputText value="#{doc.id}" />
		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputLabel value="Data" />
			</f:facet>
			<h:outputText value="#{doc.dataDocumento.time}" />
		</h:column>
		<h:column>

			<f:facet name="header">
				<h:outputLabel value="Vencimento" />
			</f:facet>
			<h:outputText value="#{doc.dataVencimento.time}" />
		</h:column>
		<h:column>
			<f:facet name="header">
				<h:outputLabel value="Ocorrência" />
			</f:facet>
			<h:outputText value="#{doc.dataOcorrencia.time}" />
		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputLabel value="Nosso número" />
			</f:facet>
			<h:outputText value="#{doc.numeroDocumento}" />
		</h:column>
		<h:column>

			<f:facet name="header">
				<h:outputLabel value="Pessoa" />
			</f:facet>
			<h:outputText value="#{doc.pessoa}" />
		</h:column>
		<h:column>
			<f:facet name="header">
				<h:outputLabel value="Valor" />
			</f:facet>
			<h:outputText value="#{doc.valorDocumento}" />
		</h:column>

		<h:column>
			<f:facet name="header">
				<h:outputLabel value="Valor pago" />
			</f:facet>
			<h:outputText value="#{doc.valorPago}" />
			<f:facet name="footer">
				<h:outputLabel value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.numeroDocumentoDuplicadoTotal}" />
			</f:facet>
		</h:column>

</h:dataTable>

<h:outputText value="Nosso número de documento não encontrado" />
<h:dataTable
	value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.numeroDocumentoNaoEncontradoList}"
	border="1"
	var="doc">

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Id" />
		</f:facet>
		<h:outputText value="#{doc.id}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Data" />
		</f:facet>
		<h:outputText value="#{doc.dataDocumento.time}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Vencimento" />
		</f:facet>
		<h:outputText value="#{doc.dataVencimento.time}" />
	</h:column>
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Ocorrência" />
		</f:facet>
		<h:outputText value="#{doc.dataOcorrencia.time}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Nosso número" />
		</f:facet>
		<h:outputText value="#{doc.numeroDocumento}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Pessoa" />
		</f:facet>
		<h:outputText value="#{doc.pessoa}" />
	</h:column>
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Valor" />
		</f:facet>
		<h:outputText value="#{doc.valorDocumento}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Valor pago" />
		</f:facet>
		<h:outputText value="#{doc.valorPago}" />
			<f:facet name="footer">
				<h:outputLabel value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.numeroDocumentoNaoEncontradoTotal}" />
			</f:facet>
	</h:column>
</h:dataTable>
<h:outputText value="Os Títulos não encontradas podem ter sido gerados por outros sistemas de cobrança. Verifique cada Nosso Número exibido acima, e busque identificar onde ele pode ter sido emitido." />

<h:outputText value="<br>Documento já liquidado" escape="false"/>
<h:dataTable
	value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.documentoJaLiquidadoList}"
	border="1"
	var="doc">
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Id" />
		</f:facet>
		<h:outputText value="#{doc.id}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Data" />
		</f:facet>
		<h:outputText value="#{doc.dataDocumento.time}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Vencimento" />
		</f:facet>
		<h:outputText value="#{doc.dataVencimento.time}" />
	</h:column>
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Ocorrência" />
		</f:facet>
		<h:outputText value="#{doc.dataOcorrencia.time}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Nosso número" />
		</f:facet>
		<h:outputText value="#{doc.numeroDocumento}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Pessoa" />
		</f:facet>
		<h:outputText value="#{doc.pessoa}" />
	</h:column>
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Valor" />
		</f:facet>
		<h:outputText value="#{doc.valorDocumento}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Valor pago" />
		</f:facet>
		<h:outputText value="#{doc.valorPago}" />
			<f:facet name="footer">
				<h:outputLabel value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.documentoJaLiquidadoTotal}" />
			</f:facet>
	</h:column>
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Mensagem" />
		</f:facet>
		<h:outputText value="#{doc.mensagem}" escape="false"/>
	</h:column>
</h:dataTable>

<h:outputText value="Ocorrência inválida" />
<h:dataTable
	value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.ocorrenciaInvalidaList}"
	border="1"
	var="doc">
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Id" />
		</f:facet>
		<h:outputText value="#{doc.id}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Data" />
		</f:facet>
		<h:outputText value="#{doc.dataDocumento.time}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Vencimento" />
		</f:facet>
		<h:outputText value="#{doc.dataVencimento.time}" />
	</h:column>
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Ocorrência" />
		</f:facet>
		<h:outputText value="#{doc.dataOcorrencia.time}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Nosso número" />
		</f:facet>
		<h:outputText value="#{doc.numeroDocumento}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Pessoa" />
		</f:facet>
		<h:outputText value="#{doc.pessoa}" />
	</h:column>
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Valor" />
		</f:facet>
		<h:outputText value="#{doc.valorDocumento}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Valor pago" />
		</f:facet>
		<h:outputText value="#{doc.valorPago}" />
		<f:facet name="footer">
				<h:outputLabel value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.ocorrenciaInvalidaTotal}" />
		</f:facet>
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Mensagem" />
		</f:facet>
		<h:outputText value="#{doc.mensagem}" escape="false"/>
	</h:column>
</h:dataTable>

<h:outputText value="Erro atualizando documento" />
<h:dataTable
	value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.erroAtualizandoDocumentoList}"
	border="1"
	var="doc">
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Id" />
		</f:facet>
		<h:outputText value="#{doc.id}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Data" />
		</f:facet>
		<h:outputText value="#{doc.dataDocumento.time}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Vencimento" />
		</f:facet>
		<h:outputText value="#{doc.dataVencimento.time}" />
	</h:column>
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Ocorrência" />
		</f:facet>
		<h:outputText value="#{doc.dataOcorrencia.time}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Nosso número" />
		</f:facet>
		<h:outputText value="#{doc.numeroDocumento}" />
	</h:column>
	<h:column>

		<f:facet name="header">
			<h:outputLabel value="Pessoa" />
		</f:facet>
		<h:outputText value="#{doc.pessoa}" />
	</h:column>
	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Valor" />
		</f:facet>
		<h:outputText value="#{doc.valorDocumento}" />
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Valor pago" />
		</f:facet>
		<h:outputText value="#{doc.valorPago}" />
		<f:facet name="footer">
				<h:outputLabel value="#{receberDocumentoCobrancaRetornoBean.process.retornoSumario.erroAtualizandoDocumentoTotal}" />
		</f:facet>
	</h:column>

	<h:column>
		<f:facet name="header">
			<h:outputLabel value="Mensagem" />
		</f:facet>
		<h:outputText value="#{doc.mensagem}" escape="false"/>
	</h:column>
</h:dataTable>

<h:form>
	<h:commandButton value="Receber outro arquivo" action="#{receberDocumentoCobrancaRetornoBean.actionStart}" />
</h:form>

