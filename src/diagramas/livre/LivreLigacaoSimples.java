/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.livre;

import controlador.Diagrama;
import desenho.preAnyDiagrama.PreLigacaoSeta;

/**
 *
 * @author ccandido
 */
public class LivreLigacaoSimples extends PreLigacaoSeta {

    private static final long serialVersionUID = -9220433713787957517L;

    public LivreLigacaoSimples(Diagrama diagrama) {
        super(diagrama);
    }


    @Override
    public void setOverMe(boolean b) {
        super.setOverMe(b);
        if (b) {
            fator_largura = 2;
        } else {
            fator_largura = 1;
        }
        InvalidateArea();
    }
}
