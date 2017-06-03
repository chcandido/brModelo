package diagramas.conceitual;

import controlador.Controler;
import controlador.Editor;
import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.FormaElementar;
import desenho.linhas.SuperLinha;
import desenho.preAnyDiagrama.PreTexto;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author ccandido
 */
public class Texto extends PreTexto { 

    private static final long serialVersionUID = -8327102253638096870L;

    public Texto(Diagrama modelo) {
        super(modelo);
    }

    public Texto(Diagrama modelo, String texto) {
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
        
        res.add(InspectorProperty.PropertyFactorySN("texto.sombra", "setSombra", isSombra()).AddCondicaoForTrue(new String[] {"setCorSombra"}));
        
        res.add(InspectorProperty.PropertyFactoryCor("texto.sombra.cor", "setCorSombra", getCorSombra()));

        res.add(InspectorProperty.PropertyFactorySN("texto.autosize", "setAutosize", isAutosize()));
                
        res.add(InspectorProperty.PropertyFactorySeparador("texto.gradiente"));
        
        String[] grupo = new String[]{"setGradienteStartColor", "setGradienteEndColor",
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
        ngrp.add("SetAlfa");
        ngrp.add("setBackColor");

        txtTipo.AddCondicao(new String[]{"2", "3"}, new String[]{"setGradientePinteDetalhe", "setGradienteCorDetalhe", "setCorSombra", "setSombra"
        });
        txtTipo.AddCondicao(new String[]{"1", "2", "3"}, ngrp.toArray(new String[]{}));
        txtTipo.AddCondicao(new String[]{"0"}, new String[]{"setAutosize"});

        tmp = InspectorProperty.FindByProperty(res, "setTextoAdicional");
        res.remove(tmp);

        res.add(InspectorProperty.PropertyFactorySeparador("texto.atreladoalinha"));
        if (LinhaMestre != null) {
           res.add(InspectorProperty.PropertyFactorySN("texto.movimentacaomanual", "setMovimentacaoManual", isMovimentacaoManual()));
        } 
        ArrayList<SuperLinha> lins = getListaDeLinhas();
        if (lins.size() > 0) {
            int a = lins.indexOf(getLinhaMestre());
            ArrayList<String> tbls = getStrListaDeLinhas(lins);
            tbls.add(0, Editor.fromConfiguracao.getValor("Inspector.obj.selecione"));
            res.add(InspectorProperty.PropertyFactoryMenu("texto.linhamestre", "SetLinhaMestreInt", a + 1, tbls));
        } else {
            res.add(InspectorProperty.PropertyFactoryApenasLeituraTexto("texto.linhamestre", ""));
        }
        return res;
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        
        me.appendChild(util.XMLGenerate.ValorText(doc, "Titulo", getTitulo()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "PaintTitulo", isPaintTitulo()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Alinhamento", getAlinhamento().ordinal()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "CentrarVertical", isCentrarVertical()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Tipo", getTipo().ordinal()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "BackColor", getBackColor()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Sombra", isSombra()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "CorSombra", getCorSombra()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Gradiente", isGradiente()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteStartColor", getGradienteStartColor()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteEndColor", getGradienteEndColor()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "GradientePinteDetalhe", isGradientePinteDetalhe()));
        me.appendChild(util.XMLGenerate.ValorColor(doc, "GradienteCorDetalhe", getGradienteCorDetalhe()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "GDirecao", getGDirecao()));
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Alfa", (int)(100 * getAlfa())));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Autosize", isAutosize()));

        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "MovimentacaoManual", isMovimentacaoManual()));

        //remover dicionário do XML do objeto.
        NodeList nl = me.getElementsByTagName("Dicionario");
        if (nl != null && nl.getLength() > 0) {
            me.removeChild(nl.item(0));
        }
        
    }
    
    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        int l = util.XMLGenerate.getValorIntegerFrom(me, "GDirecao");
        if (l != -1) {
            setGDirecao(l);
        }
        l = util.XMLGenerate.getValorIntegerFrom(me, "Alfa");
        if (l != -1) {
            SetAlfa(l);
        }
        setTitulo(util.XMLGenerate.getValorTextoFrom(me, "Titulo"));
        setPaintTitulo(util.XMLGenerate.getValorBooleanFrom(me, "PaintTitulo"));
        setCentrarVertical(util.XMLGenerate.getValorBooleanFrom(me, "CentrarVertical"));
        setTipobyInt(util.XMLGenerate.getValorIntegerFrom(me, "Tipo"));
        setAlinhamentoByInt(util.XMLGenerate.getValorIntegerFrom(me, "Alinhamento"));
        Color c = util.XMLGenerate.getValorColorFrom(me, "BackColor");
        if (c != null) setBackColor(c);
        setSombra(util.XMLGenerate.getValorBooleanFrom(me, "Sombra"));
        c = util.XMLGenerate.getValorColorFrom(me, "CorSombra");
        if (c != null) setCorSombra(c);

        setGradiente(util.XMLGenerate.getValorBooleanFrom(me, "Gradiente"));
        setGradientePinteDetalhe(util.XMLGenerate.getValorBooleanFrom(me, "GradientePinteDetalhe"));
        c = util.XMLGenerate.getValorColorFrom(me, "GradienteStartColor");
        if (c != null) setGradienteStartColor(c);
        c = util.XMLGenerate.getValorColorFrom(me, "GradienteEndColor");
        if (c != null) setGradienteEndColor(c);
        c = util.XMLGenerate.getValorColorFrom(me, "GradienteCorDetalhe");
        if (c != null) setGradienteCorDetalhe(c);

        setMovimentacaoManual(util.XMLGenerate.getValorBooleanFrom(me, "MovimentacaoManual"));
        setAutosize(util.XMLGenerate.getValorBooleanFrom(me, "Autosize"));
        
        return true;
    }

    // <editor-fold defaultstate="collapsed" desc="Como Tag">
    
    private boolean MovimentacaoManual = false;

    public boolean isMovimentacaoManual() {
        return MovimentacaoManual;
    }

    public void setMovimentacaoManual(boolean MovimentacaoManual) {
        if (this.MovimentacaoManual != MovimentacaoManual) {
            this.MovimentacaoManual = MovimentacaoManual;

            if (this.MovimentacaoManual || (LinhaMestre == null) || getMaster().IsMultSelecionado()) {
                return;
            }
            DirectPosicione();
            Reposicione();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        MovimentacaoManual = true;
    }
    
    private SuperLinha LinhaMestre = null;

    public SuperLinha getLinhaMestre() {
        return LinhaMestre;
    }

    @Override
    public void SetLinhaMestre(SuperLinha LinhaMestre) {
        if (this.LinhaMestre == LinhaMestre) {
            return;
        }
        this.LinhaMestre = LinhaMestre;
        MovimentacaoManual = false;
    }

    public void setLinhaMestre(SuperLinha LinhaMestre) {
        if (this.LinhaMestre == LinhaMestre) {
            return;
        }
        if (this.LinhaMestre != null) {
            this.LinhaMestre.SetTag(null);
        }
        this.LinhaMestre = LinhaMestre;
        if (this.LinhaMestre != null) {
           this.LinhaMestre.SetTag(this);
        }
        MovimentacaoManual = false;
    }

    @Override
    public void Posicione() {
        if (isMovimentacaoManual() || (LinhaMestre == null) || isSelecionado()) {
            return;
        }
        DirectPosicione();
    }

    protected void DirectPosicione() {
        int qtdp = LinhaMestre.getPontos().size() % 2;
        int x = 0, y = 0;
        if (qtdp == 1) {
            int a = (LinhaMestre.getPontos().size() / 2);
            Point central = LinhaMestre.getPontos().get(a).getCentro();
            x = central.x;
            y = central.y;
        } else {
            int a = (LinhaMestre.getPontos().size() / 2);
            Point ra = LinhaMestre.getPontos().get(a -1).getCentro();
            Point rb = LinhaMestre.getPontos().get(LinhaMestre.getPontos().size() - a).getCentro();
            int tmp = ra.x - rb.x;
            if (tmp > -1) {
                x = tmp / 2 + rb.x;
            } else {
                x = (rb.x - ra.x) / 2 + ra.x;
            }
            tmp = ra.y - rb.y;
            if (tmp > -1) {
                y = tmp / 2 + rb.y;
            } else {
                y = (rb.y - ra.y) / 2 + ra.y;
            }
        }
        setLocation(x+distSelecao, y+distSelecao);
        Reenquadre();
    }
    
    public void SetLinhaMestreInt(int lin) {
        ArrayList<SuperLinha> lins = getListaDeLinhas();
        //virá sempre com +1 por conta do título do menu no inspector (primeiro item).
        lin--;
        if (lin >= 0 && lin < lins.size()) {
            setLinhaMestre(lins.get(lin));
            DirectPosicione();
            Reposicione();
        } else {
            setLinhaMestre(null);
        }
    }
    
    public ArrayList<SuperLinha> getListaDeLinhas() {
        ArrayList<SuperLinha> res = new ArrayList<>();
        for (FormaElementar fe : getMaster().getListaDeItens()) {
            if (fe instanceof SuperLinha) {
                res.add((SuperLinha) fe);
            }
        }
        return res;
    }

    public ArrayList<String> getStrListaDeLinhas(ArrayList<SuperLinha> lst) {
        ArrayList<String> res = new ArrayList<>();
        for (SuperLinha fe : lst) {
            String pa = fe.getFormaPontaA() == null ? "<>" : fe.getFormaPontaA().getTexto();
            String pb = fe.getFormaPontaB() == null ? "<>" : fe.getFormaPontaB().getTexto();
            res.add(pa + " <--> " + pb);
        }
        return res;
    }

    // </editor-fold>

    @Override
    protected void ReSizedByAutoSize() {
        super.ReSizedByAutoSize();
            if (this.MovimentacaoManual || (LinhaMestre == null) || getMaster().IsMultSelecionado()) {
                return;
            }
            DirectPosicione();
            Reposicione();
    }
 }
