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
public class LivreRetanguloArr extends LivreBase {

    private static final long serialVersionUID = -2712462959374650168L;

    public LivreRetanguloArr(Diagrama modelo, String texto) {
        super(modelo, texto);
        setTipoDesenho(LivreBase.TipoDraw.tpRetanguloArred);
    }

    public LivreRetanguloArr(Diagrama modelo) {
        super(modelo);
        setTipoDesenho(LivreBase.TipoDraw.tpRetanguloArred);
    }

    
}
