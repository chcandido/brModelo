/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.fluxo;

import controlador.Diagrama;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 *
 * @author ccandido
 */
public class FluxDecisao extends FluxFormaBaseComplementar {

    private static final long serialVersionUID = 7228459112937871606L;

    public FluxDecisao(Diagrama modelo) {
        super(modelo);
        setMudarParaTextoLongo(false);
    }

    public FluxDecisao(Diagrama modelo, String texto) {
        super(modelo, texto);
        setMudarParaTextoLongo(false);
    }
    
    @Override
    public Shape getRegiao() {
        if (Regiao == null) {
            Rectangle r = new Rectangle(getLeft(), getTop(), getWidth(), getHeight()); //getBounds();
            Polygon los = new Polygon();
            los.addPoint(r.x, r.y + r.height / 2);
            los.addPoint(r.x + r.width / 2, r.y);
            los.addPoint(r.x + r.width, r.y + r.height / 2);
            los.addPoint(r.x + r.width / 2, r.y + r.height);

            Regiao = los;
        }
        return Regiao;
    }
}
