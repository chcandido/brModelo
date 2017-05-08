/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.livre;

import controlador.Controler;
import controlador.Controler.Comandos;
import controlador.Diagrama;
import controlador.Editor;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Desenhador;
import desenho.formas.Forma;
import desenho.formas.Legenda;
import desenho.preAnyDiagrama.PreLigacaoSeta;
import diagramas.atividade.TextoAtividade;
import diagramas.conceitual.Texto;
import java.awt.Point;
import java.awt.Rectangle;
import org.w3c.dom.Document;

/**
 *
 * @author SAA
 */
public class DiagramaLivre extends Diagrama {

    private static final long serialVersionUID = 1440265007689102490L;

    public DiagramaLivre(Editor omaster) {
        super(omaster);
        setTipo(Diagrama.TipoDeDiagrama.tpLivre);

        meusComandos.add(Controler.Comandos.cmdLivreRetangulo.name());
        meusComandos.add(Controler.Comandos.cmdLivreRetanguloArr.name());
        meusComandos.add(Controler.Comandos.cmdLivreComentario.name());
        meusComandos.add(Controler.Comandos.cmdLivreDocumento.name());
        meusComandos.add(Controler.Comandos.cmdLivreNota.name());

        meusComandos.add(Controler.Comandos.cmdLivreTriangulo.name());
        meusComandos.add(Controler.Comandos.cmdLivreCirculo.name());
        meusComandos.add(Controler.Comandos.cmdLivreJuncao.name());

        meusComandos.add(Controler.Comandos.cmdLivreLosango.name());
        meusComandos.add(Controler.Comandos.cmdLivreSuperTexto.name());
        meusComandos.add(Controler.Comandos.cmdLivreLigacao.name());
        meusComandos.add(Controler.Comandos.cmdLivreLigacaoSimples.name());
        meusComandos.add(Controler.Comandos.cmdLivreVariosDocumentos.name());
        meusComandos.add(Controler.Comandos.cmdLivreDrawer.name());
        
    }

    private final Class[] classesDoDiagrama = new Class[]{
        LivreRetangulo.class, LivreRetanguloArr.class, LivreComentario.class, LivreDocumento.class, LivreVariosDocumentos.class, LivreNota.class,
        LivreTriangulo.class, LivreCirculo.class, LivreLosango.class, LivreSuperTexto.class,
        LivreLigacao.class, LivreLigacaoSimples.class, LivreJuncao.class,
        LivreTextoApenso.class, TextoAtividade.class, LivreDrawer.class,
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
            case cmdLivreRetangulo:
                LivreRetangulo lr = new LivreRetangulo(this, LivreRetangulo.class.getSimpleName());
                lr.SetBounds(posi.x, posi.y, 120, 58);
                lr.Reenquadre();
                resu = lr;
                break;

            case cmdLivreRetanguloArr:
                LivreRetanguloArr lra = new LivreRetanguloArr(this, LivreRetanguloArr.class.getSimpleName());
                lra.SetBounds(posi.x, posi.y, 120, 58);
                lra.Reenquadre();
                resu = lra;
                break;

            case cmdLivreComentario:
                LivreComentario lco = new LivreComentario(this, LivreComentario.class.getSimpleName());
                lco.SetBounds(posi.x, posi.y, 120, 58);
                lco.Reenquadre();
                resu = lco;
                break;

            case cmdLivreDocumento:
                LivreDocumento ld = new LivreDocumento(this, LivreDocumento.class.getSimpleName());
                ld.SetBounds(posi.x, posi.y, 120, 58);
                ld.Reenquadre();
                resu = ld;
                break;

            case cmdLivreVariosDocumentos:
                LivreVariosDocumentos lds = new LivreVariosDocumentos(this, LivreVariosDocumentos.class.getSimpleName());
                lds.SetBounds(posi.x, posi.y, 120, 58);
                lds.Reenquadre();
                resu = lds;
                break;

            case cmdLivreNota:
                LivreNota ln = new LivreNota(this, LivreNota.class.getSimpleName());
                ln.SetBounds(posi.x, posi.y, 120, 80);
                ln.Reenquadre();
                resu = ln;
                break;
            case cmdLivreCirculo:
                LivreCirculo llc = new LivreCirculo(this, LivreCirculo.class.getSimpleName());
                llc.SetBounds(posi.x, posi.y, 60, 60);
                llc.Reenquadre();
                resu = llc;
                break;
            case cmdLivreJuncao:
                LivreJuncao llj = new LivreJuncao(this, LivreJuncao.class.getSimpleName());
                llj.SetBounds(posi.x, posi.y, 30, 30);
                llj.Reenquadre();
                resu = llj;
                break;

            case cmdLivreTriangulo:
                LivreTriangulo llt = new LivreTriangulo(this, LivreTriangulo.class.getSimpleName());
                llt.SetBounds(posi.x, posi.y, 30, 30);
                llt.Reenquadre();
                resu = llt;
                break;

            case cmdLivreLosango:
                LivreLosango ll = new LivreLosango(this, LivreLosango.class.getSimpleName());
                ll.SetBounds(posi.x, posi.y, 100, 40);
                ll.Reenquadre();
                resu = ll;
                break;

            case cmdLivreSuperTexto:
                LivreSuperTexto lst = new LivreSuperTexto(this, LivreSuperTexto.class.getSimpleName());
                lst.SetBounds(posi.x, posi.y, 120, 20);
                lst.Reenquadre();
                resu = lst;
                break;

            case cmdLivreDrawer:
                LivreDrawer ldw = new LivreDrawer(this, LivreDrawer.class.getSimpleName());
                ldw.SetBounds(posi.x, posi.y, 250, 150);
                ldw.Reenquadre();
                resu = ldw;
                break;

            case cmdLivreLigacao:
            case cmdLivreLigacaoSimples:
                if (cliq1 == null) {
                    res = CaptureFromPoint(posi);
                    obj1 = null;
                    if (res instanceof FormaElementar) {
                        obj1 = (FormaElementar) res;
                    }
                    cliq1 = new Diagrama.clickForma(obj1, posi);
                    return null;
                }
                if (cliq2 == null) {
                    obj2 = null;
                    res = CaptureFromPoint(posi);
                    if (res instanceof FormaElementar) {
                        obj2 = (FormaElementar) res;
                    }
                    cliq2 = new Diagrama.clickForma(obj2, posi);
                }
                PreLigacaoSeta linha = (com == Comandos.cmdLivreLigacao ? new LivreLigacao(this) : new LivreLigacaoSimples(this));
                resu = linha;
                pt1 = cliq1.getPonto();
                pt2 = cliq2.getPonto();

                if (cliq1.getForma() instanceof LivreTriangulo || cliq2.getForma() instanceof LivreTriangulo) {
                    linha.setInteligente(false);
                }

                if (CaptureFromPoint(pt1) instanceof LivreLigacao) {
                    Emende((LivreLigacao) CaptureFromPoint(pt1), pt1);
                    linha.setInteligente(false);
                }

                if (CaptureFromPoint(pt1) instanceof LivreLigacaoSimples) {
                    Emende((LivreLigacaoSimples) CaptureFromPoint(pt1), pt1);
                    linha.setInteligente(false);
                }

                if (CaptureFromPoint(pt2) instanceof LivreLigacao) {
                    Emende((LivreLigacao) CaptureFromPoint(pt2), pt2);
                    linha.setInteligente(false);
                }

                if (CaptureFromPoint(pt2) instanceof LivreLigacaoSimples) {
                    Emende((LivreLigacaoSimples) CaptureFromPoint(pt2), pt2);
                    linha.setInteligente(false);
                }

                linha.Inicie(new Rectangle(pt2.x, pt2.y, pt1.x - pt2.x, pt1.y - pt2.y)); // = 4 pontos
                break;

        }
        if (resu == null) {
            resu = super.RealiseComando(posi);
            if (resu instanceof Texto) {
                ((Texto) resu).setSimplesDezenho(false);
            } else if (resu instanceof Desenhador) {
                ((Desenhador) resu).setSimplesDezenho(false);
            }
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

    private void Emende(LivreLigacao lig, Point posi) {
        LivreJuncao jun = new LivreJuncao(this, LivreJuncao.class.getSimpleName());
        jun.SetBounds(posi.x - 15, posi.y - 15, 30, 30);
        jun.Reenquadre();

        Point fim = lig.getPontaA().getCentro();
        Forma f = lig.getPontaA().getEm();
        lig.SetInteligente(false);
        lig.getPontaA().LigarA(jun);
        lig.setTemSetaPontaB(false);

        lig = new LivreLigacao(this);
        lig.setInteligente(false);
        Point F = new Point(posi.x - 2, posi.y);
        lig.Inicie(new Rectangle(F.x, F.y, fim.x - F.x, fim.y - F.y));
        lig.getPontaA().LigarA(f);
        lig.setTemSetaPontaB(false);
    }

    private void Emende(LivreLigacaoSimples lig, Point posi) {
        LivreJuncao jun = new LivreJuncao(this, LivreJuncao.class.getSimpleName());
        jun.SetBounds(posi.x - 15, posi.y - 15, 30, 30);
        jun.Reenquadre();

        Point fim = lig.getPontaA().getCentro();
        Forma f = lig.getPontaA().getEm();
        lig.SetInteligente(false);
        lig.getPontaA().LigarA(jun);
        lig.setTemSetaPontaB(false);

        lig = new LivreLigacaoSimples(this);
        lig.setInteligente(false);
        Point F = new Point(posi.x - 2, posi.y);
        lig.Inicie(new Rectangle(F.x, F.y, fim.x - F.x, fim.y - F.y));
        lig.getPontaA().LigarA(f);
        lig.setTemSetaPontaB(false);
    }

    @Override
    public boolean LoadFromXML(Document doc, boolean colando) {
        boolean res = super.LoadFromXML(doc, colando);
        getListaDeItens().stream().filter((resu) -> (resu instanceof Texto)).map(te -> (Texto) te).forEach((Texto te) -> {
            te.setSimplesDezenho(false);
        });

        getListaDeItens().stream().filter((resu) -> (resu instanceof Desenhador)).map(de -> (Desenhador) de).forEach((de) -> {
            de.setSimplesDezenho(false);
        });
        return res;
    }
    
    
}
