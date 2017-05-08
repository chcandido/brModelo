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
public class FluxVDocumentos extends FluxFormaBaseComplementar {

    private static final long serialVersionUID = -1672607296106370076L;

    public FluxVDocumentos(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    public FluxVDocumentos(Diagrama modelo) {
        super(modelo);
    }

    @Override
    public Shape getRegiao() {
        if (Regiao == null) {
            final int v1 = getHeight() / 3;
            final int h1 = getWidth() / 2;
            final int repo = v1 / 3;
            final int L = getLeft();
            int recuo = h1/8;
            final int T = getTop() + recuo;
            final int TH = T + getHeight() - repo -recuo;
            final int LW = L + getWidth() -recuo;
            CubicCurve2D c = new CubicCurve2D.Double();
            c.setCurve(L, TH, L + h1, TH + v1, LW - h1, TH - v1, LW, TH);
            GeneralPath pa = new GeneralPath();
            pa.setWindingRule(GeneralPath.WIND_EVEN_ODD);
            
            pa.moveTo(LW, TH);
            pa.lineTo(LW, T);
            pa.lineTo(L, T);
            pa.lineTo(L, TH);
            pa.append(c, false);
            
            int tam = recuo /2;
            
            pa.moveTo(L + tam, T);
            pa.lineTo(L + tam, T - tam);
            pa.lineTo(LW + tam, T - tam);
            pa.lineTo(LW + tam, TH - tam);
            pa.lineTo(LW, TH -tam);
            pa.lineTo(LW, T);
            pa.lineTo(L + tam, T);

            tam = recuo;
            
            pa.moveTo(L + tam, T - (tam/2));
            pa.lineTo(L + tam, T - tam);
            pa.lineTo(LW + tam, T - tam);
            pa.lineTo(LW + tam, TH - tam);
            pa.lineTo(LW + (tam/2), TH -tam);
            pa.lineTo(LW + (tam/2), T -(tam/2));
            pa.lineTo(L + tam, T - (tam/2));
            
            pa.closePath();
            
            Regiao = pa;
            this.reposicionePonto[ptToMove] = new Point(-tam/2, -repo);
            ptsToMove[ptToMove] = 1;
        }
        return Regiao;
    }
    private final int ptToMove = 3;
}
