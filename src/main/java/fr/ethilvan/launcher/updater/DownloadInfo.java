package fr.ethilvan.launcher.updater;

import java.io.File;

public class DownloadInfo {

    private final String path;
    private final String url;

    public DownloadInfo(String path, String url) {
        this.path = path;
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public File getTemp(File tmpDir) {
        File file = new File(tmpDir, path + ".tmp");
        file.getParentFile().mkdirs();
        return file;
    }
}
