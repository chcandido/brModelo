/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.livre;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.formas.FormaTriangular;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class LivreTriangulo extends FormaTriangular {

    private static final long serialVersionUID = 1879344171165195360L;

    public LivreTriangulo(Diagrama modelo) {
        super(modelo);
        setDirecaoNaoNotifique(Direcao.Right);
        editFonte = false; 
    }

    public LivreTriangulo(Diagrama modelo, String texto) {
        super(modelo, texto);
        setDirecaoNaoNotifique(Direcao.Right);
        editFonte = false; 
    }

    /**
     * Serve apenas para alterar a direção pelo Inspector
     *
     * @param di
     */
    public void setDirecaoFromInspector(int di) {
        Direcao dr = Direcao.values()[di];
        setDirecaoTriangulo(dr);
    }

    /**
     * Serve apenas para retornar a direção para o Inspector
     */
    public int getDirecaoForInspector() {
        return getDirecaoTriangulo().ordinal();
    }

    /**
     * aqui para atender também a União
     *
     * @param doc
     * @param me
     */
    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Direcao", getDirecaoForInspector()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Dashed", isDashed()));

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Gradiente", isGradiente()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteStartColor", getGradienteStartColor()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteEndColor", getGradienteEndColor()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "GDirecao", getGDirecao()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Alfa", (int) (100 * getAlfa())));

    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }

        int l = util.XMLGenerate.getValorIntegerFrom(me, "Direcao");
        if (l != -1) {
            setDirecaoFromInspector(l);
        }

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
        l = util.XMLGenerate.getValorIntegerFrom(me, "GDirecao");
        if (l != -1) {
            setGDirecao(l);
        }
        l = util.XMLGenerate.getValorIntegerFrom(me, "Alfa");
        if (l != -1) {
            SetAlfa(l);
        }
        return true;
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        res.add(InspectorProperty.PropertyFactoryMenu("direcao", "setDirecaoFromInspector", getDirecaoForInspector(), Editor.fromConfiguracao.getLstDirecao(Controler.Comandos.cmdEspecializacao)));
        return res;
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        GP.add(InspectorProperty.PropertyFactorySN("linha.dashed", "setDashed", isDashed()));
        GP.add(InspectorProperty.PropertyFactoryNumero("diagrama.detalhe.alfa", "SetAlfa", (int) (100 * getAlfa())));
        ArrayList<InspectorProperty> res = GP;
        res.add(InspectorProperty.PropertyFactorySeparador("texto.gradiente"));

        String[] grupo = new String[]{"setGradienteStartColor", "setGradienteEndColor", "setGDirecao"
        };

        res.add(InspectorProperty.PropertyFactorySN("texto.gradiente.is", "setGradiente", isGradiente()).AddCondicaoForFalse(new String[]{"setBackColor"}).AddCondicaoForTrue(grupo));

        res.add(InspectorProperty.PropertyFactoryCor("texto.gradiente.startcor", "setGradienteStartColor", getGradienteStartColor()));

        res.add(InspectorProperty.PropertyFactoryCor("texto.gradiente.endcor", "setGradienteEndColor", getGradienteEndColor()));

        res.add(InspectorProperty.PropertyFactoryMenu("texto.gradiente.direcao", "setGDirecao", getGDirecao(), Editor.fromConfiguracao.getLstDirecao(Controler.Comandos.cmdTexto)));

        return super.CompleteGenerateProperty(GP);
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

        Stroke bkp = g.getStroke();
        if (isDashed()) {
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
        }
        if (isGradiente()) {
            g.fill(getRegiao());
        }
        g.draw(getRegiao()); //Pinta as eventuais linhas internas do dezenho. Ex. LivreVariosDocumemtos 

        g.setStroke(bkp);
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

    //<editor-fold defaultstate="collapsed" desc="Gradiente e Alfa">
    private boolean gradiente = true;
    private Color gradienteEndColor = new Color(204, 204, 204, 255);
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
