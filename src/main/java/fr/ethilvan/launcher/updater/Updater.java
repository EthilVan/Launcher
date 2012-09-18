package fr.ethilvan.launcher.updater;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.client.HttpClient;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.ui.DownloadDialog;

public class Updater {

    private final DownloadDialog dialog;
    private final HttpClient client;
    private final UpdateChecker updateChecker;

    private File tmpDir;
    private int downloadsCount;

    public Updater(UpdateChecker checker, DownloadDialog dialog) {
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
        tmpDir.mkdir();

        try {
            client.start();
        } catch (Exception exc) {
            exc.printStackTrace();
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
            File file = new File(Launcher.get().getGameDirectory(),
                    info.getPath());
            file.getParentFile().mkdirs();
            FileUtils.copyFile(tmpFile, file);
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        if (--downloadsCount == 0) {
            FileUtils.deleteQuietly(tmpDir);
            dialog.dispose();
            updateChecker.updatePerformed();
        }
    }
}
