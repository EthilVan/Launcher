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
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import fr.ethilvan.launcher.config.Config;
import fr.ethilvan.launcher.config.Options;
import fr.ethilvan.launcher.ui.LauncherFrame;
import fr.ethilvan.launcher.ui.TaskDialog;
import fr.ethilvan.launcher.updater.UpdateChecker;
import fr.ethilvan.launcher.updater.Updater;
import fr.ethilvan.launcher.util.Encryption.EncryptionException;
import fr.ethilvan.launcher.util.OS;
import fr.ethilvan.launcher.util.OneLineLoggerFormatter;

public class Launcher {

    public static final String VERSION;
    private static Logger[] loggers;

    static {
        Package ppackage = Launcher.class.getPackage();

        String version;
        if (ppackage == null) {
            version = "inconnue";
        } else {
            version = ppackage.getImplementationVersion();
            if (version == null) {
                version = "inconnue";
            }
        }

        loggers = new Logger[3]; 
        loggers[0] = Logger.getLogger("fr.ethilvan.launcher");
        loggers[1] = Logger.getLogger("com.sk89q.mclauncher");
        loggers[2] = Logger.getLogger("org.xhtmlrenderer");
        try {
            Formatter formatter = new OneLineLoggerFormatter();

            Handler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);

            File dir = new File(getSettingsDir(), "logs"); 
            FileUtils.forceMkdir(dir);
            Handler fileHandler = new FileHandler(dir.getPath()
                    + "/launcher.%u.%g.log", 1000, 10, true);
            fileHandler.setFormatter(formatter);

            Level level;
            if (version.contains("SNAPSHOT") || version.equals("inconnue")) {
                level = Level.ALL;
            } else {
                level = Level.WARNING;
            }

            for (Logger logger : loggers) {
                logger.setUseParentHandlers(false);
                logger.addHandler(consoleHandler);
                logger.addHandler(fileHandler);
                logger.setLevel(level);
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
        Logger.getLogger(Launcher.class.getName())
            .info("Starting Ethil Van Launcher version " + VERSION);

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
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE,
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
    private Config config;
    private LauncherFrame frame;

    public Launcher() {
        System.setProperty("http.agent", "EthilVanLauncher/" + VERSION
                + " (" + OS.get().name() + "; +http://ethilvan.fr)");

        Logger.getLogger(Launcher.class.getName()).info("Starting HttpClient");
        client = new HttpClient();
        try {
            client.start();
        } catch (Exception exc) {
            Logger.getLogger(Launcher.class.getName())
                    .log(Level.SEVERE, "Unable to start http client", exc);
            client = null;
        }

        config = Config.load();
        options = new Options(config.isAccountRemembered());
    }

    public Options getOptions() {
        return options;
    }

    public Config getConfig() {
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
            Logger.getLogger(Launcher.class.getName())
                    .info("Starting download of \"" + exchange.getAddress()
                            + exchange.getRequestURI() + "\"");
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
        Logger.getLogger(Launcher.class.getName())
                .info("Logging in with username : " + name);

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
                } else {
                    config.forgetAccount();
                }
                config.save();

                update(dialog, session);
            } else {
                dialog.setError("Nom d'utilisateur ou mot de passe invalide.");
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
        Logger.getLogger(Launcher.class.getName()).info("Checking for update");
        final UpdateChecker checker = new UpdateChecker();
        if (!options.getForceUpdate() && !checker.needUpdate(dialog)) {
            launch(dialog, session);
            return;
        }

        Logger.getLogger(Launcher.class.getName()).info("Updating");
        Updater updater = new Updater(checker, dialog);
        updater.perform();
        launch(dialog, session);
    }

    public void launch(TaskDialog dialog, LoginSession session) {
        Logger.getLogger(Launcher.class.getName()).info("Launching");
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
        Logger.getLogger(Launcher.class.getName())
                .info("Setting up classpath");
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

    public void cleanUp() {
        Logger.getLogger(Launcher.class.getName()).info("Cleaning up");
        instance = null;
        loggers = null;
        frame = null;
        client = null;
        options = null;
        config = null;
        System.gc();
    }
}
