package com.cardapp.card.util;

import com.cardapp.card.contants.Contants;
import com.cardlan.utils.ByteUtil;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Card数据处理的相关方法
 */

public class CardDataSet {

    //卡号取反相关操作
    private static byte[] cardNumNeg(byte[] cardNum) {
        for (int i = 0; i < cardNum.length; i++) {
            cardNum[i] = (byte) ~cardNum[i];
        }
        return cardNum;
    }

    //计算卡读取密码
    public static byte[] computeKey(byte[] cardNum) {
        byte[] cardNeg = cardNumNeg(cardNum);
        byte[] keyBytes = new byte[6];
        keyBytes[0] = 0x0A;
        ByteUtil.copyBytes(cardNeg, keyBytes, 1);
        keyBytes[5] = (byte) 0x81;
        return keyBytes;
    }

    //处理卡写入的数据

    /**
     * @param decimal 金额
     * @param time    次数
     */
    public static byte[] proceeWriteData(int decimal, int time) throws DecoderException {
        byte[] dataBytes = new byte[16];
        dataBytes[4] = 0x00;
        dataBytes[6] = 0x00;
        dataBytes[7] = 0x00;
        dataBytes[8] = 0x00;
        dataBytes[9] = 0x00;
        dataBytes[14] = 0x00;
        //计算金额写卡的数据
        //字节数据需要翻转
        byte[] mbytes = convertMoneyToBytes(decimal);
        dataBytes[2] = mbytes[0];
        dataBytes[3] = mbytes[1];
        dataBytes[1] = (byte) (dataBytes[2] + dataBytes[3] + 0x00);
        dataBytes[5] = (byte) ~(dataBytes[2] + dataBytes[3] + 0x00);
        //计算次数写卡的数据
        //字节数据需要翻转
        byte[] tbytes = convertTimeToBytes(time);
        dataBytes[10] = tbytes[0];
        dataBytes[11] = tbytes[1];
        dataBytes[12] = tbytes[2];
        dataBytes[13] = (byte) (dataBytes[10] + dataBytes[11] + dataBytes[12]);
        //计算0字节和15字节校验码
        byte[] subBytes = ByteUtil.copyBytes(dataBytes, 1, 14);
        computeZoreBitVal(subBytes, dataBytes);
        computeFBitVal(subBytes, dataBytes);

        return dataBytes;
    }


    public static BigDecimal computeToDecimal(byte[] bytes) {
        byte[] destBytes = ByteUtil.reverseByteArray(bytes);
        String hexStr = ByteUtil.byteArrayToHex(destBytes);
        int value = Integer.valueOf(hexStr, 16);
        float rst = (float) value / 100;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        BigDecimal d = new BigDecimal(decimalFormat.format(rst));
        return d;
    }

    public static int computeToInt(byte[] bytes) {
        byte[] destBytes = ByteUtil.reverseByteArray(bytes);
        String hexStr = ByteUtil.byteArrayToHex(destBytes);
        int value = Integer.valueOf(hexStr, 16);
        return value;
    }


    private static void computeZoreBitVal(byte[] subBytes, byte[] dataBytes) {
        //byte oxVal = (byte)(((((((((((( (subBytes[0] ^ subBytes[1] )^ subBytes[2] )^ subBytes[3]) ^ subBytes[4]) ^ subBytes[5]) ^ subBytes[6]) ^ subBytes[7] )^ subBytes[8]) ^ subBytes[9]) ^ subBytes[10]) ^ subBytes[11]) ^ subBytes[12] )^ subBytes[13]);
        byte oxVal = (byte) (subBytes[0] ^ subBytes[1] ^ subBytes[2] ^ subBytes[3] ^ subBytes[4] ^ subBytes[5] ^ subBytes[6] ^ subBytes[7] ^ subBytes[8] ^ subBytes[9] ^ subBytes[10] ^ subBytes[11] ^ subBytes[12] ^ subBytes[13]);
        dataBytes[0] = oxVal;
    }

    private static void computeFBitVal(byte[] subBytes, byte[] dataBytes) {
        byte oxVal = (byte) ~(subBytes[0] + subBytes[1] + subBytes[2] + subBytes[3] + subBytes[4] + subBytes[5] + subBytes[6] + subBytes[7] + subBytes[8] + subBytes[9] + subBytes[10] + subBytes[11] + subBytes[12] + subBytes[13]);
        dataBytes[15] = oxVal;
    }

    //把整形格式化成二个字节的数组
    private static byte[] convertMoneyToBytes(int money) throws DecoderException {
        String formatNum = String.format("%04X", money);
        byte[] bytes = ByteUtil.hexStringToByteArray(formatNum);
        return ByteUtil.reverseByteArray(bytes);
    }

    //把整形格式化成三个字节的数组
    private static byte[] convertTimeToBytes(int time) throws DecoderException {
        String formatNum = String.format("%06X", time);
        byte[] bytes = ByteUtil.hexStringToByteArray(formatNum);
        return ByteUtil.reverseByteArray(bytes);
    }

    //计算控制区的字节
    public static byte[] computeCtrlBytes(String newKeyAhex) {
        byte[] keyAbytes = ByteUtil.hexStringToByteArray(newKeyAhex);
        byte[] cbytes = ByteUtil.hexStringToByteArray(Contants.newCtrlKeyHex);
        byte[] keyBbytes = ByteUtil.hexStringToByteArray(Contants.newKeyBHex);
        byte[] bytes = ByteUtil.addBytes(keyAbytes, cbytes);
        bytes = ByteUtil.addBytes(bytes, keyBbytes);
        return bytes;

    }

}
