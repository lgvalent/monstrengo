package br.com.orionsoft.monstrengo.crud.labels.support;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabel;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabel;

/**
 * Esta classe gera um relatório com as etiquetas e modelos passados
 * usando somente a API do Java para impressão.
 *  
 * @author Lucio
 * @version 20110208
 *
 */
public class PrintLabelJavaApi {
	
	private static final boolean FONT_BOLD = false;
	private static final boolean FONT_ITALIC = false;
	private static final boolean FONT_UNDERLINE = false;

	private static final float CMM_PIXEL_FACTOR = new Float(28.346456693);
	
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
				
				/* Prepara a página */
				Paper paper = new Paper();
				paper.setSize(modelLabel.getPageWidth()*CMM_PIXEL_FACTOR, modelLabel.getPageHeight()*CMM_PIXEL_FACTOR);
				paper.setImageableArea(modelLabel.getMarginLeft()*CMM_PIXEL_FACTOR, modelLabel.getMarginTop()*CMM_PIXEL_FACTOR, paper.getWidth(), paper.getHeight());

				/* Prepara o formato da página */
				PageFormat formato = new PageFormat ();
				if(modelLabel.isEnvelope())
					formato.setOrientation (PageFormat.LANDSCAPE);
				else
					formato.setOrientation (PageFormat.PORTRAIT);
			    formato.setPaper(paper);
			    
		         //Iniciar o trabalho de impressão
		        PrinterJob printerJob = PrinterJob.getPrinterJob();
		        
		        printerJob.setPrintable(new Documento(labels, modelLabel, true), formato);
		        printerJob.setPrintService(PrintUtils.getPrintService(printerIndex));
		        
	        	//Executar a impressao
	        	printerJob.print();
			}
			
		} catch (Exception e) {
			throw new BusinessException(MessageList.createSingleInternalError(e));
		}
		
	}

	public static void printRules(int printerIndex){
		// TODO impressão da régua na folha de etiquetas
	}	

	public static class Documento implements Printable{
		private List<AddressLabel> labels;
		private ModelLabel modelLabel;
		private boolean printRules;

		public Documento(List<AddressLabel> labels, ModelLabel modelLabel, boolean printRules){
			this.labels = labels;
			this.modelLabel = modelLabel;
			this.printRules = printRules;
		}
		
		/**
		 * Este método é chamado duas vezes, assim, não pode usar propriedades local, pois
		 * elas serão alteradas duas vezes para cada página 
		 */
//		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
				throws PrinterException {
			
			/* Desenha a margem imprimível da página de etiquetas */
			if(this.printRules){
				Graphics2D g2d = (Graphics2D) graphics;
				g2d.translate (pageFormat.getImageableX (), pageFormat.getImageableY ());
				g2d.setPaint (Color.black);
				Rectangle2D.Double borda =
					new Rectangle2D.Double (0,0, pageFormat.getImageableWidth (), pageFormat.getImageableHeight ());
				g2d.draw (borda);
			}

            /* Define a fonte e o tamanho indicados no modelo */
            Font fonte = new Font (this.modelLabel.getFontName(), Font.PLAIN, this.modelLabel.getFontSize());
            graphics.setFont (fonte);
            
            /* Calcula qual será a primeira etiqueta da atual página */
            int firstLabelOfPage = pageIndex * (this.modelLabel.getColumnsLabel() * this.modelLabel.getLinesLabel());
            
            /* Verifica se ainda tem etiquetas pra imprimir */
            if(!(firstLabelOfPage < this.labels.size()))
            	return Printable.NO_SUCH_PAGE;
            
            /* Imprime as etiquetas: primeira linha, depois segunda linha completa */
            for(int linha = 0; linha < this.modelLabel.getLinesLabel(); linha++){
            	/* Calcula a posição y da etiqueta baseado na atual linha e altura e distancia vertical */
        		int y = linha * Math.round((this.modelLabel.getLabelHeight() +  this.modelLabel.getVerticalDistance()) * CMM_PIXEL_FACTOR);
            	/* Imprime as etiquetas de todas as volunas da atual linha */
        		for(int coluna = 0; coluna < this.modelLabel.getColumnsLabel(); coluna++){
            		/* Verifica se já imprimiu todas as etiquetas e ainda tem página em branco */
        			if(firstLabelOfPage < this.labels.size()){
        				
        				/* Calcula a posição x da etiqueta baseado na atual coluna e largura e distancia horizontal */
            			int x = coluna * Math.round((this.modelLabel.getLabelWidth() +  this.modelLabel.getHorizontalDistance()) * CMM_PIXEL_FACTOR);
            			
            			/* Desenha a área da etiqueta */
            			if(this.printRules){
            			   ((Graphics2D)graphics).draw (new Rectangle2D.Double (x, y, this.modelLabel.getLabelWidth()*CMM_PIXEL_FACTOR, this.modelLabel.getLabelHeight()*CMM_PIXEL_FACTOR));
            			}

            			/* Pega a atual etiqueta e já aponta para a próxima. Cada linha impressa incrementa o tamanho da fonte. */
            			AddressLabel label = labels.get(firstLabelOfPage++);
            			graphics.drawString(label.getLine1(), x, y += this.modelLabel.getFontSize());
            			graphics.drawString(label.getLine2(), x, y += this.modelLabel.getFontSize());
            			graphics.drawString(label.getLine3(), x, y += this.modelLabel.getFontSize());
            			graphics.drawString(label.getLine4(), x, y += this.modelLabel.getFontSize());
            			graphics.drawString(label.getLine5(), x, y += this.modelLabel.getFontSize());

                		/* Prepara a altura do y para a próxima etiqueta da mesma linha, mas da próxima coluna */
            			y = linha * Math.round((this.modelLabel.getLabelHeight() +  this.modelLabel.getVerticalDistance()) * CMM_PIXEL_FACTOR);
            		}
            	}
            }

            /* Indica que a página está completa e pode ser impressa */
            return Printable.PAGE_EXISTS;
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        //System.out.println("Float to int - " + Math.round(80));
		
		ModelLabel modelo1 = new ModelLabel();
		modelo1.setPageWidth(new Float(19));
		modelo1.setPageHeight(new Float(20));
		modelo1.setEnvelope(false);

		modelo1.setFontName("SanSerif");
		modelo1.setFontSize(10);
		
		modelo1.setMarginLeft(new Float(1));
		modelo1.setMarginTop(new Float(1));
		modelo1.setLabelWidth(new Float(8));
		modelo1.setLabelHeight(new Float(3));
		modelo1.setHorizontalDistance(1);
		modelo1.setVerticalDistance(1);

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
		
		for(int i=0;i<21;i++)
			etiquetas.add(endereco);
		try {
//			System.out.println(PrintUtils.retrievePrinters().get(3).getLabel());
//			PrintLabelJavaApi.print(etiquetas, modelo1, 3);
			//////////////////////////////////////////////////////////////////////
			PrintLabelJavaApi.print(etiquetas, modelo1, PrintUtils.DEFAULT_PRINTER_INDEX);
	        /////////////////////////////////////////////////////////////////////////////////////
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
