/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.formas;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.ElementarListener;
import desenho.FormaElementar;
import desenho.linhas.SuperLinha;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.DesenhadorDeTexto;

/**
 *
 * @author ccandido
 */
public class FormaArea extends Forma {

    private static final long serialVersionUID = 4366780837704194852L;

    public FormaArea(Diagrama modelo) {
        super(modelo);
        areaDefault = Editor.fromConfiguracao.getValor("Inspector.obj.formaarea.area.default");
        //setForeColor(new Color(204,204, 255));
    }

    public FormaArea(Diagrama modelo, String texto) {
        super(modelo, texto);
        areaDefault = Editor.fromConfiguracao.getValor("Inspector.obj.formaarea.area.default");
        //setForeColor(new Color(204,204, 255));
    }

    public int getLocalDaLinha(DimensionadorArea aThis) {
        int res = getLeft();
        for (DimensionadorArea fr : getRegioes()) {
            res += fr.getLargura();
            if (fr == aThis) {
                return res;
            }
            res += fr.getWidth();
        }
        return res;
    }

    private ArrayList<DimensionadorArea> regioes = new ArrayList<>();

    public ArrayList<DimensionadorArea> getRegioes() {
        return regioes;
    }

    public void AddRegiao(int largura) {
        DimensionadorArea nr = new DimensionadorArea(this);
        nr.setLargura(largura);
        getRegioes().add(nr);
        RePosicioneRegioes();
        nr.setTexto(NomeieDimensoes());
        DoMuda();
        InvalidateArea();
    }

    public DimensionadorArea AddRegiao() {
        DimensionadorArea nr = new DimensionadorArea(this);
        getRegioes().add(nr);
        return nr;
    }

    public void Remova(DimensionadorArea regiao) {
        getRegioes().remove(regiao);
        RemoveSubItem(regiao);
        getMaster().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        RePosicioneRegioes();
        DoMuda();
        InvalidateArea();
    }

    @Override
    public void mouseDblClicked(MouseEvent e) {
        AdicionarSubItem(-1);
    }

    private transient int alturaTexto = 20;

    public int getAlturaTexto() {
        return alturaTexto;
    }

    @Override
    public void DoPaint(Graphics2D g) {
        g.setFont(getFont());
        int larg = getMaster().getPontoWidth();
        alturaTexto = g.getFontMetrics().getHeight();
        alturaTexto += alturaTexto / 2;
        int top = getTop() + alturaTexto;
        //int newTop = getTextoFormatado().getMaxHeigth();
        int lastlarg = distSelecao + getLeft();
        PaintGradiente(g);
        boolean excesso = false;
        for (DimensionadorArea regiao : getRegioes()) {
            int x = getLocalDaLinha(regiao);
            if ((x < getLeftWidth() - distSelecao) && (x > getLeft())) {
                x += larg / 2;

                Stroke bkps = g.getStroke();
                if (isDashed()) {
                    g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
                }

                g.setColor(getForeColor());
                g.drawLine(x, top + 1, x, getTopHeight() - 2);

                if (isDashed()) {
                    g.setStroke(bkps);
                }

                DesenhadorDeTexto dz = new DesenhadorDeTexto(regiao.getTexto(), getFont(), true);
                dz.LimitarAreaDePintura = true;
                dz.PinteTexto(g, getForeColor(),
                        new Rectangle(x - regiao.getLargura(), top + 1, regiao.getLargura(), alturaTexto - distSelecao),
                        regiao.getTexto());
                lastlarg = x;
            } else {
                excesso = true;
                break;
            }
        }
        DesenhadorDeTexto dz = new DesenhadorDeTexto(excesso? "... " + areaDefault: areaDefault, getFont(), true);
        dz.LimitarAreaDePintura = true;
        dz.PinteTexto(g, getForeColor(),
                new Rectangle(lastlarg, top + 1, getLeftWidth() - lastlarg, alturaTexto - distSelecao),
                excesso? "... " + areaDefault: areaDefault);
        Stroke bkps = g.getStroke();
        if (isDashed()) {
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
        }
        g.drawLine(getLeft(), top - 1 + alturaTexto, getLeftWidth(), top - 1 + alturaTexto);
        g.setStroke(bkps);
        super.DoPaint(g);
    }

    protected void PaintGradiente(Graphics2D g) { //, boolean round) {
        Paint bkp = g.getPaint();
        int dist = distSelecao;

        int W = getWidth() - dist;
        int H = 2 * alturaTexto - 1 - dist;
        boolean dv = getGDirecao() == VERTICAL;

        Composite originalComposite = g.getComposite();
        int type = AlphaComposite.SRC_OVER;

        g.setComposite(AlphaComposite.getInstance(type, alfa));

        Stroke bkps = g.getStroke();
        if (isDashed()) {
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
        }

//        GradientPaint GP = new GradientPaint(getLeft(), getTop(), Color.LIGHT_GRAY, dv ? getLeft() : getLeft() + W, dv ? getTop() + H : getTop(), Color.white, true);
//        g.setPaint(GP);
        int w = getWidth() - dist;
        int h = getHeight() - dist;
        int L = getLeft();
        int T = getTop();

        GradientPaint GP = new GradientPaint(L, T, getGradienteStartColor(), dv ? L : L + w, dv ? T + h : T, getGradienteEndColor(), true);
        g.setPaint(GP);

        g.fillRect(getLeft() + 1, getTop() + 1, W, H);
        g.setComposite(originalComposite);
        g.setPaint(bkp);
        g.setStroke(bkps);
    }

    //<editor-fold defaultstate="collapsed" desc="Gradiente e Alfa">
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

    private boolean gradiente = true;
    private Color gradienteEndColor = Color.white;
    private Color gradienteStartColor = Color.LIGHT_GRAY;

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
    private int gdirecao = HORIZONTAL;

    private float alfa = 0.4f;

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

    @Override
    public void reSetBounds() {
        super.reSetBounds();
        RePosicioneRegioes();
    }

    public void RePosicioneRegioes() {
        HideDimensionador(true);
    }

    /**
     * A região está dentro da área do componente.
     *
     * @param regiao
     * @return
     */
    public boolean EmAreaVisivel(DimensionadorArea regiao) {
        return ((regiao.getLeft() < getLeftWidth() - distSelecao) && (regiao.getLeft() > getLeft()));
    }

    public void HideDimensionador(boolean mostar) {
        for(DimensionadorArea regiao: getRegioes()) {
            if (mostar) {
                regiao.Posicione();
                if (EmAreaVisivel(regiao)) {
                    regiao.setVisible(mostar);
                } else {
                    regiao.setVisible(false);
                }
            } else {
                regiao.setVisible(mostar);
            }
        }
    }

    @Override
    public void setSelecionado(boolean selecionado) {
        super.setSelecionado(selecionado);
        HideDimensionador(isSelecionado());
    }

    @Override
    public DesenhadorDeTexto getTextoFormatado() {
        DesenhadorDeTexto dz = super.getTextoFormatado(); //To change body of generated methods, choose Tools | Templates.
        dz.setCentrarTextoVertical(false);
        dz.LimitarAreaDePintura = true;
        dz.CorretorPosicao = new Point(0, 3 * distSelecao);
        return dz;
    }

    private transient double z = 0.0;

    @Override
    public void PinteTexto(Graphics2D g) {
        //no caso de mudança no zoom, um novo TextoFormatado deve ser criado.
        if (getMaster().getZoom() != z) {
            setTextoFormatado(null);
            z = getMaster().getZoom();
        }
        Rectangle r = getArea();
        r = new Rectangle(r.x, r.y, r.width, alturaTexto);
        getTextoFormatado().PinteTexto(g, getForeColor(), r, getTexto());
    }

    private String areaDefault = "";

    public String getAreaDefault() {
        return areaDefault;
    }

    public void setAreaDefault(String areaDefault) {
        this.areaDefault = areaDefault;
        InvalidateArea();
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
//        res.add(InspectorProperty.PropertyFactoryTexto("formaarea.area.default", "setAreaDefault", getAreaDefault()));
//        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdAdicionarSubItem.name()).setTag(-1));
//
//        int i = 0;
//        for (DimensionadorArea dime : getRegioes()) {
//            res.add(InspectorProperty.PropertyFactorySeparador("formaarea.area"));
//            res.add(InspectorProperty.PropertyFactoryTexto("formaarea.area", "SetDimensaoTexto", dime.getTexto()).setTag(i));
//            res.add(InspectorProperty.PropertyFactoryNumero("formaarea.largura", "SetDimensaoLargura", dime.getLargura()).setTag(i));
//            res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdExcluirSubItem.name()).setTag(i));
//            i++;
//        }
//
//        ArrayList<FormaElementar> overme = WhoIsOverMe();
//        res.add(InspectorProperty.PropertyFactorySeparador("formaarea.overme"));
//        res.add(InspectorProperty.PropertyFactorySN("formaarea.movesubs", "setMoverSubs", isMoverSubs()));
//        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "formaarea.capture").setTag(99));
//        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "formaarea.uncapture").setTag(-99));
//        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("formaarea.capturados", String.valueOf(overme.size())));
//        
//        if (!overme.isEmpty()) {
//            res.add(InspectorProperty.PropertyFactorySeparador("formaarea.capturados"));
//
//            overme.stream().filter((dime) -> (dime instanceof Forma)).map((dime) -> (Forma) dime).forEach((f) -> {
//                res.add(InspectorProperty.PropertyFactoryActionSelect(Editor.fromConfiguracao.getValor("diagrama." + Editor.getClassTexto(f) + ".nome"),
//                        f.getTexto(),
//                        String.valueOf(((FormaElementar) f.getPrincipal()).getID())));
//            });
//        }
        return res;
    }

    public void SetDimensaoTexto(String txt) {
        if (getMaster().getEditor().getInspectorEditor().getSelecionado() == null) {
            return;
        }
        int tag = getMaster().getEditor().getInspectorEditor().getSelecionado().getTag();
        DimensionadorArea dime = getRegioes().get(tag);
        dime.setTexto(txt);
        InvalidateArea();
    }

    public void SetDimensaoLargura(int larg) {
        if (getMaster().getEditor().getInspectorEditor().getSelecionado() == null) {
            return;
        }
        int tag = getMaster().getEditor().getInspectorEditor().getSelecionado().getTag();
        DimensionadorArea dime = getRegioes().get(tag);
        dime.setLargura(larg);
        RePosicioneRegioes();
        InvalidateArea();
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "MoverSubs", isMoverSubs()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "autoCapture", isAutoCapture()));
        me.appendChild(util.XMLGenerate.ValorString(doc, "areaDefault", getAreaDefault()));

        Element dime = doc.createElement("Dimensoes");

        getRegioes().forEach(c -> {
            c.ToXmlValores(doc, dime);
        });
        me.appendChild(dime);
        SerializeListener(doc, me);

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Dashed", isDashed()));

        //me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Gradiente", isGradiente()));
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

        setAreaDefault(util.XMLGenerate.getValorStringFrom(me, "areaDefault"));
        setMoverSubs(util.XMLGenerate.getValorBooleanFrom(me, "MoverSubs"));
        setAutoCapture(util.XMLGenerate.getValorBooleanFrom(me, "autoCapture"));

        NodeList ptLst = me.getElementsByTagName("Dimensoes");
        Element pontos = (Element) ptLst.item(0);
        ptLst = pontos.getChildNodes();

        for (int i = 0; i < ptLst.getLength(); i++) {
            Node tmp = ptLst.item(i);
            if (tmp.getNodeType() == Node.ELEMENT_NODE) {
                DimensionadorArea c = AddRegiao();
                c.LoadFromXML((Element) ptLst.item(i), colando);
            }
        }

        setDashed(util.XMLGenerate.getValorBooleanFrom(me, "Dashed"));
        //setGradiente(util.XMLGenerate.getValorBooleanFrom(me, "Gradiente"));
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
        return true;
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        //if (getTipoDesenho() != TipoDraw.tpTexto) {
        ArrayList<InspectorProperty> res = GP;
        res.add(InspectorProperty.PropertyFactorySeparador("texto.gradiente"));
        GP.add(InspectorProperty.PropertyFactorySN("linha.dashed", "setDashed", isDashed()));
        GP.add(InspectorProperty.PropertyFactoryNumero("diagrama.detalhe.alfa", "SetAlfa", (int) (100 * getAlfa())));

        //String[] grupo = new String[]{"setGradienteStartColor", "setGradienteEndColor", "setGDirecao"
        //};
        //res.add(InspectorProperty.PropertyFactorySN("texto.gradiente.is", "setGradiente", isGradiente()).AddCondicaoForFalse(new String[]{"setBackColor"}).AddCondicaoForTrue(grupo));
        res.add(InspectorProperty.PropertyFactoryCor("texto.gradiente.startcor", "setGradienteStartColor", getGradienteStartColor()));

        res.add(InspectorProperty.PropertyFactoryCor("texto.gradiente.endcor", "setGradienteEndColor", getGradienteEndColor()));

        res.add(InspectorProperty.PropertyFactoryMenu("texto.gradiente.direcao", "setGDirecao", getGDirecao(), Editor.fromConfiguracao.getLstDirecao(Controler.Comandos.cmdTexto)));

//            ArrayList<String> ngrp = new ArrayList<>(Arrays.asList(grupo));
//            ngrp.add("setGradiente");
//            ngrp.add("setBackColor");
//            ngrp.add("setGDirecao");
        //}
        ArrayList<FormaElementar> overme = WhoIsOverMe();
        res.add(InspectorProperty.PropertyFactorySeparador("formaarea.overme"));
        res.add(InspectorProperty.PropertyFactorySN("formaarea.movesubs", "setMoverSubs", isMoverSubs()));
        res.add(InspectorProperty.PropertyFactorySN("formaarea.autocapture", "setAutoCapture", isAutoCapture()));
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "formaarea.capture").setTag(99));
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "formaarea.uncapture").setTag(-99));
        res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("formaarea.capturados", String.valueOf(overme.size())));

        if (!overme.isEmpty()) {
            res.add(InspectorProperty.PropertyFactorySeparador("formaarea.capturados", true));

            overme.stream().filter((dime) -> (dime instanceof Forma)).map((dime) -> (Forma) dime).forEach((f) -> {
                res.add(InspectorProperty.PropertyFactoryActionSelect(Editor.fromConfiguracao.getValor("diagrama." + Editor.getClassTexto(f) + ".nome"),
                        f.getTexto(),
                        String.valueOf(((FormaElementar) f.getPrincipal()).getID())));
            });
        }

        res.add(InspectorProperty.PropertyFactorySeparador("formaarea.area", true));

        res.add(InspectorProperty.PropertyFactoryTexto("formaarea.area.default", "setAreaDefault", getAreaDefault()));
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdAdicionarSubItem.name()).setTag(-1));

        int i = 0;
        for (DimensionadorArea dime : getRegioes()) {
            res.add(InspectorProperty.PropertyFactorySeparador("formaarea.area", true));
            res.add(InspectorProperty.PropertyFactoryTexto("formaarea.area", "SetDimensaoTexto", dime.getTexto()).setTag(i));
            res.add(InspectorProperty.PropertyFactoryNumero("formaarea.largura", "SetDimensaoLargura", dime.getLargura()).setTag(i));
            res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdExcluirSubItem.name()).setTag(i));
            i++;
        }

        return super.CompleteGenerateProperty(GP);
    }

    private boolean autoCapture = true;

    public boolean isAutoCapture() {
        return autoCapture;
    }

    public void setAutoCapture(boolean autoCapture) {
        this.autoCapture = autoCapture;
    }

    @Override
    public boolean IsMe(Point p) {
        boolean s = super.IsMe(p);
        return (s && !(p.y > getTop() + (2 * alturaTexto) - 1));
    }

    @Override
    public void DoPontoCor(boolean verde) {
        super.DoPontoCor(verde);
        Color cor = verde ? getMaster().getPontoCorMultSel() : getMaster().getPontoCor();
        getRegioes().forEach((dim) -> {
            dim.setBackColor(cor);
        });
    }

    @Override
    public void HidePontos(boolean esconde) {
        super.HidePontos(esconde); //To change body of generated methods, choose Tools | Templates.
        getRegioes().forEach((dim) -> {
            dim.setIsHide(esconde);
        });
    }

    @Override
    public void Reposicione() {
        super.Reposicione(); //To change body of generated methods, choose Tools | Templates.
        RePosicioneRegioes();
    }

    private String NomeieDimensoes() {
        String txt = Editor.fromConfiguracao.getValor("Inspector.obj.formaarea.area");
        int res = 1;
        ArrayList<String> txts = new ArrayList<>();
        txts.add(areaDefault);
        getRegioes().forEach((el) -> {
            txts.add(el.getTexto());
        });
        while (txts.indexOf(txt + "_" + res) != -1) {
            res++;
        }
        return txt + "_" + res;
    }

    @Override
    public void ExcluirSubItem(int idx) {
        super.ExcluirSubItem(idx);
        try {
            Remova(getRegioes().get(idx));
        } catch (Exception e) {
            util.BrLogger.Logger("MSG-EXCLUIR_SUBITEM", e.getMessage());
        }
    }

    @Override
    public void AdicionarSubItem(int idx) {
        //super.AdicionarSubItem(idx);
        AddRegiao(Math.max(getWidth() / (getRegioes().size() + 2), 20));
    }

    private boolean moverSubs = true;

    public boolean isMoverSubs() {
        return moverSubs;
    }

    public void setMoverSubs(boolean moverSubs) {
        this.moverSubs = moverSubs;
    }

    @Override
    public void DoMove(int movX, int movY) {
        if (!isMoverSubs()) {
            super.DoMove(movX, movY);
            return;
        }
        ArrayList<FormaElementar> overme = WhoIsOverMe();
        overme.stream().filter((el) -> (!el.isSelecionado() && el.isVisible() && IsThatOverAndCanMove(el))).forEach((el) -> {
            el.DoMove(movX, movY);
        });
//        for (FormaElementar el : overme) {
//            if (!el.isSelecionado() && el.isVisible() && IsThatOverAndCanMove(el)) {
//                el.DoMove(movX, movY);
//            }
//        }
        overme.stream().filter((item) -> (!item.Reenquadre())).forEach((item) -> {
            item.Reposicione();
        });
        super.DoMove(movX, movY);
    }

    /**
     * O Artefato está sobre ou abaixo deste FormaArea? Ele pode ser movimentável?
     *
     * @param el
     * @return
     */
    public boolean IsThatOverAndCanMove(FormaElementar el) {
        Point r = el.getLocation();
        Point lr = getLocation();
        return (r.x > lr.x && r.y > lr.y && el.getLeftWidth() < getLeftWidth() && el.getTopHeight() < getTopHeight());
    }

    /**
     * O Artefato está sobre ou abaixo deste FormaArea? Ele pode ser capturado para futuro movimento?
     *
     * @param el
     * @return
     */
    public boolean IsThatOverAndCanCapture(FormaElementar el) {
        Point r = el.getLocation();
        Point lr = getLocation();
        return (r.x > lr.x && r.y > lr.y && el.getLeftWidth() < getLeftWidth() && el.getTopHeight() < getTopHeight() && !(el instanceof SuperLinha));
    }

    public void comandoCaptureOverMe() {
        comandoUnCapture();
        getMaster().getListaDeItens().stream().filter((el) -> (el != this && el.isVisible() && IsThatOverAndCanCapture(el))).forEach((el) -> {
            Capture(el);
        });
    }

    public void comandoUnCapture() {
        ArrayList<FormaElementar> overme = WhoIsOverMe();
        overme.forEach((el) -> {
            PerformLigacao(el, false);
        });
    }

    private void Capture(FormaElementar el) {
        if (el.getListeners() != null) {
            int i = 0;
            while (i < el.getListeners().size()) {
                ElementarListener lig = el.getListeners().get(i);
                if (lig instanceof FormaArea) {
                    FormaArea tmp = (FormaArea) lig;
                    el.PerformLigacao(tmp, false);
                } else {
                    i++;
                }
            }
        }
        PerformLigacao(el, true);
    }

    public void TestAndCapture(FormaElementar el) {
        if (!IsThatOverAndCanCapture(el)) {
            return;
        }
        PerformLigacao(el, false);
        if (el.getListeners() != null) {
            int i = 0;
            while (i < el.getListeners().size()) {
                ElementarListener lig = el.getListeners().get(i);
                if (lig instanceof FormaArea) {
                    FormaArea tmp = (FormaArea) lig;
                    el.PerformLigacao(tmp, false);
                } else {
                    i++;
                }
            }
        }
        PerformLigacao(el, true);
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == 99) {
            comandoCaptureOverMe();
        } else {
            if (Tag == -99) {
                comandoUnCapture();
            }
        }
    }

    private ArrayList<FormaElementar> WhoIsOverMe() {
        ArrayList<FormaElementar> overme = new ArrayList<>();
        if (getListeners() != null) {
            for (int i = 0; i < getListeners().size(); i++) {
                ElementarListener lig = getListeners().get(i);
                if (lig instanceof FormaElementar) {
                    overme.add((FormaElementar) lig);
                }
            }
        }
        return overme;
    }

    @Override
    public boolean CommitXML(Element me, HashMap<Element, FormaElementar> mapa) {
        UnSerializeListener(me, mapa);
        return super.CommitXML(me, mapa); //To change body of generated methods, choose Tools | Templates.
    }

}
