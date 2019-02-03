MONSTRENGO

�ltima revis�o: 2019/01/31
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
 |  - *.info.xml             // Arquivos de descri��o das entidades do sistema
 +--src                      // Armazena o c�digo fonte
 +--test                     // Armazena as classes de teste que testam o src
 -applicationContext.xml     // Arquivo de configura��o dos Beans do Spring. Cont�m, de forma integrada, toda a configura��o da aplica��o, quais m�dulos s�o usados e quais entidades ser�o manipuladas pela sess�o do hibernate.
 -build.xml                  // Define as tarefas de compila��o, hibernate, spring, jars e outras
 -readme.txt                 // Arquivo com a descri��o do projeto

Alguns detalhes importantes a serem observados nos recursos acima descritos
---------------------------------------------------------------------------

Command cache
-------------
mvn install:install-file -DpomFile=../pom.xml -DgroupId=br.com.orionsoft -DartifactId=monstrengo-core -Dversion=1.0.0 -Dfile=monstrengo-core-1.0.0.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true
