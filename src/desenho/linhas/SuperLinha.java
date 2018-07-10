/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.linhas;

import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Forma;
import desenho.preAnyDiagrama.PreTexto;
import diagramas.conceitual.Texto;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class SuperLinha extends Linha {

    private static final long serialVersionUID = -6648325468968978514L;

    public SuperLinha(Diagrama master) {
        super(master);
    }
    private boolean inteligente = false;

    /**
     * *
     * Configura se a linha deverá mostrar os atributos de seta (inclusive para salvar em xml).
     */
    protected boolean showConfigSeta = true;

    public boolean isInteligente() {
        return inteligente;
    }

    public void setInteligente(boolean inteligente) {
        if (this.inteligente != inteligente) {
            this.inteligente = inteligente;
        }
    }

    public void SetInteligente(boolean inteligente) {
        boolean md = (this.inteligente != inteligente);
        setInteligente(inteligente);
        if (md && inteligente) {
            Clean();
        } 
    }

    public void Inicie(int maisQuantosPontos, Point pFim, Point pIni) {
        if (getPontos().isEmpty()) {
            PontoDeLinha pt1 = NovoPonto();
            pt1.setCentro(pFim);
            PontoDeLinha ant = pt1;

            for (int i = 0; i < maisQuantosPontos; i++) {
                ant = InserirPonto(ant);
                ant.IsTopOrBotton = false;
            }
            pt1 = InserirPonto(ant);
            pt1.IsTopOrBotton = true;
            pt1.setCentro(pIni);
        }
        //Inicie(local);
    }

    @Override
    public void ReciveClick(Elementar sender, boolean dbl, MouseEvent e) {
        if (dbl) {   //&& !isInteligente() inteligente não trata os pontos??????.
            if (sender instanceof PontoDeLinha) {
                PontoDeLinha pt = (PontoDeLinha) sender;
                if (pt.IsTopOrBotton) {
                    return;
                }
//                int i = getPontos().indexOf(pt) - 1;
//                int j = i + 2;
//                getPontos().get(i).Proximo = getPontos().get(j);

                RemoveSubItem(pt);
                reSetBounds();
            }
        }
    }

    @Override
    protected void ProcessaDblClick(MouseEvent e) {
        Point p = e.getPoint();
        for (int i = 1; i < getPontosParaDesenho().length; i++) {
            if (Linha.SegmentIntersectRectangle(p.x - 1, p.y - 1, p.x + 1, p.y + 1,
                    getPontosParaDesenho()[i - 1].x, getPontosParaDesenho()[i - 1].y,
                    getPontosParaDesenho()[i].x, getPontosParaDesenho()[i].y)) {
                PontoDeLinha pt = InserirPonto(i);
                pt.setCentro(p);
                //pt.IsHide = false;
                pt.setVisible(true);
                mouseExited(null);//evita que a linha permaneça na forma do mouseEntred.
                return;
            }
        }
    }
    
    public static final int distancia = 10; //usada também no comando do modelo.
    
    /**
     * Super método que organiza todo o sistema de desenho de linhas Estrutura (Posições):
     * <pre>
     *
     *          +----------+----------+----------+
     *          |          |          |          |
     *          |    G     |    H     |    I     |
     *          |          |          |          |
     *          +----------+----------+----------+
     *          |          |          |          |
     *          |    F     |    A     |    B     |
     *          |          |          |          |
     *          +----------+----------+----------+
     *          |          |          |          |
     *          |    E     |    D     |    C     |
     *          |          |          |          |
     *          +----------+----------+----------+
     *
     * </pre> Nota: parto do princípio de que sempre um dos elementos de uma ligação esta na posição A Em seguida, procuro (em relação a A) qual a posição do
     * segundo elemento.
     */
    @Override
    public synchronized void OrganizeLinha() {
        //??: TO-DO: continuaar correções como o feito em AD 0-1 e 2-1.
        if (!isInteligente()) {
            return;
        }

        Forma A = getFormaPontaA();
        Forma B = getFormaPontaB();

        if (A == null || B == null || (A.isSelecionado() && B.isSelecionado())) {
            return;
        }

        A = (Forma) A.ProcessaComposicao();
        B = (Forma) B.ProcessaComposicao();

        

        int ladoA = getPontaA().getLado();
        int ladoB = getPontaB().getLado();

        Point pontaA = getPontaA().getCentro();
        Point pontaB = getPontaB().getCentro();

        char letra;
        boolean inver = false;

        int ATop = A.getTop(); //melhora velociade
        int BTop = B.getTop();
        int ALeft = A.getLeft();
        int BLeft = B.getLeft();

        if (ALeft < BLeft) {
            if (ATop < BTop) {
                letra = 'C';
                if (A.getLeftWidth() >= BLeft) {
                    letra = 'D';
                } else if (A.getTopHeight() >= BTop) {
                    letra = 'B';
                }
            } else if (ATop > BTop) {
                letra = 'I';
                if (A.getLeftWidth() >= BLeft) {
                    letra = 'D';
                    inver = true;
                } else if (pontaA.y <= B.getTopHeight()) {
                    letra = 'B';
                }

            } else { // ==
                letra = 'B';
            }
        } else if (ALeft > BLeft) {
            if (ATop < BTop) {
                //letra = 'E';//Troca e inverte
                letra = 'I';
                inver = true;
                if (B.getLeftWidth() >= ALeft) {
                    letra = 'D';
                    inver = false;
                } else if (pontaA.y >= BTop) {
                    letra = 'B';
                    //inver = false;
                }
            } else if (ATop > BTop) {
                //letra = 'G';//Troca e inverte
                letra = 'C';
                inver = true;
                if (B.getLeftWidth() >= ALeft) {
                    letra = 'D';
                } else if (B.getTopHeight() >= ATop) {
                    letra = 'B';
                }

            } else { // ==
                //letra = 'F';
                letra = 'B';
                inver = true;
            }
        } else { // ==
            if (ATop > BTop) {
                //letra = 'H';
                letra = 'D';
                inver = true;
            } else if (ATop < BTop) {
                letra = 'D';
            } else { // ==
                letra = 'B'; //sobreposto
            }
        }

        if (inver) {
            B = getFormaPontaA();
            A = getFormaPontaB();

            A = (Forma) A.ProcessaComposicao();
            B = (Forma) B.ProcessaComposicao();

            ladoB = getPontaA().getLado();
            ladoA = getPontaB().getLado();

            pontaB = getPontaA().getCentro();
            pontaA = getPontaB().getCentro();

            ATop = A.getTop(); //melhora velociade
            BTop = B.getTop();
            ALeft = A.getLeft();
            BLeft = B.getLeft();
        }

        // <editor-fold defaultstate="collapsed" desc="Pontos para entornos">
        //Pontos para entorno de A
        Rectangle r = A.getBounds();
        r.grow(distancia, distancia);

        Point A1 = new Point(r.x, r.y);
        Point A0 = new Point(r.x, pontaA.y);
        Point A7 = new Point(r.x, r.y + r.height);
        Point A2 = new Point(pontaA.x, r.y);
        Point A6 = new Point(pontaA.x, r.y + r.height);
        Point A3 = new Point(r.x + r.width, r.y);
        Point A4 = new Point(r.x + r.width, pontaA.y);
        Point A5 = new Point(r.x + r.width, r.y + r.height);

        //Pontos para entorno de B
        r = B.getBounds();
        r.grow(distancia, distancia);

        Point B1 = new Point(r.x, r.y);
        Point B0 = new Point(r.x, pontaB.y);
        Point B7 = new Point(r.x, r.y + r.height);
        Point B2 = new Point(pontaB.x, r.y);
        Point B6 = new Point(pontaB.x, r.y + r.height);
        Point B3 = new Point(r.x + r.width, r.y);
        Point B4 = new Point(r.x + r.width, pontaB.y);
        Point B5 = new Point(r.x + r.width, r.y + r.height);
        //</editor-fold>

        // Calculo do centro.
        int tmp1 = (ALeft > BLeft) ? (BLeft + ALeft + B.getWidth()) / 2 : (BLeft + ALeft + A.getWidth()) / 2;
        int tmp2 = (ATop > BTop) ? (BTop + ATop + B.getHeight()) / 2 : (BTop + ATop + A.getHeight()) / 2;

        Point pc = new Point(tmp1, tmp2);

        Point tmpPoint = null;

        Point p1 = null, p2 = null, p3 = null, p4 = null;

        //A.SetTexto("A - " + letra);
        //B.SetTexto("B - " + letra);
        switch (letra) {
            // <editor-fold defaultstate="collapsed" desc="Case B">
            case 'B':
                switch (ladoA) {
                    case 0:
                        switch (ladoB) {
                            case 0:
                                p1 = A0;
                                if (pontoMaisProx(A0.y, A1.y, A7.y)) {
                                    if ((A1.y + distancia) < pontaB.y) {
                                        p2 = A1;
                                        p3 = new Point(pc.x, A1.y);
                                        p4 = new Point(pc.x, B0.y);
                                    } else {
                                        p2 = new Point(A0.x, B0.y);
                                    }
                                } else {
                                    if ((A7.y - distancia) > pontaB.y) {
                                        p2 = A7;
                                        p3 = new Point(pc.x, A7.y);
                                        p4 = new Point(pc.x, B0.y);
                                    } else {
                                        p2 = new Point(A7.x, B0.y);
                                    }
                                }
                                break;
                            case 1:
                                p1 = A0;
                                tmpPoint = menorPonto(A1, B2, false);
                                p2 = new Point(A1.x, tmpPoint.y);
                                p3 = new Point(B2.x, tmpPoint.y);
                                break;
                            case 2:
                                p1 = A0;
                                if (pontoMaisProx(A0.y, A1.y, A7.y)) {
                                    tmpPoint = menorPonto(A1, B3, false);
                                    p2 = new Point(A1.x, tmpPoint.y);
                                    p3 = new Point(B3.x, tmpPoint.y);
                                } else {
                                    tmpPoint = maiorPonto(A7, B5, false);
                                    p2 = new Point(A7.x, tmpPoint.y);
                                    p3 = new Point(B5.x, tmpPoint.y);
                                }
                                p4 = B4;
                                break;
                            case 3:
                                p1 = A0;
                                tmpPoint = maiorPonto(A7, B6, false);
                                p2 = new Point(A7.x, tmpPoint.y);
                                p3 = new Point(B6.x, tmpPoint.y);
                                break;
                        }
                        break;
                    case 1:
                        switch (ladoB) {
                            case 0:
                                if (pontaA.y - distancia < pontaB.y) {
                                    p1 = A2;
                                    p2 = new Point(pc.x, A2.y);
                                    p4 = new Point(pc.x, B0.y);
                                } else {
                                    p1 = new Point(A2.x, B0.y);
                                }
                                break;
                            case 1:
                                tmpPoint = menorPonto(A2, B2, false);
                                p1 = new Point(A2.x, tmpPoint.y);
                                p2 = new Point(B2.x, tmpPoint.y);
                                break;
                            case 2:
                                tmpPoint = menorPonto(A2, B3, false);

                                p1 = new Point(A2.x, tmpPoint.y);
                                p2 = new Point(B3.x, tmpPoint.y);

                                p3 = B4;
                                break;
                            case 3:
                                p1 = A2;
                                p2 = new Point(pc.x, A2.y);
                                p3 = new Point(pc.x, B6.y);
                                p4 = B6;

                                break;
                        }
                        break;
                    case 2:
                        switch (ladoB) {
                            case 0:
                                p1 = new Point(pc.x, A4.y);
                                p2 = new Point(pc.x, B0.y);
                                break;
                            case 1:
                                if (pontaB.y - distancia < pontaA.y) {
                                    p1 = new Point(pc.x, A4.y);
                                    p2 = new Point(pc.x, B2.y);
                                    p3 = B2;
                                } else {
                                    p1 = new Point(B2.x, A4.y);
                                }
                                break;
                            case 2:
                                p1 = new Point(pc.x, A4.y);
                                if (pontoMaisProx(B4.y, B3.y, B5.y)) {
                                    p2 = new Point(pc.x, B3.y);
                                    p3 = B3;
                                } else {
                                    p2 = new Point(pc.x, B5.y);
                                    p3 = B5;
                                }
                                p4 = B4;
                                break;
                            case 3:
                                if (pontaA.y > pontaB.y + distancia) {
                                    p1 = new Point(B6.x, A4.y);
                                } else {

                                    p1 = new Point(pc.x, A4.y);
                                    p2 = new Point(pc.x, B6.y);
                                    p3 = B6;
                                }
                                break;
                        }
                        break;
                    case 3:
                        switch (ladoB) {
                            case 0:
                                if (A6.y > pontaB.y) {
                                    p1 = A6;
                                    p2 = new Point(pc.x, A6.y);
                                    p3 = new Point(pc.x, B0.y);
                                } else {
                                    p1 = new Point(A6.x, B0.y);
                                }
                                break;
                            case 1:
                                p1 = A6;
                                p2 = new Point(pc.x, A6.y);
                                p3 = new Point(pc.x, B2.y);
                                p4 = B2;
                                break;
                            case 2:
                                tmpPoint = maiorPonto(A6, B5, false);
                                p1 = new Point(A6.x, tmpPoint.y);
                                p2 = new Point(B5.x, tmpPoint.y);
                                p4 = B4;
                                break;
                            case 3:
                                tmpPoint = maiorPonto(A6, B6, false);
                                p1 = new Point(A6.x, tmpPoint.y);
                                p2 = new Point(B6.x, tmpPoint.y);
                                break;
                        }
                        break;
                }
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Case C">
            case 'C':
                switch (ladoA) {
                    case 0:
                        switch (ladoB) {
                            case 0:
                                p1 = A0;
                                p2 = new Point(A0.x, B0.y);
                                break;
                            case 1:
                                p1 = A0;
                                p2 = new Point(A0.x, pc.y);
                                p3 = new Point(B2.x, pc.y);
                                //p4 = B2;
                                break;
                            case 2:
                                p1 = A0;
                                if (pontoMaisProx(A0.y, A1.y, A7.y)) {
                                    p2 = A1;
                                    p3 = new Point(B4.x, A1.y);
                                } else {
                                    p2 = new Point(A1.x, pc.y);
                                    p3 = new Point(B4.x, pc.y);
                                }
                                p4 = B4;
                                break;
                            case 3:
                                p1 = A0;
                                p2 = new Point(A0.x, B6.y);
                                p3 = B6;
                                break;
                        }
                        break;
                    case 1:
                        switch (ladoB) {
                            case 0:
                                p1 = A2;
                                p2 = new Point(pc.x, A2.y);
                                p3 = new Point(pc.x, B0.y);
                                break;
                            case 1:
                                p1 = A2;
                                p2 = new Point(B2.x, A2.y);
                                break;
                            case 2:
                                p1 = A2;
                                p2 = new Point(B4.x, A2.y);
                                p3 = B4;
                                break;
                            case 3:
                                p1 = A2;
                                p2 = new Point(pc.x, A2.y);
                                p3 = new Point(pc.x, B6.y);
                                p4 = B6;
                                break;
                        }
                        break;
                    case 2:
                        switch (ladoB) {
                            case 0:
                                p1 = new Point(pc.x, A4.y);
                                p2 = new Point(pc.x, B0.y);
                                break;
                            case 1:
                                p1 = new Point(B2.x, A4.y);
                                break;
                            case 2:
                                p1 = new Point(B4.x, A4.y);
                                p2 = B4;
                                break;
                            case 3:
                                p1 = new Point(pc.x, A4.y);
                                p2 = new Point(pc.x, B6.y);
                                p3 = B6;
                                break;
                        }
                        break;
                    case 3:
                        switch (ladoB) {
                            case 0:
                                p1 = new Point(A6.x, B0.y);
                                break;
                            case 1:
                                p1 = new Point(A6.x, pc.y);
                                p2 = new Point(B2.x, pc.y);
                                break;
                            case 2:
                                p1 = new Point(A6.x, B3.y);
                                p2 = B3;
                                p4 = B4;
                                break;
                            case 3:
                                p1 = new Point(A6.x, B6.y);
                                p2 = B6;
                                break;
                        }
                        break;
                }
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Case D">
            case 'D':
                switch (ladoA) {
                    case 0:
                        switch (ladoB) {
                            case 0:
                                tmpPoint = menorPonto(A0, B0, true);
                                p1 = new Point(tmpPoint.x, A0.y);
                                p2 = new Point(tmpPoint.x, B0.y);
                                break;
                            case 1:
                                if (pontaB.x + distancia > pontaA.x) {
                                    p1 = A0;
                                    p2 = new Point(A0.x, pc.y);
                                    p3 = new Point(B2.x, pc.y);
                                } else {
                                    p1 = new Point(B2.x, A0.y);
                                }
                                break;
                            case 2:
                                p1 = A0;
                                p2 = new Point(A0.x, pc.y);
                                p3 = new Point(B4.x, pc.y);
                                p4 = B4;
                                break;
                            case 3:
                                tmpPoint = menorPonto(A0, B7, true);
                                p1 = new Point(tmpPoint.x, A0.y);
                                p2 = new Point(tmpPoint.x, B7.y);
                                p3 = B6;
                                break;
                        }
                        break;
                    case 1:
                        switch (ladoB) {
                            case 0:
                                tmpPoint = menorPonto(A1, B0, true);
                                p1 = A2;
                                p2 = new Point(tmpPoint.x, A1.y);
                                p3 = new Point(tmpPoint.x, B0.y);
                                break;
                            case 1:
                                p1 = A2;
                                if (pontoMaisProx(A2.x, A1.x, A3.x)) {
                                    if (A1.x < pontaB.x) {
                                        p2 = A1;
                                        p3 = new Point(A1.x, B2.y);
                                        p4 = B2;
                                    } else {
                                        p2 = new Point(B2.x, A1.y);
                                    }
                                } else {
                                    if (A3.x > pontaB.x) {
                                        p2 = A3;
                                        p3 = new Point(A3.x, B2.y);
                                        p4 = B2;
                                    } else {
                                        p2 = new Point(B2.x, A3.y);
                                    }
                                }
                                break;
                            case 2:
                                p1 = A2;
                                tmpPoint = maiorPonto(A3, B4, true);
                                p2 = new Point(tmpPoint.x, A3.y);
                                p3 = new Point(tmpPoint.x, B4.y);
                                break;
                            case 3:
                                p1 = A2;
                                if (pontoMaisProx(A2.x, A1.x, A3.x)) {
                                    tmpPoint = menorPonto(A1, B7, true);
                                    p2 = new Point(tmpPoint.x, A1.y);
                                    p3 = new Point(tmpPoint.x, B7.y);
                                } else {
                                    tmpPoint = maiorPonto(A3, B5, true);
                                    p2 = new Point(tmpPoint.x, A3.y);
                                    p3 = new Point(tmpPoint.x, B5.y);
                                }

                                p4 = B6;
                                break;
                        }
                        break;
                    case 2:
                        switch (ladoB) {
                            case 0:
                                p1 = A4;
                                p2 = new Point(A4.x, pc.y);
                                p3 = new Point(B0.x, pc.y);
                                p4 = B0;
                                break;
                            case 1:
                                if (pontaA.x + distancia > pontaB.x) {
                                    p1 = A4;
                                    p2 = new Point(A4.x, pc.y);
                                    p3 = new Point(B2.x, pc.y);
                                } else {
                                    p1 = new Point(B2.x, A4.y);
                                }
                                break;
                            case 2:
                                tmpPoint = maiorPonto(A4, B4, true);
                                p1 = new Point(tmpPoint.x, A4.y);
                                p2 = new Point(tmpPoint.x, B4.y);
                                break;
                            case 3:
                                tmpPoint = maiorPonto(A4, B5, true);
                                p2 = new Point(tmpPoint.x, A4.y);
                                p3 = new Point(tmpPoint.x, B5.y);
                                p4 = B6;
                                break;
                        }
                        break;
                    case 3:
                        switch (ladoB) {
                            case 0:
                                if (A6.y > pontaB.y || A6.x > pontaB.x - distancia) {
                                    p1 = new Point(A6.x, pc.y);
                                    p2 = new Point(B1.x, pc.y);
                                    p3 = new Point(B1.x, pc.y);
                                    p4 = B0;
                                } else {
                                    p1 = new Point(A6.x, B0.y);
                                }

                                break;
                            case 1:
                                p1 = new Point(A6.x, pc.y);
                                p2 = new Point(B2.x, pc.y);
                                break;
                            case 2:
                                if (pontaA.x > pontaB.x + distancia) {
                                    p1 = new Point(A6.x, B4.y);
                                } else {
                                    p1 = new Point(A6.x, pc.y);
                                    p2 = new Point(B4.x, pc.y);
                                    p4 = B4;
                                }
                                break;
                            case 3:
                                if (A6.x < B0.x || A6.x > B4.x) {
                                    p1 = new Point(A6.x, B6.y);
                                    p2 = B6;
                                } else {
                                    p1 = new Point(A6.x, pc.y);
                                    if (pontoMaisProx(A6.x, A7.x, A5.x)) {
                                        p2 = new Point(B7.x, pc.y);
                                        p3 = B7;
                                    } else {
                                        p2 = new Point(B5.x, pc.y);
                                        p3 = B5;
                                    }
                                    p4 = B6;
                                }
                                break;
                        }
                        break;
                }
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Case I">
            case 'I':
                switch (ladoA) {
                    case 0:
                        switch (ladoB) {
                            case 0:
                                p1 = A0;
                                p2 = new Point(A0.x, B0.y);
                                break;
                            case 1:
                                p1 = A0;
                                p2 = new Point(A0.x, B2.y);
                                p3 = B2;
                                break;
                            case 2:
                                p1 = A0;
                                p2 = new Point(A0.x, B3.y);
                                p3 = B3;
                                p4 = B4;
                                break;
                            case 3:
                                //p1 = A0;
                                //p2 = new Point(A0.x, pc.y);
                                //p3 = new Point(B6.x, pc.y);

                                p1 = A0;
                                tmpPoint = maiorPonto(A7, B6, false);
                                p2 = new Point(A7.x, tmpPoint.y);
                                p3 = new Point(B6.x, tmpPoint.y);

                                break;

                        }
                        break;
                    case 1:
                        switch (ladoB) {
                            case 0:
                                p1 = new Point(A2.x, B0.y);
                                break;
                            case 1:
                                p1 = new Point(A2.x, B2.y);
                                p2 = B2;
                                break;
                            case 2:
                                p1 = new Point(A2.x, pc.y);
                                p2 = new Point(B4.x, pc.y);
                                p3 = B4;
                                break;
                            case 3:
                                p1 = new Point(A2.x, pc.y);
                                p2 = new Point(B6.x, pc.y);
                                break;
                        }
                        break;
                    case 2:
                        switch (ladoB) {
                            case 0:
                                p1 = new Point(pc.x, A4.y);
                                p2 = new Point(pc.x, B0.y);
                                break;
                            case 1:
                                p1 = new Point(pc.x, A4.y);
                                p2 = new Point(pc.x, B2.y);
                                p3 = B2;
                                break;
                            case 2:
                                p1 = new Point(B4.x, A4.y);
                                p2 = B4;
                                break;
                            case 3:
                                p1 = new Point(B6.x, A4.y);
                                break;
                        }
                        break;
                    case 3:
                        switch (ladoB) {
                            case 0:
                                p1 = A6;
                                p2 = new Point(pc.x, A6.y);
                                p3 = new Point(pc.x, B0.y);
                                break;
                            case 1:
                                p1 = A6;
                                p2 = new Point(pc.x, A6.y);
                                p3 = new Point(pc.x, B2.y);
                                p4 = B2;
                                break;
                            case 2:
                                p1 = A6;
                                p2 = new Point(B4.x, A6.y);
                                p4 = B4;
                                break;
                            case 3:
                                p1 = A6;
                                p2 = new Point(B6.x, A6.y);
                                break;
                        }
                        break;
                }
                break;
            // </editor-fold>
            }

        ArrayList<PontoDeLinha> pontos = getPontos();
        while (pontos.size() > 2) {
            RemoveSubItem(pontos.get(pontos.size() - 2));
        }

        Point[] todos = inver ? new Point[]{p4, p3, p2, p1} : new Point[]{p1, p2, p3, p4};
        int tl = 0;
        boolean isvisi = getPontaA().isVisible();
        for (int i = 0; i < 4; i++) {
            if (todos[i] != null) {
                tl++;
                PontoDeLinha px = this.InserirPonto(tl);
                px.setVisible(isvisi);
                px.setCentro(todos[i]);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Pontos: apoio para OrganizeLinha">
    /**
     * O ponto "quem" esta mais próximo "deste" em relação à "naoDeste"?
     * @param quem
     * @param deste
     * @param naoDeste
     * @return Sim ou Não!
     */
    private boolean pontoMaisProx(int quem, int deste, int naoDeste) {
        return (naoDeste - quem) > (quem - deste);
    }

    /**
     * Qual o maior ponto - porém o parâmetro "x" indica se será usada a coordenada X do ponto para a comparação, caso seja "false", será usada a coordenada Y.
     * @param A 
     * @param B
     * @param x - A.x e B.x se true, A.y e B.y se false!
     * @return 
     */
    private Point maiorPonto(Point A, Point B, boolean x) {
        if (x) {
            if (A.x > B.x) {

                return A;
            }
            return B;
        } else {
            if (A.y > B.y) {
                return A;
            }
            return B;
        }
    }
    
    /**
     * Qual o menor ponto - porém o parâmetro "x" indica se será usada a coordenada X do ponto para a comparação, caso seja "false", será usada a coordenada Y.
     * @param A 
     * @param B
     * @param x - A.x e B.x se true, A.y e B.y se false!
     * @return 
     */
    private Point menorPonto(Point A, Point B, boolean x) {
        if (x) {
            if (A.x < B.x) {

                return A;
            }
            return B;
        } else {
            if (A.y < B.y) {
                return A;
            }
            return B;
        }
    }
    // </editor-fold>


//    @Override
//    protected void ToXmlValores(Document doc, Element me) {
//        super.ToXmlValores(doc, me);
//        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Inteligente", isInteligente()));
//    }
    
    private PreTexto tag = null;

    public PreTexto getTag() {
        return tag;
    }

    public void setTag(PreTexto tag) {
        if (this.tag == tag) {
            return;
        }
        if (this.tag != null) {
            this.tag.SetLinhaMestre(null);
        }
        this.tag = tag;
        if (this.tag != null) {
            this.tag.SetLinhaMestre(this);
        }
    }

    public void SetTag(PreTexto tag) {
        if (this.tag == tag) {
            return;
        }
        this.tag = tag;
    }

    @Override
    public boolean Destroy() {
        setTag(null);
        return super.Destroy();
    }

    @Override
    public void reSetBounds() {
        SetaA = null;
        SetaB = null;
        super.reSetBounds();
        if (tag != null) {
            tag.Posicione();
        }
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        Element lig = doc.createElement("Tag");
        util.XMLGenerate.AtributoRefFormElementar(lig, "LinhaMestre", getTag());
        me.appendChild(lig);
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Dashed", isDashed()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Ancorado", isAncorado()));

        if (showConfigSeta) {
            me.appendChild(util.XMLGenerate.ValorBoolean(doc, "TemSetaPontaA", isTemSetaPontaA()));
            me.appendChild(util.XMLGenerate.ValorBoolean(doc, "TemSetaPontaB", isTemSetaPontaB()));
            me.appendChild(util.XMLGenerate.ValorBoolean(doc, "SetaAberta", isSetaAberta()));
            me.appendChild(util.XMLGenerate.ValorInteger(doc, "SetaLargura", getSetaLargura()));
        }
    }

    @Override
    public boolean CommitXML(Element me, HashMap<Element, FormaElementar> mapa) {
        Element lig = util.XMLGenerate.FindByNodeName(me, "Tag");

        String stag = lig.getAttribute("LinhaMestre");
        FormaElementar resA = util.XMLGenerate.FindWhoHasID(stag, mapa);
        if (resA instanceof Texto) {
            setTag((PreTexto) resA);
        }
        return super.CommitXML(me, mapa);
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        setDashed(util.XMLGenerate.getValorBooleanFrom(me, "Dashed"));
        setAncorado(util.XMLGenerate.getValorBooleanFrom(me, "Ancorado"));

        if (showConfigSeta) {
            setTemSetaPontaA(util.XMLGenerate.getValorBooleanFrom(me, "TemSetaPontaA"));
            setTemSetaPontaB(util.XMLGenerate.getValorBooleanFrom(me, "TemSetaPontaB"));
            setSetaAberta(util.XMLGenerate.getValorBooleanFrom(me, "SetaAberta"));
            setSetaLargura(util.XMLGenerate.getValorIntegerFrom(me, "SetaLargura"));
        }
        return super.LoadFromXML(me, colando);
    }
    //<editor-fold defaultstate="collapsed" desc="Seta">
    private boolean temSetaPontaA = false;

    public boolean isTemSetaPontaA() {
        return temSetaPontaA;
    }

    public void setTemSetaPontaA(boolean temSetaPontaA) {
        if (this.temSetaPontaA == temSetaPontaA) {
            return;
        }
        this.temSetaPontaA = temSetaPontaA;
        Invalidate();
    }
    private boolean temSetaPontaB = false;

    public boolean isTemSetaPontaB() {
        return temSetaPontaB;
    }

    public void setTemSetaPontaB(boolean temSetaPontaB) {
        if (this.temSetaPontaB == temSetaPontaB) {
            return;
        }
        this.temSetaPontaB = temSetaPontaB;
        Invalidate();
    }
    protected int setaLargura = 10;

    public int getSetaLargura() {
        return setaLargura;
    }

    public void setSetaLargura(int setaLargura) {
        if (this.setaLargura == setaLargura) {
            return;
        }
        this.setaLargura = setaLargura;
        if (this.setaLargura > 99 || this.setaLargura < 10) {
            this.setaLargura = 10;
        }
        InvalidateArea();
        SetaA = null;
        SetaB = null;
    }

    /**
     * Desenha as setas
     *
     * @param g
     */
    public void paintSeta(Graphics2D g) {
        if (!(isTemSetaPontaA() || isTemSetaPontaB()) || !(showConfigSeta)) {
            return;
        }
        CalculeSetas();

        if (isTemSetaPontaA()) {
            g.fill(SetaA);
        }

        if (isTemSetaPontaB()) {
            g.fill(SetaB);
        }
    }

    private Shape SetaA = null;
    private Shape SetaB = null;

    private boolean setaAberta = true;

    public boolean isSetaAberta() {
        return setaAberta;
    }

    /**
     * Desenha as setas em forma de flecha?
     *
     * @param setaAberta
     */
    public void setSetaAberta(boolean setaAberta) {
        if (this.setaAberta != setaAberta) {
            this.setaAberta = setaAberta;
            InvalidateArea();
            SetaA = null;
            SetaB = null;
        }
    }

    @Override
    public void InvalidateArea() {
        super.InvalidateArea(); //To change body of generated methods, choose Tools | Templates.
        if (SetaA != null) {
            Rectangle r = SetaA.getBounds();
            InvalidateArea(r);
        }
        if (SetaB != null) {
            Rectangle r = SetaB.getBounds();
            InvalidateArea(r);
        }
    }

    @Override
    public boolean IsMe(Point p) {
        boolean im = super.IsMe(p); //To change body of generated methods, choose Tools | Templates.
        if (!im && (showConfigSeta) && (isTemSetaPontaA() || isTemSetaPontaB())) {
            if (SetaA != null) {
                im = SetaA.contains(p);
            }
            if ((!im) && SetaB != null) {
                im = SetaB.contains(p);
            }
        }
        return im;
    }

    /**
     * Cria os shapes das setas
     */
    protected void CalculeSetas() {
        if (!(isTemSetaPontaA() || isTemSetaPontaB()) || !(showConfigSeta)) {
            return;
        }
        int len = getSetaLargura() / 2;

        if (isTemSetaPontaA() && (SetaA == null)) {
            int x1 = getPontos().get(0).getCentro().x;
            int y1 = getPontos().get(0).getCentro().y;
            int x2 = getPontos().get(1).getCentro().x;
            int y2 = getPontos().get(1).getCentro().y;
            double dx = x2 - x1, dy = y2 - y1;
            double angle = Math.atan2(dy, dx);
            AffineTransform at = new AffineTransform();
            at.rotate(angle, x1, y1);

            Path2D res = new Path2D.Double();
            res.moveTo(x1, y1);
            res.lineTo(x1 + len, y1 - len);
            if (isSetaAberta()) {
                res.lineTo(x1 + 2, y1);
            }
            res.lineTo(x1 + len, y1 + len);
            res.lineTo(x1, y1);
            SetaA = res.createTransformedShape(at);
        }

        if (isTemSetaPontaB() && (SetaB == null)) {
            int tmp = getPontos().size() - 1;
            int x1 = getPontos().get(tmp).getCentro().x;
            int y1 = getPontos().get(tmp).getCentro().y;
            tmp--;
            int x2 = getPontos().get(tmp).getCentro().x;
            int y2 = getPontos().get(tmp).getCentro().y;
            double dx = x2 - x1, dy = y2 - y1;
            double angle = Math.atan2(dy, dx);

            AffineTransform at = new AffineTransform();
            at.rotate(angle, x1, y1);

            Path2D res = new Path2D.Double();
            res.moveTo(x1, y1);
            res.lineTo(x1 + len, y1 - len);
            if (isSetaAberta()) {
                res.lineTo(x1 + 2, y1);
            }
            res.lineTo(x1 + len, y1 + len);
            res.lineTo(x1, y1);
            SetaB = res.createTransformedShape(at);
        }
    }

    //</editor-fold>
    
    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        res.add(InspectorProperty.PropertyFactorySN("ancorado", "setAncorado", isAncorado()));
        return res;
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        ArrayList<InspectorProperty> res = super.CompleteGenerateProperty(GP);
        res.add(InspectorProperty.PropertyFactorySN("linha.dashed", "setDashed", isDashed()));
        if (showConfigSeta) {
            res.add(InspectorProperty.PropertyFactorySeparador("seta.titulo"));
            res.add(InspectorProperty.PropertyFactorySN("seta.pontadireita", "setTemSetaPontaA", isTemSetaPontaA()));
            res.add(InspectorProperty.PropertyFactorySN("seta.pontaesquerda", "setTemSetaPontaB", isTemSetaPontaB()));
            res.add(InspectorProperty.PropertyFactorySN("seta.setaaberta", "setSetaAberta", isSetaAberta()));

            res.add(InspectorProperty.PropertyFactoryNumero("seta.largura", "setSetaLargura", getSetaLargura()));
        }
        return res;
    }

    @Override
    public void DoPaint(Graphics2D g) {
        super.DoPaint(g);
        paintSeta(g);
    }

//    /**
//     * É usado em linhas no futuro que usam texto apenso.
//     */
//    public void PrepareTexto() {
//    }
}
