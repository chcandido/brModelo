/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.atividade;

import controlador.Diagrama;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author ccandido
 */
public class InicioAtividade extends PreIniFimAtiv {

    private static final long serialVersionUID = 5679101297671037972L;

    public InicioAtividade(Diagrama modelo) {
        super(modelo);
    }

    public InicioAtividade(Diagrama modelo, String texto) {
        super(modelo, texto);
    }
    
    @Override
    protected void PinteRegiao(Graphics2D g) {
        g.setPaint(this.getForeColor());
        Rectangle r = getBounds();
        //r = util.Utilidades.Grow(r, -1, -1, 0);
        g.fillOval(r.x, r.y, r.width, r.height);
        //g.fill(getRegiao());
    }

}
