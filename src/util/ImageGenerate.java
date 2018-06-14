/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import controlador.Diagrama;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author ccandido
 */
public class ImageGenerate {
    public ImageGenerate()
    {
        super();
    }
    
    public static BufferedImage CriarImagem(int w, int h) {
        //int type = BufferedImage.TYPE_INT_BGR;
        int type = BufferedImage.TYPE_INT_ARGB;
        if (w < 1) w = 1;
        if (h < 1) h = 1;
        BufferedImage image = new BufferedImage(w, h, type);
        return image;
    }
    
    public static BufferedImage[] SplitImagens(BufferedImage img, int resImgW, int resImgH, int pgW, int pgH, int tl_pg) {
        BufferedImage[] res = new BufferedImage[tl_pg];
        
        int posW = 0;
        int posH = 0;
        
        int i = 0;
        for (int l = 0; l < pgH; l++) {
            for (int c = 0; c < pgW; c++) {
                BufferedImage tmp = img.getSubimage(posW, posH, resImgW, resImgH);
                res[i++] = tmp;
                posW += resImgW;
            }
            posW = 0;
            posH += resImgH;
        }
        
        return res;
    }
    
    /**
     * Gera imagem para a conversão do conceitual para lógico (por enquanto só).
     * @param diag
     * @return 
     */
    public static BufferedImage geraImagem(Diagrama diag) {
        Point p = diag.getPontoExtremo();
        p = new Point(Math.max(p.x, 2), Math.max(p.y, 2));
        return geraImagemForPrn(diag, p.x, p.y);
    }

    public static BufferedImage geraImagemForPrn(Diagrama diag, int wdt, int ht) {
        BufferedImage res = CriarImagem(wdt, ht);
        Graphics2D g = (Graphics2D)res.getGraphics();
        g.setPaint(diag.getBackground());
        g.fillRect(0, 0, wdt, ht);
        diag.ExternalPaint(g);
        g.dispose();
        return res;
    }

    public static BufferedImage geraImagemForPrnSelecao(Diagrama diag, int wdt, int ht) {
        
        BufferedImage res = CriarImagem(wdt, ht);
        Graphics2D g = (Graphics2D)res.getGraphics();
        g.setPaint(diag.getBackground());
        g.fillRect(0, 0, wdt, ht);
        diag.ExternalPaintSelecao(g);
        g.dispose();
        
        return res;
    }
//    public static BufferedImage geraImagemForPrn(Diagrama diag, int x, int y, int wdt, int ht) {
//        
//        BufferedImage res = CriarImagem(wdt, ht);
//        Graphics2D g = (Graphics2D)res.getGraphics();
//        g.setPaint(diag.getBackground());
//        g.fillRect(x, y, wdt, ht);
//        diag.ExternalPaint(g);
//        g.dispose();
//        
//        return res;
//    }
}
