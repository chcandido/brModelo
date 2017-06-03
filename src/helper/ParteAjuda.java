/*
 * Copyright (C) 2017 chcan
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

import controlador.Diagrama;
import controlador.apoios.TreeItem;
import java.util.ArrayList;
import partepronta.GerenciadorSubParte;
import util.ItemIntStringToList;

/**
 *
 * @author chcan
 */
public class ParteAjuda extends GerenciadorSubParte {

    private static final long serialVersionUID = 23051723180000L;

    public ParteAjuda(String texto, byte[] byteImage, String XMLCopiado, String versaoDiagrama, Diagrama.TipoDeDiagrama tipo) {
        super(texto, byteImage, XMLCopiado, versaoDiagrama, tipo);
    }

    public ParteAjuda(int id, String titulo) {
        super();
        ID = id;
        setTitulo(titulo);
    }

    //<editor-fold defaultstate="collapsed" desc="Propriedades">
    private final ArrayList<Integer> links = new ArrayList<>();
    private int ID = 0;
    private ArrayList<ParteAjuda> subs = new ArrayList<>();
    private ParteAjuda superior = null;
    private String html = "";

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    protected AjudaManager master = null;

    public AjudaManager getMaster() {
        return master;
    }

    public ArrayList<ParteAjuda> getSubs() {
        return subs;
    }

    public void setSubs(ArrayList<ParteAjuda> subs) {
        this.subs = subs;
    }

    public ParteAjuda getSuperior() {
        return superior;
    }

    protected void setSuperior(ParteAjuda superior) {
        if (this.superior != superior) {
            this.superior = superior;
            doMuda();
        }
    }

    public void SetSuperior(ParteAjuda superior) {
        if (this.superior != superior) {
            if (isSub(superior)) {
                return;
            }
            if (this.superior != null) {
                this.superior.getSubs().remove(this);
            }
            this.superior = superior;
            if (this.superior != null) {
                this.superior.getSubs().add(this);
            }
            doMuda();
        }
    }

    @Override
    public void setTitulo(String titulo) {
        if (!this.getTitulo().equals(titulo)) {
            super.setTitulo(titulo);
            doMuda();
        }
    }

    public ArrayList<Integer> getLinks() {
        return links;
    }

    public int getID() {
        return ID;
    }

    public void setID(int id) {
        if (this.ID != id) {
            this.ID = id;
            doMuda();
        }
    }
//</editor-fold>

    public int FindMaxID() {
        return FindMaxID(getID());
    }

    private int FindMaxID(int p) {
        for (ParteAjuda ia : getSubs()) {
            p = Math.max(p, ia.FindMaxID());
        }
        return p;
    }

    public ParteAjuda Add(int id, String titulo) {
        ParteAjuda nw = new ParteAjuda(id, titulo);
        nw.setSuperior(this);
        getSubs().add(nw);
        master = getMaster();
        doMuda();
        return nw;
    }

    public void InitParteAjuda(byte[] byteImage, String XMLCopiado, String versaoDiagrama, Diagrama.TipoDeDiagrama tipo) {
        InitGerenciadorSubParte(getTitulo(), byteImage, XMLCopiado, versaoDiagrama, tipo);
        doMuda();
    }

    public ParteAjuda findByID(int id) {
        if (id == getID()) {
            return this;
        }
        for (ParteAjuda ia : getSubs()) {
            ParteAjuda res = ia.findByID(id);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public void PopuleTree(TreeItem tree) {
        final TreeItem sub = new TreeItem(getTitulo(), getID(), "");
        tree.add(sub);
        getSubs().stream().forEach((it) -> {
            it.PopuleTree(sub);
        });
    }

    public void doMuda() {
        if (master != null && master != this) {
            master.doMuda();
        }
    }

    public void ProcurarPorTexto(String tex, ArrayList<ParteAjuda> encontrados) {
        String tmp = tex.toUpperCase();
        if (getTitulo().toUpperCase().contains(tmp)) {
            encontrados.add(this);
        } else if (getHtml().toUpperCase().contains(tmp)) {
            encontrados.add(this);
        } else {
            if (getXMLCopiado().toUpperCase().contains(tmp)) {
                encontrados.add(this);
            }
        }
        getSubs().stream().forEach(s -> s.ProcurarPorTexto(tex, encontrados));
    }

//    public void mapLst(ArrayList<String> lst, String passo) {
//        lst.add(passo + getTitulo());
//        getSubs().stream().forEach((it) -> {
//            it.mapLst(lst, passo.isEmpty() ? "" : passo + "    ");
//        });
//    }
    public void PopuleTopicos(ArrayList<ItemIntStringToList> topicos) {
        topicos.add(new ItemIntStringToList(ID, getTitulo(), this));
        getSubs().stream().forEach((it) -> {
            it.PopuleTopicos(topicos);
        });
    }

    private boolean isSub(ParteAjuda proc) {
        return subs.stream().filter(s -> s == proc || s.isSub(proc)).findAny().isPresent();
    }
}
