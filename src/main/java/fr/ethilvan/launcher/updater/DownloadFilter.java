package fr.ethilvan.launcher.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import fr.ethilvan.launcher.ui.TaskDialog;

public enum DownloadFilter {

    None {
        public void filter(TaskDialog dialog, InputStream input,
                File targetPath) {
            FileOutputStream output = null;
            try {
                FileUtils.forceMkdir(targetPath.getParentFile());
                output = new FileOutputStream(targetPath);
                IOUtils.copy(input, output);
            } catch (IOException exc) {
                Logger.getLogger(DownloadFilter.class.getName())
                        .log(Level.SEVERE, "Unable to copy downloaded file",
                                exc);
            } finally {
                IOUtils.closeQuietly(output);
                IOUtils.closeQuietly(input);
            }
        }
    },

    TarXz {
        public void filter(TaskDialog dialog, InputStream input,
                File targetPath) {
            InputStream inputStream = null;
            try {
                inputStream = new XZCompressorInputStream(input);
                TarArchiveInputStream tarIS =
                        new TarArchiveInputStream(inputStream);
                inputStream = tarIS;

                TarArchiveEntry entry;
                while ((entry = tarIS.getNextTarEntry()) != null) {
                    if (entry.isDirectory()) {
                        continue;
                    }

                    dialog.setStatus("Décompression de " + entry.getName(),
                            null);
                    File targetFile = new File(targetPath, entry.getName());
                    FileUtils.forceMkdir(targetFile.getParentFile());
                    OutputStream output = new FileOutputStream(targetFile);
                    IOUtils.copy(tarIS, output);
                    IOUtils.closeQuietly(output);
                }
            } catch (IOException exc) {
                Logger.getLogger(DownloadFilter.class.getName())
                        .log(Level.SEVERE,
                                "Unable to uncompress downloaded file", exc);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    };

    abstract void filter(TaskDialog dialog, InputStream input, File targetDir);
}
