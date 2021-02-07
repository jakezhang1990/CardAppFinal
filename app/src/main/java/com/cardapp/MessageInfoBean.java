package com.cardapp;

/**
 * @Description: java类作用描述
 * @Author: jakezhang
 * @CreateDate: 2021/2/4 16:53
 */
public class MessageInfoBean {

    /**
     * status : 1
     * msg : ok
     * data : {"id":"1","uniacid":"1","terminal_num":"0001","snno":"sn001","equipment_num":"type001","ip":"127.0.0.1","mode":"sn001","server_domain":"http://www.bb369.com/","com_key":"sn0001","org_code":"61010001","card_type":"1","sector":"sn001","original_key":"sn001002003","user_key":"sn001002003","key_type":"1","control_word":"sn001002003","createtime":"2021-01-21 14:12:37","updatetime":null,"linetime":"2021-02-03 23:41:40","org_name":"西安高新XX学校"}
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
         * id : 1
         * uniacid : 1
         * terminal_num : 0001
         * snno : sn001
         * equipment_num : type001
         * ip : 127.0.0.1
         * mode : sn001
         * server_domain : http://www.bb369.com/
         * com_key : sn0001
         * org_code : 61010001
         * card_type : 1
         * sector : sn001
         * original_key : sn001002003
         * user_key : sn001002003
         * key_type : 1
         * control_word : sn001002003
         * createtime : 2021-01-21 14:12:37
         * updatetime : null
         * linetime : 2021-02-03 23:41:40
         * org_name : 西安高新XX学校
         */

        private String id;
        private String uniacid;
        private String terminal_num;
        private String snno;
        private String equipment_num;
        private String ip;
        private String mode;
        private String server_domain;
        private String com_key;
        private String org_code;
        private String card_type;
        private String sector;
        private String original_key;
        private String user_key;
        private String key_type;
        private String control_word;
        private String createtime;
        private Object updatetime;
        private String linetime;
        private String org_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUniacid() {
            return uniacid;
        }

        public void setUniacid(String uniacid) {
            this.uniacid = uniacid;
        }

        public String getTerminal_num() {
            return terminal_num;
        }

        public void setTerminal_num(String terminal_num) {
            this.terminal_num = terminal_num;
        }

        public String getSnno() {
            return snno;
        }

        public void setSnno(String snno) {
            this.snno = snno;
        }

        public String getEquipment_num() {
            return equipment_num;
        }

        public void setEquipment_num(String equipment_num) {
            this.equipment_num = equipment_num;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getServer_domain() {
            return server_domain;
        }

        public void setServer_domain(String server_domain) {
            this.server_domain = server_domain;
        }

        public String getCom_key() {
            return com_key;
        }

        public void setCom_key(String com_key) {
            this.com_key = com_key;
        }

        public String getOrg_code() {
            return org_code;
        }

        public void setOrg_code(String org_code) {
            this.org_code = org_code;
        }

        public String getCard_type() {
            return card_type;
        }

        public void setCard_type(String card_type) {
            this.card_type = card_type;
        }

        public String getSector() {
            return sector;
        }

        public void setSector(String sector) {
            this.sector = sector;
        }

        public String getOriginal_key() {
            return original_key;
        }

        public void setOriginal_key(String original_key) {
            this.original_key = original_key;
        }

        public String getUser_key() {
            return user_key;
        }

        public void setUser_key(String user_key) {
            this.user_key = user_key;
        }

        public String getKey_type() {
            return key_type;
        }

        public void setKey_type(String key_type) {
            this.key_type = key_type;
        }

        public String getControl_word() {
            return control_word;
        }

        public void setControl_word(String control_word) {
            this.control_word = control_word;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public Object getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(Object updatetime) {
            this.updatetime = updatetime;
        }

        public String getLinetime() {
            return linetime;
        }

        public void setLinetime(String linetime) {
            this.linetime = linetime;
        }

        public String getOrg_name() {
            return org_name;
        }

        public void setOrg_name(String org_name) {
            this.org_name = org_name;
        }
    }
}
