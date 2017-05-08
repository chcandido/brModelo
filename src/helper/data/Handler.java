/*
 * Copyright (C) 2016 chcan
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
package helper.data;

/**
 *
 * @author chcan
 */
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;


public class Handler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new DataConnection(u);
    }

    public static void install() {
        String pkgName = Handler.class.getPackage().getName();
        String pkg = pkgName.substring(0, pkgName.lastIndexOf('.'));
        
        String protocolHandlers = System.getProperty("java.protocol.handler.pkgs", "");
        if (!protocolHandlers.contains(pkg)) {
            if (!protocolHandlers.isEmpty()) {
                protocolHandlers += "|";
            }
            protocolHandlers += pkg;
            System.setProperty("java.protocol.handler.pkgs", protocolHandlers);
        }
    }
}
