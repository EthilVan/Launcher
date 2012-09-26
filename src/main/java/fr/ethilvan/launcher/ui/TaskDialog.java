package fr.ethilvan.launcher.ui;

import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.SwingUtilities;

public class TaskDialog extends JDialog {

    private static final long serialVersionUID = 3508184860311427900L;

    private final JLabel label;
    private final JProgressBar progressBar;
    private final JButton close;

    public TaskDialog(Component frame) {
        super();

        setTitle("Downloads");
        setModal(true);
        setMinimumSize(new Dimension(460, 120));
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(panel);

        label = new JLabel();
        GridBagConstraints labelC = new GridBagConstraints();
        labelC.insets = new Insets(0, 0, 20, 0);
        labelC.gridx = 0;
        labelC.gridy = 0;
        panel.add(label, labelC);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        GridBagConstraints progressC = new GridBagConstraints();
        progressC.gridx = 0;
        progressC.gridy = 1;
        progressC.weightx = 1.0;
        progressC.fill = GridBagConstraints.HORIZONTAL;
        panel.add(progressBar, progressC);

        close = new JButton("Fermer");
        close.setVisible(false);
        GridBagConstraints closeC = new GridBagConstraints();
        closeC.gridx = 0;
        closeC.gridy = 1;
        closeC.anchor = GridBagConstraints.CENTER;
        panel.add(close, closeC);

        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TaskDialog.this.dispose();
            }
        });

        pack();
        setLocationRelativeTo(frame);
    }

    public void setStatus(final String text, final BoundedRangeModel model) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
                if (model == null) {
                    progressBar.setIndeterminate(true);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setModel(model);
                }
            }
        });
    }

    public void setError(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                label.setText(message);
                progressBar.setVisible(false);
                close.setVisible(true);
            }
        });
    }
}
