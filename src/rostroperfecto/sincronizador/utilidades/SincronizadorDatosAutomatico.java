package rostroperfecto.sincronizador.utilidades;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.SwingWorker;

import rostroperfecto.sincronizador.modelo.auxiliares.PropiedadesConfiguracion;
import rostroperfecto.sincronizador.vista.ventanas.VentanaPrincipal;

public class SincronizadorDatosAutomatico extends SwingWorker<Void, Void>{

	private VentanaPrincipal ventanaPrincipal;
	private boolean sincronizando;
	
	public SincronizadorDatosAutomatico(VentanaPrincipal ventanaPrincipal) {
		super();
		this.ventanaPrincipal = ventanaPrincipal;
		this.sincronizando = false;
	}

	@Override
	protected Void doInBackground() throws Exception {
		while(!isDone()) {
			if (!this.sincronizando) {
				// EVALUAR SI SE HA INCLUIDO UN NUEVO REGISTRO EN LA TABLA CIECAB
				String nombreTabla = "CIECAB";
				int ultimoValorSincronizado = Integer.parseInt(PropiedadesConfiguracion.getUltimoValorSincronizadoTabla(nombreTabla));
				SentenciasSQL sentenciasSQL = SentenciasSQL.getInstancia();
				int cantidadDeRegistrosActuales = sentenciasSQL.consultarCantidadDeRegistrosEnTabla(nombreTabla);
				if (cantidadDeRegistrosActuales > ultimoValorSincronizado) {
					this.sincronizando = true;
		    		this.sincronizarDatos();
				}
				// EVALUAR SI SE HA LLEGADO A UNA HORA PLANIFICADA PARA SINCRONIZAR
				else {
					List<String> horasSincronizacion = PropiedadesConfiguracion.getHorasSincronizacion();
					Calendar calendar = new GregorianCalendar();
					int horaActual = calendar.get(Calendar.HOUR_OF_DAY);
					int minutoActual = calendar.get(Calendar.MINUTE);
					int segundoActual = calendar.get(Calendar.SECOND);
				    for (String horaSincronizacion: horasSincronizacion) {
				    	int horaParaSincronizar = Integer.parseInt(horaSincronizacion);
				    	if ((horaActual == horaParaSincronizar) && (minutoActual == 0) && (segundoActual <= 1)) {
				    		this.sincronizando = true;
				    		this.sincronizarDatos();
				    		break;
				    	}
				    }
				}							
			}
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException interruptedException) {
				
			}
		}
		return null;
	}

	private void sincronizarDatos() {
		this.ventanaPrincipal.procesarSolicitudSincronizacionAutomatica();
	}
}
