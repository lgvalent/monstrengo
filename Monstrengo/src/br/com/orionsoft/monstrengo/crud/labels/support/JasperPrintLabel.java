package br.com.orionsoft.monstrengo.crud.labels.support;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabel;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabel;

/**
 * Esta classe gera um relatório Jasper com as etiquetas e modelos passados.
 * O relatório JRXML já existe em um arquivo com os componentes básicos que serão
 * manipulados para configurar as etiquetas de acordo com os parâmetros do modelo.
 * Toda a configuração é realizada utilizando as classes JRDesing que permitem, por
 * TypeCast, manipular propriedades de todos elementos do relatórios. Desde a página, 
 * bands, e outros.
 *  
 * @author Lucio
 * @version 20061208
 *
 */
public class JasperPrintLabel {
	
	private static final boolean FONT_BOLD = false;
	private static final boolean FONT_ITALIC = false;
	private static final boolean FONT_UNDERLINE = false;

	private static final String LABEL_XML = "JasperPrintLabel.jrxml";
	private static final float CMM_PIXEL_FACTOR = new Float(28.345);
	
	public static void print(List<AddressLabel> labels, ModelLabel modelLabel, int printerIndex)throws BusinessException{
		try {
			/* Verifica se a lista de etiqueta não está vazia */
			if(!labels.isEmpty()){
				/* Verificando quantas etiquetas estão selecionadas 
				 * para otimizar o vetor na memoria para o tamanho exato */
				int selected = 0;
				for(AddressLabel label: labels)
					if(label.isPrint())
						selected++;
				
				/* Criando a lista de etiquetas marcadas para impressao*/
				List<AddressLabel> printLabels = new ArrayList<AddressLabel>(selected);
				for(AddressLabel label: labels)
					if(label.isPrint())
						printLabels.add(label);
				
				/* Prepara o dataSource de beans com a lista de etiquetas */
				JRDataSource jrds = new JRBeanCollectionDataSource(printLabels);
				
				/* Lê o modelo de etiqueta no arquivo XML */	
				JasperDesign design = JRXmlLoader.load(JasperPrintLabel.class.getResourceAsStream(LABEL_XML));
				
				/* Define as propriedades das MARGENS LEFT/TOP */
				design.setTopMargin(Math.round(modelLabel.getMarginTop()*CMM_PIXEL_FACTOR));
				design.setLeftMargin(Math.round(modelLabel.getMarginLeft()*CMM_PIXEL_FACTOR));
				design.setBottomMargin(0);
				design.setRightMargin(0);
				
				/* Define as propriedades da PÁGINA */
				design.setPageHeight(Math.round(modelLabel.getPageHeight()*CMM_PIXEL_FACTOR));
				design.setPageWidth(Math.round(modelLabel.getPageWidth()*CMM_PIXEL_FACTOR));
				
				/* Propriedades das colunas */
				design.setColumnCount(modelLabel.getColumnsLabel());
				design.setColumnWidth(Math.round(modelLabel.getLabelWidth()*CMM_PIXEL_FACTOR));
				if(modelLabel.getColumnsLabel()>1)
					design.setColumnSpacing(Math.round(modelLabel.getHorizontalDistance()*CMM_PIXEL_FACTOR));

				/* Calcula a altura da linha de texto de acordo com o tamanho da fonte.
				 * Esta fórmula funciona até o tamanho de 35 */
				int lineTextHeight = modelLabel.getFontSize()+(modelLabel.getFontSize()/10)+1;
				
				for(int i=0;i<5;i++){
					JRDesignTextElement text = (JRDesignTextElement) design.getDetail().getElements()[i]; 
//					text.setForecolor(Color.BLUE);
					text.setWidth(Math.round(modelLabel.getLabelWidth()*CMM_PIXEL_FACTOR));
					text.setFontName(modelLabel.getFontName());
					text.setFontSize(modelLabel.getFontSize());
					text.setHeight(lineTextHeight);
					text.setBold(FONT_BOLD);
					text.setItalic(FONT_ITALIC);
					text.setUnderline(FONT_UNDERLINE);
					text.setY(lineTextHeight*i);
					
				}
				
				JRDesignBand band = (JRDesignBand) design.getDetail();
				band.setHeight(Math.round((modelLabel.getLabelHeight()+modelLabel.getVerticalDistance())*CMM_PIXEL_FACTOR));
				
				for(Object obj: JasperCompileManager.verifyDesign(design))
					System.out.println(obj.toString());
				
				JasperReport report = JasperCompileManager.compileReport(design);
				
				JasperPrint print = JasperFillManager.fillReport(report, null, jrds);
		    	
				/* Define os parâmetros de impressão envelor=>LANDSCAPE ou PORTRAIT */
				PrintRequestAttributeSet requestAttributeSet = new HashPrintRequestAttributeSet();
				if(modelLabel.isEnvelope())
					requestAttributeSet.add(OrientationRequested.LANDSCAPE);
				else
					requestAttributeSet.add(OrientationRequested.PORTRAIT);
				
				PrintUtils.printJasper(print, requestAttributeSet, printerIndex);
				
//				JasperViewer.viewReport(print);
			}
			
		} catch (JRException e) {
			throw new BusinessException(MessageList.createSingleInternalError(e));
		}
		
	}


	public static void makePdf(List<AddressLabel> labels, ModelLabel modelLabel, OutputStream outputStream)throws BusinessException{
		try {
			/* Verifica se a lista de etiqueta não está vazia */
			if(!labels.isEmpty()){
				/* Verificando quantas etiquetas estão selecionadas 
				 * para otimizar o vetor na memoria para o tamanho exato */
				int selected = 0;
				for(AddressLabel label: labels)
					if(label.isPrint())
						selected++;
				
				/* Criando a lista de etiquetas marcadas para impressao*/
				List<AddressLabel> printLabels = new ArrayList<AddressLabel>(selected);
				for(AddressLabel label: labels)
					if(label.isPrint())
						printLabels.add(label);
				
				/* Prepara o dataSource de beans com a lista de etiquetas */
				JRDataSource jrds = new JRBeanCollectionDataSource(printLabels);
				
				/* Lê o modelo de etiqueta no arquivo XML */	
				JasperDesign design = JRXmlLoader.load(JasperPrintLabel.class.getResourceAsStream(LABEL_XML));
				
				/* Define as propriedades das MARGENS LEFT/TOP */
				design.setTopMargin(Math.round(modelLabel.getMarginTop()*CMM_PIXEL_FACTOR));
				design.setLeftMargin(Math.round(modelLabel.getMarginLeft()*CMM_PIXEL_FACTOR));
				design.setBottomMargin(0);
				design.setRightMargin(0);
				
				/* Define as propriedades da PÁGINA */
				design.setPageHeight(Math.round(modelLabel.getPageHeight()*CMM_PIXEL_FACTOR));
				design.setPageWidth(Math.round(modelLabel.getPageWidth()*CMM_PIXEL_FACTOR));
				
				/* Propriedades das colunas */
				design.setColumnCount(modelLabel.getColumnsLabel());
				design.setColumnWidth(Math.round(modelLabel.getLabelWidth()*CMM_PIXEL_FACTOR));
				if(modelLabel.getColumnsLabel()>1)
					design.setColumnSpacing(Math.round(modelLabel.getHorizontalDistance()*CMM_PIXEL_FACTOR));

				/* Calcula a altura da linha de texto de acordo com o tamanho da fonte.
				 * Esta fórmula funciona até o tamanho de 35 */
				int lineTextHeight = modelLabel.getFontSize()+(modelLabel.getFontSize()/10)+1;
				
				for(int i=0;i<5;i++){
					JRDesignTextElement text = (JRDesignTextElement) design.getDetail().getElements()[i]; 
//					text.setForecolor(Color.BLUE);
					text.setWidth(Math.round(modelLabel.getLabelWidth()*CMM_PIXEL_FACTOR));
					text.setFontName(modelLabel.getFontName());
					text.setFontSize(modelLabel.getFontSize());
					text.setHeight(lineTextHeight);
					text.setBold(FONT_BOLD);
					text.setItalic(FONT_ITALIC);
					text.setUnderline(FONT_UNDERLINE);
					text.setY(lineTextHeight*i);
					
				}
				
				JRDesignBand band = (JRDesignBand) design.getDetail();
				band.setHeight(Math.round((modelLabel.getLabelHeight()+modelLabel.getVerticalDistance())*CMM_PIXEL_FACTOR));
				
				for(Object obj: JasperCompileManager.verifyDesign(design))
					System.out.println(obj.toString());
				
				JasperReport report = JasperCompileManager.compileReport(design);
				
				JasperPrint print = JasperFillManager.fillReport(report, null, jrds);
		    	
				/* Define os parâmetros de impressão envelor=>LANDSCAPE ou PORTRAIT */
				PrintRequestAttributeSet requestAttributeSet = new HashPrintRequestAttributeSet();
				if(modelLabel.isEnvelope())
					requestAttributeSet.add(OrientationRequested.LANDSCAPE);
				else
					requestAttributeSet.add(OrientationRequested.PORTRAIT);
				
				JasperExportManager.exportReportToPdfStream(print, outputStream);
				
//				JasperViewer.viewReport(print);
			}
			
		} catch (JRException e) {
			throw new BusinessException(MessageList.createSingleInternalError(e));
		}
		
	}

	public static void printRules(int printerIndex){
		// TODO impressão da régua na folha de etiquetas
	}	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println("Float to int - " + Math.round(80));
		
		ModelLabel modelo1 = new ModelLabel();
		modelo1.setPageWidth(new Float(20.5));
		modelo1.setPageHeight(new Float(27.9));
		modelo1.setEnvelope(false);

		modelo1.setFontName("SanSerif");
		modelo1.setFontSize(10);
		
		modelo1.setMarginTop(new Float(0.1));
		modelo1.setMarginLeft(1);
		modelo1.setLabelWidth(new Float(9));
		modelo1.setLabelHeight(new Float(2.52));
		modelo1.setHorizontalDistance(0);
		modelo1.setVerticalDistance(new Float(0));

		System.out.println("columns: " + modelo1.getColumnsLabel());
		System.out.println("lines: " + modelo1.getLinesLabel());
		
		ModelLabel envelope = new ModelLabel();
		envelope.setPageWidth(new Float(20.5));
		envelope.setPageHeight(new Float(15));
		envelope.setEnvelope(true);

		envelope.setFontName("SanSerif");
		envelope.setFontSize(10);
		
		envelope.setMarginTop(new Float(2));
		envelope.setMarginLeft(12);
		envelope.setLabelWidth(new Float(7));
		envelope.setLabelHeight(new Float(12));
		envelope.setHorizontalDistance(0);
		envelope.setVerticalDistance(new Float(0));

		System.out.println("columns: " + envelope.getColumnsLabel());
		System.out.println("lines: " + envelope.getLinesLabel());

		AddressLabel endereco = new AddressLabel();
		endereco.setLine1("Lucio Geronimo Valentin Pereira Ltda");
		endereco.setLine2("Lucio Geronimo Valentin Pereira");
		endereco.setLine3("Rua Cezar Lattes, 263");
		endereco.setLine4("Casa / Jardim Alvorada");
		endereco.setLine5("CEP: 87.035-070 - Maringá/Pr ");
		
		List<AddressLabel> etiquetas = new ArrayList<AddressLabel>();
		
		for(int i=0;i<1;i++)
			etiquetas.add(endereco);
		try {
			System.out.println(PrintUtils.retrievePrinters().get(2).getLabel());
			JasperPrintLabel.print(etiquetas, envelope, 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
