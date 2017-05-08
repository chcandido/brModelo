/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.fluxo;

import controlador.Diagrama;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author ccandido
 */
public class FluxConector extends FluxFormaBaseComplementar {

    private static final long serialVersionUID = -6407281252916876141L;

    public FluxConector(Diagrama modelo) {
        super(modelo);
        setMudarParaTextoLongo(false);
        editFonte = false;
    }

    public FluxConector(Diagrama modelo, String texto) {
        super(modelo, texto);
        setMudarParaTextoLongo(false);
        editFonte = false;
    }
    
    @Override
    public Shape getRegiao() {
        if (Regiao == null) {
            Regiao = new Ellipse2D.Float(getLeft(), getTop(), getWidth(), getHeight());
        }
        return Regiao;
    }

    @Override
    public void PinteTexto(Graphics2D g) {
    }

}
