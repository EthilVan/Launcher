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

package com.sk89q.mclauncher.launch;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import fr.ethilvan.launcher.Launcher;

public class GameAppletContainer extends Applet implements AppletStub {
    
    private static final Logger logger = Logger.getLogger(GameAppletContainer.class.getCanonicalName());
    
    private static final long serialVersionUID = 356028256286037416L;

    private Map<String, String> parameters;
    private Applet applet;
    private boolean active = false;
    
    public GameAppletContainer(Map<String, String> parameters, Applet applet) {
        this.parameters = parameters;
        this.applet = applet;
        applet.setStub(this);
        setLayout(new BorderLayout());
    }
    
    @Override
    public void start() {
        logger.info("Starting " + applet.getClass().getCanonicalName());
        applet.setSize(getWidth(), getHeight());
        add(applet, BorderLayout.CENTER);
        logger.info("Initializing Minecraft...");
        applet.init();
        this.active = true;
        applet.start();
        validate();
        logger.info("Cave Johnson, we're done here.");
        Launcher.get().cleanUp();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void stop() {
        active = false;
        super.stop();
    }

    @Override
    public URL getDocumentBase() {
        try {
            return new URL("http://www.minecraft.net/game/");
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public String getParameter(String name) {
        return (String) parameters.get(name);
    }

    @Override
    public void appletResize(int width, int height) {
    }
}