package fr.ethilvan.launcher.updater;

import java.io.File;
import java.util.Set;

public class Package {

    private final String name;
    private final String path;
    private final String url;
    private final String[] tags;
    private final PackageFilter filter;

    private transient File tmpFile = null;

    public Package(String name, String path, String url, String[] tags,
            PackageFilter filter) {
        this.name = name;
        this.path = path;
        this.url = url;
        this.tags = tags;
        this.filter = filter;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public boolean isValid(Set<String> validTags) {
        if (tags == null) {
            return true;
        }

        for (String tag : tags) {
            if (!validTags.contains(tag)) {
                return false;
            }
        }

        return true;
    }

    public File getTemp(File tmpDir) {
        if (tmpFile == null) {
            tmpFile = new File(tmpDir, name);
        }

        return tmpFile;
    }

    public PackageFilter getFilter() {
        if (filter == null) {
            return PackageFilter.None;
        }

        return filter;
    }
}
