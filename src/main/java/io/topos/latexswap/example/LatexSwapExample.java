package io.topos.latexswap.example;

import io.topos.latexswap.LatexSwapService;
import io.topos.latexswap.config.Environment;
import io.topos.latexswap.config.LatexSwapConfig;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Example usage of LatexSwap Java SDK with environment configuration
 */
public class LatexSwapExample {

    public static void main(String[] args) throws Exception {
        // Method 1: Use environment variables (recommended)
        // Set LATEX_ENV=DEVNET or UATNET or MAINNET
        // Set LATEX_RPC_URL, LATEX_CONTRACT_ADDRESS, etc. if needed
        exampleWithEnvironmentVariables();

        // Method 2: Use explicit configuration
        exampleWithExplicitConfig();

        // Method 3: Use legacy constructor (for backward compatibility)
        exampleWithLegacyConstructor();
    }

    /**
     * Example 1: Using environment variables
     * Set LATEX_ENV environment variable to DEVNET, UATNET, or MAINNET
     */
    private static void exampleWithEnvironmentVariables() throws Exception {
        System.out.println("=== Example 1: Using Environment Variables ===");

        // Load credentials
        Credentials credentials = WalletUtils.loadCredentials(
            "password",
            "/path/to/keystore.json"
        );

        // Service automatically reads from environment variables
        // LATEX_ENV=DEVNET (or UATNET, MAINNET)
        // LATEX_RPC_URL, LATEX_CONTRACT_ADDRESS, etc. are optional overrides
        LatexSwapService service = new LatexSwapService(credentials);

        // Get current configuration
        LatexSwapConfig config = service.getConfig();
        System.out.println("Environment: " + config.getEnvironment());
        System.out.println("RPC URL: " + config.getRpcUrl());
        System.out.println("Contract Address: " + config.getContractAddress());
        System.out.println("Chain ID: " + config.getChainId());
        System.out.println("Explorer URL: " + config.getExplorerUrl());

        viewFunctionsExample(service);
        transactionExamples(service);
    }

    /**
     * Example 2: Using explicit configuration
     */
    private static void exampleWithExplicitConfig() throws Exception {
        System.out.println("\n=== Example 2: Using Explicit Configuration ===");

        // Load credentials
        Credentials credentials = WalletUtils.loadCredentials(
            "password",
            "/path/to/keystore.json"
        );

        // Create config for specific environment
        LatexSwapConfig config = new LatexSwapConfig(Environment.DEVNET);
        // Or with custom values:
        // LatexSwapConfig config = new LatexSwapConfig(
        //     Environment.MAINNET,
        //     "https://custom-rpc.platon.network/rpc",
        //     "0xYourContractAddress",
        //     BigInteger.valueOf(100),
        //     "https://custom-explorer.platon.network"
        // );

        // Create service with config
        LatexSwapService service = new LatexSwapService(config, credentials);

        System.out.println("Using environment: " + config.getEnvironment());
        System.out.println("Contract: " + config.getContractAddress());

        viewFunctionsExample(service);
    }

    /**
     * Example 3: Using legacy constructor (backward compatibility)
     */
    private static void exampleWithLegacyConstructor() throws Exception {
        System.out.println("\n=== Example 3: Using Legacy Constructor ===");

        // Load credentials
        Credentials credentials = WalletUtils.loadCredentials(
            "password",
            "/path/to/keystore.json"
        );

        // Manual setup (legacy way)
        org.web3j.protocol.Web3j web3j = org.web3j.protocol.Web3j.build(
            new org.web3j.protocol.http.HttpService("https://openapi2.platon.network/rpc")
        );
        String contractAddress = "0x..."; // Replace with actual contract address

        LatexSwapService service = new LatexSwapService(contractAddress, web3j, credentials);

        viewFunctionsExample(service);
    }

    private static void viewFunctionsExample(LatexSwapService service) throws Exception {
        String token0 = "0x...";
        String token1 = "0x...";

        // Platform fee
        BigInteger platformFeeRate = service.getPlatformFeeRate().get();
        System.out.println("Platform Fee Rate: " + platformFeeRate);

        // Get swap rate
        BigInteger swapRate = service.getSwapRate(token0, token1).get();
        System.out.println("Swap Rate: " + swapRate);

        // Get fee rate
        var feeInfo = service.getFeeRate(token0, token1).get();
        System.out.println("Fee Type: " + feeInfo.getFeeType());
        System.out.println("Fee Value: " + feeInfo.getValue());

        // Swap upper
        BigInteger swapUpper = service.getSwapUpper(token0, token1).get();
        System.out.println("Swap Upper: " + swapUpper);

        // Calculate swap amounts
        List<String> path = Arrays.asList(token0, token1);
        BigInteger amountIn = BigInteger.valueOf(1000000000000000000L); // 1 token
        var result = service.getAmountOut(path, amountIn).get();
        System.out.println("Amount Out: " + result.getAmount());
        System.out.println("Fee: " + result.getFee());
    }

    private static void transactionExamples(LatexSwapService service) throws Exception {
        String token0 = "0x...";
        String token1 = "0x...";
        BigInteger deadline = BigInteger.valueOf(System.currentTimeMillis() / 1000 + 3600);

        List<String> path = Arrays.asList(token0, token1);
        BigInteger amountIn = BigInteger.valueOf(1000000000000000000L);
        LatexSwapService.AmountResult outRes = service.getAmountOut(path, amountIn).get();
        BigInteger amountOutMin = outRes.getAmount().multiply(BigInteger.valueOf(9950L)).divide(BigInteger.valueOf(10000L));

        TransactionReceipt swapReceipt = service.swap(
            path,
            amountIn,
            BigInteger.ZERO,
            BigInteger.ZERO,
            amountOutMin,
            deadline,
            BigInteger.ZERO
        ).get();
        System.out.println("Swap Tx Hash: " + swapReceipt.getTransactionHash());

        TransactionReceipt rollbackReceipt = service.rollback(
            token0,
            token1,
            amountIn,
            outRes.getAmount(), // 替换成上面的swap事件对应的值
            BigInteger.ZERO, // 替换成上面的swap事件对应的值
            BigInteger.ZERO, // 替换成上面的swap事件对应的值
            deadline
        ).get();
        System.out.println("Rollback Tx Hash: " + rollbackReceipt.getTransactionHash());
    }
}

