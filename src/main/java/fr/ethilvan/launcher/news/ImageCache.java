package fr.ethilvan.launcher.news;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import fr.ethilvan.launcher.util.Util;

public class ImageCache extends Dictionary<URL, Image> {

    private final File cacheDir;
    private final CachedImage[] cachedImages;
    private final HashMap<URL, Image> map;

    public ImageCache(File cacheDir, List<CachedImage> images) {
        this.cacheDir = cacheDir;
        this.cachedImages = images.toArray(new CachedImage[images.size()]);
        this.map = new HashMap<URL, Image>();
    }

    @Override
    public int size() {
        return cachedImages.length;
    }

    @Override
    public boolean isEmpty() {
        return cachedImages.length == 0;
    }

    @Override
    public Enumeration<URL> keys() {
        return null;
    }

    @Override
    public Enumeration<Image> elements() {
        return null;
    }

    @Override
    public Image get(Object rawKey) {
        if (!(rawKey instanceof URL)) {
            return null;
        }

        URL key = (URL) rawKey;
        Image img = map.get(key);
        if (img == null) {
            CachedImage cached = null;
            for (CachedImage cachedImage : cachedImages) {
                if (key.toString().equals(cachedImage.getUrl())) {
                    cached = cachedImage;
                }
            }

            if (cached != null) {
                try {
                    img = ImageIO.read(cached.getFile(cacheDir));
                } catch (IOException exc) {
                    throw Util.wrap(exc);
                }
            }

            map.put(key, img);
        }
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }

    @Override
    public Image put(URL key, Image value) {
        return map.put(key, value);
    }

    @Override
    public Image remove(Object key) {
        throw new UnsupportedOperationException();
    }
}
