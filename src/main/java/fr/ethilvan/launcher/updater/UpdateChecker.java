package fr.ethilvan.launcher.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.ui.TaskDialog;
import fr.ethilvan.launcher.util.Util;

public class UpdateChecker {

    private final static String LOCAL_VERSION_FILE = "version";

    public UpdateChecker() {
    }

    public boolean needUpdate(TaskDialog dialog) {
        dialog.setStatus("Vérification des mises à jour.", null);

        String version = getLocalVersion();
        if (version == null) {
            return true;
        }

        String remoteVersion = getRemoteVersion();
        if (!version.equals(remoteVersion)) {
            return true;
        }

        return false;
    }

    private File getLocalVersionFile() {
        return new File(Launcher.get().getGameDirectory(), LOCAL_VERSION_FILE);
    }

    private String getLocalVersion() {
        BufferedReader reader = null;
        try {
            File versionFile = getLocalVersionFile();
            if (!versionFile.exists()) {
                return null;
            }

            reader = new BufferedReader(new FileReader(versionFile));
            return reader.readLine();
        } catch (FileNotFoundException exc) {
            throw new RuntimeException(exc);
        } catch (IOException e) {
            return null;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private String getRemoteVersion() {
        BufferedReader reader = null;
        try {
            URL url = Util.urlFor(Launcher.get().getConfig().getMode()
                    .getVersionURL());
            InputStream stream = url.openStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            return reader.readLine();
        } catch (IOException _) {
            return null;
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public void updatePerformed() {
        FileWriter writer;
        try {
            writer = new FileWriter(getLocalVersionFile());
            writer.write(getRemoteVersion());
            writer.close();
        } catch (IOException _) {
        }
    }
}
