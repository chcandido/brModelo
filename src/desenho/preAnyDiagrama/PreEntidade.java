/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preAnyDiagrama;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.Ancorador;
import desenho.FormaElementar;
import desenho.formas.Forma;
import desenho.formas.FormaNaoRetangularBase;
import desenho.formas.FormaRetangular;
import desenho.linhas.Linha;
import desenho.linhas.PontoDeLinha;
import desenho.linhas.SuperLinha;
import diagramas.conceitual.Atributo;
import diagramas.conceitual.DiagramaConceitual;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ccandido
 */
public class PreEntidade extends FormaRetangular {

    private static final long serialVersionUID = 6302631403788417883L;

    public PreEntidade(Diagrama modelo) {
        super(modelo);
        showOrgDiag = true;
//        getAncorasCode().add(Ancorador.CODE_ORG_AT);
//        getAncorasCode().add(CODE_EDT_ATR);
    }

    public PreEntidade(Diagrama modelo, String texto) {
        super(modelo, texto);
        showOrgDiag = true;
//        getAncorasCode().add(Ancorador.CODE_ORG_AT);
//        getAncorasCode().add(CODE_EDT_ATR);
    }

    @Override
    public ArrayList<Integer> getAncorasCode() {
        ArrayList<Integer> res = super.getAncorasCode();
        Integer[] ancorasCode = new Integer[]{Ancorador.CODE_ORG_AT, CODE_EDT_ATR};
        res.addAll(Arrays.asList(ancorasCode));
        return res;
    }
    
    @Override
    public boolean CanLiga(Forma forma, Linha lin) {
        if (forma == null) {
            return true;
        }
        if (super.CanLiga(forma, lin) && (!(forma instanceof PreEntidade))) {
            return forma.CanLiga(this, lin);
        }
        return false;
    }

    public final int CONST_DO_ORGATTR = 123;

    @Override
    public ArrayList<InspectorProperty> GenerateProperty() {
        ArrayList<InspectorProperty> res = super.GenerateProperty();

        res.add(InspectorProperty.PropertyFactoryTextoL("entidade.ao", "setAtributosOcultos", getAtributosOcultos()));

        return res;
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {

        ArrayList<InspectorProperty> res = GP;
        
        res.add(InspectorProperty.PropertyFactorySeparador("entidade.relacionar"));
        List<String> ents = getMaster().getListaDeItens().stream().filter(o -> o instanceof PreEntidade).map(e -> ((PreEntidade) e).getTexto()).collect(Collectors.toList());
        ents.add(0, Editor.fromConfiguracao.getValor("Inspector.obj.selecione"));
        res.add(InspectorProperty.PropertyFactoryMenu("entidade.relacione", "Relacione", 0, ents));

        
        res.add(InspectorProperty.PropertyFactorySeparador("entidade.orgattr"));
        res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "entidade.orgattr").setTag(CONST_DO_ORGATTR));
        res.add(InspectorProperty.PropertyFactorySeparador(COMM_EDT_ATTR));
        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_EDT_ATTR).setTag(EDITOR_ATTR));

        super.CompleteGenerateProperty(GP);
        return GP;
    }

    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorString(doc, "AtributosOcultos", getAtributosOcultos()));
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        String tmp = util.XMLGenerate.getValorStringFrom(me, "AtributosOcultos");
        if (tmp != null) {
            setAtributosOcultos(tmp);
        }
        return true;
    }

    public void Relacione(int idx) {
        idx--;
        if (idx < 0) return;
        List<PreEntidade> ents = getMaster().getListaDeItens().stream().filter(o -> o instanceof PreEntidade).map(e -> (PreEntidade) e).collect(Collectors.toList());
        if (idx > ents.size() -1) return;
        PreEntidade dest = ents.get(idx);
        if (dest == this) {
            getMaster().ExternalRealiseComando(Controler.Comandos.cmdAutoRelacionamento, new Point(this.getLeftWidth() -1, this.getTop() + (this.getHeight()/2)));
        } else {
            ((DiagramaConceitual)getMaster()).Relacione(this, dest);
        }
    }
    
    public boolean mudouAtributos = false;

    protected final String COMM_EDT_ATTR = "edt_attr";
    
    private final int EDITOR_ATTR = 250317;
    
    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == CONST_DO_ORGATTR) {
            List<PontoDeLinha> pts = getListaDePontosLigados().stream()
                    .filter(p -> p.getDono().getOutraPonta(p).getEm() instanceof Atributo)
                    .collect(Collectors.toList());
            Comparator<PontoDeLinha> comparator = (p1, p2) -> Integer.compare(p1.getLeft(), p2.getLeft());
            Collections.sort(pts, comparator.reversed());

            lastL1 = 0;
            lastL3 = 0;

            for (PontoDeLinha p : pts) {
                Atributo att = (Atributo) p.getDono().getOutraPonta(p).getEm();
                if (Alinhe(p, att)) {
                    mudouAtributos = true;
                }

                if (att.OrganizeAtributos()) {
                    mudouAtributos = true;
                }
            }
        }
        if (Tag == EDITOR_ATTR) {
            ((DiagramaConceitual) getMaster()).LancarEditorDeAtributos();
        }
    }

    transient int lastL1 = 0;
    transient int lastL3 = 0;

    private boolean Alinhe(PontoDeLinha p, Atributo atr) {
        if (getMaster().getItensSelecionados().indexOf(atr) > -1) {
            return false;
        }
        int L = p.getLado();
        int tmp = atr.getHeight() / 2;
        int disH = 20;
        int disW = 40;
        int l = 0, t = 0, X = 0, Y = 0;
        switch (L) {
            case 0:
                atr.setDirecaoLigacao(FormaNaoRetangularBase.Direcao.Right);
                X = this.getLeft() - disW - atr.getWidth();
                if (atr.getLeft() != X) {
                    l = atr.getLeft() - X;
                }
                Y = p.getCentro().y - tmp;
                if (atr.getTop() != Y) {
                    t = atr.getTop() - Y;
                }
                break;
            case 2:
                atr.setDirecaoLigacao(FormaNaoRetangularBase.Direcao.Left);
                X = this.getLeftWidth() + disW;
                if (atr.getLeft() != X) {
                    l = atr.getLeft() - X;
                }
                Y = p.getCentro().y - tmp;
                if (atr.getTop() != Y) {
                    t = atr.getTop() - Y;
                }
                break;

            case 1:
                atr.setDirecaoLigacao(FormaNaoRetangularBase.Direcao.Left);
                X = p.getCentro().x + SuperLinha.distancia;
                if (atr.getLeft() != X) {
                    l = atr.getLeft() - X;
                }
                Y = this.getTop() - disH - atr.getHeight();
                if (atr.getTop() != Y) {
                    t = atr.getTop() - Y;
                }
                if (lastL1 == 0) {
                    lastL1 = atr.getHeight() + 5;
                } else {
                    t += lastL1;
                    lastL1 += atr.getHeight() + 5;
                }
                break;
            case 3:
                atr.setDirecaoLigacao(FormaNaoRetangularBase.Direcao.Left);
                X = p.getCentro().x + SuperLinha.distancia;
                if (atr.getLeft() != X) {
                    l = atr.getLeft() - X;
                }
                Y = this.getTopHeight() + disH;
                if (atr.getTop() != Y) {
                    t = atr.getTop() - Y;
                }
                if (lastL3 == 0) {
                    lastL3 = atr.getHeight() + 5;
                } else {
                    t -= lastL3;
                    lastL3 += atr.getHeight() + 5;
                }
                break;

        }
        Rectangle r = new Rectangle(l, t, 0, 0);
        atr.ReciveFormaResize(r);
        atr.Reenquadre();
        return (l != 0 || t != 0);
    }

    @Override
    public void OrganizeDiagrama(boolean movA, boolean movB) {
        super.OrganizeDiagrama(movA, movB);
        DoAnyThing(CONST_DO_ORGATTR);
    }

    /**
     * Atributos detectados durante a concepção do diagrama conceitual, porém, irrelevantes para o modelo
     */
    private String atributosOcultos = "";

    public String getAtributosOcultos() {
        return atributosOcultos;
    }

    public void setAtributosOcultos(String atributosOcultos) {
        if (this.atributosOcultos.equals(atributosOcultos)) {
            return;
        }
        this.atributosOcultos = atributosOcultos;
    }

    public final int CODE_EDT_ATR = 4;
    
    @Override
    public void runAncorasCode(int cod) {
        super.runAncorasCode(cod);
        if (cod == Ancorador.CODE_ORG_AT) {
            DoAnyThing(CONST_DO_ORGATTR);
        }
        if (cod == CODE_EDT_ATR) {
            DoAnyThing(EDITOR_ATTR);
        }
    }

    @Override
    public String WhatDrawOnAcorador(Integer c) {
        if (c == Ancorador.CODE_ORG_AT) return "diagrama.ancordor.2.img";
        if (c == CODE_EDT_ATR) return "diagrama.ancordor.4.img";
        return super.WhatDrawOnAcorador(c);
    }
    
    public List<Atributo> getAtributos() {
        return getListaDePontosLigados().stream()
                .filter(p -> p.getDono().getOutraPonta(p).getEm() instanceof Atributo)
                .map(p -> (Atributo) (p.getDono().getOutraPonta(p).getEm()))
                .collect(Collectors.toList());
    }
    
}
