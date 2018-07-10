/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 *
 * @author ccandido
 */
public class InspectorItemSeparador extends InspectorItemBase {

    public InspectorItemSeparador(Inspector criador) {
        super(criador);
        Color c = getBackground();
        c = new Color(c.getRed(), c.getGreen(),  c.getBlue() - 15);
        //:BUG CORRIGIDO: Vinicius Oliveira Queiroz.
        setBackground(c);
    }

    public InspectorItemSeparador() {
        super();
        Color c = getBackground();
        c = new Color(c.getRed(), c.getGreen(), c.getBlue() - 15);
        //:BUG CORRIGIDO: Vinicius Oliveira Queiroz.
        setBackground(c);
    }

    /**Configura InspectorItemSeparador para ser base do componente. Um item a mais, invisível (visível, porém, invisível por conta da cor) colocado para facilitar o redesenho do inspector quando se oculta os últimos itens*/
    public boolean endOFF = false;
    
    @Override
    protected void paintBase(Graphics2D g) {
        if (endOFF) {
            return;
        }
        setArea(null);
        Rectangle r = this.getBounds();
        g.setColor(Color.GRAY);
        g.drawRoundRect(0, 0, r.width - 1, r.height - 1, 10, 10);

        g.setColor(Color.lightGray);
        g.drawRoundRect(5, 5, r.height - 10, r.height - 10, 4,4);

        int met = (r.height - 11) / 2;
        g.setColor(Color.black);
        g.drawLine(7, 6 + met, r.height - 7, 6 + met);
        if ('+' == getEstado()) {
            g.drawLine(6 + met, 7, 6 + met, r.height - 7);
        }

        g.setColor(Color.BLACK);
        Rectangle bkp = g.getClipBounds();
        g.clipRect(0, 0, r.width - 1, r.height);
        if (isSelecionado()) {
            g.setFont(new Font(this.getFont().getFontName(), Font.BOLD, getFont().getSize()));
            g.drawRoundRect(0, 0, r.width - 1, r.height - 1, 10, 10);
        }
        int tmp = (r.width - g.getFontMetrics().stringWidth(getTexto())) / 2;

        g.drawString(getTexto(), tmp, (int) (r.height * 0.72));
        g.setClip(bkp);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (endOFF) return;
        super.mousePressed(e);
        Rectangle r = this.getBounds();
        r = new Rectangle(5, 5, r.height - 10, r.height - 10);
        if (!r.contains(e.getPoint())) return;
        if ('+'  == getEstado()) {
            setEstado('-'); 
        } else {
            setEstado('+');
        }
        Criador.HideShow(this, getEstado());
    }

    private char estado = '-';

    public char getEstado() {
        return estado;
    }

    public void setEstado(char estado) {
        if (this.estado != estado) {
            this.estado = estado;
            repaint();
        }
    }
}
