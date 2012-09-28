package fr.ethilvan.launcher;

import java.applet.Applet;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.mclauncher.LoginSession;
import com.sk89q.mclauncher.LoginSession.LoginException;
import com.sk89q.mclauncher.LoginSession.OutdatedLauncherException;
import com.sk89q.mclauncher.launch.GameAppletContainer;
import com.sk89q.mclauncher.launch.GameFrame;

import fr.ethilvan.launcher.config.Configuration;
import fr.ethilvan.launcher.config.Options;
import fr.ethilvan.launcher.ui.LauncherFrame;
import fr.ethilvan.launcher.ui.TaskDialog;
import fr.ethilvan.launcher.updater.UpdateChecker;
import fr.ethilvan.launcher.updater.Updater;
import fr.ethilvan.launcher.util.Encryption.EncryptionException;
import fr.ethilvan.launcher.util.OS;

public class Launcher {

    public static final String VERSION;
    private static Logger logger;

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

        logger = Logger.getLogger("fr.ethilvan.launcher");
        try {
            File dir = new File(getSettingsDir(), "logs"); 
            FileUtils.forceMkdir(dir);
            FileHandler fileHandler = new FileHandler(dir.getPath()
                    + "/launcher.%u.%g.log", 80, 10, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            if (version.contains("SNAPSHOT")) {
                logger.setLevel(Level.ALL);
            } else {
                logger.setLevel(Level.WARNING);
            }
        } catch (SecurityException exc) {
            exc.printStackTrace();
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        VERSION = version;
    }

    private static Launcher instance;

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

    public static Launcher get() {
        return instance;
    }

    public static File getSettingsDir() {
        File dir = new File(OS.get().getDataDir(), ".evlauncher");
        try {
            FileUtils.forceMkdir(dir);
        } catch (IOException exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE,
                            "Unable to create .evlauncher directory", exc);
        }

        return dir;
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .create();
    }

    private HttpClient client;
    private Options options;
    private Configuration config;
    private LauncherFrame frame;

    public Launcher() {
        System.setProperty("http.agent",
                "EthilVanLauncher/" + VERSION
                + " (" + OS.get().name() +
                "; +" + Provider.get().website + ")");

        client = new HttpClient();
        try {
            client.start();
        } catch (Exception exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE, "Unable to start http client", exc);
            client = null;
        }

        options = new Options();
        config = Configuration.load();
    }

    public Options getOptions() {
        return options;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setFrame(LauncherFrame frame) {
        if (this.frame != null) {
            throw new UnsupportedOperationException();
        }

        this.frame = frame;
    }

    public void download(HttpExchange exchange) throws IOException {
        if (client != null) {
            client.send(exchange);
        }
    }

    public File getGameDirectory() {
        File file = new File(OS.get().getDataDir(),
                config.getMode().getDirectory());

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
        dialog.setStatus("Authentification ...", null);
        LoginSession session = new LoginSession(name);
        String passwordStr = new String(password);
        for (int i = 0; i < password.length; i++) {
            password[i] = 0;
        }

        try {
            if (session.login(passwordStr)) {
                if (options.getRememberMe()) {
                    try {
                        config.rememberAccount(name, passwordStr);
                    } catch (EncryptionException exc) {
                        Logger.getLogger(Launcher.class.getName())
                                .log(Level.WARNING,
                                        "Unable to store account informations",
                                        exc);
                    }
                }
                config.save();

                update(dialog, session);
            } else {
                dialog.setError("Nom d'utilisateur ou mot de passe invalide");
            }
        } catch (IOException exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE, "Unable to login", exc);
        } catch (OutdatedLauncherException exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE, "Unable to login", exc);
        } catch (LoginException exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE, "Unable to login", exc);
        }
    }

    public void update(TaskDialog dialog, LoginSession session) {
        final UpdateChecker checker = new UpdateChecker();
        if (!options.getForceUpdate() && !checker.needUpdate(dialog)) {
            launch(dialog, session);
            return;
        }

        Updater updater = new Updater(checker, dialog);
        updater.perform();
        launch(dialog, session);
    }

    public void launch(TaskDialog dialog, LoginSession session) {
        File dir = getGameDirectory();
        ClassLoader classLoader = setupClassLoader(dir);

        try {
            Class<?> klass = classLoader.loadClass(
                    "net.minecraft.client.MinecraftApplet");
            Applet applet = (Applet) klass.newInstance();

            GameFrame gameFrame = new GameFrame(new Dimension(854, 480));
            gameFrame.setDefaultCloseOperation(
                    WindowConstants.DISPOSE_ON_CLOSE);
            gameFrame.setTitle("Ethil Van");
            gameFrame.setVisible(true);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("stand-alone", "true");
            params.put("username", session.getUsername());
            params.put("sessionid", session.getSessionId());
            if (options.getQuickLaunch()) {
                String server = config.getMode().getServer();
                String[] info = server.split(":");
                params.put("server", info[0]);
                params.put("port", info.length > 1 ? info[1] : "25565");
            }

            dialog.dispose();
            frame.dispose();
            try {
                client.stop();
            } catch (Exception exc) {
                Logger.getLogger(Launcher.class.getName())
                        .log(Level.SEVERE, "Unable to stop http client", exc);
            }

            instance = null;
            logger = null;
            frame = null;
            client = null;
            options = null;
            config = null;

            GameAppletContainer container = new GameAppletContainer(params,
                    applet);
            gameFrame.start(container);
        } catch (ClassNotFoundException exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE, "Unable to initialize Minecraft", exc);
        } catch (InstantiationException exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE, "Unable to initialize Minecraft", exc);
        } catch (IllegalAccessException exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE, "Unable to initialize Minecraft", exc);
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
                Logger.getLogger(Launcher.class.getName())
                        .log(Level.SEVERE, "Unable to parse jarfile url", exc);
            }
            i++;
        }

        URLClassLoader classLoader = new URLClassLoader(urls);

        Class<?> minecraft;
        try {
            minecraft = classLoader.loadClass(
                    "net.minecraft.client.Minecraft");
            Field[] fields = minecraft.getDeclaredFields();

            boolean found = false;
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())
                        && field.getType().isAssignableFrom(File.class)) {
                    found = true;
                    Field.setAccessible(new Field[] { field }, true);
                    field.set(null, getGameDirectory());
                    break;
                }
            }

            if (!found) {
                Logger.getLogger(Launcher.class.getName())
                        .log(Level.SEVERE,
                                "Unable to set Minecraft directory");
                System.exit(1);
            }
        } catch (ClassNotFoundException exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE, "Unable to initialize class loader",
                            exc);
        } catch (IllegalArgumentException exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE, "Unable to initialize class loader",
                            exc);
        } catch (IllegalAccessException exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE, "Unable to initialize class loader",
                            exc);
        }

        return classLoader;
    }
}
