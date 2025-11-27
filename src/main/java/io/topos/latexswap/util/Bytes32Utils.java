package io.topos.latexswap.util;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.web3j.abi.datatypes.generated.Bytes32;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for bytes32 conversions
 */
public class Bytes32Utils {
    /**
     * Convert string to bytes32 (keccak256 hash) - matching Solidity keccak256("lp")
     * @param input String input
     * @return Bytes32 object
     */
    public static Bytes32 stringToBytes32(String input) {
        byte[] hash = keccak256(input.getBytes(StandardCharsets.UTF_8));
        return new Bytes32(hash);
    }

    /**
     * Convert string to bytes32 as BigInteger
     * @param input String input
     * @return BigInteger representation
     */
    public static BigInteger stringToBytes32BigInteger(String input) {
        byte[] hash = keccak256(input.getBytes(StandardCharsets.UTF_8));
        return new BigInteger(1, hash);
    }

    /**
     * Keccak256 hash function using BouncyCastle
     */
    private static byte[] keccak256(byte[] input) {
        Keccak.DigestKeccak digest = new Keccak.Digest256();
        return digest.digest(input);
    }
}

