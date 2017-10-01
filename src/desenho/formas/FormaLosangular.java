/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.formas;

import controlador.Diagrama;
import desenho.linhas.PontoDeLinha;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 *
 * @author ccandido
 */
public class FormaLosangular extends FormaNaoRetangularBase {

    private static final long serialVersionUID = 8855549220259709835L;

    public FormaLosangular(Diagrama modelo) {
        super(modelo);
        setTipo(TipoDePontos.tp4Pontos);

    }

    public FormaLosangular(Diagrama modelo, String texto) {
        super(modelo, texto);
        setTipo(TipoDePontos.tp4Pontos);
    }

    @Override
    public Shape getRegiao() {
        if (Regiao == null) {
            Rectangle r = new Rectangle(getLeft(), getTop(), getWidth(), getHeight()); //getBounds();
            Polygon los = new Polygon();
            los.addPoint(r.x, r.y + r.height / 2);
            los.addPoint(r.x + r.width / 2, r.y);
            los.addPoint(r.x + r.width, r.y + r.height / 2);
            los.addPoint(r.x + r.width / 2, r.y + r.height);

            Regiao = los;
        }
        return Regiao;
    }

    // <editor-fold defaultstate="collapsed" desc="SubPontos">
    public Point[] getAllSubPoints() {
        calculePontos();
        return SubPointos;
    }
    protected Point[] SubPointos = null;

    @Override
    protected void calculePontos() {
        if (!needRecalPts) {
            return;
        }
        super.calculePontos();
        int x = pontoPosi6.x - pontoPosi3.x;
        int y = pontoPosi3.y - pontoPosi7.y;
        int tam = x / 3;
        int nvX1 = x - tam;
        int nvX2 = nvX1 - tam;
        tam = y / 3;

        int nvY2 = y - tam;
        int nvY1 = nvY2 - tam;

        SubPointos = new Point[12];
        SubPointos[0] = pontoPosi7;
        SubPointos[1] = pontoPosi4;
        SubPointos[2] = pontoPosi5;
        SubPointos[3] = pontoPosi6;
        SubPointos[4] = new Point(nvX2 + pontoPosi0.x, nvY2 + pontoPosi0.y); //0
        SubPointos[5] = new Point(nvX1 + pontoPosi0.x, nvY1 + pontoPosi0.y); //1
//     *          0----4-----1
//     *          |  0     1 |
//     *          7          5
//     *          |  3     2 |
//     *          3----6-----2
        SubPointos[6] = new Point(nvX1 + pontoPosi4.x, nvY2 + pontoPosi1.y); //2
        SubPointos[7] = new Point(nvX2 + pontoPosi6.x, nvY2 + pontoPosi5.y); //3
        SubPointos[11] = new Point(nvX1 + pontoPosi7.x, nvY2 + pontoPosi7.y); //3
        SubPointos[9] = new Point(nvX2 + pontoPosi4.x, nvY1 + pontoPosi1.y); //1
        SubPointos[10] = new Point(nvX1 + pontoPosi6.x, nvY1 + pontoPosi5.y); //2
        SubPointos[8] = new Point(nvX2 + pontoPosi7.x, nvY1 + pontoPosi7.y); //0
        //needRecalPts = false; // o super faz isso!
    }
    // </editor-fold>

    @Override
    protected void Posicione4Pontos(PontoDeLinha ponto) {
        Point centro = ponto.getCentro();

        Point[] ll = getAllSubPoints(); //pegar todos
        int mx = retorneProximidade(centro, ll);

        ponto.setCentro(ll[mx]);
        if (mx > 3) mx-= 4;
        if (mx > 3) mx-= 4;
        ponto.setLado(mx);
    }

    @Override
    protected void DoPaintDoks(Graphics2D g) {
        Point[] pts = getAllSubPoints();
        Paint bkpP = g.getPaint();
        g.setPaint(Color.orange);
        for (Point pt : pts) {
            g.fillRect(pt.x - 2, pt.y - 2, 4, 4);
        }
        g.setPaint(bkpP);
    }
}
