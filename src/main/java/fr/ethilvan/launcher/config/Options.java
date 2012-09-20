package fr.ethilvan.launcher.config;

import fr.ethilvan.launcher.util.Encryption;

public class Options {

    private final Providers providers;
    private String username;
    private String password;
    private boolean useLatestLwjgl;

    public Options() {
        this.useLatestLwjgl = false;
        this.providers = new Providers();
    }

    public Provider getProvider() {
        return providers.getSelectedItem();
    }

    public Providers getProviders() {
        return providers;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return Encryption.decrypt(password);
    }

    public void rememberAccount(String username, String password) {
        this.username = username;
        this.password = Encryption.encrypt(password);
    }

    public boolean getUseLatestLWJGL() {
        return useLatestLwjgl;
    }

    public void setUseLatestLWJGL(boolean useLatestLWJGL) {
        this.useLatestLwjgl = useLatestLWJGL;
    }
}
