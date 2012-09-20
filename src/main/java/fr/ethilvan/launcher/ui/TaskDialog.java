package fr.ethilvan.launcher.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class TaskDialog extends JDialog {

    private static final long serialVersionUID = 3508184860311427900L;

    private final Component frame;
    private final JLabel label;
    private final JProgressBar progressBar;
    private final JButton close;

    public TaskDialog(Component frame) {
        super();
        this.frame = frame;

        setTitle("Downloads");
        setModal(true);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(panel);

        label = new JLabel();
        GridBagConstraints labelC = new GridBagConstraints();
        labelC.insets = new Insets(0, 0, 6, 0);
        labelC.gridx = 0;
        labelC.gridy = 0;
        panel.add(label, labelC);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        GridBagConstraints progressC = new GridBagConstraints();
        progressC.gridx = 0;
        progressC.gridy = 1;
        progressC.fill = GridBagConstraints.HORIZONTAL;
        panel.add(progressBar, progressC);

        close = new JButton("Fermer");
        close.setVisible(false);
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TaskDialog.this.dispose();
            }
        });
        panel.add(close, progressC);

        pack();
        setLocationRelativeTo(frame);
    }

    public void setStatus(String text, BoundedRangeModel model) {
        label.setText(text);
        if (model == null) {
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setIndeterminate(false);
            progressBar.setModel(model);
        }

        pack();
        setLocationRelativeTo(frame);
    }

    public void setLoginFailed() {
        label.setText("Nom d'utilisateur ou mot de passe invalide");
        progressBar.setVisible(false);
        close.setVisible(true);

        pack();
        setLocationRelativeTo(frame);
    }
}
