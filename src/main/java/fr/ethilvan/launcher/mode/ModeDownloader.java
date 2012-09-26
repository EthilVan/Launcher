package fr.ethilvan.launcher.mode;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.swing.DefaultBoundedRangeModel;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.ui.TaskDialog;
import fr.ethilvan.launcher.util.BasicDownloader;
import fr.ethilvan.launcher.util.Util;

public class ModeDownloader extends BasicDownloader<ByteArrayOutputStream> {

    private final TaskDialog dialog;
    private DefaultBoundedRangeModel model;

    public ModeDownloader(String url, ByteArrayOutputStream output,
            TaskDialog dialog) {
        super(url, output);
        this.dialog = dialog;
    }

    @Override
    protected void onError(int code, String reason) {
        dialog.setError("Erreur: " + code + " - " + reason);
    }

    @Override
    protected void onLengthKnown(int length) {
        model = new DefaultBoundedRangeModel(0, 0, 0, length);
        dialog.setStatus("Téléchargement des informations du mode", model);
    }

    @Override
    protected void onProgress(int progress) {
        model.setValue(model.getValue() + progress);
    }

    @Override
    protected void onComplete(ByteArrayOutputStream output) {
        Mode mode = null;
        try {
            String json = output.toString(Util.UTF8);
            mode = Launcher.getGson()
                    .fromJson(json, Mode.class);
        } catch (UnsupportedEncodingException exc) {
            throw Util.wrap(exc);
        }

        try {
            Modes modes = Launcher.get().getConfig().getModes();
            modes.addMode(mode);
            dialog.dispose();
        } catch (AlreadyRegisteredMode e) {
            dialog.setError("Le mode \"" + mode.getName()
                    + "\" existe déjà.");
        }
    }
}
