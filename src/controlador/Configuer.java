/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;

/**
 *
 * @author ccandido
 */
public class Configuer {

    public Configuer() {
        super();
        LoadCfg();
    }
    private HashMap<String, String> configuracao = new HashMap<>();

    public HashMap<String, String> getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(HashMap<String, String> configuracao) {
        this.configuracao = configuracao;
    }

    private void LoadCfg() {
        String tmp = System.getProperty("user.dir") + File.separator + "config.chc";
        File f = new File(tmp);

        ResourceBundle resourceMap = getResourceMap();

        for (String v : resourceMap.keySet()) {
//            if (v.startsWith("Inspector.") || v.startsWith("Controler.")) {
//                configuracao.put(v, resourceMap.getString(v));
//            } else if (v.startsWith("diagrama.")) {
                configuracao.put(v, resourceMap.getString(v));
//            }
        }

        if (!f.exists()) {
            SaveCfg();
        } else {
            try {
                try (FileInputStream cfgFile = new FileInputStream(tmp)) {
                    Properties prop = new Properties();
                    prop.load(cfgFile);
                    for (Object v : prop.keySet()) {
                        String vv = String.valueOf(v);
                        if (!configuracao.containsKey(vv)) { //não carrego o que for da conf. padrão - tradução!
                            configuracao.put(vv, prop.getProperty(vv));
                        }
                    }
                }
            } catch (Exception e) {
                util.BrLogger.Logger("ERROR_LOAD_CFGFILE", e.getMessage());
                SaveCfg();
            }
        }
    }

    public String getValor(String key) {
        if (configuracao.containsKey(key) && configuracao.get(key) != null) {
            return configuracao.get(key);
        }
        return key;
    }

    public boolean hasValor(String key) {
        return (configuracao.containsKey(key) && 
                (configuracao.get(key) != null) && 
                (!"".equals(configuracao.get(key)))
                );
    }

    public void setValor(String key, String value) {
        configuracao.put(key, value);
        SaveCfg();
    }

    public boolean SetAndSaveIfNeed(String key, String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        if (configuracao.containsKey(key) && configuracao.get(key) != null) {
            if (configuracao.get(key).equals(value)) {
                return false;
            }
        }
        setValor(key, value);
        return true;
    }

    public void SaveCfg() {
        String tmp = System.getProperty("user.dir") + File.separator + "config.chc";
        try {
            try (FileOutputStream cfgFile = new FileOutputStream(tmp)) {
                Properties prop = new Properties();

                for (String v : configuracao.keySet()) {
                    if (!((v.startsWith("Inspector."))
                            || (v.startsWith("diagrama."))
                            || (v.startsWith("Linha.cmd."))
                            || (v.startsWith("Controler.")))) { //não coloco os do arq. de cfg padrão - tradução.
                        prop.setProperty(v, getValor(v));
                    }
                }
                prop.store(cfgFile, "Config");
            }
        } catch (Exception e) {
            util.BrLogger.Logger("ERROR_SAVE_CFGFILE", e.getMessage());
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Listas: getList - as listas usadas no Inspector">
    private ArrayList<String> lstDirecao = new ArrayList<>();

    public ArrayList<String> getLstDirecao(Controler.Comandos cmd) {
        lstDirecao.clear();
        if (cmd == Controler.Comandos.cmdTexto || cmd == Controler.Comandos.cmdEapBarraLigacao) {
            lstDirecao.add(getValor("Inspector.lst.direcao.vertical")); //0
            lstDirecao.add(getValor("Inspector.lst.direcao.horizontal")); //1
            return lstDirecao;
        }
        if (cmd == Controler.Comandos.cmdAtributo) {
            //        Up = 0
            //        Right = 1,
            //        Down = 2,
            //        Left = 3,
            //        Horizontal = 4,
            //        Vertical = 5
            lstDirecao.add(getValor("Inspector.lst.direcao.left")); //0
            lstDirecao.add(getValor("Inspector.lst.direcao.right")); //1
            return lstDirecao;
        }
        if (cmd == Controler.Comandos.cmdEspecializacao || cmd == Controler.Comandos.cmdUniao) {
            //        Up = 0
            //        Right = 1,
            //        Down = 2,
            //        Left = 3,
            lstDirecao.add(getValor("Inspector.lst.direcao.up")); //0
            lstDirecao.add(getValor("Inspector.lst.direcao.right")); //1
            lstDirecao.add(getValor("Inspector.lst.direcao.down")); //2
            lstDirecao.add(getValor("Inspector.lst.direcao.left")); //3
        }
        return lstDirecao;
    }
    private ArrayList<String> lstTipoTexto = new ArrayList<>();

    public List<String> getLstTipoTexto() {
        if (lstTipoTexto.isEmpty()) {
            //tpEmBranco, tpRetanguloSimples, tpRetangulo, tpRetanguloArred
            lstTipoTexto.add(getValor("Inspector.lst.tipotexto.embranco")); //0
            lstTipoTexto.add(getValor("Inspector.lst.tipotexto.nota")); //1
            lstTipoTexto.add(getValor("Inspector.lst.tipotexto.retangulo")); //2
            lstTipoTexto.add(getValor("Inspector.lst.tipotexto.retanguloarred")); //3
        }
        return lstTipoTexto;
    }
    private ArrayList<String> lstTextoAlin = new ArrayList<>();

    public List<String> getLstTextoAlin() {
        //alCentro, alEsquerda, alDireita
        if (lstTextoAlin.isEmpty()) {
            lstTextoAlin.add(getValor("Inspector.lst.textoalin.centro")); //0
            lstTextoAlin.add(getValor("Inspector.lst.textoalin.esquerda")); //1
            lstTextoAlin.add(getValor("Inspector.lst.textoalin.direita")); //2
        }
        return lstTextoAlin;
    }
    
    private ArrayList<String> lstLegTipo = new ArrayList<>();

    public List<String> getLstTipoLegenda() {
        if (lstLegTipo.isEmpty()) {
            lstLegTipo.add(getValor("Inspector.lst.legenda.tpcores")); //0
            lstLegTipo.add(getValor("Inspector.lst.legenda.tplinhas")); //1
            lstLegTipo.add(getValor("Inspector.lst.legenda.tpobjetos")); //2
        }
        return lstLegTipo;
    }
    //</editor-fold>
    
    /**
    *  Carregador de Resources e Imagens a partir de Resources.
    */
    
    private static ResourceBundle bundle = null;

    public static ResourceBundle getResourceMap() {
        if (bundle == null) {
            Locale ptBr = new Locale("pt", "BR");
            bundle = ResourceBundle.getBundle("principal/Propriedades", ptBr);
        }
        return bundle;
    }

    public static Image getImageFromResource(String keyImg) {
        String tmp = "/imagens/" + getResourceMap().getString(keyImg);
        Image res = new ImageIcon(Configuer.class.getResource(tmp)).getImage();
        return res;
    }

    public static ImageIcon getImageIconFromResource(String keyImg) {
        String tmp = "/imagens/" + getResourceMap().getString(keyImg);
        return new ImageIcon(Configuer.class.getResource(tmp));
    }
    
    public String getAutoSaveFile() {
        String tmp = System.getProperty("user.dir") + File.separator + "autosave.chc";
        return tmp;
    }
}
