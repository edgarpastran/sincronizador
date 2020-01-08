package rostroperfecto.sincronizador.vista.ventanas;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import edpas.componentes.vista.componentes.CheckBoxHeader;
import edpas.componentes.vista.componentes.InfiniteProgressPanel;
import edpas.componentes.vista.componentes.JListDouble;
import edpas.componentes.vista.componentes.JPanelExtended;

import rostroperfecto.sincronizador.modelo.auxiliares.HoraSincronizacion;
import rostroperfecto.sincronizador.modelo.auxiliares.PropiedadesConfiguracion;
import rostroperfecto.sincronizador.modelo.auxiliares.TablaSincronizacion;
import rostroperfecto.sincronizador.utilidades.Logger;
import rostroperfecto.sincronizador.utilidades.SincronizadorDatos;
import rostroperfecto.sincronizador.utilidades.SincronizadorDatosAutomatico;
import rostroperfecto.sincronizador.vista.auxiliares.SincronizacionDatosTableButtonCellRenderer;
import rostroperfecto.sincronizador.vista.auxiliares.SincronizacionDatosTableModel;


public class VentanaPrincipal extends JFrame {

	private static Color COLOR_FONDO_1 = Color.BLACK;
	private static Color COLOR_FONDO_2 = new Color(254, 46, 154);
	private enum TIPOS_DE_SINCRONIZACION {AUTOMATICO, MANUAL}
	
	private SincronizadorDatos sincronizadorDatos;
	private SincronizadorDatosAutomatico sincronizadorDatosAutomatico; 

	// Panel de Progreso
	private InfiniteProgressPanel progressPanel;
	private JPanelExtended panelCentral;
    private JTabbedPane tabbedPane;
    
    // Atributos para la colocacion en la barra de tareas
    private TrayIcon trayIcon;
	private SystemTray systemTray;
    
    // Panel de Sincronizacion de Datos
    private JTable tablaSincronizacionDatos;
    private CheckBoxHeader checkBoxHeader;
    private JButton btnSincronizarDatos;
    
    // Panel de Configuracion de Sincronizacion
    private JListDouble lstHorasSincronizacion;
    private JButton btnGuardarConfiguracionSincronizacion;
    
    private static VentanaPrincipal instancia;
    public static VentanaPrincipal getInstancia() {
    	if (instancia == null) {
    		instancia = new VentanaPrincipal();    		
    	}
    	return instancia;
    }
    
	private VentanaPrincipal() throws HeadlessException {
		super();
		this.setTitle("Sincronizador de Datos");
		this.setResizable(false);
		this.initGUI();
		this.prepararAtributosParaLaBarraDeTareas();		
		this.cargarInformacion();
		this.pack();	
		this.setSize(800, 470);
		this.setLocationRelativeTo(null);
		this.progressPanel = new InfiniteProgressPanel("Cargando...", 20, 0.25f);
		this.progressPanel.setFont(new Font("Calibri", Font.BOLD, 30));
		this.setGlassPane(progressPanel);
		this.sincronizadorDatos = SincronizadorDatos.getInstancia();
		this.sincronizadorDatosAutomatico = new SincronizadorDatosAutomatico(this);
		this.sincronizadorDatosAutomatico.execute();
//		this.setVisible(true);		
		this.colocarEnBarraDeTareas();				
	}

	public void mostrarProgressPanel(String texto) {
		this.progressPanel.setText(texto);
		this.progressPanel.start();
	}

	public void ocultarProgressPanel() {
		this.progressPanel.stop();
	}
	    
	private WindowListener windowListener = new WindowListener() {
		@Override
		public void windowOpened(WindowEvent e) {			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			colocarEnBarraDeTareas();
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
		    colocarEnBarraDeTareas();
//			System.exit(0);
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void windowActivated(WindowEvent e) {
		}
	}; 
	
	private void prepararAtributosParaLaBarraDeTareas() {
		PopupMenu popupMenu = new PopupMenu();
		MenuItem menuItem = new MenuItem("Sincronizar Datos Manualmente");
		menuItem.addActionListener(this.popupMenuListener);
		popupMenu.add(menuItem);
		menuItem=new MenuItem("Configurar Datos de Sincronizacion");
		menuItem.addActionListener(this.popupMenuListener);
		popupMenu.add(menuItem);
		
		this.trayIcon =  new TrayIcon(this.getIconImage(), "Sincronizador de Datos", popupMenu);
		this.trayIcon.setImageAutoSize(true);
		this.systemTray = SystemTray.getSystemTray();
	}
	
	private ActionListener popupMenuListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			systemTray.remove(trayIcon);
			setExtendedState(JFrame.NORMAL);
			setVisible(true);
			if (arg0.getActionCommand().equals("Sincronizar Datos Manualmente")) {
				tabbedPane.setSelectedIndex(0);
			}
			else {
				tabbedPane.setSelectedIndex(1);
			}
		}
	};
	
	private void colocarEnBarraDeTareas() {
		try {
			if (SystemTray.isSupported()) {
				this.systemTray.add(this.trayIcon);
				this.setVisible(false);
			}
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	private void initGUI() {
		try {			
			String clase = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
			javax.swing.UIManager.setLookAndFeel(clase);
			SwingUtilities.updateComponentTreeUI(this);
			
			this.setIconImage(new ImageIcon(getClass().getResource("/rostroperfecto/sincronizador/vista/imagenes/Logo.png")).getImage());
			this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		    this.addWindowListener(this.windowListener);
		    
		    JPanelExtended panelBanner = new JPanelExtended();
		    panelBanner.setImagen("/rostroperfecto/sincronizador/vista/imagenes/Banner.png");
		    panelBanner.setPreferredSize(new Dimension(800, 120));
		    getContentPane().add(panelBanner, BorderLayout.NORTH);
		    
			panelCentral = new JPanelExtended(this.COLOR_FONDO_1, this.COLOR_FONDO_2);
			BorderLayout jPanel1Layout = new BorderLayout();
			panelCentral.setLayout(jPanel1Layout);
			getContentPane().add(panelCentral, BorderLayout.CENTER);
			panelCentral.setLayout(new BorderLayout());
			tabbedPane = new JTabbedPane();
			panelCentral.add(tabbedPane, BorderLayout.CENTER);
			tabbedPane.addTab("Sincronizacion de Datos", this.getPanelSincronizacionDeDatos());
			tabbedPane.addTab("Configuracion de Sincronizacion", this.getPanelConfiguracionDeSincronizacion());			
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	private JPanel getPanelSincronizacionDeDatos() {
		JPanel panelSincronizacionDeDatos = new JPanel();
		panelSincronizacionDeDatos.setLayout(new BorderLayout());

		JScrollPane jScrollPane = new JScrollPane();
		this.tablaSincronizacionDatos = new JTable();
		
		this.tablaSincronizacionDatos.setModel(new SincronizacionDatosTableModel(new ArrayList<TablaSincronizacion>()));		
	    
	    this.tablaSincronizacionDatos.getColumnModel().getColumn(0).setPreferredWidth(20);
	    this.tablaSincronizacionDatos.getColumnModel().getColumn(0).setMaxWidth(20);
	    this.tablaSincronizacionDatos.getColumnModel().getColumn(1).setPreferredWidth(150);
	    this.tablaSincronizacionDatos.getColumnModel().getColumn(1).setMaxWidth(150);
	    this.tablaSincronizacionDatos.getColumnModel().getColumn(2).setPreferredWidth(150);
	    this.tablaSincronizacionDatos.getColumnModel().getColumn(2).setMaxWidth(150);	   
	    this.tablaSincronizacionDatos.getColumnModel().getColumn(3).setPreferredWidth(150);
	    this.tablaSincronizacionDatos.getColumnModel().getColumn(3).setMaxWidth(150);
	    this.tablaSincronizacionDatos.getColumnModel().getColumn(4).setPreferredWidth(150);
	    this.tablaSincronizacionDatos.getColumnModel().getColumn(4).setMaxWidth(150);

		TableColumn tableColumn = this.tablaSincronizacionDatos.getColumnModel().getColumn(0);
	    tableColumn.setCellEditor(this.tablaSincronizacionDatos.getDefaultEditor(Boolean.class));
	    tableColumn.setCellRenderer(this.tablaSincronizacionDatos.getDefaultRenderer(Boolean.class));
	    this.checkBoxHeader = new CheckBoxHeader(new MyItemListener());
	    tableColumn.setHeaderRenderer(this.checkBoxHeader);
	    
	    DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
		tableCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		this.tablaSincronizacionDatos.getColumnModel().getColumn(2).setCellRenderer(tableCellRenderer);
		this.tablaSincronizacionDatos.getColumnModel().getColumn(3).setCellRenderer(tableCellRenderer);

	    JButton btnVer = new JButton();
	    this.tablaSincronizacionDatos.getColumnModel().getColumn(5).setCellRenderer(new SincronizacionDatosTableButtonCellRenderer(btnVer));
	    
	    this.tablaSincronizacionDatos.addMouseListener(this.tablaSincronizacionDatosMouseAdapter);
		jScrollPane.setViewportView(this.tablaSincronizacionDatos);
		panelSincronizacionDeDatos.add(jScrollPane, BorderLayout.CENTER);

		JPanelExtended panelInferior = new JPanelExtended(this.COLOR_FONDO_1, this.COLOR_FONDO_2);
		panelSincronizacionDeDatos.add(panelInferior, BorderLayout.SOUTH);
		this.btnSincronizarDatos = new JButton("Sincronizar Datos");
		this.btnSincronizarDatos.setIcon(new ImageIcon(getClass().getResource("/rostroperfecto/sincronizador/vista/imagenes/Sincronizar.png")));
		this.btnSincronizarDatos.addActionListener(this.btnSincronizarDatosListener);
		panelInferior.add(this.btnSincronizarDatos);
		
		return panelSincronizacionDeDatos;
	}
	
	private ActionListener btnSincronizarDatosListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			procesarSolicitudSincronizacion();;
		}
	};
	
	private void procesarSolicitudSincronizacion() {
		final List<TablaSincronizacion> tablasSincronizacion = ((SincronizacionDatosTableModel)tablaSincronizacionDatos.getModel()).getTablasSeleccionadas();
		if (tablasSincronizacion.size() > 0) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				    mostrarProgressPanel("Procesando Sincronizacion de Datos Manual");
				    Thread hilo = new Thread(new Runnable() {
					public void run() {
						sincronizarDatos(tablasSincronizacion, TIPOS_DE_SINCRONIZACION.MANUAL.toString());						
					}
				    }, "HiloProgressBar");
				    hilo.start();
				}
			    }); 
		}
		else {
			JOptionPane.showMessageDialog(this, "Debe seleccionar la(s) tabla(s) que desea sincronizar", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void procesarSolicitudSincronizacionAutomatica() {
		final List<TablaSincronizacion> tablasSincronizacion = new ArrayList<TablaSincronizacion>(); 
		List<String> tablas = PropiedadesConfiguracion.getTablas();
		for (String nombreTabla : tablas) {			
			String fechaUltimaSincronizacion = PropiedadesConfiguracion.getFechaUltimaSincronizacionTabla(nombreTabla);
			String horaUltimaSincronizacion = PropiedadesConfiguracion.getHoraUltimaSincronizacionTabla(nombreTabla);
			Boolean registrosActualizables = PropiedadesConfiguracion.isRegistrosActualizables(nombreTabla);
			TablaSincronizacion tablaSincronizacion = new TablaSincronizacion(nombreTabla, fechaUltimaSincronizacion, horaUltimaSincronizacion, registrosActualizables);
			tablaSincronizacion.setSeleccionada(true);
			tablasSincronizacion.add(tablaSincronizacion);
		}
		if (tablasSincronizacion.size() > 0) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				    mostrarProgressPanel("Procesando Sincronizacion de Datos Automatica");
				    Thread hilo = new Thread(new Runnable() {
					public void run() {
						sincronizarDatos(tablasSincronizacion, TIPOS_DE_SINCRONIZACION.AUTOMATICO.toString());
					}
				    }, "HiloProgressBar");
				    hilo.start();
				}
			    }); 
		}
		else {
			JOptionPane.showMessageDialog(this, "Debe seleccionar la(s) tabla(s) que desea sincronizar", "Error", JOptionPane.ERROR_MESSAGE);
		}
	} 
	
	private void sincronizarDatos(List<TablaSincronizacion> tablas, String tipo) {		
		this.panelCentral.setEnabled(false);
		Map<String, String> registrosInsertados = new LinkedHashMap<String, String>();
		Map<String, String> registrosActualizados = new LinkedHashMap<String, String>();
		Logger logger = Logger.getInstancia();
		logger.writeHeaderInfoLog(tipo);
		for (TablaSincronizacion tablaSincronizacion : tablas) {
			String tabla = tablaSincronizacion.getNombreTabla();
			if (tablaSincronizacion.isSeleccionada()) {								
				String cantidadRegistrosInsertados = this.sincronizadorDatos.insertarRegistrosNuevos(tabla);
				registrosInsertados.put(tabla, cantidadRegistrosInsertados);
			}
			if (tablaSincronizacion.isActualizarRegistros()) {
				String cantidadRegistrosActualizados = this.sincronizadorDatos.actualizarRegistros(tabla);
				registrosActualizados.put(tabla, cantidadRegistrosActualizados);
			}
		}
		logger.writeFooterInfoLog(tipo);
		this.mostrarMensajeSincronizacion(registrosInsertados, registrosActualizados);
		this.cargarInformacion();
		this.checkBoxHeader.setSelected(false);
		this.panelCentral.setEnabled(true);
		this.ocultarProgressPanel();
		if (tipo.equals(TIPOS_DE_SINCRONIZACION.AUTOMATICO.toString())) {
			this.sincronizadorDatosAutomatico = new SincronizadorDatosAutomatico(this);
			this.sincronizadorDatosAutomatico.execute();
		}
	}
	
	private void mostrarMensajeSincronizacion(Map<String, String> registrosInsertados, Map<String, String> registrosActualizados) {
		if (this.isVisible()) {
			String mensaje =
			"<html>"+
			"<table border=\"1\">"+
			"<tr>"+
			"<td colspan=\"3\"><b><i><u>Resultado de Sincronizacion de Datos</u></i></b></td>"+
			"</tr>"+
			"<tr>"+
			"<td>Tabla</td>"+
			"<td>Insertados</td>"+
			"<td>Actualizados</td>"+
			"</tr>";
			for (int i=0; i<registrosInsertados.size(); i++) {			
				String tabla = registrosInsertados.keySet().toArray()[i].toString();
				String insertados = (registrosInsertados.get(tabla)==null)?"0":registrosInsertados.get(tabla);
				String actualizados = (registrosActualizados.get(tabla)==null)?"0":registrosActualizados.get(tabla);
				mensaje +=
				"<tr>"+
			    "<td>"+tabla+"</td>"+
			    "<td>"+insertados+"</td>"+
			    "<td>"+actualizados+"</td>"+
			    "</tr>";
			}		  
			mensaje +=
			"</table>"+
			"</html>";
			JOptionPane.showMessageDialog(this, mensaje, "Informacion", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			this.trayIcon.displayMessage("Sincronización de Datos", "Realizada Satisfactoriamente", TrayIcon.MessageType.INFO);
		}
	}
	
	private MouseAdapter tablaSincronizacionDatosMouseAdapter = new MouseAdapter() {
    	public void mouseClicked(MouseEvent e) {
           int fila = tablaSincronizacionDatos.rowAtPoint(e.getPoint());
           int columna = tablaSincronizacionDatos.columnAtPoint(e.getPoint());
           if (fila > -1) {
        	   if (columna == 0) {           
        		   ((SincronizacionDatosTableModel)tablaSincronizacionDatos.getModel()).setSeleccionPorFila(fila);
        	   }
        	   else if (columna == 4) {
        		   ((SincronizacionDatosTableModel)tablaSincronizacionDatos.getModel()).setActualizarRegistros(fila);
        	   }
        	   else if (columna == 5) {
        		   String nombreTabla = ((SincronizacionDatosTableModel)tablaSincronizacionDatos.getModel()).getNombreTabla(fila);
        		   VentanaAuxiliarEstructuraTabla.getInstancia(nombreTabla);
        	   }
           }
        }
	};
	
	private class MyItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getSource();
			if (source instanceof AbstractButton == false)
				return;
			boolean checked = e.getStateChange() == ItemEvent.SELECTED;
			((SincronizacionDatosTableModel) tablaSincronizacionDatos.getModel()).setSeleccionTotal(checked);
		}
	}
		
	private JPanel getPanelConfiguracionDeSincronizacion() {
		JPanel panelConfigurarSincronizacion = new JPanel();
		panelConfigurarSincronizacion.setLayout(new BorderLayout());
		
		this.lstHorasSincronizacion = new JListDouble("Horas de Sincronizacion Automatica", "Horas No Usadas", "Horas Usadas", this.COLOR_FONDO_1, Color.WHITE);
		this.lstHorasSincronizacion.setOpaque(true);
		panelConfigurarSincronizacion.add(this.lstHorasSincronizacion, BorderLayout.CENTER);
		
		JPanelExtended panelInferior = new JPanelExtended(this.COLOR_FONDO_1, this.COLOR_FONDO_2);
		panelConfigurarSincronizacion.add(panelInferior, BorderLayout.SOUTH);
		this.btnGuardarConfiguracionSincronizacion = new JButton("Guardar Configuracion de Sincronizacion");
		this.btnGuardarConfiguracionSincronizacion.setIcon(new ImageIcon(getClass().getResource("/rostroperfecto/sincronizador/vista/imagenes/Guardar.png")));
		this.btnGuardarConfiguracionSincronizacion.addActionListener(this.btnGuardarConfiguracionSincronizacionListener);
		panelInferior.add(this.btnGuardarConfiguracionSincronizacion);
		
		return panelConfigurarSincronizacion;
	}
	
	private ActionListener btnGuardarConfiguracionSincronizacionListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			guardarConfiguracionSincronizacionDatos();
		}
	};
	
	private void guardarConfiguracionSincronizacionDatos() {
		List<HoraSincronizacion> horasParaSincronizar = this.lstHorasSincronizacion.getItemsDestino();
		String horasParaSincronizarString = "";
		for (Iterator<HoraSincronizacion> iter=horasParaSincronizar.iterator(); iter.hasNext();) {
			HoraSincronizacion horaSincronizacion = iter.next();
			horasParaSincronizarString += horaSincronizacion.getId();
			if (iter.hasNext()) {
				horasParaSincronizarString += ",";
			}
		}
		if (PropiedadesConfiguracion.setHorasSincronizacion(horasParaSincronizarString)) {
			JOptionPane.showMessageDialog(this, "La Configuracion de Sincronizacion de Datos se guardo con exito", "Informacion", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(this, "La Configuracion de Sincronizacion de Datos no se pudo guardar", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void cargarInformacion() {
		// Sincronizacion de Datos
		List<TablaSincronizacion> tablasSincronizadas = new ArrayList<TablaSincronizacion>();
		List<String> tablas = PropiedadesConfiguracion.getTablas();
		for (String nombreTabla : tablas) {			
			String fechaUltimaSincronizacion = PropiedadesConfiguracion.getFechaUltimaSincronizacionTabla(nombreTabla);
			String horaUltimaSincronizacion = PropiedadesConfiguracion.getHoraUltimaSincronizacionTabla(nombreTabla);
			Boolean registrosActualizables = PropiedadesConfiguracion.isRegistrosActualizables(nombreTabla);
			tablasSincronizadas.add(new TablaSincronizacion(nombreTabla, fechaUltimaSincronizacion, horaUltimaSincronizacion, registrosActualizables));
		}
		((SincronizacionDatosTableModel)this.tablaSincronizacionDatos.getModel()).setDatos(tablasSincronizadas);
				
		// Configuracion de Sincronizacion de Datos
		List<HoraSincronizacion> horasSincronizacion = HoraSincronizacion.getListado();
		List<String> horasSincronizacionActuales = PropiedadesConfiguracion.getHorasSincronizacion();
		List<HoraSincronizacion> horasDisponibles = new ArrayList<HoraSincronizacion>();
		List<HoraSincronizacion> horasUsadas = new ArrayList<HoraSincronizacion>();		
		for (HoraSincronizacion horaSincronizacion : horasSincronizacion) {
			for (int i=0; i<horasSincronizacionActuales.size(); i++) {
				String horaUsada = horasSincronizacionActuales.get(i); 
				if (horaSincronizacion.equals(horaUsada)) {
					horasUsadas.add(horaSincronizacion);
					break;
				}
				else if (i == horasSincronizacionActuales.size()-1) {
					horasDisponibles.add(horaSincronizacion);
				}
			}			
		}
		this.lstHorasSincronizacion.setItemsOrigen(horasDisponibles);
		this.lstHorasSincronizacion.setItemsDestino(horasUsadas);
	}
    
}
