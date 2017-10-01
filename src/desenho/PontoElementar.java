/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 *
 * @author Rick
 */
public class PontoElementar extends Elementar {

    private static final long serialVersionUID = 7864045481197956704L;

    public PontoElementar(FormaElementar pai) {
        super(pai);
        InicieSemVazamentos();
    }

    private void InicieSemVazamentos() {
        setVisible(false);
        this.setWidth(getMaster().getPontoWidth());
        this.setHeight(getMaster().getPontoHeigth());
        this.setBackColor(Color.BLACK);
        recuo = (getMaster().getPontoWidth() / 2);
    }

    private int recuo;

    public int getRecuo() {
        return recuo;
    }

    public void setRecuo(int recuo) {
        this.recuo = recuo;
    }

    public int getPosicao() {
        return 0;
    }

    public void setPosicao(int posicao) {
    }

    boolean isHide = false;

    public boolean getIsHide() {
        return isHide;
    }

    public void setIsHide(boolean ishide) {
        this.isHide = ishide;
    }

    public FormaElementar getDono() {
        return getCriador();
    }

    @Override
    public boolean isCanPaint() {
        return super.isCanPaint() && !getIsHide();
    }

    public Point getCentro() {
        return new Point(getLeft() + (getWidth() / 2), getTop() + (getHeight() / 2));
    }

    public void setCentro(int x, int y) {
        setLocation(new Point(x - (getWidth() / 2), y - (getHeight() / 2)));
    }

    public void setCentro(Point centro) {
        setCentro(centro.x, centro.y);
    }

    @Override
    public void Reposicione() {
        super.Reposicione();
    }

    @Override
    public void DoPaint(Graphics2D g) {
//        if (!CanPaint()) {
//            return;
//        }
        if ((getDono() == null) /*|| (getDono().isAtualizando())*/) {
            return;
        }
        super.DoPaint(g);
        g.setColor(this.getBackColor());
        g.fillOval(getLeft(), getTop(), getWidth(), getHeight());

    }

    // <editor-fold defaultstate="collapsed" desc="Mouse">
    Point inidown = new Point(0, 0);

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        inidown = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point enddown = new Point(e.getX(), e.getY());
        if (!enddown.equals(inidown)) {
            getDono().DoMuda();
        }
        super.mouseReleased(e);
    }

    // </editor-fold>
//    /**
//     * Acredito que deverá ser usado apenas para serializar a linha para xml.
//     */
//    public void ToXlm(Document doc, Element root) {
//        Element me = doc.createElement(Editor.getClassTexto(this));
//        ToXmlAtributos(doc, me);
//        InfoDiagrama_ToXmlValores(doc, me);
//        root.appendChild(me);
//    }
//
//    /**
//     * Acredito que deverá ser usado apenas para serializar a linha para xml.
//     */
//    protected void InfoDiagrama_ToXmlValores(Document doc, Element me) {
//    }
//    
//    /**
//     * Acredito que deverá ser usado apenas para serializar a linha para xml.
//     */
//    protected void ToXmlAtributos(Document doc, Element me) {
//    }
    @Override
    public Color getBackColor() {
        if (isDisablePainted()) {
            setDisablePainted(false);
            Color c = super.getBackColor();
            setDisablePainted(true);
            return c;
        } else {
            return super.getBackColor();
        }
    }

}
