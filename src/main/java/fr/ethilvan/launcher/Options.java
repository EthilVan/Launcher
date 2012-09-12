package fr.ethilvan.launcher;

public class Options {

    private boolean devMode;

    public Options() {
        devMode = false;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public String getEthilVanFolder() {
        return devMode ? ".ethilvandev" : ".ethilvan";
    }
}
