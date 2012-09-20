package fr.ethilvan.launcher;

import java.applet.Applet;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.io.IOUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.sk89q.mclauncher.LoginSession;
import com.sk89q.mclauncher.LoginSession.LoginException;
import com.sk89q.mclauncher.LoginSession.OutdatedLauncherException;
import com.sk89q.mclauncher.launch.GameAppletContainer;
import com.sk89q.mclauncher.launch.GameFrame;
import com.sk89q.mclauncher.security.X509KeyStore;

import fr.ethilvan.launcher.config.Options;
import fr.ethilvan.launcher.ui.TaskDialog;
import fr.ethilvan.launcher.ui.LauncherFrame;
import fr.ethilvan.launcher.updater.UpdateChecker;
import fr.ethilvan.launcher.updater.Updater;
import fr.ethilvan.launcher.util.EthilVan;
import fr.ethilvan.launcher.util.OS;
import fr.ethilvan.launcher.util.Util;

public class Launcher {

    public static final String VERSION;

    private static LauncherFrame frame;

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
                frame = new LauncherFrame();
                frame.setVisible(true);
            }
        });
    }

    private static Launcher instance;

    public static Launcher get() {
        return instance;
    }

    public static X509KeyStore getMinecraftLoginCert() {
        X509KeyStore keyStore = new X509KeyStore();
        InputStream certStream = Launcher.class
                .getResourceAsStream("/mclogin.cer");
        try {
            keyStore.addRootCertificates(certStream);
        } catch (CertificateException exc) {
            throw Util.wrap(exc);
        } catch (IOException exc) {
            throw Util.wrap(exc);
        }
        return keyStore;
    }

    private boolean forceUpdate;
    private final Options options;

    public Launcher() {
        System.setProperty("http.agent",
                "EthilVanLauncher/" + VERSION
                + " (" + OS.get().name() +
                "; +" + EthilVan.WEBSITE + ")");
        forceUpdate = false;

        File optionsFile = optionsFile();
        if (optionsFile.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(optionsFile);
                this.options = gson().fromJson(reader,
                        Options.class);
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
            this.options = new Options();
        }
    }

    private Gson gson() {
        return new GsonBuilder().setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .create();
    }

    private File optionsFile() {
        return new File(OS.get().getDataDir(), ".evlauncher.json");
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
                options.getProvider().getDirectory());

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

    public void saveOptions() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(optionsFile());
            gson().toJson(options, writer);
        } catch (JsonIOException exc) {
            throw Util.wrap(exc);
        } catch (IOException exc) {
            throw Util.wrap(exc);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public void login(TaskDialog dialog, String name, char[] password,
            boolean rememberMe, boolean quick) {
        dialog.setStatus("Logging in ...", null);
        LoginSession session = new LoginSession(name);
        String passwordStr = new String(password);
        for (int i = 0; i < password.length; i++) {
            password[i] = 0;
        }

        try {
            if (session.login(passwordStr)) {
                if (rememberMe) {
                    options.rememberAccount(name, passwordStr);
                }
                saveOptions();
                update(dialog, session, quick);
            } else {
                dialog.setLoginFailed();
            }
        } catch (IOException exc) {
            throw Util.wrap(exc);
        } catch (OutdatedLauncherException exc) {
            throw Util.wrap(exc);
        } catch (LoginException exc) {
            throw Util.wrap(exc);
        }
    }

    public void update(final TaskDialog dialog, final LoginSession session,
            final boolean quick) {
        final UpdateChecker checker = new UpdateChecker();
        if (!checker.needUpdate()) {
            launch(dialog, session, quick);
            return;
        }

        Updater updater = new Updater(checker, dialog) {
            @Override
            protected void onAllDownloadComplete() {
                launch(dialog, session, quick);
            }
        };
        updater.perform();
    }

    public void launch(TaskDialog dialog, LoginSession session,
            boolean quick) {
        File dir = getGameDirectory();
        ClassLoader classLoader = setupClassLoader(dir);

        try {
            Class<?> klass = classLoader.loadClass(
                    "net.minecraft.client.MinecraftApplet");
            Applet applet = (Applet) klass.newInstance();

            GameFrame frame = new GameFrame(new Dimension(854, 480));
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("stand-alone", "true");
            params.put("username", session.getUsername());
            params.put("sessionid", session.getSessionId());
            if (quick) {
                String server = options.getProvider().getServer();
                String[] info = server.split(":");
                params.put("server", info[0]);
                params.put("port", info.length > 1 ? info[1] : "25565");
            }

            dialog.dispose();
            Launcher.frame.dispose();
            GameAppletContainer container = new GameAppletContainer(params,
                    applet);
            frame.start(container);
        } catch (ClassNotFoundException exc) {
            throw Util.wrap(exc);
        } catch (InstantiationException exc) {
            throw Util.wrap(exc);
        } catch (IllegalAccessException exc) {
            throw Util.wrap(exc);
        }
    }

    private ClassLoader setupClassLoader(File dir) {
        System.setProperty("org.lwjgl.librarypath",
                new File(dir, "bin/natives").getAbsolutePath());
        System.setProperty("net.java.games.input.librarypath",
                new File(dir, "bin/natives").getAbsolutePath());

        List<File> files = new ArrayList<File>();
        files.add(new File(dir, "bin/lwjgl.jar"));
        files.add(new File(dir, "bin/jinput.jar"));
        files.add(new File(dir, "bin/lwjgl_util.jar"));
        files.add(new File(dir, "bin/minecraft.jar"));

        URL[] urls = new URL[files.size()];
        int i = 0;
        for (File file : files) {
            try {
                urls[i] = file.toURI().toURL();
            } catch (MalformedURLException exc) {
                throw Util.wrap(exc);
            }
            i++;
        }

        URLClassLoader classLoader = new URLClassLoader(urls);

        Class<?> minecraft;
        try {
            minecraft = classLoader.loadClass(
                    "net.minecraft.client.Minecraft");
            Field[] fields = minecraft.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())
                        && field.getType().isAssignableFrom(File.class)) {
                    Field.setAccessible(new Field[] { field }, true);
                    field.set(null, getGameDirectory());
                    return classLoader;
                }
            }

            throw new RuntimeException("!!!");
        } catch (ClassNotFoundException exc) {
            throw Util.wrap(exc);
        } catch (IllegalArgumentException exc) {
            throw Util.wrap(exc);
        } catch (IllegalAccessException exc) {
            throw Util.wrap(exc);
        }
    }
}
