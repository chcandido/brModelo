/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador.inspector;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author ccandido
 * Função agrupar propriedades no Inspector de forma a relacioná-las como habilitadas ou desabilitadas em virtude de uma dada condição.
 */
public class InspectorPprtAgrupador {

    /**
     * Propriedade a ser condcionada
     */
    private final String propriedade;
    
    /**
     * Condições impostas
     */
    private final ArrayList<InspectorPprtAgrupadorCondicao> condicao = new ArrayList<>();

    public InspectorPprtAgrupador(String pprt) {
        this.propriedade = pprt;
    }

    public String getPropriedade() {
        return propriedade;
    }
    
    public void AddCondicao(String[] enableIf, String[] afetados) {
        condicao.add(new InspectorPprtAgrupadorCondicao(enableIf, afetados));
    }

    public ArrayList<InspectorPprtAgrupadorCondicao> getCondicao() {
        return condicao;
    }

    /**
     * Uma condição qualquer: condição (ou conjunto de condições possíveis) a ser satisfeita para um grupo de propriedades afetadas
     */
    public class InspectorPprtAgrupadorCondicao {
        
        /**
         * Em que condições as propriedades afetadas ficarão habilitadas, em caso de não atendimento, ficarão desabilitadas
         */
        private final String[] enableIf;
        
        /**
         * Quais são as propriedades afetadas
         */
        private final String[] afetados;

        public InspectorPprtAgrupadorCondicao(String[] enableIf, String[] afetados) {
            this.enableIf = enableIf;
            this.afetados = afetados;
        }

//        public String getPropriedade() {
//            return InspectorPprtAgrupador.this.propriedade;
//        }

        public String[] getAfetados() {
            return afetados;
        }

        public String[] getEnableIf() {
            return enableIf;
        }
    }
    
    /**
     * Busca dentre as condições armazenadas quais devem aparecer como habilitada dado um determinado valor da propriedade. 
     * @param valor
     * @return proprieades a serem habilitadas - este método é executado primeiro, em relação ao QuaisDisableIf, prevalescendo a cnaidção de desabilitação em caso de concorrência.
     */
    public ArrayList<String> QuaisEnableIf(String valor){
        ArrayList<String> res = new ArrayList<>();
        condicao.stream().filter((cond) -> (Arrays.asList(cond.enableIf).indexOf(valor) > -1)).forEach((cond) -> {
            res.addAll(Arrays.asList(cond.afetados));
        });
        return res;
    }

    /**
     * Busca dentre as condições armazenadas quais devem aparecer como desabilitada dado um determinado valor da propriedade. 
     * @param valor
     * @return proprieades a serem desabilitadas - este método é executado após o QuaisEnableIf fazendo prevalescer a cnaidção de desabilitação em caso de concorrência.
     */
    public ArrayList<String> QuaisDisableIf(String valor){
        ArrayList<String> res = new ArrayList<>();
        condicao.stream().filter((cond) -> (Arrays.asList(cond.enableIf).indexOf(valor) == -1)).forEach((cond) -> {
            res.addAll(Arrays.asList(cond.afetados));
        });
        return res;
    }
}
