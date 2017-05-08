/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.fluxo;

import controlador.Diagrama;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;

/**
 *
 * @author ccandido
 */
public class FluxDocumento extends FluxFormaBaseComplementar {

    private static final long serialVersionUID = -4209546284523864373L;

    public FluxDocumento(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    public FluxDocumento(Diagrama modelo) {
        super(modelo);
    }

    @Override
    public Shape getRegiao() {
        if (Regiao == null) {
            final int v1 = getHeight() / 3;
            final int h1 = getWidth() / 2;
            final int repo = v1 / 3;
            final int L = getLeft();
            final int T = getTop();
            final int TH = T + getHeight() - repo;
            final int LW = L + getWidth();
            CubicCurve2D c = new CubicCurve2D.Double();
            c.setCurve(L, TH, L + h1, TH + v1, LW - h1, TH - v1, LW, TH);
            GeneralPath pa = new GeneralPath();

            pa.moveTo(LW, TH);
            pa.lineTo(LW, T);
            pa.lineTo(L, T);
            pa.lineTo(L, TH);
            pa.append(c, true);
            Regiao = pa;
            setReposicionePontoAbaixo(new Point(0, -repo));        
            ptsToMove[ptToMove] = 1;
        }
        return Regiao;
    }
    private final int ptToMove = 3;

    public void setReposicionePontoAbaixo(Point reposicionePontoAbaixo) {
        this.reposicionePonto[ptToMove] = reposicionePontoAbaixo;
    }
}
