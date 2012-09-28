package fr.ethilvan.launcher.news;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JProgressBar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonIOException;

import fr.ethilvan.launcher.Launcher;
import fr.ethilvan.launcher.Provider;
import fr.ethilvan.launcher.ui.NewsPanel;
import fr.ethilvan.launcher.util.Util;

public class NewsFetcher {

    private static final String CACHE_DIRNAME = "newscache";
    private static final String CACHE_FILENAME = CACHE_DIRNAME + ".json";

    private class NewsPageDownloader
        extends NewsDownloader<ByteArrayOutputStream> {

        public NewsPageDownloader(JProgressBar progressBar) {
            super(Provider.get().newsUrl, new ByteArrayOutputStream(),
                    progressBar);
        }

        @Override
        protected void onError(int code, String reason) {
            cancel();
            newsPage = "<html><b><center>"
                   + "Impossible d'afficher les news. ("
                   + code + ": " + reason
                   + ")</center></b></html>";
            downloadsCount--;
        }

        public void onComplete(ByteArrayOutputStream output) {
            try {
                newsPage = output.toString(Util.UTF8);
            } catch (UnsupportedEncodingException exc) {
                Logger.getLogger(NewsFetcher.class.getName())
                        .log(Level.WARNING, "Unable to convert news to UTF-8",
                                exc);
                newsPage = "<html><b><center>"
                        + "Impossible d'afficher les news."
                        + ")</center></b></html>";
            }
            downloadsCount--;
        }
    }

    private class ImageDownloader extends NewsDownloader<FileOutputStream> {

        public ImageDownloader(CachedImage img, JProgressBar progressBar)
                throws FileNotFoundException {
            super(img.getUrl(), new FileOutputStream(img.getFile(cacheDir)),
                    progressBar);
        }

        @Override
        protected void onError(int code, String reason) {
            downloadsCount--;
        }

        @Override
        protected void onComplete(FileOutputStream stream) {
            downloadsCount--;
        }
    }

    private final File cacheDir; 
    private final File jsonFile;

    private int downloadsCount;
    private String newsPage;

    public NewsFetcher() {
        cacheDir = new File(Launcher.getSettingsDir(), CACHE_DIRNAME);
        jsonFile = new File(Launcher.getSettingsDir(), CACHE_FILENAME);

        downloadsCount = 0;
        newsPage = null;
    }

    public void fetch(NewsPanel newsPanel, JProgressBar progressBar) {
        ImageCache cache = getCache(progressBar);
        NewsPageDownloader newsDownloader = new NewsPageDownloader(progressBar);

        downloadsCount++;
        try {
            Launcher.get().download(newsDownloader);
        } catch (IOException exc) {
            return;
        }

        while (downloadsCount > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException exc) {
            }
        }

        newsPanel.displayNews(cache, newsPage, progressBar);
    }

    private ImageCache getCache(JProgressBar progressBar) {
        CachedImage[] cache = loadSavedCache();
        Set<String> banners = fetchNewsImages();

        List<CachedImage> newCache = new ArrayList<CachedImage>();
        for (CachedImage cachedImage : cache) {
            banners.remove(cachedImage.getUrl());
            newCache.add(cachedImage);
        }

        for (String banner : banners) {
            CachedImage cached = new CachedImage(banner);
            try {
                ImageDownloader downloader =
                        new ImageDownloader(cached, progressBar);
                Launcher.get().download(downloader);
                downloadsCount++;
                newCache.add(cached);
            } catch (IOException exc) {
                Logger.getLogger(NewsFetcher.class.getName())
                    .log(Level.WARNING, "Unable to download image", exc);
            }
        }

        writeCache(newCache);

        return new ImageCache(cacheDir, newCache);
    }

    private CachedImage[] loadSavedCache() {
        CachedImage[] cache;

        try {
            cache = Launcher.getGson().fromJson(
                    new FileReader(jsonFile), CachedImage[].class);
        } catch (Exception exc) {
            FileUtils.deleteQuietly(cacheDir);
            FileUtils.deleteQuietly(jsonFile);
            cache = new CachedImage[0];
        }

        try {
            FileUtils.forceMkdir(cacheDir);
        } catch (IOException exc) {
            Logger.getLogger(NewsFetcher.class.getName())
                    .log(Level.WARNING, "Unable to create cache directory",
                            exc);
        }

        return cache;
    }

    private Set<String> fetchNewsImages() {
        Set<String> banners = new HashSet<String>();
        BufferedReader reader = null;
        try {
            URL url = new URL(Provider.get().imgListUrl);
            InputStream stream = url.openStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = reader.readLine()) != null) {
                banners.add(line);
            }
        } catch (MalformedURLException exc) {
            Logger.getLogger(NewsFetcher.class.getName())
                    .log(Level.WARNING, "Invalid image list url", exc);
        } catch (IOException exc) {
            Logger.getLogger(NewsFetcher.class.getName())
                    .log(Level.WARNING, "Can't download image images list at "
                            + Provider.get().imgListUrl, exc);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return banners;
    }

    private void writeCache(List<CachedImage> newCache) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(jsonFile);
            Launcher.getGson().toJson(newCache, writer);
        } catch (JsonIOException exc) {
            Logger.getLogger(NewsFetcher.class.getName())
                    .log(Level.WARNING, "Can't write images cache index",
                            exc);
        } catch (IOException exc) {
            Logger.getLogger(NewsFetcher.class.getName())
                    .log(Level.WARNING, "Can't write images cache index",
                            exc);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
