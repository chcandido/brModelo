/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preAnyDiagrama;

import controlador.Diagrama;
import desenho.formas.FormaTextoBase;
import desenho.linhas.SuperLinha;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

/**
 *
 * @author ccandido
 */
public class PreTexto extends FormaTextoBase {

    private static final long serialVersionUID = 101922832291188735L;

    private boolean autosize = false;

    public PreTexto(Diagrama modelo) {
        super(modelo);
        Init();
    }

    public PreTexto(Diagrama modelo, String texto) {
        super(modelo, texto);
        titulo = getTexto();
        Init();
    }

    private void Init() {
        getTextoFormatado().LimitarAreaDePintura = true;
        setBackColor(Color.WHITE);
    }

    protected void ReSizedByAutoSize() {
    }

    public enum TipoTexto {

        tpEmBranco, tpNota, tpRetangulo, tpRetanguloArred
    }
    private TipoTexto Tipo = TipoTexto.tpNota;

    public TipoTexto getTipo() {
        return Tipo;
    }
    private boolean sombra = true;

    public void setTipo(TipoTexto Tipo) {
        this.Tipo = Tipo;
        InvalidateArea();
    }

    public void setTipobyInt(int Tipo) {
        TipoTexto inttp = TipoTexto.tpRetangulo;

        try {
            inttp = TipoTexto.values()[Tipo];
        } catch (Exception e) {
        }

        this.Tipo = inttp;
        Rectangle rec = getBounds();
        rec.grow(distSelecao, distSelecao);
        InvalidateArea(rec);
    }

    public boolean isSombra() {
        return sombra;
    }

    public void setSombra(boolean Sombra) {
        this.sombra = Sombra;
        Invalidate();
    }
    private Color corSombra = new Color(51, 51, 51);

    public Color getCorSombra() {
        return isDisablePainted()? disabledColor : corSombra;
    }

    public void setCorSombra(Color corSombra) {
        if (this.corSombra != corSombra) {
            this.corSombra = corSombra;
            InvalidateArea(getSuperArea());
        }
    }
    private boolean gradiente = false;
    private Color gradienteEndColor = new Color(204, 204, 204, 255);//Color.WHITE;
    private Color gradienteStartColor = Color.BLACK;
    private boolean gradientePinteDetalhe = true;

    public Color getGradienteCorDetalhe() {
        return isDisablePainted()? disabledColor : gradienteCorDetalhe;
    }

    public void setGradienteCorDetalhe(Color gradienteCorDetalhe) {
        this.gradienteCorDetalhe = gradienteCorDetalhe;
        InvalidateArea();
    }

    public boolean isGradientePinteDetalhe() {
        return gradientePinteDetalhe;
    }

    public void setGradientePinteDetalhe(boolean gradientePinteDetalhe) {
        this.gradientePinteDetalhe = gradientePinteDetalhe;
        InvalidateArea();
    }
    private Color gradienteCorDetalhe = new Color(102, 102, 102);

    public Color getGradienteStartColor() {
        return isDisablePainted()? disabledColor : gradienteStartColor;
    }

    public void setGradienteStartColor(Color gradienteStartColor) {
        this.gradienteStartColor = gradienteStartColor;
        InvalidateArea();
    }

    public boolean isGradiente() {
        return gradiente;
    }

    public void setGradiente(boolean gradiente) {
        this.gradiente = gradiente;
        InvalidateArea();
    }

    public Color getGradienteEndColor() {
        return isDisablePainted()? disabledColor : gradienteEndColor;
    }

    public void setGradienteEndColor(Color gradienteEndColor) {
        this.gradienteEndColor = gradienteEndColor;
        InvalidateArea();
    }

    private transient int fonteH = 0;
    
    @Override
    public void DoPaint(Graphics2D g) {
        Composite originalComposite = g.getComposite();

        switch (Tipo) {
//            case tpEmBranco:
//                break;
            case tpNota:
//                if (isGradiente() && !isTextoSimples()) {
                if (isGradiente()) {
                    int dist = 0;
                    int w = getWidth() - dist;
                    int h = getHeight() - dist;
                    int L = getLeft();
                    int T = getTop();
                    boolean dv = getGDirecao() == VERTICAL;

                    GradientPaint GP = new GradientPaint(L, T, getGradienteStartColor(), dv ? L : L + w, dv ? T + h : T, getGradienteEndColor(), true);
                    g.setPaint(GP);
                } else {
                    //g.setColor(this.getForeColor());
                    g.setColor(this.getBackColor());
                }
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));

                GeneralPath pa = new GeneralPath();
                pa.setWindingRule(GeneralPath.WIND_NON_ZERO);

                Rectangle rec = getBounds();
                int tam = Math.min(rec.width / 6, rec.height / 6);
                int curv = tam / 4;
                int lw = rec.x + rec.width;
                int[] px = new int[]{rec.x, lw - tam, lw, lw, rec.x};
                int[] py = new int[]{rec.y, rec.y, rec.y + tam, rec.y + rec.height, rec.y + rec.height};
                Polygon po = new Polygon(px, py, 5);
                pa.append(po, true);
                pa.moveTo(lw - tam, rec.y);
                pa.curveTo(lw - tam, rec.y, lw - tam + curv, rec.y + curv, lw - tam, rec.y + tam - (1));
                pa.moveTo(lw - tam, rec.y + tam - (1));
                pa.lineTo(lw, rec.y + tam);
                pa.closePath();
//                if (isGradiente() && !isTextoSimples()) {
                if (isGradiente()) {
                    g.fill(pa);
                    g.draw(pa);
                } else {
//                    if (!isTextoSimples()) g.fill(pa);
                    g.fill(pa);
                    g.setColor(this.getForeColor());
                    g.draw(pa);
                }
                break;
            case tpRetangulo:
                //getTextoFormatado().CorretorPosicao = new Point(-2, -2);
                if (sombra) {
                    g.setPaint(getCorSombra());
                    for (int i = 0; i < distSelecao; i++) {
                        g.drawLine(getLeft() + distSelecao, getTopHeight() + i, getLeftWidth() + distSelecao - 1, getTopHeight() + i);
                        g.drawLine(getLeftWidth() + i, getTop() + distSelecao, getLeftWidth() + i, getTopHeight() + distSelecao - 1);
                    }
                }
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
                g.setPaint(this.getBackColor());
                if (isGradiente()) {
                    PaintGradiente(g, false);
                } else {
                    g.fillRect(getLeft(), getTop(), getWidth(), getHeight());
                    if (sombra) {
                        g.setPaint(getCorSombra());
                    } else {
                        g.setPaint(getForeColor());
                    }
                    g.drawRect(getLeft(), getTop(), getWidth(), getHeight());
                }
                break;
            case tpRetanguloArred:
                //getTextoFormatado().CorretorPosicao = new Point(-2, -2);
                if (sombra) {
                    int rx = roundRectSize / 2;
                    g.setPaint(getCorSombra());
                    for (int i = 0; i < distSelecao; i++) {
                        g.drawLine(getLeft() + rx, getTopHeight() + i, getLeftWidth() + distSelecao - 1, getTopHeight() + i);
                        g.drawLine(getLeftWidth() + i, getTop() + rx, getLeftWidth() + i, getTopHeight() + distSelecao - 1);
                    }
                    g.fillPolygon(new int[]{getLeftWidth() - rx, getLeftWidth(), getLeftWidth()},
                            new int[]{getTopHeight(), getTopHeight() - rx, getTopHeight()}, 3);
                }
                g.setPaint(this.getBackColor());
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
                if (isGradiente()) {
                    PaintGradiente(g, true);
                } else {
                    g.fillRoundRect(getLeft(), getTop(), getWidth(), getHeight(), roundRectSize, roundRectSize);
                    if (sombra) {
                        g.setPaint(getCorSombra());
                    } else {
                        g.setPaint(getForeColor());
                    }
                    g.drawRoundRect(getLeft(), getTop(), getWidth(), getHeight(), roundRectSize, roundRectSize);
                }
                break;
        }
        g.setColor(this.getForeColor());
        if (isPaintTitulo()) {
            g.setFont(getFont());
            if (fonteH == 0) {
                FontMetrics fm = g.getFontMetrics();
                fonteH = fm.getHeight();
            }
            int db = distSelecao * 3;
            Rectangle bkp = g.getClipBounds();
            Rectangle cl = getClientRectangle();
            g.clipRect(cl.x, cl.y, cl.width, cl.height);
            int des = fonteH + fonteH/2;
            g.drawString(getTitulo(), getLeft() + db, getTop() + des);
            g.setClip(bkp);
            int av = db + des;
            getTextoFormatado().CorretorPosicao = new Point(db, av + db);
        } else {
            getTextoFormatado().CorretorPosicao = new Point(distSelecao*2, distSelecao*2);
        }
        g.setComposite(originalComposite);
        super.DoPaint(g);
        if (Tipo == TipoTexto.tpEmBranco) {
            if (isAutosize()) {
                FontMetrics fm = g.getFontMetrics();
                int x = -1, y = -1;
                int a = getTextoFormatado().getMaxWidth() + (distSelecao * 2) + fm.charWidth('W');
                if (a != getWidth()) {
                    x = a;
                }
                a = getTextoFormatado().getMaxHeigth() + distSelecao;
                if (a != getHeight()) {
                    y = a;
                }
                if (y != -1 || x != -1) {
                    setStopRaize(true);
                    if (x != -1) {
                        setWidth(x);
                    }
                    if (y != -1) {
                        setHeight(y);
                    }
                    setStopRaize(false);
                    needRecalPts = true;
                    if (isSelecionado()) {
                        Reposicione();
                    }
                    ReSizedByAutoSize();
                }
            }
        }
        //g.setComposite(originalComposite);
    }
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    private final int roundRectSize = 6 * distSelecao;
    private int gdirecao = VERTICAL;
    private String titulo = "";
    private boolean paintTitulo = false;

    private float alfa = 0.8f;

    public float getAlfa() {
        return alfa;
    }

    public void setAlfa(float alfa) {
        this.alfa = alfa;
    }

    public void SetAlfa(int alfa) {
        this.alfa = (float) alfa / 100;
        if (this.alfa > 1) {
            this.alfa = 0.5f;
        }
        InvalidateArea();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (!this.titulo.equals(titulo)) {
            this.titulo = titulo;
            InvalidateArea();
        }
    }

    public boolean isPaintTitulo() {
        return paintTitulo;
    }

    public void setPaintTitulo(boolean paintTitulo) {
        if (this.paintTitulo != paintTitulo) {
            this.paintTitulo = paintTitulo;
            InvalidateArea();
        }
    }

    public int getGDirecao() {
        return gdirecao;
    }

    public void setGDirecao(int aDirection) {
        gdirecao = aDirection;
        InvalidateArea();
    }

    protected void PaintGradiente(Graphics2D g, boolean round) {
        int dist = 0;
        int w = getWidth() - dist;
        int h = getHeight() - dist;
        int L = getLeft();
        int T = getTop();
        boolean dv = getGDirecao() == VERTICAL;

        //Composite originalComposite = g.getComposite();
        //g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, alfa));
        GradientPaint GP = new GradientPaint(L, T, getGradienteStartColor(), dv ? L : L + w, dv ? T + h : T, getGradienteEndColor(), true);
        //g.setPaint(GP);

        g.setPaint(getForeColor());
        if (round) {
            g.drawRoundRect(L, T, w - 1, h - 1, roundRectSize, roundRectSize);
            g.setPaint(GP);
            g.fillRoundRect(L + 1, T + 1, w - 2, h - 2, roundRectSize, roundRectSize);
            g.setPaint(isDisablePainted()? disabledColor : Color.WHITE);
            g.drawRoundRect(L + 1, T + 1, w - 3, h - 3, roundRectSize, roundRectSize);
        } else {
            g.drawRect(L, T, w - 1, h - 1);
            g.setPaint(GP);
            g.fillRect(L + 1, T + 1, w - 2, h - 2);
            g.setPaint(isDisablePainted()? disabledColor : Color.WHITE);
            g.drawRect(L + 1, T + 1, w - 3, h - 3);
        }
        if (isGradientePinteDetalhe()) {
            g.setPaint(getGradienteCorDetalhe());
            GeneralPath path = new GeneralPath();
            path.moveTo(L + 2, T + 2);
            path.quadTo(L + w / 2 + 1, T + h / 2 + 1, L + w - 1, T + 2);
            path.closePath();
            g.fill(path);
        }
        //g.setComposite(originalComposite);

    }

    @Override
    public void EscrevaTexto(ArrayList<String> txts) {
        txts.add(getTitulo());
    }

    public boolean isAutosize() {
        return autosize;
    }

    public void setAutosize(boolean autosize) {
        if (this.autosize == autosize) {
            return;
        }
        this.autosize = autosize;
        InvalidateArea();
    }

    public void Posicione() {
    }

    public void SetLinhaMestre(SuperLinha LinhaMestre) {
    }

    @Override
    public void PinteTexto(Graphics2D g) {
        super.PinteTexto(g);
        if (getTextoFormatado().CorretorPosicao.equals(new Point(0, 0))) { //# foi destruído o TextoFormatado e perdeu-se a correcao.
            InvalidateArea();
        }
    }
}
