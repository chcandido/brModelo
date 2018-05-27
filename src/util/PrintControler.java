package util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.print.*;
import java.util.Locale;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.PageRanges;

/**
 * Copiado da ineternet
 */
public class PrintControler implements Printable {

    public PrintControler() {
        super();
        this.printJob = PrinterJob.getPrinterJob();
        page = printJob.defaultPage();
        page.setOrientation(PageFormat.PORTRAIT);
    }

    //private Component componentToBePrinted;
    private final PrinterJob printJob;

    private PageFormat page;

    int idx = 0;

    public void print() {
        getPrintJob().setPrintable(this, getPage());

        if (Atributos.isEmpty()) {
            Atributos.add(jbn);
        }
        Atributos.remove(pgr);
        pgr = new PageRanges(page_range.x, page_range.y);
        Atributos.add(pgr);

        if (getPrintJob().printDialog(Atributos)) {
            try {
                getPrintJob().print(Atributos);
            } catch (PrinterException pe) {
                util.BrLogger.Logger("ERROR_PRINTING", pe.getMessage());
            }
        }
//        PrintRequestAttributeSet attr_set
//                = new HashPrintRequestAttributeSet();
//        if (page_range.x == 0) {
//            res = getPrintJob().printDialog();
//        } else {
//            PrintRequestAttributeSet attr_set2
//                    = new HashPrintRequestAttributeSet();
//            attr_set2.add(new PageRanges(page_range.x, page_range.y));
//            res = getPrintJob().printDialog(attr_set2);
//        }
//        if (res) {
//            try {
//                getPrintJob().print(attr_set);
//            } catch (PrinterException pe) {
//                System.out.println("Error printing: " + pe);
//            }
//            //page = getPrintJob().getPageFormat(null);
//        }
    }//method()  

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if (pageIndex < imgs.length) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            RenderingHints renderHints
                    = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
            renderHints.put(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            g2d.addRenderingHints(renderHints);
            int w = getPageWidth();
            int h = getPageHeigth();
            g2d.drawImage(imgs[pageIndex], 0, 0, w, h, null);

            return (PAGE_EXISTS);
        } else {
            return (NO_SUCH_PAGE);
        }
    }//method()  

    public final void setJobName(String jobName) {
        getPrintJob().setJobName(jobName);
    }//method()  

    public void pageSetup() {
        page = getPrintJob().pageDialog(getPage());
    }

    private Point page_range = new Point(0, 0);
    private PrintRequestAttributeSet Atributos = new HashPrintRequestAttributeSet();
    private PageRanges pgr = null;
    private JobName jbn = new JobName("brModelo", Locale.getDefault());

    public void printSetup() {
        if (Atributos.isEmpty()) {
            Atributos.add(jbn);
        }
        Atributos.remove(pgr);
        pgr = new PageRanges(page_range.x, page_range.y);
        Atributos.add(pgr);

        if (getPrintJob().printDialog(Atributos)) {
            page = getPrintJob().getPageFormat(Atributos);
        }

//        if (page_range.x == 0) {
//            Atributos.add(new PageRanges(page_range.x, page_range.y));
//            if (getPrintJob().printDialog()) {
//                
//                page = getPrintJob().getPageFormat(null);
//            }
//        } else {
//            PrintRequestAttributeSet attr_set
//                    = new HashPrintRequestAttributeSet();
//            attr_set.add(new PageRanges(page_range.x, page_range.y));
//            
//            attr_set = getPrintJob(). getPrintService().getAttributes().;
//            
//            if (getPrintJob().printDialog(attr_set)) {
//                page = getPrintJob().getPageFormat(attr_set);
//            }
//        }
    }

    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }//method()  

    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }//method()  

    public PrinterJob getPrintJob() {
        return printJob;
    }

    public PageFormat getPage() {
        return page;
    }

    public int getPageWidth() {
        return (int) getPage().getImageableWidth();
    }

    public int getPageHeigth() {
        return (int) getPage().getImageableHeight();
    }

    public boolean isLandscape() {
        return (getPage().getOrientation() == PageFormat.LANDSCAPE);
    }

    public int getRealFolhaWidth() {
        return (int) getPage().getWidth();
    }

    public int getRealFolhaHeigth() {
        return (int) getPage().getHeight();
    }

    BufferedImage[] imgs = null;

    public void setPaginas(BufferedImage[] imgs) {
        this.imgs = imgs;
        if (imgs == null) {
            page_range = new Point(0, 0);
            return;
        }
        page_range = new Point(1, imgs.length);
    }
}//class  

