/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.apoios;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author ccandido
 */
public class PainelPintador extends JPanel {

    public PainelPintador() {
    }

    public PainelPintador(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public PainelPintador(LayoutManager layout) {
        super(layout);
    }

    public PainelPintador(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    @Override
    public void paint(Graphics g) {
        RenderingHints renderHints =
                new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        Graphics2D Canvas = (Graphics2D) g;
        Canvas.addRenderingHints(renderHints);

        super.paint(g);
        for (IObjetoPintavel p: pintaveis) {
            p.setOutroPintor(true);
            p.DoPaint(Canvas);
            p.setOutroPintor(false);
        }
    }

    private ArrayList<IObjetoPintavel> pintaveis = new ArrayList<>();

    public ArrayList<IObjetoPintavel> getPintaveis() {
        return pintaveis;
    }

    public void setPintaveis(ArrayList<IObjetoPintavel> pintaveis) {
        this.pintaveis = pintaveis;
    }
}
