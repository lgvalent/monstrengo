package br.com.orionsoft.metadata;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.com.orionsoft.Activator;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.CrudOperationType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.EntityType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.ObjectFactory;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertiesGroup;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyBooleanType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyCalendarType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyEntityType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyNumericType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyStringType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyType;

/**
 * <p>Interpreta ICompilationUnit e CompilationUnit (API de plugin do Eclipse)
 * que correspondem ao arquivo .java selecionado na IDE. A partir destes 
 * objetos, monta a estrutura de propriedades de uma entidade para o
 * framework da Orion. 
 * 
 * @author andre
 * 
 */
public class EntityParser {
		
	private ObjectFactory factory;
	private ICompilationUnit iCompilationUnit;
	private CompilationUnit compilationUnit;
	private Map<String, String> imports;
	private EntityType entityType;
	
	public EntityParser(ICompilationUnit iCompilationUnit, CompilationUnit compilationUnit) {
		factory = new ObjectFactory();
		this.iCompilationUnit = iCompilationUnit;
		this.compilationUnit = compilationUnit;
	}
	
	public void run() {
		buildImports();
		buildEntity();
		
		/*
		 * Adiciona um Visitor para navegar em todos os m�todos e superClasses
		 * do objeto CompilationUnit.
		 * Fontes: 
		 * http://stackoverflow.com/questions/3938528/getting-field-type-in-a-method-in-eclipse
		 * http://help.eclipse.org/indigo/index.jsp?topic=/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/dom/VariableDeclarationFragment.html 
		 */
		compilationUnit.accept(new ASTVisitor() {
			@Override
			public boolean visit(TypeDeclaration node) {
				ITypeBinding type = node.getSuperclassType()!=null?node.getSuperclassType().resolveBinding():null;
				while(type != null && !type.getName().equals(Object.class.getSimpleName())){
					for(IMethodBinding method: type.getDeclaredMethods()){
						this.checkAndBuildMethod(method);
					}
					type = type.getSuperclass();
				}
				return true;
			}
			
			@Override
			public boolean visit(MethodDeclaration node) {
				IMethodBinding m = node.resolveBinding();
				this.checkAndBuildMethod(m);
				return true;
			}

			private void checkAndBuildMethod(IMethodBinding method){
				if(method.getName().startsWith("get")||method.getName().startsWith("is")){
					String propertyName = method.getName().startsWith("get")?method.getName().replaceFirst("get", ""):method.getName().replaceFirst("is", "");
					propertyName = propertyName.toLowerCase().charAt(0) + propertyName.substring(1);
					buildProperty(propertyName, method.getReturnType().getName());
				}
			}
			
		});
	}
	
	/**
	 * Monta um mapa de imports para armazenar o nome da classe
	 * e seu respectivo pacote.
	 */
	private void buildImports(){
		imports = new HashMap<String, String>();
		
		try{
			for (IImportDeclaration d : iCompilationUnit.getImports()){
				String packageFull = d.getElementName();
				int lastIndexOfDot = packageFull.lastIndexOf(".");
				String className = packageFull.substring(lastIndexOfDot+1, packageFull.length());
				
				imports.put(className, packageFull);
			}
			
		}catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error reading imports from compilation unit.", e);
			Activator.getDefault().getLog().log(status);
		}
	}
	
	/**
	 * Monta a estrutura de propriedades da entidade.
	 */
	private void buildEntity(){
		entityType = factory.createEntityType();
		String className = iCompilationUnit.getElementName().split("\\.")[0];
		entityType.setClassName((compilationUnit.getPackage()!=null?compilationUnit.getPackage().getName():"") + "." + className);
		entityType.setName(className);
		entityType.setLabel(className);
		
		PropertiesGroup group = factory.createPropertiesGroup();
		group.setName("Geral");
		group.setLabel("Geral");
		group.setHint("");
		group.setDescription("");
		entityType.getGroup().add(group);
		
		entityType.getCrudOperations().add(CrudOperationType.CREATE);
		entityType.getCrudOperations().add(CrudOperationType.UPDATE);
		entityType.getCrudOperations().add(CrudOperationType.RETRIEVE);
		entityType.getCrudOperations().add(CrudOperationType.DELETE);
	}
	/**
	 * Baseado no nome completo do tipo e no nome da propriedade
	 * constr�i ma propriedade.
	 * 
	 * @param <T>
	 * @param node
	 */
	private <T extends PropertyType> void buildProperty(String name, String typeSimpleName){
		
		PropertyType propertyType = null;
		
		//para cada nó, verificar se é String, Numeric, Calendar ou Entity
		if (TypeUtil.STRING_TYPE.equals(typeSimpleName)){
			propertyType = getPropertyStringType();
			
		}else if (TypeUtil.NUMERIC_TYPES.contains(typeSimpleName)){
			propertyType = getPropertyNumericType();
			
		}else if (TypeUtil.DATE_TYPES.contains(typeSimpleName)){
			propertyType = getPropertyCalendarType();
			
		}else if (TypeUtil.BOOLEAN_TYPES.contains(typeSimpleName)){
			propertyType = getPropertyBooleanType();
			
		}else{
			//considera como uma entidade
			propertyType = getPropertyEntityType();
		}
		
		setDefaultValues(propertyType, name);
		entityType.getGroup().get(0).getProperty().add(propertyType);
		entityType.getPropertiesInQueryGrid().add(propertyType);
	}
	
	private PropertyStringType getPropertyStringType(){
		PropertyStringType result = factory.createPropertyStringType();
		
		return result;
	}
	
	private PropertyNumericType getPropertyNumericType(){
		PropertyNumericType result = factory.createPropertyNumericType();
		
		return result;
	}
	
	private PropertyBooleanType getPropertyBooleanType(){
		PropertyBooleanType result = factory.createPropertyBooleanType();
		
		return result;
	}

	private PropertyCalendarType getPropertyCalendarType(){
		PropertyCalendarType result = factory.createPropertyCalendarType();
		
		return result;
	}
	
	private PropertyEntityType getPropertyEntityType(){
		PropertyEntityType result = factory.createPropertyEntityType();
		
		return result;
	}

	private void setDefaultValues(PropertyType propertyType, String name){
		propertyType.setName(name);
		propertyType.setLabel(name.replaceFirst(name.charAt(0)+"", name.toUpperCase().charAt(0)+""));
	}
	
	public EntityType getEntityType() {
		return entityType;
	}
	
}
