/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho;

import controlador.Editor;
import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.formas.Forma;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Rick
 */
public class FormaElementar extends Elementar {

    private static final long serialVersionUID = -1032848572397943975L;

    // <editor-fold defaultstate="collapsed" desc="Criação">
    /**
     * Futuro
     */
    public FormaElementar() {
    }

    public FormaElementar(FormaElementar pai) {
        super(pai);
        ID = pai.getMaster().getElementarID();
    }

    public FormaElementar(Diagrama master) {
        ID = master.getElementarID();
//        ancorasCode.add(Ancorador.CODE_ANCORAR);
//        ancorasCode.add(Ancorador.CODE_DEL);
        InitElementar(master);
    }

    protected void InitializeSubItens(Diagrama criador) {
        SetFontAndBackColorFromModelo();
        this.subItens = new ArrayList<>();
    }

    private void InitElementar(Diagrama master) {
        setMaster(master);
        if (getMaster() != null) {
            getMaster().Add(this);
            InitializeSubItens(master);
        }
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Campos">
    private List<Elementar> subItens = null;
    //private boolean nulo = false;
    //private boolean atualizando = false;
    private boolean selecionado = false;
    protected boolean PontosIsHide = false;
    protected boolean Selecionavel = true;
    protected int ID = -1;
    private boolean raiseMuda = true;

    /**
     * Poderá ser utilizado para dizer quais objetos podem ou não ser ajustados por comandos na iterface - alinhamento.
     */
    protected boolean AceitaAjusteAutmatico = true;

    public boolean isRaiseMuda() {
        return raiseMuda;
    }

    public void setRaiseMuda(boolean raiseMuda) {
        this.raiseMuda = raiseMuda;
    }

    protected void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public boolean isSelecionavel() {
        return Selecionavel;
    }

    public void setSelecionavel(boolean Selecionavel) {
        this.Selecionavel = Selecionavel;
    }

    public boolean isPontosIsHide() {
        return PontosIsHide;
    }

    protected void setPontosIsHide(boolean PontosIsHide) {
        this.PontosIsHide = PontosIsHide;
    }

//    public boolean isAtualizando() {
//        return atualizando;
//    }
//    public void setAtualizando(boolean atualizando) {
//        this.atualizando = atualizando;
//    }
//    public boolean isNulo() {
//        return nulo;
//    }
//    public void setNulo(boolean nulo) {
//        this.nulo = nulo;
//    }
    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public void setSubItens(ArrayList<Elementar> subItens) {
        this.subItens = subItens;
    }

    public List<Elementar> getSubItens() {
        return subItens;
    }

    /**
     * Define se o objeto ficará fixo no diagrama
     */
    private boolean ancorado = false;

    public boolean isAncorado() {
        return ancorado;
    }

    public void setAncorado(boolean ancorado) {
        if (this.ancorado == ancorado) {
            return;
        }
        this.ancorado = ancorado;
        //InvalidateArea();
    }
    // </editor-fold>

    /**
     * Roda por meio de um comando disparado pelo inspector.
     *
     * @param Tag
     */
    public void DoAnyThing(int Tag) {

    }

    /**
     * Quais cores o objeto usa. Serve para o objeto legenda capturar as cores e mostrar.
     *
     * @param cores
     */
    public void PoluleColors(ArrayList<Color> cores) {
        if (cores.indexOf(getForeColor()) == -1) {
            cores.add(getForeColor());
        }
        if (cores.indexOf(getBackColor()) == -1) {
            cores.add(getBackColor());
        }
    }

    /**
     * Em ordem de criação --> os sub componetes (independente de ser tratado como subitem.
     *
     * @param i: index
     * @return
     */
    public FormaElementar getSub(int i) {
        return null;
    }

    /**
     * Caso mova-se o mouse sobre o objeto
     *
     * @param b: está sobre?
     */
    public void setOverMe(boolean b) {

    }

    // Versao 3.2
    /**
     * No Tree de navegação, quando seleciona o subitem de um artefato.
     * @param index 
     */
    public void DoSubItemSel(int index) {
        
    }

    public enum nomeComandos {

        cmdNothing, cmdLoadImg, cmdDlgLegenda, cmdCallDrawerEditor, cmdExcluirSubItem,
        cmdAdicionarSubItem, cmdDoAnyThing, cmdFonte
    }

    /**
     * Distância entre a borda de um FomraElementar.
     */
    public final int distSelecao = 2;

    public void HidePontos(boolean esconde) {
        setPontosIsHide(esconde);
    }

    public void DoPontoCor(boolean verde) {
    }

    public boolean IntersectPath(Rectangle recsel) {
        return recsel.intersects(getBounds());
    }

    public void RemoveSubItem(Elementar si) {
        si.Destroy();
        subItens.remove(si);
    }

    @Override
    public Elementar IsMeOrMine(Point p) {
        Elementar res;
        for (Elementar el : subItens) {
            res = el.IsMeOrMine(p);
            if (res != null) {
                return res;
            }
        }
        return super.IsMeOrMine(p);
    }

    @Override
    public Elementar IsMeOrMine(Point p, Elementar nor) {
        Elementar res;
        for (Elementar el : subItens) {
            res = el.IsMeOrMine(p, nor);
            if (res != null) {
                return res;
            }
        }
        return super.IsMeOrMine(p, nor);
    }

    @Override
    public Elementar IsMeOrMineBase(Point p, Elementar nor) {
        Elementar res;
        for (Elementar el : subItens) {
            res = el.IsMeOrMineBase(p, nor);
            if (res != null && res instanceof Forma) {
                return res;
            }
        }
        return super.IsMeOrMineBase(p, nor);
    }

    @Override
    public void DoPaint(Graphics2D g) {
        super.DoPaint(g);
        if (isVisible()) {
            PinteSelecao(g);
            //paintAncora(g);
            for (int i = subItens.size() - 1; i > -1; i--) {
                if (subItens.get(i).CanPaint()) {
                    subItens.get(i).DoPaint(g);
                }
            }
        }
    }

//    public void paintAncora(Graphics2D g) {
//    }
    /**
     * Rearranja o componente de acordao com a posição dos pontos que o circunda quando selecionado.
     */
    public void reSetBounds() {
    }

    //public void Recalcule() {
    //}
    /**
     * Rearranja o componente de acordo com a posição dos pontos que o circunda quando selecionado.
     *
     * @param posicao Em qual das oito posições possíveis (0-7) está o ponto movimentado.
     * @param xleft Qual o novo Left do Ponto
     * @param ytop Qual o novo Top do Ponto
     */
    public void reSetBounds(int posicao, int xleft, int ytop) {
    }

    public void PinteSelecao(Graphics2D g) {
    }

    public void DoRaizeReenquadreReposicione() {
    }

    // <editor-fold defaultstate="collapsed" desc="Mouse">
    transient Point down = new Point(0, 0);
    private transient Point inidown = new Point(0, 0);

    protected Point getIniDown() {
        return inidown;
    }

    transient boolean isMouseDown = false;

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        isMouseDown = true && !isAncorado();
        down = new Point(e.getX(), e.getY());
        inidown = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMouseDown = false;
        DoRaizeReenquadreReposicione();
        if (isPontosIsHide()) {
            getMaster().HidePontosOnSelecao(false);
        }
        Point enddown = new Point(e.getX(), e.getY());
        if (!enddown.equals(inidown)) {
            DoMuda();
        }
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isPontosIsHide()) {
            getMaster().HidePontosOnSelecao(true);
        }
        super.mouseDragged(e);
        int X = e.getX();
        int Y = e.getY();
        if (isMouseDown) {
            int movX = X - down.x;
            int movY = Y - down.y;
            if ((movX != 0) || (movY != 0)) {
                DoRaiseMove(movX, movY);
                down.setLocation(e.getPoint());
            }
        }
    }

    @Override
    public void mouseDblClicked(MouseEvent e) {
        super.mouseDblClicked(e);
        ProcessaDblClick(e);
    }
    // </editor-fold>

    @Override
    public void Reposicione() {
        super.Reposicione();
    }

    public void DoRaiseMove(int movX, int movY) {
        DoMove(movX, movY);
    }

    /**
     * Métdo responsável por receber cliques de subItens
     *
     * @param sender disparador do médoto
     * @param dbl o clique é duplo
     * @param e Mouse.
     */
    public void ReciveClick(Elementar sender, boolean dbl, MouseEvent e) {
        //////fazer linha quadrada.
    }

    /**
     * Métdo responsável por lançar o MouseDblClick sem a necessidade de usar o método principal. Padrão próprio: evitar reescrever ou mexer nos métodos principais.
     *
     * @param dbl o clique é duplo
     * @param e Mouse.
     */
    protected void ProcessaDblClick(MouseEvent e) {
    }

    //<editor-fold defaultstate="collapsed" desc="Tratamento da propriedade">
    /**
     * Carrega as propriedades para o Inspector, veja GenerateFullProperty (FormaElementar)
     *
     * @return
     */
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = new ArrayList<>();

        if (getMaster().getEditor().isMostrarIDs()) {
            res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("id", Integer.toString(ID)));
        }
        res.add(InspectorProperty.PropertyFactorySeparador("dimensoes", true));

        res.add(InspectorProperty.PropertyFactoryNumero("left", "setLeft", getLeft()));

        res.add(InspectorProperty.PropertyFactoryNumero("top", "setTop", getTop()));

        res.add(InspectorProperty.PropertyFactoryNumero("width", "setWidth", getWidth()));

        res.add(InspectorProperty.PropertyFactoryNumero("height", "setHeight", getHeight()));

        return res;
    }

    /**
     * Propriedadaes adicionadas em tempo de execução! Não sei se será implementado!
     */
    protected ArrayList<InspectorProperty> propriedadesAdicinais = null;

    /**
     * Método principal para carregar as propriedades de um objeto para o Inspector (FormaElementar)
     *
     * @return
     */
    public ArrayList<InspectorProperty> GenerateFullProperty() {
        ArrayList<InspectorProperty> res = GenerateProperty();
        if (propriedadesAdicinais != null) {
            res.addAll(propriedadesAdicinais);
        }
        return CompleteGenerateProperty(res);
    }

    /**
     * É executado após o método GenerateProperty, veja em GenerateFullProperty (FormaElementar)
     *
     * @param GP
     * @return
     */
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {
        return GP;
    }
    //</editor-fold>

    @Override
    public void DoMuda() {
        if (!isRaiseMuda()) {
            return;
        }
        super.DoMuda();
        if (getMaster() != null) {
            getMaster().DoMuda(this);
        } else if (getCriador() != null) {
            getCriador().DoMuda();
        }
    }

    /**
     * Este objeto pode ser carregdo no InfoDiagrama_LoadFromXML ?
     *
     * @return
     */
    public boolean getIsLoadedFromXML() {
        return true;
    }

    /**
     * Variavel de apoio para salvar em XML a condição de isDisablePainted(). Necessária porque, de outra forma, as cores persistidas seriam apenas a cor configurada para a condição de DisablePainted quando true.
     */
    private boolean snDisablePainted = false; 
            
    /**
     * Para que possa ser utilizado por softwares de terceiros e de diferentes linguagens, preferi este modelo ao de persistir o objeto em XLM usando o writer do próprio java.
     *
     * @param doc
     * @param root
     */
    public final void ToXlm(Document doc, Element root) {
        snDisablePainted = isDisablePainted();
        setDisablePainted(false); //# agora as cores serão salvas da meneira correta!
        Element me = doc.createElement(Editor.getClassTexto(this));
        ToXmlAtributos(doc, me);
        ToXmlValores(doc, me);
        root.appendChild(me);
        setDisablePainted(snDisablePainted);
    }

    /**
     * Serializa o ID dos objetos que estiverem ligados.
     *
     * @param doc
     * @param me
     */
    protected void SerializeListener(Document doc, Element me) {
        Element lst = doc.createElement("Listener");
        if (getListeners() != null) {
            for (ElementarListener el : getListeners()) {
                if (el instanceof FormaElementar) {
                    FormaElementar fe = (FormaElementar) el;
                    lst.appendChild(util.XMLGenerate.ValorRefFormElementar(doc, Editor.getClassTexto(fe), fe));
                }
            }
        }
        me.appendChild(lst);
    }

    /**
     * Refaz as ligações. Deve ser chamado no método CommitXML. Quando a ligação entre dois objetos passa por um objeto SuperLinha, não é necessário serializar as ligações. Os métodos
     * SerializeListener e UnSerializeListener devem ser usados para persistir as ligações entre dois objetos. Ex: FormaArea e os artefatos capturados.
     *
     * @param me
     * @param mapa
     */
    protected void UnSerializeListener(Element me, HashMap<Element, FormaElementar> mapa) {
        Element lig = util.XMLGenerate.FindByNodeName(me, "Listener");
        NodeList nodeLst = lig.getChildNodes();
        for (int s = 0; s < nodeLst.getLength(); s++) {
            Node fstNode = nodeLst.item(s);
            if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fstElmnt = (Element) fstNode;
                String xid = fstElmnt.getAttribute("ID");
                FormaElementar resA = util.XMLGenerate.FindWhoHasID(xid, mapa);
                if (resA == null) {
                    continue;
                }
                PerformLigacao(resA, true);
            }
        }
    }

    /**
     * Dado um objeto, este método gera XLM para sua propriedades a partir de campos determinados
     *
     * @param doc
     * @param me
     */
    protected void ToXmlValores(Document doc, Element me) {
        me.appendChild(util.XMLGenerate.ValorRect(doc, "Bounds", getBounds()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "DisablePainted", snDisablePainted));
    }

    /**
     * Serializa os atributos do objeto.
     *
     * @param doc
     * @param me
     */
    protected void ToXmlAtributos(Document doc, Element me) {
        me.setAttribute("ID", Integer.toString(ID));
    }

    /**
     * Carrega um elemento a partir de um código XML.
     *
     * @param me
     * @param colando
     * @return
     */
    public boolean LoadFromXML(Element me, boolean colando) {
        String aID = me.getAttribute("ID");
        if (!colando) {
            ID = Integer.valueOf(aID);
        }
        Rectangle bounds = util.XMLGenerate.getValorRectFrom(me, "Bounds");
        if (bounds != null) {
            /**
             * Referencia igual em Ligacao.LoadFromXML
             */
            SetBounds(bounds);
        }
        boolean x = util.XMLGenerate.getValorBooleanFrom(me, "DisablePainted");
        SetDisablePainted(x);
        return true;
    }

    /**
     * É executado após o carregamento do XML (quer colando, quer carregando de uma arquivo).
     *
     * @param me
     * @param mapa
     * @return
     */
    public boolean CommitXML(Element me, HashMap<Element, FormaElementar> mapa) {
        reSetBounds();
        BringToFront();
        return true;
    }

    /**
     * Dado um objeto que não pode ser apagado por conta de uma dependência, o método AskToDelete pede para que a dependência seja desfeita (caso em que importará o retorno do método: true = a
     * dependência foi desfeita), ou que o objeto providencie a sua auto-exclusão, caso em que o retorno seria irrelevante
     *
     * @return
     */
    public boolean AskToDelete() {
        return false;
    }

    /**
     * Para ser executado pelo Inspector.
     *
     * @param idx: tag ou índice do componente a ser removido.
     */
    public void ExcluirSubItem(int idx) {

    }

    /**
     * Para ser executado pelo Inspector.
     *
     * @param idx: posição onde o subitem será incluído.
     */
    public void AdicionarSubItem(int idx) {

    }

    //<editor-fold defaultstate="collapsed" desc="Ancorador">
//    /**
//     * Quais botões âncoras (botões que ficam ao lado do artefato selecionado no diagrama) deverão ser mostrados.
//     */
//    private final ArrayList<Integer> ancorasCode = new ArrayList<>();

    /**
     * Quais botões âncoras (botões que ficam ao lado do artefato selecionado no diagrama) deverão ser mostrados.
     *
     * @return
     */
    public ArrayList<Integer> getAncorasCode() {
        Integer[] ancorasCode = new Integer[] {Ancorador.CODE_ANCORAR, Ancorador.CODE_DEL};
        return new ArrayList<>(Arrays.asList(ancorasCode));
    }

    /**
     * *
     * É chamado quando clica-se em um dos botões ancorados no artefato selecionado
     *
     * @param cod
     */
    public void runAncorasCode(int cod) {
        switch (cod) {
            case Ancorador.CODE_ANCORAR:
                setAncorado(!isAncorado());
                InvalidateArea();
                getMaster().PerformInspector();
                break;
            case Ancorador.CODE_DEL:
                getMaster().ClearSelect();
                getMaster().setSelecionado(this);
                getMaster().deleteSelecao();
                break;
        }
    }

    /**
     * *
     * É chamado quando um artefato é selecionado no diagrama
     *
     * @param c - diz o que desenhar para cada um dos botões de ancoragem.
     * @return o caminho para desenho da imagem: getMaster().getEditor().getControler().ImagemDeDiagrama.get( RETURN ).getImage()
     */
    public String WhatDrawOnAcorador(Integer c) {
        String res = "diagrama.ancordor.0.img";
        switch (c) {
            case Ancorador.CODE_ANCORAR:
                res = isAncorado() ? "diagrama.ancordor.0.0.img" : "diagrama.ancordor.0.img";
                break;
            case Ancorador.CODE_DEL:
                res = isAncorado() ? "diagrama.ancordor.1.img" : "diagrama.ancordor.1.img";
                break;
        }
        return res;
    }
    //</editor-fold>
    
}
