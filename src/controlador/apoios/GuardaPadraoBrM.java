/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.apoios;

import controlador.Diagrama;
import java.io.*;

/**
 *
 * @author ccandido
 */
public class GuardaPadraoBrM implements Serializable {

    public GuardaPadraoBrM(Diagrama dg) {
        super();
        diagrama = toByteArray(Diagrama.SaveToStream(dg));
        Tag = dg.getVersao();
    }

    public GuardaPadraoBrM(byte[] dg) {
        super();
        diagrama = dg;
    }
    
    public String versao = "1.0.0";
    public String versaoDiagrama = null;
    public String data = "13-02-2012";
    public String autor = "Carlos Henrique Cândido";
    private static final long serialVersionUID = 1138331722566089686L;
    
    //Reservado para qualquer propósito.
    public String Tag = "";
    private byte[] diagrama;

    public Diagrama getDiagrama() {
        Diagrama res = null;
        try {
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(diagrama))) {
                res = (Diagrama) in.readObject();
            }
        } catch (ClassNotFoundException | IOException e) {
            util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD_PADRAO", e.getMessage());
            return null;
        }
        return res;
    }

    private byte[] toByteArray(ByteArrayOutputStream diagrama) {
        byte[] b = diagrama.toByteArray();
        return b;
    }
}
