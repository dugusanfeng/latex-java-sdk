# Environment Configuration Guide

## Overview

The LatexSwap Java SDK supports multiple environments (DEVNET, UATNET, MAINNET) through environment variables or system properties.

## Configuration Methods

### 1. Environment Variables (Recommended)

Set these environment variables before running your application:

```bash
# Required: Set environment
export LATEX_ENV=DEVNET  # or UATNET, MAINNET

# Optional: Override default values
export LATEX_RPC_URL=https://devnet2openapi.platon.network/rpc
export LATEX_CONTRACT_ADDRESS=0xYourContractAddress
export LATEX_CHAIN_ID=2206132
export LATEX_EXPLORER_URL=https://devnet2scan.platon.network
```

### 2. Java System Properties

Set as JVM arguments:

```bash
java -DLATEX_ENV=DEVNET \
     -DLATEX_RPC_URL=https://devnet2openapi.platon.network/rpc \
     -DLATEX_CONTRACT_ADDRESS=0xYourContractAddress \
     YourApp
```

Or in code:

```java
System.setProperty("LATEX_ENV", "DEVNET");
System.setProperty("LATEX_CONTRACT_ADDRESS", "0xYourContractAddress");
```

### 3. Explicit Configuration in Code

```java
import io.topos.latexswap.config.Environment;
import io.topos.latexswap.config.LatexSwapConfig;

LatexSwapConfig config = new LatexSwapConfig(Environment.DEVNET);
// Or with custom values:
LatexSwapConfig config = new LatexSwapConfig(
    Environment.MAINNET,
    "https://custom-rpc.platon.network/rpc",
    "0xYourContractAddress",
    BigInteger.valueOf(100),
    "https://custom-explorer.platon.network"
);
```

## Environment Variables Reference

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `LATEX_ENV` | Environment name: DEVNET, UATNET, or MAINNET | No* | DEVNET |
| `LATEX_RPC_URL` | RPC endpoint URL | No | See defaults below |
| `LATEX_CONTRACT_ADDRESS` | LatexSwap contract address | Yes** | None |
| `LATEX_CHAIN_ID` | Chain ID | No | See defaults below |
| `LATEX_EXPLORER_URL` | Block explorer URL | No | See defaults below |

\* If not set, defaults to DEVNET  
\*\* Must be set via environment variable or explicit configuration

## Default Configurations

### DEVNET
- **RPC URL**: `https://devnet2openapi.platon.network/rpc`
- **Chain ID**: `2206132`
- **Explorer**: `https://devnet2scan.platon.network`

### UATNET
- **RPC URL**: `https://devnet3openapi2.platon.network/rpc`
- **Chain ID**: `20250407`
- **Explorer**: `https://devnet3scan.platon.network`

### MAINNET
- **RPC URL**: `https://openapi2.platon.network/rpc`
- **Chain ID**: `210425`
- **Explorer**: `https://scan.platon.network`

## Usage Examples

### Example 1: Using Environment Variables

```bash
# Set environment
export LATEX_ENV=MAINNET
export LATEX_CONTRACT_ADDRESS=0x1234567890123456789012345678901234567890

# Run application
java -jar your-app.jar
```

```java
// In your code
Credentials credentials = WalletUtils.loadCredentials("password", "keystore.json");
LatexSwapService service = new LatexSwapService(credentials);
// Service automatically uses MAINNET configuration
```

### Example 2: Docker

```dockerfile
FROM openjdk:11
ENV LATEX_ENV=MAINNET
ENV LATEX_CONTRACT_ADDRESS=0x1234567890123456789012345678901234567890
COPY your-app.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
```

### Example 3: Kubernetes

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: latex-swap-app
spec:
  containers:
  - name: app
    image: your-app:latest
    env:
    - name: LATEX_ENV
      value: "MAINNET"
    - name: LATEX_CONTRACT_ADDRESS
      value: "0x1234567890123456789012345678901234567890"
```

### Example 4: Spring Boot application.properties

```properties
# application.properties
latex.env=MAINNET
latex.contract.address=0x1234567890123456789012345678901234567890
```

Then in your Spring Boot code:

```java
@Value("${latex.env}")
private String env;

@Value("${latex.contract.address}")
private String contractAddress;

@Bean
public LatexSwapConfig latexSwapConfig() {
    return new LatexSwapConfig(
        Environment.fromString(env),
        null, // Use default RPC URL
        contractAddress,
        null, // Use default Chain ID
        null  // Use default Explorer URL
    );
}
```

## Configuration Priority

1. **Explicit configuration** (highest priority)
2. **System properties** (`-D` flags)
3. **Environment variables**
4. **Default values** (lowest priority)

## Troubleshooting

### Issue: Contract address is 0x0000...

**Solution**: Set `LATEX_CONTRACT_ADDRESS` environment variable or pass it explicitly in configuration.

### Issue: Wrong network being used

**Solution**: Check `LATEX_ENV` is set correctly. Use `service.getConfig()` to verify current configuration.

### Issue: Connection errors

**Solution**: Verify `LATEX_RPC_URL` is correct and accessible from your network.

## Best Practices

1. **Never commit contract addresses to version control** - Use environment variables
2. **Use different keystores for different environments** - Don't use mainnet keys on devnet
3. **Validate configuration at startup** - Check that contract address is not 0x0000...
4. **Use configuration management tools** - For production, use tools like Kubernetes ConfigMaps, AWS Parameter Store, etc.

