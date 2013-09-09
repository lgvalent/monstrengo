<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%-- RECIBO DE RECEBIMENTO --%>
<h:panelGrid rendered="#{reciboBean.grupoRecebimento}">
	<h:panelGrid style="text-align:right;" width="100%">
		<h:outputLabel value="1a. Via"/>
	</h:panelGrid>
	<h:panelGrid columns="2" style="border: thin solid;" width="100%">
		<h:panelGrid style="text-align:center">
<%--  
			<h:outputText value="Operador: #{userSessionBean.userSession.user.object}" style="font-size:small"/>
 --%>
			<h:graphicImage value="../basic/img/header.gif" title="Recibo"
				style="border:0" />
				<h:outputLabel value="www.sivamar.com.br"/>
		</h:panelGrid>

		<h:panelGrid styleClass="reportHeader">
			<f:verbatim>
					Sivamar - Sind. dos Loj. do Com. e do Com. Varej. de Maringá<br>
					CNPJ: 77.266.146/0001-08      I.E.: Isento<br>
					Rua Néo Alves Martins, 2789 - sobreloja<br>
					CEP 87013-914 Maringá - PR - Fone/Fax: (44) 3026-4444<br>
			</f:verbatim>
		</h:panelGrid>
	</h:panelGrid>

	<h:panelGrid style="border: thin solid; text-align:center;" width="100%">
		<h:panelGrid style="text-align:right;background: #E9E9E9;" width="100%">
			<h:outputText value="Recibo: R$ #{reciboBean.valorGrupoAsString}" style="font-size: 20pt;"/>
		</h:panelGrid>

		<h:panelGrid style="font-size: large;">
			<h:outputText value="Recebemos de <u>#{reciboBean.grupo.contrato.pessoa.nome}</u>," escape="false"/>
			<h:outputText value="a quantia de <u>#{reciboBean.valorGrupoExtenso}</u>." escape="false"/>
			<h:outputText value="Referente a <u>#{reciboBean.grupo.descricao.nome}</u>." escape="false"/>
			<h:outputText value="________________________________________________________" escape="false"/>
<%--			<h:outputText 
				value="Conforme o documento <u>#{reciboBean.grupo.documento.formaPagamento.nome}
				</u> com o número <u>#{reciboBean.grupo.documento.numeroDocumento}
				</u>, com vencimento em <u>#{reciboBean.grupo.dataVencimentoAsString}</u>." 
				escape="false"/>
--%>				
		</h:panelGrid>
		
		<h:panelGrid styleClass="reportTitle" style="text-align:center" width="100%">
			<h:outputText />
			<h:outputText value="Maringá, ____ de __________________ de ________"/>
			<h:outputText />
			<h:outputText value="______________________________________"/>
			<h:outputText value="Sivamar"/>
		</h:panelGrid>
	</h:panelGrid>
		
	<f:verbatim>
		<br>
		<br>
		Corte aqui
		<hr>
		<br>
	</f:verbatim>
	
	<h:panelGrid style="text-align:right;" width="100%">
		<h:outputLabel value="2a. Via"/>
	</h:panelGrid>
	<h:panelGrid columns="2" style="border: thin solid;" width="100%">
		<h:panelGrid style="text-align:center">
<%--
			<h:outputText value="Operador: #{userSessionBean.userSession.user.object}" style="font-size:small"/>
 --%>
			<h:graphicImage value="../basic/img/header.gif" title="Recibo"
				style="border:0" />
				<h:outputLabel value="www.sivamar.com.br"/>
		</h:panelGrid>
	
		<h:panelGrid styleClass="reportHeader">
			<f:verbatim>
					Sivamar - Sind. dos Loj. do Com. e do Com. Varej. de Maringá<br>
					CNPJ: 77.266.146/0001-08      I.E.: Isento<br>
					Rua Néo Alves Martins, 2789 - sobreloja<br>
					CEP 87013-914 Maringá - PR - Fone/Fax: (44) 3026-4444<br>
			</f:verbatim>
		</h:panelGrid>
	</h:panelGrid>
	
	<h:panelGrid style="border: thin solid; text-align:center;" width="100%">
		<h:panelGrid style="text-align:right;background: #E9E9E9;" width="100%">
			<h:outputText value="Recibo: R$ #{reciboBean.valorGrupoAsString}" style="font-size: 20pt;"/>
		</h:panelGrid>
		
		<h:panelGrid style="font-size: large;">
			<h:outputText value="Recebemos de <u>#{reciboBean.grupo.contrato.pessoa.nome}</u>," escape="false"/>
			<h:outputText value="a quantia de <u>#{reciboBean.valorGrupoExtenso}</u>." escape="false"/>
			<h:outputText value="Referente a <u>#{reciboBean.grupo.descricao.nome}</u>." escape="false"/>
			<h:outputText value="________________________________________________________" escape="false"/>
<%--			<h:outputText 
				value="Conforme o documento <u>#{reciboBean.grupo.documento.formaPagamento.nome}
				</u> com o número <u>#{reciboBean.grupo.documento.numeroDocumento}
				</u>, com vencimento em <u>#{reciboBean.grupo.dataVencimentoAsString}</u>." 
				escape="false"/>
--%>				
		</h:panelGrid>
		
		<h:panelGrid styleClass="reportTitle" style="text-align:center" width="100%">
			<h:outputText />
			<h:outputText value="Maringá, ____ de __________________ de ________"/>
			<h:outputText />
			<h:outputText value="______________________________________"/>
			<h:outputText value="Sivamar"/>
		</h:panelGrid>
	</h:panelGrid>
</h:panelGrid>

<%-- RECIBO DE PAGAMENTO --%>
<h:panelGrid rendered="#{reciboBean.grupoPagamento}">
	<h:panelGrid style="text-align:right;" width="100%">
		<h:outputLabel value="1a. Via"/>
	</h:panelGrid>
	<h:panelGrid columns="2" style="border: thin solid;" width="100%">
		<h:panelGrid style="text-align:center">
<%--
			<h:outputText value="Operador: #{userSessionBean.userSession.user.object}" style="font-size:small"/>
--%>
			<h:graphicImage value="../basic/img/header.gif" title="Recibo"
				style="border:0" />
				<h:outputLabel value="www.sivamar.com.br"/>
		</h:panelGrid>

		<h:panelGrid styleClass="reportHeader">
			<f:verbatim>
					Sivamar - Sind. dos Loj. do Com. e do Com. Varej. de Maringá<br>
					CNPJ: 77.266.146/0001-08      I.E.: Isento<br>
					Rua Néo Alves Martins, 2789 - sobreloja<br>
					CEP 87013-914 Maringá - PR - Fone/Fax: (44) 3026-4444<br>
			</f:verbatim>
		</h:panelGrid>
	</h:panelGrid>

	<h:panelGrid style="border: thin solid; text-align:center;" width="100%">
		<h:panelGrid style="text-align:right;background: #E9E9E9;" width="100%">
			<h:outputText value="Recibo: R$ #{reciboBean.valorGrupoAsString}" style="font-size: 20pt;"/>
		</h:panelGrid>

		<h:panelGrid style="font-size: large;">
			<h:outputText value="Eu, <u>#{reciboBean.grupo.contrato.pessoa.nome}</u>," escape="false"/>
			<h:outputText value="recebi de <u>Sivamar - Sind. dos Loj. do Com. e do Com. Varej. de Maringá</u>" escape="false"/>
			<h:outputText value="a quantia de <u>#{reciboBean.valorGrupoExtenso}</u>." escape="false"/>
			<h:outputText value="Referente a <u>#{reciboBean.grupo.descricao.nome}</u>." escape="false"/>
			<h:outputText value="________________________________________________________" escape="false"/>
<%--			<h:outputText 
				value="Conforme o documento <u>#{reciboBean.grupo.documento.formaPagamento.nome}
				</u> com o número <u>#{reciboBean.grupo.documento.numeroDocumento}
				</u>, com vencimento em <u>#{reciboBean.grupo.dataVencimentoAsString}</u>." 
				escape="false" />
--%>				
		</h:panelGrid>
		
		<h:panelGrid styleClass="reportTitle" style="text-align:center" width="100%">
			<h:outputText />
			<h:outputText value="Maringá, ____ de __________________ de ________"/>
			<h:outputText />
			<h:outputText value="____________________________________________________"/>
			<h:outputText value="#{reciboBean.grupo.contrato.pessoa.nome}"/>
		</h:panelGrid>
	</h:panelGrid>
		
	<f:verbatim>
		<br>
		<br>
		Corte aqui
		<hr>
		<br>
	</f:verbatim>
	
	<h:panelGrid style="text-align:right;" width="100%">
		<h:outputLabel value="2a. Via"/>
	</h:panelGrid>
	<h:panelGrid columns="2" style="border: thin solid;" width="100%">
		<h:panelGrid style="text-align:center">
<%-- 
			<h:outputText value="Operador: #{userSessionBean.userSession.user.object}" style="font-size:small"/>
--%>
			<h:graphicImage value="../basic/img/header.gif" title="Recibo"
				style="border:0" />
				<h:outputLabel value="www.sivamar.com.br"/>
		</h:panelGrid>
	
		<h:panelGrid styleClass="reportHeader">
			<f:verbatim>
					Sivamar - Sind. dos Loj. do Com. e do Com. Varej. de Maringá<br>
					CNPJ: 77.266.146/0001-08      I.E.: Isento<br>
					Rua Néo Alves Martins, 2789 - sobreloja<br>
					CEP 87013-914 Maringá - PR - Fone/Fax: (44) 3026-4444<br>
			</f:verbatim>
		</h:panelGrid>
	</h:panelGrid>
	
	<h:panelGrid style="border: thin solid; text-align:center;" width="100%">
		<h:panelGrid style="text-align:right;background: #E9E9E9;" width="100%">
			<h:outputText value="Recibo: R$ #{reciboBean.valorGrupoAsString}" style="font-size: 20pt;"/>
		</h:panelGrid>
		
		<h:panelGrid style="font-size: large;">
			<h:outputText value="Eu, <u>#{reciboBean.grupo.contrato.pessoa.nome}</u>," escape="false"/>
			<h:outputText value="recebi de <u>Sivamar - Sind. dos Loj. do Com. e do Com. Varej. de Maringá</u>" escape="false"/>
			<h:outputText value="a quantia de <u>#{reciboBean.valorGrupoExtenso}</u>." escape="false"/>
			<h:outputText value="Referente a <u>#{reciboBean.grupo.descricao.nome}</u>." escape="false"/>
			<h:outputText value="________________________________________________________" escape="false"/>
<%--			<h:outputText 
				value="Conforme o documento <u>#{reciboBean.grupo.documento.formaPagamento.nome}
				</u> com o número <u>#{reciboBean.grupo.documento.numeroDocumento}
				</u>, com vencimento em <u>#{reciboBean.grupo.dataVencimentoAsString}</u>." 
				escape="false" />
--%>				
		</h:panelGrid>
		
		<h:panelGrid styleClass="reportTitle" style="text-align:center" width="100%">
			<h:outputText />
			<h:outputText value="Maringá, ____ de __________________ de ________"/>
			<h:outputText />
			<h:outputText value="____________________________________________________"/>
			<h:outputText value="#{reciboBean.grupo.contrato.pessoa.nome}"/>
		</h:panelGrid>
	</h:panelGrid>
</h:panelGrid>
