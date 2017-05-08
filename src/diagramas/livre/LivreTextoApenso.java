/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.livre;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.preAnyDiagrama.PreTextoApenso;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author ccandido
 */
public class LivreTextoApenso extends PreTextoApenso {

    private static final long serialVersionUID = 2640523135001628941L;

    public LivreTextoApenso(Diagrama modelo) {
        super(modelo);
    }

    public LivreTextoApenso(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();

        InspectorProperty tmp = InspectorProperty.FindByProperty(res, "setTexto");
        tmp.ReSetCaptionFromConfig("texto");
        tmp.tipo = InspectorProperty.TipoDeProperty.tpTextoLongo;

        res.add(res.indexOf(tmp), InspectorProperty.PropertyFactoryTexto("texto.titulo", "setTitulo", getTitulo()));
        res.add(res.indexOf(tmp), InspectorProperty.PropertyFactorySN("texto.painttitulo", "setPaintTitulo", isPaintTitulo()));

        res.add(InspectorProperty.PropertyFactoryMenu("texto.alinhamento", "setAlinhamentoByInt", getAlinhamento().ordinal(), Editor.fromConfiguracao.getLstTextoAlin()));
        res.add(InspectorProperty.PropertyFactorySN("texto.alinhamento.v", "setCentrarVertical", isCentrarVertical()));

        res.add(InspectorProperty.PropertyFactoryNumero("diagrama.detalhe.alfa", "SetAlfa", (int) (100 * getAlfa())));

        InspectorProperty txtTipo = InspectorProperty.PropertyFactoryMenu("texto.tipo", "setTipobyInt", getTipo().ordinal(), Editor.fromConfiguracao.getLstTipoTexto());
        res.add(txtTipo);

        res.add(InspectorProperty.PropertyFactoryCor("texto.bkcolor", "setBackColor", getBackColor()));

        tmp = InspectorProperty.FindByProperty(res, "setForeColor");
        tmp.ReSetCaptionFromConfig("texto.forecolor");

        res.add(InspectorProperty.PropertyFactorySN("texto.sombra", "setSombra", isSombra()).AddCondicaoForTrue(new String[]{"setCorSombra"}));

        res.add(InspectorProperty.PropertyFactoryCor("texto.sombra.cor", "setCorSombra", getCorSombra()));
        res.add(InspectorProperty.PropertyFactorySeparador("texto.gradiente", true));
        String[] grupo = new String[]{"setGradienteStartColor", "setGradienteEndColor", 
            //"setGradientePinteDetalhe", "setGradienteCorDetalhe", 
            "setGDirecao"
        };

        res.add(InspectorProperty.PropertyFactorySN("texto.gradiente.is", "setGradiente", isGradiente()).AddCondicaoForTrue(grupo)
                .AddCondicaoForTrue(new String[]{"setGradientePinteDetalhe", "setGradienteCorDetalhe"})
                .AddCondicaoForFalse(new String[]{"setBackColor"})
        );

        res.add(InspectorProperty.PropertyFactoryCor("texto.gradiente.startcor", "setGradienteStartColor", getGradienteStartColor()));

        res.add(InspectorProperty.PropertyFactoryCor("texto.gradiente.endcor", "setGradienteEndColor", getGradienteEndColor()));

        res.add(InspectorProperty.PropertyFactorySN("texto.gradiente.detalhe", "setGradientePinteDetalhe", isGradientePinteDetalhe()).AddCondicaoForTrue(new String[]{"setGradienteCorDetalhe"}));

        res.add(InspectorProperty.PropertyFactoryCor("texto.gradiente.detalhecor", "setGradienteCorDetalhe", getGradienteCorDetalhe()));

        res.add(InspectorProperty.PropertyFactoryMenu("texto.gradiente.direcao", "setGDirecao", getGDirecao(), Editor.fromConfiguracao.getLstDirecao(Controler.Comandos.cmdTexto)));

        res.add(InspectorProperty.PropertyFactoryNumero("diagrama.detalhe.alfa", "SetAlfa", (int) (100 * getAlfa())));
        ArrayList<String> ngrp = new ArrayList<>(Arrays.asList(grupo));
        ngrp.add("setGradiente");
        //ngrp.add("setCorSombra");
        //ngrp.add("setSombra");
        //ngrp.add("setBackColor");
        ngrp.add("SetAlfa");

        txtTipo.AddCondicao(new String[]{"2", "3"}, new String[]{"setGradientePinteDetalhe", "setGradienteCorDetalhe", "setCorSombra", "setSombra"
               // ,"setBackColor"
        });
        txtTipo.AddCondicao(new String[]{"1", "2", "3"}, ngrp.toArray(new String[]{}));
        txtTipo.AddCondicao(new String[]{"0"}, new String[]{"setAutosize"});
//        txtTipo.AddCondicao(new String[]{"2", "3"}, new String[]{});
        //tpEmBranco, tpNota, tpRetangulo, tpRetanguloArred

        tmp = InspectorProperty.FindByProperty(res, "setTextoAdicional");
        res.remove(tmp);

        res.add(InspectorProperty.PropertyFactorySN("texto.autosize", "setAutosize", isAutosize()));

        //if (isEhLegenda()) {
            res.add(InspectorProperty.PropertyFactorySeparador("texto.atreladoalinha"));
            res.add(InspectorProperty.PropertyFactorySN("texto.movimentacaomanual", "setMovimentacaoManual", isMovimentacaoManual()));
        //}

        return res;
    }
    
    @Override
    public boolean isAlinhavel() {
        return false;
    }

//
//    protected boolean ehlegenda = false;
//
//    public boolean isEhLegenda() {
//        return ehlegenda;
//    }
//
//    protected void setEhLegenda(boolean legenda) {
//        this.ehlegenda = legenda;
//    }

//    @Override
//    protected void ToXmlValores(Document doc, Element me) {
//        super.ToXmlValores(doc, me);
//        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "EhLegenda", isEhLegenda()));
//    }
//
//    @Override
//    public boolean LoadFromXML(Element me, boolean colando) {
//        if (!super.LoadFromXML(me, colando)) {
//            return false;
//        }
//        setEhLegenda(util.XMLGenerate.getValorBooleanFrom(me, "EhLegenda"));
//        return true;
//    }
}
