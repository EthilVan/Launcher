package fr.ethilvan.launcher.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ScrollPaneConstants;

import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.simple.xhtml.XhtmlNamespaceHandler;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.FSMouseListener;
import org.xhtmlrenderer.swing.LinkListener;
import org.xhtmlrenderer.util.XRRuntimeException;

import fr.ethilvan.launcher.Provider;
import fr.ethilvan.launcher.news.ImageCache;
import fr.ethilvan.launcher.news.ImageCacheReplacedElementFactory;
import fr.ethilvan.launcher.news.NewsFetcher;
import fr.ethilvan.launcher.util.Util;

public class NewsPanel extends JPanel {

    private static final long serialVersionUID = 5869234355558740443L;

    private final XHTMLPanel xhtmlPane;
    private final FSScrollPane newsScroll;

    public NewsPanel() {
        super();
        setOpaque(true);
        setBackground(Color.decode("#141414"));

        final JProgressBar progressBar = new JProgressBar();
        this.xhtmlPane = new XHTMLPanel();
        this.newsScroll = new FSScrollPane(xhtmlPane);

        URL url;
        try {
            url = new URL(Provider.get().newsUrl);
            build(url, progressBar);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    NewsFetcher newsFetcher = new NewsFetcher();
                    newsFetcher.fetch(NewsPanel.this, progressBar);
                }
            }).start();
        } catch (MalformedURLException exc) {
            Logger.getLogger(NewsPanel.class.getName())
                    .log(Level.WARNING, "Can't load news", exc);
        }
    }

    private void build(URL url, JProgressBar progressBar) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        progressBar.setPreferredSize(new Dimension(300, 18));
        progressBar.setIndeterminate(true);

        JPanel progressPane = new JPanel();
        progressPane.setOpaque(false);
        progressPane.setLayout(new GridBagLayout());
        progressPane.add(progressBar);
        add(progressPane);

        xhtmlPane.setOpaque(false);
        xhtmlPane.setBorder(BorderFactory.createEmptyBorder());
        xhtmlPane.getSharedContext().getTextRenderer()
            .setSmoothingThreshold(0);
        for (Object listener : xhtmlPane.getMouseTrackingListeners()) {
            if (listener instanceof LinkListener) {
                xhtmlPane.removeMouseTrackingListener(
                        (FSMouseListener) listener);
            }
        }
        xhtmlPane.addMouseTrackingListener(new LinkListener() {
            public void linkClicked(BasicPanel panel, String uri) {
                Util.openURL(uri);
            }
        });

        newsScroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        newsScroll.setBorder(BorderFactory.createEmptyBorder());
        newsScroll.getViewport().setOpaque(false);
        newsScroll.setOpaque(false);
        newsScroll.setVisible(false);
        add(newsScroll);
    }

    public void displayNews(ImageCache cache, String news,
            JProgressBar progressBar) {
        ReplacedElementFactory delegate =
                xhtmlPane.getSharedContext().getReplacedElementFactory();
        ReplacedElementFactory factory =
                new ImageCacheReplacedElementFactory(cache, delegate);
        xhtmlPane.getSharedContext().setReplacedElementFactory(factory);
        try {
            xhtmlPane.setDocumentFromString(news, Provider.get().newsUrl,
                new XhtmlNamespaceHandler());
            ensureNewsVisible(progressBar);
        } catch (XRRuntimeException exc) {
            displayError(progressBar);
        }
    }

    public void displayError(JProgressBar progressBar) {
        xhtmlPane.setDocumentFromString(errorPage(), Provider.get().newsUrl,
                new XhtmlNamespaceHandler());
        ensureNewsVisible(progressBar);
    }

    private void ensureNewsVisible(JProgressBar progressBar) {
        newsScroll.setVisible(true);
        progressBar.getParent().setVisible(false);
    }

    private String errorPage() {
        return
          "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\""
              + " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
          + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
          + "  <head>\n"
          + "    <meta http-equiv=\"content-type\""
              + " content=\"text/html; charset=UTF-8\" />\n"
          + "  </head>\n"
          + "  <body style=\"color: #666;\">\n"
          + "    <h3><center>Impossible d'afficher les news.</center></h3>\n"
          + "  </body>\n"
          + "</html>";
    }
}
