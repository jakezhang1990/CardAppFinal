package com.cardapp.card.util;

import java.io.Serializable;
import java.math.BigDecimal;

public class CardDataRst implements Serializable {

    //记录金额
    private BigDecimal m = new BigDecimal(0);
    //记录次数
    private int time = 0;

    private String readKeyHexStr;

    private String cardNumHexStr;

    public BigDecimal getM() {
        return m;
    }

    public void setM(BigDecimal m) {
        this.m = m;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getReadKeyHexStr() {
        return readKeyHexStr;
    }

    public void setReadKeyHexStr(String readKeyHexStr) {
        this.readKeyHexStr = readKeyHexStr;
    }

    public String getCardNumHexStr() {
        return cardNumHexStr;
    }

    public void setCardNumHexStr(String cardNumHexStr) {
        this.cardNumHexStr = cardNumHexStr;
    }
}
