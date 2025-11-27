# LatexSwap Java SDK

Java SDK for interacting with the LatexSwap smart contract on PlatON network.

## Features

- Complete contract interface coverage
- Type-safe method calls
- Async/await support via CompletableFuture
- Helper utilities for bytes32 conversions and address operations
- High-level service class for common operations

## Requirements

- Java 11 or higher
- Maven 3.6+
- Web3j 4.9.8

## Installation

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>io.topos</groupId>
    <artifactId>latex-swap-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or build from source:

```bash
mvn clean install
```

## Quick Start

### Environment Configuration

The SDK supports multiple environments (DEVNET, UATNET, MAINNET) via environment variables or explicit configuration.

#### Method 1: Using Environment Variables (Recommended)

Set environment variables:

```bash
export LATEX_ENV=DEVNET  # or UATNET, MAINNET
export LATEX_RPC_URL=https://devnet2openapi.platon.network/rpc  # Optional override
export LATEX_CONTRACT_ADDRESS=0x...  # Optional override
export LATEX_CHAIN_ID=2206132  # Optional override
export LATEX_EXPLORER_URL=https://devnet2scan.platon.network  # Optional override
```

Or set as Java system properties:

```bash
java -DLATEX_ENV=DEVNET -DLATEX_RPC_URL=... YourApp
```

Then initialize service:

```java
import io.topos.latexswap.LatexSwapService;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

// Load credentials
Credentials credentials = WalletUtils.loadCredentials("password", "/path/to/keystore.json");

// Service automatically reads from environment variables
LatexSwapService service = new LatexSwapService(credentials);

// Get current configuration
LatexSwapConfig config = service.getConfig();
System.out.println("Environment: " + config.getEnvironment());
System.out.println("RPC URL: " + config.getRpcUrl());
System.out.println("Contract: " + config.getContractAddress());
```

#### Method 2: Using Explicit Configuration

```java
import io.topos.latexswap.LatexSwapService;
import io.topos.latexswap.config.Environment;
import io.topos.latexswap.config.LatexSwapConfig;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

// Load credentials
Credentials credentials = WalletUtils.loadCredentials("password", "/path/to/keystore.json");

// Create config for specific environment
LatexSwapConfig config = new LatexSwapConfig(Environment.DEVNET);

// Or with custom values
LatexSwapConfig customConfig = new LatexSwapConfig(
    Environment.MAINNET,
    "https://custom-rpc.platon.network/rpc",
    "0xYourContractAddress",
    BigInteger.valueOf(100),
    "https://custom-explorer.platon.network"
);

// Create service
LatexSwapService service = new LatexSwapService(config, credentials);
```

#### Method 3: Legacy Constructor (Backward Compatibility)

```java
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import io.topos.latexswap.LatexSwapService;

// Connect to PlatON network
Web3j web3j = Web3j.build(new HttpService("https://openapi2.platon.network/rpc"));

// Load credentials from keystore
Credentials credentials = WalletUtils.loadCredentials("password", "/path/to/keystore.json");

// Initialize service
String contractAddress = "0x..."; // LatexSwap contract address
LatexSwapService service = new LatexSwapService(contractAddress, web3j, credentials);
```

### Default Environment Configurations

| Environment | RPC URL | Chain ID | Explorer URL |
|------------|---------|----------|--------------|
| DEVNET | https://devnet2openapi.platon.network/rpc | 2206132 | https://devnet2scan.platon.network |
| UATNET | https://devnet3openapi2.platon.network/rpc | 20250407 | https://devnet3scan.platon.network |
| MAINNET | https://openapi2.platon.network/rpc | 210425 | https://scan.platon.network |

**Note:** Contract addresses need to be set via `LATEX_CONTRACT_ADDRESS` environment variable or in code.

## CLI Query Tool

Run read-only queries from the command line without writing additional code.

1. Set the environment variables (at least `LATEX_ENV` and `LATEX_CONTRACT_ADDRESS`). See `ENV_CONFIG.md`.
2. Execute the CLI via Maven:

```bash
# Query pool address
mvn -q exec:java \
  -Dexec.mainClass=io.topos.latexswap.cli.QueryCli \
  -Dexec.args="--action pool"

# Query swap rate between two tokens
mvn -q exec:java \
  -Dexec.mainClass=io.topos.latexswap.cli.QueryCli \
  -Dexec.args="--action swap-rate --token0 0xTokenA --token1 0xTokenB"

# Compute amount out for a path
mvn -q exec:java \
  -Dexec.mainClass=io.topos.latexswap.cli.QueryCli \
  -Dexec.args="--action amount-out --path 0xTokenA,0xTokenB --amount 1000000000000000000"
```

Supported actions:
- `pool` – print current environment, contract, and pool address.
- `swap-rate` – requires `--token0`, `--token1`.
- `fee-rate` – requires `--token0`, `--token1`.
- `amount-out` – requires `--path addr1,addr2,...`, `--amount <uint256>`.
- `amount-in` – same params as `amount-out`, computes required input amount.

All calls are read-only (no private key required); the CLI uses a `ReadonlyTransactionManager`.

## Exposed SDK Methods

- `getFeeRate(token0, token1)`
- `getSwapUpper(token0, token1)`
- `getPlatformFeeRate()`
- `getSwapRate(token0, token1)`
- `getSwapRateByPath(path)`
- `getMiddleSwapRate(token0, token1)`
- `getAmountIn(path, amountOut)`
- `getAmountOut(path, amountIn)`
- `swap(path, amountIn, amountInMax, amountOut, amountOutMin, deadline, value)`
- `rollback(tokenIn, tokenOut, amountIn, amountOut, swapFee, platformFee, deadline)`

### View Examples

```java
// Fee info
FeeInfo feeInfo = service.getFeeRate(token0, token1).get();

// Swap rate
BigInteger swapRate = service.getSwapRate(token0, token1).get();

// Swap rate by path
List<String> path = Arrays.asList(token0, midToken, token1);
BigInteger rateByPath = service.getSwapRateByPath(path).get();

// Amount calculation
LatexSwapService.AmountResult quote = service.getAmountOut(path, amountIn).get();
System.out.println("Amount Out: " + quote.getAmount());
System.out.println("Fee       : " + quote.getFee());
```

### Transaction Example

```java
List<String> path = Arrays.asList(token0, token1);
BigInteger deadline = BigInteger.valueOf(System.currentTimeMillis() / 1000 + 3600);

// Execute swap
TransactionReceipt swapReceipt = service.swap(
    path,
    amountIn,
    BigInteger.ZERO,
    BigInteger.ZERO,
    amountOutMin,
    deadline,
    BigInteger.ZERO
).get();

// Rollback (authorized callers only)
TransactionReceipt rollbackReceipt = service.rollback(
    token0,
    token1,
    amountIn,
    amountOut,
    BigInteger.ZERO,
    BigInteger.ZERO,
    deadline
).get();
```

### 5. Whitelist Operations

```java
import io.topos.latexswap.model.WhitelistConstants;
import io.topos.latexswap.util.Bytes32Utils;

// Check if address is in whitelist
String pairKey = service.getPairKey(token0, token1);
boolean isInWhitelist = service.isInWhitelist(
    pairKey, 
    WhitelistConstants.LP_WHITELIST, 
    accountAddress
).get();
```

## Utility Classes

### Bytes32Utils

Convert strings to bytes32 (for whitelist IDs):

```java
import io.topos.latexswap.util.Bytes32Utils;
import org.web3j.abi.datatypes.generated.Bytes32;

Bytes32 lpWhitelist = Bytes32Utils.stringToBytes32("lp");
BigInteger bytes32Value = Bytes32Utils.stringToBytes32BigInteger("lp");
```

### AddressUtils

Address utilities:

```java
import io.topos.latexswap.util.AddressUtils;

// Get pair key from two token addresses
String pairKey = AddressUtils.getPairKey(token0, token1);

// Validate address
boolean isValid = AddressUtils.isValidAddress("0x...");
```

## Model Classes

### FeeType

```java
import io.topos.latexswap.model.FeeType;

FeeType fixed = FeeType.Fixed;
FeeType percentage = FeeType.Percentage;

// Convert from Solidity enum value
FeeType fromValue = FeeType.fromValue(0); // Fixed
```

### FeeInfo

```java
import io.topos.latexswap.model.FeeInfo;

FeeInfo feeInfo = new FeeInfo(FeeType.Percentage, BigInteger.valueOf(10000));
```

## Error Handling

```java
try {
    TransactionReceipt receipt = service.swap(...).get();
    // Check transaction status
    if (receipt.isStatusOK()) {
        System.out.println("Transaction successful!");
    } else {
        System.out.println("Transaction failed!");
    }
} catch (Exception e) {
    System.err.println("Error: " + e.getMessage());
    e.printStackTrace();
}
```

## Gas Configuration

Customize gas provider:

```java
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

ContractGasProvider gasProvider = new StaticGasProvider(
    BigInteger.valueOf(1000000000L), // gasPrice
    BigInteger.valueOf(500000L)        // gasLimit
);

LatexSwapService service = new LatexSwapService(
    contractAddress, web3j, transactionManager, gasProvider
);
```

## Code Generation (Optional)

For production, consider generating contract wrapper using Web3j CLI:

```bash
# Install Web3j CLI
npm install -g web3j

# Generate Java classes from ABI
web3j generate solidity \
  -a src/main/resources/LatexSwap.abi.json \
  -o src/main/java \
  -p io.topos.latexswap.contracts
```

Then update `LatexSwapService` to use the generated contract class.

## License

MIT

