package fr.ethilvan.launcher.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.util.Util;

public class Logo extends JPanel {

    private static final long serialVersionUID = 2153410360429608890L;

    private final Image img;

    public Logo() {
        super();
        setOpaque(false);

        Image tmpImg = null;
        try {
            InputStream is = Launcher.class
                    .getResourceAsStream("/img/logo.png");
            if (is != null) {
                tmpImg = ImageIO.read(is);
                is.close();
            }
        } catch (IOException _) {
        }
        img = tmpImg;

        if (img != null) {
            setPreferredSize(new Dimension(img.getWidth(null) + 30,
                    img.getHeight(null)));
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                Util.openProviderWebsite();
            }
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int y = (getHeight() - img.getHeight(null)) / 2;
        g.drawImage(img, 30, y, this);
    }
}
