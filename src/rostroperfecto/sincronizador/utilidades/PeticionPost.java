package rostroperfecto.sincronizador.utilidades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class PeticionPost {
	private URL url;
	private String data;
	private static String CHARSET = "ISO-8859-1";//"UTF-8";

	public PeticionPost(String url) throws MalformedURLException {
		this.url = new URL(url);
		this.data = "";
	}

	public void add(String propiedad, String valor)
			throws UnsupportedEncodingException {
		// codificamos cada uno de los valores		
		if (data.length() > 0)
			data += "&" + URLEncoder.encode(propiedad, this.CHARSET) + "="
					+ URLEncoder.encode(valor, this.CHARSET);
		else
			data += URLEncoder.encode(propiedad, this.CHARSET) + "="
					+ URLEncoder.encode(valor, this.CHARSET);
	}

	public String getRespueta() throws IOException {
		String respuesta = "";
		// abrimos la conexión
		URLConnection conn = url.openConnection();
		// especificamos que vamos a escribir
		conn.setDoOutput(true);
		// obtenemos el flujo de escritura
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());		
		// escribimos
		wr.write(data);
		// cerramos la conexión
		wr.flush();
		wr.close();

		// obtenemos el flujo de lectura
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String linea;
		// procesamos la salida
		while ((linea = rd.readLine()) != null) {
			respuesta += linea;
		}
		//reseteamos la data enviada
		this.data = "";
		return respuesta;
	}

}