package fr.ethilvan.launcher.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public final class Util {

    public final static String UTF8 = "UTF-8";

    public static URL urlFor(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException exc) {
            throw new RuntimeException(exc);
        }
    }

    public static URI uriFor(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException exc) {
            throw new RuntimeException(exc);
        }
    }

    public static void openURI(URI uri) {
        try {
            Desktop.getDesktop().browse(uri);
        } catch (IOException exc) {
            throw wrap(exc);
        }
    }

    public static void openURL(URL url) {
        try {
            openURI(url.toURI());
        } catch (URISyntaxException exc) {
            throw wrap(exc);
        }
    }

    public static void openEthilVanFR() {
        try {
            openURI(new URI(EthilVan.WEBSITE));
        } catch (URISyntaxException exc) {
            throw wrap(exc);
        }
    }

    public static File getHomeDirectory() {
        return new File(System.getProperty("user.home", "."));
    }

    public static RuntimeException wrap(Throwable throwable) {
        return new RuntimeException(throwable);
    }

    private Util() {
    }
}
