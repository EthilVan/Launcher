package fr.ethilvan.launcher.updater;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;

import org.apache.commons.io.FileUtils;

import fr.ethilvan.launcher.ui.TaskDialog;
import fr.ethilvan.launcher.util.Download;

public class PackageDownload extends Download<OutputStream> {

    public static PackageDownload create(Updater updater, TaskDialog dialog,
            File tmpDir, Package ppackage) {
        try {
            File tmpFile = ppackage.getTemp(tmpDir);
            OutputStream output = FileUtils.openOutputStream(tmpFile);
            return new PackageDownload(updater, dialog, ppackage, output);
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        return null;
    }

    private final Updater updater;
    private final TaskDialog dialog;
    private final Package ppackage;
    private final String title;
    private final BoundedRangeModel range;

    private PackageDownload(Updater updater, TaskDialog dialog,
            Package ppackage, OutputStream output) {
        super(ppackage.url, output);
        this.updater = updater;
        this.dialog = dialog;
        this.ppackage = ppackage;
        this.title = "Téléchargement de " + ppackage.name + " ...";
        this.range = new DefaultBoundedRangeModel();

        dialog.setStatus(title, null);
    }

    @Override
    protected void onError(Error error) {
        updater.decrementDownloads();
    }

    @Override
    protected void onLengthKnown(int length) {
        range.setMinimum(0);
        range.setMaximum(length);
        range.setValue(0);
        dialog.setStatus(title, range);
    }

    @Override
    protected void onProgress(int progress) {
        range.setValue(range.getValue() + progress);
    }

    @Override
    protected void onComplete(OutputStream output) {
        super.onComplete(output);
        updater.decrementDownloads();
        updater.onDownloadComplete(ppackage);
    }
}
