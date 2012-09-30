package fr.ethilvan.launcher.updater;

import java.io.File;
import java.util.Set;

public class Package {

    public final String name;
    public final String path;
    public final String url;
    private final String[][] tags;
    private final PackageFilter filter;

    private transient File tmpFile = null;

    public Package(String name, String path, String url, String[][] tags,
            PackageFilter filter) {
        this.name = name;
        this.path = path;
        this.url = url;
        this.tags = tags;
        this.filter = filter;
    }

    // [["a", "b"], ["c", "d"]] means (a and b) or (c and d)
    public boolean isNeeded(Set<String> validTags) {
        if (tags == null) {
            return true;
        }

        boolean match = false;
        for (String[] tagGroup : tags) {
            boolean matchGroup = true;
            for (String tag : tagGroup) {
                matchGroup &= validTags.contains(tag);
            }
            match |= matchGroup;
        }

        return match;
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
