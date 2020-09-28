/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import controlador.apoios.GuardaPadraoBrM;
import controlador.apoios.InfoDiagrama;
import controlador.apoios.TreeItem;
import controlador.inspector.InspectorItemBase;
import controlador.inspector.InspectorItemExtender;
import controlador.inspector.InspectorProperty;
import desenho.Ancorador;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Desenhador;
import desenho.formas.Forma;
import desenho.formas.FormaTextoBase.AlinhamentoTexto;
import desenho.formas.Legenda;
import desenho.linhas.SuperLinha;
import desenho.preAnyDiagrama.PreTexto.TipoTexto;
import diagramas.conceitual.Texto;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.BoxingJava;
import util.Utilidades;
import util.XMLGenerate;

/**
 *
 * @author Rick
 */
public class Diagrama implements Serializable, ClipboardOwner {

    private static final long serialVersionUID = 21212121212121L;

    /**
     * Classes existentes.
     */
    private final Class[] classesDoDiagrama = new Class[]{};

    public Class[] getCassesDoDiagrama() {
        return classesDoDiagrama;
    }
    // <editor-fold defaultstate="collapsed" desc="Campos">
    protected transient Editor master;
    private int baseRecuo = 3;
    private boolean draging = false;
    protected Color pontoCor = Color.BLACK;
    protected Color pontoCorMultSel = Color.GREEN;
    protected FormaElementar infoDiagrama = null;
    public static final String VERSAO_A = "3";
    public static final String VERSAO_B = "2";
    public static final String VERSAO_C = "0";
    protected String versaoA = Diagrama.VERSAO_A;
    protected String versaoB = Diagrama.VERSAO_B;
    protected String versaoC = Diagrama.VERSAO_C;
    private TipoDeDiagrama tipo = TipoDeDiagrama.tpConceitual;
    transient private String nome;
    transient private String Arquivo = "";
    //mostra uma grade no modelo.
    private Font font;
    private Color foreColor = Elementar.defaultColor;
    private int heigth = 4096;
    private int width = 4096;
    private double zoom = 1.0;
    //private Color background = Color.WHITE;
    private Ancorador superAncorador = null;

    public Cursor getCursor() {
        return this.master.getBox().getCursor();
    }

    public void setCursor(Cursor cursor) {
        if (getComando() != null) {
            this.master.getBox().setCursor(getEditor().getControler().MakeCursor(getComando()));
            return;
        }
        this.master.getBox().setCursor(cursor);
    }

    /**
     * @return the pontoWidth
     */
    public int getPontoWidth() {
        return master.getBox().getPontoWidth();
    }

    /**
     * @return the pontoHeigth
     */
    public int getPontoHeigth() {
        return master.getBox().getPontoHeigth();
    }

    public Color getBackground() {
        return master.getBox().getBackground();
    }

//    public void setBackground(Color getBackground) {
//        this.background = getBackground;
//    }
    public Color getForeColor() {
        return foreColor;
    }

    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public int getHeight() {
        return heigth;
    }

    public int getWidth() {
        return width;
    }
    /**
     * Ao verificar-se o total de itens em um Diagrama, para saber se algum foi incluído, é preciso exluir do total aqueles que fazem parte do próprio objto. Até então apenas o InfoDiagrama.
     */
    public static final int totalInicialDeItens = 1;

    public String getArquivo() {
        return Arquivo;
    }

    public void setArquivo(String Arquivo) {
        this.Arquivo = Arquivo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
        if (master != null) {
            master.RePopuleBarraDiagramas(false);
        }
    }

    public void SetNome(String nome) {
        this.nome = nome;
    }

    public TipoDeDiagrama getTipo() {
        return tipo;
    }

    protected void setTipo(TipoDeDiagrama tipo) {
        this.tipo = tipo;
    }

    public String getTipoDeDiagramaFormatado() {
//        if (getTipo() == TipoDeDiagrama.tpConceitual || getTipo() == TipoDeDiagrama.tpFisico || getTipo() == TipoDeDiagrama.tpLogico) {
//            return Editor.fromConfiguracao.getValor("Inspector.obj.modelo.tipo") +  " " + Editor.fromConfiguracao.getValor("Inspector.lst.tipomodelo." + getTipo().name().substring(2).toLowerCase());
//        }
        return Editor.fromConfiguracao.getValor("Inspector.lst.tipodiagrama." + getTipo().name().substring(2).toLowerCase());
    }

    public String getVersao() {
        return versaoA + "." + versaoB + "." + versaoC;
    }

    public boolean isMostrarInfoDiagrama() {
        return infoDiagrama.isVisible();
    }

    public void setMostrarInfoDiagrama(boolean mostrarInfoDiagrama) {
        this.infoDiagrama.setVisible(mostrarInfoDiagrama);
    }

    public Editor getEditor() {
        return master;
    }

    public Color getPontoCorMultSel() {
        return pontoCorMultSel;
    }

    public void setPontoCorMultSel(Color pc) {
        this.pontoCorMultSel = pc;
    }

    public Color getPontoCor() {
        return pontoCor;
    }

    public void setPontoCor(Color pontoCor) {
        this.pontoCor = pontoCor;
    }

    /**
     * @return the editorBackColor
     */
    public final Color getEditorBackColor() {
        return master.getBox().getBackground();
    }

    /**
     * @return the baseRecuo
     */
    public int getBaseRecuo() {
        return baseRecuo;
    }

    /**
     * @param baseRecuo the baseRecuo to set
     */
    public void setBaseRecuo(int baseRecuo) {
        this.baseRecuo = baseRecuo;
    }
    protected ArrayList<Elementar> subItens = new ArrayList<>();

    public ArrayList<Elementar> getSubItens() {
        return subItens;
    }
    protected ArrayList<FormaElementar> ListaDeItens = new ArrayList<>();

    public ArrayList<FormaElementar> getListaDeItens() {
        return ListaDeItens;
    }
    private ArrayList<FormaElementar> itensSelecionados = new ArrayList<>();

    public ArrayList<FormaElementar> getItensSelecionados() {
        return itensSelecionados;
    }
    // </editor-fold>

    public Diagrama(Editor omaster) {
        super();
        master = omaster;
        font = Elementar.CloneFont(this.master.getBox().getFont());
        isLoadCreate = true;
        infoDiagrama = new InfoDiagrama(this);
        infoDiagrama.SetBounds(5, 5, 300, 150);
        isLoadCreate = false;
        heigth = master.getBox().EditorMaxHeigth;
        width = master.getBox().EditorMaxWidth;

        meusComandos.add(Controler.Comandos.cmdDesenhador.name());
        meusComandos.add(Controler.Comandos.cmdLegenda.name());
        meusComandos.add(Controler.Comandos.cmdTexto.name());
        meusComandos.add(Controler.Comandos.cmdApagar.name());
        nome = "";

        superAncorador = new Ancorador(this);
        superAncorador.SetBounds(50, 50, 18, 36);
    }

    public final ArrayList<String> meusComandos = new ArrayList<>();

    public void setMaster(Editor master) {
        this.master = master;
    }
    int _tick = 0;

    private void tick() {
        _tick++;
    }

    /**
     * @return the zoom
     */
    public double getZoom() {
        return zoom;
    }

    /**
     * @param zoom the zoom to set
     */
    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    private void PinteGrade(Graphics2D g) {
        int w = master.getGridWidth();
        int gW = (getWidth() / w) + 1;
        int gH = (getHeight() / w) + 1;

        int ww = getWidth();
        int hh = getHeight();

        Paint bkppaint = g.getPaint();
        g.setColor(new Color(241, 246, 251));

        for (int i = 1; i < gW; i++) {
            g.drawLine(w * i, 0, w * i, hh);
        }

        for (int i = 1; i < gH; i++) {
            g.drawLine(0, w * i, ww, w * i);
        }

        g.setPaint(bkppaint);
    }

    /**
     * Mostra a área de impressão no diagrama
     *
     * @param g
     * @param wdt largura
     * @param ht altura
     */
    public void PaintAI(Graphics2D g, int wdt, int ht) {
        Paint bkppaint = g.getPaint();
        g.setColor(new Color(221, 221, 221));
        int w = wdt;
        while (w < getWidth()) {
            g.drawLine(w, 1, w, getHeight() - 1);
            w += wdt;
        }
        int h = ht;
        while (h < getHeight()) {
            g.drawLine(1, h, getWidth() - 1, h);
            h += ht;
        }
        g.setPaint(bkppaint);
    }

    /**
     * Não deve ser salvo. Trata-se de uma lista de elementos visíveis para criar uma navegação
     */
    private transient TreeItem TreeNavegacao = null;

    /**
     * Atualiza uma lista de elementos visíveis para criar uma navegação.
     *
     * @param atualisar a lista: (sim | não)
     * @return
     */
    public TreeItem AtualizeTreeNavegacao(boolean atualisar) {
        if (atualisar || TreeNavegacao == null) {
            TreeNavegacao = new TreeItem(getNomeFormatado());
            getListaDeItens().stream().filter((it) -> (it instanceof Forma)).map(it -> (Forma) it).forEach((it) -> {
                it.MostreSeParaExibicao(TreeNavegacao);
            });
        }
        return TreeNavegacao;
    }

    /**
     * Trata-se de uma lista de elementos visíveis para criar uma navegação
     *
     * @return [lista de elementos]
     */
    public TreeItem getTreeNavegacao() {
        return AtualizeTreeNavegacao(false);
    }

    public void SelecioneByID(int i, boolean mostrar) {
        FormaElementar e = FindByID(i);
        if (e == null) {
            master.AtualizeTreeNavegacao();
            return;
        }
        DiagramaDoSelecao(e, false, false);
        if (mostrar) {
            master.Mostre(e.getLocation());
        }
    }

    /**
     * Quando InfoDigrama gera as propriedades ele as informa ao modelo, possibilitando a inclusão de algum comando.
     *
     * @param res propriedades ainda não preenchidas
     */
    public void BeginProperty(ArrayList<InspectorProperty> res) {

    }

    /**
     * Quando InfoDigrama gera as propriedades ele as informa ao modelo, possibilitando a inclusão de algum comando.
     *
     * @param res propriedades já preenchidas
     */
    public void EndProperty(ArrayList<InspectorProperty> res) {

    }

    /**
     * Quando InfoDigrama roda o DoAnyThing() ela chama este método no Diagrama,
     *
     * @param Tag - vem do DoAnyThing do InfoDiagrama
     */
    public void DoAnyThing(int Tag) {

    }

    /**
     * Insere comandos no submenu "menuCMD" (Comandos) do menu diagrama. O menu "item" é um submenu a ser populado.
     *
     * @param item Implementado, como exemplo em DiagramaEap
     */
    public void populeComandos(JMenuItem item) {
        item.setEnabled(false);
    }

    /**
     * Roda um comando criado pelo AcaoDiagrama.
     *
     * @param comm qualquer comando em formato string. Implementado, como exemplo em DiagramaEap
     */
    public void rodaComando(String comm) {

    }

    public int getID() {
        return infoDiagrama.getID();
    }

    public String getUniversalUnicID() {
        return ((InfoDiagrama) infoDiagrama).getDiagramaUniversalUnicID();
    }

    public final void ReGeraUniversalUnicID() {
        ((InfoDiagrama) infoDiagrama).ReGeraGuardaUnicDiagramaID();
    }

    public String getNomeFormatado() {
        return getNome().isEmpty() ? "<<" + getTipoDeDiagramaFormatado() + ">>" : getNome();
    }

    /**
     * Método para setar um valor String diretamente no diagrama, disparado pelo InfoDiagrama
     *
     * @param str
     * @param tag
     */
    public void setFromString(String str, int tag) {
        //# talvez implantar o setFromInt e etc.
    }

    /**
     * Rodado após o carregamento do diagrama a aprtir de um arquivo. será ultil no futuro para setar propriedades default em novas versões dos diagramas a partir das versões do brMOdelo. roda dentro do método: ProcessePosOpen(Diagrama res) na classe Editor
     */
    public void OnAfterLoad(boolean isXml) {

    }

    public enum TipoDeDiagrama {
        tpConceitual, tpLogico, tpFluxo, tpAtividade, tpEap, tpLivre
    }

    public boolean isAlterado() {
        return (this.getListaDeItens().size() > Diagrama.totalInicialDeItens
                || this.getMudou() || !("".equals(this.getArquivo())));
    }

    // <editor-fold defaultstate="collapsed" desc="Eventos">
    public void ProcessPaint(Graphics2D Canvas) {
        //Canvas.drawString(Integer.toString(_tick), 200, 200);

        double z = master.getBox().getZoom();
        Canvas.scale(z, z);
        if (master.isShowGrid()) {
            PinteGrade(Canvas);
        }
        if (master.getBox().isMostrarAreaImpressao()) {
            PaintAI(Canvas, master.getBox().areaImpressaoWidth, master.getBox().areaImpressaoHeigth);
        }

        for (int i = subItens.size() - 1; i > -1; i--) {
            Elementar e = subItens.get(i);
            if (e.CanPaint()) {
                e.DoPaint(Canvas);
            }
        }

        superAncorador.DoPaint(Canvas);
    }

    public void ExternalPaint(Graphics g) {
        RenderingHints renderHints
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        renderHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Graphics2D Canvas = (Graphics2D) g;

        Canvas.addRenderingHints(renderHints);

        Canvas.setStroke(new BasicStroke(
                1f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        Canvas.setPaint(Color.BLACK);

        for (int i = subItens.size() - 1; i > -1; i--) {
            Elementar e = subItens.get(i);
            if (e.CanPaint()) {
                e.DoPaint(Canvas);
            }
        }
    }

    public void ExternalPaintSelecao(Graphics g) {
        RenderingHints renderHints
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        renderHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Graphics2D Canvas = (Graphics2D) g;

        Canvas.addRenderingHints(renderHints);

        Canvas.setStroke(new BasicStroke(
                1f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        Canvas.setPaint(Color.BLACK);

        for (int i = getItensSelecionados().size() - 1; i > -1; i--) {
            FormaElementar e = getItensSelecionados().get(i);
            e.HidePontos(true);
            if (e.CanPaint()) {
                e.DoPaint(Canvas);
            }
            e.HidePontos(false);
        }
    }

    /**
     * Máxima região pintada X e Y
     *
     * @return Point
     */
    public Point getPontoExtremo() {
        final int borda = 4;
        int rX = 0;
        int rY = 0;
        for (FormaElementar el : ListaDeItens) {
            rX = Math.max(rX, el.getLeftWidth());
            rY = Math.max(rY, el.getTopHeight());
        }
        if (rX > 0) {
            rX += borda;
        }
        if (rY > 0) {
            rY += borda;
        }
        return new Point(rX, rY);
    }

    /**
     * Máxima região pintada dentre os selecionados. X e Y
     *
     * @return Point
     */
    public Point getPontoExtremoSelecionado() {
        final int borda = 4;
        int rX = 0;
        int rY = 0;
        for (FormaElementar el : getItensSelecionados()) {
            rX = Math.max(rX, el.getLeftWidth());
            rY = Math.max(rY, el.getTopHeight());
        }
        if (rX > 0) {
            rX += borda;
        }
        if (rY > 0) {
            rY += borda;
        }
        return new Point(rX, rY);
    }

    public Point getPontoMenorSelecionado() {
        int rX = getWidth();
        int rY = getHeight();
        for (FormaElementar el : getItensSelecionados()) {
            rX = Math.min(rX, el.getLeft());
            rY = Math.min(rY, el.getTop());
        }
        return new Point(rX, rY);
    }

    public void mouseClick(MouseEvent e) {
//        if (tmp.isMyEvent(e)) { //Usar mouse pressed!
//            tmp.mouseClicked(e);
//        }
    }

    public void mouseDblClick(MouseEvent e) {
        e = tradutorZoom(e);

        //elementarSobMouse = CaptureFromPoint(e.getPoint());
        setElementarSobMouse((superAncorador.IsMe(e.getPoint()) ? superAncorador : CaptureFromPoint(e.getPoint())));
        if (elementarSobMouse != null) {
            elementarSobMouse.mouseDblClicked(e);
        }
    }

    public void mousePressed(MouseEvent e) {
        master.requestFocus();
        e = tradutorZoom(e);

        if (comando != null) {
            isLoadCreate = true;
            RealiseComando(e.getPoint());
            isLoadCreate = false;
            if (cliq1 == null) {
                DoMuda(null);
            }
            return;
        }

        if (!((elementarSobMouse != null) && (elementarSobMouse == elementarSobMouse.IsMeOrMine(e.getPoint())))) {
            setElementarSobMouse(CaptureFromPoint(e.getPoint()));
        }

        if (elementarSobMouse != null) {
            elementarSobMouse.mousePressed(e);
            draging = true;
            if (elementarSobMouse != superAncorador) {
                superAncorador.SetVisible(false); //não precisa repintar!
            }
        } else {
            DiagramaDownPos.setLocation(e.getPoint());
            isMouseDown = true;
            master.InitMultiSel(DiagramaDownPos);
        }
    }

    public void mouseEntered(MouseEvent e) {
        //processaMouseEntredExited(e, elementarSobMouse, true);//não precisa
    }

    public void mouseExited(MouseEvent e) {
        processaMouseEntredExited(e, elementarSobMouse, false);
    }

    private void processaMouseEntredExited(MouseEvent ev, Elementar el, boolean enter) {
        ev = tradutorZoom(ev);

        if (el != null) {
            if (enter) {
                el.mouseEntered(ev);
            } else {
                el.mouseExited(ev);
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        e = tradutorZoom(e);

        if (draging && (elementarSobMouse != null)) {
            elementarSobMouse.mouseDragged(e);
            return;
        }
        if (isMouseDown) {
            int x = e.getX();
            int y = e.getY();
            final int ALeft = (x < DiagramaDownPos.x) ? x : DiagramaDownPos.x;
            final int ATop = (y < DiagramaDownPos.y) ? y : DiagramaDownPos.y;
            final int AWidth = Math.abs(x - DiagramaDownPos.x);
            final int AHeight = Math.abs(y - DiagramaDownPos.y);

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    master.getMultSel().setBounds(ALeft, ATop, AWidth, AHeight);
                    master.repaint();
                }
            });
        }
    }

    /**
     * Método que aplica o fator de zoom nas coordenadas do mouse.
     *
     * @param e evento do Mouse (geralmente interessa apenas as coordenadas);
     * @return novo evento com novas coordenadas
     */
    protected MouseEvent tradutorZoom(MouseEvent e) {
        double z = 1.0 / getZoom();
        Point p = new Point((int) (e.getX() * z), (int) (e.getY() * z));

        MouseEvent ex = new MouseEvent(master.getBox(), e.getID(), e.getWhen(), e.getModifiers(),
                p.x, p.y, e.getClickCount(), false);

        return ex;

    }

    /**
     * Método que aplica o fator de zoom nas coordenadas e.
     *
     * @param e = ponto (geralmente coordenadas);
     * @return novo ponto
     */
    protected Point tradutorZoom(Point e) {
        double z = 1.0 / getZoom();
        return new Point((int) (e.x * z), (int) (e.y * z));
    }

    /**
     * Dado um retângulo ele é converido para as coordenadas na tela no caso do diagrma estar ampliado ou reduzido (Zoom)
     *
     * @param r retângulo a converter.
     * @return retângulo convertido.
     */
    public Rectangle ZoomRectangle(Rectangle r) {
        double z = getZoom();
        return new Rectangle((int) (r.x * z), (int) (r.y * z), (int) (r.width * z), (int) (r.height * z));
    }

    public void mouseMoved(MouseEvent e) {
        if (isMouseDown || draging) {
            mouseReleased(e);
            return;
        }

        e = tradutorZoom(e);

        Elementar olde = elementarSobMouse;
//        elementarSobMouse = CaptureFromPoint(e.getPoint());

        setElementarSobMouse((superAncorador.IsMe(e.getPoint()) ? superAncorador : CaptureFromPoint(e.getPoint())));

        if (elementarSobMouse != null) {
            elementarSobMouse.mouseMoved(e);
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        if (comando != null) {
            Elementar composicao = elementarSobMouse;
            if (composicao != null && elementarSobMouse.isComposto()) {
                composicao = composicao.ProcessaComposicao(e.getPoint());
            }
            ProcessaOverDraw(false, composicao);
        } else if (olde != elementarSobMouse) {
            processaMouseEntredExited(e, olde, false);
            processaMouseEntredExited(e, elementarSobMouse, true);
        }
    }
    private FormaElementar overDraw = null;

    public Rectangle AlmentarRetangulo(Rectangle r, int x, int y) {
        return Utilidades.Grow(r, x, y, 0);
    }

    /**
     * Colore uma borda na Forma que estiver sobre o Mouse no momento em que estiver selecionado um novo objeto a ser inserido no Diagrama.
     *
     * @param limpa apagar.
     * @param el elementar um cujo o mouse está ou estava sobre .
     *
     */
    public void ProcessaOverDraw(boolean limpa, Elementar el) {
        int x = 4;
        int y = 4;
        if (limpa) {
            if (overDraw != null) {
                overDraw.setOverMe(false);
                Rectangle rec = AlmentarRetangulo(overDraw.getBounds(), x, y);
                overDraw.InvalidateArea(rec);
                overDraw = null;
            }
            return;
        }
        if (el == null && overDraw != null) {
            overDraw.setOverMe(false);
            Rectangle rec = AlmentarRetangulo(overDraw.getBounds(), x, y);
            overDraw.InvalidateArea(rec);
            overDraw = null;
        } else if (el != null && el instanceof FormaElementar && el != overDraw) {
            if (overDraw != null) {
                overDraw.setOverMe(false);
                Rectangle rec = AlmentarRetangulo(overDraw.getBounds(), x, y);
                overDraw.InvalidateArea(rec);
            }
            overDraw = (FormaElementar) el;
            overDraw.setOverMe(true);
            Rectangle rec = AlmentarRetangulo(overDraw.getBounds(), x, y);
            overDraw.InvalidateArea(rec);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Trata Elementar">
    /**
     * Verifica que componente está na posição "ponto" obedecendo a ordem z [melhoria: 16/05/2014] Dá preferência ao obj selecionado, independentemente da ordem z para fins de melhor seleção e movimentação com o mouse.
     *
     * @param ponto = local. Poder ser a posição x e y do mouse
     * @return o Componente.
     */
    public Elementar CaptureFromPoint(Point ponto) {
        Elementar res = getSelecionado();
        if (res != null) {
            res = res.IsMeOrMine(ponto);
            if (res != null) {
                return res;
            }
        }
        for (Elementar el : subItens) {
            res = el.IsMeOrMine(ponto);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public Elementar CaptureFromPoint(Elementar nor, Point ponto) {
        Elementar res = null;
        for (Elementar el : subItens) {
            res = el.IsMeOrMine(ponto, nor);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public Forma CaptureBaseFromPoint(Elementar nor, Point ponto) {
        Elementar res = null;
        for (Elementar el : subItens) {
            res = el.IsMeOrMineBase(ponto, nor);
            if (res != null) {
                return (Forma) res;
            }
        }
        return null;
    }
    private transient Elementar elementarSobMouse = null;

    /**
     * @return the elementarSobMouse
     */
    public Elementar getElementarSobMouse() {
        return elementarSobMouse;
    }

    /**
     * @param elementarSobMouse the elementarSobMouse to set
     */
    public void setElementarSobMouse(Elementar elementarSobMouse) {
        this.elementarSobMouse = elementarSobMouse;
    }
    // </editor-fold>
    Point DiagramaDownPos = new Point(0, 0);
    boolean isMouseDown = false;

    public void mouseReleased(MouseEvent e) {
        e = tradutorZoom(e);

        if (isMouseDown) {
            isMouseDown = false;
            Rectangle recsel = master.getMultSel().getBounds();
            boolean combine = (isShiftDown() || isControlDown());
            if (!combine) {
                ClearSelect();
            }
            final Point p = e.getPoint();
            master.FinishMultiSel();
            subItens.stream().filter((c) -> (c instanceof FormaElementar)).map((c) -> (FormaElementar) c).filter((item) -> (item.IntersectPath(recsel)))
                    .sorted((p1, p2) -> Double.compare(Utilidades.distance(p1.getLocation(), p), Utilidades.distance(p2.getLocation(), p)))
                    .forEach((item) -> {
                        DiagramaDoSelecao(item, true, true);
                    });

            //tmp.Posicione(getSelecionado());
            repaint(Utilidades.Grow(recsel, 2, 2, 0));
        }
        if (draging && (elementarSobMouse != null)) {
            elementarSobMouse.mouseReleased(e);
            superAncorador.Posicione(getSelecionado());
        }
        draging = false;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isControlDown()) {
            if (e.getWheelRotation() < 0) {
                getEditor().ZoomMais();
            } else {
                getEditor().ZoomMenos();
            }
            e.consume();
        }
    }

//    public void EndPoupup() {
//        JOptionPane.showMessageDialog(null, "FIM", "FIM2", JOptionPane.CANCEL_OPTION);
//    }
    // </editor-fold>
    public transient boolean IsStopEvents = false;
    public transient boolean isCarregando = false;
    protected transient boolean isLoadCreate = false;
    private boolean mudou = false;
    /**
     * Armazena o ID
     */
    protected int TotalID = 0;
    public Point ScrPosicao = new Point(0, 0);

    /**
     * Gera um ID único para cada elemetar criado pelo modelo.
     *
     * @return novo ID
     */
    public int getElementarID() {
        return ++TotalID;
    }

    public boolean getMudou() {
        return mudou;
    }

    public void setMudou(boolean value) {
        if (mudou != value) {
            mudou = value;
            if (!value) {
                PerformInspector();
            }
            getEditor().getShowDiagramas().repaint();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Seleção">
    /**
     * Esconde ou mostra os pontos de uma Componente
     *
     * @param esconde true: esconde, false: mostra.
     */
    public void HidePontosOnSelecao(boolean esconde) {
        for (FormaElementar item : itensSelecionados) {
            item.HidePontos(esconde);
        }
    }

    /**
     * Seleciona um componente
     *
     * @param item
     * @return selecionou?
     */
    public boolean DiagramaDoSelecao(FormaElementar item) {
        return DiagramaDoSelecao(item, false, false);
    }

    /**
     * Seleciona um componente
     *
     * @param item
     * @param ehmouse "É mouse": true: processa combinações de teclado para seleção múltipla (multiseleção).
     * @param ForcarMultSel
     * @return
     */
    public boolean DiagramaDoSelecao(FormaElementar item, boolean ehmouse, boolean ForcarMultSel) {
        if (item == null) {
            ClearSelect();
            return false;
        }
        if (!item.isSelecionavel()) {
            master.getControler().makeEnableComands();
            return false;
        }

        boolean combine = ((isShiftDown() || isControlDown()) && ehmouse) || ForcarMultSel;
        if (itensSelecionados.indexOf(item) == -1) {
            if (combine) {
                AddSelect(item);
            } else {
                if (itensSelecionados.size() > 0) {
                    ClearSelect(false);
                }
                AddSelect(item);
            }
            PerformInspector();
            master.getControler().makeEnableComands();

            superAncorador.Posicione(getSelecionado());

            return true;
        } else if ((combine) && (itensSelecionados.size() > 1)) {
            RemoveSelect(item);
            PerformInspector();
            //master.getControler().makeEnableComands();

            superAncorador.Posicione(getSelecionado());

            return false;
        } else {
            //PerformInspector();
            master.getControler().makeEnableComands();

            superAncorador.Posicione(getSelecionado());

            return true;
        }
    }

    private void PontosCor(FormaElementar item) {
        PontosCor(item, false);
    }

    private void PontosCor(FormaElementar item, boolean verde) {
        item.DoPontoCor(verde);
    }

    private void AddSelect(FormaElementar item) {
        itensSelecionados.add(item);
        if (itensSelecionados.size() > 1) {
            PontosCor(item, true);
        }
        item.setSelecionado(true);
    }

    public void PromoveToFirstSelect(FormaElementar item) {
        int idx = itensSelecionados.indexOf(item);
        if (idx > 0) {
            PontosCor(itensSelecionados.get(0), true);
            itensSelecionados.remove(item);
            itensSelecionados.add(0, item);
            PontosCor(item);
        }
    }

    private void RemoveSelect(FormaElementar item) {
        if (itensSelecionados.indexOf(item) == -1) {
            return;
        }
        itensSelecionados.remove(item);
        PontosCor(item);
        item.setSelecionado(false);
        //if (itensSelecionados.size() == 1) {
        PontosCor(itensSelecionados.get(0));
        //}
    }

    /**
     * Limpa/des-seleciona todos os selecionados.
     */
    public void ClearSelect() {
        ClearSelect(true);
    }

    /**
     * Limpa/des-seleciona todos os selecionados. Redesenha o Inspector
     *
     * @param performInsp
     */
    public void ClearSelect(boolean performInsp) {
        itensSelecionados.forEach(item -> {
            PontosCor(item);
            item.setSelecionado(false);
        });
        itensSelecionados.clear();
        if (performInsp) {
            master.getControler().makeEnableComands();
            PerformInspector();
        }
        superAncorador.SetVisible(false);
    }

    public boolean TemSelecionado() {
        return itensSelecionados.size() > 0;
    }

    public FormaElementar getSelecionado() {
        if (TemSelecionado()) {
            return itensSelecionados.get(0);
        }
        return null;
    }

    public void setSelecionado(FormaElementar sel) {
        DiagramaDoSelecao(sel, false, false);
    }
    // </editor-fold>

    public void ReciveProcessMove(FormaElementar nor, int x, int y) {
        itensSelecionados.stream().filter(item -> item != nor).forEach(item -> item.DoMove(x, y));
//        for (FormaElementar item : itensSelecionados) {
//            if (item == nor) {
//                continue;
//            }
//            item.DoMove(x, y);
//        }
    }

    //<editor-fold defaultstate="collapsed" desc="Teclas">
    /**
     * @return the shiftDown
     */
    public boolean isShiftDown() {
        return master.isShiftDown();
    }

    /**
     * @return the altDown
     */
    public boolean isAltDown() {
        return master.isAltDown();
    }

    /**
     * @return the controlDown
     */
    public boolean isControlDown() {
        return master.isControlDown();
    }
    // </editor-fold>

    public void DoFormaResize(Rectangle ret) {
        if (ret.x == 0 && ret.y == 0 && ret.width == 0 && ret.height == 0) {
            return;
        }
        itensSelecionados.stream().filter((de) -> (de instanceof Forma)).map((de) -> (Forma) de).forEach((item) -> {
            item.ReciveFormaResize(ret);
        });
        repaint();
    }

    public void DoBaseReenquadreReposicione() {
        itensSelecionados.stream().filter((item) -> (!item.Reenquadre())).forEach((item) -> {
            item.Reposicione();
        });
    }

    /**
     * Recebe as teclas do Editor.
     *
     * @param e
     */
    public void ProcesseTeclas(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            setComando(null);
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            InspectorItemBase ppr = getEditor().getInspectorEditor().FindByProperty("setTexto");
            if (ppr != null) {
                if (getEditor().getInspectorEditor().getSelecionado() == ppr) {
                    getEditor().getInspectorEditor().PerformSelect(null);
                }
                getEditor().getInspectorEditor().PerformSelect(ppr);
                if (ppr instanceof InspectorItemExtender) {
                    ((InspectorItemExtender) ppr).ExternalRun();
                }
            }
            return;
        }

        if (itensSelecionados.isEmpty()) {
            return;
        }

        FormaElementar item = itensSelecionados.get(0);
        int x = 0, y = 0;
        int inc = 3;
        if (e.isControlDown()) {
            inc = 1;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                x = -inc;
                y = 0;
                break;
            case KeyEvent.VK_RIGHT:
                x = inc;
                y = 0;
                break;
            case KeyEvent.VK_UP:
                x = 0;
                y = -inc;
                break;
            case KeyEvent.VK_DOWN:
                x = 0;
                y = inc;
                break;
        }

        if (item.isAncorado()) {
            //HidePontosOnSelecao(false);
            e.consume();
        } else if (x != 0 || y != 0) {
            if (e.isShiftDown() && item instanceof Forma) {
                Rectangle rec = new Rectangle(0, 0, -x, -y);
                ((Forma) item).DoFormaResize(rec);
            } else {
                item.DoRaiseMove(x, y);
            }
            superAncorador.Posicione(item);
            DoBaseReenquadreReposicione();
            HidePontosOnSelecao(false);
            PerformInspector();
            e.consume();
        }
    }

    /**
     * Recebe as comandos na forma de teclas Editor.
     *
     * @param k
     */
    public void ProcesseTeclas(int k) {
        if (itensSelecionados.isEmpty()) {
            return;
        }

        FormaElementar item = itensSelecionados.get(0);
        if (item.isAncorado()) {
            return;
        }

        int x = 0, y = 0;
        int inc = (isControlDown()) ? 1 : 3;

        switch (k) {
            case KeyEvent.VK_LEFT:
                x = -inc;
                y = 0;
                break;
            case KeyEvent.VK_RIGHT:
                x = inc;
                y = 0;
                break;
            case KeyEvent.VK_UP:
                x = 0;
                y = -inc;
                break;
            case KeyEvent.VK_DOWN:
                x = 0;
                y = inc;
                break;
        }

        if (x != 0 || y != 0) {
            if (isShiftDown() && item instanceof Forma) {
                Rectangle rec = new Rectangle(0, 0, -x, -y);
                ((Forma) item).DoFormaResize(rec);
            } else {
                item.DoRaiseMove(x, y);
            }
            superAncorador.Posicione(item);
            DoBaseReenquadreReposicione();
            HidePontosOnSelecao(false);

        }
        master.requestFocus();
        PerformInspector();
    }

    public boolean SelecioneProximo() {
        if (itensSelecionados.isEmpty()) {
            return false;
        }

        FormaElementar item = itensSelecionados.get(0);
        if (itensSelecionados.size() == 1 && ListaDeItens.size() > 1) {
            int idxAtual = ListaDeItens.indexOf(item);
            int idx = idxAtual;
            while (true) {
                idx++;
                if (idx >= ListaDeItens.size()) {
                    idx = 0;
                }
                if (idx == idxAtual) {
                    break;
                }
                item = ListaDeItens.get(idx);
                if (item.isSelecionavel()) {
                    DiagramaDoSelecao(item, false, false);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean SelecioneAnterior() {
        if (itensSelecionados.isEmpty()) {
            return false;
        }

        FormaElementar item = itensSelecionados.get(0);
        if (itensSelecionados.size() == 1 && ListaDeItens.size() > 1) {
            int idxAtual = ListaDeItens.indexOf(item);
            int idx = idxAtual;
            while (true) {
                idx--;
                if (idx < 0) {
                    idx = ListaDeItens.size() - 1;
                }
                if (idx == idxAtual) {
                    break;
                }
                item = ListaDeItens.get(idx);
                if (item.isSelecionavel()) {
                    DiagramaDoSelecao(item, false, false);
                    return true;
                }
            }
        }
        return false;
    }

    public void SelecioneTodos() {
        ClearSelect(false);
        getListaDeItens().forEach((it) -> {
            DiagramaDoSelecao(it, false, true);
        });
        PerformInspector();
    }

    public void SelecioneTodosDoTipo() {
        FormaElementar sel = getSelecionado();
        if (sel == null) return;
        ClearSelect(false);
        DiagramaDoSelecao(sel, false, true);
        getListaDeItens().stream().filter(s -> s.getClass() == sel.getClass() && s != sel).forEach((it) -> {
            DiagramaDoSelecao(it, false, true);
        });
        PerformInspector();
    }

    /**
     * Adiciona um componente ao Diagrama.
     *
     * @param aThis
     */
    public final void Add(FormaElementar aThis) {
        subItens.add(aThis);
        ListaDeItens.add(aThis);
    }

    public boolean IsMultSelecionado() {
        return itensSelecionados.size() > 1;
    }

    private boolean Remove(FormaElementar item) {
        if (!item.isCanBeDeleted()) {
            return item.AskToDelete();
        }
        ListaDeItens.remove(item);
        subItens.remove(item);
        item.Destroy();
        return true;
    }

    public boolean Remove(FormaElementar item, boolean removeSel) {
        if (Remove(item)) {
            if (removeSel) {
                itensSelecionados.remove(item);
            }
            return true;
        }
        return false;
    }

    public boolean deleteSelecao() {
        if (TemSelecionado()) {
            itensSelecionados.stream().forEach((el) -> {
                Remove(el);
            });
            itensSelecionados.clear();
            DoMuda(null);
            repaint();
            master.getControler().makeEnableComands();
            return true;
        }
        return false;
    }

    // <editor-fold defaultstate="collapsed" desc="Comando">
    private transient Controler.Comandos comando = null;

    protected void setComando(Controler.Comandos cmd) {
        comando = cmd;
        if (cmd == null) {
            master.NoAction();
            setCursor(new Cursor((Cursor.DEFAULT_CURSOR)));
            ProcessaOverDraw(true, null);
        }
    }

    public Controler.Comandos getComando() {
        return comando;
    }

    public void DoAction(ActionEvent ev) {
        if (ev.getActionCommand() == null || ev.getActionCommand().isEmpty()) {
            setComando(null);
            return;
        }
        try {
            Controler.Comandos cmd = Controler.Comandos.valueOf(ev.getActionCommand());
            if (comando != cmd) {
                cliq1 = null;
                cliq2 = null;
            }
            setComando(cmd);
        } catch (Exception e) {
            setComando(null);
        }
    }

    public class clickForma {

        private final FormaElementar forma;
        private final Point ponto;

        public clickForma(FormaElementar forma, Point ponto) {
            this.forma = forma;
            this.ponto = ponto;
        }

        public FormaElementar getForma() {
            return forma;
        }

        public Point getPonto() {
            return ponto;
        }
    }

    /**
     * Usada para criar objetos na forma padrão de forma externa
     *
     * @param cmd
     * @param posi
     * @return novo objeto/objeto criado
     */
    public FormaElementar ExternalRealiseComando(Controler.Comandos cmd, Point posi) {
        Controler.Comandos c = getComando();
        setComando(cmd);
        FormaElementar res = RealiseComando(posi);
        setComando(c);
        return res;
    }

    protected transient clickForma cliq1 = null, cliq2 = null;

    protected FormaElementar RealiseComando(Point posi) {
        //ClearSelect(false);
        FormaElementar resu = null;
        //Point tmpPt;
        Controler.Comandos com = comando;
        Elementar res = null;

        switch (com) {
            case cmdDesenhador:
                Desenhador dz = new Desenhador(this);
                dz.SetBounds(posi.x, posi.y, 250, 150);
                dz.Reenquadre();
                resu = dz;
                break;
            case cmdLegenda:
                Legenda leg = new Legenda(this);
                leg.SetBounds(posi.x, posi.y, 150, 45);
                leg.Reenquadre();
                resu = leg;
                break;
            case cmdTexto:
                res = CaptureFromPoint(posi);
                Texto Tx = new Texto(this, "Texto");
                if (res instanceof SuperLinha) {
                    Tx.SetBounds(posi.x, posi.y, 100, 18);
                    Tx.setAlinhamento(AlinhamentoTexto.alEsquerda);
                    Tx.setTipo(TipoTexto.tpEmBranco);
                    Tx.setCentrarVertical(false);
                    ((SuperLinha) res).setTag(Tx);
                } else {
                    Tx.SetBounds(posi.x, posi.y, 150, 36);
                    Tx.Reenquadre();
                }
                resu = Tx;
                break;
            case cmdApagar:
                res = CaptureFromPoint(posi);
                if (res instanceof FormaElementar) {
                    resu = (FormaElementar) res;
                    ClearSelect();
                    setSelecionado(resu);
                    deleteSelecao();
                }
                resu = null;
        }
        cliq1 = null;
        cliq2 = null;

        if (!master.isControlDown()) {
            setComando(null);
        } else {
            setComando(com);
        }
        if (resu != null) {
            resu.BringToFront();
        }
        return resu;
    }

    // </editor-fold>
    /**
     * Redesenha o inspector com o objeto selecionado. É chamado também no método setMuda(true).
     */
    public void PerformInspector() {
        if (!itensSelecionados.isEmpty()) {
            getEditor().PerformInspectorFor(itensSelecionados.get(0));
        } else {
            getEditor().PerformInspectorFor(infoDiagrama);
        }
    }

    /**
     * Redesenha o inspector com o objeto selecionado.
     *
     * @param force = limpa as propriedades previamente capturadas de forma que a chamada ao inspector force uma recarga.
     */
    public void PerformInspector(boolean force) {
        if (force) {
            getEditor().getInspectorEditor().ForceFullOnCarregue();
        }
        PerformInspector();
    }

    /**
     * Este médodo é usado quando precisa-se usar o inspector para editar propriedade de sub componete
     *
     * @param ed: componete principal em edição.
     * @param bj: o java não tem passagem por referencia - boxing da "propriedade" em edição
     * @return: o sub componente (se for o caso).
     */
    public Object processeEdicaoSubItem(FormaElementar ed, BoxingJava bj) {
        return ed;
    }

    public boolean AceitaEdicao(InspectorProperty prop, String valor) {
        FormaElementar param;
        if (itensSelecionados.isEmpty()) {
            param = infoDiagrama;
        } else {
            param = itensSelecionados.get(0);
        }
        Object ed = param;
        String P = prop.property;

        //caso de edição de sub componente. Exemplo: EntidadeAssociativa.Relacao
        if (P.indexOf('.') > 0) {
            util.BoxingJava bj = new util.BoxingJava(P);
            ed = processeEdicaoSubItem(param, bj);
            P = bj.Str;
        }
        final String propriedade = P;

        final Class[] par = new Class[1];
        final Object[] vl = new Object[1];
        try {
            switch (prop.tipo) {
                case tpBooleano:
                    par[0] = Boolean.TYPE;
                    vl[0] = Boolean.parseBoolean(valor);
                    break;
                case tpCor:
                    par[0] = Color.class;
                    vl[0] = util.Utilidades.StringToColor(valor);
                    break;
                case tpMenu:
                    par[0] = Integer.TYPE;
                    int p = Integer.parseInt(valor);
                    vl[0] = p;
                    break;
                case tpNumero:
                    par[0] = Integer.TYPE;
                    final int tmp = Integer.parseInt(valor);
                    vl[0] = tmp;

                    final String[] pprtMv = {"setLeft", "setTop", "setWidth", "setHeight"};
                    if (Arrays.asList(pprtMv).indexOf(propriedade) > -1) {
                        if (ed instanceof Forma) {
                            IsStopEvents = true;
                            itensSelecionados.stream().filter(e -> e.getClass().equals(param.getClass())).map(f -> (Forma) f).forEach(Ed -> {
                                Rectangle ret;
                                if ((propriedade.equals(pprtMv[0]))) {
                                    ret = new Rectangle(Ed.getLeft() - tmp, 0, 0, 0);
                                } else if (propriedade.equals(pprtMv[1])) {
                                    ret = new Rectangle(0, Ed.getTop() - tmp, 0, 0);
                                } else if (propriedade.equals(pprtMv[2])) {
                                    ret = new Rectangle(0, 0, Ed.getWidth() - tmp, 0);
                                } else {
                                    ret = new Rectangle(0, 0, 0, Ed.getHeight() - tmp);
                                }
                                //Ed.DoFormaResize(ret);
                                Ed.ReciveFormaResize(ret);

                                Ed.DoRaizeReenquadreReposicione();
                                //DoMuda(Ed);
                            });
                            IsStopEvents = false;
                            DoMuda(param);
                            return true;
                        }
                    }
                    break;
                default:
                    par[0] = String.class;
                    vl[0] = valor;
            }
//            final String[] multi = {"setForeColor", "setTipoAtributo"}; //quais propriedades proderão ser editadas em cojunto
//
//            if (Arrays.asList(multi).indexOf(propriedade) == -1 || ((ed instanceof Forma) && ((Forma) ed).getPrincipal() != null)) {
//                Class cl = ed.getClass();
//                Method mthd = cl.getMethod(propriedade, par);
//                mthd.invoke(ed, vl);
//                DoMuda(param);
//            } else {
//                //List<FormaElementar> lst =  itensSelecionados.stream().filter(e -> e.getClass().equals(param.getClass())).collect(Collectors.toList());
//                final List<FormaElementar> lst;
//                final String[] multi_any = {"setForeColor"}; //de qualquer classe ? (sim, neste array). ELSE: mesma classe do objeto selecionado principal
//                if (Arrays.asList(multi_any).indexOf(propriedade) > -1) {
//                    lst = itensSelecionados.stream().collect(Collectors.toList());
//                } else {
//                    lst = itensSelecionados.stream().filter(e -> e.getClass().equals(param.getClass())).collect(Collectors.toList());
//                }
//                IsStopEvents = true;
//                for (FormaElementar Ed : lst) {
//                    Class cl = Ed.getClass();
//                    Method mthd = cl.getMethod(propriedade, par);
//                    mthd.invoke(Ed, vl);
//                }
//                IsStopEvents = false;
//                DoMuda(param);
//            }

//            final String[] multi = {"setForeColor", "setTipoAtributo"}; //quais propriedades proderão ser editadas em cojunto

            if ((ed instanceof InfoDiagrama) || !(ed instanceof FormaElementar) || ((ed instanceof FormaElementar) && (((FormaElementar) ed).isParte()))) {
                Class cl = ed.getClass();
                Method mthd = cl.getMethod(propriedade, par);
                mthd.invoke(ed, vl);
                DoMuda(param);
            } else {
                final List<FormaElementar> lst;
                lst = itensSelecionados.stream().filter(e -> e.getClass().equals(param.getClass())).collect(Collectors.toList());

                IsStopEvents = true;
                for (FormaElementar Ed : lst) {
                    Class cl = Ed.getClass();
                    Method mthd = cl.getMethod(propriedade, par);
                    mthd.invoke(Ed, vl);
                }
                IsStopEvents = false;
                DoMuda(param);
            }

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            util.BrLogger.Logger("ERROR_SET_PROPERTY", e.getMessage());
            IsStopEvents = false;
            superAncorador.InvalidateArea();
            return false;
        }
        superAncorador.InvalidateArea();
        return true;
    }

    /**
     * Usado para os métodos copiarFormatacao.
     *
     * @param param O que está sendo colado
     * @param prop uma de suas propriedades
     * @param valor novo valor.
     * @return
     */
    public boolean ColeFormatacao(FormaElementar param, InspectorProperty prop, String valor) {
        Object ed = param;
        String propriedade = prop.property;

        //caso de edição de sub componente. Exemplo: EntidadeAssociativa.Relacao
        if (propriedade.indexOf('.') > 0) {
            util.BoxingJava bj = new util.BoxingJava(propriedade);
            ed = processeEdicaoSubItem(param, bj);
            propriedade = bj.Str;
        }

        Class[] par = new Class[1];
        Object[] vl = new Object[1];
        try {
            switch (prop.tipo) {
                case tpBooleano:
                    par[0] = Boolean.TYPE;
                    vl[0] = Boolean.parseBoolean(valor);
                    break;
                case tpCor:
                    par[0] = Color.class;
                    vl[0] = util.Utilidades.StringToColor(valor); //new Color(Integer.parseInt(valor));
                    break;
                case tpMenu:
                    par[0] = Integer.TYPE;
                    int p = Integer.parseInt(valor);
                    vl[0] = p;
                    break;
                case tpNumero:
                    par[0] = Integer.TYPE;
                    int tmp = Integer.parseInt(valor);
                    vl[0] = tmp;

                    String[] pprtMv = {"setLeft", "setTop", "setWidth", "setHeight"};
                    if (Arrays.asList(pprtMv).indexOf(propriedade) > -1) {
                        Rectangle ret;
                        if (ed instanceof Forma) {
                            Forma Ed = (Forma) ed;
                            if ((propriedade.equals(pprtMv[0]))) {
                                ret = new Rectangle(Ed.getLeft() - tmp, 0, 0, 0);
                            } else if (propriedade.equals(pprtMv[1])) {
                                ret = new Rectangle(0, Ed.getTop() - tmp, 0, 0);
                            } else if (propriedade.equals(pprtMv[2])) {
                                ret = new Rectangle(0, 0, Ed.getWidth() - tmp, 0);
                            } else {
                                ret = new Rectangle(0, 0, 0, Ed.getHeight() - tmp);
                            }
                            Ed.DoFormaResize(ret);
                            Ed.DoRaizeReenquadreReposicione();
                            DoMuda(Ed);
                            return true;
                        }
                    }
                    break;
                default:
                    par[0] = String.class;
                    vl[0] = valor;
            }
            Class cl = ed.getClass();
            Method mthd = cl.getMethod(propriedade, par);
            mthd.invoke(ed, vl);
            DoMuda(param);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            util.BrLogger.Logger("ERROR_SET_PROPERTY", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Com base nos nomes dos objetos traduzidos nos arq. de confg. nomeia os objetos no ato de criação.
     *
     * @param padrao nome dado por mim
     * @return nome traduzido/configurado pelo usuário.
     */
    public final String Nomeie(String padrao) {
        String txt = Editor.fromConfiguracao.getValor("diagrama." + padrao + ".nome");
        int res = 1;
        ArrayList<String> txts = new ArrayList<>();
        ListaDeItens.stream().filter((el) -> (el instanceof Forma)).map(el -> (Forma) el).forEach(el -> el.EscrevaTexto(txts));

        while (txts.indexOf(txt + "_" + res) != -1) {
            res++;
        }
        return txt + "_" + res;
    }

    /**
     * Procura um artefato pelo ID
     *
     * @param id
     * @return encontrado
     */
    public FormaElementar FindByID(int id) {
        for (FormaElementar f : getListaDeItens()) {
            if (f.getID() == id) {
                return f;
            }
        }
        return null;
    }

//    Nunca usado! 20/09/2014
//    public void CheckLigConsistencia(Forma sender, Forma emQuem, Linha por) {
//    }
    public void DoMuda(FormaElementar who) {
        if (isLoadCreate || isCarregando) {
            return;
        }
        setMudou(true);
        try {
            master.DoDiagramaMuda();
        } catch (Exception e) {
            util.BrLogger.Logger("ERROR_DIAGRAMA_MUDA", e.getMessage());
        }
//        if (who != null && TemSelecionado()) {
//            if (who != itensSelecionados.get(0)) return;
//        }
        PerformInspector();
    }

    //<editor-fold defaultstate="collapsed" desc="Save Load">
    /**
     * Gera uma Stream a partir de um modelo
     *
     * @param othis
     * @return
     */
    public synchronized static ByteArrayOutputStream SaveToStream(Diagrama othis) {
        try {
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            try (ObjectOutput out = new ObjectOutputStream(ba)) {
                out.writeObject(othis);
            }
            othis.tick();
            return ba;
        } catch (IOException iOException) {
            util.BrLogger.Logger("ERROR_DIAGRAMA_SAVELOAD_STREAM", iOException.getMessage());
            return null;
        }
    }
    public final static String nodePrincipal = "DIAGRAMA";

    private static TipoDeDiagrama GetTipoOnXml(Document doc) {
        try {
            NodeList nodeLst = doc.getElementsByTagName(nodePrincipal);
            Element prin = (Element) nodeLst.item(0);
            String tp = prin.getAttribute("TIPO");
            return TipoDeDiagrama.valueOf(tp);
        } catch (Exception e) {
            util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD_TIPO_XML", e.getMessage());
            return null;
        }
    }

    /**
     * Gera um diagrama novo a partir de uma Stream
     *
     * @param ba
     * @return
     */
    public synchronized static Diagrama LoadFromStream(ByteArrayOutputStream ba) {
        try {
            byte[] bytes = ba.toByteArray();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            //in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Diagrama res = (Diagrama) in.readObject();
            in.close();
            return res;
        } catch (ClassNotFoundException | IOException e) {
            util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD", e.getMessage());
            return null;
        }
    }

    /**
     * Gera um diagrama novo a partir de um Arquivo
     *
     * @param arq
     * @param master
     * @return
     */
    public synchronized static Diagrama LoadFromFile(File arq, Editor master) {
        if (arq == null || master.IsOpen(arq)) {
            return null;
        }
        Diagrama res = null;

        String onome = arq.getName();
        if (util.Arquivo.IsbrM3(arq)) {
            onome = onome.substring(0, onome.length() - util.Arquivo.brM3.length() - 1);
        } else {
            onome = onome.substring(0, onome.length() - util.Arquivo.xml.length() - 1);
        }

        if (util.Arquivo.IsbrM3(arq)) {
            try {
                FileInputStream fi = new FileInputStream(arq);
                try (ObjectInput in = new ObjectInputStream(fi)) {
                    GuardaPadraoBrM seguranca = (GuardaPadraoBrM) in.readObject();
                    in.close();
                    res = seguranca.getDiagrama();
                    res.setMaster(master);
                }
                //Recria o UID para se ter a certeza de que ele é único, não repetido por uma eventual cópaia do arquivo.
                res.ReGeraUniversalUnicID();
                res.setArquivo(arq.getAbsolutePath());
                master.addLastOpened(arq.getAbsolutePath());
                res.SetNome(onome);
                return res;
            } catch (NullPointerException | IOException | ClassNotFoundException iOException) {
                util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD_FILE_BRM", iOException.getMessage());
                return null;
            }
        } else {
            Document doc = util.XMLGenerate.LoadDocument(arq);
            if (doc == null) {
                return null;
            }
            TipoDeDiagrama tp = GetTipoOnXml(doc);
            res = master.Novo(tp);
            if (!res.LoadFromXML(doc, false)) {
                util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD_FILE_XML", "[IS BRM XML?]");
            }
            //Recria o UID para se ter a certeza de que ele é único, não repetido por uma eventual cópaia do arquivo.
            res.ReGeraUniversalUnicID();
            res.setArquivo(arq.getAbsolutePath());
            res.SetNome(onome);
            master.addLastOpened(arq.getAbsolutePath());
            return res;
        }
    }

    /**
     * Gera um diagrama novo a partir de um GuardaPadraoBrM
     *
     * @param seguranca
     * @param master
     * @return
     */
    public synchronized static Diagrama LoadFromBrm(GuardaPadraoBrM seguranca, Editor master) {
        if (seguranca == null) {
            return null;
        }
        Diagrama res = seguranca.getDiagrama();
        res.setMaster(master);
        //Recria o UID para se ter a certeza de que ele é único, não repetido por uma eventual cópaia do arquivo.
        res.ReGeraUniversalUnicID();
        res.setArquivo("");
        //O nome está aramzendo no TAG para facilitar a identificação.
        res.SetNome(seguranca.Tag);
        return res;
    }

    public boolean LoadFromXML(Document doc, boolean colando) {

        HashMap<Element, FormaElementar> link = new HashMap<>();

        try {

            //<editor-fold defaultstate="collapsed" desc="Remover espaços - http://stackoverflow.com/questions/978810/how-to-strip-whitespace-only-text-nodes-from-a-dom-before-serialization">
            XPathFactory xpathFactory = XPathFactory.newInstance();
            // XPath to find empty text nodes.
            XPathExpression xpathExp = xpathFactory.newXPath().compile(
                    "//text()[normalize-space(.) = '']");
            NodeList emptyTextNodes = (NodeList) xpathExp.evaluate(doc, XPathConstants.NODESET);

            // Remove each empty text node from document.
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }
            //</editor-fold>

            NodeList nodeLst = doc.getElementsByTagName(Diagrama.nodePrincipal);
            Node mer = nodeLst.item(0);
            nodeLst = mer.getChildNodes();

            if (colando) {
                ClearSelect(true);
                ((InfoDiagrama) infoDiagrama).setDiagramaOldUniversalUnicID(String.valueOf(((Element) mer).getAttribute("UniversalUnicID")));
            }

            this.isLoadCreate = true;
            this.isCarregando = true;
            int tl = 0;
            int maxID = 0;
            for (int s = 0; s < nodeLst.getLength(); s++) {
                Node fstNode = nodeLst.item(s);
                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fstElmnt = (Element) fstNode;
                    FormaElementar res = runCriadorFromXml(fstElmnt, colando);
                    if (res == null) {
                        util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD", "Lixo ou objeto alienígena encontrado: " + fstElmnt.toString() + " - não é possível colar este objeto", "[]");
                        continue;
                    }
                    tl++;
                    if (!colando) {
                        maxID = Math.max(maxID, res.getID());
                    }
                    link.put(fstElmnt, res);
                    maxID = OnLoadingXMLitem(res, fstElmnt, colando, maxID, link);
                }
            }
            if (!colando) {
                TotalID = maxID;
            }
            this.isLoadCreate = false;

            link.keySet().stream().forEach((el) -> {
                FormaElementar proc = link.get(el);
                proc.CommitXML(el, link);
            });
            this.isCarregando = false;

            if (colando) {
                if (((Element) mer).hasAttribute("FIRST_SEL")) {
                    ReestrutureSelecao(((Element) mer).getAttribute("FIRST_SEL"), link);
                }
            }
            if (tl > 0 && colando) {
                DoMuda(null);
            }
            PerformInspector();

        } catch (Exception e) {
            util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD", e.getMessage());
            this.isLoadCreate = false;
            this.isCarregando = false;
            return false;
        }
        repaint();
        return true;
    }

    //private boolean naoMostre = false;
    protected int OnLoadingXMLitem(FormaElementar res, Element fstElmnt, boolean colando, int maxID, HashMap<Element, FormaElementar> link) {
        return maxID;
    }

    protected void ReestrutureSelecao(String oid, HashMap<Element, FormaElementar> link) {
        FormaElementar ja = null;
        for (Element el : link.keySet()) {
            FormaElementar proc = link.get(el);
            if (ja == null && el.hasAttribute("ID") && el.getAttribute("ID").equals(oid)) {
                ja = proc;
            }
            DiagramaDoSelecao(proc, false, true);
        }
        if (ja != null) {
            PromoveToFirstSelect(ja);
        }
    }

    protected FormaElementar ReflectionObj(Class classeDoObj) {
        Class[] argsConstr = new Class[]{Diagrama.class};
        Object[] omodelo = new Object[]{this};
        Constructor construtor;
        FormaElementar res;
        try {
            construtor = classeDoObj.getConstructor(argsConstr);
            try {
                res = (FormaElementar) construtor.newInstance(omodelo);
                return res;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD_REFLECTION_CREATE_OBJ", e.getMessage());
            }
        } catch (NoSuchMethodException e) {
            util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD_REFLECTION_CONSTRUCTOR", e.getMessage());
        }
        return null;
    }

    protected FormaElementar runCriadorFromXml(Element xml, boolean colando) {
        int classe = xml.getNodeName().hashCode();

        if (classe == InfoDiagrama.class.getSimpleName().hashCode()) {
            if (!colando) {
                LoadFromXML(this.infoDiagrama, xml, false);
            }
            return this.infoDiagrama;
        }

        final Class[] classes = getCassesDoDiagrama();

        for (Class cl : classes) {
            if (cl.getSimpleName().hashCode() == classe) {
                FormaElementar res = ReflectionObj(cl);
                if (res != null) {
                    LoadFromXML(res, xml, colando);
                }
                return res;
            }
        }
        return null;
    }

    protected void LoadFromXML(FormaElementar obj, Element xml, boolean colando) {
        try {
            obj.LoadFromXML(xml, colando);
        } catch (Exception e) {
            util.BrLogger.Logger("ERROR_OBJECT_LOAD", Integer.toString(obj.getID()) + " - " + obj.getClass().getSimpleName(), e.getMessage());
        }
    }

    public static String SaveToXml(Diagrama othis, boolean justSel) {
        return XMLGenerate.GeraXMLFrom(othis, justSel);
    }

    public boolean Salvar() {
        File arq = util.Dialogos.ShowDlgSaveDiagrama(master.getRootPane(), this);
        if (arq == null) {
            return false;
        }
        return Salvar(arq, true);
    }

    public boolean Salvar(String fileName) {
        if ("".equals(fileName) || fileName == null) {
            return Salvar();
        }
        File arq = new File(fileName);
        return Salvar(arq, false);
    }

    public boolean Salvar(File fileName, boolean pergunta) {
        if (fileName.exists() && pergunta) {
            if (util.Dialogos.ShowMessageConfirm(master.getRootPane(), Editor.fromConfiguracao.getValor("Controler.MSG_QUESTION_REWRITE")) != JOptionPane.YES_OPTION) {
                return false;
            }
        }
        String txt = getNome();
        String onome = fileName.getName();
        versaoA = Diagrama.VERSAO_A;
        versaoB = Diagrama.VERSAO_B;
        versaoC = Diagrama.VERSAO_C;
        if (util.Arquivo.IsbrM3(fileName)) {
            onome = onome.substring(0, onome.length() - util.Arquivo.brM3.length() - 1);
        } else {
            onome = onome.substring(0, onome.length() - util.Arquivo.xml.length() - 1);
        }
        setNome(onome);

        if (util.Arquivo.IsbrM3(fileName)) {
            try {
                FileOutputStream fo = new FileOutputStream(fileName);
                try (ObjectOutput out = new ObjectOutputStream(fo)) {
                    //não guardar o diretório onde se encontra na origem (segurança!!?? Inútil guardar.)
                    this.setArquivo("");
                    //out.writeObject(this);
                    GuardaPadraoBrM seg = new GuardaPadraoBrM(this);
                    seg.versaoDiagrama = versaoA + "." + versaoB + "." + versaoC;
                    out.writeObject(seg);
                }
                this.setArquivo(fileName.getAbsolutePath());
                master.addLastOpened(fileName.getAbsolutePath());

                this.setMudou(false);
                master.DoAutoSaveCompleto();
                PerformInspector();
                return true;
            } catch (IOException iOException) {
                util.BrLogger.Logger("ERROR_DIAGRAMA_SAVE_BRM", iOException.getMessage());
                setNome(txt);
                return false;
            }
        } else {
            try {
                StringWriter ou = XMLGenerate.GeraXMLtoSaveFrom(this, false);
                try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName))) {
                    out.write(ou.getBuffer().toString());
                    this.setArquivo(fileName.getAbsolutePath());
                    master.addLastOpened(fileName.getAbsolutePath());

                    this.setMudou(false);
                    master.DoAutoSaveCompleto();
                    PerformInspector();
                    return true;
                }
            } catch (IOException iOException) {
                util.BrLogger.Logger("ERROR_DIAGRAMA_SAVE_XML", iOException.getMessage());
                setNome(txt);
                return false;
            }
        }
    }

    public boolean AutoSalvar(ArrayList<byte[]> as) {
        versaoA = Diagrama.VERSAO_A;
        versaoB = Diagrama.VERSAO_B;
        versaoC = Diagrama.VERSAO_C;
        try {
            ByteArrayOutputStream fo = new ByteArrayOutputStream();
            try (ObjectOutput out = new ObjectOutputStream(fo)) {
                GuardaPadraoBrM seg = new GuardaPadraoBrM(this);
                seg.versaoDiagrama = versaoA + "." + versaoB + "." + versaoC;
                seg.Tag = this.getNome();
                out.writeObject(seg);
            }
            as.add(fo.toByteArray());
            return true;
        } catch (IOException iOException) {
            util.BrLogger.Logger("ERROR_DIAGRAMA_AUTOSAVE_MEM", iOException.getMessage());
            return false;
        }
    }
    //</editor-fold>

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void doCopy() {
        String res = Diagrama.SaveToXml(this, true);
        StringSelection vai = new StringSelection(res);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(vai, this);
    }

    public void doCopy(BufferedImage img) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        util.TransferableImage trans = new util.TransferableImage(img);
        clipboard.setContents(trans, this);
    }

    public void doPaste() {
        String txt = getClipboardContents();
        if (!"".equals(txt)) {
            Document doc = util.XMLGenerate.LoadDocument(txt);
            processePaste(doc);
        } else {
            BufferedImage img = getClipboardContentsImage();
            if (img != null) {
                int x = getEditor().getMargem() >= ScrPosicao.x ? 4 : ScrPosicao.x + 4 - getEditor().getMargem();
                int y = getEditor().getMargem() >= ScrPosicao.y ? 4 : ScrPosicao.y + 4 - getEditor().getMargem();

                Point p = tradutorZoom(new Point(x, y));
                x = p.x;
                y = p.y;

                FormaElementar xres = ExternalRealiseComando(Controler.Comandos.cmdDesenhador, p);
                Desenhador de = (Desenhador) xres;
                de.setTipoImg();
                de.setImagem(img);
                de.SetBounds(x, y, img.getWidth(), img.getHeight());
                de.InvalidateArea();
                DiagramaDoSelecao(de, false, false);
                DoMuda(null);
                PerformInspector();
            }
        }
    }

    public boolean doPaste(String txt) {
        if (!"".equals(txt)) {
            Document doc = util.XMLGenerate.LoadDocument(txt);
            return (processePaste(doc));
        }
        return false;
    }

    private boolean processePaste(Document doc) {
        if (doc != null) {
            boolean res = LoadFromXML(doc, true);
            if (res) {
                Point p = getPontoMenorSelecionado();
                Point q = new Point((getEditor().getMargem() >= ScrPosicao.x ? ScrPosicao.x : ScrPosicao.x - getEditor().getMargem()),
                        (getEditor().getMargem() >= ScrPosicao.y ? ScrPosicao.y : ScrPosicao.y - getEditor().getMargem()));
                q = tradutorZoom(q);
                final int x = p.x - q.x;
                final int y = p.y - q.y;

                ArrayList<FormaElementar> lst = new ArrayList<>();
                getItensSelecionados().stream().forEach(f -> lst.add(f));
                setSelecionado(null);

                lst.stream().filter(f -> f instanceof Forma).forEach(fe -> {
                    fe.HidePontos(true);
                    fe.DoMove(-x + 2 * fe.distSelecao, -y + 2 * fe.distSelecao);
                    fe.Reposicione();
                    fe.HidePontos(false);
                });

                lst.stream().filter(f -> f instanceof SuperLinha).map(sl -> (SuperLinha) sl).forEach(fe -> {
                    fe.HidePontos(true);
                    final int a = -x + 2 * fe.distSelecao;
                    final int b = -y + 2 * fe.distSelecao;
                    if (fe.getPontaA().getEm() == null && fe.getPontaB().getEm() == null) {
                        fe.DoMove(a, b);
                    } else {
                        if (fe.getPontaA().getEm() != null && fe.getPontaB().getEm() == null) {
                            fe.getPontos().stream().filter(pt -> pt != fe.getPontaA()).forEach(pt -> pt.DoMove(a, b));
                        }
                        if (fe.getPontaB().getEm() != null && fe.getPontaA().getEm() == null) {
                            fe.getPontos().stream().filter(pt -> pt != fe.getPontaB()).forEach(pt -> pt.DoMove(a, b));
                        }
                    }
                    fe.Reposicione();
                    fe.HidePontos(false);
                });

                //Seleciona novamente.
                if (!lst.isEmpty()) {
                    lst.stream().forEach(el -> DiagramaDoSelecao(el, false, true));
                    PromoveToFirstSelect(lst.get(0));
                }
            }
            return true;
        }
        return false;
    }

    public void repaint() {
        if (isCarregando) {
            return;
        }
        master.getBox().repaint();
    }

    public void repaint(Rectangle r) {
        if (isCarregando) {
            return;
        }
        r = ZoomRectangle(r);
        master.getBox().repaint(r);
    }

    /**
     * Get the String residing on the clipboard.
     *
     * @return any text found on the Clipboard; if none found, return an empty String. Código copiado da internet.
     */
    public static String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null)
                && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                util.BrLogger.Logger("ERROR_DIAGRAMA_CLIPBOARD", ex.getMessage());
            }
        }
        return result;
    }

    public static BufferedImage getClipboardContentsImage() {
        BufferedImage result = null;
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableImg = (contents != null)
                && contents.isDataFlavorSupported(DataFlavor.imageFlavor);
        if (hasTransferableImg) {
            try {
                result = (BufferedImage) contents.getTransferData(DataFlavor.imageFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                util.BrLogger.Logger("ERROR_DIAGRAMA_CLIPBOARD", ex.getMessage());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + "{"
                + Diagrama.SaveToXml(this, false) + "}";
    }

    public static Diagrama Factory(TipoDeDiagrama otipo, Editor ed) {
        Diagrama res = null;
        switch (otipo) {
            case tpConceitual:
                res = new diagramas.conceitual.DiagramaConceitual(ed);
                break;
            case tpLogico:
                res = new diagramas.logico.DiagramaLogico(ed);
                break;
            case tpFluxo:
                res = new diagramas.fluxo.DiagramaFluxo(ed);
                break;
            case tpAtividade:
                res = new diagramas.atividade.DiagramaAtividade(ed);
                break;
            case tpEap:
                res = new diagramas.eap.DiagramaEap(ed);
                break;
            case tpLivre:
                res = new diagramas.livre.DiagramaLivre(ed);
                break;
            default:
                res = new Diagrama(ed);
                break;
        }
        ed.NomeieDiagrama(res);
        return res;
    }

    public class AcaoDiagrama extends Acao {

        public AcaoDiagrama(Editor editor, String texto, String ico, String descricao, String command) {
            super(editor, texto, ico, descricao, command);
            comm = command;
        }

        private Diagrama d = null;
        private String comm = "";

        public AcaoDiagrama(Diagrama diag, String texto, String ico, String descricao, String command) {
            this(diag.getEditor(), texto, ico, descricao, command);
            d = diag;
            comm = command;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            if (d != null) {
                d.rodaComando(comm);
            }
        }
    }

    public boolean EstaColandoNoMesmoDigDaOrigem() {
        return ((InfoDiagrama) infoDiagrama).IsTheShame();
    }

    public int AlinhamentoH() {
        return ((InfoDiagrama) infoDiagrama).getAlinhamento_h();
    }

    public int AlinhamentoV() {
        return ((InfoDiagrama) infoDiagrama).getAlinhamento_v();
    }

    public void ExternalSuperAncorador() {
        superAncorador.Posicione(getSelecionado());
    }

    //# Introduzino em 21/04/2017
    public void InfoDiagrama_ToXmlValores(Document doc, Element me) {
    }

    //# Introduzino em 21/04/2017
    public boolean InfoDiagrama_LoadFromXML(Element me, boolean colando) {
        return true;
    }

    /**
     * Mostra apenas os artefatos selecionados e os que estiverem ligados a eles. É setado para false no OnAfterLoad
     */
    private boolean realce = false;

    public boolean isRealce() {
        return realce;
    }

    public void SetRealce(boolean realce) {
        this.realce = realce;
    }

    public void setRealce(boolean realce) {
        if (this.realce == realce) {
            return;
        }
        this.realce = realce;
        if (!realce) {
            getListaDeItens().stream().filter(fo -> fo instanceof FormaElementar).forEach(fo -> fo.setDisablePainted(false));
        } else {
            final ArrayList<FormaElementar> res = new ArrayList<>();
            getListaDeItens().stream().filter(f -> getItensSelecionados().indexOf(f) > -1).forEach(item -> {
                if (item instanceof Forma) {
                    AdicionePrinFromRealce(res, item);
                    //res.add(item);
                    Forma f = (Forma) item;
                    f.getListaDeFormasLigadas().forEach(lfl -> {
                        AdicioneSubsFromRealce(res, lfl);
                    });
                    f.getListaDeLigacoes().stream().filter(l -> l instanceof SuperLinha).forEach(lfl -> {
                        AdicioneSubsFromRealce(res, lfl);
                    });
                } else if (item instanceof SuperLinha) {
                    AdicionePrinFromRealce(res, item);
                    SuperLinha sl = (SuperLinha) item;
                    res.add(sl.getFormaPontaA());
                    res.add(sl.getFormaPontaB());
                }
            });
            getListaDeItens().stream().filter(f -> res.indexOf(f) == -1).forEach(fo -> fo.setDisablePainted(true));
        }
        repaint();
    }

    /**
     * Função acionada no momento de se coletar os artefatos que são parte de um artefato princiapal no momento de realçar o diagrama.<br>
     * É chamada pela função AdicionePrinFromRealce(...)<br>
     *
     * @param res: lista de eleentos coletado.<br>
     * @param item: item a ser analisado.
     */
    protected void AdicioneSubsFromRealce(ArrayList<FormaElementar> res, FormaElementar item) {
        if (item instanceof Forma) {
            Forma f = (Forma) item;
            if (f.isParte()) {
                res.add((Forma) f.getPrincipal());
                return;
            }
        }
        res.add(item);
    }

    /**
     * Função acionada no momento de se coletar os artefatos no momento de realçar o diagrama.<br>
     * <br>
     *
     * @param res: lista de eleentos coletado.<br>
     * @param item: item a ser analisado.
     */
    protected void AdicionePrinFromRealce(ArrayList<FormaElementar> res, FormaElementar item) {
        AdicioneSubsFromRealce(res, item);
    }

    private final String v300 = "3.0.0";
    private final String v310 = "3.1.0";

    public boolean LoadVersao(String fromXml) {
        if (fromXml == null || fromXml.isEmpty()) {
            return false;
        }
        ArrayList<String> ver = new ArrayList<>();
        ver.add(v300);
        ver.add(v310);
        ver.add(Diagrama.VERSAO_A + "." + Diagrama.VERSAO_B + "." + Diagrama.VERSAO_C); //Atual
        
        fromXml = fromXml.trim();
        if (ver.indexOf(ver) == -1) {
            return false;
        }
        
        String[] v = fromXml.split("\\.");
        versaoA = v[0];
        versaoB = v[1];
        versaoC = v[2];
        
//        String va = Diagrama.VERSAO_A;
//        String vb = Diagrama.VERSAO_B;
//        String vc = Diagrama.VERSAO_C;
//        try {
//            String[] v = fromXml.split("\\.");
//            if (v.length != 3) {
//                return false;
//            }
//            int a = Integer.valueOf(v[0]);
//            int b = Integer.valueOf(v[1]);
//            int c = Integer.valueOf(v[2]);
//
//            int A = Integer.valueOf(va);
//            int B = Integer.valueOf(vb);
//            int C = Integer.valueOf(vc);
//
//            if ((a > A) || (a == A && b > B) || (a == A && b == B && c > C)) {
//                return false;
//            }
//
//            versaoA = v[0];
//            versaoB = v[1];
//            versaoC = v[2];
//
//        } catch (Exception ex) {
//            return false;
//        }

        return true;
    }
}