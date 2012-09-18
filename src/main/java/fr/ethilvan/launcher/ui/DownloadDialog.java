package fr.ethilvan.launcher.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class DownloadDialog extends JDialog {

    private static final long serialVersionUID = 3508184860311427900L;

    private final Component frame;
    private final JLabel label;
    private final JProgressBar progressBar;

    public DownloadDialog(Component frame) {
        super();
        this.frame = frame;

        setTitle("Downloads");
        setModal(true);
        setMinimumSize(new Dimension(360, 120));
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(panel);

        label = new JLabel();
        GridBagConstraints labelC = new GridBagConstraints();
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

        pack();
        setLocationRelativeTo(frame);
    }

    public void update(String text, BoundedRangeModel model) {
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
}
