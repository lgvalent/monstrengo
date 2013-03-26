package br.com.orionsoft.monstrengo.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.FileCopyUtils;

import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;

/**
 * Classe que faz opera��es b�sicas em arquivos de upload
 * 
 * @author Andre
 *
 */
public class UploadUtils
{
    /**
     * <p>O arquivo de upload n�o pode ser interpretado simplesmente como um objeto do tipo File;
     * Por isso, passa-se o seu conte�do para esta classe, como um InputStream, e ent�o
     * � feita uma c�pia desse conte�do em um arquivo do tipo File, que pode ent�o ser manipulado
     * facilmente. 
     * 
     * @param nomeArquivo - O nome a ser usado para salvar o arquivo
     * @param input - InputStream que cont�m as informa��e do arquivo de upload
     * @return - Um objeto do tipo File, com o conte�do do arquivo de upload
     */
    public static File salvarArquivo (String nomeArquivo, InputStream input){
        
        try
        {   
            //cria ou abre um diret�rio padr�o
            File diretorio = new File("upload");
            if (!diretorio.exists()){
                diretorio.mkdir();
            }
            
            //cria um novo arquivo com o nome do arquivo "uploaded" 
            File arquivo = new File(diretorio, nomeArquivo);
            
            /*
             * Primeiramente, obt�m-se um InputStream com o arquivo do tipo UploadedFile 
             * (com o m�todo getInputStream()) e copia seu conte�do para um Array de Bytes;
             * Depois, copia o Array de Bytes para um arquivo do tipo File
             */
            
            FileCopyUtils.copy(FileCopyUtils.copyToByteArray(input), arquivo);
            
            return arquivo;
            
        } catch (IOException e)
        {
            UtilsTest.showMessageList(MessageList.createSingleInternalError(e));
            return null;
        }
    }
}