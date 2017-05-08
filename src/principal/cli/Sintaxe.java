/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal.cli;

import controlador.Editor;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ccandido
 */
public class Sintaxe {

    public static Sintaxe Create(String fstSintaxe) {
        return new Sintaxe(fstSintaxe);
    }

    private String comando;

    public Sintaxe(String com) {
        super();
        this.comando = com;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public final ArrayList<Sintaxe> Proximos = new ArrayList<>();

    public void AddProx(String[] prox) {
        ArrayList<String> px = new ArrayList<>();
        px.addAll(java.util.Arrays.asList(prox));
        AddProx(px);
    }

    public void AddProx(ArrayList<String> prox) {
        prox.stream().forEach((str) -> {
            Proximos.add(new Sintaxe(str));
        });
    }

    public Sintaxe AddProx(String prox) {
        Sintaxe tmp = new Sintaxe(prox);
        Proximos.add(tmp);
        return tmp;
    }

    public void AddProx(String[][] proxx) {
        boolean pri;
        for (String[] prox : proxx) {
            pri = true;
            Sintaxe fst = null;
            for (String str : prox) {
                if (pri) {
                    pri = false;
                    fst = new Sintaxe(str);
                    Proximos.add(fst);
                    continue;
                }
                fst.Proximos.add(new Sintaxe(str));
            }
        }
    }

    public void Listar(StringBuilder lst, String tabs) {
        String tab = tabs + " - " + getSintaxeCMD();
        if (Proximos.isEmpty()) {
            lst.append(tab);
            lst.append("\n");
        }
        for (Sintaxe sx : Proximos) {
            sx.Listar(lst, tab);
        }
    }

    public Sintaxe FindByCMD(String com) {
        for (Sintaxe sx : Proximos) {
            if (sx.getComando().equals(com)) {
                return sx;
            }
        }
        return this;
    }

    boolean isValido(ArrayList<Sintaxe> cadeia, ArrayList<String> comm) {
        return isValido(cadeia, comm, 0);
    }

    public static final String VariavelNum = "variavelnum";
    public static final String Variavel = "variavel";

    private boolean isCMDTipoVariavel() {
        return getComando().equals(Variavel) || getComando().equals(VariavelNum);
    }

    boolean isValido(ArrayList<Sintaxe> cadeia, ArrayList<String> comm, int nv) {
        if (nv > comm.size() - 1) {
            return false;
        }
        if (isCMD(comm.get(nv))) {
            nv++;
            cadeia.add(this);
            if (nv == comm.size()) {
                if (Proximos.isEmpty()) {
                    return true;
                }
            }
            for (Sintaxe sx : Proximos) {
                if (sx.isValido(cadeia, comm, nv)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSintaxe(ArrayList<String> comm) {
        String res = getBestCMD(comm.get(0));
        if (res.isEmpty()) {
            return "Comando Inexistente";
        }
        if (Proximos.isEmpty()) {
            return res;
        }
        Sintaxe prx = null;
        int nv = 0;
        for (Sintaxe sx : Proximos) {
            int tmp = sx.getNivelDeValidade(comm, 1);
            if (tmp > nv) {
                nv = tmp;
                prx = sx;
            }
        }
        if (prx == null || comm.size() == 1) {
            res += " [";
            for (Sintaxe sx : Proximos) {
                res += sx.getSintaxeCMD() + ",";
            }
            res = res.substring(0, res.length() - 1) + "]";
            return res;
        }
        return prx.getSintaxe(res, comm, 1);
    }

    public String getSintaxe(String stx, ArrayList<String> comm, int nv) {
        if (nv > comm.size() - 1) {
            return stx;
        }
        String res = getBestCMD(comm.get(nv));
        //este trecho nào deverá ser executado
//        if (res.isEmpty()) {
//            if (Proximos.isEmpty()) {
//                return stx;
//            }
//            res = stx + " [";
//            for (Sintaxe sx : Proximos) {
//                res += sx.getSintaxeCMD() + ",";
//            }
//            res = res.substring(0, res.length() - 1) + "]";
//            return res;
//        }
        //
        stx += " " + res;
        res = stx;
        if (Proximos.isEmpty()) {
            return stx;
        }

        nv++;
        int tmpnv = nv;
        Sintaxe prx = null;
        for (Sintaxe sx : Proximos) {
            int tmp = sx.getNivelDeValidade(comm, nv);
            if (tmp > tmpnv) {
                tmpnv = tmp;
                prx = sx;
            }
        }
        if (prx == null || comm.size() == nv) {
            res += " [";
            for (Sintaxe sx : Proximos) {
                res += sx.getSintaxeCMD() + ",";
            }
            res = res.substring(0, res.length() - 1) + "]";
            return res;
        }
        return prx.getSintaxe(res, comm, nv);
    }

    public String AutoComplete(ArrayList<String> comm) {
        String res = AutoComplete(comm, 0);
        return res;
    }

    public String AutoComplete(ArrayList<String> comm, int nv) {
        if (nv > comm.size() - 1) {
            return "";
        }
        String tmp = comm.get(nv);
        String res = getBestCMD(tmp);
        if (res.isEmpty()) {
            return tmp;
        }
        if (isCMDTipoVariavel()) {
            res = tmp;
        }

        nv++;
        if (nv == comm.size()) {
            if (Proximos.isEmpty()) {
                return res;
            }
            return res + " ";
        }

        int tmpnv = nv;
        Sintaxe prx = null;
        for (Sintaxe sx : Proximos) {
            int idx = sx.getNivelDeValidade(comm, nv);
            if (idx > tmpnv) {
                tmpnv = idx;
                prx = sx;
            }
        }
        if (prx == null) {
            return res + " " + comm.get(nv);
        }
        return res + " " + prx.AutoComplete(comm, nv);
    }

    public int getNivelDeValidade(ArrayList<String> comm) {
        return getNivelDeValidade(comm, 0);
    }

    public int getNivelDeValidade(ArrayList<String> comm, int nv) {
        if (nv > comm.size() - 1) {
            return nv--;
        }
        if (isCMD(comm.get(nv))) {
            int subnv = nv + 1;
            if (subnv == comm.size()) {
                if (Proximos.isEmpty()) {
                    return subnv;
                }
            }
            int res = nv;
            for (Sintaxe sx : Proximos) {
                res = Math.max(res, sx.getNivelDeValidade(comm, subnv));
            }
            return res;
        }
        return nv--;
    }

    private String cmdDigitado = null;
    public String varValor = "";

    public void Preencha(ArrayList<String> comm, HashMap<String, String> Vars) {
        Preencha(comm, Vars, 0);
    }

    public void Preencha(ArrayList<String> comm, HashMap<String, String> Vars, int nv) {
        if (nv == comm.size()) {
            return;
        }
        if (isCMD(comm.get(nv))) {
            cmdDigitado = comm.get(nv);
            if (isCMDTipoVariavel()) {
                varValor = Vars.get(cmdDigitado);
            }
            nv++;
            for (Sintaxe sx : Proximos) {
                sx.Preencha(comm, Vars, nv);
            }
        }
    }

    String getStrPreenchida() {
        return ("Não sei o que eu quiz Not supported yet.");
    }

    public boolean isCMD(String incmd) {
        String[] cmds = getStrSintaxe(getComando()).split("\\|");
        if (isCMDTipoVariavel()) {
            return incmd.startsWith(cmds[1]);
        }
        incmd = incmd.toUpperCase();
        for (String str : cmds) {
            if (str.equals(incmd)) {
                return true;
            }
        }
        return false;
    }

    public String getBestCMD(String incmd) {
        return (isCMD(incmd)) ? getStrSintaxe(getComando()).split("\\|")[0] : "";
    }

    public String getSintaxeCMD() {
        return getStrSintaxe(getComando()).split("\\|")[0];
    }

    public String getStrSintaxe(String partCap) {
        return Editor.fromConfiguracao.getValor("Controler.cli.exp." + partCap.toLowerCase());
    }
}
