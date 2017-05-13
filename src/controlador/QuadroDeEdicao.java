/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @author Rick
 */
public class QuadroDeEdicao extends BaseControlador {

    // <editor-fold defaultstate="collapsed" desc="Campos">
    public final int EditorMaxWidth = 4096;
    public final int EditorMaxHeigth = 4096;

    
    private transient Editor master;
    private int pontoWidth = 6;
    private int pontoHeigth = 6;
    private int editorAtualWidth = EditorMaxWidth;
    private int editorAtualHeigth = EditorMaxHeigth;
    private int editorMargem = 50;
    private Color editorBackColor = Color.WHITE;
    public final String versao = "3.0.0";

    private double zoom = 1.0;

    //private boolean ShowGrid = false;
    //private int GridWidth = 20;
//
//    public int getGridWidth() {
//        return GridWidth;
//    }
//
//    public void setGridWidth(int GridWidth) {
//        if (GridWidth < 0 || GridWidth > 600) {
//            GridWidth = 20;
//        }
//        this.GridWidth = GridWidth;
//    }
//
//    public void setShowGrid(boolean ShowGrid) {
//        //if (this.ShowGrid != ShowGrid) {
//        this.ShowGrid = ShowGrid;
//        //}
//    }
//
//    public boolean isShowGrid() {
//        return ShowGrid;
//    }
    public Editor getEditor() {
        return master;
    }

    /**
     * @return the pontoWidth
     */
    public int getPontoWidth() {
        return pontoWidth;
    }

    /**
     * @param pontoWidth the pontoWidth to set
     */
    public void setPontoWidth(int pontoWidth) {
        this.pontoWidth = pontoWidth;
    }

    /**
     * @return the pontoHeigth
     */
    public int getPontoHeigth() {
        return pontoHeigth;
    }

    /**
     * @param pontoHeigth the pontoHeigth to set
     */
    public void setPontoHeigth(int pontoHeigth) {
        this.pontoHeigth = pontoHeigth;
    }

    /**
     * @return the editorAtualWidth
     */
    public int getEditorAtualWidth() {
        return (int) (editorAtualWidth * getZoom());
    }

    /**
     * @param editorAtualWidth the editorAtualWidth to set
     */
    public void setEditorAtualWidth(int editorAtualWidth) {
        this.editorAtualWidth = editorAtualWidth;
    }

    /**
     * @return the editorAtualHeigth
     */
    public int getEditorAtualHeigth() {
        return (int) (editorAtualHeigth * getZoom());
    }

    /**
     * @param editorAtualHeigth the editorAtualHeigth to set
     */
    public void setEditorAtualHeigth(int editorAtualHeigth) {
        this.editorAtualHeigth = editorAtualHeigth;
    }

    /**
     * @return the editorMargem
     */
    public int getEditorMargem() {
        return editorMargem;
    }

    /**
     * @param editorMargem the editorMargem to set
     */
    public void setEditorMargem(int editorMargem) {
        this.editorMargem = editorMargem;
    }

    /**
     * @return the editorBackColor
     */
    public final Color getEditorBackColor() {
        return editorBackColor;
    }

    /**
     * @param editorBackColor the editorBackColor to set
     */
    public final void setEditorBackColor(Color editorBackColor) {
        this.editorBackColor = editorBackColor;
    }

    private transient boolean mostrarAreaImpressao = false;
    transient int areaImpressaoWidth = 0;
    transient int areaImpressaoHeigth = 0;

    /**
     * Mostrar a área de impressão nos diagramas.
     * @return 
     */
    public boolean isMostrarAreaImpressao() {
        return mostrarAreaImpressao;
    }

    /**
     * Mostrar a área de impressão nos diagramas.
     *
     * @param mostrarAreaImpressao sim|não
     * @param wdt largura da página
     * @param ht altura da página
     */
    public void setMostrarAreaImpressao(boolean mostrarAreaImpressao, int wdt, int ht) {
        //if (this.mostrarAreaImpressao != mostrarAreaImpressao) {
            this.mostrarAreaImpressao = mostrarAreaImpressao;
            this.areaImpressaoWidth = wdt;
            this.areaImpressaoHeigth = ht;
            repaint();
        //}
    }

    // </editor-fold>
    public QuadroDeEdicao(Editor omaster) {
        super();
        master = omaster;
        setDoubleBuffered(true);
        this.setBackground(this.getEditorBackColor());
        setFont(new Font("Arial", Font.BOLD, 12));
    }

    public void setMaster(Editor master) {
        this.master = master;
    }

    public Diagrama getDiagramaAtual() {
        if (master == null) {
            return null;
        }
        return master.diagramaAtual;
    }

    // <editor-fold defaultstate="collapsed" desc="Eventos">
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); //paint background
        RenderingHints renderHints
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        renderHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //renderHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        Graphics2D Canvas = (Graphics2D) g;

        Canvas.addRenderingHints(renderHints);

        Canvas.setStroke(new BasicStroke(
                1f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        Canvas.setPaint(Color.BLACK);
        ProcessPaint(Canvas);
    }

    private void ProcessPaint(Graphics2D Canvas) {
        if (getDiagramaAtual() == null) {
            return;
        }
        getDiagramaAtual().ProcessPaint(Canvas);
    }

//    BufferedImage grade = null;
//    int gradeW = -1;
//    int gradeWidth = -1;
//    int gradeHeigth = -1;
//    /**
//     * Pinta a grade.
//     * @param e
//     */
//    private void PinteGrade(Graphics2D gx) {
//        Editor ma = master;
//        if (ma == null) {
//            return;
//        }
//
//        if (gradeW != ma.getGridWidth() || gradeWidth != getWidth() || gradeHeigth != getHeight()){
//            gradeW = ma.getGridWidth();
//            gradeWidth = getWidth();
//            gradeHeigth = getHeight();
//            grade = null;
//        }
//        
//        if (grade == null) {
//            grade = new BufferedImage(gradeWidth, gradeHeigth,
//                    BufferedImage.BITMASK);
//
//            Graphics2D g = grade.createGraphics();
//
//            g.setStroke(new BasicStroke(
//                    1f,
//                    BasicStroke.CAP_ROUND,
//                    BasicStroke.JOIN_ROUND,
//                    3f,
//                    new float[]{2f, 1f},
//                    0f));
//
//            int w = gradeW;
//            int gW = (gradeWidth / w) + 1;
//            int gH = (gradeHeigth / w) + 1;
//
//            int ww = gradeWidth;
//            int hh = gradeHeigth;
//
//            g.setColor(new Color(231, 231, 231));
//
//            for (int i = 1; i < gW; i++) {
//                g.drawLine(w * i, 0, w * i, hh);
//            }
//
//            for (int i = 1; i < gH; i++) {
//                g.drawLine(0, w * i, ww, w * i);
//            }
//            g.dispose();
//        }
//        gx.drawImage(grade, null, 0, 0);
//            //grade = null;
//    }
    
    @Override
    public void mouseClick(MouseEvent e) {
        if (getDiagramaAtual() == null) {
            return;
        }
        getDiagramaAtual().mouseClick(e);
    }

    @Override
    public void mouseDblClick(MouseEvent e) {
        super.mouseDblClick(e);
        if (getDiagramaAtual() == null) {
            return;
        }
        getDiagramaAtual().mouseDblClick(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (getDiagramaAtual() == null) {
            return;
        }
        getDiagramaAtual().mousePressed(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (getDiagramaAtual() == null) {
            return;
        }
        getDiagramaAtual().mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (getDiagramaAtual() == null) {
            return;
        }
        getDiagramaAtual().mouseExited(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (getDiagramaAtual() == null) {
            return;
        }
        getDiagramaAtual().mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        if (getDiagramaAtual() == null) {
            return;
        }
        getDiagramaAtual().mouseMoved(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (getDiagramaAtual() == null) {
            return;
        }
        getDiagramaAtual().mouseReleased(e);
        super.mouseReleased(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (getDiagramaAtual() == null) {
            return;
        }
        getDiagramaAtual().mouseWheelMoved(e);
        if (!e.isConsumed()) {
            master.ScrollMove(e);
        }
        
        super.mouseWheelMoved(e);
    }
    // </editor-fold>

    @Override
    public void repaint() {
        if (getDiagramaAtual() != null) {
            if (getDiagramaAtual().isCarregando) {
                return;
            }
        }
        super.repaint();
    }

    @Override
    public void repaint(Rectangle r) {
        if (getDiagramaAtual() != null) {
            if (getDiagramaAtual().isCarregando) {
                return;
            }
        }
        super.repaint(r);
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
}
