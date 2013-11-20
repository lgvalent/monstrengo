BASIC

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
 |  - applicationContext.xml // Arquivo de configuração dos Beans do Spring
 +--src                      // Armazena o código fonte
 +--test                     // Armazena as classes de teste que testam o src
 +--viewJSF                  // Armazena os controladores de view com tecnologia JSF (backing-beans)
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

...
	<property name="mappingJarLocations">
		<value>../Library/OrionSoft/framework.jar</value>
	</property>
...
A configuração acima diz para o hibernate procurar arquivos de mapeamentos
também dentro dos Jars especificados. Como estes Jars são de outros projetos,
ao acessar um entidade destes projetos o hibernate precisará de um arquivo de 
mapeamento. Este encontra-se dentro do Jar do projeto utilizado.