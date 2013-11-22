<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGrid>
  <f:verbatim>
	<table align="left" style="font-size: small">
	 <tr>
	   <td>
	     <a href="../financeiro/documentoCobrancaGerarRemessa1.xhtml" ><img src="../sindicato/img24/run.png" style="border:0"/>Arquivo de Remessa</a>
	   </td>
	 </tr>
	 <tr>
	   <td>
	     <a href="../financeiro/documentoCobrancaReceberRetorno1.xhtml" ><img src="../sindicato/img24/run.png" style="border:0"/>Arquivo de Retorno</a>
	   </td>
	 </tr>
	 <tr>
	   <td>
	     <a href="../financeiro/gerenciadorInserirLancamento1.xhtml" ><img src="../sindicato/img24/run.png" style="border:0"/>Inserir lançamento</a>
	   </td>
	 </tr>
	 <tr>
	   <td>
	     <a href="../financeiro/gerenciadorListarLancamento.xhtml" ><img src="../sindicato/img24/run.png" style="border:0"/>Listagem de lançamentos</a>
	   </td>
	 </tr>
	 <tr>
	   <td>
	     <a href="../financeiro/gerenciadorListarLancamentoMovimento.jsp" ><img src="../sindicato/img24/run.png" style="border:0"/>Movimentação da conta</a>
	   </td>
	 </tr>
	 <tr>
	   <td>
	     <a href="../financeiro/gerenciadorListarItemCusto.jsp" ><img src="../sindicato/img24/run.png" style="border:0"/>Movimentação por item de custo</a>
	   </td>
	 </tr>
	 <tr>
	   <td>
	     <a href="../financeiro/gerenciadorListarPosicaoContrato.jsp" ><img src="../sindicato/img24/run.png" style="border:0"/>Posição do cliente</a>
	   </td>
	 </tr>
	 <tr>
	   <td>
	     <a href="../financeiro/relatorioCadastro1.jsp" ><img src="../sindicato/img24/run.png" style="border:0"/>Relatório de cadastro</a>
	   </td>
	 </tr>
	 <tr>
	   <td>
	     <a href="../financeiro/gerenciadorRelatorioCobranca.jsp" ><img src="../sindicato/img24/run.png" style="border:0"/>Relatório de cobrança</a>
	   </td>
	 </tr>
	 <tr>
	   <td>
	     <a href="../financeiro/gerenciadorRelatorioRecebimento.jsp" ><img src="../sindicato/img24/run.png" style="border:0"/>Relatório de recebimento</a>
	   </td>
	 </tr>
	 <tr>
	   <td>
	     <a href="../financeiro/gerenciadorTransferir.jsp" ><img src="../sindicato/img24/run.png" style="border:0"/>Transferir entre contas</a>
	   </td>
	 </tr>
	</table>

  </f:verbatim>

  <f:verbatim>
    <hr>
  </f:verbatim>

    <h:panelGrid columns="7" style="font-size: small">
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.Agendamento.name}')" rendered="#{menuBean.crudMap.Agendamento.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.Agendamento.canCreate}" />
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.Agendamento.name}','')" rendered="#{menuBean.crudMap.Agendamento.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		 </h:outputLink>
 		 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.Agendamento.canRetrieve}" />
	     <h:outputText value="#{menuBean.infoMap.Agendamento.label}" />			 
    </h:panelGrid>
    
	<h:panelGrid columns="7" style="font-size: small">
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.ContratoFinanceiro.name}')" rendered="#{menuBean.crudMap.ContratoFinanceiro.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.ContratoFinanceiro.canCreate}" />
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.ContratoFinanceiro.name}','')" rendered="#{menuBean.crudMap.ContratoFinanceiro.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		 </h:outputLink>
 		 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.ContratoFinanceiro.canRetrieve}" />
	     <h:outputText value="#{menuBean.infoMap.ContratoFinanceiro.label}" />			 
    </h:panelGrid>
    
    <h:panelGrid columns="7" style="font-size: small" >
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.ContratoFinanceiroCategoria.name}')" rendered="#{menuBean.crudMap.ContratoFinanceiroCategoria.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.ContratoFinanceiroCategoria.canCreate}" />
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.ContratoFinanceiroCategoria.name}','')" rendered="#{menuBean.crudMap.ContratoFinanceiroCategoria.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		 </h:outputLink>
 		 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.ContratoFinanceiroCategoria.canRetrieve}" />
	     <h:outputText value="#{menuBean.infoMap.ContratoFinanceiroCategoria.label}" />			 
    </h:panelGrid>

    <%--h:panelGrid columns="7" >
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.Documento.name}')" rendered="#{menuBean.crudMap.Documento.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.Documento.canCreate}" />
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.Documento.name}','')" rendered="#{menuBean.crudMap.Documento.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		 </h:outputLink>
 		 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.Documento.canRetrieve}" />
	     <h:outputText value="#{menuBean.infoMap.Documento.label}" />			 
    </h:panelGrid--%>

    <h:panelGrid columns="7" style="font-size: small" >
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.CentroCusto.name}')" rendered="#{menuBean.crudMap.CentroCusto.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.CentroCusto.canCreate}" />
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.CentroCusto.name}','')" rendered="#{menuBean.crudMap.CentroCusto.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		 </h:outputLink>
 		 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.CentroCusto.canRetrieve}" />
	     <h:outputText value="#{menuBean.infoMap.CentroCusto.label}" />			 
    </h:panelGrid>
	
    <h:panelGrid columns="7" style="font-size: small" >
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.Conta.name}')" rendered="#{menuBean.crudMap.Conta.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.Conta.canCreate}" />
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.Conta.name}','')" rendered="#{menuBean.crudMap.Conta.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		 </h:outputLink>
 		 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.Conta.canRetrieve}" />
	     <h:outputText value="#{menuBean.infoMap.Conta.label}" />			 
    </h:panelGrid>
    
	<h:panelGrid columns="7" style="font-size: small" >
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.ItemCusto.name}')" rendered="#{menuBean.crudMap.ItemCusto.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.ItemCusto.canCreate}" />
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.ItemCusto.name}','')" rendered="#{menuBean.crudMap.ItemCusto.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		 </h:outputLink>
 		 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.ItemCusto.canRetrieve}" />
	     <h:outputText value="#{menuBean.infoMap.ItemCusto.label}" />			 
    </h:panelGrid>     
	
	<h:panelGrid columns="7" style="font-size: small" >
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.Lancamento.name}')" rendered="#{menuBean.crudMap.Lancamento.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.Lancamento.canCreate}" />
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.Lancamento.name}','')" rendered="#{menuBean.crudMap.Lancamento.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		 </h:outputLink>
 		 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.Lancamento.canRetrieve}" />
	     <h:outputText value="#{menuBean.infoMap.Lancamento.label}" />			 
    </h:panelGrid> 
     
    <h:panelGrid columns="7" style="font-size: small" >
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.LancamentoMovimento.name}')" rendered="#{menuBean.crudMap.LancamentoMovimento.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.LancamentoMovimento.canCreate}" />
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.LancamentoMovimento.name}','')" rendered="#{menuBean.crudMap.LancamentoMovimento.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		 </h:outputLink>
 		 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.LancamentoMovimento.canRetrieve}" />
	     <h:outputText value="#{menuBean.infoMap.LancamentoMovimento.label}" />			 
    </h:panelGrid>
	
</h:panelGrid>
	