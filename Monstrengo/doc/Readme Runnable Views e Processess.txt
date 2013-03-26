ADAPTAR:==========================================================================================
- Excluir do cadastro de entidades as entidades que nao existem mais:
  - Grupo
  - Movimento

08103782'

Runnable:=========================================================================================
Antes de implementar um Runnable, leia este documento. Ele ir� te ajudar a lembrar dos detalhes que
envolvem a implanta��o de um novo processo come esta caracter�stica.

Vamos precisar de tr�s componentes para implementar a caracter�stica Runnable em um processo de neg�cio.
	1) Um processo que implemente a interface IRunnableEntityProcess;
	2) Um controlador runnable para o processo: IRunnableEntityProcessController (RunnableEntityProcessControllerBasic);
	3) Uma vis�o que implemente a interface IRunnableProcessView;

1) Processo Runnable
  Um processo � Runnable quando ele implementa a interface IRunnableEntityProcess e possui um controlador implementado.
  Ao definir um processo como Runnable lembre-se de definir os metadados do processo utilizando as anota��es.
  Exemplo: @ProcessMetadataAnnotation(label="Redefinir a senha de um operador", hint="Permite redefinir a senha de um operador sem conhecer a sua atual senha", description="Permite redefinir a senha de um operador sem conhecer a sua atual senha. �til para os administradores redefinirem a senha esquecida de um operador")
		   public class OverwritePasswordProcess extends ProcessBasic implements IRunnableEntityProcess{
		   ...
		   }
  Abaixo est� um exemplo de implementa��o do m�todo definido pela interface IRunnableEntityProcess
  
  	/*==============================================================================
	 * IRunnableEntityProcess	
	 *==============================================================================*/
	
	/** Implementa��o fict�cia */
	public boolean runWithEntity(IEntity entity) {
        super.beforeRun();

        boolean result = false;
		
		/* Verifica se a entidade � compat�vel */
		if(entity.getInfo().getType() == ApplicationUser.class){
			try {
				/* Extra� os par�metros da entidade que s�o �teis para o atual processo */
				this.login = entity.getProperty(ApplicationUser.LOGIN).getValue().getAsString();

				/* Preenche algumas op��es padr�es do processo caso seja chamado por runWithEntity() */
				this.dataInicial = '01/01/1900';
				this.dataFinal = '31/12/2008';

				/* Executa alguma a��o para que o processo j� exiba algum resultado
				 * na primeira exibi��o */
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
  Ao iniciar a execu��o de um processo de neg�cio, o sistema fornece uma inst�ncia individual para cada operador.
  Assim, haver� v�rias inst�ncias de um mesmo processo em execu��o no sistema.
  O ProcessManager � respons�vel por identificar o processo solicitado e instanci�-lo.
  Tamb�m � cargo do ProcessManager, verificar se um determinado processo � ou n�o Runnable.
  Isto � poss�vel, porque o ProcessManager mant�m o registro de todos os controladores de processo existentes.
  Cada processo Runnable deve ter um controlador implementado para ele.
  Este controlador � respons�vel por verificar se um processo pode ou n�o ser executado a partir de uma determinada entidade.
  Isto porque, uma entidade pode estar em um estado que impede a execu��o de um processo sobre ela.
  Exemplo: Um operador inativo n�o pode ter sua senha trocada. Isto j� � indicado pelo controlador OverwritePasswordProcessController;  


3) Bean de vis�o que implementa a interface IRunnableProcessView
  - metodo 'public String getViewName()' que retorne o nome da vis�o que est� registrado no arquivo facesConfig.xml.
    Exemplo: Para a classe ListarPosicaoContratoBean o m�todo getViewName() retorna:
             listarPosicaoContratoBean
             Assim, no arquivo facesConfig.xml temos:
		<managed-bean>
			<managed-bean-name>listarPosicaoContratoBean</managed-bean-name>
		...
		
  - m�todo 'public String getRunnableEntityProcessName()' que retorna o nome do processo que � manipulado por esta vis�o. O gerenciador de vis�o do usu�rio 'UserSessionBean' controla todas as vis�es que s�o instanciadas para este usu�rio.
    Quando um processo compat�vel com uma entidade � identificado, o gerenciador pega o nome do processo e tenta buscar qual vis�o manipula ele.
    Para isto, o gerenciador remove a sufixo 'Process' do nome do processo e coloca o sufixo 'Bean' e busca uma vis�o com este nome.
    A busca � realizar pelo m�todo utilit�rio do Faces, como ser� mostrado no exemplo a seguir.
    Este m�todo foi utilizado, porque um processo na camada inferior n�o pode referenciar uma vis�o espec�fica na camada superior.
    Uma vez que podemos ter v�rias tecnologias de vis�o para um mesmo processo.
    Futuramente, o gerenciador, ao identificar um processo compat�vel dever� solicitar ao gerenciador de interface atual uma vis�o compat�vel com o processo.
    E isto ser� poss�vel, porque as vis�es possuem o nome do processo que elas manipulam.
    A implementa��o atual resolve a quest�o de que o JSFaces n�o fornece todas as views cadastradas no facesConfig.xml.
    O JSFaces vai construindo por demanda cada vis�o referenciada. No entanto, ele fornece um m�todo que permite pesquisar se uma determinada vis�o est� configurada ou n�o:
	
	Exemplo:
		FacesContext context = FacesContext.getCurrentInstance();
		ValueBinding value = context.getApplication().createValueBinding("#{" + viewName+ "}");
		return (IRunnableProcessView)  value.getValue(context);

  - m�todo 'public String runWithEntity(IEntity entity)' que invoca o processo e executa o m�todo process.runWithEntity com a entidade selecionada.

