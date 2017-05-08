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
import java.awt.Rectangle;
import java.awt.Shape;
import util.Constantes;

/**
 *
 * @author ccandido
 */
public class FormaNaoRetangularBase extends Forma {

    private static final long serialVersionUID = 8190306678762122439L;

    public FormaNaoRetangularBase(Diagrama modelo) {
        super(modelo);
    }

    public FormaNaoRetangularBase(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    @Override
    public boolean IntersectPath(Rectangle recsel) {
        return getRegiao().intersects(recsel);
    }

    @Override
    public void DoPaint(Graphics2D g) {
        PinteRegiao(g);
        super.DoPaint(g);
    }

    protected void PinteRegiao(Graphics2D g) {
        g.setPaint(this.getForeColor());
        g.draw(getRegiao());
    }

    @Override
    public boolean IsMe(Point p) {
        if (super.IsMe(p)) {
            return getRegiao().contains(p);
        }
        return false;
    }
    protected Shape Regiao = null;

    /**
     * Região a ser escrita/pintada na Base - Poligono quando a área é irregular
     * @return 
     */
    public Shape getRegiao() {
        if (Regiao == null) {
            Regiao = getArea();
        }
        return Regiao;
    }

    public void setRegiao(Shape regiao) {
        Regiao = regiao;
    }

    @Override
    public void ReSized() {
        DestruaRegiao();
        super.ReSized();
    }

    protected void DestruaRegiao() {
        setRegiao(null);
    }

    public enum TipoDePontos {

        tp4Pontos,
        tp3Pontos,
        tp2Pontos
    }

    public enum Direcao {

        Up,
        Right,
        Down,
        Left,
        Horizontal,
        Vertical
    }
    
    protected TipoDePontos Tipo = TipoDePontos.tp4Pontos;

    protected TipoDePontos getTipo() {
        return Tipo;
    }

    protected void setTipo(TipoDePontos tipo) {
        if (this.Tipo != tipo) {
            this.Tipo = tipo;
            setRegiao(null);
            SendNotificacao(Constantes.Operacao.opReposicione);
        }
    }
    protected Direcao direcao = Direcao.Up;

    protected Direcao getDirecao() {
        return direcao;
    }

    protected void setDirecao(Direcao direcao) {
        if (this.direcao != direcao) {
            this.direcao = direcao;
            setRegiao(null);
            SendNotificacao(Constantes.Operacao.opReposicione);
        }
    }

    protected void setDirecaoNaoNotifique(Direcao direcao) {
        if (this.direcao != direcao) {
            this.direcao = direcao;
            setRegiao(null);
        }
    }

    /**
     * Posiciona os pontos das ligações.
     * <br/>--Poderá ser melhorado!
     * @param ponto
     */
    @Override
    public void PosicionePonto(PontoDeLinha ponto) {
        //super.PosicionePonto(ponto);
        switch (Tipo) {
            case tp2Pontos:
                Posicione2Pontos(ponto);
                break;
            case tp3Pontos:
                Posicione3Pontos(ponto);
                break;
            case tp4Pontos:
                Posicione4Pontos(ponto);
                break;
        }
    }

    protected void Posicione2Pontos(PontoDeLinha ponto) {
        calculePontos();
        Point centro = ponto.getCentro();

        switch (direcao) {
            case Up:
            case Down:
            case Vertical:
                double dp3 = distance(centro, pontoPosi6);
                double dp1 = distance(centro, pontoPosi4);
                if (dp1 < dp3) {
                    ponto.setCentro(pontoPosi4);
                    ponto.setLado(1);
                } else {
                    ponto.setCentro(pontoPosi6);
                    ponto.setLado(3);
                }
                break;
            case Left:
            case Right:
            case Horizontal:
                double dp0 = distance(centro, pontoPosi7);
                double dp2 = distance(centro, pontoPosi5);
                if (dp0 < dp2) {
                    ponto.setCentro(pontoPosi7);
                    ponto.setLado(0);
                } else {
                    ponto.setCentro(pontoPosi5);
                    ponto.setLado(2);
                }
                break;
        }
    }

    protected void Posicione4Pontos(PontoDeLinha ponto) {
        //calculePontos();//getPontosCola...
        Point centro = ponto.getCentro();

        //Point[] ll = getPontosCalculados(); // getPontosColaterais();
        Point[] ll = getPontosColaterais();

        int mx = retorneProximidade(centro, ll);

//        int f = mx / 2;
//        if (mx == 1 || mx == 7) {
//            f = 0;
//        } else if (mx == 3 || mx == 5) {
//            f = 2;
//        }
        ponto.setCentro(ll[mx]);
        ponto.setLado(mx);
    }

    /**
     * Melhorado na classe triâgulo.
     * @param ponto
     */
    protected void Posicione3Pontos(PontoDeLinha ponto) {
        calculePontos();
        Point pt1, pt2, pt3, pMeio;

        Rectangle r = new Rectangle(getLeft(), getTop(), getWidth(), getHeight()); //getBounds();
        Point p0 = r.getLocation();
        Point p1 = new Point(r.x + r.width, r.y);
        Point p2 = new Point(r.x + r.width, r.y + r.height);
        Point p3 = new Point(r.x, r.y + r.height);
        Point res;
        Point centro = ponto.getCentro();
        int idx;
        Point[] pts;

        switch (direcao) {
            case Up:
                pt1 = pontoPosi4;
                pt2 = p2;
                pt3 = p3;
                pMeio = pontoPosi6;

                pts = new Point[]{pt1, pt2, pt3, pMeio};
                idx = retorneProximidade(centro, pts);
                res = pts[idx];

                if (res.equals(pt1)) {
                    ponto.setCentro(pt1);
                    ponto.setLado(1);
                } else if (res.equals(pMeio)) {
                    ponto.setCentro(pontoPosi6);
                    ponto.setLado(3);
                } else if (res.equals(pt2)) {
                    ponto.setCentro(pt2);
                    ponto.setLado(2);
                } else if (res.equals(pt3)) {
                    ponto.setCentro(pt3);
                    ponto.setLado(0);
                }
                break;
            case Right:
                pt1 = pontoPosi5;
                pt2 = p3;
                pt3 = p0;
                pMeio = pontoPosi7;
                pts = new Point[]{pt1, pt2, pt3, pMeio};
                idx = retorneProximidade(centro, pts);
                res = pts[idx];
                if (res.equals(pt1)) {
                    ponto.setCentro(pt1);
                    ponto.setLado(2);
                } else if (res.equals(pMeio)) {
                    ponto.setCentro(pontoPosi7);
                    ponto.setLado(0);
                } else if (res.equals(pt2)) {
                    ponto.setCentro(pt2);
                    ponto.setLado(3);
                } else if (res.equals(pt3)) {
                    ponto.setCentro(pt3);
                    ponto.setLado(1);
                }
                break;
            case Down:
                pt1 = pontoPosi6;
                pt2 = p0;
                pt3 = p1;
                pMeio = pontoPosi4;
                pts = new Point[]{pt1, pt2, pt3, pMeio};
                idx = retorneProximidade(centro, pts);
                res = pts[idx];
                if (res.equals(pt1)) {
                    ponto.setCentro(pt1);
                    ponto.setLado(3);
                } else if (res.equals(pMeio)) {
                    ponto.setCentro(pontoPosi4);
                    ponto.setLado(1);
                } else if (res.equals(pt2)) {
                    ponto.setCentro(pt2);
                    ponto.setLado(0);
                } else if (res.equals(pt3)) {
                    ponto.setCentro(pt3);
                    ponto.setLado(2);
                }

                break;
            case Left:
                pt1 = pontoPosi7;
                pt2 = p1;
                pt3 = p2;
                pMeio = pontoPosi5;
                pts = new Point[]{pt1, pt2, pt3, pMeio};
                idx = retorneProximidade(centro, pts);
                res = pts[idx];
                if (res.equals(pt1)) {
                    ponto.setCentro(pt1);
                    ponto.setLado(0);
                } else if (res.equals(pMeio)) {
                    ponto.setCentro(pontoPosi5);
                    ponto.setLado(2);
                } else if (res.equals(pt2)) {
                    ponto.setCentro(pt2);
                    ponto.setLado(1);
                } else if (res.equals(pt3)) {
                    ponto.setCentro(pt3);
                    ponto.setLado(3);
                }
                break;
        }
    }

    @Override
    public int retorneProximidade(Point centro) {
        return retorneProximidade(centro, getPontosColaterais());
    }

    protected int retorneProximidade(Point centro, Point[] osPts) {
        double[] dp = new double[osPts.length];
        for (int i = 0; i < osPts.length; i++) {
            dp[i] = distance(centro, osPts[i]);
        }
        int mx = 0;
        for (int i = 1; i < osPts.length; i++) {
            if (dp[mx] > dp[i]) {
                mx = i;
            }
        }
        return mx;
    }

    public final double distance(Point p, Point q) {
        return util.Utilidades.distance(p, q);
//        double dx = p.x - q.x;         //horizontal difference
//        double dy = p.y - q.y;         //vertical difference
//        double dist = Math.sqrt(dx * dx + dy * dy); //distance using Pythagoras theorem
//        return dist;
    }

    @Override
    protected void PropagueResizeParaLigacoes() {
        SendNotificacao(Constantes.Operacao.opReposicione);
    }

    @Override
    protected void DoPaintDoks(Graphics2D g) {
        Point[] pts = getPontosColaterais();
        Paint bkpP = g.getPaint();
        g.setPaint(Color.yellow);
        for (int i = 0; i < pts.length; i ++) {
            g.fillRect(pts[i].x -2, pts[i].y -2, 4, 4);
        }
        g.setPaint(bkpP);
    }
    
//    @Override
//    protected void ToXmlValores(Document doc, Element me) {
//        super.ToXmlValores(doc, me);
//        //me.appendChild(util.XMLGenerate.ValorInteger(doc, "Tipo", getTipo().ordinal()));
//        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Direcao", getDirecao().ordinal()));
//    }
}
