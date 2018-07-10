/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.conceitual;

import controlador.Controler;
import controlador.Editor;
import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.Ancorador;
import desenho.linhas.PontoDeLinha;
import desenho.preAnyDiagrama.PreAtributo;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class Atributo extends PreAtributo {

    private static final long serialVersionUID = -5223581706365131753L;

    public Atributo(Diagrama modelo) {
        super(modelo);
        nodic = false;
    }

    public Atributo(Diagrama modelo, String texto) {
        super(modelo, texto);
        nodic = false;
    }
    
    @Override
    public ArrayList<Integer> getAncorasCode() {
        ArrayList<Integer> res = super.getAncorasCode();
        Integer[] ancorasCode = new Integer[]{Ancorador.CODE_ORG_AT};
        res.addAll(Arrays.asList(ancorasCode));
        return res;
    }

    /**
     * Serve apenas para alterar a direção pelo Inspector
     *
     * @param di
     */
    public void setDirecaoFromInspector(int di) {
//        Up = 0
//        Right = 1,
//        Down = 2,
//        Left = 3,
//        Horizontal = 4,
//        Vertical = 5
//            lstDirecao.add(getValor("Inspector.lst.direcao.left")); //0
//            lstDirecao.add(getValor("Inspector.lst.direcao.right")); //1
        if (di == 0) {
            //di = 3;
            setDirecao(Direcao.Left);
        } else {
            //di = 1;
            setDirecao(Direcao.Right);
        }
    }

    /**
     * Serve apenas para retornar a direção para o Inspector
     */
    private int getDirecaoForInspector() {
//        Up = 0
//        Right = 1,
//        Down = 2,
//        Left = 3,
//        Horizontal = 4,
//        Vertical = 5
//            lstDirecao.add(getValor("Inspector.lst.direcao.left")); //0
//            lstDirecao.add(getValor("Inspector.lst.direcao.right")); //1
        if (getDirecao() == Direcao.Left) {
            return 0;
        } else {
            return 1;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Campos">
    public int getCardMaxima() {
        return cardMaxima;
    }

    public void setCardMaxima(int cardMaxima) {
        if (cardMaxima == 0 || cardMaxima < -1) {
            cardMaxima = 1;
        }
        if (this.cardMaxima != cardMaxima) {
            this.cardMaxima = cardMaxima;
            if (this.cardMinima > cardMaxima && cardMaxima != -1) {
                setCardMinima(cardMaxima);
            }
            InvalidateArea();
        }
    }

    public int getCardMinima() {
        return cardMinima;
    }

    public void setCardMinima(int cardMinima) {
        if (cardMinima < -1) {
            cardMinima = 0;
        }
        if (this.cardMinima != cardMinima) {
            this.cardMinima = cardMinima;
            if (cardMinima > this.cardMaxima && this.cardMaxima != -1) {
                if (cardMinima == 0) {
                    this.cardMaxima = 1;
                } else {
                    this.cardMaxima = cardMinima;
                }
            }
            //if (isMultivalorado()) {
                if (this.cardMinima == 0) {
                    setOpcional(true);
                } else {
                    setOpcional(false);
                }
            //}
            InvalidateArea();
        }
    }

    @Override
    public void setOpcional(boolean opcional) {
        super.setOpcional(opcional);
        if (opcional && cardMinima != 0) {
            cardMinima = 0;
        } else if (!opcional && cardMinima == 0) {
            setCardMinima(1);
        }
//        if (opcional && !isMultivalorado() && cardMinima != 0) {
//            cardMinima = 0;
//        } else if (!opcional && !isMultivalorado() && cardMinima == 0) {
//            cardMinima = 1;
//        }
    }

    public boolean isMultivalorado() {
        return multivalorado;
    }

    public void setMultivalorado(boolean multivalorado) {
        if (this.multivalorado != multivalorado) {
            this.multivalorado = multivalorado;
            if (isMultivalorado()) {
                if (this.cardMinima == 0) {
                    setOpcional(true);
                } else {
                    setOpcional(false);
                }
            }
            InvalidateArea();
        }
    }

    public String getTipoAtributo() {
        return tipoAtributo;
    }

    public void setTipoAtributo(String tipo) {
        this.tipoAtributo = tipo;
    }

    private int cardMaxima = -1;
    private boolean multivalorado = false;
    private String tipoAtributo = "";
    private int cardMinima = 1;

    //</editor-fold>
//    @Override
//    public String getTexto() {
//        return super.getTexto() + (isMultivalorado() ? " (" + getCardMinFromString() + ", " + getCardMaxFromString() + ")" : "");
//    }
    public String getCardMinFromString() {
        int res = getCardMinima();
        String s = String.valueOf(res);
        if (res == -1) {
            s = "n";
        }
        return s;
    }

    public String getCardMaxFromString() {
        int res = getCardMaxima();
        String s = String.valueOf(res);
        if (res == -1) {
            s = "n";
        }
        return s;
    }

    public void setCardMinFromString(String valor) {
        setCard(valor, false);
    }

    public void setCardMaxFromString(String valor) {
        setCard(valor, true);
    }

    public void setCard(String vl, boolean max) {
        if (vl.toLowerCase().equals("n")) {
            vl = "-1";
        }
        int valor = -1;
        try {
            valor = Integer.valueOf(vl);
        } catch (Exception e) {
            util.BrLogger.Logger("ERROR_SET_CARD", e.getMessage());
        }
        if (valor < -1) {
            util.BrLogger.Logger("ERROR_SET_CARD", "INVALID!");
            valor = -1;
        }
        if (max) {
            setCardMaxima(valor);
        } else {
            setCardMinima(valor);
        }
    }

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();
        res.add(InspectorProperty.PropertyFactoryMenu("direcao", "setDirecaoFromInspector", getDirecaoForInspector(), Editor.fromConfiguracao.getLstDirecao(Controler.Comandos.cmdAtributo)));
        res.add(InspectorProperty.PropertyFactorySN("atributo.autosize", "setAutosize", isAutosize()));
        res.add(InspectorProperty.PropertyFactorySeparador("esquema"));
        res.add(InspectorProperty.PropertyFactorySN("atributo.identificador", "setIdentificador", isIdentificador()));
        res.add(InspectorProperty.PropertyFactorySN("atributo.opcional", "setOpcional", isOpcional()));
        res.add(InspectorProperty.PropertyFactoryApenasLeituraSN("atributo.composto", isAtributoComposto()));

        res.add(InspectorProperty.PropertyFactorySN("atributo.multivalorado", "setMultivalorado",
                isMultivalorado()).AddCondicaoForTrue(new String[]{"setCardMinFromString", "setCardMaxFromString"}).AddCondicaoForFalse(new String[]{"setOpcional"}));
        res.add(InspectorProperty.PropertyFactoryTexto("atributo.cardinalidademinima", "setCardMinFromString", getCardMinFromString()));
        res.add(InspectorProperty.PropertyFactoryTexto("atributo.cardinalidademaxima", "setCardMaxFromString", getCardMaxFromString()));

        res.add(InspectorProperty.PropertyFactoryTexto("atributo.tipoatributo", "setTipoAtributo", getTipoAtributo()));
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "entidade.orgattr").setTag(CONST_DO_ORGATTR));

        return res;
    }

    public final int CONST_DO_ORGATTR = 123;

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "DirecaoFromInspector", getDirecaoForInspector()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Autosize", isAutosize()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Identificador", isIdentificador()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Opcional", isOpcional()));
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Multivalorado", isMultivalorado()));
        me.appendChild(util.XMLGenerate.ValorString(doc, "CardMinFromString", getCardMinFromString()));
        me.appendChild(util.XMLGenerate.ValorString(doc, "CardMaxFromString", getCardMaxFromString()));
        me.appendChild(util.XMLGenerate.ValorString(doc, "TipoAtributo", getTipoAtributo()));

//        PontoDeLinha pt = PontoLigacaoPrincipal(null);
//        if (pt != null) {
//            me.appendChild(util.XMLGenerate.ValorRefFormElementar(doc, "PontoLigacaoPrincipal", pt.getDono()));
//        } else {
//            me.appendChild(util.XMLGenerate.ValorRefFormElementar(doc, "PontoLigacaoPrincipal", null));
//        }
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        int l = util.XMLGenerate.getValorIntegerFrom(me, "DirecaoFromInspector");
        if (l != -1) {
            setDirecaoFromInspector(l);
        }

        setAutosize(util.XMLGenerate.getValorBooleanFrom(me, "Autosize"));
        setIdentificador(util.XMLGenerate.getValorBooleanFrom(me, "Identificador"));
        setOpcional(util.XMLGenerate.getValorBooleanFrom(me, "Opcional"));
        setMultivalorado(util.XMLGenerate.getValorBooleanFrom(me, "Multivalorado"));
        setCardMinFromString(util.XMLGenerate.getValorStringFrom(me, "CardMinFromString"));
        setCardMaxFromString(util.XMLGenerate.getValorStringFrom(me, "CardMaxFromString"));
        setTipoAtributo(util.XMLGenerate.getValorStringFrom(me, "TipoAtributo"));

        return true;
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == CONST_DO_ORGATTR) {
            PontoDeLinha pp = PontoLigacaoPrincipal(null);
            List<Atributo> pts = getListaDePontosLigados().stream()
                    .filter(p -> p != pp && p.getDono().getOutraPonta(p).getEm() instanceof Atributo).map(p -> (Atributo) p.getDono().getOutraPonta(p).getEm())
                    .collect(Collectors.toList());

            boolean ja = Alinhe(pts);

            for (Atributo att : pts) {
                if (att.OrganizeAtributos()) {
                    ja = true;
                }
            }
            if (ja) {
                DoMuda();
            }
        }
    }

    private boolean Alinhe(List<Atributo> atrs) {
        if (atrs.isEmpty()) {
            return false;
        }
        int distH = 5;
        int H = atrs.stream().mapToInt(a -> a.getHeight()).sum() + ((atrs.size() - 1) * distH);
        int T = getTop() - (H / 2) + getHeight() / 2;
        int distW = 40;
        boolean res = false;
        if (getDirecaoLigacao() == Direcao.Left) {
            int L = getLeftWidth() + distW;
            for (Atributo a : atrs) {
                if (getMaster().getItensSelecionados().indexOf(a) > -1) {
                    T += a.getHeight() + distH;
                    continue;
                }
                a.setDirecaoLigacao(Direcao.Left);
                res = alteraLeftTop(a, L, T);
                T += a.getHeight() + distH;
            }
        } else {
            for (Atributo a : atrs) {
                if (getMaster().getItensSelecionados().indexOf(a) > -1) {
                    T += a.getHeight() + distH;
                    continue;
                }
                int L = getLeft() - distW - a.getWidth();
                a.setDirecaoLigacao(Direcao.Right);
                res = alteraLeftTop(a, L, T);
                T += a.getHeight() + distH;
            }
        }

        return res;
    }

    public boolean OrganizeAtributos() {
        final PontoDeLinha pp = PontoLigacaoPrincipal(null);
        List<Atributo> pts = getListaDePontosLigados().stream()
                .filter(p -> p != pp && p.getDono().getOutraPonta(p).getEm() instanceof Atributo).map(p -> (Atributo) p.getDono().getOutraPonta(p).getEm())
                .collect(Collectors.toList());

        boolean ja = Alinhe(pts);

        for (Atributo att : pts) {
            if (att.OrganizeAtributos()) {
                ja = true;
            }
        }
        return ja;
    }

    private boolean alteraLeftTop(Atributo a, int L, int T) {
        int l = a.getLeft() - L;
        int t = a.getTop() - T;
        if (l != 0 || t != 0) {
            Rectangle r = new Rectangle(l, t, 0, 0);
            a.ReciveFormaResize(r);
            a.Reenquadre();
            return true;
        }
        return false;
    }

    public List<Atributo> getAtributos() {
        final PontoDeLinha pp = PontoLigacaoPrincipal(null);

        List<Atributo> lst = getListaDePontosLigados().stream()
                .filter(p -> (p != pp) && (p.getDono().getOutraPonta(p).getEm() instanceof Atributo))
                .map(p -> (Atributo) (p.getDono().getOutraPonta(p).getEm()))
                .collect(Collectors.toList());
        return lst;
    }

    @Override
    public String getTextoToDraw() {
        return super.getTexto() + (isMultivalorado() ? " (" + getCardMinFromString() + ", " + getCardMaxFromString() + ")" : "");
    }

    @Override
    public boolean isAlinhavel() {
        return false;
    }

    @Override
    public void runAncorasCode(int cod) {
        super.runAncorasCode(cod);
        if (cod == Ancorador.CODE_ORG_AT) {
            DoAnyThing(CONST_DO_ORGATTR);
        }
    }

    @Override
    public String WhatDrawOnAcorador(Integer c) {
        if (c == Ancorador.CODE_ORG_AT) {
            return "diagrama.ancordor.2.img";
        }
        return super.WhatDrawOnAcorador(c);
    }
}
