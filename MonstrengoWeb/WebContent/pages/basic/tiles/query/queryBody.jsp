<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
				<%-- Armazena os parãmetros de ordenação na visão para não perde-los e
				     submetelos a cada nova requisição >
				<h:inputHidden value="#{queryBean.currentProcess.orderParam.property}" />
				<h:inputHidden value="#{queryBean.currentProcess.orderParam.direction}" /--%>
				<f:verbatim>
					<div style="overflow-x: scroll;">
				</f:verbatim>
				<h:dataTable value="#{queryBean.array}" 
				             var='item'
				             rendered="#{queryBean.currentProcess.userReport.pageParam.itemsCount>0}"
				             headerClass="tableListHeader"
				             styleClass="tableList"
				             rowClasses="tableListRowEven,tableListRowOdd"
				             columnClasses="tableListColumn"
				             style="border-collapse: collapse; white-space: nowrap">

					<h:column rendered="#{queryBean.propertiesSize > 0}" >
						<%-- f:facet name="header">
	    	            	<h:outputText value="Comandos:"/>
    	                </f:facet %--%>
    	                <%-- Atualiza a entidade corrente para ser utilizada nas rotinas posteriores
    	                     que necessitam saber qual é a entidade corrente da tabela.
    	                     Força a execução do método next(). Utiliza title para nada ser exibido 
    	                     na interface. utiliza ="a #{}" para forçar o JSF a interpretar tudo como
    	                     uma String, senão ele não converte Long -> String --%>
   	                     <h:outputLabel value="" title="a #{queryBean.nextCurrentEntity.id}"/>
    	                
						<h:outputLink value="javascript:linkRetrieve('#{item.object.class.name}', '#{item.id}')" rendered="#{queryBean.canRetrieve}">
							<h:graphicImage value="../basic/img/retrieve.png" title="Visualiza os detalhes do registro" style="border:0"/>
						</h:outputLink>
						<h:graphicImage value="../basic/img/retrieve_d.png" title="Você não possui direitos para visualizar os dados do registro" style="border:0" rendered="#{!queryBean.canRetrieve}"/>

						<%-- h:outputLink value="javascript:linkUpdate('#{item.info.type.name}', '#{item.id}')" rendered="#{queryBean.canUpdate}">
							<h:graphicImage value="../basic/img/update.png" title="Altera os dados do registro" style="border:0"/>
						</h:outputLink>
						<h:graphicImage value="../basic/img/update_d.png" title="Você não possui direitos para alterar os dados do registro" style="border:0" rendered="#{!queryBean.canUpdate}"/>

						<h:outputLink value="javascript:linkDelete('#{item.info.type.name}', '#{item.id}')" rendered="#{queryBean.canDelete}">
							<h:graphicImage value="../basic/img/delete.png" title="Apaga o registro" style="border:0"/>
						</h:outputLink>
						<h:graphicImage value="../basic/img/delete_d.png" title="Você não possui direitos para apagar o registro" style="border:0" rendered="#{!queryBean.canDelete}"/ --%>

						<h:outputLink value="javascript: closeSelectOne('#{queryBean.currentParams.selectOneDest}','#{queryBean.selectPropertyValue}',window)" rendered="#{queryBean.selectOneActive}">
							<h:graphicImage style="border: none;" value="../basic/img/query_select_one.png" alt="Clique para selecionar este registro e voltar para a tela anterior"/>
						</h:outputLink>

						<%--h:selectBooleanCheckbox value="#{item.selected}"/--%>
					</h:column>

					<h:column rendered="#{queryBean.propertiesSize > 0}" >
						<f:facet name="header">
							<h:panelGroup>
		    	            	<h:outputText value="#{queryBean.properties[0].label} " title="#{queryBean.properties[0].hint}"/>
								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[0].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc) && !(queryBean.properties[0].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_asc.gif" alt="Ordenação crescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[0].name}" />
									<f:param name="orderDirection" value="asc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_asc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[0].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc}"/>

								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[0].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc) && !(queryBean.properties[0].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_desc.gif" alt="Ordenação Decrescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[0].name}"/>
									<f:param name="orderDirection" value="desc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_desc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[0].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc}"/>
							</h:panelGroup>
    	                </f:facet>
						<h:outputText value="#{item.properties[queryBean.properties[0].index].value.asString}" rendered="#{(not queryBean.properties[0].boolean) and queryBean.properties[0].visible}" />
						<h:selectBooleanCheckbox disabled="true" value="#{item.properties[queryBean.properties[0].index].value.asBoolean}" rendered="#{queryBean.properties[0].boolean and queryBean.properties[0].visible}" />
					</h:column>

					<h:column rendered="#{queryBean.propertiesSize > 1}" >
						<f:facet name="header">
							<h:panelGroup>
		    	            	<h:outputText value="#{queryBean.properties[1].label} "  title="#{queryBean.properties[1].hint}"/>
								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[1].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc) && !(queryBean.properties[1].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_asc.gif" alt="Ordenação crescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[1].name}" />
									<f:param name="orderDirection" value="asc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_asc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[1].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc}"/>

								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[1].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc) && !(queryBean.properties[1].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_desc.gif" alt="Ordenação Decrescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[1].name}"/>
									<f:param name="orderDirection" value="desc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_desc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[1].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc}"/>
							</h:panelGroup>
    	                </f:facet>
						<h:outputText value="#{item.properties[queryBean.properties[1].index].value.asString}" rendered="#{(not queryBean.properties[1].boolean) and queryBean.properties[1].visible}" />
						<h:selectBooleanCheckbox disabled="true" value="#{item.properties[queryBean.properties[1].index].value.asBoolean}" rendered="#{queryBean.properties[1].boolean and queryBean.properties[1].visible}" />
					</h:column>
					
					<h:column rendered="#{queryBean.propertiesSize > 2}" >
						<f:facet name="header">
							<h:panelGroup>
		    	            	<h:outputText value="#{queryBean.properties[2].label} "  title="#{queryBean.properties[2].hint}"/>
								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[2].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc) && !(queryBean.properties[2].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_asc.gif" alt="Ordenação crescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[2].name}" />
									<f:param name="orderDirection" value="asc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_asc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[2].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc}"/>

								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[2].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc) && !(queryBean.properties[2].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_desc.gif" alt="Ordenação Decrescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[2].name}"/>
									<f:param name="orderDirection" value="desc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_desc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[2].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc}"/>
							</h:panelGroup>
    	                </f:facet>
						<h:outputText value="#{item.properties[queryBean.properties[2].index].value.asString}" rendered="#{(not queryBean.properties[2].boolean) and queryBean.properties[2].visible}" />
						<h:selectBooleanCheckbox disabled="true" value="#{item.properties[queryBean.properties[2].index].value.asBoolean}" rendered="#{queryBean.properties[2].boolean and queryBean.properties[2].visible}" />
					</h:column>

					<h:column rendered="#{queryBean.propertiesSize > 3}" >
						<f:facet name="header">
							<h:panelGroup>
		    	            	<h:outputText value="#{queryBean.properties[3].label} "  title="#{queryBean.properties[3].hint}"/>
								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[3].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc) && !(queryBean.properties[3].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_asc.gif" alt="Ordenação crescente"/>
									<%-- Parâmetros da view --%>
									<f:param name="entityType" value="#{queryBean.entityParam.typeName}" />
									<f:param name="orderProperty" value="#{queryBean.properties[3].name}" />
									<f:param name="orderDirection" value="asc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_asc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[3].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc}"/>

								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[3].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc) && !(queryBean.properties[3].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_desc.gif" alt="Ordenação Decrescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[3].name}"/>
									<f:param name="orderDirection" value="desc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_desc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[3].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc}"/>
							</h:panelGroup>
    	                </f:facet>
						<h:outputText value="#{item.properties[queryBean.properties[3].index].value.asString}" rendered="#{(not queryBean.properties[3].boolean) and queryBean.properties[3].visible}" />
						<h:selectBooleanCheckbox disabled="true" value="#{item.properties[queryBean.properties[3].index].value.asBoolean}" rendered="#{queryBean.properties[3].boolean and queryBean.properties[3].visible}" />
					</h:column>

					<h:column rendered="#{queryBean.propertiesSize > 4}" >
						<f:facet name="header">
							<h:panelGroup>
		    	            	<h:outputText value="#{queryBean.properties[4].label} "  title="#{queryBean.properties[4].hint}"/>
								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[4].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc) && !(queryBean.properties[4].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_asc.gif" alt="Ordenação crescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[4].name}" />
									<f:param name="orderDirection" value="asc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_asc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[4].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc}"/>

								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[4].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc) && !(queryBean.properties[4].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_desc.gif" alt="Ordenação Decrescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[4].name}"/>
									<f:param name="orderDirection" value="desc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_desc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[4].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc}"/>
							</h:panelGroup>
    	                </f:facet>
						<h:outputText value="#{item.properties[queryBean.properties[4].index].value.asString}" rendered="#{(not queryBean.properties[4].boolean) and queryBean.properties[4].visible}" />
						<h:selectBooleanCheckbox disabled="true" value="#{item.properties[queryBean.properties[4].index].value.asBoolean}" rendered="#{queryBean.properties[4].boolean and queryBean.properties[4].visible}" />
					</h:column>

					<h:column rendered="#{queryBean.propertiesSize > 5}" >
						<f:facet name="header">
							<h:panelGroup>
		    	            	<h:outputText value="#{queryBean.properties[5].label} "  title="#{queryBean.properties[5].hint}"/>
								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[5].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc) && !(queryBean.properties[5].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_asc.gif" alt="Ordenação crescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[5].name}" />
									<f:param name="orderDirection" value="asc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_asc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[5].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc}"/>

								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[5].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc) && !(queryBean.properties[5].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_desc.gif" alt="Ordenação Decrescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[5].name}"/>
									<f:param name="orderDirection" value="desc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_desc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[5].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc}"/>
							</h:panelGroup>
    	                </f:facet>
						<h:outputText value="#{item.properties[queryBean.properties[5].index].value.asString}" rendered="#{(not queryBean.properties[5].boolean) and queryBean.properties[5].visible}" />
						<h:selectBooleanCheckbox disabled="true" value="#{item.properties[queryBean.properties[5].index].value.asBoolean}" rendered="#{queryBean.properties[5].boolean and queryBean.properties[5].visible}" />
					</h:column>

					<h:column rendered="#{queryBean.propertiesSize > 6}" >
						<f:facet name="header">
							<h:panelGroup>
		    	            	<h:outputText value="#{queryBean.properties[6].label} "  title="#{queryBean.properties[6].hint}"/>
								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[6].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc) && !(queryBean.properties[6].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_asc.gif" alt="Ordenação crescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[6].name}" />
									<f:param name="orderDirection" value="asc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_asc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[6].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc}"/>

								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[6].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc) && !(queryBean.properties[6].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_desc.gif" alt="Ordenação Decrescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[6].name}"/>
									<f:param name="orderDirection" value="desc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_desc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[6].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc}"/>
							</h:panelGroup>
    	                </f:facet>
						<h:outputText value="#{item.properties[queryBean.properties[6].index].value.asString}" rendered="#{(not queryBean.properties[6].boolean) and queryBean.properties[6].visible}" />
						<h:selectBooleanCheckbox disabled="true" value="#{item.properties[queryBean.properties[6].index].value.asBoolean}" rendered="#{queryBean.properties[6].boolean and queryBean.properties[6].visible}" />
					</h:column>

					<h:column rendered="#{queryBean.propertiesSize > 7}" >
						<f:facet name="header">
							<h:panelGroup>
		    	            	<h:outputText value="#{queryBean.properties[7].label} "  title="#{queryBean.properties[7].hint}"/>
								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[7].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc) && !(queryBean.properties[7].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_asc.gif" alt="Ordenação crescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[7].name}" />
									<f:param name="orderDirection" value="asc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_asc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[7].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc}"/>

								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[7].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc) && !(queryBean.properties[7].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_desc.gif" alt="Ordenação Decrescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[7].name}"/>
									<f:param name="orderDirection" value="desc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_desc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[7].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc}"/>
							</h:panelGroup>
    	                </f:facet>
						<h:outputText value="#{item.properties[queryBean.properties[7].index].value.asString}" rendered="#{(not queryBean.properties[7].boolean) and queryBean.properties[7].visible}" />
						<h:selectBooleanCheckbox disabled="true" value="#{item.properties[queryBean.properties[7].index].value.asBoolean}" rendered="#{queryBean.properties[7].boolean and queryBean.properties[7].visible}" />
					</h:column>

					<h:column rendered="#{queryBean.propertiesSize > 8}" >
						<f:facet name="header">
							<h:panelGroup>
		    	            	<h:outputText value="#{queryBean.properties[8].label} "  title="#{queryBean.properties[8].hint}"/>
								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[8].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc) && !(queryBean.properties[8].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_asc.gif" alt="Ordenação crescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[8].name}" />
									<f:param name="orderDirection" value="asc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_asc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[8].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc}"/>

								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[8].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc) && !(queryBean.properties[8].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_desc.gif" alt="Ordenação Decrescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[8].name}"/>
									<f:param name="orderDirection" value="desc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_desc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[8].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc}"/>
							</h:panelGroup>
    	                </f:facet>
						<h:outputText value="#{item.properties[queryBean.properties[8].index].value.asString}" rendered="#{(not queryBean.properties[8].boolean) and queryBean.properties[8].visible}" />
						<h:selectBooleanCheckbox disabled="true" value="#{item.properties[queryBean.properties[8].index].value.asBoolean}" rendered="#{queryBean.properties[8].boolean and queryBean.properties[8].visible}" />
					</h:column>

					<h:column rendered="#{queryBean.propertiesSize > 9}" >
						<f:facet name="header">
							<h:panelGroup>
		    	            	<h:outputText value="#{queryBean.properties[9].label} "  title="#{queryBean.properties[9].hint}"/>
								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[9].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc) && !(queryBean.properties[9].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_asc.gif" alt="Ordenação crescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[9].name}" />
									<f:param name="orderDirection" value="asc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_asc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[9].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderAsc}"/>

								<h:commandLink action="#{queryBean.doOrder}" rendered="#{!(queryBean.properties[9].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc) && !(queryBean.properties[9].collection)}">
									<h:graphicImage style="border: none;" value="../basic/img/order_desc.gif" alt="Ordenação Decrescente"/>
									<f:param name="orderProperty" value="#{queryBean.properties[9].name}"/>
									<f:param name="orderDirection" value="desc"/>
								</h:commandLink>
								<h:graphicImage style="border: none;" value="../basic/img/order_desc_d.gif" alt="Ordenação crescente" rendered="#{queryBean.properties[9].name==queryBean.newOrderParam.propertyPath && queryBean.newOrderParam.orderDesc}"/>
							</h:panelGroup>
    	                </f:facet>
						<h:outputText value="#{item.properties[queryBean.properties[9].index].value.asString}" rendered="#{(not queryBean.properties[9].boolean) and queryBean.properties[9].visible}" />
						<h:selectBooleanCheckbox disabled="true" value="#{item.properties[queryBean.properties[9].index].value.asBoolean}" rendered="#{queryBean.properties[9].boolean and queryBean.properties[9].visible}" />
					</h:column>

					<h:column rendered="#{(queryBean.propertiesSize > 3) && (queryBean.canRetrieve)}" >
						<f:facet name="header" >
	    	            	<h:outputText value="..." title="Visualizar detalhes do registro" />
    	                </f:facet>

						<h:outputLink value="javascript:linkRetrieve('#{item.info.type.name}', '#{item.id}')">
	    	            	<h:outputText value="..." title="Visualizar detalhes do registro" />
						</h:outputLink>
					</h:column>

				</h:dataTable>
				<f:verbatim>
					</div>
				</f:verbatim>

				<h:messages layout="table" />
				