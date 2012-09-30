package fr.ethilvan.launcher.updater;

public class Update {

    public final Package[] packages;
    public final String[] toRemoves;

    public Update() {
        this.packages = new Package[0];
        this.toRemoves = new String[0];
    }
}
