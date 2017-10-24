/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

/**
 *
 * @author ccandido
 */
public class DesenhadorDeTexto implements Serializable {

    private static final long serialVersionUID = -1504310755614588296L;

    public DesenhadorDeTexto() {
    }

    public DesenhadorDeTexto(String texto, Font font, boolean centraTexto) {
        setTexto(texto);
        this.font = font;
        this.CentrarTextoVertical = centraTexto;
        this.setCentrarTextoHorizontal(centraTexto);
    }
    // <editor-fold defaultstate="collapsed" desc="Alinhamento">
    private boolean CentrarTextoHorizontal = false;

    public boolean isCentrarTextoHorizontal() {
        return CentrarTextoHorizontal;
    }

    public void setCentrarTextoHorizontal(boolean CentrarTextoHorizontal) {
        this.CentrarTextoHorizontal = CentrarTextoHorizontal;
        if (CentrarTextoHorizontal) {
            this.AlinharEsquerda = false;
            this.AlinharDireita = false;
        }
    }
    private boolean CentrarTextoVertical = false;

    public boolean isCentrarTextoVertical() {
        return CentrarTextoVertical;
    }

    public void setCentrarTextoVertical(boolean CentrarTexto) {
        this.CentrarTextoVertical = CentrarTexto;
    }
    private boolean AlinharEsquerda = true;

    public boolean isAlinharEsquerda() {
        return AlinharEsquerda;
    }

    public void setAlinharEsquerda(boolean AlinharEsquerda) {
        this.AlinharEsquerda = AlinharEsquerda;
        if (AlinharEsquerda) {
            this.CentrarTextoHorizontal = false;
            this.AlinharDireita = false;
        }
    }
    private boolean AlinharDireita = false;

    public boolean isAlinharDireita() {
        return AlinharDireita;
    }

    public void setAlinharDireita(boolean AlinharDireita) {
        this.AlinharDireita = AlinharDireita;
        if (AlinharDireita) {
            this.AlinharEsquerda = false;
            this.CentrarTextoHorizontal = false;
        }
    }
    // </editor-fold>
    public Point CorretorPosicao = new Point(0, 0);
    private String Texto = "";
    private String[] Textos;
    private transient LineBreakMeasurer[] lbmTexto;

    public String getTexto() {
        return Texto;
    }

    public void setTexto(String Texto) {
        //if (!this.Texto.equals(Texto)) {
        this.Texto = Texto;
        if (Texto == null || Texto.equals("")) {
            return;
        }
        Textos = Texto.split("\n");
        lbmTexto = null;
        //}
    }
    private Font font;

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
        lbmTexto = null;
    }
    
    public boolean LimitarAreaDePintura = true;
    private int MaxWidth = 0;

    public int getMaxWidth() {
        return MaxWidth;
    }
    private int MaxHeigth = 0;

    public int getMaxHeigth() {
        return MaxHeigth;
    }

    public void PinteTexto(Graphics2D g, Color foreColor, Rectangle clientArea, String texto) {
        //Font bkp = g.getFont();
        //g.setFont(this.getFont());
        if (!Texto.equals(texto)) {
            setTexto(texto);
        }

        if (Texto.equals("") || Texto == null) {
            return;
        }

        g.setPaint(foreColor);
        LineBreakMeasurer[] lbms = getLineBreakMeasurers(g);

        if (lbms == null || lbms.length == 0) {
            return;
        }

        Rectangle bkp = g.getClipBounds();
        if (LimitarAreaDePintura) {
            g.clipRect(clientArea.x, clientArea.y, clientArea.width, clientArea.height);
        }

        //boolean isCentro = isCentrarTextoVertical();
        int recuo = 2;

        int x = clientArea.x + recuo + CorretorPosicao.x;
        int y = clientArea.y + CorretorPosicao.y;
        int w = clientArea.width - (2 * recuo) - CorretorPosicao.x;
        int h = clientArea.height - CorretorPosicao.y;

        float wrappingWidth = w;
        MaxHeigth = 0;
        MaxWidth = 0;

        if (isCentrarTextoVertical()) {
            int esp = 0;
            float corr = 0;
            for (int i = 0; i < lbms.length; i++) {
                LineBreakMeasurer lbm = lbms[i];
                lbm.setPosition(0);
                while (lbm.getPosition() < Textos[i].length()) {
                    TextLayout layout;
                    try {
                        layout = lbm.nextLayout(wrappingWidth);
                    } catch (Exception e) {
                        if (LimitarAreaDePintura) {
                            g.setClip(bkp);
                        }
                        return;
                    }
                    corr = layout.getDescent() + layout.getLeading();
                    esp += layout.getAscent() + corr;
                }
            }
            esp -= corr;
            y = ((h - esp) / 2) + y;
        }

        for (int i = 0; i < lbms.length; i++) {
            LineBreakMeasurer lbm = lbms[i];
            lbm.setPosition(0);
            while (lbm.getPosition() < Textos[i].length()) {
                TextLayout layout;
                try {
                    layout = lbm.nextLayout(wrappingWidth);
                } catch (Exception e) {
                    if (LimitarAreaDePintura) {
                        g.setClip(bkp);
                    }
                    return;
                }
                y += layout.getAscent();

                int le = x;
                int larg = (int) layout.getBounds().getWidth();
                if (isCentrarTextoHorizontal()) {
                    le = x + ((w - larg) / 2);
                } else if (isAlinharDireita()) {
                    le = x + w - larg;
                }

                layout.draw(g, le, y);
                MaxHeigth = y;
                y += layout.getDescent() + layout.getLeading();
                MaxWidth = Math.max(MaxWidth, larg);
            }
        }
        MaxHeigth -= clientArea.y;

        if (LimitarAreaDePintura) {
            g.setClip(bkp);
        }
        //g.drawString(Integer.toString(getFont().getSize()), x, y);
        //g.setFont(bkp);
    }
    
    public void PinteTexto(Graphics2D g, Rectangle clientArea, String texto) {
        Font bkpf = g.getFont();
        g.setFont(this.getFont());
        if (!Texto.equals(texto)) {
            setTexto(texto);
        }

        if (Texto.equals("") || Texto == null) {
            return;
        }

        LineBreakMeasurer[] lbms = getLineBreakMeasurers(g);

        if (lbms == null || lbms.length == 0) {
            return;
        }

        Rectangle bkp = g.getClipBounds();
        if (LimitarAreaDePintura) {
            g.clipRect(clientArea.x, clientArea.y, clientArea.width, clientArea.height);
        }

        //boolean isCentro = isCentrarTextoVertical();
        int recuo = 2;

        int x = clientArea.x + recuo + CorretorPosicao.x;
        int y = clientArea.y + CorretorPosicao.y;
        int w = clientArea.width - (2 * recuo) - CorretorPosicao.x;
        int h = clientArea.height - CorretorPosicao.y;

        float wrappingWidth = w;
        MaxHeigth = 0;
        MaxWidth = 0;

        if (isCentrarTextoVertical()) {
            int esp = 0;
            float corr = 0;
            for (int i = 0; i < lbms.length; i++) {
                LineBreakMeasurer lbm = lbms[i];
                lbm.setPosition(0);
                while (lbm.getPosition() < Textos[i].length()) {
                    TextLayout layout;
                    try {
                        layout = lbm.nextLayout(wrappingWidth);
                    } catch (Exception e) {
                        if (LimitarAreaDePintura) {
                            g.setClip(bkp);
                        }
                        return;
                    }
                    corr = layout.getDescent() + layout.getLeading();
                    esp += layout.getAscent() + corr;
                }
            }
            esp -= corr;
            y = ((h - esp) / 2) + y;
        }

        for (int i = 0; i < lbms.length; i++) {
            LineBreakMeasurer lbm = lbms[i];
            lbm.setPosition(0);
            while (lbm.getPosition() < Textos[i].length()) {
                TextLayout layout;
                try {
                    layout = lbm.nextLayout(wrappingWidth);
                } catch (Exception e) {
                    if (LimitarAreaDePintura) {
                        g.setClip(bkp);
                    }
                    return;
                }
                y += layout.getAscent();

                int le = x;
                int larg = (int) layout.getBounds().getWidth();
                if (isCentrarTextoHorizontal()) {
                    le = x + ((w - larg) / 2);
                } else if (isAlinharDireita()) {
                    le = x + w - larg;
                }

                layout.draw(g, le, y);
                MaxHeigth = y;
                y += layout.getDescent() + layout.getLeading();
                MaxWidth = Math.max(MaxWidth, larg);
            }
        }
        MaxHeigth -= clientArea.y;

        if (LimitarAreaDePintura) {
            g.setClip(bkp);
        }
        //g.drawString(Integer.toString(getFont().getSize()), x, y);
        g.setFont(bkpf);
    }

    private LineBreakMeasurer[] getLineBreakMeasurers(Graphics2D g) {
        if (lbmTexto == null && (Texto != null && !Texto.equals(""))) {
            lbmTexto = new LineBreakMeasurer[Textos.length];
            for (int i = 0; i < lbmTexto.length; i++) {
                String tmp = Textos[i].isEmpty()? " " : Textos[i];
                AttributedString attribString = new AttributedString(tmp);
                attribString.addAttribute(TextAttribute.FONT, getFont());
                //attribString.addAttribute(TextAttribute.FONT, getFont());
                AttributedCharacterIterator attribCharIterator = attribString.getIterator();
                //FontRenderContext frc = new FontRenderContext(null, true, false);
                FontRenderContext frc = g.getFontRenderContext();
                lbmTexto[i] = new LineBreakMeasurer(attribCharIterator, frc);
            }
        }
        return lbmTexto;
    }
}
