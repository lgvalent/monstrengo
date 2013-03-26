FRAMEWORK

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
 +--src                      // Armazena o c�digo fonte
 +--test                     // Armazena as classes de teste que testam o src
 -applicationContext.xml     // Arquivo de configura��o dos Beans do Spring. Cont�m, de forma integrada, toda a configura��o da aplica��o, quais m�dulos s�o usados e quais entidades ser�o manipuladas pela sess�o do hibernate.
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
