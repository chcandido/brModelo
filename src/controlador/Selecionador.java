package controlador;

import desenho.formas.Forma;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Rick
 */
public class Selecionador extends Forma {

    public Selecionador(Diagrama diagrama) {
        super(diagrama);
        setVisible(false);
        setSelecionavel(false);
    }

    public Selecionador(Diagrama diagrama, String texto) {
        super(diagrama, texto);
        setVisible(false);
        setSelecionavel(false);
    }

    @Override
    public void DoPaint(Graphics2D g) {
        Paint bkppaint = g.getPaint();
        //super.DoPaint(g); //To change body of generated methods, choose Tools | Templates.
        Graphics2D Canvas = g;
        Stroke stroke = g.getStroke();
        Canvas.setStroke(new BasicStroke(
                1f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                2f,
                new float[]{2f, 2f},
                1f));

        //Composite ori = g.getComposite();
        //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 1f));

        Canvas.setPaint(Color.BLACK);
        Canvas.drawRect(getLeft(), getTop(), getWidth() - 2, getHeight() - 2);
        g.setStroke(stroke);
        g.setPaint(bkppaint);//g.setComposite(ori);
    }

    public void Init(Point local) {
        this.setBounds(local.x, local.y, 1, 1);
        setVisible(true);
        BringToFront();
    }

    public void Finish() {
        setVisible(false);
    }
}
