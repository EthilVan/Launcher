package fr.ethilvan.launcher.news;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.resource.CSSResource;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.resource.XMLResource;

import fr.ethilvan.launcher.Launcher;

public class NewsUAC implements UserAgentCallback {

    private static final String CACHE_DIRNAME = "newscache";
    private static final String CACHE_FILENAME = CACHE_DIRNAME + ".json";

    private final File cacheDir;
    private final File jsonFile;
    private final Map<String, CachedResource> cache;

    private URI baseUri;

    public NewsUAC() {
        File settingsDir = Launcher.getSettingsDir();
        cacheDir = new File(settingsDir, CACHE_DIRNAME);
        jsonFile = new File(settingsDir, CACHE_FILENAME);

        cache = new HashMap<String, CachedResource>();
        loadCache();
    }

    private void loadCache() {
        CachedResource[] cacheArray;

        FileReader reader = null;
        try {
            reader = new FileReader(jsonFile);
            cacheArray = Launcher.getGson().fromJson(reader,
                    CachedResource[].class);
        } catch (IOException exc) {
            Logger.getLogger(NewsUAC.class.getName()).log(Level.WARNING,
                    "Unable to load images cache", exc);
            FileUtils.deleteQuietly(cacheDir);
            FileUtils.deleteQuietly(jsonFile);
            cacheArray = new CachedResource[0];
        } finally {
            IOUtils.closeQuietly(reader);
        }

        try {
            FileUtils.forceMkdir(cacheDir);
        } catch (IOException exc) {
            Logger.getLogger(NewsUAC.class.getName()).log(Level.WARNING,
                    "Unable to create cache directory", exc);
        }

        for (CachedResource res : cacheArray) {
            cache.put(res.getUrl(), res);
        }
    }

    @Override
    public String getBaseURL() {
        return baseUri.toString();
    }

    @Override
    public void setBaseURL(String baseUrl) {
        try {
            this.baseUri = new URI(baseUrl);
        } catch (URISyntaxException exc) {
            Logger.getLogger(NewsUAC.class.getName()).log(Level.WARNING,
                    "Unable to parse base uri", exc);
            this.baseUri = null;
        }
    }

    @Override
    public String resolveURI(String uri) {
        if (baseUri == null) {
            return uri;
        }

        return baseUri.resolve(uri).toString();
    }

    @Override
    public boolean isVisited(String uri) {
        return false;
    }

    @Override
    public CSSResource getCSSResource(String uri) {
        return null;
    }

    @Override
    public ImageResource getImageResource(String uri) {
        return null;
    }

    @Override
    public XMLResource getXMLResource(String uri) {
        return null;
    }

    @Override
    public byte[] getBinaryResource(String uri) {
        return null;
    }
}
