/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package desenho.formas;

import desenho.Elementar;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class DimensionadorArea extends Elementar {

    private static final long serialVersionUID = -625690746882991181L;

    public DimensionadorArea(FormaArea reg) {
        super(reg);
        //setVisible(false);
        this.setWidth(getMaster().getPontoWidth());
        this.setHeight(getMaster().getPontoHeigth() * 4);
        this.setBackColor(Color.BLACK);
        regiao = reg;
    }

    private final FormaArea regiao;
    
    boolean isHide = false;

    public boolean getIsHide() {
        return isHide;
    }

    public void setIsHide(boolean ishide) {
        this.isHide = ishide;
    }

    @Override
    public boolean isCanPaint() {
        return super.isCanPaint() && !getIsHide();
    }

    @Override
    public void DoPaint(Graphics2D g) {
        if ((regiao == null) /*|| (getDono().isAtualizando())*/) {
            return;
        }
        super.DoPaint(g);
        g.setColor(this.getBackColor());
        g.fillRect(getLeft(), getTop(), getWidth(), getHeight());
    }

    // <editor-fold defaultstate="collapsed" desc="Mouse">
    Point inidown = new Point(0, 0);

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        inidown = new Point(e.getX(), e.getY());
        isMouseDown = true;
        down = new Point(e.getX(), e.getY());
   }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMouseDown = false;
        FormaArea dono = regiao;
        dono.Reenquadre();
        dono.DoRaizeReenquadreReposicione();

        Point enddown = new Point(e.getX(), e.getY());
        if (!enddown.equals(inidown)) {
            dono.DoMuda();
        }
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        FormaArea dono = regiao;
        super.mouseDragged(e);
        int X = e.getX();
        if (isMouseDown) {
            if ((X - down.getX()) != 0) {
                int mov = X - down.x;
                int b = getLargura(); //pego a largura anterior
                setLargura(getLargura() + mov);
                if (b != getLargura()){
                    
                    b = getLargura() - b; //quanto moveu.
                    
                    if (b  != mov) X-= mov - b; //moveu menos do que devia? se sim, reduza a diferença de onde ficará o último down.
                    down.setLocation(new Point(X, e.getY()));
                }
                dono.RePosicioneRegioes();
            }
        }
    }

    // </editor-fold>
    
    public void Posicione() {
        FormaArea fa =  regiao;
        int top = regiao.getTop() + regiao.getAlturaTexto();
        int t = top + (fa.getHeight() - getHeight()) / 2;
        int l = regiao.getLocalDaLinha(this);
        SetBounds(l, t, getWidth(), getHeight());
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.E_RESIZE_CURSOR));
    }
    
    Point down = new Point(0, 0);
    boolean isMouseDown = false;
    public boolean AllwaysHide = false;

    @Override
    public void mouseDblClicked(MouseEvent e) {
        regiao.Remova(this);
    }
    
        
    private int largura = 10;

    public int getLargura() {
        return largura;
    }

    public void setLargura(int largura) {
        this.largura = largura;
        if (this.largura < getWidth()/2) {
            this.largura = getWidth()/2;
        }
    }
    
    private String texto = "";

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
    
    protected void ToXmlValores(Document doc, Element root) {
        Element me = doc.createElement("Dimensao");
        me.appendChild(util.XMLGenerate.ValorString(doc, "Texto", getTexto()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Largura", getLargura()));
        root.appendChild(me);
    }
    
    public boolean LoadFromXML(Element me, boolean colando) {
        setTexto(util.XMLGenerate.getValorStringFrom(me, "Texto"));
        setLargura(util.XMLGenerate.getValorIntegerFrom(me, "Largura"));
        return true;
    }
}

