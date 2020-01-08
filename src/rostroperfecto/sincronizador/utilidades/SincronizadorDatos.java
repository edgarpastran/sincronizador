package rostroperfecto.sincronizador.utilidades;

import java.util.StringTokenizer;

import rostroperfecto.sincronizador.modelo.auxiliares.PropiedadesConfiguracion;

public class SincronizadorDatos {

	private Logger logger;
	private SentenciasSQL sentenciasSQL;	
	
	private static SincronizadorDatos instancia;		
	public static SincronizadorDatos getInstancia() {
		if (instancia == null) {
			instancia = new SincronizadorDatos();
		}
		return instancia;
	}
	
	private SincronizadorDatos() {
		this.logger = Logger.getInstancia();
		this.sentenciasSQL = SentenciasSQL.getInstancia();
	}
	
	private boolean actualizarLocalmenteRegistrosInsertadosEnElServidor(String tabla) {
		this.logger.writeInfoLog("Consultando en el servidor los registros insertados de la tabla "+tabla);
		String respuesta = "";
		PeticionPost peticionPost = null;
		String URLWeb = PropiedadesConfiguracion.getURLWeb();
		String consultaString = tabla+this.sentenciasSQL.condicionCodigoSucursal()+";";
		try {
			peticionPost = new PeticionPost(URLWeb);
			peticionPost.add("consultaString", consultaString);
			respuesta = peticionPost.getRespueta();
			if (this.isNumber(respuesta)) {
				PropiedadesConfiguracion.setUltimoValorSincronizadoTabla(tabla, respuesta);
				this.logger.writeSuccesfulLog("TOTAL: "+respuesta+" registros insertados en el servidor previamente");
				return true;
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			this.logger.writeErrorLog(e.getMessage());
		}
		return false;
	}
	
	public String insertarRegistrosNuevos(String tabla) {
		this.logger.writeInfoLog("Intentando insertar en el servidor nuevos registros de la tabla "+tabla);
		String ultimoValorSincronizadoPrevioString = PropiedadesConfiguracion.getUltimoValorSincronizadoTabla(tabla);
		String ultimoValorSincronizadoPosteriorString = ultimoValorSincronizadoPrevioString;
		if (actualizarLocalmenteRegistrosInsertadosEnElServidor(tabla)) {
			ultimoValorSincronizadoPrevioString = PropiedadesConfiguracion.getUltimoValorSincronizadoTabla(tabla);
			ultimoValorSincronizadoPosteriorString = ultimoValorSincronizadoPrevioString;
			String inicioSentenciasString = tabla+this.sentenciasSQL.condicionCodigoSucursal()+";"; 
			String sentenciasString = inicioSentenciasString;
			sentenciasString += this.sentenciasSQL.sentenciaParaInsertarRegistros(tabla);
			boolean conexionAlServidor = true;
			while(!sentenciasString.equals(inicioSentenciasString) && conexionAlServidor) {
				String respuesta = ultimoValorSincronizadoPrevioString;
				PeticionPost peticionPost = null;
				String URLWeb = PropiedadesConfiguracion.getURLWeb();
				try {
					peticionPost = new PeticionPost(URLWeb);
					peticionPost.add("sentenciasString", sentenciasString);
					respuesta = peticionPost.getRespueta();
				} 
				catch (Exception e) {
					e.printStackTrace();
					conexionAlServidor = false;
					this.logger.writeErrorLog(e.getMessage());
				}
				if (conexionAlServidor) {
					ultimoValorSincronizadoPosteriorString = respuesta;
					if (this.isNumber(respuesta)) {					
						StringTokenizer tokens = new StringTokenizer(sentenciasString, ";");
						int cantidadDeInserts = tokens.countTokens()-1;
						this.logger.writeSuccesfulLog(cantidadDeInserts+" nuevos registros insertados en el servidor");
						PropiedadesConfiguracion.setUltimoValorSincronizadoTabla(tabla, respuesta);
					}
					else {
						this.logger.writeErrorLog(respuesta);
						conexionAlServidor = false;
					}
				}			
				sentenciasString = inicioSentenciasString;
				sentenciasString += this.sentenciasSQL.sentenciaParaInsertarRegistros(tabla);
			}
		}
		Integer nuevosRegistrosInsertados = 0;
		if (!this.isNumber(ultimoValorSincronizadoPosteriorString)) {			
			return ultimoValorSincronizadoPosteriorString;
		}
		else {
			try {
				int ultimoValorSincronizadoPrevio = Integer.parseInt(ultimoValorSincronizadoPrevioString);
				int ultimoValorSincronizadoPosterior = Integer.parseInt(ultimoValorSincronizadoPosteriorString);
				nuevosRegistrosInsertados = ultimoValorSincronizadoPosterior - ultimoValorSincronizadoPrevio;
				this.logger.writeSuccesfulLog("TOTAL: "+nuevosRegistrosInsertados+" nuevos registros insertados en el servidor\n");
			}
			catch(NumberFormatException e) {
				e.printStackTrace();
			}			
			return nuevosRegistrosInsertados.toString();
		}		
	}
	
	private boolean isNumber(String cadena) {
		if (cadena.length() <= 0) {
			return false;
		}
		for (int i=0; i<cadena.length(); i++) {
			if (!Character.isDigit(cadena.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public String actualizarRegistros(String tabla) {
		this.logger.writeInfoLog("Intentando actualizar en el servidor los registros de la tabla "+tabla);
		String respuesta = "0";
		int cantidadDeUpdates = 0;
		String inicioSentenciasString = tabla+this.sentenciasSQL.condicionCodigoSucursal()+";"; 
		String sentenciasString = inicioSentenciasString;
		sentenciasString += this.sentenciasSQL.sentenciaParaActualizarRegistros(tabla, cantidadDeUpdates);
		boolean conexionAlServidor = true;
		while(!sentenciasString.equals(inicioSentenciasString) && conexionAlServidor) {
			PeticionPost peticionPost = null;
			String URLWeb = PropiedadesConfiguracion.getURLWeb();
			try {
				peticionPost = new PeticionPost(URLWeb);
				peticionPost.add("sentenciasString", sentenciasString);
				respuesta = peticionPost.getRespueta();
			} 
			catch (Exception e) {
				e.printStackTrace();
				conexionAlServidor = false;
				this.logger.writeErrorLog(e.getMessage());
			}
			if (this.isNumber(respuesta)) {				
				StringTokenizer tokens = new StringTokenizer(sentenciasString, ";");
				int nuevosUpdates = tokens.countTokens()-1;
				cantidadDeUpdates += nuevosUpdates;
				if (Integer.parseInt(respuesta) > 0) {
					this.logger.writeSuccesfulLog(nuevosUpdates+" viejos registros actualizados en el servidor");
				}
				sentenciasString = inicioSentenciasString;
				sentenciasString += this.sentenciasSQL.sentenciaParaActualizarRegistros(tabla, cantidadDeUpdates);
			}
			else {
				conexionAlServidor = false;
				this.logger.writeErrorLog(respuesta);
			}
		}
		if (this.isNumber(respuesta) && Integer.parseInt(respuesta) > 0) {
			this.logger.writeSuccesfulLog("TOTAL: "+cantidadDeUpdates+" viejos registros actualizados en el servidor\n");
		}
		return respuesta;
	}
}
