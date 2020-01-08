package rostroperfecto.sincronizador.utilidades;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private static final String DIRECTORIO_ARCHIVOS_LOG = System.getProperty("user.dir")+"/logs/";
	private static final String FORMATO_ARCHIVOS_LOG = "YYYY-MM-dd";
	private static final String FORMATO_TIEMPO_LOG = "HH:mm:ss";

	
	private static Logger instancia;
    public static Logger getInstancia() {
		if (instancia == null) {
		    instancia = new Logger();
		}	
		return instancia;
    }
    
    private Logger() {
    	super();
    	try {	
    	    File archivoLog = new File(this.getRutaArchivoLog()); 
    	    if (!archivoLog.exists()) {
    	    	this.crearArchivo();
    	    }
    	}
    	catch (Exception e) {
    	    e.printStackTrace();
    	}
    }
    
    private String getRutaArchivoLog() {
    	String nombreArchivoLog = "";
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.FORMATO_ARCHIVOS_LOG);
    	nombreArchivoLog = this.DIRECTORIO_ARCHIVOS_LOG + simpleDateFormat.format(new Date())+".log";
    	return nombreArchivoLog;    	
    }
    
    private void crearArchivo(){
    	FileWriter fileWriter = null;
    	try {
    	    File directorio = new File(this.DIRECTORIO_ARCHIVOS_LOG);
    	    if (!directorio.exists()) {
    	    	directorio.mkdirs();
    	    }
    	    fileWriter = new FileWriter(this.getRutaArchivoLog());
    	    fileWriter.close();
    	}
    	catch (IOException ex) {
    	    ex.printStackTrace();
    	}
    }
    
    private void writeLog(String informacion) {
    	try {
    		File archivoLog = new File(this.getRutaArchivoLog()); 
    	    if (!archivoLog.exists()) {
    	    	this.crearArchivo();
    	    }    
    	    FileWriter fileWriter = new FileWriter(archivoLog, true);
    	    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    	    PrintWriter printerWriter = new PrintWriter(bufferedWriter);
    	    
    	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.FORMATO_TIEMPO_LOG);
    	    String tiempo = simpleDateFormat.format(new Date());
    	    printerWriter.append(tiempo+" -> "+informacion+"\n");
    		
//    	    printerWriter.flush();
    	    printerWriter.close();
//    	    bufferedWriter.flush();
    	    bufferedWriter.close();
//    	    fileWriter.flush();
    	    fileWriter.close();
    	}
    	catch (Exception e) {
    	    e.printStackTrace();
    	}
    }
    
    public void writeSuccesfulLog(String informacion) {
    	this.writeLog("OK: "+informacion);
    }
    
    public void writeErrorLog(String informacion) {
    	this.writeLog("ERROR: "+informacion);
    }
    
    public void writeInfoLog(String informacion) {
    	this.writeLog("INFO: "+informacion);
    }
    
    public void writeHeaderInfoLog(String tipo) {
    	this.writeLog("-------------------------------------------------------------------------------");
    	this.writeLog("INFO: INICIO DE PROCESO DE SINCRONIZACION DE DATOS "+tipo.toUpperCase());
    	this.writeLog("-------------------------------------------------------------------------------");        
    }
    
    public void writeFooterInfoLog(String tipo) {
    	this.writeLog("-------------------------------------------------------------------------------");
    	this.writeLog("INFO: FIN DE PROCESO DE SINCRONIZACION DE DATOS "+tipo.toUpperCase());
    	this.writeLog("-------------------------------------------------------------------------------\n\n");        
    }
}
