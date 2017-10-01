/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.formas;

import controlador.Editor;
import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.linhas.PontoDeLinha;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import util.DesenhadorDeTexto;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author ccandido
 */
public class Legenda extends Forma {

    private static final long serialVersionUID = -706529994751090967L;

    public Legenda(Diagrama modelo) {
        super(modelo);
        //setForeColorWithOutRepaint(Color.LIGHT_GRAY);
        SetTexto(Editor.fromConfiguracao.getValor("diagrama.Legenda"));
//        DesenhadorDeTexto edt = getTextoFormatado();
//        edt.setCentrarTextoVertical(false);
//        edt.setAlinharEsquerda(true);

//        Itens.add(new ItemDeLegenda("Vermelho", Color.red));
//        Itens.add(new ItemDeLegenda("Vermelho adfs dsfsfsafsf", Color.red));
//        Itens.add(new ItemDeLegenda("Vermelho azul", Color.blue));
    }

    public void addLegenda(String res, Color c) {
        addLegenda(res, c, 0);
    }

    public void addLegenda(String res, Color c, int tag) {
        getItens().add(new Legenda.ItemDeLegenda(res, c, tag));
        int tam = ((altura + 4) * getItens().size()) + alturaTitulo;
        if (tam != getHeight()) {
            setHeight(tam);
            Reposicione();
        } else {
            InvalidateArea();
        }
    }

    protected void AddLegenda(String res, Color c, int tag) {
        getItens().add(new Legenda.ItemDeLegenda(res, c, tag));
    }

    @Override
    public void mouseDblClicked(MouseEvent e) {
        super.mouseDblClicked(e);
        if (getTipo() == TipoLegenda.tpCores) {
            LoadColorList();
        }
    }

    public void LoadColorList() {
        ArrayList<Color> co = new ArrayList<>();
        getMaster().getListaDeItens().stream().forEach((fe) -> {
            fe.PoluleColors(co);
        });
        getItens().stream().map((it) -> co.indexOf(it.cor)).filter((tmp) -> (tmp > -1)).forEach((tmp) -> {
            co.remove(tmp);
        });

        co.stream().forEach((c) -> {
            addLegenda("?", c);
        });

        DoMuda();
    }

    public void setTextLegenda(int selectedIndex, String res) {
        getItens().get(selectedIndex).texto = res;
    }

    public void RefreshInpector() {
        if (isSelecionado()) {
            getMaster().getEditor().PerformInspectorFor(this);
            InvalidateArea();
        }
    }

    public boolean canShowEditor() {
        return getTipo() != TipoLegenda.tpObjetos;
    }

    private void SelecioneLegenda(MouseEvent e) {
        boolean ja = false;
        for (ItemDeLegenda leg : getItens()) {
            leg.setSelecionada(false);
            if (!ja) {
                Rectangle r = new Rectangle(getLeft(), leg.Area.x, getWidth(), leg.Area.y);
                if (r.contains(e.getPoint())) {
                    ja = true;
                    leg.setSelecionada(true);
                }
            }
        }
        InvalidateArea();
    }

    public void SelecioneLegenda(int idx) {
        boolean ja = false;
        int i = -1;
        for (ItemDeLegenda leg : getItens()) {
            leg.setSelecionada(false);
            if (!ja) {
                if (++i == idx) {
                    ja = true;
                    leg.setSelecionada(true);
                }
            }
        }
        InvalidateArea();
    }

    public class ItemDeLegenda implements Serializable {

        public ItemDeLegenda(String texto, Color cor, int tag) {
            this.texto = texto;
            this.cor = cor;
            this.tag = tag;
        }

        protected int tag = 0;
        protected String texto;
        protected Color cor;

        public void setTag(int tag) {
            this.tag = tag;
        }

        public int getTag() {
            return tag;
        }

        public String getTexto() {
            return this.texto;
        }

        public Color getCor() {
            return this.cor;
        }

        private boolean selecionada = false;

        public boolean isSelecionada() {
            return selecionada;
        }

        public void setSelecionada(boolean selecionada) {
            this.selecionada = selecionada;
        }

        protected transient Point Area = new Point(0, 0);
    }

    private final ArrayList<ItemDeLegenda> Itens = new ArrayList<>();

    public ArrayList<ItemDeLegenda> getItens() {
        return Itens;
    }

    public void RemoveLegenda(int index) {
        try {
            getItens().remove(index);
            InvalidateArea();
        } catch (Exception e) {
            return;
        }
        int tam = ((altura + 4) * getItens().size()) + (2 * altura) - 4;
        if (tam != getHeight()) {
            setHeight(tam);
            Reposicione();
        } else {
            InvalidateArea();
        }

    }

    private Color BorderColor = Color.LIGHT_GRAY;

    public Color getBorderColor() {
        return isDisablePainted()? disabledColor : BorderColor;
    }

    public void setBorderColor(Color BorderColor) {
        this.BorderColor = BorderColor;
        InvalidateArea();
    }

    @Override
    public void SetTexto(String Texto) {
        super.SetTexto(Texto);
    }

    @Override
    public DesenhadorDeTexto getTextoFormatado() {
        DesenhadorDeTexto dz = super.getTextoFormatado();
        dz.setCentrarTextoVertical(false);
        dz.setCentrarTextoHorizontal(true);
        dz.LimitarAreaDePintura = true;
        dz.CorretorPosicao = new Point(0, 3 * distSelecao);
        return dz;
    }

    public enum TipoLegenda {

        tpCores, tpLinhas, tpObjetos
    };

    private TipoLegenda tipo = TipoLegenda.tpCores;

    public TipoLegenda getTipo() {
        return tipo;
    }

    public void setTipo(TipoLegenda tipo) {
        if (this.tipo != tipo) {
            this.tipo = tipo;
            getItens().clear();
            DoMuda();
            InvalidateArea();
        }
    }

    public void SetTipo(int tpForInspector) {
        try {
            setTipo(TipoLegenda.values()[tpForInspector]);
        } catch (Exception e) {
        }
    }

    private int altura = 22;
    private int alturaTitulo = 22;

    @Override
    public void DoPaint(Graphics2D g) {
        super.DoPaint(g);
        Rectangle rec = getArea();
        Color bkpc = g.getColor();

        g.setColor(getBorderColor());

        g.drawRect(rec.x, rec.y, rec.width, rec.height);

        Rectangle bkp = g.getClipBounds();
        g.clipRect(rec.x, rec.y, rec.width, rec.height);

        Font fn = getFont();
        Font font = new Font(fn.getFontName(), fn.getStyle(), fn.getSize() - 2);

        fn = g.getFont();
        g.setFont(font);

        altura = g.getFontMetrics().getHeight() + g.getFontMetrics().getDescent();
        alturaTitulo = altura + altura / 2;

        Composite originalComposite = g.getComposite();
        float alfa = 0.8f;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfa));

        if (getTipo() == TipoLegenda.tpObjetos) {
            altura = Math.max(32, altura);
        }

        int posi = altura + alturaTitulo + rec.y;
        final int lft = rec.x + 2;

        for (ItemDeLegenda it : getItens()) {

            if (it.isSelecionada()) {
                g.setColor(isDisablePainted()? disabledColor : new Color(204, 204, 255, 50));
                g.fillRect(lft, posi - altura - 2, getWidth(), altura + 4);
            }
            g.setColor(isDisablePainted()? disabledColor : it.cor);
            int moveleft;
            switch (getTipo()) {
                case tpLinhas:
                    moveleft = 3 * altura;
                    g.fillRoundRect(lft, posi - (altura / 2) - 2, moveleft - 2, 4, 2, 2);
                    g.setColor(bkpc);
                    g.drawString(it.texto, lft + moveleft, posi - 6);
                    break;
                case tpObjetos:
                    ImageIcon img = Editor.fromControler().getImagemNormal(getMaster().getCassesDoDiagrama()[it.getTag()].getSimpleName());
                    g.drawImage(util.TratadorDeImagens.ReColorBlackImg(img, it.getCor()), lft, posi - altura, null);
                    moveleft = altura + 2;
                    g.drawString(it.texto, lft + moveleft, posi - altura / 2 + 6);
                    break;
                default:
                    moveleft = altura;
                    g.fillRect(lft, posi - altura, altura - 4, altura - 4);
                    g.setColor(bkpc);
                    g.drawRect(lft, posi - altura, altura - 4, altura - 4);
                    g.drawString(it.texto, lft + moveleft, posi - 6);
            }
            it.Area = new Point(posi - altura - 2, altura + 4);
            posi += altura + 4;
        }

        g.setComposite(originalComposite);

//        g.setColor(Color.LIGHT_GRAY);
//        g.drawLine(lft - 1, posi - altura - 2, getLeft() + getWidth() - 1, posi - altura - 2);
        g.setClip(bkp);
        g.setFont(fn);
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        res.add(InspectorProperty.PropertyFactoryCor("bordercolor", "setBorderColor", this.getBorderColor()));
        res.add(InspectorProperty.PropertyFactoryMenu("legenda.tipo", "SetTipo", getTipo().ordinal(), Editor.fromConfiguracao.getLstTipoLegenda())
                .AddCondicao(new String[]{String.valueOf(TipoLegenda.tpCores.ordinal())}, new String[]{nomeComandos.cmdDoAnyThing.name()})
                .AddCondicao(new String[]{String.valueOf(TipoLegenda.tpCores.ordinal()), String.valueOf(TipoLegenda.tpLinhas.ordinal())}, new String[]{nomeComandos.cmdDlgLegenda.name()})
        );

        res.add(InspectorProperty.PropertyFactorySeparador("desenho"));

        //if (canShowEditor()) {
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDlgLegenda.name()));
        //}

        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdAdicionarSubItem.name()).setTag(-1));
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "legenda.capturar").setTag(124));

        //remover dicionário do objeto legenda.
        InspectorProperty tmp = InspectorProperty.FindByProperty(res, "setTextoAdicional");
        if (tmp != null) {
            res.remove(tmp);
        }

        tmp = InspectorProperty.FindByProperty(res, "setForeColor");
        tmp.ReSetCaptionFromConfig("texto.forecolor");

        int i = 0;
        for (ItemDeLegenda it : getItens()) {
            if (it.isSelecionada()) {
                res.add(InspectorProperty.PropertyFactorySeparador("legenda.legenda.sel", false));
            } else {
                res.add(InspectorProperty.PropertyFactorySeparador("legenda.legenda", true));
            }
            res.add(InspectorProperty.PropertyFactoryTexto("legenda.texto", "setLegendaTexto", it.getTexto()).setTag(i));
            res.add(InspectorProperty.PropertyFactoryCor("legenda.cor", "setLegendaCor", it.getCor()).setTag(i));

            if (!canShowEditor()) {
                ArrayList<String> objs = new ArrayList<>();
                for (Class cl : getMaster().getCassesDoDiagrama()) {
                    objs.add(Editor.fromConfiguracao.getValor("diagrama." + cl.getSimpleName() + ".nome"));
                }
                res.add(InspectorProperty.PropertyFactoryMenu("legenda.objs", "setLegendaTag", it.getTag(), objs).setTag(i));
            }

            res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdExcluirSubItem.name()).setTag(i));
            i++;
        }

        return res;
    }

    public void setLegendaTexto(String txt) {
        if (getMaster().getEditor().getInspectorEditor().getSelecionado() == null) {
            return;
        }
        int tag = getMaster().getEditor().getInspectorEditor().getSelecionado().getTag();
        Legenda.ItemDeLegenda leg = getItens().get(tag);
        leg.texto = txt;
        DoMuda();
        SelecioneLegenda(tag);
        InvalidateArea();
    }

    public void setLegendaTag(int tg) {
        if (getMaster().getEditor().getInspectorEditor().getSelecionado() == null) {
            return;
        }
        int tag = getMaster().getEditor().getInspectorEditor().getSelecionado().getTag();
        Legenda.ItemDeLegenda leg = getItens().get(tag);
        leg.setTag(tg);
        DoMuda();
        SelecioneLegenda(tag);
        InvalidateArea();
    }

    public void setLegendaCor(Color cor) {
        if (getMaster().getEditor().getInspectorEditor().getSelecionado() == null) {
            return;
        }
        int tag = getMaster().getEditor().getInspectorEditor().getSelecionado().getTag();
        Legenda.ItemDeLegenda leg = getItens().get(tag);
        leg.cor = cor;
        DoMuda();
        SelecioneLegenda(tag);
        InvalidateArea();
    }

    @Override
    protected void DoPaintDoks(Graphics2D g) {
    }

    @Override
    public boolean CanLiga(PontoDeLinha aThis) {
        return false;
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        /**
         * Os subitens somente serão recarrecados no tipo de modelo onde foram criados se for do TipoLegenda == Objetos.
         */
        me.appendChild(util.XMLGenerate.ValorText(doc, "Diagrama", getMaster().getTipoDeDiagramaFormatado()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Tipo", getTipo().ordinal()));
        Element sbItens = doc.createElement("Itens");
        for (ItemDeLegenda il : getItens()) {
            sbItens.appendChild(util.XMLGenerate.ValorLegenda(doc, il.getTexto(), "Texto", il.getCor(), "Cor", il.getTag(), "Tag"));
        }
        me.appendChild(sbItens);

        //remover dicionário do XML do objeto legenda.
//        NodeList nl = me.getElementsByTagName("Dicionario");
//        if (nl != null && nl.getLength() > 0) {
//            me.removeChild(nl.item(0));
//        }
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        String tpdiag = util.XMLGenerate.getValorTextoFrom(me, "Diagrama");
        SetTipo(util.XMLGenerate.getValorIntegerFrom(me, "Tipo"));

        //copiou em um tipo de diagrama e está colando em outro!
        if (getTipo() == TipoLegenda.tpObjetos && (!getMaster().getTipoDeDiagramaFormatado().equals(tpdiag))) {
            return true;
        }
        NodeList ptLst = me.getElementsByTagName("Itens");
        Element eitens = (Element) ptLst.item(0);
        ptLst = eitens.getChildNodes();
        for (int i = 0; i < ptLst.getLength(); i++) {
            Node tmp = ptLst.item(i);
            if (tmp.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) ptLst.item(i);
                String txt = e.getAttribute("Texto");
                Color c = util.Utilidades.StringToColor(e.getAttribute("Cor"));// new Color(Integer.valueOf(e.getAttribute("Cor")));
                int tag = Integer.valueOf(e.getAttribute("Tag"));
                AddLegenda(txt, c, tag);
            }
        }
        InvalidateArea();
        return true;
    }

    @Override
    public void ExcluirSubItem(int idx) {
        super.ExcluirSubItem(idx);
        RemoveLegenda(idx);
    }

    @Override
    public void AdicionarSubItem(int idx) {
        addLegenda("?", Color.BLACK);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        ItemDeLegenda leg = null;
        if (isSelecionado()) {
            leg = getItens().stream().filter(it -> it.isSelecionada()).findAny().orElse(null);
        }
        if (isSelecionavel()) {
            SelecioneLegenda(e);
        }
        super.mousePressed(e);
        if (leg != null) {
            ItemDeLegenda leg2 = getItens().stream().filter(it -> it.isSelecionada()).findAny().orElse(null);;
            if (leg2 != null && leg != leg2) {
                getMaster().PerformInspector(true);
            }
        }
//        super.mousePressed(e);
//        if (isSelecionado()) {
//            SelecioneLegenda(e);
//        }
///16/12/2016 - testar mais!
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == 124) {
            if (getTipo() == TipoLegenda.tpCores) {
                LoadColorList();
            }
        }
    }
}
