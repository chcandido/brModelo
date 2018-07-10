/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.conceitual;

import controlador.Diagrama;
import controlador.Editor;
import controlador.apoios.TreeItem;
import controlador.inspector.InspectorProperty;
import desenho.FormaElementar;
import desenho.formas.Forma;
import desenho.preAnyDiagrama.PreEntidadeAssociativa;
import java.awt.Font;
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
public class EntidadeAssociativa extends PreEntidadeAssociativa {

    private static final long serialVersionUID = 7518332308924246293L;

    public EntidadeAssociativa(Diagrama modelo) {
        super(modelo);
        nodic = false;
    }

    public EntidadeAssociativa(Diagrama modelo, String texto) {
        super(modelo, texto);
        nodic = false;
    }

    public EntidadeAssociativa(Diagrama modelo, String texto, Relacionamento fr) {
        super(modelo, texto, fr);
        nodic = false;
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        
        ArrayList<InspectorProperty> res = GP;
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "entassociativa.convrelacionamento").setTag(90816));

        res.add(InspectorProperty.PropertyFactorySeparador("entidadeasossiativa.relacao"));
//        res.add(InspectorProperty.PropertyFactoryTexto("nome", "setRelTexto", getRelTexto()));
//        res.add(InspectorProperty.PropertyFactoryTextoL("observacao", "setRelObservacao", getRelObservacao()));
        String relaName = Editor.getClassTexto(this.getInterno());
        res.add(InspectorProperty.PropertyFactoryTexto("nome", relaName + ".setTexto", this.getInterno().getTexto()));
        res.add(InspectorProperty.PropertyFactoryTextoL("observacao", relaName + ".setObservacao", this.getInterno().getObservacao()));
        res.add(InspectorProperty.PropertyFactoryTextoL("dicionario", relaName + ".setTextoAdicional", this.getInterno().getTextoAdicional()));
        res.add(InspectorProperty.PropertyFactoryCor("forecolor", relaName + ".setForeColor", this.getInterno().getForeColor()));

        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "entidade.orgattr").setTag(CONST_DO_ORGATTR + 1));
        
        super.CompleteGenerateProperty(GP);
        int p = GP.size() -1;
        ArrayList<Forma> lst = getInterno().getListaDeFormasLigadas();
        boolean ja = InspectorProperty.FindByProperty(res, "ligacoes") != null;
        for (Forma f : lst) {
            InspectorProperty ipp = InspectorProperty.PropertyFactoryActionSelect(Editor.fromConfiguracao.getValor("diagrama." + Editor.getClassTexto(f) + ".nome"),
                    f.getTexto(),
                    String.valueOf(((FormaElementar) f.getPrincipal()).getID()));
            if (!ja) {
                ja = true;
                GP.add(InspectorProperty.PropertyFactorySeparador("ligacoes", true));
            }
            GP.add(p, ipp);
        }

        return GP;
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        getInterno().ToXlm(doc, me);

        //remover Bounds.
        NodeList nl = me.getElementsByTagName(getInterno().getClass().getSimpleName());
        if (nl != null && nl.getLength() > 0) {
            Element achado = (Element)nl.item(0);
            nl = achado.getElementsByTagName("Bounds");
            achado.removeChild(nl.item(0));
        }

    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        getInterno().setFont(font);
    }
    
    
    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        Element inter = (Element)(me.getElementsByTagName(getInterno().getClass().getSimpleName()).item(0));
        boolean ret = getInterno().LoadFromXML(inter, colando);
        ReenquadreInterno();
        return ret;
    }

    @Override
    public boolean CommitXML(Element me, HashMap<Element, FormaElementar> mapa) {
        boolean res = super.CommitXML(me, mapa);
        ReenquadreInterno();
        return res;
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == CONST_DO_ORGATTR + 1) {
            getInterno().DoAnyThing(CONST_DO_ORGATTR);
            if (mudouAtributos || getInterno().mudouAtributos) {
                DoMuda();
            }
        }
        if (Tag == 90816) {
            ConverteRel();
        }
    }
    
    private void ConverteRel() {
        if (!getListaDePontosLigados().isEmpty()) {
            util.Dialogos.ShowMessageInform(getMaster().getEditor().getParent(), Editor.fromConfiguracao.getValor("Controler.MSG_INFO_TEM_LIGACAO"));
            return;
        }
        Rectangle res = getInterno().getBounds();
        diagramas.conceitual.Relacionamento novo = new Relacionamento(getMaster());
        novo.SetTexto(getInterno().getTexto());
        novo.SetBounds(res);

        getInterno().getListaDePontosLigados().stream().forEach(p -> {
            p.SetEm(novo);
        });

        novo.setObservacao(getInterno().getObservacao());
        novo.setTextoAdicional(getInterno().getTextoAdicional());
        novo.setBackColor(getInterno().getBackColor());
        novo.setForeColorWithOutRepaint(getInterno().getForeColor());
        novo.setFont(getInterno().getFont());
        
        getMaster().Remove(this, true);
        getMaster().setSelecionado(novo);
        novo.DoMuda();
    }

    // versão 3.2
    @Override
    public boolean MostreSeParaExibicao(TreeItem root) {
        TreeItem t = new TreeItem(getTexto(), getID(), this.getClass().getSimpleName());
        t.add(new TreeItem(getInterno().getTexto(), getID(), getInterno().getClass().getSimpleName()));
        root.add(t);
        return true;
    }
}
