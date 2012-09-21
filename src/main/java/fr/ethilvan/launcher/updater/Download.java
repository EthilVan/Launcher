package fr.ethilvan.launcher.updater;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.io.Buffer;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.ui.TaskDialog;

public class Download extends HttpExchange {

    private final TaskDialog dialog;
    protected final DownloadInfo info;
    private final String title;
    private final BoundedRangeModel progress;

    private OutputStream output;

    public Download(TaskDialog dialog, DownloadInfo info) {
        super();
        this.dialog = dialog;
        this.info = info;
        this.title = "Téléchargement de " + info.getName() + " ...";
        this.progress = new DefaultBoundedRangeModel();
    }

    public void start(File tmpDir) {
        try {
            File tmpFile = info.getTemp(tmpDir);
            output = FileUtils.openOutputStream(tmpFile);
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        dialog.setStatus(title, null);
        setURL(info.getUrl());
        Launcher.get().download(this);
    }

    @Override
    protected void onResponseHeader(Buffer name, Buffer value) {
        if (name.toString().equals("Content-Length")) {
            int length = Integer.parseInt(value.toString());
            progress.setMinimum(0);
            progress.setMaximum(length);
            progress.setValue(0);
            dialog.setStatus(title, progress);
        }
    }

    @Override
    protected void onResponseContent(Buffer content) {
        progress.setValue(progress.getValue() + content.length());
        dialog.setStatus(title, progress);
        try {
            content.writeTo(output);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    @Override
    protected void onResponseComplete() {
        IOUtils.closeQuietly(output);
    }
}
