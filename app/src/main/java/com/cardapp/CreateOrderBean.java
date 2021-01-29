package com.cardapp;

/**
 * @Author: jakezhang
 * Company:DHC
 * Description： 描述内容
 * Date: 2021/1/29 15:30
 */
public class CreateOrderBean {

    /**
     * status : 1
     * msg :
     * data : {"serial":"","wallet_id":"","personcardno":"","money":"","id":""}
     */

    private int status;
    private String msg;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * serial :
         * wallet_id :
         * personcardno :
         * money :
         * id :
         */

        private String serial;
        private String wallet_id;
        private String personcardno;
        private String money;
        private String id;

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public String getWallet_id() {
            return wallet_id;
        }

        public void setWallet_id(String wallet_id) {
            this.wallet_id = wallet_id;
        }

        public String getPersoncardno() {
            return personcardno;
        }

        public void setPersoncardno(String personcardno) {
            this.personcardno = personcardno;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
