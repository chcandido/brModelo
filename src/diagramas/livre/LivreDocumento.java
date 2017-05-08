/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.livre;

import controlador.Diagrama;

/**
 *
 * @author ccandido
 */
public class LivreDocumento extends LivreBase {

    private static final long serialVersionUID = 3465943528995307046L;

    public LivreDocumento(Diagrama modelo, String texto) {
        super(modelo, texto);
        setTipoDesenho(LivreBase.TipoDraw.tpDocSimples);
    }

    public LivreDocumento(Diagrama modelo) {
        super(modelo);
        setTipoDesenho(LivreBase.TipoDraw.tpDocSimples);
    }

}
