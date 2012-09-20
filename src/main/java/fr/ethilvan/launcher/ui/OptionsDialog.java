package fr.ethilvan.launcher.ui;

import java.awt.Color;
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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
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

        JTabbedPane tabbed = new JTabbedPane();
        tabbed.addTab("Générales", new GeneralPanel());
        tabbed.addTab("Modes", new ProvidersPanel());
        add(tabbed);

        pack();
        setLocationRelativeTo(frame);
    }

    class TabPanel extends JPanel {

        private static final long serialVersionUID = -1344334160948547435L;

        TabPanel() {
            setBackground(Color.WHITE);
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        }
    }

    class GeneralPanel extends TabPanel {

        private static final long serialVersionUID = 8643168349333966777L;

        GeneralPanel() {
            super();
            Insets insets = new Insets(0, 0, 6, 0);

            JLabel forceUpdateLabel = new JLabel("Forcer la mise à jour :");
            GridBagConstraints forceUpdateLabelC = new GridBagConstraints();
            forceUpdateLabelC.insets = insets;
            forceUpdateLabelC.anchor = GridBagConstraints.LINE_START;
            forceUpdateLabelC.gridx = 0;
            forceUpdateLabelC.gridy = 0;
            add(forceUpdateLabel, forceUpdateLabelC);

            JCheckBox forceUpdate = new JCheckBox();
            forceUpdate.setOpaque(false);
            forceUpdate.setSelected(Launcher.get().getForceUpdate());
            GridBagConstraints forceUpdateC = new GridBagConstraints();
            forceUpdateC.insets = insets;
            forceUpdateC.anchor = GridBagConstraints.LINE_END;
            forceUpdateC.gridx = 1;
            forceUpdateC.gridy = 0;
            add(forceUpdate, forceUpdateC);

            JLabel latestLWJGLLabel =
                    new JLabel("Utiliser la dernière version de LWJGL :");
            GridBagConstraints latestLWJGLLabelC = new GridBagConstraints();
            latestLWJGLLabelC.insets = insets;
            latestLWJGLLabelC.anchor = GridBagConstraints.LINE_START;
            latestLWJGLLabelC.gridx = 0;
            latestLWJGLLabelC.gridy = 1;
            add(latestLWJGLLabel, latestLWJGLLabelC);

            JToggleButton latestLWJGL = new JCheckBox();
            latestLWJGL.setOpaque(false);
            latestLWJGL.setHorizontalTextPosition(SwingConstants.LEFT);
            latestLWJGL.setSelected(Launcher.get().getOptions()
                    .getUseLatestLWJGL());
            GridBagConstraints latestLWJGLC = new GridBagConstraints();
            latestLWJGLC.insets = insets;
            latestLWJGLC.anchor = GridBagConstraints.LINE_END;
            latestLWJGLC.gridx = 1;
            latestLWJGLC.gridy = 1;
            add(latestLWJGL, latestLWJGLC);

            JLabel directoryLabel = new JLabel("Dossier du jeu : ",
                    SwingConstants.RIGHT);
            GridBagConstraints directoryLabelC = new GridBagConstraints();
            directoryLabelC.insets = insets;
            directoryLabelC.ipady = 8;
            directoryLabelC.anchor = GridBagConstraints.LINE_START;
            directoryLabelC.gridx = 0;
            directoryLabelC.gridy = 2;
            add(directoryLabel, directoryLabelC);

            final JLabel directory = new JLabel("<html><a href=\"\">"
                    + Launcher.get().getGameDirectory().getName() + "</a></html>");
            directory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            GridBagConstraints directoryC = new GridBagConstraints();
            directoryC.insets = insets;
            directoryC.ipady = 8;
            directoryC.anchor = GridBagConstraints.LINE_END;
            directoryC.gridx = 1;
            directoryC.gridy = 2;
            add(directory, directoryC);

            JLabel versionLabel = new JLabel("Version du launcher : ");
            GridBagConstraints versionLabelC = new GridBagConstraints();
            versionLabelC.insets = insets;
            versionLabelC.ipady = 8;
            versionLabelC.anchor = GridBagConstraints.LINE_START;
            versionLabelC.gridx = 0;
            versionLabelC.gridy = 3;
            add(versionLabel, versionLabelC);

            JLabel version = new JLabel(Launcher.VERSION);
            GridBagConstraints versionC = new GridBagConstraints();
            versionC.insets = insets;
            versionC.ipady = 8;
            versionC.anchor = GridBagConstraints.LINE_END;
            versionC.gridx = 1;
            versionC.gridy = 3;
            add(version, versionC);

            JButton done = new JButton("Terminer");
            GridBagConstraints doneC = new GridBagConstraints();
            doneC.anchor = GridBagConstraints.LAST_LINE_END;
            doneC.insets = new Insets(6, 0, 0, 0);
            doneC.gridx = 1;
            doneC.gridy = 4;
            add(done, doneC);

            forceUpdate.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent event) {
                    Launcher.get().setForceUpdate(
                            event.getStateChange() == ItemEvent.SELECTED);
                }
            });

            forceUpdate.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent event) {
                    Launcher.get().getOptions().setUseLatestLWJGL(
                            event.getStateChange() == ItemEvent.SELECTED);
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

    class ProvidersPanel extends TabPanel {

        private static final long serialVersionUID = -4138796647771985393L;

        ProvidersPanel() {
            super();
        }
    }
}
