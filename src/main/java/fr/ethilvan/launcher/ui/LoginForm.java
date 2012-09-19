package fr.ethilvan.launcher.ui;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.ethilvan.launcher.Launcher;

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

        GridBagConstraints optionsC = new GridBagConstraints();
        optionsC.fill = GridBagConstraints.HORIZONTAL;
        optionsC.insets = buttonInsets;
        optionsC.gridx = 2;
        optionsC.gridy = 0;

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

        GridBagConstraints rememberMeC = new GridBagConstraints();
        rememberMeC.anchor = GridBagConstraints.LINE_END;
        rememberMeC.insets = new Insets(6, 0, 2, 0);
        rememberMeC.gridx = 2;
        rememberMeC.gridy = 2;

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

        JCheckBox rememberMe = new JCheckBox("Retenir le mot de passe");
        rememberMe.setOpaque(false);
        rememberMe.setForeground(Color.WHITE);
        rememberMe.setBorder(null);

        JButton options = new JButton("Options");
        options.setOpaque(false);
        JButton login = new JButton("Connexion");
        login.setOpaque(false);

        options.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                new OptionsDialog(getRootPane()).setVisible(true);
            }
        });

        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                final TaskDialog dialog =
                        new TaskDialog(getRootPane());
                dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                new Thread(new Runnable() {
                    public void run() {
                        Launcher.get().login(dialog, username.getText(),
                                password.getPassword());
                    }
                }).start();
                dialog.setVisible(true);
            }
        });

        add(usernameLabel, usernameLabelC);
        add(username, usernameC);
        add(options, optionsC);
        add(passwordLabel, passwordLabelC);
        add(password, passwordC);
        add(login, loginC);
        add(rememberMe, rememberMeC);
    }
}
