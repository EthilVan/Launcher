package fr.ethilvan.launcher.updater;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.ui.TaskDialog;
import fr.ethilvan.launcher.util.Download;
import fr.ethilvan.launcher.util.Util;

public class UpdateDownload extends Download<ByteArrayOutputStream> {

    private static final String STATUS = "Récuperation des téléchargements";

    private final TaskDialog dialog;
    private final BoundedRangeModel range;

    private Update list;

    public UpdateDownload(TaskDialog dialog) {
        super(Launcher.get().getConfig().getMode().getListUrl(),
                new ByteArrayOutputStream());
        this.dialog = dialog;
        this.range = new DefaultBoundedRangeModel();
        this.list = null;

        dialog.setStatus(STATUS, null);
    }

    @Override
    public void onError(Error error) {
        
    }

    @Override
    public void onLengthKnown(int length) {
        range.setMinimum(0);
        range.setMaximum(length);
        range.setValue(0);
        dialog.setStatus(STATUS, range);
    }

    @Override
    protected void onProgress(int progress) {
        range.setValue(range.getValue() + progress);
    }

    public void onComplete(ByteArrayOutputStream output) {
        try {
            String listJson = output.toString(Util.UTF8);
            Gson gson = new GsonBuilder().create();
            list = gson.fromJson(listJson, Update.class);
        } catch (UnsupportedEncodingException exc) {
            Logger.getLogger(UpdateDownload.class.getName())
                    .log(Level.SEVERE,
                            "Unable to convert packages list to UTF-8", exc);
        } catch (JsonParseException exc) {
            Logger.getLogger(UpdateDownload.class.getName())
                    .log(Level.SEVERE, "Unable to parse packages list", exc);
        }
    }

    public Update getPackageList() {
        return list;
    }
}
