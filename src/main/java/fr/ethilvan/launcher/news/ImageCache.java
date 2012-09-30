package fr.ethilvan.launcher.news;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import fr.ethilvan.launcher.config.Config;

public class ImageCache {

    private final File cacheDir;
    private final CachedImage[] cachedImages;
    private final HashMap<String, Image> map;

    public ImageCache(File cacheDir, List<CachedImage> images) {
        this.cacheDir = cacheDir;
        this.cachedImages = images.toArray(new CachedImage[images.size()]);
        this.map = new HashMap<String, Image>();
    }

    public boolean contains(String url) {
        if (map.containsKey(url)) {
            return true;
        }

        for (CachedImage cachedImage : cachedImages) {
            if (url.toString().equals(cachedImage.getUrl())) {
                return true;
            }
        }

        return false;
    }

    public Image get(String url) {
        if (!map.containsKey(url)) {
            loadImage(url);
        }

        return map.get(url);
    }

    private void loadImage(String url) {
        CachedImage cached = null;
        for (CachedImage cachedImage : cachedImages) {
            if (url.toString().equals(cachedImage.getUrl())) {
                cached = cachedImage;
            }
        }

        if (cached != null) {
            try {
                Image img = ImageIO.read(cached.getFile(cacheDir));
                map.put(url, img);
            } catch (IOException exc) {
                Logger.getLogger(Config.class.getName()).log(Level.WARNING,
                        "Unable to load cached image", exc);
            }
        }
    }
}
