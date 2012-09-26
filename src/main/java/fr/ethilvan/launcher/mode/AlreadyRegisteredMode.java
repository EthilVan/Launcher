package fr.ethilvan.launcher.mode;

public class AlreadyRegisteredMode extends Exception {

    private static final long serialVersionUID = -8164464305660945609L;

    public AlreadyRegisteredMode(Mode mode) {
        super("Mode " + mode.getName() + " is already registered.");
    }
}
