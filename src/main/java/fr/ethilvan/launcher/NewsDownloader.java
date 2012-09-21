package fr.ethilvan.launcher;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JProgressBar;

import fr.ethilvan.launcher.ui.NewsPanel;
import fr.ethilvan.launcher.util.BasicDownloader;
import fr.ethilvan.launcher.util.EthilVan;
import fr.ethilvan.launcher.util.Util;

public class NewsDownloader extends BasicDownloader<ByteArrayOutputStream> {

    private final NewsPanel newsPanel;
    private final JProgressBar progressBar;

    public NewsDownloader(NewsPanel newsPanel, JProgressBar progressBar) {
        super(EthilVan.NEWS, new ByteArrayOutputStream());
        this.newsPanel = newsPanel;
        this.progressBar = progressBar;
    }

    @Override
    protected void onError(int code, String reason) {
        cancel();
        newsPanel.displayNews("<html><b><center>"
               + "Impossible d'afficher les news. ("
               + code + ": " + reason
               + ")</center></b></html>",
               progressBar);
    }

    @Override
    protected void onLengthKnown(int length) {
        BoundedRangeModel range = new DefaultBoundedRangeModel(0, 0, 0,
                length);
        progressBar.setIndeterminate(false);
        progressBar.setModel(range);
        progressBar.setValue(0);
    }

    @Override
    protected void onProgress(int progress) {
        progressBar.setValue(progressBar.getValue() + progress);
    }

    @Override
    protected void onComplete(ByteArrayOutputStream output) {
        try {
            newsPanel.displayNews(output.toString(Util.UTF8), progressBar);
        } catch (UnsupportedEncodingException exc) {
            throw Util.wrap(exc);
        }
    }
}
