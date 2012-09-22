package fr.ethilvan.launcher.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.util.Encryption;
import fr.ethilvan.launcher.util.Util;

public class Configuration {

    private final Providers providers;
    private String username;
    private String password;
    private boolean useLatestLwjgl;

    public static Configuration load() {
        File optionsFile = configFile();
        Configuration options;
        if (optionsFile.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(optionsFile);
                options = Launcher.getGson().fromJson(reader,
                        Configuration.class);
            } catch (JsonSyntaxException exc) {
                throw Util.wrap(exc);
            } catch (JsonIOException exc) {
                throw Util.wrap(exc);
            } catch (FileNotFoundException exc) {
                throw Util.wrap(exc);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        } else {
            options = new Configuration();
        }

        return options;
    }

    private static File configFile() {
        return new File(Launcher.getSettingsDir(), "config.json");
    }

    private Configuration() {
        this.useLatestLwjgl = false;
        this.providers = new Providers();
    }

    public void save() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(configFile());
            Launcher.getGson().toJson(this, writer);
        } catch (JsonIOException exc) {
            throw Util.wrap(exc);
        } catch (IOException exc) {
            throw Util.wrap(exc);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public Provider getProvider() {
        return providers.getSelectedItem();
    }

    public Providers getProviders() {
        return providers;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return Encryption.decrypt(password);
    }

    public void rememberAccount(String username, String password) {
        this.username = username;
        this.password = Encryption.encrypt(password);
    }

    public boolean getUseLatestLWJGL() {
        return useLatestLwjgl;
    }

    public void setUseLatestLWJGL(boolean useLatestLWJGL) {
        this.useLatestLwjgl = useLatestLWJGL;
    }
}
