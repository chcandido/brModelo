/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.fluxo;

import controlador.Diagrama;
import desenho.preAnyDiagrama.PreLigacaoSetaComApenso;
import desenho.preAnyDiagrama.PreTextoApenso;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author ccandido
 */
public class FluxSeta extends PreLigacaoSetaComApenso {

    private static final long serialVersionUID = -2743241381072752114L;

    public FluxSeta(Diagrama diagrama) {
        super(diagrama);
    }
    
    @Override
    public void PrepareTexto() {
        PreTextoApenso texto = getTexto();
        if (texto == null) {
            setTexto(new FluxTexto(this.getMaster(), FluxTexto.class.getSimpleName()));
            texto = getTexto();
        }
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
    
    public boolean isPositivo(){
        return  ((FluxTexto)getTexto()).isPositivo();
    }

    public void setPositivo(boolean b) {
        ((FluxTexto)getTexto()).setPositivo(b);
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        boolean res = super.LoadFromXML(me, colando);
        if (!res) {
            return res;
        }
        NodeList ptLst = me.getElementsByTagName(FluxTexto.class.getSimpleName());
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
}
