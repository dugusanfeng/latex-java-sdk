package io.topos.latexswap.config;

import java.math.BigInteger;

/**
 * Configuration manager for LatexSwap SDK
 * Supports multiple environments: DEVNET, UATNET, MAINNET
 *
 * Configuration priority:
 * 1. System properties (LATEX_ENV, LATEX_RPC_URL, LATEX_CONTRACT_ADDRESS, etc.)
 * 2. Environment variables (LATEX_ENV, LATEX_RPC_URL, LATEX_CONTRACT_ADDRESS, etc.)
 * 3. Default configurations based on environment
 */
public class LatexSwapConfig {

    // Environment variable names
    public static final String ENV_LATEX_ENV = "LATEX_ENV";
    public static final String ENV_RPC_URL = "LATEX_RPC_URL";
    public static final String ENV_CONTRACT_ADDRESS = "LATEX_CONTRACT_ADDRESS";
    public static final String ENV_CHAIN_ID = "LATEX_CHAIN_ID";
    public static final String ENV_EXPLORER_URL = "LATEX_EXPLORER_URL";

    // Default configurations for each environment (contract addresses should be set via env vars)
    private static final NetworkConfig DEVNET_CONFIG = new NetworkConfig(
        "DEVNET",
        "https://devnet2openapi.platon.network/rpc",
        "0x3a9386bfFC5C5c0ca865C586254E257e5bF82Cb6", // Set via LATEX_CONTRACT_ADDRESS env var
        new BigInteger("2206132"),
        "https://devnet2scan.platon.network"
    );

    private static final NetworkConfig UATNET_CONFIG = new NetworkConfig(
        "UATNET",
        "https://devnet3openapi2.platon.network/rpc",
        "0x0000000000000000000000000000000000000000", // Set via LATEX_CONTRACT_ADDRESS env var
        new BigInteger("20250407"),
        "https://devnet3scan.platon.network"
    );

    private static final NetworkConfig MAINNET_CONFIG = new NetworkConfig(
        "MAINNET",
        "https://samurai.platon.network",
        "0x0000000000000000000000000000000000000000", // Set via LATEX_CONTRACT_ADDRESS env var
        new BigInteger("210425"),
        "https://scan.platon.network"
    );

    private final Environment environment;
    private final NetworkConfig networkConfig;

    /**
     * Create config from system property or environment variable
     */
    public LatexSwapConfig() {
        this(Environment.fromSystemProperty());
    }

    /**
     * Create config for specific environment
     */
    public LatexSwapConfig(Environment environment) {
        this.environment = environment;
        this.networkConfig = buildNetworkConfig(environment);
    }

    /**
     * Create custom config with overrides
     */
    public LatexSwapConfig(Environment environment, String rpcUrl, String contractAddress,
                          BigInteger chainId, String explorerUrl) {
        this.environment = environment;
        this.networkConfig = new NetworkConfig(
            environment.getValue(),
            rpcUrl != null ? rpcUrl : getDefaultRpcUrl(environment),
            contractAddress != null ? contractAddress : getDefaultContractAddress(environment),
            chainId != null ? chainId : getDefaultChainId(environment),
            explorerUrl != null ? explorerUrl : getDefaultExplorerUrl(environment)
        );
    }

    private NetworkConfig buildNetworkConfig(Environment environment) {
        // Check for environment variable overrides
        String rpcUrl = getEnvOrDefault(ENV_RPC_URL, null);
        String contractAddress = getEnvOrDefault(ENV_CONTRACT_ADDRESS, null);
        String chainIdStr = getEnvOrDefault(ENV_CHAIN_ID, null);
        String explorerUrl = getEnvOrDefault(ENV_EXPLORER_URL, null);

        BigInteger chainId = chainIdStr != null ? new BigInteger(chainIdStr) : null;

        // Use defaults if not overridden
        if (rpcUrl == null) rpcUrl = getDefaultRpcUrl(environment);
        if (contractAddress == null) contractAddress = getDefaultContractAddress(environment);
        if (chainId == null) chainId = getDefaultChainId(environment);
        if (explorerUrl == null) explorerUrl = getDefaultExplorerUrl(environment);

        return new NetworkConfig(
            environment.getValue(),
            rpcUrl,
            contractAddress,
            chainId,
            explorerUrl
        );
    }

    private String getDefaultRpcUrl(Environment environment) {
        switch (environment) {
            case DEVNET:
                return DEVNET_CONFIG.getRpcUrl();
            case UATNET:
                return UATNET_CONFIG.getRpcUrl();
            case MAINNET:
                return MAINNET_CONFIG.getRpcUrl();
            default:
                return DEVNET_CONFIG.getRpcUrl();
        }
    }

    private String getDefaultContractAddress(Environment environment) {
        switch (environment) {
            case DEVNET:
                return DEVNET_CONFIG.getLatexSwapAddress();
            case UATNET:
                return UATNET_CONFIG.getLatexSwapAddress();
            case MAINNET:
                return MAINNET_CONFIG.getLatexSwapAddress();
            default:
                return DEVNET_CONFIG.getLatexSwapAddress();
        }
    }

    private BigInteger getDefaultChainId(Environment environment) {
        switch (environment) {
            case DEVNET:
                return DEVNET_CONFIG.getChainId();
            case UATNET:
                return UATNET_CONFIG.getChainId();
            case MAINNET:
                return MAINNET_CONFIG.getChainId();
            default:
                return DEVNET_CONFIG.getChainId();
        }
    }

    private String getDefaultExplorerUrl(Environment environment) {
        switch (environment) {
            case DEVNET:
                return DEVNET_CONFIG.getExplorerUrl();
            case UATNET:
                return UATNET_CONFIG.getExplorerUrl();
            case MAINNET:
                return MAINNET_CONFIG.getExplorerUrl();
            default:
                return DEVNET_CONFIG.getExplorerUrl();
        }
    }

    /**
     * Get environment variable or system property, with fallback to default
     */
    private static String getEnvOrDefault(String key, String defaultValue) {
        // Check system property first
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        // Check environment variable
        value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return defaultValue;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public NetworkConfig getNetworkConfig() {
        return networkConfig;
    }

    public String getRpcUrl() {
        return networkConfig.getRpcUrl();
    }

    public String getContractAddress() {
        return networkConfig.getLatexSwapAddress();
    }

    public BigInteger getChainId() {
        return networkConfig.getChainId();
    }

    public String getExplorerUrl() {
        return networkConfig.getExplorerUrl();
    }

    @Override
    public String toString() {
        return "LatexSwapConfig{" +
                "environment=" + environment +
                ", networkConfig=" + networkConfig +
                '}';
    }
}

