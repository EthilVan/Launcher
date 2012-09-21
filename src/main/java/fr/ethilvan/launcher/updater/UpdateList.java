package fr.ethilvan.launcher.updater;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.io.Buffer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.ui.TaskDialog;

public class UpdateList extends HttpExchange {

    private static final String STATUS = "Récuperation des téléchargements.";

    private final TaskDialog dialog;
    private final BoundedRangeModel progress;

    private ByteArrayOutputStream output;

    public UpdateList(TaskDialog dialog) {
        super();
        this.dialog = dialog;
        this.progress = new DefaultBoundedRangeModel();
    }

    public void fetch(HttpClient client) {
        output = new ByteArrayOutputStream();
        setURL(Launcher.get().getOptions().getProvider().getListUrl());

        dialog.setStatus(STATUS, null);
        try {
            client.send(this);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    @Override
    protected void onResponseHeader(Buffer name, Buffer value) {
        if (name.toString().equals("Content-Length")) {
            int length = Integer.parseInt(value.toString());
            progress.setMinimum(0);
            progress.setMaximum(length);
            progress.setValue(0);
            dialog.setStatus(STATUS, progress);
        }
    }

    @Override
    protected void onResponseContent(Buffer content) {
        progress.setValue(progress.getValue() + content.length());
        dialog.setStatus(STATUS, progress);
        try {
            content.writeTo(output);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public DownloadInfo[] getDownloads() {
        String listJson = output.toString();
        IOUtils.closeQuietly(output);
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(listJson, DownloadInfo[].class);
    }
}
