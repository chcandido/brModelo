/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho;

import controlador.Diagrama;
import desenho.formas.Forma;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import util.Constantes;

/**
 *
 * @author Rick
 */
public class Elementar implements ElementarListener, Serializable {
    
    /**
     * Tenta garantir a mudança de versões
     */
    static final long serialVersionUID = 2103191058069777L;

    private Diagrama master;
    private boolean visible = true;
    private Font font;
    private Color backColor = Color.LIGHT_GRAY;
    private int left = 0;
    private int top = 0;
    private int width = 20;
    private int height = 20;
    private Dimension size = new Dimension(20, 20);
    public static Color defaultColor = Color.BLACK;
    private Color foreColor = Elementar.defaultColor;
    private Cursor cursor = Cursor.getDefaultCursor();
    private Rectangle clientRectangle = new Rectangle(0, 0, 20, 20);

    /**
     * Para o envio de mensagens, se true.
     */
    private boolean stopRaize = false;
    private boolean canPaint = true;

    /**
     * Este objeto pode ser apagado?
     * @return 
     */
    private boolean canBeDeleted = true;


    // <editor-fold defaultstate="collapsed" desc="Campos / Get e Set">
    /**
     * Este objeto pode ser apagado?
     * @return 
     */
    public boolean isCanBeDeleted() {
        return canBeDeleted;
    }
    
    /**
     * Este objeto pode ser apagado?
     * @return 
     */
    public void setCanBeDeleted(boolean canBeDeleted) {
        this.canBeDeleted = canBeDeleted;
    }

    /**
     * Diagrama 
     * @return the master
     */
    public Diagrama getMaster() {
        return master;
    }

    /**
     * Diagrama 
     * @param master the master to set
     */
    public void setMaster(Diagrama master) {
        if (this.master == master) {
            return;
        }
        Diagrama bkp = this.master;
        this.master = master;
        if ((master == null) && (bkp != null) && (isVisible())) {
            bkp.repaint();
        } else {
            InvalidateArea();
        }
        this.master = master;
    }

    /**
     * Para o envio de mensagens, se true.
     */
    public boolean isStopRaize() {
        return stopRaize;
    }

    /**
     * Para o envio de mensagens, se true.
     */
    public void setStopRaize(boolean stopRaize) {
        this.stopRaize = stopRaize;
    }

    public boolean CanPaint() {
        return isCanPaint();
    }

    public boolean isCanPaint() {
        return canPaint && isVisible();
    }

    public void setCanPaint(boolean canPaint) {
        this.canPaint = canPaint;
    }

    /**
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * @param font the font to set
     */
    public void setFont(Font font) {
        this.font = font;
        InvalidateArea();
    }

    /**
     * @return the backColor
     */
    public Color getBackColor() {
        return isDisablePainted()? disabledColor : backColor;
    }

    /**
     * @param backColor the backColor to set
     */
    public void setBackColor(Color backColor) {
        if (this.backColor != backColor) {
            this.backColor = backColor;
            InvalidateArea();
        }

    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            if (!visible) {
                DoMasterInvalidate();
            } else {
                InvalidateArea();
            }
        }

    }

    /**
     * @return the location
     */
    public Point getLocation() {
        return new Point(left, top);
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Point location) {
        this.left = location.x;
        this.top = location.y;
        RecrieBounds();
    }

    public void setLocation(int x, int y) {
        this.left = x;
        this.top = y;
        RecrieBounds();
    }

    /**
     * @return the left
     */
    public int getLeft() {
        return left;
    }

    /**
     * @return the left + width
     */
    public int getLeftWidth() {
        return left + width;
    }

    /**
     * @return the top + heigth
     */
    public int getTopHeight() {
        return top + height;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(int left) {
        if (this.left != left) {
            this.left = left;
            RecrieBounds();
        }
    }

    /**
     * @return the top
     */
    public int getTop() {
        return top;
    }

    /**
     * @param top the top to set
     */
    public void setTop(int top) {
        if (this.top != top) {
            this.top = top;
            RecrieBounds();
        }
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        if (this.width != width) {
            this.width = width;
            RecrieBounds();
        }
    }

    /**
     * @return the heigth
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param heigth the heigth to set
     */
    public void setHeight(int height) {
        if (this.height != height) {
            this.height = height;
            RecrieBounds();
        }
    }

    /**
     * @return the size
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Dimension size) {
        if (this.size != size) {
            this.size = size;
            width = size.width;
            height = size.height;
            RecrieBounds();
        }
    }

    /**
     * @return the foreColor
     */
    public Color getForeColor() {
        return isDisablePainted()? disabledColor : foreColor;
    }

    /**
     * @param foreColor the foreColor to set
     */
    public void setForeColor(Color foreColor) {
        if (this.foreColor != foreColor) {
            this.foreColor = foreColor;
            InvalidateArea();
        }
    }
    /**
     * Força mudança de cor sem lançar um MUDA.
     * @param foreColor the foreColor to set
     */
    public void SetForeColor(Color foreColor) {
            this.foreColor = foreColor;
            InvalidateArea();
    }
    
    /**Altera foreColor sem repintar
     * @param foreColor the foreColor to set
     */
    public void setForeColorWithOutRepaint(Color foreColor) {
        this.foreColor = foreColor;
    }

    /**
     * @return the cursor
     */
    public Cursor getCursor() {
        return cursor;
    }

    /**
     * @param cursor the cursor to set
     */
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * @return the clientRectangle
     */
    public Rectangle getClientRectangle() {
        return clientRectangle.getBounds();
    }

    /**
     * @param clientRectangle the clientRectangle to set
     */
    public void setClientRectangle(Rectangle clientRectangle) {
        if (clientRectangle == null) {
            clientRectangle = new Rectangle(0, 0, 0, 0);
        }
        SetBounds(clientRectangle.x, clientRectangle.y, clientRectangle.width, clientRectangle.height);
        //this.clientRectangle = clientRectangle;
    }

    /**
     * Retorna Left, Top, Width, Height.
     * @return ClientRectangle.
     */
    public Rectangle getBounds() {
        return getClientRectangle();
    }

    /**
     * @param bounds the bounds to set
     */
    public void setBounds(Rectangle bounds) {
        setClientRectangle(bounds);
    }
    /**
     * @return the criador
     */
    private FormaElementar criador;

    public FormaElementar getCriador() {
        return criador;
    }

    // <editor-fold defaultstate="collapsed" desc="Trabalho com Partes">

    public boolean isParte() {
        return false;
    }

    public boolean isComposto() {
        return false;
    }

    public Elementar ProcessaComposicao() {
        return this;
    }

    /**
     * Verifica se um ponto pertence à um sub-componente ou ao Principal.
     * No caso do Elementar, retorna sempre o próprio. Será reescrito no futuro de modo a retornar
     * eventuais sub-componentes.
     */
    public Elementar ProcessaComposicao(Point pt) {
        return this;
    }

    public Elementar getPrimeiroSubComponente() {
        return null;
    }

    public Elementar getPrincipal() {
        return this;
    }
// </editor-fold>

    // </editor-fold>
    public void SetBounds(int left, int top, int width, int height) {
        setBounds(left, top, width, height);
    }

    public void setBounds(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        RecrieBounds();
    }

    protected void SetFontAndBackColorFromModelo() {
        this.font = Elementar.CloneFont(this.master.getFont());
        this.backColor = this.master.getBackground();
    }

    /**
     * Chama o método responsável pela repintagem de todo o Diagrama
     */
    protected void DoMasterInvalidate() {
        if (master != null) {
            master.repaint(); //invalida-se todo o master.
        }
    }

    public void SetBounds(Rectangle rect) {
        setBounds(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * Cria/Recria o retângulo do componente.
     */
    public void RecrieBounds() {
        width = width < 2 ? 2 : width;
        height = height < 2 ? 2 : height;

        clientRectangle = new Rectangle(left, top, width, height);

        if (isStopRaize()) {
            return;
        }

        ReSized();
        if (visible) {
            DoMasterInvalidate();
            //InvalidateArea();
        }
        //DoMuda();
    }

    /**
     * Movimenta o componente.
     * Difere de setLocation() por que pode lançar evento de movimentação de
     * possíveis componentes selecionados
     * @param movX Quanto deve ser adicionado a Left
     * @param movY Quanto deve ser adicionado a Top
     */
    public void DoMove(int movX, int movY) {
        setLocation(new Point(left + movX, top + movY));
    }

    public static Font CloneFont(Font origem) {
        return new Font(origem.getName(), origem.getStyle(), origem.getSize());
    }

    /**
     * Nome da função de repintura em C#. Em Java sera repaint.
     * Repinta todo o modelo
     */
    public void Invalidate() {
        if (master != null) {
            master.repaint();
        }
    }

    /**
     * Função de repintura.
     * Repinta apenas a área do componente
     */
    public void repaint() {
        if (master != null) {
            master.repaint(clientRectangle);
        }
    }

    /**
     * Nome da função de repintura em C#. Em Java sera repaint.
     * Repinta apenas a área do componente
     * @param area: Área a ser repintada.
     */
    public void InvalidateArea(Rectangle area) {
        if (master != null) {
            master.repaint(area);
        }
    }

    /**
     * Nome da função de repintura em C#. Em Java sera repaint.
     * Repinta apenas a área do componente
     */
    public void InvalidateArea() {
        if (master != null) {
            Rectangle r = (Rectangle)clientRectangle.clone();
            r.grow(1, 1);
            master.repaint(r);
        }
    }

    /**
     * o Java obriga a existência de um construtor vazio, caso no futuro queira introduzir construtores independentes
     */
    public Elementar() {
        //o Java obriga a existência de um construtor vazio, caso no futuro queira introduzir construtores independentes
    }

    /**
     * Cria um elemento subitem da FormaElementar pai.
     * @param pai FormaElementar
     */
    public Elementar(FormaElementar pai) {
        InitElementar(pai);
    }

    private void InitElementar(FormaElementar pai) {
        criador = pai;
        if (criador != null) {
            this.master = criador.getMaster();
            criador.getSubItens().add(this);
            SetFontAndBackColorFromModelo();
        }
        this.cursor = new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR);
    }

    protected void DoMuda() {
    }

    public void SendToBack() {
        List<Elementar> lista;
        if (criador != null) {
            lista = criador.getSubItens();
        } else {
            lista = (master != null ? master.getSubItens() : null);
        }
        if (lista == null) {
            return;
        }

        int tmp = lista.indexOf(this);
        if ((tmp == lista.size() - 1) || (tmp == -1)) {
            return; // já é o último!
        }
        lista.remove(this);
        lista.add(this);
    }

    public void BringToFront() {
        List<Elementar> lista;
        if (criador != null) {
            lista = criador.getSubItens();
        } else {
            lista = (master != null ? master.getSubItens() : null);
        }
        if (lista == null) {
            return;
        }

        if (lista.indexOf(this) < 1) {
            return; // já é o primeiro!
        }
        lista.remove(this);
        lista.add(0, this);
    }

    /**
     * Processo fundamental que verifica se um ponto está na área di componente.
     * @param p Point
     * @return 
     */
    public boolean IsMe(Point p) {
        if (!isVisible()) {
            return false;
        }
        return clientRectangle.contains(p);
    }

    /**
     * Verifica se um ponto está na área do componente
     * @param p Ponto
     * @return Retorna o componente ou subitem se o ponto o pertencer.
     */
    public Elementar IsMeOrMine(Point p) {
        if (IsMe(p)) {
            return this;
        } else {
            return null;
        }
    }

    /**
     * Verifica se um ponto está na área do componente
     * @param p Ponto
     * @param nor - exceto
     * @return Retorna o componente (ou subitem nos override!) se o ponto o pertencer.
     */
    public Elementar IsMeOrMine(Point p, Elementar nor) {
        if ((this != nor) && IsMe(p)) {
            return this;
        } else {
            return null;
        }
    }

    /**
     * Verifica se um ponto está na área do componente
     * @param p Ponto
     * @param nor - exceto
     * @return Retorna o componente (ou subitem nos override!) se o ponto o pertencer e se for instância de forma.
     */
    public Elementar IsMeOrMineBase(Point p, Elementar nor) {
        if ((this != nor) && IsMe(p) && this instanceof Forma) {
            return this;
        } else {
            return null;
        }
    }

    /**
     * Método principal de desenho
     * @param g = Graphics
     */
    public void DoPaint(Graphics2D g) {
        g.setFont(getFont());
    }

    /**
     * Recoloca o componente na área útil do Diagrama
     * @return 
     */
    public boolean Reenquadre() {
        if (master == null) return false;
        int ALeft = left < 0 ? 0: left;
        int ATop = top < 0 ? 0: top;
        
        if ((left + width) > master.getWidth()) {
            ALeft = master.getWidth() - width;
        }
        if ((top + height) > master.getHeight()) {
            ATop = master.getHeight() - height;
        }
        if ((ALeft != left) || (ATop != top)) {
            BeforeReenquadre(ALeft - left, ATop - top);
            SetBounds(ALeft, ATop, width, height);
            Reposicione();
            return true;
        }
        return false;
    }

    /**
     * É executado instantes  antes de reenquadrar o objeto pelo método Reenquadre
     * @param movidoX quanto será movido com o método Reenquadre
     * @param movidoY quanto será movido com o método Reenquadre
     */
    protected void BeforeReenquadre(int movidoX, int movidoY) {
    }

    /**
     * Reposiciona subitens do componente
     * Pode ser, por exemplo, pontos de uma Forma.
     */
    protected void Reposicione() {
    }

    // <editor-fold defaultstate="collapsed" desc="Eventos do mouse">
    public void mouseClicked(MouseEvent e) {
    }

    public void mouseDblClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        //alterado em 16/12/2013 - não sei se terá algum efeito
//        if (criador != null) {
//            criador.BringToFront();
//        }
//        BringToFront();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        if (master == null) return;
        if (master.getCursor().getType() != this.getCursor().getType()) {
            master.setCursor(new Cursor(this.getCursor().getType()));
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Listiner Interface">
    /**
     * Listener principal do componente, usado para tratar as mensagens entre os objetos
     */
    private List<ElementarListener> listeners;

    public List<ElementarListener> getListeners() {
        return listeners;
    }

    private void addElementarListener(ElementarListener el) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(el);
    }

    private void removeElementarListener(ElementarListener el) {
        if (listeners != null) {
            listeners.remove(el);
        }
    }

    @Override
    public final void SendNotificacao(int cod) {
        if (isStopRaize()) {
            return;
        }
        if (listeners != null) {
            ElementarEvento evt = new ElementarEvento(this, cod);
            int i = 0;
            while (i < listeners.size()) { 
                listeners.get(i).ReciveNotificacao(evt);
                i++;
            }
            //            nos eventos de destroy: java.util.ConcurrentModificationException     
            //            for (ElementarListener el : listeners) {
            //                el.ReciveNotificacao(evt);
            //            }
        }
    }

    public final void SendNotificacao(Object msg, int cod) {
        if (isStopRaize()) {
            return;
        }
        if (listeners != null) {
            ElementarEvento evt = new ElementarEvento(this, msg, cod);
            for (ElementarListener el : listeners) {
                el.ReciveNotificacao(evt);
            }
        }
    }

    /**
     * Cria um vincul o entre dois artefatos de forma que um possa enviar notificações para o outro
     * @param emQuem
     * @param isIN = true para ligar e false para desligar.
     */
    public void PerformLigacao(Elementar emQuem, boolean isIN) {
        if (emQuem == null || emQuem == this) {
            return;
        }
        if (isIN) {
            addElementarListener(emQuem);
            emQuem.addElementarListener(this);
        } else {
            removeElementarListener(emQuem);
            emQuem.removeElementarListener(this);
        }
    }

    @Override
    public void ReciveNotificacao(ElementarEvento evt) {
        int i = evt.getCod();
        if (i == Constantes.Operacao.opDestroy) {
            removeElementarListener(evt.getSender());
        }
    }
    // </editor-fold>
    
    private boolean destruido = false;
    
    public boolean Destroy() {
        destruido = true;
        if (master == null || master.IsStopEvents) {
            return false;
        }
        SendNotificacao(Constantes.Operacao.opDestroy);
        //visible = false;
        return true;
    }

    /**
     * Lançado todas as vezes que o tamanho do elementar mudar.
     */
    public void ReSized() {
    }

    /**
     * Informa se o método Destroy do objeto foi chamado.
     * @return destruido?
     */
    public boolean isDestruido() {
        return destruido;
    }
    
    /**
     * Função que altera a condição de visibilidade do componente sem, no entanto, forçar repinturas
     */
    public final void SetVisible(boolean sn) {
        visible = sn;
    }
    

    protected Color disabledColor = new Color(221, 221, 221);
    
    /**
     * Mostra os artefatos em cor padrão ao ser disabilitado na pintura.
     */
    private boolean disablePainted = false;
    //protected boolean disablePainted = false;

    public boolean isDisablePainted() {
        return disablePainted;
    }

    public void setDisablePainted(boolean disablePainted) {
//        if (this.disablePainted == disablePainted) {
//            return;
//        }
        this.disablePainted = disablePainted;
    }
    
    public final void SetDisablePainted(boolean disablePainted) {
        this.disablePainted = disablePainted;
    }
}
