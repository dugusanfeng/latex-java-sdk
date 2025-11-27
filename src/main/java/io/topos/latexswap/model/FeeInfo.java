package io.topos.latexswap.model;

import java.math.BigInteger;

/**
 * Fee information structure matching Solidity struct
 */
public class FeeInfo {
    private FeeType feeType;
    private BigInteger value; // 固定金额或比例值（比例值需要除以基数，decimal为6位，比如设置为10%, 对应的值为100000）

    public FeeInfo() {
    }

    public FeeInfo(FeeType feeType, BigInteger value) {
        this.feeType = feeType;
        this.value = value;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FeeInfo{" +
                "feeType=" + feeType +
                ", value=" + value +
                '}';
    }
}

