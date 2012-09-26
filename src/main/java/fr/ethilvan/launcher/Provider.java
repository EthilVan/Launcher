package fr.ethilvan.launcher;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.ethilvan.launcher.mode.Mode;
import fr.ethilvan.launcher.util.Util;

public class Provider {

    private static Provider instance;

    public static Provider get() {
        if (instance == null) {
            instance = load();
        }

        return instance;
    }

    private static Provider load() {
        InputStream stream =
                Provider.class.getResourceAsStream("/config.json");

        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(stream);
            return Launcher.getGson().fromJson(reader,
                    Provider.class);
        } catch (JsonSyntaxException exc) {
            throw Util.wrap(exc);
        } catch (JsonIOException exc) {
            throw Util.wrap(exc);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public final String website;
    public final String newsUrl;
    public final String imgListUrl;
    public final Mode[] modes;

    private Provider() {
        website = null;
        newsUrl = null;
        imgListUrl = null;
        modes = null;
    }
}
