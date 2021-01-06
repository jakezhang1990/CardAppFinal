package com.cardapp.card.util;


import android.util.Log;

import com.cardapp.card.contants.Contants;
import com.cardlan.twoshowinonescreen.CardLanStandardBus;
import com.cardlan.twoshowinonescreen.DeviceCardConfig;
import com.cardlan.utils.ByteUtil;


import org.apache.commons.codec.binary.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;


/**
 * 对于空白卡的初始化流程：
 * 1：首先读数据确认
 * 2：修改控制字
 * 3：按协议写入第一块
 * 4：如果有数据写入数据
 *
 * <p>
 * 对于旧密码改用新控制字和密钥方式的流程
 * 1：先把读取卡数据
 * 2：修改控制字
 * 3：修改第一块
 * 4：再写数据
 * <p>
 * <p>
 * 空白卡的相关内容：
 * 默认密钥：FFFFFFFFFFFF
 * 默认控制字：FF078069
 */

public class CardOperator implements Serializable {
    private static final String TAG = "CardOperator";

    private static boolean initCarDevFlag = false;
    private static final int S_Reset_buffer_size = 32;

    private static CardLanStandardBus cardLanStandardBus = null;

    private static CardOperator cardOperator = null;

    //创建对象时，对卡设备进行初始化
    private CardOperator() throws Exception {
        if (!initCarDevFlag) {
            initCardDev();
        }
    }

    public static CardOperator createObject() throws Exception {
        if (cardOperator == null) {
            cardOperator = new CardOperator();
        }
        return cardOperator;
    }


    //默认读卡方法，取当前卡号为转为密钥读卡
    public CardDataRst readCardData() throws InterruptedException {
        String cardNumHexStr = getCardNumberStr();
        if (cardNumHexStr == null) {
            return null;
        }
        String keyStr = getCardKeyStr(ByteUtil.hexStringToByteArray(cardNumHexStr));
        CardDataRst cardDataRst = readCardData(keyStr, cardNumHexStr);
        return cardDataRst;
    }


    //根据传入的16进制密钥和卡号，读取卡数据，卡号可以为空值
    public CardDataRst readCardData(String keyHexStr, String cardNumStr) throws InterruptedException {
        VoiceOrLedOperator.openVoice();
        char secId = Contants.secNum;
        char bId = Contants.blockNum[0];
        byte[] keyBuffer = ByteUtil.hexStringToByteArray(keyHexStr);
        char blockAreaCode = Contants.readBlockAreaCode;
        char flag = Contants.chkFlag;
        byte[] data = cardLanStandardBus.callReadOneSectorDataFromCard(secId, bId, flag, keyBuffer, blockAreaCode);
        CardDataRst cardDataRst = null;
        if (ByteUtil.notNull(data)) {
            //取金字节数组
            cardDataRst = new CardDataRst();
            byte[] moneyBytes = ByteUtil.copyBytes(data, 2, 2);
            BigDecimal rst = CardDataSet.computeToDecimal(moneyBytes);
            //取次数字节数组
            byte[] timeBytes = ByteUtil.copyBytes(data, 10, 3);
            int time = CardDataSet.computeToInt(timeBytes);
            cardDataRst.setM(rst);
            cardDataRst.setTime(time);
            cardDataRst.setReadKeyHexStr(keyHexStr);
            cardDataRst.setCardNumHexStr(cardNumStr);

        }
        Thread.sleep(100);
        VoiceOrLedOperator.closeVoice();
        return cardDataRst;
    }


    //用于空白卡初始化
    public int voidCardInit(String data) throws Exception {
        if (data == null && data == "") {
            data = "0.00";
        }
        //十六进制卡号
        String cardHexStr = getCardNumberStr();
        //十六进制密钥
        String keyHexStr = getCardKeyStr(ByteUtil.hexStringToByteArray(cardHexStr));
        //读取卡的数据，初始读卡时，采用默认密钥
        CardDataRst cardDataRst = readCardData(Contants.voidCardKeyHexStr, cardHexStr);
        int initFlag = 0;
        if (cardDataRst != null) {
            boolean f = modifyCtrlArea(keyHexStr);
            //控制字修改成功后，才允许写入
            if (f) {
                //修改第一块
                f = writeBlockFirstDat(keyHexStr);
                if (f) {
                    f = writeCardData(new BigDecimal(data), 1, keyHexStr);
                    if (!f) {
                        //初始数据写入失败
                        initFlag = -2;
                    }
                } else {
                    //写入批一块失败
                    initFlag = -4;
                }
            } else {
                //修改控制字失败
                initFlag = -1;
            }
        } else {
            //表示初始化时读卡失败
            initFlag = -3;
        }
        return initFlag;
    }


    //重置老卡的控制字，同时把老卡的数据按新方式写入卡中
    public boolean resetOldCardCtrl(String oldCardKeyHex, String data) throws Exception {
        if (data == null && data == "") {
            data = "0.00";
        }
        if (oldCardKeyHex == null && oldCardKeyHex == "") {
            return false;
        }
        String cardNumHexStr = getCardNumberStr();
        String newKeyHexStr = getCardKeyStr(ByteUtil.hexStringToByteArray(cardNumHexStr));
        CardDataRst cardDataRst = readCardData(oldCardKeyHex, cardNumHexStr);
        if (cardDataRst != null) {
            boolean f = modifyCtrlArea(newKeyHexStr, oldCardKeyHex);
            if (f) {
                f = writeBlockFirstDat(newKeyHexStr);
                if (f) {
                    int time = cardDataRst.getTime() == 0 ? 1 : cardDataRst.getTime();
                    return writeCardData(cardDataRst.getM().add(new BigDecimal(data)), time, newKeyHexStr);
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    //此方法默认下不建议使用，只是在空白卡初始化后，或者老卡按照新的协议修改控制字后，忘记写第一块时使用。
    public boolean restFirstBlock() throws Exception {
        String keyHexStr = getCardKeyStr();
        return this.writeBlockFirstDat(keyHexStr);
    }


    //采用新方式写入的方法
    public boolean writeData(String data, int costType) throws Exception {
        String cardNumHexStr = getCardNumberStr();
        if (cardNumHexStr == null) {
            return false;
        }
        String keyStr = getCardKeyStr(ByteUtil.hexStringToByteArray(cardNumHexStr));
        CardDataRst cardDataRst = readCardData(keyStr, cardNumHexStr);
        //类型等于1为消费操作;0为充值操作
        if (costType == 1) {
            cardDataRst.setTime(cardDataRst.getTime() + 1);
        }
        return writeCardData(new BigDecimal(data).add(cardDataRst.getM()), cardDataRst.getTime(), keyStr);
    }

    public boolean writeCardData(BigDecimal data, int time, String keyStr) throws Exception {
        byte[] dataBytes = CardDataSet.proceeWriteData(data.multiply(new BigDecimal(100)).intValue(), time);
        byte[] keyBuffer = ByteUtil.hexStringToByteArray(keyStr);
        char blockAreaCode = Contants.writeBlockAreaCode;
        char flag = Contants.chkFlag;
        boolean fflag = false;
        char secId = Contants.secNum;
        char dataBid = Contants.blockNum[0];
        char backDataBid = Contants.blockNum[1];
        int f = cardLanStandardBus.callWriteOneSertorDataToCard(dataBytes, secId, backDataBid, flag, keyBuffer, blockAreaCode);
        if (f == DeviceCardConfig.MONE_CARD_WRITE_SUCCESS_STATUS) {
            fflag = true;
        }
        if (fflag) {
            f = cardLanStandardBus.callWriteOneSertorDataToCard(dataBytes, secId, dataBid, flag, keyBuffer, blockAreaCode);
            if (f == DeviceCardConfig.MONE_CARD_WRITE_SUCCESS_STATUS) return true;
        }
        return fflag;
    }


    //根据卡号划算成验证密钥A
    public String getCardKeyStr(byte[] bytes) throws InterruptedException {
        if (bytes != null) {
            byte[] keyBuffer = CardDataSet.computeKey(bytes);
            String cardKey = ByteUtil.byteArrayToHex(keyBuffer);
            return cardKey;
        }
        return null;
    }

    //根据卡号划算成验证密钥A
    public String getCardKeyStr() throws InterruptedException {
        byte[] buffer = readCardNumberBytes();
        if (buffer != null) {
            byte[] subBuf = ByteUtil.copyBytes(buffer, 0, 4);
            byte[] keyBuffer = CardDataSet.computeKey(subBuf);
            String cardKey = ByteUtil.byteArrayToHex(keyBuffer);
            return cardKey;
        }
        return null;
    }

    //获取卡的字节数组
    public byte[] getKeyBytes() throws InterruptedException {
        byte[] buffer = readCardNumberBytes();
        if (buffer != null) {
            byte[] subBuf = ByteUtil.copyBytes(buffer, 0, 4);
            byte[] keyBuffer = CardDataSet.computeKey(subBuf);
            return keyBuffer;
        }
        return null;
    }

    public String getCardNumberStr() throws InterruptedException {
        byte[] buffer = readCardNumberBytes();
        if (buffer != null) {
            byte[] subBuf = ByteUtil.copyBytes(buffer, 0, 4);
            String carNum = ByteUtil.byteArrayToHex(subBuf);
            return carNum;
        }
        return null;
    }


    //读取卡号
    private byte[] readCardNumberBytes() throws InterruptedException {
        //
        Thread.sleep(100);
        byte[] buffer = new byte[S_Reset_buffer_size];
        int rst = cardLanStandardBus.callCardReset(buffer);
        // VoiceOrLedOperator.closeVoice();
        //ic序列号读取
        if (rst == DeviceCardConfig.CARD_RESET_STATUS_MONE_SUCCESS) {
            return buffer;
        } else {
            return null;
        }
    }

    //初始化设备测试声音
    private void testVoiceAndLight() throws InterruptedException {
        VoiceOrLedOperator.openVoice();
        Thread.sleep(1000);
        VoiceOrLedOperator.closeVoice();
//        Thread.sleep(1000);
//        VoiceOrLedOperator.writeProc(Contants.ADCPATH,Contants.open_led_green.getBytes());
//        Thread.sleep(1000);
//        VoiceOrLedOperator.writeProc(Contants.ADCPATH,Contants.close_led_green.getBytes());
//        Thread.sleep(1000);
//        VoiceOrLedOperator.writeProc(Contants.ADCPATH,Contants.open_led_red.getBytes());
//        Thread.sleep(1000);
//        VoiceOrLedOperator.writeProc(Contants.ADCPATH,Contants.close_led_red.getBytes());
    }

    //初始化设置
    private void initCardDev() throws Exception {
        cardLanStandardBus = new CardLanStandardBus();
        int f = cardLanStandardBus.callInitDev();
        if (f == DeviceCardConfig.INIT_DEVICE_STATUS_SUCCESS) {
            initCarDevFlag = true;
            testVoiceAndLight();
        } else {
            Log.e(TAG, "IC设备初始化失败", new Exception());
            throw new Exception("IC设备初始化失败");
        }
    }


    private boolean modifyCtrlArea(String newKeyA, String oldKeyA) {
        byte[] cbytes = CardDataSet.computeCtrlBytes(newKeyA);
        char sec = 10;
        char b = 3;
        char cf = 1;
        byte[] keyAbytes = ByteUtil.hexStringToByteArray(oldKeyA);
        int f = cardLanStandardBus.callWriteOneSertorDataToCard(cbytes, sec, b, cf, keyAbytes, Contants.readBlockAreaCode);
        if (f == DeviceCardConfig.MONE_CARD_WRITE_SUCCESS_STATUS) {
            return true;
        }
        return false;
    }

    /**
     * 当是新卡时，修改控制区
     */
    private boolean modifyCtrlArea(String newKeyA) {
        byte[] cbytes = CardDataSet.computeCtrlBytes(newKeyA);
        char sec = Contants.secNum;
        char b = 3;
        char cf = Contants.chkFlag;
        byte[] keyAbytes = ByteUtil.hexStringToByteArray(Contants.voidCardKeyHexStr);
        int f = cardLanStandardBus.callWriteOneSertorDataToCard(cbytes, sec, b, cf, keyAbytes, Contants.readBlockAreaCode);
        if (f == DeviceCardConfig.MONE_CARD_WRITE_SUCCESS_STATUS) {
            return true;
        }
        return false;
    }


    //写第一块的数据
    private boolean writeBlockFirstDat(String keyHexStr) {
        byte[] keyABuffer = ByteUtil.hexStringToByteArray(keyHexStr);
        char sec = Contants.secNum;
        char firstBlockId = 1;
        char cf = Contants.chkFlag;
        byte[] bytes = readFirstBlock(keyHexStr);
        if (!Arrays.equals(bytes, Contants.firstBlockData)) {
            int f = cardLanStandardBus.callWriteOneSertorDataToCard(Contants.firstBlockData, sec, firstBlockId, cf, keyABuffer, Contants.readBlockAreaCode);
            return f == DeviceCardConfig.MONE_CARD_WRITE_SUCCESS_STATUS ? true : false;
        }
        return true;
    }

    private byte[] readFirstBlock(String keyHexStr) {
        byte[] keyABuffer = ByteUtil.hexStringToByteArray(keyHexStr);
        char sec = Contants.secNum;
        char firstBlockId = 1;
        char cf = Contants.chkFlag;
        return cardLanStandardBus.callReadOneSectorDataFromCard(sec, firstBlockId, cf, keyABuffer, Contants.readBlockAreaCode);
    }
}
