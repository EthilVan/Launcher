package fr.ethilvan.launcher.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public final class Util {

    public static final String ETHILVAN_FR = "http://ethilvan.fr";

    public static void openURI(URI uri)
            throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(uri);
    }

    public static void openURL(URL url)
            throws IOException, URISyntaxException {
        openURI(url.toURI());
    }

    public static void openEthilVanFR()
            throws MalformedURLException, IOException, URISyntaxException {
        openURI(new URI(ETHILVAN_FR));
    }

    public static File getHomeDirectory() {
        return new File(System.getProperty("user.home", "."));
    }

    private Util() {
    }
}