package fr.ethilvan.launcher.mode;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultBoundedRangeModel;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.config.Config;
import fr.ethilvan.launcher.ui.TaskDialog;
import fr.ethilvan.launcher.util.Download;
import fr.ethilvan.launcher.util.Util;

public class ModeDownload extends Download<ByteArrayOutputStream> {

    private final TaskDialog dialog;
    private DefaultBoundedRangeModel model;

    public ModeDownload(String url, ByteArrayOutputStream output,
            TaskDialog dialog) {
        super(url, output);
        this.dialog = dialog;
    }

    @Override
    protected void onError(Download.Error error) {
        dialog.setError("Erreur: " + error.getMessage());
        Logger.getLogger(Config.class.getName()).log(Level.WARNING,
                "Cannot download mode informations" + error.getMessage());
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
            mode = Launcher.getGson().fromJson(json, Mode.class);
        } catch (UnsupportedEncodingException exc) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE,
                    "Unable to read downloaded mode", exc);
        }

        try {
            Modes modes = Launcher.get().getConfig().getModes();
            modes.addMode(mode);
            dialog.dispose();
        } catch (AlreadyRegisteredMode exc) {
            dialog.setError("Le mode \"" + mode.getName() + "\" existe déjà.");
        }
    }
}
