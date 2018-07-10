/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.conceitual;

import controlador.Controler;
import controlador.Editor;
import controlador.Diagrama;
import controlador.conversor.conversorConceitualParaLogico;
import controlador.editores.EditorDeAtributos;
import controlador.inspector.InspectorProperty;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Desenhador;
import desenho.formas.Forma;
import desenho.formas.FormaNaoRetangularBase.Direcao;
import desenho.formas.Legenda;
import desenho.linhas.SuperLinha;
import desenho.preAnyDiagrama.PreCardinalidade;
import desenho.preAnyDiagrama.PreEntidade;
import desenho.preAnyDiagrama.PreEntidadeAssociativa;
import desenho.preAnyDiagrama.PreEspecializacao;
import desenho.preAnyDiagrama.PreLigacao;
import diagramas.logico.DiagramaLogico;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import principal.Aplicacao;
import util.BoxingJava;

/**
 *
 * @author ccandido
 */
public class DiagramaConceitual extends Diagrama {

    private static final long serialVersionUID = -3835903539927031392L;

    public DiagramaConceitual(Editor omaster) {
        super(omaster);
        setTipo(TipoDeDiagrama.tpConceitual);

        meusComandos.add(Controler.Comandos.cmdEntidade.name());
        meusComandos.add(Controler.Comandos.cmdRelacionamento.name());
        meusComandos.add(Controler.Comandos.cmdAutoRelacionamento.name());
        meusComandos.add(Controler.Comandos.cmdEspecializacao.name());
        meusComandos.add(Controler.Comandos.cmdEspecializacao_Exclusiva.name());
        meusComandos.add(Controler.Comandos.cmdEspecializacao_Dupla.name());
        meusComandos.add(Controler.Comandos.cmdUniao.name());
        meusComandos.add(Controler.Comandos.cmdUniao_Entidades.name());
        meusComandos.add(Controler.Comandos.cmdEntidadeAssociativa.name());
        meusComandos.add(Controler.Comandos.cmdAtributo.name());
        meusComandos.add(Controler.Comandos.cmdAtributo_Multivalorado.name());
        meusComandos.add(Controler.Comandos.cmdLinha.name());
    }

    private final Class[] classesDoDiagrama = new Class[]{
        Entidade.class, Atributo.class, Cardinalidade.class, EntidadeAssociativa.class,
        Especializacao.class, Ligacao.class, Relacionamento.class,
        Texto.class, Uniao.class, Desenhador.class, Legenda.class
    };

    @Override
    public Class[] getCassesDoDiagrama() {
        return classesDoDiagrama;
    }

    protected final String COMM_CONV = "convlogico";
    protected final String COMM_EDT_ATTR = "edt_attr";

    @Override
    public void populeComandos(JMenuItem menu) {
        super.populeComandos(menu);
        menu.removeAll();
        menu.setEnabled(true);
        String tmp = Editor.fromConfiguracao.getValor("Controler.interface.Diagrama.Command.Conceitual.Conv.descricao");
        Diagrama.AcaoDiagrama ac = new Diagrama.AcaoDiagrama(this, tmp, "Controler.interface.Diagrama.Command.Conceitual.Conv.img", tmp, COMM_CONV);
        ac.normal = false;
        JMenuItem mi = new JMenuItem(ac);
        mi.setName(tmp);
        menu.add(mi);

        tmp = Editor.fromConfiguracao.getValor("Controler.interface.Diagrama.Command.Logico.EdtA.descricao");
        ac = new Diagrama.AcaoDiagrama(this, tmp, "Controler.interface.Diagrama.Command.Logico.EdtA.img", tmp, COMM_EDT_ATTR);
        ac.normal = false;
        mi = new JMenuItem(ac);
        mi.setName(tmp);
        menu.add(mi);
    }

    @Override
    public void rodaComando(String comm) {
        if (comm.equals(COMM_CONV)) {
            conversorConceitualParaLogico conv = new conversorConceitualParaLogico();
            getEditor().AddAsAtual(Diagrama.TipoDeDiagrama.tpLogico.name());

            conv.beginConvert(this, (DiagramaLogico) getEditor().diagramaAtual);
        }
        if (comm.equals(COMM_EDT_ATTR)) {
            LancarEditorDeAtributos();
        }
    }

    private final int ATAG = 85;
    private final int EDITOR_ATTR = 250317;

    @Override
    public void EndProperty(ArrayList<InspectorProperty> res) {
        super.EndProperty(res);
        res.add(InspectorProperty.PropertyFactorySeparador(COMM_CONV));
        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_CONV).setTag(ATAG));
        res.add(InspectorProperty.PropertyFactorySeparador(COMM_EDT_ATTR));
        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_EDT_ATTR).setTag(EDITOR_ATTR));
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == ATAG) {
            rodaComando(COMM_CONV);
        }
        if (Tag == EDITOR_ATTR) {
            LancarEditorDeAtributos();
        }
    }

    @Override
    protected FormaElementar RealiseComando(Point posi) {
        ClearSelect(false);
        FormaElementar resu = null;
        //Point tmpPt;
        Point pt1, pt2, pt3, pt4;
        FormaElementar obj1, obj2, obj3;
        Forma obj;
        Elementar res;
        boolean ok = false;
        int x = 0, y = 0, mx = 0;
        Controler.Comandos com = getComando();

        switch (com) {
            case cmdEntidade:
                Entidade ent = new diagramas.conceitual.Entidade(this, "Entidade");
                //Largura da entidade usada em  "case cmdUniao_Entidades"
                ent.SetBounds(posi.x, posi.y, 120, 58);
                ent.Reenquadre();
                resu = ent;
                break;

            // <editor-fold defaultstate="collapsed" desc="Ligação e Relação">
            case cmdLinha:
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

                Ligacao linha = new Ligacao(this);
                resu = linha;
                pt1 = cliq1.getPonto();
                pt2 = cliq2.getPonto();
                obj1 = cliq1.getForma();
                obj2 = cliq2.getForma();

                if (obj1 instanceof PreEspecializacao || obj2 instanceof PreEspecializacao) {
                    linha.setInteligente(false);
                } else {
                    ok = true;
                    if ((obj1 instanceof Entidade || obj1 instanceof EntidadeAssociativa)
                            && (obj2 instanceof Entidade || obj2 instanceof EntidadeAssociativa)) {

                        if (obj1 instanceof EntidadeAssociativa) {
                            EntidadeAssociativa ea = (EntidadeAssociativa) obj1;
                            if (ea.ProcessaComposicao(pt1) != ea) {
                                ok = false;
                            }
                        }
                        if (obj2 instanceof EntidadeAssociativa) {
                            EntidadeAssociativa ea = (EntidadeAssociativa) obj2;
                            if (ea.ProcessaComposicao(pt2) != ea) {
                                ok = false;
                            }
                        }

                        if (ok && (obj1 == obj2)) {
                            setComando(Controler.Comandos.cmdAutoRelacionamento);
                            cliq1 = null;
                            cliq2 = null;
                            return RealiseComando(pt2);
                        }

                        if (ok) {
                            //crio uma relação no centro
                            x = (obj1.getLeft() <= obj2.getLeft())
                                    ? (obj1.getLeftWidth() + obj2.getLeft()) / 2
                                    : (obj2.getLeftWidth() + obj1.getLeft()) / 2;
                            y = (obj1.getTop() <= obj2.getTop())
                                    ? (obj1.getTopHeight() + obj2.getTop()) / 2
                                    : (obj2.getTopHeight() + obj1.getTop()) / 2;

                            Point ptcentral = new Point(x, y);
                            setComando(Controler.Comandos.cmdRelacionamento);
                            FormaElementar resLi = RealiseComando(ptcentral);
                            resLi.setLocation(resLi.getLeft() - (resLi.getWidth() / 2), resLi.getTop() - (resLi.getHeight() / 2));
                            obj1.BringToFront();
                            obj2.BringToFront();
                            resLi.BringToFront();
                            //Fim: crio uma relação no centro

                            //Ao chamar este método terá-se como retorno uma relação:
                            resu = resLi;

                            //Ligo esta nova relação ao pt1 (melhor ponto a ser ligado a ele)
                            pt3 = ((Forma) resLi).getMelhorPontoDeLigacao(pt1);
                            linha.FormasALigar = new Forma[]{(Forma) obj1, (Forma) resLi};
                            linha.SuperInicie(0, pt1, pt3);

                            //Ligo esta nova relação ao pt2!
                            linha = new Ligacao(this);
                            linha.FormasALigar = new Forma[]{(Forma) obj2, (Forma) resLi};
                            pt1 = pt2;
                            pt2 = ((Forma) resLi).getMelhorPontoDeLigacao(pt2);
                            //segue o código abaixo:....
                        }
                    }
                }
                linha.SuperInicie(0, pt2, pt1); // = 4 pontos
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Relacionamento e Auto-Relacionamento">
            case cmdRelacionamento:
                diagramas.conceitual.Relacionamento rel = new Relacionamento(this, "Relacionamento");
                rel.SetBounds(posi.x, posi.y, 150, 50);
                rel.Reenquadre();
                resu = rel;
                break;
            case cmdAutoRelacionamento:
                res = CaptureFromPoint(posi);
                obj1 = null;
                if (res instanceof PreEntidade) {
                    obj1 = (FormaElementar) res;
                }
                if (obj1 == null) {
                    setComando(Controler.Comandos.cmdRelacionamento);
                    return RealiseComando(posi);
                }
                Relacionamento au_rel = new Relacionamento(this, "AutoRelacionamento");
                mx = ((Forma) obj1).retorneProximidade(posi);
                switch (mx) {
                    case 0:
                        x = obj1.getLeft() - 150 - 50;
                        y = ((obj1.getHeight() - 50) / 2) + obj1.getTop();
                        break;
                    case 1:
                        x = ((obj1.getWidth() - 150) / 2) + obj1.getLeft();
                        y = obj1.getTop() - 50 - 50;
                        break;
                    case 2:
                        x = obj1.getLeftWidth() + 50;
                        y = ((obj1.getHeight() - 50) / 2) + obj1.getTop();
                        break;
                    default:
                        x = ((obj1.getWidth() - 150) / 2) + obj1.getLeft();
                        y = obj1.getTopHeight() + 50;
                        break;
                }
                au_rel.SetBounds(x, y, 150, 50);
                Point[] pts = au_rel.getAllSubPoints();
                switch (mx) {
                    case 0:
                        pt1 = pts[6];
                        pt2 = new Point(obj1.getLeft() + 2, pts[6].y);
                        pt3 = new Point(pts[10].x - 5, pts[10].y); //por conta do recuo
                        pt4 = new Point(obj1.getLeft() + 2, pts[10].y);
                        break;
                    case 1:
                        pt1 = new Point(pts[7].x, pts[7].y - 5); //por conta do recuo
                        pt2 = new Point(pts[7].x, obj1.getTop() + 2);
                        pt3 = new Point(pts[11].x, pts[11].y - 5); //por conta do recuo
                        pt4 = new Point(pts[11].x, obj1.getTop() + 2);
                        ;
                        break;
                    case 2:
                        pt1 = pts[4];
                        pt2 = new Point(obj1.getLeftWidth() - 2, pts[4].y);
                        pt3 = new Point(pts[8].x + 5, pts[8].y); //por conta do recuo
                        pt4 = new Point(obj1.getLeftWidth() - 2, pts[8].y);
                        break;
                    default:
                        pt1 = pts[5];
                        pt2 = new Point(pts[5].x, obj1.getTopHeight() - 2);
                        pt3 = pts[9];
                        pt4 = new Point(pts[9].x, obj1.getTopHeight() - 2);
                        break;
                }
                linha = new Ligacao(this);
                linha.SuperInicie(0, pt1, pt2);
                ((Ligacao) linha).getCard().setCard(PreCardinalidade.TiposCard.C01);
                linha = new Ligacao(this);
                linha.SuperInicie(0, pt3, pt4);
                au_rel.Reenquadre();
                resu = au_rel;
                break;

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="União e Especialização">
            case cmdUniao:
                Uniao uni = new Uniao(this, "Uniao");
                //largura da união usada em "case cmdUniao_Entidades"
                uni.SetBounds(posi.x, posi.y, 40, 32);

                uni.Reenquadre();
                resu = uni;
                break;
            case cmdUniao_Entidades:
                boolean direto = false;
                obj1 = null;
                if (cliq1 == null) {
                    res = CaptureFromPoint(posi);
                    if (res instanceof FormaElementar) {
                        obj1 = (FormaElementar) res;
                    }
                    cliq1 = new clickForma(obj1, posi);
                    if (obj1 != null) {
                        return null;
                    }
                    direto = true;
                }
                obj2 = null;
                if (cliq2 == null && !direto) {
                    res = CaptureFromPoint(posi);
                    if (res instanceof FormaElementar) {
                        obj2 = (FormaElementar) res;
                    }
                    cliq2 = new clickForma(obj2, posi);
                }
                obj1 = cliq1.getForma();
                //obj2 já ok.
                //if ((obj1 instanceof Entidade || obj1 instanceof EntidadeAssociativa) && (obj2 instanceof Entidade || obj2 instanceof EntidadeAssociativa) && obj1 != obj2) {
                if ((obj1 instanceof Entidade) && (obj2 instanceof Entidade) && obj1 != obj2) {
                    int a = (Math.min(obj1.getLeftWidth(), obj2.getLeftWidth()) + Math.max(obj1.getLeft(), obj2.getLeft())) / 2;
                    int b = Math.max(obj1.getTopHeight(), obj2.getTopHeight()); //(Math.min(obj1.getTopHeight(), obj2.getTopHeight()) + Math.max(obj1.getTop(), obj2.getTop())) / 2;
                    //largura da união / 2"
                    a += -20; // 40 div 2 = 20
                    //largura da entidade / 3"
                    b += 40; //Math.min(obj1.getWidth(), obj2.getWidth()) / 2;

                    setComando(Controler.Comandos.cmdUniao);
                    Uniao resUni = (Uniao) RealiseComando(new Point(a, b));
                    //largura da entidade / 3 (40), largura da entidade / 2 e
                    //largura da união / 2 (20)
                    setComando(Controler.Comandos.cmdEntidade);
                    res = RealiseComando(new Point(a - 60 + 20, b + 40));

                    Ligacao liUni = new Ligacao(this);
                    liUni.setInteligente(false);
                    pt2 = new Point(res.getLeft() + res.getWidth() / 2, res.getTop() + 2);
                    pt1 = resUni.getMelhorPontoDeLigacao(pt2);
                    liUni.SuperInicie(0, pt1, pt2); // = 4 pontos

                    liUni = new Ligacao(this);
                    liUni.setInteligente(false);
                    pt2 = new Point(obj1.getLeft() + obj1.getWidth() / 2, obj1.getTopHeight() - 2);
                    pt1 = resUni.getMelhorPontoDeLigacao(pt2);
                    liUni.SuperInicie(0, pt1, pt2); // = 4 pontos

                    liUni = new Ligacao(this);
                    liUni.setInteligente(false);
                    pt2 = new Point(obj2.getLeft() + obj2.getWidth() / 2, obj2.getTopHeight() - 2);
                    pt1 = resUni.getMelhorPontoDeLigacao(pt2);
                    liUni.SuperInicie(0, pt1, pt2); // = 4 pontos

                    resu = resUni;
                } else {
                    cliq1 = null;
                    cliq2 = null;
                    setComando(Controler.Comandos.cmdUniao);
                    resu = RealiseComando(posi);
                }
                break;
            case cmdEspecializacao:
                diagramas.conceitual.Especializacao esp = new Especializacao(this, "Especializacao");
                esp.SetBounds(posi.x, posi.y, 40, 32);
                esp.Reenquadre();
                resu = esp;
                break;
            case cmdEspecializacao_Dupla:
            case cmdEspecializacao_Exclusiva:
                boolean ehExclusiva = (com == Controler.Comandos.cmdEspecializacao_Exclusiva);
                obj1 = null;
                if (cliq1 == null) {
                    res = CaptureFromPoint(posi);
                    obj1 = null;
                    if (res instanceof FormaElementar) {
                        obj1 = (FormaElementar) res;
                    }
                    cliq1 = new clickForma(obj1, posi);
                }
                pt1 = posi;
                pt2 = null;
                //if (obj1 instanceof Entidade || obj1 instanceof EntidadeAssociativa) {
                if (obj1 instanceof Entidade) {
                    pt2 = new Point(posi.x, obj1.getTopHeight() - 2);
                    pt1 = new Point(posi.x, obj1.getTop() + (int) (obj1.getHeight() * 1.5));
                }
                setComando(Controler.Comandos.cmdEspecializacao);
                resu = RealiseComando(pt1);
                //if (obj1 instanceof Entidade || obj1 instanceof EntidadeAssociativa) {
                if (obj1 instanceof Entidade) {
                    obj1.BringToFront();
                    Especializacao espED = (Especializacao) resu;
                    espED.DoMove(-resu.getWidth() / 2, 0);
                    espED.BringToFront();
                    pt1 = espED.getMelhorPontoDeLigacao(pt2);
                    Ligacao liEsp = new Ligacao(this);
                    liEsp.setInteligente(false);
                    liEsp.SuperInicie(0, pt1, pt2); // = 4 pontos

                    if (ehExclusiva) {
                        pt1 = new Point(obj1.getLeft(), obj1.getTop() + obj1.getHeight() * 2 + espED.getHeight());
                        setComando(Controler.Comandos.cmdEntidade);
                        obj2 = RealiseComando(pt1);
                        obj2.BringToFront();

                        pt1 = ((Forma) obj2).getPontosColaterais()[1];

                        liEsp = new Ligacao(this);
                        liEsp.setInteligente(false);
                        pt2 = espED.getPontosColaterais()[3]; // espED.getMelhorPontoDeLigacao(pt1);
                        pt2 = new Point(pt2.x, pt2.y - 2);
                        pt1 = new Point(pt2.x, pt1.y + 2);

                        liEsp.SuperInicie(0, pt1, pt2); // = 4 pontos
                    } else {
                        pt1 = new Point(obj1.getLeft() - obj1.getWidth() * 2 / 3, obj1.getTop() + obj1.getHeight() * 2 + espED.getHeight());
                        pt3 = new Point(obj1.getLeft() + obj1.getWidth() * 2 / 3, pt1.y);
                        setComando(Controler.Comandos.cmdEntidade);
                        obj2 = RealiseComando(pt1);
                        obj2.BringToFront();

                        pt1 = ((Forma) obj2).getPontosColaterais()[1];
                        pt1 = new Point(pt1.x + obj2.getWidth() / 4, pt1.y + 2);

                        liEsp = new Ligacao(this);
                        liEsp.setInteligente(false);
                        pt2 = espED.getMelhorPontoDeLigacao(pt1);
                        liEsp.SuperInicie(0, pt1, pt2); // = 4 pontos

                        setComando(Controler.Comandos.cmdEntidade);
                        obj3 = RealiseComando(pt3);
                        obj3.BringToFront();

                        pt3 = ((Forma) obj3).getPontosColaterais()[1];
                        pt3 = new Point(pt3.x - obj3.getWidth() / 4, pt3.y + 2);
                        liEsp = new Ligacao(this);
                        liEsp.setInteligente(false);
                        pt2 = espED.getMelhorPontoDeLigacao(pt3);
                        liEsp.SuperInicie(0, pt3, pt2); // = 4 pontos
                    }
                }
                break;
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="Atributo">
            case cmdAtributo_Multivalorado:
            case cmdAtributo:
                res = CaptureFromPoint(posi);
                Atributo att = new Atributo(this, "Atributo");
                int wa = 7;
                Ligacao la = new Ligacao(this);
                int largAtt = 72;
                Point pt1a = new Point(posi.x + largAtt, posi.y);
                mx = 2;
                int distancia = 0;
                if (res == null || res instanceof PreLigacao) {
                    att.SetBounds(pt1a.x - 1, pt1a.y - 3, largAtt, (2 * wa));
                    att.BringToFront();
                    //att.Reenquadre();
                    att.repaint();
                    la.SuperInicie(0, pt1a, new Point(pt1a.x - largAtt, pt1a.y + 4));
                    //la.Reenquadre();
                } else {
                    res = res.ProcessaComposicao(posi);
                    Point pt2a = new Point(res.getLeftWidth() + res.getWidth() / 2, res.getTop() + res.getHeight() / 2);
                    if (res instanceof Forma) {
                        obj = (Forma) res;
                        mx = obj.retorneProximidade(posi);
                        switch (mx) {
                            case 0:
                                att.setDirecaoLigacao(Direcao.Right);
                                pt2a = new Point(obj.getLeft() - largAtt - obj.getWidth() / 2, posi.y);
                                break;
                            case 1:
                                distancia = SuperLinha.distancia;
                                pt2a = new Point(posi.x, obj.getTop() - 4 * wa);
                                break;
                            case 2:
                                pt2a = new Point(pt2a.x, posi.y);
                                break;
                            case 3:
                                distancia = SuperLinha.distancia;
                                pt2a = new Point(posi.x, obj.getTopHeight() + 4 * wa);
                                break;
                        }
                    }
                    att.SetBounds(pt2a.x, pt2a.y - wa, largAtt, (2 * wa));
                    att.BringToFront();
                    if (mx == 0) {
                        la.SuperInicie(0, pt2a, posi);
                    } else {
                        la.SuperInicie(0, posi, pt2a);
                    }

                    if (!(la.getFormaPontaA() instanceof Atributo && la.getFormaPontaB() instanceof Atributo)) {
                        int v = la.getPontaB().getLado();
                        x = la.getPontaB().getCentro().x - la.getPontaA().getCentro().x;
                        y = la.getPontaB().getCentro().y - la.getPontaA().getCentro().y;

                        if (v == 1 || v == 3) {
                            y = 0;
                        } else {
                            x = 0;
                        }
                        att.DoMove(x, y);
                    }
                }
                att.reSetBounds();
                att.Reenquadre();
                resu = att;
                if (com == Controler.Comandos.cmdAtributo_Multivalorado) {
                    posi = new Point(mx == 0 ? att.getLeft() + 2 : att.getLeftWidth() - 2, att.getTop());
                    setComando(Controler.Comandos.cmdAtributo);
                    //posi = att.getBounds().getLocation();
                    FormaElementar tmp = RealiseComando(posi);
                    tmp.DoMove(distancia, - 2);

                    setComando(Controler.Comandos.cmdAtributo);
                    tmp = RealiseComando(posi);
                    tmp.DoMove(distancia, tmp.getHeight() + 2);
                }
                if (distancia != 0) {
                    att.DoMove(distancia, 0);
                }
                break;
            // </editor-fold>

            case cmdEntidadeAssociativa:
                EntidadeAssociativa entA = new EntidadeAssociativa(this, "EntidadeAssociativa", new Relacionamento(this, "Relacao"));
                entA.SetBounds(posi.x, posi.y, 158, 58);
                entA.Reenquadre();
                entA.ReenquadreInterno();
                resu = entA;
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

    public void Relacione(PreEntidade A, PreEntidade B) {
        int m = Forma.MapaPosi(A, B);
        int sp = 2;

        //  1.      2      .3
        //  0.      A      .4
        //  7.      6      .5
        Point ptA = new Point();
        Point ptB = new Point();
        switch (m) {
            case 1:
            case 0:
            case 7:
                ptA = new Point(A.getLeft() + sp, A.getTop() + A.getHeight() / 2);
                ptB = new Point(B.getLeftWidth() - sp, B.getTop() + B.getHeight() / 2);
                break;

            case 2:
                ptA = new Point(A.getLeft() + A.getWidth() / 2, A.getTop() + sp);
                ptB = new Point(B.getLeft() + B.getWidth() / 2, B.getTopHeight() - sp);
                break;

            case 3:
            case 4:
            case 5:
                ptA = new Point(A.getLeftWidth() - sp, A.getTop() + A.getHeight() / 2);
                ptB = new Point(B.getLeft() + sp, B.getTop() + B.getHeight() / 2);
                break;

            case 6:
                ptA = new Point(A.getLeft() + A.getWidth() / 2, A.getTopHeight() - sp);
                ptB = new Point(B.getLeft() + B.getWidth() / 2, B.getTop() + sp);
                break;
        }
        setComando(Controler.Comandos.cmdLinha);
        RealiseComando(ptA);
        RealiseComando(ptB);
    }

    @Override
    protected int OnLoadingXMLitem(FormaElementar res, Element fstElmnt, boolean colando, int maxID, HashMap<Element, FormaElementar> link) {
        if (res instanceof EntidadeAssociativa) {
            NodeList nl = fstElmnt.getElementsByTagName(((EntidadeAssociativa) res).getInterno().getClass().getSimpleName());
            if (nl != null && nl.getLength() > 0) {
                Element achado = (Element) nl.item(0);
                if (!colando) {
                    maxID = Math.max(maxID, res.getID());
                }
                link.put(achado, ((PreEntidadeAssociativa) res).getInterno());
            }
        }
        return maxID;
    }

    @Override
    public Object processeEdicaoSubItem(FormaElementar ed, BoxingJava bj) {
        if (ed instanceof EntidadeAssociativa) {
            String resp = bj.Str.substring(bj.Str.indexOf('.') + 1);
            bj.Str = resp;
            return ((PreEntidadeAssociativa) ed).getInterno();
        } else {
            return super.processeEdicaoSubItem(ed, bj);
        }
    }

    public void LancarEditorDeAtributos() {
        if ((getListaDeItens().stream().filter(tb -> tb instanceof PreEntidade).count() == 0)) { // ||  !(getSelecionado() instanceof PreEntidade)) {
            JOptionPane.showMessageDialog(Aplicacao.fmPrincipal,
                    Editor.fromConfiguracao.getValor("Controler.interface.mensagem.sem_attr"),
                    Editor.fromConfiguracao.getValor("Controler.interface.mensagem.tit_informacao"),
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        EditorDeAtributos de = new EditorDeAtributos(Aplicacao.fmPrincipal, true);
        de.setLocationRelativeTo(Aplicacao.fmPrincipal);
        de.Inicie(this);
        de.setVisible(true);
        PerformInspector();
    }

    @Override
    protected void AdicioneSubsFromRealce(ArrayList<FormaElementar> res, FormaElementar item) {
        if (item instanceof Relacionamento) {
            Relacionamento re = (Relacionamento) item;
            re.getListaDeLigacoes().stream().filter(l -> l instanceof SuperLinha && (((SuperLinha) l).getOutraPonta(re) instanceof PreEntidade)).forEach(lfl -> {
                AdicioneSubsFromRealce(res, lfl);
                AdicioneSubsFromRealce(res, ((SuperLinha) lfl).getOutraPonta(re));
            });
        }
        super.AdicioneSubsFromRealce(res, item);
        if (item instanceof Ligacao) {
            Ligacao lig = (Ligacao) item;
            res.add(lig.getCard());
        }
    }

    @Override
    protected void AdicionePrinFromRealce(ArrayList<FormaElementar> res, FormaElementar item) {
        super.AdicionePrinFromRealce(res, item);
        if (item instanceof EntidadeAssociativa) {
            EntidadeAssociativa ea = (EntidadeAssociativa) item;
            if (ea.getInterno() != null) {
                ea.getInterno().getListaDeLigacoes().stream().filter(l -> l instanceof SuperLinha).forEach(lfl -> {
                    AdicioneSubsFromRealce(res, lfl);
                });
                ea.getInterno().getListaDeFormasLigadas().forEach(lfl -> {
                    AdicioneSubsFromRealce(res, lfl);
                });
            }
        }
    }
}
