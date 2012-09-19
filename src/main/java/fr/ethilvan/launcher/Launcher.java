package fr.ethilvan.launcher;

import java.applet.Applet;
import java.awt.Dimension;
import java.io.File;
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

import com.sk89q.mclauncher.LoginSession;
import com.sk89q.mclauncher.LoginSession.LoginException;
import com.sk89q.mclauncher.LoginSession.OutdatedLauncherException;
import com.sk89q.mclauncher.launch.GameAppletContainer;
import com.sk89q.mclauncher.launch.GameFrame;
import com.sk89q.mclauncher.security.X509KeyStore;

import fr.ethilvan.launcher.ui.TaskDialog;
import fr.ethilvan.launcher.ui.LauncherFrame;
import fr.ethilvan.launcher.updater.UpdateChecker;
import fr.ethilvan.launcher.updater.Updater;
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

    public void login(TaskDialog dialog, String name, char[] password) {
        dialog.setStatus("Logging in ...", null);
        LoginSession session = new LoginSession(name);
        String passwordStr = new String(password);
        for (int i = 0; i < password.length; i++) {
            password[i] = 0;
        }

        try {
            if (session.login(passwordStr)) {
                System.out.println("Logged in !");
                //update(dialog);
                launch(dialog, session);
            } else {
                System.out.println("Invalid login or password !");
                dialog.dispose();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutdatedLauncherException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public void update(TaskDialog dialog) {
        final UpdateChecker checker = new UpdateChecker();
        if (!checker.needUpdate()) {
            dialog.dispose();
            return;
        }

        Updater updater = new Updater(checker, dialog);
        updater.perform();
    }

    public void launch(TaskDialog dialog, LoginSession session) {
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

            dialog.dispose();
            Launcher.frame.dispose();
            GameAppletContainer container = new GameAppletContainer(params, applet);
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
            minecraft = classLoader.loadClass("net.minecraft.client.Minecraft");
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
