/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.apoios;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 *
 * @author ccandido
 */
public class ProcessadorExprSimples {

    public ProcessadorExprSimples() {
        super();
    }
    
    public int processaExprInt(String expr) throws Exception {
        Erro = false;
        String res = processaMathExp(expr.replace(" ", ""));
        if (isErro()) {
            throw new Exception("Format");
        }
        return (int)(Double.parseDouble(res));
    }
    
    public final String mathExpr = "((\\(*[+-]*[0-9]+\\.?[0-9]*\\)*[+-/%\\*/^])*|(\\(*\\$\\w+\\)*[+-/%\\*/^])*)*";

    public boolean IsMathExpr(String v2) {
        String tmp = v2.replaceAll(" ", "") + "+";
        return tmp.matches(mathExpr);
    }
    private boolean Erro = false;

    public boolean isErro() {
        return Erro;
    }

    public void setErro(boolean Erro) {
        this.Erro = Erro;
    }
    public final char[] operadores = new char[]{'+', '-', '/', '*', '%', '^'};

    public boolean isOperador(char sep) {
        for (char a : operadores) {
            if (a == sep) {
                return true;
            }
        }
        return false;
    }

    public String processaMathExp(String expr) {
        if (expr.isEmpty()) {
            return "";
        }
        int inipar = expr.indexOf("(");
        int fimpar = -1;
        int abr = 0;
        //procuro qual parentese feixa
        for (int i = inipar + 1; i < expr.length(); i++) {
            char a = expr.toCharArray()[i];
            if (a == '(') {
                abr++;
            }
            if (a == ')') {
                if (abr > 0) {
                    abr--;
                } else {
                    fimpar = i;
                    break;
                }
            }
        }
        if ((inipar > -1 && fimpar == -1) || (inipar == -1 && fimpar > -1) || (inipar > fimpar)) {
            setErro(true);
            return "";
        }
        if (inipar > -1 && fimpar > -1) {
            if (inipar > 0) {
                char t = expr.toCharArray()[inipar - 1];
                if (!isOperador(t)) {
                    setErro(true);
                    return "";
                }
            }
            String tmp = expr.substring(inipar, fimpar + 1);
            String tmp2 = expr.substring(inipar + 1, fimpar);
            expr = expr.replace(tmp, processaMathExp(tmp2));
            if (isErro()) {
                return "";
            }
            return processaMathExp(expr);
        }

        StringBuilder str = new StringBuilder();
        char[] arr = expr.toCharArray();
        char oper = '\0';
        for (int i = 0; i < expr.length(); i++) {
            if (arr[i] == '+' || arr[i] == '-') {
                if (oper != '\0') {
                    if (arr[i] == '-') {
                        if (oper == '-') {
                            oper = '+';
                        } else {
                            oper = '-';
                        }
                    }
                    //if (arr[i] == '-') não importa!!
                } else {
                    oper = arr[i];
                }
            } else {
                if (oper != '\0') {
                    str.append(oper);
                    oper = '\0';
                }
                str.append(arr[i]);
            }
        }
        expr = str.toString();
        //System.out.println(expr + " [fim]");

        str = new StringBuilder();
        String palavra = "";
        boolean negativo = false;
        ArrayList<BigDecimal> num = new ArrayList<>();
        ArrayList<String> opers = new ArrayList<>();
        expr = expr + "+0"; //para não ter que corrigir a saída do loop.
        for (char tx : expr.toCharArray()) {
            if (isOperador(tx)) {
                palavra = str.toString();
                if (!palavra.isEmpty()) {
                    try {
                        if (isVariavel(palavra)) {
                            palavra = getVarValue(palavra);
                        } else {
                            if (palavra.matches("\\$\\w+")) {
                                setErro(true);
                                return "";
                            }
                        }
                        BigDecimal p = new BigDecimal(palavra);
                        if (negativo) {
                            p = p.negate(); // *= -1;
                            negativo = false;
                        }
                        num.add(p);
                        palavra = "";
                        opers.add(String.valueOf(tx));
                        str = new StringBuilder();
                    } catch (NumberFormatException er) {
                        setErro(true);
                        return "";
                    }
                } else {
//                    //-(-5+3)*(---4)?????
                    if (tx == '-') {
                        negativo = true;
                    } else if (tx != '+') {
                        setErro(true);
                        return "";
                    }
                }
            } else {
                str.append(tx);
            }
        }
        if (num.isEmpty()) {
            setErro(true);
            return "";
        }
        BigDecimal res = num.get(0);
        for (int i = 1; i < num.size(); i++) {
            BigDecimal v1 = num.get(i);
            String op = opers.get(i - 1);
            try {
                switch (op) {
                    case "+":
                        res = res.add(v1);// += v1;
                        break;
                    case "-":
                        res = res.subtract(v1);// -= v1;
                        break;
                    case "*":
                        res = res.multiply(v1);// *= v1;
                        break;
                    case "/":
                        res = res.divide(v1, 5, RoundingMode.FLOOR);// /= v1;
                        break;
                    case "%":
                        res = res.remainder(v1);// %= v1;
                        break;
                    case "^":
                        res = res.pow(v1.intValueExact());
                        break;
                }
            } catch (ArithmeticException ex) {
                setErro(true);
                return "";
            }
        }
        return res.toString(); // Double.toString(res);
    }

    //reescrever nos processadores mais sofisticados
    public boolean isVariavel(String palavra) {
        return false;
    }

    //reescrever nos processadores mais sofisticados
    public String getVarValue(String palavra) {
        return palavra;
    }
}
