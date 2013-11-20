package br.com.orionsoft.financeiro.documento.pagamento.cheque;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import br.com.orionsoft.financeiro.utils.Extenso;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;

/**
 * Esta classe gera um relat�rio Jasper com os cheques passados pela lista de ChequePrintBean.
 * O relat�rio JRXML j� existe em um arquivo com os componentes b�sicos que ser�o
 * manipulados para configurar os cheques de acordo com os par�metros do modelo.
 * Toda a configura��o � realizada utilizando as classes JRDesing que permitem, por
 * TypeCast, manipular propriedades de todos elementos do relat�rios. Desde a p�gina, 
 * bands, e outros.
 *  
 * @author Andre
 * @version 20070122
 *
 */
public class ChequeImpressaoJasper {
	
	private static final boolean FONTE_BOLD = false;
	private static final boolean FONTE_ITALIC = false;
	private static final boolean FONTE_UNDERLINE = false;

	private static final String CHEQUE_MODELO_XML = "ChequeJasper.jrxml";
	private static final int CHEQUE_QUANTIDADE_CAMPOS = 9;
	
	private static final float CMM_PIXEL_FACTOR = new Float(28.345);
	
	public static void print(List<ChequePrintBean> beans, ChequeModelo chequeModelo, OutputStream outputStream, int printerIndex)throws BusinessException{
		try {
			/* Verifica se a lista de cheques n�o est� vazia */
			if(!beans.isEmpty()){
				/* Prepara o dataSource de beans com a lista de cheques */
				JRDataSource jrds = new JRBeanCollectionDataSource(beans);
				
				/* L� o modelo de cheques no arquivo XML */	
				JasperDesign design = JRXmlLoader.load(ChequeImpressaoJasper.class.getResourceAsStream(CHEQUE_MODELO_XML));

				/* Define as propriedades das MARGENS LEFT/TOP/BOTTOM/RIGHT como ZERO, pois 
				 * todas as demais medidas s�o relativas ao in�cio da folha */
				design.setTopMargin(0);
				design.setLeftMargin(0);
				design.setBottomMargin(0);
				design.setRightMargin(0);
				
				/* Define as propriedades da P�GINA */
				design.setPageWidth(Math.round(chequeModelo.getFolhaLargura()*CMM_PIXEL_FACTOR));
				design.setPageHeight(Math.round(chequeModelo.getFolhaAltura()*CMM_PIXEL_FACTOR));
				
				/*
				 * Setando as propriedades de cada campo, como tamanho, posi��o e fonte.
				 */
				setPropriedadesCampos(design, chequeModelo);
				
				for(Object obj: JasperCompileManager.verifyDesign(design))
					System.out.println(obj.toString());
				
				//definindo a altura de cada cheque
				JRDesignBand band = (JRDesignBand) design.getDetail();
				band.setHeight(Math.round(chequeModelo.getChequeAltura()*CMM_PIXEL_FACTOR));
				
				//Compilando o relat�rio
				JasperReport report = JasperCompileManager.compileReport(design);
				
				//Preparando para impress�o
				JasperPrint jasperPrint = JasperFillManager.fillReport(report, new HashMap<Object, Object>(), jrds);
				
				/* Define os par�metros de impress�o como PORTRAIT ou LANDSCAPE */
				PrintRequestAttributeSet requestAttributeSet = new HashPrintRequestAttributeSet();
				requestAttributeSet.add(OrientationRequested.PORTRAIT);
				//TODO ver se � necess�rio imprimir com a folha em LANDSCAPE
				
				//TODO - verificar se a impressora p�ra ap�s imprimir o cheque, com o procedimento usado, sen�o rever o bloco comentado abaixo
//				JRTextExporter exporter = new JRTextExporter();
//				File destFile = new File("/home/andre/Desktop/", print.getName() + ".txt");
//
//				exporter.setParameter(JRTextExporterParameter.JASPER_PRINT, print);
//				exporter.setParameter(JRTextExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
//				exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, 7);
//				exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, 7);
//				exporter.exportReport();
//				
//				try {
//					/* Cria o documento de impress�o usando o buffer preenchido logo acima */
//					SimpleDoc doc = new SimpleDoc(print, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
//					/* Prepara alguns atributos de impress�o do trabalho de impress�o */
//					PrintRequestAttributeSet att = new  HashPrintRequestAttributeSet();
//					att.add(MediaSizeName.PERSONAL_ENVELOPE);
//					att.add(OrientationRequested.LANDSCAPE);
//					att.add(MediaSizeName.ISO_B8);
//					
//					/* Seleciona a impressora de acordo com o �ndice passado e j� cria um trabalho de impress�o pra ela */
//					DocPrintJob job = PrintUtils.createDocPrintJob(printerIndex, "");
//					
//					job.print(doc, att);
//
//				} catch (PrintException e) {
//					e.printStackTrace();
//				}
				
				/* Envia para PDF e impress�o oa mesmo tempo. A fun��o detecta qual op��o � v�lida.
				 * � poss�vel imprimir em ambas m�dias */
				PrintUtils.printJasper(jasperPrint, outputStream);
				/* print recebe o arquivo relacionado � impress�o do T�tulo e priterIndex � a impressora selecionada para a impress�o */
				PrintUtils.printJasper(jasperPrint, printerIndex);

				//Visualisa
//				JasperViewer.viewReport(print);
//				while(true);
			}
			
		} catch (JRException e) {
			throw new BusinessException(MessageList.createSingleInternalError(e));
		}
		
	}

	private static void setPropriedadesCampos(JasperDesign design, ChequeModelo chequeModelo) throws BusinessException{
		/* 
		 * Calcula a altura da linha de texto de acordo com o tamanho da fonte.
		 * Esta f�rmula funciona at� o tamanho de fonte 35.
		 */ 
		int lineTextHeight = chequeModelo.getFonteTamanho()+(chequeModelo.getFonteTamanho()/10)+1;
		
		/* 
		 * Setando as propriedades da fonte.
		 * Estas proporiedades ser�o comuns a todos os campos.
		 */
		JRDesignTextElement text = null;
		for(int i = 0; i < CHEQUE_QUANTIDADE_CAMPOS; i++){
			/*
			 * 9 componentes no total, onde os pr�ximos passos s�o comuns a todos eles. Em seguida, 
			 * no bloco switch-case, est� a configura��o espec�fica de cada um dos componentes. 
			 */
			text = (JRDesignTextElement) design.getDetail().getElements()[i]; 
			text.setForecolor(Color.BLACK);
			text.setFontName(chequeModelo.getFonteNome());
			text.setFontSize(chequeModelo.getFonteTamanho());
			text.setHeight(lineTextHeight);
			text.setBold(FONTE_BOLD);
			text.setItalic(FONTE_ITALIC);
			text.setUnderline(FONTE_UNDERLINE);
			
			/* 
			 * Setando o tamanho de cada componente. 
			 * No caso do cheque, s�o 9 componentes, que possuem tamanhos e posicionamentos diferentes.
			 */
			int j = i;
			switch (j) {
			case 0:
				text.setWidth(Math.round(chequeModelo.getNumeroChequeTamanho()*CMM_PIXEL_FACTOR));
				text.setX(Math.round(chequeModelo.getNumeroChequeX()*CMM_PIXEL_FACTOR)); //posi��o horizontal
				text.setY(Math.round(chequeModelo.getNumeroChequeY()*CMM_PIXEL_FACTOR)); //posi��o vertical
				break;
				
			case 1:
				text.setWidth(Math.round(chequeModelo.getCodigoBancoTamanho()*CMM_PIXEL_FACTOR));
				text.setX(Math.round(chequeModelo.getCodigoBancoX()*CMM_PIXEL_FACTOR)); //posi��o horizontal
				text.setY(Math.round(chequeModelo.getCodigoBancoY()*CMM_PIXEL_FACTOR)); //posi��o vertical
				break;
				
			case 2:
				text.setWidth(Math.round(chequeModelo.getValorExtensoTamanho()*CMM_PIXEL_FACTOR));
				text.setX(Math.round(chequeModelo.getValorExtensoX()*CMM_PIXEL_FACTOR)); //posi��o horizontal
				text.setY(Math.round(chequeModelo.getValorExtensoY()*CMM_PIXEL_FACTOR)); //posi��o vertical
				break;
				
			case 3:
				text.setWidth(Math.round(chequeModelo.getValorDecimalTamanho()*CMM_PIXEL_FACTOR));
				text.setX(Math.round(chequeModelo.getValorDecimalX()*CMM_PIXEL_FACTOR)); //posi��o horizontal
				text.setY(Math.round(chequeModelo.getValorDecimalY()*CMM_PIXEL_FACTOR)); //posi��o vertical
				break;
				
			case 4:
				text.setWidth(Math.round(chequeModelo.getNomeFavorecidoTamanho()*CMM_PIXEL_FACTOR));
				text.setX(Math.round(chequeModelo.getNomeFavorecidoX()*CMM_PIXEL_FACTOR)); //posi��o horizontal
				text.setY(Math.round(chequeModelo.getNomeFavorecidoY()*CMM_PIXEL_FACTOR)); //posi��o vertical
				break;
				
			case 5:
				text.setWidth(Math.round(chequeModelo.getCidadeEstadoTamanho()*CMM_PIXEL_FACTOR));
				text.setX(Math.round(chequeModelo.getCidadeEstadoX()*CMM_PIXEL_FACTOR)); //posi��o horizontal
				text.setY(Math.round(chequeModelo.getCidadeEstadoY()*CMM_PIXEL_FACTOR)); //posi��o vertical
				break;
				
			case 6:
				text.setWidth(Math.round(chequeModelo.getDiaTamanho()*CMM_PIXEL_FACTOR));
				text.setX(Math.round(chequeModelo.getDiaX()*CMM_PIXEL_FACTOR)); //posi��o horizontal
				text.setY(Math.round(chequeModelo.getDiaY()*CMM_PIXEL_FACTOR)); //posi��o vertical
				break;
				
			case 7:
				text.setWidth(Math.round(chequeModelo.getMesTamanho()*CMM_PIXEL_FACTOR));
				text.setX(Math.round(chequeModelo.getMesX()*CMM_PIXEL_FACTOR)); //posi��o horizontal
				text.setY(Math.round(chequeModelo.getMesY()*CMM_PIXEL_FACTOR)); //posi��o vertical
				break;
				
			case 8:
				text.setWidth(Math.round(chequeModelo.getAnoTamanho()*CMM_PIXEL_FACTOR));
				text.setX(Math.round(chequeModelo.getAnoX()*CMM_PIXEL_FACTOR)); //posi��o horizontal
				text.setY(Math.round(chequeModelo.getAnoY()*CMM_PIXEL_FACTOR)); //posi��o vertical
				break;	

			default:
				throw new BusinessException(MessageList.create(ChequeImpressaoJasper.class, "PROPRIEDADE_NAO_DEFINIDA"));
			}
		}
	}
		
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ChequeModelo chequeModelo = new ChequeModelo();

		chequeModelo.setFolhaAltura(new Float(7.6));
		chequeModelo.setFolhaLargura(new Float(21));
		chequeModelo.setChequeAltura(new Float(7.6));
		
		chequeModelo.setNumeroChequeTamanho(new Float(5));
		chequeModelo.setNumeroChequeX(new Float(1));
		chequeModelo.setNumeroChequeY(new Float(4.5));
		
		chequeModelo.setCodigoBancoTamanho(new Float(0.7));
		chequeModelo.setCodigoBancoX(new Float(2));
		chequeModelo.setCodigoBancoY(new Float(0.6));

		chequeModelo.setValorExtensoTamanho(new Float(13));
		chequeModelo.setValorExtensoX(new Float(3));
		chequeModelo.setValorExtensoY(new Float(1.2));
		
		chequeModelo.setValorDecimalTamanho(new Float(5.2));
		chequeModelo.setValorDecimalX(new Float(14));
		chequeModelo.setValorDecimalY(new Float(0.6));
		
		chequeModelo.setNomeFavorecidoTamanho(new Float(13));
		chequeModelo.setNomeFavorecidoX(new Float(1));
		chequeModelo.setNomeFavorecidoY(new Float(2.4));
		
		chequeModelo.setCidadeEstadoTamanho(new Float(3.2));
		chequeModelo.setCidadeEstadoX(new Float(8.5));
		chequeModelo.setCidadeEstadoY(new Float(3.4));
		
		chequeModelo.setDiaTamanho(new Float(0.7));
		chequeModelo.setDiaX(new Float(12.4));
		chequeModelo.setDiaY(new Float(3.4));
		
		chequeModelo.setMesTamanho(new Float(3));
		chequeModelo.setMesX(new Float(13.6));
		chequeModelo.setMesY(new Float(3.4));
		
		chequeModelo.setAnoTamanho(new Float(1));
		chequeModelo.setAnoX(new Float(16.7));
		chequeModelo.setAnoY(new Float(3.4));
		
		chequeModelo.setFonteNome("Courier 10 Pitch");
		chequeModelo.setFonteTamanho(11);

		ChequePrintBean bean = new ChequePrintBean();
		bean.setNumeroCheque("Controle: 910910");
		bean.setCodigoBanco("756");
		bean.setValorExtenso(Extenso.getExtenso(new BigDecimal(1930)));
		bean.setValorDecimal("##(1930)##");
		bean.setNomeFavorecido("Andre Moraes");
		bean.setCidadeEstado("Maring�-PR");
		bean.setDia("23");
		bean.setMes("Janeiro");
		bean.setAno("2007");
		
		List<ChequePrintBean> cheques = new ArrayList<ChequePrintBean>();
		
		for(int i=0;i<5;i++)
			cheques.add(bean);
		
		try {
			System.out.println(PrintUtils.retrievePrinters().get(4).getLabel());
			FileOutputStream file = new FileOutputStream("/home/orion/Desktop/documentoPagamento.pdf");

			ChequeImpressaoJasper.print(cheques, chequeModelo, file, PrintUtils.PRINTER_INDEX_NO_PRINT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
