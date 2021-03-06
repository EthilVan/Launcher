package fr.ethilvan.launcher.news;

import java.io.File;
import java.util.UUID;

public class CachedImage {

    private final String url;
    private final String filename;

    public CachedImage(String url) {
        this.url = url;
        this.filename = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String getUrl() {
        return url;
    }

    public File getFile(File dir) {
        return new File(dir, filename);
    }
}
