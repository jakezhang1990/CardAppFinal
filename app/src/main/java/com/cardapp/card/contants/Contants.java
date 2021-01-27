package com.cardapp.card.contants;

import com.cardlan.utils.ByteUtil;

/**
 * 常量定义
 * 用于IC卡读写控制
 */
public interface Contants {
    static final String procpath = "/proc/gpio_set/rp_gpio_set";
    //Parameters that control the sound of a buzzer.
    static final String open_bee_voice = "c_24_1_1";
    static final String close_bee_voice = "c_24_1_0";
    //Control the parameters of Red_LED and other switches
    static final String open_led_red = "d_26_0_1";
    static final String close_led_red = "d_26_0_0";
    //Control the parameters of Green_LED and other switches
    static final String open_led_green = "c_5_1_1";
    static final String close_led_green = "c_5_1_0";
    static final String ADCPATH = "/proc/adc/adc_ctrl";

    //The key related
    static final byte[] assembleBytes = new byte[]{0x26, (byte) 0x91, 0x13, 0x00};
    //The system authorization card is relevant 0x82,0x26,0x00,0x36,0x82,0x42,0x27,0x79
    static final byte[] desKeyBytes = new byte[]{(byte) 0x82, 0x26, 0x00, 0x36, (byte) 0x82, 0x42,
            0x27, 0x79};
    static final byte[] checkBytes = new byte[]{0x55, (byte) 0xa0, (byte) 0xa1, (byte) 0xa2};


    static final char chkFlag = 1;//校验标位
    static final char readBlockAreaCode = ByteUtil.hexStringToChar("0a");
    static final char writeBlockAreaCode = ByteUtil.hexStringToChar("0a");
    //写块0数据块，2备用块
    static final char[] blockNum = new char[]{0, 2};
    //    //写入扇区
    static final char secNum = 10;

    //空白卡和老卡的密A数组static final String[] oldCardPwdKeyAHex = new String[]{"029223685580", "E1E12368558D"};


    static final String voidCardKeyHexStr = "FFFFFFFFFFFF";


    static final String newCtrlKeyHex = "7F078869";

    static final String newKeyBHex = "A2020BC1027D";

    static  final byte[] firstBlockData=new byte[]{0x23, (byte) 0xD8,0x04,0x03, (byte) 0xD1,0x27, (byte) 0xAA,0x00, (byte) 0xA0,0x01,0x01,0x00,0x00,0x01,0x01, (byte) 0xDA};


}
