/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.atividade;

import controlador.Diagrama;
import desenho.formas.FormaRetangular;
import java.awt.Graphics2D;
import java.awt.Paint;

/**
 *
 * @author ccandido
 */
public class ForkJoinAtividade extends FormaRetangular {

    private static final long serialVersionUID = 8001639822305503977L;

    public ForkJoinAtividade(Diagrama modelo) {
        super(modelo);
        editFonte = false;

    }

    public ForkJoinAtividade(Diagrama modelo, String texto) {
        super(modelo, texto);
        editFonte = false;
    }

    @Override
    public void PinteTexto(Graphics2D g) {
    }

    @Override
    public void DoPaint(Graphics2D g) {
        super.DoPaint(g); //To change body of generated methods, choose Tools | Templates.
        Paint bkp = g.getPaint();
        g.setPaint(this.getForeColor());
        g.fillRect(getLeft(), getTop(), getWidth() - 1, getHeight() - 1);
        g.setPaint(bkp);
    }
}
