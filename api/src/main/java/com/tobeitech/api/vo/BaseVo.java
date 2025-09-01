package com.tobeitech.api.vo;

/**
 * Created by ParkJinYoung on 16. 7. 31..
 */
public class BaseVo {

    private String errMsg;
    private int errCode;
    private int success;

    public String getErrMsg() {
        return errMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public int getSuccess() {
        return success;
    }
}
