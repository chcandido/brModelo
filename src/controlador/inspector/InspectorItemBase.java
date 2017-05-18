/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

import controlador.BaseControlador;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author ccandido
 */
public class InspectorItemBase extends BaseControlador {

    //<editor-fold defaultstate="collapsed" desc="Base do componente">
    protected Inspector Criador;
    private boolean selecionado = false;
    protected boolean falhou = false;

    public boolean isSelecionado() {
        return selecionado;
    }

    protected final void getCorParaTexto(Graphics g) {
        if (falhou) {
            g.setColor(Color.red);
            return;
        }
        if (!CanEdit()) {
            g.setColor(Color.LIGHT_GRAY);
            return;
        }

        g.setColor(Color.black);
//        if (isSelecionado()) {
//            g.setColor(Color.BLUE);
//        }
    }

    protected void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
        if (ondeEditar != null) {
            if (selecionado) {
                int tam = (getWidth() / 2) - 2;
                ondeEditar.setBounds(tam + 3, 1, tam - 2, Criador.altura - 2);
                add(ondeEditar);//, new org.netbeans.lib.awtextra.AbsoluteConstraints(tam + 3, 1, tam - 2, Criador.altura - 2));
                ondeEditar.setVisible(true);
                ondeEditar.requestFocus();
                validate();
                ondeEditar.setEnabled(CanEdit());
                if (ondeEditar instanceof JTextField) {
                    ((JTextField) ondeEditar).selectAll();
                }
            } else {
                remove(ondeEditar);
            }
        }
        repaint();
    }

    public InspectorItemBase(Inspector criador) {
        this();
        Criador = criador;
        setDoubleBuffered(true);
    }

    public InspectorItemBase() {
        super();
        setLayout(null);//new org.netbeans.lib.awtextra.AbsoluteLayout());
        setFocusable(true);
    }
    private boolean canEdit = true;

    public boolean CanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        if (propriedade.isForceDisable()) {
            canEdit = false;
        } else if (propriedade.isForceEnable()) {
            canEdit = true;
        }
        if (this.canEdit != canEdit) {
            this.canEdit = canEdit;
            if (ondeEditar != null && selecionado) {
                ondeEditar.setEnabled(CanEdit());
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
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

        paint2D(Canvas);
    }

    public void paint2D(Graphics2D g) {
        int esq = (int) (getWidth() * Criador.getDivisor()) - 2;
        int dir = getWidth() - (esq + 2);
        if (isSelecionado() && ondeEditar != null && ondeEditar.isVisible() && ondeEditar.getWidth() != dir - 2) {
            Dimension d = new Dimension(dir - 2, Criador.altura - 2);
            ondeEditar.setPreferredSize(d);
            ondeEditar.setSize(d);
            ondeEditar.setLocation(esq + 3, 1);
            ondeEditar.repaint();
            ondeEditar.validate();
        }
//        int tam = (getWidth() / 2) - 2;
//        if (isSelecionado() && ondeEditar != null && ondeEditar.isVisible() && ondeEditar.getWidth() != tam) {
//            Dimension d = new Dimension(tam - 2, Criador.altura - 2);
//            ondeEditar.setPreferredSize(d);
//            ondeEditar.setSize(d);
//            ondeEditar.setLocation(tam + 3, 1);
//            ondeEditar.repaint();
//            ondeEditar.validate();
//        }

        paintBase(g);
    }

    protected void paintBase(Graphics2D g) {
        Rectangle r = this.getBounds();
        int esq = (int) (r.width * Criador.getDivisor());
        int dir = r.width - esq;
        area = new Rectangle(esq - 2, 0, 4, r.height - 1);

        if (!isSelecionado()) {
            g.setColor(Color.GRAY);
            g.drawRoundRect(0, 0, r.width - 1, r.height - 1, 10, 10);
            g.drawLine(esq, 0, esq, r.height - 1);

            g.setColor(Color.BLACK);

            Rectangle bkp = g.getClipBounds();
            g.clipRect(0, 0, esq - 1, r.height);

            getCorParaTexto(g);
            g.drawString(getTexto(), (Criador.espaco * 2) + 1, (int) (r.height * 0.72));
            //g.setColor(Color.BLACK);
            g.setClip(bkp);
            g.clipRect(esq + 1, 0, dir - 1, r.height);
            //getCorParaTexto(g);
            g.drawString(getTransValor().replaceAll("\n", " | "), esq + (Criador.espaco * 2) + 1, (int) (r.height * 0.72));

            g.setClip(bkp);

        } else {
            g.setColor(Color.BLACK);
            g.drawRoundRect(0, 0, r.width - 1, r.height - 1, 10, 10);
            g.drawLine(esq, 0, esq, r.height - 1);
            Rectangle bkp = g.getClipBounds();

            g.setFont(new Font(this.getFont().getFontName(), Font.BOLD, getFont().getSize()));
            g.clipRect(0, 0, esq - 1, r.height);

            g.setColor(Color.BLACK);

            getCorParaTexto(g);

            g.drawString(getTexto(), (Criador.espaco * 2) + 1, (int) (r.height * 0.72));

            g.setClip(bkp);

        }
//        Rectangle r = this.getBounds();
//        int tmp = r.width / 2;
//        if (!isSelecionado()) {
//            g.setColor(Color.GRAY);
//            g.drawRoundRect(0, 0, r.width - 1, r.height - 1, 10, 10);
//            g.drawLine(tmp, 0, tmp, r.height - 1);
//
//            g.setColor(Color.BLACK);
//
//            Rectangle bkp = g.getClipBounds();
//            g.clipRect(0, 0, tmp - 1, r.height);
//
//            getCorParaTexto(g);
//            g.drawString(getTexto(), (Criador.espaco * 2) + 1, (int) (r.height * 0.72));
//            //g.setColor(Color.BLACK);
//            g.setClip(bkp);
//            g.clipRect(tmp + 1, 0, tmp - 1, r.height);
//            //getCorParaTexto(g);
//            g.drawString(getTransValor().replaceAll("\n", " | "), tmp + (Criador.espaco * 2) + 1, (int) (r.height * 0.72));
//
//            g.setClip(bkp);
//
//        } else {
//            g.setColor(Color.BLACK);
//            g.drawRoundRect(0, 0, r.width - 1, r.height - 1, 10, 10);
//            g.drawLine(tmp, 0, tmp, r.height - 1);
//            Rectangle bkp = g.getClipBounds();
//
//            g.setFont(new Font(this.getFont().getFontName(), Font.BOLD, getFont().getSize()));
//            g.clipRect(0, 0, tmp - 1, r.height);
//
//            g.setColor(Color.BLACK);
//
//            getCorParaTexto(g);
//
//            g.drawString(getTexto(), (Criador.espaco * 2) + 1, (int) (r.height * 0.72));
//
//            g.setClip(bkp);
//
//        }
    }

    private JComponent ondeEditar;

    public JComponent getOndeEditar() {
        return ondeEditar;
    }

    public void setOndeEditar(JComponent ondeEditar) {
        if (this.ondeEditar != null) {
            remove(ondeEditar);
        }
        this.ondeEditar = ondeEditar;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Propriedades">
    private InspectorProperty propriedade = null;

    public InspectorProperty getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(InspectorProperty propriedade) {
        this.propriedade = propriedade;
        setCanEdit(true);

        if (Criador.getEditor().isMostrarTooltips()) {
            this.setToolTipText(getTransValor());
        }
    }

    public String getTexto() {
        if (propriedade == null) {
            return "";
        }
        return propriedade.caption;
    }

    public String getTransValor() {
        if (propriedade == null) {
            return "";
        }
        return Traduza(propriedade.valor_string);
    }

    public String getValor() {
        if (propriedade == null) {
            return "";
        }
        return propriedade.valor_string;
    }

    public void setValor(String valor) {
        if (propriedade == null) {
            return;
        }
        propriedade.valor_string = valor;
    }

    public void setFalhou(boolean b) {
        if (this.falhou != b) {
            this.falhou = b;
            repaint();
        }
    }

    public int getTag() {
        return propriedade.getTag();
    }

    public void setTag(int tag) {
        propriedade.setTag(tag);
    }
    //</editor-fold>

    public String Traduza(String texto) {
        return texto;
    }

    public static InspectorItemBase SuperFactory(Inspector principal, InspectorProperty pprt) {
        InspectorItemBase ib = null;
        if (pprt.tipo == InspectorProperty.TipoDeProperty.tpNothing) {
            return null;
        }
        switch (pprt.tipo) {
            case tpBooleano:
                ib = new InspectorItemSN(principal);
                break;
            case tpCor:
                ib = new InspectorItemCor(principal);
                break;
            case tpTextoLongo:
                //ib = new InspectorItemTextoLongo(principal);
                ib = new InspectorItemExtender(principal, InspectorExtenderEditor.TipoDeAcao.tpAcaoDlgTexto);
                break;
            case tpApenasLeituraTexto:
                //ib = new InspectorItemApenasLeitura(principal);
                ib = new InspectorItemExtender(principal, InspectorExtenderEditor.TipoDeAcao.tpReadOnlyTexto);
                break;
            case tpApenasLeituraCor:
                //ib = new InspectorItemApenasLeitura(principal, true);
                ib = new InspectorItemExtender(principal, InspectorExtenderEditor.TipoDeAcao.tpReadOnlyCor);
                break;
            case tpSelecObject:
                ib = new InspectorItemExtender(principal, InspectorExtenderEditor.TipoDeAcao.tpAcaoSelectObj);
                //ib = new InspectorItemSelectObj(principal);
                break;
            case tpCommand:
                ib = new InspectorItemExtender(principal, InspectorExtenderEditor.TipoDeAcao.tpAcaoCommand);
                //ib = new InspectorItemSelectObj(principal);
                break;
            case tpSeparador:
                ib = new InspectorItemSeparador(principal);
                break;
            case tpMenu:
                ib = new InspectorItemMenu(principal);
                break;
            default:
                ib = new InspectorItemTexto(principal);
                break;
        }
        ib.setPropriedade(pprt);
        return ib;
    }

    protected void performGroupSelect() {
    }

    public void RefreshGrupoCanEdit() {
        if (propriedade.agrupada == null || !CanEdit()) {
            return;
        }
        Criador.MakeCanEditGrupo(this);
    }

    private Rectangle area = null;

    public Rectangle getArea() {
        return area;
    }

    public void setArea(Rectangle area) {
        this.area = area;
    }

    transient boolean isMouseDown = false;

    @Override
    public void mouseReleased(MouseEvent e) {
        if (isMouseDown) {
//            setCursor(Cursor.getDefaultCursor());
            isMouseDown = false;
        }
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        int X = e.getX();
        if (isMouseDown) {
            Caucule(X);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Criador.PerformSelect(this);
        if (area != null && area.contains(e.getPoint())) {
//            setCursor(new Cursor(Cursor.HAND_CURSOR));
            isMouseDown = true;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e); //To change body of generated methods, choose Tools | Templates.
        if (area != null && area.contains(e.getPoint())) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    public void Caucule(int movido) {
        if (movido > getWidth() - 20) {
            movido = getWidth() - 20;
        }
        if (movido < 20) {
            movido = 20;
        }
        double x = ((double) movido) / getWidth();
        Criador.setDivisor(x);
    }
}
