/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JLabel;

/**
 *
 * @author ccandido
 */
public class BrLogger {

    //private final Editor editor;
    private static JLabel status;

    public static JLabel getStatus() {
        return status;
    }

    public static void setStatus(JLabel status) {
        BrLogger.status = status;
    }

    /**
     * Objeto simples para organizar as exceções.
     */
    public static class Excecao {
        public String Tipo = "";
        public String Valor = "";
        public String Complemento = "";
        public Excecao (String tp, String valor) {
            Tipo = tp;
            Valor = valor;
        }
        
        public Excecao (String tp, String valor, String complemento) {
            this(tp, valor);
            Complemento = complemento;
        }

        @Override
        public String toString() {
            return Tipo + ": " + Valor;
        }
    }
            
    /**
     * Lista de pares de erro e mensagens.
     */
    public final static ArrayList<Excecao> Logs = new ArrayList<>();
    
    public static void Logger(String rpt, String exception) {
        Excecao p = new Excecao(rpt , (exception != null ? " (java: " + exception + ")" : ""));
        Logs.add(p);
        if (status != null) {
            status.setForeground(Color.red);
            String msg = p.Tipo + p.Valor;
            status.setText(msg);
        }
    }

    public static void Logger(String rpt, String complemento, String exception) {
        Excecao p = new Excecao(rpt, (exception != null ? " (java: " + exception + ")" : ""), complemento);
        Logs.add(p);
        if (status != null) {
            status.setForeground(Color.red);
            String msg = p.Tipo + p.Complemento + p.Valor ;
            status.setText(msg);
        }
    }
    
    public static void Clean() {
        Logs.clear();
        getStatus().setText("");
    }
}
