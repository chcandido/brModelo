/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

import controlador.Editor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ccandido
 */
public class InspectorProperty {

    public InspectorProperty() {
        super();
    }

    public InspectorProperty(String partCaption) {
        super();
        if (!"".equals(partCaption)) {
            caption = CaptionFromConfig(partCaption);
        }
        configuracaoStr = partCaption;
        GeraTextoHelper(FullDica(partCaption));
    }

    public final String CaptionFromConfig(String newParCaption) {
        return Editor.fromConfiguracao.getValor(InspectorProperty.FullCaption(newParCaption));
    }

    public void ReSetCaptionFromConfig(String partCaption) {
        caption = CaptionFromConfig(partCaption);
        GeraTextoHelper(FullDica(partCaption));
    }

    public static String FullCaption(String parCaption) {
        return "Inspector.obj." + parCaption;
    }

    public static String FullDica(String parCaption) {
        return "Inspector.dica." + parCaption;
    }

    private void GeraTextoHelper(String fullDica) {
        dica = Editor.fromConfiguracao.getValor(fullDica);
        if (dica.equals(fullDica)) dica = "";
    }

    public enum TipoDeProperty {
        tpNothing,
        tpSeparador,
        tpApenasLeituraTexto,
        tpApenasLeituraCor,
        tpTextoNormal,
        tpTextoLongo,
        tpNumero,
        tpBooleano,
        tpMenu,
        tpCor, tpSelecObject,
        tpCommand}
    
    public String configuracaoStr = "";
    public String opcional = "";
    public String property = "";
    public String caption = "";
    public String dica = "";
    public String valor_string = "";
    public TipoDeProperty tipo = TipoDeProperty.tpNothing;
    public InspectorPprtAgrupador agrupada = null;

    /**
     * Valor arbitrátio usado para diferenciar uma proporty de outra em uma lista para edição (use o método inteligente setTag())
     */
    public int Tag = 0;
    
    /**
     * Independentemente da organização de uma propriedade no Inspector, caso forceDisable seja true, ele ficará desabilitado <br/>
     * É especialmente útil quanto a condição principal leva a propriedade a figurar como desabilitada, porém, é sabido que deve estar habilitado.
     * Ainda não 100% testado.
     */
    private boolean forceDisable = false;

    /**
     * Independentemente da organização de uma propriedade no Inspector, caso forceEnable seja true, ele ficará habilitado <br/>
     * É especialmente útil quanto a condição principal leva a propriedade a figurar como habilitada, porém, é sabido que deve estar desabilitado.
     * Ainda não 100% testado.
     */
    private boolean forceEnable = false;

    /**
     * Independentemente da organização de uma propriedade no Inspector, caso forceDisable seja true, ele ficará desabilitado <br/>
     * É especialmente útil quanto a condição principal leva a propriedade a figurar como desabilitada, porém, é sabido que deve estar habilitado.
     * Ainda não 100% testado.
     */
    public boolean isForceDisable() {
        return forceDisable;
    }

    /**
     * Independentemente da organização de uma propriedade no Inspector, caso forceDisable seja true, ele ficará desabilitado <br/>
     * É especialmente útil quanto a condição principal leva a propriedade a figurar como desabilitada, porém, é sabido que deve estar habilitado.
     * Ainda não 100% testado.
     */
    public void setForceDisable(boolean forceDisable) {
        this.forceDisable = forceDisable;
        if (this.forceDisable) this.forceEnable = false;
    }

    /**
     * Independentemente da organização de uma propriedade no Inspector, caso forceEnable seja true, ele ficará habilitado <br/>
     * É especialmente útil quanto a condição principal leva a propriedade a figurar como habilitada, porém, é sabido que deve estar desabilitado.
     * Ainda não 100% testado.
     */
    public boolean isForceEnable() {
        return forceEnable;
    }

    /**
     * Independentemente da organização de uma propriedade no Inspector, caso forceEnable seja true, ele ficará habilitado <br/>
     * É especialmente útil quanto a condição principal leva a propriedade a figurar como habilitada, porém, é sabido que deve estar desabilitado.
     * Ainda não 100% testado.
     */
    public void setForceEnable(boolean forceEnable) {
        this.forceEnable = forceEnable;
        if (this.forceEnable) this.forceDisable = false;
    }
    
    /**
     * Força a uma condição: Disabled! - Veja: forceDisable
     * @param sn
     * @return: this!
    */    
    public InspectorProperty PropertyForceDisable(boolean sn) {
        this.setForceDisable(sn);
        return this;
    }
   
    /**
     * Força a uma condição: Enabled! - Veja: forceEnable
     * @param sn
     * @return: this!
    */   
    public InspectorProperty PropertyForceEnable(boolean sn) {
        this.setForceEnable(sn);
        return this;
    }
    
    public List<String> opcoesMenu = null;

    public boolean isMineCaption(String byCaption) {
        return caption.equals(CaptionFromConfig(byCaption));
    }

    public boolean isMe(String byProperty) {
        return property.equals(byProperty);
    }

    public boolean IsIgual(InspectorProperty este) {
        boolean r = this.tipo.equals(este.tipo);
        if (!r) return false;
        if (agrupada == null && este.agrupada == null) {
            return true;
        }
        if (agrupada != null && este.agrupada != null) {
            return agrupada.getPropriedade().equals(este.agrupada.getPropriedade());
        } else {
            return false;
        }
    }
    
    /**
     * Método que seta uma tag para a property. Pode ser usado para diferenciar proporties de mesmo tipo em um Inspector.
     * @param tg
     * @return 
     */
    public InspectorProperty setTag(int tg) {
        this.Tag = tg;
        return this;
    }

    public int getTag() {
        return Tag;
    }
    
    public ArrayList<String> QuaisCanEditIf(String valor) {
        if (agrupada == null) return null;
        return agrupada.QuaisEnableIf(valor);
    }

    public ArrayList<String> QuaisCanEditNotIf(String valor) {
        if (agrupada == null) {
            return null;
        }
        return agrupada.QuaisDisableIf(valor);
    }

    /**
     * Cria um grupo (caso não haja um grupo) e coloca um condição para CanEdit (des/habilitar edição).
     * @param enableIf
     * @param afetados
     * @return this apenas para facilitar a criação de propriedades
     */
    public InspectorProperty AddCondicao(String[] enableIf, String[] afetados) {
        if (agrupada == null) {
            agrupada = new InspectorPprtAgrupador(property);
        }
        agrupada.AddCondicao(enableIf, afetados);
        //facilita a criação de propriedade com apenas uma condicional
        return this;
    }

    /**
     * Adiciona Properties que serão habilitadas se esta property tiver valor = true.
     */
    public InspectorProperty AddCondicaoForTrue(String[] afetados) {
        return AddCondicao(new String[] {Boolean.toString(true)}, afetados);
    }
    
    /**
     * Adiciona Properties que serão habilitadas se esta property tiver valor = false.
     */
    public InspectorProperty AddCondicaoForFalse(String[] afetados) {
        return AddCondicao(new String[] {Boolean.toString(false)}, afetados);
    }
    
    public static InspectorProperty getPropertySeparador(String caption) {
        InspectorProperty pprt = new InspectorProperty();
        pprt.tipo = TipoDeProperty.tpSeparador;
        pprt.caption = caption;
        pprt.dica = "";
        return pprt;
    }

    public static InspectorProperty FindByCaption(ArrayList<InspectorProperty> lista, String cap) {
        for (InspectorProperty res : lista) {
            if (res.isMineCaption(cap)) {
                return res;
            }
        }
        return null;
    }

    public static InspectorProperty FindByProperty(ArrayList<InspectorProperty> lista, String ppr) {
        for (InspectorProperty res : lista) {
            if (res.isMe(ppr)) {
                return res;
            }
        }
        return null;
    }

    public static InspectorProperty PropertyFactorySN(String partCaption, String prop, boolean valor) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.property = prop;
        ppr.valor_string = Boolean.toString(valor);
        ppr.tipo = InspectorProperty.TipoDeProperty.tpBooleano;
        return ppr;
    }

    public static InspectorProperty PropertyFactorySeparador(String partCaption) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.tipo = InspectorProperty.TipoDeProperty.tpSeparador;
        ppr.property = partCaption;
        return ppr;
    }
    
    public static InspectorProperty PropertyFactorySeparador(String partCaption, boolean encolhido) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.tipo = InspectorProperty.TipoDeProperty.tpSeparador;
        ppr.property = partCaption;
        if (encolhido) ppr.opcional = "-";
        return ppr;
    }
    
    public static InspectorProperty PropertyFactoryApenasLeituraSN(String partCaption, boolean valor){
        return PropertyFactoryApenasLeituraTexto(partCaption, Editor.fromConfiguracao.getValor("Inspector.obj." + Boolean.toString(valor).toLowerCase()));
    }
    
    public static InspectorProperty PropertyFactoryApenasLeituraTexto(String partCaption, String valor) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.property = partCaption;
        ppr.valor_string = valor;
        ppr.tipo = InspectorProperty.TipoDeProperty.tpApenasLeituraTexto;
        return ppr;
    }

    public static InspectorProperty PropertyFactoryCommand(String pprt) {
        InspectorProperty ppr = new InspectorProperty(pprt.toLowerCase());
        ppr.valor_string = "...";
        ppr.property = pprt;
        ppr.tipo = InspectorProperty.TipoDeProperty.tpCommand;
        return ppr;
    }

    public static InspectorProperty PropertyFactoryCommand(String pprt, String partCaption) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.valor_string = "...";
        ppr.property = pprt;
        //ppr.property = partCaption;//não pode ser assim
        ppr.tipo = InspectorProperty.TipoDeProperty.tpCommand;
        return ppr;
    }

    public static InspectorProperty PropertyFactoryCommand(String pprt, String partCaption, String valor) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.valor_string = valor;
        //ppr.valor_string = "...";
        ppr.property = pprt;
        //ppr.property = partCaption;//não pode ser assim
        ppr.tipo = InspectorProperty.TipoDeProperty.tpCommand;
        return ppr;
    }

    public static InspectorProperty PropertyFactoryCommandPlain(String pprt, String partCaption, String caption) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.caption = caption;
        ppr.valor_string = "...";
        ppr.property = pprt;
        //ppr.property = partCaption;//não pode ser assim
        ppr.tipo = InspectorProperty.TipoDeProperty.tpCommand;
        return ppr;
    }

    public static InspectorProperty PropertyFactoryActionSelect(String Caption, String valor, String strID) {
        InspectorProperty ppr = new InspectorProperty();
        ppr.caption = Caption;
        
        String tmp = Editor.fromConfiguracao.getValor(FullDica(Caption));
        if (tmp.equals(FullDica(Caption))) {
            ppr.GeraTextoHelper(FullDica("siga"));
        } else {
            ppr.GeraTextoHelper(FullDica(Caption));
            ppr.configuracaoStr = Caption;
        }
        ppr.valor_string = valor;
        ppr.tipo = InspectorProperty.TipoDeProperty.tpSelecObject;
        ppr.property = strID;
        return ppr;
    }
    
//    public static InspectorProperty PropertyFactoryApenasLeituraCor(String partCaption, String valor) {
//        InspectorProperty ppr = new InspectorProperty(partCaption);
//        ppr.valor_string = valor;
//        ppr.tipo = InspectorProperty.TipoDeProperty.tpApenasLeituraCor;
//        return ppr;
//    }

    public static InspectorProperty PropertyFactoryCor(String partCaption, String prop, Color valor) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.property = prop;
        ppr.valor_string = util.Utilidades.ColorToString(valor);//   String.valueOf(valor.getRGB());
        ppr.tipo = InspectorProperty.TipoDeProperty.tpCor;
        return ppr;
    }

    public static InspectorProperty PropertyFactoryTextoL(String partCaption, String prop, String valor) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.property = prop;
        ppr.valor_string = valor;
        ppr.tipo = InspectorProperty.TipoDeProperty.tpTextoLongo;
        return ppr;
    }
    
    public static InspectorProperty PropertyFactoryTexto(String partCaption, String prop, String valor) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.property = prop;
        ppr.valor_string = valor;
        ppr.tipo = InspectorProperty.TipoDeProperty.tpTextoNormal;
        return ppr;
    }

    public static InspectorProperty PropertyFactoryNumero(String partCaption, String prop, int valor) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.property = prop;
        ppr.valor_string = Integer.toString(valor);
        ppr.tipo = InspectorProperty.TipoDeProperty.tpNumero;
        return ppr;
    }
    
    public static InspectorProperty PropertyFactoryMenu(String partCaption, String prop, int valor, List<String> opc) {
        InspectorProperty ppr = new InspectorProperty(partCaption);
        ppr.property = prop;
        ppr.valor_string = Integer.toString(valor);
        ppr.tipo = InspectorProperty.TipoDeProperty.tpMenu;
        ppr.opcoesMenu = new ArrayList<>(opc);
        return ppr;
    }

}        
