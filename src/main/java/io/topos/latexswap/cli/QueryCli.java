package io.topos.latexswap.cli;

import io.topos.latexswap.LatexSwapContract;
import io.topos.latexswap.LatexSwapService;
import io.topos.latexswap.config.LatexSwapConfig;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.topos.latexswap.model.FeeInfo;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

/**
 * Simple command line tool to query LatexSwap view functions.
 *
 * <p>Usage examples:
 *
 * <pre>
 *   mvn -q exec:java \\
 *     -Dexec.mainClass=io.topos.latexswap.cli.QueryCli \\
 *     -Dexec.args="--action pool"
 *
 *   mvn -q exec:java \\
 *     -Dexec.mainClass=io.topos.latexswap.cli.QueryCli \\
 *     -Dexec.args="--action swap-rate --token0 0x... --token1 0x..."
 *
 *   mvn -q exec:java \\
 *     -Dexec.mainClass=io.topos.latexswap.cli.QueryCli \\
 *     -Dexec.args="--action amount-out --path 0xTokenA,0xTokenB --amount 1000000000000000000"
 * </pre>
 *
 * Environment variables described in ENV_CONFIG.md (LATEX_ENV, LATEX_RPC_URL, LATEX_CONTRACT_ADDRESS, etc.)
 * control which network the CLI queries.
 */
public final class QueryCli {

    private QueryCli() { }

    public static <web3j> void main(String[] args) throws Exception {
        Map<String, String> params = parseArgs(args);
        if (params.containsKey("--help") || params.isEmpty()) {
            printUsage();
            return;
        }

        String action = params.get("--action");
        if (action == null) {
            System.err.println("Missing --action parameter\n");
            printUsage();
            System.exit(1);
            return;
        }

        LatexSwapConfig config = new LatexSwapConfig();
        System.out.println(config);
        if (config.getContractAddress() == null || config.getContractAddress().equalsIgnoreCase("0x0000000000000000000000000000000000000000")) {
            System.err.println("LATEX_CONTRACT_ADDRESS must be set via environment variable or explicit configuration.");
            System.exit(2);
            return;
        }

        Web3j web3j = null;
        String rpcUrl = config.getRpcUrl();
        web3j = Web3j.build(new HttpService(rpcUrl));

        try {

            String privateKey = "0x43659514c429509e4993f5b82f0fae44cdba8174d32c09bf913f8201be4cd294";

            // 2. 创建 Credentials
            Credentials credentials = Credentials.create(privateKey);
            LatexSwapService service = new LatexSwapService(credentials);

            switch (action) {
                case "swap-rate":
                    handleSwapRate(service, params);
                    break;
                case "swap-rate-by-path":
                    handleSwapRateByPath(service, params);
                    break;
                case "middle-swap-rate":
                    handleMiddleSwapRate(service, params);
                    break;
                case "swap-upper":
                    handleSwapUpper(service, params);
                    break;
                case "platform-fee-rate":
                    handlePlatformFeeRate(service);
                    break;
                case "fee-rate":
                    handleFeeRate(service, params);
                    break;
                case "amount-out":
                    handleAmountOut(service, params);
                    break;
                case "amount-in":
                    handleAmountIn(service, params);
                    break;
                default:
                    System.err.printf("Unknown action: %s%n%n", action);
                    printUsage();
                    System.exit(1);
            }
        } catch (IllegalArgumentException e) {
            // 处理无效 URL 异常
        } catch (Exception e) {
            // 处理其他业务异常
        } finally {
        }
    }

    private static void handleSwapRate(LatexSwapService service, Map<String, String> params) throws Exception {
        String token0 = requireParam(params, "--token0");
        String token1 = requireParam(params, "--token1");

        BigInteger rate = service.getSwapRate(token0, token1).get();
        System.out.printf("SwapRate(%s -> %s) = %s%n", token0, token1, rate);
    }

    private static void handleSwapRateByPath(LatexSwapService service, Map<String, String> params) throws Exception {
        List<String> path = parsePath(requireParam(params, "--path"));
        BigInteger rate = service.getSwapRateByPath(path).get();
        System.out.printf("SwapRateByPath(%s) = %s%n", path, rate);
    }

    private static void handleMiddleSwapRate(LatexSwapService service, Map<String, String> params) throws Exception {
        String token0 = requireParam(params, "--token0");
        String token1 = requireParam(params, "--token1");
        BigInteger rate = service.getMiddleSwapRate(token0, token1).get();
        System.out.printf("MiddleSwapRate(%s -> %s) = %s%n", token0, token1, rate);
    }

    private static void handleSwapUpper(LatexSwapService service, Map<String, String> params) throws Exception {
        String token0 = requireParam(params, "--token0");
        String token1 = requireParam(params, "--token1");
        BigInteger upper = service.getSwapUpper(token0, token1).get();
        System.out.printf("SwapUpper(%s -> %s) = %s%n", token0, token1, upper);
    }

    private static void handlePlatformFeeRate(LatexSwapService service) throws Exception {
        BigInteger feeRate = service.getPlatformFeeRate().get();
        System.out.printf("PlatformFeeRate = %s%n", feeRate);
    }

    private static void handleFeeRate(LatexSwapService service, Map<String, String> params) throws Exception {
        String token0 = requireParam(params, "--token0");
        String token1 = requireParam(params, "--token1");

        FeeInfo feeInfo = service.getFeeRate(token0, token1).get();
//        var feeType = (org.web3j.abi.datatypes.generated.Uint8) values.get(0);
//        var feeValue = (org.web3j.abi.datatypes.generated.Uint256) values.get(1);

        System.out.printf("FeeRate(%s -> %s) type=%s value=%s%n",
                token0, token1, feeInfo.getFeeType(), feeInfo.getValue());
    }

    private static void handleAmountOut(LatexSwapService service, Map<String, String> params) throws Exception {
        List<String> path = parsePath(requireParam(params, "--path"));
        BigInteger amountIn = new BigInteger(requireParam(params, "--amount"));

        LatexSwapService.AmountResult result = service.getAmount(path).get();
        System.out.printf("AmountOut path=%s amountIn=%s amountOut=%s fee=%s%n",
            path, amountIn, result.getAmount(), result.getFee());
    }

    private static void handleAmountIn(LatexSwapService service, Map<String, String> params) throws Exception {
        List<String> path = parsePath(requireParam(params, "--path"));
        BigInteger amountOut = new BigInteger(requireParam(params, "--amount"));

        LatexSwapService.AmountResult result = service.getAmountIn(path, amountOut);
        System.out.printf("AmountIn path=%s amountOut=%s amountIn=%s fee=%s%n",
            path, amountOut, result.getAmount(), result.getFee());
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--")) {
                String value = (i + 1) < args.length && !args[i + 1].startsWith("--") ? args[++i] : "";
                map.put(arg, value);
            }
        }
        return map;
    }

    private static String requireParam(Map<String, String> params, String key) {
        String value = params.get(key);
        if (value == null || value.isEmpty()) {
            System.err.printf("Missing parameter %s%n%n", key);
            printUsage();
            System.exit(1);
        }
        return value;
    }

    private static List<String> parsePath(String pathArg) {
        return Arrays.stream(pathArg.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }

    private static void printUsage() {
        System.out.println("LatexSwap Query CLI");
        System.out.println("Usage:");
        System.out.println("  --action swap-rate --token0 <addr> --token1 <addr>");
        System.out.println("  --action swap-rate-by-path --path <addr,addr,...>");
        System.out.println("  --action middle-swap-rate --token0 <addr> --token1 <addr>");
        System.out.println("  --action swap-upper --token0 <addr> --token1 <addr>");
        System.out.println("  --action platform-fee-rate");
        System.out.println("  --action fee-rate  --token0 <addr> --token1 <addr>");
        System.out.println("  --action amount-out --path <addr,addr,...> --amount <uint>");
        System.out.println("  --action amount-in  --path <addr,addr,...> --amount <uint>");
        System.out.println();
        System.out.println("Environment variables:");
        System.out.println("  LATEX_ENV, LATEX_RPC_URL, LATEX_CONTRACT_ADDRESS, LATEX_CHAIN_ID, LATEX_EXPLORER_URL");
        System.out.println("See ENV_CONFIG.md for details.");
    }
}


