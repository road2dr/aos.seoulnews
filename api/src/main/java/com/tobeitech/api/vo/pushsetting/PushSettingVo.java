package com.tobeitech.api.vo.pushsetting;

/**
 * Created by ParkJinYoung on 16. 7. 31..
 */
public class PushSettingVo {

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAlarmOnOff() {
        return alarmOnOff;
    }

    public void setAlarmOnOff(boolean alarmOnOff) {
        this.alarmOnOff = alarmOnOff;
    }

    public String getNoNotiStartTime() {
        return noNotiStartTime;
    }

    public void setNoNotiStartTime(String noNotiStartTime) {
        this.noNotiStartTime = noNotiStartTime;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getNoNotiEndTime() {
        return noNotiEndTime;
    }

    public void setNoNotiEndTime(String noNotiEndTime) {
        this.noNotiEndTime = noNotiEndTime;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getFontSize() {
        return fontSize;
    }

    private String platform;
    private String token;
    private boolean alarmOnOff;
    private String noNotiStartTime;
    private String noNotiEndTime;
    private String fontSize;
}
