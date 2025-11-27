package io.topos.latexswap;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * Minimal LatexSwap contract wrapper exposing only the required interfaces.
 */
public class LatexSwapContract extends Contract {
    public static final String BINARY = "";

    protected LatexSwapContract(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider gasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, gasProvider);
    }

    public static LatexSwapContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider gasProvider) {
        return new LatexSwapContract(contractAddress, web3j, transactionManager, gasProvider);
    }

    public RemoteCall<List<Type>> getFeeRateRaw(String token0, String token1) {
        final Function function = new Function("getFeeRate",
                Arrays.asList(new Address(token0), new Address(token1)),
                Arrays.asList(new TypeReference<Uint8>() {
                }, new TypeReference<Uint256>() {
                }));
        return executeRemoteCallMultipleValueReturn(function);
    }

    public RemoteCall<BigInteger> getSwapUpper(String token0, String token1) {
        final Function function = new Function("getSwapUpper",
                Arrays.asList(new Address(token0), new Address(token1)),
                Arrays.asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getPlatformFeeRate() {
        final Function function = new Function("getPlatformFeeRate",
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getSwapRate(String token0, String token1) {
        final Function function = new Function("getSwapRate",
                Arrays.asList(new Address(token0), new Address(token1)),
                Arrays.asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getSwapRateByPath(List<String> path) {
        final Function function = new Function("getSwapRateByPath",
                Arrays.asList(toAddressArray(path)),
                Arrays.asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> getMiddleSwapRate(String token0, String token1) {
        final Function function = new Function("getMiddleSwapRate",
                Arrays.asList(new Address(token0), new Address(token1)),
                Arrays.asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<List<Type>> getAmountIn(List<String> path, BigInteger amountOut) {
        final Function function = new Function("getAmountIn",
                Arrays.asList(toAddressArray(path), new Uint256(amountOut)),
                Arrays.asList(
                    new TypeReference<Uint256>() {},
                    new TypeReference<Uint256>() {}
                ));
        return executeRemoteCallMultipleValueReturn(function);
    }

    public RemoteCall<List<Type>> getAmountOut(List<String> path, BigInteger amountIn) {
        final Function function = new Function("getAmountOut",
                Arrays.asList(toAddressArray(path), new Uint256(amountIn)),
                Arrays.asList(new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }));
        return executeRemoteCallMultipleValueReturn(function);
    }

    public RemoteCall<TransactionReceipt> swap(List<String> path, BigInteger amountIn, BigInteger amountInMax,
            BigInteger amountOut, BigInteger amountOutMin, BigInteger deadline, BigInteger value) {
        final Function function = new Function("swap",
                Arrays.asList(
                        toAddressArray(path),
                        new Uint256(amountIn),
                        new Uint256(amountInMax),
                        new Uint256(amountOut),
                        new Uint256(amountOutMin),
                        new Uint256(deadline)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function, value);
    }

    public RemoteCall<TransactionReceipt> rollback(String tokenIn, String tokenOut, BigInteger amountIn,
            BigInteger amountOut, BigInteger swapFee, BigInteger platformFee, BigInteger deadline) {
        final Function function = new Function("rollback",
                Arrays.asList(
                        new Address(tokenIn),
                        new Address(tokenOut),
                        new Uint256(amountIn),
                        new Uint256(amountOut),
                        new Uint256(swapFee),
                        new Uint256(platformFee),
                        new Uint256(deadline)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    private static DynamicArray<Address> toAddressArray(List<String> path) {
        List<Address> addresses = path.stream().map(Address::new).collect(Collectors.toList());
        return new DynamicArray<>(Address.class, addresses);
    }
}

