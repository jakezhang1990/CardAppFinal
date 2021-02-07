package com.cardapp;

/**
 * @Author: jakezhang
 * Company:DHC
 * Description： 描述内容
 * Date: 2021/1/26 17:13
 */
public interface NetURL {
//域名
    public static final String DOMAIN="http://bb369api.qa.meixinyun.net/";
//申请领款接口，通过该接口可以创建一个领款订单
    public static final String URL_APPLY_DRAW_MONEY=DOMAIN+"api/Method/add_order";
    //写卡后向服务器确认写卡状态
    public static final String URL_CONFIRM_WRITE_STATUS=DOMAIN+"api/Method/order_status";
    //向服务器报告设备状态，每隔1分钟请求一次
    public static final String URL_MACHINE_STATE=DOMAIN+"api/Method/monitor";
//基本信息
    public static final String URL_INFOS=DOMAIN+"api/Method/getInfo";
}
