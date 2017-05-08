/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.atividade;

import controlador.Diagrama;
import desenho.formas.FormaCircular;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author ccandido
 */
public class EstadoAtividade extends FormaCircular{

    private static final long serialVersionUID = -3283778296478726591L;

    public EstadoAtividade(Diagrama modelo) {
        super(modelo);
    }

    public EstadoAtividade(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    @Override
    public Shape getRegiao() {
        if (Regiao == null) {
            Regiao = new RoundRectangle2D.Float(getLeft(), getTop(), getWidth(), getHeight(), getWidth()/3, getHeight());
        }
        return Regiao;
    }

}
