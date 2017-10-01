/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preDiagrama;

import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import util.DesenhadorDeTexto;

/**
 *
 * @author ccandido
 */
public class baseDrawer extends baseDrawerFromForma implements iBaseDrawer {

    private static final long serialVersionUID = -5417641185054502949L;

    public baseDrawer(Diagrama diagrama, String texto) {
        super(diagrama, texto);
        Inicie();
    }

    public baseDrawer(Diagrama diagrama) {
        super(diagrama);
        Inicie();
    }

    /**
     * Mostrar o texto (medida) ao lado da régua
     */
    private boolean mostrarTextoRegua = true;

    public boolean isMostrarTextoRegua() {
        return mostrarTextoRegua;
    }

    public void setMostrarTextoRegua(boolean mostrarTextoRegua) {
        if (this.mostrarTextoRegua == mostrarTextoRegua) {
            return;
        }
        this.mostrarTextoRegua = mostrarTextoRegua;
        InvalidateArea();
    }

    private int margem = 24;

    @Override
    protected void DimensioneParaPintura() {
        super.DimensioneParaPintura();
        W -= margem * 2;
        H -= margem * 2;
        L += margem;
        T += margem;
    }

    @Override
    public void DoPaint(Graphics2D g) {
        g.setPaint(getForeColor());
        Paint ori = g.getPaint();
        Composite com = g.getComposite();

        super.DoPaint(g);

        Paint gra = g.getPaint();
        Composite gcom = g.getComposite();

        g.setComposite(com);
        if (isMetricaTop()) {
            bordaTopDown(g, true);
        }
        if (isMetricaDown()) {
            bordaTopDown(g, false);
        }
        if (isMetricaRigth()) {
            bordaLeftRigth(g, true);
        }
        if (isMetricaLeft()) {
            bordaLeftRigth(g, false);
        }

        g.setComposite(gcom);
        g.setPaint(gra);

        Shape bkpA = g.getClip();

        if (roundrect > 0) {
            if (isPintarBorda()) {
                Shape sh = new RoundRectangle2D.Float(L, T, W, H, roundrect, roundrect);
                g.setClip(sh);
            }
        } else {
            g.setClip(L, T, W, H);
        }
        getItens().stream().forEach((bi) -> {
            if (!bi.isRecivePaint()) {
                g.setComposite(com);
                g.setPaint(ori);
            } else {
                g.setComposite(gcom);
                g.setPaint(gra);
            }
            bi.setDisablePainted(isDisablePainted());
            bi.DoPaint(g);
        });
        g.setClip(bkpA);
        g.setComposite(com);
        g.setPaint(ori);
    }

    public String convertMedidas(int valor) {

        //a (proporcaoQtdPixel) --> b (proporcaoMedida)
        //valor ---> x  
        double res = (double) (valor * getProporcaoMedida()) / getProporcaoQtdPixel();
        return (new DecimalFormat(getProporcaoMedida() != getProporcaoQtdPixel() ? "0.00" : "0.##").format(res));
    }

    protected double getMedidaConvertida(int valor) {
        return (double) (valor * getProporcaoMedida()) / getProporcaoQtdPixel();
    }

    private final int largTraco = 4;
    private Color corRegua = new Color(0, 204, 204);

    public Color getCorRegua() {
        return corRegua;
    }

    public void setCorRegua(Color corTraco) {
        this.corRegua = corTraco;
        InvalidateArea();
    }

    private int calculeSubEspaco(int vl) {
        final int bs = 32;
        double pro = (double) getProporcaoQtdPixel() / getProporcaoMedida();
        while (pro < bs) {
            pro *= 2;
        }
        return (int) Math.round(pro);
    }

    private int modInteiro(int vl) {
        if ((vl % 5) == 0) {
            return 5;
        }
        if ((vl % 4) == 0) {
            return 4;
        }
        if ((vl % 3) == 0) {
            return 3;
        }
        if ((vl % 2) == 0) {
            return 2;
        }
        return 0;
    }

    public void bordaTopDown(Graphics2D g, boolean isTop) {
        FontMetrics fm = g.getFontMetrics();
        String vl = FormateUnidadeMedida(W);
        int xini = getLeft() + margem;
        int pre_y = (isTop ? getTop() : getTopHeight());
        int xfim = getLeftWidth() - margem;
        int yfim = pre_y + (isTop ? 2 : -2);

        int traco = largTraco < margem ? largTraco : margem;
        traco = (isTop ? traco : -traco);
        int ytraco = yfim;
//        int xini = getLeft() + margem;
//        int pre_y = (isTop ? getTop() : getTopHeight() - margem);
//        int xfim = getLeftWidth() - margem;
//        int yfim = pre_y + margem / 2;
//
//        int traco = largTraco < margem ? largTraco : margem;
//        int ytraco = pre_y + (margem - traco) / 2;

        g.setColor(getCorRegua());
        g.drawLine(xini, ytraco, xini, ytraco + 2 * traco);
        g.drawLine(xfim, ytraco, xfim, ytraco + 2 * traco);
        g.drawLine(xini, yfim, xfim, yfim);

        int blc = calculeSubEspaco(W);
        int sr = xini;
        int dv = modInteiro(blc);
        int subblc = 0;
        if (dv > 0) {
            subblc = blc / dv;
        }
        while (sr < xfim) {
            if (dv > 0) {
                int a = subblc;
                while (a < blc) {
                    if (sr + a < xfim) {
                        g.drawLine(sr + a, ytraco, sr + a, ytraco + traco / 2);
                    }
                    a += subblc;
                }
            }
            sr += blc;
            if (sr < xfim) {
                g.drawLine(sr, ytraco, sr, ytraco + traco);
            }
        }

        if (isMostrarTextoRegua()) {
            g.setColor(getForeColor());
            yfim = pre_y + (isTop ? margem / 2 : -margem / 2);
            xini = xini + (W - fm.stringWidth(vl)) / 2;
            int yini = yfim + (fm.getHeight()) / 2 - fm.getDescent();
            g.drawString(vl, xini, yini);
        }
    }

    public void bordaLeftRigth(Graphics2D g, boolean isrigth) {
        FontMetrics fm = g.getFontMetrics();
        String vl = FormateUnidadeMedida(H);
        int pre_x = (isrigth ? getLeftWidth() : getLeft());
        int traco = largTraco < margem ? largTraco : margem;
        traco = (isrigth ? -traco : traco);
        int xIni = pre_x + (isrigth ? -2 : 2);
        int xFim = xIni + 2 * traco;
        int yIni = getTop() + margem;
        int yFim = getTopHeight() - margem;
        int xLin = xIni;
//        int pre_x = (isrigth ? getLeftWidth() - margem : getLeft());
//        int traco = largTraco < margem ? largTraco : margem;
//        int xIni = pre_x + (margem - traco) / 2;
//        int xFim = xIni + traco;
//        int yIni = getTop() + margem;
//        int yFim = getTopHeight() - margem;
//        int xLin = pre_x + margem / 2;

        g.setColor(getCorRegua());
        g.drawLine(xIni, yIni, xFim, yIni);
        g.drawLine(xIni, yFim, xFim, yFim);
        g.drawLine(xLin, yIni, xLin, yFim);

        int blc = calculeSubEspaco(W);
        int sr = yIni;
        xFim -= traco;

        int dv = modInteiro(blc);
        int subblc = 0;
        if (dv > 0) {
            subblc = blc / dv;
        }
        while (sr < yFim) {
            if (dv > 0) {
                int a = blc - subblc;
                while (a > 0) {
                    if (sr + a < yFim) {
                        g.drawLine(xIni, sr + a, xFim - traco / 2, sr + a);
                    }
                    a -= subblc;
                }
            }
            g.drawLine(xIni, sr, xFim, sr);
            sr += blc;
        }

        if (isMostrarTextoRegua()) {
            int degrees = isrigth ? 90 : -90;
            int desse = isrigth ? 0 : fm.stringWidth(vl);
            int centra = fm.getHeight() / 2 - fm.getDescent();
            centra = isrigth ? -centra : centra;

            AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(degrees));
            Font f = new Font(getFont().getName(), Font.BOLD, getFont().getSize());
            g.setFont(f.deriveFont(at));
            g.setColor(getForeColor());
            xLin = pre_x - (isrigth ? margem / 2 : -margem / 2);
            yIni = yIni + (H - fm.stringWidth(vl)) / 2 + desse;
            g.drawString(vl, xLin + centra, yIni);
            g.setFont(getFont());
        }
    }

    private void Inicie() {
        setDelimite(false);
        SetTexto("");
        //blankPaint = true;
        setAlteraForma(true);
    }

    @Override
    public void SetTexto(String Texto) {
        super.SetTexto(Texto);
//        getTextoFormatado().CorretorPosicao = new Point(0, 3 * distSelecao + margem);
    }

    @Override
    public DesenhadorDeTexto getTextoFormatado() {
        DesenhadorDeTexto dz = super.getTextoFormatado();
        dz.CorretorPosicao = new Point(0, 3 * distSelecao + margem);
        return dz;
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        res.add(InspectorProperty.PropertyFactorySeparador("basedrawer.titulo.unidademedida"));
        res.add(InspectorProperty.PropertyFactoryTexto("basedrawer.unidademedida", "setUnidadeMedida", getUnidadeMedida()));
        res.add(InspectorProperty.PropertyFactoryNumero("basedrawer.proporcaoqtdpixel", "setProporcaoQtdPixel", getProporcaoQtdPixel()));
        res.add(InspectorProperty.PropertyFactoryNumero("basedrawer.proporcaomedida", "setProporcaoMedida", getProporcaoMedida()));
        res.add(InspectorProperty.PropertyFactorySN("basedrawer.metricaleft", "setMetricaLeft", isMetricaLeft()));
        res.add(InspectorProperty.PropertyFactorySN("basedrawer.metricatop", "setMetricaTop", isMetricaTop()));
        res.add(InspectorProperty.PropertyFactorySN("basedrawer.metricadown", "setMetricaDown", isMetricaDown()));
        res.add(InspectorProperty.PropertyFactorySN("basedrawer.metricarigth", "setMetricaRigth", isMetricaRigth()));
        res.add(InspectorProperty.PropertyFactoryTexto("basedrawer.widthunidmedida", "setWidthUnidMedida", getWidthUnidMedida()));
        res.add(InspectorProperty.PropertyFactoryTexto("basedrawer.heightunidmedida", "setHeightUnidMedida", getHeightUnidMedida()));
        res.add(InspectorProperty.PropertyFactoryCor("basedrawer.corregua", "setCorRegua", getCorRegua()));
        res.add(InspectorProperty.PropertyFactoryNumero("basedrawer.margem", "setMargem", getMargem()));
        res.add(InspectorProperty.PropertyFactorySN("basedrawer.mostrartextoregua", "setMostrarTextoRegua", isMostrarTextoRegua()).PropertyForceDisable(
                !(isMetricaLeft() || isMetricaDown() || isMetricaRigth() || isMetricaTop())
        ));

        res.add(InspectorProperty.PropertyFactorySeparador("basedrawer.editor"));
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdCallDrawerEditor.name()));

        res.add(InspectorProperty.PropertyFactorySeparador("diagrama.formato"));
        res.add(InspectorProperty.PropertyFactorySN("basedrawer.pintarborda", "setPintarBorda", isPintarBorda()).AddCondicaoForTrue(new String[]{"setCorBorda", "setRoundrect"}));
        res.add(InspectorProperty.PropertyFactoryCor("basedrawer.corborda", "setCorBorda", getCorBorda()));

        return res;
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorString(doc, "UnidadeMedida", getUnidadeMedida()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "ProporcaoQtdPixel", getProporcaoQtdPixel()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "ProporcaoMedida", getProporcaoMedida()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "MetricaLeft", isMetricaLeft()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "MetricaTop", isMetricaTop()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "MetricaDown", isMetricaDown()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "MetricaRigth", isMetricaRigth()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Margem", getMargem()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "CorRegua", getCorRegua()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "CorBorda", getCorBorda()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "PintarBorda", isPintarBorda()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "MostrarTextoRegua", isMostrarTextoRegua()));

        Element e = util.XMLGenerate.ValorInteger(doc, "DrawerItens", getItens().size());
        getItens().stream().forEach((bi) -> {
            bi.ToXml(doc, e);
        });
        me.appendChild(e);
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        setUnidadeMedida(util.XMLGenerate.getValorStringFrom(me, "UnidadeMedida"));
        setProporcaoQtdPixel(util.XMLGenerate.getValorIntegerFrom(me, "ProporcaoQtdPixel"));
        setProporcaoMedida(util.XMLGenerate.getValorIntegerFrom(me, "ProporcaoMedida"));
        setMetricaLeft(util.XMLGenerate.getValorBooleanFrom(me, "MetricaLeft"));
        setMetricaTop(util.XMLGenerate.getValorBooleanFrom(me, "MetricaTop"));
        setMetricaDown(util.XMLGenerate.getValorBooleanFrom(me, "MetricaDown"));
        setMetricaRigth(util.XMLGenerate.getValorBooleanFrom(me, "MetricaRigth"));
        setMargem(util.XMLGenerate.getValorIntegerFrom(me, "Margem"));

        setCorRegua(util.XMLGenerate.getValorColorFrom(me, "CorRegua"));
        setCorBorda(util.XMLGenerate.getValorColorFrom(me, "CorBorda"));
        setPintarBorda(util.XMLGenerate.getValorBooleanFrom(me, "PintarBorda"));
        setMostrarTextoRegua(util.XMLGenerate.getValorBooleanFrom(me, "MostrarTextoRegua"));

        Element inter = util.XMLGenerate.FindByNodeName(me, "DrawerItens");
        if (inter != null) {
            for (int s = 0; s < inter.getChildNodes().getLength(); s++) {
                Node fstNode = inter.getChildNodes().item(s);
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    baseDrawerItem item = AddItem();
                    if (!item.LoadFromXML(fstElmnt, colando)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private String unidadeMedida = "";

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        if (this.unidadeMedida.equals(unidadeMedida)) {
            return;
        }
        this.unidadeMedida = unidadeMedida;
        InvalidateArea();
    }

    private int proporcaoQtdPixel = 1;

    public int getProporcaoQtdPixel() {
        return proporcaoQtdPixel;
    }

    public void setProporcaoQtdPixel(int proporcaoQtdPixel) {
        if (this.proporcaoQtdPixel == proporcaoQtdPixel) {
            return;
        }
        this.proporcaoQtdPixel = proporcaoQtdPixel;
        if (this.proporcaoQtdPixel < 2) {
            this.proporcaoQtdPixel = 1;
        }
        InvalidateArea();
    }

    private int proporcaoMedida = 1;

    public int getProporcaoMedida() {
        return proporcaoMedida;
    }

    public void setProporcaoMedida(int proporcaoMedida) {
        if (this.proporcaoMedida == proporcaoMedida) {
            return;
        }
        this.proporcaoMedida = proporcaoMedida;
        if (this.proporcaoMedida < 2) {
            this.proporcaoMedida = 1;
        }
        InvalidateArea();
    }

    private boolean metricaLeft = true;

    public boolean isMetricaLeft() {
        return metricaLeft;
    }

    public void setMetricaLeft(boolean metricaLeft) {
        if (this.metricaLeft == metricaLeft) {
            return;
        }
        this.metricaLeft = metricaLeft;
        InvalidateArea();
    }

    private boolean metricaTop = true;

    public boolean isMetricaTop() {
        return metricaTop;
    }

    public void setMetricaTop(boolean metricaTop) {
        if (this.metricaTop == metricaTop) {
            return;
        }
        this.metricaTop = metricaTop;
        InvalidateArea();
    }

    private boolean metricaDown = true;

    public boolean isMetricaDown() {
        return metricaDown;
    }

    public void setMetricaDown(boolean metricaDown) {
        if (this.metricaDown == metricaDown) {
            return;
        }
        this.metricaDown = metricaDown;
        InvalidateArea();
    }

    private boolean metricaRigth = true;

    public boolean isMetricaRigth() {
        return metricaRigth;
    }

    public void setMetricaRigth(boolean metricaRigth) {
        if (this.metricaRigth == metricaRigth) {
            return;
        }
        this.metricaRigth = metricaRigth;
        InvalidateArea();
    }

    public String getWidthUnidMedida() {
        DimensioneParaPintura();
        return convertMedidas(W);
    }

    public void setWidthUnidMedida(String widthUnidMedida) {
        try {
            double res = Double.valueOf(widthUnidMedida.replace(',', '.'));
            //10p ---> 1m
            //xp  --> res
            int r = (int) (Math.round(res * proporcaoQtdPixel / proporcaoMedida));
            setWidth(r + 2 * margem);
            Reposicione();
            PropagueResizeParaLigacoes();
        } catch (Exception e) {

        }
    }

    public String getHeightUnidMedida() {
        DimensioneParaPintura();
        return convertMedidas(H);
    }

    public void setHeightUnidMedida(String heightUnidMedida) {
        try {
            double res = Double.valueOf(heightUnidMedida.replace(',', '.'));
            int r = (int) (Math.round(res * proporcaoQtdPixel / proporcaoMedida));
            setHeight(r + 2 * margem);
            Reposicione();
            PropagueResizeParaLigacoes();
        } catch (Exception e) {
        }
    }

    public int getMargem() {
        return margem;
    }

    public void setMargem(int margem) {
        if (this.margem == margem) {
            return;
        }
        if ((margem > getWidth() / 2) || (margem > getHeight() / 2) || margem < 0) {
            margem = 14;
        }
        this.margem = margem;
        InvalidateArea();
    }

    private ArrayList<baseDrawerItem> Itens = new ArrayList<>();

    @Override
    public ArrayList<baseDrawerItem> getItens() {
        return Itens;
    }

    @Override
    public baseDrawerItem AddItem() {
        baseDrawerItem bi = new baseDrawerItem(this, baseDrawerItem.tipoDrawer.tpRetangulo);
        getItens().add(bi);
        return bi;
    }

    public void setItens(ArrayList<baseDrawerItem> Itens) {
        this.Itens = Itens;
    }

    @Override
    public int getL() {
        return L;
    }

    @Override
    public int getH() {
        return H;
    }

    @Override
    public int getT() {
        return T;
    }

    @Override
    public int getW() {
        return W;
    }

    @Override
    public String FormateUnidadeMedida(int valor) {
        return convertMedidas(valor) + getUnidadeMedida();
    }
}
