/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package desenho.preAnyDiagrama;

import controlador.Diagrama;
import desenho.formas.Forma;
import desenho.formas.FormaTriangular;
import desenho.linhas.Linha;
import desenho.linhas.PontoDeLinha;
import desenho.linhas.SuperLinha;
import diagramas.conceitual.Entidade;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import util.Constantes;

/**
 *
 * @author ccandido
 */
public class PreEspecializacao extends FormaTriangular {

    private static final long serialVersionUID = 4701897775925018386L;

    public PreEspecializacao(Diagrama modelo) {
        super(modelo);
    }

    public PreEspecializacao(Diagrama modelo, String texto) {
        super(modelo, texto);
    }

    protected PontoDeLinha[] ocupados = new PontoDeLinha[]{null, null, null};

    @Override
    public void menosLigacao(PontoDeLinha aThis) {
        super.menosLigacao(aThis);
        RemovaOcupados(aThis);
    }

    public PreEntidade LigadaAoPontoPrincipal() {
        PontoDeLinha pri = ocupados[0];
        if (pri == null) {
            return null;
        }
        Forma op = pri.getDono().getOutraPonta(this);
        if (op != null) {
            return (PreEntidade) op;
        }
        return null;
    }

    @Override
    protected void Posicione3Pontos(PontoDeLinha ponto) {
        super.Posicione3Pontos(ponto);
        Point[] pts = getPontosDoTriangulo();
        Point pt1 = pts[0], pt2 = pts[1], pt3 = pts[2], pMeio = pts[3];
        if (ponto.getCentro().equals(pt1)) {
            verifiqueOcupacao(ponto, pt1, 0, pMeio);
        } else if (ponto.getCentro().equals(pt2)) {
            verifiqueOcupacao(ponto, pt2, 1, pMeio);
        } else if (ponto.getCentro().equals(pt3)) {
            verifiqueOcupacao(ponto, pt3, 2, pMeio);
        }
    }

    private void verifiqueOcupacao(PontoDeLinha ponto, Point p, int posi, Point pMeio) {
        RemovaOcupados(ponto);
        if (ocupados[posi] != null) {
            ponto.setCentro(pMeio);
        } else {
            ocupados[posi] = ponto;
            ponto.setCentro(p);
        }
    }

    private void RemovaOcupados(PontoDeLinha pt) {
        for (int i = 0; i < ocupados.length; i++) {
            if (ocupados[i] == pt) {
                ocupados[i] = null;
                return;
            }
        }
    }

    @Override
    public void setDirecaoTriangulo(Direcao direcaoTriangulo) {
        Point[] pts = getPontosDoTriangulo();

        //evita outras formas de direção.
        if (direcaoTriangulo.ordinal() > 3) {
            direcaoTriangulo = Direcao.Up;
        }
        setDirecaoNaoNotifique(direcaoTriangulo);
        DestruaRegiao();

        Point[] pts2 = getPontosDoTriangulo();

        ArrayList<PontoDeLinha> btns = getListaDePontosLigados();
        for (PontoDeLinha pdl : btns) {
            Point atual = pdl.getCentro();
            int i = 0;
            for (Point p : pts) {
                if (atual.equals(p)) {
                    pdl.setCentro(pts2[i]);
                    break;
                }
                i++;
            }
        }
        SendNotificacao(Constantes.Operacao.opReposicione);
        InvalidateArea();
    }

    @Override
    public boolean CanLiga(Forma forma, Linha lin) {
        if (forma == null) {
            ((SuperLinha) lin).setInteligente(false);
            return true;
        }
        if (super.CanLiga(forma, lin)) {
            if (!(forma instanceof Entidade)) {
                return false;
            }
            if (!FinderLinked(forma, this)) {
            //    return false;
            //} else {
                //getMaster().CheckLigConsistencia(this, forma, lin);//nunca usado! 20/09/2014
                return true;
            }
        }
        return false;
    }

    protected boolean FinderLinked(Forma quem, Forma origem) {
//        ArrayList<Forma> outras = getListaDeFormasLigadas(origem);
//        for (Forma f : outras) {
//            if (f == quem) {
//                return true;
//            }
//            ArrayList<Forma> esp = f.getListaDeFormasLigadas(PreEspecializacao.class);
//            for (Forma f2 : esp) {
//                if (f2 == this) {
//                    continue;
//                }
//                PreEspecializacao pree = (PreEspecializacao) f2;
//                if (pree.FinderLinked(quem, f)) {
//                    return true;
//                }
//            }
//        }
        return false;
    }
    
    private boolean parcial = false;
    protected boolean paintParcial = false;

    public boolean isParcial() {
        return parcial;
    }

    public void setParcial(boolean parcial) {
        this.parcial = parcial;
        InvalidateArea();
    }
    
    /**
     * Serve apenas para alterar a direção pelo Inspector
     *
     * @param di
     */
    public void setDirecaoFromInspector(int di) {
        Direcao dr = Direcao.values()[di];
        setDirecaoTriangulo(dr);
    }

    /**
     * Serve apenas para retornar a direção para o Inspector
     *
     * @return
     */
    public int getDirecaoForInspector() {
        return getDirecaoTriangulo().ordinal();
    }

    /**
     * aqui para atender também a União
     *
     * @param doc
     * @param me
     */
    @Override
    protected void ToXmlValores(Document doc, Element me) {
        super.ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorInteger(doc, "Direcao", getDirecaoForInspector()));

        me.appendChild(util.XMLGenerate.ValorRefFormElementar(doc, "Ocupado01", ocupados[0] == null ? null : ocupados[0].getEm()));
        me.appendChild(util.XMLGenerate.ValorRefFormElementar(doc, "Ocupado02", ocupados[1] == null ? null : ocupados[1].getEm()));
        me.appendChild(util.XMLGenerate.ValorRefFormElementar(doc, "Ocupado03", ocupados[2] == null ? null : ocupados[2].getEm()));
        
        me.appendChild(util.XMLGenerate.ValorBoolean(doc, "Parcial", isParcial()));
    }

    @Override
    public boolean LoadFromXML(Element me, boolean colando) {
        if (!super.LoadFromXML(me, colando)) {
            return false;
        }
        
        setParcial(util.XMLGenerate.getValorBooleanFrom(me, "Parcial"));
        
        int l = util.XMLGenerate.getValorIntegerFrom(me, "Direcao");
        if (l != -1) {
            setDirecaoFromInspector(l);
        }
        return true;
    }

    @Override
    public void DoPaint(Graphics2D g) {
        super.DoPaint(g);
        int h = getHeight() / 8;
        int w = getWidth() / 8;
        
        Font fbkp = getFont();
        g.setFont(getFont());
        
        int fh = g.getFontMetrics().getHeight() / 2;
        int fw = g.getFontMetrics().stringWidth("p");
        int x =0, y = 0;
        Rectangle r = getBounds();
        Shape bkp = g.getClip();
        g.setClip(Regiao);
        switch (getDirecao()) {
            case Up:
                g.fillRect(r.x, r.y, r.width, h);
                x =  r.x + r.width -fw;
                y =  r.y + r.height/2 + fh/2;
                break;
            case Right:
                g.fillRect(getLeftWidth() - w, r.y, w, r.height);
                x =  r.x + r.width/2 - fw/2;
                y =  r.y + fh;
                break;
            case Down:
                g.fillRect(r.x, getTopHeight() - h, r.width, h);
                x =  r.x + r.width - fw;
                y =  r.y + r.height/2 + fh/2;
                break;
            case Left:
                g.fillRect(r.x, r.y, w, r.height);
                x =  r.x + r.width/2 - fw/2;
                y =  r.y + fh;
                break;
        }
        g.setClip(bkp);
        PaintPDeParcial(g, x, y);
        g.setFont(fbkp);
    }

    protected void PaintPDeParcial(Graphics2D g, int x, int y) {
        if (paintParcial && isParcial()) {
            g.drawString("p", x, y);
        }
    }
}
