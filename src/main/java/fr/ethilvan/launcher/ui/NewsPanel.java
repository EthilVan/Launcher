package fr.ethilvan.launcher.ui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import fr.ethilvan.launcher.Launcher;

public class NewsPanel extends JPanel {

    private static final long serialVersionUID = 5869234355558740443L;

    private final Image bg;

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
        bg = tmpBg;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        StringBuilder news = new StringBuilder();
        InputStream is = Launcher.class
                .getResourceAsStream("/news.txt");
        if (is != null) {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));
                char[] temp = new char[1024];
                int length = 0;
                while((length = reader.read(temp)) > 0) {
                    news.append(temp, 0, length);
                }
                reader.close();
            } catch (IOException _) {
            }
        }

        JTextPane textPane = new JTextPane();
        textPane.setOpaque(false);
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setText(news.toString());
        textPane.setCaretPosition(0);
        JScrollPane newsScroll = new JScrollPane(textPane);
        newsScroll.getViewport().setOpaque(false);
        newsScroll.setOpaque(false);
        add(newsScroll);
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
