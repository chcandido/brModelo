/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.eap;

import controlador.Diagrama;
import desenho.preAnyDiagrama.PreLigacaoSeta;
import java.awt.Rectangle;

/**
 *
 * @author ccandido
 */
public class EapLigacao extends PreLigacaoSeta{

    private static final long serialVersionUID = 6205513013521266652L;

    public EapLigacao(Diagrama diagrama) {
        super(diagrama);
    }

    @Override
    public void Inicie(Rectangle r) {
        showConfigSeta = false;
        super.Inicie(r);
        setTemSetaPontaB(false);
    }
}
