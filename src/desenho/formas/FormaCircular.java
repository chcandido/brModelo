/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.formas;

import controlador.Diagrama;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author ccandido
 */
public class FormaCircular extends FormaNaoRetangularBase {

    private static final long serialVersionUID = -452954710335094585L;

    public FormaCircular(Diagrama modelo) {
        super(modelo);
        setTipo(TipoDePontos.tp4Pontos);
    }

    public FormaCircular(Diagrama modelo, String texto) {
        super(modelo, texto);
        setTipo(TipoDePontos.tp4Pontos);
    }

    @Override
    public Shape getRegiao() {
        if (Regiao == null) {
            Regiao = new Ellipse2D.Float(getLeft(), getTop(), getWidth(), getHeight());
        }
        return Regiao;
    }
}
