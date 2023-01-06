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
 * Classe utilitária para manipulação e escolha de impressoras.
 * @author Lucio
 * @version 20060618
 */
public class PrintUtils {

    public static int DEFAULT_PRINTER_INDEX = -1;
    /* Permite definir que nada será impresso */
    public static int PRINTER_INDEX_NO_PRINT = -2;
    
    
	/**
     * Recebe um índice de um impressora e obtem o
     * serviço de impressão com o índice correspondente.<br>
     * Verifica se o índice passado é PRINTER_INDEX_DEFAULT
     * para pegar a impressora definida como padrão no sistema
     */
    public static PrintService getPrintService(int printerIndex) {
		if(printerIndex==DEFAULT_PRINTER_INDEX)
        	return PrintServiceLookup.lookupDefaultPrintService();
        
		/* Obtem a lista de todas as impressoras disponíveis no sistema */
		PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
		
		/* Verifica se o índice da impressora é válido */
		if(((services.length-1)<printerIndex) || (printerIndex == PRINTER_INDEX_NO_PRINT))
			throw new  RuntimeException("Não há nenhum serviço de impressão para a impressora de índice " + printerIndex + ".");

        return services[printerIndex];
    }

	/**
     * Recebe um índice de um impressora e cria um
     * trabalho de impressão para a impressora de índice correspondente.<br>
     * Verifica se o índice passado é PRINTER_INDEX_DEFAULT
     * para pegar a impressora definida como padrão no sistema
     */
    public static DocPrintJob createDocPrintJob(int printerIndex, String jobName) {
    	DocPrintJob result = getPrintService(printerIndex).createPrintJob();
// Set READONLY    	result.getAttributes().add(new JobName(jobName, Locale.getDefault()));
    	return result;
    }

    /**
     * Cria uma lista de opções de impressoras disponíveis no computador local.<br>
     * Como padrão, escolhe a impressora default. No entanto, em alguns sistemas,
     * pode não haver uma impressora default definida. Assim, este método
     * escolherá a primeira da lista, se houver.
     */
    public static List<SelectItem> retrievePrinters() {
    	List<SelectItem> result = new ArrayList<SelectItem>();
    	
    	/* Obtem a impressora padrão e coloca a impressora padrão no início da lista
    	 * Lucio = 22/11/207: Pode não haver defaultPrinter, o que retornará um null */
    	PrintService printDefaultService = PrintServiceLookup.lookupDefaultPrintService();
    	
    	/* Verifica se achou alguma impressora instalada */
    	if(printDefaultService != null){
    		result.add(new SelectItem(PRINTER_INDEX_NO_PRINT, "Não enviar para impressora"));
    		result.add(new SelectItem(DEFAULT_PRINTER_INDEX, printDefaultService.getName()));
    	}else
    		/* Não pode ficar sem item de escolha */
    		result.add(new SelectItem(DEFAULT_PRINTER_INDEX, "(Nenhuma impressora instalada no servidor ou sem impressora padrão definida)"));
    	
		int printerIndex = 0;
		PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
		for(PrintService printService: services){
			/* Adiciona todas as impressoras, menos a padrão que já foi adicionada */
			/* Lucio 02/06/08: printDefaultService pode ser null. Colocada o null safe*/
			if((printDefaultService != null) && !printService.getName().equals(printDefaultService.getName()))
				result.add(new SelectItem(printerIndex, printService.getName()));
				
			printerIndex++;
		}
		
		return result;
    }
    
    /**
     * Imprime um documento do Jasper, já compilador e preenchido na impressora definida pelo 
     * printerIndex.<br>
     * @throws JRException 
     */
    public static void printJasper(JasperPrint print, int printerIndex) throws JRException {
    	
		PrintService service;
    	/* Se o índice da impressora é NO_PRINT, não imprime nada*/
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
     * Imprime um documento do Jasper, já compilador e preenchido em um Stream PDF.<br>
     * @throws JRException 
     * @author lucio 20080611
     */
    public static void printJasper(JasperPrint print, OutputStream outputStream) throws JRException {
    	if(outputStream != null)
    		JasperExportManager.exportReportToPdfStream(print, outputStream);
    }
    
    /**
     * Imprime um documento do Jasper, já compilador e preenchido na impressora definida pelo 
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
