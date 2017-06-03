/*
 * Copyright (C) 2015 SAA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package partepronta;

import controlador.Diagrama;
import desenho.FormaElementar;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author SAA
 */
public class GerenciadorPartes implements Serializable{

    private static final long serialVersionUID = 5549339395194123920L;

    private final ArrayList<GerenciadorSubParte> lista = new ArrayList<>();

    public ArrayList<GerenciadorSubParte> getLista() {
        return lista;
    }

    public void Add(GerenciadorSubParte parte) {
        lista.add(parte);
        setMudou(true);
    }

    public GerenciadorSubParte Add(String texto, Diagrama diag) {
        byte[] byteImage = ImagemDaSelecao(diag);
        String XMLCopiado = Diagrama.SaveToXml(diag, true);
        String versaoDiagrama = diag.getVersao();
        Diagrama.TipoDeDiagrama tipo = diag.getTipo();
        
        GerenciadorSubParte parte = new GerenciadorSubParte(texto, byteImage, XMLCopiado, versaoDiagrama, tipo);
        
        Add(parte);
        return parte;
    }

    public byte[] ImagemDaSelecao(Diagrama diagramaAtual) {
        final int borda = 2;
        Point p2 = diagramaAtual.getPontoExtremoSelecionado();
        int minX = p2.x;
        int minY = p2.y;

        for (int i = diagramaAtual.getItensSelecionados().size() - 1; i > -1; i--) {
            FormaElementar el = diagramaAtual.getItensSelecionados().get(i);
            minX = Math.min(minX, el.getLeft());
            minY = Math.min(minY, el.getTop());
        }

        minX = Math.max(minX - borda, 0);
        minY = Math.max(minY - borda, 0);

        BufferedImage cp_img = util.ImageGenerate.geraImagemForPrnSelecao(diagramaAtual, p2.x + borda, p2.y + borda);
        return util.TratadorDeImagens.toByteArray(cp_img.getSubimage(minX, minY, p2.x - minX, p2.y - minY));
    }

    public void Remova(GerenciadorSubParte sp) {
        lista.remove(sp);
        setMudou(true);
    }
   
    
    //<editor-fold defaultstate="collapsed" desc="Salvar">
    private boolean mudou = false;
    
    public boolean isMudou() {
        return mudou;
    }
    
    public void setMudou(boolean mudou) {
        this.mudou = mudou;
    }
    
    public static GerenciadorPartes LoadData(String arq) {
        GerenciadorPartes res;
        try {
            FileInputStream fi = new FileInputStream(arq);
            try (ObjectInput in = new ObjectInputStream(fi)) {
                res = (GerenciadorPartes) in.readObject();
                in.close();
            }
            res.setMudou(false);
            return res;
        } catch (NullPointerException | IOException | ClassNotFoundException iOException) {
            util.BrLogger.Logger("ERROR_TEMPLATE_LOAD", iOException.getMessage());
            return null;
        }
    }
    
    public static GerenciadorPartes LoadDataTemplate() {
        String tmp = System.getProperty("user.dir") + File.separator + "Template.brMt";
        GerenciadorPartes gp = LoadData(tmp);
        if (gp == null) {
            gp = new GerenciadorPartes();
            GerenciadorPartes.SaveDataTemplate(gp);
        }
        return gp;
    }
    
    public static boolean SaveData(GerenciadorPartes obj, String fileName) {
        try {
            FileOutputStream fo = new FileOutputStream(fileName);
            try (ObjectOutput out = new ObjectOutputStream(fo)) {
                out.writeObject(obj);
            }
            obj.setMudou(false);
            return true;
        } catch (IOException iOException) {
            util.BrLogger.Logger("ERROR_TEMPLATE_SAVELOAD", iOException.getMessage());
            return false;
        }
    }
    
    public static boolean SaveDataTemplate(GerenciadorPartes obj) {
        String tmp = System.getProperty("user.dir") + File.separator + "Template.brMt";
        return SaveData(obj, tmp);
    }
    //</editor-fold>

    public void Edit(GerenciadorSubParte Parte, String txt) {
        Parte.setTitulo(txt);
        setMudou(true);
    }
}
