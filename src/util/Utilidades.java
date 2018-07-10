/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import controlador.Editor;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author ccandido
 */
public class Utilidades {

    
    public static String ColorToString(Color c) {
        String res = String.valueOf(c.getRed());
        res += "," + String.valueOf(c.getGreen());
        res += "," + String.valueOf(c.getBlue());
        res += "," + String.valueOf(c.getAlpha());
        return res;
    }

    public static Color StringToColor(String str) {
        String[] res = str.split(",");
        int r = Integer.parseInt(res[0]);
        int g = Integer.parseInt(res[1]);
        int b = Integer.parseInt(res[2]);
        int a = Integer.parseInt(res[3]);
        return new Color(r, g, b, a);
    }
    
    /**
     * Verifica se uma string é um int, se não: retorna int.
     * @param res Valor a ser testado.
     * @param retornoEmCasoDeErro retorno em caso de erro.
     * @return O mesmo valor de res se res for um int válido.
     */
    public static String TryIntStr(String res, String retornoEmCasoDeErro) {
        int r;
        try {
            r = Integer.valueOf(res);
        } catch (NumberFormatException e){
            return retornoEmCasoDeErro;
        }
        return res;
    }

    public static int TryIntStr(String res, int retornoEmCasoDeErro) {
        int r;
        try {
            r = Integer.valueOf(res);
        } catch (NumberFormatException e){
            return retornoEmCasoDeErro;
        }
        return r;
    }

    public static Color CorInversa(Color cor) {
        int A = cor.getAlpha();
        int R = 255 - cor.getRed();
        int G = 255 - cor.getGreen();
        int B = 255 - cor.getBlue();
        return new Color(R, G, B, A);
    }
    
    public static String decodeFontStyle(int style) {
        switch(style) {
            case Font.BOLD:
                return Editor.fromConfiguracao.getValor("Inspector.obj.font.bold");
            case Font.PLAIN:
                return Editor.fromConfiguracao.getValor("Inspector.obj.font.plain");
            case Font.ITALIC:
                return Editor.fromConfiguracao.getValor("Inspector.obj.font.italic");
            case Font.BOLD | Font.ITALIC:
                return Editor.fromConfiguracao.getValor("Inspector.obj.font.bolditalic");
        }
        return "";
    }

    public static String ArrayToStr(String[] arr) {
        String res = "";
        for (String a: arr) {
            if (res.isEmpty()) {
                res = a;
            } else {
                res += "," + a;
            }
        }
        return res;
    }

    public static boolean IsUpper(String texto) {
        return texto.toUpperCase().equals(texto);
    }

    public Utilidades() {
        super();
    }
    
    public static String Hexadecimal(byte[] arr) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            int parteAlta = ((arr[i] >> 4) & 0xf) << 4;
            int parteBaixa = arr[i] & 0xf;
            if (parteAlta == 0) {
                s.append('0');
            }
            s.append(Integer.toHexString(parteAlta | parteBaixa));
        }
        return s.toString();
    }

    public static byte[] HexadecimalToByteArr(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    
    /**
     * Aumenta ou reduz o tamanho de um retangulo. <br/>
     * Conhecendo o retangulo e sabendo que ele não possui valores irreais torna-se mais eficiente que a Rectange.grow().
     * @param rect      Retângulo
     * @param x         ampliação de x
     * @param y         ampliação de y
     * @param desconto  correção (opcional)
     * @return          novo retângulo
     */
    public static Rectangle Grow(Rectangle rect, int x, int y, int desconto) {
        return new Rectangle(rect.x - x, rect.y - y, rect.width + 2 * x - desconto, rect.height + 2 * y - desconto);
    }
    
    public static String[] getFontsList() {
        GraphicsEnvironment gEnv = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        String envfonts[] = gEnv.getAvailableFontFamilyNames();
        return envfonts;
    }
    
    public static final double distance(Point p, Point q) {
        double dx = p.x - q.x;         //horizontal difference
        double dy = p.y - q.y;         //vertical difference
        double dist = Math.sqrt(dx * dx + dy * dy); //distance using Pythagoras theorem
        return dist;
    }

    /**
     *
     * @param msg: Editor.fromConfiguracao.getValor("Controler.interface.mensagem.?)
     * @param param: str, str, str
     * @return : formatada.
     */
    public static String EncapsuleMsg(String msg, Object [] param) {
        return String.format(Editor.fromConfiguracao.getValor("Controler.interface.mensagem." + msg), param);
    }

    /**
     *
     * @param msg: Editor.fromConfiguracao.getValor("Controler.interface.mensagem.?)
     * @param param: str
     * @return : formatada.
     */
    public static String EncapsuleMsg(String msg, String param) {
        return EncapsuleMsg(msg, new Object[] {param});
    }
    
    /**
     *
     * @param msg: Editor.fromConfiguracao.getValor("Controler.interface.mensagem.?)
     * @param param1: str
     * @param param2: str
     * @return : formatada.
     */
    public static String EncapsuleMsg(String msg, String param1, String param2) {
        return EncapsuleMsg(msg, new Object[] {param1, param2});
    }

//    public static int[] Diminua(int[] base) {
//        int[] res = new int[base.length];
//        for (int i = 0; i < base.length; i++) {
//            res[i] = base[i]-1;
//        }
//        return res;
//    }
//    
//    public static int[] Aumente(int[] base) {
//        int[] res = new int[base.length];
//        for (int i = 0; i < base.length; i++) {
//            res[i] = base[i]+1;
//        }
//        return res;
//    }
    
    public static String textoParaCampo(String original) {
        return original.replaceAll("[ãâàáä]", "a")
                .replaceAll("[êèéë]", "e")
                .replaceAll("[îìíï]", "i")
                .replaceAll("[õôòóö]", "o")
                .replaceAll("[ûúùü]", "u")
                .replaceAll("[ÃÂÀÁÄ]", "A")
                .replaceAll("[ÊÈÉË]", "E")
                .replaceAll("[ÎÌÍÏ]", "I")
                .replaceAll("[ÕÔÒÓÖ]", "O")
                .replaceAll("[ÛÙÚÜ]", "U")
                .replace('ç', 'c')
                .replace('Ç', 'C')
                .replace('ñ', 'n')
                .replace('Ñ', 'N')
                .replaceAll("!", "")
                .replaceAll("[\\[\\´\\`\\?!\\@\\#\\$\\%\\¨\\*]", "_")
                .replaceAll("[\\(\\)\\=\\{\\}\\~\\^\\]]", "_")
                .replaceAll("[\\.\\;\\-\\+\\'\\ª\\º\\:\\;\\/]", "_")
                .replaceAll("\\s+","_");
                
    }
}
