/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

/**
 *
 * @author ccandido
 */
public class InspectorItemTexto extends InspectorItemBase{

    public InspectorItemTexto(Inspector criador) {
        super(criador);
        setOndeEditar(criador.TipoTexto);
    }
    
    public InspectorItemTexto(){
        super();
    }
}

