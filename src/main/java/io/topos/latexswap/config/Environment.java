package io.topos.latexswap.config;

/**
 * Environment enumeration
 */
public enum Environment {
    DEVNET("DEVNET"),
    UATNET("UATNET"),
    MAINNET("MAINNET");

    private final String value;

    Environment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get environment from system property or environment variable
     * Defaults to DEVNET if not set
     */
    public static Environment fromSystemProperty() {
        String env = System.getProperty("LATEX_ENV");
        if (env == null || env.isEmpty()) {
            env = System.getenv("LATEX_ENV");
        }
        if (env == null || env.isEmpty()) {
            return DEVNET; // Default to DEVNET
        }
        try {
            return valueOf(env.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEVNET; // Default to DEVNET on invalid value
        }
    }

    /**
     * Get environment from string value
     */
    public static Environment fromString(String value) {
        if (value == null || value.isEmpty()) {
            return DEVNET;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DEVNET;
        }
    }
}

