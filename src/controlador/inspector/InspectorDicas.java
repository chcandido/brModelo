/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

import controlador.BaseControlador;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import util.DesenhadorDeTexto;

/**
 *
 * @author ccandido
 */
public class InspectorDicas extends BaseControlador {

    public InspectorDicas() {
        super();
    }
    private String Texto = "";

    public String getTexto() {
        return Texto;
    }

    public void setTexto(String Texto) {
        if (this.Texto == null || this.Texto.equals(Texto)) {
            return;
        }
        this.Texto = Texto;
        getTextoFormatado().setTexto(Texto);
        repaint();
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        getTextoFormatado().setFont(font);
    }
    private Color ForeColor = Color.BLACK;

    public Color getForeColor() {
        return ForeColor;
    }

    public void setForeColor(Color ForeColor) {
        this.ForeColor = ForeColor;
    }
    private DesenhadorDeTexto TextoFormatado = null;

    public DesenhadorDeTexto getTextoFormatado() {
        if (TextoFormatado == null) {
            TextoFormatado = new DesenhadorDeTexto(getTexto(), getFont(), false);
        }
        return TextoFormatado;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); //paint background

        Graphics2D Canvas = (Graphics2D) g;

        //Canvas.setPaint(Color.RED);
        //Canvas.draw3DRect(1, 1, getWidth() - 3, getHeight() - 3, true);
        Rectangle area = new Rectangle(0, 0, getWidth(), getHeight());
        getTextoFormatado().PinteTexto(Canvas, getForeColor(), area, getTexto());
    }
}
