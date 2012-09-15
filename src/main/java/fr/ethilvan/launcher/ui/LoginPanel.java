package fr.ethilvan.launcher.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import fr.ethilvan.launcher.Launcher;

public class LoginPanel extends JPanel {

    private static final long serialVersionUID = 2960119737707517234L;

    private final Image bg;

    public LoginPanel() {
        super();

        Image tmpBg = null;
        try {
            InputStream is = Launcher.class
                    .getResourceAsStream("/img/leaves.png");
            if (is != null) {
                tmpBg = ImageIO.read(is);
                is.close();
            }
        } catch (IOException _) {
        }
        bg = tmpBg;

        setLayout(new BorderLayout());

        add(new Logo(), BorderLayout.LINE_START);
        add(new LoginForm(), BorderLayout.LINE_END);
        setMaximumSize(new Dimension(getMaximumSize().width,
                getPreferredSize().height));
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

        if (g instanceof Graphics2D) {
            Graphics2D g2D = (Graphics2D) g;

            g2D.setPaint(Color.BLACK);
            g2D.fillRect(0, 0, width, 2);
            g2D.setPaint(new Color(553648127, true));
            g2D.fillRect(2, 0, width, 3);

            GradientPaint gradient2 = new GradientPaint(
                    new Point2D.Float(0f, 0f), new Color(0, true),
                    new Point2D.Float(0f, height), new Color(1610612736, true));
            g2D.setPaint(gradient2);
            g2D.fillRect(0, 0, width, height);
        }
    }
}
