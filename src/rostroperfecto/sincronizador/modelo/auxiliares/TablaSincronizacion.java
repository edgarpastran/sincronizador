package rostroperfecto.sincronizador.modelo.auxiliares;

public class TablaSincronizacion {

	private boolean seleccionada;
	private String nombreTabla;
	private String fechaUltimaSincronizacion;
	private String horaUltimaSincronizacion;
	private boolean registrosActualizables;
	private boolean actualizarRegistros;
	

	public TablaSincronizacion(String nombreTabla, String fechaUltimaSincronizacion, String horaUltimaSincronizacion, boolean registrosActualizables) {
		super();
		this.seleccionada = false;
		this.nombreTabla = nombreTabla;
		this.fechaUltimaSincronizacion = fechaUltimaSincronizacion;
		this.horaUltimaSincronizacion = horaUltimaSincronizacion;
		this.registrosActualizables = registrosActualizables;
		this.actualizarRegistros = false;
	}
	
	public boolean isSeleccionada() {
		return seleccionada;
	}


	public void setSeleccionada(boolean seleccionada) {
		this.seleccionada = seleccionada;
	}


	public String getNombreTabla() {
		return nombreTabla;
	}


	public void setNombreTabla(String nombreTabla) {
		this.nombreTabla = nombreTabla;
	}


	public String getFechaUltimaSincronizacion() {
		return fechaUltimaSincronizacion;
	}


	public void setFechaUltimaSincronizacion(String fechaUltimaSincronizacion) {
		this.fechaUltimaSincronizacion = fechaUltimaSincronizacion;
	}


	public String getHoraUltimaSincronizacion() {
		return horaUltimaSincronizacion;
	}


	public void setHoraUltimaSincronizacion(String horaUltimaSincronizacion) {
		this.horaUltimaSincronizacion = horaUltimaSincronizacion;
	}

	public boolean isRegistrosActualizables() {
		return registrosActualizables;
	}

	public void setRegistrosActualizables(boolean registrosActualizables) {
		this.registrosActualizables = registrosActualizables;
	}

	public boolean isActualizarRegistros() {
		return actualizarRegistros;
	}

	public void setActualizarRegistros(boolean actualizarRegistros) {
		this.actualizarRegistros = actualizarRegistros;
	}

}
