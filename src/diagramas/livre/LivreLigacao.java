/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.livre;

import controlador.Diagrama;
import desenho.preAnyDiagrama.PreLigacaoSetaComApenso;
import desenho.preAnyDiagrama.PreTextoApenso;
import java.awt.Dimension;
import java.awt.Point;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author ccandido
 */
public class LivreLigacao extends PreLigacaoSetaComApenso {

    private static final long serialVersionUID = 3699704445132677421L;

    public LivreLigacao(Diagrama diagrama) {
        super(diagrama);
    }

    @Override
    public void PrepareTexto() {
        if (getTexto() == null) {
            LivreTextoApenso tmp = new LivreTextoApenso(this.getMaster(), "LivreTextoApenso");
            tmp.setGradiente(true);
            tmp.setTexto("...");
            tmp.setGradiente(false);
            setTexto(tmp);
            tmp.setSize(new Dimension(50, 30));
        }
        PreTextoApenso texto = getTexto();
        if (texto.isMovimentacaoManual()) {
            return;
        }
        int x = (getWidth() - texto.getWidth()) / 2;
        int y = (getHeight() - texto.getHeight()) / 2 - (texto.getHeight() / 2 + 2);
        x += getLeft();
        y += getTop();

        Point p = getTexto().getLocation();
        if ((p.x != x) || (p.y != y)) {
            getTexto().setLocation(x, y);
        }
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        boolean res = super.LoadFromXML(me, colando);
        if (!res) {
            return res;
        }
        NodeList ptLst = me.getElementsByTagName(LivreTextoApenso.class.getSimpleName());
        if (ptLst.getLength() > 0) {
            Element ecard = (Element) ptLst.item(0);
            PrepareTexto();
            PreTextoApenso txt = getTexto();
            if (txt != null) {
                txt.LoadFromXML(ecard, colando);
            }
        }
        return true;
    }

    @Override
    public void setOverMe(boolean b) {
        super.setOverMe(b);
        if (b) {
            fator_largura = 2;
        } else {
            fator_largura = 1;
        }
        InvalidateArea();
    }

//    @Override
//    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
//        ArrayList<InspectorProperty> res = super.CompleteGenerateProperty(GP);
//        int i = res.indexOf(InspectorProperty.FindByProperty(res, "ligacoes"));
//        if (i > -1) {
//            if (getTexto() != null) {
//                res.add(i, InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "livreligacao.mostrarlegenda.s").setTag(90316));
//            } else {
//                res.add(i, InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "livreligacao.mostrarlegenda.n").setTag(90316));
//            }
//            res.add(i, InspectorProperty.PropertyFactorySeparador("livreligacao.mostrarlegenda.titulo"));
//        }
//        return GP;
//    }
//
//    @Override
//    public void DoAnyThing(int Tag) {
//        super.DoAnyThing(Tag);
//        if (Tag == 90316) {
//            if (getTexto() == null) {
//                PrepareTexto();
//            } else {
//                getTexto().setCanBeDeleted(true);
//                getMaster().Remove(getTexto(), true);
//            }
//        }
//    }

}
