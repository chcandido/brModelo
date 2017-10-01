/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.livre;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.formas.FormaNaoRetangularDisformeBase;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class LivreBase extends FormaNaoRetangularDisformeBase {

    private static final long serialVersionUID = 634717196393984568L;

    public LivreBase(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    public LivreBase(Diagrama modelo) {
        super(modelo);
    }

    public enum TipoDraw {

        tpTexto, tpRetangulo, tpRetanguloArred, tpCirculo, tpLosango, tpDocSimples, tpNota, tpVariosDocumentos, tpComentario
    }

    private TipoDraw tipoDesenho = TipoDraw.tpRetangulo;

    public TipoDraw getTipoDesenho() {
        return tipoDesenho;
    }

    protected void setTipoDesenho(TipoDraw tipoDesenho) {
        if (this.tipoDesenho != tipoDesenho) {
            setRegiao(null);
            ptsToMove = new int[]{-1, -1, -1, -1};
            this.tipoDesenho = tipoDesenho;
            InvalidateArea();
        }
    }

    @Override
    protected void PinteRegiao(Graphics2D g) {
        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));
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

        if (getTipoDesenho() != TipoDraw.tpTexto) {
            Stroke bkp = g.getStroke();
            if (isDashed()) {
                g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
            }
            if (isGradiente()) {
                g.fill(getRegiao());
            }
            g.draw(getRegiao()); //Pinta as eventuais linhas internas do dezenho. Ex. LivreVariosDocumemtos 

            g.setStroke(bkp);
        }
        g.setComposite(originalComposite);
    }

    public boolean isDashed() {
        return dashed;
    }

    public void setDashed(boolean dasehd) {
        if (this.dashed != dasehd) {
            this.dashed = dasehd;
            DoMuda();
            InvalidateArea();
        }
    }
    private boolean dashed = false;

    @Override
    public Shape getRegiao() {
        switch (getTipoDesenho()) {
            case tpRetanguloArred:
                return getRegiaoRecArred();
            case tpCirculo:
                return getRegiaoCirculo();
            case tpLosango:
                return getRegiaoLosanglo();
            case tpDocSimples:
                return getRegiaoDocumento();
            case tpNota:
                return getRegiaoNota();
            case tpVariosDocumentos:
                return getRegiaoVDocumentos();
            case tpComentario:
                return getRegiaoComentario();
        }
        return getRegiaoRec();
    }

    public Shape getRegiaoDocumento() {
        if (Regiao == null) {
            final int v1 = getHeight() / 3;
            final int h1 = getWidth() / 2;
            final int repo = v1 / 3;
            final int L = getLeft();
            final int T = getTop();
            final int TH = T + getHeight() - repo;
            final int LW = L + getWidth();
            CubicCurve2D c = new CubicCurve2D.Double();
            c.setCurve(L, TH, L + h1, TH + v1, LW - h1, TH - v1, LW, TH);
            GeneralPath pa = new GeneralPath();

            pa.moveTo(LW, TH);
            pa.lineTo(LW, T);
            pa.lineTo(L, T);
            pa.lineTo(L, TH);
            pa.append(c, true);
            Regiao = pa;
            final int ptToMove = 3;
            this.reposicionePonto[ptToMove] = new Point(0, -repo);
            ptsToMove[ptToMove] = 1;
        }
        return Regiao;
    }

    public Shape getRegiaoVDocumentos() {
        if (Regiao == null) {
            final int v1 = getHeight() / 3;
            final int h1 = getWidth() / 2;
            final int repo = v1 / 3;
            final int L = getLeft();
            int recuo = h1 / 8;
            final int T = getTop() + recuo;
            final int TH = T + getHeight() - repo - recuo;
            final int LW = L + getWidth() - recuo;
            CubicCurve2D c = new CubicCurve2D.Double();
            c.setCurve(L, TH, L + h1, TH + v1, LW - h1, TH - v1, LW, TH);
            GeneralPath pa = new GeneralPath();
            pa.setWindingRule(GeneralPath.WIND_EVEN_ODD);

            pa.moveTo(LW, TH);
            pa.lineTo(LW, T);
            pa.lineTo(L, T);
            pa.lineTo(L, TH);
            pa.append(c, false);

            int tam = recuo / 2;

            pa.moveTo(L + tam, T);
            pa.lineTo(L + tam, T - tam);
            pa.lineTo(LW + tam, T - tam);
            pa.lineTo(LW + tam, TH - tam);
            pa.lineTo(LW, TH - tam);
            pa.lineTo(LW, T);
            pa.lineTo(L + tam, T);

            tam = recuo;

            pa.moveTo(L + tam, T - (tam / 2));
            pa.lineTo(L + tam, T - tam);
            pa.lineTo(LW + tam, T - tam);
            pa.lineTo(LW + tam, TH - tam);
            pa.lineTo(LW + (tam / 2), TH - tam);
            pa.lineTo(LW + (tam / 2), T - (tam / 2));
            pa.lineTo(L + tam, T - (tam / 2));

            pa.closePath();

            Regiao = pa;
            final int ptToMove = 3;
            this.reposicionePonto[ptToMove] = new Point(-tam / 2, -repo);
            ptsToMove[ptToMove] = 1;
        }
        return Regiao;
    }

    public Shape getRegiaoNota() {
        if (Regiao == null) {
            final int v1 = getHeight() / 3;
            final int h1 = getWidth() / 2;
            final int repo = v1 / 3;
            final int L = getLeft();
            final int T = getTop();
            final int TH = T + getHeight() - repo;
            final int LW = L + getWidth();
            CubicCurve2D c = new CubicCurve2D.Double();
            c.setCurve(LW, TH, LW - h1, TH - v1, L + h1, TH + v1, L, TH);
            CubicCurve2D c2 = new CubicCurve2D.Double();
            int v2 = v1 / 3;
            c2.setCurve(L, T + v2, L + h1, T + v1 + v2, LW - h1, T - v1 + v2, LW, T + v2);

            GeneralPath pa = new GeneralPath();
            pa.setWindingRule(GeneralPath.WIND_EVEN_ODD);
            pa.append(c2, true);
            pa.lineTo(LW, TH);
            pa.append(c, true);
            pa.lineTo(L,  T + v2);
            pa.closePath();

            Regiao = pa;
            int ptToMove = 3;
            this.reposicionePonto[ptToMove] = new Point(0, -repo);
            ptsToMove[ptToMove] = 1;
            ptToMove = 1;
            this.reposicionePonto[ptToMove] = new Point(0, repo);
            ptsToMove[ptToMove] = 1;
        }
        return Regiao;
    }

    public Shape getRegiaoLosanglo() {
        if (Regiao == null) {
            Rectangle r = new Rectangle(getLeft(), getTop(), getWidth(), getHeight()); //getBounds();
            Polygon los = new Polygon();
            los.addPoint(r.x, r.y + r.height / 2);
            los.addPoint(r.x + r.width / 2, r.y);
            los.addPoint(r.x + r.width, r.y + r.height / 2);
            los.addPoint(r.x + r.width / 2, r.y + r.height);

            Regiao = los;
        }
        return Regiao;
    }

    public Shape getRegiaoCirculo() {
        if (Regiao == null) {
            Regiao = new Ellipse2D.Float(getLeft(), getTop(), getWidth(), getHeight());
        }
        return Regiao;
    }

    public Shape getRegiaoRecArred() {
        if (Regiao == null) {
            Regiao = new RoundRectangle2D.Float(getLeft(), getTop(), getWidth(), getHeight(), getWidth() / 3, getHeight());
        }
        return Regiao;
    }

    public Shape getRegiaoRec() {
        if (Regiao == null) {
            Regiao = new Rectangle2D.Float(getLeft(), getTop(), getWidth(), getHeight());
        }
        return Regiao;
    }

    public Shape getRegiaoComentario() {
        if (Regiao == null) {

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
            Regiao = pa;
        }
        return Regiao;
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Dashed", isDashed()));

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Gradiente", isGradiente()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteStartColor", getGradienteStartColor()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteEndColor", getGradienteEndColor()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "GDirecao", getGDirecao()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Alfa", (int) (100 * getAlfa())));

    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        setDashed(util.XMLGenerate.getValorBooleanFrom(me, "Dashed"));
        setGradiente(util.XMLGenerate.getValorBooleanFrom(me, "Gradiente"));
        Color c = util.XMLGenerate.getValorColorFrom(me, "GradienteStartColor");
        if (c != null) {
            setGradienteStartColor(c);
        }
        c = util.XMLGenerate.getValorColorFrom(me, "GradienteEndColor");
        if (c != null) {
            setGradienteEndColor(c);
        }
        int l = util.XMLGenerate.getValorIntegerFrom(me, "GDirecao");
        if (l != -1) {
            setGDirecao(l);
        }
        l = util.XMLGenerate.getValorIntegerFrom(me, "Alfa");
        if (l != -1) {
            SetAlfa(l);
        }
        return super.LoadFromXML(me, colando);
    }

    /**
     * Usado para evitar as properties LivreJuncao = true
     */
    protected boolean noNewProperty = false;

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        if (noNewProperty) {
            return super.CompleteGenerateProperty(GP);
        }

        GP.add(InspectorProperty.PropertyFactorySN("linha.dashed", "setDashed", isDashed()));
        GP.add(InspectorProperty.PropertyFactoryNumero("diagrama.detalhe.alfa", "SetAlfa", (int) (100 * getAlfa())));
        //if (getTipoDesenho() != TipoDraw.tpTexto) {
        ArrayList<InspectorProperty> res = GP;
        res.add(InspectorProperty.PropertyFactorySeparador("texto.gradiente", true));

        String[] grupo = new String[]{"setGradienteStartColor", "setGradienteEndColor", "setGDirecao"
        };

        res.add(InspectorProperty.PropertyFactorySN("texto.gradiente.is", "setGradiente", isGradiente()).AddCondicaoForFalse(new String[]{"setBackColor"}).AddCondicaoForTrue(grupo));

        res.add(InspectorProperty.PropertyFactoryCor("texto.gradiente.startcor", "setGradienteStartColor", getGradienteStartColor()));

        res.add(InspectorProperty.PropertyFactoryCor("texto.gradiente.endcor", "setGradienteEndColor", getGradienteEndColor()));

        res.add(InspectorProperty.PropertyFactoryMenu("texto.gradiente.direcao", "setGDirecao", getGDirecao(), Editor.fromConfiguracao.getLstDirecao(Controler.Comandos.cmdTexto)));

//            ArrayList<String> ngrp = new ArrayList<>(Arrays.asList(grupo));
//            ngrp.add("setGradiente");
//            ngrp.add("setBackColor");
//            ngrp.add("setGDirecao");
        //}
        return super.CompleteGenerateProperty(GP);
    }

    //<editor-fold defaultstate="collapsed" desc="Gradiente e Alfa">
    private boolean gradiente = true; //false;
    private Color gradienteEndColor = Color.WHITE; // new Color(204, 204, 204, 255);
    private Color gradienteStartColor = Color.BLACK;

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

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    private int gdirecao = VERTICAL;

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

    public int getGDirecao() {
        return gdirecao;
    }

    public void setGDirecao(int aDirection) {
        gdirecao = aDirection;
        InvalidateArea();
    }
    //</editor-fold>
}
