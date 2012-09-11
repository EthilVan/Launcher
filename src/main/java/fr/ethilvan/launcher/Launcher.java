package fr.ethilvan.launcher;

import javax.swing.SwingUtilities;

import fr.ethilvan.launcher.ui.LauncherFrame;

public class Launcher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                LauncherFrame frame = new LauncherFrame();
                frame.setVisible(true);
            }
        });
    }
}
