package com.tobeitech.api.vo.userinfo;

/**
 * Created by ParkJinYoung on 16. 7. 31..
 */
public class UserInfoResultVo {

    private UserInfoInfoVo userinfo;

    public class UserInfoInfoVo {
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

    public UserInfoInfoVo getUserinfo() {
        return userinfo;
    }
}
