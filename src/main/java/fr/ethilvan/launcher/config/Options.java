package fr.ethilvan.launcher.config;

public class Options {

    private boolean useLatestLWJGL;
    private final Providers providers;

    public Options() {
        this.useLatestLWJGL = false;
        this.providers = new Providers();
    }

    public boolean getUseLatestLWJGL() {
        return useLatestLWJGL;
    }

    public void setUseLatestLWJGL(boolean useLatestLWJGL) {
        this.useLatestLWJGL = useLatestLWJGL;
    }

    public Provider getProvider() {
        return providers.getSelectedItem();
    }

    public Providers getProviders() {
        return providers;
    }
}
