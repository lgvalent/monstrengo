package br.com.orionsoft.monstrengo.core.util;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

/*
 * Created on 22/12/2005
 *
 */

/**
 * Classe utilit�ria para manipula��o e escolha de impressoras.
 * @author Lucio
 * @version 20060618
 */
public class PrintUtils {

    public static int DEFAULT_PRINTER_INDEX = -1;
    /* Permite definir que nada ser� impresso */
    public static int PRINTER_INDEX_NO_PRINT = -2;
    
    
	/**
     * Recebe um �ndice de um impressora e obtem o
     * servi�o de impress�o com o �ndice correspondente.<br>
     * Verifica se o �ndice passado � PRINTER_INDEX_DEFAULT
     * para pegar a impressora definida como padr�o no sistema
     */
    public static PrintService getPrintService(int printerIndex) {
		if(printerIndex==DEFAULT_PRINTER_INDEX)
        	return PrintServiceLookup.lookupDefaultPrintService();
        
		/* Obtem a lista de todas as impressoras dispon�veis no sistema */
		PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
		
		/* Verifica se o �ndice da impressora � v�lido */
		if(((services.length-1)<printerIndex) || (printerIndex == PRINTER_INDEX_NO_PRINT))
			throw new  RuntimeException("N�o h� nenhum servi�o de impress�o para a impressora de �ndice " + printerIndex + ".");

        return services[printerIndex];
    }

	/**
     * Recebe um �ndice de um impressora e cria um
     * trabalho de impress�o para a impressora de �ndice correspondente.<br>
     * Verifica se o �ndice passado � PRINTER_INDEX_DEFAULT
     * para pegar a impressora definida como padr�o no sistema
     */
    public static DocPrintJob createDocPrintJob(int printerIndex, String jobName) {
    	DocPrintJob result = getPrintService(printerIndex).createPrintJob();
// Set READONLY    	result.getAttributes().add(new JobName(jobName, Locale.getDefault()));
    	return result;
    }

    /**
     * Cria uma lista de op��es de impressoras dispon�veis no computador local.<br>
     * Como padr�o, escolhe a impressora default. No entanto, em alguns sistemas,
     * pode n�o haver uma impressora default definida. Assim, este m�todo
     * escolher� a primeira da lista, se houver.
     */
    public static List<SelectItem> retrievePrinters() {
    	List<SelectItem> result = new ArrayList<SelectItem>();
    	
    	/* Obtem a impressora padr�o e coloca a impressora padr�o no in�cio da lista
    	 * Lucio = 22/11/207: Pode n�o haver defaultPrinter, o que retornar� um null */
    	PrintService printDefaultService = PrintServiceLookup.lookupDefaultPrintService();
    	
    	/* Verifica se achou alguma impressora instalada */
    	if(printDefaultService != null){
    		result.add(new SelectItem(PRINTER_INDEX_NO_PRINT, "N�o enviar para impressora"));
    		result.add(new SelectItem(DEFAULT_PRINTER_INDEX, printDefaultService.getName()));
    	}else
    		/* N�o pode ficar sem item de escolha */
    		result.add(new SelectItem(DEFAULT_PRINTER_INDEX, "(Nenhuma impressora instalada no servidor ou sem impressora padr�o definida)"));
    	
		int printerIndex = 0;
		PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
		for(PrintService printService: services){
			/* Adiciona todas as impressoras, menos a padr�o que j� foi adicionada */
			/* Lucio 02/06/08: printDefaultService pode ser null. Colocada o null safe*/
			if((printDefaultService != null) && !printService.getName().equals(printDefaultService.getName()))
				result.add(new SelectItem(printerIndex, printService.getName()));
				
			printerIndex++;
		}
		
		return result;
    }
    
    /**
     * Imprime um documento do Jasper, j� compilador e preenchido na impressora definida pelo 
     * printerIndex.<br>
     * @throws JRException 
     */
    public static void printJasper(JasperPrint print, int printerIndex) throws JRException {
    	
		PrintService service;
    	/* Se o �ndice da impressora � NO_PRINT, n�o imprime nada*/
		if(printerIndex==PRINTER_INDEX_NO_PRINT)
    		return;
    	
       	if(printerIndex==DEFAULT_PRINTER_INDEX)
        	service = PrintServiceLookup.lookupDefaultPrintService();
    	else
    		service = PrintServiceLookup.lookupPrintServices(null, null)[printerIndex];

		PrintServiceAttributeSet serviceAttributeSet = new HashPrintServiceAttributeSet();
    	serviceAttributeSet.add(new PrinterName(service.getName(), null)); 

    	JRPrintServiceExporter exp = new JRPrintServiceExporter();
    	exp.setParameter(JRPrintServiceExporterParameter.JASPER_PRINT, print);
    	exp.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, serviceAttributeSet);
    	exp.exportReport();
    }
    
    /**
     * Imprime um documento do Jasper, j� compilador e preenchido em um Stream PDF.<br>
     * @throws JRException 
     * @author lucio 20080611
     */
    public static void printJasper(JasperPrint print, OutputStream outputStream) throws JRException {
    	if(outputStream != null)
    		JasperExportManager.exportReportToPdfStream(print, outputStream);
    }
    
    /**
     * Imprime um documento do Jasper, j� compilador e preenchido na impressora definida pelo 
     * printerIndex.<br>
     * @throws JRException 
     */
    public static void printJasper(JasperPrint print, PrintRequestAttributeSet printRequestAttributeSet, int printerIndex) throws JRException {
    	
		PrintService service;
    	if(printerIndex==DEFAULT_PRINTER_INDEX)
        	service = PrintServiceLookup.lookupDefaultPrintService();
    	else
    		service = PrintServiceLookup.lookupPrintServices(null, null)[printerIndex];

		PrintServiceAttributeSet serviceAttributeSet = new HashPrintServiceAttributeSet();
    	serviceAttributeSet.add(new PrinterName(service.getName(), null)); 
    	
    	JRPrintServiceExporter exp = new JRPrintServiceExporter();
    	exp.setParameter(JRPrintServiceExporterParameter.JASPER_PRINT, print);
    	exp.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, serviceAttributeSet);
    	exp.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
    	exp.exportReport();
    }
    
}
