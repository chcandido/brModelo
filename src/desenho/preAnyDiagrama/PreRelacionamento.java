/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preAnyDiagrama;

import controlador.Diagrama;
import controlador.inspector.InspectorProperty;
import desenho.Ancorador;
import desenho.formas.Forma;
import desenho.formas.FormaLosangular;
import desenho.formas.FormaNaoRetangularBase;
import desenho.linhas.Linha;
import desenho.linhas.PontoDeLinha;
import desenho.linhas.SuperLinha;
import diagramas.conceitual.Atributo;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ccandido
 */
public class PreRelacionamento extends FormaLosangular {

    private static final long serialVersionUID = -361853607881278277L;

    public PreRelacionamento(Diagrama modelo) {
        super(modelo);        
    }

    public PreRelacionamento(Diagrama modelo, String texto) {
        super(modelo, texto);        
    }

    @Override
    public ArrayList<Integer> getAncorasCode() {
        ArrayList<Integer> res = super.getAncorasCode();
        Integer[] ancorasCode = new Integer[]{Ancorador.CODE_ORG_AT};
        res.addAll(Arrays.asList(ancorasCode));
        return res;
    }

    public boolean isAutoRelacionamento() {
        return AutoRelacionamento(getListaDeFormasLigadasNaoExclusiva(PreEntidade.class));
    }

    private boolean AutoRelacionamento(ArrayList<Forma> ligados) {
        return ((ligados.size() == 2) && (ligados.get(0) == ligados.get(1)));// instanceof PreEntidade) && (ligados.get(1) instanceof PreEntidade));
    }

    @Override
    public boolean CanLiga(Forma forma, Linha lin) {
        if (forma == null) {
            return true;
        }
        if (super.CanLiga(forma, lin)) {
            if (forma instanceof PreAtributo) {
                return forma.CanLiga(this, lin);
            }
            if (forma instanceof PreEntidade) {
                ArrayList<Forma> lst = getListaDeFormasLigadasNaoExclusiva(PreEntidade.class);
                if (AutoRelacionamento(lst) || getPrincipal() == forma) {
                    return false;
                }
                if (isSubComponente()) { //entidade ass.
                    if (lst.indexOf(forma) > -1) {
                        return false; //NÃO PODE SER AUTO-REL.
                    }
                } else {
                    if (lst.indexOf(forma) > -1 && lst.size() > 1) {
                        return false; //já está ligado e possui outra ligação, por isso não pode ser alto-rel.
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<InspectorProperty> CompleteGenerateProperty(ArrayList<InspectorProperty> GP) {

        if (!isSubComponente()) {
            ArrayList<InspectorProperty> res = GP;
            res.add(InspectorProperty.PropertyFactorySeparador("entidade.orgattr"));
            res.add(InspectorProperty.PropertyFactoryCommand(nomeComandos.cmdDoAnyThing.name(), "entidade.orgattr").setTag(CONST_DO_ORGATTR));
            super.CompleteGenerateProperty(GP);
        }
        return GP;
    }
    public final int CONST_DO_ORGATTR = 123;

    public boolean mudouAtributos = false;

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
    
    public List<Atributo> getAtributos() {
        return getListaDePontosLigados().stream()
                .filter(p -> p.getDono().getOutraPonta(p).getEm() instanceof Atributo)
                .map(p -> (Atributo) (p.getDono().getOutraPonta(p).getEm()))
                .collect(Collectors.toList());
    }
}
