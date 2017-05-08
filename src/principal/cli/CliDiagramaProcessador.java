/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal.cli;

import controlador.Diagrama;
import controlador.Editor;
import desenho.FormaElementar;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author ccandido
 */
public class CliDiagramaProcessador {

    /**
     * Variáveis de ambiente
     */
    public HashMap<String, String> Ambiente = new HashMap<>();

    public final String varScrPosX = "amb.scr.x";
    public final String varScrPosY = "amb.scr.y";
    public final String varObjW = "amb.obj.w";
    public final String varObjH = "amb.obj.h";

    public void SetDirectVar(String var, String value) {
        Ambiente.put(var, value);
    }
    
    public void carregueVarsAmbiente() {
        Ambiente.put(varScrPosX, "200");
        Ambiente.put(varScrPosY, "200");
        Ambiente.put(varObjW, "120");
        Ambiente.put(varObjH, "58");
    }

    public final MasterCli CLI;
    public final String cmdSAIR = "sair";
    public final String cmdLISTAR = "listar";
    public final String cmdSET = "set";
    public final String cmdCLEAR = "cls";

    public final String msgNOTHING = "$$NOTHING$$123##123##CARLOS.H.CANDIDO";
    public final String msgOK = "ok";
    public final String msgERRO = "erro";
    public final ArrayList<Sintaxe> Comandos = new ArrayList<>();
    public HashMap<String, Variavel> Vars = new HashMap<>();
    /**
     * Como é identificada uma variável em uma cadeia de caracteres.
     */
    public final String varPrefix = "$$var_";

    public CliDiagramaProcessador(MasterCli cli) {
        super();
        CLI = cli;
        promptNormal = cli.getPrompt();
        Comandos.add(new Sintaxe(cmdSAIR));
        Comandos.add(new Sintaxe(cmdLISTAR));
        Comandos.add(new Sintaxe(cmdCLEAR));

        Sintaxe cmdset = new Sintaxe(cmdSET);
        Sintaxe cmdamb = cmdset.AddProx("ambient");
        cmdamb.AddProx(new String[][]{
            {"amb.scr.x", Sintaxe.VariavelNum},
            {"amb.scr.y", Sintaxe.VariavelNum},
            {"amb.obj.w", Sintaxe.VariavelNum},
            {"amb.obj.h", Sintaxe.VariavelNum}
        });
        Comandos.add(cmdset);
        carregueVarsAmbiente();
    }
    private boolean entradaTexto = false;
    String promptNormal = "";
    String promptEntradaTexto = ".>";
    StringBuilder buffer = new StringBuilder();
    private String erroMsg = "";

    public void setEntradaTexto(boolean entradaTexto) {
        this.entradaTexto = entradaTexto;
        if (entradaTexto) {
            CLI.setPrompt(promptEntradaTexto);
            buffer = new StringBuilder();
        } else {
            CLI.setPrompt(promptNormal);
        }
    }

    public boolean isEntradaTexto() {
        return entradaTexto;
    }

    private String processaChavesParenteses(String comm){
        ArrayList<String> pilha = new ArrayList<>();
        char last = '!';
        for (char a : comm.toCharArray()) {
            if (a == '{' || a == '(') {
                last = a;
                pilha.add(String.valueOf(a));
            } else if (a == '}' || a == ')') {
                if ((a == '}' && last == '{') || (a == ')' && last == '(')) {
                    if (pilha.isEmpty()) {
                        return msgERRO;
                    }
                    pilha.remove(pilha.size() - 1);
                    if (!pilha.isEmpty()) {
                        last = pilha.get(pilha.size() - 1).charAt(0);
                    }
                } else {
                    return msgERRO;
                }
            }
        }
        if (!pilha.isEmpty()) {
            return "TXT";
        }
        return msgNOTHING;
    }

    private boolean writeNothing = false;

    public boolean isWriteNothing() {
        return writeNothing;
    }
    
    public String processeComando(String comm) {
        if (comm.isEmpty()) {
            return msgNOTHING;
        }
        writeNothing = false;
        setJustNewLine(true);
        setLastCmdErro(false);
        Vars.clear();
        if (isEntradaTexto()) {
            buffer.append(comm);
            comm = buffer.toString();
            String res = processaChavesParenteses(comm);
            if (res.equals(msgERRO)) {
                setEntradaTexto(false);
                setLastCmdErro(true);
                return Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcli001");
            }

            if (res.equals("TXT")) {
                buffer.append("\n");
                return msgNOTHING;
            }

            comm = buffer.toString();
            setEntradaTexto(false);
        } else {
            String res = processaChavesParenteses(comm);
            if (res.equals("TXT")) {
                setEntradaTexto(true);
                buffer.append(comm).append("\n");
                return msgNOTHING;
            }
            if (res.equals(msgERRO)) {
                setLastCmdErro(true);
                return Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcli001");
            }
        }
        ArrayList<String> cmds = processadorMor(comm);
        if (isLastCmdErro()) {
            return getmsgErro() + " " + Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcli001");
        }
        if (!cmds.isEmpty()) {
            if (RunCMD(cmds)) {
                if (writeNothing) {
                    return msgNOTHING;
                }
                return getmsgOk();
            }
        }
        if (isLastCmdErro()) {
            return util.Utilidades.EncapsuleMsg("msgcli002", getmsgErro(), getErroMsg());
        }
        return Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcli003");
    }

    public boolean isCMD(String incmd, String comando) {
        String[] cmds = getComando(comando).split("\\|");
        for (String str : cmds) {
            if (str.equals(incmd)) {
                return true;
            }
        }
        return false;
    }

    public void setErroMsg(String erroMsg) {
        this.erroMsg = erroMsg;
    }

    private boolean lastCmdErro = false;

    public boolean isLastCmdErro() {
        return lastCmdErro;
    }

    public void setLastCmdErro(boolean lastCmdErro) {
        this.lastCmdErro = lastCmdErro;
    }

    public String getErroMsg() {
        return erroMsg;
    }

    public String doCancel() {
        Vars.clear();
        setEntradaTexto(false);
        return "^D";
    }

    public String getComando(String partCap) {
        return Editor.fromConfiguracao.getValor("Controler.cli.cmd." + partCap.toLowerCase());
    }

    public String getSintaxe(String partCap) {
        return Editor.fromConfiguracao.getValor("Controler.cli.exp." + partCap.toLowerCase());
    }
    
    public String getPrincipalCMD(String partCap) {
        return getSintaxe(partCap).split("\\|")[0];
    }

    public String getDica(String partCapDica) {
        return Editor.fromConfiguracao.getValor("Controler.cli.dica." + partCapDica.toLowerCase());
    }

    public String getMsg(String partCapDica) {
        return Editor.fromConfiguracao.getValor("Controler.cli.msg." + partCapDica.toLowerCase());
    }

    public String getmsgOk() {
        return getMsg(msgOK);
    }

    public String getmsgErro() {
        return getMsg(msgERRO);
    }

    private boolean justNewLine = true;

    /**
     * O retorno do comando deve ser escrito ou apenas deve-se mostar uma nova linha em branco.
     *
     * @return
     */
    public boolean isJustNewLine() {
        return justNewLine;
    }

    public void setJustNewLine(boolean justNewLine) {
        this.justNewLine = justNewLine;
    }

    public ArrayList<String> processadorMor(String comm) {
        ArrayList<String> res = new ArrayList<>();
        if (comm.isEmpty()) {
            return res;
        }
        setLastCmdErro(false);
        String tmp = processaChavesParenteses(comm);
        if (tmp.equals(msgERRO)) {
            setLastCmdErro(true);
            return res;
        }
        comm = removaConteiner(comm, Vars);

        String[] comms = comm.replaceAll(" +", " ").split(" ");
        res = new ArrayList<>(Arrays.asList(comms));
        return res;
    }

    public String removaConteiner(String comm, HashMap<String, Variavel> vars) {
        int ini = -1;
        char cp = '!';
        int idx = -1;
        String bkp = comm;
        for (char a : comm.toCharArray()) {
            idx++;
            if (a == '{' || a == '(') {
                cp = a;
                ini = idx;
                break;
            }
        }
        if (ini > -1) {
            char achar = (cp == '{' ? '}' : ')');
            int qtd = 0;
            idx = -1;
            int fim = -1;
            for (char a : comm.toCharArray()) {
                idx++;
                if (a == cp) {
                    qtd++;
                }
                if (a == achar) {
                    qtd--;
                }
                if (idx > ini && qtd == 0) {
                    fim = idx;
                    break;
                }
            }
            String tmp = comm.substring(ini + 1, fim);
            Variavel var = new Variavel();
            var.setNome(varPrefix + String.valueOf(Vars.size()));
            var.setOriginal(comm.substring(ini, fim + 1));
            var.setValor(tmp);
            Vars.put(var.getNome(), var);
            comm = (ini > 0 ? comm.substring(0, ini) + " " : "") + var.getNome() + " "
                    + (fim < comm.length() - 1 ? comm.substring(fim + 1) : "");
        }
        if (comm.equals(bkp)) {
            return comm;
        }
        return removaConteiner(comm, vars);
    }
    
    public String DoAutoComplete(String palavra) {
        if (isEntradaTexto()) {
            return "";
        }
        palavra = palavra.trim();
        if (palavra.isEmpty()) {
            return "";
        }
        setLastCmdErro(false);
        Vars.clear();
        ArrayList<String> comm = processadorMor(palavra);
        if (isLastCmdErro()) {
            return "";
        }

        int nv = -1;
        Sintaxe hlp = null;
        for (Sintaxe sx : Comandos) {
            int tmp = sx.getNivelDeValidade(comm);
            if (tmp > nv) {
                hlp = sx;
                nv = tmp;
            }
        }
        if (hlp != null) {
            String res = hlp.AutoComplete(comm);
            return restoreVars(res);
        }
        return "";
    }

    public boolean RunCMD(ArrayList<String> comm) {
        Sintaxe hlp = null;
        int nv = -1;
        for (Sintaxe sx : Comandos) {
            ArrayList<Sintaxe> cadeia = new ArrayList<>();
            if (sx.isValido(cadeia, comm)) {
                return ProcesseComandoValido(cadeia, comm);
            }
            int tmp = sx.getNivelDeValidade(comm);
            if (tmp > nv) {
                hlp = sx;
                nv = tmp;
            }
        }
        if (hlp != null) {
            CLI.doShowMsg(util.Utilidades.EncapsuleMsg("msgcli004", hlp.getSintaxe(comm)));
        }
        return false;
    }

    public boolean ProcesseComandoValido(ArrayList<Sintaxe> cadeia, ArrayList<String> comm) {
        Sintaxe sx = cadeia.get(0);
        if (sx.getComando().equals(cmdSAIR)) {
            CLI.Sair();
            return true;
        }
        
        if (sx.getComando().equals(cmdCLEAR)) {
            CLI.Clear();
            writeNothing = true;
            return true;
        }
        
        if (sx.getComando().equals(cmdLISTAR)) {
            for (Sintaxe sx2 : Comandos) {
                StringBuilder r = new StringBuilder();
                sx2.Listar(r, "");
                CLI.doShowMsg(r.toString());
            }
            for (String k : Ambiente.keySet()) {
                CLI.doShowMsg(getBestCMD(k) + "=" + Ambiente.get(k));
            }
            return true;
        }
        if (sx.getComando().equals(cmdSET)) {
            return processeComandoSet(cadeia, comm);
        }
        return false;
    }

    private String restoreVars(String cmd) {
        for (String k : Vars.keySet()) {
            String javaReplaceDollarDaErro = "\\$\\$" + k.substring(2);
            cmd = cmd.replaceFirst(javaReplaceDollarDaErro, Vars.get(k).getOriginal());
        }
        return cmd;
    }

    public Point restoreVarToPoint(String var) {
        try {
            String res = Vars.get(var).getValor();
            String[] spt = res.trim().replaceAll(" ", "").split(",");
            if (spt.length < 2) {
                return new Point(-1, -1);
            }
            int x = Integer.parseInt(spt[0]);
            int y = Integer.parseInt(spt[1]);
            if (x < 0 || y < 0) {
                return new Point(-1, -1);
            }
            return new Point(x, y);
        } catch (NumberFormatException | NullPointerException numberFormatException) {
            return new Point(-1, -1);
        }
    }

    private Diagrama diag = null;

    public Diagrama getDiag() {
        return diag;
    }

    public void setDiag(Diagrama diag) {
        this.diag = diag;
    }

    protected boolean processeComandoSet(ArrayList<Sintaxe> cadeia, ArrayList<String> comm) {
        Sintaxe sx = cadeia.get(1);
        if (sx.getComando().equals("ambient")) {
            CLI.doShowMsg(getBestCMD(cadeia.get(2).getComando()) + " = " + Empilhe(cadeia.get(2).getComando(), comm.get(3)));
            if (!isLastCmdErro()) {
                return true;
            }
        }
        return false;
    }

    public String Empilhe(String comando, String var) {
        String res = Vars.get(var).getValor().trim();
        String tmp = util.Utilidades.TryIntStr(res, "0");
        Ambiente.put(comando, res);
        if (!tmp.equals(res)) {
            setLastCmdErro(true);
            CLI.doShowMsg(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcli005"));
        }
        return res;
    }

    public int getAmbientInteger(String var) {
        return Integer.parseInt(Ambiente.get(var));
    }

    public String getBestCMD(String partCap) {
        return Editor.fromConfiguracao.getValor("Controler.cli.exp." + partCap.toLowerCase()).split("\\|")[0];
    }

    public boolean IsCommandGetID(String txt) {
        return txt.matches("GET\\(*[0-9]\\)");
    }

    /**
     * Processa a instrução GET(999)
     *
     * @param var GET(x)
     * @return Objeto Encontrado.
     */
    public FormaElementar GetByID(String var) {
        if (IsCommandGetID(var.toUpperCase())) {
            String r = var.substring(4, var.length() - 1).trim();
            String tmp = util.Utilidades.TryIntStr(r, "-1");
            if (!tmp.equals("-1")) {
                return getDiag().FindByID(Integer.parseInt(r));
            }
        }
        return null;

    }

}
