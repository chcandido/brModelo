/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author ccandido
 */
public class Mostrador extends BaseControlador {

    public Mostrador() {
        super();
    }
    private Editor master;
    private final ArrayList<Rectangle> areas = new ArrayList<>();
    private final int larg = 130;
    private final int largPonta = 10;
    private final int dist = 2;
    private final int tabRecuo = largPonta + dist;
    private final int mover = 20;
    private int scroll = tabRecuo;
    private int selectedIndex = 0;

    public final static String Img = "Controler.interface.mostrador.fechar.img";

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        Construa();
        if (areas.isEmpty() || selectedIndex == -1) {
            return;
        }
        setTableVisible(selectedIndex);
    }

    public ArrayList<Diagrama> getDiagramas() {
            if (master != null) {
                return master.getDiagramas();
            }
            return null;
    }

    public Editor getMaster() {
        return master;
    }

    public void setMaster(Editor master) {
        this.master = master;
    }

    /**
     * Deve ser chamada apenas quando inserir ou excluir um modelo.
     * @param selIdex
     */
    public void Reset(final int selIdex) {
        setSelectedIndex(selIdex);
        repaint();
    }

    public void Construa() {
        areas.clear();
        if (getDiagramas() == null) {
            return;
        }
        areas.add(new Rectangle(0, 0, largPonta, getHeight() - 1));
        areas.add(new Rectangle(getWidth() - largPonta, 0, largPonta, getHeight() - 1));
        int y = 0;

        int sx = scroll;
        for (Diagrama m : getDiagramas()) {
            areas.add(new Rectangle(sx, y, larg, getHeight() - y));
            sx += larg + dist;
        }
    }

    private Rectangle regFechar = null;

    private int fh = -1;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        regFechar = null;
        
        RenderingHints renderHints
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        renderHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Graphics2D  g2 = (Graphics2D) g;

         g2.addRenderingHints(renderHints);

         g2.setStroke(new BasicStroke(
                1f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        if (fh == -1) {
            FontMetrics fm = g2.getFontMetrics();
            fh = fm.getHeight() - fm.getDescent();
        }
        Construa();
        if (areas.isEmpty()) {
            return;
        }
        g2.setColor(new Color(204, 204, 255));
        g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        g2.setColor(Color.gray);
        int imgw = 16 + 2 + 4;
        int i = 1;
        int txL = (getHeight() / 2) + (fh / 2);
        for (Diagrama m : getDiagramas()) {
            i++; // i = 2.
            g2.setColor(Color.gray);
            //g2.fill(r);
            Rectangle r = areas.get(i);
            //g2.drawImage(, i, i, master);
            boolean esse = false;
            if (overRNow != null && overRNow.equals(r)) {
                esse = true;
            }
            if (selectedIndex == (i - 2)) {
                r = new Rectangle(r.x, r.y + dist, r.width, r.height);
                g2.setColor(new Color(204, 204, 255));
                g2.drawRect(r.x - 1, r.y - 1, r.width + 1, r.height);

                g2.setColor(Color.WHITE);
                g2.fill(r);

                g2.setColor(Color.BLACK);
            } else {
                g2.setColor(new Color(204, 204, 255));
                g2.drawRect(r.x, r.y, r.width, r.height - 1);
                if (overRNow != null && overRNow.equals(r)) {
                    g2.setColor(Color.WHITE);
                    g2.fill(new Rectangle(r.x + 1, r.y + 1, r.width - 2, r.height - 3));
                }
                g2.setColor(Color.gray);
            }
            g2.drawImage(
                    master.getControler().ImagemDeDiagrama.get(m.getTipo().name()).getImage(),
                    r.x + 2, r.y + fh / 2, null);
            //# Shape bkp = g2.getClip();
            Rectangle bkp = g.getClipBounds();
            
            //# g2.setClip(r);
            g2.clipRect(r.x, r.y, r.width, r.height);
            String tmp = (m.getMudou()? "*":"") + m.getNomeFormatado();
            int tamtxt = g.getFontMetrics().stringWidth(tmp);
            if (tamtxt > r.width -imgw) {
                tmp = tmp.substring(0, tmp.length() -3) + "...";
                tamtxt = g.getFontMetrics().stringWidth(tmp);
                while (tamtxt  > r.width -imgw) {
                    tmp = tmp.substring(0, tmp.length() -4) + "...";
                    tamtxt = g.getFontMetrics().stringWidth(tmp);
                }
            }
            g2.drawString(tmp, imgw + r.x, r.y + txL);

            g2.setClip(bkp);
            if (esse || (overRNow != null && overRNow.equals(r))) {
                g2.setColor(Color.WHITE);
                regFechar = new Rectangle(r.x + r.width - 18, r.y + fh / 2, 16, 16);
                g2.fillRect(regFechar.x - 2, regFechar.y - 2, 20, 20);
                g2.drawImage(
                        master.getControler().ImagemDeDiagrama.get(Img).getImage(),
                        r.x + r.width - 18, r.y + fh / 2 -1, null);
            }
        }

        //pinta as setas.
        g2.setColor(getBackground());
        Rectangle r1 = areas.get(0);
        g2.fill(r1);
        Rectangle r2 = areas.get(1);
        g2.fill(r2);
        int len = getHeight() / 2;
        int ARR_SIZE = 8;
        int l = 8;
        int ini = 2;

        if (this.overRNow != null && this.overRNow.equals(r1) && (scroll < tabRecuo)) {
            g2.setColor(Color.RED);
        } else {
            if (scroll < tabRecuo) {
                g2.setColor(Color.BLACK);
            } else {
                g2.setColor(Color.LIGHT_GRAY);
            }
        }

        g2.fillPolygon(new int[]{ini, l + ini, l + ini, ini},
                new int[]{len, len - ARR_SIZE, len + ARR_SIZE, len}, 4);

        int tam = (((areas.size() - 2) * (larg + dist)) + (2 * largPonta) - dist) - getWidth();
        if (this.overRNow != null && this.overRNow.equals(r2) && (tam > (scroll * -1))) {
            g2.setColor(Color.RED);
        } else {
            if (tam > (scroll * -1)) {
                g2.setColor(Color.BLACK);
            } else {
                g2.setColor(Color.LIGHT_GRAY);
            }
        }

        g2.fillPolygon(new int[]{getWidth() - ini, getWidth() - ini - l, getWidth() - ini - l, getWidth() - ini},
                new int[]{len, len - ARR_SIZE, len + ARR_SIZE, len}, 4);

//        if (this.overRNow != null) {
//            if (this.overRNow.equals(r1) && this.overRNow.equals(r2)) {
//                g2.drawImage(
//                        master.getControler().ImagemDeDiagrama.get(Img).getImage(),
//                        this.overRNow.x + this.overRNow.width - 18, this.overRNow.y + fh / 2, null);
//            }
//        }
        //pinta as pontas
        //g2.fill(areas.get(0));
        //g2.fill(areas.get(areas.size() -1));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        setOverNow(null);
    }
    /**
     * Mouse over agora.
     */
    private Rectangle overRNow = null;

    public Rectangle getOverNow() {
        return overRNow;
    }

    public void setOverNow(Rectangle overNow) {
        if (this.overRNow == overNow) {
            return;
        }
        if (this.overRNow != null && this.overRNow.equals(overNow)) {
            return;
        }
        //pinto o antigo
        if (this.overRNow != null) {
            //Rectangle r = new Rectangle(this.overRNow);
            //this.overRNow = null; //new Rectangle(0,0,0,0);
            //repaint(r);
            repaint(this.overRNow);
        }
        this.overRNow = overNow;
        //pinto o novo
        if (this.overRNow != null) {
            repaint(this.overRNow);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        Point p = e.getPoint();
        ChecaCursor(p);
    }

    private void ChecaCursor(Point p) {
        if (areas.isEmpty()) {
            return;
        }
        int i = 0;
        for (Rectangle r : areas) {
            if (r.contains(p)) {
                setOverNow(r);
                if (i > 1) {
                    Rectangle r2 = new Rectangle(r.x + r.width - 18, r.y + fh / 2, 16, 16);
                    if (r2.contains(p)) {
                        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    } else {
                        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    }
                }
                return;
            }
            i++;
        }
        setOverNow(null);
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (areas.isEmpty()) {
            return;
        }
        int i = 0;
        for (Rectangle r : areas) {
            if (r.contains(e.getPoint())) {
                Rectangle r2 = new Rectangle(r.x + r.width - 18, r.y + fh / 2, 16, 16);
                ProcessClick(i);
                if (i > 1 && r2.contains(e.getPoint())) {
                    master.FechaDiagrama(i - 2);
                    Construa();
                    ChecaCursor(e.getPoint());
                    repaint();
                }
                break;
            }
            i++;
        }
        e.consume();
    }

    private void ProcessClick(int i) {
        if (i == 0 || i == 1) {
            if (i == 0) {
                if (scroll < tabRecuo) {
                    scroll += mover;
                    repaint();
                }
                return;
            }
            int tam = (((areas.size() - 2) * (larg + dist)) + (2 * largPonta) - dist) - getWidth();
            if (tam > (scroll * -1)) {// && tam > 0  && ((scroll - (tabRecuo) * -1) < tam )) {
                scroll -= mover;
                repaint();
                return;
            }
            return;
        }
        setSelectedIndex(i - 2);
        if (master != null) {
            master.AtiveDiagrama(selectedIndex);
        }
    }

    private void setTableVisible(int idx) {
        //areas já esta calculado.
        if (areas == null || areas.isEmpty() || idx < 0) {
            return;
        }
        if (areas.size() - 2 > idx) {
            Rectangle r = areas.get(idx + 2);
            if ((r.x + r.width) > getWidth() - tabRecuo) {
                int calc = (r.x + r.width) - (getWidth() - tabRecuo);
                //multiplos de "mover"
                int tmp = Math.abs(calc % mover);
                if (tmp > 0) {
                    calc += mover - Math.abs(calc % mover);
                }
                scroll = scroll - (calc);
                repaint();
            } else if (r.x < tabRecuo) {
                int calc = tabRecuo - r.x;
                //multiplos de "mover"
                int tmp = Math.abs(calc % mover);
                if (tmp > 0) {
                    calc += mover - Math.abs(calc % mover);
                }
                scroll += calc;
                repaint();
            }
        }
    }
}
