/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.livre;

import controlador.Diagrama;

/**
 *
 * @author SAA
 */
public class LivreNota extends LivreBase {

    private static final long serialVersionUID = -8978661788500185689L;

    public LivreNota(Diagrama modelo, String texto) {
        super(modelo, texto);
        setTipoDesenho(LivreBase.TipoDraw.tpNota);
    }

    public LivreNota(Diagrama modelo) {
        super(modelo);
        setTipoDesenho(LivreBase.TipoDraw.tpNota);
    }

}
