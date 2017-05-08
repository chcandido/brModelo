/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;

/**
 *
 * @author ccandido
 */
public class Arquivo {

    public static final String brM3 = "brM3"; 
    public static final String xml = "xml"; 
    public static final String bmp = "bmp"; 
    public static final String png = "png"; 
    
    public Arquivo() {
        super();
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static boolean IsbrM3(File f) {
        String ext = getExtension(f);
        return ext.toUpperCase().equals(brM3.toUpperCase());
    }

    public static boolean Isxml(File f) {
        String ext = getExtension(f);
        return ext.toUpperCase().equals(xml.toUpperCase());
    }

    public static boolean IsBMP(File f) {
        String ext = getExtension(f);
        return ext.toUpperCase().equals(bmp.toUpperCase());
    }

    public static boolean IsPNG(File f) {
        String ext = getExtension(f);
        return ext.toUpperCase().equals(png.toUpperCase());
    }

}
