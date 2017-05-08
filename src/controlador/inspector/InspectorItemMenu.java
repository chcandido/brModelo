/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

/**
 *
 * @author ccandido
 */
public class InspectorItemMenu extends InspectorItemBase{

    public InspectorItemMenu(Inspector criador) {
        super(criador);
        setOndeEditar(criador.TipoMenu);
        criador.TipoMenu.setEditable(false);
    }
    
    public InspectorItemMenu(){
        super();
    }

    @Override
    public String Traduza(String texto) {
        int idx = Integer.parseInt(texto);
        if ((idx > -1) && (getPropriedade().opcoesMenu != null) && (getPropriedade().opcoesMenu.size() > idx)) {
            return getPropriedade().opcoesMenu.get(idx);
        } else {
            return super.Traduza(texto);
        }
    }
}
