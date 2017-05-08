/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.atividade;

import controlador.Diagrama;
import desenho.FormaElementar;
import desenho.formas.FormaArea;
import desenho.linhas.PontoDeLinha;
import java.awt.Graphics2D;

/**
 *
 * @author ccandido
 */
public class RaiaAtividade extends FormaArea {

    private static final long serialVersionUID = 3142806418501291919L;

    public RaiaAtividade(Diagrama modelo) {
        super(modelo);
    }

    public RaiaAtividade(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    @Override
    public boolean CanLiga(PontoDeLinha aThis) {
        return false;
    }
    
    @Override
    protected void DoPaintDoks(Graphics2D g) {
    }

    @Override
    public boolean IsThatOverAndCanMove(FormaElementar el) {
        boolean res = (el instanceof TextoAtividade)? ((TextoAtividade)el).isMovimentacaoManual(): true;
        return super.IsThatOverAndCanMove(el) && res; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAlinhavel() {
        return false;
    }

}
