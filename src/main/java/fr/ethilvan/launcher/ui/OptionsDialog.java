package fr.ethilvan.launcher.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.util.Util;

public class OptionsDialog extends JDialog {

    private static final long serialVersionUID = -4589816185806932545L;

    public OptionsDialog(Component frame) {
        super();

        setTitle("Options");
        setModal(true);
        setResizable(false);
        setLayout(new GridBagLayout());

        Insets insets = new Insets(6, 12, 6, 12);

        JToggleButton forceUpdate = new JToggleButton("Forcer la mise à jour");
        forceUpdate.setSelected(Launcher.get().getForceUpdate());
        GridBagConstraints forceUpdateC = new GridBagConstraints();
        forceUpdateC.insets = new Insets(6, 12, 6, 4);
        forceUpdateC.gridx = 0;
        forceUpdateC.gridy = 0;
        add(forceUpdate, forceUpdateC);

        JToggleButton devMode = new JToggleButton("Mode développement");
        devMode.setSelected(Launcher.get().getOptions().isDevMode());
        GridBagConstraints devModeC = new GridBagConstraints();
        devModeC.insets = new Insets(6, 4, 6, 12);;
        devModeC.gridx = 1;
        devModeC.gridy = 0;
        add(devMode, devModeC);

        JLabel directoryLabel = new JLabel("Dossier du jeu : ",
                SwingConstants.RIGHT);
        GridBagConstraints directoryLabelC = new GridBagConstraints();
        directoryLabelC.anchor = GridBagConstraints.LINE_START;
        directoryLabelC.insets = insets;
        directoryLabelC.gridx = 0;
        directoryLabelC.gridy = 2;
        add(directoryLabel, directoryLabelC);

        final JLabel directory = new JLabel("<html><a href=\"\">"
                + Launcher.get().getGameDirectory() + "</a></html>");
        directory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        directory.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                try {
                    Util.openURI(Launcher.get().getGameDirectory().toURI());
                } catch (IOException _) {
                } catch (URISyntaxException _) {
                }
            }
        });
        GridBagConstraints directoryC = new GridBagConstraints();
        directoryC.anchor = GridBagConstraints.LINE_END;
        directoryC.insets = insets;
        directoryC.gridx = 0;
        directoryC.gridy = 3;
        directoryC.gridwidth = 2;
        add(directory, directoryC);

        JLabel versionLabel = new JLabel("Version du launcher : ");
        GridBagConstraints versionLabelC = new GridBagConstraints();
        versionLabelC.anchor = GridBagConstraints.LINE_START;
        versionLabelC.insets = insets;
        versionLabelC.gridx = 0;
        versionLabelC.gridy = 5;
        add(versionLabel, versionLabelC);

        JLabel version = new JLabel(Launcher.VERSION);
        GridBagConstraints versionC = new GridBagConstraints();
        versionC.anchor = GridBagConstraints.FIRST_LINE_END;
        versionC.insets = insets;
        versionC.gridx = 1;
        versionC.gridy = 5;
        add(version, versionC);

        JButton done = new JButton("Terminer");
        GridBagConstraints doneC = new GridBagConstraints();
        doneC.anchor = GridBagConstraints.LAST_LINE_END;
        doneC.insets = new Insets(12, 12, 6, 12);
        doneC.gridx = 1;
        doneC.gridy = 7;
        add(done, doneC);

        pack();
        setLocationRelativeTo(frame);

        forceUpdate.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                Launcher.get().setForceUpdate(
                        event.getStateChange() == ItemEvent.SELECTED);
            }
        });

        devMode.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                Launcher.get().getOptions().setDevMode(
                        event.getStateChange() == ItemEvent.SELECTED);
                directory.setText("<html><a href=\"\">"
                        + Launcher.get().getGameDirectory() + "</a></html>");
                OptionsDialog.this.pack();
            }
        });

        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                OptionsDialog.this.dispose();
            }
        });
    }
}
