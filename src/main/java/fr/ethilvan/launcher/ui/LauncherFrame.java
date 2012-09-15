package fr.ethilvan.launcher.ui;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.UIManager;

import fr.ethilvan.launcher.Launcher;

public class LauncherFrame extends JFrame {

    private static final long serialVersionUID = -7209654571693605339L;

    public LauncherFrame() {
        super();
        build();
        pack();
    }

    private void build() {
        setTitle("Ethil Van Launcher");
        setMinimumSize(new Dimension(874, 520));
        setLocationRelativeTo(null);

        try {
            InputStream is = Launcher.class
                    .getResourceAsStream("/img/icon.png");
            if (is != null) {
                setIconImage(ImageIO.read(is));
            }
            is.close();
        } catch (IOException _) {
        }

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception _) {
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new NewsPanel());
        add(new LoginPanel());
    }
}
