/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.linhas;

import controlador.Diagrama;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Forma;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author ccandido
 */
public class Linha extends FormaElementar {

    private static final long serialVersionUID = 2744377223700872067L;

    private float largura = 1f;
    protected float fator_largura = 1f;
    private final float larguraBase = 1f;

    public Linha(Diagrama master) {
        super(master);
    }
    private ArrayList<PontoDeLinha> Pontos = new ArrayList<>();

    public ArrayList<PontoDeLinha> getPontos() {
        return Pontos;
    }
    private Point[] PontosParaDesenho = null;
    private int[] pontosParaDesenhoX = null;
    private int[] pontosParaDesenhoY = null;

    public synchronized Point[] getPontosParaDesenho() {
        if (PontosParaDesenho != null) {
            return PontosParaDesenho;
        }

        PontosParaDesenho = new Point[Pontos.size()];
        pontosParaDesenhoX = new int[Pontos.size()];
        pontosParaDesenhoY = new int[Pontos.size()];

        int i = 0;
        for (PontoDeLinha pt : Pontos) {
            PontosParaDesenho[i] = pt.getCentro();
            pontosParaDesenhoX[i] = PontosParaDesenho[i].x;
            pontosParaDesenhoY[i] = PontosParaDesenho[i].y;
            i++;
        }
        return PontosParaDesenho;
    }

    public synchronized void setPontosParaDesenho(Point[] ptt) {
        PontosParaDesenho = ptt;
        if (ptt == null) {
            pontosParaDesenhoX = null;
            pontosParaDesenhoY = null;
            return;
        }
        int i = 0;
        pontosParaDesenhoX = new int[PontosParaDesenho.length];
        pontosParaDesenhoY = new int[PontosParaDesenho.length];
        for (Point pt : PontosParaDesenho) {
            pontosParaDesenhoX[i] = pt.x;
            pontosParaDesenhoY[i] = pt.y;
            i++;
        }
    }

    public PontoDeLinha NovoPonto() {
        setPontosParaDesenho(null);
        PontoDeLinha pt = new PontoDeLinha(this);
        this.Pontos.add(pt);
        return pt;
    }

    public synchronized PontoDeLinha InserirPonto(int posicao) {
        if (posicao < 0 || posicao > Pontos.size() - 1) {
            return null;
        }

        setPontosParaDesenho(null);
        PontoDeLinha pt = new PontoDeLinha(this);
        Pontos.add(posicao, pt);

//        if (posicao > 0) {
//            Pontos.get(posicao - 1).Proximo = pt;
//        }
//        if (posicao < Pontos.size() - 1) {
//            pt.Proximo = Pontos.get(posicao + 1);
//        }
//
        pt.IsTopOrBotton = false;

        return pt;
    }

    public PontoDeLinha InserirPonto(PontoDeLinha autal) {
        PontoDeLinha pt = InserirPonto(Pontos.indexOf(autal));
        return pt;
    }

    public void Inicie(Rectangle local) {
        SetBounds(local);
        if (Pontos.isEmpty()) {
            PontoDeLinha pt1 = NovoPonto();
            //pt1.IsHide = false;
            PontoDeLinha pt2 = InserirPonto(pt1);
            pt2.IsTopOrBotton = false;
            //PontoDeLinha pt3 =
            InserirPonto(pt2);
            //pt3.IsHide = false;
        }
        PontoDeLinha pt = Pontos.get(0);
        pt.setLocation(local.getLocation());

        int qtdPontos = Pontos.size();
        pt = Pontos.get(qtdPontos - 1);
        pt.setLocation(local.x + local.width - pt.getWidth(), local.y + local.height - pt.getHeight());

        int espacoX = local.width / (qtdPontos - 1);
        int posX = espacoX;
        int espacoY = local.height / (qtdPontos - 1);
        int posY = espacoY;
        for (int i = 1; i < qtdPontos - 1; i++) {
            Pontos.get(i).setCentro(new Point(getLeft() + posX, getTop() + posY));
            posX += espacoX;
            posY += espacoY;
        }
    }

    public boolean isDuplaLinha() {
        //int res = Float.compare(getLargura(), 1f);
        return (largura != larguraBase);
    }

    public void setDuplaLinha(boolean sn) {
        if (sn) {
            setLargura(2f);
        } else {
            setLargura(1f);
        }
        InvalidateArea();
    }

    private boolean dashed = false;

    public boolean isDashed() {
        return dashed;
    }

    public void setDashed(boolean dashed) {
        if (this.dashed != dashed) {
            this.dashed = dashed;
            DoMuda();
            InvalidateArea();
        }
    }

    @Override
    public void DoPaint(Graphics2D g) {
        super.DoPaint(g);
        Stroke bkp = g.getStroke();

        g.setPaint(getForeColor());
        if (getPontosParaDesenho() != null) {
            if (isDashed()) {
                g.setStroke(new BasicStroke(getLargura(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 2}, 0));
            } else {
                g.setStroke(new BasicStroke(
                        getLargura(),
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
            }

            g.drawPolyline(pontosParaDesenhoX, pontosParaDesenhoY, pontosParaDesenhoX.length);
        }
        g.setStroke(bkp);
    }

//    public void Diminua(int[] baseX, int[] baseY) {
//        final int w = getMaster().getPontoWidth();
//        for (int i = 0; i < baseX.length; i++) {
//            if ((baseX.length > 1) && ((i == 0) || (i == baseX.length - 1))) {
//                int a, b, c, d, n;
//                if (i == 0) {
//                    a = baseX[0];
//                    b = baseX[1];
//                    c = baseY[0];
//                    d = baseY[1];
//                    n = 1;
//                } else {
//                    a = baseX[baseX.length - 2];
//                    b = baseX[baseX.length - 1];
//                    c = baseY[baseX.length - 2];
//                    d = baseY[baseX.length - 1];
//                    n = -1;
//                }
//                int m = w*n*2;
//                if (a == b) {
//                    if (c < d) {
//                        baseY[i] = baseY[i] + m;
//                    } else {
//                        baseY[i] = baseY[i] - m;
//                    }
//                } else {
//                    if (a < b) {
//                        baseX[i] = baseX[i] + m;
//                    } else {
//                        baseX[i] = baseX[i] - m;
//                    }
//                }
//            } 
//        }
//    }
    @Override
    public boolean IsMe(Point p) {
        boolean res = false;
        if (!super.IsMe(p) || (getPontosParaDesenho() == null)) {
            return false;
        }
        for (int i = 1; i < PontosParaDesenho.length; i++) {
            if (Linha.SegmentIntersectRectangle(p.x - 1, p.y - 1, p.x + 1, p.y + 1,
                    PontosParaDesenho[i - 1].x, PontosParaDesenho[i - 1].y,
                    PontosParaDesenho[i].x, PontosParaDesenho[i].y)) {
                return true;
            }

        }
        return res;
    }

    @Override
    public void setSelecionado(boolean sel) {
        super.setSelecionado(sel);
        for (int i = 0; i < Pontos.size(); i++) {
            Pontos.get(i).setVisible(sel && isVisible());
            if (sel) {
                Pontos.get(i).BringToFront();
            }
        }
        mouseExited(null);
    }

    @Override
    public void DoPontoCor(boolean verde) {
        Color cor = verde ? getMaster().getPontoCorMultSel() : getMaster().getPontoCor();
        for (PontoDeLinha pt : Pontos) {
            pt.setBackColor(cor);
        }
    }

    @Override
    public void HidePontos(boolean esconde) {
        super.HidePontos(esconde);
        for (int i = 0; i < Pontos.size(); i++) {
            Pontos.get(i).setIsHide(esconde);
        }
    }

    @Override
    public void reSetBounds() {
        PontosParaDesenho = null;
        int menorLeft = Pontos.get(0).getLeft();
        int menorTop = Pontos.get(0).getTop();
        int maiorLeft = Pontos.get(0).getLeft();
        int maiorTop = Pontos.get(0).getTop();

        for (PontoDeLinha pt : Pontos) {
            menorLeft = Math.min(pt.getLeft(), menorLeft);
            menorTop = Math.min(pt.getTop(), menorTop);
            maiorLeft = Math.max(pt.getLeft(), maiorLeft);
            maiorTop = Math.max(pt.getTop(), maiorTop);
        }
        SetBounds(menorLeft, menorTop, maiorLeft + Pontos.get(0).getWidth() - menorLeft, maiorTop + Pontos.get(0).getHeight() - menorTop);
        InvalidateArea();
    }

    /**
     * Anexa e reposiciona os pontos que foram soltados sobre uma Forma
     */
    public boolean AnexePontos() {
        PontoDeLinha pt = Pontos.get(0);
        boolean done = false;
        if (FormasALigar != null && FormasALigar.length == 0) {
            return Ligar();
        }
        Elementar res = getMaster().CaptureBaseFromPoint(null, pt.getCentro());
        if (res instanceof Forma) {
            res = res.ProcessaComposicao(pt.getCentro());
            pt.setEm((Forma) res);
            done = true;
        } else {
            pt.setEm(null);
        }
        PontoDeLinha pt2 = Pontos.get(Pontos.size() - 1);
        res = getMaster().CaptureBaseFromPoint(null, pt2.getCentro());
        if (res instanceof Forma) {
            res = res.ProcessaComposicao(pt2.getCentro());
            pt2.setEm((Forma) res);
            done = true;
        } else {
            pt2.setEm(null);
        }
        if (done) {
            if (pt.getEm() != null) {
                pt.getEm().PosicionePonto(pt);
            }
            if (pt2.getEm() != null) {
                pt2.getEm().PosicionePonto(pt2);
            }
            OrganizeLinha();
            Recalcule();
        } else {
            InvalidateArea();
        }
        return done;
    }

    /**
     * Quando o objetivo for ligar duas formas, basta que FormasALigar seja carregada com elas! Isso forçará uma ligação entre duas unidades, impedido que uma ligação indesejada ocorra por conde de existir uma outra unidade sobre a que esta sendo ligaga.
     */
    transient public Forma[] FormasALigar = null;

    /**
     * Anexa e reposiciona os pontos que ligam duas formas.
     *
     * @return Ligou?
     */
    public boolean Ligar() {
        if (FormasALigar == null || FormasALigar.length != 2) {
            return false;
        }
        PontoDeLinha pt = Pontos.get(0);
        Elementar res = FormasALigar[0];
        res = res.ProcessaComposicao(pt.getCentro());
        pt.setEm((Forma) res);
        PontoDeLinha pt2 = Pontos.get(Pontos.size() - 1);
        res = FormasALigar[1];
        res = res.ProcessaComposicao(pt2.getCentro());
        pt2.setEm((Forma) res);

        FormasALigar = null;

        if (pt.getEm() != null) {
            pt.getEm().PosicionePonto(pt);
        }
        if (pt2.getEm() != null) {
            pt2.getEm().PosicionePonto(pt2);
        }
        OrganizeLinha();
        Recalcule();
        return true;
    }

    /**
     * Reposiciona o clientRectangle da objeto linha com base em seus pontos = reSetBounds
     */
    private void Recalcule() {
        reSetBounds();
    }

    @Override
    public void DoRaiseMove(int movX, int movY) {
        super.DoRaiseMove(movX, movY);
        getMaster().ReciveProcessMove(this, movX, movY);
    }

    @Override
    public void DoMove(int movX, int movY) {
        if (getMaster().IsMultSelecionado()) {
            for (PontoDeLinha pt : Pontos) {
                if (pt.IsTopOrBotton && pt.isEstaLigado()) {
                    if (pt.getEm().isParte() && (pt.getEm().getPrincipal() instanceof Forma)) {
                        Forma tmp = (Forma) pt.getEm().getPrincipal();
                        if ((!tmp.isSelecionado())) {
                            continue;
                        }
                    } else {
                        if ((!pt.getEm().isSelecionado())) {
                            continue;
                        }
                    }
                }
                pt.DoMove(movX, movY);
            }
        } else {
            Pontos.stream().forEach((pt) -> {
                pt.DoMove(movX, movY);
            });
        }
        Recalcule();
    }

    @Override
    public boolean Reenquadre() {
        boolean res = false;
        if (isSelecionado() && getMaster().IsMultSelecionado()) {
            for (PontoDeLinha pt : Pontos) {
                if (!pt.isEstaLigado()) {
                    res = pt.Reenquadre();
                }
            }
        } else {
            for (PontoDeLinha pt : Pontos) {
                res = pt.Reenquadre();
            }
        }
        if (res) {
            Recalcule();
        }
        return res;
    }

    @Override
    public void DoRaizeReenquadreReposicione() {
        getMaster().DoBaseReenquadreReposicione();
    }

    protected void DoRaizeReenquadre() {
        if (isSelecionado()) {
            getMaster().DoBaseReenquadreReposicione();
        }
    }
    transient boolean jaSel = false;

    @Override
    public void mousePressed(MouseEvent e) {
//        if (isNulo()) {
//            return;
//        }
        if (!isSelecionado()) {
            getMaster().DiagramaDoSelecao(this, true, false);
            jaSel = false;
        } else {
            jaSel = true;
        }
        //Prescionou em cima do ponto de ligação.
        PontoDeLinha pt = getPontaA();
        if (pt != null) {
            if (pt.ForceIsMe(e.getPoint())) {
                getMaster().setElementarSobMouse(pt);
                pt.mousePressed(e);
                return;
            }
        }
        pt = getPontaB();
        if (pt != null) {
            if (pt.ForceIsMe(e.getPoint())) {
                getMaster().setElementarSobMouse(pt);
                pt.mousePressed(e);
                return;
            }
        }
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (jaSel) {
            boolean combine = (getMaster().isShiftDown() || getMaster().isControlDown());
            if (combine) {
                getMaster().DiagramaDoSelecao(this, true, false);
            }
        }

        if (!isAncorado()) { 
            ProcessPontoMouseUp();
        }
        dragging = false;
        //reSetBounds();
        getPontaA().ProcessaOverDraw(true);
        getPontaB().ProcessaOverDraw(true);
        super.mouseReleased(e);
        Invalidate();
    }
    transient boolean dragging = false;

    @Override
    public void mouseDragged(MouseEvent e) {
        //if (!jaSel) return; //# Permite a movimentação apenas se já estiver selecionado
        super.mouseDragged(e);
        dragging = true;
        if (!getMaster().IsMultSelecionado()) {
            getPontaA().ProcessaOverDraw(false);
            getPontaB().ProcessaOverDraw(false);
        }
    }

    private void ProcessPontoMouseUp() {
        if (getMaster().IsMultSelecionado() || !dragging) {
            return;
        }
        AnexePontos();
    }

    @Override
    public boolean IntersectPath(Rectangle recsel) {
        if (!super.IntersectPath(recsel)) {
            return false;
        }
        int fimX = recsel.width + recsel.x;
        int fimY = recsel.height + recsel.y;
        for (int i = 1; i < PontosParaDesenho.length; i++) {
            if (Linha.SegmentIntersectRectangle(recsel.x, recsel.y, fimX, fimY, PontosParaDesenho[i - 1].x,
                    PontosParaDesenho[i - 1].y, PontosParaDesenho[i].x, PontosParaDesenho[i].y)) {
                return true;
            }
        }
        return false;
    }

    public static boolean SegmentIntersectRectangle(double a_rectangleMinX,
            double a_rectangleMinY, double a_rectangleMaxX, double a_rectangleMaxY,
            double a_p1x, double a_p1y, double a_p2x,
            double a_p2y) {
        // Find min and max X for the segment

        double minX = a_p1x;
        double maxX = a_p2x;
        if (a_p1x > a_p2x) {
            minX = a_p2x;
            maxX = a_p1x;
        } // Find the intersection of the segment's and rectangle's x-projections

        if (maxX > a_rectangleMaxX) {
            maxX = a_rectangleMaxX;
        }

        if (minX < a_rectangleMinX) {
            minX = a_rectangleMinX;
        }

        if (minX > maxX) // If their projections do not intersect return false
        {
            return false;
        } // Find corresponding min and max Y for min and max X we found before

        double minY = a_p1y;
        double maxY = a_p2y;
        double dx = a_p2x - a_p1x;
        if (Math.abs(dx) > 0.0000001) {
            double a = (a_p2y - a_p1y) / dx;
            double b = a_p1y - a * a_p1x;
            minY = a * minX + b;
            maxY = a * maxX + b;
        }

        if (minY > maxY) {
            double tmp = maxY;
            maxY = minY;
            minY = tmp;
        } // Find the intersection of the segment's and rectangle's y-projections

        if (maxY > a_rectangleMaxY) {
            maxY = a_rectangleMaxY;
        }

        if (minY < a_rectangleMinY) {
            minY = a_rectangleMinY;
        }

        if (minY > maxY) // If Y-projections do not intersect return false
        {
            return false;
        }
        return true;
    }

    public synchronized void OrganizeLinha() {
    }

    protected float getLargura() {
        return largura * fator_largura;
    }

    protected void setLargura(float largura) {
        this.largura = largura;
    }

    // <editor-fold defaultstate="collapsed" desc="Pontos">
    public PontoDeLinha getPontaA() {
        if (Pontos.size() > 0) {
            return getPontos().get(0);
        }
        return null;
    }

    public PontoDeLinha getPontaB() {
        if (Pontos.size() > 0) {
            return getPontos().get(getPontos().size() - 1);
        }
        return null;
    }

    public Forma getFormaPontaA() {
        return getPontos().get(0).getEm();
    }

    public Forma getFormaPontaB() {
        return getPontos().get(getPontos().size() - 1).getEm();
    }

    public Forma getOutraPonta(Forma forma) {
        if (getFormaPontaA() == forma) {
            return getFormaPontaB();
        } else {
            return getFormaPontaA();
        }
    }

    public PontoDeLinha getOutraPonta(PontoDeLinha pt) {
        if (getPontaA() == pt) {
            return getPontaB();
        } else {
            return getPontaA();
        }
    }
    // </editor-fold>

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        fator_largura = 2;
        //Rectangle r = getBounds();
        //r.grow(50, 50);
        InvalidateArea(getBounds());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (e != null) {
            super.mouseExited(e);
        }
        fator_largura = 1;
        InvalidateArea(getBounds());
//        Rectangle r = getBounds();
//        r.grow(50, 50);
//        InvalidateArea(r);
    }

//    @Override
//    protected void InfoDiagrama_ToXmlValores(Document doc, Element me) {
//        super.InfoDiagrama_ToXmlValores(doc, me);
//        //me.appendChild(util.XMLGenerate.ValorInteger(doc, "Largura", (int)largura));
//        //me.appendChild(util.XMLGenerate.ValorInteger(doc, "Fator_Largura", (int)fator_largura));
//        Element sbPontos = doc.createElement("Pontos");
//        for (PontoDeLinha pl : getPontos()) {
//            pl.ToXlm(doc, sbPontos);
//        }
//        me.appendChild(sbPontos);
//    }
    public void Clean() {
        while (2 < getPontos().size()) {
            RemoveSubItem(Pontos.get(1));
        }
        setPontosParaDesenho(null);
        OrganizeLinha();
        Recalcule();
    }

    @Override
    public void RemoveSubItem(Elementar si) {
        super.RemoveSubItem(si);
        if (si instanceof PontoDeLinha) {
            Pontos.remove((PontoDeLinha) si);
        }
    }
}
