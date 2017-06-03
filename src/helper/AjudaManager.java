/*
 * Copyright (C) 2016 chcan
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
package helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 *
 * @author chcan
 */
public class AjudaManager extends ParteAjuda {

    private static final long serialVersionUID = -3897772703895330700L;

    public AjudaManager(int id, String titulo) {
        super(id, titulo);
        master = this;
    }

    //<editor-fold defaultstate="collapsed" desc="Abrir e Salvar">
    public static AjudaManager LoadDataHelp() {
        String tmp = System.getProperty("user.dir") + File.separator + "Ajuda.brMh";
        return LoadData(tmp);
    }

    public static AjudaManager LoadData(String arq) {
        AjudaManager res;
        try {
            FileInputStream fi = new FileInputStream(arq);
            try (ObjectInput in = new ObjectInputStream(fi)) {
                res = (AjudaManager) in.readObject();
                in.close();
            }
            return res;
        } catch (NullPointerException | IOException | ClassNotFoundException iOException) {
            util.BrLogger.Logger("ERROR_HELP_LOAD", iOException.getMessage());
            return null;
        }
    }

    public static boolean SaveData(AjudaManager obj, String fileName) {
        try {
            FileOutputStream fo = new FileOutputStream(fileName);
            try (ObjectOutput out = new ObjectOutputStream(fo)) {
                out.writeObject(obj);
            }
            return true;
        } catch (IOException iOException) {
            util.BrLogger.Logger("ERROR_HELP_SAVE", iOException.getMessage());
            return false;
        }
    }

    public static boolean SaveDataHelp(AjudaManager obj) {
        String tmp = System.getProperty("user.dir") + File.separator + "Ajuda.brMh";
        obj.setMudou(false);
        if (!SaveData(obj, tmp)) {
            obj.setMudou(true);
            return false;
        }
        return true;
    }
    //</editor-fold>

    private boolean mudou = false;

    public boolean isMudou() {
        return mudou;
    }

    public void setMudou(boolean mudou) {
        this.mudou = mudou;
    }
    
    @Override
    public void doMuda() {
        setMudou(true);
    }
}
