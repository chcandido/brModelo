/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 *
 * @author ccandido
 */
public class ImpressorPreview extends BaseControlador {

    private final Impressor prn;

    int h = 0;
    int w = 0;
    int t = 0;
    int l = 0;

    private int pgatual = 0;
    private int tlpagina = 0;

    public int Largura;
    public int Altura;

    public ImpressorPreview(Impressor prn) {
        super();
        setDoubleBuffered(true);
        this.prn = prn;
        w = prn.Impressora.getPageWidth();
        h = prn.Impressora.getPageHeigth();

        l = (int) prn.Impressora.getPage().getPaper().getImageableX();
        t = (int) prn.Impressora.getPage().getPaper().getImageableY();

        tlpagina = prn.getQtdPagina();
        Largura = prn.Impressora.getRealFolhaWidth();
        Altura = prn.Impressora.getRealFolhaHeigth();
        //setPreferredSize(getSize());
        setBackground(Color.WHITE);
    }

    public int getPgatual() {
        return pgatual;
    }

    public void setPgatual(int pgatual) {
        this.pgatual = pgatual;
        if (pgatual > tlpagina || pgatual < 0) {
            pgatual = 0;
        }
    }

    public int getTlpagina() {
        return tlpagina;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); //paint background
        RenderingHints renderHints
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        Graphics2D Canvas = (Graphics2D) g;
        Canvas.addRenderingHints(renderHints);

//        Canvas.setPaint(Color.BLACK);
//        Canvas.draw3DRect(0, 0, getWidth() - 4, getHeight() - 4, true);
        Canvas.setPaint(Color.BLACK);
        Stroke stroke = new BasicStroke(1.f,
                BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
        Canvas.setStroke(stroke);

        Canvas.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        Canvas.setPaint(Color.GRAY);
        Canvas.drawRect(0, 0, getWidth() - 2, getHeight() - 2);

        if (pgatual == 0) {
            return;
        }
        float[] dash4 = {2f, 2f, 2f};

        BasicStroke bs4 = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash4,
                2f);
        Canvas.setStroke(bs4);

        Canvas.drawLine(l - 1, 1, l - 1, getHeight() - 1);
        Canvas.drawLine(l + w + 1, 1, l + w + 1, getHeight() - 1);
        Canvas.drawLine(1, t - 1, getWidth() - 1, t - 1);
        Canvas.drawLine(1, t + h + 1, getWidth() - 1, t + h + 1);

        Canvas.setStroke(new BasicStroke(
                1f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        Canvas.setPaint(Color.BLACK);
        DrawPagina(Canvas);
    }

    private void DrawPagina(Graphics2D Canvas) {
        if (pgatual > 0) {
            Canvas.drawImage(prn.imgs[pgatual - 1], l, t, w, h, null); //melhor
            //Canvas.drawImage(prn.imgs[pgatual - 1], null, l, t);
            //Canvas.drawImage(prn.imgs[pgatual -1], l, t, null);
        }
    }
    
    public void PrintPg() {
        int pg = pgatual;
        prn.iniceImpressaoNoPreview(pg);
        prn.Impressora.print();
        prn.finalizeImpressaoNoPreview();
    }

    /**
     * A impressora já deve estar iniciada - já está no preview.
     */
    public void Print() {
        prn.Impressora.print();
    }

}
