package br.com.orionsoft.monstrengo.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.FileCopyUtils;

import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;

/**
 * Classe que faz operações básicas em arquivos de upload
 * 
 * @author Andre
 *
 */
public class UploadUtils
{
    /**
     * <p>O arquivo de upload não pode ser interpretado simplesmente como um objeto do tipo File;
     * Por isso, passa-se o seu conteúdo para esta classe, como um InputStream, e então
     * é feita uma cópia desse conteúdo em um arquivo do tipo File, que pode então ser manipulado
     * facilmente. 
     * 
     * @param nomeArquivo - O nome a ser usado para salvar o arquivo
     * @param input - InputStream que contém as informaçõe do arquivo de upload
     * @return - Um objeto do tipo File, com o conteúdo do arquivo de upload
     */
    public static File salvarArquivo (String nomeArquivo, InputStream input){
        
        try
        {   
            //cria ou abre um diretório padrão
            File diretorio = new File("upload");
            if (!diretorio.exists()){
                diretorio.mkdir();
            }
            
            //cria um novo arquivo com o nome do arquivo "uploaded" 
            File arquivo = new File(diretorio, nomeArquivo);
            
            /*
             * Primeiramente, obtém-se um InputStream com o arquivo do tipo UploadedFile 
             * (com o método getInputStream()) e copia seu conteúdo para um Array de Bytes;
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