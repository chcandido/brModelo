/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.livre;

import controlador.Diagrama;
import desenho.linhas.PontoDeLinha;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author SAA
 */
public class LivreJuncao extends LivreBase {

    private static final long serialVersionUID = -6937144218354697754L;

    public LivreJuncao(Diagrama modelo, String texto) {
        super(modelo, texto);
        setTipoDesenho(LivreBase.TipoDraw.tpCirculo);
        setDashed(true);
        editFonte = false;
        noNewProperty = true;
    }

    public LivreJuncao(Diagrama modelo) {
        super(modelo);
        setTipoDesenho(LivreBase.TipoDraw.tpCirculo);
        setDashed(true);
        editFonte = false;
        noNewProperty = true;
    }

    @Override
    protected void Posicione4Pontos(PontoDeLinha ponto) {
        super.Posicione4Pontos(ponto);
        int x = getLeft() + getWidth() / 2;
        int y = getTop() + getHeight() / 2;

        ponto.setCentro(x, y);
    }

    @Override
    public void PinteTexto(Graphics2D g) {
    }

    @Override
    public void ReciveFormaResize(Rectangle ret) {
        Rectangle rec = new Rectangle(0, 0, 0, 0);
        super.ReciveFormaResize(rec);
    }

    @Override
    public Shape getRegiaoCirculo() {
        if (Regiao == null) {
            Regiao = new Ellipse2D.Float(getLeft(), getTop(), getWidth(), getHeight());
        }
        return Regiao;
    }

    @Override
    protected void DoPaintDoks(Graphics2D g) {
        super.DoPaintDoks(g);
        Stroke bkp = g.getStroke();
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
        Shape t = new Ellipse2D.Float(getLeft(), getTop(), getWidth(), getHeight());
        g.draw(t);
        g.setStroke(bkp);
    }
    
    private transient boolean isover = false;

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        if (!isover){
            isover = true;
            InvalidateArea();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e); 
        if (isover) {
            isover = false;
            InvalidateArea();
        }
    }
    
    @Override
    protected void PinteRegiao(Graphics2D g) {
        g.setPaint(this.getForeColor());
        int x = getLeft() + getWidth() / 2;
        int y = getTop() + getHeight() / 2;
        g.draw(new Rectangle(x - 1, y - 1, 2, 2));
        if (isover || isSelecionado()) {
            Stroke bkp = g.getStroke();
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
            g.draw(getRegiao());
            g.setStroke(bkp);
        } 
    }
}
