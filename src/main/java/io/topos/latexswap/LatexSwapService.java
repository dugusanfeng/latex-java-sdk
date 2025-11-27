package io.topos.latexswap;

import io.topos.latexswap.config.LatexSwapConfig;
import io.topos.latexswap.model.FeeInfo;
import io.topos.latexswap.model.FeeType;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

/**
 * High-level service class exposing only the approved LatexSwap interfaces.
 */
public class LatexSwapService {
    private final LatexSwapContract contract;
    private final Web3j web3j;
    private final TransactionManager transactionManager;
    private final LatexSwapConfig config;

    public LatexSwapService(Credentials credentials) {
        this(new LatexSwapConfig(), credentials);
    }

    public LatexSwapService(LatexSwapConfig config, Credentials credentials) {
        this(config, credentials, new DefaultGasProvider());
    }

    public LatexSwapService(LatexSwapConfig config, Credentials credentials, ContractGasProvider gasProvider) {
        this.config = config;
        this.web3j = Web3j.build(new HttpService(config.getRpcUrl()));
        this.transactionManager = new RawTransactionManager(web3j, credentials, config.getChainId().longValue());
        this.contract = LatexSwapContract.load(config.getContractAddress(), web3j, transactionManager, gasProvider);
    }

    public LatexSwapService(String contractAddress, Web3j web3j, Credentials credentials) {
        this(contractAddress, web3j, new RawTransactionManager(web3j, credentials), new DefaultGasProvider());
    }

    public LatexSwapService(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider gasProvider) {
        this.config = null;
        this.web3j = web3j;
        this.transactionManager = transactionManager;
        this.contract = LatexSwapContract.load(contractAddress, web3j, transactionManager, gasProvider);
    }

    public LatexSwapConfig getConfig() {
        return config;
    }

    public Web3j getWeb3j() {
        return web3j;
    }

    // ----- View functions -----

    public CompletableFuture<FeeInfo> getFeeRate(String token0, String token1) {
        return contract.getFeeRateRaw(token0, token1).sendAsync()
                .thenApply(result -> {
                    List<?> values = (List<?>) result;
                    org.web3j.abi.datatypes.generated.Uint8 feeTypeValue =
                            (org.web3j.abi.datatypes.generated.Uint8) values.get(0);
                    org.web3j.abi.datatypes.generated.Uint256 feeValue =
                            (org.web3j.abi.datatypes.generated.Uint256) values.get(1);
                    FeeInfo feeInfo = new FeeInfo();
                    feeInfo.setFeeType(FeeType.fromValue(feeTypeValue.getValue().intValue()));
                    feeInfo.setValue(feeValue.getValue());
                    return feeInfo;
                });
    }

    public CompletableFuture<BigInteger> getSwapUpper(String token0, String token1) {
        return contract.getSwapUpper(token0, token1).sendAsync();
    }

    public CompletableFuture<BigInteger> getPlatformFeeRate() {
        return contract.getPlatformFeeRate().sendAsync();
    }

    public CompletableFuture<BigInteger> getSwapRate(String token0, String token1) {
        return contract.getSwapRate(token0, token1).sendAsync();
    }

    public CompletableFuture<BigInteger> getSwapRateByPath(List<String> path) {
        return contract.getSwapRateByPath(path).sendAsync();
    }

    public CompletableFuture<BigInteger> getMiddleSwapRate(String token0, String token1) {
        return contract.getMiddleSwapRate(token0, token1).sendAsync();
    }

    public CompletableFuture<AmountResult> getAmountIn(List<String> path, BigInteger amountOut) {
        return contract.getAmountIn(path, amountOut).sendAsync()
                .thenApply(result -> {
                    List<BigInteger> values = result.stream()
                        .map(type -> {
                            // 校验类型：确保是 Uint256（与合约返回类型一致）
                            if (!(type instanceof Uint256)) {
                                throw new IllegalArgumentException(
                                        "不支持的类型：" + type.getClass().getName() + "，期望 Uint256"
                                );
                            }
                            // 转换：Uint256 → BigInteger
                            return (BigInteger) type.getValue();
                        })
                        .collect(Collectors.toList());
                    return new AmountResult(values.get(0), values.get(1));
                });
    }

    public CompletableFuture<AmountResult> getAmountOut(List<String> path, BigInteger amountIn) {
        return contract.getAmountOut(path, amountIn).sendAsync()
                .thenApply(result -> {
                    List<BigInteger> values = result.stream()
                        .map(type -> {
                            // 校验类型：确保是 Uint256（与合约返回类型一致）
                            if (!(type instanceof Uint256)) {
                                throw new IllegalArgumentException(
                                        "不支持的类型：" + type.getClass().getName() + "，期望 Uint256"
                                );
                            }
                            // 转换：Uint256 → BigInteger
                            return (BigInteger) type.getValue();
                        })
                        .collect(Collectors.toList());
                    return new AmountResult(values.get(0), values.get(1));
                });
    }

    // ----- Transaction functions -----

    public CompletableFuture<TransactionReceipt> swap(List<String> path, BigInteger amountIn, BigInteger amountInMax,
            BigInteger amountOut, BigInteger amountOutMin, BigInteger deadline, BigInteger value) {
        return contract.swap(path, amountIn, amountInMax, amountOut, amountOutMin).sendAsync();
    }

    public CompletableFuture<TransactionReceipt> rollback(String tokenIn, String tokenOut, BigInteger amountIn,
            BigInteger amountOut, BigInteger swapFee, BigInteger platformFee, BigInteger deadline) {
        return contract.rollback(tokenIn, tokenOut, amountIn, amountOut, swapFee, platformFee, deadline).sendAsync();
    }

    // ----- Helper class -----

    public static class AmountResult {
        private final BigInteger amount;
        private final BigInteger fee;

        public AmountResult(BigInteger amount, BigInteger fee) {
            this.amount = amount;
            this.fee = fee;
        }

        public BigInteger getAmount() {
            return amount;
        }

        public BigInteger getFee() {
            return fee;
        }
    }
}

