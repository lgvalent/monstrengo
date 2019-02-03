MONSTRENGO

Última revisão: 2019/01/31
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
 |  - *.info.xml             // Arquivos de descrição das entidades do sistema
 +--src                      // Armazena o código fonte
 +--test                     // Armazena as classes de teste que testam o src
 -applicationContext.xml     // Arquivo de configuração dos Beans do Spring. Contém, de forma integrada, toda a configuração da aplicação, quais módulos são usados e quais entidades serão manipuladas pela sessão do hibernate.
 -build.xml                  // Define as tarefas de compilação, hibernate, spring, jars e outras
 -readme.txt                 // Arquivo com a descrição do projeto

Alguns detalhes importantes a serem observados nos recursos acima descritos
---------------------------------------------------------------------------

Command cache
-------------
mvn install:install-file -DpomFile=../pom.xml -DgroupId=br.com.orionsoft -DartifactId=monstrengo-core -Dversion=1.0.0 -Dfile=monstrengo-core-1.0.0.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true
