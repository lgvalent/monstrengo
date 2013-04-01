<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>

<%-- Esta página (imprimir1.jsp)obtem os parametros necessarios para o processo imprimirGrcsProcess --%>

<h:panelGrid columns="1" border="0">
    <h:outputText value="Processo de impressão de documentos de pagamento" styleClass="title"/>
</h:panelGrid>

<h:form id="form" >

	   	<h:panelGrid columns="3" cellpadding="3">
	  	
		  <h:panelGrid columns="5">
      			<h:outputText value="CPF"/>
	    		<%-- link para CPF --%>
		    	<h:outputLink value="javascript:openSelectOneProp('br.com.orionsoft.basic.entities.pessoa.Fisica', document.getElementById('form:cpfCnpj').value, 'documento', document.getElementById('form:cpfCnpj'))" >
					<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0"/>
			    </h:outputLink>

      			<h:outputText value="/CNPJ" />
	    		<%-- link para CNPJ --%>
		    	<h:outputLink value="javascript:openSelectOneProp('br.com.orionsoft.basic.entities.pessoa.Juridica', document.getElementById('form:cpfCnpj').value, 'documento', document.getElementById('form:cpfCnpj'))" >
					<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0"/>
			    </h:outputLink>
      			<h:outputText value=":" />
      	  </h:panelGrid>
	  	
      	  <h:inputText value="#{imprimirDocumentosPagamentoBean.currentProcess.cpfCnpj}" id="cpfCnpj" size="14" maxlength="14" onkeypress="return keyPressInt(this, event)"/>
	      <h:message  styleClass="errorMessage" for="cpfCnpj" />

     	  <h:outputText value="Data do documento:" title="Informe a data de emissão dos documentos a serem selecionados"/>
	  	  <h:panelGrid columns="5">
	 		<h:inputText id="dataDocumentoDe" required="true" value="#{imprimirDocumentosPagamentoBean.currentProcess.dataDocumentoDe.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="pt_BR" pattern="dd/MM/yyyy"/>
			</h:inputText>
		    <h:message  styleClass="errorMessage" for="dataDocumentoDe"/>
			<h:outputText value=" ate "/>
	 		<h:inputText id="dataDocumentoAte" required="true" value="#{imprimirDocumentosPagamentoBean.currentProcess.dataDocumentoAte.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="pt_BR" pattern="dd/MM/yyyy"/>
			</h:inputText>
			<h:message  styleClass="errorMessage" for="dataDocumentoAte"/>
		  </h:panelGrid>
		  <h:outputText value=""/>
		  
     	  <h:outputText value="Data de vencimento:" title="Informe a data de vencimento dos documentos a serem selecionados"/>
	  	  <h:panelGrid columns="5">
	 		<h:inputText id="dataVencimentoDe" required="true" value="#{imprimirDocumentosPagamentoBean.currentProcess.dataVencimentoDe.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="pt_BR" pattern="dd/MM/yyyy"/>
			</h:inputText>
		    <h:message  styleClass="errorMessage" for="dataVencimentoDe"/>
			<h:outputText value=" até "/>
	 		<h:inputText id="dataVencimentoAte" required="true" value="#{imprimirDocumentosPagamentoBean.currentProcess.dataVencimentoAte.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="pt_BR" pattern="dd/MM/yyyy"/>
			</h:inputText>
			<h:message  styleClass="errorMessage" for="dataVencimentoAte"/>
		  </h:panelGrid>
		  <h:outputText value=""/>

      	  <h:outputText value="Escritório Contábil:" title="Informe o identificador de um Escritório Contábil."/>
	  	  <h:panelGrid columns="2">
			<h:graphicImage value="../basic/img/query_open_select.png" title="Dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
			<h:inputText value="#{imprimirDocumentosPagamentoBean.currentProcess.escritorioContabilId}" id="escritorioContabil" size="5" maxlength="5" required="true" title="Dê um clique na caixa de texto para abrir a pesquisa" styleClass="queryInputSelectOne" onblur="javascript: if(this.value=='')this.value='-1';" onclick="javascript:openSelectOneId('br.com.orionsoft.basic.entities.pessoa.EscritorioContabil',this.value,this)"/>
	  	  </h:panelGrid>
       	  <h:message  styleClass="errorMessage" for="escritorioContabil" />

      	  <h:outputText value="Município:" title="Informe o identificador de um Município."/>
	  	  <h:panelGrid columns="2">
			<h:graphicImage value="../basic/img/query_open_select.png" title="Dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
      		<h:inputText value="#{imprimirDocumentosPagamentoBean.currentProcess.municipioId}" id="municipio" size="5" maxlength="5" rendered="true" title="Dê um clique na caixa de texto para abrir a pesquisa" styleClass="queryInputSelectOne" onblur="javascript: if(this.value=='')this.value='-1';" onclick="javascript:openSelectOneId('br.com.orionsoft.basic.entities.endereco.Municipio',this.value,this)"/>
	  	  </h:panelGrid>
	      <h:message   styleClass="errorMessage" for="municipio" />
	    	
    	  <h:outputLabel value=""/>
     	  <h:outputText value="Omitir os documentos do município acima" >
      		 <h:selectBooleanCheckbox value="#{imprimirDocumentosPagamentoBean.currentProcess.excluirMunicipio}" title="Marque esta opção para que os documentos de pessoas residentes no município informada não sejam listados"/>
    	  </h:outputText> 
    	  <h:outputLabel value=""/>

		<h:outputText value="Forma de Pagamento:" title="Escolha a forma de pagamento que gerou os documentos de pagamento" />
		<h:selectOneMenu id="fp"
			value="#{imprimirDocumentosPagamentoBean.currentProcess.documentoCobrancaCategoriaId}">
			<f:selectItems value="#{imprimirDocumentosPagamentoBean.currentProcess.documentoCobrancaCategoriaList}" />
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="fp" />

    	  <h:outputLabel value=""/>
     	  <h:outputText value="Imprimir os documentos com seus valores em branco" >
      		 <h:selectBooleanCheckbox value="#{imprimirDocumentosPagamentoBean.currentProcess.zerarValor}" title="Marque esta opção para que os documentos sejam impressos com valor em branco"/>
    	  </h:outputText> 
    	  <h:outputLabel value=""/>

	  	</h:panelGrid>

		<h:panelGrid columns="2" cellpadding="3" >
		  <h:commandButton value="Limpar" type="reset" title="Limpa os dados preenchidos"/>
		  <h:commandButton value="Visualizar" action="#{imprimirDocumentosPagamentoBean.doVisualizar}" onclick="this.value='Aguarde...'" title="Mostra a lista de documentos de pagamento selecionados de acordo com os filtros preenchidos"/>
		</h:panelGrid>

</h:form>
