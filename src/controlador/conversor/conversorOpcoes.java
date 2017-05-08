/*
 * Copyright (C) 2014 CANDIDO
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

import desenho.FormaElementar;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author CANDIDO
 */
public class conversorOpcoes {
    public ArrayList<String> Questoes = new ArrayList<>();
    public ArrayList<String> Textos = new ArrayList<>();
    public ArrayList<String> Observacoes = new ArrayList<>();
    public ArrayList<String> Erros = new ArrayList<>();
    public ArrayList<Integer> Disables = new ArrayList<>();
    
    public int opcDefault = 0;
    public int OPC = 0;
    //public int registradorTemporario = -1;
    public boolean isYesToAll() { 
        return Resultado == resultOfQuestion.resOkToAll;
    }
    
    public FormaElementar obj = null;
    
    public enum resultOfQuestion {
        resOk, resOkToAll, respCancel
    }
    
    public resultOfQuestion Resultado = resultOfQuestion.respCancel;
    
    public void Inicializar() {
        Questoes.clear();
        Textos.clear();
        Observacoes.clear();
        //Erros.clear();
        Disables.clear();
        opcDefault = 0;
        //Resultado = resultOfQuestion.respCancel;
    }
    
    public Point LastPosi = null;
}
