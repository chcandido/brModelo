/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.logico;

import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Forma;
import desenho.linhas.PontoDeLinha;
import desenho.linhas.SuperLinha;
import desenho.preAnyDiagrama.PreCardinalidade.TiposCard;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Linha usada para o modelo lógico (pode ser aproveitada), possui duas cardinalidades
 *
 * @author ccandido
 */
public class LogicoLinha extends SuperLinha {

    private static final long serialVersionUID = 250742280172172228L;

    // <editor-fold defaultstate="collapsed" desc="Criação">
    public LogicoLinha(Diagrama modelo) {
        super(modelo);
        setInteligente(true);
        InitLCC();
    }

    public LogicoLinha(Diagrama modelo, boolean comCard) {
        super(modelo);
        setInteligente(true);
        if (comCard) {
            InitLCC();
        }
    }

    protected final void InitLCC() {
        setCardA(new LogicoCardinalidade(getMaster(), LogicoCardinalidade.class.getSimpleName()));
        setCardB(new LogicoCardinalidade(getMaster(), LogicoCardinalidade.class.getSimpleName()));
        getCardA().setCard(1);
        //PrepareCardinalidade();
        getCardA().Fixe(null);
        getCardB().Fixe(null);
    }

    // </editor-fold>
    private LogicoCardinalidade CardA;
    private LogicoCardinalidade CardB;

    public LogicoCardinalidade getCardA() {
        return CardA;
    }

    public LogicoCardinalidade getCardB() {
        return CardB;
    }

    public void setCardA(LogicoCardinalidade aCard) {
        if (this.CardA != aCard) {
            if (this.CardA != null) {
                CardA.setLigadoA(null);
            }
            this.CardA = aCard;
            if (this.CardA != null) {
                CardA.setLigadoA(this);
            }
        }
    }

    public void setCardB(LogicoCardinalidade aCard) {
        if (this.CardB != aCard) {
            if (this.CardB != null) {
                CardB.setLigadoA(null);
            }
            this.CardB = aCard;
            if (this.CardB != null) {
                CardB.setLigadoA(this);
            }
        }
    }

    public void SuperInicie(int tlPt, Point ptPrimeiro, Point ptFinal) {
        Inicie(tlPt, ptPrimeiro, ptFinal);
        AnexePontos();
        OrganizeLinha();
        reSetBounds();
    }

    public void SuperSetInteligente(boolean sn) {
        SetInteligente(sn);
        OrganizeLinha();
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        res.stream().filter((iip) -> (iip.tipo == InspectorProperty.TipoDeProperty.tpNumero)).forEach((iip) -> {
            iip.tipo = InspectorProperty.TipoDeProperty.tpApenasLeituraTexto;
        });
        res.add(InspectorProperty.PropertyFactoryCor("forecolor", "setForeColor", getForeColor()));
        res.add(InspectorProperty.PropertyFactorySN("linha.auto", "SuperSetInteligente", isInteligente()));
        return res;
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);

        //remover Bounds.
        NodeList nl = me.getElementsByTagName("Bounds");
        me.removeChild(nl.item(0));

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Inteligente", isInteligente()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "SetaAutomatica", isSetaAutomatica()));

        if (!getForeColor().equals(Elementar.defaultColor)) {
            me.appendChild(util.XMLGenerate.ValorColor(doc, "ForeColor", getForeColor()));
        }
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Largura", (int) getLargura()));

        Element lig = doc.createElement("Ligacoes");
        util.XMLGenerate.AtributoRefFormElementar(lig, "PontaA", getFormaPontaA());
        util.XMLGenerate.AtributoRefFormElementar(lig, "PontaB", getFormaPontaB());
        me.appendChild(lig);

        Element sbPontos = doc.createElement("Pontos");
        for (PontoDeLinha pl : getPontos()) {
            sbPontos.appendChild(util.XMLGenerate.ValorPoint(doc, "Ponto", pl.getLocation()));
        }
        me.appendChild(sbPontos);

        if (getCardA() != null) {
            getCardA().ToXlm(doc, me);
        }
        if (getCardB() != null) {
            getCardB().ToXlm(doc, me);
        }
    }

    @Override
    public boolean CommitXML(Element me, HashMap<Element, FormaElementar> mapa) {
        Element lig = util.XMLGenerate.FindByNodeName(me, "Ligacoes");

        String idPt = lig.getAttribute("PontaA");
        FormaElementar resA = util.XMLGenerate.FindWhoHasID(idPt, mapa);
        if (resA instanceof Forma) {
            getPontaA().SetEm((Forma) resA);
        }
        idPt = lig.getAttribute("PontaB");
        FormaElementar resB = util.XMLGenerate.FindWhoHasID(idPt, mapa);
        if (resB instanceof Forma) {
            getPontaB().SetEm((Forma) resB);
        }
        //reSetBounds()
        if (resA instanceof Forma) {
            ((Forma) resA).PosicionePonto(getPontaA());
        }
        if (resB instanceof Forma) {
            ((Forma) resB).PosicionePonto(getPontaB());
        }
        OrganizeLinha();
        PrepareCardinalidade();
        return super.CommitXML(me, mapa);
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }

        setInteligente(util.XMLGenerate.getValorBooleanFrom(me, "Inteligente"));
        setSetaAutomatica(util.XMLGenerate.getValorBooleanFrom(me, "SetaAutomatica"));

        Color c = util.XMLGenerate.getValorColorFrom(me, "ForeColor");
        if (c != null) {
            setForeColor(c);
        }
        int l = util.XMLGenerate.getValorIntegerFrom(me, "Largura");
        if (l != -1) {
            setLargura(l);
        }

        NodeList ptLst = me.getElementsByTagName("Pontos");
        Element pontos = (Element) ptLst.item(0);
        ptLst = pontos.getChildNodes();

        Inicie(ptLst.getLength() - 2, new Point(20, 20), new Point(40, 40));
        ArrayList<PontoDeLinha> arrpontos = getPontos();
        for (int i = 0; i < ptLst.getLength(); i++) {
            Point p = util.XMLGenerate.getValorPoint((Element) ptLst.item(i));
            arrpontos.get(i).setLocation(p);
        }
        NodeList tmp = me.getElementsByTagName(Editor.getClassTexto(getCardA()));
        getCardA().LoadFromXML((Element) tmp.item(0), colando);

        //apaga a referência a CardA para que CardB possa ser carregado.
        if (tmp.getLength() > 0) {
            me.removeChild(tmp.item(0));
            //Se não tiver os dois não carrega nenhum
            getCardB().LoadFromXML((Element) tmp.item(0), colando);
        }
        return true;
    }

    public void PrepareCardinalidade() {
        if (getPontaA() == null || getPontaB() == null) {
            return;
        }
        if (!getPontaA().isEstaLigado()) {
            getCardA().Fixe(null);
        } else {
            LogicoCardinalidade card = getCardA();
            card.setVisible(true);
            card.Fixe(getPontaA());
            card.Posicione();
        }
        if (!getPontaB().isEstaLigado()) {
            getCardB().Fixe(null);
        } else {
            LogicoCardinalidade card = getCardB();
            card.setVisible(true);
            card.Fixe(getPontaB());
            card.Posicione();
        }
    }

    @Override
    public void reSetBounds() {
        super.reSetBounds();
        PrepareCardinalidade();
    }

    @Override
    public boolean AnexePontos() {
        boolean res = super.AnexePontos();
        if (!res) {
            PrepareCardinalidade();
        }
        return res;
    }

    @Override
    public boolean Destroy() {
        PerformRoqued(false);
        ArrayList<PontoDeLinha> pontos = getPontos();
        for (PontoDeLinha pdl : pontos) {
            pdl.Destroy();
        }
        getCardA().setCanBeDeleted(true);
        getCardB().setCanBeDeleted(true);
        getMaster().Remove(getCardA(), false);
        getMaster().Remove(getCardB(), false);

        if (getFormaPontaA() instanceof Tabela) {
            Tabela tab1 = ((Tabela) getFormaPontaA());
            tab1.Desligacao(this);
        }

        if (getFormaPontaB() instanceof Tabela) {
            Tabela tab2 = ((Tabela) getFormaPontaB());
            tab2.Desligacao(this);
        }
        return super.Destroy();
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        GP = super.CompleteGenerateProperty(GP);
        if (showConfigSeta) {
            InspectorProperty tmp = InspectorProperty.FindByProperty(GP, "seta.titulo");
            GP.add(GP.indexOf(tmp) + 1, InspectorProperty.PropertyFactorySN("seta.setaautomatica", "setSetaAutomatica", isSetaAutomatica()).AddCondicaoForFalse(new String[]{"setTemSetaPontaA", "setTemSetaPontaB"}));
        }

        ArrayList<Forma> lst = new ArrayList<>();
        if (getFormaPontaA() != null) {
            lst.add(getFormaPontaA());
        }
        if (getFormaPontaB() != null) {
            lst.add(getFormaPontaB());
        }
        if (getCardB().isVisible()) {
            lst.add(0, getCardB());
        }
        if (getCardA().isVisible()) {
            lst.add(0, getCardA());
        }
        boolean ja = false;
        for (Forma f : lst) {
            InspectorProperty ipp = InspectorProperty.PropertyFactoryActionSelect(Editor.fromConfiguracao.getValor("diagrama." + Editor.getClassTexto(f) + ".nome"),
                    f.getTexto(),
                    String.valueOf(f.getID()));
            if (!ja) {
                ja = true;
                GP.add(InspectorProperty.PropertyFactorySeparador("ligacoes"));
            }
            GP.add(ipp);
        }
        return GP;
    }

    public void AlterCard(LogicoCardinalidade aThis) {
        if (aThis.getCard() == TiposCard.C01 || aThis.getCard() == TiposCard.C11) {
            return;
        }
        LogicoCardinalidade card = (aThis == getCardA() ? getCardB() : getCardA());
        if (card.getCard() == TiposCard.C0N) {
            card.setCard(TiposCard.C01);
        } else if (card.getCard() == TiposCard.C1N) {
            card.setCard(TiposCard.C11);
        }
        ajusteSeta();
    }

    @Override
    public FormaElementar getSub(int i) {
        if (i == 0) {
            return getCardA();
        }
        if (i == 1) {
            return getCardB();
        }
        return super.getSub(i);
    }

    /**
     * A cardinalidade controlará a direção da seta (true)
     */
    private boolean setaAutomatica = true;

    public boolean isSetaAutomatica() {
        return setaAutomatica;
    }

    public void setSetaAutomatica(boolean setaAutomatica) {
        if (this.setaAutomatica == setaAutomatica) {
            return;
        }
        this.setaAutomatica = setaAutomatica;
        if (setaAutomatica) {
            ajusteSeta();
        }
        InvalidateArea();
    }

    public void ajusteSeta() {
        if (isSetaAutomatica()) {
            int cardO = getCardA().getCard().ordinal();
            int cardD = getCardB().getCard().ordinal();
            if (cardO > cardD) {
                setTemSetaPontaA(true);
                setTemSetaPontaB(false);
            } else if (cardO == cardD) {
                setTemSetaPontaA(true);
                setTemSetaPontaB(true);

            } else {
                setTemSetaPontaA(false);
                setTemSetaPontaB(true);
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        PerformRoqued(false);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        PerformRoqued(true);
    }

    public void PerformRoqued(boolean sn) {
        Forma fmA = getFormaPontaA();
        Forma fmB = getFormaPontaB();

        if (fmA instanceof Tabela && fmB instanceof Tabela) {
            ((Tabela) fmA).setRoqued(sn, this);
            ((Tabela) fmB).setRoqued(sn, this);
        }
    }


    /**
     * Acesso externo ao fator_largura
     * @param f 
     */
    public void SetFatorLargura(float f) {
        fator_largura = f;
    }

}
