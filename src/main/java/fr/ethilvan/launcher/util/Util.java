package fr.ethilvan.launcher.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ethilvan.launcher.Provider;

public final class Util {

    public final static String UTF8 = "UTF-8";

    public static File getHomeDirectory() {
        return new File(System.getProperty("user.home", "."));
    }

    public static void openURI(URI url) {
        try {
            Desktop.getDesktop().browse(url);
        } catch (IOException exc) {
            Logger.getLogger(Util.class.getName())
                    .log(Level.WARNING, "Unable to open uri in browser", exc);
        }
    }

    public static void openURL(URL url) {
        try {
            openURI(url.toURI());
        } catch (URISyntaxException exc) {
            Logger.getLogger(Util.class.getName())
                    .log(Level.WARNING, "Unable to parse url", exc);
        }
    }

    public static void openURL(String url) {
        try {
            openURI(new URI(url));
        } catch (URISyntaxException exc) {
            Logger.getLogger(Util.class.getName())
                    .log(Level.WARNING, "Unable to parse url", exc);
        }
    }

    public static void openProviderWebsite() {
        openURL(Provider.get().website);
    }

    private Util() {
    }
}
