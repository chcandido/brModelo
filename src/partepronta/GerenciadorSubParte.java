/*
 * Copyright (C) 2015 SAA
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
package partepronta;

import controlador.Diagrama;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author SAA
 */
public class GerenciadorSubParte implements Serializable{

    private static final long serialVersionUID = 8170769009687181088L;

    private String titulo = "";

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    private byte[] byteImage = null;
    private String XMLCopiado = "";
    private String versaoDiagrama = "";
    private Diagrama.TipoDeDiagrama tipoDeDiagrama = Diagrama.TipoDeDiagrama.tpConceitual;
    
    public GerenciadorSubParte(String titulo, byte[] byteImage, String XMLCopiado, String versaoDiagrama, Diagrama.TipoDeDiagrama tipoDeDiagrama) {
        this.byteImage = byteImage;
        this.XMLCopiado = XMLCopiado;
        this.titulo = titulo;
        this.versaoDiagrama = versaoDiagrama;
        this.tipoDeDiagrama = tipoDeDiagrama;
    }
    
    public GerenciadorSubParte() {
        //# Futuro
    }
    
    public void InitGerenciadorSubParte(String titulo, byte[] byteImage, String XMLCopiado, String versaoDiagrama, Diagrama.TipoDeDiagrama tipo) {
        this.byteImage = byteImage;
        this.XMLCopiado = XMLCopiado;
        this.titulo = titulo;
        this.versaoDiagrama = versaoDiagrama;
        this.tipoDeDiagrama = tipo;
    }

    public String getTitulo() {
        return titulo;
    }
    
    public byte[] getByteImage() {
        return byteImage;
    }

    public String getXMLCopiado() {
        return XMLCopiado;
    }

    public String getVersaoDiagrama() {
        return versaoDiagrama;
    }
    
    public Diagrama.TipoDeDiagrama getTipoDeDiagrama() {
        return tipoDeDiagrama;
    }

    public void setXMLCopiado(String XMLCopiado) {
        this.XMLCopiado = XMLCopiado;
    }

    public void setVersaoDiagrama(String versaoDiagrama) {
        this.versaoDiagrama = versaoDiagrama;
    }

    public void setTipoDeDiagrama(Diagrama.TipoDeDiagrama tipoDeDiagrama) {
        this.tipoDeDiagrama = tipoDeDiagrama;
    }
    
    
}
