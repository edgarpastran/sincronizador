package rostroperfecto.sincronizador.vista.auxiliares;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import rostroperfecto.sincronizador.modelo.auxiliares.TablaSincronizacion;

public class SincronizacionDatosTableModel extends AbstractTableModel {

	private static final String[] titulos = {"", "Tabla", "Fecha Ult. Sincronizacion", "Hora Ult. Sincronizacion", "Actualizar Registros", "Ëstructura de Tabla"};
	private List<TablaSincronizacion> datos;
	
		
	public SincronizacionDatosTableModel(List<TablaSincronizacion> datos) {
		super();
		this.datos = datos;
		this.fireTableDataChanged();
	}

	public List<TablaSincronizacion> getDatos() {
		return datos;
	}

	public void setDatos(List<TablaSincronizacion> datos) {
		this.datos = datos;
	}

	@Override
	public int getColumnCount() {
		return titulos.length;
	}

	@Override
    public String getColumnName(int column) {	
		return this.titulos[column];
    }
	
	
	@Override
	public Class<?> getColumnClass(int arg0) {
		switch (arg0) {
		case 0:			
		case 4:
			return Boolean.class;
		case 5:
		    return JButton.class;
		default:
		    return String.class;
		}
	}

	@Override
	public int getRowCount() {
		return datos.size();
	}

	@Override
	public Object getValueAt(int fila, int columna) {
		TablaSincronizacion tablaSincronizacion = datos.get(fila);
		switch (columna) {
		case 0:			
			return new Boolean(tablaSincronizacion.isSeleccionada());
		case 1:
		    return tablaSincronizacion.getNombreTabla();
		case 2:
		    return tablaSincronizacion.getFechaUltimaSincronizacion(); 
		case 3:
		    return tablaSincronizacion.getHoraUltimaSincronizacion();
		case 4:
			if (tablaSincronizacion.isRegistrosActualizables()) {
				return new Boolean(tablaSincronizacion.isActualizarRegistros());
			}
			return new Boolean(false);
		case 5:
		    return tablaSincronizacion.getNombreTabla();
		default:
		    break;
		}
		return null;
	}
	
	public void setSeleccionPorFila(int fila) {
		TablaSincronizacion tablaSincronizacion = datos.get(fila); 
		tablaSincronizacion.setSeleccionada(!tablaSincronizacion.isSeleccionada());
		if (!tablaSincronizacion.isSeleccionada()) {
			tablaSincronizacion.setActualizarRegistros(false);
		}
		this.fireTableDataChanged();
	}
	
	public void setSeleccionTotal(boolean valor) {
		for (TablaSincronizacion tablaSincronizacion: datos) {
			tablaSincronizacion.setSeleccionada(valor);
			if (!valor) {
				tablaSincronizacion.setActualizarRegistros(false);
			}
		}
		this.fireTableDataChanged();
	}
	
	public void setActualizarRegistros(int fila) {
		TablaSincronizacion tablaSincronizacion = datos.get(fila);
		if (tablaSincronizacion.isRegistrosActualizables()) {
			tablaSincronizacion.setActualizarRegistros(!tablaSincronizacion.isActualizarRegistros());
			if (tablaSincronizacion.isActualizarRegistros()) {
				tablaSincronizacion.setSeleccionada(true);
			}	
			this.fireTableDataChanged();
		}
	}
	
	public String getNombreTabla(int fila) {
		return this.datos.get(fila).getNombreTabla();
	}

	public List<TablaSincronizacion> getTablasSeleccionadas() {
		List<TablaSincronizacion> tablasSeleccionadas = new ArrayList<TablaSincronizacion>();
		for (TablaSincronizacion tablaSincronizacion : this.getDatos()) {
			if (tablaSincronizacion.isSeleccionada()) {
				tablasSeleccionadas.add(tablaSincronizacion);
			}
		}
		return tablasSeleccionadas;
	}
}
