package fr.ethilvan.launcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventObject;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JProgressBar;

import com.sk89q.mclauncher.DownloadListener;
import com.sk89q.mclauncher.DownloadProgressEvent;
import com.sk89q.mclauncher.HTTPDownloader;

import fr.ethilvan.launcher.ui.NewsPanel;
import fr.ethilvan.launcher.util.Util;

public class NewsDownloader implements DownloadListener {

    private static final String NEWS_URL = Util.ETHILVAN_FR + "/news/launcher";

    private final NewsPanel newsPanel;
    private final JProgressBar progressBar;
    private final ByteArrayOutputStream output;
    private final HTTPDownloader newsDownload;

    public NewsDownloader(NewsPanel newsPanel, JProgressBar progressBar) {
        this.newsPanel = newsPanel;
        this.progressBar = progressBar;

        try {
            output = new ByteArrayOutputStream();
            URL newsUrl = new URL(NEWS_URL);
            newsDownload = new HTTPDownloader(newsUrl, output);
            newsDownload.addDownloadListener(this);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    public void download() throws IOException {
        newsDownload.download();
    }

    @Override
    public void connectionStarted(EventObject event) {
    }

    @Override
    public void lengthKnown(EventObject event) {
        HTTPDownloader source = (HTTPDownloader) event.getSource();
        BoundedRangeModel range = new DefaultBoundedRangeModel(0, 0, 0,
                (int) source.getTotalLength());
        progressBar.setIndeterminate(false);
        progressBar.setModel(range);
        progressBar.setValue(0);
    }

    @Override
    public void downloadProgress(DownloadProgressEvent event) {
        progressBar.setValue((int) event.getDownloadedLength());
    }

    @Override
    public void downloadCompleted(EventObject event) {
        try {
            newsPanel.displayNews(new URL(NEWS_URL), output.toString("UTF-8"),
                    progressBar);
        } catch (UnsupportedEncodingException exc) {
            exc.printStackTrace();
        } catch (MalformedURLException exc) {
            exc.printStackTrace();
        }
    }
}
