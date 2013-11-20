BASIC

�ltima revis�o: 2006/01/04
Revisor: Lucio

Descri��o da estrutura b�sica do projeto:
-----------------------------------------

Este documento apresenta in�meros t�picos que devem estar
�ntegros para que o projeto possa ser compilado, executado e
encontre todas as classes e recursos necess�rios.

Os projetos seguem um estrutura b�sica de diret�rios.
-----------------------------------------------------

 +Basedir
 +--bin                      // Armazena as informa��es que ser�o distribu�das por um JAR
 |  - *.class                // As classes compiladas
 |  - *.hbm.xml              // Os arquivos de mapeamentos das entidades do hibernate
 |  - *.properties           // Arquivos de descri��o das entidades do sistema
 |  - applicationContext.xml // Arquivo de configura��o dos Beans do Spring
 +--src                      // Armazena o c�digo fonte
 +--test                     // Armazena as classes de teste que testam o src
 +--viewJSF                  // Armazena os controladores de view com tecnologia JSF (backing-beans)
 -build.xml                  // Define as tarefas de compila��o, hibernate, spring, jars e outras 
 -readme.txt                 // Arquivo com a descri��o do projeto
 -spring-beans.xml           // Define a integra��o Hiberante-Spring e outras propriedades
 
 
Alguns detalhes importantes a serem observados nos recursos acima descritos
---------------------------------------------------------------------------

-spring-beans.xml
...
	<property name="mappingDirectoryLocations">
		<list>
			<value>bin</value>
		</list>
	</property>
...
A configura��o acima indica para a sess�o do hibernate localizar os arquivos de 
mapeamento (hbm.xml) a partir do diret�rio bin.

...
	<property name="mappingJarLocations">
		<value>../Library/OrionSoft/framework.jar</value>
	</property>
...
A configura��o acima diz para o hibernate procurar arquivos de mapeamentos
tamb�m dentro dos Jars especificados. Como estes Jars s�o de outros projetos,
ao acessar um entidade destes projetos o hibernate precisar� de um arquivo de 
mapeamento. Este encontra-se dentro do Jar do projeto utilizado.