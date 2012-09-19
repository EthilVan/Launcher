package fr.ethilvan.launcher.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.client.HttpClient;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.ui.TaskDialog;
import fr.ethilvan.launcher.util.Util;

public class Updater {

    private final TaskDialog dialog;
    private final HttpClient client;
    private final UpdateChecker updateChecker;

    private File tmpDir;
    private int downloadsCount;

    public Updater(UpdateChecker checker, TaskDialog dialog) {
        this.updateChecker = checker;
        this.dialog = dialog;
        this.client = new HttpClient();
    }

    public boolean needUpdate() {
        return updateChecker.needUpdate();
    }

    public void perform() {
        tmpDir = new File(Launcher.get().getGameDirectory(), ".tmp");
        if (tmpDir.exists()) {
            FileUtils.deleteQuietly(tmpDir);
        }
        try {
            FileUtils.forceMkdir(tmpDir);
        } catch (IOException exc) {
            throw Util.wrap(exc);
        }

        try {
            client.start();
        } catch (Exception exc) {
            throw Util.wrap(exc); 
        }

        UpdateList updateList = new UpdateList(dialog) {
            @Override
            protected void onResponseComplete() {
                downloadAll(getDownloads());
            }
        };
        updateList.fetch(client);
    }

    private void downloadAll(DownloadInfo[] downloadsInfo) {
        downloadsCount = downloadsInfo.length;
        for (DownloadInfo downloadInfo : downloadsInfo) {
            Download download = new Download(dialog, downloadInfo) {
                @Override
                protected void onResponseComplete() {
                    super.onResponseComplete();
                    onDownloadComplete(info);
                }
            };
            download.start(client, tmpDir);
        }
    }

    private void onDownloadComplete(DownloadInfo info) {
        try {
            File tmpFile = info.getTemp(tmpDir);
            File targetPath = new File(Launcher.get().getGameDirectory(),
                    info.getPath());
            InputStream input = new FileInputStream(tmpFile);
            info.getFilter().filter(dialog, input, targetPath);
        } catch (IOException exc) {
            throw Util.wrap(exc);
        }

        if (--downloadsCount == 0) {
            FileUtils.deleteQuietly(tmpDir);
            dialog.dispose();
            updateChecker.updatePerformed();
        }
    }
}
