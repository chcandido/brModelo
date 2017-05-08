/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import util.PrintControler;

/**
 *
 * @author ccandido
 */
public class Impressor extends BaseControlador {

    public Impressor() {
        super();
        setSize(new Dimension(512, 512));
        setPreferredSize(getSize());
        setBackground(Color.WHITE);
        setLinhas(3);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); //paint background

        Graphics2D Canvas = (Graphics2D) g;

        Canvas.setPaint(Color.BLACK);
        Canvas.draw3DRect(0, 0, getWidth() - 1, getHeight() - 1, true);

        if (getDiagrama() == null) {
            return;
        }

        int w = LarguraPagina;
        int h = AlturaPagina;

        if (!proporcionalLinha) {
            int nh = AlturaPagina;
            PinteNoArea(Canvas);

            for (int i = w; i < getWidth() + getColunas(); i += w) {
                Canvas.drawLine(i, 1, i, getHeight());
            }
            if (!isNaoConsiderarLinhasColunas()) {
                Canvas.setStroke(new BasicStroke(
                        1f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND,
                        3f,
                        new float[]{2f, 1f},
                        0f));
            }
            for (int i = nh; i < getHeight() + getLinhas(); i += nh) {
                Canvas.drawLine(1, i, getWidth(), i);
            }
        } else {
            int nw = LarguraPagina;
            PinteNoArea(Canvas);

            for (int i = h; i < getHeight() + getLinhas(); i += h) {
                Canvas.drawLine(1, i, getWidth(), i);
            }
            if (!isNaoConsiderarLinhasColunas()) {
                Canvas.setStroke(new BasicStroke(
                        1f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND,
                        3f,
                        new float[]{2f, 1f},
                        0f));
            }
            for (int i = nw; i < getWidth() + getColunas(); i += nw) {
                Canvas.drawLine(i, 1, i, getHeight());
            }
        }
        double z = (double) getWidth() / getDiagrama().getWidth();
        Canvas.scale(z, z);
        getDiagrama().ExternalPaint(g);
    }

    private final int LINHAS = 3;
    private int Linhas = LINHAS;
    private int Colunas = 3;

    /**
     * @return the Linhas
     */
    public int getLinhas() {
        return Linhas;
    }

    private boolean proporcionalLinha = true;

    public boolean isLandscape() {
        return (Impressora.getPage().getOrientation() == PageFormat.LANDSCAPE);
    }

    /**
     * @param Linhas the Linhas to set
     */
    public final void setLinhas(int Linhas) {
        this.Linhas = Linhas;
        proporcionalLinha = true;

        int p_larg = Impressora.getPageWidth();
        int p_alt = Impressora.getPageHeigth();

        int h = getHeight() / Linhas;
        int nw = h * p_larg / p_alt;
        int nc = getWidth() / nw + (getWidth() % nw > 1 ? 1 : 0);
        Colunas = nc;
        CalculePagina();
        repaint();
    }

    /**
     * @return the Colunas
     */
    public int getColunas() {
        return Colunas;
    }

    /**
     * @param Colunas the Colunas to set
     */
    public void setColunas(int Colunas) {
        this.Colunas = Colunas;
        proporcionalLinha = false;

        int p_larg = Impressora.getPageWidth();
        int p_alt = Impressora.getPageHeigth();

        int w = getWidth() / Colunas;
        int nh = w * p_alt / p_larg;
        int nl = getHeight() / nh + (getHeight() % nh > 1 ? 1 : 0);
        Linhas = nl;
        CalculePagina();
        repaint();
    }

    private Diagrama diagrama = null;
    public final util.PrintControler Impressora = new PrintControler();

    public Diagrama getDiagrama() {
        return diagrama;
    }

    public void setDiagrama(Diagrama diagrama) {
        this.diagrama = diagrama;
        CalculePagina();
        repaint();
    }

    public util.PrintControler getImpressora() {
        return Impressora;
    }

//    public void setImpressora(util.PrintControler impressora) {
//        this.Impressora = impressora;
//    }
    /**
     * Pinta a área que não será impressa
     */
    private void PinteNoArea(Graphics2D Canvas) {
        if (getDiagrama() != null) {
            Paint bkp = Canvas.getPaint();
            Canvas.setColor(new Color(241, 241, 241));

            int x = PaginasW * LarguraPagina;
            Canvas.fillRect(x + 2, 2, getWidth() - (x + 4), getHeight() - 4);

            int y = PaginasH * AlturaPagina;
            Canvas.fillRect(2, y + 2, getWidth() - 4, getHeight() - (y + 4));
            Canvas.setPaint(bkp);
        }
    }

    private boolean naoConsiderarLinhasColunas = false;

    public void CalculePagina() {
        int p_larg = Impressora.getPageWidth();
        int p_alt = Impressora.getPageHeigth();

        if (isNaoConsiderarLinhasColunas() && getDiagrama() != null) {
            double proporcao = (double) getWidth() / getDiagrama().getWidth();
            p_larg = (int) (p_larg * proporcao);
            p_alt = (int) (p_alt * proporcao);
            SubCalculePagina(p_larg, p_alt);

            Linhas = (int) Math.ceil((double)getWidth() / p_larg);
            Colunas = (int) Math.ceil((double)getHeight() / p_alt);
        } else {
            int w = getWidth() / getColunas();
            int h = getHeight() / getLinhas();
            if (!proporcionalLinha) {
                int nh = w * p_alt / p_larg;
                SubCalculePagina(w, nh);
            } else {
                int nw = h * p_larg / p_alt;
                SubCalculePagina(nw, h);
            }
        }
    }

    private void SubCalculePagina(int wdt, int ht) {
        if (getDiagrama() != null) {
            double z = (double) getWidth() / getDiagrama().getWidth();
            Point mx = getDiagrama().getPontoExtremo();
            Point r = new Point((int) (mx.x * z), (int) (mx.y * z));

            LarguraPagina = wdt;
            AlturaPagina = ht;
            PaginasW = r.x / wdt + (r.x % wdt > 0 ? 1 : 0);//não quero usar ceil!
            PaginasH = r.y / ht + (r.y % ht > 0 ? 1 : 0);
        }
    }

    private int LarguraPagina = 0;
    private int AlturaPagina = 0;
    private int PaginasW = 0;
    private int PaginasH = 0;

    public int getQtdPagina() {
        return PaginasH * PaginasW;
    }

    BufferedImage[] imgs = null;

    public boolean iniceProcessoImpressao() {
        double proporcao = ((double) getDiagrama().getWidth()) / getWidth();
        int w = (int) (LarguraPagina * proporcao);
        int h = (int) (AlturaPagina * proporcao);

        BufferedImage img = util.ImageGenerate.geraImagemForPrn(getDiagrama(),
                w * PaginasW, h * PaginasH);

        imgs = util.ImageGenerate.SplitImagens(img, w, h, PaginasW, PaginasH, getQtdPagina());
        return true;
    }

    public Point getAreaImpressao() {
        double proporcao = ((double) getDiagrama().getWidth()) / getWidth();
        int w = (int) (LarguraPagina * proporcao);
        int h = (int) (AlturaPagina * proporcao);
        return new Point(w, h);
    }

    /**
     * Gera as imagens que serão impressas.
     */
    public void iniceImpressao() {
        iniceProcessoImpressao();
        Impressora.setPaginas(imgs);
    }

    /**
     * Apos iniceImpressao(), é usada para selecionar uma página.
     *
     * @param unica: página a ser impressa.
     */
    public void iniceImpressaoNoPreview(int unica) {
        if (imgs == null) {
            return;
        }
        BufferedImage[] im = new BufferedImage[]{imgs[unica - 1]};
        Impressora.setPaginas(im);
    }

    /**
     * Retaura as imagens para impressão.
     */
    public void finalizeImpressaoNoPreview() {
        Impressora.setPaginas(imgs);
    }

    /**
     * Finaliza as imagens para impressao.
     */
    public void finalizeImpressao() {
        imgs = null;
        Impressora.setPaginas(imgs);
    }

    public boolean isNaoConsiderarLinhasColunas() {
        return naoConsiderarLinhasColunas;
    }

    public void setNaoConsiderarLinhasColunas(boolean naoConsiderarLinhasColunas) {
        this.naoConsiderarLinhasColunas = naoConsiderarLinhasColunas;
        if (!naoConsiderarLinhasColunas) {
            setLinhas(LINHAS);
        } else {
            CalculePagina();
        }
        repaint();
    }
}
