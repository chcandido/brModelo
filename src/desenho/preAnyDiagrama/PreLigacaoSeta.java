/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preAnyDiagrama;

import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Forma;
import desenho.linhas.PontoDeLinha;
import desenho.linhas.SuperLinha;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author ccandido
 */
public class PreLigacaoSeta extends SuperLinha {

    private static final long serialVersionUID = -8546091399978837981L;

    public PreLigacaoSeta(Diagrama diagrama) {
        super(diagrama);
        setInteligente(true);
        showConfigSeta = true;
        setSetaLargura(20);
    }

    @Override
    public boolean Destroy() {
        ArrayList<PontoDeLinha> pontos = getPontos();
        pontos.stream().forEach((pdl) -> {
            pdl.Destroy();
        });
        return super.Destroy();
    }

    @Override
    public void Inicie(Rectangle r) {
        super.Inicie(0, new Point(r.x, r.y), new Point(r.x + r.width, r.y + r.height)); //To change body of generated methods, choose Tools | Templates.
        setTemSetaPontaB(true);
        AnexePontos();
        OrganizeLinha();
        reSetBounds();
    }
    
    public void SuperSetInteligente(boolean sn) {
        SetInteligente(sn);
        OrganizeLinha();
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        GP.add(InspectorProperty.PropertyFactorySN("linha.auto", "SuperSetInteligente", isInteligente()));
        GP.add(InspectorProperty.PropertyFactoryCor("forecolor", "setForeColor", getForeColor()));
        
        GP = super.CompleteGenerateProperty(GP);

        if (getPontaA().getEm() != null || getPontaB().getEm() != null) {
            GP.add(InspectorProperty.PropertyFactorySeparador("ligacoes"));

            if (getPontaA().getEm() != null) {
                Forma f = getPontaA().getEm();
                InspectorProperty ipp = InspectorProperty.PropertyFactoryActionSelect(Editor.fromConfiguracao.getValor("diagrama." + Editor.getClassTexto(f) + ".nome"),
                        f.getTexto(),
                        String.valueOf(f.getID()));
                GP.add(ipp);
            }
            if (getPontaB().getEm() != null) {
                Forma f = getPontaB().getEm();
                InspectorProperty ipp = InspectorProperty.PropertyFactoryActionSelect(Editor.fromConfiguracao.getValor("diagrama." + Editor.getClassTexto(f) + ".nome"),
                        f.getTexto(),
                        String.valueOf(f.getID()));
                GP.add(ipp);
            }
        }
        return GP;
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);

        //remover Bounds.
        NodeList nl = me.getElementsByTagName("Bounds");
        me.removeChild(nl.item(0));

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Inteligente", isInteligente()));
        //me.appendChild(util.XMLGenerate.ValorBoolean(doc, "SetaAberta", isSetaAberta())); //já

        //me.appendChild(util.XMLGenerate.ValorBoolean(doc, "TemSetaPontaA", isTemSetaPontaA()));
        //me.appendChild(util.XMLGenerate.ValorBoolean(doc, "TemSetaPontaB", isTemSetaPontaB()));

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
    }

    @Override
    public boolean CommitXML(Element me, HashMap<Element, FormaElementar> mapa) {
        //NodeList ligLst = me.getElementsByTagName("Ligacoes");
        //Element lig =  (Element) ligLst.item(0);
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
        return super.CommitXML(me, mapa);
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }

        setInteligente(util.XMLGenerate.getValorBooleanFrom(me, "Inteligente"));
        //setSetaAberta(util.XMLGenerate.getValorBooleanFrom(me, "SetaAberta")); //já
        //setTemSetaPontaA(util.XMLGenerate.getValorBooleanFrom(me, "TemSetaPontaA"));
        //setTemSetaPontaB(util.XMLGenerate.getValorBooleanFrom(me, "TemSetaPontaB"));
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
        return true;
    }

}
