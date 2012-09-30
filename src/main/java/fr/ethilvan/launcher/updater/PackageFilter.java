package fr.ethilvan.launcher.updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import fr.ethilvan.launcher.ui.TaskDialog;

public enum PackageFilter {

    None {
        public void filter(TaskDialog dialog, InputStream input,
                File targetPath) {
            Logger.getLogger(PackageFilter.class.getName())
                    .info("Copying package " + targetPath);
            FileOutputStream output = null;
            try {
                FileUtils.forceMkdir(targetPath.getParentFile());
                output = new FileOutputStream(targetPath);
                IOUtils.copy(input, output);
            } catch (IOException exc) {
                Logger.getLogger(PackageFilter.class.getName())
                        .log(Level.SEVERE, "Unable to copy downloaded file",
                                exc);
            } finally {
                IOUtils.closeQuietly(output);
                IOUtils.closeQuietly(input);
            }
        }
    },

    Zip {
        public void filter(TaskDialog dialog, InputStream input,
                File targetPath) {
            ZipArchiveInputStream zipIS = null;
            try {
                zipIS = new ZipArchiveInputStream(input);
                uncompressArchive(dialog, zipIS, targetPath);
            } catch (IOException exc) {
                Logger.getLogger(PackageFilter.class.getName())
                        .log(Level.SEVERE,
                                "Unable to uncompress downloaded file", exc);
            } finally {
                IOUtils.closeQuietly(zipIS);
            }
        }
    },

    Jar {
        public void filter(TaskDialog dialog, InputStream input,
                File targetPath) {
            JarArchiveInputStream jarIS = null;
            try {
                jarIS = new JarArchiveInputStream(input);
                uncompressArchive(dialog, jarIS, targetPath);
            } catch (IOException exc) {
                Logger.getLogger(PackageFilter.class.getName())
                        .log(Level.SEVERE,
                                "Unable to uncompress downloaded file", exc);
            } finally {
                IOUtils.closeQuietly(jarIS);
            }
        }
    },

    Tar {
        public void filter(TaskDialog dialog, InputStream input,
                File targetPath) {
            TarArchiveInputStream tarIS = null;
            try {
                tarIS = new TarArchiveInputStream(input);
                uncompressArchive(dialog, tarIS, targetPath);
            } catch (IOException exc) {
                Logger.getLogger(PackageFilter.class.getName())
                        .log(Level.SEVERE,
                                "Unable to uncompress downloaded file", exc);
            } finally {
                IOUtils.closeQuietly(tarIS);
            }
        }
    },

    TarGz {
        public void filter(TaskDialog dialog, InputStream input,
                File targetPath) {
            InputStream inputStream = null;
            try {
                inputStream = new GzipCompressorInputStream(input);
                TarArchiveInputStream tarIS =
                        new TarArchiveInputStream(inputStream);
                inputStream = tarIS;
                uncompressArchive(dialog, tarIS, targetPath);
            } catch (IOException exc) {
                Logger.getLogger(PackageFilter.class.getName())
                        .log(Level.SEVERE,
                                "Unable to uncompress downloaded file", exc);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    },

    TarBz2 {
        public void filter(TaskDialog dialog, InputStream input,
                File targetPath) {
            InputStream inputStream = null;
            try {
                inputStream = new BZip2CompressorInputStream(input);
                TarArchiveInputStream tarIS =
                        new TarArchiveInputStream(inputStream);
                inputStream = tarIS;
                uncompressArchive(dialog, tarIS, targetPath);
            } catch (IOException exc) {
                Logger.getLogger(PackageFilter.class.getName())
                        .log(Level.SEVERE,
                                "Unable to uncompress downloaded file", exc);
            } finally {
                IOUtils.closeQuietly(inputStream);
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
                uncompressArchive(dialog, tarIS, targetPath);
            } catch (IOException exc) {
                Logger.getLogger(PackageFilter.class.getName())
                        .log(Level.SEVERE,
                                "Unable to uncompress downloaded file", exc);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    };

    private static void uncompressArchive(TaskDialog dialog,
            ArchiveInputStream is, File targetPath)
                    throws FileNotFoundException, IOException {
        ArchiveEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }

            File targetFile = new File(targetPath, entry.getName());
            Logger.getLogger(PackageFilter.class.getName())
                    .info("Uncompressing " + entry.getName());
            dialog.setStatus("Décompression de " + entry.getName(),
                    null);
            FileUtils.forceMkdir(targetFile.getParentFile());
            OutputStream output = new FileOutputStream(targetFile);
            IOUtils.copy(is, output);
            IOUtils.closeQuietly(output);
        }
    }

    abstract void filter(TaskDialog dialog, InputStream input, File targetDir);
}
