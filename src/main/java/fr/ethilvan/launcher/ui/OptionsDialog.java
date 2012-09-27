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
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.config.Configuration;
import fr.ethilvan.launcher.config.Options;
import fr.ethilvan.launcher.mode.ModeDownloader;
import fr.ethilvan.launcher.mode.Modes;
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
        tabbed.addTab("Modes", new ModesPanel());
        add(tabbed);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(frame);
    }

    class TabPanel extends JPanel {

        private static final long serialVersionUID = -1344334160948547435L;

        TabPanel() {
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        }
    }

    class GeneralPanel extends TabPanel {

        private static final long serialVersionUID = 8643168349333966777L;

        GeneralPanel() {
            super();
            Options options = Launcher.get().getOptions();
            Configuration config = Launcher.get().getConfig();

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
            forceUpdate.setSelected(options.getForceUpdate());
            GridBagConstraints forceUpdateC = new GridBagConstraints();
            forceUpdateC.insets = insets;
            forceUpdateC.anchor = GridBagConstraints.LINE_END;
            forceUpdateC.gridx = 1;
            forceUpdateC.gridy = 0;
            add(forceUpdate, forceUpdateC);

            JLabel defaultConfigLabel =
                    new JLabel("Utiliser la configuration recommandée :");
            GridBagConstraints defaultConfigLabelC = new GridBagConstraints();
            defaultConfigLabelC.insets = insets;
            defaultConfigLabelC.anchor = GridBagConstraints.LINE_START;
            defaultConfigLabelC.gridx = 0;
            defaultConfigLabelC.gridy = 1;
            add(defaultConfigLabel, defaultConfigLabelC);

            JToggleButton defaultConfig = new JCheckBox();
            defaultConfig.setOpaque(false);
            defaultConfig.setHorizontalTextPosition(SwingConstants.LEFT);
            defaultConfig.setSelected(config.getUseDefaultConfig());
            GridBagConstraints defaultConfigC = new GridBagConstraints();
            defaultConfigC.insets = insets;
            defaultConfigC.anchor = GridBagConstraints.LINE_END;
            defaultConfigC.gridx = 1;
            defaultConfigC.gridy = 1;
            add(defaultConfig, defaultConfigC);

            JLabel latestLWJGLLabel =
                    new JLabel("Utiliser la dernière version de LWJGL :");
            GridBagConstraints latestLWJGLLabelC = new GridBagConstraints();
            latestLWJGLLabelC.insets = insets;
            latestLWJGLLabelC.anchor = GridBagConstraints.LINE_START;
            latestLWJGLLabelC.gridx = 0;
            latestLWJGLLabelC.gridy = 2;
            add(latestLWJGLLabel, latestLWJGLLabelC);

            JToggleButton latestLWJGL = new JCheckBox();
            latestLWJGL.setOpaque(false);
            latestLWJGL.setHorizontalTextPosition(SwingConstants.LEFT);
            latestLWJGL.setSelected(config.getUseLatestLWJGL());
            GridBagConstraints latestLWJGLC = new GridBagConstraints();
            latestLWJGLC.insets = insets;
            latestLWJGLC.anchor = GridBagConstraints.LINE_END;
            latestLWJGLC.gridx = 1;
            latestLWJGLC.gridy = 2;
            add(latestLWJGL, latestLWJGLC);

            JLabel directoryLabel = new JLabel("Dossier du jeu : ",
                    SwingConstants.RIGHT);
            GridBagConstraints directoryLabelC = new GridBagConstraints();
            directoryLabelC.insets = insets;
            directoryLabelC.ipady = 8;
            directoryLabelC.anchor = GridBagConstraints.LINE_START;
            directoryLabelC.gridx = 0;
            directoryLabelC.gridy = 3;
            add(directoryLabel, directoryLabelC);

            final JLabel directory = new JLabel("<html><a href=\"\">"
                    + Launcher.get().getGameDirectory().getName() + "</a></html>");
            directory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            GridBagConstraints directoryC = new GridBagConstraints();
            directoryC.insets = insets;
            directoryC.ipady = 8;
            directoryC.anchor = GridBagConstraints.LINE_END;
            directoryC.gridx = 1;
            directoryC.gridy = 3;
            add(directory, directoryC);

            JLabel versionLabel = new JLabel("Version du launcher : ");
            GridBagConstraints versionLabelC = new GridBagConstraints();
            versionLabelC.insets = insets;
            versionLabelC.ipady = 8;
            versionLabelC.anchor = GridBagConstraints.LINE_START;
            versionLabelC.gridx = 0;
            versionLabelC.gridy = 4;
            add(versionLabel, versionLabelC);

            JLabel version = new JLabel(Launcher.VERSION);
            GridBagConstraints versionC = new GridBagConstraints();
            versionC.insets = insets;
            versionC.ipady = 8;
            versionC.anchor = GridBagConstraints.LINE_END;
            versionC.gridx = 1;
            versionC.gridy = 4;
            add(version, versionC);

            JButton done = new JButton("Terminer");
            GridBagConstraints doneC = new GridBagConstraints();
            doneC.anchor = GridBagConstraints.LAST_LINE_END;
            doneC.insets = new Insets(6, 0, 0, 0);
            doneC.gridx = 1;
            doneC.gridy = 5;
            add(done, doneC);

            forceUpdate.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent event) {
                    Launcher.get().getOptions().setForceUpdate(
                            event.getStateChange() == ItemEvent.SELECTED);
                }
            });

            defaultConfig.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent event) {
                    Launcher.get().getConfig().setUseDefaultConfig(
                            event.getStateChange() == ItemEvent.SELECTED);
                }
            });

            latestLWJGL.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent event) {
                    Launcher.get().getConfig().setUseLatestLWJGL(
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

    class ModesPanel extends TabPanel {

        private static final long serialVersionUID = -4138796647771985393L;

        ModesPanel() {
            super();

            setLayout(new GridBagLayout());

            Modes modes = Launcher.get().getConfig().getModes();
            JList modesList = new JList(modes);
            modesList.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            modesList.setSelectedIndex(modes.getSelectedIndex());

            GridBagConstraints modesListC = new GridBagConstraints();
            modesListC.insets = new Insets(0, 0, 3, 0);
            modesListC.fill = GridBagConstraints.BOTH;
            modesListC.weightx = 1.0;
            modesListC.weighty = 1.0;
            modesListC.gridy = 0;
            add(modesList, modesListC);

            final JTextField addUrl = new JTextField();
            GridBagConstraints addUrlC = new GridBagConstraints();
            addUrlC.insets = new Insets(0, 0, 3, 0);
            addUrlC.fill = GridBagConstraints.HORIZONTAL;
            addUrlC.weightx = 1.0;
            addUrlC.gridy = 1;
            add(addUrl, addUrlC);

            JButton addBtn = new JButton("Ajouter");
            GridBagConstraints addBtnC = new GridBagConstraints();
            addBtnC.anchor = GridBagConstraints.LINE_END;
            addBtnC.weightx = 1.0;
            addBtnC.gridy = 2;
            add(addBtn, addBtnC);

            addBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    final TaskDialog dialog = new TaskDialog(ModesPanel.this);

                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    String url = addUrl.getText();
                    try {
                        ModeDownloader modeDownloader = 
                                 new ModeDownloader(url, output, dialog);
                         Launcher.get().download(modeDownloader);
                    } catch (IllegalArgumentException exc) {
                        dialog.setError("URL invalide !");
                    } catch (IOException exc) {
                        dialog.setError("Impossible d'établir la connexion.");
                    }

                    dialog.setVisible(true);
                }
            });
        }
    }
}
