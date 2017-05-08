/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.eap;

import desenho.FormaElementar;
import desenho.formas.Forma;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import principal.cli.CliDiagramaProcessador;
import principal.cli.MasterCli;
import principal.cli.Sintaxe;

/**
 *
 * @author ccandido
 */
public class EapCLI extends CliDiagramaProcessador {

    public EapCLI(MasterCli cli) {
        super(cli);
        Sintaxe cmdnovo = new Sintaxe(cmdNOVO);
        Comandos.add(cmdnovo);
        cmdnovo.AddProx(new String[][]{
            {"eap.eap", "horizontal", "vertical"},
            {"eap.processo", Sintaxe.VariavelNum}
        });

        cmdnovo.FindByCMD("eap.processo").FindByCMD(Sintaxe.VariavelNum).AddProx(Sintaxe.Variavel);

        cmdnovo.FindByCMD("eap.eap").FindByCMD("horizontal").AddProx(new String[][]{
            {"centro", Sintaxe.Variavel},
            {"esquerda", Sintaxe.Variavel},
            {"direita", Sintaxe.Variavel}
        });
        cmdnovo.FindByCMD("eap.eap").FindByCMD("vertical").AddProx(new String[]{
            Sintaxe.Variavel
        });

    }

    final String cmdNOVO = "novo";
    final String cmdEAP = "eap.eap";
    final String cmdPROCESSO = "eap.processo";
    final String cmdHORIZONTAL = "horizontal";
    final String cmdVERTICAL = "vertical";
    final String cmdCENTRO = "centro";
    final String cmdESQUERDA = "esquerda";
    final String cmdDIREITA = "direita";

    
    @Override
    public boolean ProcesseComandoValido(ArrayList<Sintaxe> cadeia, ArrayList<String> comm) {
        if (super.ProcesseComandoValido(cadeia, comm)) {
            return true;
        }
        CLI.doShowMsg(comm.toString());

        Sintaxe sx = cadeia.get(0);
        if (sx.getComando().equals(cmdNOVO)) {
            sx = cadeia.get(1);
            String cmd = sx.getComando();
            switch (cmd) {
                case cmdPROCESSO:
                    getDiag().isCarregando = true;
                    boolean r = processecmdNovoProcesso(comm.get(2), comm.get(3));
                    getDiag().isCarregando = false;
                    if (r) {
                        getDiag().DoMuda(null);
                        getDiag().repaint();
                    }
                    return r;
                case cmdEAP:
                    getDiag().isCarregando = true;
                    boolean r2 = processecmdNovoEap(cadeia, comm);
                    getDiag().isCarregando = false;
                    if (r2) {
                        getDiag().DoMuda(null);
                        getDiag().repaint();
                    }
                    return r2;
//                    //break;
            }
        }
        return true;
    }

    private boolean processecmdNovoProcesso(String var1, String var2) {
        Point p = restoreVarToPoint(var1);
        if (p.x == -1) {
            setLastCmdErro(true);
            setErroMsg("Erro ao informar o valor da posição do objeto");
            return false;
        }
        NovoProcesso(p, Vars.get(var2).getValor());

        return true;
    }

    private EapProcesso NovoProcesso(Point posi, String txt) {
        FormaElementar fnd = GetByID(txt);
        if (fnd instanceof EapProcesso) {
            return (EapProcesso) fnd;
        } else {
            if (IsCommandGetID(txt)) {
                txt = "Não encontrado!";
            }
        }

        int larg = getAmbientInteger(varObjW);
        int alt = getAmbientInteger(varObjH);
        EapProcesso eapp = new EapProcesso(getDiag(), EapProcesso.class.getSimpleName());
        eapp.SetBounds(posi.x, posi.y, larg, alt);
        eapp.SetTexto(txt);
        eapp.reSetBounds();
        eapp.Reposicione();
        eapp.BringToFront();
        return eapp;
    }

    private EapBarraLigacao NovaBarra(Point posi, int dir) {
        EapBarraLigacao eapp = new EapBarraLigacao(getDiag(), EapBarraLigacao.class.getSimpleName());
        eapp.setDirecao(dir);
        eapp.setLocation(posi);
        eapp.reSetBounds();
        eapp.Reposicione();
        eapp.BringToFront();
        return eapp;
    }

    private boolean processecmdNovoEap(ArrayList<Sintaxe> cadeia, ArrayList<String> comm) {
        Sintaxe sx = cadeia.get(2);
        String cmd = sx.getComando();
        String strs;
        if (cmd.equals(cmdVERTICAL)) {
            strs = comm.get(3);
        } else {
            strs = comm.get(4);
            cmd += cadeia.get(3).getComando();
        }
        ArrayList<String> itens = processeStr(strs);
        if (itens.size() < 2) {
            setLastCmdErro(true);
            setErroMsg("Erro ao informar a quantidade de processos.");
            return false;
        }
        X = getAmbientInteger(varScrPosX);
        Y = getAmbientInteger(varScrPosY);
        EapProcesso proc = criarUnidades(itens, cmd, true);
        EapBarraLigacao b = proc.getListaDeFormasLigadas().stream().filter(f -> f instanceof EapBarraLigacao).map(f -> (EapBarraLigacao)f).findFirst().orElse(null);
        if (b != null) {
            b.getMaster().isCarregando = true;
            b.FullOrganizeEap();
            b.getMaster().isCarregando = false;         
        }
        return true;
    }

    private ArrayList<String> processeStr(String strs) {
        strs = Vars.get(strs).getValor();
        strs = removaConteiner(strs, Vars);
        String[] objs = strs.split("\n");
        ArrayList<String> itens = new ArrayList<>();
        for (String str : objs) {
            if (!(str == null || str.isEmpty())) {
                itens.add(str);
            }
        }
        return itens;
    }
    
    public boolean haveVars(String cmd) {
        String[] vls = cmd.split(" ");
        for (String a: vls) {
            if (a.startsWith("$$") && Vars.containsKey(a)) {
                return true;
            } 
        }
        return false;
    }
    
    public ArrayList<String> reempilhe(String cmd) {
        String[] vls = cmd.split(" ");
        ArrayList<String> res = new ArrayList<>();
        for (String a: vls) {
            if (a.startsWith(varPrefix) && Vars.containsKey(a)) {
                res.addAll(processeStr(a));
            } 
        }
        int i = cmd.indexOf(varPrefix);
        if (i > 0) {
            res.add(0, cmd.substring(0, i));
        } else res.add(0, "?");
        return res;
    }

    final int VERTICAL = 0;
    final int HORIZONTAL = 1;
    final int ABAIXO = 0;
    final int ESQUERDA = 1;
    final int ACIMA = 2;

    
    int X = 0;
    int Y = 0;
    private EapProcesso criarUnidades(ArrayList<String> itens, String tp, boolean principal) {
        EapBarraLigacao br = null;
        EapProcesso PP = null;
        if (tp.equals(cmdVERTICAL)) {
            PP = NovoProcesso(new Point(X, Y), itens.get(0));
            Y = PP.getTopHeight() + 50;
            br = NovaBarra(new Point(X + (PP.getWidth()/2) -5, Y), VERTICAL);
            Y += 50;
            Ligue(br, PP, ABAIXO);
            X += 100;
            for (int i = 1; i < itens.size(); i++) {
                String tmp = itens.get(i);
                EapProcesso p;
                if (haveVars(tmp)) {
                    ArrayList<String> subs = reempilhe(tmp);
                    p = criarUnidades(subs, tp, false);
                    X -= 100;
                } else {
                    p = NovoProcesso(new Point(X, Y), tmp);
                }
                Y = p.getTopHeight() + 50;
                Ligue(br, p, ESQUERDA);
            }
        }
        int larg = getAmbientInteger(varObjW);
        int x = X, y = Y;

        if (tp.equals(cmdHORIZONTAL + cmdCENTRO)) {
            int bkp = x;
            x = x + (((itens.size() - 1) * (larg + 10)) - 10) / 2 - (larg / 2); // total de itens, largura do proc, distancia entre itens, , metade da largura.
            PP = NovoProcesso(new Point(x, y), itens.get(0));
            y = PP.getTopHeight() + 50;
            x = PP.getLeft(); //caso o processo já exista.
            br = NovaBarra(new Point(x, y), HORIZONTAL);
            br.setPosicaoDireto(br.HCENTRO);
            y += 50;
            Ligue(br, PP, ABAIXO);
            x = bkp;
            for (int i = 1; i < itens.size(); i++) {
                //EapProcesso p = NovoProcesso(new Point(x, y), itens.get(i));
                String tmp = itens.get(i);
                EapProcesso p;
                if (haveVars(tmp)) {
                    X = x;
                    Y = y;
                    ArrayList<String> subs = reempilhe(tmp);
                    p = criarUnidades(subs, tp, false);
                } else {
                    p = NovoProcesso(new Point(x, y), tmp);
                }
                x += 130;
                Ligue(br, p, ACIMA);
            }
        }
        if (tp.equals(cmdHORIZONTAL + cmdESQUERDA)) {
            int tmp2 = ((itens.size() - 1) * (larg + 10)); // total de itens, largura do proc, distancia entre itens, , metade da largura.
            x += tmp2;
            PP = NovoProcesso(new Point(x, y), itens.get(0));

            x -= tmp2;

            y = PP.getTopHeight() + 50;
            br = NovaBarra(new Point(x, y), HORIZONTAL);
            br.setPosicaoDireto(br.HESQUERDA);
            y += 50;
            Ligue(br, PP, ABAIXO);
            for (int i = 1; i < itens.size(); i++) {
                //EapProcesso p = NovoProcesso(new Point(x, y), itens.get(i));
                String tmp = itens.get(i);
                EapProcesso p;
                if (haveVars(tmp)) {
                    X = x;
                    Y = y;
                    ArrayList<String> subs = reempilhe(tmp);
                    p = criarUnidades(subs, tp, false);
                } else {
                    p = NovoProcesso(new Point(x, y), tmp);
                }
                x += 130;
                Ligue(br, p, ACIMA);
            }
        }
        if (tp.equals(cmdHORIZONTAL + cmdDIREITA)) {
            PP = NovoProcesso(new Point(x, y), itens.get(0));
            x = PP.getLeft();
            x = x + (larg + 10) + 10;
            y = PP.getTopHeight() + 50;
            br = NovaBarra(new Point(x, y), HORIZONTAL);
            br.setPosicaoDireto(br.HDIREITA);
            y += 50;
            Ligue(br, PP, ABAIXO);
            for (int i = 1; i < itens.size(); i++) {
                //EapProcesso p = NovoProcesso(new Point(x, y), itens.get(i));
                String tmp = itens.get(i);
                EapProcesso p;
                if (haveVars(tmp)) {
                    X = x;
                    Y = y;
                    ArrayList<String> subs = reempilhe(tmp);
                    p = criarUnidades(subs, tp, false);
                } else {
                    p = NovoProcesso(new Point(x, y), tmp);
                }
                x += larg + 10;
                Ligue(br, p, ACIMA);
            }
        }
        if ((br != null) && principal) {
            br.PreOrganizeEap();
        }
        return PP;
    }

    private void Ligue(EapBarraLigacao br, EapProcesso p, int tp) {
        EapLigacao linha = new EapLigacao(getDiag());
        Point pt1;
        Point pt2;
        switch (tp) {
            case ABAIXO:
                pt1 = new Point(p.getLeft() + p.getWidth() / 2, p.getTopHeight() - 2);
                pt2 = new Point(br.getLeft() + br.getWidth() / 2, br.getTop() + 2);
                break;
            case ESQUERDA:
                pt1 = new Point(p.getLeft() + 2, p.getTop() + p.getHeight() / 2);
                pt2 = new Point(br.getLeft() + br.getWidth() / 2, br.getTopHeight() - 2);
                break;
            default: //ACIMA
                pt1 = new Point(p.getLeft() + p.getWidth() / 2, p.getTop() + 2);
                pt2 = new Point(br.getLeft() + br.getWidth() / 2, br.getTopHeight() - 2);
        }
        linha.FormasALigar = new Forma[] {br, p};
        linha.Inicie(new Rectangle(pt2.x, pt2.y, pt1.x - pt2.x, pt1.y - pt2.y)); // = 4 pontos
    }

}
