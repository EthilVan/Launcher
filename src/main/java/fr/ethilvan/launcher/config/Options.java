package fr.ethilvan.launcher.config;

public class Options {

    private final Providers providers;

    public Options() {
        this.providers = new Providers();
    }

    public Provider getProvider() {
        return providers.getSelectedItem();
    }

    public Providers getProviders() {
        return providers;
    }
}
