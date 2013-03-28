package br.com.orionsoft.metadata;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import br.com.orionsoft.Activator;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.EntityType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.ObjectFactory;

/**
 * Classe responsável por ler os arquivos .java e interpretar suas propriedades.
 * 
 * @author andre
 *
 */
public class Generator extends AbstractHandler {
	
	private static final String METADATA_EXTENSION = ".info.xml";
	
	private static final String PROPERTY_SCHEMA_LOCATION_VALUE = 
		"http://orionsoft.net.br/entityMetadata http://orionsoft.net.br/entityMetadata.xsd";
	private static final Boolean PROPERTY_FORMATTED_VALUE = Boolean.TRUE;
	

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		/*
		 * Lê os arquivos selecionados no Package Explorer
		 * Fontes:
		 * http://www.vogella.de/articles/EclipsePlugIn/article.html#contribute
		 */
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveMenuSelection(event);
		
		
		try{
			/*
			 * Os arquivos selecionados podem ser de mais de um projeto, desta
			 * forma, armazena os projetos num mapa para que possam ser
			 * atualizados (Refresh) após a criação dos arquivos de metadados
			 */
			Set<IProject> projects = new HashSet<IProject>();
			
			for (Object element : selection.toList()){
				if (element instanceof ICompilationUnit) {
					ICompilationUnit iCompilationUnit = (ICompilationUnit) element;
					String metadataFileName = getMetadataFileName(iCompilationUnit);
					if (metadataFileName != null){
						/*
						 * Obtém um CompilationUnit para percorrer as propriedades
						 * com um Visitor
						 * Fontes:
						 * http://www.guj.com.br/java/245139-duvida-com-classloader-ao-desenvolver-um-plugin-para-o-eclipse
						 * http://stackoverflow.com/questions/3938528/getting-field-type-in-a-method-in-eclipse
						 */
						CompilationUnit compilationUnit = getAst(iCompilationUnit);
						
						EntityParser parser = new EntityParser(iCompilationUnit, compilationUnit);
						parser.run();

						EntityType entityType = parser.getEntityType();
						
						// Grava os dados da entidade em um arquivo xml
						write(metadataFileName, entityType);
						
						//adicina o projeto no conjunto, para ser atualizado após a criação dos arquivos
						projects.add(iCompilationUnit.getCorrespondingResource()
								.getProject());
						
					} else {
						MessageDialog
								.openInformation(
										HandlerUtil.getActiveShell(event),
										"Erro",
										"Não foi possível gravar o arquivo "
												+ METADATA_EXTENSION
												+ ". Verifique se você possui permissão de escrita no projeto.");
					}
					
				} else {
					MessageDialog.openInformation(HandlerUtil.getActiveShell(event),
							"Atenção", "Selecione apenas arquivos Java");
					return null;
				}
			}
			
			/*
			 *  Atualiza os projetos vinculados aos arquivos
			 *  Fontes:
			 *  http://www.openarchitectureware.org/forum/viewtopic.php?showtopic=4958
			 */
			for (IProject project : projects){
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
			
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event),
					"Sucesso", "Arquivos de metadados criados com sucesso.");

		}catch (Exception e) {
			/*
			 * Logs, Fonte:
			 * http://www.ibm.com/developerworks/library/os-eclog/
			 */
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error executing plugin orion-metadata.", e);
			Activator.getDefault().getLog().log(status);
			
			throw new ExecutionException(
					"Error executing plugin orion-metadata.", e);
		}
		
		return null;
	}
	
	private String getMetadataFileName(ICompilationUnit iCompilationUnit){
		String result = null;
		
		try {
			result = iCompilationUnit.getCorrespondingResource().getLocation()
					.removeFileExtension().toString()
					+ METADATA_EXTENSION;
			
		
		} catch (JavaModelException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error reading compilation unit at getMetadataFileName.", e);
			Activator.getDefault().getLog().log(status);
			
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error reading compilation unit at getMetadataFileName.", e);
			Activator.getDefault().getLog().log(status);
		}
		
		return result;

	}
	
	public static CompilationUnit getAst(ICompilationUnit compUnit) {
		ASTParser astParser = ASTParser.newParser(AST.JLS3);
		astParser.setResolveBindings(true);
		astParser.setBindingsRecovery(true);
		astParser.setStatementsRecovery(true);
		astParser.setSource(compUnit);
		return (CompilationUnit) astParser.createAST(null);
	}

	private void write(String metadataFileName, EntityType entityType) {
		OutputStream output = null;
		
		try {
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
			Marshaller marshaller = context.createMarshaller();
			
			output = new FileOutputStream(metadataFileName);
			/*
			 * Informa que o arquivo gerado deve conter um schemaLocation e ser formatado.
			 * Fontes:
			 * http://download.oracle.com/docs/cd/E17802_01/webservices/webservices/docs/1.5/tutorial/doc/JAXBWorks2.html
			 */
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, PROPERTY_SCHEMA_LOCATION_VALUE);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, PROPERTY_FORMATTED_VALUE);
			marshaller.marshal(entityType, output);
			
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error writing metadata file.", e);
			Activator.getDefault().getLog().log(status);
			
		} finally {
			try {
				output.close();
				
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Error closing metadata file.", e);
				Activator.getDefault().getLog().log(status);
			}
		}

	}
}
