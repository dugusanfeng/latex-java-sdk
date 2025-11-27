package io.topos.latexswap.util;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Utility class for address operations
 */
public class AddressUtils {
    /**
     * Get pair key from two token addresses (matching Solidity getPairKey function)
     * @param token0 First token address
     * @param token1 Second token address
     * @return Pair key address
     */
    public static String getPairKey(String token0, String token1) {
        // Sort addresses to ensure consistent pair key
        String tokenA, tokenB;
        if (token0.compareToIgnoreCase(token1) < 0) {
            tokenA = token0;
            tokenB = token1;
        } else {
            tokenA = token1;
            tokenB = token0;
        }

        // Encode packed: abi.encodePacked(tokenA, tokenB)
        byte[] tokenABytes = Numeric.hexStringToByteArray(tokenA);
        byte[] tokenBBytes = Numeric.hexStringToByteArray(tokenB);

        ByteBuffer buffer = ByteBuffer.allocate(tokenABytes.length + tokenBBytes.length);
        buffer.put(tokenABytes);
        buffer.put(tokenBBytes);
        byte[] packed = buffer.array();

        // keccak256 hash
        byte[] hash = keccak256(packed);

        // Convert to address (first 20 bytes)
        byte[] addressBytes = new byte[20];
        System.arraycopy(hash, 12, addressBytes, 0, 20);

        return "0x" + Numeric.toHexString(addressBytes).substring(2);
    }

    /**
     * Keccak256 hash function using BouncyCastle
     */
    private static byte[] keccak256(byte[] input) {
        Keccak.DigestKeccak digest = new Keccak.Digest256();
        return digest.digest(input);
    }

    /**
     * Convert string to bytes32 (keccak256 hash)
     * @param input String input
     * @return bytes32 as BigInteger
     */
    public static BigInteger stringToBytes32(String input) {
        byte[] hash = keccak256(input.getBytes());
        return new BigInteger(1, hash);
    }

    /**
     * Validate Ethereum address format
     */
    public static boolean isValidAddress(String address) {
        if (address == null || !address.startsWith("0x")) {
            return false;
        }
        String hex = address.substring(2);
        return hex.length() == 40 && hex.matches("[0-9a-fA-F]+");
    }
}

