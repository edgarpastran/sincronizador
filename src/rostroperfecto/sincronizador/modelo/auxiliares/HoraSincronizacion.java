package rostroperfecto.sincronizador.modelo.auxiliares;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoraSincronizacion extends DataEstatica implements Comparable<HoraSincronizacion> {

    private HoraSincronizacion(Object id, String nombre) {
    	super(id, nombre);
    }

    public static List<HoraSincronizacion> getListado() {
		List<HoraSincronizacion> listado = new ArrayList<HoraSincronizacion>();
		for (Integer i=0; i<24; i++) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
			Date fecha = new Date();
			fecha.setHours(i);
			fecha.setMinutes(0);
			String nombre = simpleDateFormat.format(fecha);
			String id = i.toString();
			if (i < 10) {
				id = "0"+id;
			}
			listado.add(new HoraSincronizacion(id, nombre));
		}
		return listado;
    }
    
    public static String getNombrePorId(Object id) {
    	HoraSincronizacion horaSincronizacion = getHoraSincronizacionPorId(id);
		if (horaSincronizacion != null) {
		    return horaSincronizacion.getNombre();
		}
		return null;
    }

    public static HoraSincronizacion getHoraSincronizacionPorId(Object id) {
		List<HoraSincronizacion> listado = getListado();
		for (HoraSincronizacion horaSincronizacion: listado) {
		    if (horaSincronizacion.getId().equals(id)) {
		    	return horaSincronizacion;
		    }
		}
		return null;
    }

    public Integer getIndiceHoraSincronizacion() {
    	return getListado().indexOf(this);
    }
    
    @Override
    public int compareTo(HoraSincronizacion horaSincronizacion) {
		if (!this.equals(horaSincronizacion)) {
		    return this.getIndiceHoraSincronizacion().compareTo(horaSincronizacion.getIndiceHoraSincronizacion());
		}
		return 1;
    }
    
}
