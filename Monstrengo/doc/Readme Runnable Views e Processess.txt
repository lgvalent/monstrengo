ADAPTAR:==========================================================================================
- Excluir do cadastro de entidades as entidades que nao existem mais:
  - Grupo
  - Movimento

08103782'

Runnable:=========================================================================================
Antes de implementar um Runnable, leia este documento. Ele irá te ajudar a lembrar dos detalhes que
envolvem a implantação de um novo processo come esta característica.

Vamos precisar de três componentes para implementar a característica Runnable em um processo de negócio.
	1) Um processo que implemente a interface IRunnableEntityProcess;
	2) Um controlador runnable para o processo: IRunnableEntityProcessController (RunnableEntityProcessControllerBasic);
	3) Uma visão que implemente a interface IRunnableProcessView;

1) Processo Runnable
  Um processo é Runnable quando ele implementa a interface IRunnableEntityProcess e possui um controlador implementado.
  Ao definir um processo como Runnable lembre-se de definir os metadados do processo utilizando as anotações.
  Exemplo: @ProcessMetadataAnnotation(label="Redefinir a senha de um operador", hint="Permite redefinir a senha de um operador sem conhecer a sua atual senha", description="Permite redefinir a senha de um operador sem conhecer a sua atual senha. Útil para os administradores redefinirem a senha esquecida de um operador")
		   public class OverwritePasswordProcess extends ProcessBasic implements IRunnableEntityProcess{
		   ...
		   }
  Abaixo está um exemplo de implementação do método definido pela interface IRunnableEntityProcess
  
  	/*==============================================================================
	 * IRunnableEntityProcess	
	 *==============================================================================*/
	
	/** Implementação fictícia */
	public boolean runWithEntity(IEntity entity) {
        super.beforeRun();

        boolean result = false;
		
		/* Verifica se a entidade é compatível */
		if(entity.getInfo().getType() == ApplicationUser.class){
			try {
				/* Extraí os parâmetros da entidade que são úteis para o atual processo */
				this.login = entity.getProperty(ApplicationUser.LOGIN).getValue().getAsString();

				/* Preenche algumas opções padrões do processo caso seja chamado por runWithEntity() */
				this.dataInicial = '01/01/1900';
				this.dataFinal = '31/12/2008';

				/* Executa alguma ação para que o processo já exiba algum resultado
				 * na primeira exibição */
				result = runListar();
				
			} catch (BusinessException e) {
				this.getMessageList().addAll(e.getErrorList());
			}
			
		}else
		{
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		
		return result;
	}

  
  
2) Controlador Runnable
  Ao iniciar a execução de um processo de negócio, o sistema fornece uma instância individual para cada operador.
  Assim, haverá várias instâncias de um mesmo processo em execução no sistema.
  O ProcessManager é responsável por identificar o processo solicitado e instanciá-lo.
  Também é cargo do ProcessManager, verificar se um determinado processo é ou não Runnable.
  Isto é possível, porque o ProcessManager mantém o registro de todos os controladores de processo existentes.
  Cada processo Runnable deve ter um controlador implementado para ele.
  Este controlador é responsável por verificar se um processo pode ou não ser executado a partir de uma determinada entidade.
  Isto porque, uma entidade pode estar em um estado que impede a execução de um processo sobre ela.
  Exemplo: Um operador inativo não pode ter sua senha trocada. Isto já é indicado pelo controlador OverwritePasswordProcessController;  


3) Bean de visão que implementa a interface IRunnableProcessView
  - metodo 'public String getViewName()' que retorne o nome da visão que está registrado no arquivo facesConfig.xml.
    Exemplo: Para a classe ListarPosicaoContratoBean o método getViewName() retorna:
             listarPosicaoContratoBean
             Assim, no arquivo facesConfig.xml temos:
		<managed-bean>
			<managed-bean-name>listarPosicaoContratoBean</managed-bean-name>
		...
		
  - método 'public String getRunnableEntityProcessName()' que retorna o nome do processo que é manipulado por esta visão. O gerenciador de visão do usuário 'UserSessionBean' controla todas as visões que são instanciadas para este usuário.
    Quando um processo compatí­vel com uma entidade é identificado, o gerenciador pega o nome do processo e tenta buscar qual visão manipula ele.
    Para isto, o gerenciador remove a sufixo 'Process' do nome do processo e coloca o sufixo 'Bean' e busca uma visão com este nome.
    A busca é realizar pelo método utilitário do Faces, como será mostrado no exemplo a seguir.
    Este método foi utilizado, porque um processo na camada inferior não pode referenciar uma visão especí­fica na camada superior.
    Uma vez que podemos ter várias tecnologias de visão para um mesmo processo.
    Futuramente, o gerenciador, ao identificar um processo compatí­vel deverá solicitar ao gerenciador de interface atual uma visão compatí­vel com o processo.
    E isto será possí­vel, porque as visões possuem o nome do processo que elas manipulam.
    A implementação atual resolve a questão de que o JSFaces não fornece todas as views cadastradas no facesConfig.xml.
    O JSFaces vai construindo por demanda cada visão referenciada. No entanto, ele fornece um método que permite pesquisar se uma determinada visão está configurada ou não:
	
	Exemplo:
		FacesContext context = FacesContext.getCurrentInstance();
		ValueBinding value = context.getApplication().createValueBinding("#{" + viewName+ "}");
		return (IRunnableProcessView)  value.getValue(context);

  - método 'public String runWithEntity(IEntity entity)' que invoca o processo e executa o método process.runWithEntity com a entidade selecionada.

