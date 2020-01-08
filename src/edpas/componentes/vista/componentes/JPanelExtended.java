package edpas.componentes.vista.componentes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

public class JPanelExtended extends JPanel {

    private Image imagen;
    private Color[] colores = new Color[2];
    private boolean gradienteHorizontal = true;

    private boolean enabled;
    
    
    public JPanelExtended() {
	super();
    }

    public JPanelExtended(String nombreImagen) {
	if (nombreImagen != null) {
	    URL url = getClass().getResource(nombreImagen);
	    this.imagen = new ImageIcon(url).getImage();
	}
    }

    public JPanelExtended(Image imagen) {
	if (imagen != null) {
	    this.imagen = imagen;
	}
    }

    public JPanelExtended(Color color1, Color color2) {
	this(color1, color2, true);
    }

    public JPanelExtended(Color color1, Color color2, boolean gradienteHorizontal) {
	this.colores[0] = color1;
	this.colores[1] = color2;
	this.gradienteHorizontal = gradienteHorizontal;
    }

    public void setImagen(String nombreImagen) {
	if (nombreImagen != null) {
	    URL url = getClass().getResource(nombreImagen);
	    this.imagen = new ImageIcon(url).getImage();
	    repaint();
	}
	else {
	    this.imagen = null;
	}
    }

    public void setImagen(Image imagen) {
	this.imagen = imagen;
	this.repaint();
    }

    public void setImagen(byte[] imagen) {
	try {
	    this.imagen = (new ImageIcon(imagen)).getImage();
	}
	catch(Exception e) {
	    e.printStackTrace();
	}	
	repaint();
    }
    
    public Image getImagen() {
	return this.imagen;
    }
    
    public void setColores(Color color1, Color color2, boolean gradienteHorizontal) {
	Border border = this.getBorder();
	this.colores[0] = color1;
	this.colores[1] = color2;
	this.gradienteHorizontal = gradienteHorizontal;
	this.setBorder(border);
	repaint();
	this.setBorder(border);
    }
    
    private void setEnabledComponent(Component component, boolean enabled) {
	if (component instanceof JPanel ||
	    component instanceof JTabbedPane) {
	    for (Component componentChild: ((Container)component).getComponents()) {
		this.setEnabledComponent(componentChild, enabled);
	    }
	}
	else if (!(component instanceof JLabel)) {
	    component.setEnabled(enabled);		
	}
    }
    
    @Override
    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
	for (Component component : this.getComponents()) {
	    this.setEnabledComponent(component, enabled);
	}
    }
    
    @Override
    public boolean isEnabled() {
	return this.enabled;
    }
    
    @Override
    public void paint(Graphics g) {	    
	if (imagen != null) {
	    g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
	    setOpaque(false);
	    super.paintChildren(g);
	    super.paint(g);		
	}
	else {	
	    setOpaque(true);
	    if (colores != null &&
		    colores[0] != null &&
		    colores[1] != null) {
		Graphics2D g2 = (Graphics2D) g;
		if (gradienteHorizontal) {
		    g2.setPaint(new GradientPaint(0, 0, this.colores[0],0,getHeight(), this.colores[1]));
		}
		else {
		    g2.setPaint(new GradientPaint(0, 0, this.colores[0],getWidth(),0, this.colores[1]));
		}
		g2.fillRect(0, 0, getWidth(), getHeight());
		super.paintChildren(g2);
	    }
	    else {		    
		super.paint(g);
	    }
	}	    	    
    }
}
