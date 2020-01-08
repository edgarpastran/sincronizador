package rostroperfecto.sincronizador.vista.ventanas;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import rostroperfecto.sincronizador.utilidades.SentenciasSQL;

public class VentanaAuxiliarEstructuraTabla extends JFrame {

	private JTextArea textArea;
	private SentenciasSQL sentenciasSQL;
	private static VentanaAuxiliarEstructuraTabla instancia;
	
	
	public static VentanaAuxiliarEstructuraTabla getInstancia(String nombreTabla) {
		if (instancia == null) {
			instancia = new VentanaAuxiliarEstructuraTabla();
		}
		instancia.setTitle(nombreTabla);
		instancia.mostrarEstructuraDeTabla(nombreTabla);
		instancia.setVisible(true);
		instancia.setLocationRelativeTo(null);
		instancia.toFront();
		return instancia;
	}
	
	private VentanaAuxiliarEstructuraTabla() {
		super();
		String clase = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
		try {
			javax.swing.UIManager.setLookAndFeel(clase);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(this);
		this.sentenciasSQL = SentenciasSQL.getInstancia();
		this.setIconImage(new ImageIcon(getClass().getResource("/rostroperfecto/sincronizador/vista/imagenes/Logo.png")).getImage());
		this.initComponents();
		this.pack();
		this.setSize(700, 250);		
		this.setResizable(false);				
	}
	
	private void initComponents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.CENTER);
		
		this.textArea = new JTextArea();
		this.textArea.setAutoscrolls(true);
		this.textArea.setEditable(false);
		this.textArea.setLineWrap(true);
		panel.add(this.textArea, BorderLayout.CENTER);						
	}
	
	private void mostrarEstructuraDeTabla(String nombreTabla) {
		String sentencia = this.sentenciasSQL.sentenciaParaCrearTabla(nombreTabla);
		this.textArea.setText(sentencia);
	}
}
