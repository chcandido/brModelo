/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.livre;

import controlador.Diagrama;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 *
 * @author SAA
 */
public class LivreSuperTexto extends LivreBase {

    private static final long serialVersionUID = 5883576226863251810L;
    
    public LivreSuperTexto(Diagrama modelo) {
        super(modelo);
        setTipoDesenho(LivreBase.TipoDraw.tpTexto);
    }

    public LivreSuperTexto(Diagrama modelo, String texto) {
        super(modelo, texto);
        setTipoDesenho(LivreBase.TipoDraw.tpTexto);
    }

    @Override
    public void PinteTexto(Graphics2D g) {
        
        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlfa()));
        g.setPaint(this.getForeColor());

        if (isGradiente()) {
            int dist = 0;
            int w = getWidth() - dist;
            int h = getHeight() - dist;
            int L = getLeft();
            int T = getTop();
            boolean dv = getGDirecao() == VERTICAL;

            GradientPaint GP = new GradientPaint(L, T, getGradienteStartColor(), dv ? L : L + w, dv ? T + h : T, getGradienteEndColor(), true);
            g.setPaint(GP);
        }

        Stroke bkp = g.getStroke();
        if (isDashed()) {
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
        }
        
        if (getMaster().getZoom() != z) {
            setTextoFormatado(null);
            z = getMaster().getZoom();
        }
        
        getTextoFormatado().PinteTexto(g, getArea(), getTexto());
    
        g.setStroke(bkp);
        g.setComposite(originalComposite);
    }
    
    private transient double z = 0.0;
    
}
