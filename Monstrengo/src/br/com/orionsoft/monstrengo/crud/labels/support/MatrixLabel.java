package br.com.orionsoft.monstrengo.crud.labels.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;

import org.apache.commons.lang.StringUtils;

import br.com.adilson.util.PrinterMatrix;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabel;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabel;

public class MatrixLabel {
	
	private static void print(PrinterMatrix printer, int printerIndex){
		
		/* Cria o buffer de memória que receberá o(s) cheque(s) impressos 1440 = 18Lin X 80Col = 1 cheque */
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(1440);
		/* Prepara a interface que irá escrever no buffer criado logo acima */
		PrintStream ps = new PrintStream(buffer);
		/* Escreve o(s) cheque(s) no buffer */
		printer.toPrintStream(ps);
		
		/* Cria o documento de impressão usando o buffer preenchido logo acima */
		SimpleDoc doc = new SimpleDoc(new ByteArrayInputStream(buffer.toByteArray()), DocFlavor.INPUT_STREAM.AUTOSENSE, null);
		/* Prepara alguns atributos de impressão do trabalho de impressão */
		PrintRequestAttributeSet att = new  HashPrintRequestAttributeSet();
		att.add(MediaSizeName.PERSONAL_ENVELOPE);
		
//		System.out.println(buffer);
		try {
			/* Seleciona a impressora de acordo com o índice passado e já cria um trabalho de impressão pra ela */
			DocPrintJob job = PrintUtils.createDocPrintJob(printerIndex, "Etiquetas " + CalendarUtils.formatDate(Calendar.getInstance()));
			
			job.print(doc, att);
		} catch (PrintException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void print(List<AddressLabel> etiquetas, ModelLabel modelo, int printerIndex){
		/* Verifica se a lista de etiqueta não está vazia */
		if(!etiquetas.isEmpty()){

			/*  Preenche o etiqueta */
			PrinterMatrix printer = new PrinterMatrix();
			
			/* Prepara a dimensão do buffer da(s) folha(s) do(s) etiqueta(s).
			 * Utiliza a margem esquerda + o numero de colunas * (larguraEtiqueta+distanciaHorizontal) */
			int larguraFolha = Math.round(modelo.getMarginLeft() + 
					                     modelo.getColumnsLabel() * 
					                      (modelo.getLabelWidth() +
					                       modelo.getHorizontalDistance()
					                      )
					                    );
			
			/* Verificando quatas etiquetas estão selecionadas para criar a página */
			int selected = 0;
			for(AddressLabel etiqueta: etiquetas)
				if(etiqueta.isPrint())
					selected++;
			
			/* Utiliza a margem superior + o numero de etiquetas * (larguraEtiqueta+distanciaHorizontal) */
			int alturaFolha = Math.round(modelo.getMarginTop() + 
										 Math.round(new Float(selected) / modelo.getColumnsLabel() + 0.4) * 
										 (modelo.getLabelHeight() + modelo.getVerticalDistance()
										 )
									    );

			printer.setOutSize(alturaFolha, larguraFolha);
			
			int deltaCol = 1+Math.round(modelo.getMarginLeft());
			int deltaLin =  1+Math.round(modelo.getMarginTop());
			int etiquetaAtual = 0;
			for(AddressLabel etiqueta: etiquetas){
				if(etiqueta.isPrint()){
					/*  Preenche o etiqueta */
					printer.printTextLinCol(deltaLin, deltaCol, StringUtils.substring(etiqueta.getLine1(), 0, Math.round(modelo.getLabelWidth())));
					printer.printTextLinCol(deltaLin+1, deltaCol, StringUtils.substring(etiqueta.getLine2(), 0, Math.round(modelo.getLabelWidth())));
					printer.printTextLinCol(deltaLin+2, deltaCol, StringUtils.substring(etiqueta.getLine3(), 0, Math.round(modelo.getLabelWidth())));
					printer.printTextLinCol(deltaLin+3, deltaCol, StringUtils.substring(etiqueta.getLine4(), 0, Math.round(modelo.getLabelWidth())));
					printer.printTextLinCol(deltaLin+4, deltaCol, StringUtils.substring(etiqueta.getLine5(), 0, Math.round(modelo.getLabelWidth())));
					
					// Verifica se deltaCol + a largura da proxima etiqueta vai ultrapassar a largura da pagina
					if (deltaCol+(modelo.getLabelWidth()*2) > larguraFolha){
						// SIM: reinicia deltaCol e incrementa deltaRow
						deltaCol = 1+Math.round(modelo.getMarginLeft());
						deltaLin = deltaLin + Math.round(modelo.getLabelHeight()+modelo.getVerticalDistance());
					}else 
						// NAO: incrementa delta
						deltaCol = deltaCol + Math.round(modelo.getLabelWidth()+modelo.getHorizontalDistance());
				}
				etiquetaAtual++;
			}
			
//			printer.show();
			print(printer, printerIndex);
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
		modelo1.setPageWidth(0);
		modelo1.setPageHeight(0);
		
		modelo1.setMarginTop(0);
		modelo1.setMarginLeft(2);
		modelo1.setLabelWidth(34);
		modelo1.setLabelHeight(5);
		modelo1.setHorizontalDistance(3);
		modelo1.setVerticalDistance(1);
		
		ModelLabel modelo2 = new ModelLabel();
		modelo2.setPageWidth(0);
		modelo2.setPageHeight(0);
		
		modelo2.setMarginTop(0);
		modelo2.setMarginLeft(0);
		modelo2.setLabelWidth(35);
		modelo2.setLabelHeight(5);
		modelo2.setHorizontalDistance(0);
		modelo2.setVerticalDistance(1);
		
		
		AddressLabel endereco = new AddressLabel();
		endereco.setLine1("Lucio Geronimo Valentin Pereira Ltda");
		endereco.setLine2("Lucio Geronimo Valentin Pereira");
		endereco.setLine3("Rua Cezar Lattes, 263");
		endereco.setLine4("Casa / Jardim Alvorada");
		endereco.setLine5("CEP: 87.035-070 - Maringá/Pr ");
		
		List<AddressLabel> etiquetas = new ArrayList<AddressLabel>();
		
		for(int i=0;i<3;i++)
			etiquetas.add(endereco);
		MatrixLabel.print(etiquetas, modelo1, 1);
	}
	
}
