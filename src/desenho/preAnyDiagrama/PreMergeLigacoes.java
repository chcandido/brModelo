/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package desenho.preAnyDiagrama;

import controlador.Diagrama;
import desenho.formas.FormaNaoRetangularDisformeBase;
import desenho.linhas.PontoDeLinha;

/**
 *
 * @author SAA
 */
public class PreMergeLigacoes extends FormaNaoRetangularDisformeBase{

    private static final long serialVersionUID = -1543607393468216991L;

    public PreMergeLigacoes(Diagrama modelo, String texto) {
        super(modelo, texto);
    }
    
    public PreMergeLigacoes(Diagrama modelo) {
        super(modelo);
    }

    @Override
    protected void Posicione4Pontos(PontoDeLinha ponto) {
        super.Posicione4Pontos(ponto);
        int x = getLeft() + getWidth()/2;
        int y = getTop() + getHeight()/2;
        ponto.setCentro(x, y);
    }
    
}
