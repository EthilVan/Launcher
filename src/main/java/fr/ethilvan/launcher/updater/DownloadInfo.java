package fr.ethilvan.launcher.updater;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import fr.ethilvan.launcher.util.Util;

public class DownloadInfo {

    private final String path;
    private final String url;
    private final DownloadFilter filter;

    private transient File tmpFile = null;

    public DownloadInfo(String path, String url, DownloadFilter filter) {
        this.path = path;
        this.url = url;
        this.filter = filter;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public File getTemp(File tmpDir) {
        if (tmpFile == null) {
            tmpFile = new File(tmpDir, path + ".tmp");
        }

        try {
            FileUtils.forceMkdir(tmpFile.getParentFile());
        } catch (IOException exc) {
            throw Util.wrap(exc);
        }
        return tmpFile;
    }

    public DownloadFilter getFilter() {
        if (filter == null) {
            return DownloadFilter.None;
        }

        return filter;
    }
}
