package fr.ethilvan.launcher.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.mode.Mode;
import fr.ethilvan.launcher.mode.Modes;
import fr.ethilvan.launcher.util.Encryption;
import fr.ethilvan.launcher.util.Encryption.EncryptionException;

public class Config {

    private final Modes modes;
    private String username;
    private String password;
    private boolean useDefaultConfig;
    private boolean useLatestLwjgl;
    private boolean downloadTexturePack;

    public static Config load() {
        Logger.getLogger(Config.class.getName()).info("Loading configuration");
        File optionsFile = configFile();
        Config options = null;
        if (optionsFile.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(optionsFile);
                options = Launcher.getGson().fromJson(reader, Config.class);
            } catch (JsonSyntaxException exc) {
                Logger.getLogger(Config.class.getName())
                        .log(Level.SEVERE, "Unable to read user config", exc);
            } catch (JsonIOException exc) {
                Logger.getLogger(Config.class.getName())
                        .log(Level.SEVERE, "Unable to read user config", exc);
            } catch (FileNotFoundException exc) {
                Logger.getLogger(Config.class.getName())
                        .log(Level.SEVERE, "Unable to read user config", exc);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }

        if (options == null) {
            options = new Config();
        }

        return options;
    }

    private static File configFile() {
        return new File(Launcher.getSettingsDir(), "config.json");
    }

    private Config() {
        this.modes = new Modes();
        this.useDefaultConfig = true;
        this.useLatestLwjgl = false;
    }

    public void save() {
        Logger.getLogger(Config.class.getName()).info("Saving configuration");
        FileWriter writer = null;
        try {
            writer = new FileWriter(configFile());
            Launcher.getGson().toJson(this, writer);
        } catch (JsonIOException exc) {
            Logger.getLogger(Config.class.getName())
                    .log(Level.SEVERE, "Unable to write user config", exc);
        } catch (IOException exc) {
            Logger.getLogger(Config.class.getName())
                    .log(Level.SEVERE, "Unable to write user config", exc);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public Mode getMode() {
        return modes.getSelectedItem();
    }

    public Modes getModes() {
        return modes;
    }

    public boolean isAccountRemembered() {
        return username != null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() throws EncryptionException {
        return Encryption.decrypt(password);
    }

    public void rememberAccount(String username, String password)
            throws EncryptionException{
        this.username = username;
        this.password = Encryption.encrypt(password);
    }

    public void forgetAccount() {
        this.username = null;
        this.password = null;
    }

    public boolean getUseDefaultConfig() {
        return useDefaultConfig;
    }

    public void setUseDefaultConfig(boolean useDefaultConfig) {
        this.useDefaultConfig = useDefaultConfig;
    }

    public boolean getUseLatestLWJGL() {
        return useLatestLwjgl;
    }

    public void setUseLatestLWJGL(boolean useLatestLWJGL) {
        this.useLatestLwjgl = useLatestLWJGL;
    }

    public boolean getDownloadTexturePack() {
        return downloadTexturePack;
    }

    public void setDownloadTexturePack(boolean downloadTexturePack) {
        this.downloadTexturePack = downloadTexturePack;
    }
}
