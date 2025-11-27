package io.topos.latexswap.model;

/**
 * Fee type enumeration matching Solidity enum
 */
public enum FeeType {
    Fixed(0),      // 固定手续费
    Percentage(1); // 比例手续费

    private final int value;

    FeeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static FeeType fromValue(int value) {
        for (FeeType type : FeeType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid FeeType value: " + value);
    }
}

