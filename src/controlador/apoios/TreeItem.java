package controlador.apoios;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author ccandido
 */
public class TreeItem extends DefaultMutableTreeNode {

    public TreeItem() {
        super();
    }

    public TreeItem(Object userObject) {
        super(userObject);
    }

    public TreeItem(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    public TreeItem(String caption, int id, String extraInfo) {
        super(caption);
        setId(id);
        setExtraInfo(extraInfo);
        titulo = caption;
    }

    private String texto = "";
    private int id = 0;
    private String extraInfo = "";
    private String titulo = "";

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public final void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
    
    public int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }
    
    
    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

}
