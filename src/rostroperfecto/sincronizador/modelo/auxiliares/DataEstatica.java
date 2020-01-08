package rostroperfecto.sincronizador.modelo.auxiliares;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class DataEstatica implements Serializable {

    private Object id;
    private String nombre;
    
    protected DataEstatica(Object id, String nombre) {
		super();
		this.id = id;
		this.nombre = nombre;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object obj) {
		if (obj != null) {
		    if (obj instanceof DataEstatica) {
		    	return ((DataEstatica) obj).getId().equals(this.id);
		    } else {
		    	return obj.equals(this.id);
		    }
		}
		return false;	    	    
    }

    @Override
    public String toString() {	
    	return this.nombre;
    }
    
}
