package rostroperfecto.sincronizador.utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rostroperfecto.sincronizador.modelo.auxiliares.PropiedadesConfiguracion;


public class SentenciasSQL {

	private static String TABLA_EMPRESA = "EMPRESA";
	private static String CAMPO_EMPRESA_CONFIG = "config";
	private static String NOMBRE_CAMPO_CODIGO_SUCURSAL = "codsuc";
	private static String TIPO_CAMPO_CODIGO_SUCURSAL = "VARCHAR";
	private static String LONGITUD_CAMPO_CODIGO_SUCURSAL = "8";
	
	private enum TIPOS_DE_DATO {VARCHAR, LONGVARCHAR, NUMERIC, DOUBLE, BOOLEAN, DATE, TIMESTAMP}
	private static int MAXIMO_REGISTROS_INSERTADOS_ACTUALIZADOS = 500;
	
	private static SentenciasSQL instancia;
	
	public static SentenciasSQL getInstancia() {
		if (instancia == null) {
			instancia = new SentenciasSQL();
		}
		return instancia;
	}
	
	private SentenciasSQL() {
		
	}
	
	private Connection getConexionEmpresa() {
		Connection connection = null;
		try {
            Class.forName("com.hxtt.sql.dbf.DBFDriver").newInstance();
            String url = "jdbc:DBF:///"+PropiedadesConfiguracion.getDirectorioInstalacion();
            connection = DriverManager.getConnection(url, "", "");
		}
		catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
		return connection;
	}
	
	private Connection getConexionNoEmpresa() {
		Connection connection = null;
		try {
            Class.forName("com.hxtt.sql.dbf.DBFDriver").newInstance();
            String url = "jdbc:DBF:///"+PropiedadesConfiguracion.getDirectorioInstalacion()+"/"+PropiedadesConfiguracion.getSubDirectorioDBF();
            connection = DriverManager.getConnection(url, "", "");
		}
		catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
		return connection;
	}
	
	private ResultSet consultaSobreTabla(String sql, String nombreTabla) {
		ResultSet resultSet = null;
		Connection connection = null;
		if (nombreTabla.equals(this.TABLA_EMPRESA)) {
			connection = this.getConexionEmpresa();
		}
		else {
			connection = this.getConexionNoEmpresa();
		}
		if (connection != null) {
			Statement statement;
			try {
				statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				statement.setFetchSize(10);
	            resultSet = statement.executeQuery(sql);
//	            statement.close();
//	            connection.close();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}            
		}
		else {
			System.out.println("La conexion a la base de datos no se puedo establecer con exito");
		}
		return resultSet;
	}
	
	private ResultSetMetaData estructuraDeTabla(String nombreTabla) {
		ResultSetMetaData resultSetMetaData = null;
		String sql = "SELECT TOP 1 * FROM "+nombreTabla;
		ResultSet resultSet = this.consultaSobreTabla(sql, nombreTabla);
		if (resultSet != null) {
			try {
				resultSetMetaData = resultSet.getMetaData();
//				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	    
		return resultSetMetaData;
	}
	
	public String sentenciaParaBorrarTabla(String nombreTabla) {
		String sentencia = "";
		sentencia = "DROP TABLE IF EXISTS " + nombreTabla + ";";
		System.out.println(sentencia);
		return sentencia;
	}
	
	public String sentenciaParaCrearTabla(String nombreTabla) {
		return this.sentenciaParaCrearTabla(nombreTabla, false);
	}
	
	public String sentenciaParaCrearTabla(String nombreTabla, boolean conClavePrimaria) {
		String sentencia = "";
		ResultSetMetaData estructuraTabla = this.estructuraDeTabla(nombreTabla);
		try {
			int cantidadCampos = estructuraTabla.getColumnCount();
			sentencia  = "CREATE TABLE IF NOT EXISTS " + nombreTabla + " (";
			sentencia += this.NOMBRE_CAMPO_CODIGO_SUCURSAL+" "+this.TIPO_CAMPO_CODIGO_SUCURSAL+"("+this.LONGITUD_CAMPO_CODIGO_SUCURSAL+"), ";
			for (int i = 1; i <= cantidadCampos; i++) {
				String nombreCampo = estructuraTabla.getColumnLabel(i).toLowerCase();// resultSetMetaData.getColumnName(i).toLowerCase();
				String tipoDatoCampo = estructuraTabla.getColumnTypeName(i).equals(TIPOS_DE_DATO.LONGVARCHAR.toString()) ? "TEXT": estructuraTabla.getColumnTypeName(i);
				int longitudCampo = estructuraTabla.getColumnDisplaySize(i);				
				if (tipoDatoCampo.equals(TIPOS_DE_DATO.NUMERIC.toString()) ||
					(tipoDatoCampo.equals(TIPOS_DE_DATO.DOUBLE.toString()))) {
					longitudCampo = estructuraTabla.getPrecision(i);
				}
				if (tipoDatoCampo.equals(TIPOS_DE_DATO.NUMERIC.toString())) {
					longitudCampo += 2;
				}
				int decimalesCampo = estructuraTabla.getScale(i);
				sentencia += nombreCampo + " " + tipoDatoCampo;
				if (!tipoDatoCampo.equals(TIPOS_DE_DATO.BOOLEAN.toString()) && 
					!tipoDatoCampo.equals("TEXT") &&
					!tipoDatoCampo.equals(TIPOS_DE_DATO.DATE.toString()) &&
					!tipoDatoCampo.equals(TIPOS_DE_DATO.TIMESTAMP.toString())) {
					sentencia += "(" + longitudCampo;
					if (decimalesCampo > 1) {
						if (tipoDatoCampo.equals(TIPOS_DE_DATO.NUMERIC.toString())) {
							sentencia += ",2";
						}
						else if (tipoDatoCampo.equals(TIPOS_DE_DATO.DOUBLE.toString())) {
							sentencia += ",4";
						}
						else {
							sentencia += "," + decimalesCampo;
						}
					}
					sentencia += ")";
				}
				if (i < cantidadCampos) {
					sentencia += ", ";
				}
			}
			if (conClavePrimaria) {
				String clavePrimaria = this.NOMBRE_CAMPO_CODIGO_SUCURSAL+", ";
				List<String> clavePrimariaList = PropiedadesConfiguracion.getClavePrimariaTabla(nombreTabla);
				for (Iterator<String> iter = clavePrimariaList.iterator(); iter.hasNext();) {
					clavePrimaria += iter.next().toLowerCase();
					if (iter.hasNext()) {
						clavePrimaria += ", ";
					}
				}
				sentencia += " CONSTRAINT PK_"+nombreTabla+" PRIMARY KEY ("+clavePrimaria+"));";
			}
			else {
				sentencia += ");";
			}
			System.out.println(sentencia);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return sentencia;
	}
			
	public Integer consultarCantidadDeRegistrosEnTabla(String nombreTabla) {
		int numRows = 0;
		try {						
            String sql = "SELECT * FROM "+nombreTabla;            
			System.out.println(sql);						
    		ResultSet resultSet = this.consultaSobreTabla(sql, nombreTabla);    		    		
			
			resultSet.last(); 
			numRows = resultSet.getRow(); 
			resultSet.beforeFirst(); // esto te lo deja como al principio 
			System.out.println("Registros: "+numRows);
		}
        catch (Exception ex) {
        	ex.printStackTrace();
        }		
		return numRows;
	}
	
	public String sentenciaParaInsertarRegistros(String nombreTabla) {
		String resultado = "";
		try {						
            String sql = "SELECT * FROM "+nombreTabla;            
			System.out.println(sql);						
    		ResultSet resultSet = this.consultaSobreTabla(sql, nombreTabla);
    		
    		ResultSetMetaData estructuraTabla = resultSet.getMetaData();
    		int cantidadCampos = estructuraTabla.getColumnCount();
    		List<String> nombresCampos = new ArrayList<String>();
    		List<String> tiposDeDatosCampos = new ArrayList<String>();
    		for (int c = 1; c <= cantidadCampos; c++) {
    			String nombreCampo = estructuraTabla.getColumnLabel(c).toLowerCase();// resultSetMetaData.getColumnName(i).toLowerCase();
    			nombresCampos.add(nombreCampo);
				String tipoDatoCampo = estructuraTabla.getColumnTypeName(c);
				if (nombreTabla.equals(this.TABLA_EMPRESA) && nombreCampo.equals(this.CAMPO_EMPRESA_CONFIG)) {
					tipoDatoCampo = "null";
				}
				tiposDeDatosCampos.add(tipoDatoCampo);
    		}
			
			resultSet.last(); 
			int numRows = resultSet.getRow(); 
			resultSet.beforeFirst(); // esto te lo deja como al principio 
			System.out.println("Registros: "+numRows);
			
			int ultimoValorSincronizado = Integer.parseInt(PropiedadesConfiguracion.getUltimoValorSincronizadoTabla(nombreTabla));
			if (ultimoValorSincronizado > 0) {
				resultSet.absolute(ultimoValorSincronizado);
			}
						
			int x = 1;
			while (resultSet.next() && (x<=this.MAXIMO_REGISTROS_INSERTADOS_ACTUALIZADOS)) {	
				System.out.print((ultimoValorSincronizado + x)+") ");
				String sentencia = "INSERT INTO "+nombreTabla+ " (";
				sentencia += this.NOMBRE_CAMPO_CODIGO_SUCURSAL+", ";
				for (int i = 1; i <= cantidadCampos; i++) {					
					sentencia += nombresCampos.get(i-1);					
					if (i < cantidadCampos) {
						sentencia += ", ";
					}
				}
				sentencia += ") VALUES (";
				sentencia += formatoInsertarValorDeCampo(PropiedadesConfiguracion.getCodigoSucursal(), this.TIPO_CAMPO_CODIGO_SUCURSAL)+", ";
				for (int j = 0; j < cantidadCampos; j++) {
					Object valorCampo = resultSet.getObject(j+1);
					if (tiposDeDatosCampos.get(j).equals("null")) {
						valorCampo = null;
					}
					sentencia += formatoInsertarValorDeCampo(valorCampo, tiposDeDatosCampos.get(j));
					if (j < (cantidadCampos-1)) {
						sentencia += ", ";
					}
				}
				sentencia += ");";
				resultado += sentencia;
				System.out.println(sentencia);
				x++;
			}
			
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        }		
		return resultado;
	}
	
	private String formatoInsertarValorDeCampo(Object valorCampo, String tipoDeDatoCampo) {		
		if (valorCampo == null || valorCampo.equals("")) {
			return "null";
		}
		else if (tipoDeDatoCampo.equals(TIPOS_DE_DATO.VARCHAR.toString()) ||
	    		tipoDeDatoCampo.equals(TIPOS_DE_DATO.LONGVARCHAR.toString()) ||
	    		tipoDeDatoCampo.equals(TIPOS_DE_DATO.DATE.toString()) ||
	    		tipoDeDatoCampo.equals(TIPOS_DE_DATO.TIMESTAMP.toString())) {
			return "'"+valorCampo+"'";
		}
		else {
			return valorCampo.toString();
		}
	}
		
	public String sentenciaParaActualizarRegistros(String nombreTabla, int registroInicial) {
		String resultado = "";
		try {			
            String sql = "SELECT * FROM "+nombreTabla;
    		ResultSet resultSet = this.consultaSobreTabla(sql, nombreTabla);
    		
    		ResultSetMetaData estructuraTabla = resultSet.getMetaData();
    		int cantidadCampos = estructuraTabla.getColumnCount();
    		Map<String, String> campos = new HashMap<String, String>();
    		List<String> nombresCampos = new ArrayList<String>();
    		List<String> clavePrimaria = PropiedadesConfiguracion.getClavePrimariaTabla(nombreTabla);    		
    		for (int c = 1; c <= cantidadCampos; c++) {
    			String nombreCampo = estructuraTabla.getColumnLabel(c).toLowerCase();// resultSetMetaData.getColumnName(i).toLowerCase();
    			nombresCampos.add(nombreCampo);
				String tipoDatoCampo = estructuraTabla.getColumnTypeName(c);
				campos.put(nombreCampo, tipoDatoCampo);
    		}
    		
			if (registroInicial > 0) {
				resultSet.absolute(registroInicial);
			}
			
			int x = 1;
			while (resultSet.next() && (x<=this.MAXIMO_REGISTROS_INSERTADOS_ACTUALIZADOS)) {
				System.out.print((x++)+") ");
				List<Object> valoresClavePrimaria = new ArrayList<Object>();
				String sentencia = "UPDATE " + nombreTabla + " SET ";
				for (int i = 1; i <= cantidadCampos; i++) {
					Object valorCampo = resultSet.getObject(i);
					if (nombreTabla.equals(this.TABLA_EMPRESA) && nombresCampos.get(i-1).equals(this.CAMPO_EMPRESA_CONFIG)) {
						valorCampo = null;
					}
					if (clavePrimaria.size() >= i && clavePrimaria.get(i-1).equals(nombresCampos.get(i-1))) {
						valoresClavePrimaria.add(valorCampo);
					}
					sentencia += formatoActualizarValorDeCampo(valorCampo, nombresCampos.get(i-1), campos.get(nombresCampos.get(i-1)));
					if (i < cantidadCampos) {
						sentencia += ", ";
					}
				}
				sentencia += " WHERE ";
				sentencia += formatoActualizarValorDeCampo(PropiedadesConfiguracion.getCodigoSucursal(), this.NOMBRE_CAMPO_CODIGO_SUCURSAL, this.TIPO_CAMPO_CODIGO_SUCURSAL);
				sentencia += " AND ";
				for (int j=0; j<clavePrimaria.size(); j++) {
					sentencia += formatoActualizarValorDeCampo(valoresClavePrimaria.get(j), clavePrimaria.get(j), campos.get(clavePrimaria.get(j)));
					if (j < (clavePrimaria.size()-1)) {
						sentencia += " AND ";
					}
				}
				sentencia += ";";
				resultado += sentencia;
				System.out.println(sentencia);
			}
        } 
        catch (Exception ex) {
        	ex.printStackTrace();
        }
		return resultado;
	}
	
	private String formatoActualizarValorDeCampo(Object valorCampo, String nombreCampo, String tipoDeDatoCampo) {
		if (valorCampo == null || valorCampo.equals("")) {
			return nombreCampo+" = null";
		}
		else if (tipoDeDatoCampo.equals(TIPOS_DE_DATO.VARCHAR.toString()) ||
	    		tipoDeDatoCampo.equals(TIPOS_DE_DATO.LONGVARCHAR.toString()) ||
	    		tipoDeDatoCampo.equals(TIPOS_DE_DATO.DATE.toString()) ||
	    		tipoDeDatoCampo.equals(TIPOS_DE_DATO.TIMESTAMP.toString())) {
			return nombreCampo+" = '"+valorCampo+"'";
		}
		else {
			return nombreCampo+" = "+valorCampo.toString();
		}
	}
	
	public String condicionCodigoSucursal() {
		String condicion = " WHERE ";
		condicion += this.formatoActualizarValorDeCampo(PropiedadesConfiguracion.getCodigoSucursal(), this.NOMBRE_CAMPO_CODIGO_SUCURSAL, this.TIPO_CAMPO_CODIGO_SUCURSAL);
		return condicion;
	}
	
}
