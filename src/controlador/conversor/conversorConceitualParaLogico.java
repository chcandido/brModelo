/*
 * Copyright (C) 2014 CHC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package controlador.conversor;

import controlador.Controler;
import controlador.Editor;
import desenho.FormaElementar;
import desenho.formas.Desenhador;
import desenho.formas.Forma;
import desenho.formas.Legenda;
import desenho.linhas.PontoDeLinha;
import desenho.preAnyDiagrama.PreCardinalidade;
import desenho.preAnyDiagrama.PreEntidade;
import diagramas.conceitual.Atributo;
import diagramas.conceitual.DiagramaConceitual;
import diagramas.conceitual.EntidadeAssociativa;
import diagramas.conceitual.Especializacao;
import diagramas.conceitual.Ligacao;
import diagramas.conceitual.Relacionamento;
import diagramas.conceitual.Texto;
import diagramas.conceitual.Uniao;
import diagramas.logico.Campo;
import diagramas.logico.Constraint;
import diagramas.logico.DiagramaLogico;
import diagramas.logico.LogicoLinha;
import diagramas.logico.Tabela;
import java.awt.Frame;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import util.BrLogger;

/**
 *
 * @author CHC
 */
public class conversorConceitualParaLogico {

    public DiagramaConceitual origem = null;
    public DiagramaLogico destino = null;

    private boolean removerEspecial = false;

    public conversorOpcoes Opcoes = new conversorOpcoes();

    public boolean beginConvert(DiagramaConceitual or, DiagramaLogico des) {
        origem = or;
        destino = des;
        destino.isCarregando = true;
        origem.ClearSelect();

        boolean sn = perguntaCaracteres()
                && converterEntidades()
                && converterAtributos()
                && converterEspecializacao()
                && converterUniao()
                && converterAutoRelacionamento()
                && converterRelacionamento()
                && converterEntidadeAssossiativa()
                && exportarAssessorios()
                && processeConstraints();

        destino.isCarregando = false; //para facilitar o repaint.
        destino.getEditor().paintImmediately(0, 0, destino.getEditor().getWidth(), destino.getEditor().getHeight());
        if (sn) {
            destino.isCarregando = true; // para evitar o DoMuda().
            renomeieFKs();
            organizeDiagrama();
            destino.isCarregando = false; // pronto.
            destino.DoMuda(null);
            destino.repaint();
            String tmp = "";
            if (!Opcoes.Erros.isEmpty()) {
                tmp = Opcoes.Erros.stream().map(s -> "\n" + s).reduce(tmp, String::concat);
            }
            JOptionPane.showMessageDialog(origem.getEditor().getParent(), Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg75")
                    + (tmp.isEmpty() ? "" : "\n" + Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg76") + tmp), Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg77"), JOptionPane.INFORMATION_MESSAGE);

        } else {
            destino.DoMuda(null);
            JOptionPane.showMessageDialog(origem.getEditor().getParent(), Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg78"),
                    Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg79"), JOptionPane.WARNING_MESSAGE);
        }

        destino.getEditor().diagramaAtual.PerformInspector();

        return sn;
    }

    private boolean perguntaCaracteres() {
        Opcoes.Inicializar();
        Opcoes.opcDefault = 0;
        Opcoes.Textos.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.conceitual"));
        Opcoes.Questoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.conceitual.carcteres"));
        Opcoes.Questoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.conceitual.carcteresNO"));
        Opcoes.obj = null;
        if (!Questione()) {
            return false;
        }
        removerEspecial = (Opcoes.OPC == 0);
        return true;
    }

    private conversorLink Links = new conversorLink();

    public conversorLink getLinks() {
        return Links;
    }

    public void setLinks(conversorLink Links) {
        this.Links = Links;
    }

    public boolean converterEntidades() {
        /* Primeiro processo de conversão. Cada entidade correponderá a uma Tabela */
        origem.getListaDeItens().stream().filter(o -> o instanceof PreEntidade).map(e -> (PreEntidade) e).forEach(e -> {
            FormaElementar res = destino.ExternalRealiseComando(Controler.Comandos.cmdTabela, e.getLocation());
            if (res != null) {
                ((Tabela) res).SetTexto(removerCaracteresEspeciais(e.getTexto()));
                Links.Add(e, res);
            }
        });
        destino.isCarregando = false;
        destino.repaint();
        destino.isCarregando = true;
        return true;
    }

    public boolean converterAtributos() {

        /*Atributos de entidades - Segundo processo de conversão */
        List<PreEntidade> ents = origem.getListaDeItens().stream().filter(o -> o instanceof PreEntidade).map(e -> (PreEntidade) e).collect(Collectors.toList());
        for (PreEntidade E : ents) {
            List<Atributo> lst = E.getListaDePontosLigados().stream()
                    .filter(p -> p.getDono().getOutraPonta(p).getEm() instanceof Atributo)
                    .map(p -> (Atributo) (p.getDono().getOutraPonta(p).getEm()))
                    .collect(Collectors.toList());

            List<FormaElementar> tmp = Links.getLigadosOrigem(E);
            if (tmp.isEmpty() || tmp.size() > 1) {
                BrLogger.Logger("MSG_CONVERT_ERROR", "QTD TBL " + String.valueOf(tmp.size()));
                Opcoes.Erros.add("MSG_CONVERT_ERROR: " + "QTD TBL " + String.valueOf(tmp.size()));
                return false;
            }

            Tabela tbl = (Tabela) tmp.stream().filter(t -> t instanceof Tabela).findFirst().orElse(null);

            /* Precaussão. Nunca deverá acontecer! */
            if (tbl == null) {
                BrLogger.Logger("MSG_CONVERT_ERROR", "QTD TBL 0");
                Opcoes.Erros.add("MSG_CONVERT_ERROR: " + "QTD TBL 0");
                return false;
            }

            if (!recebaEConvertaAtributos(E, tbl, lst)) {
                return false;
            }
            recebaEConvertaAtributosOcultos(tbl, E.getAtributosOcultos().trim());
        }
        return true;
    }

    private boolean recebaEConvertaAtributosOcultos(Tabela tb, String ao) {
        String[] lst = ao.split("\n");
        for (String a : lst) {
            a = a.trim();
            String tipo = "";
            if (!a.isEmpty()) {
                if (a.contains(" ")) {
                    String[] ct = a.replaceAll(" +", " ").split(" ");
                    a = ct[0];
                    tipo = ct[1];
                }
                Campo c = tb.Add(a);
                c.setTexto(removerCaracteresEspeciais(a));
                if (!tipo.isEmpty()) {
                    c.setTipo(tipo);
                }
            }
        }
        return true;
    }

    private final int DIST = 120;

    private boolean recebaEConvertaAtributos(Forma E, Tabela tb, List<Atributo> lst) {
        boolean res = true;
        ArrayList<String> infos = Opcoes.Textos;
        String pk = Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.pk.sufix");
        String fk = Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.sufix");
        for (Atributo a : lst) {
            boolean tmpcardMaxUtil = a.getCardMaxima() > 1 || a.getCardMaxima() == -1;
            if (a.isAtributoComposto() || a.isOpcional() || (a.isMultivalorado() && tmpcardMaxUtil)) {
                int tmp = (a.isOpcional() ? 1 : 0) + (a.isAtributoComposto() ? 1 : 0) + (a.isMultivalorado() ? 1 : 0);

                Opcoes.Inicializar();

                String and = " " + Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg06") + " ";

                infos.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.atributo.atributo") + " " + (a.isAtributoComposto() ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.atributo.composto") : "")
                        + (tmp == 3 ? ", " : (tmp > 1 ? and : ""))
                        + (a.isOpcional() ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.atributo.opcional") : "")
                        + (tmp > 1 ? and : "")
                        + (a.isMultivalorado() ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.atributo.multivalorado") : "")
                        + ": "
                        + a.getTexto());
                String s = util.Utilidades.EncapsuleMsg("msg12", (a.getObservacao().isEmpty() ? "[" + Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg09") + "]" : a.getObservacao()), E.getTexto());
                infos.add(s);

                Opcoes.opcDefault = (a.isMultivalorado() && tmpcardMaxUtil) ? 0 : 1;

                Opcoes.Questoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg11"));
                if (a.isOpcional() && tmp == 1) {
                    Opcoes.Questoes.add(util.Utilidades.EncapsuleMsg("msg12", tb.getTexto()));
                } else {
                    Opcoes.Questoes.add(util.Utilidades.EncapsuleMsg("msg13", tb.getTexto()));
                }

                List<Atributo> attrs = new ArrayList<>();
                boolean Limitado = captureAtributos(a, attrs);

                if (Limitado) {
                    Opcoes.Observacoes.add(0, Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg14"));
                    Opcoes.Disables.add(1);
                    Opcoes.opcDefault = 0;
                }
                Opcoes.obj = a;
                if (!Questione()) {
                    return false;
                }
                Tabela ntb = tb;
                if (Opcoes.OPC == 0) {
                    FormaElementar xres = CriarTabela(tb);
                    if (xres != null) {
                        ntb = ((Tabela) xres);
                        String ax = removerCaracteresEspeciais(a.getTexto());
                        ntb.setTexto(ax);
                        Links.Add(a, xres);
                        setOrigemLigacao(
                                LinkTable(tb, ntb, (a.isOpcional() ? 0 : (a.isMultivalorado() ? 0 : 1)), (Limitado ? 3 : 1)),
                                tb);
                        Campo c = ntb.Add(ax + pk);
                        c.setTexto(ax + pk);
                        c.setKey(true);
                        c.setTipo("INT");
                        c.setComplemento("NOT NULL");

                        Campo c2 = tb.Add(ax + fk);
                        c2.setTexto(ax + fk);
                        c2.setFkey(true);
//#                        c2.setTabelaOrigem(ntb);
//                        c2.setCampoOrigem(c);
                        setCampoOrigem(c2, c);
                    }
                }
                final Tabela T = ntb;
                attrs.forEach(at -> {
                    ImportaComoCampo(T, at);
                });
            } else {
                ImportaComoCampo(tb, a);
            }
        }
        return res;
    }

    private void ImportaComoCampo(Tabela tb, Atributo a) {
        String ax = removerCaracteresEspeciais(a.getTexto());
        Campo c = tb.Add(ax);
        c.setTexto(ax);
        c.setTipo(a.getTipoAtributo());
        c.setKey(a.isIdentificador());
        //c.setFkey(false);
        c.setObservacao(a.getObservacao());
        c.setDicionario(a.getTextoAdicional());
        //Links.Add(a, ?);
    }

    private Campo ImportaCampo(Tabela tb, Campo a, String preTxt) {
        Campo c = tb.Add("_");
        String ax = removerCaracteresEspeciais(preTxt + a.getTexto());
        c.setTexto(ax);
        c.setTipo(a.getTipo());
        c.setKey(a.isKey());
        c.setFkey(a.isFkey());
        c.setObservacao(a.getObservacao());
        c.setDicionario(a.getDicionario());
        //Links.Add(a, ?);
        return c;
    }

    private Campo ImportaCampoIgnoreConstrais(Tabela tb, Campo a, String preTxt) {
        Campo c = tb.Add("_");
        String ax = removerCaracteresEspeciais(preTxt + a.getTexto());
        c.setTexto(ax);
        c.setTipo(a.getTipo());
        //c.setKey(a.isKey());
        //c.setFkey(a.isFkey());
        c.setObservacao(a.getObservacao());
        c.setDicionario(a.getDicionario());
        return c;
    }

    private void ImportaCampoChave(Tabela ori, Tabela dest, String preTxt) {
        if (ori == dest) {
            return;
        }
        ori.getCampos().stream().filter(c -> c.isKey()).forEach(a -> {
            Campo c = dest.Add("_");
            String ax = removerCaracteresEspeciais(preTxt + a.getTexto());
            c.setTexto(ax);
            c.setTipo(a.getTipo());
            c.setFkey(true);
//#            c.setTabelaOrigem(ori);
//            c.setCampoOrigem(a);
            setCampoOrigem(c, a);
            c.setObservacao(a.getObservacao());
            c.setDicionario(a.getDicionario());
        });
    }

    private void ImportaCampoChaveKFK(Tabela ori, Tabela dest, String preTxt) {
        if (ori == dest) {
            return;
        }
        ori.getCampos().stream().filter(c -> c.isKey()).forEach(a -> {
            Campo c = dest.Add("_");
            String ax = removerCaracteresEspeciais(preTxt + a.getTexto());
            c.setTexto(ax);
            c.setTipo(a.getTipo());
            c.setFkey(true);
            c.setKey(true);
            setCampoOrigem(c, a);
            c.setObservacao(a.getObservacao());
            c.setDicionario(a.getDicionario());
        });
    }

    private void ImportaCampo(Tabela ori, Tabela dest) {
        if (ori == dest || ori == null || dest == null) {
            return;
        }
        ori.getCampos().stream().forEach(a -> {

            if (dest.getCampos().stream().filter(ca -> ca.getTexto().equals(a.getTexto()) && (getCampoOrigem(ca) == getCampoOrigem(a))
                    && (ca.isFkey() == a.isFkey()) && (ca.isKey() == a.isKey())).count() == 0) {
                Campo c = dest.Add("_");
                //c.setStopExplode(true);
                String ax = removerCaracteresEspeciais(a.getTexto());
                c.setTexto(ax);
                c.setTipo(a.getTipo());
                c.setKey(a.isKey());
                c.setFkey(a.isFkey());
                if (c.isFkey()) {
                    //c.setTabelaOrigem(a.getTabelaOrigem());
                    setCampoOrigem(c, getCampoOrigem(a));
                }
                c.setObservacao(a.getObservacao());
                c.setDicionario(a.getDicionario());
                //c.setStopExplode(false);
            }
        });
    }

    BufferedImage dig_img = null;
    private boolean Questione() {
        if (Opcoes.isYesToAll()) {
            Opcoes.OPC = Opcoes.opcDefault;
            return true;
        }
        
        conversorDialogo fm = new conversorDialogo((Frame) origem.getEditor().getFramePrincipal(), true);
        if (Opcoes.LastPosi == null) {
            fm.setLocationRelativeTo((Frame) origem.getEditor().getFramePrincipal());
        } else {
            fm.setLocation(Opcoes.LastPosi);
        }
        if (dig_img == null) {
            dig_img = util.ImageGenerate.geraImagem(origem);
        }
        fm.Inicializar(origem, destino, Opcoes, dig_img);
        fm.setVisible(true);

        return Opcoes.Resultado != conversorOpcoes.resultOfQuestion.respCancel;
    }

    //<editor-fold defaultstate="collapsed" desc="Percorre os atributos, gerando uma lista. Explora a condição dos atributos multivalorados e/ou composto">
    private boolean captureAtributos(Atributo A, List<Atributo> lst) {
        int mv = 1;
        boolean sera_limitado = false;
        if (A.isMultivalorado()) {
            sera_limitado = A.getCardMaxima() < 0 || A.getCardMaxima() > 10;
            mv = sera_limitado ? 1 : A.getCardMaxima(); //incluir apenas 1 no caso dos multivalorados!
        }
        List<Atributo> tmp = A.getAtributos();
        for (int i = 0; i < mv; i++) {
            if (tmp.isEmpty()) {
                lst.add(A);
            } else {
                for (Atributo a : tmp) {
                    if (captureAtributos(a, lst)) {
                        sera_limitado = true;
                    }
                }
            }
            if (lst.size() > 50) {
                sera_limitado = true;
                break;
            }
        }
        if (sera_limitado) {
            Opcoes.Observacoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg16") + " " + A.getTexto() + " "
                    + Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg17"));
        }
        return sera_limitado;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Liga tabelas">
    private LogicoLinha LinkTable(Tabela origem, Tabela destino, int cardO, int cardD) {
        LogicoLinha lin = Ligue(origem, destino);
        lin.getCardA().setCard(cardO);
        lin.getCardB().setCard(cardD);
        lin.ajusteSeta();
        return lin;
    }

    private LogicoLinha Ligue(Tabela A, Tabela B) {
        LogicoLinha linha = new LogicoLinha(A.getMaster());
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
        linha.FormasALigar = new Forma[]{A, B};
        linha.SuperInicie(0, ptB, ptA);
        linha.Ligar();

        return linha;
    }
    //</editor-fold>

    private Point EspacoOcupado(Tabela tb, Point p) {
        FormaElementar ac = tb; //# apenas para facilitar o looping
        Point res = new Point(p);
        while (ac != null) {
            final Point x = new Point(res);
            ac = tb.getMaster().getListaDeItens().stream().filter(o -> o.getLocation().equals(x)).findFirst().orElse(null);
            if (ac != null) {
                res = new Point(ac.getLeft(), ac.getTopHeight() + DIST / 5);
            }
        }
        return res;
    }

    private void organizeDiagrama() {
        destino.OrganizeTabelas();
        destino.getListaDeTabelas().stream().forEach(tt -> {
            tt.OrganizeDiagrama();
        });
    }

    private ArrayList<Tabela> frutoAutoRelacionamento = new ArrayList<>();

    public boolean converterAutoRelacionamento() {
        List<Relacionamento> lst = origem.getListaDeItens().stream().filter(o -> (o instanceof Relacionamento) && ((Relacionamento) o).isAutoRelacionamento())
                .map(r -> (Relacionamento) r).collect(Collectors.toList());
        for (Relacionamento R : lst) {
            if (!R.getListaDeFormasLigadas().isEmpty()) {
                PreEntidade E = R.getListaDeFormasLigadas().stream().filter(o -> o instanceof PreEntidade).map(e -> (PreEntidade) e).findFirst().orElse(null);
                List<Ligacao> lig = R.getListaDePontosLigados().stream().filter(o -> o.getDono().getOutraPonta(R) instanceof PreEntidade)
                        .map(o -> (Ligacao) o.getDono()).collect(Collectors.toList());
                int tmp1 = lig.get(0).getCard().CardToInt();
                int tmp2 = lig.get(1).getCard().CardToInt();

                int Card2 = Math.max(tmp1, tmp2);
                int Card1 = Math.min(tmp1, tmp2);

                boolean AdCol = false;
                if (Card2 == 1) {
                    AdCol = true;
                } else if (!(Card1 > 1)) {

                    Opcoes.Inicializar();
                    String tmp = util.Utilidades.EncapsuleMsg("msg18", PreCardinalidade.CardToString(Card1), PreCardinalidade.CardToString(Card2));
                    Opcoes.Textos.add(tmp);
                    tmp = util.Utilidades.EncapsuleMsg("msg20", E.getTexto(), R.getTexto());
                    Opcoes.Textos.add(tmp);

                    AddObservacoes(R, true);
                    Opcoes.opcDefault = (Card2 == 2 ? 0 : 1);

                    tmp = util.Utilidades.EncapsuleMsg("msg21", E.getTexto());
                    Opcoes.Questoes.add(tmp);
                    Opcoes.Questoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg22"));
                    if (Card1 == 0 && (Card2 == 0 || Card2 == 2)) {
                        //# Se for obrigatória, ou seja (1,1) e ((1,1)||(1,N)) não será possível criar a recursividade.
                        Opcoes.Disables.add(0);
                        tmp = util.Utilidades.EncapsuleMsg("msg23", PreCardinalidade.CardToString(Card1), PreCardinalidade.CardToString(Card2));
                        Opcoes.Textos.add(tmp);
                    }
                    Opcoes.obj = R;
                    if (!Questione()) {
                        return false;
                    }
                    AdCol = (Opcoes.OPC == 0);
                }

                List<Tabela> tabs = Links.getLigadosOrigem(E).stream().filter(t -> t instanceof Tabela).map(t -> (Tabela) t).collect(Collectors.toList());

                for (Tabela T : tabs) {
                    Tabela T2 = T;
                    if (AdCol) {
                        for (int i = 0; i < T.getCampos().size(); i++) {
                            Campo C = T.getCampos().get(i);
                            if (!C.isKey()) {
                                continue;
                            }
                            Campo c = ImportaCampo(T, C, T.getTexto() + "_");
                            c.setKey(false);
                            c.setFkey(true);
                            setCampoOrigem(c, C);
                        }
                    } else {
                        FormaElementar xres = CriarTabela(T);
                        if (xres != null) {
                            T2 = ((Tabela) xres);
                            String ax = removerCaracteresEspeciais(R.getTexto() + (tabs.size() > 1 ? "_" + String.valueOf(tabs.indexOf(T) + 1) : ""));
                            T2.setTexto(ax);
                            Links.Add(R, xres);
                            setOrigemLigacao(
                                    LinkTable(T, T2, 0, Card2), //# recursividade sempre cad min = (1,1).
                                    T2);
                            final Tabela TT = T2;
                            //duas vezes
                            T.getCampos().stream().filter(c -> c.isKey()).forEach(C -> {
                                Campo c = ImportaCampo(TT, C, T.getTexto() + "_A_");
                                c.setKey(false);
                                c.setFkey(true);
                                setCampoOrigem(c, C);
                            });
                            //Conforme acima: duas vezes
                            T.getCampos().stream().filter(c -> c.isKey()).forEach(C -> {
                                Campo c = ImportaCampo(TT, C, T.getTexto() + "_B_");
                                c.setKey(false);
                                c.setFkey(true);
                                setCampoOrigem(c, C); //# desta vez não assossiar
                            });
                            frutoAutoRelacionamento.add(TT);
                        }
                    }
                    if (!recebaEConvertaAtributos(R, T2, R.getListaDeFormasLigadas().stream()
                            .filter(a -> a instanceof Atributo)
                            .map(a -> (Atributo) a)
                            .collect(Collectors.toList()))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private FormaElementar CriarTabela(Tabela T) {
        Point p = new Point(T.getLeftWidth() + DIST, T.getTop());
        p = EspacoOcupado(T, p);
        FormaElementar xres = destino.ExternalRealiseComando(Controler.Comandos.cmdTabela, p);
        return xres;
    }

    private FormaElementar CriarTabela(Point pos) {
        Point p = new Point(pos.x, pos.y);
        FormaElementar xres = destino.ExternalRealiseComando(Controler.Comandos.cmdTabela, p);
        return xres;
    }

    public boolean converterEspecializacao() {
        List<Especializacao> lst = origem.getListaDeItens().stream().filter(o -> (o instanceof Especializacao))
                .map(r -> (Especializacao) r).collect(Collectors.toList());
        int i = 0;
        //Remove as epecializações mal formadas.
        while (i < lst.size()) {
            Especializacao Esp = lst.get(i);
            if (Esp.LigadaAoPontoPrincipal() == null || Esp.getListaDePontosLigados().size() < 2) {
                Opcoes.Erros.add(util.Utilidades.EncapsuleMsg("msg25", new Object[]{Esp.getID()}));
                lst.remove(Esp);
                continue;
            }
            i++;
        }
        i = 0;
        //# Verifica se a especialização tem uma entidade principal que é especializada de outra especialização: 
        //# se não especializada -> processa; se é especializada mas já se processou a especialização superior -> precessa; se não: continue em busca da especialização superior.
        while (lst.size() > 0 && i < lst.size()) {
            //se i >= lst.size() -> cíclico!
            Especializacao Esp = lst.get(i);
            PreEntidade entP = Esp.LigadaAoPontoPrincipal();
            boolean tmpContinue = true;
            List<Especializacao> tmp = getEspecializacaoDeEspecializada(entP, Esp);
            for (Especializacao espTmp : tmp) {
                if (lst.indexOf(espTmp) != -1) {
                    tmpContinue = false;
                    break;
                }
            }
            if (tmpContinue) {
                if (!converterEspecializacaoProcesse(Esp, entP)) {
                    return false;
                }
                lst.remove(Esp);
                i = 0;
            } else {
                i++;
            }
        }

        return true;
    }

    private List<Especializacao> getEspecializacaoDeEspecializada(PreEntidade ent, Especializacao exceto) {
        return ent.getListaDeFormasLigadas().stream().filter(f -> (f instanceof Especializacao) && (f != exceto))
                .map(f -> (Especializacao) f)
                .filter(e -> e.LigadaAoPontoPrincipal() != ent)
                .collect(Collectors.toList());
    }

    private HashMap<Tabela, Campo> campoTipoJaSetado = new HashMap<>();

    private boolean converterEspecializacaoProcesse(Especializacao Esp, PreEntidade entP) {
        List<PreEntidade> lst = Esp.getListaDeFormasLigadas().stream().filter(o -> (o instanceof PreEntidade) && o != entP)
                .map(pr -> (PreEntidade) pr).collect(Collectors.toList());
        Opcoes.Inicializar();
        Opcoes.Textos.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg26") + " "
                + (Esp.isParcial() ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg27") + " " : (Esp.isTotal() ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg28") + " " : ""))
                + (Esp.isExclusiva() ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg29") : (Esp.isNaoExclusiva() ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg30") : ""))
        );
        if (lst.size() == 1) {
            Opcoes.Textos.add(util.Utilidades.EncapsuleMsg("msg31", entP.getTexto()));
        } else {
            Opcoes.Textos.add(util.Utilidades.EncapsuleMsg("msg32", new Object[]{lst.size(), entP.getTexto()}));
        }
        AddObservacoes(Esp, false);
        Opcoes.opcDefault = (Esp.isParcial() ? 0 : 1);

        Opcoes.Questoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg33"));
        Opcoes.Questoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg34"));
        if (entP.getListaDeFormasLigadas().stream().filter(e -> e instanceof Especializacao)
                .map(f -> (Especializacao) f)
                .filter(e -> e.LigadaAoPontoPrincipal() == entP)
                .count() == 1) {
            Opcoes.Questoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg35")
                    + (Esp.isParcial() ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg36") : ""));
        } else {
            Opcoes.Textos.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg37"));
        }

        Opcoes.obj = Esp;

        //# para cada PreEnt em list, verificar se é especilizada de herança multipla, se sim, desabilita as opções 1.
        for (PreEntidade pre : lst) {
            if ((pre.getListaDeFormasLigadas().stream().filter(e -> e instanceof Especializacao)
                    .map(f -> (Especializacao) f)
                    .filter(e -> e.LigadaAoPontoPrincipal() != pre)
                    .count() > 1)) {
                Opcoes.opcDefault = 0;
                Opcoes.Disables.add(1);
                Opcoes.Textos.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg38"));
                break;
            }
        }

        if (!Questione()) {
            return false;
        }

        Links.getLigadosOrigem(entP).stream().filter(o -> o instanceof Tabela).map(o -> (Tabela) o).forEach(principal -> {

            List<Tabela> secundarias = new ArrayList<>();
            lst.stream().forEach(pre -> {
                Links.getLigadosOrigem(pre).stream().filter(o -> o instanceof Tabela).map(o -> (Tabela) o).forEach(t -> {
                    secundarias.add(t);
                });
            });

            final String pretx = (util.Utilidades.IsUpper(principal.getTexto()) ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix").toLowerCase()) + principal.getTexto() + "_";
            int opc = Opcoes.OPC;
            switch (opc) {
                case 0:
                    secundarias.forEach(s -> {
                        setOrigemLigacao(
                                LinkTable(principal, s, 0, 1),
                                s);
                        ImportaCampoChaveKFK(principal, s, pretx);
                    });
                    break;
                case 1:
                    String st = "";
                    secundarias.stream().filter(s -> s != principal).map(s -> ", " + s.getTexto()).reduce(st, String::concat);
                    secundarias.stream().filter(s -> s != principal).forEach(s -> {
                        MoverLigacoes(s, principal);

                        List<Campo> oldkeys = principal.getCampos().stream().filter(cam -> cam.isKey()).collect(Collectors.toList());

                        ImportaCampo(s, principal);

                        NormaliseImportacaoKeys(principal, oldkeys, secundarias, pretx);

                        TroqueLinksDestino(s, principal);
                        this.destino.Remove(s, true);
                    });
                    if (Esp.isExclusiva()) {
                        if (!campoTipoJaSetado.containsKey(principal)) {
                            Campo c = principal.Add("");
                            String ax = removerCaracteresEspeciais(principal.getTexto());
                            c.setTexto(ax + Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.conceitual.sufixo.tipo"));
                            c.setTipo(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg15"));
                            c.setObservacao(util.Utilidades.EncapsuleMsg("msg39", principal.getTexto()));
                            campoTipoJaSetado.put(principal, c);
                        }
                        campoTipoJaSetado.get(principal).setObservacao(campoTipoJaSetado.get(principal).getObservacao() + st);
                    }
                    break;
                case 2:
                    principal.getListaDePontosLigados().stream().map(p -> (LogicoLinha) p.getDono()).forEach(L -> {
                        Tabela ori = (Tabela) L.getOutraPonta(principal);
                        secundarias.stream().filter(s -> s != principal && ori != s).forEach(s -> {
                            int c1 = L.getCardA().CardToInt();
                            int c2 = L.getCardB().CardToInt();
                            if (L.getFormaPontaA() != principal) {
                                c2 = L.getCardA().CardToInt();
                                c1 = L.getCardB().CardToInt();
                            }
                            setOrigemLigacao(
                                    LinkTable(ori, s, c2, c1),
                                    s);
                        });
                        this.destino.Remove(L, true);
                    });

                    //# Listo e removo todos cujo destino era Principal (que será removida).
                    List<conversorLink.par> destEqPrinc = Links.Lista.stream().filter(p -> p.destino == principal).collect(Collectors.toList());
                    destEqPrinc.forEach(dp -> {
                        Links.Lista.remove(dp);
                    });
                    secundarias.stream().filter(s -> s != principal).forEach(s -> {
                        ImportaCampo(principal, s);
                        //# altero o destino para que aqueles que antes apontavam para principal, passem a apontar para as tabelas restantes. 
                        destEqPrinc.forEach(dp -> {
                            Links.Add(dp.origem, s);
                        });
                        //# Adiciono um link para a entidade principla desta especialização.
                        Links.Add(entP, s);
                    });
                    this.destino.Remove(principal, true);
                    Links.RemovePar(entP, principal);

                    break;
            }
        });
        return true;
    }

    private boolean MoverLigacoes(Tabela origem, Tabela destino) {
        if (origem == destino || origem == null || destino == null) {
            return false;
        }
        List<PontoDeLinha> lst = origem.getListaDePontosLigados().stream().collect(Collectors.toList());

        while (lst.size() > 0) {
            PontoDeLinha pt = lst.get(0);
            int ld = pt.getLado();
            Point p = new Point();
            switch (ld) {
                case 0:
                    p = new Point(destino.getLeft() + 2, destino.getTop() + destino.getHeight() / 2);
                    break;
                case 1:
                    p = new Point(destino.getLeft() + destino.getWidth() / 2, destino.getTop() + 2);
                    break;
                case 2:
                    p = new Point(destino.getLeftWidth() - 2, destino.getTop() + destino.getHeight() / 2);
                    break;
                case 3:
                    p = new Point(destino.getLeft() + destino.getWidth() / 2, destino.getTopHeight() - 2);
                    break;
            }
            pt.setCentro(p);
            pt.SetEm(destino);
            pt.getDono().OrganizeLinha();
            pt.getDono().reSetBounds();
            lst.remove(pt);
        }

        RemovaLigacoesIguais(destino);

        return true;
    }

    private boolean CloneLigacoes(Tabela origem, Tabela destino) {
        if (origem == destino || origem == null || destino == null) {
            return false;
        }
        List<LogicoLinha> ligs = origem.getListaDeLigacoes().stream().map(L -> (LogicoLinha) L).collect(Collectors.toList());
        ligs.stream().forEach(lig -> {
            int ca = lig.getCardA().CardToInt();
            int cb = lig.getCardB().CardToInt();
            LogicoLinha lin;
            if (lig.getFormaPontaA() == origem) {
                Tabela tmp = (Tabela) (lig.getFormaPontaB());
                lin = LinkTable(destino, tmp, ca, cb);
            } else {
                Tabela tmp = (Tabela) (lig.getFormaPontaA());
                lin = LinkTable(tmp, destino, cb, ca);
            }
            if (origemLigacao.containsKey(lig)) {
                setOrigemLigacao(lin, destino);
            }
        });

        RemovaLigacoesIguais(destino);

        return true;
    }

    private void RemovaLigacoesIguais(Tabela destino1) {
        //# Remover ligações ambiguas: duas ligações ligando as mesmas tabelas com as mesmas cardinalidades. Uma deve ser removida.
        List<LogicoLinha> tmpLst1 = destino1.getListaDePontosLigados().stream().map(p -> ((LogicoLinha) p.getDono())).collect(Collectors.toList());
        List<LogicoLinha> ja = new ArrayList<>();
        tmpLst1.forEach(Linha -> {
            tmpLst1.stream().filter((lin) -> ((lin != Linha) && (ja.indexOf(Linha) == -1 && ja.indexOf(lin) == -1) && (LigacoeIguais(Linha, lin)))).forEach(L -> {
                this.destino.Remove(L, true);
                ja.add(L);
                //ja.add(Linha);
            });
        });
    }

    private void TroqueLinksDestino(Tabela de, Tabela para) {
        if (de == para || de == null || para == null) {
            return;
        }
        Links.Lista.stream().filter(p -> p.destino == de).forEach(p -> p.destino = para);
    }

    public boolean converterRelacionamento() {
        List<Relacionamento> lst = origem.getListaDeItens().stream().filter(o -> (o instanceof Relacionamento))
                .map(r -> (Relacionamento) r).collect(Collectors.toList());

        origem.getListaDeItens().stream().filter(o -> (o instanceof EntidadeAssociativa)).map(ea -> (Relacionamento) ((EntidadeAssociativa) ea).getInterno()).forEach(r -> {
            lst.add(r);
        });

        int j = 0;
        //# Remove as relações mal formadas e auto-relacionamentos.
        while (j < lst.size()) {
            Relacionamento re = lst.get(j);
            if (re.isAutoRelacionamento()) {
                lst.remove(re);
                continue;
            }
            if (re.getListaDeFormasLigadas().stream().filter(f -> f instanceof PreEntidade).map(f -> (PreEntidade) f).count() < 2) {
                Opcoes.Erros.add(util.Utilidades.EncapsuleMsg("msg40", re.getTexto()));
                lst.remove(re);
                continue;
            }
            j++;
        }

        for (Relacionamento re : lst) {
            List<Ligacao> ligacoes = re.getListaDePontosLigados().stream().filter(p -> p.getDono().getOutraPonta(re) instanceof PreEntidade)
                    .map(p -> (Ligacao) p.getDono()).collect(Collectors.toList());

            int tl = Math.toIntExact(ligacoes.stream().filter(L -> L.getCard().getCard() == PreCardinalidade.TiposCard.C11).count());
            boolean tudocard0 = (tl == ligacoes.size());
            if (tudocard0) {
                if (!converterRelacionamentoMerge(re, ligacoes.stream().map(L -> (PreEntidade) L.getOutraPonta(re)).collect(Collectors.toList()))) {
                    return false;
                } else {
                    continue;
                }
            }
            if (ligacoes.size() > 2) {
                //# ternário ou maior?
                if (!converterRelacionamentoTernario(re, ligacoes)) {
                    return false;
                }
            } else {
                //# binário.
                if (!converterRelacionamentoBinario(re, ligacoes)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean converterEntidadeAssossiativa() {
        origem.getListaDeItens().stream().filter(o -> (o instanceof EntidadeAssociativa))
                .map(r -> (EntidadeAssociativa) r).forEach(entP -> {

            Tabela principal = Links.getLigadosOrigem(entP).stream().filter(o -> o instanceof Tabela).map(o -> (Tabela) o).findFirst().orElse(null);
            Tabela secundaria = Links.getLigadosOrigem(entP.getInterno()).stream().filter(o -> o instanceof Tabela).map(o -> (Tabela) o).findFirst().orElse(null);

            MoverLigacoes(secundaria, principal);
            ImportaCampo(secundaria, principal);
            TroqueLinksDestino(secundaria, principal);
            String ax = removerCaracteresEspeciais(principal.getTexto() + "_" + secundaria.getTexto());
            principal.SetTexto(ax);

            this.destino.Remove(secundaria, true);
        });
        return true;
    }

    private boolean converterRelacionamentoMerge(Relacionamento re, List<PreEntidade> lst) {
        final List<Tabela> tabelas = new ArrayList<>();
        lst.stream().forEach(pree -> {
            Links.getLigadosOrigem(pree).stream().filter(fe -> fe instanceof Tabela).map(fe -> (Tabela) fe).forEach(t -> {
                if (tabelas.indexOf(t) == -1) {
                    tabelas.add(t);
                }
            });
        });
        if (tabelas.size() < 2) {
            if (tabelas.size() == 1) {
                return recebaEConvertaAtributos(re, tabelas.get(0), re.getListaDeFormasLigadas().stream().filter(f -> f instanceof Atributo)
                        .map(f -> (Atributo) f).collect(Collectors.toList()));
            }
            return true;
        }
        Tabela prin = tabelas.get(0);
        tabelas.remove(0);

        re.getListaDeFormasLigadas().stream().filter(f -> f instanceof Atributo).map(f -> (Atributo) f).forEach(a -> {
            ImportaComoCampo(prin, a);
        });
        int x = prin.getLeft(), y = prin.getTop();
        for (Tabela t : tabelas) {
            TroqueLinksDestino(t, prin);
            if (MoverLigacoes(t, prin)) {
                ImportaCampo(t, prin);
            }
            this.destino.Remove(t, true);
            x += t.getLeft();
            y += t.getTop();
            String ax = removerCaracteresEspeciais(prin.getTexto() + "_" + t.getTexto());
            prin.SetTexto(ax);
        }
        x /= (tabelas.size() + 1);
        y /= (tabelas.size() + 1);

        prin.DoMove(x - prin.getLeft(), y - prin.getTop());
        Links.Add(re, prin);
        return true;
    }

    private boolean converterRelacionamentoTernario(Relacionamento re, List<Ligacao> ligacoes) {
        final List<Tabela> tabelas = new ArrayList<>();
        final List<PreCardinalidade.TiposCard> cards = new ArrayList<>();

        ligacoes.stream().forEach(L -> {
            PreEntidade pre = (PreEntidade) L.getOutraPonta(re);
            Links.getLigadosOrigem(pre).stream().filter(fe -> fe instanceof Tabela).map(fe -> (Tabela) fe).forEach(t -> {
                if (tabelas.indexOf(t) == -1) {
                    tabelas.add(t);
                    cards.add(L.getCard().getCard());
                }
            });
        });

        if (tabelas.size() < 2) {
            if (tabelas.size() == 1) {
                return recebaEConvertaAtributos(re, tabelas.get(0), re.getListaDeFormasLigadas().stream().filter(f -> f instanceof Atributo)
                        .map(f -> (Atributo) f).collect(Collectors.toList()));
            }
            return true;
        }

        Tabela prin = (Tabela) CriarTabela(re.getLocation());
        String ax = removerCaracteresEspeciais(re.getTexto());
        prin.SetTexto(ax);
        for (int i = 0; i < tabelas.size(); i++) {
            Tabela t = tabelas.get(i);
            PreCardinalidade.TiposCard card = cards.get(i);
            PreCardinalidade.TiposCard card2 = ((cards.get(i) == PreCardinalidade.TiposCard.C01 || cards.get(i) == PreCardinalidade.TiposCard.C0N) ? PreCardinalidade.TiposCard.C01 : PreCardinalidade.TiposCard.C11);

            setOrigemLigacao(
                    LinkTable(prin, t, card2.ordinal(), card.ordinal()),
                    prin);
            ImportaCampoChave(t, prin, t.getTexto() + "_");
            ax = removerCaracteresEspeciais(prin.getTexto() + "_" + t.getTexto());
            prin.setTexto(ax);
        }
        Links.Add(re, prin);
        return true;
    }

    private boolean converterRelacionamentoBinario(Relacionamento re, List<Ligacao> ligacoes) {

        final List<Tabela> tabelasO = new ArrayList<>();
        final List<Tabela> tabelasD = new ArrayList<>();

        final List<PreCardinalidade.TiposCard> cards = new ArrayList<>();
        final List<PreEntidade> entidades = new ArrayList<>();

        ligacoes.stream().filter(L -> L.getOutraPonta(re) instanceof PreEntidade).forEach(L -> {
            entidades.add((PreEntidade) L.getOutraPonta(re));
            cards.add(L.getCard().getCard());
        });

        Links.getLigadosOrigem(entidades.get(0)).stream().filter(fe -> fe instanceof Tabela).map(fe -> (Tabela) fe).forEach(t -> {
            if (tabelasO.indexOf(t) == -1) {
                tabelasO.add(t);
            }
        });

        Links.getLigadosOrigem(entidades.get(1)).stream().filter(fe -> fe instanceof Tabela).map(fe -> (Tabela) fe).forEach(t -> {
            if (tabelasD.indexOf(t) == -1) {
                tabelasD.add(t);
            }
        });

        if (tabelasO.isEmpty() || tabelasD.isEmpty()) {
            if (tabelasO.isEmpty() && tabelasD.isEmpty()) {
                return true;
            }
            return recebaEConvertaAtributos(re, tabelasO.isEmpty() ? tabelasD.get(0) : tabelasO.get(0), re.getListaDeFormasLigadas().stream().filter(f -> f instanceof Atributo)
                    .map(f -> (Atributo) f).collect(Collectors.toList()));
        }

        final int card1;
        final int card2;
        final PreEntidade ent1;
        final PreEntidade ent2;
        final List<Tabela> tabs_origem;
        final List<Tabela> tabs_destino;
        

        if (cards.get(1).ordinal() < cards.get(0).ordinal()) {
            card1 = cards.get(1).ordinal();
            card2 = cards.get(0).ordinal();
            ent2 = entidades.get(0);
            ent1 = entidades.get(1);
            tabs_destino = tabelasO;
            tabs_origem = tabelasD;
        } else {
            tabs_destino = tabelasD;
            tabs_origem = tabelasO;
            card1 = cards.get(0).ordinal();
            card2 = cards.get(1).ordinal();
            ent1 = entidades.get(0);
            ent2 = entidades.get(1);
        }
        List<Atributo> attrs = re.getListaDeFormasLigadas().stream().filter(f -> f instanceof Atributo)
                .map(f -> (Atributo) f).collect(Collectors.toList());

        if ((card1 == 0 && card2 == 0) || (card1 < 2 && card2 > 1) || (card1 == 0 && card2 == 1)) {
            Opcoes.Inicializar();
            Opcoes.Textos.add(util.Utilidades.EncapsuleMsg("msg41", PreCardinalidade.CardToString(card1), PreCardinalidade.CardToString(card2)));
            Opcoes.Textos.add(util.Utilidades.EncapsuleMsg("msg20", ent1.getTexto(), ent2.getTexto())
                    + util.Utilidades.EncapsuleMsg("msg42", re.getTexto()));

            AddObservacoes(re, true);
            Opcoes.opcDefault = 0;

            Opcoes.Questoes.add(util.Utilidades.EncapsuleMsg("msg43", ent1.getTexto(), ent2.getTexto()));
            if (card2 == 0) {
                Opcoes.Questoes.add(util.Utilidades.EncapsuleMsg("msg43", ent2.getTexto(), ent1.getTexto()));
            } else if (card2 == 1) {
                Opcoes.Questoes.add(util.Utilidades.EncapsuleMsg("msg46", ent1.getTexto(), ent2.getTexto()));
            }
            Opcoes.Questoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg47"));
            Opcoes.obj = re;
            if (!Questione()) {
                return false;
            }
            if (Opcoes.OPC == 0 || (Opcoes.OPC == 1 && card2 == 0)) {
                
                if (Opcoes.OPC == 0) {
                    final String pf = util.Utilidades.IsUpper(ent1.getTexto()) ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix").toLowerCase();
                    tabs_origem.forEach(tab1 -> {
                        tabs_destino.forEach(tab2 -> {
                            if (tab1 != tab2) {
                                ImportaCampoChave(tab1, tab2, pf + ent1.getTexto() + "_");
                                setOrigemLigacao(
                                        LinkTable(tab1, tab2, card1, card2),
                                        tab2);
                                Links.Add(re, tab2);
                            } else {
                                // # Como se fosse um altorelacionamento
                                AdicionarChaveEstrangeira(tab1, tab1, null);
                            }
                        });
                    });
                } else {
                    final String pf = util.Utilidades.IsUpper(ent2.getTexto()) ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix").toLowerCase();
                    tabs_destino.forEach(tab2 -> {
                        tabs_origem.forEach(tab1 -> {
                            if (tab1 != tab2) {
                                ImportaCampoChave(tab2, tab1, pf + ent2.getTexto() + "_");
                                setOrigemLigacao(
                                        LinkTable(tab2, tab1, card2, card1),
                                        tab1);
                                Links.Add(re, tab1);
                            } else {
                                // # Como se fosse um altorelacionamento
                                AdicionarChaveEstrangeira(tab1, tab1, null);
                            }
                        });
                    });
                }

                int oldopc = Opcoes.OPC;
                int continuo = (card2 == 0) ? -1 : 3;

                int tl = 0;
                for (Atributo a : attrs) {
                    if (continuo < 2) {
                        Opcoes.Inicializar();
                        Opcoes.opcDefault = oldopc;
                        Opcoes.Textos.add(util.Utilidades.EncapsuleMsg("msg44", a.getTexto(), re.getTexto()));
                        AddObservacoes(a, true);
                        Opcoes.Questoes.add(util.Utilidades.EncapsuleMsg("msg45", ent2.getTexto()));
                        Opcoes.Questoes.add(util.Utilidades.EncapsuleMsg("msg63", ent1.getTexto()));
                        if (attrs.size() > 1 && tl < attrs.size() - 1) {
                            Opcoes.Questoes.add(util.Utilidades.EncapsuleMsg("msg64", ent2.getTexto()));
                            Opcoes.Questoes.add(util.Utilidades.EncapsuleMsg("msg64", ent1.getTexto()));
                            Opcoes.opcDefault = oldopc + 2;
                        }
                        Opcoes.obj = a;
                        if (!Questione()) {
                            return false;
                        }
                        continuo = Opcoes.OPC;
                    }
                    tl++;
                    List<Atributo> tmp = new ArrayList<>();
                    tmp.add(a);

                    if (continuo == 0 || continuo == 2) {
                        if (!tabs_origem.stream().noneMatch((tab1) -> (!recebaEConvertaAtributos(re, tab1, tmp)))) {
                            return false;
                        }
                    } else {
                        if (!tabs_destino.stream().noneMatch((tab2) -> (!recebaEConvertaAtributos(re, tab2, tmp)))) {
                            return false;
                        }
                    }
//# Bug da versão 2.0 - Os atributos devem ser movido para a tabela de maior cardinalidade.                     
//                    if (continuo == 0 || continuo == 2) {
//                        if (!tabs_destino.stream().noneMatch((tab2) -> (!recebaEConvertaAtributos(re, tab2, tmp)))) {
//                            return false;
//                        }
//                    } else {
//                        if (!tabs_origem.stream().noneMatch((tab1) -> (!recebaEConvertaAtributos(re, tab1, tmp)))) {
//                            return false;
//                        }
//                    }
                }

                return true;
            } else if (Opcoes.OPC == 1 && card2 == 1) {
                return converterRelacionamentoMerge(re, entidades);
            }
        }

        Tabela prin = (Tabela) CriarTabela(re.getLocation());
        String ax = removerCaracteresEspeciais(re.getTexto().isEmpty() ? ent1.getTexto() + "_" + ent2.getTexto() : re.getTexto());
        prin.SetTexto(ax);
        
        final String pf = util.Utilidades.IsUpper(prin.getTexto()) ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix").toLowerCase();
        
        ArrayList<Tabela> jaLigada = new ArrayList<>();
        for (Tabela t : tabs_origem) {
            int c2 = card1;
            int c1 = 1; //(0,1)
            if (c2 == 0 || c2 == 2) {
                c1 = 0; //(1,1)
            }
            if (prin != t) {
                setOrigemLigacao(
                        LinkTable(prin, t, c2, c1),
                        prin);
                jaLigada.add(t);
            }
            ImportaCampoChave(t, prin, pf + t.getTexto() + "_");
        }
        for (Tabela t : tabs_destino) {
            int c2 = card2;
            int c1 = 1; //(0,1)
            if (c2 == 0 || c2 == 2) {
                c1 = 0; //(1,1)
            }
            if (jaLigada.indexOf(t) > -1) {
                final LogicoLinha lig = LinkTable(prin, t, c2, c1);
                setOrigemLigacao(lig, prin);
                // # Um duplo relacionamento entre a nova tabela e a existente! 
                AdicionarChaveEstrangeira(prin, t, lig);
            } else {
                if (prin != t) {
                    setOrigemLigacao(LinkTable(prin, t, c2, c1), prin);
                }
                ImportaCampoChave(t, prin, pf + t.getTexto() + "_");
            }
        }

        if (!recebaEConvertaAtributos(re, prin, attrs)) {
            return false;
        }
        Links.Add(re, prin);

        return true;
    }

    HashMap<Constraint, LogicoLinha> directFK = new HashMap<>();
    HashMap<Constraint, Tabela> directPK = new HashMap<>();

    private void AdicionarChaveEstrangeira(Tabela tab_recebedora, Tabela tab_origen_PK, final LogicoLinha lig) {
        final Constraint nova_fk = new Constraint(tab_recebedora);
        nova_fk.setTipo(Constraint.CONSTRAINT_TIPO.tpFK);

        List<Campo> camposKey = tab_origen_PK.getCampos().stream().filter(cm -> cm.isKey()).collect(Collectors.toList());

        final String pf = util.Utilidades.IsUpper(tab_recebedora.getTexto()) ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix").toLowerCase();

        camposKey.forEach(C -> {
            Campo c = ImportaCampoIgnoreConstrais(tab_recebedora, C, pf + tab_origen_PK.getTexto() + "_");
            c.SetFkey(true);
            nova_fk.Add(C, c);
        });
        directFK.put(nova_fk, lig);
        directPK.put(nova_fk, tab_origen_PK);
    }

    private void AddObservacoes(Forma re, boolean origem) {
        Opcoes.Textos.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg08") + " " + (re.getObservacao().isEmpty() ? "["
                + Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg09") + "]" : re.getObservacao()) + (origem ? " - " + Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg10") + " " + re.getTexto() : ""));
    }

    private boolean LigacoeIguais(LogicoLinha Lo, LogicoLinha Ld) {
        return (((Lo.getFormaPontaA() == Ld.getFormaPontaA())
                && (Lo.getFormaPontaB() == Ld.getFormaPontaB())
                && (Lo.getCardA().getCard() == Ld.getCardA().getCard())
                && (Lo.getCardB().getCard() == Ld.getCardB().getCard())))
                || (((Lo.getFormaPontaA() == Ld.getFormaPontaB())
                && (Lo.getFormaPontaB() == Ld.getFormaPontaA())
                && (Lo.getCardA().getCard() == Ld.getCardB().getCard())
                && (Lo.getCardB().getCard() == Ld.getCardA().getCard())));
    }

    public boolean exportarAssessorios() {
        List<Texto> lst_tex = origem.getListaDeItens().stream().filter(o -> (o instanceof Texto))
                .map(r -> (Texto) r).collect(Collectors.toList());
        List<Desenhador> lst_des = origem.getListaDeItens().stream().filter(o -> (o instanceof Desenhador))
                .map(r -> (Desenhador) r).collect(Collectors.toList());
        List<Legenda> lst_leg = origem.getListaDeItens().stream().filter(o -> (o instanceof Legenda))
                .map(r -> (Legenda) r).collect(Collectors.toList());

        Forma obj = null;
        if (!lst_tex.isEmpty()) {
            obj = lst_tex.get(0);
        } else if (!lst_des.isEmpty()) {
            obj = lst_des.get(0);
        } else if (!lst_leg.isEmpty()) {
            obj = lst_leg.get(0);
        }

        if (obj == null) {
            return true;
        }

        Opcoes.Inicializar();
        Opcoes.Textos.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg65"));

        Opcoes.opcDefault = 0;

        String tmp = (lst_tex.isEmpty() ? "" : String.valueOf(lst_tex.size()) + " " + (lst_tex.size() == 1 ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg66") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg67")));
        tmp += (lst_des.isEmpty() ? "" : (tmp.isEmpty() ? " " : ", ") + String.valueOf(lst_des.size()) + " " + (lst_des.size() == 1 ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg68") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg69")));
        tmp += (lst_leg.isEmpty() ? "" : (tmp.isEmpty() ? " " : ", ") + String.valueOf(lst_leg.size()) + " " + (lst_leg.size() == 1 ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg70") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg71")));

        Opcoes.Questoes.add(util.Utilidades.EncapsuleMsg("msg72", tmp));
        Opcoes.Questoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg73"));
        Opcoes.obj = obj;

        if (!Questione()) {
            return false;
        }
        if (Opcoes.OPC == 0) {
            origem.ClearSelect();
            origem.getListaDeItens().stream().filter(o -> (o instanceof Texto) || (o instanceof Desenhador) || (o instanceof Legenda)).forEach(ass -> {
                origem.DiagramaDoSelecao(ass, false, true);
            });
            origem.doCopy();
            destino.doPaste();
            origem.ClearSelect();
            destino.ClearSelect();
        }
        return true;
    }

    public boolean converterUniao() {
        List<Uniao> unioes = origem.getListaDeItens().stream().filter(o -> (o instanceof Uniao))
                .map(r -> (Uniao) r).collect(Collectors.toList());
        int i = 0;
        //Remove as uniões mal formadas.
        while (i < unioes.size()) {
            Uniao U = unioes.get(i);
            if (U.LigadaAoPontoPrincipal() == null || U.getListaDePontosLigados().size() < 2) {
                Opcoes.Erros.add(util.Utilidades.EncapsuleMsg("msg74", new Object[]{U.getID()}));
                unioes.remove(U);
                continue;
            }
            i++;
        }

        for (Uniao U : unioes) {
            //unioes.forEach(U -> {
            PreEntidade entP = U.LigadaAoPontoPrincipal();
            List<PreEntidade> lst = U.getListaDeFormasLigadas().stream().filter(o -> (o instanceof PreEntidade) && o != entP)
                    .map(pr -> (PreEntidade) pr).collect(Collectors.toList());

            Opcoes.Inicializar();
            Opcoes.Textos.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.uniao.uniao") + " " + util.Utilidades.EncapsuleMsg("msg32", new Object[]{lst.size(), entP.getTexto()}));
            AddObservacoes(U, false);

            Opcoes.opcDefault = 0;

            Opcoes.Questoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg33"));
            Opcoes.Questoes.add(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msg34"));

            Opcoes.obj = U;

            if (!Questione()) {
                return false;
            }

            int opc = Opcoes.OPC;
            switch (opc) {
                case 0:
                    Links.getLigadosOrigem(entP).stream().filter(o -> o instanceof Tabela).map(o -> (Tabela) o).forEach(principal -> {

                        List<Campo> oldkeys = principal.getCampos().stream().filter(cam -> cam.isKey()).collect(Collectors.toList());
                        List<Tabela> secundarias = new ArrayList<>();
                        final String prefx = util.Utilidades.IsUpper(principal.getTexto()) ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix").toLowerCase()+ principal.getTexto() + "_";

                        lst.stream().forEach(pre -> {
                            Links.getLigadosOrigem(pre).stream().filter(o -> o instanceof Tabela).map(o -> (Tabela) o).forEach(s -> {
                                setOrigemLigacao(LinkTable(principal, s, -1, 0), s);
                                ImportaCampoChave(principal, s, prefx);

                                secundarias.add(s);

                            });
                        });

                        NormaliseImportacaoKeys(principal, oldkeys, secundarias, prefx);

                    });
                    break;
                case 1:
                    lst.stream().forEach(pre -> {
                        List<Tabela> secundarias = Links.getLigadosOrigem(pre).stream().filter(o -> o instanceof Tabela).map(o -> (Tabela) o).collect(Collectors.toList());
                        Links.getLigadosOrigem(entP).stream().filter(o -> o instanceof Tabela).map(o -> (Tabela) o).forEach(principal -> {
                            List<Campo> oldkeys = principal.getCampos().stream().filter(cam -> cam.isKey()).collect(Collectors.toList());
                            secundarias.forEach(s -> {

                                CloneLigacoes(s, principal);

                                ImportaCampo(s, principal);

                                //TroqueLinksDestino(s, principal);
                                //# Listo e removo todos cujo destino era s (que será removida).
                                List<conversorLink.par> destEqPrinc = Links.Lista.stream().filter(p -> p.destino == s).collect(Collectors.toList());
                                destEqPrinc.forEach(dp -> {
                                    Links.Lista.remove(dp);
                                });
                                //# altero o destino para que aqueles que antes apontavam para s, passem a apontar para principal. 
                                destEqPrinc.forEach(dp -> {
                                    Links.Add(dp.origem, principal);
                                });

                            });

                            final String pretx = util.Utilidades.IsUpper(principal.getTexto()) ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix").toLowerCase()  + principal.getTexto() + "_";

                            NormaliseImportacaoKeys(principal, oldkeys, secundarias, pretx);
                        });
                        secundarias.forEach(s -> {
                            this.destino.Remove(s, true);
                            List<LogicoLinha> tmpLst1 = s.getListaDePontosLigados().stream().map(p -> ((LogicoLinha) p.getDono())).collect(Collectors.toList());
                            tmpLst1.forEach(L -> {
                                this.destino.Remove(L, true);
                            });
                        });
                    });
                    break;
            }
        }
        return true;
    }

    private void NormaliseImportacaoKeys(Tabela principal, List<Campo> oldkeys, List<Tabela> secundarias, final String pretx) {
        List<Campo> newkeys = principal.getCampos().stream().filter(cam -> cam.isKey() && oldkeys.indexOf(cam) == -1).collect(Collectors.toList());
        if (!newkeys.isEmpty()) {
            final List<Tabela> tb_fks = new ArrayList<>();
            camposOrigem.keySet().stream().forEach(dest -> {
                Campo ori = camposOrigem.get(dest);
                if (oldkeys.indexOf(ori) > -1) {
                    Tabela tbl = dest.getTabela();
                    if (secundarias.indexOf(tbl) == -1 && tb_fks.indexOf(tbl) == -1) {
                        tb_fks.add(tbl);
                    }
                }
            });
            if (!tb_fks.isEmpty()) {
                newkeys.stream().forEach(a -> {
                    tb_fks.forEach(Tab -> {
                        Campo c = Tab.Add("_");
                        String ax = removerCaracteresEspeciais(pretx + a.getTexto());
                        c.setTexto(ax);
                        c.setTipo(a.getTipo());

                        c.setFkey(true);
                        setCampoOrigem(c, a);

                        c.setObservacao(a.getObservacao());
                        c.setDicionario(a.getDicionario());
                    });
                });
            }
        }
    }

    /**
     * Algumas tabelas são excluídas no processo de conversão e as FK precisam atender ao novo nome. Só é chamada no fim da conversão.
     */
    private void renomeieFKs() {
        destino.getListaDeTabelas().stream().forEach(tt -> {
            ArrayList<String> nm = new ArrayList<>();
            
            final String pf = util.Utilidades.IsUpper(tt.getTexto()) ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix").toLowerCase();

            tt.getCampos().stream().filter(C -> C.isFkey() && C.getCampoOrigem() != null).forEach(C -> {
                String ax = removerCaracteresEspeciais(pf + C.getTabelaOrigem().getTexto() + "_" + C.getCampoOrigem().getTexto());
                while (nm.indexOf(ax) > -1) {
                    ax += "_";
                }
                nm.add(ax);
                C.setTexto(ax);
            });
            if (frutoAutoRelacionamento.indexOf(tt) > -1) {
                ArrayList<String> ja = new ArrayList<>();
                tt.getCampos().stream().forEach(cmp -> {
                    if (ja.indexOf(cmp.getTexto()) > -1) {
                        cmp.setTexto(cmp.getTexto() + "_" + tt.getTexto());
                    } else {
                        ja.add(cmp.getTexto());
                    }
                });
            }
        });
    }

    private String removerCaracteresEspeciais(String original) {
        if (removerEspecial) {
            return util.Utilidades.textoParaCampo(original);
        }
        return original;
    }

    HashMap<Campo, Campo> camposOrigem = new HashMap<>();

    private void setCampoOrigem(Campo cmp_prin, Campo cmp_origem) {
        camposOrigem.put(cmp_prin, cmp_origem);
    }

    private Campo getCampoOrigem(Campo cmp_prin) {
        return camposOrigem.get(cmp_prin);
    }

    /**
     * Grava qual tabela será receberá a FK após uma ligação. 
     * //??: TO-DO: Ver se todos as chamadas ao método LinkTable informa corretamente qual a tabela será a FK e ver também se não há outras ligações de tabelas não cobertas!
     */
    HashMap<LogicoLinha, Tabela> origemLigacao = new HashMap<>();

    private void setOrigemLigacao(LogicoLinha linha, Tabela tab) {
        origemLigacao.put(linha, tab);
    }

    public boolean processeConstraints() {
        destino.getListaDeTabelas().forEach(tb -> {
            tb.getCampos().stream().filter(cmp -> cmp.isKey()).forEach(cmp -> {
                tb.direct_ProcesseIrKey(cmp);
            });
        });
        destino.getListaDeTabelas().forEach(tb_fk -> {
            tb_fk.getCampos().stream().filter(cmp -> cmp.isFkey() && getCampoOrigem(cmp) != null).forEach(cmp -> {
                final Campo cmp_ori = getCampoOrigem(cmp);
                Tabela tb_pk = cmp_ori.getTabela();
                Constraint constr_pk = tb_pk.getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpPK).findFirst().orElse(null);
                Constraint constr_fk = tb_fk.getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK)
                        .filter(c -> (c.getConstraintOrigem() == constr_pk)).findFirst().orElse(null);
                if (constr_fk == null) {
                    constr_fk = new Constraint(tb_fk);
                    constr_fk.setTipo(Constraint.CONSTRAINT_TIPO.tpFK);
                }

                LogicoLinha lin = tb_fk.getListaDeLigacoes().stream().filter(L -> L.getOutraPonta(tb_fk) == tb_pk && origemLigacao.get((LogicoLinha) L) == tb_fk)
                        //                LogicoLinha lin = tb_fk.getListaDeLigacoes().stream().filter(L -> L.getOutraPonta(tb_fk) == tb_pk)
                        .map(L -> (LogicoLinha) L).findAny().orElse(null);

                constr_fk.Add(cmp_ori, cmp, lin, constr_pk);
                condicaoIR(lin, tb_fk, constr_fk);
                constr_fk.Valide();
            });
            if (frutoAutoRelacionamento.indexOf(tb_fk) > -1) {
                ProcesseFrutoAutoRel(tb_fk);
            }
        });
        return processeConstraintsDirect();
    }

    /**
     * Acrescenta a RI à FK
     * @param linha
     * @param tab_fk
     * @param IR 
     */
    private void condicaoIR(LogicoLinha linha, Tabela tab_fk, Constraint IR) {
        
        //O banco Oracle não implementa a RI ON UPDATE. Analisamos as razões e verificamos que faz sentido. Comentamos (abaixo) o uso da RI on update!
        
        if (linha == null || tab_fk == null || IR == null) {
            return;
        }
        final String CASCADE = "CASCADE";
        final String SETNULL = "SET NULL";
        final String NOACTION = "NO ACTION";
        final String RESTRICT = "RESTRICT";

        PreCardinalidade.TiposCard cardA;
        PreCardinalidade.TiposCard cardB;

        if (linha.getFormaPontaB() == tab_fk) {
            cardA = linha.getCardA().getCard();
            cardB = linha.getCardB().getCard();
        } else {
            cardA = linha.getCardB().getCard();
            cardB = linha.getCardA().getCard();
        }

        if (cardA == PreCardinalidade.TiposCard.C01) {
            if (cardB == PreCardinalidade.TiposCard.C0N || cardB == PreCardinalidade.TiposCard.C01) {
                IR.setDdlOnDelete(SETNULL);
            } else {
                IR.setDdlOnDelete(CASCADE);
            }
            //IR.setDdlOnUpdate("");
        } else {
            if (cardA == PreCardinalidade.TiposCard.C11) {
                if (cardB == PreCardinalidade.TiposCard.C0N || cardB == PreCardinalidade.TiposCard.C01) {
                    IR.setDdlOnDelete(CASCADE);
                    //IR.setDdlOnUpdate(CASCADE);
                } else {
                    IR.setDdlOnDelete(RESTRICT);
                    //IR.setDdlOnUpdate(RESTRICT);
                }
            } else {
                IR.setDdlOnDelete(NOACTION);
                //IR.setDdlOnUpdate(NOACTION);
            }
        }
    }

    /**
     * Caso em que se precisa de uma segunda chave estrangeira. Será neessário uma nova forma de processamento!
     */
    private boolean processeConstraintsDirect() {
        directFK.keySet().stream().forEach(fk -> {
            Tabela t_pk = directPK.get(fk);
            Constraint constr_pk = t_pk.getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpPK).findFirst().orElse(null);
            if (constr_pk != null) {
                Tabela t_fk = fk.getTabela();
                final String pf = util.Utilidades.IsUpper(t_fk.getTexto()) ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix") : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix").toLowerCase();
                t_pk.getCampos().stream().filter(cm -> cm.isKey() && (fk.getCamposDeOrigem().indexOf(cm) < 0)).forEach(C -> {
                    Campo c = ImportaCampoIgnoreConstrais(t_fk, C, pf + t_pk.getTexto() + "_");
                    c.SetFkey(true);
                    fk.Add(C, c);
                });
                fk.LigacaoDireta(constr_pk, directFK.get(fk));
                condicaoIR(directFK.get(fk), t_fk, fk);
            }
            fk.Valide();
        });
        return true;
    }

    private void ProcesseFrutoAutoRel(Tabela tb_fk) {
        ArrayList<Constraint> ascons = new ArrayList<>(tb_fk.getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK).collect(Collectors.toList()));
        ascons.stream().forEach(constr_fk -> {

            ArrayList<Campo> tmp = new ArrayList<>();
            ArrayList<Campo> replicado = new ArrayList<>();

            for (int i = 0; i < constr_fk.getCamposDeOrigem().size(); i++) {
                Campo co = constr_fk.getCamposDeOrigem().get(i);
                if (tmp.indexOf(co) > -1) {
                    replicado.add(constr_fk.getCamposDeDestino().get(i));
                } else {
                    tmp.add(co);
                }
            }

            if (!replicado.isEmpty()) {
                final Constraint nova_fk = new Constraint(tb_fk);
                nova_fk.setTipo(Constraint.CONSTRAINT_TIPO.tpFK);

                LogicoLinha lin = constr_fk.getLigacao();
                if (lin != null) {
                    //int Card1 = lin.getCardA().CardToInt();
                    int Card2 = lin.getCardB().CardToInt();
                    if (lin.getFormaPontaB() != tb_fk) {
                        Card2 = lin.getCardA().CardToInt();
                        //Card1 = lin.getCardB().CardToInt();
                    }
                    lin = LinkTable(constr_fk.getConstraintOrigem().getTabela(), tb_fk, 0, Card2);
                    setOrigemLigacao(lin, tb_fk);
                }
                final LogicoLinha lig = lin;
                replicado.stream().forEach(rp -> {
                    Campo cmp_ori = constr_fk.getOrigem(rp);
                    constr_fk.RemoveFromDestino(rp);
                    nova_fk.Add(cmp_ori, rp, lig, constr_fk.getConstraintOrigem());
                });
                condicaoIR(lin, tb_fk, nova_fk);
                nova_fk.Valide();
            }
        });
    }
}
