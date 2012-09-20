package fr.ethilvan.launcher.config;

public class Provider {

    private final String name;
    private final String directory;
    private final String versionUrl;
    private final String listUrl;
    private final String server;

    public Provider(String name, String directory, String versionPath,
            String listPath, String server) {
        this.name = name;
        this.directory = directory;
        this.versionUrl = versionPath;
        this.listUrl = listPath;
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public String getDirectory() {
        return directory;
    }

    public String getVersionURL() {
        return versionUrl;
    }

    public String getListUrl() {
        return listUrl;
    }

    public String getServer() {
        return server;
    }

    @Override
    public String toString() {
        return name;
    }
}
