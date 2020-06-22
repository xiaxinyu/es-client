package com.xiaxinyu.es.client.es;

public enum ESLogStatus {
    //开始记录
    INIT("start"),
    //运行中
    SUCCESS("success"),
    //运行异常
    FAIL("fail"),
    //完成
    FINISH("end");

    /**
     * 状态编码
     */
    private String code;

    ESLogStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
