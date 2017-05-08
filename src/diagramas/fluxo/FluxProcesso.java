/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.fluxo;

import controlador.Diagrama;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author ccandido
 */
public class FluxProcesso extends FluxFormaBaseComplementar {

    private static final long serialVersionUID = 4191424730638062945L;

    public FluxProcesso(Diagrama modelo) {
        super(modelo);
    }

    public FluxProcesso(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    @Override
    public Shape getRegiao() {
        if (Regiao == null) {
            Regiao = new RoundRectangle2D.Float(getLeft(), getTop(), getWidth(), getHeight(), distSelecao * 8, distSelecao * 8);
        }
        return Regiao;
    }
}
