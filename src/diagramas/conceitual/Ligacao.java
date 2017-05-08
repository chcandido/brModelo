/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.conceitual;

import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Forma;
import desenho.linhas.PontoDeLinha;
import desenho.preAnyDiagrama.PreCardinalidade;
import desenho.preAnyDiagrama.PreEntidade;
import desenho.preAnyDiagrama.PreLigacao;
import desenho.preAnyDiagrama.PreRelacionamento;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author ccandido
 */
public class Ligacao extends PreLigacao {

    private static final long serialVersionUID = -8105972246347830149L;

    public Ligacao(Diagrama modelo) {
        super(modelo);
    }

//    public Ligacao(Diagrama modelo, boolean cardNome) {
//        super(modelo, cardNome);
//    }

    public Ligacao(Diagrama modelo, Cardinalidade aCard) {
        super(modelo, aCard);
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

    public boolean LigaComEntFraca() {
        return isDuplaLinha();
    }

    public boolean LigaRelacaoEntidade() {
        Forma fa = getFormaPontaA();
        Forma fb = getFormaPontaB();
        if ((fa instanceof PreEntidade) || (fb instanceof PreEntidade)) {
            if ((fa instanceof PreRelacionamento) || (fb instanceof PreRelacionamento)) {
                return true;
            }
        }
        return false;
    }

    public String getPapel() {
        if (getCard() != null) {
            return getCard().getPapel();
        }
        return "";
    }

    public void setPapel(String papel) {
        if (getCard() != null) {
            getCard().setPapel(papel);
        }
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        for (InspectorProperty iip : res) {
            if (iip.tipo == InspectorProperty.TipoDeProperty.tpNumero) {
                iip.tipo = InspectorProperty.TipoDeProperty.tpApenasLeituraTexto;
            }
        }

        res.add(InspectorProperty.PropertyFactoryCor("forecolor", "setForeColor", getForeColor()));

        res.add(InspectorProperty.PropertyFactorySN("linha.auto", "SuperSetInteligente", isInteligente()));
        //Ligado à entidade: é fraca?
        if (LigaRelacaoEntidade()) {
            res.add(InspectorProperty.PropertyFactorySeparador("mer"));
            res.add(InspectorProperty.PropertyFactoryTexto("cardinalidade.papel", "setPapel", getPapel()));
            res.add(InspectorProperty.PropertyFactorySN("linha.entidadefraca", "setDuplaLinha", LigaComEntFraca()));
        }
        return res;
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);

        //remover Bounds.
        NodeList nl = me.getElementsByTagName("Bounds");
        me.removeChild(nl.item(0));

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Inteligente", isInteligente()));

        if (!getForeColor().equals(Elementar.defaultColor)) {
            me.appendChild(util.XMLGenerate.ValorColor(doc, "ForeColor", getForeColor()));
        }
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Largura", (int) getLargura()));
        //me.appendChild(util.XMLGenerate.ValorInteger(doc, "Fator_Largura", (int)fator_largura));

        Element lig = doc.createElement("Ligacoes");
        util.XMLGenerate.AtributoRefFormElementar(lig, "PontaA", getFormaPontaA());
        util.XMLGenerate.AtributoRefFormElementar(lig, "PontaB", getFormaPontaB());
        me.appendChild(lig);

        Element sbPontos = doc.createElement("Pontos");
        for (PontoDeLinha pl : getPontos()) {
            sbPontos.appendChild(util.XMLGenerate.ValorPoint(doc, "Ponto", pl.getLocation()));
        }
        me.appendChild(sbPontos);
        
        PreCardinalidade card = getCard();
        if (card != null) {
            card.ToXlm(doc, me);
        }
    }

    @Override
    public boolean CommitXML(Element me, HashMap<Element, FormaElementar> mapa) {
        //NodeList ligLst = me.getElementsByTagName("Ligacoes");
        //Element lig =  (Element) ligLst.item(0);
        Element lig = util.XMLGenerate.FindByNodeName(me, "Ligacoes");

        String idPt = lig.getAttribute("PontaA");
        FormaElementar resA = util.XMLGenerate.FindWhoHasID(idPt, mapa);
        if (resA instanceof Forma) {
            getPontaA().SetEm((Forma)resA);
        }
        idPt = lig.getAttribute("PontaB");
        FormaElementar resB = util.XMLGenerate.FindWhoHasID(idPt, mapa);
        if (resB instanceof Forma) {
            getPontaB().SetEm((Forma)resB);
        }
        //reSetBounds()
        if (resA instanceof Forma) ((Forma)resA).PosicionePonto(getPontaA());
        if (resB instanceof Forma) ((Forma)resB).PosicionePonto(getPontaB());
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
        //cardinalidade
        ptLst = me.getElementsByTagName(Cardinalidade.class.getSimpleName());
        if (ptLst.getLength() > 0) {
            Element ecard = (Element) ptLst.item(0);
            addCard();
            PreCardinalidade card = getCard();
            if (card != null) {
                card.LoadFromXML(ecard, colando);
            }
        }
        //reSetBounds();
        return true;
    }

    private void removeCar() {
        PreCardinalidade card = getCard();
        if (card == null) return;
        setCard(null);
        card.Fixe(null);
        card.setCanBeDeleted(true);
        getMaster().Remove(card, true);
    }
    
    private void addCard() {
        PreCardinalidade card = getCard();
        if (card != null) return;
        //if (cardNome) {
            setCard(new Cardinalidade(getMaster(), "Cardinalidade"));
        //} else {
        //    setCard(new Cardinalidade(getMaster()));
        //}
    }
    
    @Override
    public void PrepareCardinalidade() {
//        PreCardinalidade card = getCard();
//        if (card == null) {
//            return;
//        }
        if (getPontaA() == null || getPontaB() == null) {
            removeCar();
            return;
        }
        
        if (!getPontaA().isEstaLigado() || !getPontaB().isEstaLigado()) {
            //removeCar();
            PreCardinalidade card = getCard();
            if (card != null) card.Fixe(null);
            return;
        }

        boolean t1 = getFormaPontaA() instanceof PreEntidade;
        boolean t1b = getFormaPontaB() instanceof PreRelacionamento;
        if (t1 && t1b) {
            addCard();
            PreCardinalidade card = getCard();
            card.setVisible(true);
            card.Fixe(getPontaA());
            card.Posicione();
            return;
        }

        boolean t2 = getFormaPontaB() instanceof PreEntidade;
        boolean t2b = getFormaPontaA() instanceof PreRelacionamento;
        if (t2 && t2b) {
            addCard();
            PreCardinalidade card = getCard();
            card.setVisible(true);
            card.Fixe(getPontaB());
            card.Posicione();
            return;
        }
        removeCar();
//        card.Fixe(null);
   }
}
