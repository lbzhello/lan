package com.liubaozhu.lan.core.exception;


public class LanEexception extends RuntimeException {
    private String code;

    public LanEexception(String code) {
        this.code = code;
    }

    public LanEexception(String code, String message) {
        super(message);
        this.code = code;
    }

    public LanEexception(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
