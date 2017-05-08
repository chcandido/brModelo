/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.atividade;

import controlador.Diagrama;
import desenho.formas.Forma;
import desenho.formas.FormaLosangular;
import desenho.linhas.Linha;
import java.awt.Graphics2D;

/**
 *
 * @author ccandido
 */
public class DecisaoAtividade extends FormaLosangular{

    private static final long serialVersionUID = 3967982872321626365L;

    public DecisaoAtividade(Diagrama modelo) {
        super(modelo);
        editFonte = false;
    }

    public DecisaoAtividade(Diagrama modelo, String texto) {
        super(modelo, texto);
        editFonte = false;
    }

    @Override
    public void PinteTexto(Graphics2D g) {
        //super.PinteTexto(g); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean CanLiga(Forma forma, Linha lin) {
        return true;
//        if (forma == null) {
//            return true;
//        }
//        if (super.CanLiga(forma, lin) && (!(forma instanceof PreEntidade))) {
//            return forma.CanLiga(this, lin);
//        }
//        return false;
    }

}
