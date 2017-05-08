/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.fluxo;

import controlador.Diagrama;
import desenho.formas.FormaNaoRetangularDisformeBase;

/**
 *
 * @author ccandido
 */
public class FluxFormaBaseComplementar extends FormaNaoRetangularDisformeBase {

    private static final long serialVersionUID = 6541937677078308606L;

    public FluxFormaBaseComplementar(Diagrama modelo) {
        super(modelo);
    }

    public FluxFormaBaseComplementar(Diagrama modelo, String texto) {
        super(modelo, texto);
    }
    
}
