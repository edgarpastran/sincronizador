package rostroperfecto.sincronizador.vista.auxiliares;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class SincronizacionDatosTableButtonCellRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 2L;
	private JButton boton;

	public SincronizacionDatosTableButtonCellRenderer(final JButton aBtn) {
		super();
		boton = aBtn;
		boton.setFocusPainted(false);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object val,
			boolean select, boolean focus, int row, int col) {
		super.getTableCellRendererComponent(table, val, select, focus, row, col);
		if (focus) {
			boton.setForeground(table.getForeground());
			boton.setBackground(UIManager.getColor("Button.background"));
		} else if (select) {
			boton.setForeground(table.getSelectionForeground());
			boton.setBackground(table.getSelectionBackground());
		} else {
			boton.setForeground(table.getForeground());
			boton.setBackground(UIManager.getColor("Button.background"));
		}
		boton.setText("Ver");
		return boton;
	}
}
