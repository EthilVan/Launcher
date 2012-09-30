package fr.ethilvan.launcher.ui;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.config.Config;
import fr.ethilvan.launcher.util.Encryption.EncryptionException;

public class LoginForm extends JPanel {

    private static final long serialVersionUID = 734863763316836264L;

    public LoginForm() {
        super();
        setSize(600, 120);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 12));

        setLayout(new GridBagLayout());

        Insets insets = new Insets(2, 4, 2, 0);
        Insets buttonInsets = new Insets(2, 10, 2, 0);

        GridBagConstraints usernameLabelC = new GridBagConstraints();
        usernameLabelC.fill = GridBagConstraints.HORIZONTAL;
        usernameLabelC.insets = insets;
        usernameLabelC.gridx = 0;
        usernameLabelC.gridy = 0;

        GridBagConstraints usernameC = new GridBagConstraints();
        usernameC.fill = GridBagConstraints.HORIZONTAL;
        usernameC.insets = insets;
        usernameC.gridx = 1;
        usernameC.gridy = 0;

        GridBagConstraints modesC = new GridBagConstraints();
        modesC.fill = GridBagConstraints.HORIZONTAL;
        modesC.insets = buttonInsets;
        modesC.gridx = 2;
        modesC.gridy = 0;

        GridBagConstraints optionsBtnC = new GridBagConstraints();
        optionsBtnC.insets = buttonInsets;
        optionsBtnC.gridx = 3;
        optionsBtnC.gridy = 0;

        GridBagConstraints passwordLabelC = new GridBagConstraints();
        passwordLabelC.fill = GridBagConstraints.HORIZONTAL;
        passwordLabelC.insets = insets;
        passwordLabelC.gridx = 0;
        passwordLabelC.gridy = 1;

        GridBagConstraints passwordC = new GridBagConstraints();
        passwordC.fill = GridBagConstraints.HORIZONTAL;
        passwordC.insets = insets;
        passwordC.gridx = 1;
        passwordC.gridy = 1;

        GridBagConstraints loginC = new GridBagConstraints();
        loginC.fill = GridBagConstraints.HORIZONTAL;
        loginC.insets = buttonInsets;
        loginC.gridx = 2;
        loginC.gridy = 1;
        loginC.gridwidth = 2;

        GridBagConstraints rememberMeC = new GridBagConstraints();
        rememberMeC.anchor = GridBagConstraints.LINE_END;
        rememberMeC.insets = new Insets(6, 0, 2, 0);
        rememberMeC.gridx = 1;
        rememberMeC.gridy = 2;

        GridBagConstraints quickLoginC = new GridBagConstraints();
        quickLoginC.fill = GridBagConstraints.HORIZONTAL;
        quickLoginC.insets = buttonInsets;
        quickLoginC.gridx = 2;
        quickLoginC.gridy = 2;
        quickLoginC.gridwidth = 2;

        JLabel usernameLabel = new JLabel("Identifiant :",
                SwingConstants.RIGHT);
        JLabel passwordLabel = new JLabel("Mot de passe :",
                SwingConstants.RIGHT);
        final JTextField username = new JTextField(16);
        final JPasswordField password = new JPasswordField(16);
        usernameLabel.setForeground(Color.WHITE);
        passwordLabel.setForeground(Color.WHITE);
        usernameLabel.setLabelFor(username);
        passwordLabel.setLabelFor(password);

        Config config = Launcher.get().getConfig();
        String rememberedUsername = config.getUsername();
        if (rememberedUsername != null) {
            username.setText(rememberedUsername);
            try {
                password.setText(config.getPassword());
            } catch (EncryptionException exc) {
                Logger.getLogger(LoginForm.class.getName())
                        .log(Level.WARNING, "Unable to decrypt password", exc);
            }
        }

        final JCheckBox rememberMe = new JCheckBox("Retenir le mot de passe");
        rememberMe.setOpaque(false);
        rememberMe.setForeground(Color.WHITE);
        rememberMe.setBorder(null);

        JComboBox modes = new JComboBox(config.getModes());
        modes.setOpaque(false);
        JButton optionsBtn = new JButton("Options");
        optionsBtn.setOpaque(false);
        JButton login = new JButton("Connexion");
        login.setOpaque(false);
        JButton quickLogin = new JButton("Connexion Rapide");
        quickLogin.setOpaque(false);

        rememberMe.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                Launcher.get().getOptions().setRememberMe(
                        event.getStateChange() == ItemEvent.SELECTED);
            }
        });

        optionsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                new OptionsDialog(getRootPane()).setVisible(true);
            }
        });

        ActionListener loginListener = new ActionListener() {
            public void actionPerformed(final ActionEvent event) {
                final TaskDialog dialog =
                        new TaskDialog(getRootPane());
                dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

                Launcher.get().getOptions().setQuickLaunch(
                        event.getActionCommand().contains("Rapide"));
                new Thread(new Runnable() {
                    public void run() {
                        Launcher.get().login(dialog, username.getText(),
                                password.getPassword());
                    }
                }).start();
                dialog.setVisible(true);
            }
        };

        login.addActionListener(loginListener);
        quickLogin.addActionListener(loginListener);

        add(usernameLabel, usernameLabelC);
        add(username, usernameC);
        add(optionsBtn, optionsBtnC);
        add(modes, modesC);
        add(passwordLabel, passwordLabelC);
        add(password, passwordC);
        add(login, loginC);
        add(rememberMe, rememberMeC);
        add(quickLogin, quickLoginC);
    }
}
