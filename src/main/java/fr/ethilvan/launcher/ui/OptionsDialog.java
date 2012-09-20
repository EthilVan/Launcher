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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(panel);

        JToggleButton forceUpdate = new JToggleButton("Forcer la mise à jour");
        forceUpdate.setSelected(Launcher.get().getForceUpdate());
        GridBagConstraints forceUpdateC = new GridBagConstraints();
        forceUpdateC.insets = new Insets(0, 0, 10, 4);
        forceUpdateC.gridx = 0;
        forceUpdateC.gridy = 0;
        panel.add(forceUpdate, forceUpdateC);

        JComboBox providers = new JComboBox(
                Launcher.get().getOptions().getProviders());
        GridBagConstraints providersC = new GridBagConstraints();
        providersC.insets = new Insets(0, 4, 10, 0);
        providersC.gridx = 1;
        providersC.gridy = 0;
        panel.add(providers, providersC);

        JLabel directoryLabel = new JLabel("Dossier du jeu : ",
                SwingConstants.RIGHT);
        GridBagConstraints directoryLabelC = new GridBagConstraints();
        directoryLabelC.anchor = GridBagConstraints.LINE_START;
        directoryLabelC.insets = new Insets(0, 0, 6, 0);;
        directoryLabelC.gridx = 0;
        directoryLabelC.gridy = 2;
        panel.add(directoryLabel, directoryLabelC);

        final JLabel directory = new JLabel("<html><a href=\"\">"
                + Launcher.get().getGameDirectory() + "</a></html>");
        directory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        GridBagConstraints directoryC = new GridBagConstraints();
        directoryC.anchor = GridBagConstraints.LINE_END;
        directoryC.insets = new Insets(0, 0, 10, 0);;
        directoryC.gridx = 0;
        directoryC.gridy = 3;
        directoryC.gridwidth = 2;
        panel.add(directory, directoryC);

        JLabel versionLabel = new JLabel("Version du launcher : ");
        GridBagConstraints versionLabelC = new GridBagConstraints();
        versionLabelC.anchor = GridBagConstraints.LINE_START;
        versionLabelC.insets = new Insets(0, 0, 6, 0);;
        versionLabelC.gridx = 0;
        versionLabelC.gridy = 5;
        panel.add(versionLabel, versionLabelC);

        JLabel version = new JLabel(Launcher.VERSION);
        GridBagConstraints versionC = new GridBagConstraints();
        versionC.anchor = GridBagConstraints.FIRST_LINE_END;
        versionC.insets = new Insets(0, 0, 10, 0);;
        versionC.gridx = 1;
        versionC.gridy = 5;
        panel.add(version, versionC);

        JButton done = new JButton("Terminer");
        GridBagConstraints doneC = new GridBagConstraints();
        doneC.anchor = GridBagConstraints.LAST_LINE_END;
        doneC.insets = new Insets(6, 0, 0, 0);
        doneC.gridx = 1;
        doneC.gridy = 7;
        panel.add(done, doneC);

        pack();
        setLocationRelativeTo(frame);

        forceUpdate.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                Launcher.get().setForceUpdate(
                        event.getStateChange() == ItemEvent.SELECTED);
            }
        });

        providers.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                directory.setText("<html><a href=\"\">"
                        + Launcher.get().getGameDirectory() + "</a></html>");
                OptionsDialog.this.pack();
            }
        });

        directory.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                Util.openURI(Launcher.get().getGameDirectory().toURI());
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
