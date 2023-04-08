package com.liubaozhu.lan.core.exception;

import com.liubaozhu.lan.core.parser.LanParser;

/**
 * 语法解析错误
 */
public class ParseException extends RuntimeException {
    private String message;
    private LanParser parser;

    public ParseException(String message) {
        this(message, null);
    }

    public ParseException(String message, LanParser parser) {
        this(message, parser, null);
    }

    public ParseException(String message, LanParser parser, Exception exception) {
        super(message, exception);
        this.message = "[" +
                "line=" + parser.getLine() + ", " +
                "column=" + parser.getColumn() + ", " +
                "char='" + parser.current() + "'" +
                "] " +
                message;
        this.parser = parser;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        String message = this.message;
        return (message != null) ? (s + ": " + message) : s;
    }
}
