package br.com.orionsoft.basico.cadastro.endereco;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.notification.RunNotifier;

import br.com.orionsoft.basic.entities.endereco.Municipio;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Esta classe popula o banco de dados com os municípios de acordo com o IBGE em 2007
 * 
 * @author andre 
 * @since 20071023
 */
public class PopularMunicipios extends ServiceBasicTest {

	/**
	 * Permite que esta classe seja executada diretamente por linha de comando.
	 * 
	 * @param args
	 * @throws InitializationError
	 */
	public static void main(String[] args) throws InitializationError
	{
		new TestClassRunner(PopularMunicipios.class).run(new RunNotifier());
	}
	
	@Test
	public void popular() {
		try {
			Map<String, String> listaMunicipio = new HashMap<String, String>();
			
			POIFSFileSystem fileSystem = null;
			HSSFWorkbook workbook = null;
			String fileName = "";
			InputStream input = null;
			
			try {
				fileName = openFileChooser();
				input = new FileInputStream(fileName);
				fileSystem = new POIFSFileSystem(input);
				workbook = new HSSFWorkbook(fileSystem);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//obtendo diretamente o primeiro "Sheet"
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			//obtendo o número de linhas
			int rows = sheet.getPhysicalNumberOfRows();

			for (int r = 0; r < rows; r++) {
				//obtendo a linha
				HSSFRow row = sheet.getRow(r);

				//cada linha possui 2 colunas - Uf e Município
				HSSFCell cell1  = row.getCell((short)0); //UF
				HSSFCell cell2  = row.getCell((short)0); //Município
				
				String municipio = cell2.getRichStringCellValue() + "-" + cell1.getRichStringCellValue();

				if (!listaMunicipio.containsKey(municipio)){
					//adiciona na lista para que não grave o município novamente
					listaMunicipio.put(municipio, municipio);
					
					//criar nova entidade e gravar no banco
					IEntity eMunicipio = UtilsCrud.create(this.serviceManager, Municipio.class, null);
					eMunicipio.getProperty(Municipio.NOME).getValue().setAsString(cell2.getRichStringCellValue().toString());
					
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	
	private String openFileChooser(){
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(null); //abre um "selecionador de arquivo"

		//se selecionou o botão 'Cancel'
		if (result == JFileChooser.CANCEL_OPTION){
			return "";
			//se selecionou o botão 'Open'
		}else{
			return fileChooser.getSelectedFile().getAbsolutePath();
		}
	}
	
}
