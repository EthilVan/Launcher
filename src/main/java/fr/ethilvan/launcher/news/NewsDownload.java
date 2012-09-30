package fr.ethilvan.launcher.news;

import java.io.OutputStream;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import fr.ethilvan.launcher.util.Download;

public abstract class NewsDownload<T extends OutputStream>
        extends Download<T> {

    private final JProgressBar progressBar;

    public NewsDownload(String url, T output, JProgressBar progressBar) {
        super(url, output);
        this.progressBar = progressBar;
    }

    @Override
    protected void onLengthKnown(final int length) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BoundedRangeModel model = progressBar.getModel();
                if (model.getExtent() != 13) {
                    model = new DefaultBoundedRangeModel(0, 13, 0, length);
                    progressBar.setModel(model);
                    progressBar.setIndeterminate(false);
                } else {
                    model.setMaximum(model.getMaximum() + length);
                }
            }
        });
    }

    @Override
    protected void onProgress(final int progress) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue(progressBar.getValue() + progress);
            }
        });
    }
}
