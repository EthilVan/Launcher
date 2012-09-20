/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010, 2011 Albert Pham <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sk89q.mclauncher.security;

public class CertificateVerificationException extends Exception {

    private static final long serialVersionUID = 5426631298249721608L;

    public CertificateVerificationException() {
    }

    public CertificateVerificationException(String message) {
        super(message);
    }

    public CertificateVerificationException(Throwable cause) {
        super(cause);
    }

    public CertificateVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

}
