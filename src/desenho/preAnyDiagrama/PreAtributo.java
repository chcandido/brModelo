/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preAnyDiagrama;

import controlador.Diagrama;
import desenho.formas.Forma;
import desenho.formas.FormaNaoRetangularBase;
import desenho.linhas.Linha;
import desenho.linhas.PontoDeLinha;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import util.Constantes;
import util.DesenhadorDeTexto;

/**
 *
 * @author ccandido
 */
public class PreAtributo extends FormaNaoRetangularBase {

    private static final long serialVersionUID = 7136642795302925356L;

    public PreAtributo(Diagrama modelo) {
        super(modelo);
        setTipo(TipoDePontos.tp2Pontos);
        setDirecao(Direcao.Left);
        AceitaAjusteAutmatico = false;
    }

    public PreAtributo(Diagrama modelo, String texto) {
        super(modelo, texto);
        setTipo(TipoDePontos.tp2Pontos);
        setDirecao(Direcao.Left);
        AceitaAjusteAutmatico = false;
    }

    public Direcao getDirecaoLigacao() {
        return getDirecao();
    }

    public void setDirecaoLigacao(Direcao DirecaoLigacao) {
        if (DirecaoLigacao != Direcao.Left && DirecaoLigacao != Direcao.Right) {
            DirecaoLigacao = Direcao.Left;
        }
        if (DirecaoLigacao != getDirecao()) {
            needRecalPts = true;
            calculePontos();
            ArrayList<PontoDeLinha> pts = getListaDePontosLigados();
            pts.stream().forEach((p) -> {
                if (p.getLado() == 0) {
                    p.setCentro(pontoPosi5);
                    p.setLado(2);
                } else {
                    p.setCentro(pontoPosi7);
                    p.setLado(0);
                }
            });
            setDirecao(DirecaoLigacao);
        }
    }

    public boolean isAtributoComposto() {
        ArrayList<PontoDeLinha> lst = getListaDePontosLigados();
        int tmp = lst.size();
        if (tmp > 2) {
            return true;
        }
        if (tmp == 0) {
            return false;
        }
        PontoDeLinha pt = PontoLigacaoPrincipal(lst);
        if ((pt == null && tmp > 0) || (pt != null && tmp > 1)) {
            return true;
        }
        return false;
    }

    public PontoDeLinha PontoLigacaoPrincipal(ArrayList<PontoDeLinha> pts) {
        if (pts == null) {
            pts = getListaDePontosLigados();
        }
        int lado = (getDirecaoLigacao() == Direcao.Left) ? 0 : 2;
        for (PontoDeLinha p : pts) {
            if (p.getLado() == lado) {
                return p;
            }
        }
        return null;
    }

    @Override
    protected void Posicione2Pontos(PontoDeLinha ponto) {
        calculePontos();
        if (getMaster().isCarregando) {
            super.Posicione2Pontos(ponto);
            return;
        }
        ArrayList<PontoDeLinha> pts = getListaDePontosLigados();
        PontoDeLinha pt = PontoLigacaoPrincipal(pts);
        boolean pontaPrin = false;

        Forma op = ponto.getDono().getOutraPonta(this);
        if (pt == null) {
            if (op == null) {
                super.Posicione2Pontos(ponto);
                return;//não me importa, não há ninguém na outra ponta!
            }
            if (op instanceof PreAtributo) { // a outra ponta é um atributo;
                PreAtributo att = (PreAtributo) op;
                PontoDeLinha qualPt = att.PontoLigacaoPrincipal(null); //qual ponto liga a ligação principal do atributo?

                if (qualPt != null && qualPt.getDono() == ponto.getDono()) { //a ligação principal do atributo é a deste ponto?
                    pontaPrin = false;
                } else { // este é filho!
                    pontaPrin = true;
                }
            } else {
                pontaPrin = true;
            }
        } else if (pt == ponto) { // está se mechendo na ligação principal
            if (op == null) {
                super.Posicione2Pontos(ponto);
                return;//não me importa, não há ninguém na outra ponta!
            }
            //obrigo-o a voltar a principal
            pontaPrin = true;
        } else {
            //vai para a outra ponta!
            pontaPrin = false;
        }

        if (!pontaPrin) {
            if (getDirecaoLigacao() == Direcao.Left) {
                ponto.setLado(2);
                ponto.setCentro(pontoPosi5);
            } else {
                ponto.setLado(0);
                ponto.setCentro(pontoPosi7);
            }
        } else if (getDirecaoLigacao() == Direcao.Left) {
            ponto.setLado(0);
            ponto.setCentro(pontoPosi7);
        } else {
            ponto.setLado(2);
            ponto.setCentro(pontoPosi5);
        }
    }

    @Override
    protected void ProcessaDblClick(MouseEvent e) {
        super.ProcessaDblClick(e);
        if (getDirecaoLigacao() == Direcao.Left) {
            setDirecaoLigacao(Direcao.Right);
        } else {
            setDirecaoLigacao(Direcao.Left);
        }
        if (isSelecionado()) {
            getMaster().PerformInspector();
        }
        DoMuda();
    }

    private boolean autosize = true;
    private boolean identificador = false;
    private boolean opcional = false;

    public boolean isOpcional() {
        return opcional;
    }

    public void setOpcional(boolean opcional) {
        if (this.opcional != opcional) {
            this.opcional = opcional;
            PontoDeLinha qp = PontoLigacaoPrincipal(null);
            if (opcional) {
                this.identificador = false;
                if (qp != null) {
                    qp.getDono().setDashed(true);
                }
            } else if (qp != null) {
                qp.getDono().setDashed(false);
            }
            InvalidateArea();
        }
    }

    public boolean isIdentificador() {
        return identificador;
    }

    public void setIdentificador(boolean identificador) {
        if (this.identificador != identificador) {
            this.identificador = identificador;
            if (identificador && opcional) {
                setOpcional(false);
            }
            InvalidateArea();
        }
    }

    public boolean isAutosize() {
        return autosize;
    }

    public void setAutosize(boolean autosize) {
        if (this.autosize == autosize) {
            return;
        }
        this.autosize = autosize;
        InvalidateArea();
    }

    @Override
    public void DoPaint(Graphics2D g) {
        if (isAutosize()) {
            int largura = g.getFontMetrics(getFont()).stringWidth(getTextoToDraw()) + getHeight() + 4 + 4;
            if (getWidth() != largura) {
                setStopRaize(true);
                setWidth(largura);
                setStopRaize(false);
                needRecalPts = true;
                calculePontos();
                SendNotificacao(Constantes.Operacao.opResize);
                setRegiao(null);
                if (isSelecionado()) {
                    Reposicione();
                }
                ReSizedByAutoSize();
            }
        }
        super.DoPaint(g);
        Shape reg;
        if (getDirecaoLigacao() == Direcao.Left) {
            reg = new Ellipse2D.Float(getLeft(), getTop(), getHeight() - 1, getHeight() - 1);
        } else {
            reg = new Ellipse2D.Float(getLeft() + getWidth() - getHeight(), getTop(), getHeight() - 1, getHeight() - 1);
        }
        Stroke bkps = g.getStroke();
        if (isOpcional()) {
            g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 2}, 0));
        }
        if (isIdentificador()) {
            g.fill(reg);
        } else {
            g.draw(reg);
        }
        if (isOpcional()) {
            g.setStroke(bkps);
        }
    }

    @Override
    protected void PinteRegiao(Graphics2D g) {
        //super.PinteRegiao(g);//não pinta nada
    }

    private transient double z = 0.0;

    @Override
    public void PinteTexto(Graphics2D g) {
        //no caso de mudança no zoom, um novo TextoFormatado deve ser criado.
        if (getMaster().getZoom() != z) {
            setTextoFormatado(null);
            z = getMaster().getZoom();
        }

        DesenhadorDeTexto txtf = getTextoFormatado();
        Rectangle rec = new Rectangle(
                getDirecaoLigacao() != Direcao.Left ? getLeft() : getLeft() + getHeight() + 2,
                getTop() - 2,
                getWidth() - getHeight() - 2,
                getHeight() + 2);

        //g.draw(rec);
        txtf.LimitarAreaDePintura = true;
        if (getDirecaoLigacao() != Direcao.Left) {
            txtf.setAlinharDireita(true);
        } else {
            txtf.setAlinharEsquerda(true);
        }
        txtf.PinteTexto(g, getForeColor(), rec, getTextoToDraw());

        if (isAtributoComposto()) {
            drawArrow(g, rec.x, rec.y, rec.width, rec.height);
        }
    }

    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        int ARR_SIZE = 3 + 0;
        int p = y1 + y2 / 2 + 1;

        if (getDirecaoLigacao() != Direcao.Left) {
            int q = x1;
            g.fillPolygon(new int[]{q, q + ARR_SIZE, q + ARR_SIZE, q},
                    new int[]{p, p - ARR_SIZE, p + ARR_SIZE, p}, 4);
        } else {
            int q = x1 + x2;

            g.fillPolygon(new int[]{q, q - ARR_SIZE, q - ARR_SIZE, q},
                    new int[]{p, p - ARR_SIZE, p + ARR_SIZE, p}, 4);
        }
    }

    @Override
    public void ReciveFormaResize(Rectangle ret) {
        if (!isAutosize()) {
            super.ReciveFormaResize(ret);
        } else {
            Rectangle rec = new Rectangle(ret.x, ret.y, 0, ret.height);
            super.ReciveFormaResize(rec);
        }
    }

    @Override
    protected void calculePontos() {
        if (!needRecalPts) {
            return;
        }
        super.calculePontos();
        //Rectangle r = new Rectangle(getLeft(), getTop(), getWidth(), getHeight()); //getBounds();
        //pontoPosi5 = new Point(r.x + r.width, r.y + r.height / 2); //lado 2
        //pontoPosi7 = new Point(r.x, r.y + r.height / 2); // lado 0
        if (getDirecaoLigacao() == Direcao.Left) {
            pontoPosi4 = pontoPosi6 = pontoPosi5;
        } else {
            pontoPosi4 = pontoPosi6 = pontoPosi7;
        }
//        if (getDirecaoLigacao() == Direcao.Left) {
//            pontoPosi4 = new Point(r.x + r.height / 2, r.y); //lado 1
//            pontoPosi5 = new Point(r.x + r.width, r.y + r.height / 2); //lado 2
//            pontoPosi6 = new Point(r.x + r.height / 2, r.y + r.height); // lado 3
//            pontoPosi7 = new Point(r.x, r.y + r.height / 2); // lado 0
//        } else {
//            pontoPosi4 = new Point(r.x + r.width - r.height / 2, r.y); //lado 1
//            pontoPosi5 = new Point(r.x + r.width, r.y + r.height / 2); //lado 2
//            pontoPosi6 = new Point(r.x + r.width - r.height / 2, r.y + r.height); // lado 3
//            pontoPosi7 = new Point(r.x, r.y + r.height / 2); // lado 0
//        }
        //pontoPosi0 = new Point(pontoPosi7.x, pontoPosi4.y);
        //pontoPosi1 = new Point(pontoPosi5.x, pontoPosi4.y);
        //pontoPosi2 = new Point(pontoPosi5.x, pontoPosi6.y);
        //pontoPosi3 = new Point(pontoPosi7.x, pontoPosi6.y);
    }

    @Override
    protected void DoPaintDoks(Graphics2D g) {
        calculePontos();
        Point[] pts = new Point[]{pontoPosi7, pontoPosi5};
        Paint bkpP = g.getPaint();
        g.setPaint(Color.orange);
        for (int i = 0; i < pts.length; i++) {
            g.fillRect(pts[i].x - 2, pts[i].y - 2, 4, 4);
        }
        g.setPaint(bkpP);
    }

    @Override
    public int retorneProximidade(Point centro) {
//        int res = super.retorneProximidade(centro);
//        if (res == 1 || res == 3) {
//            if (getDirecaoLigacao() == Direcao.Left) {
//                res = 0;
//            } else {
//                res = 2;
//            }
//        }
//        return res;
        calculePontos();
        double dp0 = distance(centro, pontoPosi7);
        double dp2 = distance(centro, pontoPosi5);
        if (dp0 < dp2) {
            return 0;
        } else {
            return 2;
        }
    }

    @Override
    public boolean CanLiga(Forma forma, Linha lin) {
        if (forma == null) {
            return true;
        }
        if (super.CanLiga(forma, lin)) {
            PontoDeLinha qp = PontoLigacaoPrincipal(null);
            if (forma instanceof PreAtributo) {
                PreAtributo pre = (PreAtributo) forma;
                PontoDeLinha qplig = pre.PontoLigacaoPrincipal(null);
                if (qp == null || qplig == null) {
                    return true;
                }
                if (qplig.getDono() == lin) {
                    return true;
                }
            } else {
                //if (forma instanceof PreEntidade) {
                if (lin.getFormaPontaA() == this || lin.getFormaPontaB() == this) {
                    if (qp == null) {
                        return false;
                    }
                }
                //}
                return (qp == null || qp.getDono() == lin);
            }
        }
        return false;
    }

    protected void ReSizedByAutoSize() {

    }

    public String getTextoToDraw() {
        return super.getTexto();
    }
}
