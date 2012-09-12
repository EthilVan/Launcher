package fr.ethilvan.launcher.util;

import java.io.File;

public enum OS {

    WINDOWS {

        @Override
        protected String[] identifiers() {
            return new String[] { "win" };
        }

        @Override
        public File getDataDir() {
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                return new File(appData);
            } else {
                return Util.getHomeDirectory();
            }
        }
    },

    MAC_OS_X {
        @Override
        protected String[] identifiers() {
            return new String[] { "mac" };
        }
    },

    LINUX {
        @Override
        protected String[] identifiers() {
            return new String[] { "linux", "unix" };
        }
    },

    SOLARIS {
        @Override
        protected String[] identifiers() {
            return new String[] { "solaris", "sunos" };
        }
    },

    UNKNOWN;

    public static OS get() {
        String osName = System.getProperty("os.name").toLowerCase();
        for (OS os : values()) {
            for (String identifier : os.identifiers()) {
                if (osName.contains(identifier)) {
                    return os;
                }
            }
        }

        return UNKNOWN;
    }

    protected String[] identifiers() {
        return new String[0];
    }

    public File getDataDir() {
        return Util.getHomeDirectory();
    }
}
