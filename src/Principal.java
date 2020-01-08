import java.util.List;

import javax.swing.JOptionPane;

import rostroperfecto.sincronizador.modelo.auxiliares.PropiedadesConfiguracion;
import rostroperfecto.sincronizador.utilidades.ControladorEjecucionesSimultaneas;
import rostroperfecto.sincronizador.utilidades.SentenciasSQL;
import rostroperfecto.sincronizador.vista.ventanas.VentanaPrincipal;


public class Principal {

	public static void main(String[] args) {
//		List<String> tablas = PropiedadesConfiguracion.getTablas();
//		SentenciasSQL sentenciasSQL = SentenciasSQL.getInstancia();		
//		for (String tabla: tablas) {
//			if (tabla.equals("CIECAB") || tabla.equals("CIELIN")) {
//				sentenciasSQL.sentenciaParaBorrarTabla(tabla);
//				sentenciasSQL.sentenciaParaCrearTabla(tabla, true);
//				sentenciasSQL.sentenciaParaInsertarRegistros(tabla);
//				sentenciasSQL.sentenciaParaActualizarRegistros(tabla, 0);
//			}
//		}
		
		if( new ControladorEjecucionesSimultaneas().comprobar() ) {
			//JOptionPane.showMessageDialog(null,"LA APLICACION SE INICIO CORRECTAMENTE.");
			VentanaPrincipal.getInstancia();
        }        
        else {
            System.exit(0);
        }
				
	}
}
