package fr.ethilvan.launcher.util;

import fr.ethilvan.launcher.config.Provider;

public final class EthilVan {

    public static final String WEBSITE = "http://ethilvan.fr";
    public static final String NEWS = WEBSITE + "/news/launcher";
    public static final String SERVER = "play.ethilvan.fr";

    public static Provider getProvider() {
        return new Provider("Ethil Van", ".ethilvan",
                WEBSITE + "/launcher/version",
                WEBSITE + "/launcher/list.json",
                SERVER);
    }

    private EthilVan() {
    }
}
