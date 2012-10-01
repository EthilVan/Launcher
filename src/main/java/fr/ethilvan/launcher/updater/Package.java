package fr.ethilvan.launcher.updater;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ethilvan.launcher.Launcher;

public class Package {

    public final String name;
    public final String path;
    private final String url;
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

    public String getUrl() {
        String baseUrl = Launcher.get().getConfig().getMode().getListUrl();
        try { 
            URI uri = new URI(baseUrl);
            return uri.resolve(url).toString();
        } catch (URISyntaxException exc) {
            Logger.getLogger(Package.class.getName()).log(Level.SEVERE,
                    "Can't resolve uri (base: \"" + baseUrl + "\", path: \""
                            + path + "\")");
        }

        return url;
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
