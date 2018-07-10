/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagramas.logico;

import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import controlador.editores.EditorDeCampos;
import controlador.editores.EditorDeIR;
import controlador.editores.EditorDeIrFK;
import controlador.editores.EditorDeIrUnique;
import controlador.editores.EditorDeTipos;
import controlador.inspector.InspectorProperty;
import desenho.Elementar;
import desenho.FormaElementar;
import desenho.formas.Desenhador;
import desenho.formas.Forma;
import desenho.formas.Legenda;
import diagramas.conceitual.Texto;
import java.awt.Frame;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import principal.Aplicacao;
import util.BoxingJava;
import controlador.editores.MostradorDeCodigo;

/**
 *
 * @author ccandido
 */
public class DiagramaLogico extends Diagrama {

    private static final long serialVersionUID = 5042110897774875283L;

    public DiagramaLogico(Editor omaster) {
        super(omaster);
        setTipo(TipoDeDiagrama.tpLogico);

        meusComandos.add(Controler.Comandos.cmdTabela.name());
        meusComandos.add(Controler.Comandos.cmdCampo.name());
        meusComandos.add(Controler.Comandos.cmdCampo_Key.name());
        meusComandos.add(Controler.Comandos.cmdCampo_Fkey.name());
        meusComandos.add(Controler.Comandos.cmdCampo_KeyFkey.name());
        meusComandos.add(Controler.Comandos.cmdLogicoLinha.name());

    }

    private final Class[] classesDoDiagrama = new Class[]{
        Campo.class,
        Tabela.class, LogicoLinha.class,
        Texto.class, Desenhador.class, Legenda.class
    };

    @Override
    public Class[] getCassesDoDiagrama() {
        return classesDoDiagrama;
    }

    @Override
    public Object processeEdicaoSubItem(FormaElementar ed, BoxingJava bj) {
        if (ed instanceof Tabela) {
            String prin = bj.Str;
            String resp = bj.Str.substring(bj.Str.indexOf('.') + 1);
            bj.Str = resp;
            Tabela tab = ((Tabela) ed);
            if (prin.startsWith("Campo")) {
                return tab.getCampoSelecionado();
            }
            return tab.getConstraintSelecionado();
        } else {
            return super.processeEdicaoSubItem(ed, bj);
        }
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
            case cmdTabela: {
                Tabela tab = new Tabela(this, "Tabela");
                tab.SetBounds(posi.x, posi.y, 150, 100);
                tab.Reenquadre();
                resu = tab;
            }
            break;

            // <editor-fold defaultstate="collapsed" desc="Ligação">
            case cmdLogicoLinha:
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

                LogicoLinha linha = new LogicoLinha(this);
                resu = linha;
                pt1 = cliq1.getPonto();
                pt2 = cliq2.getPonto();

                linha.SuperInicie(0, pt2, pt1); // = 4 pontos

                obj1 = cliq1.getForma();
                obj2 = cliq2.getForma();
                if (obj1 instanceof Tabela && obj2 instanceof Tabela) {
                    Tabela ori = (Tabela) obj1;
                    Tabela dest = (Tabela) obj2;
                    Campo cmpO = ori.getCampoFromPoint(pt1);// getCampoSelecionado();
                    Campo cmpD = dest.getCampoFromPoint(pt2);// getCampoSelecionado();

                    if (cmpO != null && (cmpO.isUnique() || cmpO.isKey())) {
                        if (cmpD == null) {
                            cmpD = dest.Add((util.Utilidades.IsUpper(ori.getTexto())
                                    ? Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix")
                                    : Editor.fromConfiguracao.getValor("Controler.interface.mensagem.msgcov.fk.prefix").toLowerCase())
                                    + ori.getTexto() + "_" + cmpO.getTexto());
                        }
                        if (!cmpD.isFkey()) { //não é chave estrangeira.
                            cmpD.SetFkey(true);
                            Constraint constr_un_pk = cmpO.isUnique()
                                    ? ori.getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpUNIQUE && c.getCamposDeOrigem().indexOf(cmpO) > -1).findFirst().orElse(null)
                                    : ori.getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpPK).findFirst().orElse(null);

                            Constraint constr_fk = dest.getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK)
                                    .filter(c -> c.getConstraintOrigem() == constr_un_pk).findFirst().orElse(null);

                            if (constr_fk == null) {
                                constr_fk = new Constraint(dest);
                                constr_fk.setTipo(Constraint.CONSTRAINT_TIPO.tpFK);
                                constr_fk.Add(cmpO, cmpD, linha, constr_un_pk);
                            } else {
                                constr_fk.Add(cmpO, cmpD);
                                if (constr_fk.getConstraintOrigem() == null) {
                                    constr_fk.setConstraintOrigem(constr_un_pk);
                                }
                                if (constr_fk.getLigacao() == null) {
                                    constr_fk.setLigacao(linha);
                                }
                            }
                            //constr_fk.Valide();
                        }
                    }
                    linha.ajusteSeta();
                }
                break;
            // </editor-fold>

            case cmdCampo:
            case cmdCampo_Fkey:
            case cmdCampo_Key:
            case cmdCampo_KeyFkey:
                res = CaptureFromPoint(posi);
                if (res instanceof FormaElementar) {
                    resu = (FormaElementar) res;
                } else {
                    setComando(Controler.Comandos.cmdTabela);
                    cliq1 = null;
                    cliq2 = null;
                    resu = RealiseComando(posi);
                }
                if (resu instanceof Tabela) {
                    Tabela tab = (Tabela) resu;
                    Campo c = tab.Add(Editor.fromConfiguracao.getValor("diagrama.Campo.nome"));
                    if (com == Controler.Comandos.cmdCampo_Key || com == Controler.Comandos.cmdCampo_KeyFkey) {
                        c.setKey(true);
                    }
                    if (com == Controler.Comandos.cmdCampo_Fkey || com == Controler.Comandos.cmdCampo_KeyFkey) {
                        c.setFkey(true);
                    }
                }
                break;
        }
        if (resu == null) {
            if ((com == Controler.Comandos.cmdApagar) && (CaptureFromPoint(posi) instanceof Tabela)) {
                res = CaptureFromPoint(posi);
                resu = (FormaElementar) res;
                ClearSelect();
                setSelecionado(resu);
                Campo cmp = ((Tabela) res).getCampoFromPoint(posi);
                boolean sn = false;
                if (cmp == null) {
                    Constraint constr = ((Tabela) res).getConstraintFromPoint(posi);
                    if (constr != null) {
                        ((Tabela) res).RemoveConstraint(constr);
                        setSelecionado(resu);
                        sn = true;
                    }
                } else {
                    ((Tabela) res).RemoveCampo(cmp);
                    setSelecionado(resu);
                    sn = true;
                }

                if (!sn) {
                    deleteSelecao();
                    resu = null;
                }
            } else {
                return super.RealiseComando(posi);
            }
        }
        cliq1 = null;
        cliq2 = null;
        if (!master.isControlDown()) {
            setComando(null);
        } else {
            setComando(com);
        }
        if (resu != null) {
            resu.BringToFront();
        }

        return resu;
    }

    public ArrayList<Campo> getListaDeCampos() {
        ArrayList<Campo> res = new ArrayList<>();
        getListaDeItens().stream().filter((el) -> (el instanceof Tabela)).map(el -> (Tabela) el).forEach((el) -> {
            res.addAll(el.getCampos());
        });
        return res;
    }

    private final int CONS_DISTANCIA_LEFT = 160;
    private final int CONS_DISTANCIA_TOP = 80;

    public void OrganizeTabelas() {
        List<Tabela> tabelas = getListaDeItens().stream().filter(o -> o instanceof Tabela).map(t -> (Tabela) t).collect(Collectors.toList());

        tabelas.forEach(t1 -> {
            tabelas.stream().filter(t -> t != t1).forEach(t2 -> {
                ApliqueDistancia(t1, t2);
            });
        });

        final List<Tabela> ord = tabelas.stream().sorted((t1, t2) -> {
            return Integer.compare(t1.getLeft(), t2.getLeft());
        }).collect(Collectors.toList());

        ord.forEach(t1 -> {
            ord.stream().filter(t -> t != t1).forEach(t2 -> {
                ApliqueDistancia(t1, t2);
            });
        });
    }

    private void ApliqueDistancia(Tabela ori, Tabela dest) {
        int m = Forma.MapaPosi(ori, dest);

        //  1.      2      .3
        //  0.      A      .4
        //  7.      6      .5
        int x = 0;
        int y = 0;
        switch (m) {
            case 6:
                if (dest.getTop() - ori.getTopHeight() < CONS_DISTANCIA_TOP) {
                    y = CONS_DISTANCIA_TOP - (dest.getTop() - ori.getTopHeight());
                }
                break;
            case 5:
                int distX = dest.getLeft() - ori.getLeftWidth();
                int distY = dest.getTop() - ori.getTopHeight();
                if (distY < CONS_DISTANCIA_TOP) {
                    y = CONS_DISTANCIA_TOP - (dest.getTop() - ori.getTopHeight());
                }
                if (distX < CONS_DISTANCIA_LEFT) {
                    x = CONS_DISTANCIA_LEFT - (dest.getLeft() - ori.getLeftWidth());
                }

                if (x > y) { //mova o mínimo/necessário. Se x for maior então mova apenas y
                    if (distX < (CONS_DISTANCIA_LEFT / 4)) { // tá muito perto?
                        x = (CONS_DISTANCIA_LEFT / 4);
                    } else {
                        x = 0;
                    }
                } else {
                    if (distY < (CONS_DISTANCIA_TOP / 4)) {
                        y = (CONS_DISTANCIA_TOP / 4);
                    } else {
                        y = 0;
                    }
                }
                break;
            case 3:
            case 4:
                if (dest.getLeft() - ori.getLeftWidth() < CONS_DISTANCIA_LEFT) {
                    x = CONS_DISTANCIA_LEFT - (dest.getLeft() - ori.getLeftWidth());
                }
                break;
        }
        if (x > 0 || y > 0) {
            organizou = true;
            dest.DoMove(x, y);
        }
    }

    protected final String COMM_ORG = "orgtab";
    protected final String COMM_EDT_CMPS = "edt_campos";
    protected final String COMM_EDT_CMPS_TP = "edt_campos_tipo";
    protected final String COMM_COV_FISICO = "logico.cov_fisico";

    @Override
    public void populeComandos(JMenuItem menu) {
        super.populeComandos(menu);
        menu.removeAll();
        menu.setEnabled(true);
        String tmp = Editor.fromConfiguracao.getValor("Controler.interface.Diagrama.Command.Logico.Org.descricao");
        Diagrama.AcaoDiagrama ac = new Diagrama.AcaoDiagrama(this, tmp, "Controler.interface.Diagrama.Command.Logico.Org.img", tmp, COMM_ORG);
        ac.normal = false;
        JMenuItem mi = new JMenuItem(ac);
        mi.setName(tmp);
        menu.add(mi);

        tmp = Editor.fromConfiguracao.getValor("Controler.interface.Diagrama.Command.Logico.EdtC.descricao");
        ac = new Diagrama.AcaoDiagrama(this, tmp, "Controler.interface.Diagrama.Command.Logico.EdtC.img", tmp, COMM_EDT_CMPS);
        ac.normal = false;
        mi = new JMenuItem(ac);
        mi.setName(tmp);
        menu.add(mi);

        tmp = Editor.fromConfiguracao.getValor("Controler.interface.Diagrama.Command.Logico.EdtT.descricao");
        ac = new Diagrama.AcaoDiagrama(this, tmp, "Controler.interface.Diagrama.Command.Logico.EdtT.img", tmp, COMM_EDT_CMPS_TP);
        ac.normal = false;
        mi = new JMenuItem(ac);
        mi.setName(tmp);
        menu.add(mi);

        tmp = Editor.fromConfiguracao.getValor("Controler.interface.Diagrama.Command.Logico.converter.descricao");
        ac = new Diagrama.AcaoDiagrama(this, tmp, "Controler.interface.Diagrama.Command.Logico.converter.img", tmp, COMM_COV_FISICO);
        ac.normal = false;
        mi = new JMenuItem(ac);
        mi.setName(tmp);
        menu.add(mi);
    }

    private transient boolean organizou = false;

    @Override
    public void rodaComando(String comm) {
        if (comm.equals(COMM_ORG)) {
            setSelecionado(null);
            organizou = false;
            OrganizeTabelas();
            if (organizou) {
                DoMuda(null);
            }
        } else if (comm.equals(COMM_EDT_CMPS)) {
            LancarEditorDeCampos();
        } else if (comm.equals(COMM_EDT_CMPS_TP)) {
            LancarEditorDeCamposTP();
        } else if (comm.equals(COMM_COV_FISICO)) {
            ConverterParaFisico();
        }
    }

    private final int ATAG = 86;
    private final int EDITOR_CAMPOS = 200317;
    private final int EDITOR_CAMPOS_TP = 180717;
    private final int CONV_FISICO = 310317;

    @Override
    public void EndProperty(ArrayList<InspectorProperty> res) {
        super.EndProperty(res);
        res.add(InspectorProperty.PropertyFactorySeparador("diagrama.logico.sql"));
        res.add(InspectorProperty.PropertyFactoryTexto("diagrama.logico.separadorsql", "setFromString", getSeparatorSQL()).setTag(21042017));
        res.add(InspectorProperty.PropertyFactoryTexto("diagrama.logico.prefixo", "setFromString", getPrefixo()).setTag(7072018));
        res.add(InspectorProperty.PropertyFactorySeparador(COMM_ORG));
        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_ORG).setTag(ATAG));
        res.add(InspectorProperty.PropertyFactorySeparador(COMM_EDT_CMPS));
        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_EDT_CMPS).setTag(EDITOR_CAMPOS));
        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_EDT_CMPS_TP).setTag(EDITOR_CAMPOS_TP));
        res.add(InspectorProperty.PropertyFactorySeparador(COMM_COV_FISICO));
        res.add(InspectorProperty.PropertyFactoryCommand(FormaElementar.nomeComandos.cmdDoAnyThing.name(), COMM_COV_FISICO).setTag(CONV_FISICO));
    }

    @Override
    public void InfoDiagrama_ToXmlValores(Document doc, Element me) {
        super.InfoDiagrama_ToXmlValores(doc, me);
        me.appendChild(util.XMLGenerate.ValorString(doc, "SeparatorSQL", getSeparatorSQL()));
        me.appendChild(util.XMLGenerate.ValorString(doc, "Prefixo", getPrefixo()));
    }

    @Override
    public boolean InfoDiagrama_LoadFromXML(Element me, boolean colando) {
        if (!super.InfoDiagrama_LoadFromXML(me, colando)) {
            return false;
        }
        setSeparatorSQL(util.XMLGenerate.getValorStringFrom(me, "SeparatorSQL"));
        setPrefixo(util.XMLGenerate.getValorStringFrom(me, "Prefixo"));
        return true;
    }

    @Override
    public void setFromString(String str, int tag) {
        super.setFromString(str, tag);
        if (tag == 21042017) {
            setSeparatorSQL(str);
        } else if (tag == 7072018) {
            setPrefixo(str);
        }
    }

    @Override
    public void DoAnyThing(int Tag) {
        super.DoAnyThing(Tag);
        if (Tag == ATAG) {
            rodaComando(COMM_ORG);
            return;
        }

        if (Tag == CONV_FISICO) {
            rodaComando(COMM_COV_FISICO);
            return;
        }

        if (Tag == EDITOR_CAMPOS) {
            LancarEditorDeCampos();
            return;
        }
        if (Tag == EDITOR_CAMPOS_TP) {
            LancarEditorDeCamposTP();
        }
    }

    public void LancarEditorDeCampos() {
        if (getListaDeItens().stream().filter(tb -> tb instanceof Tabela).count() == 0) {
            JOptionPane.showMessageDialog(Aplicacao.fmPrincipal,
                    Editor.fromConfiguracao.getValor("Controler.interface.mensagem.sem_campos"),
                    Editor.fromConfiguracao.getValor("Controler.interface.mensagem.tit_informacao"),
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        EditorDeCampos de = new EditorDeCampos(Aplicacao.fmPrincipal, true);
        de.setLocationRelativeTo(Aplicacao.fmPrincipal);
        de.Inicie(this);
        de.setVisible(true);
        PerformInspector();
    }

    public void LancarEditorDeIR(int Tag) {
        if (getListaDeItens().stream().filter(tb -> tb instanceof Tabela).count() == 0) {
            JOptionPane.showMessageDialog(Aplicacao.fmPrincipal,
                    Editor.fromConfiguracao.getValor("Controler.interface.mensagem.sem_campos"),
                    Editor.fromConfiguracao.getValor("Controler.interface.mensagem.tit_informacao"),
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        switch (Tag) {
            case Constraint.TAG_COMMAND_PK:
                EditorDeIR de = new EditorDeIR(Aplicacao.fmPrincipal, true);
                de.setLocationRelativeTo(Aplicacao.fmPrincipal);
                de.Inicie(this);
                de.setVisible(true);
                break;
            case Constraint.TAG_COMMAND_UN:
                EditorDeIrUnique un = new EditorDeIrUnique(Aplicacao.fmPrincipal, true);
                un.setLocationRelativeTo(Aplicacao.fmPrincipal);
                un.Inicie(this);
                un.setVisible(true);
                break;
            case Constraint.TAG_COMMAND_FK:
                EditorDeIrFK fk = new EditorDeIrFK(Aplicacao.fmPrincipal, true);
                fk.setLocationRelativeTo(Aplicacao.fmPrincipal);
                fk.Inicie(this);
                fk.setVisible(true);
                break;
        }
        PerformInspector();
        repaint();
    }

    public boolean ConverterParaFisico() {
        boolean vai = true;
        while (getListaDeTabelas().stream().anyMatch(t -> t.getCampos().stream().anyMatch(cc -> cc.getTipo().isEmpty()))) {
            EditorDeTipos edt = new EditorDeTipos((Frame) (Aplicacao.fmPrincipal.getRootPane()).getParent(), true);
            edt.setLocationRelativeTo(Aplicacao.fmPrincipal.getRootPane());
            edt.Inicie(this);
            edt.setVisible(true);
            vai = edt.getResultado() == JOptionPane.OK_OPTION;
            if (!vai) {
                break;
            }
        }

//        for (Tabela t : getListaDeTabelas()) {
//            if (t.getCampos().stream().anyMatch(cc -> cc.getTipo().isEmpty())) {
//                EditorDeTipos edt = new EditorDeTipos((Frame) (Aplicacao.fmPrincipal.getRootPane()).getParent(), true);
//                edt.setLocationRelativeTo(Aplicacao.fmPrincipal.getRootPane());
//                edt.Inicie(this);
//                edt.setVisible(true);
//                vai = edt.getResultado() == JOptionPane.OK_OPTION;
//                if (!vai) break;
//            }
//        }
        if (!vai) {
            return false;
        }

        String tmp = "/* " + getNomeFormatado() + ": */";
        List<Tabela> tabelas = getListaDeTabelas();
        ArrayList<String> ddl = new ArrayList<>();
        tabelas.forEach(t -> {
            ddl.add("");
            t.DDLGenerate(ddl, t.DDL_PEGAR_TABELAS);
            t.DDLGenerate(ddl, t.DDL_PEGAR_INTEGRIDADE_PK_UN_NOMEADAS);
        });
        tabelas.forEach(t -> {
            t.DDLGenerate(ddl, t.DDL_PEGAR_INTEGRIDADE_FK);
        });
        MostradorDeCodigo edt = new MostradorDeCodigo((Frame) (Aplicacao.fmPrincipal.getRootPane()).getParent(), true);
        edt.setLocationRelativeTo(Aplicacao.fmPrincipal.getRootPane());
        edt.setTexto(ddl.stream().map(s -> "\n" + s).reduce(tmp, String::concat));
        edt.setVisible(true);
        return true;
    }

    private DataBaseModel dataModel = new DataBaseModel();

    public DataBaseModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataBaseModel dataModel) {
        this.dataModel = dataModel;
    }

    public List<Tabela> getListaDeTabelas() {
        return getListaDeItens().stream().filter(tb -> tb instanceof Tabela).map(tb -> (Tabela) tb).collect(Collectors.toList());
    }

//    public static final int MSG_IR_CHANGE_ADD_CMP = 2;
//    public static final int MSG_IR_CHANGE_DEL_CMP = 3;
//    public static final int MSG_IR_PREDELETE = 4;
//    public static final int MSG_CMP_DELETE = 5;
//    public static final int MSG_CMP_CHANGE_TIPO = 6;
    public void ReciveNotifiqueIR(Constraint cons, int msg, Campo cmp) {

        switch (msg) {
            case Tabela.MSG_CMP_CHANGE_TIPO:
                if (cmp == null) {
                    return;
                }
                ArrayList<Campo> campos = new ArrayList<>();
                getListaDeTabelas().stream().forEach(ta -> {
                    ta.getConstraints().stream().forEach(c -> {
                        int idx = c.getCamposDeOrigem().indexOf(cmp);
                        if (idx > -1) {
                            Campo tmp = c.getCamposDeDestino().get(idx);
                            if (tmp != null && !tmp.getTipo().equals(cmp.getTipo())) {
                                campos.add(tmp);
                            }
                        } else {
                            idx = c.getCamposDeDestino().indexOf(cmp);
                            if (idx > -1) {
                                Campo tmp = c.getCamposDeOrigem().get(idx);
                                if (tmp != null && !tmp.getTipo().equals(cmp.getTipo())) {
                                    campos.add(tmp);
                                }
                            }
                        }
                    });
                });
                campos.stream().forEach(cp -> {
                    cp.setTipo(cmp.getTipo());
                });
                break;

            case Tabela.MSG_IR_CHANGE_DEL_CMP:
                if (cons == null) {
                    return;
                }
            case Tabela.MSG_CMP_DELETE:
                if (cmp == null) {
                    return;
                }
                getListaDeTabelas().stream().forEach(ta -> {
                    ta.getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK)
                            .forEach(c -> {
                                if (c.getCamposDeOrigem().indexOf(cmp) > -1) {
                                    boolean sn = true;
                                    if (msg == Tabela.MSG_IR_CHANGE_DEL_CMP) {
                                        sn = (c.getConstraintOrigem() == cons);
                                    }
                                    if (sn) {
                                        int idx = c.getCamposDeOrigem().indexOf(cmp);
                                        Campo tmp = c.getCamposDeDestino().get(idx);
                                        c.Add(null, tmp);
                                    }
                                } else {
                                    c.Valide();
                                }
                            });
                });
                break;
            case Tabela.MSG_IR_CHANGE_ADD_CMP:
                if (cons == null) {
                    return;
                }
                getListaDeTabelas().stream().forEach(ta -> {
                    ta.getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK)
                            .filter(c -> (c.getConstraintOrigem() == cons))
                            .forEach(c -> {
                                c.Valide();
                            });
                });
                break;
            case Tabela.MSG_IR_PREDELETE:
                if (cons == null) {
                    return;
                }
                getListaDeTabelas().stream().forEach(ta -> {
                    ta.getConstraints().stream().filter(c -> c.getTipo() == Constraint.CONSTRAINT_TIPO.tpFK)
                            .filter(c -> (c.getConstraintOrigem() == cons))
                            .forEach(c -> {
                                c.setConstraintOrigem(null);
                            });
                });
                break;
        }
    }

    /**
     * Separador SQL
     */
    private String separatorSQL = ";";

    public String getSeparatorSQL() {
        return separatorSQL;
    }

    public void setSeparatorSQL(String separatorSQL) {
        if (this.separatorSQL.equals(separatorSQL)) {
            return;
        }
        this.separatorSQL = separatorSQL;
        repaint();
    }

    /**
     * Versão 3.2 Prefixo do Esquema
     */
    private String prefixo = "";

    public String getPrefixo() {
        return prefixo;
    }

    public void setPrefixo(String prefixo) {
        if (this.prefixo.equals(prefixo)) {
            return;
        }
        this.prefixo = prefixo;
        repaint();
    }

    public void LancarEditorDeCamposTP() {
        if (getListaDeItens().stream().filter(tb -> tb instanceof Tabela).count() == 0) {
            JOptionPane.showMessageDialog(Aplicacao.fmPrincipal,
                    Editor.fromConfiguracao.getValor("Controler.interface.mensagem.sem_campos"),
                    Editor.fromConfiguracao.getValor("Controler.interface.mensagem.tit_informacao"),
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        EditorDeTipos de = new EditorDeTipos(Aplicacao.fmPrincipal, true);
        de.lblMsg.setText(Editor.fromConfiguracao.getValor("Controler.interface.mensagem.edt_tipos"));
        de.setLocationRelativeTo(Aplicacao.fmPrincipal);
        de.Inicie(this);
        de.SelecioneByDiagramaSelecionado();
        de.setVisible(true);
        PerformInspector();
    }

    @Override
    protected void AdicioneSubsFromRealce(ArrayList<FormaElementar> res, FormaElementar item) {
        super.AdicioneSubsFromRealce(res, item);
        if (item instanceof LogicoLinha) {
            LogicoLinha lig = (LogicoLinha) item;
            res.add(lig.getCardA());
            res.add(lig.getCardB());
        }
    }

    @Override
    public void OnAfterLoad(boolean isXml) {
        super.OnAfterLoad(isXml); //To change body of generated methods, choose Tools | Templates.
        if (prefixo == null) {
            prefixo = "";
        }
//        if (!isXml) {
//            if (versaoA.endsWith("3") && versaoB.equals("0")) {
//                getListaDeTabelas().stream().forEach(T -> {
//                    T.getAncorasCode().add(T.CODE_DDL);
//                    T.getAncorasCode().add(T.CODE_SOBE);
//                    T.getAncorasCode().add(T.CODE_DESCE);
//                    T.getAncorasCode().add(T.CODE_DEL_CMP_CONST);
//                });
//            }
//        }
    }

}
/////??? Copyright do brModelo
/*

============================================================================

Copyright (c) 2018 SIS4.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


 */
