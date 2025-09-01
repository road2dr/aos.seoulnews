package com.tobeitech.api.vo.alarmreply;

/**
 * Created by ParkJinYoung on 16. 7. 31..
 */
public class AlarmReplyItemVo {

    private int cid;
    private int idx;
    private int board_type;
    private int pid;
    private int uid;

    private String text;
    private String reg_date;
    private String update_date;
    private String title;
    private String nick;
    private String photo_url;

    private int user_level;

    public int getCid() {
        return cid;
    }

    public int getIdx() {
        return idx;
    }

    public int getBoard_type() {
        return board_type;
    }

    public int getPid() {
        return pid;
    }

    public int getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }

    public String getReg_date() {
        return reg_date;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public String getTitle() {
        return title;
    }

    public String getNick() {
        return nick;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public int getUser_level() {
        return user_level;
    }
}
