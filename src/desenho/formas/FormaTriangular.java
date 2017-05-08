/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.formas;

import controlador.Diagrama;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;

/**
 *
 * @author ccandido
 */
public class FormaTriangular extends FormaNaoRetangularBase {

    private static final long serialVersionUID = -582124975126214085L;

    public FormaTriangular(Diagrama modelo) {
        super(modelo);
        setTipo(TipoDePontos.tp3Pontos);
        setDirecao(Direcao.Up);
    }

    public FormaTriangular(Diagrama modelo, String texto) {
        super(modelo, texto);
        setTipo(TipoDePontos.tp3Pontos);
        setDirecao(Direcao.Up);
    }

    public Direcao getDirecaoTriangulo() {
        return getDirecao();
    }

    public void setDirecaoTriangulo(Direcao direcaoTriangulo) {
        //evita outras formas de direção.
        if (direcaoTriangulo.ordinal() > 3) {
            direcaoTriangulo = Direcao.Up;
        }
        setDirecao(direcaoTriangulo);
        DestruaRegiao();
        InvalidateArea();
    }

    @Override
    public Shape getRegiao() {
        if (Regiao == null) {
            Point[] tri = getPontosDoTriangulo();
            Point p1 = tri[0], p2 = tri[1], p3 = tri[2];

            Polygon triang = new Polygon();
            triang.addPoint(p1.x, p1.y);
            triang.addPoint(p2.x, p2.y);
            triang.addPoint(p3.x, p3.y);
            Regiao = triang;
        }
        return Regiao;
    }

    private Point[] PontosDoTriangulo = null;

    protected Point[] getPontosDoTriangulo() {
        //poupa recursos. Zerado no método reescrito resized.
        if (PontosDoTriangulo != null) {
            return PontosDoTriangulo;
        }
        calculePontos();
        Point pt1, pt2, pt3, pMeio;
        Rectangle r = new Rectangle(getLeft(), getTop(), getWidth(), getHeight()); //getBounds();
        //r.grow(-espaco, -espaco);
        Point p0 = r.getLocation();
        Point p1 = new Point(r.x + r.width, r.y);
        Point p2 = new Point(r.x + r.width, r.y + r.height);
        Point p3 = new Point(r.x, r.y + r.height);

        switch (direcao) {
            case Up:
                pt1 = pontoPosi4;
                pt2 = p2;
                pt3 = p3;
                pMeio = pontoPosi6;
                break;
            case Right:
                pt1 = pontoPosi5;
                pt2 = p3;
                pt3 = p0;
                pMeio = pontoPosi7;
                break;
            case Down:
                pt1 = pontoPosi6;
                pt2 = p0;
                pt3 = p1;
                pMeio = pontoPosi4;
                break;
            default: //case  Left:
                pt1 = pontoPosi7;
                pt2 = p1;
                pt3 = p2;
                pMeio = pontoPosi5;
                break;
        }
        PontosDoTriangulo = new Point[]{pt1, pt2, pt3, pMeio};
        return PontosDoTriangulo;
    }

    @Override
    protected void DestruaRegiao() {
        PontosDoTriangulo = null;
        super.DestruaRegiao();
    }

    @Override
    protected void ProcessaDblClick(MouseEvent e) {
        super.ProcessaDblClick(e);
        Girar();
    }

    public void Girar() {
        setDirecaoTriangulo(Direcao.values()[getDirecaoTriangulo().ordinal() + 1]);
        reSetBounds();
        if (isSelecionado()) getMaster().PerformInspector();
    }

    @Override
    public int retorneProximidade(Point centro) {
        return retorneProximidade(centro, getPontosDoTriangulo());
    }

    @Override
    public Point getMelhorPontoDeLigacao(Point estePonto) {
        int tmp = retorneProximidade(estePonto);
        Point[] ptsLi = getPontosDoTriangulo();
        Point res = null;
        int a = 0;
        int b = 0;
        switch (direcao) {
            case Up:
                a = 2;
                break;
            case Right:
                b = 2;
            case Down:
                a = -2;
                break;
            default: //case  Left:
                b = -2;
                break;
        }
        switch (tmp) {
            case 0:
                res = new Point(ptsLi[0].x - b, ptsLi[0].y + a);
                break;
            case 1:
                res = new Point(ptsLi[1].x - a -b, ptsLi[1].y - b - a);
                break;
            case 2:
                res = new Point(ptsLi[2].x + a - b, ptsLi[2].y + b - a);
                break;
            case 3:
                res = new Point(ptsLi[3].x + b, ptsLi[3].y - a);
                break;
        }
//        switch (direcao) {
//            case Up:
//                switch (tmp) {
//                    case 0:
//                        res = new Point(ptsLi[0].x, ptsLi[0].y  + 2);
//                        break;
//                    case 1:
//                        res = new Point(ptsLi[1].x - 2, ptsLi[1].y);
//                        break;
//                    case 2:
//                        res = new Point(ptsLi[2].x + 2, ptsLi[2].y);
//                        break;
//                    case 3:
//                        res = new Point(ptsLi[3].x, ptsLi[3].y - 2);
//                        break;
//                }
//                break;
//            case Right:
//                switch (tmp) {
//                    case 0:
//                        res = new Point(ptsLi[0].x -2, ptsLi[0].y);
//                        break;
//                    case 1:
//                        res = new Point(ptsLi[1].x, ptsLi[1].y - 2);
//                        break;
//                    case 2:
//                        res = new Point(ptsLi[2].x, ptsLi[2].y  + 2);
//                        break;
//                    case 3:
//                        res = new Point(ptsLi[3].x + 2, ptsLi[3].y);
//                        break;
//                }
//                break;
//            case Down:
//                switch (tmp) {
//                    case 0:
//                        res = new Point(ptsLi[0].x, ptsLi[0].y  - 2);
//                        break;
//                    case 1:
//                        res = new Point(ptsLi[1].x + 2, ptsLi[1].y);
//                        break;
//                    case 2:
//                        res = new Point(ptsLi[2].x - 2, ptsLi[2].y);
//                        break;
//                    case 3:
//                        res = new Point(ptsLi[3].x, ptsLi[3].y + 2);
//                        break;
//                }
//                break;
//            default: //case  Left:
//                switch (tmp) {
//                    case 0:
//                        res = new Point(ptsLi[0].x + 2, ptsLi[0].y);
//                        break;
//                    case 1:
//                        res = new Point(ptsLi[1].x, ptsLi[1].y + 2);
//                        break;
//                    case 2:
//                        res = new Point(ptsLi[2].x, ptsLi[2].y  - 2);
//                        break;
//                    case 3:
//                        res = new Point(ptsLi[3].x - 2, ptsLi[3].y);
//                        break;
//                }
//                break;
//        }

        return res;
    }

    @Override
    protected void DoPaintDoks(Graphics2D g) {
        Point[] pts = getPontosDoTriangulo();
        Paint bkpP = g.getPaint();
        g.setPaint(Color.orange);
        for (Point pt : pts) {
            g.fillRect(pt.x - 2, pt.y - 2, 4, 4);
        }
        g.setPaint(bkpP);
    }

    protected String toPaintTxt = "";
    
    private transient double z = 0.0;

    @Override
    public void PinteTexto(Graphics2D g) {
        //no caso de mudança no zoom, um novo TextoFormatado deve ser criado.
        if (getMaster().getZoom() != z) {
            setTextoFormatado(null);
            z = getMaster().getZoom();
        }
        getTextoFormatado().PinteTexto(g, getForeColor(), getArea(), toPaintTxt);
    }
}
