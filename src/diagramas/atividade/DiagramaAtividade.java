/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.atividade;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Desenhador;
import desenho.formas.Legenda;
import desenho.linhas.SuperLinha;
import diagramas.conceitual.Texto;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author ccandido
 */
public class DiagramaAtividade extends Diagrama {

    private static final long serialVersionUID = -4112112303506516169L;

    public DiagramaAtividade(Editor omaster) {
        super(omaster);
        setTipo(TipoDeDiagrama.tpAtividade);

        meusComandos.add(Controler.Comandos.cmdInicioAtividade.name());
        meusComandos.add(Controler.Comandos.cmdEstadoAtividade.name());
        meusComandos.add(Controler.Comandos.cmdDecisaoAtividade.name());
        meusComandos.add(Controler.Comandos.cmdSetaAtividade.name());
        meusComandos.add(Controler.Comandos.cmdLigacaoAtividade.name());
        meusComandos.add(Controler.Comandos.cmdRaiaAtividade.name());
        meusComandos.add(Controler.Comandos.cmdForkJoinAtividade.name());
        meusComandos.add(Controler.Comandos.cmdFimAtividade.name());
    }

    private final Class[] classesDoDiagrama = new Class[]{
        InicioAtividade.class, EstadoAtividade.class, DecisaoAtividade.class, SetaAtividade.class,
        LigacaoAtividade.class, ForkJoinAtividade.class,
        RaiaAtividade.class, FimAtividade.class, TextoAtividade.class,
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
        //Point tmpPt;
        Point pt1, pt2;
        FormaElementar obj1, obj2;
        Elementar res;
        Controler.Comandos com = getComando();

        switch (com) {
            case cmdInicioAtividade:
                InicioAtividade ia = new diagramas.atividade.InicioAtividade(this, diagramas.atividade.InicioAtividade.class.getSimpleName());
                ia.SetBounds(posi.x, posi.y, 20, 20);
                ia.Reenquadre();
                resu = ia;
                break;
            case cmdFimAtividade:
                FimAtividade fa = new diagramas.atividade.FimAtividade(this, diagramas.atividade.FimAtividade.class.getSimpleName());
                fa.SetBounds(posi.x, posi.y, 20, 20);
                fa.Reenquadre();
                resu = fa;
                break;
            case cmdEstadoAtividade:
                EstadoAtividade ent = new diagramas.atividade.EstadoAtividade(this, diagramas.atividade.EstadoAtividade.class.getSimpleName());
                ent.SetBounds(posi.x, posi.y, 120, 58);
                ent.Reenquadre();
                resu = ent;
                break;
            case cmdForkJoinAtividade:
                ForkJoinAtividade fj = new diagramas.atividade.ForkJoinAtividade(this, diagramas.atividade.ForkJoinAtividade.class.getSimpleName());
                fj.SetBounds(posi.x, posi.y, 60, 10);
                fj.Reenquadre();
                resu = fj;
                break;
            case cmdRaiaAtividade:
                RaiaAtividade ra = new diagramas.atividade.RaiaAtividade(this, diagramas.atividade.RaiaAtividade.class.getSimpleName());
                ra.SetBounds(posi.x, posi.y, 600, 580);
                ra.Reenquadre();
                resu = ra;
                break;
            case cmdDecisaoAtividade:
                DecisaoAtividade da = new diagramas.atividade.DecisaoAtividade(this, diagramas.atividade.DecisaoAtividade.class.getSimpleName());
                da.SetBounds(posi.x, posi.y, 100, 40);
                da.Reenquadre();
                resu = da;
                break;

            case cmdSetaAtividade:
            case cmdLigacaoAtividade:
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
                SuperLinha linha;
                if (com == Controler.Comandos.cmdSetaAtividade) {
                    linha = new SetaAtividade(this);
                } else {
                    linha = new LigacaoAtividade(this);
                }
                resu = linha;
                pt1 = cliq1.getPonto();
                pt2 = cliq2.getPonto();

                linha.Inicie(new Rectangle(pt2.x, pt2.y, pt1.x - pt2.x, pt1.y - pt2.y)); // = 4 pontos
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
        if (resu != null && (resu.isVisible())) {
            final FormaElementar lbd = resu;
            getListaDeItens().stream().filter(r -> r instanceof RaiaAtividade).map(r -> (RaiaAtividade)r).forEach(raia -> {
                if (raia.isAutoCapture()) {
                    raia.TestAndCapture(lbd);
                }
            }) ;
        }

        return resu;
    }

//    @Override
//    public boolean InfoDiagrama_LoadFromXML(Document doc, boolean colando) {
//
//        HashMap<Element, FormaElementar> link = new HashMap<>();
//
//        try {
//            NodeList nodeLst = doc.getElementsByTagName(Diagrama.nodePrincipal);
//            Node mer = nodeLst.item(0);
//            nodeLst = mer.getChildNodes();
//            if (colando) {
//                ClearSelect(true);
//            }
//            this.isLoadCreate = true;
//            this.isCarregando = true;
//            int maxID = 0;
//            for (int s = 0; s < nodeLst.getLength(); s++) {
//                Node fstNode = nodeLst.item(s);
//                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element fstElmnt = (Element) fstNode;
//                    FormaElementar res = runCriadorFromXml(fstElmnt, colando);
//                    if (res == null) {
//                        ////// juntar todos os objetos alienígenas para mostrá-los na mensagem de erro.
//                        util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD", "Lixo encontrado: " + fstElmnt.getNodeName() + " [" + fstElmnt.getTextContent() + "]");
//                        continue;
//                    }
//                    if (!colando) {
//                        maxID = Math.max(maxID, res.getID());
//                    }
//                    link.put(fstElmnt, res);
//                }
//            }
//            if (!colando) {
//                TotalID = maxID;
//            }
//            this.isLoadCreate = false;
//
//            for (Element el : link.keySet()) {
//                FormaElementar proc = link.get(el);
//                proc.CommitXML(el, link);
//            }
//            this.isCarregando = false;
//
//            if (colando) {
//                ReestrutureSelecao(((Element) mer).getAttribute("FIRST_SEL"), link);
//            }
//            PerformInspector();
//
//        } catch (DOMException | NullPointerException e) {
//            util.BrLogger.Logger("ERROR_DIAGRAMA_LOAD", e.getMessage());
//            this.isLoadCreate = false;
//            this.isCarregando = false;
//            return false;
//        }
//        repaint();
//        return true;
//    }
}
