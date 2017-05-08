/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preAnyDiagrama;

import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.FormaElementar;
import desenho.formas.Forma;
import desenho.linhas.PontoDeLinha;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class PreLigacaoSetaComApenso extends PreLigacaoSeta {

    private static final long serialVersionUID = -2754545357698771189L;

    public PreLigacaoSetaComApenso(Diagrama diagrama) {
        super(diagrama);
    }

    @Override
    public void reSetBounds() {
        super.reSetBounds();
        PrepareTexto();
    }

    @Override
    public boolean AnexePontos() {
        boolean res = super.AnexePontos();
        if (!res) {
            PrepareTexto();
        }
        return res;
    }

    @Override
    public boolean Destroy() {
        ArrayList<PontoDeLinha> pontos = getPontos();
        for (PontoDeLinha pdl : pontos) {
            pdl.Destroy();
        }
        if (texto != null) {
            texto.setCanBeDeleted(true);
            getMaster().Remove(texto, false);
        }
        return super.Destroy();
    }

    private PreTextoApenso texto = null;

    public PreTextoApenso getTexto() {
        return texto;
    }

    public void setTexto(PreTextoApenso texto) {
        this.texto = texto;
        if (texto != null) {
            this.texto.SetLinhaMestre(this);
            this.texto.SetBounds(new Rectangle(0, 0, 80, 40));
            texto.setCanBeDeleted(false);
            if (!getMaster().isCarregando) {
                setTemSetaPontaB(true);
                texto.setAutosize(true);
            }
        }
    }

    public void PrepareTexto() {
    }

    @Override
    public void Inicie(Rectangle r) {
        super.Inicie(0, new Point(r.x, r.y), new Point(r.x + r.width, r.y + r.height)); //To change body of generated methods, choose Tools | Templates.
        AnexePontos();
        OrganizeLinha();
        reSetBounds();
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        if (getTexto() != null) {
            getTexto().ToXlm(doc, me);
        }
    }

    @Override
    public boolean CommitXML(Element me, HashMap<Element, FormaElementar> mapa) {
        PrepareTexto();
        return super.CommitXML(me, mapa);
    }

    @Override
    public FormaElementar getSub(int i) {
        if (i == 0) {
            return getTexto();
        }
        return super.getSub(i);
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        super.CompleteGenerateProperty(GP);
        if (getPontaA().getEm() == null && getPontaB().getEm() == null) {
            GP.add(InspectorProperty.PropertyFactorySeparador("ligacoes"));
        }

        if (getTexto() != null) {
            Forma f = getTexto();
            InspectorProperty ipp = InspectorProperty.PropertyFactoryActionSelect(Editor.fromConfiguracao.getValor("diagrama." + Editor.getClassTexto(f) + ".nome"),
                    f.getTexto(),
                    String.valueOf(f.getID()));
            GP.add(ipp);
        }

        return GP;
    }

}
