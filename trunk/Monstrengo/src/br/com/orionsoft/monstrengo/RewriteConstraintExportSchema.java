package br.com.orionsoft.monstrengo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Esta classe reescreve o schemaExport para evitar os conflitos
 * de nomes dos Constrainst.
 * 
 * @author Lucio
 *
 */
public class RewriteConstraintExportSchema {
	
	private static final String ADD_CONSTRAINT = "add constraint ";
	private static final String ALTER_TABLE = "alter table ";
	private static final String ADD_INDEX = "add index ";
	private static final String FOREIGN_KEY = "foreign key ";
	private static final String DROP_FOREIGN_KEY = "drop foreign key ";
	 
	private static final int MAX_LENGTH_CONSTRAINT_ID = 60; // Para o MySql, pelo menos
	
	private String fileName = "";
	
	public RewriteConstraintExportSchema(String fileName) {
		this.fileName = fileName;
	}

	public void runRewriteFile() {
		try {
			System.out.println("Preparando os arquivos...");
			/* Prepara o arquivo de entrada */
			File fileIn = new File(fileName);
			BufferedReader in = new BufferedReader(new FileReader(fileIn));
			
			/* Prepara o arquivo de saída */
			File fileOut = new File(fileName+ "-temp");
			fileOut.delete();
			fileOut.createNewFile();
			BufferedWriter out = new  BufferedWriter(new FileWriter(fileOut));
			
			String linha;
			while((linha=in.readLine()) != null){
				linha = modificarLinhaDropForeignKey(linha);
				linha = modificarLinhaContrainst(linha);
				/* Coloca o ; no final e quebra de linha, 
				 * pois o hibernate não coloca e o BufferedReader 
				 * remove o fim de linha e só retorna a linha útil
				 */
				out.write(linha + ";\n");
				System.out.println(modificarLinhaContrainst(linha));
			}
			out.flush();
			
			/* Apaga o arquivo tempo e deixa somente o processado */
			if(!fileIn.renameTo(new File(fileName+".del")))
				System.out.println("ERRO");
			if(!fileOut.renameTo(new File(fileName)))
				System.out.println("ERRO");
			if(!new File(fileName+".del").delete())
				System.out.println("ERRO");
			
			System.out.println("Processo finalizado.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String modificarLinhaContrainst(String linha){
		String result = "";
		if(linha.contains(ADD_CONSTRAINT)){
			//System.exit(0); // Pára a execução para poder visualizar as primeiras linhas no Log
			int alterTableEndIndex = linha.indexOf(ALTER_TABLE) + ALTER_TABLE.length();
			int addIndexStartIndex = linha.indexOf(ADD_INDEX);
			int addConstraintEndIndex = linha.indexOf(ADD_CONSTRAINT) + ADD_CONSTRAINT.length();
			int foreignKeyStartIndex = linha.indexOf(FOREIGN_KEY);
			
			String tableName = linha.substring(alterTableEndIndex, addIndexStartIndex-1);
			String propertyName = linha.substring(addConstraintEndIndex, foreignKeyStartIndex-1);
			String newConstraintName = tableName + "_" + propertyName;
			
			/* Verifica se o novo identificador não ultrapassa 60 caracteres
			 * Se ultrapassar, corta à esquerda (o início) do novo 
			 * nome */
			if(newConstraintName.length()>MAX_LENGTH_CONSTRAINT_ID){
				newConstraintName = newConstraintName.substring(newConstraintName.length()-MAX_LENGTH_CONSTRAINT_ID);
				//System.out.println(newConstraintName);
			}
			
			result = linha.substring(0, addConstraintEndIndex);
			result += newConstraintName;
			result += linha.substring(foreignKeyStartIndex-1);
		}
		else
			result = linha;
		
		
		//System.out.println(result);
		return result;
	}

	private String modificarLinhaDropForeignKey(String linha){
		String result = "";
		if(linha.contains(DROP_FOREIGN_KEY)){
			int alterTableEndIndex = linha.indexOf(ALTER_TABLE) + ALTER_TABLE.length();
			int dropForeignKeyStartIndex = linha.indexOf(DROP_FOREIGN_KEY);
			int dropForeignKeyEndIndex = linha.indexOf(DROP_FOREIGN_KEY) + DROP_FOREIGN_KEY.length();
			
			String tableName = linha.substring(alterTableEndIndex, dropForeignKeyStartIndex-1);
			String propertyName = linha.substring(dropForeignKeyEndIndex);
			String newForeignKeyName = tableName + "_" + propertyName;
			
			/* Verifica se o novo identificador não ultrapassa 60 caracteres
			 * Se ultrapassar, corta à esquerda (o início) do novo 
			 * nome */
			if(newForeignKeyName.length()>MAX_LENGTH_CONSTRAINT_ID){
				newForeignKeyName = newForeignKeyName.substring(newForeignKeyName.length()-MAX_LENGTH_CONSTRAINT_ID);
				//System.out.println(newForeignKeyName);
			}
			
			result = linha.substring(0, dropForeignKeyEndIndex);
			result += newForeignKeyName;
		}
		else
			result = linha;
		
		//System.out.println(result);
		return result;
	}

	public String getFileName() {
		return fileName;
	}
	
	public static void main(String[] args) {
		new RewriteConstraintExportSchema("schema-export.sql").runRewriteFile();
	}
	
	
}