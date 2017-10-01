/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preDiagrama;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.formas.Forma;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import util.DesenhadorDeTexto;

/**
 *
 * @author ccandido
 */
public class baseDrawerFromForma extends Forma {

    private static final long serialVersionUID = 4532072927400419827L;

    public baseDrawerFromForma(Diagrama diagrama) {
        super(diagrama);
    }

    public baseDrawerFromForma(Diagrama diagrama, String texto) {
        super(diagrama, texto);
    }
    public int margemTitulo = 10;

    @Override
    public DesenhadorDeTexto getTextoFormatado() {
        DesenhadorDeTexto dz = super.getTextoFormatado(); //To change body of generated methods, choose Tools | Templates.
        dz.setCentrarTextoVertical(false);
        dz.LimitarAreaDePintura = true;
        dz.CorretorPosicao = new Point(0, 3 * distSelecao);
        return dz;
    }

    protected int L = 0, T = 0, W = 0, H = 0;

    protected void DimensioneParaPintura() {
        L = getLeft();
        T = getTop();
        W = getWidth();
        H = getHeight();
    }
    /**
     * Impede a pintura em baseDrawerFromForma se true.
     */
    protected boolean blankPaint = false;

    private boolean pintarBorda = true;

    public boolean isPintarBorda() {
        return pintarBorda;
    }

    public void setPintarBorda(boolean pintarBorda) {
        this.pintarBorda = pintarBorda;
        InvalidateArea();
    }

    @Override
    public void DoPaint(Graphics2D g) {
        g.setPaint(this.getForeColor());
        if (blankPaint) {
            DimensioneParaPintura();
            super.DoPaint(g);
            return;
        }
        Composite ori = g.getComposite();
        
        if (isGradiente()) {
            PaintGradiente(g);
        }
        Paint paintg = g.getPaint();
        Composite orig = g.getComposite();
        
        g.setComposite(ori);
        g.setPaint(this.getForeColor());
        super.DoPaint(g);
        g.setPaint(paintg);
        g.setComposite(orig);
        DimensioneParaPintura();
        if (roundrect > 0) {
            if (isPintarBorda()) {
                g.setPaint(getCorBorda());
                g.drawRoundRect(L, T, W, H, roundrect, roundrect);
            }
            margemTitulo = g.getFontMetrics(getFont()).getHeight() + 4 * distSelecao;
            if (delimite) {
                g.drawLine(L + 1, T + margemTitulo, W + L - distSelecao + 1, T + margemTitulo);
            }
        } else {
            if (isPintarBorda()) {
                g.setPaint(isDisablePainted()? disabledColor : Color.darkGray);
                g.drawRect(L, T, W, H);

                g.setPaint(isDisablePainted()? disabledColor : Color.gray);
                g.drawRect(L, T, W + 1, H + 1);

                g.setPaint(getCorBorda());
                g.drawRect(L, T, W - 1, H - 1);
            }
            margemTitulo = g.getFontMetrics(getFont()).getHeight() + 4 * distSelecao;
            if (delimite) {
                g.drawLine(L + 1, T + margemTitulo, W + L - distSelecao + 1, T + margemTitulo);
            }
        }
        g.setPaint(paintg);
        //Pode terminar a pintura na forma gradiente, cor da borda e etc, deve ser tratado pelas classes descedentes.
    }

    private boolean gradiente = false;
    public final int VERTICAL = 0;
    public final int HORIZONTAL = 1;
    private int gdirecao = VERTICAL;
    private Color gradienteEndColor = new Color(204, 204, 204, 255);//Color.WHITE;
    private Color gradienteStartColor = new Color(0, 0, 0, 255);//Color.BLACK;
    protected int roundrect = 22;
    private boolean delimite = true;
    private float alfa = 0.5f;
    private boolean alteraForma = false;
    private Color corBorda = Color.BLACK;

    public Color getCorBorda() {
        return isDisablePainted()? disabledColor : corBorda;
    }

    public void setCorBorda(Color corBorda) {
        this.corBorda = corBorda;
        InvalidateArea();
    }

    /**
     * Pode um descendente alterar determinadas propriedades (forma) do obj por meio do inspector
     *
     * @return
     */
    public boolean isAlteraForma() {
        return alteraForma;
    }

    /**
     * Pode um descendente alterar determinadas propriedades (forma) do obj por meio do inspector
     *
     * @param alteraForma
     */
    public void setAlteraForma(boolean alteraForma) {
        this.alteraForma = alteraForma;
    }

    public float getAlfa() {
        return alfa;
    }

    public void setAlfa(float alfa) {
        this.alfa = alfa;
        InvalidateArea();
    }

    public void SetAlfa(int alfa) {
        this.alfa = (float) alfa / 100;
        if (this.alfa > 1) {
            this.alfa = 0.5f;
        }
        InvalidateArea();
    }

    public boolean isDelimite() {
        return delimite;
    }

    public void setDelimite(boolean delimite) {
        this.delimite = delimite;
        InvalidateArea();
    }

    public int getRoundrect() {
        return roundrect;
    }

    public void setRoundrect(int roundrect) {
        this.roundrect = roundrect;
        InvalidateArea();
    }

    public int getGDirecao() {
        return gdirecao;
    }

    public void setGDirecao(int aDirection) {
        gdirecao = aDirection;
        InvalidateArea();
    }

    public boolean isGradiente() {
        return gradiente;
    }

    public void setGradiente(boolean gradiente) {
        if (this.gradiente != gradiente) {
            this.gradiente = gradiente;
            InvalidateArea();
        }
    }

    public Color getGradienteStartColor() {
        return isDisablePainted()? disabledColor : gradienteStartColor;
    }

    public void setGradienteStartColor(Color gradienteStartColor) {
        this.gradienteStartColor = gradienteStartColor;
        InvalidateArea();
    }

    public Color getGradienteEndColor() {
        return isDisablePainted()? disabledColor : gradienteEndColor;
    }

    public void setGradienteEndColor(Color gradienteEndColor) {
        this.gradienteEndColor = gradienteEndColor;
        InvalidateArea();
    }

    protected void PaintGradiente(Graphics2D g) { //, boolean round) {
        int dist = 0;
        DimensioneParaPintura();
        W -= dist;
        H -= dist;
        boolean dv = getGDirecao() == VERTICAL;

        int type = AlphaComposite.SRC_OVER;

        g.setComposite(AlphaComposite.getInstance(type, alfa));

        GradientPaint GP = new GradientPaint(L, T, getGradienteStartColor(), dv ? L : L + W, dv ? T + H : T, getGradienteEndColor(), true);
        g.setPaint(GP);

        if (roundrect > 0 && isPintarBorda()) {
            g.fillRoundRect(L + 1, T + 1, W, H, roundrect, roundrect);
        } else {
            g.fillRect(L + 1, T + 1, W, H);
        }
    }
 
    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        ArrayList<InspectorProperty> res = GP;

        if (!blankPaint) {
            if (isAlteraForma()) {
                res.add(InspectorProperty.PropertyFactoryNumero("diagrama.detalhe.roundrect", "setRoundrect", getRoundrect()));
                res.add(InspectorProperty.PropertyFactorySN("diagrama.detalhe.delimite", "setDelimite", isDelimite()));
            }

            res.add(InspectorProperty.PropertyFactorySeparador("diagrama.gradiente"));
            String[] grupo = new String[]{"setGradienteStartColor", "setGradienteEndColor", "SetAlfa",
                "setGDirecao"
            };
            res.add(InspectorProperty.PropertyFactoryCor("diagrama.gradiente.corborda", "setCorBorda", getCorBorda()));
            res.add(InspectorProperty.PropertyFactorySN("diagrama.gradiente.is", "setGradiente", isGradiente()).AddCondicaoForTrue(grupo));
            res.add(InspectorProperty.PropertyFactoryCor("diagrama.gradiente.startcor", "setGradienteStartColor", getGradienteStartColor()));
            res.add(InspectorProperty.PropertyFactoryCor("diagrama.gradiente.endcor", "setGradienteEndColor", getGradienteEndColor()));
            res.add(InspectorProperty.PropertyFactoryNumero("diagrama.detalhe.alfa", "SetAlfa", (int) (100 * getAlfa())));
            res.add(InspectorProperty.PropertyFactoryMenu("diagrama.gradiente.direcao", "setGDirecao", getGDirecao(), Editor.fromConfiguracao.getLstDirecao(Controler.Comandos.cmdTexto)));
        }
        return super.CompleteGenerateProperty(GP);
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Delimite", isDelimite()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Roundrect", getRoundrect()));

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Gradiente", isGradiente()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteStartColor", getGradienteStartColor()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteEndColor", getGradienteEndColor()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "CorBorda", getCorBorda()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "GDirecao", getGDirecao()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Alfa", (int) (100 * getAlfa())));

    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        setGradiente(util.XMLGenerate.getValorBooleanFrom(me, "Gradiente"));
        setDelimite(util.XMLGenerate.getValorBooleanFrom(me, "Delimite"));
        setRoundrect(util.XMLGenerate.getValorIntegerFrom(me, "Roundrect"));
        Color c = util.XMLGenerate.getValorColorFrom(me, "GradienteStartColor");
        if (c != null) {
            setGradienteStartColor(c);
        }
        c = util.XMLGenerate.getValorColorFrom(me, "GradienteEndColor");
        if (c != null) {
            setGradienteEndColor(c);
        }
        c = util.XMLGenerate.getValorColorFrom(me, "CorBorda");
        if (c != null) {
            setCorBorda(c);
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

}
