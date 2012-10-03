package fr.ethilvan.launcher;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.ethilvan.launcher.mode.Mode;

public class Provider {

    private static Provider instance;

    public static Provider get() {
        if (instance == null) {
            instance = load();
        }

        return instance;
    }

    private static Provider load() {
        Logger.getLogger(Provider.class.getName()).info("Loading provider");
        InputStream stream =
                Provider.class.getResourceAsStream("/config.json");

        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(stream);
            return Launcher.getGson().fromJson(reader,
                    Provider.class);
        } catch (JsonSyntaxException exc) {
            Logger.getLogger(Provider.class.getName())
                    .log(Level.SEVERE, "Unable to read bundled config", exc);
        } catch (JsonIOException exc) {
            Logger.getLogger(Provider.class.getName())
                    .log(Level.SEVERE, "Unable to read bundled config", exc);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        System.exit(200);
        return null;
    }

    public final String launcherTitle;
    public final String gameTitle;
    public final String website;
    public final String newsUrl;
    public final String imgListUrl;
    public final Mode[] modes;

    private Provider() {
        launcherTitle = null;
        gameTitle = null;
        website = null;
        newsUrl = null;
        imgListUrl = null;
        modes = null;
    }
}
