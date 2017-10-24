/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.formas;

import desenho.Elementar;
import desenho.FormaElementar;
import desenho.ElementarListener;
import desenho.PontoElementar;
import controlador.Editor;
import controlador.Diagrama;
import controlador.apoios.TreeItem;
import controlador.inspector.InspectorProperty;
import desenho.linhas.Linha;
import desenho.linhas.PontoDeLinha;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import util.Constantes;
import util.DesenhadorDeTexto;
import util.Utilidades;

/**
 *
 * @author Rick
 */
public class Forma extends FormaElementar {

    private static final long serialVersionUID = 632178864799887969L;

    public Forma(Diagrama diagrama) {
        super(diagrama);

        this.Pontos = new PontoElementar[8];
        InitPontos();

        SetBounds(10, 10, 150, 100);
        setBackColor(new Color(255, 0, 0, 128));
    }

    private void InitPontos() {
        PontoElementar[] pontos = getPontos();
        for (int i = 0; i < pontos.length; i++) {
            pontos[i] = new PontoDeForma(this);
        }
    }

    public Forma(Diagrama diagrama, String texto) {
        this(diagrama);//super(diagrama);
        this.SetTexto(diagrama.Nomeie(texto));
    }

    // <editor-fold defaultstate="collapsed" desc="Campos">
    private PontoElementar[] Pontos;

    public PontoElementar[] getPontos() {
        return Pontos;
    }

    public void setPontos(PontoElementar[] Pontos) {
        this.Pontos = Pontos;
    }
    private transient boolean overMe = false;

    public boolean isOverMe() {
        return overMe;
    }

    @Override
    public void setOverMe(boolean overMe) {
        if (overMe != this.overMe) {
            this.overMe = overMe;
            InvalidateArea();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Texto">
    private String Texto = "";

    public String getTexto() {
        return Texto;
    }

    public void setTexto(String Texto) {
        SetTexto(Texto);
        InvalidateArea();
    }
    private DesenhadorDeTexto TextoFormatado;

    public DesenhadorDeTexto getTextoFormatado() {
        if (TextoFormatado == null) {
            TextoFormatado = new DesenhadorDeTexto(getTexto(), getFont(), isCentrarTexto());
        }
        return TextoFormatado;
    }

    //apenas para forçar a criação de um novo texto formatado!
    public void setTextoFormatado(DesenhadorDeTexto textoFormatado) {
        this.TextoFormatado = textoFormatado;
    }

    /**
     * Seta o valor do texto sem realizar a repintura
     *
     * @param Texto
     */
    public void SetTexto(String Texto) {
        if (this.Texto.equals(Texto)) {
            return;
        }
        this.Texto = Texto;
        getTextoFormatado().setTexto(Texto);
    }
    private String observacao = "";
    private String textoAdicional = "";

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        if (!this.observacao.equals(observacao)) {
            this.observacao = observacao;
        }
    }

    public String getTextoAdicional() {
        return textoAdicional;
    }

    public void setTextoAdicional(String textoAdicional) {
        if (!this.textoAdicional.equals(textoAdicional)) {
            this.textoAdicional = textoAdicional;
        }
    }

    @Override
    public void setFont(Font font) {
        getTextoFormatado().setFont(font);
        super.setFont(font);
    }

    protected boolean isCentrarTexto() {
        return true;
    }
    // </editor-fold>

//    /**
//     * Centraliza ou não o texto na Base
//     */
//    public void setCentrarTextox(boolean CentrarTexto) {
//        if (this.CentrarTextox == CentrarTexto) {
//            return;
//        }
//        this.CentrarTextox = CentrarTexto;
//        InvalidateArea();
//        DoMuda();
//    }
    // <editor-fold defaultstate="collapsed" desc="Ligaçoes">
    //protected int qtdLigacoes = 0;
//    public int getQtdLigacoes() {
//        return qtdLigacoes;
//    }
//
    public void menosLigacao(PontoDeLinha aThis) {
        //qtdLigacoes--;
    }

    public void maisLigacao(PontoDeLinha aThis) {
        //qtdLigacoes++;
    }

    // </editor-fold>
    // </editor-fold>
    protected void PropagueResizeParaLigacoes() {
        SendNotificacao(Constantes.Operacao.opResize);
    }

    @Override
    public void reSetBounds() {
        PontoElementar[] pontos = getPontos();
        int espacoI = distSelecao;
        int mW = pontos[0].getWidth() + espacoI;
        int mH = pontos[0].getHeight() + espacoI;

        int H = pontos[2].getTop() - pontos[0].getTop() - (mH + espacoI);
        int W = pontos[2].getLeft() - pontos[0].getLeft() - (mW + espacoI);
        int L = pontos[0].getLeft() + mW;
        int T = pontos[0].getTop() + mH;

        if ((W < 10) || (H < 10)) {
            Reposicione();
            return;
        }

        Rectangle ret = new Rectangle(getLeft() - L, getTop() - T, getWidth() - W, getHeight() - H);
        DoFormaResize(ret);
    }

    @Override
    public void reSetBounds(int posicao, int xleft, int ytop) {

        PontoElementar[] pontos = getPontos();

        switch (posicao) {
            case 0:
                pontos[posicao].setLocation(xleft, ytop);
                break;
            case 1:
                pontos[posicao].setLocation(xleft, ytop);
                pontos[0].setTop(ytop);
                pontos[2].setLeft(xleft);
                break;
            case 2:
                pontos[posicao].setLocation(xleft, ytop);
                break;
            case 3:
                pontos[posicao].setLocation(xleft, ytop);
                pontos[2].setTop(ytop);
                pontos[0].setLeft(xleft);
                break;

            case 4:
                pontos[posicao].setTop(ytop);
                pontos[0].setTop(ytop);
                break;
            case 5:
                pontos[posicao].setLeft(xleft);
                pontos[2].setLeft(xleft);
                break;
            case 6:
                pontos[posicao].setTop(ytop);
                pontos[2].setTop(ytop);
                break;
            case 7:
                pontos[posicao].setLeft(xleft);
                pontos[0].setLeft(xleft);
                break;
        }
        reSetBounds();
    }

    @Override
    public void setSelecionado(boolean selecionado) {
        super.setSelecionado(selecionado);
        PontoElementar[] pontos = getPontos();
        if (selecionado) {
            for (int i = 0; i < pontos.length; i++) {
                pontos[i].setPosicao(i);
                pontos[i].setVisible(true);
            }
        } else {
            for (int i = 0; i < pontos.length; i++) {
                pontos[i].setVisible(false);
            }
        }
    }
    transient boolean dragging = false;

    @Override
    public void mouseDragged(MouseEvent e) {
        dragging = true;
        super.mouseDragged(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;

        boolean bkp = isRaiseMuda();
        if (bkp) {
            setRaiseMuda(false);
        }
        super.mouseReleased(e);
        if (bkp) {
            setRaiseMuda(true);
        }

        if (jaSel) {
            boolean combine = (getMaster().isShiftDown() || getMaster().isControlDown());
            if (combine) {
                getMaster().DiagramaDoSelecao(this, true, false);
            }
        }
        Point enddown = new Point(e.getX(), e.getY());
        if (!enddown.equals(getIniDown())) {
            DoMuda();
        }
    }
    transient boolean jaSel = false;

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isSelecionado()) {
            getMaster().DiagramaDoSelecao(this, true, false);
            jaSel = false;
        } else {
            jaSel = true;
        }
        super.mousePressed(e);
    }

    @Override
    public void DoPontoCor(boolean verde) {
        PontoElementar[] pontos = getPontos();
        Color cor = verde ? getMaster().getPontoCorMultSel() : getMaster().getPontoCor();
        for (PontoElementar ponto : pontos) {
            ponto.setBackColor(cor);
        }
    }

    @Override
    public void Reposicione() {
        PontoElementar[] pontos = getPontos();
        if ((pontos[0] == null)/* || (isNulo())*/) {
            return;
        }
        for (int i = 0; i < pontos.length; i++) {
            pontos[i].setPosicao(i);
        }
    }

    @Override
    public void PinteSelecao(Graphics2D g) {
        super.PinteSelecao(g);
        if (!isSelecionado() || isPontosIsHide()) {
            return;
        }
        PontoElementar[] pontos = getPontos();

        Stroke bkp = g.getStroke();

        g.setStroke(new BasicStroke(
                1f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                2f,
                new float[]{2f, 2f},
                0f));

        g.setPaint(pontos[0].getBackColor());
        Point p1 = pontos[0].getCentro();
        Point p2 = pontos[2].getCentro();
        g.drawRect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
        g.setStroke(bkp);
    }

    public void DoFormaResize(Rectangle ret) {
        if (isSelecionado()) {
            getMaster().DoFormaResize(ret);
        }
    }

    public void ReciveFormaResize(Rectangle ret) {
        int H = getHeight() - ret.height;
        int W = getWidth() - ret.width;
        if (W < 10) {
            if (!isParte()) {
                W = 10;
            }
        }
        if (H < 10) {
            if (!isParte()) {
                H = 10;
            }
        }
        SetBounds(getLeft() - ret.x, getTop() - ret.y, W, H);
        Reposicione();
        PropagueResizeParaLigacoes();
    }

    @Override
    public void DoRaizeReenquadreReposicione() {
        getMaster().DoBaseReenquadreReposicione();
    }

    @Override
    public void DoRaiseMove(int movX, int movY) {
        super.DoRaiseMove(movX, movY);
        getMaster().ReciveProcessMove(this, movX, movY);
    }

    @Override
    public void DoMove(int movX, int movY) {
        super.DoMove(movX, movY);
        SendNotificacao(new Point(movX, movY), Constantes.Operacao.opMove);
    }

    @Override
    protected void BeforeReenquadre(int movidoX, int movidoY) {
        super.BeforeReenquadre(movidoX, movidoY);
        SendNotificacao(new Point(movidoX, movidoY), Constantes.Operacao.opReenquadre);
    }

    @Override
    public void HidePontos(boolean esconde) {
        super.HidePontos(esconde);
        PontoElementar[] pontos = getPontos();
        for (PontoElementar ponto : pontos) {
            ponto.setIsHide(esconde);
        }
        InvalidateArea(getSuperArea());
    }

    public Rectangle getSuperArea() {
        int espacoL = distSelecao;
        int mW = getMaster().getPontoWidth() + espacoL;
        int mH = getMaster().getPontoHeigth() + espacoL;
        Rectangle rec = getClientRectangle().getBounds();
        rec.grow(mW, mH);
        return rec;
    }

    @Override
    public void DoPaint(Graphics2D g) {
        //g.draw(getSuperArea());
//        if (isAtualizando()) {
//            return;
//        }
        super.DoPaint(g);
        PinteTexto(g);
        //teste: g.drawString(Integer.toString(getListaDePontosLigados().size()), getLeft() + 5, getTop() + 15);

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
            g.drawLine(0, getTop(), getMaster().getWidth(), getTop());
            g.drawLine(getLeft(), 0, getLeft(), getMaster().getHeight());

            Font bkpf = g.getFont();
            Font f = getMaster().getFont();
            g.setFont(new Font(f.getName(), Font.ITALIC, f.getSize() - 2));
            g.drawString("[" + String.valueOf(getLeft()) + ","
                    + String.valueOf(getTop()) + "]", getLeft() + 4, getTop() - 4);
            g.setFont(bkpf);
            g.setStroke(bkp);
            g.setPaint(bkpP);

        }
        if (isOverMe()) {
            DoPaintDoks(g);
        }
    }

    protected void DoPaintDoks(Graphics2D g) {
        Rectangle rec = Utilidades.Grow(getBounds(), 1, 1, -1);
        //rec.grow(-2, -2);
        Paint bkpP = g.getPaint();
        g.setPaint(Color.yellow);
        //g.drawRect(rec.x, rec.y, rec.width -2, rec.height -2);
        g.drawRect(rec.x, rec.y, rec.width, rec.height);
        g.setPaint(bkpP);
    }

    private transient double z = 0.0;

    /**
     * Pinta a texto formatado no Paint
     * <br/>--Meu padrão para DoPaint
     *
     * @param g
     */
    public void PinteTexto(Graphics2D g) {
        //no caso de mudança no zoom, um novo TextoFormatado deve ser criado.
        if (getMaster().getZoom() != z) {
            TextoFormatado = null;
            z = getMaster().getZoom();
        }
        getTextoFormatado().PinteTexto(g, getForeColor(), getArea(), getTexto());
    }

    /**
     * Área quadrada da Base a ser efetivamente usada.
     *
     * @return
     */
    public Rectangle getArea() {
        return getBounds();
    }

    public void PontoReciveDblClick(PontoDeLinha ponto, MouseEvent e) {

    }

    @Override
    public void ReSized() {
        super.ReSized();
        needRecalPts = true;
    }

    // <editor-fold defaultstate="collapsed" desc="Posicionador de Pontos">
    //Mudado a cada resize!
    protected boolean needRecalPts = true;
    protected Point pontoPosi4, pontoPosi5, pontoPosi6, pontoPosi7;
    protected Point pontoPosi0, pontoPosi1, pontoPosi2, pontoPosi3;

    /**
     * Super método tem a função de manter as medidas laterais da Forma atualizadas (evitando o overhead) Estrutura (Posições): 0----4-----1 | | 7 5 | | 3----6-----2 Nota: Pontos normais: 0 .. 3,
     * colaterais: 4 .. 7
     */
    protected void calculePontos() {
        if (!needRecalPts) {
            return;
        }
        Rectangle r = new Rectangle(getLeft(), getTop(), getWidth(), getHeight()); //getBounds();
        pontoPosi4 = new Point(r.x + r.width / 2, r.y); //lado 1
        pontoPosi5 = new Point(r.x + r.width, r.y + r.height / 2); //lado 2
        pontoPosi6 = new Point(r.x + r.width / 2, r.y + r.height); // lado 3
        pontoPosi7 = new Point(r.x, r.y + r.height / 2); // lado 0

        pontoPosi0 = new Point(pontoPosi7.x, pontoPosi4.y);
        pontoPosi1 = new Point(pontoPosi5.x, pontoPosi4.y);
        pontoPosi2 = new Point(pontoPosi5.x, pontoPosi6.y);
        pontoPosi3 = new Point(pontoPosi7.x, pontoPosi6.y);

        needRecalPts = false;
    }

    public Point[] getPontosColaterais() {
        calculePontos();
        return new Point[]{pontoPosi7, pontoPosi4, pontoPosi5, pontoPosi6};
    }

    public Point[] getPontosLaterais() {
        calculePontos();
        return new Point[]{pontoPosi0, pontoPosi1, pontoPosi2, pontoPosi3};
    }

    public Point[] getPontosCalculados() {
        calculePontos();
        return new Point[]{pontoPosi7, pontoPosi4, pontoPosi5, pontoPosi6, pontoPosi0, pontoPosi1, pontoPosi2, pontoPosi3};
    }

    /**
     * Super método tem a função de colocar o ponto do lado certo da Forma Estrutura (Posições): +----1-----+ | | 0 2 | | +----3-----+ Nota: Pontos normais: 0 .. 3, colaterais: 4 .. 7
     *
     * @param ponto
     */
    public void PosicionePonto(PontoDeLinha ponto) {

        Point centro = ponto.getCentro();

        int mx = retorneProximidade(centro);

        int recuo = -1;
        //int corecuo = 1;

        switch (mx) {
            case 0:
                ponto.setLado(0);
                ponto.setLeft(getLeft() - ponto.getWidth() / 2 + recuo);
                break;
            case 1:
                ponto.setLado(1);
                ponto.setTop(getTop() - ponto.getHeight() / 2 + recuo);
                break;
            case 2:
                ponto.setLado(2);
                ponto.setLeft(getLeftWidth() - ponto.getWidth() / 2 /*+ corecuo*/);
                break;
            case 3:
                ponto.setLado(3);
                ponto.setTop(getTopHeight() - ponto.getHeight() / 2 /*+ corecuo*/);
                break;
        }

    }

    public int retorneProximidade(Point centro) {
        Rectangle r = new Rectangle(getLeft(), getTop(), getWidth(), getHeight()); //getBounds();
        int[] vl = new int[4];
        vl[0] = centro.x - r.x;
        vl[2] = r.x + r.width - centro.x;
        vl[1] = centro.y - r.y;
        vl[3] = r.y + r.height - centro.y;
        int mx = 0;
        for (int i = 1; i < 4; i++) {
            if (vl[mx] > vl[i]) {
                mx = i;
            }
        }
        return mx;
    }

    // </editor-fold>
    public void OrganizeLigacoes() {
        SendNotificacao(Constantes.Operacao.opOrganizeLigacoes);
    }

    public boolean CanLiga(PontoDeLinha aThis) {
        Linha l = aThis.getDono();
        return CanLiga(l.getOutraPonta(aThis.getEm()), l);
    }

    public boolean CanLiga(Forma forma, Linha lin) {
        if (forma == this) {
            return false;
        }
        return true;
    }

    public ArrayList<PontoDeLinha> getListaDePontosLigados() {
        ArrayList<PontoDeLinha> res = new ArrayList<>();
        List<ElementarListener> lst = getListeners();
        if (lst != null) {
            for (ElementarListener el : lst) {
                //if (el instanceof PontoDeLinha) { //alterado em 27/07/2014 - realmente está ligado?
                if (el instanceof PontoDeLinha && ((PontoDeLinha) el).getEm() == this) {
                    res.add((PontoDeLinha) el);
                }
            }
        }
        return res;
    }

    public List<Linha> getListaDeLigacoes() {
        return getListaDePontosLigados().stream().map(p -> p.getDono()).collect(Collectors.toList());
    }

    public ArrayList<Forma> getListaDeFormasLigadas(Forma exceto) {
        if (exceto == null) {
            return getListaDeFormasLigadas();
        }
        ArrayList<PontoDeLinha> pontos = getListaDePontosLigados();
        ArrayList<Forma> outrasPontas = new ArrayList<>();
        for (PontoDeLinha pt : pontos) {
            Forma op = pt.getDono().getOutraPonta(this);
            if (op != null && op != exceto && outrasPontas.indexOf(op) == -1) {
                outrasPontas.add(op);
            }
        }
        return outrasPontas;
    }

    /**
     * Permite duplicidade no retorno das forms ligadas ao contrário da getListaDeFormasLigadas
     *
     * @param destaClasse
     * @return
     */
    public ArrayList<Forma> getListaDeFormasLigadasNaoExclusiva(Class destaClasse) {
        ArrayList<PontoDeLinha> pontos = getListaDePontosLigados();
        ArrayList<Forma> outrasPontas = new ArrayList<>();
        for (PontoDeLinha pt : pontos) {
            Forma op = pt.getDono().getOutraPonta(this);
            if (op != null) {
                if (destaClasse.isAssignableFrom(op.getClass())) {
                    outrasPontas.add(op);
                }
            }
        }
        return outrasPontas;
    }

    public ArrayList<Forma> getListaDeFormasLigadas(Class destaClasse) {
        ArrayList<PontoDeLinha> pontos = getListaDePontosLigados();
        ArrayList<Forma> outrasPontas = new ArrayList<>();
        for (PontoDeLinha pt : pontos) {
            Forma op = pt.getDono().getOutraPonta(this);
            if (op != null) {
                if (destaClasse.isAssignableFrom(op.getClass())) {
                    if (outrasPontas.indexOf(op) == -1) {
                        outrasPontas.add(op);
                    }
                }
            }
        }
        return outrasPontas;
    }

    public ArrayList<Forma> getListaDeFormasLigadas() {
        ArrayList<PontoDeLinha> pontos = getListaDePontosLigados();
        ArrayList<Forma> outrasPontas = new ArrayList<>();
        for (PontoDeLinha pt : pontos) {
            Forma op = pt.getDono().getOutraPonta(this);
            if (op != null && outrasPontas.indexOf(op) == -1) {
                outrasPontas.add(op);
            }
        }
        return outrasPontas;
    }

//    public void getListaDeFormasLigadas(ArrayList<Forma> resu) {
//        ArrayList<PontoDeLinha> pontos = getListaDePontosLigados();
//        ArrayList<Forma> outrasPontas = new ArrayList<Forma>();
//        for (PontoDeLinha pt: pontos) {
//            Forma op = pt.getDono().getOutraPonta(this);
//            if (op != null && resu.indexOf(op) == -1) {
//                outrasPontas.add(op);
//                resu.add(op);
//            }
//        }
//        for (Forma f: outrasPontas) {
//            f.getListaDeFormasLigadas(resu);
//        }
//    }
    // <editor-fold defaultstate="collapsed" desc="Composto">
    @Override
    public Elementar IsMeOrMine(Point p) {
        Elementar res = super.IsMeOrMine(p);
        if (res != null) {
            if (res.isParte()) {
                return res.ProcessaComposicao();
            }
        }
        return res;
    }

    @Override
    public Elementar IsMeOrMine(Point p, Elementar nor) {
        Elementar res = super.IsMeOrMine(p, nor);
        if (res != null) {
            if (res.isParte()) {
                return res.ProcessaComposicao();
            }
        }
        return res;
    }

    @Override
    public Elementar IsMeOrMineBase(Point p, Elementar nor) {
        Elementar res = super.IsMeOrMineBase(p, nor);
        if (res != null) {
            if (res.isParte()) {
                return res.ProcessaComposicao();
            }
        }
        return res;
    }
    private Forma principal = null;

    @Override
    public Elementar ProcessaComposicao() {
        if (this.principal == null) {
            return this;
        }
        return principal;
    }

    @Override
    public Elementar getPrincipal() {
        return ProcessaComposicao();
    }

    public boolean isSubComponente() {
        return (getPrincipal() != this);
    }

    public void setPrincipal(Elementar oprincipal) {
        if (oprincipal instanceof Forma) {
            this.principal = (Forma) oprincipal;
        } else {
            this.principal = null;
        }
    }

    @Override
    public boolean isParte() {
        return (this.principal != null);
    }

    public Point getMelhorPontoDeLigacao(Point estePonto) {
        int tmp = retorneProximidade(estePonto);
        Point[] ptsLi = getPontosColaterais();
        Point res = null;
        switch (tmp) {
            case 0:
                res = new Point(ptsLi[0].x + 2, ptsLi[0].y);
                break;
            case 1:
                res = new Point(ptsLi[1].x, ptsLi[1].y + 2);
                break;
            case 2:
                res = new Point(ptsLi[2].x - 2, ptsLi[2].y);
                break;
            case 3:
                res = new Point(ptsLi[3].x, ptsLi[3].y - 2);
                break;
        }
        return res;
    }

    public void EscrevaTexto(ArrayList<String> txts) {
        txts.add(getTexto());
    }
    // </editor-fold>

    /**
     * Mudar o nome do texto para "Texto adicional" onde estaria "Dicionário"
     */
    public boolean nodic = true;

    /**
     * A fonte do componente pode ser editada.
     */
    public boolean editFonte = true;

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();

        res.add(InspectorProperty.PropertyFactorySN("ancorado", "setAncorado", isAncorado()));
        if (editFonte) {
            //# res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdFonte.name()));
            res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdFonte.name(), nomeComandos.cmdFonte.name().toLowerCase(), getFont().getFontName()));
        }
        res.add(InspectorProperty.PropertyFactoryCor("forecolor", "setForeColor", getForeColor()));

        res.add(InspectorProperty.PropertyFactorySeparador("mer"));

        res.add(InspectorProperty.PropertyFactoryTexto("nome", "setTexto", getTexto()));

        res.add(InspectorProperty.PropertyFactoryTextoL("observacao", "setObservacao", getObservacao()));

        res.add(InspectorProperty.PropertyFactoryTextoL((nodic ? "textoadicional" : "dicionario"), "setTextoAdicional", getTextoAdicional()));

        return res;
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        ArrayList<Forma> lst = getListaDeFormasLigadas();
        boolean ja = false;
        int tl = 0;
        for (Forma f : lst) {
            InspectorProperty ipp = InspectorProperty.PropertyFactoryActionSelect(Editor.fromConfiguracao.getValor("diagrama." + Editor.getClassTexto(f) + ".nome"),
                    f.getTexto(),
                    String.valueOf(((FormaElementar) f.getPrincipal()).getID()));
            if (!ja) {
                ja = true;
                GP.add(InspectorProperty.PropertyFactorySeparador("ligacoes", true));
            }
            GP.add(ipp);
            tl++;
        }
        if (showOrgDiag && tl > 0) {
            GP.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "forma.organizediagrama").setTag(CONST_ORG_DIAG));
        }
        return GP;
    }

    protected final int CONST_ORG_DIAG = 70;

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == CONST_ORG_DIAG) {
            OrganizeDiagrama();
        }
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorText(doc, "Texto", getTexto()));
        me.appendChild(util.XMLGenerate.ValorText(doc, "Observacao", getObservacao()));
        me.appendChild(util.XMLGenerate.ValorText(doc, "Dicionario", getTextoAdicional()));
        me.appendChild(util.XMLGenerate.ValorFonte(doc, getFont()));
        //me.appendChild(util.XMLGenerate.ValorBoolean(doc, "CentrarTexto", isCentrarTexto()));

        //ForeColor não é default.
        if (!getForeColor().equals(Elementar.defaultColor)) {
            me.appendChild(util.XMLGenerate.ValorColor(doc, "ForeColor", getForeColor()));
        }
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Ancorado", isAncorado()));
        //me.appendChild(util.XMLGenerate.ValorColor(doc, "BackColor", getBackColor()));
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        String tmp = util.XMLGenerate.getValorTextoFrom(me, "Texto");
        if (tmp != null) {
            setTexto(tmp);
        }
        tmp = util.XMLGenerate.getValorTextoFrom(me, "Observacao");
        if (tmp != null) {
            setObservacao(tmp);
        }
        tmp = util.XMLGenerate.getValorTextoFrom(me, "Dicionario");
        if (tmp != null) {
            setTextoAdicional(tmp);
        }

        Color c = util.XMLGenerate.getValorColorFrom(me, "ForeColor");
        if (c != null) {
            setForeColor(c);
        } else {
            setForeColor(getMaster().getForeColor());
        }
        Font f = util.XMLGenerate.getValorFonte(me);
        if (f != null) {
            setFont(f);
        }

        setAncorado(util.XMLGenerate.getValorBooleanFrom(me, "Ancorado"));

        return true;
    }

    /**
     * Gera uma lista de objetos mostráveis no JTree de navegação de objetos.
     *
     * @param root - objeto raiz
     * @return true se tudo certo.
     */
    public boolean MostreSeParaExibicao(TreeItem root) {
        root.add(new TreeItem(getTexto(), getID(), this.getClass().getSimpleName()));
        return true;
    }

    public void ManualResized() {
        if (!Reenquadre()) {
            Reposicione();
        }
        PropagueResizeParaLigacoes();
    }

    //<editor-fold defaultstate="collapsed" desc="Orgnizar ligações em um Artefato">
    /**
     * Mostar esta opção no Inspector?
     */
    protected boolean showOrgDiag = false;

    public void OrganizeDiagrama() {
        OrganizeDiagrama(true, false);
        //ou: OrganizeFluxo();
    }

    /**
     * 1 2 3 <br/>
     * 0 A 4 <br/>
     * 7 6 5 <br/>
     *
     * @param A - é o A da imagem.
     * @param B
     */
    public static int MapaPosi(Forma A, Forma B) {
        Point posiA = A.getLocation();
        Point posiB = B.getLocation();

        //  1       2       3
        //  0       A       4
        //  7       6       5
        int dist = Math.max(A.getWidth(), B.getWidth());
        if (Math.abs(posiA.x - posiB.x) < dist) {
            //mesma coluna
            if (posiA.y > posiB.y) {
                return 2;
            } else {
                return 6;
            }
        }

        dist = Math.max(A.getHeight(), B.getHeight());
        if (Math.abs(posiA.y - posiB.y) < dist) {
            //mesma linha
            if (posiA.x > posiB.x) {
                return 0;
            } else {
                return 4;
            }
        }

        if (posiA.x < posiB.x) {
            // 3 ou 5
            if (posiA.y < posiB.y) {
                return 5;
            } else {
                return 3;
            }
        } else {
            //if (posiA.x < posiB.x) {
            // 1 ou 7
            if (posiA.y > posiB.y) {
                return 1;
            } else {
                return 7;
            }
        }
    }

    /**
     * Organiza as ligações do diagrama
     *
     * @param movA: pode mover pontos de ligação da entidade A?
     * @param movB: pode mover pontos de ligação da entidade B?
     */
    protected void OrganizeDiagrama(boolean movA, boolean movB) {
        final Forma A = this;
        A.getListaDePontosLigados().stream().map(p -> p.getDono()).forEach(L -> {
            Forma B = L.getOutraPonta(A);
            if (B == null) {
                B = A;
            }
            int sp = 0;
            int m = Forma.MapaPosi(A, B);

            //  1.      2      .3
            //  0.      A      .4
            //  7.      6      .5
            Point ptA = new Point();
            Point ptB = new Point();
            int lpA = 0;
            int lpB = 0;
            switch (m) {
                case 1:
                case 0:
                case 7:
                    ptA = new Point(A.getLeft() + sp, A.getTop() + A.getHeight() / 2);
                    ptB = new Point(B.getLeftWidth() - sp, B.getTop() + B.getHeight() / 2);
                    lpA = 0;
                    lpB = 2;
                    break;

                case 2:
                    ptA = new Point(A.getLeft() + A.getWidth() / 2, A.getTop() + sp);
                    ptB = new Point(B.getLeft() + B.getWidth() / 2, B.getTopHeight() - sp);
                    lpA = 1;
                    lpB = 3;
                    break;

                case 3:
                case 4:
                case 5:
                    ptA = new Point(A.getLeftWidth() - sp, A.getTop() + A.getHeight() / 2);
                    ptB = new Point(B.getLeft() + sp, B.getTop() + B.getHeight() / 2);
                    lpA = 2;
                    lpB = 0;
                    break;

                case 6:
                    ptA = new Point(A.getLeft() + A.getWidth() / 2, A.getTopHeight() - sp);
                    ptB = new Point(B.getLeft() + B.getWidth() / 2, B.getTop() + sp);
                    lpA = 3;
                    lpB = 1;
                    break;
            }

            if (L.getPontaA().getEm() == A) {
                if (movA) {
                    L.getPontaA().setLado(lpA);
                    L.getPontaA().setCentro(ptA);
                }
                if (movB) {
                    L.getPontaB().setLado(lpB);
                    L.getPontaB().setCentro(ptB);
                }

            } else {
                if (movB) {
                    L.getPontaA().setLado(lpB);
                    L.getPontaA().setCentro(ptB);
                }
                if (movA) {
                    L.getPontaB().setLado(lpA);
                    L.getPontaB().setCentro(ptA);
                }
            }
        });
        organizeDiagramaRedistribuaLinhas();
        DoMuda();
    }

    /**
     * Variável que designa qual o início da área de posicionamento dos pontos.
     */
    protected int INI_ORGDIAG = 0;

    private void organizeDiagramaRedistribuaLinhas() {
        Forma tt = this;
        for (int lado = 0; lado < 4; lado++) {
            final int ld = lado;
            final boolean sn = ld % 2 == 0;
            int tl = Math.toIntExact(tt.getListaDePontosLigados().stream().filter(p -> p.getLado() == ld).count());
            if (tl == 0) {
                continue;
            }

            final int espaco = ((sn ? tt.getHeight() : tt.getWidth()) - 2 * INI_ORGDIAG) / (tl + 1);
            int ini = INI_ORGDIAG;

            if (sn) {
                ini += tt.getTop();
            } else {
                ini += tt.getLeft();
            }

            List<PontoDeLinha> ord = tt.getListaDePontosLigados().stream().filter(p -> p.getLado() == ld).sorted((p1, p2) -> {
                if (sn) {
                    return Integer.compare(p1.getDono().getOutraPonta(p1).getTop(), p2.getDono().getOutraPonta(p2).getTop());
                } else {
                    return Integer.compare(p1.getDono().getOutraPonta(p1).getLeft(), p2.getDono().getOutraPonta(p2).getLeft());
                }

            }).collect(Collectors.toList());

            for (PontoDeLinha p : ord) {
                //if (p.getLado() == l) {
                ini += espaco;
                if (sn) {
                    p.setTop(ini);
                } else {
                    p.setLeft(ini);
                }
                p.getDono().OrganizeLinha();
                p.getDono().reSetBounds();
                //}
            }
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Organiza os elementos em linha (utliza-se de showOrgDiag, OrganizeDiagrama()">
    /**
     * Organiza os elementos em linha Sobrescrever:
     *
     * public void OrganizeDiagrama() { <br/>
     *      //OrganizeDiagrama(true, false); <br/>
     * OrganizeFluxo();<br/> }
     */
    protected void OrganizeFluxo() {
        List<Forma> lst = new ArrayList<>();
        lst.add(this);
        this.getListaDeFormasLigadas().forEach(item -> {
            OrganizeFluxo(this, item, lst);
        });
    }

    protected void OrganizeFluxo(Forma origem, Forma dest, List<Forma> lstJA) {
        if (lstJA.indexOf(dest) > -1) {
            return; // já.
        }
        origem.getListaDeLigacoes().stream().filter(L -> L.getOutraPonta(origem) == dest).forEach(lin -> {
            int pa = lin.getPontaA().getLado();
            int pb = lin.getPontaB().getLado();

            boolean sim = false;

            switch (pa) {
                case 0:
                    sim = (pb == 2);
                    break;
                case 1:
                    sim = (pb == 3);
                    break;
                case 2:
                    sim = (pb == 0);
                    break;
                case 3:
                    sim = (pb == 1);
                    break;
            }

            if (sim) {
                Point PA = lin.getPontaA().getLocation();
                Point PB = lin.getPontaB().getLocation();

                int x = 0, y = 0;
                if (pa == 0 || pa == 2) {
                    y = PA.y - PB.y;
                    if (lin.getPontaB().getEm() == origem) {
                        y = PB.y - PA.y;
                    }
                } else {
                    x = PA.x - PB.x;
                    if (lin.getPontaB().getEm() == origem) {
                        x = PB.x - PA.x;
                    }
                }
                dest.DoMove(x, y);
                dest.Reenquadre();
            }
            lstJA.add(dest);

            dest.getListaDeFormasLigadas().forEach(f -> {
                OrganizeFluxo(dest, f, lstJA);
            });
        });
    }

    //</editor-fold>
    @Override
    public boolean Destroy() {
        List<Linha> lst = getListaDeLigacoes();
        boolean res = super.Destroy();
        if (getMaster().getEditor().isPropagueDeleteToLines()) {
            lst.stream().filter((L) -> (!L.isSelecionado())).forEach((L) -> {
                getMaster().Remove(L, false);
            });
        }
        return res;
    }

    /**
     * Dado um objeto, saber se ele pode ser alinhado (vertical e horizontal).
     *
     * @return SIM
     */
    public boolean isAlinhavel() {
        return true;
    }

//    @Override
//    public void setDisablePainted(boolean disablePainted) {
//        super.setDisablePainted(disablePainted);
//        if (isParte()) {
//            
//        }
//    }
    
    
}
