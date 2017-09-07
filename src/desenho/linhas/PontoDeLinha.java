/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.linhas;

import desenho.Elementar;
import desenho.ElementarEvento;
import desenho.FormaElementar;
import desenho.PontoElementar;
import desenho.formas.Forma;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import util.Constantes;

/**
 *
 * @author Rick
 */
public class PontoDeLinha extends PontoElementar {

    private static final long serialVersionUID = -8695206427443411236L;

    public PontoDeLinha(FormaElementar pai) {
        super(pai);
        IsTopOrBotton = true;
        if (pai instanceof Linha) {
            linhaDona = (Linha) pai;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Campos">
    //public PontoDeLinha Proximo;
    public boolean IsTopOrBotton;
    private int posicao = -1;

    @Override
    public int getPosicao() {
        return posicao;
    }

    @Override
    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    private transient Point down = new Point(0, 0);
    private transient boolean isMouseDown = false;
    //public boolean AllwaysHide = false;
    //public int[] Fixacao = new int[]{0, 0, 0};

    public boolean isEstaLigado() {
        return getEm() != null;
    }

    public Forma getEm() {
        return em;
    }

    public void setEm(Forma destino) {
        if (this.em != destino) {
            if (this.em != null) //atual
            {
                PerformLigacao(this.em, false);
                this.em.menosLigacao(this);
                this.em = null;
            }

            if (destino != null) {
                if (!destino.CanLiga(this)) {
                    this.em = null;
                    return;
                }
            }
            this.em = destino;
            if (destino != null) {
                PerformLigacao(destino, true);
                this.em.maisLigacao(this);
            }
        }
    }
    private Forma em = null;

    /**
     * Força o setEm()
     */
    public void SetEm(Forma destino) {
        this.em = destino;
        if (destino != null) {
            PerformLigacao(destino, true);
            //this.em.maisLigacao(this);
        }
    }

    // </editor-fold>
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        isMouseDown = true;
        down = new Point(e.getX(), e.getY());
    }

    private Linha linhaDona;

    @Override
    public Linha getDono() {
        return linhaDona;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMouseDown = false;
        dragging = false;

        if (IsTopOrBotton) {
            Elementar res = getMaster().CaptureBaseFromPoint(null, e.getPoint());
            if (res instanceof Forma) {
                res = res.ProcessaComposicao(e.getPoint());
//                if (res.isComposto()) {
//                    res = res.ProcessaComposicao(e.getPoint());
//                }
                setEm((Forma) res);
                if (getEm() != null) {
                    getEm().PosicionePonto(this);
                    getDono().OrganizeLinha();
                }
            } else {
                setEm(null);
            }
        }
        getDono().reSetBounds();
        ReenquadreLinha();
        Rectangle rec = getBounds();
        rec.grow(5, 5);
        InvalidateArea(rec);
        ProcessaOverDraw(true);
        super.mouseReleased(e);
    }

    /**
     * Liga uma linha a um objeto manualmente (sem o uso do mouse).
     *
     * @param res
     * @return
     */
    public boolean LigarA(Elementar res) {
        boolean sn = false;
        if (res instanceof Forma) {
            setEm((Forma) res);
            if (getEm() != null) {
                getEm().PosicionePonto(this);
                getDono().OrganizeLinha();
                sn = true;
            }
        }
        getDono().reSetBounds();
        ReenquadreLinha();
        Rectangle rec = getBounds();
        rec.grow(5, 5);
        InvalidateArea(rec);
        //ProcessaOverDraw(true);
        return sn;
    }

    private transient boolean dragging = false;

    @Override
    public void mouseDragged(MouseEvent e) {
        dragging = true;
        super.mouseDragged(e);
        int X = e.getX();
        int Y = e.getY();
        if (isMouseDown) {
            int movX = X - down.x;
            int movY = Y - down.y;
            if ((movX != 0) || (movY != 0)) {
                DoMove(movX, movY);
                down.setLocation(e.getPoint());
                getDono().reSetBounds();
            }
            //EmLinha.setTop(getTop());
            //EmColuna.setLeft(getLeft());
            down.setLocation(e.getPoint());
            ProcessaOverDraw(false);
        }
    }

    private Forma overDraw = null;

    public void ProcessaOverDraw(boolean zera) {
        if (IsTopOrBotton) {
            if (zera) {
                if (overDraw != null) {
                    overDraw.setOverMe(false);
                    overDraw = null;
                }
                return;
            }
            Elementar el = getMaster().CaptureBaseFromPoint(this, getCentro());
            if (el == null) {
                if (overDraw != null) {
                    overDraw.setOverMe(false);
                    overDraw = null;
                }
            } else {
                if (el.isComposto()) {
                    el = el.ProcessaComposicao(getCentro());
                }
                if (el instanceof Forma && el != overDraw) {
                    if (overDraw != null) {
                        overDraw.setOverMe(false);
                    }
                    overDraw = (Forma) el;
                    overDraw.setOverMe(true);
                }
            }
        }
    }

    @Override
    public void mouseDblClicked(MouseEvent e) {
        super.mouseDblClicked(e);
        getDono().ReciveClick(this, true, e);
    }

    private void ReenquadreLinha() {
        int recuo = getRecuo();
        boolean ok = false;
        if (getLeft() < 0) {
            setLeft(0);
            ok = true;
        }
        if (getTop() < 0) {
            setTop(0);
            ok = true;
        }
        if (getLeft() + getWidth() > getMaster().getWidth()) {
            setLeft(getMaster().getWidth() - getWidth() - recuo);
            ok = true;
        }
        if (getTop() + getHeight() > getMaster().getHeight()) {
            setTop(getMaster().getHeight() - getHeight() - recuo);
            ok = true;
        }
        if (ok) {
            getDono().reSetBounds();
        }
    }

    @Override
    public void ReciveNotificacao(ElementarEvento evt) {
        int i = evt.getCod();
        Linha linha = getDono();
        switch (i) {
            case Constantes.Operacao.opMove:
                if (!linha.isSelecionado()) {
                    Point pt = (Point) evt.getMsg();
                    DoMove(pt.x, pt.y);
                    meOrganizeLigacao();
                }
                break;
            case Constantes.Operacao.opReenquadre:
                Point pt = (Point) evt.getMsg();
                DoMove(pt.x, pt.y);
                meOrganizeLigacao();
                break;
            case Constantes.Operacao.opResize:
                ReposicioneAfterFormaResize((Forma) evt.getSender());
                break;
            case Constantes.Operacao.opReposicione:
                SimplesmenteReposicione((Forma) evt.getSender());
                break;
            case Constantes.Operacao.opOrganizeLigacoes:
                meOrganizeLigacao();
                break;
            case Constantes.Operacao.opDestroy:
                if (getEm() == evt.getSender()) {
                    em = null;
                    super.ReciveNotificacao(evt);
                    meOrganizeLigacao();
                }
                break;
            default:
                super.ReciveNotificacao(evt);
                break;
        }

//        if (i == Constantes.Operacao.opMove) {
//            if (!linha.isSelecionado()) {
//                Point pt = (Point) evt.getMsg();
//                DoMove(pt.x, pt.y);
//                meOrganizeLigacao();
//            }
//        } else if (i == Constantes.Operacao.opReenquadre) {
//            Point pt = (Point) evt.getMsg();
//            DoMove(pt.x, pt.y);
//            meOrganizeLigacao();
//        } else if (i == Constantes.Operacao.opResize) {
//            //if (isEstaLigado()) {
//            ReposicioneAfterFormaResize((Forma) evt.getSender());
//            //}
//        } else if (i == Constantes.Operacao.opReposicione) {
//            SimplesmenteReposicione((Forma) evt.getSender());
//        } else if (i == Constantes.Operacao.opOrganizeLigacoes) {
//            meOrganizeLigacao();
//        } else {
//            super.ReciveNotificacao(evt);
//        }
    }

    private void ReposicioneAfterFormaResize(Forma formaMovida) {
        boolean ok = false;
        Point area = getLocation();

        int recuo = -1;
        int corecuo = 1;

        int extremo1 = 0;
        int extremo2 = 0;
        int pos = 0;

        switch (getLado()) {
            case 0:
                setLeft(formaMovida.getLeft() - getWidth() / 2 + recuo);
                extremo1 = formaMovida.getTop() - getWidth() / 2 + recuo;
                extremo2 = formaMovida.getTopHeight() - getHeight() + corecuo;
                pos = getTop();
                if (pos < extremo1 || pos > extremo2) {
                    if (pos < extremo1) {
                        setTop(extremo1);
                    } else {
                        setTop(extremo2);
                    }
                    ok = true;
                }
                break;
            case 1:
                setTop(formaMovida.getTop() - getWidth() / 2 + recuo);
                extremo1 = formaMovida.getLeft() - getWidth() / 2 + recuo;
                extremo2 = formaMovida.getLeftWidth() - getWidth() + corecuo;
                pos = getLeft();
                if (pos < extremo1 || pos > extremo2) {
                    if (pos < extremo1) {
                        setLeft(extremo1);
                    } else {
                        setLeft(extremo2);
                    }
                    ok = true;
                }
                break;
            case 2:
                setLeft(formaMovida.getLeftWidth() - getWidth() / 2 + corecuo);
                extremo1 = formaMovida.getTop() - getWidth() / 2 + recuo;
                extremo2 = formaMovida.getTopHeight() - getHeight() / 2 + corecuo;
                pos = getTop();
                if (pos < extremo1 || pos > extremo2) {
                    if (pos < extremo1) {
                        setTop(extremo1);
                    } else {
                        setTop(extremo2);
                    }
                    ok = true;
                }
                break;
            case 3:
                setTop(formaMovida.getTopHeight() - getHeight() / 2 + corecuo);
                extremo1 = formaMovida.getLeft() - getWidth() / 2 + recuo;
                extremo2 = formaMovida.getLeftWidth() - getWidth() / 2 + corecuo;
                pos = getLeft();
                if (pos < extremo1 || pos > extremo2) {
                    if (pos < extremo1) {
                        setLeft(extremo1);
                    } else {
                        setLeft(extremo2);
                    }
                    ok = true;
                }
                break;
        }
        if (ok || area != getLocation()) {
            meOrganizeLigacao();
        }
    }

    public void SimplesmenteReposicione(Forma base) {
        base.PosicionePonto(this);
        meOrganizeLigacao();
    }

    protected void meOrganizeLigacao() {
        getDono().OrganizeLinha();
        getDono().reSetBounds();
    }

    private int lado = 0;

    public int getLado() {
        return lado;
    }

    public void setLado(int lado) {
        this.lado = lado;
    }

    @Override
    public void DoPaint(Graphics2D g) {
        super.DoPaint(g);
        if (IsTopOrBotton && !isEstaLigado()) {
            Paint bkpP = g.getPaint();
            Rectangle rec = getBounds();
            //rec.grow(2, 2);
            //g.setPaint(getMaster().getPontoCorMultSel());
            if (getMaster().IsMultSelecionado() && getDono().isSelecionado()) {
                g.setPaint(this.getBackColor());
            } else {
                g.setPaint(Color.red);
            }
            g.fillOval(rec.x, rec.y, rec.width, rec.height);
            g.drawString("x", getCentro().x, getCentro().y);
            g.setPaint(bkpP);

        }

        if (entrou) {
            Rectangle rec = getBounds();
            int x = rec.width / 2;
            int y = rec.height / 2;
            rec.grow(x + 1, y + 1);

            //rec = new Rectangle(rec.x - x, rec.y - y, rec.width * 2, rec.height * 2);
            Stroke bkp = g.getStroke();
            Paint bkpP = g.getPaint();

            g.setStroke(new BasicStroke(
                    1f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND,
                    2f,
                    new float[]{2f, 2f},
                    0f));
            g.setPaint(Color.gray);
            g.drawOval(rec.x, rec.y, rec.width, rec.height);
            g.setStroke(bkp);
            g.setPaint(bkpP);
        }

        if (dragging && getMaster().getEditor().isMostrarDimensoesAoMover()) {
            Stroke bkp = g.getStroke();
            Paint bkpP = g.getPaint();

            g.setStroke(new BasicStroke(
                    1f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND,
                    2f,
                    new float[]{2f, 2f},
                    0f));
            g.setPaint(Color.gray);
            g.drawLine(0, getCentro().y, getMaster().getWidth(), getCentro().y);
            g.drawLine(getCentro().x, 0, getCentro().x, getMaster().getHeight());

            Font bkpf = g.getFont();
            g.setFont(new Font(bkpf.getName(), Font.ITALIC, bkpf.getSize() - 2));
            g.drawString("[" + String.valueOf(getLeft()) + ","
                    + String.valueOf(getTop()) + "]", getWidth() + getLeft() + 2, getTop() - 2);
            g.setFont(bkpf);

            g.setStroke(bkp);
            g.setPaint(bkpP);
        }
    }

    /**
     * Vê se o ponto pertence a um ponto de linha mesmo que ele esteja oculto (invisível).
     */
    boolean ForceIsMe(Point p) {
        return getBounds().contains(p);
    }

    @Override
    public boolean IsMe(Point p) {
//        if (IsTopOrBotton && linhaDona.isSelecionado()) {
        if (linhaDona.isSelecionado()) {
            if (!isVisible()) {
                return super.IsMe(p);
            }
            Rectangle r = getBounds();
            int x = r.width / 2;
            int y = r.height / 2;
            //r = new Rectangle(r.x - x, r.y - y, r.width * 2, r.height * 2);
            r.grow(x, y);
            return r.contains(p);
        }
        return super.IsMe(p);
    }

    private boolean entrou = false;

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e); //To change body of generated methods, choose Tools | Templates.
        entrou = true;
        Rectangle r = getBounds();
        int x = r.width / 2;
        int y = r.height / 2;
        //r = new Rectangle(r.x - x, r.y - y, r.width * 2, r.height * 2);
        r.grow(x + 2, y + 2);
        InvalidateArea(r);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e); //To change body of generated methods, choose Tools | Templates.
        entrou = false;
        Rectangle r = getBounds();
        int x = r.width / 2;
        int y = r.height / 2;
        //r = new Rectangle(r.x - x, r.y - y, r.width * 2, r.height * 2);
        r.grow(x + 2, y + 2);
        InvalidateArea(r);
    }
}
