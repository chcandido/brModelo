/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package desenho.preAnyDiagrama;

import controlador.Diagrama;

/**
 *
 * @author ccandido
 */
public class PreUniao extends PreEspecializacao {

    private static final long serialVersionUID = -3414008491237272878L;

    public PreUniao(Diagrama modelo) {
        super(modelo);
        setDirecao(Direcao.Down);
        //SetTexto("U");
        toPaintTxt = "U";
    }

    public PreUniao(Diagrama modelo, String texto) {
        super(modelo, texto);
        setDirecao(Direcao.Down);
        toPaintTxt = "U";
    }
}
