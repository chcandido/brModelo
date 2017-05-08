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
public class FluxNota extends FluxFormaBaseComplementar {

    private static final long serialVersionUID = -7379680056945455946L;

    public FluxNota(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    public FluxNota(Diagrama modelo) {
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
            c.setCurve(LW, TH, LW - h1, TH - v1, L + h1, TH + v1, L, TH);
            CubicCurve2D c2 = new CubicCurve2D.Double();
            int v2 = v1 / 3;
            c2.setCurve(L, T + v2, L + h1, T + v1 + v2, LW - h1, T - v1 + v2, LW, T + v2);

            GeneralPath pa = new GeneralPath();
            pa.setWindingRule(GeneralPath.WIND_EVEN_ODD);
            pa.append(c2, true);
            pa.lineTo(LW, TH);
            pa.append(c, true);
            pa.lineTo(L,  T + v2);
            pa.closePath();
            
            Regiao = pa;
            int ptToMove = 3;
            this.reposicionePonto[ptToMove] = new Point(0, -repo);
            ptsToMove[ptToMove] = 1;
            ptToMove = 1;
            this.reposicionePonto[ptToMove] = new Point(0, repo);
            ptsToMove[ptToMove] = 1;
        }
        return Regiao;
    }
}
