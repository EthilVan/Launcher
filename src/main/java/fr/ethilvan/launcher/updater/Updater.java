package fr.ethilvan.launcher.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.config.Config;
import fr.ethilvan.launcher.ui.TaskDialog;
import fr.ethilvan.launcher.util.OS;

public class Updater {

    private final TaskDialog dialog;
    private final UpdateChecker updateChecker;

    private File tmpDir;
    private int downloadsCount;
    private boolean done = false; 

    public Updater(UpdateChecker checker, TaskDialog dialog) {
        this.updateChecker = checker;
        this.dialog = dialog;
    }

    public void perform() {
        dialog.setStatus("Mise à jour", null);

        tmpDir = new File(Launcher.get().getGameDirectory(), ".tmp");
        if (tmpDir.exists()) {
            FileUtils.deleteQuietly(tmpDir);
        }
        try {
            FileUtils.forceMkdir(tmpDir);
        } catch (IOException exc) {
            Logger.getLogger(Updater.class.getName())
                    .log(Level.SEVERE, "Unable to create temporary directory"
                            + "to store downloaded files", exc);
        }

        PackageList updateList = new PackageList(dialog);
        updateList.fetch();
        try {
            updateList.waitForDone();
        } catch (InterruptedException _) {
        }

        downloadAll(updateList.getDownloads());
    }

    private void downloadAll(Package[] downloadsInfo) {
        downloadsCount = downloadsInfo.length;

        Set<String> tags = new HashSet<String>();
        tags.add(OS.get().name().toLowerCase());
        Config config = Launcher.get().getConfig();
        if (config.getUseDefaultConfig()) {
            tags.add("config");
        }
        tags.add(config.getUseLatestLWJGL() ? "lwjgl" : "lwjglold");

        for (Package downloadInfo : downloadsInfo) {
            if (!downloadInfo.isValid(tags)) {
                downloadsCount--;
                continue;
            }

            PackageDownload download = new PackageDownload(dialog, downloadInfo) {
                @Override
                protected void onResponseComplete() {
                    super.onResponseComplete();
                    onDownloadComplete(info);
                }
            };
            download.start(tmpDir);
        }

        while (!done) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException _) {
            }
        }
    }

    private void onDownloadComplete(Package info) {
        try {
            File tmpFile = info.getTemp(tmpDir);
            File targetPath = new File(Launcher.get().getGameDirectory(),
                    info.getPath());
            InputStream input = new FileInputStream(tmpFile);
            info.getFilter().filter(dialog, input, targetPath);
        } catch (IOException exc) {
            Logger.getLogger(Updater.class.getName())
                    .log(Level.SEVERE, "Unable to read downloaded file",
                            exc);
        }

        if (--downloadsCount == 0) {
            FileUtils.deleteQuietly(tmpDir);
            updateChecker.updatePerformed();
            done = true;
        }
    }
}
