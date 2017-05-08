/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package diagramas.fluxo;

import controlador.Diagrama;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author ccandido
 */
public class FluxIniFim extends FluxFormaBaseComplementar {

    private static final long serialVersionUID = -2307344882941774598L;

    public FluxIniFim(Diagrama modelo) {
        super(modelo);
        setMudarParaTextoLongo(false);
        showOrgDiag = true;
    }

    public FluxIniFim(Diagrama modelo, String texto) {
        super(modelo, texto);
        setMudarParaTextoLongo(false);
        showOrgDiag = true;
    }

    @Override
    public Shape getRegiao() {
        if (Regiao == null) {
            Regiao = new RoundRectangle2D.Float(getLeft(), getTop(), getWidth(), getHeight(), getWidth() / 3, getHeight());
        }
        return Regiao;
    }

    @Override
    public void OrganizeDiagrama() {
        OrganizeFluxo();
    }
    
}
