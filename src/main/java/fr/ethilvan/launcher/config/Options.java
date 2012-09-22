package fr.ethilvan.launcher.config;

public class Options {

    private boolean forceUpdate;
    private boolean rememberMe;
    private boolean quickLaunch;

    public Options() {
        forceUpdate = false;
        rememberMe = false;
        quickLaunch = false;
    }

    public boolean getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public boolean getQuickLaunch() {
        return quickLaunch;
    }

    public void setQuickLaunch(boolean quickLaunch) {
        this.quickLaunch = quickLaunch;
    }
}
