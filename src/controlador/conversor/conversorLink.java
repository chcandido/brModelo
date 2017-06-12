/*
 * Copyright (C) 2014 SAA
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author SAA
 */
public class conversorLink {
    public class par {
        protected FormaElementar origem = null, destino = null;
        public par(FormaElementar origem, FormaElementar destino) {
            this.origem = origem;
            this.destino = destino;
        }
        
    }
    
    public List<par> Lista = new ArrayList<>();
    
    public boolean InOrigem(FormaElementar or) {
        return Lista.stream().anyMatch(o -> o.origem == or);
    }
    public boolean InDestino(FormaElementar de) {
        return Lista.stream().anyMatch(o -> o.destino == de);
    }
    
    public final void Add(FormaElementar ori, FormaElementar dest) {
        for (par L : Lista) {
            if (L.origem == ori) {
                if (L.destino == dest) {
                    return;
                }
            }
        }
        Lista.add(new par(ori, dest));
    }
    
    public conversorLink() {
        
    }
    
    public List<FormaElementar> getLigadosOrigem(FormaElementar ori) {
        return Lista.stream().filter(L -> L.origem == ori).map(L -> L.destino).collect(Collectors.toList());
    }
    
    public FormaElementar getLigadoOrigem(FormaElementar ori) {
        return Lista.stream().filter(L -> L.origem == ori).map(L -> L.destino).findFirst().orElse(null);
    }
    
    public List<FormaElementar> getLigadosDestino(FormaElementar dest) {
        return Lista.stream().filter(L -> L.destino == dest).map(L -> L.origem).collect(Collectors.toList());
    }
//    
//    public void TroqueDestino(FormaElementar dest, FormaElementar NovoDest) {
//        Lista.stream().filter(L -> L.destino == dest).forEach(L -> {
//            L.destino = NovoDest;
//        });
//    }
    
    public boolean ExistePar(FormaElementar ori, FormaElementar dest){
        return Lista.stream().filter(L -> L.origem == ori).anyMatch(L -> L.destino == dest);
    }
    
    public boolean RemovePar(FormaElementar ori, FormaElementar dest){
        int i = 0;
        boolean res = false;
        while (i < Lista.size()) {
            par p = Lista.get(i);
            if (p.origem == ori && p.destino == dest) {
                Lista.remove(i);
                res = true;
            } else i++;
        }
        return res;
    }
    
}
