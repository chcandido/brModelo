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

import controlador.apoios.TreeItem;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author chcan
 */
public class ItemAjuda implements Serializable {

    private static final long serialVersionUID = 5691662203711762594L;
    //private int imageMaxID = -1;
    private int ID = -1;
    private String titulo = "";
    private String texto = "";
    private String textoPuro = "";
    private Object tag = null;
    private ArrayList<ItemAjuda> childs = new ArrayList<>();
    private ItemAjuda pai = null;

    public ItemAjuda getPai() {
        return pai;
    }

    public void setPai(ItemAjuda pai) {
        this.pai = pai;
    }
    
    /**
     * @return the imageMaxID
     */
    public String getTextoPuro() {
        return textoPuro;
    }

    public void setTextoPuro(String textoPuro) {
        this.textoPuro = textoPuro;
    }
//
//    public int getImageMaxID() {
//        return imageMaxID;
//    }
//
//    /**
//     * @param imageMaxID the imageMaxID to set
//     */
//    public void setImageMaxID(int imageMaxID) {
//        this.imageMaxID = imageMaxID;
//    }

    /**
     * @return the ID
     */
    public int getID() {
        return ID;
    }

    /**
     * @param ID the ID to set
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the texto
     */
    public String getTexto() {
        return texto;
    }

    /**
     * @param texto the texto to set
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * @return the tag
     */
    public Object getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(Object tag) {
        this.tag = tag;
    }

    /**
     * @return the childs
     */
    public ArrayList<ItemAjuda> getChilds() {
        return childs;
    }

    /**
     * @param childs the childs to set
     */
    public void setChilds(ArrayList<ItemAjuda> childs) {
        this.childs = childs;
    }

    //------------
    
    public ItemAjuda(int id, String titulo, String texto) {
        ID = id;
        this.titulo = titulo;
        this.texto = texto;
    }

    public ItemAjuda(int id) {
        ID = id;
    }
    
    public ItemAjuda Add(int id, String titulo, String texto) {
        ItemAjuda nw = new ItemAjuda(id, titulo, texto);
        nw.setPai(this);
        getChilds().add(nw);
        return nw;
    }
    
    //------------
    
    public int FindMaxID() {
        return FindMaxID(getID());
    }

    private int FindMaxID(int p) {
        for (ItemAjuda ia: getChilds()) {
            p = Math.max(p, ia.FindMaxID());
        }
        return p;
    }
    
    //-----------
    
    public ItemAjuda findByID(int id) {
        if (id == getID()) return this;
        for (ItemAjuda ia: getChilds()) {
            ItemAjuda res = ia.findByID(id);
            if (res != null) return res;
        }
        return null;
    }

    public void PopuleTree(TreeItem tree) {
        final TreeItem sub = new TreeItem(getTitulo(), getID(), "");
        tree.add(sub);
        getChilds().stream().forEach((it) -> {
            it.PopuleTree(sub);
        });
    }
}
