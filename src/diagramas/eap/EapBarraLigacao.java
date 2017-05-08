/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.eap;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.formas.Forma;
import desenho.linhas.Linha;
import desenho.linhas.PontoDeLinha;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class EapBarraLigacao extends Forma {

    private static final long serialVersionUID = 1939864565434922497L;

    public EapBarraLigacao(Diagrama modelo) {
        super(modelo);
        editFonte = false;
    }

    public EapBarraLigacao(Diagrama modelo, String texto) {
        super(modelo, texto);
        editFonte = false;
    }

    public final int LARG_ALT = 10;
    public final int VERTICAL = 0;
    public final int HORIZONTAL = 1;
    public final int HCENTRO = 0;
    public final int HESQUERDA = 1;
    public final int HDIREITA = 2;

    private int direcao = HORIZONTAL;
    private int posicao = HCENTRO;

    public int getDirecao() {
        return direcao;
    }

    public void setDirecao(int aDirection) {
        boolean mud = (direcao != aDirection);
        direcao = aDirection;
        if (((direcao == VERTICAL) && (getWidth() > LARG_ALT)) || ((getHeight() > LARG_ALT) && (direcao == HORIZONTAL))) {
            setStopRaize(true);
            if (direcao == VERTICAL) {
                setHeight(getWidth());
                setWidth(LARG_ALT);
            }
            if (direcao == HORIZONTAL) {
                setWidth(getHeight());
                setHeight(LARG_ALT);
            }
            setStopRaize(false);
            needRecalPts = true;
            if (isSelecionado()) {
                Reposicione();
            }
            mud = true;
        }
        PropagueResizeParaLigacoes();
        OrganizeLigacoes();
        if (mud) {
            DoMuda();
        }
        InvalidateArea();
    }

        public void SetDirecao(int aDirection) {
            boolean mud = (direcao != aDirection);
            setDirecao(aDirection);
            if (mud) OrganizeEap();
        }
        
    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        if (this.posicao != posicao) {
            this.posicao = posicao;
            OrganizeEap();
        }
    }

    public void setPosicaoDireto(int posicao) {
        this.posicao = posicao;
    }

    @Override
    public void DoPaint(Graphics2D g) {
        super.DoPaint(g);
        g.setColor(getForeColor());
        Rectangle r = getBounds();
        if (direcao == VERTICAL) {
            int m = r.width / 2;
            g.drawLine(r.x + m, r.y, r.x + m, r.y + r.height);
        } else {
            int m = r.height / 2;
            g.drawLine(r.x, r.y + m, r.x + r.width, r.y + m);
        }
        if (((direcao == VERTICAL) && (getWidth() > LARG_ALT)) || ((getHeight() > LARG_ALT) && (direcao == HORIZONTAL))) {
            setStopRaize(true);
            if (direcao == VERTICAL) {
                setWidth(LARG_ALT);
            }
            if (direcao == HORIZONTAL) {
                setHeight(LARG_ALT);
            }
            setStopRaize(false);
            needRecalPts = true;
            if (isSelecionado()) {
                Reposicione();
            }
        }
    }

    @Override
    public void PinteTexto(Graphics2D g) {
    }

    protected final int CONST_ORG_FULL = 060316;

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        ArrayList<InspectorProperty> res = GP;
        res.add(InspectorProperty.PropertyFactorySeparador("eapbarraligacao.organizacao"));
        res.add(InspectorProperty.PropertyFactoryMenu("diagrama.direcaovh", "SetDirecao", getDirecao(), Editor.fromConfiguracao.getLstDirecao(Controler.Comandos.cmdEapBarraLigacao)).
                AddCondicao(new String[]{"1"}, new String[]{"setPosicao"}));

        ArrayList<String> tmp = new ArrayList<>();
        tmp.add(Editor.fromConfiguracao.getValor("Inspector.lst.direcao.center")); //0
        tmp.add(Editor.fromConfiguracao.getValor("Inspector.lst.direcao.left")); //1
        tmp.add(Editor.fromConfiguracao.getValor("Inspector.lst.direcao.right")); //2
        res.add(InspectorProperty.PropertyFactoryMenu("eapbarraligacao.posicao", "setPosicao", getPosicao(), tmp));

        res.add(InspectorProperty.PropertyFactoryNumero("eapbarraligacao.distancia", "setDistancia", getDistancia()));
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "diagrama.comando.organizar"));
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "diagrama.comando.fullorganizar").setTag(CONST_ORG_FULL));

        return super.CompleteGenerateProperty(GP);
    }

    @Override
    public void PosicionePonto(PontoDeLinha ponto) {
        PontoDeLinha outraponta = ponto.getDono().getOutraPonta(ponto);
        if (outraponta.getTopHeight() < getTop()) {
            if (getDirecao() == HORIZONTAL) {
                int recuo = -1;
                int to = getTop() + getHeight() / 2 - ponto.getHeight() / 2;
                if (outraponta.getLeft() < getLeft()) {
                    ponto.setLado(0);
                    ponto.setLocation(getLeft() - ponto.getWidth() / 2 + recuo, to);
                    return;
                } else if (outraponta.getLeft() > getLeftWidth()) {
                    ponto.setLado(2);
                    ponto.setLocation(getLeftWidth() - ponto.getWidth() / 2 + recuo, to);
                    return;
                }
            }
            ponto.setLado(1);
            ponto.setCentro(getLeft() + getWidth() / 2, getTop() + ((getDirecao() == HORIZONTAL) ? getHeight() / 2 - 1 : 0));
            return;
        }

        if (getDirecao() == HORIZONTAL) {
            ponto.setLado(3);
            ponto.setTop(getTop() + getHeight() / 2 - ponto.getHeight() / 2);
            //} 
        } else {
            if (outraponta.getLeft() > getLeftWidth()) {
                ponto.setLado(2);
            } else {
                ponto.setLado(0);
            }
            ponto.setLeft(getLeft() + getWidth() / 2 - ponto.getWidth() / 2);
        }
    }

    @Override
    protected void PropagueResizeParaLigacoes() {
        super.PropagueResizeParaLigacoes();
        ArrayList<PontoDeLinha> lst = getListaDePontosLigados();
        for (PontoDeLinha pt : lst) {
            PosicionePonto(pt);
        }
    }

    @Override
    public boolean CanLiga(Forma forma, Linha lin) {
        if (super.CanLiga(forma, lin)) {
            if (forma == this) {
                return false;
            }
            return (forma == null || getListaDeFormasLigadas().indexOf(forma) < 0);
        }
        return false;
    }

    @Override
    public void mouseDblClicked(MouseEvent e) {
        super.mouseDblClicked(e);
        OrganizeEap();
    }

    private int distancia = LARG_ALT;

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
        DoMuda();
    }

    /**
     * Organiza uma Eap sem levar em conta se ela é um subnível de uma Eap maior
     */
    protected void PreOrganizeEap() {
        if (getMaster().IsMultSelecionado()) {
            return;
        }
        ArrayList<Forma> lst = getListaDeFormasLigadas();
        ArrayList<PontoDeLinha> lstp = getListaDePontosLigados();
        RemovaLinhasNaoLigadas(lstp);
        Forma maisAci = capturePrincipal();
        PontoDeLinha pt = getPontoLigadoA(lstp, maisAci);

        final int dist = getDistancia();
        if (lst.size() < 2) {
            return;
        }
        final int recuo = -1;
        final int corecuo = 1;
        HidePontos(true);
        if (getDirecao() == VERTICAL) {
            //<editor-fold defaultstate="collapsed" desc="VERTICAL">
            final int meiopt = pt.getWidth() / 2;
            final int tamPt = pt.getWidth();

            int le = ((maisAci.getWidth() - getWidth()) / 2) + maisAci.getLeft();
            int to = maisAci.getTopHeight() + getWidth();
            PontoDeLinha opt = pt.getDono().getOutraPonta(pt);
            setLocation(le, to);
            Reposicione();
            Reenquadre();

            le = maisAci.getLeft() + (maisAci.getWidth() - tamPt) / 2;
            opt.setLado(3);
            opt.setLocation(le, maisAci.getTopHeight() - meiopt + corecuo);
            pt.setLado(1);
            pt.setLocation(le, getTop() - meiopt + recuo);
            lst.remove(maisAci);
            lstp.remove(pt);
            //int tl = lst.size();
            //int i = 0;
            int posiL = getLeft() + getWidth() / 2;
            while (lst.size() > 0) {
                to = maisAci.getTopHeight() + 2 * dist;
                le = getLeftWidth() + getWidth();
                maisAci = getFormaMaisAcima(lst);
                pt = getPontoLigadoA(lstp, maisAci);
                opt = pt.getDono().getOutraPonta(pt);

                maisAci.setLocation(le, to);
                maisAci.ManualResized();
                to = maisAci.getTop() + (maisAci.getHeight() - tamPt) / 2;

                if (getTopHeight() < to) {
                    setHeight(to - getTop() + meiopt);
                    Reposicione();
                    Reenquadre();
                }

                pt.setLado(2);
                pt.setLocation(posiL, to);

                opt.setLado(0);
                opt.setLocation(maisAci.getLeft() - meiopt + recuo, to);

                lst.remove(maisAci);
                lstp.remove(pt);
                //i++;
//                maHeigth = Math.max(maHeigth, maisAci.getTopHeight());
//                maWidth = Math.max(maWidth, maisAci.getLeftWidth());
            }
            if (getTopHeight() > to) {
                int tmp = to - getTop() + meiopt;
                setHeight(Math.max(tmp, LARG_ALT));
                Reposicione();
            }
            //</editor-fold>
        } else {
            //<editor-fold defaultstate="collapsed" desc="HORIZONTAL">
            final int meiopt = pt.getHeight() / 2;
            //final int tamPt = pt.getHeight();

            int le = (maisAci.getLeft() + (maisAci.getWidth()) / 2) + meiopt;
            int to = maisAci.getTopHeight() + getHeight() + getHeight() / 2;
            lst.remove(maisAci);
            lstp.remove(pt);

            PontoDeLinha opt = pt.getDono().getOutraPonta(pt);
            opt.setLado(3);
            opt.setLocation(le - meiopt, maisAci.getTopHeight() - meiopt + corecuo);

            lst = ordeneMenor(lst);
            int tam = 0;
            for (Forma forma : lst) {
                tam += forma.getWidth() + dist;
            }
            tam = tam - dist - (lst.get(0).getWidth() / 2 + lst.get(lst.size() - 1).getWidth() / 2) - 1;
            tam = Math.max(tam, LARG_ALT);
            //if (opt.getLeft() < getLeft()) {
            if (getPosicao() == HESQUERDA) {
                SetBounds(le + lst.get(0).getWidth() + getHeight(), to, tam, getHeight());
                Reposicione();
                pt.setLado(0);
                to = getTop() + getHeight() / 2 - 1;
                pt.setLocation(getLeft() - pt.getWidth() / 2 + recuo, to);
                //} else if (opt.getLeft() > getLeftWidth()) {
            } else if (getPosicao() == HDIREITA) {
                SetBounds(le - tam - getHeight() - lst.get(lst.size() - 1).getWidth() - getHeight(), to, tam, getHeight());
                Reposicione();
                pt.setLado(2);
                to = getTop() + getHeight() / 2 - 1;
                pt.setLocation(getLeftWidth() - (pt.getWidth() / 2) + recuo, to);
            } else {
                SetBounds(le - tam / 2, to, tam, getHeight());
                Reposicione();
                pt.setLado(1);
                to = getTop() + getHeight() / 2 - 1;
                pt.setCentro(getLeft() + (getWidth() / 2), to);
            }
            Reenquadre();

            int tl = lst.size();
            int i = 0;
            int posiT = getTop() + getHeight() / 2 - 1;
            to = getTopHeight() + getHeight() + getHeight() / 2;
            le = getLeft();
            maisAci = getFormaMaisEsquerda(lst);
            while (i < tl) {
                maisAci.setLocation(le - maisAci.getWidth() / 2, to);
                maisAci.ManualResized();

                pt = getPontoLigadoA(lstp, maisAci);
                opt = pt.getDono().getOutraPonta(pt);

                pt.setLado(3);
                pt.setLocation(le - opt.getWidth() / 2 + recuo, posiT);

                opt.setLado(1);
                opt.setLocation(le - opt.getWidth() / 2 + recuo, to - getHeight() / 2 + 1);

                lst.remove(maisAci);
                lstp.remove(pt);
                le = maisAci.getLeftWidth() + dist;
                maisAci = getFormaMaisEsquerda(lst);
                if (maisAci != null) {
                    le += maisAci.getWidth() / 2;
//                    maHeigth = Math.max(maHeigth, maisAci.getTopHeight());
//                    maWidth = Math.max(maWidth, maisAci.getLeftWidth());
//                    maLeft = Math.min(maLeft, maisAci.getLeft());
                }
                i++;
            }
            //</editor-fold>
        }
        HidePontos(false);
        PropagueResizeParaLigacoes();
        OrganizeLigacoes();
    }

    protected int getLarguraEapHorizontal(ArrayList<EapBarraLigacao> ja, HashMap<Forma, EapBarraLigacao> areas) {
        if (ja.indexOf(this) > -1) {
            return 0;
        }
        ja.add(this);
        ArrayList<Forma> lst = getListaDeFormasLigadas();
        Forma ma = capturePrincipal();
        lst.remove(ma);
        int tam = 0;
        int dist = getDistancia();
        boolean hor = getDirecao() == HORIZONTAL;
        if (hor) {
            for (Forma forma : lst) {
                tam += (areas.get(forma) == null ? forma.getWidth() + dist : areas.get(forma).getLarguraEapHorizontal(ja, areas));
            }
            if (getPosicao() != HCENTRO) {
                tam += ma.getWidth() + getHeight();// 2*dist;
            }
        } else {
            tam = ma.getWidth() + getDistancia() - (ma.getWidth() / 2);
            int subtam = 0;
            for (Forma forma : lst) {
                if (areas.get(forma) == null) {
                    subtam = Math.max(forma.getWidth(), subtam);
                } else {
                    subtam = Math.max(areas.get(forma).getLarguraEapHorizontal(ja, areas), subtam);
                }
            }
            tam += subtam;
        }
        return tam;
    }

    //<editor-fold defaultstate="collapsed" desc="Qual a forma mais a direita e qual a mais a esquerda e qual a distnacia delas ao 'centro' da EAP">
    /**
     * Qual a forma mais a direita e qual a mais a esquerda e qual a distnacia delas ao 'centro' da EAP
     *
     * @param areas
     * @return p.x = LEFT e p.y = Right
     */
    protected Point calcDifCentroEapHorizontal(HashMap<Forma, EapBarraLigacao> areas) {
        ArrayList<Forma> lst = getListaDeFormasLigadas();
        Forma ma = capturePrincipal();
        lst.remove(ma);
        Forma fmE = getFormaMaisEsquerda(lst);
        Forma fmD = getFormaMaisDireita(lst);
        ArrayList<EapBarraLigacao> jatmp = new ArrayList<>();

        fmE = (areas.get(fmE) == null ? fmE : areas.get(fmE).calcMaisAEsquerda(jatmp, areas));
        jatmp = new ArrayList<>();
        fmD = (areas.get(fmD) == null ? fmD : areas.get(fmD).calcMaisADireita(jatmp, areas));

        int tamE = ma.getLeft() + (ma.getWidth() / 2) - fmE.getLeft();
        int tamD = fmD.getLeftWidth() - (ma.getLeftWidth() - (ma.getWidth() / 2));

        return new Point(tamE, tamD);
    }

    protected Forma calcMaisAEsquerda(ArrayList<EapBarraLigacao> ja, HashMap<Forma, EapBarraLigacao> areas) {
        if (ja.indexOf(this) > -1) {
            return null;
        }
        ja.add(this);
        ArrayList<Forma> lst = getListaDeFormasLigadas();
        Forma ma = capturePrincipal();
        lst.remove(ma);
        for (Forma forma : lst) {
            if (areas.get(forma) != null) {
                Forma tmp = areas.get(forma).calcMaisAEsquerda(ja, areas);
                if (tmp != null) {
                    forma = tmp;
                }
            }
            if (forma.getLeft() < ma.getLeft()) {
                ma = forma;
            }
        }
        return ma;
    }

    protected Forma calcMaisADireita(ArrayList<EapBarraLigacao> ja, HashMap<Forma, EapBarraLigacao> areas) {
        if (ja.indexOf(this) > -1) {
            return null;
        }
        ja.add(this);
        ArrayList<Forma> lst = getListaDeFormasLigadas();
        Forma ma = capturePrincipal();
        lst.remove(ma);
        for (Forma forma : lst) {
            if (areas.get(forma) != null) {
                Forma tmp = areas.get(forma).calcMaisADireita(ja, areas);
                if (tmp != null) {
                    forma = tmp;
                }
            }
            if (forma.getLeftWidth() > ma.getLeftWidth()) {
                ma = forma;
            }
        }
        return ma;
    }
    //</editor-fold>

    /**
     * Finaliza o processo de organização da Eap.
     *
     * @param ja: áreas ocupadas pela Eap conforme forma principal.
     * @param areas
     * @return autura
     */
    protected int EndOrganizeEap(ArrayList<EapBarraLigacao> ja, HashMap<Forma, EapBarraLigacao> areas) {
        int retorno = 0;
        if (ja.indexOf(this) == -1) {
            releasePrinciapal();
            return retorno;
        }
        ja.remove(this);

        ArrayList<Forma> lst = getListaDeFormasLigadas();
        ArrayList<PontoDeLinha> lstp = getListaDePontosLigados();
        RemovaLinhasNaoLigadas(lstp);
        Forma maisAci = capturePrincipal();

        //Finaliza o processo!
        releasePrinciapal();
        PontoDeLinha pt = getPontoLigadoA(lstp, maisAci);

        final int dist = getDistancia();
        if (lst.size() < 2) {
            return 0;
        }
        final int recuo = -1;
        final int corecuo = 1;
        HidePontos(true);
        if (getDirecao() == VERTICAL) {
            //<editor-fold defaultstate="collapsed" desc="VERTICAL">
            final int meiopt = pt.getWidth() / 2;
            final int tamPt = pt.getWidth();

            int le = ((maisAci.getWidth() - getWidth()) / 2) + maisAci.getLeft();
            int to = maisAci.getTopHeight() + getWidth();
            PontoDeLinha opt = pt.getDono().getOutraPonta(pt);
            setLocation(le, to);
            Reposicione();
            Reenquadre();

            le = maisAci.getLeft() + (maisAci.getWidth() - tamPt) / 2;
            opt.setLado(3);
            opt.setLocation(le, maisAci.getTopHeight() - meiopt + corecuo);
            pt.setLado(1);
            pt.setLocation(le, getTop() - meiopt + recuo);
            lst.remove(maisAci);
            lstp.remove(pt);
            //int tl = lst.size();
            //int i = 0;
            int posiL = getLeft() + getWidth() / 2;
            to = maisAci.getTopHeight() + 2 * dist;
            le = getLeftWidth() + getWidth();
            while (lst.size() > 0) {
                maisAci = getFormaMaisAcima(lst);
                pt = getPontoLigadoA(lstp, maisAci);
                opt = pt.getDono().getOutraPonta(pt);

                if ((areas.get(maisAci) != null) && (areas.get(maisAci).getDirecao() == HORIZONTAL && areas.get(maisAci).getPosicao() == HDIREITA)) {
                    ArrayList<EapBarraLigacao> jatmp = new ArrayList<>();
                    int letmp = areas.get(maisAci).getLarguraEapHorizontal(jatmp, areas);
                    le += letmp - maisAci.getWidth();
                }

                maisAci.setLocation(le, to);
                maisAci.ManualResized();
                int oldTO = to;
                to = maisAci.getTop() + (maisAci.getHeight() - tamPt) / 2;

                if (getTopHeight() < to) {
                    setHeight(to - getTop() + meiopt);
                    Reposicione();
                    Reenquadre();
                }

                pt.setLado(2);
                pt.setLocation(posiL, to);

                opt.setLado(0);
                opt.setLocation(maisAci.getLeft() - meiopt + recuo, to);

                lst.remove(maisAci);
                lstp.remove(pt);

                if (areas.get(maisAci) == null) {
                    to = maisAci.getTopHeight() + (2 * dist);
                } else if ((areas.get(maisAci).getDirecao() == HORIZONTAL) && (areas.get(maisAci).getPosicao() == HCENTRO)) {
                    ArrayList<EapBarraLigacao> jatmp = new ArrayList<>();
                    jatmp.addAll(ja);
                    to = areas.get(maisAci).EndOrganizeEap(ja, areas);
                    Point p = areas.get(maisAci).calcDifCentroEapHorizontal(areas);

                    if (p.x != p.y) {
                        le += p.x - (maisAci.getWidth() / 2);
                        maisAci.setLocation(le, oldTO);
                        maisAci.ManualResized();
                        areas.get(maisAci).EndOrganizeEap(jatmp, areas);
                    }
                } else {
                    to = areas.get(maisAci).EndOrganizeEap(ja, areas);
                }

                le = getLeftWidth() + getWidth();
            }
            if (getTopHeight() > to) {
                int tmp = to - getTop() + meiopt;
                setHeight(Math.max(tmp, LARG_ALT));
                Reposicione();
            }
            retorno = to;
            //</editor-fold>
        } else {
            //<editor-fold defaultstate="collapsed" desc="HORIZONTAL">
            final int meiopt = pt.getHeight() / 2;

            int le = (maisAci.getLeft() + (maisAci.getWidth()) / 2) + meiopt;
            int to = maisAci.getTopHeight() + getHeight() + getHeight() / 2;
            lst.remove(maisAci);
            lstp.remove(pt);

            PontoDeLinha opt = pt.getDono().getOutraPonta(pt);
            opt.setLado(3);
            opt.setLocation(le - meiopt, maisAci.getTopHeight() - meiopt + corecuo);

            lst = ordeneMenor(lst);
            int tam = 0;
            HashMap<Forma, Integer> tams = new HashMap<>();
            HashMap<Forma, Integer> meios = new HashMap<>();
            Forma fmD = getFormaMaisDireita(lst);
            Forma fmE = getFormaMaisEsquerda(lst);
            for (Forma forma : lst) {
                if (areas.get(forma) == null) {
                    tam += forma.getWidth() + dist;
                } else {
                    ArrayList<EapBarraLigacao> jatmp = new ArrayList<>();
                    int tmp = areas.get(forma).getLarguraEapHorizontal(jatmp, areas);
                    tams.put(forma, tmp);
                    if (fmE == forma) {
                        switch (areas.get(forma).getPosicao()) {
                            case HESQUERDA:
                                tmp -= forma.getWidth() / 2;
                                break;
                            case HCENTRO:
                                tmp = ((tmp - dist) / 2) + dist;
                                if ((areas.get(fmE).getDirecao() == HORIZONTAL) && (areas.get(fmE).getPosicao() == HCENTRO)) {
                                    jatmp = new ArrayList<>();
                                    jatmp.addAll(ja);
                                    areas.get(fmE).EndOrganizeEap(jatmp, areas);
                                    Point p = areas.get(fmE).calcDifCentroEapHorizontal(areas);

                                    int x = 0;
                                    if (p.x != p.y) {
                                        x = (p.y - p.x) / 2;
                                    }
                                    tmp += x;
                                    meios.put(fmE, x);
                                }

                                break;
                            case HDIREITA:
                                tmp = forma.getWidth() / 2 + dist;
                                break;
                        }
                    } else if (fmD == forma) {
                        switch (areas.get(forma).getPosicao()) {
                            case HESQUERDA:
                                tmp = forma.getWidth() / 2 + dist;
                                break;
                            case HDIREITA:
                                tmp -= forma.getWidth() / 2;
                                break;
                            case HCENTRO:
                                tmp = ((tmp - dist) / 2) + dist;

                                if ((areas.get(fmD).getDirecao() == HORIZONTAL) && (areas.get(fmD).getPosicao() == HCENTRO)) {
                                    jatmp = new ArrayList<>();
                                    jatmp.addAll(ja);
                                    areas.get(fmD).EndOrganizeEap(jatmp, areas);
                                    Point p = areas.get(fmD).calcDifCentroEapHorizontal(areas);
                                    int x = 0;
                                    if (p.x != p.y) {
                                        x = (p.x - p.y) / 2;
                                    }
                                    tmp += x;
                                    meios.put(fmD, x);
                                }
                                break;
                        }
                    }
                    tam += tmp;
                }
            }
            tam = tam - dist;
            if (lst.size() > 1) {
                if (areas.get(fmE) == null) {
                    tam -= fmE.getWidth() / 2;
                }
                if (areas.get(fmD) == null) {
                    int tmp = fmD.getWidth() / 2;
                    tam -= tmp;
                }
                tam -= 1;
            } else {
                tam = maisAci.getWidth();
            }
            tam = Math.max(tam, LARG_ALT);
            switch (getPosicao()) {
                case HESQUERDA:
                    SetBounds(le + fmE.getWidth() + getHeight(), to, tam, getHeight());
                    Reposicione();
                    pt.setLado(0);
                    to = getTop() + getHeight() / 2 - 1;
                    pt.setLocation(getLeft() - pt.getWidth() / 2 + recuo, to);
                    break;
                case HDIREITA:
                    SetBounds(le - tam - getHeight() - fmD.getWidth() - getHeight(), to, tam, getHeight());
                    Reposicione();
                    pt.setLado(2);
                    to = getTop() + getHeight() / 2 - 1;
                    pt.setLocation(getLeftWidth() - (pt.getWidth() / 2) + recuo, to);
                    break;
                default:
                    SetBounds(le - tam / 2, to, tam, getHeight());
                    Reposicione();
                    pt.setLado(1);
                    to = getTop() + getHeight() / 2 - 1;
                    pt.setCentro(getLeft() + (getWidth() / 2), to);
                    break;
            }
            Reenquadre();

            int posiT = getTop() + getHeight() / 2 - 1;
            to = getTopHeight() + getHeight() + getHeight() / 2;
            le = getLeft();
            int larg = 0;
            maisAci = getFormaMaisEsquerda(lst);
            le -= maisAci.getWidth() / 2;
            retorno = maisAci.getTopHeight();
            while (lst.size() > 0) {
                maisAci.setLocation(le, to);
                maisAci.ManualResized();
                retorno = Math.max(retorno, maisAci.getTopHeight());

                int meio = maisAci.getWidth() / 2;

                pt = getPontoLigadoA(lstp, maisAci);
                opt = pt.getDono().getOutraPonta(pt);

                pt.setLado(3);
                pt.setLocation(le + meio - opt.getWidth() / 2 + recuo, posiT);

                opt.setLado(1);
                opt.setLocation(le + meio - opt.getWidth() / 2 + recuo, to - getHeight() / 2 + 1);

                lst.remove(maisAci);
                lstp.remove(pt);

                le = maisAci.getLeftWidth() + dist;
                larg = 0;
                if (areas.get(maisAci) != null) {
                    retorno = Math.max(retorno, areas.get(maisAci).EndOrganizeEap(ja, areas) - (2 * dist));
                    larg = tams.get(maisAci) - dist;
                    if (areas.get(maisAci).getDirecao() == VERTICAL) {
                        larg = larg - maisAci.getWidth();
                    } else {
                        switch (areas.get(maisAci).getPosicao()) {
                            case HCENTRO:
                                larg = ((larg - maisAci.getWidth()) / 2);
                                if (maisAci == fmE) {
                                    larg += meios.get(fmE);
                                }
                                break;
                            case HDIREITA:
                                larg = 0;
                                break;
                            case HESQUERDA:
                                larg = (larg - maisAci.getWidth());
                                break;
                        }
                    }
                }
                le += larg;
                maisAci = getFormaMaisEsquerda(lst);
                larg = 0;
                if (areas.get(maisAci) != null) {
                    larg = tams.get(maisAci) - dist;
                    if (areas.get(maisAci).getDirecao() == VERTICAL) {
                        larg = larg - maisAci.getWidth();
                    } else {
                        switch (areas.get(maisAci).getPosicao()) {
                            case HCENTRO:
                                larg = (larg - maisAci.getWidth()) / 2;
                                if (maisAci == fmD) {
                                    larg += meios.get(fmD);
                                }
                                break;
                            case HDIREITA:
                                larg = larg - (maisAci.getWidth());
                                break;
                            case HESQUERDA:
                                larg = 0;
                                break;
                        }
                    }
                }
                le += larg;
            }
            retorno += (2 * dist);
            //</editor-fold>
        }
        HidePontos(false);
        PropagueResizeParaLigacoes();
        OrganizeLigacoes();
        return retorno;
    }

    private transient Forma principal = null;

    public Forma capturePrincipal() {
        if (principal == null) {
            ArrayList<Forma> lst = getListaDeFormasLigadas();
            principal = getFormaMaisAcima(lst);
        }
        return principal;
    }

    public void releasePrinciapal() {
        principal = null;
    }

    public void OrganizeEap() {
        boolean bkp = getMaster().isCarregando;
        getMaster().isCarregando = true;
        capturePrincipal();
        PreOrganizeEap();
        releasePrinciapal();
        getMaster().isCarregando = bkp;
        getMaster().repaint();
        DoMuda();
    }

    /**
     * Organiza todo o Eap, inclusive as EaPs folha. Caso seja uma Eap folha (subnível), sobe até a Eap principal e organiza tudo.
     */
    public void FullOrganizeEap() {
        if (getMaster().IsMultSelecionado()) {
            return;
        }
        boolean bkp = getMaster().isCarregando;
        getMaster().isCarregando = true;
        final ArrayList<EapBarraLigacao> ja = new ArrayList<>();
        getAllEap(ja);
        ja.stream().forEach(eap -> {
            eap.releasePrinciapal();
            eap.capturePrincipal();
        });

        final ArrayList<Forma> maximas = new ArrayList<>();
        final HashMap<Forma, EapBarraLigacao> areas = new HashMap<>();
        ja.stream().forEach(eap -> {
            eap.PreOrganizeEap();
            Forma maisAci = eap.capturePrincipal();
            maximas.add(maisAci);
            areas.put(maisAci, eap);
        });

        List<Forma> maximasOrd = maximas.stream().sorted((f1, f2) -> {
            return Integer.compare(f1.getTop(), f2.getTop());
        }).collect(Collectors.toList());

        EapBarraLigacao prin = areas.get(maximasOrd.get(0));
        for (int i = 0; i < maximasOrd.size(); i++) {
            if (maximasOrd.get(i).getListaDePontosLigados().size() == 1) {
                prin = areas.get(maximasOrd.get(i));
                break;
            }
        }
        prin.EndOrganizeEap(ja, areas);
        getMaster().isCarregando = bkp;
        getMaster().repaint();
        DoMuda();
    }

    /**
     * Pega todas as Eaps ligadas entre si (subníveis e principal).
     *
     * @param ja: eaps que já foram pegadas.
     */
    protected void getAllEap(ArrayList<EapBarraLigacao> ja) {
        ja.add(this);
        final ArrayList<EapBarraLigacao> lst = new ArrayList<>();
        getListaDeFormasLigadas().stream().forEach(f -> {
            lst.addAll(f.getListaDeFormasLigadas().stream().filter(b -> (b instanceof EapBarraLigacao) && (ja.indexOf(b) == -1)).map(b -> (EapBarraLigacao) b).collect(Collectors.toList()));
        });
        lst.forEach(eap -> {
            eap.getAllEap(ja);
        });
    }

//    protected Forma getFormaPrincipal(EapBarraLigacao eap, HashMap<Forma, EapBarraLigacao> lst) {
//        if (lst.isEmpty()) {
//            return null;
//        }
//        for (Forma forma : lst.keySet()) {
//            if (lst.get(forma) == eap) {
//                return forma;
//            }
//        }
//        return null;
//    }
//
//    private Forma getFormaPrincipal(HashMap<Forma, EapBarraLigacao> lst) {
//        return getFormaPrincipal(this, lst);
//    }
    private Forma getFormaMaisAcima(ArrayList<Forma> lst) {
        if (lst.isEmpty()) {
            return null;
        }
        Forma res = lst.get(0);
        for (Forma forma : lst) {
            if (forma.getTop() < res.getTop()) {
                res = forma;
            }
        }
        return res;
    }

    private Forma getFormaMaisEsquerda(ArrayList<Forma> lst) {
        if (lst.isEmpty()) {
            return null;
        }
        Forma res = lst.get(0);
        for (Forma forma : lst) {
            if (forma.getLeft() < res.getLeft()) {
                res = forma;
            }
        }
        return res;
    }

    private Forma getFormaMaisDireita(ArrayList<Forma> lst) {
        if (lst.isEmpty()) {
            return null;
        }
        Forma res = lst.get(0);
        for (Forma forma : lst) {
            if (forma.getLeft() > res.getLeft()) {
                res = forma;
            }
        }
        return res;
    }

    private PontoDeLinha getPontoLigadoA(ArrayList<PontoDeLinha> lstp, Forma maisAci) {
        for (PontoDeLinha pt : lstp) {
            if (pt.getDono().getOutraPonta(this) == maisAci) {
                return pt;
            }
        }
        return null;
    }

    private void RemovaLinhasNaoLigadas(ArrayList<PontoDeLinha> lstp) {
        int i = 0;
        while (i < lstp.size()) {
            PontoDeLinha pt = lstp.get(i);
            if (pt.getDono().getOutraPonta(this) == null) {
                lstp.remove(pt);
            } else {
                i++;
            }
        }
    }

    private ArrayList<Forma> ordeneMenor(ArrayList<Forma> lst) {
        ArrayList<Forma> res = new ArrayList<>();
        while (!lst.isEmpty()) {
            Forma menor = getFormaMaisEsquerda(lst);
            lst.remove(menor);
            res.add(menor);
        }
        return res;
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorColor(doc, "BackColor", getBackColor()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Direcao", getDirecao()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Posicao", getPosicao()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Distancia", getDistancia()));
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        int l = util.XMLGenerate.getValorIntegerFrom(me, "Direcao");
        if (l != -1) {
            setDirecao(l);
        }
        l = util.XMLGenerate.getValorIntegerFrom(me, "Posicao");
        if (l != -1) {
            posicao = l;
        }
        l = util.XMLGenerate.getValorIntegerFrom(me, "Distancia");
        if (l != -1) {
            setDistancia(Math.min(LARG_ALT, l));
        }
        Color c = util.XMLGenerate.getValorColorFrom(me, "BackColor");
        if (c != null) {
            setBackColor(c);
        }

        return true;
    }

    @Override
    public void DoAnyThing(int Tag) {
        if (Tag == CONST_ORG_FULL) {
            FullOrganizeEap();
        } else {
            OrganizeEap();
        }
    }
}
