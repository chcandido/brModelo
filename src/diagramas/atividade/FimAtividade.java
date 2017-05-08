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
public class FimAtividade extends PreIniFimAtiv{

    private static final long serialVersionUID = -209450608909519046L;

    public FimAtividade(Diagrama modelo) {
        super(modelo);
    }

    public FimAtividade(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    @Override
    protected void PinteRegiao(Graphics2D g) {
        super.PinteRegiao(g); //To change body of generated methods, choose Tools | Templates.
        Rectangle r = getBounds();
        r = util.Utilidades.Grow(r, -2*distSelecao, -2 *distSelecao, 0);
        g.fillOval(r.x, r.y, r.width + 1, r.height + 1);
    }
}
