package fr.ethilvan.launcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JProgressBar;

import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.io.Buffer;

import fr.ethilvan.launcher.ui.NewsPanel;
import fr.ethilvan.launcher.util.EthilVan;
import fr.ethilvan.launcher.util.Util;

public class NewsDownloader extends HttpExchange {

    private final NewsPanel newsPanel;
    private final JProgressBar progressBar;
    private final ByteArrayOutputStream output;

    public NewsDownloader(NewsPanel newsPanel, JProgressBar progressBar) {
        super();
        this.newsPanel = newsPanel;
        this.progressBar = progressBar;

        setURL(EthilVan.NEWS);
        output = new ByteArrayOutputStream();
    }

    public void download() {
        try {
            Launcher.get().getHttpClient().send(this);
        } catch (IOException exc) {
            throw Util.wrap(exc);
        }
    }

    @Override
    protected void onResponseHeader(Buffer name, Buffer value) {
        if (name.toString().equals("Content-Length")) {
            int length = Integer.parseInt(value.toString());
            BoundedRangeModel range = new DefaultBoundedRangeModel(0, 0, 0,
                    (int) length);
            progressBar.setIndeterminate(false);
            progressBar.setModel(range);
            progressBar.setValue(0);
        }
    }

    @Override
    protected void onResponseContent(Buffer content) {
        int length = content.length();
        try {
            content.writeTo(output);
        } catch (IOException exc) {
            throw Util.wrap(exc);
        }
        progressBar.setValue(progressBar.getValue() + length);
    }

    @Override
    protected void onResponseComplete() {
        try {
            newsPanel.displayNews(Util.urlFor(EthilVan.NEWS),
                    output.toString(Util.UTF8), progressBar);
        } catch (UnsupportedEncodingException exc) {
            throw Util.wrap(exc);
        }
    }
}
