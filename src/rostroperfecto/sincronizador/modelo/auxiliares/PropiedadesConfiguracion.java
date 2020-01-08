package rostroperfecto.sincronizador.modelo.auxiliares;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class PropiedadesConfiguracion {

	private enum PROPIEDADES_TABLA {CLAVE_PRIMARIA, ULTIMO_VALOR_SINCRONIZADO, FECHA_ULTIMA_SINCRONIZACION, HORA_ULTIMA_SINCRONIZACION, REGISTROS_ACTUALIZABLES}
	private static String ARCHIVO_PROPERTIES = "sincronizador.properties";
	
	private static Properties cargarArchivoProperties() {
		Properties propiedades = null;
		File archivoProperties = new File(ARCHIVO_PROPERTIES);
		if (archivoProperties.exists()) {
			propiedades = new Properties();
			try {
				propiedades.load(new FileInputStream(archivoProperties));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return propiedades;
	}

	private static String getVariable(String nombreVariable) {
		Properties propiedades = cargarArchivoProperties();
		if (propiedades != null) {
			return propiedades.getProperty(nombreVariable);
		}
		return null;
	}

	private static boolean setVariable(String nombreVariable, String valor) {
		Properties propiedades = cargarArchivoProperties();
		if (propiedades != null) {
			propiedades.setProperty(nombreVariable, valor);
			try {
				FileOutputStream os = new FileOutputStream(ARCHIVO_PROPERTIES);
				propiedades.store(os, ARCHIVO_PROPERTIES);
				return true;
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String getURLWeb() {
		String URLWeb = getVariable("URL_WEB");
		return URLWeb;
	}
	
	public static String getDirectorioInstalacion() {
		String directorioInstalacion = getVariable("DIRECTORIO_INSTALACION");
		return directorioInstalacion;
	}
	
	public static String getSubDirectorioDBF() {
		String subDirectorioDBF = getVariable("SUBDIRECTORIO_DBF");
		return subDirectorioDBF;
	}

	public static String getCodigoSucursal() {
		String codigoSucursal = getVariable("CODIGO_SUCURSAL");
		return codigoSucursal;
	}
	
	public static List<String> getHorasSincronizacion() {
		String horasString = getVariable("HORAS_SINCRONIZACION");
		StringTokenizer tokens = new StringTokenizer(horasString, ",");
		List<String> horasSincronizacion = new ArrayList<String>();
		while(tokens.hasMoreTokens()){
			horasSincronizacion.add(tokens.nextToken().trim());
		}
		return horasSincronizacion;
	}
	
	public static boolean setHorasSincronizacion(String valor) {
		return setVariable("HORAS_SINCRONIZACION", valor);
	}
	
	public static List<String> getTablas() {
		String tablasString = getVariable("TABLAS");
		StringTokenizer tokens = new StringTokenizer(tablasString, ",");
		List<String> tablas = new ArrayList<String>();
		while(tokens.hasMoreTokens()){
			tablas.add(tokens.nextToken().trim());
		}
		return tablas;
	}
	
	private static List<String> getPropiedadesTabla(String nombreTabla) {
		String propiedadesTablaString = getVariable(nombreTabla);
		StringTokenizer tokens = new StringTokenizer(propiedadesTablaString, ",");
		List<String> propiedadesTabla = new ArrayList<String>();
		while(tokens.hasMoreTokens()){
			propiedadesTabla.add(tokens.nextToken().trim());
		}
		return propiedadesTabla;
	}
	
	private static List<String> getPropiedadTabla(String nombreTabla, Enum propiedad) {		
		List<String> propiedadesTabla = getPropiedadesTabla(nombreTabla);		
		String propiedadTablaString = "";
		if ((propiedadesTabla != null) && (propiedadesTabla.size() > 0)){
			if (propiedad.equals(PROPIEDADES_TABLA.CLAVE_PRIMARIA)) {
				propiedadTablaString = (propiedadesTabla.get(0)==null?"":propiedadesTabla.get(0));
			}
			else if (propiedad.equals(PROPIEDADES_TABLA.ULTIMO_VALOR_SINCRONIZADO)) {
				propiedadTablaString = (propiedadesTabla.get(1)==null?"":propiedadesTabla.get(1));
			}
			else if (propiedad.equals(PROPIEDADES_TABLA.FECHA_ULTIMA_SINCRONIZACION)) {
				propiedadTablaString = (propiedadesTabla.get(2)==null?"":propiedadesTabla.get(2));
			}
			else if (propiedad.equals(PROPIEDADES_TABLA.HORA_ULTIMA_SINCRONIZACION)) {
				propiedadTablaString = (propiedadesTabla.get(3)==null?"":propiedadesTabla.get(3));
			}
			else if (propiedad.equals(PROPIEDADES_TABLA.REGISTROS_ACTUALIZABLES)) {
				propiedadTablaString = (propiedadesTabla.get(4)==null?"":propiedadesTabla.get(4));
			}
		}
		StringTokenizer tokens = new StringTokenizer(propiedadTablaString, "+");
		List<String> propiedadTabla = new ArrayList<String>();
		while(tokens.hasMoreTokens()){
			propiedadTabla.add(tokens.nextToken().trim());
		}
		return propiedadTabla;
	}
	
	private static void setPropiedadTabla(String nombreTabla, Enum propiedad, String valorPropiedad) {
		List<String> propiedadesTabla = getPropiedadesTabla(nombreTabla);
		if ((propiedadesTabla != null) && (propiedadesTabla.size() > 0)){
			if (propiedad.equals(PROPIEDADES_TABLA.CLAVE_PRIMARIA)) {
				propiedadesTabla.set(0, valorPropiedad);
			}
			else if (propiedad.equals(PROPIEDADES_TABLA.ULTIMO_VALOR_SINCRONIZADO)) {
				propiedadesTabla.set(1, valorPropiedad);
			}
			else if (propiedad.equals(PROPIEDADES_TABLA.FECHA_ULTIMA_SINCRONIZACION)) {
				propiedadesTabla.set(2, valorPropiedad);
			}
			else if (propiedad.equals(PROPIEDADES_TABLA.HORA_ULTIMA_SINCRONIZACION)) {
				propiedadesTabla.set(3, valorPropiedad);
			}
			else if (propiedad.equals(PROPIEDADES_TABLA.REGISTROS_ACTUALIZABLES)) {
				propiedadesTabla.set(4, valorPropiedad);
			}
		}
		String propiedadesTablaString = "";
		for (Iterator<String> iter=propiedadesTabla.iterator(); iter.hasNext();) {
			propiedadesTablaString += iter.next();
			if (iter.hasNext()) {
				propiedadesTablaString += ",";
			}
		}
		setVariable(nombreTabla, propiedadesTablaString);
	}
	
	public static List<String> getClavePrimariaTabla(String nombreTabla) {
		List<String> camposList = getPropiedadTabla(nombreTabla, PROPIEDADES_TABLA.CLAVE_PRIMARIA);
		List<String> clavePrimaria = new ArrayList<String>();
		if ((camposList != null) && (camposList.size()>0)) {
			for (int i=0; i<camposList.size(); i++) {
				clavePrimaria.add(camposList.get(i).toLowerCase());
			}
		}
		return clavePrimaria;
	}
	
	public static String getUltimoValorSincronizadoTabla(String nombreTabla) {
		List<String> valorList = getPropiedadTabla(nombreTabla, PROPIEDADES_TABLA.ULTIMO_VALOR_SINCRONIZADO);
		String valor = "0";
		if ((valorList != null) && (valorList.size()>0)) {
			valor = valorList.get(0);
		}
		return valor;
	}
	
	public static void setUltimoValorSincronizadoTabla(String nombreTabla, String valor) {
		setPropiedadTabla(nombreTabla, PROPIEDADES_TABLA.ULTIMO_VALOR_SINCRONIZADO, valor);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");
		String fecha =  simpleDateFormat.format(new Date());				
		setPropiedadTabla(nombreTabla, PROPIEDADES_TABLA.FECHA_ULTIMA_SINCRONIZACION, fecha);
		simpleDateFormat.applyPattern("HH:mm");
		String hora =  simpleDateFormat.format(new Date());				
		setPropiedadTabla(nombreTabla, PROPIEDADES_TABLA.HORA_ULTIMA_SINCRONIZACION, hora);
	}
	
	public static String getFechaUltimaSincronizacionTabla(String nombreTabla) {
		List<String> fechaList = getPropiedadTabla(nombreTabla, PROPIEDADES_TABLA.FECHA_ULTIMA_SINCRONIZACION);
		String fecha = null;
		if ((fechaList != null) && (fechaList.size()>0)) {
			fecha = fechaList.get(0);
		}
		return fecha;
	}
	
	public static String getHoraUltimaSincronizacionTabla(String nombreTabla) {
		List<String> horaList = getPropiedadTabla(nombreTabla, PROPIEDADES_TABLA.HORA_ULTIMA_SINCRONIZACION);
		String hora = null;
		if ((horaList != null) && (horaList.size()>0)) {
			hora = horaList.get(0);
		}
		return hora;
	}
	
	public static Boolean isRegistrosActualizables(String nombreTabla) {
		List<String> registrosActualizablesList = getPropiedadTabla(nombreTabla, PROPIEDADES_TABLA.REGISTROS_ACTUALIZABLES);
		Boolean registrosActualizables = false;
		if ((registrosActualizablesList != null) && (registrosActualizablesList.size()>0) && (registrosActualizablesList.get(0).equals("true"))) {
			registrosActualizables = true;
		}
		return registrosActualizables;
	}
	
}
