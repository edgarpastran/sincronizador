package edpas.componentes.vista.componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class JListDouble extends JPanel {

	private JListSingle listaIzquierda;
	private JListSingle listaDerecha;
	private JButton btnAgregar;
	private JButton btnQuitar;
	private Color colorFondo;
	private Color colorTexto;

	public JListDouble(String tituloCentral, String tituloIzquierdo, String tituloDerecho) {
    	this(tituloCentral, tituloIzquierdo, tituloDerecho, null, null);
    }

	public JListDouble(String tituloCentral, String tituloIzquierdo,
			String tituloDerecho, Color colorFondo, Color colorTexto) {
		super();
		this.setOpaque(false);
		this.colorFondo = colorFondo;
		this.colorTexto = colorTexto;
		if (colorFondo != null) {
			this.setBackground(colorFondo);
		}
		if (colorTexto != null) {
			
		}
				
		this.setLayout(new BorderLayout());		
		if (this.colorTexto != null) {
			this.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, colorTexto));
			JLabel lblTitulo = new JLabel(tituloCentral, SwingConstants.CENTER);
			lblTitulo.setForeground(this.colorTexto);
			this.add(lblTitulo, BorderLayout.NORTH);
		}
		else {
			this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), tituloCentral, TitledBorder.CENTER, TitledBorder.TOP));
		}		
		this.initGUI(tituloIzquierdo, tituloDerecho);
	}

	private void initGUI(String tituloIzquierdo, String tituloDerecho) {	
		JPanel panelCentral = new JPanel();
		if (this.colorFondo != null) {
			panelCentral.setBackground(this.colorFondo);
		}
		panelCentral.setOpaque(true);
		panelCentral.setLayout(new GridBagLayout());
		this.add(panelCentral, BorderLayout.CENTER);

		JPanel panelListaIzquierda = new JPanel();
		panelListaIzquierda.setLayout(new BorderLayout());
		panelListaIzquierda.setOpaque(false);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		panelCentral.add(panelListaIzquierda, constraints);

		JLabel lblIzquierda = new JLabel(tituloIzquierdo, SwingConstants.CENTER);
		if (this.colorTexto != null) {
			lblIzquierda.setForeground(this.colorTexto);
		}
		panelListaIzquierda.add(lblIzquierda, BorderLayout.NORTH);
		this.listaIzquierda = new JListSingle();
		panelListaIzquierda.add(this.listaIzquierda, BorderLayout.CENTER);

		JPanel panelMedio = new JPanel();
		panelMedio.setLayout(new BorderLayout());
		panelMedio.setOpaque(false);
		panelMedio.add(new JLabel("  "), BorderLayout.NORTH);
		JPanel panelBotones = new JPanel();
		panelBotones.setLayout(new BorderLayout());
		panelBotones.setOpaque(false);
		panelMedio.add(panelBotones, BorderLayout.CENTER);
		constraints.weightx = 0.0;
		panelCentral.add(panelMedio, constraints);

		this.btnAgregar = new JButton();
		this.btnAgregar.addActionListener(this.btnAgregarListener);
		this.btnAgregar.setIcon(new ImageIcon(getClass().getResource(
				"/edpas/componentes/vista/resources/icons/siguiente.png")));
		this.btnAgregar.setFocusable(false);
		panelBotones.add(this.btnAgregar, BorderLayout.NORTH);

		this.btnQuitar = new JButton();
		this.btnQuitar.addActionListener(this.btnQuitarListener);
		this.btnQuitar.setIcon(new ImageIcon(getClass().getResource(
				"/edpas/componentes/vista/resources/icons/anterior.png")));
		this.btnQuitar.setFocusable(false);
		panelBotones.add(this.btnQuitar, BorderLayout.SOUTH);

		JPanel panelListaDerecha = new JPanel();
		panelListaDerecha.setLayout(new BorderLayout());
		panelListaDerecha.setOpaque(false);
		constraints.weightx = 1.0;
		panelCentral.add(panelListaDerecha, constraints);

		JLabel lblDerecha = new JLabel(tituloDerecho, SwingConstants.CENTER);
		if (this.colorTexto != null) {
			lblDerecha.setForeground(this.colorTexto);
		}
		panelListaDerecha.add(lblDerecha, BorderLayout.NORTH);
		this.listaDerecha = new JListSingle();
		panelListaDerecha.add(this.listaDerecha, BorderLayout.CENTER);
	}

	private ActionListener btnAgregarListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			List objetos = listaIzquierda.getSelectedItems();
			for (Object objeto : objetos) {
				listaIzquierda.removeItem(objeto);
				listaDerecha.addItem(objeto);
			}
		}
	};

	private ActionListener btnQuitarListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			List objetos = listaDerecha.getSelectedItems();
			for (Object objeto : objetos) {
				listaDerecha.removeItem(objeto);
				listaIzquierda.addItem(objeto);
			}
		}
	};

	public void setItemsOrigen(List items) {
		this.listaIzquierda.setItems(items);
	}

	public List getItemsOrigen() {
		return this.listaIzquierda.getItems();
	}

	public void setItemsDestino(List items) {
		this.listaDerecha.setItems(items);
	}

	public List getItemsDestino() {
		return this.listaDerecha.getItems();
	}

	public void setBackgroundColor(Color color) {
		this.listaIzquierda.setBackground(color);
		this.listaDerecha.setBackground(color);
	}

	public void setCellRenderer(ListCellRenderer listCellRenderer) {
		this.listaIzquierda.setCellRenderer(listCellRenderer);
		this.listaDerecha.setCellRenderer(listCellRenderer);
	}
}
