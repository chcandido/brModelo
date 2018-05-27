/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import controlador.Configuer;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author ccandido
 */
public class TratadorDeImagens {
    
    public TratadorDeImagens()
    {
        super();
    }

    public static Image makeColorTransparent(Image im, final Color color) {
        //(C)
        //Copiado da internet: 13/02/2011 - http://www.rgagnon.com/javadetails/java-0265.html e http://www.coderanch.com/t/331731/GUI/java/Resize-ImageIcon
        //

        ImageFilter filter = new RGBImageFilter() {
            // the color we are looking for... Alpha bits are set to opaque

            public int markerRGB = color.getRGB() | 0xFF000000;

            @Override
            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
    
    public static Image ReColorBlackImg(ImageIcon im, final Color novaCor) {
        BufferedImage destImage = new BufferedImage(im.getIconWidth(),
                im.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g = destImage.createGraphics();
        g.drawImage(im.getImage(), 0, 0, null);
        g.dispose();        
        for (int i = 0; i < destImage.getHeight(); i++) {
            for (int j = 0; j < destImage.getWidth(); j++) {
                int rgb = destImage.getRGB(j, i);

                if (rgb != 0 && rgb != -1) {//branco ou transparente
                    destImage.setRGB(j, i, novaCor.getRGB());
                }
            }
        }
        return destImage;
    }
    
    public static ImageIcon loadFromResource(String key, boolean reescale) {
        ImageIcon ic;
        try {
            ic = Configuer.getImageIconFromResource(key);
            Image img = TratadorDeImagens.makeColorTransparent(ic.getImage(), Color.WHITE);
            if (reescale) { 
                return new ImageIcon(img.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH));
            }
            return new ImageIcon(img);
        } catch (Exception e) {
            util.BrLogger.Logger("ERROR_LOAD_ICON", e.getMessage());
        }
        return null;
    }
    
    public static BufferedImage fromByteArray(byte[] imagebytes) {
        try {
            if (imagebytes != null && (imagebytes.length > 0)) {
                BufferedImage im = ImageIO.read(new ByteArrayInputStream(imagebytes));
                return im;
            }
        } catch (IOException e) {
            util.BrLogger.Logger("ERROR_IMG_FROMBYTES", e.getMessage());
        }
        return null;
    }

    public static byte[] toByteArray(BufferedImage bufferedImage) {
        if (bufferedImage != null) {
            BufferedImage image = bufferedImage;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", baos);
            } catch (IOException e) {
                util.BrLogger.Logger("ERROR_IMG_TOBYTES", e.getMessage());
            }
            byte[] b = baos.toByteArray();
            return b;
        }
        return new byte[0];
    }
    
    /**
     * Fonte: https://stackoverflow.com/questions/21382966/colorize-a-picture-in-java MODIFICADO
     *
     * @param image
     * @param color
     * @return
     */
    public static BufferedImage dye(ImageIcon image, Color color) {
        int w = image.getIconWidth();
        int h = image.getIconHeight();
        BufferedImage dyed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dyed.createGraphics();
        g.drawImage(image.getImage(), 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return dyed;
    }
}
