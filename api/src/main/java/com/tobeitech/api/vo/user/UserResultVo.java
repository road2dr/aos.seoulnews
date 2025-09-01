package com.tobeitech.api.vo.user;

/**
 * Created by ParkJinYoung on 16. 7. 31..
 */
public class UserResultVo {

    private boolean firstLogin;
    private LoginUserInfoVo userInfo;
    private RankInfoVo rankInfo;

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public LoginUserInfoVo getUserInfo() {
        return userInfo;
    }

    public RankInfoVo getRankInfo() {
        return rankInfo;
    }

    public class LoginUserInfoVo {

        private int uid;
        private String id;
        private String pwd;
        private String access_token;
        private String phone;
        private String mobile;
        private String post_code;
        private String addr;
        private String addr_detail;
        private String addr_type2;
        private int is_drop;
        private int user_level;
        private String email;
        private String name;
        private String nick;
        private String photo_url;
        private int idx;
        private int wing;
        private int hilight_reply_item;
        private int memberpoint;

        public int getUid() {
            return uid;
        }

        public String getId() {
            return id;
        }

        public String getPwd() {
            return pwd;
        }

        public String getAccess_token() {
            return access_token;
        }

        public String getPhone() {
            return phone;
        }

        public String getMobile() {
            return mobile;
        }

        public String getPost_code() {
            return post_code;
        }

        public String getAddr() {
            return addr;
        }

        public String getAddr_detail() {
            return addr_detail;
        }

        public String getAddr_type2() {
            return addr_type2;
        }

        public int getIs_drop() {
            return is_drop;
        }

        public int getUser_level() {
            return user_level;
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }

        public String getNick() {
            return nick;
        }

        public String getPhoto_url() {
            return photo_url;
        }

        public int getIdx() {
            return idx;
        }

        public int getWing() {
            return wing;
        }

        public int getHilight_reply_item() {
            return hilight_reply_item;
        }

        public int getMemberpoint() {
            return memberpoint;
        }

    }


    public class RankInfoVo {

        private int pre_level;
        private int p_userrank;
        private int p_usertotal;
        private int cur_level;

        public int getPre_level() {
            return pre_level;
        }

        public int getP_userrank() {
            return p_userrank;
        }

        public int getP_usertotal() {
            return p_usertotal;
        }

        public int getCur_level() {
            return cur_level;
        }


    }

}
