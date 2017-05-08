/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

import controlador.Editor;

/**
 *
 * @author ccandido
 */
public class InspectorItemSN extends InspectorItemBase{

    private final String sim;
    private final String nao;
    
    public InspectorItemSN(Inspector criador) {
        super(criador);
        setOndeEditar(criador.TipoSN);
        sim = Editor.fromConfiguracao.getValor("Inspector.obj.true");
        nao = Editor.fromConfiguracao.getValor("Inspector.obj.false");
    }
    
    public InspectorItemSN(){
        super();
        sim = Editor.fromConfiguracao.getValor("Inspector.obj.true");
        nao = Editor.fromConfiguracao.getValor("Inspector.obj.false");
    }

    @Override
    public String Traduza(String texto) {
        return texto.toLowerCase().equals("true")? sim: nao;
    }
}
