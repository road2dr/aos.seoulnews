package com.tobeitech.api.vo;

/**
 * Created by Ted
 */
public class ErrorVO {

    private String nonce;
    private Error error;

    public String getNonce() {
        return nonce;
    }

    public Error getError() {
        return error;
    }

    public class Error {
        private String code;
        private String type;
        private String message;

        public String getCode() {
            return code;
        }

        public String getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }
    }

}
