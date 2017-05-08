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

/**
 *
 * @author SAA
 */
public class GerenciadorSubParte implements Serializable{

    private static final long serialVersionUID = 8170769009687181088L;
    private String texto, titulo;
    
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
    private final byte[] byteImage;
    private final String XMLCopiado;
    private final String versaoDiagrama;
    private final Diagrama.TipoDeDiagrama tipo;

    public GerenciadorSubParte(String texto, byte[] byteImage, String XMLCopiado, String versaoDiagrama, Diagrama.TipoDeDiagrama tipo) {
        this.byteImage = byteImage;
        this.XMLCopiado = XMLCopiado;
        this.texto = texto;
        this.versaoDiagrama = versaoDiagrama;
        this.tipo = tipo;
        this.titulo = texto.length() > 20? texto.substring(20): texto;
    }

    public String getTexto() {
        return texto;
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
    
    public Diagrama.TipoDeDiagrama getTipo() {
        return tipo;
    }
}
