/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.fluxo;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Desenhador;
import desenho.formas.Forma;
import desenho.formas.Legenda;
import desenho.linhas.Linha;
import desenho.linhas.PontoDeLinha;
import desenho.preAnyDiagrama.PreLigacaoSeta;
import diagramas.conceitual.Texto;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author ccandido
 */
public class DiagramaFluxo extends Diagrama {

    private static final long serialVersionUID = 8966014390922983766L;

    public DiagramaFluxo(Editor omaster) {
        super(omaster);
        setTipo(TipoDeDiagrama.tpFluxo);
        
        meusComandos.add(Controler.Comandos.cmdFluxIniFim.name());
        meusComandos.add(Controler.Comandos.cmdFluxProcesso.name());
        meusComandos.add(Controler.Comandos.cmdFluxConector.name());
        meusComandos.add(Controler.Comandos.cmdFluxDecisao.name());
        meusComandos.add(Controler.Comandos.cmdFluxDocumento.name());
        meusComandos.add(Controler.Comandos.cmdFluxLigacao.name());
        meusComandos.add(Controler.Comandos.cmdFluxSeta.name());
        meusComandos.add(Controler.Comandos.cmdFluxVDocumentos.name());
        meusComandos.add(Controler.Comandos.cmdFluxNota.name());
    }
    
    private final Class[] classesDoDiagrama = new Class[]{
        FluxIniFim.class, FluxProcesso.class, FluxConector.class, 
        FluxDecisao.class, FluxDocumento.class, FluxVDocumentos.class, FluxNota.class, FluxLigacao.class, 
        FluxSeta.class, FluxTexto.class,
        Texto.class, Desenhador.class, Legenda.class
    };

    @Override
    public Class[] getCassesDoDiagrama() {
        return classesDoDiagrama;
    }

    @Override
    protected FormaElementar RealiseComando(Point posi) {
        ClearSelect(false);
        FormaElementar resu = null;
        Point pt1, pt2;
        FormaElementar obj1, obj2;
        Elementar res;
        Controler.Comandos com = getComando();
        switch (com) {
            case cmdFluxIniFim:
                {
                FluxIniFim fif = new FluxIniFim(this, FluxIniFim.class.getSimpleName());
                fif.SetBounds(posi.x, posi.y, 80, 30);
                fif.Reenquadre();
                resu = fif;
                }
                break;
            case cmdFluxSeta:
            case cmdFluxLigacao:
                if (cliq1 == null) {
                    res = CaptureFromPoint(posi);
                    obj1 = null;
                    if (res instanceof FormaElementar) {
                        obj1 = (FormaElementar) res;
                    }
                    cliq1 = new clickForma(obj1, posi);
                    return null;
                }
                if (cliq2 == null) {
                    obj2 = null;
                    res = CaptureFromPoint(posi);
                    if (res instanceof FormaElementar) {
                        obj2 = (FormaElementar) res;
                    }
                    cliq2 = new clickForma(obj2, posi);
                }
                PreLigacaoSeta linha;
                if (com == Controler.Comandos.cmdFluxSeta) {
                    linha = new FluxSeta(this);
                } else {
                    linha = new FluxLigacao(this);
                }
                resu = linha;
                pt1 = cliq1.getPonto();
                pt2 = cliq2.getPonto();

                linha.Inicie(new Rectangle(pt2.x, pt2.y, pt1.x - pt2.x, pt1.y - pt2.y)); // = 4 pontos
                if (com == Controler.Comandos.cmdFluxSeta) {
                    Forma pa = linha.getFormaPontaA();
                    Forma pb = linha.getFormaPontaB();
                    prepareLinha(pa, linha);
                    prepareLinha(pb, linha);
                }                
                break;
            case cmdFluxProcesso:
                FluxProcesso fp = new FluxProcesso(this, FluxProcesso.class.getSimpleName());
                fp.SetBounds(posi.x, posi.y, 120, 58);
                fp.Reenquadre();
                resu = fp;
                break;
            case cmdFluxDocumento:
                FluxDocumento fdo = new FluxDocumento(this, FluxDocumento.class.getSimpleName());
                fdo.SetBounds(posi.x, posi.y, 120, 58);
                fdo.Reenquadre();
                resu = fdo;
                break;
            case cmdFluxVDocumentos:
                FluxVDocumentos fdos = new FluxVDocumentos(this, FluxVDocumentos.class.getSimpleName());
                fdos.SetBounds(posi.x, posi.y, 120, 58);
                fdos.Reenquadre();
                resu = fdos;
                break;
            case cmdFluxNota:
                FluxNota fdosn = new FluxNota(this, FluxNota.class.getSimpleName());
                fdosn.SetBounds(posi.x, posi.y, 120, 80);
                fdosn.Reenquadre();
                resu = fdosn;
                break;
            case cmdFluxDecisao:
                FluxDecisao fd = new FluxDecisao(this, FluxDecisao.class.getSimpleName());
                fd.SetBounds(posi.x, posi.y, 100, 40);
                fd.Reenquadre();
                resu = fd;
                break;
            case cmdFluxConector:
                FluxConector fc = new FluxConector(this, FluxConector.class.getSimpleName());
                fc.SetBounds(posi.x, posi.y, 30, 30);
                fc.Reenquadre();
                resu = fc;
                break;
        }
        if (resu == null) {
            resu = super.RealiseComando(posi);
        } else {
            cliq1 = null;
            cliq2 = null;
            if (!master.isControlDown()) {
                setComando(null);
            } else {
                setComando(com);
            }
            resu.BringToFront();
        }
        return resu;
    }

    private void prepareLinha(Forma formaPonta, PreLigacaoSeta linha) {
        if (formaPonta instanceof FluxDecisao) {
            ArrayList<PontoDeLinha> lst = formaPonta.getListaDePontosLigados();
            //retira da lista as ligações que não são seta.
            int i = 0;
            while (i < lst.size()) {
                if (!(lst.get(i).getDono() instanceof FluxSeta)) {
                    lst.remove(i);
                } else {
                    i++;
                }
            }
            if (lst.size() == 2) {
                Linha l;
                if (lst.get(0).getDono() == linha) {
                    l = lst.get(1).getDono();
                } else {
                    l = lst.get(0).getDono();
                }
                ((FluxSeta)linha).setPositivo(!((FluxSeta)l).isPositivo());
            }
            ((FluxSeta)linha).getTexto().setSize(new Dimension(40, 20));
        }
    }
     
}
