package rostroperfecto.sincronizador.utilidades;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
 /**
  * @web http://www.jc-mouse.net
  * @author Mouse
  */
public class ControladorEjecucionesSimultaneas {

    //fichero TMP
    private String appPath = System.getProperties().getProperty("user.dir");
    private File fichero = new File( appPath + "\\sincronizador.tmp");    
    //tiempo en que se actualiza el fichero TMP
    private int segundos = 20;

    /** Constructor de clase */
    public ControladorEjecucionesSimultaneas() {
    	
    };

    /**
	 * Comprueba que archivo TMP exista, sino lo crea e inicia valores
	 */
    public boolean comprobar() {           
        if (fichero.exists()) {            
            long res = restarTiempo();            
            if( res < segundos ) {              
                JOptionPane.showMessageDialog(null,"Error: La aplicacion ya esta en ejecución.");
                return false;
            }
            else {
                programarTarea();
                return true;
            }
        }
        else { // no existe fichero 
            crearTMP();   
            programarTarea();
            return true;
        }
    }

    /**
	 * Programa un proceso que se repite cada cierto tiempo
	 */
    private void programarTarea() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate( 
            new Runnable() {
                @Override
                public void run() {                   
                    crearTMP(); 
                }
              }, 1000, segundos * 1000 , TimeUnit.MILLISECONDS ); //comienza dentro de 1 segundo y luego se repite cada N segundos
    }

    /**
	 * Crea un archivo TMP con un unico valor, el tiempo en milisegundos
	 */
    public void crearTMP() {
        Date fecha=new Date();        
        try {            
            BufferedWriter writer = new BufferedWriter(new FileWriter( fichero ));                        
            writer.write(  String.valueOf( fecha.getTime() ) );                        
            writer.close();            
        } 
        catch (IOException e) {
            System.err.println( e.getMessage() );
        }
    }

	/**
	 * Lee el archivo TMP y retorna su valor 
	 * @return LONG cantidad de milisegundos 
	 */
    private long leer() {
        String linea = "0";        
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader( new FileReader( fichero ) );            
            while(bufferedReader.ready()){
            	String aux = bufferedReader.readLine().trim();
            	//System.out.println("'"+aux+"'");
            	if (aux != "" && aux.length() > 0) {
            		linea = aux;
            	}
            }
        }
        catch (IOException e) {
            System.err.println( e.getMessage() );
        }
        return Long.valueOf(linea).longValue();
    }
    
    /**
	 * Resta el tiempo expresado en segundos
	 * @return tiempo el resultado expresado en segundos
	 */
    private long restarTiempo() {
    	long tiempoTMP = leer();    	
        Date date = new Date();        
        long tiempoActual = date.getTime();        
        long tiempo = (tiempoActual - tiempoTMP)/1000;
        //System.out.println((tiempoActual/1000)+" - "+ (tiempoTMP/1000)+" = "+tiempo);
        return tiempo;
    }

    /**
	 * Elimina el fichero TMP si es que existe
	 */
    public void cerrarApp() {   
        if ( fichero.exists() ) { 
        	fichero.delete(); 
    	}
        System.exit(0);
    }

}//--> fin clase