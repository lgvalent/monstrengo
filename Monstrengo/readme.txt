FRAMEWORK

Última revisão: 2006/01/04
Revisor: Lucio

Descrição da estrutura básica do projeto:
-----------------------------------------

Este documento apresenta inúmeros tópicos que devem estar
íntegros para que o projeto possa ser compilado, executado e
encontre todas as classes e recursos necessários.

Os projetos seguem um estrutura básica de diretórios.
-----------------------------------------------------
 +Basedir
 +--bin                      // Armazena as informações que serão distribuídas por um JAR
 |  - *.class                // As classes compiladas
 |  - *.hbm.xml              // Os arquivos de mapeamentos das entidades do hibernate
 |  - *.properties           // Arquivos de descrição das entidades do sistema
 +--src                      // Armazena o código fonte
 +--test                     // Armazena as classes de teste que testam o src
 -applicationContext.xml     // Arquivo de configuração dos Beans do Spring. Contém, de forma integrada, toda a configuração da aplicação, quais módulos são usados e quais entidades serão manipuladas pela sessão do hibernate.
 -build.xml                  // Define as tarefas de compilação, hibernate, spring, jars e outras
 -readme.txt                 // Arquivo com a descrição do projeto
 -spring-beans.xml           // Define a integração Hiberante-Spring e outras propriedades

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
 A configuração acima indica para a sessão do hibernate localizar os arquivos de
 mapeamento (hbm.xml) a partir do diretório bin.
