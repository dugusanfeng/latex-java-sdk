package io.topos.latexswap.config;

import java.math.BigInteger;

/**
 * Network configuration for different environments
 */
public class NetworkConfig {
    private final String name;
    private final String rpcUrl;
    private final String latexSwapAddress;
    private final BigInteger chainId;
    private final String explorerUrl;

    public NetworkConfig(String name, String rpcUrl, String latexSwapAddress, BigInteger chainId, String explorerUrl) {
        this.name = name;
        this.rpcUrl = rpcUrl;
        this.latexSwapAddress = latexSwapAddress;
        this.chainId = chainId;
        this.explorerUrl = explorerUrl;
    }

    public String getName() {
        return name;
    }

    public String getRpcUrl() {
        return rpcUrl;
    }

    public String getLatexSwapAddress() {
        return latexSwapAddress;
    }

    public BigInteger getChainId() {
        return chainId;
    }

    public String getExplorerUrl() {
        return explorerUrl;
    }

    @Override
    public String toString() {
        return "NetworkConfig{" +
                "name='" + name + '\'' +
                ", rpcUrl='" + rpcUrl + '\'' +
                ", latexSwapAddress='" + latexSwapAddress + '\'' +
                ", chainId=" + chainId +
                ", explorerUrl='" + explorerUrl + '\'' +
                '}';
    }
}

