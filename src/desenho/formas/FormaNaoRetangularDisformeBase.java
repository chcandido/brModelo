/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.formas;

import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.linhas.PontoDeLinha;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;

/**
 *
 * @author ccandido
 */
public class FormaNaoRetangularDisformeBase extends FormaNaoRetangularBase {

    private static final long serialVersionUID = 4979116307999403371L;

    public FormaNaoRetangularDisformeBase(Diagrama modelo) {
        super(modelo);
        setTipo(TipoDePontos.tp4Pontos);

    }

    public FormaNaoRetangularDisformeBase(Diagrama modelo, String texto) {
        super(modelo, texto);
        setTipo(TipoDePontos.tp4Pontos);
    }

    @Override
    public Shape getRegiao() {
        return Regiao;
    }

    /**
     * Lados: 0 1 2 3 Default: 0,0
     */
    protected Point[] reposicionePonto = new Point[]{new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0)};

    /**
     * eposicionePontoEsquerda reposicionePontoCima reposicionePontoDireita
     * reposicionePontoAbaixo Lados: 0 1 2 3 Default: -1 -1 -1 -1 = nenhum ponto
     * a ser movido.
     */
    protected int[] ptsToMove = new int[]{-1, -1, -1, -1};

    protected boolean shouldMove(int ldo) {
        if (ldo > 3 || ldo < 0) {
            return false;
        }
        return ptsToMove[ldo] > -1;
    }

    protected Point getReposicionePonto(int ldo) {
        //if (!shouldMove(ldo)) return new Point(0, 0);
        return reposicionePonto[ldo];
    }

    @Override
    protected void Posicione4Pontos(PontoDeLinha ponto) {
        super.Posicione4Pontos(ponto);
        /*
         *          +----1-----+
         *          |          |
         *          0          2
         *          |          |
         *          +----3-----+    
         */

        if (shouldMove(ponto.getLado())) {
            Point p = ponto.getCentro();
            Point pM = getReposicionePonto(ponto.getLado());
            ponto.setCentro(p.x + pM.x, p.y + pM.y);
        }
    }

    @Override
    protected void DoPaintDoks(Graphics2D g) {
        Point[] pts = getPontosColaterais();
        Paint bkpP = g.getPaint();
        g.setPaint(Color.yellow);
        for (int i = 0; i < pts.length; i++) {
            if (shouldMove(i)) {
                Point p = getReposicionePonto(i);
                g.fillRect(pts[i].x - 2 + p.x, pts[i].y - 2 + p.y, 4, 4);
            } else {
                g.fillRect(pts[i].x - 2, pts[i].y - 2, 4, 4);
            }
        }
        g.setPaint(bkpP);
    }

    private boolean mudarParaTextoLongo = true;

    public final void setMudarParaTextoLongo(boolean mudarParaTextoLongo) {
        this.mudarParaTextoLongo = mudarParaTextoLongo;
    }

    public boolean isMudarParaTextoLongo() {
        return mudarParaTextoLongo;
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        if (isMudarParaTextoLongo()) {
            InspectorProperty tmp = InspectorProperty.FindByProperty(res, "setTexto");
            tmp.ReSetCaptionFromConfig("nometexto");
            tmp.tipo = InspectorProperty.TipoDeProperty.tpTextoLongo;
        }
        return res;
    }
}
