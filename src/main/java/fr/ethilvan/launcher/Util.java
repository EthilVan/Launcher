package fr.ethilvan.launcher;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public final class Util {

    private static final String ETHILVAN_FR = "http://ethilvan.fr";

    public static void openURL(URL url)
            throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(url.toURI());
    }

    private Util() {
    }

    public static void openEthilVanFR()
            throws MalformedURLException, IOException, URISyntaxException {
        openURL(new URL(ETHILVAN_FR));
    }
}
