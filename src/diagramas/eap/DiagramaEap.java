/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.eap;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import controlador.inspector.InspectorProperty;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Desenhador;
import desenho.formas.Forma;
import desenho.formas.Legenda;
import diagramas.conceitual.Texto;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import principal.cli.FormCli;

/**
 *
 * @author ccandido
 */
public class DiagramaEap extends Diagrama {

    private static final long serialVersionUID = -2209832873627630709L;

    public DiagramaEap(Editor omaster) {
        super(omaster);
        setTipo(TipoDeDiagrama.tpEap);

        meusComandos.add(Controler.Comandos.cmdEapProcesso.name());
        meusComandos.add(Controler.Comandos.cmdEapBarraLigacao.name());
        meusComandos.add(Controler.Comandos.cmdEapLigacao.name());
    }

    private final Class[] classesDoDiagrama = new Class[]{
        EapProcesso.class, EapBarraLigacao.class, EapLigacao.class,
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
            case cmdEapProcesso:
                EapProcesso ep = new EapProcesso(this, EapProcesso.class.getSimpleName());
                ep.SetBounds(posi.x, posi.y, 120, 58);
                ep.Reenquadre();
                resu = ep;
                break;
            case cmdEapBarraLigacao:
                EapBarraLigacao bl = new EapBarraLigacao(this, EapBarraLigacao.class.getSimpleName());
                bl.SetBounds(posi.x, posi.y, 120, 10);
                bl.Reenquadre();
                resu = bl;
                break;

            case cmdEapLigacao:
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
                EapLigacao linha = new EapLigacao(this);
                resu = linha;
                pt1 = cliq1.getPonto();
                pt2 = cliq2.getPonto();

                obj1 = cliq1.getForma();
                obj2 = cliq2.getForma();

                if (obj1 != obj2 && (obj1 instanceof EapProcesso) && (obj2 instanceof EapProcesso)) {
                    //crio uma Barra no centro
                    int x = (obj1.getLeft() <= obj2.getLeft())
                            ? (obj1.getLeftWidth() + obj2.getLeft()) / 2
                            : (obj2.getLeftWidth() + obj1.getLeft()) / 2;
                    int y = (obj1.getTop() <= obj2.getTop())
                            ? (obj1.getTopHeight() + obj2.getTop()) / 2
                            : (obj2.getTopHeight() + obj1.getTop()) / 2;

                    Point ptcentral = new Point(x, y);
                    setComando(Controler.Comandos.cmdEapBarraLigacao);
                    FormaElementar resLi = RealiseComando(ptcentral);
                    resLi.setLocation(resLi.getLeft() - (resLi.getWidth() / 2), resLi.getTop() - (resLi.getHeight() / 2));
                    obj1.BringToFront();
                    obj2.BringToFront();
                    resLi.BringToFront();

                    resu = resLi;

                    //Ligo esta nova relação ao pt1 (melhor ponto a ser ligado a ele)
                    Point pt3 = ((Forma) resLi).getMelhorPontoDeLigacao(pt1);
                    linha.FormasALigar = new Forma[]{(Forma) obj1, (Forma) resLi};
                    linha.Inicie(new Rectangle(pt3.x, pt3.y, pt1.x - pt3.x, pt1.y - pt3.y));

                    //Ligo esta nova relação ao pt2!
                    linha = new EapLigacao(this);
                    linha.FormasALigar = new Forma[]{(Forma) obj2, (Forma) resLi};
                    pt1 = pt2;
                    pt2 = ((Forma) resLi).getMelhorPontoDeLigacao(pt2);
                    //segue o código abaixo:....
                }

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
        return resu;
    }

    protected final String COMM_CLI = "CLI";

    @Override
    public void populeComandos(JMenuItem menu) {
        super.populeComandos(menu);
        menu.removeAll();
        menu.setEnabled(true);
        String tmp = Editor.fromConfiguracao.getValor("Controler.interface.Diagrama.Command.Eap.Cli.descricao");
        Diagrama.AcaoDiagrama ac = new Diagrama.AcaoDiagrama(this, tmp, "Controler.interface.Diagrama.Command.Eap.Cli.img", tmp, COMM_CLI);
        ac.normal = false;
        JMenuItem mi = new JMenuItem(ac);
        mi.setName(tmp);
        menu.add(mi);
    }

    @Override
    public void rodaComando(String comm) {
        FormCli fm = new FormCli((Frame) getEditor().getFramePrincipal(), false);
        fm.setLocationRelativeTo((Frame) getEditor().getFramePrincipal());
        fm.SetDiagrama(this);
        fm.setVisible(true);
    }

    private final int ATAG = 95;

    @Override
    public void EndProperty(ArrayList<InspectorProperty> res) {
        super.EndProperty(res);
        res.add(InspectorProperty.PropertyFactorySeparador("cli"));
        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), "cli").setTag(ATAG));
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == ATAG) {
            rodaComando("");
        }
    }

}
