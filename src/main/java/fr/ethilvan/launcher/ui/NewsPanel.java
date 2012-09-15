package fr.ethilvan.launcher.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.NewsDownloader;
import fr.ethilvan.launcher.util.Util;

public class NewsPanel extends JPanel {

    private static final long serialVersionUID = 5869234355558740443L;

    private final Image bg;
    private final JTextPane textPane;

    public NewsPanel() {
        super();
        setOpaque(true);
        Image tmpBg = null;
        try {
            InputStream is = Launcher.class
                    .getResourceAsStream("/img/bg-news.jpg");
            if (is != null) {
                tmpBg = ImageIO.read(is);
                is.close();
            }
        } catch (IOException _) {
        }
        this.bg = tmpBg;

        final JProgressBar progressBar = new JProgressBar();
        this.textPane = new JTextPane();

        build(progressBar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                NewsDownloader newsDownloader =
                        new NewsDownloader(NewsPanel.this, progressBar);
                try {
                    newsDownloader.download();
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }
        }).start();
    }

    private void build(JProgressBar progressBar) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        progressBar.setPreferredSize(new Dimension(300, 18));
        progressBar.setIndeterminate(true);

        JPanel progressPane = new JPanel();
        progressPane.setOpaque(false);
        progressPane.setLayout(new GridBagLayout());
        progressPane.add(progressBar);
        add(progressPane);

        textPane.setVisible(false);
        textPane.setOpaque(false);
        textPane.setEditable(false);
        textPane.setContentType("text/html;charset=utf-8");

        textPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent event) {
                if (event.getEventType()
                        != HyperlinkEvent.EventType.ACTIVATED) {
                    return;
                }

                if (event.getURL() == null) {
                    return;
                }

                try {
                    Util.openURL(event.getURL());
                } catch (IOException _) {
                } catch (URISyntaxException _) {
                }
            }
        });

        JScrollPane newsScroll = new JScrollPane(textPane);
        newsScroll.getViewport().setOpaque(false);
        newsScroll.setOpaque(false);
        add(newsScroll);
    }

    public void displayNews(URL url, String news, JProgressBar progressBar) {
        textPane.getDocument().putProperty(
                Document.StreamDescriptionProperty, url);
        textPane.setText(news.toString());
        textPane.setCaretPosition(0);

        progressBar.getParent().setVisible(false);
        textPane.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        int imgWidth = bg.getWidth(null);
        int imgHeight = bg.getHeight(null);

        for (int x = 0; x < width; x += imgWidth) {
            for (int y = 0; y < height; y += imgHeight) {
                g.drawImage(bg, x, y, this);
            }
        }
    }
}