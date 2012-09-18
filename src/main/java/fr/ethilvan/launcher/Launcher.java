package fr.ethilvan.launcher;

import java.io.File;

import javax.swing.SwingUtilities;

import fr.ethilvan.launcher.ui.LauncherFrame;
import fr.ethilvan.launcher.util.OS;
import fr.ethilvan.launcher.util.Util;

public class Launcher {

    public static final String VERSION;

    static {
        Package ppackage = Launcher.class.getPackage();
        String version;
        if (ppackage == null) {
            version = "(inconnue)";
        } else {
            version = ppackage.getImplementationVersion();
            if (version == null) {
                version = "(inconnue)";
            }
        }

        VERSION = version;
    }

    public static void main(String[] args) {
        instance = new Launcher();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                LauncherFrame frame = new LauncherFrame();
                frame.setVisible(true);
            }
        });
    }

    private static Launcher instance;

    public static Launcher get() {
        return instance;
    }

    private boolean forceUpdate;
    private final Options options;

    public Launcher() {
        System.setProperty("http.agent",
                "EthilVanLauncher/" + VERSION
                + " (" + OS.get().name() +
                "; +" + Util.ETHILVAN_FR + ")");
        forceUpdate = false;
        this.options = new Options();
    }

    public boolean getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public Options getOptions() {
        return options;
    }

    public File getGameDirectory() {
        File file = new File(OS.get().getDataDir(),
                options.getEthilVanFolder());

        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("Impossible de créer le dossier : "
                        + file.toString());
            }
        } else if (!file.isDirectory()) {
            throw new RuntimeException(file.toString()
                    + " n'est pas un dossier.");
        }

        return file;
    }
}
